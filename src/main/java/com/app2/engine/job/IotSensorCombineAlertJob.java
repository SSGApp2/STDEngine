package com.app2.engine.job;

import com.app2.engine.Service.linenotify.LineNotifyService;
import com.app2.engine.constant.ServerConstant;
import com.app2.engine.entity.model.MainSensorModel;
import com.app2.engine.entity.vcc.iot.IotSensorCombine;
import com.app2.engine.entity.vcc.iot.IotSensorCombineLog;
import com.app2.engine.entity.vcc.iot.IotSensorCombineView;
import com.app2.engine.repository.IotSensorCombineLogRepository;
import com.app2.engine.repository.IotSensorCombineRepository;
import com.app2.engine.util.BeanUtils;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.*;

@Service
public class IotSensorCombineAlertJob {

    private static final Logger LOGGER = LogManager.getLogger(IotSensorCombineAlertJob.class);

    @Autowired
    WebSocketStompClient stompClient;

    @Autowired
    IotSensorCombineRepository iotSensorCombineRepository;

    @Autowired
    private LineNotifyService lineNotifyService;

    @Autowired
    private IotSensorCombineLogRepository iotSensorCombineLogRepository;

    public void startJob() {
        //initial
        String socketURL = ServerConstant.WebSockerServer.replace("http://", "");
        stompClient.connect("ws://" + socketURL + "/ws", new StompSessionHandlerAdapter() {

            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.setAutoReceipt(true);
                session.subscribe("/topic/public", this);
                LOGGER.info("New session: {}", session.getSessionId());
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                LOGGER.error("Error : {}", exception.getMessage(), exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                LOGGER.error("=========================== Try To Reconnect ========================");
                LOGGER.error("Error : {}", exception.getMessage());
                LOGGER.error("=====================================================================");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startJob(); //reconnect
                }
            }

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MainSensorModel.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                long stTime1 = System.currentTimeMillis();
                long timeLog;
                MainSensorModel mainSensorModel = ((MainSensorModel) payload);
                String deviceCode = mainSensorModel.getDeviceName();
                String ouCode = mainSensorModel.getOuCode();
                Gson gson = new Gson();
                Boolean checkAlert = Boolean.FALSE;
                Boolean checkTimeAlert = Boolean.TRUE;
                DateTime currentTime = new DateTime();

                List<IotSensorCombineView> iotSensorCombineView = iotSensorCombineRepository.findByDeviceCodeAndOuCode(deviceCode, ouCode);
                if (iotSensorCombineView.size() > 0) {
                    List<IotSensorCombine> iotSensorCombines = iotSensorCombineView.get(0).groupByCombineId(iotSensorCombineView);
                    for (IotSensorCombine iotSensorCombine : iotSensorCombines) {
                        IotSensorCombineLog iotSensorRangeCombineLog = iotSensorCombineLogRepository.findByIotSensorCombineId(iotSensorCombine.getId());
                        boolean isAlert = false;
                        boolean isRepeat = false;
                        /***************************************/
                        String totalStatus = null; //D / W
                        String repeatTime = null;
                        String lineToken=null;
                        String alertTypeMessage=null;
                        String alertMessage=null;
                        /*************************************/
                        List<String> sensorCodeList = new ArrayList<>();
                        List<Double> valueSensorList = new ArrayList<>();

                        for (IotSensorCombineView detail : iotSensorCombine.getIotSensorCombineViews()) {
                            totalStatus = detail.getAlertType();
                            repeatTime = detail.getRepeatAlert() + "#" + detail.getRepeatUnit();
                            alertTypeMessage=detail.alertTypeMessage();
                            lineToken=detail.getLineToken();
                            alertMessage=detail.getAlertMessage();
                            String sensorCode = detail.getSensorCode();
                            Double currentValue = mainSensorModel.getValueBySensorCode(sensorCode);


                            LOGGER.debug("sensorCode : {}",sensorCode);
                            LOGGER.debug("deviceCode : {}",deviceCode);
                            LOGGER.debug("iotSensorCombine ID : {}",iotSensorCombine.getId());
                            String sensorStatus = detail.getSensorStatus(currentValue);
                            LOGGER.debug("sensorCode : {} Status : {} ", sensorCode, sensorStatus);

                            if (BeanUtils.isNotNull(sensorStatus)) {
                                isAlert = true;
                                sensorCodeList.add(sensorCode);
                                valueSensorList.add(currentValue);
                            } else {
                                isAlert = false;
                                break;
                            }

                        }
                        /////////////////////////////////////////////////////////////////////////
                        if (isAlert) {
                            LOGGER.debug("totalStatus {}", totalStatus);
                            LOGGER.debug("repeatTime {}", repeatTime);
                            if (BeanUtils.isNotNull(repeatTime) && BeanUtils.isNotNull(iotSensorRangeCombineLog)) {
                                //มีการ set repeat
                                Integer timeAlert = Integer.parseInt(repeatTime.split("#")[0]);
                                String timeAlertUnit = repeatTime.split("#")[1];
                                Boolean isRepeatTime = isTimeRepeat(timeAlert, timeAlertUnit, currentTime.toDate(), iotSensorRangeCombineLog.getAlertTime());
                                if (timeAlertUnit.equals("H")) {
                                    LOGGER.debug("Alert every : " + timeAlert + " hours");
                                } else if (timeAlertUnit.equals("M")) {
                                    LOGGER.debug("Alert every : " + timeAlert + " minutes");
                                } else if (timeAlertUnit.equals("S")) {
                                    LOGGER.debug("Alert every : " + timeAlert + " seconds");
                                }
                                if (isRepeatTime == false) {
                                    continue;
                                }
                            }
                            //ถึงเกณฑ์ที่ต้องเริ่มแจ้งเตือน
                            String lineMessage = alertTypeMessage + " " + alertMessage + "  ";
                            for (int i = 0; i < sensorCodeList.size(); i++) {
                                lineMessage += sensorCodeList.get(i) + ":" + valueSensorList.get(i) + " ";
                            }
                            Map<String, String> mapPostAlertJson = new HashMap<>();
                            mapPostAlertJson.put("message", lineMessage);
                            mapPostAlertJson.put("token", lineToken);
                            mapPostAlertJson.put("iotSensorCombine", String.valueOf(iotSensorCombine.getId()));
                            LOGGER.debug("Send Message {}", lineMessage);


                            lineNotifyService.postAsyncMessageWithToken(gson.toJson(mapPostAlertJson)).addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    LOGGER.error("Error Line {}", throwable.getMessage(), throwable);
                                }

                                @Override
                                public void onSuccess(ResponseEntity<String> stringResponseEntity) {
                                    Gson gson = new Gson();
                                    Map<String, String> mapping = gson.fromJson(stringResponseEntity.getBody(), Map.class);
                                    Long iotSensorCombine = Long.parseLong(mapping.get("iotSensorCombine"));
                                    IotSensorCombineLog iotSensorCombineLog = iotSensorCombineLogRepository.findByIotSensorCombineId(iotSensorCombine);
                                    if (BeanUtils.isNull(iotSensorCombineLog)) {
                                        iotSensorCombineLog = new IotSensorCombineLog();
                                    }
                                    iotSensorCombineLog.setAlertTime(new Date());
                                    iotSensorCombineLog.setIotSensorCombineId(iotSensorCombine);
                                    iotSensorCombineLogRepository.save(iotSensorCombineLog);
                                }
                            });

                        } else {
                            //ไม่เข้าเงื่อนไข
                            if (BeanUtils.isNotNull(iotSensorRangeCombineLog)) {
                                iotSensorCombineLogRepository.delete(iotSensorRangeCombineLog);
                            }
                        }
                    }
                }
            }
        });
    }

    private Boolean isTimeRepeat(Integer timeAlert, String timeAlertUnit, Date currentTime, Date lastAlert) {
        //Calc Diff Time
        Duration duration = new Duration(new DateTime(lastAlert), new DateTime(currentTime));
        Boolean validateAlert = false;
        LOGGER.debug("Diff Hours   : {}", duration.getStandardHours());
        LOGGER.debug("Diff Min     : {}", duration.getStandardMinutes());
        LOGGER.debug("Diff Sec     : {}", duration.getStandardSeconds());
        if (timeAlertUnit.equals("H") && duration.getStandardHours() >= timeAlert) {
            validateAlert = true;
        } else if (timeAlertUnit.equals("M") && duration.getStandardMinutes() >= timeAlert) {
            validateAlert = true;
        } else if (timeAlertUnit.equals("S") && duration.getStandardSeconds() >= timeAlert) {
            validateAlert = true;
        }
        LOGGER.debug("ยังไม่ถึงรอบแจ้งเตือน");
        return validateAlert;
    }
}
        
