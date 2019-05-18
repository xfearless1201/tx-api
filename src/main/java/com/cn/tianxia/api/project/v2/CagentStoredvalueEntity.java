package com.cn.tianxia.api.project.v2;

import java.io.Serializable;

/**
 * 
 * @ClassName CagentStoredvalueEntity
 * @Description 平台可用储值额度表实体类
 * @author Hardy
 * @Date 2019年3月13日 下午3:23:11
 * @version 1.0.0
 */
public class CagentStoredvalueEntity implements Serializable{
   
    private static final long serialVersionUID = 2829015521542393211L;

    private Integer id;

    private Integer cid;

    private Double totalvalue;

    private Double usedvaue;

    private Double remainvalue;

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

    public Double getTotalvalue() {
        return totalvalue;
    }

    public void setTotalvalue(Double totalvalue) {
        this.totalvalue = totalvalue;
    }

    public Double getUsedvaue() {
        return usedvaue;
    }

    public void setUsedvaue(Double usedvaue) {
        this.usedvaue = usedvaue;
    }

    public Double getRemainvalue() {
        return remainvalue;
    }

    public void setRemainvalue(Double remainvalue) {
        this.remainvalue = remainvalue;
    }
}