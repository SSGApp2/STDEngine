package com.app2.engine.spring;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        log.info("ApplicationStartup.....!");
        log.info("Swagger UI : /swagger-ui.html");
        log.info("Spring Data REST : /rest-api");
//        AppParameter appParameterConfig = appParameterRepository.findByCode("50");//example
    }

}
