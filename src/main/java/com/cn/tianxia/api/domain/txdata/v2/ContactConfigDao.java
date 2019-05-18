package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.ContactConfigEntity;

public interface ContactConfigDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ContactConfigEntity record);

    int insertSelective(ContactConfigEntity record);

    ContactConfigEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ContactConfigEntity record);

    int updateByPrimaryKey(ContactConfigEntity record);
    
    /**
     * 
     * @Description 获取平台联系信息
     * @param cagent
     * @return
     */
    ContactConfigEntity selectByCagent(@Param("cagent") String cagent);
}