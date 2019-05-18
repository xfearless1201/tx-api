package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName DissociateEntity
 * @Description 用户游离表实体类
 * @author Hardy
 * @Date 2019年2月6日 下午3:57:09
 * @version 1.0.0
 */
public class DissociateEntity implements Serializable{
    
    private static final long serialVersionUID = -2712181850933317613L;

    private Integer uid;

    private String password;

    private String username;

    private String realname;

    private String loginIp;

    private String regIp;

    private String agUsername;

    private String agPassword;

    private String hgUsername;

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

    private Integer topUid;

    private String isStop;

    private String isMobile;

    private String rmk;

    private Integer typeId;

    private Integer juniorUid;

    private String regurl;

    private String loginmobile;

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
        this.password = password == null ? null : password.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname == null ? null : realname.trim();
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp == null ? null : loginIp.trim();
    }

    public String getRegIp() {
        return regIp;
    }

    public void setRegIp(String regIp) {
        this.regIp = regIp == null ? null : regIp.trim();
    }

    public String getAgUsername() {
        return agUsername;
    }

    public void setAgUsername(String agUsername) {
        this.agUsername = agUsername == null ? null : agUsername.trim();
    }

    public String getAgPassword() {
        return agPassword;
    }

    public void setAgPassword(String agPassword) {
        this.agPassword = agPassword == null ? null : agPassword.trim();
    }

    public String getHgUsername() {
        return hgUsername;
    }

    public void setHgUsername(String hgUsername) {
        this.hgUsername = hgUsername == null ? null : hgUsername.trim();
    }

    public String getMgUsername() {
        return mgUsername;
    }

    public void setMgUsername(String mgUsername) {
        this.mgUsername = mgUsername == null ? null : mgUsername.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(String vipLevel) {
        this.vipLevel = vipLevel == null ? null : vipLevel.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent == null ? null : cagent.trim();
    }

    public String getIsDaili() {
        return isDaili;
    }

    public void setIsDaili(String isDaili) {
        this.isDaili = isDaili == null ? null : isDaili.trim();
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete == null ? null : isDelete.trim();
    }

    public String getQkPwd() {
        return qkPwd;
    }

    public void setQkPwd(String qkPwd) {
        this.qkPwd = qkPwd == null ? null : qkPwd.trim();
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
        this.isStop = isStop == null ? null : isStop.trim();
    }

    public String getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(String isMobile) {
        this.isMobile = isMobile == null ? null : isMobile.trim();
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk == null ? null : rmk.trim();
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
        this.regurl = regurl == null ? null : regurl.trim();
    }

    public String getLoginmobile() {
        return loginmobile;
    }

    public void setLoginmobile(String loginmobile) {
        this.loginmobile = loginmobile == null ? null : loginmobile.trim();
    }
}