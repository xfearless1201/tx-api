package com.cn.tianxia.api.domain.txdata.v2;

import com.cn.tianxia.api.project.v2.UserQuantityEntity;

public interface UserQuantityDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UserQuantityEntity record);

    int insertSelective(UserQuantityEntity record);

    UserQuantityEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserQuantityEntity record);

    int updateByPrimaryKey(UserQuantityEntity record);

    UserQuantityEntity selectUserQuantityByUid(String uid);
}