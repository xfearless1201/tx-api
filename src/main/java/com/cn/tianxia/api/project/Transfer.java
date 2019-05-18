package com.cn.tianxia.api.project;

import java.util.Date;

/**
 * @ClassName Transfer
 * @Description 转账实体类
 * @author Hardy
 * @Date 2018年11月6日 下午8:39:21
 * @version 1.0.0
 */
public class Transfer {

    public final static String TTYPE_IN = "IN";

    public final static String TTYPE_OUT = "OUT";

    public final static int STATUS_WAIT = 0;//待处理

    public final static int STATUS_FINISH = 1;//已处理

    private Integer id;

    private Integer uid;

    private String billno;

    private String username;

    private String tType;

    private Float tMoney;

    private Float oldMoney;

    private Float newMoney;

    private String type;

    private Date tTime;

    private String ip;

    private String result;

    private String cagent;

    private Integer cid;// 平台ID

    private Integer status;// 转账订单状态

    private String description;// 转账订单描述

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String gettType() {
        return tType;
    }

    public void settType(String tType) {
        this.tType = tType;
    }

    public Float gettMoney() {
        return tMoney;
    }

    public void settMoney(Float tMoney) {
        this.tMoney = tMoney;
    }

    public Float getOldMoney() {
        return oldMoney;
    }

    public void setOldMoney(Float oldMoney) {
        this.oldMoney = oldMoney;
    }

    public Float getNewMoney() {
        return newMoney;
    }

    public void setNewMoney(Float newMoney) {
        this.newMoney = newMoney;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date gettTime() {
        return tTime;
    }

    public void settTime(Date tTime) {
        this.tTime = tTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Transfer [id=" + id + ", uid=" + uid + ", billno=" + billno + ", username=" + username + ", tType="
                + tType + ", tMoney=" + tMoney + ", oldMoney=" + oldMoney + ", newMoney=" + newMoney + ", type=" + type
                + ", tTime=" + tTime + ", ip=" + ip + ", result=" + result + ", cagent=" + cagent + ", cid=" + cid
                + ", status=" + status + ", description=" + description + "]";
    }

}
