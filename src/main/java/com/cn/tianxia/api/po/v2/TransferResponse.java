package com.cn.tianxia.api.po.v2;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 17:52
 * @Description: 转账接口层返回对象封装
 */
public class TransferResponse {

    public static JSONObject transferSuccess() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "success");
        jsonObject.put("errmsg", "转账成功");
        return jsonObject;
    }

    public static JSONObject transferFaild() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "faild");
        jsonObject.put("errmsg", "转账失败");
        return jsonObject;
    }

    public static JSONObject transferProcess() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "process");
        jsonObject.put("errmsg", "转账处理中");
        return jsonObject;
    }

    public static JSONObject others(String code, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", code);
        jsonObject.put("errmsg", message);
        jsonObject.put("status", "error");
        return jsonObject;
    }

    public static JSONObject queryBalaceSuccess(String balance) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg","success");
        jsonObject.put("balance",balance);
        return jsonObject;
    }

    public static JSONObject queryBalaceFailed() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg","faild");
        jsonObject.put("balance","0.00");
        return jsonObject;
    }

    public static JSONObject forwardGameForm(String url) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", url);
        jsonObject.put("type","form");
        return jsonObject;
    }

    public static JSONObject forwardGameLink(String url) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", url);
        jsonObject.put("type","link");
        return jsonObject;
    }

    public static JSONObject forwardGameError() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "error");
        return jsonObject;
    }

}
