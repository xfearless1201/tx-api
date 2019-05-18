package com.cn.tianxia.api.service.v2.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.cn.tianxia.api.domain.txdata.v2.OnlineUserDao;
import com.cn.tianxia.api.project.v2.OnlineUserEntity;
import com.cn.tianxia.api.service.v2.OnlineUserService;


/**
 * @Author: zed
 * @Date: 2019/5/11 17:56
 * @Description: 插入或更新在线用户
 */
@Service
public class OnlineUserServiceImpl implements OnlineUserService {

    @Autowired
    private OnlineUserDao onlineUserDao;

    @Override
    public int insertOrUpdateOnlineUser(OnlineUserEntity record) {
        if(record.getId() == null){
            return onlineUserDao.insertSelective(record);
        }else{
            return onlineUserDao.updateByPrimaryKeySelective(record);
        }
    }

    @Override
    public JSONObject findAllByCagent(String cagent) {
        JSONObject data = new JSONObject();
        List<OnlineUserEntity> users = onlineUserDao.findAllByCagent(cagent);
        if(!CollectionUtils.isEmpty(users)){
            //先去重
            List<OnlineUserEntity> list = users.stream().distinct().collect(Collectors.toList());
            list.stream().forEach(user->{
                JSONObject onlineuser = new JSONObject();
                onlineuser.put("uid",user.getUid());
                onlineuser.put("address",user.getAddress());
                onlineuser.put("cagent",user.getCagent());
                onlineuser.put("login_time",user.getLoginTime());
                onlineuser.put("ip",user.getIp());
                onlineuser.put("sessionid",user.getToken());
                onlineuser.put("refurl",user.getRefurl());
                onlineuser.put("isMobile",data.get("isMobile"));
                data.put(String.valueOf(user.getUid()),onlineuser);
            });
        }
        return data;
    }

    @Override
    public OnlineUserEntity getByUid(String uid) {
        return onlineUserDao.selectByUid(uid);
    }
}
