package com.cugb.spring5.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.cugb.spring5")
@EnableTransactionManagement // 开启事务
public class MyConfig {
}
