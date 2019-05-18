package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName TransferEntity
 * @Description 转账表实体类
 * @author Hardy
 * @Date 2019年2月1日 上午10:37:59
 * @version 1.0.0
 */
public class TransferEntity implements Serializable {

    private static final long serialVersionUID = -3610266856604913052L;

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

}
