package com.cn.tianxia.api.po;

import com.cn.tianxia.api.project.v2.CagentQrcodepayEntity;

import net.sf.json.JSONObject;


/**
 * 
 * @ClassName OfflineScanReponse
 * @Description 线下扫码返回封装类
 * @author Hardy
 * @Date 2019年1月11日 下午3:41:00
 * @version 1.0.0
 */
public class OfflineScanReponse {
    
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
        data.put("code", EROOR_CODE);
        return data;
    }
    
    public static final JSONObject success(String msg,CagentQrcodepayEntity cagentQrcodepayEntity){
        JSONObject data = new JSONObject();
        //data.put("msg", msg);
        data.put("status", SUCCESS_STATUS);
        data.put("accountimg", cagentQrcodepayEntity.getAccountimg());
        data.put("minquota",cagentQrcodepayEntity.getMinquota());
        data.put("maxquota",cagentQrcodepayEntity.getMaxquota());
        data.put("dayquota", cagentQrcodepayEntity.getDayquota());
        data.put("id",cagentQrcodepayEntity.getId());
        data.put("code", SUCCESS_CODE);
        return data;
    }

}
