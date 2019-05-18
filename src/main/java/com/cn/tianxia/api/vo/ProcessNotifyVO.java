package com.cn.tianxia.api.vo;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/1/17 17:25
 * @Description: 处理回调请求VO类
 */
public class ProcessNotifyVO {
    private String payment;
    private String order_no;
    private String trade_no;
    private String trade_status;
    private String t_trade_status;
    private String ip;
    private String infoMap;
    private String ret__success;
    private String ret__failed;
    private double realAmount;
    private JSONObject config;

    public String getRet__failed() {
        return ret__failed;
    }

    public void setRet__failed(String ret__failed) {
        this.ret__failed = ret__failed;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public double getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(double realAmount) {
        this.realAmount = realAmount;
    }

    public String getRet__success() {
        return ret__success;
    }

    public void setRet__success(String ret__success) {
        this.ret__success = ret__success;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getTrade_status() {
        return trade_status;
    }

    public void setTrade_status(String trade_status) {
        this.trade_status = trade_status;
    }

    public String getT_trade_status() {
        return t_trade_status;
    }

    public void setT_trade_status(String t_trade_status) {
        this.t_trade_status = t_trade_status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getInfoMap() {
        return infoMap;
    }

    public void setInfoMap(String infoMap) {
        this.infoMap = infoMap;
    }

	public JSONObject getConfig() {
		return config;
	}

	public void setConfig(JSONObject config) {
		this.config = config;
	}
}
