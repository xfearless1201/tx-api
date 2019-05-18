package com.cn.tianxia.api.domain.txdata.v2;


import com.cn.tianxia.api.project.v2.ApiLogEntity;

public interface ApiLogDao {
    int deleteByPrimaryKey(Long id);

    int insert(ApiLogEntity record);

    int insertSelective(ApiLogEntity record);

    ApiLogEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ApiLogEntity record);

    int updateByPrimaryKey(ApiLogEntity record);
}