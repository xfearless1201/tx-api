package com.cn.tianxia.api.domain.txdata.v2;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.OnlineUserEntity;

public interface OnlineUserDao {
    int deleteByPrimaryKey(Long id);

    int insert(OnlineUserEntity record);

    int insertSelective(OnlineUserEntity record);

    OnlineUserEntity selectByPrimaryKey(Long id);
    
    OnlineUserEntity selectByUid(@Param("uid") String uid);

    int updateByPrimaryKeySelective(OnlineUserEntity record);

    int updateByPrimaryKey(OnlineUserEntity record);

    int insertOrUpdateOnlineUser(OnlineUserEntity record);
    
    List<OnlineUserEntity> findAllByCagent(@Param("cagent") String cagent);
}