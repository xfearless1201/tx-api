package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName LoginerrormapEntity
 * @Description 用户登录失败次数表实体类
 * @author Hardy
 * @Date 2019年2月6日 下午5:00:45
 * @version 1.0.0
 */
public class LoginerrormapEntity implements Serializable{
    
    private static final long serialVersionUID = -3744282610001244042L;

    private Integer id;

    private String username;

    private Integer times;

    private Date logintime;

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

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Date getLogintime() {
        return logintime;
    }

    public void setLogintime(Date logintime) {
        this.logintime = logintime;
    }
}