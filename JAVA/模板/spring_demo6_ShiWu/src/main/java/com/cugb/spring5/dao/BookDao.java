package com.cugb.spring5.dao;

import com.cugb.spring5.entity.User;

import java.sql.SQLException;

public interface BookDao {
    void add(User user);
}
