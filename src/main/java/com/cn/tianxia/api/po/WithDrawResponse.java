package com.cn.tianxia.api.po;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName WithDrawResponse
 * @Description 封装提现返回类
 * @author Hardy
 * @Date 2019年1月29日 下午2:49:30
 * @version 1.0.0
 */
public class WithDrawResponse {

    public static final String ERROR_STATUS = "faild";
    
    public static final String SUCCESS_STATUS = "success";
    
    public static final String EROOR_CODE = "0";
    
    public static final String SUCCESS_CODE = "1";
    
    public static JSONObject faild(String message){
        JSONObject data = new JSONObject();
        data.put("status", ERROR_STATUS);
        data.put("code", EROOR_CODE);
        data.put("msg", message);
        return data;
    }
    
    public static JSONObject success(String message){
        JSONObject data = new JSONObject();
        data.put("status", SUCCESS_STATUS);
        data.put("code", SUCCESS_CODE);
        data.put("msg", message);
        return data;
    }
}
