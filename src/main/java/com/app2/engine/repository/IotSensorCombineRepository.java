package com.app2.engine.repository;

import com.app2.engine.entity.vcc.iot.IotSensorCombineView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IotSensorCombineRepository extends JpaRepository<IotSensorCombineView, Long>{

    @Query("select o from IotSensorCombineView o where  o.isActiveCombine='Y' order by o.ouCode,o.deviceCode ")
    List<IotSensorCombineView> findByStatusActive();

    @Query("select o from IotSensorCombineView o where  o.isActiveCombine='Y' and o.deviceCode =:deviceCode and o.ouCode=:ouCode order by o.iotSensorCombine ")
    List<IotSensorCombineView> findByDeviceCodeAndOuCode(@Param("deviceCode") String deviceCode, @Param("ouCode") String ouCode);
}
