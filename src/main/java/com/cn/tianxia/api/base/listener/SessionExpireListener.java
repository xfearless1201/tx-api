//package com.cn.tianxia.api.base.listener;
//
//import javax.servlet.annotation.WebListener;
//import javax.servlet.http.HttpSession;
//import javax.servlet.http.HttpSessionEvent;
//import javax.servlet.http.HttpSessionListener;
//
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.cn.tianxia.api.common.v2.CacheKeyConstants;
//import com.cn.tianxia.api.common.v2.TokenUtils;
//import com.cn.tianxia.api.project.v2.OnlineUserEntity;
//import com.cn.tianxia.api.service.v2.OnlineUserService;
//import com.cn.tianxia.api.utils.RedisUtils;
//
//@WebListener
//public class SessionExpireListener implements HttpSessionListener{
//
//    private static final Logger logger = LoggerFactory.getLogger(SessionExpireListener.class);
//    
//    @Autowired
//    private RedisUtils redisUtils;
//    
//    @Autowired
//    private OnlineUserService onlineUserService;
//    
//    @Override
//    public void sessionDestroyed(HttpSessionEvent event) {
//        try {
//            HttpSession session = event.getSession();
//            //获取sessionId
//            String sessionId = session.getId();
//            //生成缓存key
//            String token = TokenUtils.generatorToken(sessionId);
//            //获取在线会员KEY
//            String onlineKey = CacheKeyConstants.ONLINE_USER_KEY_UID + token;
//            String uid = redisUtils.get(onlineKey);
//            //查询会员信息
//            if(StringUtils.isNoneBlank(uid)){
//                String loginKey = CacheKeyConstants.LOGIN_USER_KEY_TOKEN+token;
//                String kickKey = CacheKeyConstants.KICK_USER_KEY_UID + uid;
//                OnlineUserEntity onlineUser = onlineUserService.getByUid(uid);
//                onlineUser.setLogoutTime(System.currentTimeMillis());
//                onlineUser.setOffStatus((byte)0);
//                onlineUser.setIsOff((byte)1);
//                onlineUser.setUid(Long.parseLong(uid));
//                onlineUserService.insertOrUpdateOnlineUser(onlineUser);
//                redisUtils.delete(kickKey);
//                redisUtils.delete(onlineKey);
//                redisUtils.delete(loginKey);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("销毁缓存key异常:{}",e.getMessage());
//        }
//        
//    }
//    
//}
