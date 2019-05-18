package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: zed
 * @Date: 2019/1/31 21:59
 * @Description: 存款记录Entity
 */
public class DepositRecordEntity implements Serializable{
    
    private static final long serialVersionUID = -3997482216733174808L;
    private Integer uid;
    private Date orderTime;
    private String payType;
    private Float orderAmount;
    private String tradeStatus;
    private Integer type;
    private String rmk;

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Float getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Float orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrdertime(Date ordertime) {
        this.orderTime = ordertime;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }
}
