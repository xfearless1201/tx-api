package com.cn.tianxia.api.po;

import net.sf.json.JSONObject;

/**
 * @ClassName: BaseResponse
 * @Description: JSON返回基础类
 * @Author: Zed
 * @Date: 2019-01-01 21:11
 * @Version:1.0.0
 **/

public class BaseResponse {
    public static final String SUCCESS_STATUS = "success";

    public static final String ERROR_STATUS = "error";
    
    public static final String FAILD_STATUS = "faild";

    public static final String SUCCESS_CODE = "1000";

    public static final String ERROR_CODE = "1001";
    
    public static final String EXCEPTION_CODE = "1002";

    public static JSONObject error(String errorCode, String msg){
        JSONObject data = new JSONObject();
        data.put("code",errorCode);
        data.put("status",ERROR_STATUS);
        data.put("msg",msg);
        return data;
    }

    public static JSONObject success(String msg){
        JSONObject data = new JSONObject();
        data.put("code",SUCCESS_CODE);
        data.put("status",SUCCESS_STATUS);
        data.put("msg",msg);
        return data;
    }
    
    public static JSONObject faild(String errorCode, String msg){
        JSONObject data = new JSONObject();
        data.put("code",ERROR_CODE);
        data.put("status",FAILD_STATUS);
        data.put("msg",msg);
        return data;
    }
}
