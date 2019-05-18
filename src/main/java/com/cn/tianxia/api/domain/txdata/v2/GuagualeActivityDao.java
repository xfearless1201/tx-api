package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.GuagualeActivityEntity;

public interface GuagualeActivityDao {
    int deleteByPrimaryKey(Long id);

    int insert(GuagualeActivityEntity record);

    int insertSelective(GuagualeActivityEntity record);

    GuagualeActivityEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GuagualeActivityEntity record);

    int updateByPrimaryKey(GuagualeActivityEntity record);
    
    GuagualeActivityEntity findOneByActivityId(@Param("activityId") long activityId);

    int subtractActicityUserMoney(@Param("userMoney") Long userMoney,@Param("activityId") Long activityId);
}