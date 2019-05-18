package com.cn.tianxia.api.project.v2;

import java.util.Date;

public class UserLuckrdrawLogEntity {

    private Integer id;

    private Integer cid;

    private Integer lid;

    private Integer uid;

    private String orderid;

    private Float amount;

    private Date addtime;

    private Integer todaytimes;

    private Integer totaltimes;

    private String ip;

    private String address;

    private Double usedBet;

    private String typesof;

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

    public Integer getLid() {
        return lid;
    }

    public void setLid(Integer lid) {
        this.lid = lid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid == null ? null : orderid.trim();
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Integer getTodaytimes() {
        return todaytimes;
    }

    public void setTodaytimes(Integer todaytimes) {
        this.todaytimes = todaytimes;
    }

    public Integer getTotaltimes() {
        return totaltimes;
    }

    public void setTotaltimes(Integer totaltimes) {
        this.totaltimes = totaltimes;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Double getUsedBet() {
        return usedBet;
    }

    public void setUsedBet(Double usedBet) {
        this.usedBet = usedBet;
    }

    public String getTypesof() {
        return typesof;
    }

    public void setTypesof(String typesof) {
        this.typesof = typesof == null ? null : typesof.trim();
    }
}