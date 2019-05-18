package com.cn.tianxia.api.po;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName: PaymentListPO
 * @Description: 支付渠道列表VO
 * @Author: Zed
 * @Date: 2018-12-31 19:21
 * @Version:1.0.0
 **/

public class PaymentListPO extends BaseResponse{

    public static JSONObject success(JSONArray typeList, String msg){
        JSONObject data = new JSONObject();
        data.put("code",SUCCESS_CODE);
        data.put("status",SUCCESS_STATUS);
        data.put("typeList",typeList);
        data.put("msg",msg);
        return data;
    }

}
