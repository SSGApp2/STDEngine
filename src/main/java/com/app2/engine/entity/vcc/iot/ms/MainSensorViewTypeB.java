package com.app2.engine.entity.vcc.iot.ms;

import com.app2.engine.entity.base.MainSensorViewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@Entity
@EqualsAndHashCode(of = {"id"})
@Table(name = "main_sensor_type_d", uniqueConstraints = {@UniqueConstraint(columnNames = {"_id"})})
public class MainSensorViewTypeB extends MainSensorViewBaseEntity {
    //Every 1 Hours
}
