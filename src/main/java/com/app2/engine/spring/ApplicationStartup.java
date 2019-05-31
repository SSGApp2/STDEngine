package com.app2.engine.spring;

import com.app2.engine.constant.ServerConstant;
import com.app2.engine.job.IotSensorCombineAlertJob;
import com.app2.engine.job.MongodbExampleData;
import com.app2.engine.job.SensorAlertJob;
import com.app2.engine.job.SensorAlertRepeatJob;
import com.app2.engine.repository.custom.ParameterDetailRepositoryCustom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LogManager.getLogger(ApplicationStartup.class);

    @Autowired
    private ParameterDetailRepositoryCustom appParameterRepository;
    @Autowired
    private ParameterDetailRepositoryCustom parameterDetailRepositoryCustom;

    @Autowired
    private SensorAlertJob sensorAlertJob;

    @Autowired
    private SensorAlertRepeatJob sensorAlertJobRepeat;

    @Autowired
    private MongodbExampleData mongodbExampleData;

    @Autowired
    IotSensorCombineAlertJob iotSensorCombineAlertJob;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        LOGGER.info("ApplicationStartup.....!");
        LOGGER.info("Swagger UI : /swagger-ui.html");
        LOGGER.info("Spring Data REST : /rest-api");

        ServerConstant.WebSockerServer = parameterDetailRepositoryCustom.findByParameterCodeAndParameterValue1("50", "VCCWebSocketServer").getParameterValue2();
        sensorAlertJob.startJob();
        sensorAlertJobRepeat.startJob();
//        mongodbExampleData.startJob();
        iotSensorCombineAlertJob.startJob();
    }

}
