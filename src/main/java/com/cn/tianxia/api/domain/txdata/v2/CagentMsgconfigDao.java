package com.cn.tianxia.api.domain.txdata.v2;

import org.springframework.data.repository.query.Param;

import com.cn.tianxia.api.project.v2.CagentMsgconfigEntity;

public interface CagentMsgconfigDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CagentMsgconfigEntity record);

    int insertSelective(CagentMsgconfigEntity record);

    CagentMsgconfigEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CagentMsgconfigEntity record);

    int updateByPrimaryKey(CagentMsgconfigEntity record);

    CagentMsgconfigEntity selectByCagent(@Param("cagent") String cagent);
}