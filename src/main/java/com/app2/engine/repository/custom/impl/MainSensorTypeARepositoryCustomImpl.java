package com.app2.engine.repository.custom.impl;

import com.app2.engine.entity.vcc.iot.ms.MainSensorTypeA;
import com.app2.engine.repository.custom.MainSensorTypeARepositoryCustom;
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
public class MainSensorTypeARepositoryCustomImpl implements MainSensorTypeARepositoryCustom {
    private static final Logger LOGGER = LogManager.getLogger(MainSensorTypeARepositoryCustomImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Map> findBySensorCode(String device, String sensorCode, String ouCode) {
        Date currentDate = DateUtil.getCurrentDate();

        Criteria criteria = ((Session) em.getDelegate()).createCriteria(MainSensorTypeA.class);
        criteria.add(Restrictions.eq("deviceName", device));
        criteria.add(Restrictions.ge("date",DateUtil.getDateWithRemoveTime(currentDate)));
        criteria.add(Restrictions.le("date",DateUtil.getDateWithMaxTime(currentDate)));

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property(sensorCode), "y");
        projectionList.add(Projections.property("date"), "x");
        criteria.setProjection(projectionList);
        criteria.addOrder(Order.asc("date"));

        criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP); //TOMAP


        return criteria.list();
    }
}
