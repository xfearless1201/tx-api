package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.UserWalletEntity;

/**
 * 
 * @ClassName UserWalletDao
 * @Description 会员积分钱包表dao
 * @author Hardy
 * @Date 2019年1月22日 下午5:27:37
 * @version 1.0.0
 */
public interface UserWalletDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UserWalletEntity record);

    int insertSelective(UserWalletEntity record);

    UserWalletEntity selectByPrimaryKey(Integer id);
    
    UserWalletEntity selectByParams(UserWalletEntity userWalletEntity);

    int updateByPrimaryKeySelective(UserWalletEntity record);

    int updateByPrimaryKey(UserWalletEntity record);
    
    /**
     * 
     * @Description 获取用户积分余额
     * @param uid
     * @return
     */
    Double getIntegralBalance(@Param("uid") Integer uid);
    
    /***
     * 
     * @Description 扣除用户积分余额
     * @param uid
     * @param balance
     * @return
     */
    int deductUserIntegralBalance(@Param("uid") Integer uid,@Param("balance") Double balance);
    
    /**
     * 
     * @Description 增加用户积分余额
     * @param uid
     * @param balance
     * @return
     */
    int plusUserIntegralBalance(@Param("uid") Integer uid,@Param("balance") Double balance);
}