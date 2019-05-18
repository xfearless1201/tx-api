package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName CagentQrcodepayEntity
 * @Description 平台扫描支付配置实体类
 * @author Hardy
 * @Date 2019年1月11日 下午3:07:14
 * @version 1.0.0
 */
public class CagentQrcodepayEntity implements Serializable{
    
    private static final long serialVersionUID = 8797331676770423799L;

    private Integer id;

    private Integer cid;

    private String type;

    private String accountcode;

    private String accountname;

    private String accountimg;

    private String rmk;

    private String status;

    private Date updatetime;

    private Integer uid;

    private Double dayquota;

    private Double minquota;

    private Double maxquota;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getAccountcode() {
        return accountcode;
    }

    public void setAccountcode(String accountcode) {
        this.accountcode = accountcode == null ? null : accountcode.trim();
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname == null ? null : accountname.trim();
    }

    public String getAccountimg() {
        return accountimg;
    }

    public void setAccountimg(String accountimg) {
        this.accountimg = accountimg == null ? null : accountimg.trim();
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk == null ? null : rmk.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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

    public Double getDayquota() {
        return dayquota;
    }

    public void setDayquota(Double dayquota) {
        this.dayquota = dayquota;
    }

    public Double getMinquota() {
        return minquota;
    }

    public void setMinquota(Double minquota) {
        this.minquota = minquota;
    }

    public Double getMaxquota() {
        return maxquota;
    }

    public void setMaxquota(Double maxquota) {
        this.maxquota = maxquota;
    }
}