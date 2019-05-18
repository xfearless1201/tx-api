package com.cn.tianxia.api.po;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName: PaymentChannelPO
 * @Description: 可用支付类型VO
 * @Author: Zed
 * @Date: 2018-12-31 19:19
 * @Version:1.0.0
 **/

public class PaymentChannelPO extends BaseResponse{

    public static final String SUCCESS_CODE = "1000";  //支付渠道数据获取成功

//    public static final String ERROR_UNLOGIN_STATUS = "1001"; //用户未登陆
//
//    public static final String ERROR_EMPTY_STATUS = "1002";  //渠道数据为空
//
//    public static final String ERROR_UNSET_STATUS = "1003";  //未设置渠道数据



    public static JSONObject success(JSONArray pcChannel, JSONArray mbChannel, String msg ){
        JSONObject data = new JSONObject();
        data.put("code",SUCCESS_CODE);
        data.put("status",SUCCESS_STATUS);
        data.put("PCchannel",pcChannel);
        data.put("MBchannel",mbChannel);
        data.put("msg",msg);
        return data;
    }
    
    public static JSONObject error(JSONArray pcChannel, JSONArray mbChannel, String msg ){
        JSONObject data = new JSONObject();
        data.put("code",ERROR_CODE);
        data.put("status",ERROR_STATUS);
        data.put("PCchannel",pcChannel);
        data.put("MBchannel",mbChannel);
        data.put("msg",msg);
        return data;
    }
}
