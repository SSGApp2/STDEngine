package com.app2.engine.repository.custom;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MainSensorTypeBRepositoryCustom {
    List<Map> findByDateFromTo(String deviceCode, String sensorCode, Date dateFrom, Date dateTo, String ouCode);
    String findMaxIdByDeviceCodeAndOuCode(String device, String ouCode);
}
