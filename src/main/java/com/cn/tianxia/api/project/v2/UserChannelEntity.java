package com.cn.tianxia.api.project.v2;

import java.io.Serializable;

/**
 * 
 * @ClassName UserChannelEntity
 * @Description 用户支付渠道表实体类
 * @author Hardy
 * @Date 2019年3月12日 下午12:25:30
 * @version 1.0.0
 */
public class UserChannelEntity implements Serializable{
    
    private static final long serialVersionUID = -9086366301402788365L;

    private Integer id;

    private Integer paymentId;

    private Integer cid;

    private Integer status;

    private String type;

    private Integer typeid;

    private String channel;

    private Float dividendRate;

    private Float codingRate;

    private String solidAmount;

    private Integer solidStatus;

    private Integer isDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getTypeid() {
        return typeid;
    }

    public void setTypeid(Integer typeid) {
        this.typeid = typeid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel == null ? null : channel.trim();
    }

    public Float getDividendRate() {
        return dividendRate;
    }

    public void setDividendRate(Float dividendRate) {
        this.dividendRate = dividendRate;
    }

    public Float getCodingRate() {
        return codingRate;
    }

    public void setCodingRate(Float codingRate) {
        this.codingRate = codingRate;
    }

    public String getSolidAmount() {
        return solidAmount;
    }

    public void setSolidAmount(String solidAmount) {
        this.solidAmount = solidAmount == null ? null : solidAmount.trim();
    }

    public Integer getSolidStatus() {
        return solidStatus;
    }

    public void setSolidStatus(Integer solidStatus) {
        this.solidStatus = solidStatus;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}