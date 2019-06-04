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
import java.util.*;

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
                    if (range >= (checkRange + this.normalValue)) {
                        return Boolean.TRUE;
                    } else if (range <= (this.normalValue - checkRange)) {
                        return Boolean.TRUE;
                    }
                } else {
                    return Boolean.TRUE;
                }
            } else if (this.equals("P")) {
                checkRange = ((this.normalValue / 100) * this.amount) + this.normalValue;
                if (!this.normalValue.equals(range)) {
                    if (range >= checkRange) {
                        return Boolean.TRUE;
                    }
                } else {
                    return Boolean.FALSE;
                }
            } else if (this.equals("N")) {
                checkRange = ((this.normalValue / 100) * this.amount) + this.normalValue;
                checkRange = this.normalValue - checkRange;
                if (!this.normalValue.equals(range)) {
                    if (range <= checkRange) {
                        return Boolean.TRUE;
                    }
                } else {
                    return Boolean.FALSE;
                }
            }

        } else {
            if (this.displayType.equals("A")) {
                checkRange = this.amount + this.normalValue;
                if (!this.normalValue.equals(range)) {
                    if (range >= checkRange) {
                        return Boolean.TRUE;
                    } else if (range <= (this.normalValue - this.amount)) {
                        return Boolean.TRUE;
                    }
                } else {
                    return Boolean.FALSE;
                }
            } else if (this.equals("P")) {
                checkRange = this.amount + this.normalValue;
                if (!this.normalValue.equals(range)) {
                    if (range >= checkRange) {
                        return Boolean.TRUE;
                    }
                } else {
                    return Boolean.FALSE;
                }
            } else if (this.equals("N")) {
                checkRange = this.normalValue - this.amount;
                if (!this.normalValue.equals(range)) {
                    if (range <= checkRange) {
                        return Boolean.TRUE;
                    }
                } else {
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public Boolean checkTimeLog(long timePresent, long timeLog) {
        long repeatTime;
        long subTime = timePresent - timeLog;
        if (this.repeatUnit.equals("H")) {
            repeatTime = (60 * 60 * 1000) * this.repeatAlert;
            if (subTime >= repeatTime) {
                return Boolean.TRUE;
            }
        } else if (this.repeatUnit.equals("M")) {
            repeatTime = (60 * 1000) * this.repeatAlert;
            if (subTime >= repeatTime) {
                return Boolean.TRUE;
            }
        } else {
            repeatTime = 1000 * this.repeatAlert;
            if (subTime >= repeatTime) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    public String alertTypeMessage() {
        return this.alertType.equals("D") ? "Danger" : "Warning";
    }

    public List<IotSensorCombine> groupByCombineId(List<IotSensorCombineView> iotSensorCombineView) {

        if (!iotSensorCombineView.isEmpty()) {
            Long idcombine = iotSensorCombineView.get(0).iotSensorCombine;
//            String deviceCode=iotSensorCombineView.get(0).getDeviceCode();
//            if(deviceCode.equals("XDK002")){
//                deviceCode.hashCode();
//            }
            List<IotSensorCombine> iotSensorCombines = new LinkedList<>(); //header
            Map<Long, IotSensorCombine> mapCombine = new HashMap<>();

            for (int i = 0; i < iotSensorCombineView.size(); i++) {
                Long idcombine1 = iotSensorCombineView.get(i).iotSensorCombine;
                IotSensorCombine header = new IotSensorCombine();
                List<IotSensorCombineView> detail = new ArrayList<>();

                if (mapCombine.containsKey(idcombine1) == false) {
                    header.setIotSensorCombineViews(detail);
                    mapCombine.put(idcombine1, header);
                }
                header = mapCombine.get(idcombine1);
                header.setId(idcombine1);
                detail = header.getIotSensorCombineViews();
                detail.add(iotSensorCombineView.get(i));
                header.setIotSensorCombineViews(detail);
                mapCombine.put(idcombine1, header);
            }
            for(Map.Entry<Long, IotSensorCombine> map :mapCombine.entrySet()){
                iotSensorCombines.add(map.getValue());
            }
            return iotSensorCombines;
        }
        return null;
    }


    public String getSensorStatus(Double currentValue) {
        Double alertValue = null;
        String message = null;

        if (String.valueOf(this.valueType).equals("0")) {
            //Percent
            if (BeanUtils.isNotNull(this.amount)) {
                alertValue = (this.amount / 100D * this.normalValue);
            }

        } else {
            //Amount
            if (BeanUtils.isNotNull(this.amount)) {
                alertValue = this.amount;
            }
        }

        if ("N".equals(this.displayType)) {
            if (BeanUtils.isNotNull(alertValue) && (currentValue <= this.normalValue - alertValue)) {
                message = "danger";
            }
        } else if ("P".equals(displayType)) {
            if (BeanUtils.isNotNull(alertValue) && (currentValue >= this.normalValue + alertValue)) {
                message = "danger";
            }
        } else {
            if (BeanUtils.isNotNull(alertValue) && (currentValue <= this.normalValue - alertValue)) {
                message = "danger";
            } else if (BeanUtils.isNotNull(alertValue) && (currentValue >= this.normalValue + alertValue)) {
                message = "danger";
            }
        }
        return message;
    }
}
