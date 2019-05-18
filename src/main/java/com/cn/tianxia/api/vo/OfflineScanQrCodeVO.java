package com.cn.tianxia.api.vo;

/**
 * @ClassName OfflineScanQrCodeVO
 * @Description 线下扫码VO类
 * @author Hardy
 * @Date 2019年1月11日 下午3:26:44
 * @version 1.0.0
 */
public class OfflineScanQrCodeVO {

    private String uid;// 用户ID
    private String id;// 二维码图片ID
    private String amount;// 订单金额
    private String orderNum;// 订单号
    private String type;// 扫码类型 1 支付宝 2 财付通 3 微信

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "OfflineScanQrCodeVO [uid=" + uid + ", id=" + id + ", amount=" + amount + ", orderNum=" + orderNum
                + ", type=" + type + "]";
    }

}
