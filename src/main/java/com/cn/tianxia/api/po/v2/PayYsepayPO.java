package com.cn.tianxia.api.po.v2;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PaymentsPO
 * @Description 支付商列表PO类
 * @author Hardy
 * @Date 2019年3月12日 下午3:15:51
 * @version 1.0.0
 */
public class PayYsepayPO implements Serializable {

    private static final long serialVersionUID = 1114484957211668038L;

    private String id;// 支付商ID

    private String paymentName;// 支付商名称

    private Double minquota;// 最小限额

    private Double maxquota;// 最大限额

    private Integer isSolid;// 是否有固额

    private List<String> solidAmouns;// 固额列表

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public Double getMinquota() {
        return minquota;
    }

    public void setMinquota(Double minquota) {
        this.minquota = minquota;
    }

    public Double getMaxquota() {
        return maxquota;
    }

    public void setMaxquota(Double maxquota) {
        this.maxquota = maxquota;
    }

    public Integer getIsSolid() {
        return isSolid;
    }

    public void setIsSolid(Integer isSolid) {
        this.isSolid = isSolid;
    }

    public List<String> getSolidAmouns() {
        return solidAmouns;
    }

    public void setSolidAmouns(List<String> solidAmouns) {
        this.solidAmouns = solidAmouns;
    }

    @Override
    public String toString() {
        return "PaymentListPO [id=" + id + ", paymentName=" + paymentName + ", minquota=" + minquota + ", maxquota="
                + maxquota + ", isSolid=" + isSolid + ", solidAmouns=" + solidAmouns + "]";
    }

}
