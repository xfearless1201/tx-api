package com.cn.tianxia.api.po.v2;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName LoginResponse
 * @Description 登录封装返回类
 * @author Hardy
 * @Date 2019年2月12日 上午11:15:23
 * @version 1.0.0
 */
public class LoginResponse {
    
    public static final String SUCCESS_STATUS = "success";

    public static final String ERROR_STATUS = "error";
    
    public static final String FAILD_STATUS = "faild";

    public static final String SUCCESS_CODE = "1";

    public static final String ERROR_CODE = "2";
    
    public static final String FIALD_CODE = "0";
    
    public static JSONObject faild(String errorCode, String msg){
        JSONObject data = new JSONObject();
        data.put("code",ERROR_CODE);
        data.put("status",FAILD_STATUS);
        data.put("errmsg", msg);
        return data;
    }

}
