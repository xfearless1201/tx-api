package com.cn.tianxia.api.base.config;

import javax.servlet.DispatcherType;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cn.tianxia.api.base.filter.XssFilter;

/**
 * 
 * @ClassName FilterConfig
 * @Description 过滤器
 * @author Hardy
 * @Date 2019年5月13日 下午12:29:37
 * @version 1.0.0
 */
@Configuration
public class FilterConfig {

    @SuppressWarnings({"rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean xssFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }
}
