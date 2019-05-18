package com.cn.tianxia.api.vo.v2;

/**
 * @ClassName PaymentVO
 * @Description 支付VO类
 * @author Hardy
 * @Date 2019年3月18日 下午12:35:36
 * @version 1.0.0
 */
public class PaymentVO {

    private String acounmt;// 订单金额

    private String payId;// 支付商ID

    private String payCode;// 支付编码

    private String ip;// 发起支付IP地址

    private String requestUrl;// 发起支付的域名

    public String getAcounmt() {
        return acounmt;
    }

    public void setAcounmt(String acounmt) {
        this.acounmt = acounmt;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public String toString() {
        return "PaymentVO [acounmt=" + acounmt + ", payId=" + payId + ", payCode=" + payCode + ", ip=" + ip
                + ", requestUrl=" + requestUrl + "]";
    }

}
