package com.cn.tianxia.api.project.v2;

import java.io.Serializable;

public class CagentLuckyDrawDetailEntity implements Serializable {

    private static final long serialVersionUID = 0L;

    private Integer id;

    private Integer lid;

    private Float balance;

    private Integer times;

    private Float validbetamount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLid() {
        return lid;
    }

    public void setLid(Integer lid) {
        this.lid = lid;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Float getValidbetamount() {
        return validbetamount;
    }

    public void setValidbetamount(Float validbetamount) {
        this.validbetamount = validbetamount;
    }

    @Override
    public String toString() {
        return "CagentLuckyDrawDetailEntity{" +
                "id=" + id +
                ", lid=" + lid +
                ", balance=" + balance +
                ", times=" + times +
                ", validbetamount=" + validbetamount +
                '}';
    }
}