package com.cn.tianxia.api.common;

import java.util.Map;

/**
 * <p>
 * Title: PayEntity
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:Copyright (c) 2017
 * </p>
 * 
 * @author zouwei
 * @date 2017年12月6日
 */
public class PayEntity {

    // 来源域名
    private String refererUrl;
    // ip
    private String ip;
    // 用户id
    private String uId;
    // 支付类型编码
    private String payCode;
    // 金额
    private double amount;
    // 支付商编码
    private String topay;
    // 订单号
    private String orderNo;
    // 支付网关跳转url
    private String payUrl;
    // 支付商
    private String payId;
    // 用户姓名
    private String username;
    // 用于手机h5支付参数
    private String mobile;

    private String oriMobile;     //手机端标识原始参数

    private String cagent;// 平台编码

    private String cid;// 平台ID

    private String description;// 订单描述

    private String payType;// 支付渠道

    // 支付商配置
    private Map<String, String> payConfig;
    // 扩展参数
    private Map<String, Object> extendMap;

    public String getRefererUrl() {
        return refererUrl;
    }

    public void setRefererUrl(String refererUrl) {
        this.refererUrl = refererUrl;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

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

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public Map<String, String> getPayConfig() {
        return payConfig;
    }

    public void setPayConfig(Map<String, String> payConfig) {
        this.payConfig = payConfig;
    }

    public Map<String, Object> getExtendMap() {
        return extendMap;
    }

    public void setExtendMap(Map<String, Object> extendMap) {
        this.extendMap = extendMap;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOriMobile() {
        return oriMobile;
    }

    public void setOriMobile(String oriMobile) {
        this.oriMobile = oriMobile;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    @Override
    public String toString() {
        return "PayEntity{" +
                "refererUrl='" + refererUrl + '\'' +
                ", ip='" + ip + '\'' +
                ", uId='" + uId + '\'' +
                ", payCode='" + payCode + '\'' +
                ", amount=" + amount +
                ", topay='" + topay + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", payUrl='" + payUrl + '\'' +
                ", payId='" + payId + '\'' +
                ", username='" + username + '\'' +
                ", mobile='" + mobile + '\'' +
                ", oriMobile='" + oriMobile + '\'' +
                ", cagent='" + cagent + '\'' +
                ", cid='" + cid + '\'' +
                ", description='" + description + '\'' +
                ", payType='" + payType + '\'' +
                ", payConfig=" + payConfig +
                ", extendMap=" + extendMap +
                '}';
    }
}
