package com.cn.tianxia.api.domain.txdata.v2;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.UserCardEntity;

public interface UserCardDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UserCardEntity record);

    int insertSelective(UserCardEntity record);

    UserCardEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserCardEntity record);

    int updateByPrimaryKey(UserCardEntity record);
    
    /**
     * 
     * @Description 查询用户银行卡信息
     * @param uid
     * @return
     */
    UserCardEntity selectUserCard(@Param("id") Integer id,@Param("uid") Integer uid);

    UserCardEntity selectUserCardByUid(String uid);

    Map<String,String> selectCardTypeByBankId(String bankId);

    Map<String,String> selectUserCardInfo(String uid);
}