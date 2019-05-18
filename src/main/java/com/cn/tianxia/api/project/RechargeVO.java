package com.cn.tianxia.api.project;

import java.io.Serializable;

/**
 * @ClassName RechargeVO
 * @Description 订单VO类
 * @author Hardy
 * @Date 2018年9月25日 下午7:23:35
 * @version 1.0.0
 */
public class RechargeVO implements Serializable {

    private static final long serialVersionUID = 5082172406842307159L;

    private Integer id;// 订单表逐渐ID
    private Integer uid;// 用户ID
    private Integer cid;// 平台ID
    private Integer payId;// 支付商ID
    private String cagent;// 平台编号
    private String orderNo;// 订单号
    private String payType;// 本地支付渠道
    private String payCode;// 第三方支付方式
    private Double orderAmount;// 订单金额
    private String tradeStatus;// 订单状态
    private String tradeNo;// 流水号
    private Double walletBalance;// 钱包余额
    private Double dividendRate;// 彩金倍率
    private Double codingRade;// 打码量倍率
    private String paymentName;// 支付商编号
    private String paymentConfig;// 支付商配置信息
    private Integer type;// 是否插入彩金流水
    private Double cj;// 彩金
    private Double dml;// 打码量
    private String params;// 回调参数串
    private String ip;// ip地址
    private String payStatus;// 支付状态
    private Double remainvalue;// 平台剩余金额
    private Double usedvaue;// 平台已用额度
    private Double oldMoney;
    private Double newMoney;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getPayId() {
        return payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
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

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Double getDividendRate() {
        return dividendRate;
    }

    public void setDividendRate(Double dividendRate) {
        this.dividendRate = dividendRate;
    }

    public Double getCodingRade() {
        return codingRade;
    }

    public void setCodingRade(Double codingRade) {
        this.codingRade = codingRade;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getCj() {
        return cj;
    }

    public void setCj(Double cj) {
        this.cj = cj;
    }

    public Double getDml() {
        return dml;
    }

    public void setDml(Double dml) {
        this.dml = dml;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public Double getRemainvalue() {
        return remainvalue;
    }

    public void setRemainvalue(Double remainvalue) {
        this.remainvalue = remainvalue;
    }

    public Double getUsedvaue() {
        return usedvaue;
    }

    public void setUsedvaue(Double usedvaue) {
        this.usedvaue = usedvaue;
    }

    public Double getOldMoney() {
        return oldMoney;
    }

    public void setOldMoney(Double oldMoney) {
        this.oldMoney = oldMoney;
    }

    public Double getNewMoney() {
        return newMoney;
    }

    public void setNewMoney(Double newMoney) {
        this.newMoney = newMoney;
    }

    @Override
    public String toString() {
        return "RechargeVO [id=" + id + ", uid=" + uid + ", cid=" + cid + ", payId=" + payId + ", cagent=" + cagent
                + ", orderNo=" + orderNo + ", payType=" + payType + ", payCode=" + payCode + ", orderAmount="
                + orderAmount + ", tradeStatus=" + tradeStatus + ", tradeNo=" + tradeNo + ", walletBalance="
                + walletBalance + ", dividendRate=" + dividendRate + ", codingRade=" + codingRade + ", paymentName="
                + paymentName + ", paymentConfig=" + paymentConfig + ", type=" + type + ", cj=" + cj + ", dml=" + dml
                + ", params=" + params + ", ip=" + ip + ", payStatus=" + payStatus + ", remainvalue=" + remainvalue
                + ", usedvaue=" + usedvaue + ", oldMoney=" + oldMoney + ", newMoney=" + newMoney + "]";
    }

}
