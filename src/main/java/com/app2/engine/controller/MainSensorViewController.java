package com.app2.engine.controller;

import com.app2.engine.entity.vcc.iot.ms.MainSensorTypeA;
import com.app2.engine.repository.MainSensorTypeARepository;
import com.app2.engine.repository.custom.MainSensorTypeARepositoryCustom;
import com.app2.engine.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("mainsensorviews")
public class MainSensorViewController {
    private static final Logger LOGGER = LogManager.getLogger(MainSensorViewController.class);

    @Autowired
    MainSensorTypeARepository mainSensorTypeARepository;
    @Autowired
    MainSensorTypeARepositoryCustom mainSensorTypeARepositoryCustom;

    @GetMapping("/example")
    public List<Map> example() {
        return mainSensorTypeARepositoryCustom.findBySensorCode("XDK001", "temp", "DUMMY");
//        return mainSensorTypeARepository.findAll();
    }

    @Transactional
    @GetMapping("/makeData")
    public void makeData(@RequestParam(value = "feq") Integer feq) {
        mainSensorTypeARepository.deleteAll();
        Date current = DateUtil.getCurrentDate();
        DateTime dt = new DateTime(DateUtil.getDateWithRemoveTime(current));
        Date currentMax = DateUtil.getDateWithMaxTime(current);
        int count=0;
        while (dt.toDate().compareTo(currentMax)<0) {
            dt = dt.plusMinutes(feq);
            MainSensorTypeA mainSensorModel = new MainSensorTypeA();
            mainSensorModel.setDeviceName("XDK001");
            mainSensorModel.setAcust((new Random().nextDouble()));
            mainSensorModel.setHumid((new Random().nextDouble()));
            mainSensorModel.setLight((new Random().nextDouble()));
            mainSensorModel.setPressu((new Random().nextDouble()));
            mainSensorModel.setTemp((new Random().nextDouble()));
            mainSensorModel.setOuCode("DEV");
            mainSensorModel.setDate(dt.toDate());
            mainSensorTypeARepository.save(mainSensorModel);

            count++;
        }
        LOGGER.debug("Example Job Save Data {} ",count);
    }


}
