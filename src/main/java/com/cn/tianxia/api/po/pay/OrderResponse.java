package com.cn.tianxia.api.po.pay;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName OrderResponse
 * @Description 查询订单响应结果
 * @author Hardy
 * @Date 2018年11月19日 上午10:31:14
 * @version 1.0.0
 */
public class OrderResponse {

    private static final String SUCCESS_CODE = "success";//支付结果状态 成功
    
    private static final String ERROR_CODE = "error";//支付结果状态 失败
    
    private static final int SUCCESS_STATUS = 1;//支付结果状态 成功
    
    private static final int ERROR_STATUS = 0;//支付结果状态 失败
    
    public static JSONObject success(String message,String response){
        JSONObject data = new JSONObject();
        data.put("code",SUCCESS_CODE);
        data.put("status",SUCCESS_STATUS);
        data.put("messge", message);
        data.put("data", response);
        return data;
    }
    
    public static JSONObject error(String message){
        JSONObject data = new JSONObject();
        data.put("code",ERROR_CODE);
        data.put("status",ERROR_STATUS);
        data.put("messge", message);
        return data;
    }
}
