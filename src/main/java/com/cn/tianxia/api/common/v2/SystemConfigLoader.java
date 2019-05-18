package com.cn.tianxia.api.common.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @Auther: zed
 * @Date: 2019/1/23 20:07
 * @Description: 系统配置加载类
 */
@Configuration
@PropertySource(value = "classpath:conf/file.properties")
public class SystemConfigLoader {
    @Autowired
    private Environment env;

    // 获取参数
    public String getProperty(String key) {
       return env.getProperty(key);
    }
}
