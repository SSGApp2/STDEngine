package com.app2.engine.repository;

import com.app2.engine.entity.vcc.iot.IotSensorRangeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IotSensorRangeRepository extends JpaRepository<IotSensorRangeView, Long> {
    @Query("select o from IotSensorRangeView o where  o.isActive='Y' order by o.ouCode,o.deviceCode ")
    List<IotSensorRangeView> findByStatusActive();

    @Query("select o from IotSensorRangeView o where  o.isActive='Y' and o.deviceCode =:deviceCode and o.ouCode=:ouCode order by o.ouCode,o.deviceCode ")
    List<IotSensorRangeView> findByDeviceCodeAndOuCode(@Param("deviceCode") String deviceCode,@Param("ouCode") String ouCode);
}
