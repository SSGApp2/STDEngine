package com.app2.engine.entity.vcc.iot;

import com.app2.engine.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode(of = {"id"})
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"iotSensorRangeId"})})
public class IotSensorRangeLog extends BaseEntity {

    @Temporal(TemporalType.TIMESTAMP)
    private Date alertTime;

    private Long iotSensorRangeId;
}
