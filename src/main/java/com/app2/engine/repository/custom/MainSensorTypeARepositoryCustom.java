package com.app2.engine.repository.custom;

import com.app2.engine.entity.vcc.iot.ms.MainSensorTypeA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


public interface MainSensorTypeARepositoryCustom {
    List<Map> findBySensorCode(String device, String sensorCode, String oucode);
    List<Map> findLastByDeviceCodeAndOuMaxSize(String device, String ouCode,Integer maxSize);

    String findMaxIdByDeviceCodeAndOuCode(String device,String ouCode);
}
