package com.app2.engine.repository.custom;

import com.app2.engine.entity.app.Employee;
import com.app2.engine.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Slf4j
@Repository
public class HibernateTrainRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    EmployeeRepository employeeRepository;

    public void jdbcUpdate() {
        //https://www.netjstech.com/2016/11/insert-update-using-jdbctemplate-in-spring-framework.html
        String INSERT_QUERY = "insert into employee (name, age) values (?, ?)";
        String UPDATE_QUERY = "update employee set age = ? where id = ?";
        String DELETE_QUERY = "delete from employee where id = ?";
        int row = jdbcTemplate.update(UPDATE_QUERY, 10, 20);
        log.debug("Row update is : {}", row);
    }

    public void criteriaTutorial() { //easy
        //https://howtodoinjava.com/hibernate/hibernate-criteria-queries-tutorial/
    }

    public Employee insertEmployee(String name, String lName, String empCode) {
        Employee employee = new Employee();
        employee.setThaiName(name);
        employee.setThaiLastName(lName);
        employee.setEmpCode(empCode);
        employeeRepository.save(employee);
        return employee;
    }

    public Employee findByEmpCode(String empCode) {
        Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(Employee.class);
        criteria.add(Restrictions.eq("empCode",empCode));
        List<Employee> employeeList = criteria.list();
        log.debug("Emp size {}",employeeList.size());
        return null;
    }

    public void jpaTutorial() {
        //https://medium.com/blog-blog/spring-data-jpa-dbadd054bfef
        //https://www.tutorialspoint.com/jpa/jpa_jpql.htm
        Query query1 = entityManager.createQuery("Select MAX(e.salary) from Employee e"); //jpql
        Double result = (Double) query1.getSingleResult();
        log.debug("Max Employee Salary :" + result);
    }

    public void criteriaDelete() { //medium
        //https://thorben-janssen.com/criteria-updatedelete-easy-way-to/
       /* CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // create delete
        CriteriaDelete<Order> delete = cb.createCriteriaDelete(Order.class);
        // set the root class
        Root e = delete.from(Order.class);
        // set where clause
        delete.where(cb.lessThanOrEqualTo(e.get("amount"), amount));
        // perform update
        this.entityManager.createQuery(delete).executeUpdate();*/
    }


}
