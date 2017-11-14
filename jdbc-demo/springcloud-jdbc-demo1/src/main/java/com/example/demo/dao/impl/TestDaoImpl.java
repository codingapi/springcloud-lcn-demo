package com.example.demo.dao.impl;

import com.example.demo.dao.TestDao;
import com.example.demo.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by lorne on 2017/6/26.
 */
@Repository
public class TestDaoImpl implements TestDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int save() {
        String sql = "insert into t_test(name) values (?)";
        return jdbcTemplate.update(sql,"jdbc-hello-1");
    }

    @Override
    public List<Test> list() {
        String sql = "select * from t_test ";
        return jdbcTemplate.query(sql, new RowMapper<Test>() {
            @Override
            public Test mapRow(ResultSet resultSet, int i) throws SQLException {
                Test test = new Test();
                test.setId(resultSet.getInt("id"));
                test.setName(resultSet.getString("name"));
                return test;
            }
        });
    }
}
