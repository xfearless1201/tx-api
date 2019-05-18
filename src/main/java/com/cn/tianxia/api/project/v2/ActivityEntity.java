package com.cn.tianxia.api.project.v2;

import java.io.Serializable;

/**
 * @ClassName ActivityEntity
 * @Description 活动表实体类
 * @author Hardy
 * @Date 2019年3月13日 上午11:43:55
 * @version 1.0.0
 */
public class ActivityEntity implements Serializable {

    private static final long serialVersionUID = -1229453071583714224L;

    private Long id;

    private String cagent;

    private Integer cid;

    private Integer typeId;

    private String name;

    private Byte type;

    private Byte status;

    private String ruid;

    private Long stime;

    private Long etime;

    private String cuid;// 修改人

    private Long createTime;

    private Long updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent == null ? null : cagent.trim();
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getRuid() {
        return ruid;
    }

    public void setRuid(String ruid) {
        this.ruid = ruid == null ? null : ruid.trim();
    }

    public Long getStime() {
        return stime;
    }

    public void setStime(Long stime) {
        this.stime = stime;
    }

    public Long getEtime() {
        return etime;
    }

    public void setEtime(Long etime) {
        this.etime = etime;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ActivityEntity [id=" + id + ", cagent=" + cagent + ", cid=" + cid + ", typeId=" + typeId + ", name="
                + name + ", type=" + type + ", status=" + status + ", ruid=" + ruid + ", stime=" + stime + ", etime="
                + etime + ", cuid=" + cuid + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
    }

}
