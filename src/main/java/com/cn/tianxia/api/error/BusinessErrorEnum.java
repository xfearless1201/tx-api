package com.cn.tianxia.api.error;

/**
 * @Auther: zed
 * @Date: 2019/3/3 10:02
 * @Description: 业务异常信息枚举定义
 */
public enum BusinessErrorEnum implements CommonError {
    // 通用错误类型
    PARAMMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKNOW_ERROR(10002,"未知异常"),
    UUID_NOT_MATCH(10003, "非法请求,UUID不匹配"),
    VERIFYCODE_NOT_MATCH(10004, "验证码不正确"),





    //20000开头为用户信息相关错误定义
    USER_EXIST(20001,"用户已存在"),
    USER_NOT_EXIST(20002,"用户不存在"),
    USER_LOGIN_FAIL(20003,"用户手机号或密码错误"),
    USER_NOT_LOGIN(20004,"用户还未登录"),
    USER_MOBILE_EXIST(20005,"手机号已绑定"),
    USER_STATUS_ERROR(20006,"用户状态异常"),
    //30000开头为转账类异常
    TRANSFER_AMOUNT_LIMIT(30001, "转账金额限制"),
    WALLET_BALANCE_NOT_SUFFICIENT(30002, "用户钱包余额不足"),
    GAME_BALANCE_NOT_SUFFICIENT(30003, "游戏账户余额不足"),
    PLATFORM_IN_MAINTENANCE(30004, "游戏平台维护中"),
    CAGENT_GAME_CLOSED(30005, "代理平台游戏关闭"),
    CREATE_OR_CHECK_ACCOUNT_FAIL(30006, "检查或创建用户失败"),
    CREATE_BILLNO_EROOR(30007,"生成转账订单号异常"),
    REQUEST_ALREADY_EXIST(30008,"转账订单已存在"),


    //40000开头为支付类异常
    RECHARGE_AMOUNT_LIMIT(40001, "充值金额限制"),
    

    //50000开头的为参数错误
    REQUEST_PARAMS_ERROR(50000, "参数异常:")
    ;
    
    
    BusinessErrorEnum(int errCode,String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }


    private int errCode;
    private String errMsg;

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }

    @Override
    public String toString() {
        return "BusinessErrorEnum{" +
                "errCode=" + errCode +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }}
