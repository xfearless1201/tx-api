package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;

/**
 * @ClassName UserLoginVO
 * @Description 用户登录VO类
 * @author Hardy
 * @Date 2019年2月6日 下午3:00:29
 * @version 1.0.0
 */
public class UserLoginVO implements Serializable {

    private static final long serialVersionUID = 3404232464422130878L;

    private String username;// 登录账号(账号、手机号码、邮箱)

    private String password;// 登录密码

    private String isMobile;// 是否为移动端

    private String cagent;// 平台编码

    private String refurl;// 请求域名

    private String ip;// 登录请求ip地址

    private String address;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(String isMobile) {
        this.isMobile = isMobile;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public String getRefurl() {
        return refurl;
    }

    public void setRefurl(String refurl) {
        this.refurl = refurl;
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

    @Override
    public String toString() {
        return "UserLoginVO [username=" + username + ", password=" + password + ", isMobile=" + isMobile + ", cagent="
                + cagent + ", refurl=" + refurl + ", ip=" + ip + ", address=" + address + "]";
    }

}
