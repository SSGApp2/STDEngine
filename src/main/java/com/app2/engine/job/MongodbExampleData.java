package com.app2.engine.job;

import com.app2.engine.Service.SensorRange.SensorRangeService;
import com.app2.engine.Service.linenotify.LineNotifyService;
import com.app2.engine.constant.ServerConstant;
import com.app2.engine.entity.model.MainSensorModel;
import com.app2.engine.entity.vcc.device.MainSensor;
import com.app2.engine.entity.vcc.iot.IotSensorRangeLog;
import com.app2.engine.entity.vcc.iot.IotSensorRangeView;
import com.app2.engine.repository.IotSensorRangeLogRepository;
import com.app2.engine.repository.IotSensorRangeRepository;
import com.app2.engine.repository.custom.MainSensorRepositoryCustom;
import com.app2.engine.repository.mongodb.MainSensorRepository;
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
public class MongodbExampleData {
    private static final Logger LOGGER = LogManager.getLogger(MongodbExampleData.class);

    @Autowired
    WebSocketStompClient stompClient;

    @Autowired
    MainSensorRepository mainSensorRepository;
    @Autowired
    MainSensorRepositoryCustom mainSensorRepositoryCustom;

    public void startJob() {
        //initial
        String socketURL= ServerConstant.WebSockerServer.replace("http://", "");
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
                Gson gson=new Gson();
                long stTime1 = System.currentTimeMillis();
                MainSensorModel mainSensorModel = ((MainSensorModel) payload);
                MainSensor mainSensor=gson.fromJson(gson.toJson(mainSensorModel),MainSensor.class);
                mainSensorRepository.save(mainSensor);
            }
        });

    }
}
