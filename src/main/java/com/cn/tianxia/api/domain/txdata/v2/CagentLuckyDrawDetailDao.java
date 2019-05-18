package com.cn.tianxia.api.domain.txdata.v2;

import java.util.List;

import com.cn.tianxia.api.project.v2.CagentLuckyDrawDetailEntity;

public interface CagentLuckyDrawDetailDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CagentLuckyDrawDetailEntity record);

    int insertSelective(CagentLuckyDrawDetailEntity record);

    CagentLuckyDrawDetailEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CagentLuckyDrawDetailEntity record);

    int updateByPrimaryKey(CagentLuckyDrawDetailEntity record);

    List<CagentLuckyDrawDetailEntity> selectByLid(Integer lid);
}