package com.cn.tianxia.api.vo;

import java.io.Serializable;

/**
 * 商品信息VO
 * 
 * @author Bing
 */
public class PluOrderVO implements Serializable {

    private static final long serialVersionUID = 1638836611684153933L;

    private String uid;// 用户ID

    private String cid;// 用户平台ID

    private Integer id;// 商品ID

    private Integer num;// 购买数量

    private String deliverName;// 收货人姓名

    private String deliverPhone;// 收货人电话号码

    private String deliverRmk;// 备注

    private String deliverAddress;// 收货人地址

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getDeliverName() {
        return deliverName;
    }

    public void setDeliverName(String deliverName) {
        this.deliverName = deliverName;
    }

    public String getDeliverPhone() {
        return deliverPhone;
    }

    public void setDeliverPhone(String deliverPhone) {
        this.deliverPhone = deliverPhone;
    }

    public String getDeliverRmk() {
        return deliverRmk;
    }

    public void setDeliverRmk(String deliverRmk) {
        this.deliverRmk = deliverRmk;
    }

    public String getDeliverAddress() {
        return deliverAddress;
    }

    public void setDeliverAddress(String deliverAddress) {
        this.deliverAddress = deliverAddress;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public String toString() {
        return "PluOrderVO [uid=" + uid + ", cid=" + cid + ", id=" + id + ", num=" + num + ", deliverName="
                + deliverName + ", deliverPhone=" + deliverPhone + ", deliverRmk=" + deliverRmk + ", deliverAddress="
                + deliverAddress + "]";
    }

    
}
