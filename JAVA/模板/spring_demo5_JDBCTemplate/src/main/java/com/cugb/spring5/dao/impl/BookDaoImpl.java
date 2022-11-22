package com.cugb.spring5.dao.impl;

import com.cugb.spring5.compents.JDBCTemplate;
import com.cugb.spring5.config.MyDruidDataSource;
import com.cugb.spring5.dao.BookDao;
import com.cugb.spring5.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Repository
public class BookDaoImpl implements BookDao {
    // 注入JdbcTemplate
    @Autowired
    private JDBCTemplate jdbcTemplate;

    @Override
    public String toString() {
        return "BookDaoImpl{" +
                "jdbcTemplate=" + jdbcTemplate +
                '}';
    }

    public void add(User user) {
        // 1.创建sql语句
        String sql = "insert into t_user values(?,?,?)";
        jdbcTemplate.update(sql,user.getUserId(),user.getUsername(),user.getUsername());
    }
}
