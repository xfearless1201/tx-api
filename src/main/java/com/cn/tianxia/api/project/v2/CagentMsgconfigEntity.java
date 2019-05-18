package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

public class CagentMsgconfigEntity implements Serializable {

    private static final long serialVersionUID = 0L;

    private Integer id;

    private Integer cid;

    private String mname;

    private String sign;

    private String config;

    private Date udat;

    private String upsn;

    private String status;

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

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname == null ? null : mname.trim();
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign == null ? null : sign.trim();
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config == null ? null : config.trim();
    }

    public Date getUdat() {
        return udat;
    }

    public void setUdat(Date udat) {
        this.udat = udat;
    }

    public String getUpsn() {
        return upsn;
    }

    public void setUpsn(String upsn) {
        this.upsn = upsn == null ? null : upsn.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}