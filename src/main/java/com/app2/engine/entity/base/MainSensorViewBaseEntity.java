package com.app2.engine.entity.base;


import com.app2.engine.entity.vcc.device.MainSensor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class MainSensorViewBaseEntity extends BaseEntity {


    @NotNull
    @Column(unique = true)
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

    public void convertMainSensor(MainSensor mainSensor) {
        this._id = mainSensor.get_id();
        this.status = mainSensor.getStatus();
        this.time = mainSensor.getTime();
        this.temp = Double.valueOf(mainSensor.getTemp());
        this.pressu = Double.valueOf(mainSensor.getPressu());
        this.acust = Double.valueOf(mainSensor.getAcust());
        this.humid = Double.valueOf(mainSensor.getHumid());
        this.light = Double.valueOf(mainSensor.getLight());
        this.mStatus = (mainSensor.getMStatus());
        this.date = mainSensor.getDateTime();
        this.deviceName = mainSensor.getDeviceName();
    }
}
