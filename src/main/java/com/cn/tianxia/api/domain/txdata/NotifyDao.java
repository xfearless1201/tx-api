package com.cn.tianxia.api.domain.txdata;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.RechargeVO;
import com.cn.tianxia.api.vo.CagentYespayVO;
import com.cn.tianxia.api.vo.CjOrDmlRateVO;
import com.cn.tianxia.api.vo.RechargeOrderVO;

/**
 * 
 * @ClassName NotifyDao
 * @Description 回调的sql
 * @author HH
 * @Date 2018年8月2日 下午1:53:50
 * @version 1.0.0
 */
public interface NotifyDao {
    
    //更新用户钱包余额
    int updateUserMoney(Map<String, Object> paramsMap);
    //记录钱包流水日志
    int insertUserTreasure(Map<String, Object> paramsMap);
    int insertUserTreasure1(Map<String, Object> paramsMap); 
    //更新订单状态
    int updateTrecharge(Map<String, Object> paramsMap);
    //计算打码量
    int insertTUserQuantity(Map<String, Object> paramsMap);
    //更新平台额度
    int updateTCagentStoredvalue(Map<String, Object> paramsMap);
    // 记录平台额度日志
    int insertTCagentStoredvalueLog(Map<String, Object> paramsMap);
    int insertTCagentStoredvalueLog1(Map<String, Object> paramsMap); 
    //计算充值产生的积分，并更新
/*    Double queryTuser(Map<String, Object> paramsMap);
    Double querytuserwallet(Map<String, Object> paramsMap);
    int updateTuserwallet(Map<String, Object> paramsMap);
    int insertTuserwallet(Map<String, Object> paramsMap);*/
    //记录积分流水日志
    int insertTuserwalletlog(Map<String, Object> paramsMap);
    
    /**
     * 
     * @Description 查询用户订单
     * @param orderNo
     * @param uid
     * @return
     */
    RechargeVO findRechargeByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 
     * @Description 更新订单状态
     * @param orderNo
     * @param uid
     * @param tradeNo
     * @param tradeStatus
     * @return
     */
    int updateRechargeStatus(@Param("orderNo") String orderNo,@Param("uid") String uid,
            @Param("tradeNo") String tradeNo,@Param("tradeStatus") String tradeStatus);
    
    /**
     * 
     * @Description 插入用户资金流水
     * @param recharge
     * @return
     */
    int saveUserTreasureAndQuantity(RechargeVO recharge);
    
    
    /**
     * 
     * @Description 根据订单号统计用户资金流水
     * @param uid
     * @param orderNo
     * @param cagent
     * @return
     */
    int findUserTreasureByOrderNo(@Param("uid") Integer uid,@Param("orderNo") String orderNo,@Param("cagent") String cagent);
    
    /**
     * 
     * @Description 更新用户钱包余额
     * @param recharge
     * @return
     */
    int updateRechargeAndBalance(RechargeVO recharge);
    
    
    //平台可用额度
    Double selectCagentQuota(@Param("cid") Integer cid);
    
    
    /**
     * 
     * @Description 通过订单号查询订单详情
     * @param orderNo
     * @return
     */
    RechargeOrderVO findNotifyOrderByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 
     * @Description 通过支付商ID查询支付商信息
     * @param payId
     * @return
     */
    CagentYespayVO getCagentYespayByPayId(@Param("payId") Integer payId);

    /**
     *
     * @Description 通过支付商名称和平台编码查询支付商信息
     * @param cagent 平台编码
     * @param paymentName 支付商名称
     * @return
     */
    CagentYespayVO getCagentYsepayByCagentAndPayment(@Param("cagent") String cagent,@Param("paymentName") String paymentName);
    
    /**
     * 
     * @Description 获取用户余额
     * @param uid
     * @return
     */
    Double getUserBalance(@Param("uid") Integer uid);
    
    /**
     * 
     * @Description 获取用户所在平台所在分层的彩金倍率和打码量倍率
     * @param uid
     * @param cid
     * @param payId
     * @return
     */
    CjOrDmlRateVO getCjOrDmlRate(@Param("uid") Integer uid,@Param("cid") Integer cid,@Param("payId") Integer payId, @Param("type")String type);
    
    /**
     * 
     * @Description 批量修改订单业务
     * @param rechargeOrderVO
     * @return
     */
    int batchUpdateNotifyOrderProcess(RechargeOrderVO rechargeOrderVO);
    
    /**
     * 
     * @Description 修改订单状态
     * @param rechargeOrderVO
     * @return
     */
    int updateNotifyOrderStatus(RechargeOrderVO rechargeOrderVO);
    
    /**
     * 
     * @Description 批量写入回调业务
     * @param rechargeOrderVO
     * @return
     */
    int batchSaveNotifyProcess(RechargeOrderVO rechargeOrderVO);
    
    /**
     * 
     * @Description 获取平台ID
     * @param cagent
     * @return
     */
    int getCangetIdByCagent(@Param("uid") Integer uid);
    
    /**
     * 
     * @Description 获取平台储值积分比例
     * @param uid
     * @return
     */
    double getCagentIntegralRatio(@Param("uid") Integer uid);
    
    /**
     * 
     * @Description 查询平台用户积分余额
     * @param uid
     * @return
     */
    Double getCagentIntegralBalance(@Param("uid") Integer uid);
}
