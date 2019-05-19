package com.app2.engine.util;

import com.app2.engine.entity.vcc.iot.IotSensorRangeView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LineMessageUtil {

    //    //      Configuration
//    String emojiDanger = String.valueOf(Character.toChars(Integer.decode("0x1000A4"))) + " ";
//    String emojiWarning = String.valueOf(Character.toChars(Integer.decode("0x100036"))) + " ";
//    String imageDanger = "/usr/local/VCC/images/danger1.jpg";
//    String imageWarning = "/usr/local/VCC/images/warning1.png";
//
    public static String toMessage(IotSensorRangeView iotSensorRange, String status, Double currentValue) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
        StringBuilder stringBuilder = new StringBuilder();
        String imagePath = null;
        String valueStr;
        String unit = "";
//        String unit = SensorValueCurrent.getUnit(sensorCode);
        String currentDate = dt.format(new Date());
//        currentDate = "";//not show
        if (status.contains("danger")) {
//            imagePath = imageDanger;
            stringBuilder.append("Danger " + currentDate + "\n");
            valueStr = "Value : " + numberWithCommas(currentValue);

        } else {
//            imagePath = imageWarning;
            stringBuilder.append("Warning " + currentDate + "\n");
            valueStr = "Value : " + numberWithCommas(currentValue);
        }
        if (BeanUtils.isNotNull(unit)) {
            valueStr += " " + unit;
        }
        stringBuilder.append("Machine : " + iotSensorRange.getMacName() + "\n");
        stringBuilder.append("Sensor : " + iotSensorRange.getSensorName() + "\n");
        stringBuilder.append(valueStr);
        return stringBuilder.toString();
    }

    public static String toMessageCombine(IotSensorRangeView iotSensorRange, String status, Double currentValue) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
        StringBuilder stringBuilder = new StringBuilder();
        String imagePath = null;
        String valueStr;
        String currentDate = dt.format(new Date());
        String unit = "";
        if (status.contains("danger")) {
//            imagePath = imageDanger;
//            stringBuilder.append("Danger " + currentDate + "\n");
            valueStr = "Value : " + numberWithCommas(currentValue);

        } else {
//            imagePath = imageWarning;
//            stringBuilder.append("Warning " + currentDate + "\n");
            valueStr = "Value : " + numberWithCommas(currentValue);
        }
        if (BeanUtils.isNotNull(unit)) {
            valueStr += " " + unit;
        }
//        stringBuilder.append("Device : " + SensorValueCurrent.getDeviceName(deviceCode) + "\n");
        stringBuilder.append("Sensor : " + iotSensorRange.getSensorName() + "\n");
        stringBuilder.append(valueStr);
        return stringBuilder.toString();
    }


    public static String numberWithCommas(Double number) {
        DecimalFormat formatter = new DecimalFormat("#,###.##########");
        return formatter.format(number);
    }

}
