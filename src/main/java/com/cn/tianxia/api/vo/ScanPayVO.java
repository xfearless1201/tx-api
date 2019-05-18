package com.cn.tianxia.api.vo;

/**
 * @ClassName: ScanPayVO
 * @Description: 扫码支付VO类
 * @Author: Zed
 * @Date: 2019-01-02 14:01
 * @Version:1.0.0
 **/

public class ScanPayVO {
    private Double amount;    //支付金额
    private String scancode;   //扫码类型
    private String payId;      //支付商id
    private String topay;      //支付商名称
    private String uid;
    private String userName;
    private String bankcode;   //银行编号(非必传，有些快捷支付必传)
    private String bankCardNo; //银行卡id(非必传，有些快捷支付必传)
    private String mobile;     //手机端标识(手机端必传)
    private String oriMobile;     //手机端标识原始参数
    private String pmapsconfig; //支付商配置
    private String cid;        //平台编码
    private String cagent;     //平台id
    private String refererUrl;
    private String payUrl;
    private String ip;
    private String orderNo;  //订单号
    private String payType;    //扫码类型
    private String payCode;
    private String description;
    private String reservedField;  //预留字段

    public String getTopay() {
        return topay;
    }

    public void setTopay(String topay) {
        this.topay = topay;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReservedField() {
        return reservedField;
    }

    public void setReservedField(String reservedField) {
        this.reservedField = reservedField;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getScancode() {
        return scancode;
    }

    public void setScancode(String scancode) {
        this.scancode = scancode;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPmapsconfig() {
        return pmapsconfig;
    }

    public void setPmapsconfig(String pmapsconfig) {
        this.pmapsconfig = pmapsconfig;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public String getRefererUrl() {
        return refererUrl;
    }

    public void setRefererUrl(String refererUrl) {
        this.refererUrl = refererUrl;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOriMobile() {
        return oriMobile;
    }

    public void setOriMobile(String oriMobile) {
        this.oriMobile = oriMobile;
    }
}
