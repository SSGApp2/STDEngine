package com.app2.engine.entity.app;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Employee {

    @Id
    @GeneratedValue
    private Long id;
    private String thaiName;
    private String thaiLastName;
    private String empCode;

}
