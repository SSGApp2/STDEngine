package com.app2.engine.repository;

import com.app2.engine.entity.vcc.iot.IotSensorRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IotSensorRangeRepository extends JpaRepository<IotSensorRange,Long>{
    @Query("select o from IotSensorRange o where  o.isActive='Y' order by o.ouCode,o.deviceCode ")
    List<IotSensorRange> findByStatusActive();

    List<IotSensorRange> findByDeviceCodeAndOuCode(String deviceCode, String ouCode);
}
