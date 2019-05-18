package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.DissociateEntity;

/**
 * 
 * @ClassName DissociateDao
 * @Description 用户游离表dao
 * @author Hardy
 * @Date 2019年2月6日 下午3:56:49
 * @version 1.0.0
 */
public interface DissociateDao {
    int deleteByPrimaryKey(Integer uid);

    int insert(DissociateEntity record);

    int insertSelective(DissociateEntity record);

    DissociateEntity selectByPrimaryKey(Integer uid);

    int updateByPrimaryKeySelective(DissociateEntity record);

    int updateByPrimaryKey(DissociateEntity record);
    
    DissociateEntity getDissociateInfoByUsername(@Param("username") String username,@Param("cagent") String cagent,@Param("flag") Integer flag);
}