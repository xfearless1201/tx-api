package com.cn.tianxia.api.project;

/**
 * @ClassName Recharge
 * @Description 订单实体类
 * @author Hardy
 * @Date 2018年11月8日 下午12:32:13
 * @version 1.0.0
 */
public class Recharge {

    private Integer id;// 订单表逐渐ID
    private Integer uid;// 用户ID
    private Integer cid;// 平台ID
    private Integer payId;// 支付商ID
    private String cagent;// 平台编号
    private String orderNo;// 订单号
    private Double orderAmount;// 订单金额
    private String tradeStatus;// 订单状态
    private String tradeNo;// 流水号
    private String finishTime;// 更新时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getPayId() {
        return payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    @Override
    public String toString() {
        return "Recharge [id=" + id + ", uid=" + uid + ", cid=" + cid + ", payId=" + payId + ", cagent=" + cagent
                + ", orderNo=" + orderNo + ", orderAmount=" + orderAmount + ", tradeStatus=" + tradeStatus
                + ", tradeNo=" + tradeNo + ", finishTime=" + finishTime + "]";
    }

}
