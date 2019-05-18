package com.cn.tianxia.api.dx.service;
 
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class DDYShortMessageServiceImpl {
	
	private final  Logger logger = LoggerFactory.getLogger(DDYShortMessageServiceImpl.class);
	
	
	// 查账户信息的http地址
//    private  String URL_GET_USER_INFO = "https://api.dingdongcloud.com/v1/sms/userinfo";

    // 查询账户余额的http地址
//    private  String URL_GET_BALANCE = "https://api.dingdongcloud.com/v1/sms/querybalance";

    // 验证码短信发送接口的http地址 "https://api.dingdongcloud.com/v1/sms/sendyzm"
    private  String URL_SEND_YZM ;

//    private  String URL_SEND_YYYZM = "https://api.dingdongcloud.com/v1/sms/sendyyyzm";
 
    // 通知短信发送接口的http地址 = "https://api.dingdongcloud.com/v1/sms/sendtz";
    private  String URL_SEND_TZ ;

    // 营销短信发送接口的http地址
//    private  String URL_SEND_YX = "https://api.dingdongcloud.com/v1/sms/sendyx";

    // 编码格式。发送编码格式统一用UTF-8
    private  String ENCODING = "UTF-8";
 // 修改为您的apikey. apikey可在官网（https://www.dingdongcloud.com)登录后获取bae87d0c3baff316fc9e1b93ae6e01e5
    public String apikey;
    
    public DDYShortMessageServiceImpl(Map<String, String> pmap) {
        JSONObject jo =JSONObject.parseObject(JSON.toJSONString(pmap));
		if (null != pmap) {
			apikey = jo.get("apikey").toString();
			URL_SEND_YZM = jo.get("URL_SEND_YZM").toString();
			URL_SEND_TZ = jo.get("URL_SEND_TZ").toString();
		}
	}
    
    public String SendRegOut(String mobile,String yzmContent,String sign){  
       String content="【"+sign+"】尊敬的用户:您已注册完成,帐号:"+mobile+",密码:"+yzmContent+",请登录修改密码"; 
       String returnStr = sendTz(apikey, mobile, content);
       JSONObject myJsonObject = JSONObject.parseObject(returnStr);
       if("1".equals(myJsonObject.get("code").toString())){
       	logger.info("发送成功");
       	return "success";
       }else{
       	logger.info("发送失败原因:"+myJsonObject.get("msg"));
       	return "fail";
       }
	}
    
    public String SendOut(String mobile,String yzmContent,String sign){  
    	String content="【"+sign+"】你的验证码是 :"+yzmContent; 
       String returnStr = sendYzm(apikey, mobile, content);
       JSONObject myJsonObject = JSONObject.parseObject(returnStr);
       if("1".equals(myJsonObject.get("code").toString())){
       	logger.info("发送成功");
       	return "success";
       }else{
       	logger.info("发送失败原因:"+myJsonObject.get("msg"));
       	return "fail";
       }
	}
    
    /**
     * 发送验证码短信
     * 
     * @param apikey
     *            apikey
     * @param mobile
     *            手机号码(唯一，不许多个)
     * @param content
     *            短信发送内容（必须经过utf-8格式编码)
     * @return json格式字符串
     */
    public  String sendYzm(String apikey, String mobile, String content) {
 
        try {
            content = URLEncoder.encode(content, ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        NameValuePair[] data = { new NameValuePair("apikey", apikey),

        new NameValuePair("mobile", mobile),

        new NameValuePair("content", content) };

        return doPost(URL_SEND_YZM, data);
    }
    /**
     * 发送通知短信
     * 
     * @param apikey
     *            apikey
     * @param mobile
     *            手机号码（多个号码用英文半角逗号分开，最多可提交1000个）
     * @param content
     *            短信发送内容（必须经过utf-8格式编码)
     * @return json格式字符串
     */
    public  String sendTz(String apikey, String mobile, String content) {

    	 try {
             content = URLEncoder.encode(content, ENCODING);
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }

        NameValuePair[] data = { new NameValuePair("apikey", apikey),

        new NameValuePair("mobile", mobile),

        new NameValuePair("content", content) };

        return doPost(URL_SEND_TZ, data);
    }
    
    /**
     * 基于HttpClient的post函数
     * PH
     * @param url
     *            提交的URL
     * 
     * @param data
     *            提交NameValuePair参数
     * @return 提交响应
     */
    private  String doPost(String url, NameValuePair[] data) {

        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url); 
        method.setRequestBody(data); 
        client.getParams().setConnectionManagerTimeout(10000);
        try {
            client.executeMethod(method);
            return method.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }    

}
