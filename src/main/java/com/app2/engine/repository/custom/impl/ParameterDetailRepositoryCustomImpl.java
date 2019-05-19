package com.app2.engine.repository.custom.impl;

import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.custom.ParameterDetailRepositoryCustom;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Repository
public class ParameterDetailRepositoryCustomImpl implements ParameterDetailRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public ParameterDetail findByParameterCodeAndParameterValue1(String code, String parameterValue1) {
        Criteria criteria = ((Session) em.getDelegate()).createCriteria(ParameterDetail.class);
        criteria.createAlias("parameterHeader", "ParameterHeader");
        criteria.add(Restrictions.eq("ParameterHeader.code", code));
        criteria.add(Restrictions.eq("parameterValue1", parameterValue1));
        return (ParameterDetail) criteria.uniqueResult();
    }
}
