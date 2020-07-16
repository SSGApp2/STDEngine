package com.app2.engine.controller;

import com.app2.engine.service.CalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/soap/")
public class SoapClientController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CalculatorService calculatorService;

    @GetMapping("/calcAdd")
    public Integer SOAP() {
        return calculatorService.add(1, 2).getAddResult();
    }

}
