/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下网络 
 *    http://www.d-telemedia.com/
 *
 *    Package:     com.cn.tianxia.enums 
 *
 *    Filename:    RedisKeysPrefix.java
 *
 *    Description: 存入redis的key前缀
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下网络科技 
 *
 *    @author: Wilson
 *
 *    @version: 1.0.0
 *
 *    Create at:   2018年11月05日 21:02 
 *
 *    Revision: 
 *
 *    2018/11/5 21:02 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.enums;

/**
 * @ClassName RedisKeysPrefix
 * @Description 存入redis的key前缀
 * @Author Wilson
 * @Date 2018年11月05日 21:02
 * @Version 1.0.0
 **/
public enum RedisKeysPrefix {
    /**用户登录信息 USER_LOGIN_INFO*/
    GROUP_USER_LOGIN_INFO("USER_LOGIN_INFO:-","用户登录信息"),
    GROUP_LUCKYDRAW_LOCK_INFO("LUCKYDRAW_LOCK_INFO:","会员抽奖锁限制访问频率"),
    GROUP_LUCKYDRAW_TIMES_INFO("LUCKYDRAW_TIMES_INFO:","会员当前活动可抽奖的次数"),
    GROUP_LUCKYDRAW_TOTAYTIMES_INFO("LUCKYDRAW_TOTAYTIMES_INFO:","会员今日抽奖总次数"),
    GROUP_LUCKYDRAW_TOTALTIMES_INFO("LUCKYDRAW_TOTALTIMES_INFO:","会员截止目前抽奖总次数"),
    GROUP_REDIS_LOCK_INFO("REDIS_LOCK_INFO:","redis锁"),
    GROUP_AMOUNTUSED_INFO("LUCKYDRAW_AMOUNTUSED_INFO:","红包已使用金额"),
    GROUP_AMOUNTLIMIT_INFO("LUCKYDRAW_LIMIT_INFO:","红包总金额"),
    ;

    private String key;
    private String desc;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    RedisKeysPrefix(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }
}