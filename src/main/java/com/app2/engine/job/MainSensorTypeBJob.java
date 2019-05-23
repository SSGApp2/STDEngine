package com.app2.engine.job;

import com.app2.engine.entity.vcc.device.MainSensor;
import com.app2.engine.entity.vcc.iot.DeviceMachineView;
import com.app2.engine.entity.vcc.iot.ms.MainSensorViewTypeB;
import com.app2.engine.repository.DeviceMachineViewRepository;
import com.app2.engine.repository.MainSensorTypeBRepository;
import com.app2.engine.repository.custom.MainSensorRepositoryCustom;
import com.app2.engine.repository.custom.MainSensorTypeBRepositoryCustom;
import com.app2.engine.util.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class MainSensorTypeBJob {
    private static final Logger LOGGER = LogManager.getLogger(MainSensorTypeBJob.class);

    @Autowired
    private MainSensorTypeBRepository mainSensorTypeBRepository;
    @Autowired
    private MainSensorTypeBRepositoryCustom mainSensorTypeBRepositoryCustom;
    @Autowired
    private DeviceMachineViewRepository deviceMachineViewRepository;
    @Autowired
    private MainSensorRepositoryCustom mainSensorRepositoryCustom;

    @Transactional
//    @Scheduled(fixedDelay = 2500)
    @Scheduled(cron = "0 0 * ? * *") //every Hours
    public void saveData() {
        LOGGER.info("=================Start Job MainSensorTypeBJob=================");
        for (DeviceMachineView deviceMachineView : deviceMachineViewRepository.findAll()) {
            String deviceCode = deviceMachineView.getDeviceCode();
            String ouCode = deviceMachineView.getOuCode();
            String maxId = mainSensorTypeBRepositoryCustom.findMaxIdByDeviceCodeAndOuCode(deviceCode, ouCode);
            MainSensor mainSensor;
            if (BeanUtils.isNotNull(maxId)) {
                mainSensor = mainSensorRepositoryCustom.findLastRecordByDeviceCodeAndOuAndGtMaxId(deviceCode, ouCode, maxId);
            } else {
                mainSensor = mainSensorRepositoryCustom.findLastRecordByDeviceCodeAndOu(deviceCode, ouCode);
            }
            if (BeanUtils.isNotNull(mainSensor)) {
                //Have new Data
                MainSensorViewTypeB mainSensorTypeB = new MainSensorViewTypeB();
                mainSensorTypeB.convertMainSensor(mainSensor);
                mainSensorTypeB.setOuCode(ouCode);
                mainSensorTypeBRepository.save(mainSensorTypeB);
                LOGGER.debug("New data {}", mainSensorTypeB.get_id());
            }
        }
    }

}
