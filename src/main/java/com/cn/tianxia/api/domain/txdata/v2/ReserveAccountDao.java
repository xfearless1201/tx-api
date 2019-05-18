package com.cn.tianxia.api.domain.txdata.v2;


import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.ReserveAccountEntity;


public interface ReserveAccountDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ReserveAccountEntity record);

    int insertSelective(ReserveAccountEntity record);

    ReserveAccountEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ReserveAccountEntity record);

    int updateByPrimaryKey(ReserveAccountEntity record);

    ReserveAccountEntity selectReserveAccount(@Param("userName") String userName, @Param("cagent") String cagent);
}