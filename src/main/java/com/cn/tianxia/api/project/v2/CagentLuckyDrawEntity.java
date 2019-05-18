package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

public class CagentLuckyDrawEntity implements Serializable {

    private static final long serialVersionUID = 0L;

    private Integer id;

    private Integer cid;

    private String luckyname;

    private String status;

    private Float amountlimit;

    private Float amountused;

    private Float minamount;

    private Float maxamount;

    private Date begintime;

    private Date endtime;

    private String type;

    private Integer updateuid;

    private Date updatetime;

    private Integer adduid;

    private Date addtime;

    private String typesof;

    private Date oldbegintime;

    private Date oldendtime;

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

    public String getLuckyname() {
        return luckyname;
    }

    public void setLuckyname(String luckyname) {
        this.luckyname = luckyname == null ? null : luckyname.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Float getAmountlimit() {
        return amountlimit;
    }

    public void setAmountlimit(Float amountlimit) {
        this.amountlimit = amountlimit;
    }

    public Float getAmountused() {
        return amountused;
    }

    public void setAmountused(Float amountused) {
        this.amountused = amountused;
    }

    public Float getMinamount() {
        return minamount;
    }

    public void setMinamount(Float minamount) {
        this.minamount = minamount;
    }

    public Float getMaxamount() {
        return maxamount;
    }

    public void setMaxamount(Float maxamount) {
        this.maxamount = maxamount;
    }

    public Date getBegintime() {
        return begintime;
    }

    public void setBegintime(Date begintime) {
        this.begintime = begintime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getUpdateuid() {
        return updateuid;
    }

    public void setUpdateuid(Integer updateuid) {
        this.updateuid = updateuid;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Integer getAdduid() {
        return adduid;
    }

    public void setAdduid(Integer adduid) {
        this.adduid = adduid;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public String getTypesof() {
        return typesof;
    }

    public void setTypesof(String typesof) {
        this.typesof = typesof == null ? null : typesof.trim();
    }

    public Date getOldbegintime() {
        return oldbegintime;
    }

    public void setOldbegintime(Date oldbegintime) {
        this.oldbegintime = oldbegintime;
    }

    public Date getOldendtime() {
        return oldendtime;
    }

    public void setOldendtime(Date oldendtime) {
        this.oldendtime = oldendtime;
    }
}