package com.app2.engine.controller;

import com.app2.engine.Service.MainSensorViewService;
import com.app2.engine.entity.vcc.iot.ms.MainSensorViewTypeA;
import com.app2.engine.entity.vcc.iot.ms.MainSensorViewTypeB;
import com.app2.engine.repository.MainSensorTypeARepository;
import com.app2.engine.repository.MainSensorTypeBRepository;
import com.app2.engine.repository.custom.MainSensorTypeARepositoryCustom;
import com.app2.engine.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/mainsensorviews")
public class MainSensorViewController {
    private static final Logger LOGGER = LogManager.getLogger(MainSensorViewController.class);

    @Autowired
    private MainSensorTypeARepository mainSensorTypeARepository;
    @Autowired
    private MainSensorTypeBRepository mainSensorTypeBRepository;
    @Autowired
    private MainSensorTypeARepositoryCustom mainSensorTypeARepositoryCustom;


    @Autowired
    private MainSensorViewService mainSensorViewService;

    @GetMapping("/example")
    public List<Map> example() {
        return mainSensorTypeARepositoryCustom.findBySensorCode("XDK001", "temp", "DUMMY");
    }

    @PostMapping("/findByCriteria")
    public List<Map> findByCriteria(@RequestBody String json) {
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy");
            JSONObject jsonObject = new JSONObject(json);
            String ouCode = jsonObject.getString("ouCode");
            String dateFrom = jsonObject.getString("dateFrom");
            String dateTo = jsonObject.getString("dateTo");
            String deviceCode = jsonObject.getString("deviceCode");
            String sensorCode = jsonObject.getString("sensorCode");
            return mainSensorViewService.findByCriteria(deviceCode, sensorCode, ouCode, spf.parse(dateFrom), spf.parse(dateTo));
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /***********************************************************************************
     *
     *                      MAKE DATA
     *
     ***********************************************************************************/
    @Transactional
    @GetMapping("/makeDataA")
    public void makeDataA(@RequestParam(value = "deviceName")String deviceName) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date current = format.parse("2019-01-01");
            DateTime dt = new DateTime(DateUtil.getDateWithRemoveTime(current));
            Date currentMax = format.parse("2019-12-31");
            int count = 0;
            while (dt.toDate().compareTo(currentMax) < 0) {
                dt = dt.plusMinutes(1);
                MainSensorViewTypeA mainSensorModel = new MainSensorViewTypeA();
                mainSensorModel.set_id(String.valueOf(count));
                mainSensorModel.setDeviceName(deviceName);
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

    @Transactional
    @GetMapping("/makeDataB")
    public void makeDataB(@RequestParam(value = "deviceName")String deviceName) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date current = format.parse("2019-01-01");
            DateTime dt = new DateTime(DateUtil.getDateWithRemoveTime(current));
            Date currentMax = format.parse("2019-12-31");
            int count = 0;
            while (dt.toDate().compareTo(currentMax) < 0) {
                dt = dt.plusHours(1);
                MainSensorViewTypeB mainSensorModel = new MainSensorViewTypeB();
                mainSensorModel.set_id(String.valueOf(count));
                mainSensorModel.setDeviceName(deviceName);
                mainSensorModel.setAcust(Math.random() * 100 + 1);
                mainSensorModel.setHumid(Math.random() * 100 + 1);
                mainSensorModel.setLight(Math.random() * 100 + 1);
                mainSensorModel.setPressu(Math.random() * 100 + 1);
                mainSensorModel.setTemp(Math.random() * 100 + 1);
                mainSensorModel.setOuCode("DEV");
                mainSensorModel.setDate(dt.toDate());
                mainSensorTypeBRepository.save(mainSensorModel);
                count++;
            }
            LOGGER.debug("Example Job Save Data {} ", count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
