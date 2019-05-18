package com.cn.tianxia.api.service.v2;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @ClassName TokenService
 * @Description token接口
 * @author Hardy
 * @Date 2019年5月11日 上午1:23:07
 * @version 1.0.0
 */
public interface TokenService {
    
    /**
     * 
     * @Description 通过token获取用户信息
     * @param token
     * @return
     */
    public Map<String,String> getUserInfo(HttpServletRequest request);

    
    public void destroyToken(String token);
}
