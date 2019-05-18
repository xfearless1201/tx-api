package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName PluInventoryEntity
 * @Description 商品库存表实体类
 * @author Hardy
 * @Date 2019年2月25日 下午4:02:34
 * @version 1.0.0
 */
public class PluInventoryEntity implements Serializable{
    
    private static final long serialVersionUID = 1887065312692309736L;

    private Integer id;

    private Integer pluid;

    private Integer cid;

    private Integer num;

    private Integer freezeNum;

    private Date uptime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPluid() {
        return pluid;
    }

    public void setPluid(Integer pluid) {
        this.pluid = pluid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getFreezeNum() {
        return freezeNum;
    }

    public void setFreezeNum(Integer freezeNum) {
        this.freezeNum = freezeNum;
    }

    public Date getUptime() {
        return uptime;
    }

    public void setUptime(Date uptime) {
        this.uptime = uptime;
    }
}