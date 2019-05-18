package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.CagentWebcodeEntity;

public interface CagentWebcodeDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CagentWebcodeEntity record);

    int insertSelective(CagentWebcodeEntity record);

    CagentWebcodeEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CagentWebcodeEntity record);

    int updateByPrimaryKey(CagentWebcodeEntity record);
    
    CagentWebcodeEntity getWebcomConfig(@Param("type") Integer type,@Param("cid") Integer cid);
}