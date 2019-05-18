package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.CagentBankcardEntity;

/**
 * 
 * @ClassName CagentBankcardDao
 * @Description 平台银行卡dao
 * @author Hardy
 * @Date 2019年1月5日 上午11:21:57
 * @version 1.0.0
 */
public interface CagentBankcardDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CagentBankcardEntity record);

    int insertSelective(CagentBankcardEntity record);

    CagentBankcardEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CagentBankcardEntity record);

    int updateByPrimaryKey(CagentBankcardEntity record);
    
    /**
     * 
     * @Description 获取用户在平台上绑定的银行卡信息
     * @param uid
     * @param cid
     * @return
     */
    CagentBankcardEntity selectUserBankRemittanceInfo(@Param("uid")String uid);
}