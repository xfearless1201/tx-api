package com.cn.tianxia.api.po.v2;

import com.cn.tianxia.api.po.BaseResponse;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/22 17:00
 * @Description: 游戏转账po
 */
public class GameTransferResponse extends BaseResponse {

    public static JSONObject error(String msg,String errmsg){
        JSONObject data = new JSONObject();
        data.put("code",ERROR_CODE);
        data.put("status",ERROR_STATUS);
        data.put("msg",msg);
        data.put("errmsg",errmsg);
        return data;
    }

    public static JSONObject fail(String msg, String errmsg, String result){
        JSONObject data = new JSONObject();
        data.put("code",ERROR_CODE);
        data.put("status",ERROR_STATUS);
        data.put("msg",msg);
        data.put("errmsg",errmsg);
        data.put("result",result);
        return data;
    }

}
