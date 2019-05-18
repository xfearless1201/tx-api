package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.ActivityEntity;

public interface ActivityDao {
    int deleteByPrimaryKey(Long id);

    int insert(ActivityEntity record);

    int insertSelective(ActivityEntity record);

    ActivityEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ActivityEntity record);

    int updateByPrimaryKey(ActivityEntity record);
    
    ActivityEntity findOneByCagent(@Param("cagent") String cagent,@Param("type") String type);
}