package com.app2.engine.repository;

import com.app2.engine.entity.app.ParameterHeader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParameterRepository extends JpaRepository<ParameterHeader, Long> {

    ParameterHeader findByCode(String code);
}
