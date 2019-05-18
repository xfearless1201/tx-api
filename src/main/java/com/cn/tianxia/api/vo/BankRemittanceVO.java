package com.cn.tianxia.api.vo;

import java.util.Date;

/**
 * @ClassName BankRemittanceVO
 * @Description 异常汇款VO类
 * @author Hardy
 * @Date 2019年1月5日 上午11:28:26
 * @version 1.0.0
 */
public class BankRemittanceVO {

    private String uid;
    private String bid;
    private String name;
    private String account;
    private Double amount;
    private String ctime;
    private String type;
    private String caijin;

    private Integer cid;// 平台商ID
    private String cagent;// 平台编码
    private Integer times;// 汇款次数
    private Date remittanceDate;// 汇款时间

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCaijin() {
        return caijin;
    }

    public void setCaijin(String caijin) {
        this.caijin = caijin;
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

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Date getRemittanceDate() {
        return remittanceDate;
    }

    public void setRemittanceDate(Date remittanceDate) {
        this.remittanceDate = remittanceDate;
    }

    @Override
    public String toString() {
        return "BankRemittanceVO [uid=" + uid + ", bid=" + bid + ", name=" + name + ", account=" + account + ", amount="
                + amount + ", ctime=" + ctime + ", type=" + type + ", caijin=" + caijin + ", cid=" + cid + ", cagent="
                + cagent + ", times=" + times + ", remittanceDate=" + remittanceDate + "]";
    }

}
