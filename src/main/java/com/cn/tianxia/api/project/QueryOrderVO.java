package com.cn.tianxia.api.project;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName PayOrderVO
 * @Description 查询订单VO类
 * @author Hardy
 * @Date 2018年11月19日 上午10:28:20
 * @version 1.0.0
 */
public class QueryOrderVO implements Serializable {

    private static final long serialVersionUID = -6335545801278316087L;

    private Integer payId;// 支付商ID

    private Integer uid;// 用户ID

    private String cagent;// 平台编码

    private String orderNo;// 订单号

    private String treadeNo;// 流水号

    private Double amount;// 订单金额

    private Integer payType;// 支付方式

    private String payCode;// 第三方支付渠道

    private Date payDate;// 支付时间

    private String paymentName;// 支付商编码

    private String paymentConfig;// 支付商配置文件

    private String ip;// 终端IP

    public Integer getPayId() {
        return payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTreadeNo() {
        return treadeNo;
    }

    public void setTreadeNo(String treadeNo) {
        this.treadeNo = treadeNo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getPaymentConfig() {
        return paymentConfig;
    }

    public void setPaymentConfig(String paymentConfig) {
        this.paymentConfig = paymentConfig;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
