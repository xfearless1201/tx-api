package com.cn.tianxia.api.service.v2;

import com.alibaba.fastjson.JSONObject;
import com.cn.tianxia.api.project.v2.OnlineUserEntity;

/**
 * @Author: zed
 * @Date: 2019/5/11 13:51
 * @Description: 插入在线用户记录
 */
public interface OnlineUserService {

    int insertOrUpdateOnlineUser(OnlineUserEntity onlineUserEntity);

    JSONObject findAllByCagent(String cagent);
    
    OnlineUserEntity getByUid(String uid);
}
