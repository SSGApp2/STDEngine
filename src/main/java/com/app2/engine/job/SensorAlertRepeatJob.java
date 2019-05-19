package com.app2.engine.job;

import com.app2.engine.Service.SensorRange.SensorRangeService;
import com.app2.engine.Service.linenotify.LineNotifyService;
import com.app2.engine.constant.ServerConstant;
import com.app2.engine.entity.model.MainSensorModel;
import com.app2.engine.entity.vcc.iot.IotSensorRange;
import com.app2.engine.entity.vcc.iot.IotSensorRangeLog;
import com.app2.engine.repository.IotSensorRangeLogRepository;
import com.app2.engine.repository.IotSensorRangeRepository;
import com.app2.engine.util.BeanUtils;
import com.app2.engine.util.LineMessageUtil;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensorAlertRepeatJob {
    private static final Logger LOGGER = LogManager.getLogger(SensorAlertRepeatJob.class);

    @Autowired
    WebSocketStompClient stompClient;

    @Autowired
    IotSensorRangeLogRepository iotSensorRangeLogRepository;

    @Autowired
    SensorRangeService sensorRangeService;

    @Autowired
    IotSensorRangeRepository iotSensorRangeRepository;

    @Autowired
    private LineNotifyService lineNotifyService;



    public void startJob() {
        //initial
        stompClient.connect("ws://" + ServerConstant.WebSockerServer + "/ws", new StompSessionHandlerAdapter() {
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
                MainSensorModel mainSensorModel = ((MainSensorModel) payload);
                String deviceCode = mainSensorModel.getDeviceName();
                String ouCode = mainSensorModel.getOuCode();
                Gson gson = new Gson();
                DateTime currentTime = new DateTime();
                for (IotSensorRange iotSensorRange : iotSensorRangeRepository.findByDeviceCodeAndOuCode(deviceCode, ouCode)) {

                    Long id = iotSensorRange.getId();
                    String sensorCode = iotSensorRange.getSensorCode();
                    String sensorStatus = mainSensorModel.getStatusBySensorCode(sensorCode);
                    Double currentValue = mainSensorModel.getValueBySensorCode(sensorCode);
                    IotSensorRangeLog iotSensorRangeLog = iotSensorRangeLogRepository.findByIotSensorRangeId(id);
                    if (BeanUtils.isNull(iotSensorRangeLog)) {
                        continue;
                    }
                    LOGGER.debug("====================================================");
                    LOGGER.debug("deviceCode {} :", deviceCode);
                    LOGGER.debug("sensorCode {} :", sensorCode);
                    LOGGER.debug("currentValue {} :", currentValue);
                    LOGGER.debug("sensorStatus {} :", sensorStatus);
                    LOGGER.debug("====================================================");

                    Integer timeAlert = null;
                    String timeAlertUnit = null;
                    Integer dangerAlert = iotSensorRange.getDangerAlert();
                    String dangerAlertUnit = iotSensorRange.getDangerUnit();
                    LOGGER.debug("Danger {} {}", dangerAlert, dangerAlertUnit);
                    Integer warningAlert = iotSensorRange.getWarningAlert();
                    String warningAlertUnit = iotSensorRange.getWarningUnit();
                    LOGGER.debug("Warning {} {}", warningAlert, warningAlertUnit);
                    LOGGER.debug("Status {}", sensorStatus);

                    boolean isHasSetting = false;
                    //ตรวจสอบว่ามีการ set ค่า Repeat หรือไม่
                    if (String.valueOf(sensorStatus).equals("danger") && BeanUtils.isNotNull(dangerAlert) && BeanUtils.isNotNull(dangerAlertUnit)) {
                        timeAlert = dangerAlert;
                        timeAlertUnit = dangerAlertUnit;
                        isHasSetting = true;
                    } else if (String.valueOf(sensorStatus).equals("warning") && BeanUtils.isNotNull(warningAlert) && BeanUtils.isNotNull(warningAlertUnit)) {
                        timeAlert = warningAlert;
                        timeAlertUnit = warningAlertUnit;
                        isHasSetting = true;
                    }
                    if (isHasSetting) {
                        //Calc Diff Time
                        DateTime alertTime = new DateTime(iotSensorRangeLog.getAlertTime());
                        Duration duration = new Duration(alertTime, currentTime);
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
                        if (validateAlert) {
                            String messageFooter = iotSensorRange.getMessageRepeatAlert(sensorStatus);
                            String lineMessage = LineMessageUtil.toMessage(iotSensorRange, sensorStatus, currentValue) + "\n" + messageFooter;
                            String lineToken = iotSensorRange.getLineToken();

                            Map<String, String> mapPostAlertJson = new HashMap<>();
                            mapPostAlertJson.put("message", lineMessage);
                            mapPostAlertJson.put("token", lineToken);
                            mapPostAlertJson.put("iotSensor", String.valueOf(id));


                            lineNotifyService.postAsyncMessageWithToken(gson.toJson(mapPostAlertJson)).addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    LOGGER.error("Error Line {}", throwable.getMessage(), throwable);
                                }

                                @Override
                                public void onSuccess(ResponseEntity<String> stringResponseEntity) {
                                    Gson gson = new Gson();
                                    Map<String, String> mapping = gson.fromJson(stringResponseEntity.getBody(), Map.class);
                                    Long iotSensor = Long.parseLong(mapping.get("iotSensor"));
                                    IotSensorRangeLog iotSensorRangeLog = iotSensorRangeLogRepository.findByIotSensorRangeId(iotSensor);
                                    if (BeanUtils.isNull(iotSensorRangeLog)) {
                                        iotSensorRangeLog = new IotSensorRangeLog();
                                    }
                                    iotSensorRangeLog.setAlertTime(new Date());
                                    iotSensorRangeLog.setIotSensorRangeId(iotSensor);
                                    iotSensorRangeLogRepository.save(iotSensorRangeLog);
                                }
                            });
                        }
                    }
                }
                LOGGER.debug("deviceCode {} :", deviceCode);
                LOGGER.debug("ouCode {} :", ouCode);

                LOGGER.info("Used Time/Process {} SensorAlertJobRepeat ", System.currentTimeMillis() - stTime1);
                LOGGER.info("=============================================");
            }
        });

    }
}
