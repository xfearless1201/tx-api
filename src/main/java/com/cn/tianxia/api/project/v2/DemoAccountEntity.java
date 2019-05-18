package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName DemoAccountEntity
 * @Description 游戏试玩账号表实体类
 * @author Hardy
 * @Date 2019年2月6日 上午11:31:28
 * @version 1.0.0
 */
public class DemoAccountEntity implements Serializable{
    
    private static final long serialVersionUID = -6125199734394604047L;

    private Integer id;

    private String username;

    private String password;

    private Date addtime;

    private String ip;

    private String cagent;

    private String accountcode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent == null ? null : cagent.trim();
    }

    public String getAccountcode() {
        return accountcode;
    }

    public void setAccountcode(String accountcode) {
        this.accountcode = accountcode == null ? null : accountcode.trim();
    }
}