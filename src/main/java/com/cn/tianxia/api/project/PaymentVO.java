package com.cn.tianxia.api.project;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName PaymentVO
 * @Description 支付VO类
 * @author Hardy
 * @Date 2018年11月2日 下午4:40:45
 * @version 1.0.0
 */
public class PaymentVO implements Serializable {

    private static final long serialVersionUID = 4799943581017253273L;

    private Integer uid;// 用户ID

    private String username;// 用户名称

    private String mobile;// 移动端标识

    private Integer cid;// 会员所属平台ID

    private String cagentCode;// 会员所属平台编码

    private String cagentName;// 平台名称

    private Integer typeId;// 会员分层ID

    private Double dividendRate;// 打码量

    private Double codingRate;// 彩金

    private Integer payId;// 支付商ID

    private String sellerId;// 商户ID

    private String paymentName;// 支付商编码

    private String paymentConfig;// 支付商配置信息

    private String payUrl;// 支付跳转地址

    private Integer ish5Wx;// 是否为微信H5

    private Integer ish5Ali;// 是否为支付宝H5

    private Integer ish5Cft;

    private Integer ish5Yl;//

    private Integer ish5Jd;//

    private Double maxquota;// 最大限额

    private Double minquota;// 最小限额

    private Double wxMinquota;// 微信最小限额

    private Double wxMaxquota;// 微信最大限额

    private Double aliMinquota;// 支付宝最小限额

    private Double aliMaxquota;// 支付宝最大限额

    private Double qrminquota;// 最小限额

    private Double qrmaxquota;

    private Double ylMinquota;

    private Double ylMaxquota;

    private Double jdMinquota;

    private Double jdMaxquota;//

    private Double kjMinquota;

    private Double kjMaxquota;

    private Double wxtmMinquota;//

    private Double wxtmMaxquota;//

    private Double alitmMinquota;//

    private Double alitmMaxquota;//
    
    
    

    private String amount;// 订单金额

    private String payChannel;// 支付渠道编码
    
    private Integer enterChannel;//1网银 2扫码

    private String payCode;// 第三方支付渠道编码,或者第三方网银银行编码

    private String payType;// 支付类型

    private Double balance;// 用户钱包余额
    
    private Integer mbish5;//是否为手机类型 0:H5 1:扫码'
    
    private String ip;//ID地址
    
    private String returnUrl;//同步回调地址
    
    private String orderNo;//订单号
    
    
    private Double orderAmount;//订单金额
    
    private Date orderTime;//订单时间
    
    private String tradeStatus;//订单状态

    private String tradeNo;//流水号
    
    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
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

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public String getCagentCode() {
        return cagentCode;
    }

    public void setCagentCode(String cagentCode) {
        this.cagentCode = cagentCode;
    }

    public String getCagentName() {
        return cagentName;
    }

    public void setCagentName(String cagentName) {
        this.cagentName = cagentName;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Double getDividendRate() {
        return dividendRate;
    }

    public void setDividendRate(Double dividendRate) {
        this.dividendRate = dividendRate;
    }

    public Double getCodingRate() {
        return codingRate;
    }

    public void setCodingRate(Double codingRate) {
        this.codingRate = codingRate;
    }

    public Integer getPayId() {
        return payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
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

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public Integer getIsh5Wx() {
        return ish5Wx;
    }

    public void setIsh5Wx(Integer ish5Wx) {
        this.ish5Wx = ish5Wx;
    }

    public Integer getIsh5Ali() {
        return ish5Ali;
    }

    public void setIsh5Ali(Integer ish5Ali) {
        this.ish5Ali = ish5Ali;
    }

    public Integer getIsh5Cft() {
        return ish5Cft;
    }

    public void setIsh5Cft(Integer ish5Cft) {
        this.ish5Cft = ish5Cft;
    }

    public Integer getIsh5Yl() {
        return ish5Yl;
    }

    public void setIsh5Yl(Integer ish5Yl) {
        this.ish5Yl = ish5Yl;
    }

    public Integer getIsh5Jd() {
        return ish5Jd;
    }

    public void setIsh5Jd(Integer ish5Jd) {
        this.ish5Jd = ish5Jd;
    }

    public Double getMaxquota() {
        return maxquota;
    }

    public void setMaxquota(Double maxquota) {
        this.maxquota = maxquota;
    }

    public Double getMinquota() {
        return minquota;
    }

    public void setMinquota(Double minquota) {
        this.minquota = minquota;
    }

    public Double getWxMinquota() {
        return wxMinquota;
    }

    public void setWxMinquota(Double wxMinquota) {
        this.wxMinquota = wxMinquota;
    }

    public Double getWxMaxquota() {
        return wxMaxquota;
    }

    public void setWxMaxquota(Double wxMaxquota) {
        this.wxMaxquota = wxMaxquota;
    }

    public Double getAliMinquota() {
        return aliMinquota;
    }

    public void setAliMinquota(Double aliMinquota) {
        this.aliMinquota = aliMinquota;
    }

    public Double getAliMaxquota() {
        return aliMaxquota;
    }

    public void setAliMaxquota(Double aliMaxquota) {
        this.aliMaxquota = aliMaxquota;
    }

    public Double getQrminquota() {
        return qrminquota;
    }

    public void setQrminquota(Double qrminquota) {
        this.qrminquota = qrminquota;
    }

    public Double getQrmaxquota() {
        return qrmaxquota;
    }

    public void setQrmaxquota(Double qrmaxquota) {
        this.qrmaxquota = qrmaxquota;
    }

    public Double getYlMinquota() {
        return ylMinquota;
    }

    public void setYlMinquota(Double ylMinquota) {
        this.ylMinquota = ylMinquota;
    }

    public Double getYlMaxquota() {
        return ylMaxquota;
    }

    public void setYlMaxquota(Double ylMaxquota) {
        this.ylMaxquota = ylMaxquota;
    }

    public Double getJdMinquota() {
        return jdMinquota;
    }

    public void setJdMinquota(Double jdMinquota) {
        this.jdMinquota = jdMinquota;
    }

    public Double getJdMaxquota() {
        return jdMaxquota;
    }

    public void setJdMaxquota(Double jdMaxquota) {
        this.jdMaxquota = jdMaxquota;
    }

    public Double getKjMinquota() {
        return kjMinquota;
    }

    public void setKjMinquota(Double kjMinquota) {
        this.kjMinquota = kjMinquota;
    }

    public Double getKjMaxquota() {
        return kjMaxquota;
    }

    public void setKjMaxquota(Double kjMaxquota) {
        this.kjMaxquota = kjMaxquota;
    }

    public Double getWxtmMinquota() {
        return wxtmMinquota;
    }

    public void setWxtmMinquota(Double wxtmMinquota) {
        this.wxtmMinquota = wxtmMinquota;
    }

    public Double getWxtmMaxquota() {
        return wxtmMaxquota;
    }

    public void setWxtmMaxquota(Double wxtmMaxquota) {
        this.wxtmMaxquota = wxtmMaxquota;
    }

    public Double getAlitmMinquota() {
        return alitmMinquota;
    }

    public void setAlitmMinquota(Double alitmMinquota) {
        this.alitmMinquota = alitmMinquota;
    }

    public Double getAlitmMaxquota() {
        return alitmMaxquota;
    }

    public void setAlitmMaxquota(Double alitmMaxquota) {
        this.alitmMaxquota = alitmMaxquota;
    }
    
    public String getAmount() {
        return amount;
    }
    
    public void setAmount(String amount) {
        this.amount = amount;
    }
    
    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
    
    public Integer getMbish5() {
        return mbish5;
    }
    
    public void setMbish5(Integer mbish5) {
        this.mbish5 = mbish5;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
    
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    
    public Integer getEnterChannel() {
        return enterChannel;
    }

    
    public void setEnterChannel(Integer enterChannel) {
        this.enterChannel = enterChannel;
    }

    
    public Double getOrderAmount() {
        return orderAmount;
    }

    
    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    @Override
    public String toString() {
        return "PaymentVO [uid=" + uid + ", username=" + username + ", mobile=" + mobile + ", cid=" + cid
                + ", cagentCode=" + cagentCode + ", cagentName=" + cagentName + ", typeId=" + typeId + ", dividendRate="
                + dividendRate + ", codingRate=" + codingRate + ", payId=" + payId + ", sellerId=" + sellerId
                + ", paymentName=" + paymentName + ", paymentConfig=" + paymentConfig + ", payUrl=" + payUrl
                + ", ish5Wx=" + ish5Wx + ", ish5Ali=" + ish5Ali + ", ish5Cft=" + ish5Cft + ", ish5Yl=" + ish5Yl
                + ", ish5Jd=" + ish5Jd + ", maxquota=" + maxquota + ", minquota=" + minquota + ", wxMinquota="
                + wxMinquota + ", wxMaxquota=" + wxMaxquota + ", aliMinquota=" + aliMinquota + ", aliMaxquota="
                + aliMaxquota + ", qrminquota=" + qrminquota + ", qrmaxquota=" + qrmaxquota + ", ylMinquota="
                + ylMinquota + ", ylMaxquota=" + ylMaxquota + ", jdMinquota=" + jdMinquota + ", jdMaxquota="
                + jdMaxquota + ", kjMinquota=" + kjMinquota + ", kjMaxquota=" + kjMaxquota + ", wxtmMinquota="
                + wxtmMinquota + ", wxtmMaxquota=" + wxtmMaxquota + ", alitmMinquota=" + alitmMinquota
                + ", alitmMaxquota=" + alitmMaxquota + ", amount=" + amount + ", payChannel=" + payChannel
                + ", enterChannel=" + enterChannel + ", payCode=" + payCode + ", payType=" + payType + ", balance="
                + balance + ", mbish5=" + mbish5 + ", ip=" + ip + ", returnUrl=" + returnUrl + ", orderNo=" + orderNo
                + ", orderAmount=" + orderAmount + ", orderTime=" + orderTime + ", tradeStatus=" + tradeStatus
                + ", tradeNo=" + tradeNo + "]";
    }
    
}
