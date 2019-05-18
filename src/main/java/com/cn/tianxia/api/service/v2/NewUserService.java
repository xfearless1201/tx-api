package com.cn.tianxia.api.service.v2;

import java.util.List;

import com.cn.tianxia.api.project.v2.UserEntity;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName NewUserService
 * @Description 重构用户接口
 * @author Hardy
 * @Date 2019年2月7日 下午4:29:07
 * @version 1.0.0
 */
public interface NewUserService {

    /**
     * 
     * @Description 获取用户详情
     * @param uid
     * @return
     */
    public JSONObject getUserInfo(String uid);

    /**
     * 查询用户余额
     * @param uid 用户id
     * @return
     */
    double queryUserBalance(Integer uid);
    
    public List<UserEntity> findAllByPage(Integer pageNo,Integer pageSize);
}
