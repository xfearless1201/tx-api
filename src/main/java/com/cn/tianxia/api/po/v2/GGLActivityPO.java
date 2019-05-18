package com.cn.tianxia.api.po.v2;

import java.io.Serializable;

/**
 * @ClassName GGActivityPO
 * @Description 刮刮乐活动PO类
 * @author Hardy
 * @Date 2019年3月13日 下午3:16:07
 * @version 1.0.0
 */
public class GGLActivityPO implements Serializable {

    private static final long serialVersionUID = 3395986786008055870L;

    private Long activityId;// 活动ID

    private Integer status;// 活动开启状态

    private Double usermoney;// 奖金金额

    private Integer type;// 活动类型

    private Integer verifyPhone;//是否验证过手机号

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getUsermoney() {
        return usermoney;
    }

    public void setUsermoney(Double usermoney) {
        this.usermoney = usermoney;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getVerifyPhone() {
        return verifyPhone;
    }

    public void setVerifyPhone(Integer verifyPhone) {
        this.verifyPhone = verifyPhone;
    }

    @Override
    public String toString() {
        return "GGLActivityPO{" +
                "activityId=" + activityId +
                ", status=" + status +
                ", usermoney=" + usermoney +
                ", type=" + type +
                ", verifyPhone=" + verifyPhone +
                '}';
    }
}
