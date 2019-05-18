package com.cn.tianxia.api.vo;

/**
 * @ClassName: BankPayVO
 * @Description: 网银支付VO类
 * @Author: Zed
 * @Date: 2019-01-02 11:08
 * @Version:1.0.0
 **/

public class BankPayVO {
    private Double amount;  //支付金额
    private String bankcode; //银行编码
    private String payId;    //支付商id
    private String uid;  //用户id
    private String username;  //用户名
    private String orderNo;  //订单号
    private String pay_url;  //支付url
    private String return_url;  //回调url
    private String pmapsconfig; //支付商配置
    private String cid;        //平台编码
    private String cagent;     //平台id
    private String payType;    // 1 网银
    private String ip;
    private String topay;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPay_url() {
        return pay_url;
    }

    public void setPay_url(String pay_url) {
        this.pay_url = pay_url;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
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

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTopay() {
        return topay;
    }

    public void setTopay(String topay) {
        this.topay = topay;
    }
}
