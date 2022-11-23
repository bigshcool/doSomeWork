package com.cugb.spring5.service;

import com.cugb.spring5.dao.BookDao;
import com.cugb.spring5.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
public class BookService {
    @Autowired
    private BookDao bookDao;


    // 添加方法
    public void addBook(User user){
        bookDao.add(user);
    }

    @Override
    public String toString() {
        return "BookService{" +
                "bookDao=" + bookDao +
                '}';
    }
}
