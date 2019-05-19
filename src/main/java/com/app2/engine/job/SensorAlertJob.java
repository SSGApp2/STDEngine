package com.app2.engine.job;

import com.app2.engine.Service.SensorRange.SensorRangeService;
import com.app2.engine.Service.linenotify.LineNotifyService;
import com.app2.engine.constant.ServerConstant;
import com.app2.engine.entity.model.MainSensorModel;
import com.app2.engine.entity.vcc.iot.IotSensorRange;
import com.app2.engine.entity.vcc.iot.IotSensorRangeLog;
import com.app2.engine.repository.IotSensorRangeLogRepository;
import com.app2.engine.repository.IotSensorRangeRepository;
import com.app2.engine.repository.custom.ParameterDetailRepositoryCustom;
import com.app2.engine.util.BeanUtils;
import com.app2.engine.util.LineMessageUtil;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensorAlertJob {
    private static final Logger LOGGER = LogManager.getLogger(SensorAlertJob.class);

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
                for (IotSensorRange iotSensorRange : iotSensorRangeRepository.findByDeviceCodeAndOuCode(deviceCode, ouCode)) {
                    Long id = iotSensorRange.getId();
                    String sensorCode = iotSensorRange.getSensorCode();
                    String sensorStatus = mainSensorModel.getStatusBySensorCode(sensorCode);
                    Double currentValue = mainSensorModel.getValueBySensorCode(sensorCode);
                    LOGGER.debug("====================================================");
                    LOGGER.debug("deviceCode {} :", deviceCode);
                    LOGGER.debug("sensorCode {} :", sensorCode);
                    LOGGER.debug("currentValue {} :", currentValue);
                    LOGGER.debug("sensorStatus {} :", sensorStatus);
                    LOGGER.debug("====================================================");
                    //Find Last Log
                    IotSensorRangeLog iotSensorRangeLog = iotSensorRangeLogRepository.findByIotSensorRangeId(id);
                    Boolean isEmptyLog = BeanUtils.isNull(iotSensorRangeLog);
                    if (BeanUtils.isNotNull(sensorStatus)) {
                        Boolean isAlertDanger = sensorStatus.equals("danger") && (iotSensorRange.isRepeatDanger() == false || isEmptyLog);
                        Boolean isAlertWarning = sensorStatus.equals("warning") && (iotSensorRange.isRepeatWarning() == false || isEmptyLog);
                        if (isAlertDanger || isAlertWarning) {
                            //ถึงเกณฑ์ที่ต้องเริ่มแจ้งเตือน
                            String lineToken = iotSensorRange.getLineToken();
                            String lineMessage = LineMessageUtil.toMessage(iotSensorRange, sensorStatus, currentValue);
                            String messageFooter = iotSensorRange.getMessageRepeatAlert(sensorStatus);//repeat alert


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
                    } else {
                        if (BeanUtils.isNotNull(iotSensorRangeLog)) {
                            iotSensorRangeLogRepository.delete(iotSensorRangeLog);
                        }
                    }
                }
                LOGGER.info("Used  Time/Process SensorAlertJob {} ", System.currentTimeMillis() - stTime1);
                LOGGER.info("=============================================");
            }
        });

    }
}
