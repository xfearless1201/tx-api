package com.cn.tianxia.api.common.v2;

import com.alibaba.fastjson.JSONObject;

public class BaseResponse {
    
    public static JSONObject error(String status, String msg){
        JSONObject data = new JSONObject();
        data.put("code",ResponseCode.ERROR_CODE);
        data.put("status",status);
        data.put("msg",msg);
        return data;
    }

    public static JSONObject success(String status,String msg){
        JSONObject data = new JSONObject();
        data.put("code",ResponseCode.SUCCESS_CODE);
        data.put("status",status);
        data.put("msg",msg);
        return data;
    }
    
    public static JSONObject faild(String status, String msg){
        JSONObject data = new JSONObject();
        data.put("code",ResponseCode.FAILD_CODE);
        data.put("status",status);
        data.put("msg",msg);
        return data;
    }
}
