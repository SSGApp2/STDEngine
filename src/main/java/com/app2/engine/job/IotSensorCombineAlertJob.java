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
                List<String> sensorCode = new ArrayList<>();
                List<Double> valueSensor = new ArrayList<>();
                List<IotSensorCombineView> iotSensorCombineView
                        = iotSensorCombineRepository.findByDeviceCodeAndOuCode(deviceCode, ouCode);
                if (!iotSensorCombineView.isEmpty() && iotSensorCombineView.size() != 0) {


                    List<IotSensorCombine> iotSensorCombines = iotSensorCombineView.get(0).groupByCombineId(iotSensorCombineView);

                    for (IotSensorCombine iotSensorCombine : iotSensorCombines) {
                        IotSensorCombineLog iotSensorCombineLog = iotSensorCombineLogRepository
                                .findByIotSensorCombineId(iotSensorCombine.getId());

                        if (iotSensorCombineLog != null) {
                            timeLog = iotSensorCombineLog.getAlertTime().getTime();
                            checkTimeAlert = iotSensorCombine.getIotSensorCombineViews().get(0).checkTimeLog(stTime1, timeLog);
                        }

                        for (IotSensorCombineView iotSensorCombineView1 : iotSensorCombine.getIotSensorCombineViews()) {
                            if (iotSensorCombineView1.calculateCombineRange(mainSensorModel.getValueBySensorCode(iotSensorCombineView1.getSensorCode()))) {
                                checkAlert = Boolean.TRUE;
                                sensorCode.add(iotSensorCombineView1.getSensorCode());
                                valueSensor.add(mainSensorModel.getValueBySensorCode(iotSensorCombineView1.getSensorCode()));
                            } else {
                                checkAlert = Boolean.FALSE;
                                sensorCode.clear();
                                valueSensor.clear();
                                break;
                            }
                        }

                        if (iotSensorCombineLog != null && checkAlert.equals(Boolean.FALSE)) {
                            iotSensorCombineLogRepository.delete(iotSensorCombineLog);
                        }


                        if (checkAlert.equals(Boolean.TRUE) && checkTimeAlert.equals(Boolean.TRUE)) {
                            //ถึงเกณฑ์ที่ต้องเริ่มแจ้งเตือน
                            String lineToken = iotSensorCombineView.get(0).getLineToken();
                            String lineMessage = iotSensorCombineView.get(0).alertTypeMessage() + " " + iotSensorCombineView.get(0).getAlertMessage() + "  ";
                            for (int i = 0; i < sensorCode.size(); i++) {
                                lineMessage += sensorCode.get(i) + ":" + valueSensor.get(i) + " ";
                            }

                            Map<String, String> mapPostAlertJson = new HashMap<>();
                            mapPostAlertJson.put("message", lineMessage);
                            mapPostAlertJson.put("token", lineToken);
                            mapPostAlertJson.put("iotSensorCombine", String.valueOf(iotSensorCombine.getId()));


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

                            checkAlert = Boolean.FALSE;
                            checkTimeAlert = Boolean.TRUE;
                            sensorCode = new ArrayList<>();
                            valueSensor = new ArrayList<>();
                        } else {
                            checkAlert = Boolean.FALSE;
                            checkTimeAlert = Boolean.TRUE;
                            sensorCode = new ArrayList<>();
                            valueSensor = new ArrayList<>();
                        }
                    }
                }
            }
        });
    }
}
