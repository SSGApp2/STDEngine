package com.app2.engine.controller;

import com.app2.engine.entity.app.Employee;
import com.app2.engine.repository.custom.HibernateTrainRepository;
import com.app2.engine.util.AuthorizeUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    AuthorizeUtil authorizeUtil;

    @Autowired
    HibernateTrainRepository hibernateTrainRepository;

    @GetMapping("/")
    public String test() {
        log.debug("TEST");
        return "success";
    }

    @GetMapping("/findByEmpCode")
    public Employee findByEmpCode(@RequestParam(value = "empCode") String empCode) {
        return hibernateTrainRepository.findByEmpCode(empCode);
    }

    @GetMapping("/insEmp")
    public Employee insertEmp(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "lName") String lastName,
            @RequestParam(value = "empCode") String empCode
    ) {
        log.debug("Hello World!!!!! {},{},{}", name, lastName, empCode);
        Employee emp = hibernateTrainRepository.insertEmployee(name, lastName, empCode);
        return emp;
    }


}
