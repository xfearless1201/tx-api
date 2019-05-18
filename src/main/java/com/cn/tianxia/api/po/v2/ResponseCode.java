package com.cn.tianxia.api.po.v2;

public interface ResponseCode {

    static final String ERROR_CODE = "error";//异常
    
    static final String FAIL_CODE = "fail";//失败
    
    static final String SUCCESS_CODE = "success";
    
    static final int ERROR_STATUS = 0;
    
    static final int SUCCESS_STATUS = 1;
    
    static final int FAIL_STATUS = 2;//失败
}
