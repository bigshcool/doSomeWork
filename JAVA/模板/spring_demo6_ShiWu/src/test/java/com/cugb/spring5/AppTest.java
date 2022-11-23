package com.cugb.spring5;
import com.cugb.spring5.config.MyConfig;
import com.cugb.spring5.entity.User;
import com.cugb.spring5.service.BookService;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppTest {
    @Test
    public void JdbcTest(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        BookService bookService = context.getBean("bookService", BookService.class);
        System.out.println(bookService);
    }

    @Test
    public void testAdd(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        BookService bookService = context.getBean("bookService", BookService.class);
        System.out.println();
        User user = new User();
        user.setUsername("zjl");
        user.setUstatus("1");
        bookService.addBook(user);
    }
}
