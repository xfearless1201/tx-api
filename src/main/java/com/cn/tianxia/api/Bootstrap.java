package com.cn.tianxia.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value={"classpath:conf/file.properties","classpath:conf/scanmobilepay.properties","classpath:conf/scanpay.properties"},ignoreResourceNotFound = true)
public class Bootstrap extends SpringBootServletInitializer{
    
    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Bootstrap.class);
    }
}
