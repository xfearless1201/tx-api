package com.cn.tianxia.api.project.v2;

import java.io.Serializable;

/**
 * t_user_quantity 实体类 用户打码量
 */
public class UserQuantityEntity implements Serializable {

 private static final long serialVersionUID = -3735128322451039488L;


    private Integer id;

    private Integer cid;

    private Integer uid;

    private Double markingQuantity;

    private Double userQuantity;

    private Double winamount;

    private Double userWinamount;

    private Double userQuantityHistory;

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

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Double getMarkingQuantity() {
        return markingQuantity;
    }

    public void setMarkingQuantity(Double markingQuantity) {
        this.markingQuantity = markingQuantity;
    }

    public Double getUserQuantity() {
        return userQuantity;
    }

    public void setUserQuantity(Double userQuantity) {
        this.userQuantity = userQuantity;
    }

    public Double getWinamount() {
        return winamount;
    }

    public void setWinamount(Double winamount) {
        this.winamount = winamount;
    }

    public Double getUserWinamount() {
        return userWinamount;
    }

    public void setUserWinamount(Double userWinamount) {
        this.userWinamount = userWinamount;
    }

    public Double getUserQuantityHistory() {
        return userQuantityHistory;
    }

    public void setUserQuantityHistory(Double userQuantityHistory) {
        this.userQuantityHistory = userQuantityHistory;
    }
}