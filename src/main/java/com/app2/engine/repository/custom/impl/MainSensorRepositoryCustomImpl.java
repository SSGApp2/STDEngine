package com.app2.engine.repository.custom.impl;

import com.app2.engine.entity.vcc.device.MainSensor;
import com.app2.engine.repository.custom.MainSensorRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MainSensorRepositoryCustomImpl implements MainSensorRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public MainSensor findLastRecordByDeviceCodeAndOu(String deviceCode,String ouCode) {
        Query query = new Query();
        query.limit(1);
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        query.addCriteria(Criteria.where("DeviceName").is(deviceCode));
        List<MainSensor> data = mongoTemplate.find(query, MainSensor.class);
        if(data.size()>0){
            return data.get(0);
        }else{
            return null;
        }

    }

    @Override
    public MainSensor findLastRecordByDeviceCodeAndOuAndGtMaxId(String deviceCode, String ouCode, String maxId) {
        Query query = new Query();
        query.limit(1);
        query.addCriteria(Criteria.where("DeviceName").is(deviceCode));
        query.addCriteria(Criteria.where("_id").gt(maxId));
        List<MainSensor> data = mongoTemplate.find(query, MainSensor.class);
        if(data.size()>0){
            return data.get(0);
        }else{
            return null;
        }
    }
}
