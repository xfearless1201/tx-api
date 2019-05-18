package com.cn.tianxia.api.vo;

import java.io.Serializable;

/**
 * @ClassName UserWalletVO
 * @Description 用户彩金 和打码量比例
 * @author Hardy
 * @Date 2018年11月21日 下午9:54:20
 * @version 1.0.0
 */
public class CjOrDmlRateVO implements Serializable {

    private static final long serialVersionUID = 406871473615036595L;

    private Integer uid;

    private Double walletBalance;

    private Double dividendRate;// 彩金倍率

    private Double codingRate;// 打码量倍率

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

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }

    @Override
    public String toString() {
        return "CjOrDmlRateVO [uid=" + uid + ", walletBalance=" + walletBalance + ", dividendRate=" + dividendRate
                + ", codingRate=" + codingRate + "]";
    }

}
