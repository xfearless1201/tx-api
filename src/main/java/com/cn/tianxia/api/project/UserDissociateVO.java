package com.cn.tianxia.api.project;

import java.io.Serializable;
import java.util.Date;

/**
 * t_user_dissociate
 * @author 
 */
public class UserDissociateVO implements Serializable {
    private Integer uid;

    private String password;

    private String username;

    private String realname;

    private String loginIp;

    private String regIp;

    private String agUsername;

    private String agPassword;

    private String hgUsername;

    /**
     * mg游戏别名
     */
    private String mgUsername;

    private String email;

    private String vipLevel;

    private String mobile;

    private String cagent;

    private String isDaili;

    private String isDelete;

    private String qkPwd;

    private Date regDate;

    private Date loginTime;

    private Double wallet;

    /**
     * 上级用户
     */
    private Integer topUid;

    private String isStop;

    private String isMobile;

    /**
     * 备注
     */
    private String rmk;

    /**
     * 用户类型，对应t_user_type表
     */
    private Integer typeId;

    /**
     * 二级代理商
     */
    private Integer juniorUid;

    /**
     * 注册域名
     */
    private String regurl;

    private String loginmobile;

    private static final long serialVersionUID = 1L;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getRegIp() {
        return regIp;
    }

    public void setRegIp(String regIp) {
        this.regIp = regIp;
    }

    public String getAgUsername() {
        return agUsername;
    }

    public void setAgUsername(String agUsername) {
        this.agUsername = agUsername;
    }

    public String getAgPassword() {
        return agPassword;
    }

    public void setAgPassword(String agPassword) {
        this.agPassword = agPassword;
    }

    public String getHgUsername() {
        return hgUsername;
    }

    public void setHgUsername(String hgUsername) {
        this.hgUsername = hgUsername;
    }

    public String getMgUsername() {
        return mgUsername;
    }

    public void setMgUsername(String mgUsername) {
        this.mgUsername = mgUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(String vipLevel) {
        this.vipLevel = vipLevel;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public String getIsDaili() {
        return isDaili;
    }

    public void setIsDaili(String isDaili) {
        this.isDaili = isDaili;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getQkPwd() {
        return qkPwd;
    }

    public void setQkPwd(String qkPwd) {
        this.qkPwd = qkPwd;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Double getWallet() {
        return wallet;
    }

    public void setWallet(Double wallet) {
        this.wallet = wallet;
    }

    public Integer getTopUid() {
        return topUid;
    }

    public void setTopUid(Integer topUid) {
        this.topUid = topUid;
    }

    public String getIsStop() {
        return isStop;
    }

    public void setIsStop(String isStop) {
        this.isStop = isStop;
    }

    public String getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(String isMobile) {
        this.isMobile = isMobile;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getJuniorUid() {
        return juniorUid;
    }

    public void setJuniorUid(Integer juniorUid) {
        this.juniorUid = juniorUid;
    }

    public String getRegurl() {
        return regurl;
    }

    public void setRegurl(String regurl) {
        this.regurl = regurl;
    }

    public String getLoginmobile() {
        return loginmobile;
    }

    public void setLoginmobile(String loginmobile) {
        this.loginmobile = loginmobile;
    }
}