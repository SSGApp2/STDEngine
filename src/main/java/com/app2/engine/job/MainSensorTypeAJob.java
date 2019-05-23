package com.app2.engine.job;

import com.app2.engine.entity.vcc.device.MainSensor;
import com.app2.engine.entity.vcc.iot.DeviceMachineView;
import com.app2.engine.entity.vcc.iot.ms.MainSensorViewTypeA;
import com.app2.engine.repository.DeviceMachineViewRepository;
import com.app2.engine.repository.MainSensorTypeARepository;
import com.app2.engine.repository.custom.MainSensorRepositoryCustom;
import com.app2.engine.repository.custom.MainSensorTypeARepositoryCustom;
import com.app2.engine.util.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class MainSensorTypeAJob {
    private static final Logger LOGGER = LogManager.getLogger(MainSensorTypeAJob.class);

    @Autowired
    private MainSensorTypeARepository mainSensorTypeARepository;
    @Autowired
    private MainSensorTypeARepositoryCustom mainSensorTypeARepositoryCustom;
    @Autowired
    private DeviceMachineViewRepository deviceMachineViewRepository;
    @Autowired
    private MainSensorRepositoryCustom mainSensorRepositoryCustom;

    @Transactional
//    @Scheduled(fixedDelay = 2500)
    @Scheduled(cron = "0 * * ? * *") //every min
    public void saveData() {
        LOGGER.info("=================Start Job MainSensorTypeAJob=================");
        for (DeviceMachineView deviceMachineView : deviceMachineViewRepository.findAll()) {
            String deviceCode = deviceMachineView.getDeviceCode();
            String ouCode = deviceMachineView.getOuCode();
            String maxId = mainSensorTypeARepositoryCustom.findMaxIdByDeviceCodeAndOuCode(deviceCode, ouCode);
            MainSensor mainSensor;
            if (BeanUtils.isNotNull(maxId)) {
                mainSensor = mainSensorRepositoryCustom.findLastRecordByDeviceCodeAndOuAndGtMaxId(deviceCode, ouCode, maxId);
            } else {
                mainSensor = mainSensorRepositoryCustom.findLastRecordByDeviceCodeAndOu(deviceCode, ouCode);
            }
            if (BeanUtils.isNotNull(mainSensor)) {
                //Have new Data
                MainSensorViewTypeA mainSensorTypeA = new MainSensorViewTypeA();
                mainSensorTypeA.convertMainSensor(mainSensor);
                mainSensorTypeA.setOuCode(ouCode);
                mainSensorTypeARepository.save(mainSensorTypeA);
                LOGGER.debug("New data {}", mainSensorTypeA.get_id());
            }
        }
    }

}