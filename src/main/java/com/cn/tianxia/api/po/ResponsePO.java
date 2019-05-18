package com.cn.tianxia.api.po;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName ResponsePO
 * @Description 封装返回结果
 * @author Hardy
 * @Date 2019年1月22日 上午11:31:26
 * @version 1.0.0
 */
public class ResponsePO {

    public static final String SUCCESS_STATUS = "success";//成功

    public static final String FAILD_STATUS = "faild";//失败
    
    public static final String ERROR_STATUS ="error";//异常 
    
    public static final String SUCCESS_CODE = "0";
    
    public static final String FAILD_CODE = "1";
    
    public static final String ERROR_CODE = "2";
    
    public static JSONObject success(){
        JSONObject data = new JSONObject();
        data.put("status", SUCCESS_STATUS);
        data.put("code", SUCCESS_CODE);
        data.put("errmsg", "操作成功");
        return data;
    }

    public static JSONObject faild(String message){
        JSONObject data = new JSONObject();
        data.put("status", FAILD_STATUS);
        data.put("code", FAILD_CODE);
        data.put("errmsg", message);
        return data;
    }
    
    public static JSONObject error(String message){
        JSONObject data = new JSONObject();
        data.put("status", ERROR_STATUS);
        data.put("code", ERROR_CODE);
        data.put("errmsg", message);
        return data;
    }

}
