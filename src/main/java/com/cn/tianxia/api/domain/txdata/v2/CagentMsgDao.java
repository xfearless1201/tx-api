package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.CagentMsgEntity;

public interface CagentMsgDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CagentMsgEntity record);

    int insertSelective(CagentMsgEntity record);

    CagentMsgEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CagentMsgEntity record);

    int updateByPrimaryKey(CagentMsgEntity record);

    CagentMsgEntity selectMsgLog(@Param("cagent") String cagent, @Param("mobileNo") String mobileNo, @Param("type") String type, @Param("sendTime") String sendTime);
}