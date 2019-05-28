package com.app2.engine.repository;

import com.app2.engine.entity.vcc.iot.IotSensorCombineLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IotSensorCombineLogRepository extends JpaRepository<IotSensorCombineLog, Long> {
    IotSensorCombineLog findByIotSensorCombineId(Long id);
}
