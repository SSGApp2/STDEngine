package com.app2.engine.controller;

import com.app2.engine.util.AuthorizeUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    AuthorizeUtil authorizeUtil;

    @GetMapping("/")
    public String test() {
        log.debug("TEST");
        return "success";
    }
}
