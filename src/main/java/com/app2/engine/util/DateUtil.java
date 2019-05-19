package com.app2.engine.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

    public static Locale getSystemLocale() {
        return Locale.US;
    }

    public static Timestamp getCurrentDate() {
        Timestamp today = null;
        try {
            Date nowDate = Calendar.getInstance().getTime();
            today = new java.sql.Timestamp(nowDate.getTime());
        } catch (Exception e) {
            LOGGER.error("error msg : {} ", e);
            throw new RuntimeException(e);
        }
        return today;
    }

    public static Timestamp getDateWithRemoveTime(Date date) {
        SimpleDateFormat newformat = new SimpleDateFormat("yyyy-MM-dd", DateUtil.getSystemLocale());
        Timestamp maxTimeDate = Timestamp.valueOf(newformat.format(date) + " " + "00:00:00.000");

        return maxTimeDate;
    }

    public static Timestamp getDateWithMaxTime(Date date) {
        SimpleDateFormat newformat = new SimpleDateFormat("yyyy-MM-dd", DateUtil.getSystemLocale());
        Timestamp minTimeDate = Timestamp.valueOf(newformat.format(date) + " " + "23:59:59.999");
        return minTimeDate;
    }


}
