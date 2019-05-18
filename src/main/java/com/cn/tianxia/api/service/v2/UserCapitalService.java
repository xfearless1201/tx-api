package com.cn.tianxia.api.service.v2;

/**
 * 
 * @ClassName UserCapitalService
 * @Description 用户资金接口
 * @author Hardy
 * @Date 2019年3月11日 下午5:41:21
 * @version 1.0.0
 */
public interface UserCapitalService {

    /**
     * 
     * @Description 操作用户钱包余额
     * @param uid 用户ID
     * @param money 金额
     * @param type 类型 1 加钱 2 扣钱 
     * @return
     */
    public Double handleUserWalletBalance(Integer uid,Double money,Integer type)throws Exception;
    
    /**
     * 
     * @Description 操作用户积分余额
     * @param uid
     * @param integral
     * @param type
     * @return
     */
    public Double handleUserIntegralBalance(Integer uid,Double integral,Integer type) throws Exception;
    
    
}
