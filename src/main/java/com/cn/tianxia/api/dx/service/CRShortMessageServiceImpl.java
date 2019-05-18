package com.cn.tianxia.api.dx.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject; 

public class CRShortMessageServiceImpl {
	private final  Logger logger = LoggerFactory.getLogger(CRShortMessageServiceImpl.class);
	//发送短信账号 "1876643226"
	public  String name ;
	// 发送短信加密过后密码 "9FA6EB4D075A98F84F896CF0D451"
	public  String pwd ;
	//  "pt"
	public  String type ;
	//短信接口地址 "http://web.cr6868.com/asmx/smsservice.aspx?"
	public  String url; 
	public CRShortMessageServiceImpl(Map<String, String> pmap) {
		JSONObject jo =JSONObject.parseObject(JSON.toJSONString(pmap));
		if (null != pmap) {
			name = jo.get("name").toString();
			pwd = jo.get("pwd").toString();
			type = jo.get("type").toString();
			url = jo.get("url").toString(); 
		}
	}
	
	/**
	 * 发送短信
	 * 
	 * @param name
	 *            用户名
	 * @param pwd
	 *            密码
	 * @param mobileString
	 *            电话号码字符串，中间用英文逗号间隔
	 * @param contextString
	 *            内容字符串
	 * @param sign
	 *            签名
	 * @param stime
	 *            追加发送时间，可为空，为空为及时发送
	 * @param extno
	 *            扩展码，必须为数字 可为空
	 * @return
	 * @throws Exception
	 */ 
	public  String doPost( String mobileString, String contextString,String stime,String sign) throws Exception {
		
		// 扩展码，必须为数字 可为空
		StringBuffer extno=new StringBuffer();
		StringBuffer param = new StringBuffer();
		param.append("name=" + name);
		param.append("&pwd=" + pwd);
		param.append("&mobile=").append(mobileString);
		param.append("&content=").append(URLEncoder.encode(contextString.toString(), "UTF-8"));
		param.append("&stime=" + stime);
		param.append("&sign=").append(URLEncoder.encode(sign, "UTF-8"));
		param.append("&type="+type);
		param.append("&extno=").append(extno);

		URL localURL = new URL(url);
		HttpURLConnection connection = (HttpURLConnection)localURL.openConnection(); 

		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Accept-Charset", "utf-8");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", String.valueOf(param.length()));

		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		String resultBuffer = "";

		try {
			outputStream = connection.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream);

			outputStreamWriter.write(param.toString());
			outputStreamWriter.flush();

			if (connection.getResponseCode() >= 300) {
				throw new Exception(
						"HTTP Request is not success, Response code is " + connection.getResponseCode());
			}

			inputStream = connection.getInputStream();
			resultBuffer = convertStreamToString(inputStream);

		} finally {

			if (outputStreamWriter != null) {
				outputStreamWriter.close();
			}

			if (outputStream != null) {
				outputStream.close();
			}

			if (reader != null) {
				reader.close();
			}

			if (inputStreamReader != null) {
				inputStreamReader.close();
			}

			if (inputStream != null) {
				inputStream.close();
			}

		}

		return resultBuffer;
	}

	/**
	 * 转换返回值类型为UTF-8格式.
	 * 
	 * @param is
	 * @return
	 */
	public  String convertStreamToString(InputStream is) {
		StringBuilder sb1 = new StringBuilder();
		byte[] bytes = new byte[4096];
		int size = 0;

		try {
			while ((size = is.read(bytes)) > 0) {
				String str = new String(bytes, 0, size, "UTF-8");
				sb1.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb1.toString();
	}
	public String SendRegOut(String mobileString,String contextString,String stime,String sign){ 
		String context="【"+sign+"】尊敬的用户:您已注册完成,帐号:"+mobileString+",密码:"+contextString+",请登录修改密码"; 
        try {
			String returnStr = doPost(mobileString, context,stime,sign);
			String [] str = returnStr.split(",");
			if("0".equals(str[0])){
				logger.info("发送短信成功!");
				return "success";
			}else{
				logger.info("发送短信失败原因:"+str[1]);
				return "fail";
			}
		} catch (Exception e) { 
			e.printStackTrace();           
		}
		return "";
	}
	
	public String SendOut(String mobileString,String contextString,String stime,String sign){ 
		String context = "您的验证码是:"+contextString;
        try {
			String returnStr = doPost(mobileString, context,stime,sign);
			String [] str = returnStr.split(",");
			if("0".equals(str[0])){
				logger.info("发送短信成功!");
				return "success";
			}else{
				logger.info("发送短信失败原因:"+str[1]);
				return "fail";
			}
		} catch (Exception e) { 
			e.printStackTrace();           
		}
		return "";
	}
}
