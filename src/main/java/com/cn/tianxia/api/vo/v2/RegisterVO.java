package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;

/**
 * @Auther: zed
 * @Date: 2019/1/23 19:43
 * @Description: 用户注册VO
 */
public class RegisterVO implements Serializable {

    private static final long serialVersionUID = -3165443052617910156L;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 手机号
     */
    private String mobileNo;
    /**
     * 密码
     */
    private String passWord;
    /**
     * 确认密码
     */
    private String repassWord;
    /**
     * 注册token
     */
    private String reguuid;
    /**
     * 缓存uuid
     */
    private String ruuid;
    /**
     * 图片验证码
     */
    private String imgcode;
    /**
     * 是否手机
     */
    private String isMobile;
    /**
     * 取款密码
     */
    private String qkpwd;
    /**
     * 确认取款密码
     */
    private String reqkpwd;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 代理号
     */
    private String cagent;
    /**
     * 手机端注册验证码
     */
    private String msgCode;
    /**
     * 注册来源
     */
    private String from;

    private String proxyname;
    private String referralCode;
    private String remark;
    private String isImgCode;
    private String refererUrl;
    private String address;
    private String loginIp;
    private String simgcode;
    private String agpassword;

    
    private String activityAmount;//活动金额 
    
    private String activityId;//活动ID
    
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getRepassWord() {
        return repassWord;
    }

    public void setRepassWord(String repassWord) {
        this.repassWord = repassWord;
    }

    public String getReguuid() {
        return reguuid;
    }

    public void setReguuid(String reguuid) {
        this.reguuid = reguuid;
    }

    public String getRuuid() {
        return ruuid;
    }

    public void setRuuid(String ruuid) {
        this.ruuid = ruuid;
    }

    public String getImgcode() {
        return imgcode;
    }

    public void setImgcode(String imgcode) {
        this.imgcode = imgcode;
    }

    public String getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(String isMobile) {
        this.isMobile = isMobile;
    }

    public String getQkpwd() {
        return qkpwd;
    }

    public void setQkpwd(String qkpwd) {
        this.qkpwd = qkpwd;
    }

    public String getReqkpwd() {
        return reqkpwd;
    }

    public void setReqkpwd(String reqkpwd) {
        this.reqkpwd = reqkpwd;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getProxyname() {
        return proxyname;
    }

    public void setProxyname(String proxyname) {
        this.proxyname = proxyname;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIsImgCode() {
        return isImgCode;
    }

    public void setIsImgCode(String isImgCode) {
        this.isImgCode = isImgCode;
    }

    public String getRefererUrl() {
        return refererUrl;
    }

    public void setRefererUrl(String refererUrl) {
        this.refererUrl = refererUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getSimgcode() {
        return simgcode;
    }

    public void setSimgcode(String simgcode) {
        this.simgcode = simgcode;
    }

    public String getAgpassword() {
        return agpassword;
    }

    public void setAgpassword(String agpassword) {
        this.agpassword = agpassword;
    }
    
    public String getActivityAmount() {
        return activityAmount;
    }

    
    public void setActivityAmount(String activityAmount) {
        this.activityAmount = activityAmount;
    }

    
    public String getActivityId() {
        return activityId;
    }

    
    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    @Override
    public String toString() {
        return "RegisterVO [userName=" + userName + ", mobileNo=" + mobileNo + ", passWord=" + passWord
                + ", repassWord=" + repassWord + ", reguuid=" + reguuid + ", ruuid=" + ruuid + ", imgcode=" + imgcode
                + ", isMobile=" + isMobile + ", qkpwd=" + qkpwd + ", reqkpwd=" + reqkpwd + ", realName=" + realName
                + ", cagent=" + cagent + ", msgCode=" + msgCode + ", from=" + from + ", proxyname=" + proxyname
                + ", referralCode=" + referralCode + ", remark=" + remark + ", isImgCode=" + isImgCode + ", refererUrl="
                + refererUrl + ", address=" + address + ", loginIp=" + loginIp + ", simgcode=" + simgcode
                + ", agpassword=" + agpassword + ", activityAmount=" + activityAmount + ", activityId=" + activityId
                + "]";
    }

    
    
}
