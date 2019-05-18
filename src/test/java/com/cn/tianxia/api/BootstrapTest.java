package com.cn.tianxia.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.tianxia.api.utils.RedisUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Bootstrap.class)
public class BootstrapTest {

    @Autowired
    private RedisUtils redisUtils;
    
    @Test
    public void test(){
        for (int i = 0; i < 100; i++) {
            String key = "TEST:KEY:"+i;
            redisUtils.set(key,key,180);
        }
    }
}
