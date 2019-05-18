package com.cn.tianxia.api.vo;

import java.io.Serializable;

/**
 * @ClassName CreateWithDrawOrderVO
 * @Description 创建提现订单VO类
 * @author Hardy
 * @Date 2019年1月29日 下午2:58:13
 * @version 1.0.0
 */
public class CreateWithDrawOrderVO implements Serializable {

    private static final long serialVersionUID = 7217660819507087839L;

    private String uid;// 用户ID
    private int credit;// 提现金额
    private String cardid;// 提现卡号
    private String password;// 提现密码
    private Double poundage;// 手续费
    private Double administrative_fee;// 行政费
    private Double withdrawConfig;// 强制提款比率
    private String billNo;// 订单号

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getPoundage() {
        return poundage;
    }

    public void setPoundage(Double poundage) {
        this.poundage = poundage;
    }

    public Double getAdministrative_fee() {
        return administrative_fee;
    }

    public void setAdministrative_fee(Double administrative_fee) {
        this.administrative_fee = administrative_fee;
    }

    public Double getWithdrawConfig() {
        return withdrawConfig;
    }

    public void setWithdrawConfig(Double withdrawConfig) {
        this.withdrawConfig = withdrawConfig;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    @Override
    public String toString() {
        return "CreateWithDrawOrderVO [uid=" + uid + ", credit=" + credit + ", cardid=" + cardid + ", password="
                + password + ", poundage=" + poundage + ", administrative_fee=" + administrative_fee
                + ", withdrawConfig=" + withdrawConfig + ", billNo=" + billNo + "]";
    }

}
