
package com.cn.tianxia.api.utils.pay;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * 
 * @ClassName XUNCUtils
 * @Description 迅驰支付工具类
 * @author Hardy
 * @Date 2019年1月10日 下午3:00:28
 * @version 1.0.0
 */
public class XUNCUtils {
	
	public static String sign(Map<String, String> map, String key, String charset){
		map.put("key", key);
		
		String signStr = "";
		for(Map.Entry<String, String> entry : map.entrySet()){
			if( signStr.length() >0 ) signStr += "&";
			signStr += entry.getKey() + "=" +entry.getValue();
		}
		map.remove("key");
		
		signStr = signStr.toUpperCase();
		System.out.println("签名字符串["+signStr+"]");
		
		String sign = md5(signStr, charset);
		
		System.out.println("签名sign["+sign+"]");
		
		return sign;
	}
	
	public static String md5(String data, String charset) {
		try {
			return md5(data.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
    }
	
    private static String md5(byte[] b) {
        try {
        	MessageDigest md = MessageDigest.getInstance("MD5");
        	 md.reset();
             md.update(b);
             byte[] hash = md.digest();
             StringBuffer outStrBuf = new StringBuffer(32);
             for (int i = 0; i < hash.length; i++) {
                 int v = hash[i] & 0xFF;
                 if (v < 16) {
                 	outStrBuf.append('0');
                 }
                 outStrBuf.append(Integer.toString(v, 16).toLowerCase());
             }
             return outStrBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new String(b);
        }
    }
    
    public static String send(String reqUrl, Map<String, String> map) {
    	String reqData = "";
    	for(Map.Entry<String, String> entry : map.entrySet()){
    		if( reqData.length() > 0 ) reqData += "&";
			reqData += entry.getKey() + "=" +entry.getValue();
		}
    	
		System.out.println("请求地址："+reqUrl);
		System.out.println("请求数据："+reqData);
		
		String response = null;// 请求返回

		try {
			URL url = new URL(reqUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("GET");// post请求
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("contentType", "application/x-www-form-urlencoded");
			
			// 设置超时时间
			conn.setConnectTimeout(60000);
			conn.setReadTimeout(60000);

			// 数据及字符集
			conn.setRequestProperty("Charset","utf-8");
			byte[] data = reqData.getBytes("utf-8");
			conn.setRequestProperty("Content-Length", String.valueOf(data.length));

			conn.connect();
			OutputStream out = conn.getOutputStream();
			out.write((reqData).getBytes());
			out.flush();
			out.close();

			// 请求返回状态
			int httpStatus = conn.getResponseCode();
			System.out.println("请求返回状态1：" + httpStatus+",请求数据["+reqData+"]");

			if (httpStatus == 200) {
				InputStream in = conn.getInputStream();
				try {
					byte[] respData = new byte[in.available()];
					in.read(respData);
					response = new String(respData, "utf-8");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			System.out.println("请求返回状态2：" + httpStatus+",返回数据["+response+"]");
			
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}
    
}


