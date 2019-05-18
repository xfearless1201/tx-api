package com.cn.tianxia.api.game.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName CGServiceImpl
 * @Description 卡卡湾88(视讯)接口实现类
 * @author Hardy
 * @Date 2019年2月9日 下午4:25:00
 * @version 1.0.0
 */
public class CGGameServiceImpl{

        private final static Logger logger = LoggerFactory.getLogger(CGGameServiceImpl.class);


		DESEncrypt d=new DESEncrypt("");
		private static  String apiurl;
		private static  String hashcode;
		private static String currency;


		public CGGameServiceImpl(Map<String, String> pmap) {
			PlatFromConfig pf=new PlatFromConfig();
			pf.InitData(pmap, "CG");
			JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
			apiurl=jo.getString("apiurl");
			hashcode=jo.getString("hashcode");
			currency = jo.getString("currency");
		}
		
		public String LoginGame(String username,String password){
			password=d.getMd5(password);
			String data = "{\"hashCode\":\""+hashcode+"\",\"command\":\"LOGIN\",\"params\":{\"username\":\""+username+"\",\"password\":\""+password+"\",";
	        data += "\"nickname\":\""+username+"\",\"currency\":\""+currency+"\",\"language\":\"CN\"}}"; 
	        String msg=sendPost(apiurl, data);
	        if(StringUtils.isBlank(msg) || msg.equals("error")){
	            logger.info("创建或检查用户账号发起HTTP请求无响应结果");
	            JSONObject jsonObject = new JSONObject();
	            jsonObject.put("errorCode", "error");
	            jsonObject.put("errorMsg", "创建或检查用户账号发起HTTP请求无响应结果");
	            return jsonObject.toString();
	        }
	        JSONObject json = JSONObject.fromObject(msg);
			if (!"null".equals(json.getString("errorMessage")) || json.getString("errorMessage") != "null") {
				FileLog f=new FileLog(); 
				Map<String,String> map =new HashMap<>();
				map.put("apiurl", apiurl);
				map.put("data", data); 
				map.put("msg", msg); 
				map.put("Function", "LoginGame");
				f.setLog("CG", map);
			}
			return msg; 
		}
		
		public String getBalance(String username,String password){ 
	    	password=d.getMd5(password);
	    	 //登录创建账号
	        String data = "{\"hashCode\":\""+hashcode+"\",\"command\":\"GET_BALANCE\",\"params\":";
	        data += "{\"username\":\""+username+"\",\"password\":\""+password+"\"}}";
	        String msg=sendPost(apiurl,data); 
	        JSONObject json;
			json = JSONObject.fromObject(msg);
			if (!"null".equals(json.getString("errorMessage")) || json.getString("errorMessage") != "null") {
				FileLog f=new FileLog(); 
				Map<String,String> map =new HashMap<>();
				map.put("apiurl", apiurl);
				map.put("data", data); 
				map.put("msg", msg); 
				map.put("Function", "getBalance");
				f.setLog("CG", map);
			}
			return msg;   
	    }
	    
		public String DEPOSIT(String username,String password,String billno,String amount){ 
			try{
		    	password=d.getMd5(password);
		    	 //登录创建账号
		    	String data = "{\"hashCode\":\""+hashcode+"\",\"command\":\"DEPOSIT\",\"params\":{\"username\":\""+username+"\",\"password\":\""+password+"\",";
		        data += "\"ref\":\""+billno+"\",\"desc\":\"DEPOSIT\",\"amount\":\""+amount+"\"}}";
				logger.info("[CG] 用户【"+username+"】 http请求参数：{}",data);

				String msg=sendPost(apiurl,data);
				/**
				 * return error message
				 */
				if(msg.equals("error")) return "error";
				if(StringUtils.isEmpty(msg)) return  "process";


				JSONObject json = JSONObject.fromObject(msg);

				logger.info("[CG] 发起Http转账请求第三方响应报文：{}",json.toString());
				if(!"0".equals(json.getString("errorCode"))){
					FileLog f=new FileLog(); 
					Map<String,String> map =new HashMap<>();
					map.put("apiurl", apiurl);
					map.put("data", data); 
					map.put("msg", msg); 
					map.put("Function", "DEPOSIT");
					f.setLog("CG", map);
					return "process";
				}
				return "success";   
			}catch(Exception e){
				logger.info("[CG] 用户【"+username+"】游戏上分异常：",e);
				return "process";
			}
	    }
	    
		public String WITHDRAW(String username,String password,String billno,String amount){ 
			try{
		    	password=d.getMd5(password);
		    	 //登录创建账号
		    	String data = "{\"hashCode\":\""+hashcode+"\",\"command\":\"WITHDRAW\",\"params\":{\"username\":\""+username+"\",\"password\":\""+password+"\",";
		        data += "\"ref\":\""+billno+"\",\"desc\":\"withdra "+amount+"\",\"amount\":\""+amount+"\"}}";
		        logger.info("[CG] 用户【"+username+"】 http请求参数：{}",data);
		        String msg=sendPost(apiurl,data);
		        if(msg.equals("error")) return  "error";
		        if(StringUtils.isEmpty(msg))  return "process";

				JSONObject json = JSONObject.fromObject(msg);
				logger.info("[CG] 发起Http转账请求第三方响应报文：{}",json.toString());
                String errorCode = json.getString("errorCode");
				if(!"0".equals(errorCode)){
					//交易订单号存在
					if(errorCode.equals("6617") || errorCode.equals("6614")) return  "error";

					return "process";
				}
				return "success";   
			}catch(Exception e){
				logger.info("[CG] 用户【"+username+"】游戏下分异常：",e);
				return "process";
			}
	    }
		
		public String CHECK_REF(String billno){  
	    	 //登录创建账号
	    	String data = "{\"hashCode\":\""+hashcode+"\",\"command\":\"CHECK_REF\",\"params\":{\"ref\":\""+billno+"\"}}";
	        String msg=sendPost(apiurl,data); 
	        if(StringUtils.isBlank(msg) || msg.equals("error")){
	            logger.info("发起第三方查询订单详情无响应结果");
	            return "6617";
	        }
	        JSONObject json = JSONObject.fromObject(msg);
			String errorcode=json.getString("errorCode");   
			if("0".equals(errorcode)||"6601".equals(errorcode)||"6617".equals(errorcode)){
				FileLog f=new FileLog(); 
				Map<String,String> map =new HashMap<>();
				map.put("apiurl", apiurl);
				map.put("data", data); 
				map.put("msg", msg); 
				map.put("Function", "CHECK_REF");
				f.setLog("CG", map);
			}
			return errorcode;   
	    }
		
		/**   
	     * 发送请求到server端   
	     * @param tagUrl 请求数据地址
	     * @param Data 发送的数据流
	     * @return null发送失败，否则返回响应内容   
	     */      
		public static String sendPost(String tagUrl,String Data){
			logger.info("sendPost(String tagUrl,String Data = {},{} -start",tagUrl,Data);
	        HttpClient client = new HttpClient();
	        //创建post请求方法     
	        PostMethod myPost = new PostMethod(tagUrl);      
	        String responseString = null;      
	        try{

	        	logger.info("CG请求:"+tagUrl+JSONObject.fromObject(Data));
				client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
				client.getHttpConnectionManager().getParams().setSoTimeout(15000);
	            //设置请求头部类型     
	            myPost.setRequestHeader("Content-Type","application/json");    
	            myPost.setRequestHeader("charset","utf-8");    
	            myPost.setRequestBody(Data);

	            //设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式      
	            int statusCode = client.executeMethod(myPost);
	            logger.info("[CG] HTTP请求响应状态码："+ statusCode);

	            if(statusCode == HttpStatus.SC_OK){

	            	InputStream inputStream = myPost.getResponseBodyAsStream();
					responseString = IOUtils.toString(inputStream,Consts.UTF_8);
	                logger.info("CG响应:"+responseString);

	                inputStream.close();
	            }else{

	            	logger.info("[CG] HTTP请求响应状态异常,响应状态码：{}",statusCode);
	            	if(String.valueOf(statusCode).startsWith("2")) return  null;
					responseString = "error";
	            }

	        }catch (Exception e) {
	        	logger.error("[CG] HTTP请求异常：",e);
				if(e.getMessage().equals("Read timed out")) return  null; return "error";
	        }finally{
	             myPost.releaseConnection();
				 client.getHttpConnectionManager().closeIdleConnections(0);
	        }
	        return responseString;      
	    }
		

}
