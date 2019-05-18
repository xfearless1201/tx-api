package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

public class UserTypeEntity implements Serializable {

    private static final long serialVersionUID = 775398727647139775L;

    private Integer id;

    private Integer cid;

    private String typename;

    private Date updatetime;

    private String rmk;

    private String status;

    private String isdefault;

    private Integer bankcardId;

    private String alipayId;

    private String wechatId;

    private String tenpayId;

    private String onlinepayId;

    private Float integralRatio;

    private Float cIntegralRatio;

    private String paymentChannel;

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

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(String isdefault) {
        this.isdefault = isdefault;
    }

    public Integer getBankcardId() {
        return bankcardId;
    }

    public void setBankcardId(Integer bankcardId) {
        this.bankcardId = bankcardId;
    }

    public String getAlipayId() {
        return alipayId;
    }

    public void setAlipayId(String alipayId) {
        this.alipayId = alipayId;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getTenpayId() {
        return tenpayId;
    }

    public void setTenpayId(String tenpayId) {
        this.tenpayId = tenpayId;
    }

    public String getOnlinepayId() {
        return onlinepayId;
    }

    public void setOnlinepayId(String onlinepayId) {
        this.onlinepayId = onlinepayId;
    }

    public Float getIntegralRatio() {
        return integralRatio;
    }

    public void setIntegralRatio(Float integralRatio) {
        this.integralRatio = integralRatio;
    }

    public Float getcIntegralRatio() {
        return cIntegralRatio;
    }

    public void setcIntegralRatio(Float cIntegralRatio) {
        this.cIntegralRatio = cIntegralRatio;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    @Override
    public String toString() {
        return "UserTypeEntity [id=" + id + ", cid=" + cid + ", typename=" + typename + ", updatetime=" + updatetime
                + ", rmk=" + rmk + ", status=" + status + ", isdefault=" + isdefault + ", bankcardId=" + bankcardId
                + ", alipayId=" + alipayId + ", wechatId=" + wechatId + ", tenpayId=" + tenpayId + ", onlinepayId="
                + onlinepayId + ", integralRatio=" + integralRatio + ", cIntegralRatio=" + cIntegralRatio
                + ", paymentChannel=" + paymentChannel + "]";
    }

}
