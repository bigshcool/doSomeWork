package com.cugb.spring5.compents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class JDBCTemplate extends JdbcTemplate {

    @Autowired
    private DataSource dataSource;

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public String toString() {
        return "JDBCTemplate{" +
                "dataSource=" + dataSource +
                '}';
    }
}
