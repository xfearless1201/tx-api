package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.UserTypeHandicapEntity;

public interface UserTypeHandicapDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UserTypeHandicapEntity record);

    int insertSelective(UserTypeHandicapEntity record);

    UserTypeHandicapEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserTypeHandicapEntity record);

    int updateByPrimaryKey(UserTypeHandicapEntity record);

    UserTypeHandicapEntity selectByGameAndUid(@Param("game") String game, @Param("uid") String uid);
}