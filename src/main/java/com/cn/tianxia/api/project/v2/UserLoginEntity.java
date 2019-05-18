package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;
/**
 * 
 * @ClassName UserLoginEntity
 * @Description 用户表实体类
 * @author Hardy
 * @Date 2019年2月6日 下午5:34:47
 * @version 1.0.0
 */
public class UserLoginEntity implements Serializable{
    
    private static final long serialVersionUID = -478572974206967978L;

    private Integer id;

    private Integer uid;

    private Date loginTime;

    private String loginIp;

    private Byte isLogin;

    private Integer loginNum;

    private String status;

    private String isMobile;

    private String address;

    private String refurl;

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

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp == null ? null : loginIp.trim();
    }

    public Byte getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(Byte isLogin) {
        this.isLogin = isLogin;
    }

    public Integer getLoginNum() {
        return loginNum;
    }

    public void setLoginNum(Integer loginNum) {
        this.loginNum = loginNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(String isMobile) {
        this.isMobile = isMobile == null ? null : isMobile.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getRefurl() {
        return refurl;
    }

    public void setRefurl(String refurl) {
        this.refurl = refurl == null ? null : refurl.trim();
    }
}