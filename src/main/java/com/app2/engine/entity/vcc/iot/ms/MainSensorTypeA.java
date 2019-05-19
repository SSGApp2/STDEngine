package com.app2.engine.entity.vcc.iot.ms;

import com.app2.engine.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode(of = {"id"})
@Table(name = "MAIN_SENSOR_TYPE_A")
public class MainSensorTypeA extends BaseEntity {

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

}
