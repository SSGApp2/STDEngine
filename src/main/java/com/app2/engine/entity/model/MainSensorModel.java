package com.app2.engine.entity.model;

import lombok.Data;

@Data
public class MainSensorModel {

    private String deviceName;

    private String status;

    private String date;

    private String time;

    private Double temp;
    private String tempStatus;

    private Double pressu;
    private String pressuStatus;

    private Double acust;
    private String acustStatus;

    private Double humid;
    private String humidStatus;

    private Double light;
    private String lightStatus;


    private String mStatus;

    private String ouCode;

    public String getCurrentTime() {
        return String.valueOf(this.date) + ":" + String.valueOf(this.time);
    }

    public String getStatusBySensorCode(String sensorCode) {
        switch (sensorCode) {
            case "temp":
                return this.tempStatus;
            case "pressu":
                return this.pressuStatus;
            case "acust":
                return this.acustStatus;
            case "humid":
                return this.humidStatus;
            case "light":
                return this.lightStatus;
            default:
                return null;
        }
    }

    public Double getValueBySensorCode(String sensorCode) {
        switch (sensorCode) {
            case "temp":
                return this.temp;
            case "pressu":
                return this.pressu;
            case "acust":
                return this.acust;
            case "humid":
                return this.humid;
            case "light":
                return this.light;
            default:
                return null;
        }
    }
}
