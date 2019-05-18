package com.cn.tianxia.api.po.pay;

import com.cn.tianxia.api.common.PayEntity;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName PayScanRes
 * @Description 扫码支付响应结果PO类
 * @author Hardy
 * @Date 2018年11月6日 下午5:10:03
 * @version 1.0.0
 */
public class PayResponse {
    
    private static final String SUCCESS_CODE = "success";//支付结果状态 成功
    
    private static final String ERROR_CODE = "error";//支付结果状态 失败
    
    private static final String SM_FORM_TYPE = "1";//显示类型  form表单
    
    private static final String SM_QRCODE_TYPE = "2";//显示类型   二维码图片
    
    private static final String SM_QRURL_TYPE = "3";//显示类型  二位码链接
    
    private static final String SM_LINK_TYPE = "4";//显示类型  跳转url
    
    private static final String SM_TEMPLATE_TYPE = "5";//显示类型  填充模板
    
    private static final String WY_FORM_TYPE="form";//网银form表单格式
    
    private static final String WY_JSP_TYPE="jsp";//网银jsp类型
    
    private static final String WY_LINK_TYPE="link";//网银跳转链接类型
    
    private static final String WY_WRITER_TYPE="writer";//输出流
    
    /**
     * 
     * @Description form表单提交方式
     * @param entity
     * @param response
     * @param message
     * @return
     */
    public static JSONObject sm_form(PayEntity entity,String response,String message){
        JSONObject data = new JSONObject();
        data.put("res_type",SM_FORM_TYPE);//form表单提交方式
        data.put("status",SUCCESS_CODE);
        data.put("msg", message);
        data.put("acount", String.valueOf(entity.getAmount()));
        data.put("user_name",entity.getUsername());
        data.put("order_no", entity.getOrderNo());
        data.put("html",response);
        return data;
    }
    
    /**
     * 
     * @Description 扫码跳转二位码生成图片结果
     * @param entity
     * @param response
     * @param message
     * @return
     */
    public static JSONObject sm_qrcode(PayEntity entity,String response,String message){
        JSONObject data = new JSONObject();
        data.put("res_type",SM_QRCODE_TYPE);//form表单提交方式
        data.put("status",SUCCESS_CODE);
        data.put("msg", message);
        data.put("acount", String.valueOf(entity.getAmount()));
        data.put("user_name",entity.getUsername());
        data.put("order_no", entity.getOrderNo());
        data.put("qrcode",response);
        return data;
    }
    
    /**
     * 
     * @Description 扫码跳转二维码链接结果
     * @param entity
     * @param response
     * @param message
     * @return
     */
    public static JSONObject sm_qrurl(PayEntity entity,String response,String message){
        JSONObject data = new JSONObject();
        data.put("res_type",SM_QRURL_TYPE);//form表单提交方式
        data.put("status",SUCCESS_CODE);
        data.put("msg", message);
        data.put("acount", String.valueOf(entity.getAmount()));
        data.put("user_name",entity.getUsername());
        data.put("order_no", entity.getOrderNo());
        data.put("qrcode_url",response);
        return data;
    }
    
    /**
     * 
     * @Description 扫码跳转链接返回结果
     * @param entity
     * @param response
     * @param message
     * @return
     */
    public static JSONObject sm_link(PayEntity entity,String response,String message){
        JSONObject data = new JSONObject();
        data.put("res_type",SM_LINK_TYPE);//form表单提交方式
        data.put("status",SUCCESS_CODE);
        data.put("msg", message);
        data.put("acount", String.valueOf(entity.getAmount()));
        data.put("user_name",entity.getUsername());
        data.put("order_no", entity.getOrderNo());
        data.put("html",response);
        return data;
    }
    
    /**
     * 
     * @Description 响应信息模板填充
     * @param entity
     * @param response
     * @param message
     * @return
     */
    public static JSONObject sm_template(PayEntity entity,String response,String message){
        JSONObject data = new JSONObject();
        data.put("res_type",SM_TEMPLATE_TYPE);//模板填充方式
        data.put("status",SUCCESS_CODE);
        data.put("msg", message);
        data.put("acount", String.valueOf(entity.getAmount()));
        data.put("user_name",entity.getUsername());
        data.put("order_no", entity.getOrderNo());
        data.put("template",response);
        return data;
    }
    
    /**
     * 
     * @Description 网银form表单提交结果
     * @param payUrl
     * @param response
     * @return
     */
    public static JSONObject wy_form(String payUrl,String response){
        JSONObject data = new JSONObject();
        data.put("status",SUCCESS_CODE);
        data.put("type",WY_FORM_TYPE);
        data.put("form",response);
        data.put("redirect", "redirect:http://" + payUrl + "/pay.action");
        return data;
    }
    
    /**
     * 
     * @Description 网银jsp返回结果
     * @param payUrl
     * @param response
     * @param jspName
     * @return
     */
    public static JSONObject wy_jsp(String payUrl,String response,String jspName){
        JSONObject data = new JSONObject();
        data.put("status",SUCCESS_CODE);
        data.put("type",WY_JSP_TYPE);
        data.put("jsp_name",jspName);
        data.put("jsp_content",response);
        return data;
    }
    
    /**
     * 
     * @Description 网银跳转返回结果
     * @param response
     * @return
     */
    public static JSONObject wy_link(String response){
        JSONObject data = new JSONObject();
        data.put("status",SUCCESS_CODE);
        data.put("type",WY_LINK_TYPE);
        data.put("link",response);
        return data;
    }
    
    /**
     * 
     * @Description 
     * @param response
     * @return
     */
    public static JSONObject wy_write(String response){
        JSONObject data = new JSONObject();
        data.put("status",SUCCESS_CODE);
        data.put("type",WY_WRITER_TYPE);
        data.put("file",response);
        return data;
    }
    
    /**
     * 
     * @Description 失败返回结果
     * @param message
     * @return
     */
    public static JSONObject error(String message){
        JSONObject data = new JSONObject();
        data.put("status",ERROR_CODE);
        data.put("msg", message);
        return data;
    }
}
