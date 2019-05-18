package com.cn.tianxia.api.service.v2.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.common.v2.CacheKeyConstants;
import com.cn.tianxia.api.common.v2.TokenUtils;
import com.cn.tianxia.api.project.v2.OnlineUserEntity;
import com.cn.tianxia.api.service.v2.OnlineUserService;
import com.cn.tianxia.api.service.v2.TokenService;
import com.cn.tianxia.api.utils.RedisUtils;

/**
 * 
 * @ClassName TokenServiceImpl
 * @Description token接口
 * @author Hardy
 * @Date 2019年5月11日 上午1:23:33
 * @version 1.0.0
 */
@Service
public class TokenServiceImpl implements TokenService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);
    
    @Autowired
    private RedisUtils redisUtils;
    
    @Autowired
    private OnlineUserService onlineUserService;
    
    /**
     * 获取用户信息
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getUserInfo(HttpServletRequest request) {
        try {
            //获取用户sessionId
            String sessionId = request.getSession().getId();
            //校验签名
            String token = TokenUtils.generatorToken(sessionId);
            //通过token获取用户信息
            String key = CacheKeyConstants.LOGIN_USER_KEY_TOKEN+token;
            Map<String,String> data = redisUtils.get(key,Map.class);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("校验用户token异常:{}",e.getMessage());
            return null;
        }
    }

    @Override
    public void destroyToken(String token) {
        try {
            //通过token获取用户信息
            String key = CacheKeyConstants.LOGIN_USER_KEY_TOKEN+token;
            Map<String,String> data = redisUtils.get(key,Map.class);
            OnlineUserEntity onlineUserEntity = onlineUserService.getByUid(data.get("uid"));
            onlineUserEntity.setLogoutTime(System.currentTimeMillis());
            onlineUserEntity.setUid(Long.parseLong(data.get("uid")));
            onlineUserEntity.setIsOff((byte)1);
            onlineUserEntity.setOffStatus((byte)1);
            onlineUserService.insertOrUpdateOnlineUser(onlineUserEntity);
            //用户踢人KEY
            String kickKey = CacheKeyConstants.KICK_USER_KEY_UID + data.get("uid");
            //在线会员KEY
            String onlineKey = CacheKeyConstants.ONLINE_USER_KEY_UID + token;
            redisUtils.delete(key);
            redisUtils.delete(kickKey);
            redisUtils.delete(onlineKey);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("销毁token异常:{}",e.getMessage());
        }
    }

}
