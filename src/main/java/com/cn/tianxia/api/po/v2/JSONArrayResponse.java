package com.cn.tianxia.api.po.v2;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONArrayResponse {
    
    public static final String ERROR_STATUS = "faild";
    
    public static final String SUCCESS_STATUS = "success";
    
    public static final String ERROR_CODE = "0";
    
    public static final String SUCCESS_CODE = "1";
    
    public static JSONArray faild(String message){
        JSONArray array = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("error", message);
        data.put("status", ERROR_STATUS);
        data.put("code", ERROR_CODE);
        array.add(data);
        return array;
    }
    
    public static JSONArray success(String message,Object obj){
        JSONArray array = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("error", message);
        data.put("status", SUCCESS_STATUS);
        data.put("code", SUCCESS_CODE);
        data.put("data", obj);
        array.add(data);
        return array;
    }
}
