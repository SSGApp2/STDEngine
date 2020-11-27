package com.app2.engine.aop;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Log4j2
@Component
@Aspect
public class LoggingAspect {

    @Pointcut("@annotation(com.app2.engine.aop.annotation.Loggable)")
    public void loggableMethods() {
    }


    @Before("loggableMethods()")
    public void logMethod(JoinPoint jp) {
        String methodName = jp.getSignature().getName();
        Object[] signatureArgs = jp.getArgs();
        log.info("=================== Executing method: {} ARG : {}", methodName, signatureArgs);
    }

}
