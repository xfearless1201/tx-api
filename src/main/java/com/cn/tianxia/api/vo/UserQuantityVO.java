package com.cn.tianxia.api.vo;

import java.io.Serializable;

/**
 * @ClassName UserQuantityVO
 * @Description 用户打码量
 * @author Hardy
 * @Date 2018年11月21日 下午6:26:50
 * @version 1.0.0
 */
public class UserQuantityVO implements Serializable {

    private static final long serialVersionUID = -5566406278010080831L;

    private Integer id;// 主键ID
    private Integer cid;// 平台ID
    private Integer uid;// 用户ID
    private Double markingQuantity;// 打码量
    private Double userQuantity;// 会员打码量
    private Double winAmount;// 计算输赢金额
    private Double userWinAmount;// 用户输赢金额
    private Double userQuantityHistory;// 用户历史打码量

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

    public Double getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(Double winAmount) {
        this.winAmount = winAmount;
    }

    public Double getUserWinAmount() {
        return userWinAmount;
    }

    public void setUserWinAmount(Double userWinAmount) {
        this.userWinAmount = userWinAmount;
    }

    public Double getUserQuantityHistory() {
        return userQuantityHistory;
    }

    public void setUserQuantityHistory(Double userQuantityHistory) {
        this.userQuantityHistory = userQuantityHistory;
    }

    @Override
    public String toString() {
        return "UserQuantityVO [id=" + id + ", cid=" + cid + ", uid=" + uid + ", markingQuantity=" + markingQuantity
                + ", userQuantity=" + userQuantity + ", winAmount=" + winAmount + ", userWinAmount=" + userWinAmount
                + ", userQuantityHistory=" + userQuantityHistory + "]";
    }

}
