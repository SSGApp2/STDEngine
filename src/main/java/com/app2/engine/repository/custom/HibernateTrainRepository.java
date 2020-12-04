package com.app2.engine.repository.custom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Slf4j
@Repository
public class HibernateTrainRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    JdbcTemplate jdbcTemplate;
    //chaichana

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
