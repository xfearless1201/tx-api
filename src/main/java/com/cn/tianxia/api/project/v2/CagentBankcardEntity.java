package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName CagentBankcardEntity
 * @Description 平台银行卡
 * @author Hardy
 * @Date 2019年1月5日 上午11:21:01
 * @version 1.0.0
 */
public class CagentBankcardEntity implements Serializable{

    private static final long serialVersionUID = 2183971952435352381L;

    private Integer id;

    private Integer cid;

    private String cardno;

    private String realname;

    private String bankname;

    private String bankcode;

    private String bankaddress;

    private String type;

    private String status;

    private Float minquota;

    private Float maxquota;

    private Date updatetime;

    private Integer uid;

    private String cagent;// 冗余的平台编码

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

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno == null ? null : cardno.trim();
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname == null ? null : realname.trim();
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname == null ? null : bankname.trim();
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode == null ? null : bankcode.trim();
    }

    public String getBankaddress() {
        return bankaddress;
    }

    public void setBankaddress(String bankaddress) {
        this.bankaddress = bankaddress == null ? null : bankaddress.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Float getMinquota() {
        return minquota;
    }

    public void setMinquota(Float minquota) {
        this.minquota = minquota;
    }

    public Float getMaxquota() {
        return maxquota;
    }

    public void setMaxquota(Float maxquota) {
        this.maxquota = maxquota;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    @Override
    public String toString() {
        return "CagentBankcardEntity [id=" + id + ", cid=" + cid + ", cardno=" + cardno + ", realname=" + realname
                + ", bankname=" + bankname + ", bankcode=" + bankcode + ", bankaddress=" + bankaddress + ", type="
                + type + ", status=" + status + ", minquota=" + minquota + ", maxquota=" + maxquota + ", updatetime="
                + updatetime + ", uid=" + uid + ", cagent=" + cagent + "]";
    }

}
