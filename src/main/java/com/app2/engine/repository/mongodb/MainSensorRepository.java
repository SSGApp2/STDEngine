package com.app2.engine.repository.mongodb;

import com.app2.engine.entity.vcc.device.MainSensor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MainSensorRepository extends MongoRepository<MainSensor, ObjectId> {

}
