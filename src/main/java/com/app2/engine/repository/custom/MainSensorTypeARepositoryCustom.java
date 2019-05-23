package com.app2.engine.repository.custom;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface MainSensorTypeARepositoryCustom {
    List<Map> findBySensorCode(String device, String sensorCode, String oucode);

    List<Map> findLastByDeviceCodeAndOuMaxSize(String device, String ouCode, Integer maxSize);

    List<Map> findByDateFromTo(String deviceCode, String sensorCode, Date dateFrom, Date dateTo, String ouCode);

    String findMaxIdByDeviceCodeAndOuCode(String device, String ouCode);
}
