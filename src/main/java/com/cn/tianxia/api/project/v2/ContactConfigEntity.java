package com.cn.tianxia.api.project.v2;

import java.io.Serializable;

public class ContactConfigEntity implements Serializable{
    
    private static final long serialVersionUID = -7566110916321937427L;

    private Integer id;

    private String cagent;

    private String qq;

    private String wechat;

    private String qqcode;

    private String wechatcode;

    private String customer;

    private String website;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent == null ? null : cagent.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat == null ? null : wechat.trim();
    }

    public String getQqcode() {
        return qqcode;
    }

    public void setQqcode(String qqcode) {
        this.qqcode = qqcode == null ? null : qqcode.trim();
    }

    public String getWechatcode() {
        return wechatcode;
    }

    public void setWechatcode(String wechatcode) {
        this.wechatcode = wechatcode == null ? null : wechatcode.trim();
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer == null ? null : customer.trim();
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website == null ? null : website.trim();
    }
}