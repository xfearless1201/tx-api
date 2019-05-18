package com.cn.tianxia.api.dx.service;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class YDXShortMessageServiceImpl {
	
	private final  Logger logger = LoggerFactory.getLogger(YDXShortMessageServiceImpl.class);
	
	public  String url ;//请求地址= "http://api.1cloudsp.com/api/v2/send"
	public  String accesskey ; //用户开发key = "Se2iTDv7e0OFOGdd"
	public  String accessSecret ; //用户开发秘钥= "6NQHSqea3TZXmM3xTkzIriOGBlHhPw28"
	public  String templateId ; //模板ID = "2425" 
	public  String regid ; //模板ID = "2434" 
	public YDXShortMessageServiceImpl(Map<String, String> pmap) {
	    JSONObject jo =JSONObject.parseObject(JSON.toJSONString(pmap));
			if (null != pmap) {
				accesskey = jo.get("accesskey").toString();
				accessSecret = jo.get("accessSecret").toString();
				templateId = jo.get("templateId").toString(); 
				regid = jo.get("regid").toString(); 
				url = jo.get("url").toString();
			}
	}
	//普通短信
    public String SendOut(String mobile, String content,String sign) {
    	try{
    		 HttpClient httpClient = new HttpClient();
    	        PostMethod postMethod = new PostMethod(url);
    	        postMethod.getParams().setContentCharset("UTF-8");
    	        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler()); 
    	        NameValuePair[] data = {
    	                new NameValuePair("accesskey", accesskey),
    	                new NameValuePair("secret", accessSecret),
    	                new NameValuePair("sign", sign),
    	                new NameValuePair("templateId",templateId),
    	                new NameValuePair("mobile", mobile),
    	                new NameValuePair("content", URLEncoder.encode(content, "utf-8"))//（示例模板：{1}您好，您的订单于{2}已通过{3}发货，运单号{4}）
    	        };
    	        postMethod.setRequestBody(data);

    	        int statusCode = httpClient.executeMethod(postMethod);
    	        
    	        String myJsonObject = postMethod.getResponseBodyAsString();
    	        JSONObject returnStr = JSONObject.parseObject(myJsonObject);
    	        System.out.println("statusCode: " + statusCode + ", body: "
    	                    + postMethod.getResponseBodyAsString());
    	        if("0".equals(returnStr.get("code"))){
    	        	logger.info("发送成功");
    	        	return "success";
    	        }else{
    	        	logger.info("发送失败原因:"+returnStr.get("msg"));
    	        	return "fail";
    	        }
    	}catch(Exception e){
    		return "fail";
    	}
       
    } 
    
    public String SendRegOut(String mobile, String content,String sign) {
    	try{
    		 HttpClient httpClient = new HttpClient();
    	        PostMethod postMethod = new PostMethod(url);
    	        postMethod.getParams().setContentCharset("UTF-8");
    	        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler()); 
    	        NameValuePair[] data = {
    	                new NameValuePair("accesskey", accesskey),
    	                new NameValuePair("secret", accessSecret),
    	                new NameValuePair("sign", sign),
    	                new NameValuePair("templateId",regid),
    	                new NameValuePair("mobile", mobile),
    	                new NameValuePair("content", URLEncoder.encode(mobile+"##"+content, "utf-8"))//（示例模板：{1}您好，您的订单于{2}已通过{3}发货，运单号{4}）
    	        };
    	        postMethod.setRequestBody(data);

    	        int statusCode = httpClient.executeMethod(postMethod);
    	        
    	        String myJsonObject = postMethod.getResponseBodyAsString();
    	        JSONObject returnStr = JSONObject.parseObject(myJsonObject);
    	        System.out.println("statusCode: " + statusCode + ", body: "
    	                    + postMethod.getResponseBodyAsString());
    	        if("0".equals(returnStr.get("code"))){
    	        	logger.info("发送成功");
    	        	return "success";
    	        }else{
    	        	logger.info("发送失败原因:"+returnStr.get("msg"));
    	        	return "fail";
    	        }
    	}catch(Exception e){
    		return "fail";
    	}
       
    } 
}
