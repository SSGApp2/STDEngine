package com.app2.engine.entity.vcc.iot;

import com.app2.engine.util.BeanUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Immutable
@Table(name = "V_IOT_SENSOR_RANGE")
@EqualsAndHashCode(of = {"id"})
public class IotSensorRange {

    @Id
    @Column(updatable = false, nullable = false)
    private Long id;

    @NotNull
    private String deviceCode;

    @NotNull
    private String sensorCode;

    private String macName;


    private String sensorName;

    private String sensorUnit ;

    private Double normalValue;

    private Double dangerAmt;
    private Integer dangerAlert;
    private String dangerUnit;

    private Double warningAmt;
    private Integer warningAlert;
    private String warningUnit;

    private String valueType;
    private String displayType;

    private String lineToken;

    private String isActive;

    private String ouCode;


    public Boolean isRepeatDanger() {
        if (BeanUtils.isNotNull(this.dangerAlert) && BeanUtils.isNotNull(this.dangerUnit)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isRepeatWarning() {
        if (BeanUtils.isNotNull(this.warningAlert) && BeanUtils.isNotNull(this.warningUnit)) {
            return true;
        } else {
            return false;
        }
    }

    public String getMessageRepeatAlert(String status) {
        String result = null;
        Integer timeAlert = null;
        String timeAlertUnit=null;
        if (this.isRepeatDanger() || this.isRepeatWarning()) {
            if (String.valueOf(status).equals("danger")) {
                timeAlert = this.dangerAlert;
                timeAlertUnit = this.dangerUnit;
            } else if (String.valueOf(status).equals("warning")) {
                timeAlert = this.warningAlert;
                timeAlertUnit = this.warningUnit;
            }
        }
        if (BeanUtils.isNotNull(timeAlert) && BeanUtils.isNotNull(timeAlertUnit)) {
            if (timeAlertUnit.equals("H")) {
                result="Alert every : "+timeAlert+" hours";
            } else if (timeAlertUnit.equals("M")) {
                result="Alert every : "+timeAlert+" minutes";
            } else if (timeAlertUnit.equals("S")) {
                result="Alert every : "+timeAlert+" seconds";
            }
        }
        result="";
        return result;
    }
    
}
