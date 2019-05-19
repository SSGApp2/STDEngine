package com.app2.engine.entity.vcc.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "MainSensor")
public class MainSensor {
    @Id
    private String _id;

    @Field("DeviceName")
    private String deviceName;

    @Field("Status")
    private String status;

    @Field("Date")
    private String date;

    @Field("Time")
    private String time;

    @Field("Temp")
    private String temp;

    @Field("Pressu")
    private String pressu;

    @Field("Acust")
    private String acust;

    @Field("Humid")
    private String humid;

    @Field("Light")
    private String light;

    @Field("Vibra")
    private ArrayList<Object> vibra;

    @Field("MStatus")
    private String mStatus;

    public Date getDateTime() {
        try {
            String dateStr = date + " " + time;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date dt = sdf.parse(dateStr);
            return dt;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
