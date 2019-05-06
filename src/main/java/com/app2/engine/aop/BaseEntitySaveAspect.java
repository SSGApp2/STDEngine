package com.app2.engine.aop;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@Aspect
@Component
public class BaseEntitySaveAspect {
    private static final Logger LOGGER = LogManager.getLogger(BaseEntitySaveAspect.class);

    @Pointcut("execution(* org.springframework.data.jpa.repository.**.save*(..)) " +
            "or execution(* org.springframework.data.repository.**.save*(..))")
    public void jpaRepositorySave() {
    }

    @Before("jpaRepositorySave()")
    public void addStampStandardField(JoinPoint jp) {
        String progId = "SYSTEM";
        String user = "SYSTEM";
        try {
            for (Object arg : jp.getArgs()) {
                LOGGER.debug("addStandardFields: arg: " + arg.getClass().getName());
                Date currentDate = Calendar.getInstance().getTime();

                if (arg instanceof ArrayList<?>) {
                    for (Object obj : (ArrayList) arg) {
                        assignValueToCommonFields(obj, user, progId, currentDate);
                    }
                } else {
                    assignValueToCommonFields(arg, user, progId, currentDate);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error : {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private void assignValueToCommonFields(Object arg, String user, String progId, Date currentDate) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        BeanUtils.setProperty(arg, "updatedBy", user);
        BeanUtils.setProperty(arg, "updatedDate", currentDate);
        if (!(BeanUtils.getProperty(arg, "id") != null && BeanUtils.getProperty(arg, "id").trim().length() > 0)) {
            BeanUtils.setProperty(arg, "createdBy", user);
            BeanUtils.setProperty(arg, "createdDate", currentDate);
        }
    }

}
