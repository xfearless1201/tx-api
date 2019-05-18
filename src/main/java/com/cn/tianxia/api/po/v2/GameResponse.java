package com.cn.tianxia.api.po.v2;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @ClassName GameResponse
 * @Description 封装游戏返回类
 * @author Hardy
 * @Date 2019年2月9日 下午5:09:17
 * @version 1.0.0
 */
public class GameResponse {
    
    public static final String SUCCESS_STATUS = "success";

    public static final String PROCESS_STATUS = "process";
    
    public static final String FAILD_STATUS = "faild";

    public static final String SUCCESS_CODE = "1";

    public static final String PROCESS_CODE = "2";
    
    public static final String FAILD_CODE = "0";
    
    public static JSONObject faild(String message){
        JSONObject data = new JSONObject();
        data.put("msg", "error");
        data.put("errmsg", message);
        data.put("status", FAILD_STATUS);
        data.put("code", FAILD_CODE);
        return data;
    }
    
    public static JSONObject error(String code,String message){
        JSONObject data = new JSONObject();
        data.put("msg", code);
        data.put("errmsg", message);
        data.put("status", FAILD_STATUS);
        data.put("code", FAILD_CODE);
        return data;
    }
    
    public static JSONObject process(String message){
        JSONObject data = new JSONObject();
        data.put("msg", "error");
        data.put("errmsg", message);
        data.put("status", PROCESS_STATUS);
        data.put("code", PROCESS_CODE);
        return data;
    }
    
    public static JSONObject success(String message){
        JSONObject data = new JSONObject();
        data.put("msg", "success");
        data.put("errmsg", message);
        data.put("status", SUCCESS_STATUS);
        data.put("code", SUCCESS_CODE);
        return data;
    }
    
    public static JSONObject form(String message){
        JSONObject data = new JSONObject();
        data.put("msg", message);
        data.put("errmsg", message);
        data.put("status", SUCCESS_STATUS);
        data.put("code", SUCCESS_CODE);
        data.put("type","form");
        return data;
    }
    
    public static JSONObject link(String message){
        JSONObject data = new JSONObject();
        data.put("msg", message);
        data.put("errmsg", message);
        data.put("status", SUCCESS_STATUS);
        data.put("code", SUCCESS_CODE);
        data.put("type","link");
        return data;
    }
}
