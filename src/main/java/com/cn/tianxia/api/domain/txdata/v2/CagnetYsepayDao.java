package com.cn.tianxia.api.domain.txdata.v2;

import org.springframework.data.repository.query.Param;

import com.cn.tianxia.api.project.v2.CagnetYsepayEntity;

public interface CagnetYsepayDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CagnetYsepayEntity record);

    int insertSelective(CagnetYsepayEntity record);

    CagnetYsepayEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CagnetYsepayEntity record);

    int updateByPrimaryKey(CagnetYsepayEntity record);

    CagnetYsepayEntity selectYsepayConfigByUsername(@Param("username") String username);
}