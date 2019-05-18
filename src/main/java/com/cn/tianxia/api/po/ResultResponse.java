package com.cn.tianxia.api.po;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName ResultResponse
 * @Description 封装返回结果
 * @author Hardy
 * @Date 2018年11月21日 下午8:12:19
 * @version 1.0.0
 */
public class ResultResponse {
    
    public static final String SUCCESS_CODE = "SUCCESS";//支付结果状态 成功
    
    public static final String ERROR_CODE = "ERROR";//支付结果状态 失败
    
    public static final int SUCCESS_STATUS = 1;//支付结果状态 成功
    
    public static final int ERROR_STATUS = 0;//支付结果状态 失败
    
    public static JSONObject success(){
        JSONObject data = new JSONObject();
        data.put("code",SUCCESS_CODE);
        data.put("status",SUCCESS_STATUS);
        return data;
    }
    
    public static JSONObject error(){
        JSONObject data = new JSONObject();
        data.put("code",ERROR_CODE);
        data.put("status",ERROR_STATUS);
        return data;
    }
    
    public static JSONObject success(String response){
        JSONObject data = new JSONObject();
        data.put("code",SUCCESS_CODE);
        data.put("status",SUCCESS_STATUS);
        data.put("mqParams", response);
        return data;
    }
}
