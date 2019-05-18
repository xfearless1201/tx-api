package com.cn.tianxia.api.service;

import java.util.Map;

import com.cn.tianxia.api.vo.CagentYespayVO;
import com.cn.tianxia.api.vo.RechargeOrderVO;

public interface NotifyService {

    //回调主方法
    public int saveProcess(Map<String, Object> paramsMap) throws Exception;
    
    //回调更新订单信息
    public String saveRecharge(String orderNo,String tradeStatus,String tradeNo,String successStatus,String ip,String params) throws Exception;

    
    /**
     * 
     * @Description 根据订单号查询订单信息
     * @param orderNo
     * @return
     */
    public RechargeOrderVO findNotifyOrderByOrderNo(String orderNo) throws Exception;
    
    /**
     * 
     * @Description 通过支付商ID 查询支付商信息
     * @param payId
     * @return
     */
    public CagentYespayVO getCagentYespayByPayId(Integer payId) throws Exception;
    
    /**
     * 
     * @Description 处理回调业务
     * @param rechargeOrderVO
     * @return
     * @throws Exception
     */
    public String processNotifyOrder(RechargeOrderVO rechargeOrderVO) throws Exception;
    
    /**
     * 
     * @Description 修改订单的描述信息
     * @param rechargeOrderVO
     * @throws Exception
     */
    public void updateNotifyOrderDescription(RechargeOrderVO rechargeOrderVO)throws Exception;

    /**
     * 通过平台编号和支付商编号获取支付商配置
     * @param cagent
     * @param payment
     * @return
     */
    CagentYespayVO getCagentYsepayByCagentAndPayment(String cagent, String payment);
}
