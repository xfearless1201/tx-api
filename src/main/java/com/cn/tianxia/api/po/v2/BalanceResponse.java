package com.cn.tianxia.api.po.v2;

import com.cn.tianxia.api.po.BaseResponse;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/28 15:19
 * @Description: 获取游戏余额PO
 */
public class BalanceResponse extends BaseResponse {

    public static JSONObject error(String errmsg, String balance) {
        JSONObject jo = new JSONObject();
        jo.put("code", ERROR_CODE);
        jo.put("status", ERROR_STATUS);
        jo.put("errmsg", errmsg);
        jo.put("balance", balance);
        return jo;
    }

    public static JSONObject success(String balance) {
        JSONObject jo = new JSONObject();
        jo.put("code", SUCCESS_CODE);
        jo.put("status", SUCCESS_STATUS);
        jo.put("balance", balance);
        return jo;
    }

}
