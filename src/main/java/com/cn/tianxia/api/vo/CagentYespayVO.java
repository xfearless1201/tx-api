package com.cn.tianxia.api.vo;

import java.io.Serializable;

/**
 * @ClassName CagentYespayVO
 * @Description 支付商配置信息
 * @author Hardy
 * @Date 2018年11月22日 上午10:51:09
 * @version 1.0.0
 */
public class CagentYespayVO implements Serializable {

    private static final long serialVersionUID = 6358035190431049950L;

    private Integer id;

    private Integer cid;

    private String paymentName;

    private String paymentConfig;

    private String payUrl;// 支付跳转地址

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
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

    @Override
    public String toString() {
        return "CagentYespayVO [id=" + id + ", cid=" + cid + ", paymentName=" + paymentName + ", paymentConfig="
                + paymentConfig + ", payUrl=" + payUrl + "]";
    }

}
