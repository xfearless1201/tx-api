package com.cn.tianxia.api.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName RechargeVO
 * @Description 订单VO类
 * @author Hardy
 * @Date 2018年11月21日 下午5:28:58
 * @version 1.0.0
 */
public class RechargeOrderVO implements Serializable {

    private static final long serialVersionUID = -4936127222284919975L;

    private Integer rId;// 订单主键ID
    private Integer uid;// 会员ID
    private byte payType;// 支付类型 1:网银，2:微信,3:支付宝,4:财付通
    private String bankCode;// 网银支付，银行编码 或者 扫码支付 第三方支付渠道类型
    private String orderNo;// 商户唯一订单号
    private Double orderAmount;// 订单金额,两位小数点
    private Date orderTime;// 订单时间
    private String tradeStatus;// 订单状态 paying 待支付 process 处理中 success 支付成功
    private String tradeNo;// 第三方流水号
    private String ip;// IP地址
    private Date finishTime;// 更新订单时间
    private String merchant;// 支付商编码
    private Integer upuid;// 更新人ID
    private Integer payId;// 支付商ID
    private Integer cid;// 平台ID
    private String cagent;// 平台商编码
    private String description;// 订单描述
    private String orderStatus;// 第三方订单状态
    private Double payAmount;// 支付金额
    private String notifyIp;// 回调IP
    private Double cj;// 彩金
    private Double dml;// 打码量
    private Double integralAmount;// 积分
    private Double integralBalance;// 积分余额

    private Double walletBalance;// 用户钱包余额
    private Double remainvalue;// 平台剩余额度
    private String successStatus;// 第三方约定支付成功状态
    private String notifyParams;// 回调请求参数
    private Integer type;
    

    public Integer getrId() {
        return rId;
    }

    public void setrId(Integer rId) {
        this.rId = rId;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public byte getPayType() {
        return payType;
    }

    public void setPayType(byte payType) {
        this.payType = payType;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public Integer getUpuid() {
        return upuid;
    }

    public void setUpuid(Integer upuid) {
        this.upuid = upuid;
    }

    public Integer getPayId() {
        return payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Double payAmount) {
        this.payAmount = payAmount;
    }

    public String getNotifyIp() {
        return notifyIp;
    }

    public void setNotifyIp(String notifyIp) {
        this.notifyIp = notifyIp;
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

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Double getRemainvalue() {
        return remainvalue;
    }

    public void setRemainvalue(Double remainvalue) {
        this.remainvalue = remainvalue;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getIntegralAmount() {
        return integralAmount;
    }

    public void setIntegralAmount(Double integralAmount) {
        this.integralAmount = integralAmount;
    }

    public Double getIntegralBalance() {
        return integralBalance;
    }

    public void setIntegralBalance(Double integralBalance) {
        this.integralBalance = integralBalance;
    }

}
