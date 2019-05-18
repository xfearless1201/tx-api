package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;

/**
 * @Auther: zed
 * @Date: 2019/2/15 14:38
 * @Description: 手机号登录VO类
 */
public class MobileLoginVO implements Serializable {
    private final static long serialVersionUID = 3234897592347983598L;
    private String mobileNo;
    private String msgCode;
    private String isMobile;
    private String ip;
    private String address;
    private String refurl;
    private String cagent;

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(String isMobile) {
        this.isMobile = isMobile;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRefurl() {
        return refurl;
    }

    public void setRefurl(String refurl) {
        this.refurl = refurl;
    }
}
