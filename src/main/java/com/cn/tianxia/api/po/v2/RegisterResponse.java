package com.cn.tianxia.api.po.v2;

import com.cn.tianxia.api.po.BaseResponse;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/6 21:05
 * @Description: 用户注册返回po
 */
public class RegisterResponse extends BaseResponse {

    public static JSONObject success(JSONObject msg){
        JSONObject data = new JSONObject();
        data.put("code",SUCCESS_CODE);
        data.put("status",SUCCESS_STATUS);
        data.put("msg",msg);
        return data;
    }

    public static JSONObject error(String errorCode, String msg, String errMsg){
        JSONObject data = new JSONObject();
        data.put("code",errorCode);
        data.put("status",ERROR_STATUS);
        data.put("msg",msg);
        data.put("errMsg",errMsg);
        return data;
    }

}
