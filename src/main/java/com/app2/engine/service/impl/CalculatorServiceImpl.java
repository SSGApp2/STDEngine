package com.app2.engine.service.impl;

import com.app2.engine.service.CalculatorService;
import com.app2.engine.soap.CalculatorClient;
import com.app2.engine.spring.config.SoapClientConfiguration;
import com.calculator.wsdl.AddResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class CalculatorServiceImpl implements CalculatorService {

    private static Logger LOGGER = LoggerFactory.getLogger(CalculatorServiceImpl.class);

    @Override
    public AddResponse add(Integer a, Integer b) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SoapClientConfiguration.class);
        CalculatorClient weatherClient = context.getBean(CalculatorClient.class);
        AddResponse response = weatherClient.calcAdd(a, b);
        context.close();
        return response;
    }
}
