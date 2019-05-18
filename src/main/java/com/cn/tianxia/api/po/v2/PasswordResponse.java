package com.cn.tianxia.api.po.v2;

import java.io.Serializable;

import net.sf.json.JSONObject;


/**
 * 
 * @ClassName PasswordResponse
 * @Description 密码类接口封装返回类
 * @author Hardy
 * @Date 2019年2月1日 下午5:46:42
 * @version 1.0.0
 */
public class PasswordResponse implements Serializable{

    private static final long serialVersionUID = -7249021071275967234L;
    
    public static final String ERROR_STATUS = "faild";
    
    public static final String SUCCESS_STATUS = "success";
    
    public static final String EROOR_CODE = "0";
    
    public static final String SUCCESS_CODE = "1";

    public static final JSONObject error(String msg){
        JSONObject data = new JSONObject();
        data.put("msg", msg);
        data.put("status", ERROR_STATUS);
        data.put("code", EROOR_CODE);
        return data;
    }
    
    public static final JSONObject success(String msg){
        JSONObject data = new JSONObject();
        data.put("msg", msg);
        data.put("status", SUCCESS_STATUS);
        data.put("code", SUCCESS_CODE);
        return data;
    }
}
