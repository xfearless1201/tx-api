package com.cn.tianxia.api.base.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.cn.tianxia.api.common.v2.CacheKeyConstants;
import com.cn.tianxia.api.project.v2.OnlineUserEntity;
import com.cn.tianxia.api.service.v2.OnlineUserService;
import com.cn.tianxia.api.utils.SpringContextUtils;


@Component
public class KeyExpiredEventMessageListener implements MessageListener {
    
    private static final Logger logger = LoggerFactory.getLogger(KeyExpiredEventMessageListener.class);
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        logger.info("过期的offline：{}",message);
        String expired = message.toString();
        String onlineKey = CacheKeyConstants.ONLINE_USER_KEY_UID;
        if(expired.contains(onlineKey)){
            String uid = expired.replace(CacheKeyConstants.ONLINE_USER_KEY_UID, "");
            OnlineUserService onlineUserService = (OnlineUserService) SpringContextUtils.getBeanByClass(OnlineUserService.class);
            OnlineUserEntity onlineUser = onlineUserService.getByUid(uid);
            if(onlineUser != null){
                onlineUser.setLogoutTime(System.currentTimeMillis());
                onlineUser.setOffStatus((byte)0);
                onlineUser.setIsOff((byte)1);
                onlineUser.setUid(Long.parseLong(uid));
                onlineUserService.insertOrUpdateOnlineUser(onlineUser);
            }
        }
    }
    
}
