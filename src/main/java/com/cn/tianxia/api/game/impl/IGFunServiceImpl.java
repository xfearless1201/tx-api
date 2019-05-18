package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * @ClassName IGFunServiceImpl
 * @Description IG试玩
 * @author Hardy
 * @Date 2019年2月9日 下午4:31:07
 * @version 1.0.0
 */
public class IGFunServiceImpl{
	private static final Logger logger = LoggerFactory.getLogger(IGFunServiceImpl.class);
	/*
	 * private static final String
	 * apiurl="http://igyfsw.iasia99.com/igapiyf/app/api.do"; private static
	 * final String hashcode="ttx_2e823371-a192-40db-b8ad-9904edd8";
	 */
	/*
	 * (1)LOGIN命令 (2)CHANGE_PASSWORD命令 (3)GET_BALANCE 命令 (4)DEPOSIT 命令
	 * (5)WITHDRAW 命令 (6)CHECK_REF命令
	 */
	DESEncrypt d = new DESEncrypt("");
	private static String apiurl;// 命令(3) (4) (5) (6)对接地址为
	private static String hashcode;
	private static String line;
	private static String lotto_url; // 香港彩 命令(1) (2) 对接地址为
	private static String lottery_url; // 时时彩 命令(1) (2) 对接地址为

	public IGFunServiceImpl(Map<String, String> pmap) {
		PlatFromConfig pf = new PlatFromConfig();
		pf.InitData(pmap, "IGFUN");
		JSONObject jo = new JSONObject().fromObject(pf.getPlatform_config());
		apiurl = jo.getString("apiurl").toString();
		hashcode = jo.getString("hashcode").toString();
		lotto_url = jo.getString("lotto_url").toString();
		lottery_url = jo.getString("lottery_url").toString();
	}

	public String LoginGame(String username, String password, String gameType, String gameid, String type) {
		password = d.getMd5(password);
		String data = "{\"hashCode\":\"" + hashcode + "\",\"command\":\"LOGIN\",\"params\":{\"username\":\"" + username
				+ "\",\"password\":\"" + password + "\",";
		if ("LOTTERY".equals(gameType)) {
			data += "\"currency\":\"CNY\",\"language\":\"CN\",\"gameType\":\"" + gameType
					+ "\",\"lotteryTray\":\"A\",\"lotteryPage\":\"" + gameid + "\",\"userCode\":\"" + username
					+ "\",\"lotteryType\":\"" + type + "\"}}";
		} else {
			data += "\"currency\":\"CNY\",\"language\":\"CN\",\"gameType\":\"" + gameType
					+ "\",\"lottoTray\":\"A\",\"userCode\":\"" + username + "\",\"lottoType\":\"" + type + "\"}}";
		}
		String tempUrl="";
		// 香港彩票
		if ("LOTTO".equals(gameType)) {
			tempUrl=lottery_url;
		} else if ("LOTTERY".equals(gameType)) {// 时时彩
			tempUrl=lottery_url;
		}
		logger.info("IG试玩创建账号请求链接：{},参数：{}",apiurl,data);
		String msg = sendPost(tempUrl, data);
		logger.info("IG试玩创建账号返回参数：{}",msg);
		JSONObject json;
		json = JSONObject.fromObject(msg);
		if (!"0".equals(json.getString("errorCode"))) {
			FileLog f = new FileLog();
			Map<String, String> map = new HashMap<>();
			map.put("apiurl", tempUrl);
			map.put("data", data);
			map.put("msg", msg);
			map.put("Function", "LoginGame");
			f.setLog("IGFUN", map);
		}
		return msg;
	}

	public String getBalance(String username, String password) {
		password = d.getMd5(password);
		// 登录创建账号
		String data = "{\"hashCode\":\"" + hashcode + "\",\"command\":\"GET_BALANCE\",\"params\":";
		data += "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}}";
		logger.info("IG试玩查询余额请求链接：{},参数：{}",apiurl,data);
		String msg = sendPost(apiurl, data);
		logger.info("IG试玩查询返回结果：{}",msg);
		JSONObject json;
		json = JSONObject.fromObject(msg);
		if (!"0".equals(json.getString("errorCode"))) {
			FileLog f = new FileLog();
			Map<String, String> map = new HashMap<>();
			map.put("apiurl", apiurl);
			map.put("data", data);
			map.put("msg", msg);
			map.put("Function", "getBalance");
			f.setLog("IGFUN", map);
		}
		return msg;
	}

	public String DEPOSIT(String username, String password, String billno, String amount) {
		try {
			password = d.getMd5(password);
			// 登录创建账号
			String data = "{\"hashCode\":\"" + hashcode + "\",\"command\":\"DEPOSIT\",\"params\":{\"username\":\""
					+ username + "\",\"password\":\"" + password + "\",";
			data += "\"ref\":\"" + billno + "\",\"desc\":\"\",\"amount\":\"" + amount + "\"}}";
			logger.info("IG试玩充值请求链接：{},参数：{}",apiurl,data);
			String msg = sendPost(apiurl, data);
			logger.info("IG试玩充值返回参数：{}",msg);
			JSONObject json;
			json = JSONObject.fromObject(msg);
			if (!"0".equals(json.getString("errorCode"))) {
				FileLog f = new FileLog();
				Map<String, String> map = new HashMap<>();
				map.put("apiurl", apiurl);
				map.put("data", data);
				map.put("msg", msg);
				map.put("Function", "DEPOSIT");
				f.setLog("IGFUN", map);
				return "error";
			}
			return "success";
		} catch (Exception e) {
			return "error";
		}

	}

	public String WITHDRAW(String username, String password, String billno, String amount) {
		try {
			password = d.getMd5(password);
			// 登录创建账号
			String data = "{\"hashCode\":\"" + hashcode + "\",\"command\":\"WITHDRAW\",\"params\":{\"username\":\""
					+ username + "\",\"password\":\"" + password + "\",";
			data += "\"ref\":\"" + billno + "\",\"desc\":\"\",\"amount\":\"" + amount + "\"}}";
			String msg = sendPost(apiurl, data);
			JSONObject json;
			json = JSONObject.fromObject(msg);
			if (!"0".equals(json.getString("errorCode"))) {
				FileLog f = new FileLog();
				Map<String, String> map = new HashMap<>();
				map.put("apiurl", apiurl);
				map.put("data", data);
				map.put("msg", msg);
				map.put("Function", "WITHDRAW");
				f.setLog("IGFUN", map);
				return "error";
			}
			return "success";
		} catch (Exception e) {
			return "error";
		}
	}

	public String CHECK_REF(String billno) {

		// 登录创建账号
		String data = "{\"hashCode\":\"" + hashcode + "\",\"command\":\"CHECK_REF\",\"params\":{\"ref\":\"" + billno
				+ "\"}}";
		String msg = sendPost(apiurl, data);
		JSONObject json;
		json = JSONObject.fromObject(msg);
		String errorcode = json.getString("errorCode");
		if ("0".equals(errorcode) || "6601".equals(errorcode) || "6617".equals(errorcode)) {
			FileLog f = new FileLog();
			Map<String, String> map = new HashMap<>();
			map.put("apiurl", apiurl);
			map.put("data", data);
			map.put("msg", msg);
			map.put("Function", "CHECK_REF");
			f.setLog("IGFUN", map);
		}
		return errorcode;
	}

	/**
	 * 发送请求到server端
	 * 
	 * @param url
	 *            请求数据地址
	 * @param 发送的数据流
	 * @return null发送失败，否则返回响应内容
	 */
	public static String sendPost(String tagUrl, String Data) {
		// System.out.println(tagUrl);
		// System.out.println(Data);
		// 创建httpclient工具对象
		HttpClient client = new HttpClient();
		// 创建post请求方法
		PostMethod myPost = new PostMethod(tagUrl);
		String responseString = null;
		try {
			// 设置请求头部类型
			myPost.setRequestHeader("Content-Type", "application/json");
			myPost.setRequestHeader("charset", "utf-8");
			myPost.setRequestBody(Data);
			// 设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
			int statusCode = client.executeMethod(myPost);
			// 只有请求成功200了，才做处理
			if (statusCode == HttpStatus.SC_OK) {
				InputStream inputStream = myPost.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				StringBuffer stringBuffer = new StringBuffer();
				String str = "";
				while ((str = br.readLine()) != null) {
					stringBuffer.append(str);
				}
				responseString = stringBuffer.toString();
			} else {
				FileLog f = new FileLog();
				Map<String, String> map = new HashMap<>();
				map.put("statusCode", statusCode + "");
				map.put("ResponseBody", myPost.getResponseBodyAsString());
				map.put("tagUrl", tagUrl);
				map.put("Function", "sendPost");
				f.setLog("IGFUN", map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			myPost.releaseConnection();
			client.getHttpConnectionManager().closeIdleConnections(0);
		}
		// System.out.println(responseString);
		return responseString;
	}


}
