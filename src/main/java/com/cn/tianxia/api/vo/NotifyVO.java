package com.cn.tianxia.api.vo;

import java.io.Serializable;

/**
 * 
 * @ClassName NotifyVO
 * @Description 回调VO类
 * @author Hardy
 * @Date 2018年11月21日 下午8:44:54
 * @version 1.0.0
 */
public class NotifyVO implements Serializable{
  
    private static final long serialVersionUID = -7290529942925479739L;

    private Integer uid;//用户ID
    
    private String orderNo;//订单号
    
    private String tradeNo;//第三方流水号
    
    private String tradeStatus;//第三方订单状态
    
    private String successStatus;//支付成功的状态
    
    private String notifyParams;//回调请求参数

    
    public Integer getUid() {
        return uid;
    }

    
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    
    public String getOrderNo() {
        return orderNo;
    }

    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    
    public String getTradeNo() {
        return tradeNo;
    }

    
    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    
    public String getTradeStatus() {
        return tradeStatus;
    }

    
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    
    public String getSuccessStatus() {
        return successStatus;
    }

    
    public void setSuccessStatus(String successStatus) {
        this.successStatus = successStatus;
    }

    
    public String getNotifyParams() {
        return notifyParams;
    }

    
    public void setNotifyParams(String notifyParams) {
        this.notifyParams = notifyParams;
    }
    
}
