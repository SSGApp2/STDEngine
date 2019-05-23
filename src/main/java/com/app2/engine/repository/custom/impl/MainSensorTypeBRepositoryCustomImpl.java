package com.app2.engine.repository.custom.impl;

import com.app2.engine.entity.vcc.iot.ms.MainSensorViewTypeB;
import com.app2.engine.repository.custom.MainSensorTypeBRepositoryCustom;
import com.app2.engine.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class MainSensorTypeBRepositoryCustomImpl implements MainSensorTypeBRepositoryCustom {
    private static final Logger LOGGER = LogManager.getLogger(MainSensorTypeBRepositoryCustomImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Map> findByDateFromTo(String deviceCode, String sensorCode, Date dateFrom, Date dateTo, String ouCode) {
        Date currentDate = DateUtil.getCurrentDate();

        Criteria criteria = ((Session) em.getDelegate()).createCriteria(MainSensorViewTypeB.class);
        criteria.add(Restrictions.eq("deviceName", deviceCode));
        criteria.add(Restrictions.eq("ouCode", ouCode));
        criteria.add(Restrictions.ge("date", DateUtil.getDateWithRemoveTime(dateFrom)));
        criteria.add(Restrictions.le("date", DateUtil.getDateWithMaxTime(dateTo)));

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property(sensorCode), "y");
        projectionList.add(Projections.property("date"), "x");
        criteria.setProjection(projectionList);
        criteria.addOrder(Order.asc("date"));
        criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP); //TOMAP
        return criteria.list();
    }

    @Override
    public String findMaxIdByDeviceCodeAndOuCode(String device, String ouCode) {
        Criteria criteria = ((Session) em.getDelegate()).createCriteria(MainSensorViewTypeB.class);
        criteria.add(Restrictions.eq("deviceName", device));
        criteria.add(Restrictions.eq("ouCode", ouCode));
        criteria.add(Restrictions.isNotNull("_id"));
        ProjectionList proj = Projections.projectionList();
        proj.add(Projections.max("_id"));
        proj.add(Projections.groupProperty("deviceName"));
        proj.add(Projections.groupProperty("ouCode"));
        criteria.setProjection(proj);
        List list = criteria.list();
        String maxId = null;
        if (list.size() > 0) {
            Object[] row = (Object[]) list.get(0);
            maxId = String.valueOf(row[0]);
        }
        return maxId;
    }
}
