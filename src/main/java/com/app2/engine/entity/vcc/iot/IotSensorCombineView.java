package com.app2.engine.entity.vcc.iot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
@Entity
@Immutable
@Table(name = "v_iot_sensor_combine_range")
@EqualsAndHashCode(of = {"id"})
public class IotSensorCombineView {
    @Id
    @Column(updatable = false, nullable = false)
    private Long id;

    @NotNull
    private String deviceCode;

    @NotNull
    private Long iotSensorCombine;

    private String macName;

    private String lineToken;

    private String isActive;

    private String ouCode;

    private Double normalValue;

    private String valueType;

    private String displayType;

    private String alertMessage;

    private String alertType;

    private Integer repeatAlert;

    private String repeatUnit;

    private Double amount;

    private String isActiveCombine;

    private String sensorCode;


    public Boolean calculateCombineRange(Double range) {
        Double checkRange = 0d;
        if (this.valueType.equals("0")) {
            if (this.displayType.equals("A")) {
                checkRange = ((this.normalValue / 100) * this.amount);
                if (!this.normalValue.equals(range)) {
                    if (range >= (checkRange+this.normalValue)) {
                        return true;
                    } else if (range <= (this.normalValue - checkRange)) {
                        return true;
                    }
                } else {
                    return false;
                }
            } else if (this.equals("P")) {
                checkRange = ((this.normalValue / 100) * this.amount) + this.normalValue;
                if (!this.normalValue.equals(range)) {
                    if (range >= checkRange) {
                        return true;
                    }
                } else {
                    return false;
                }
            } else  if(this.equals("N")){
                checkRange = ((this.normalValue / 100) * this.amount) + this.normalValue;
                checkRange = this.normalValue - checkRange;
                if (!this.normalValue.equals(range)) {
                    if (range <= checkRange) {
                        return true;
                    }
                } else {
                    return false;
                }
            }

        } else {
            if (this.displayType.equals("A")) {
                checkRange = this.amount + this.normalValue;
                if (!this.normalValue.equals(range)) {
                    if (range >= checkRange) {
                        return true;
                    } else if (range <= (this.normalValue - this.amount)) {
                        return true;
                    }
                } else {
                    return false;
                }
            } else if (this.equals("P")) {
                checkRange = this.amount + this.normalValue;
                if (!this.normalValue.equals(range)) {
                    if (range >= checkRange) {
                        return true;
                    }
                } else {
                    return false;
                }
            } else if(this.equals("N")) {
                checkRange = this.normalValue - this.amount;
                if (!this.normalValue.equals(range)) {
                    if (range <= checkRange) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public Boolean checkTimeLog(long timePresent, long timeLog) {
        long repeatTime;
        long subTime = timePresent - timeLog;
        if (this.repeatUnit.equals("H")) {
            repeatTime = (60 * 60 * 1000) * this.repeatAlert;
            if (subTime >= repeatTime) {
                return true;
            }
        } else if (this.repeatUnit.equals("M")) {
            repeatTime = (60 * 1000) * this.repeatAlert;
            if (subTime >= repeatTime) {
                return true;
            }
        } else {
            repeatTime = 1000 * this.repeatAlert;
            if (subTime >= repeatTime) {
                return true;
            }
        }

        return false;
    }

    public String alertTypeMessage() {
        return this.alertType.equals("D") ? "Danger" : "Warning";
    }

    public List<IotSensorCombine> groupByCombineId(List<IotSensorCombineView> iotSensorCombineView) {

        if (!iotSensorCombineView.isEmpty()) {
            Long idcombine = iotSensorCombineView.get(0).iotSensorCombine;
            List<IotSensorCombineView> iotSensorCombineViews = new ArrayList<>();
            List<IotSensorCombine> iotSensorCombines = new LinkedList<>();
            IotSensorCombine iotSensorCombine = new IotSensorCombine();
            for (int i = 0; i < iotSensorCombineView.size(); i++) {

                if (idcombine == iotSensorCombineView.get(i).iotSensorCombine) {
                    iotSensorCombineViews.add(iotSensorCombineView.get(i));
                } else {
                    //--Add---
                    iotSensorCombine.setId(idcombine);
                    iotSensorCombine.setIotSensorCombineViews(iotSensorCombineViews);
                    iotSensorCombines.add(iotSensorCombine);
                    //---Clear----
                    idcombine = iotSensorCombineView.get(i).iotSensorCombine;
                    iotSensorCombineViews = new ArrayList<>();
                    iotSensorCombine = new IotSensorCombine();
                    //--first-change--
                    iotSensorCombineViews.add(iotSensorCombineView.get(i));
                }
                if (i == iotSensorCombineView.size() - 1) {
                    //--Add---
                    iotSensorCombine.setId(idcombine);
                    iotSensorCombine.setIotSensorCombineViews(iotSensorCombineViews);
                    iotSensorCombines.add(iotSensorCombine);
                    //---Clear----
                    idcombine = iotSensorCombineView.get(i).iotSensorCombine;
                    iotSensorCombineViews = new ArrayList<>();
                }
            }
            return iotSensorCombines;
        }
        return null;
    }

}
