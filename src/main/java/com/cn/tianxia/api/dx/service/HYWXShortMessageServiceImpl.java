package com.cn.tianxia.api.dx.service;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import org.dom4j.Document;   
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;   
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;;

public class HYWXShortMessageServiceImpl {
	
	private final  Logger logger = LoggerFactory.getLogger(HYWXShortMessageServiceImpl.class);
	//发送短信账号"C75689727"
	public  String account ;
	// 发送短信加密过后密码 "5c186df844511d699fbecd1a2e9ae4a5"
	public  String password ;
	//短信接口地址 "http://106.ihuyi.cn/webservice/sms.php?method=Submit"
	public  String url ;
	
	
	public HYWXShortMessageServiceImpl(Map<String, String> pmap) {
	    JSONObject jo =JSONObject.parseObject(JSON.toJSONString(pmap));
		if (null != pmap) {
			account = jo.get("account").toString();
			password = jo.get("password").toString();
			url = jo.get("url").toString();
		}
	} 
	
	public String SendOut (String mobile,String content){
		HttpClient client = new HttpClient(); 
		PostMethod method = new PostMethod(url);

		client.getParams().setContentCharset("GBK");
		method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=GBK"); 

	    content = new String("您的验证码是：" + content + "。请不要把验证码泄露给其他人。");

		NameValuePair[] data = {//提交短信
			    new NameValuePair("account", account), //查看用户名是登录用户中心->验证码短信->产品总览->APIID
			    new NameValuePair("password", password),  //查看密码请登录用户中心->验证码短信->产品总览->APIKEY 
			    new NameValuePair("mobile", mobile), 
			    new NameValuePair("content", content),
		};
		method.setRequestBody(data);

		try {
			client.executeMethod(method);
			
			String SubmitResult =method.getResponseBodyAsString();

			Document doc = DocumentHelper.parseText(SubmitResult);
			Element root = doc.getRootElement();

			String code = root.elementText("code");
			String msg = root.elementText("msg");
			String smsid = root.elementText("smsid"); 

			 if("2".equals(code)){
				 logger.info("发送短信成功!");
				 return "success";
			 }else{
				 logger.info("发送短信失败原因为："+"=====>"+code+","+smsid+","+msg);
				 return "短信发送失败:"+msg;
			 }

		} catch (HttpException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		} catch (DocumentException e) { 
			e.printStackTrace();
		}
		return null;
	}
	
	public String SendRegOut (String mobile,String content){
		HttpClient client = new HttpClient(); 
		PostMethod method = new PostMethod(url);

		client.getParams().setContentCharset("GBK");
		method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=GBK"); 

	    content = new String("	尊敬的用户:您已注册完成,帐号:"+mobile+",密码:"+content+",请登录修改密码");

		NameValuePair[] data = {//提交短信
			    new NameValuePair("account", account), //查看用户名是登录用户中心->验证码短信->产品总览->APIID
			    new NameValuePair("password", password),  //查看密码请登录用户中心->验证码短信->产品总览->APIKEY 
			    new NameValuePair("mobile", mobile), 
			    new NameValuePair("content", content),
		};
		method.setRequestBody(data);

		try {
			client.executeMethod(method);
			
			String SubmitResult =method.getResponseBodyAsString();

			Document doc = DocumentHelper.parseText(SubmitResult);
			Element root = doc.getRootElement();

			String code = root.elementText("code");
			String msg = root.elementText("msg");
			String smsid = root.elementText("smsid"); 

			 if("2".equals(code)){
				 logger.info("发送短信成功!");
				 return "success";
			 }else{
				 logger.info("发送短信失败原因为："+"=====>"+code+","+smsid+","+msg);
				 return "短信发送失败:"+msg;
			 }

		} catch (HttpException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		} catch (DocumentException e) { 
			e.printStackTrace();
		}
		return null;
	}
}
