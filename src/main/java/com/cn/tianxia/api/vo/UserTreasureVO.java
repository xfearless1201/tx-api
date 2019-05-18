package com.cn.tianxia.api.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName UserTreasureVO
 * @Description 用户资金流水VO类
 * @author Hardy
 * @Date 2018年11月21日 下午6:05:27
 * @version 1.0.0
 */
public class UserTreasureVO implements Serializable {

    private static final long serialVersionUID = -3468831491478077735L;

    private Integer id;// 资金流水主键ID
    private Integer uid;// 用户ID
    private Double amount;// 金额大小
    private Double oldMoney;// 变更前金额
    private Double newMoney;// 变更后金额
    private String number;// 订单号
    private String tType;// 来源类型 存款 or 彩金
    private String type;// 转入转出(IN OR OUT)
    private Date addTime;// 操作时间
    private Integer platformType;// 返回平台类型
    private String sType;// 操作类型 0 系统 1手工
    private String rmk;// 描述
    private Integer operatorId;// 操作人
    private String cagent;// 平台类型
    private Integer isFirst;// 是否首存0:是 1 否
    private String ip;// IP地址

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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getOldMoney() {
        return oldMoney;
    }

    public void setOldMoney(Double oldMoney) {
        this.oldMoney = oldMoney;
    }

    public Double getNewMoney() {
        return newMoney;
    }

    public void setNewMoney(Double newMoney) {
        this.newMoney = newMoney;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String gettType() {
        return tType;
    }

    public void settType(String tType) {
        this.tType = tType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public String getsType() {
        return sType;
    }

    public void setsType(String sType) {
        this.sType = sType;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public Integer getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(Integer isFirst) {
        this.isFirst = isFirst;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "UserTreasureVO [id=" + id + ", uid=" + uid + ", amount=" + amount + ", oldMoney=" + oldMoney
                + ", newMoney=" + newMoney + ", number=" + number + ", tType=" + tType + ", type=" + type + ", addTime="
                + addTime + ", platformType=" + platformType + ", sType=" + sType + ", rmk=" + rmk + ", operatorId="
                + operatorId + ", cagent=" + cagent + ", isFirst=" + isFirst + ", ip=" + ip + ", getId()=" + getId()
                + ", getUid()=" + getUid() + ", getAmount()=" + getAmount() + ", getOldMoney()=" + getOldMoney()
                + ", getNewMoney()=" + getNewMoney() + ", getNumber()=" + getNumber() + ", gettType()=" + gettType()
                + ", getType()=" + getType() + ", getAddTime()=" + getAddTime() + ", getPlatformType()="
                + getPlatformType() + ", getsType()=" + getsType() + ", getRmk()=" + getRmk() + ", getOperatorId()="
                + getOperatorId() + ", getCagent()=" + getCagent() + ", getIsFirst()=" + getIsFirst() + ", getIp()="
                + getIp() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
                + super.toString() + "]";
    }

}
