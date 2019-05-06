package com.app2.engine.repository;

import com.app2.engine.entity.app.AppParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppParameterRepository extends JpaRepository<AppParameter, Long> {

    AppParameter findByCode(String code);
}
