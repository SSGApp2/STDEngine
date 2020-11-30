package com.app2.engine.repository;

import com.app2.engine.entity.app.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
}
