package com.app2.engine.entity.vcc.iot.ms;

import com.app2.engine.entity.base.MainSensorViewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@EqualsAndHashCode(of = {"id"})
@Table(name = "main_sensor_type_c", uniqueConstraints = {@UniqueConstraint(columnNames = {"_id"})})
public class MainSensorViewTypeA extends MainSensorViewBaseEntity {
    //Every 1 Mins
}
