package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName DSServiceImpl
 * @Description DS视讯
 * @author Hardy
 * @Date 2019年2月9日 下午4:27:08
 * @version 1.0.0
 */
public class DSGameServiceImpl{
    
    private static final Logger logger = LoggerFactory.getLogger(DSGameServiceImpl.class);
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
    /*String url = "http://dsapitest.iasia999.com:81/dsapi/app/api.do";
    String dskey="tx3_63bfceea-009d-40b6-aaa6-708dfe8e";*/
    DESEncrypt d=new DESEncrypt("");
    Calendar Cal=Calendar.getInstance();   
    private static  String url;
	private static  String dskey;
    public DSGameServiceImpl(Map<String, String> pmap) {
		PlatFromConfig pf=new PlatFromConfig();
		pf.InitData(pmap, "DS");
		JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
		url=jo.getString("url").toString();
		dskey=jo.getString("dskey").toString();
    }
    
     
	public String LoginGame(String username,String password){ 
    	password=d.getMd5(password);
    	 //登录创建账号
        String data = "{\"hashCode\":\""+dskey+"\",\"command\":\"LOGIN\",\"params\":";
        data += "{\"username\":\""+username+"\",\"password\":\""+password+"\",\"nickname\":\""+username+"\",\"currency\":\"CNY\",\"language\":\"CN\",\"line\":1}}";
        String msg=sendPost(url,data); 
        JSONObject json;
		json = JSONObject.fromObject(msg);
		if (!"null".equals(json.getString("errorMessage")) || json.getString("errorMessage") != "null") {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();
			map.put("apiurl", url);
			map.put("data", data); 
			map.put("msg", msg); 
			map.put("Function", "LoginGame");
			f.setLog("DS", map);
		}
		return msg;   
    }
     
	public String getBalance(String username,String password){ 
    	password=d.getMd5(password);
    	 //登录创建账号
        String data = "{\"hashCode\":\""+dskey+"\",\"command\":\"GET_BALANCE\",\"params\":";
        data += "{\"username\":\""+username+"\",\"password\":\""+password+"\"}}";
        String msg=sendPost(url,data); 
        JSONObject json;
		json = JSONObject.fromObject(msg);
		if (!"null".equals(json.getString("errorMessage")) || json.getString("errorMessage") != "null") {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();
			map.put("apiurl", url);
			map.put("data", data); 
			map.put("msg", msg); 
			map.put("Function", "getBalance");
			f.setLog("DS", map);
		}
		return msg;   
    }
     
	public String DEPOSIT(String username,String password,String billno,String amount){ 
		try{
	    	password=d.getMd5(password);
	    	 //登录创建账号
	    	String data = "{\"hashCode\":\""+dskey+"\",\"command\":\"DEPOSIT\",\"params\":{\"username\":\""+username+"\",\"password\":\""+password+"\",";
	        data += "\"ref\":\""+billno+"\",\"desc\":\"\",\"amount\":\""+amount+"\"}}";
	        String msg=sendPost(url,data); 
	        JSONObject json;
			json = JSONObject.fromObject(msg);
            logger.info("[DS] 发起Http转账请求第三方响应报文：{}",json.toString());
			if(!"0".equals(json.getString("errorCode"))){
				FileLog f=new FileLog(); 
				Map<String,String> map =new HashMap<>();
				map.put("apiurl", url);
				map.put("data", data); 
				map.put("msg", msg); 
				map.put("Function", "DEPOSIT");
				f.setLog("DS", map);
				return "error";
			}
			return "success";   
		}catch(Exception e){
			return "error";
		}
    }
     
	public String WITHDRAW(String username,String password,String billno,String amount){ 
		try{
	    	password=d.getMd5(password);
	    	 //登录创建账号
	    	String data = "{\"hashCode\":\""+dskey+"\",\"command\":\"WITHDRAW\",\"params\":{\"username\":\""+username+"\",\"password\":\""+password+"\",";
	        data += "\"ref\":\""+billno+"\",\"desc\":\"\",\"amount\":\""+amount+"\"}}";
	        String msg=sendPost(url,data); 
	        JSONObject json;
			json = JSONObject.fromObject(msg);
            logger.info("[DS] 发起Http转账请求第三方响应报文：{}",json.toString());
			if(!"0".equals(json.getString("errorCode"))){
				FileLog f=new FileLog(); 
				Map<String,String> map =new HashMap<>();
				map.put("apiurl", url);
				map.put("data", data); 
				map.put("msg", data); 
				map.put("Function", "WITHDRAW");
				f.setLog("DS", map);
				return "error";
			}
			return "success";   
		}catch(Exception e){
			return "error";
		}
    }
	
	public String CHECK_REF(String billno){  
   	 //登录创建账号
   	String data = "{\"hashCode\":\""+dskey+"\",\"command\":\"CHECK_REF\",\"params\":{\"ref\":\""+billno+"\"}}";
       String msg=sendPost(url,data); 
       JSONObject json;
		json = JSONObject.fromObject(msg);
		String errorcode=json.getString("errorCode");  
		Map<String,String> map =new HashMap<>();
		FileLog f=new FileLog(); 
		if("0".equals(errorcode)||"6601".equals(errorcode)||"6617".equals(errorcode)){
			map.put("apiurl", url);
			map.put("data", data); 
			map.put("errorcode", errorcode);
			map.put("msg", msg); 
			map.put("Function", "CHECK_REF");
		}
		else
		{
		    map.put("apiurl", url);
            map.put("data", data); 
            map.put("errorcode", errorcode);
            map.put("msg", msg); 
            map.put("Function", "CHECK_REF");
        }
		f.setLog("DS", map);
		return errorcode;   
   }
    
    /**   
     * 发送xml请求到server端   
     * @param tagUrl xml请求数据地址
     * @param Data 发送的xml数据流
     * @return null发送失败，否则返回响应内容   
     */      
    public static String sendPost(String tagUrl,String Data){         
    	//System.out.println(Data);
        //创建httpclient工具对象     
        HttpClient client = new HttpClient();      
        //创建post请求方法     
        PostMethod myPost = new PostMethod(tagUrl);      
        String responseString = null;      
        try{      
            //设置请求头部类型     
            myPost.setRequestHeader("Content-Type","application/json");    
            myPost.setRequestHeader("charset","utf-8");    
            myPost.setRequestBody(Data);
            //设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式      
            int statusCode = client.executeMethod(myPost);     
            //只有请求成功200了，才做处理  
            if(statusCode == HttpStatus.SC_OK){       
            	InputStream inputStream = myPost.getResponseBodyAsStream();  
            	BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));  
            	StringBuffer stringBuffer = new StringBuffer();  
            	String str= "";  
            	while((str = br.readLine()) != null){  
            	stringBuffer.append(str );  
            	}   
                responseString = stringBuffer.toString();
            }else{
            	FileLog f=new FileLog(); 
    			Map<String,String> map =new HashMap<>(); 
    			map.put("statusCode", statusCode+"");
    			map.put("ResponseBody", myPost.getResponseBodyAsString()); 
    			map.put("tagUrl", tagUrl);
    			map.put("Function", "sendPost");
    			f.setLog("DS", map);
            }      
        }catch (Exception e) {   
            e.printStackTrace();      
        }finally{  
             myPost.releaseConnection();
             client.getHttpConnectionManager().closeIdleConnections(0);
        }   
        return responseString;      
    }
}
