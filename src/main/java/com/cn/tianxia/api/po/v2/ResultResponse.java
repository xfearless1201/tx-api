package com.cn.tianxia.api.po.v2;


/**
 * 
 * @ClassName ResultResponse
 * @Description HTTP请求响应结果
 * @author
 * @Date 2018年11月7日 上午12:35:01
 * @version 1.0.0
 */
public class ResultResponse {
    
    private String code;
    private int status;
    private String message;
    private Object data;

    private String orderNo;//转账时订单号必须带回
    private String balance;//余额
    
    public ResultResponse(String code, int status, String message, Object data) {
        super();
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResultResponse(String code, int status, String message) {
        super();
        this.code = code;
        this.status = status;
        this.message = message;
    }
    
    public ResultResponse(String code, int status) {
        super();
        this.code = code;
        this.status = status;
    }

    //创建和新建账户
    public static ResultResponse success(String message, Object data){
        return new ResultResponse(ResponseCode.SUCCESS_CODE,ResponseCode.SUCCESS_STATUS, message, data);
    }

    public static ResultResponse faild(String message, Object data){
        return new ResultResponse(ResponseCode.FAIL_CODE, ResponseCode.FAIL_STATUS,message, data);
    }
    
    public static ResultResponse error(String message, Object data){
        return new ResultResponse(ResponseCode.ERROR_CODE, ResponseCode.ERROR_STATUS,message, data);
    }

    //转账模块添加 有余额和订单号返回的
    public ResultResponse(String code,int status, String message, String orderNo , String balance,Object data) {
        super();
        this.code = code;
        this.status = status;
        this.message = message;
        this.orderNo = orderNo;
        this.balance = balance;
        this.data = data;
    }


    public static ResultResponse success(String message, String balance, String orderNo, Object data){
        return new ResultResponse(ResponseCode.SUCCESS_CODE,ResponseCode.SUCCESS_STATUS, message,orderNo,balance, data);
    }

    public static ResultResponse faild(String message, String balance, String orderNo, Object data){
        return new ResultResponse(ResponseCode.FAIL_CODE, ResponseCode.FAIL_STATUS,message,orderNo,balance, data);
    }

    public static ResultResponse error(String message, String balance, String orderNo, Object data){
        return new ResultResponse(ResponseCode.ERROR_CODE, ResponseCode.ERROR_STATUS,message,orderNo,balance, data);
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCode() {
        return code;
    }

    
    public void setCode(String code) {
        this.code = code;
    }

    
    public int getStatus() {
        return status;
    }

    
    public void setStatus(int status) {
        this.status = status;
    }

    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    //========================================================新版============================================//
    public static ResultResponse faild(String message){
        return new ResultResponse(ResponseCode.FAIL_CODE, ResponseCode.FAIL_STATUS,message);
    }
    
    public static ResultResponse success(){
        return new ResultResponse(ResponseCode.SUCCESS_CODE, ResponseCode.FAIL_STATUS);
    }
}
