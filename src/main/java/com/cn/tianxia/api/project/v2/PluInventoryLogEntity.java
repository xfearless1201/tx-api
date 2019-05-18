package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName PluInventoryLogEntity
 * @Description 商品库存流水表实体类
 * @author Hardy
 * @Date 2019年2月25日 下午4:03:16
 * @version 1.0.0
 */
public class PluInventoryLogEntity implements Serializable{
    
    private static final long serialVersionUID = 3413599279269668836L;

    private Integer id;

    private Integer cid;

    private Integer pluid;

    private String orderno;

    private Integer num;

    private String type;

    private String tType;

    private Date uptime;

    private Integer upuid;

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

    public Integer getPluid() {
        return pluid;
    }

    public void setPluid(Integer pluid) {
        this.pluid = pluid;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno == null ? null : orderno.trim();
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String gettType() {
        return tType;
    }

    public void settType(String tType) {
        this.tType = tType == null ? null : tType.trim();
    }

    public Date getUptime() {
        return uptime;
    }

    public void setUptime(Date uptime) {
        this.uptime = uptime;
    }

    public Integer getUpuid() {
        return upuid;
    }

    public void setUpuid(Integer upuid) {
        this.upuid = upuid;
    }
}