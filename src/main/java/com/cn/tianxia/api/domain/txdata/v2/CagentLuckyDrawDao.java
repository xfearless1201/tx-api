package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.CagentLuckyDrawEntity;

public interface CagentLuckyDrawDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CagentLuckyDrawEntity record);

    int insertSelective(CagentLuckyDrawEntity record);

    CagentLuckyDrawEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CagentLuckyDrawEntity record);

    int updateByPrimaryKey(CagentLuckyDrawEntity record);

    CagentLuckyDrawEntity selectByCid(Integer cid);

    int updateAmountUsedByPrimaryKey(@Param("lid") Integer lid, @Param("amount") Float amount);
}