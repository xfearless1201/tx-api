package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.DemoAccountEntity;

/**
 * 
 * @ClassName DemoAccountMapper
 * @Description 游戏试玩账号表dao
 * @author Hardy
 * @Date 2019年2月6日 上午11:32:08
 * @version 1.0.0
 */
public interface DemoAccountDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DemoAccountEntity record);

    int insertSelective(DemoAccountEntity record);

    DemoAccountEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DemoAccountEntity record);

    int updateByPrimaryKey(DemoAccountEntity record);
    
    /**
     * 
     * @Description 根据试玩码获取试玩账号信息
     * @param accountcode
     * @return
     */
    DemoAccountEntity selectByAccountcode(@Param("accountcode") String accountcode);
}