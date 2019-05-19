package com.app2.engine.repository.custom;

import com.app2.engine.entity.vcc.device.MainSensor;

public interface MainSensorRepositoryCustom {
    MainSensor findLastRecordByDeviceCodeAndOu(String deviceCode,String ouCode);
    MainSensor findLastRecordByDeviceCodeAndOuAndGtMaxId(String deviceCode,String ouCode,String maxId);
}
