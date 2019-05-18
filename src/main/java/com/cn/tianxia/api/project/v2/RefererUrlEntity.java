package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName RefererUrlEntity
 * @Description 域名白名单表实体类
 * @author Hardy
 * @Date 2019年2月6日 下午3:33:00
 * @version 1.0.0
 */
public class RefererUrlEntity implements Serializable{
    
    private static final long serialVersionUID = -3735128322451039214L;

    private Integer id;

    private String domain;

    private String name;

    private String rmk;

    private Date addtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain == null ? null : domain.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk == null ? null : rmk.trim();
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }
}