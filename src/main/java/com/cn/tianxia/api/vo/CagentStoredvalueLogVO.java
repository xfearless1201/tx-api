package com.cn.tianxia.api.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName CagentStoredvalueLogVO
 * @Description 代理平台可用储值额度日志vo类
 * @author Hardy
 * @Date 2018年11月21日 下午6:20:37
 * @version 1.0.0
 */
public class CagentStoredvalueLogVO implements Serializable {

    private static final long serialVersionUID = -6219955771155288615L;

    private Integer id;// 主键ID
    private Integer cid;// 平台ID
    private String tType;// 日志类型 加款 OR 彩金
    private String type;// 类型:IN OR OUT
    private Double value;// 金额
    private Date addTime;// 生成时间
    private Integer operatorId;// 操作人
    private String loginIp;// 登录ID

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

    public String gettType() {
        return tType;
    }

    public void settType(String tType) {
        this.tType = tType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    @Override
    public String toString() {
        return "CagentStoredvalueLogVO [id=" + id + ", cid=" + cid + ", tType=" + tType + ", type=" + type + ", value="
                + value + ", addTime=" + addTime + ", operatorId=" + operatorId + ", loginIp=" + loginIp + "]";
    }

}
