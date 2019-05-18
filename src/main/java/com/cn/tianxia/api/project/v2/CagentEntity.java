package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述:
 * 平台实体类
 * @Author: Hardy
 * @Date: 2018年12月08日 14:35:33
 **/
public class CagentEntity implements Serializable {
    private static final long serialVersionUID = 7131613693492602461L;
    private Integer id;

    private String code;

    private Float lineCost;

    private String cagent;

    private String name;

    private String type;

    private String rmk;

    private Date addtime;

    private Date upLineTime;

    private Date expirationTime;

    private String domain;

    private String mobileDomain;

    private Integer status;

    private Float storedvalue;

    private Float storedratio;

    private String announcement;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Float getLineCost() {
        return lineCost;
    }

    public void setLineCost(Float lineCost) {
        this.lineCost = lineCost;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent == null ? null : cagent.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk == null ? null : rmk.trim();
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Date getUpLineTime() {
        return upLineTime;
    }

    public void setUpLineTime(Date upLineTime) {
        this.upLineTime = upLineTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain == null ? null : domain.trim();
    }

    public String getMobileDomain() {
        return mobileDomain;
    }

    public void setMobileDomain(String mobileDomain) {
        this.mobileDomain = mobileDomain == null ? null : mobileDomain.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Float getStoredvalue() {
        return storedvalue;
    }

    public void setStoredvalue(Float storedvalue) {
        this.storedvalue = storedvalue;
    }

    public Float getStoredratio() {
        return storedratio;
    }

    public void setStoredratio(Float storedratio) {
        this.storedratio = storedratio;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement == null ? null : announcement.trim();
    }

    @Override
    public String toString() {
        return "CagentEntity{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", lineCost=" + lineCost +
                ", cagent='" + cagent + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", rmk='" + rmk + '\'' +
                ", addtime=" + addtime +
                ", upLineTime=" + upLineTime +
                ", expirationTime=" + expirationTime +
                ", domain='" + domain + '\'' +
                ", mobileDomain='" + mobileDomain + '\'' +
                ", status=" + status +
                ", storedvalue=" + storedvalue +
                ", storedratio=" + storedratio +
                ", announcement='" + announcement + '\'' +
                '}';
    }
}