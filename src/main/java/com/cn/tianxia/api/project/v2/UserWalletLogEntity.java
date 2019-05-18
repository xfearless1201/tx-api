package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName UserWalletLogEntity
 * @Description 会员钱包变动日志表实体类
 * @author Hardy
 * @Date 2019年2月25日 下午4:04:00
 * @version 1.0.0
 */
public class UserWalletLogEntity implements Serializable{
    
    private static final long serialVersionUID = 865775643485105134L;

    private Integer id;

    private Integer uid;

    private String type;

    private String wtype;

    private Float amount;

    private Float oldMoney;

    private Float newMoney;

    private Date uptime;

    private Integer upuid;

    private String rmk;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getWtype() {
        return wtype;
    }

    public void setWtype(String wtype) {
        this.wtype = wtype == null ? null : wtype.trim();
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
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

    public Date getUptime() {
        return uptime;
    }

    public void setUptime(Date uptime) {
        this.uptime = uptime;
    }

    public Integer getUpuid() {
        return upuid;
    }

    public void setUpuid(Integer upuid) {
        this.upuid = upuid;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk == null ? null : rmk.trim();
    }
}