package com.app2.engine.entity.vcc.iot.ms;

import com.app2.engine.entity.base.BaseEntity;
import com.app2.engine.entity.vcc.device.MainSensor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode(of = {"id"})
@Table(name = "MAIN_SENSOR_TYPE_A", uniqueConstraints = {@UniqueConstraint(columnNames = {"_id"})})
public class MainSensorTypeA extends BaseEntity {

    @NotNull
    private String _id;


    private String deviceName;


    private String status;


    private String time;


    private Double temp;


    private Double pressu;


    private Double acust;


    private Double humid;


    private Double light;

//    private ArrayList<Object> vibra;

    private String mStatus;

    private String ouCode;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public void convertMainSensor(MainSensor mainSensor){
        this._id=mainSensor.get_id();
        this.status=mainSensor.getStatus();
        this.time=mainSensor.getTime();
        this.temp=Double.valueOf(mainSensor.getTemp());
        this.pressu=Double.valueOf(mainSensor.getPressu());
        this.acust=Double.valueOf(mainSensor.getAcust());
        this.humid=Double.valueOf(mainSensor.getHumid());
        this.light=Double.valueOf(mainSensor.getLight());
        this.mStatus=(mainSensor.getMStatus());
        this.date=mainSensor.getDateTime();
        this.deviceName=mainSensor.getDeviceName();
    }

}
