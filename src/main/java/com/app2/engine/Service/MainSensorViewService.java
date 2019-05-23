package com.app2.engine.Service;

import com.app2.engine.repository.MainSensorTypeARepository;
import com.app2.engine.repository.custom.MainSensorTypeARepositoryCustom;
import com.app2.engine.repository.custom.MainSensorTypeBRepositoryCustom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MainSensorViewService {
    private static final Logger LOGGER = LogManager.getLogger(MainSensorViewService.class);

    @Autowired
    private MainSensorTypeARepositoryCustom mainSensorTypeARepositoryCustom; //every 1 min

    @Autowired
    private MainSensorTypeBRepositoryCustom mainSensorTypeBRepositoryCustom; //every 1 Hours

    public List<Map> findByCriteria(String deviceCode, String sensorCode, String ouCode, Date dateFrom, Date dateTo) {
        DateTime dtFrom = new DateTime(dateFrom);
        DateTime dtDateTo = new DateTime(dateTo);
        int diffDay = Days.daysBetween(dtFrom, dtDateTo).getDays();
        LOGGER.debug("Diff day {}", diffDay);
        if (diffDay < 7) {
            return mainSensorTypeARepositoryCustom.findByDateFromTo(deviceCode, sensorCode, dateFrom, dateTo, ouCode);
        } else {
            return mainSensorTypeBRepositoryCustom.findByDateFromTo(deviceCode, sensorCode, dateFrom, dateTo, ouCode);
        }
    }

}
