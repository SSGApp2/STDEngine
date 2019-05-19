package com.app2.engine.repository;

import com.app2.engine.entity.vcc.iot.IotSensorRangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IotSensorRangeLogRepository extends JpaRepository<IotSensorRangeLog, Long> {
    IotSensorRangeLog findByIotSensorRangeId(Long id);
}
