package com.cn.tianxia.api.po;

import com.cn.tianxia.api.project.v2.AmountRecordEntity;

import net.sf.json.JSONObject;


/**
 * 
 * @ClassName BankRemittanceResponse
 * @Description 银行汇款封装类
 * @author Hardy
 * @Date 2019年1月5日 上午11:56:35
 * @version 1.0.0
 */
public class BankRemittanceResponse {

    public static final String ERROR_STATUS = "faild";
    
    public static final String SUCCESS_STATUS = "success";
    
    public static final JSONObject error(String msg){
        JSONObject data = new JSONObject();
        data.put("msg", msg);
        data.put("status", ERROR_STATUS);
        return data;
    }
    
    public static final JSONObject success(String msg,AmountRecordEntity amountRecordEntity){
        JSONObject data = new JSONObject();
        data.put("msg", msg);
        data.put("status", SUCCESS_STATUS);
        data.put("ref_id", amountRecordEntity.getRefId());
        data.put("amount", amountRecordEntity.getAmount());
        return data;
    }
}
