package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName CagentWebcodeEntity
 * @Description 洗码表实体类
 * @author Hardy
 * @Date 2019年2月5日 下午8:07:39
 * @version 1.0.0
 */
public class CagentWebcodeEntity implements Serializable{
    
    private static final long serialVersionUID = 5580732615673854435L;

    private Integer id;

    private Integer cid;

    private String type;

    private String code;

    private Date utime;

    private Integer uid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Date getUtime() {
        return utime;
    }

    public void setUtime(Date utime) {
        this.utime = utime;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }
}