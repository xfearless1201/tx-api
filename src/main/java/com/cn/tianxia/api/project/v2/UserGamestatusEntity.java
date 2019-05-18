package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName UserGamestatusEntity
 * @Description 会员游戏状态表实体类
 * @author Hardy
 * @Date 2019年2月7日 下午2:42:56
 * @version 1.0.0
 */
public class UserGamestatusEntity implements Serializable{
    
    private static final long serialVersionUID = -5673736236267007056L;

    private Integer id;

    private Integer uid;

    private String gametype;

    private String status;

    private Date addtime;

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

    public String getGametype() {
        return gametype;
    }

    public void setGametype(String gametype) {
        this.gametype = gametype == null ? null : gametype.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }
}