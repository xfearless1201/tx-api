package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.CagentStoredvalueEntity;

/**
 * 
 * @ClassName CagentStoredvalueDao
 * @Description 平台额度接口
 * @author Hardy
 * @Date 2019年3月13日 下午3:25:05
 * @version 1.0.0
 */
public interface CagentStoredvalueDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CagentStoredvalueEntity record);

    int insertSelective(CagentStoredvalueEntity record);

    CagentStoredvalueEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CagentStoredvalueEntity record);

    int updateByPrimaryKey(CagentStoredvalueEntity record);
    
    Double getCagentRemainvalue(@Param("cid") Integer cid);
    
    int updateCagentRemainvalue(@Param("cid") Integer cid,@Param("remainvalue") Double remainvalue);
}