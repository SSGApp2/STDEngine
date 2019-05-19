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

import java.text.SimpleDateFormat;
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

    @GetMapping("/example2")
    public List<Map> example2(
            @RequestParam(value = "deviceCode") String deviceCode,
            @RequestParam(value = "ouCode") String ouCode,
            @RequestParam(value = "maxSize") Integer maxsize
    ) {
        return mainSensorTypeARepositoryCustom.findLastByDeviceCodeAndOuMaxSize(deviceCode, ouCode, maxsize);
    }

    @Transactional
    @GetMapping("/makeData")
    public void makeData(@RequestParam(value = "feq") Integer feq) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            mainSensorTypeARepository.deleteAll();
            Date current = format.parse("2019-01-01");
            DateTime dt = new DateTime(DateUtil.getDateWithRemoveTime(current));
            Date currentMax = format.parse("2019-12-31");
            int count = 0;
            while (dt.toDate().compareTo(currentMax) < 0) {
                dt = dt.plusMinutes(1);
                MainSensorTypeA mainSensorModel = new MainSensorTypeA();
                mainSensorModel.set_id(String.valueOf(count));
                mainSensorModel.setDeviceName("XDK001");
                mainSensorModel.setAcust(Math.random() * 100 + 1);
                mainSensorModel.setHumid(Math.random() * 100 + 1);
                mainSensorModel.setLight(Math.random() * 100 + 1);
                mainSensorModel.setPressu(Math.random() * 100 + 1);
                mainSensorModel.setTemp(Math.random() * 100 + 1);
                mainSensorModel.setOuCode("DEV");
                mainSensorModel.setDate(dt.toDate());
                mainSensorTypeARepository.save(mainSensorModel);

                count++;
            }
            LOGGER.debug("Example Job Save Data {} ", count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
