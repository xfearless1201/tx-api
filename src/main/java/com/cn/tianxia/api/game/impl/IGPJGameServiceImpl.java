package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName IGPJServiceImpl
 * @Description IG新彩
 * @author Hardy
 * @Date 2019年2月9日 下午4:31:46
 * @version 1.0.0
 */
public class IGPJGameServiceImpl{
	private static Logger logger = LoggerFactory.getLogger(IGPJGameServiceImpl.class);

	DESEncrypt d = new DESEncrypt("");
	private static String apiurl;// 命令(3) (4) (5) (6)对接地址为
	private static String hashcode;
	private static String line;
	private static String lotto_url; // 香港彩 命令(1) (2) 对接地址为
	private static String lottery_url; // 时时彩 命令(1) (2) 对接地址为
	String lotteryTray = null;

	private static String currency;// 游戏货币类型

	private static String mobileVersion; // 彩票版本
	private static final String ERROR= "error";

	public IGPJGameServiceImpl(Map<String, String> pmap,String cagent) {
		PlatFromConfig pf = new PlatFromConfig();
		pf.InitData(pmap, "IGPJ");
		JSONObject jsonObject = new JSONObject().fromObject(pf.getPlatform_config());
		JSONObject jo = null;
		if(jsonObject.containsKey(cagent)){
			jo = JSONObject.fromObject(jsonObject.getString(cagent));
		}else if(jsonObject.containsKey("ALL")){
			jo = JSONObject.fromObject(jsonObject.getString("ALL"));
		}
		apiurl = jo.getString("apiurl");
		hashcode = jo.getString("hashcode");
		lotto_url = jo.getString("lotto_url");
		lottery_url = jo.getString("lottery_url");
		currency = jo.getString("currency");
		try {
			line = jo.getString("line");
		} catch (Exception e) {
			line = "0";
		}
		try {
			// 获取盘口
			lotteryTray = jo.getString("lotteryTray");
		} catch (Exception e) {

		}
		
		if(jo.containsKey("mobileVersion")){
			mobileVersion = jo.getString("mobileVersion");
		}else{
			mobileVersion="new";
		}
	}

	public String LoginGame(String username, String password, String gameType, String gameid, String type,
			String handicap) {
		logger.info("用户【"+ username +"】调用IGPJ游戏登录业务开始");
		password = d.getMd5(password);

		// TODO ig埔京测试帐号的游戏币种类型:TEST
		String data = "{\"hashCode\":\"" + hashcode + "\",\"command\":\"LOGIN\",\"params\":{\"username\":\"" + username
				+ "\",\"password\":\"" + password + "\",";
		if ("LOTTERY".equals(gameType)) {
			data += "\"currency\":\"" + currency + "\",\"language\":\"CN\",\"gameType\":\"" + gameType
					+ "\",\"lotteryTray\":\"" + handicap + "\",\"lotteryPage\":\"" + gameid + "\",\"userCode\":\""
					+ username + "\",\"lotteryType\":\"" + type + "\",\"line\":" + line + ",\"mobileVersion\":\""
					+ mobileVersion + "\"}}";
		} else {
			data += "\"currency\":\"" + currency + "\",\"language\":\"CN\",\"gameType\":\"" + gameType
					+ "\",\"lottoTray\":\"" + handicap + "\",\"userCode\":\"" + username + "\",\"lottoType\":\"" + type
					+ "\",\"line\":" + line + ",\"mobileVersion\":\"" + mobileVersion + "\"}}";
		}
		logger.info("用户【"+ username +"】调用IGPJ游戏登录业务,请求参数:{}",data);
		// 香港彩票
		if ("LOTTO".equals(gameType)) {
			apiurl = lotto_url;
		} else if ("LOTTERY".equals(gameType)) {// 时时彩
			apiurl = lottery_url;
		}
		logger.info("用户【"+ username +"】调用IGPJ游戏登录业务,请求地址:{}",apiurl);
		String msg = sendPost(apiurl, data);
		if (StringUtils.isBlank(msg)) {
			logger.error("用户【"+ username +"】调用IGPJ游戏登录业务,发起HTTP请求响应结果为空");
			return null;
		}
		logger.info("用户【"+ username +"】调用IGPJ游戏登录业务,发起HTTP请求响应结果:{}",msg);
		JSONObject json = JSONObject.fromObject(msg);
		if (!"0".equals(json.getString("errorCode"))) {
			FileLog f = new FileLog();
			Map<String, String> map = new HashMap<>();
			map.put("apiurl", apiurl);
			map.put("data", data);
			map.put("msg", msg);
			map.put("Function", "LoginGame");
			f.setLog("IGPJ", map);
		}
		return msg;
	}

	public String getBalance(String username, String password) {
		logger.info("用户【"+ username +"】调用IGPJ游戏查询余额业务开始");
		password = d.getMd5(password);
		// 登录创建账号
		String data = "{\"hashCode\":\"" + hashcode + "\",\"command\":\"GET_BALANCE\",\"params\":";
		data += "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}}";
		logger.info("用户【"+ username +"】调用IGPJ游戏查询余额,请求地址:{},请求参数:{}",apiurl,data);
		String msg = sendPost(apiurl, data);
		if (StringUtils.isBlank(msg)) {
			logger.error("用户【"+ username +"】调用IG游戏查询余额,发起HTTP请求响应结果为空");
			return "error";
		}
		logger.info("用户【"+ username +"】调用IG游戏查询余额,发起HTTP请求响应结果:{}",msg);
		JSONObject json = JSONObject.fromObject(msg);
		if (!"0".equals(json.getString("errorCode"))) {
			FileLog f = new FileLog();
			Map<String, String> map = new HashMap<>();
			map.put("apiurl", apiurl);
			map.put("data", data);
			map.put("msg", msg);
			map.put("Function", "getBalance");
			f.setLog("IGPJ", map);
		}
		return msg;
	}

	public String DEPOSIT(String username, String password, String billno, String amount) {
		logger.info("用户【"+ username +"】调用IGPJ游戏上分业务开始,订单号:{}",billno);
		try {
			password = d.getMd5(password);
			// 登录创建账号
			String data = "{\"hashCode\":\"" + hashcode + "\",\"command\":\"DEPOSIT\",\"params\":{\"username\":\""
					+ username + "\",\"password\":\"" + password + "\",";
			data += "\"ref\":\"" + billno + "\",\"desc\":\"\",\"amount\":\"" + amount + "\"}}";
			logger.info("IGPJ转账转出,post请求url"+apiurl+"参数"+data);
			String msg = sendPost(apiurl, data);
			if(msg.equals(ERROR)) return  ERROR;
			JSONObject json = JSONObject.fromObject(msg);
			if (StringUtils.isBlank(msg)) {
				logger.error("用户【"+ username +"】调用IGPJ游戏上分,订单号【"+billno+"】发起HTTP请求响应结果为空");
				return "process";
			}
			logger.info("用户【"+ username +"】调用IGPJ游戏上分,订单号【"+billno+"】发起HTTP请求响应结果:{}",msg);

			if ("0".equals(json.getString("errorCode"))) {
				return "success";
			}
			return "process";
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "process";
		}

	}

	public String WITHDRAW(String username, String password, String billno, String amount) {
		logger.info("用户【"+ username +"】调用IGPJ游戏下分业务开始,订单号:{}",billno);
		try {
			password = d.getMd5(password);
			// 登录创建账号
			String data = "{\"hashCode\":\"" + hashcode + "\",\"command\":\"WITHDRAW\",\"params\":{\"username\":\""
					+ username + "\",\"password\":\"" + password + "\",";
			data += "\"ref\":\"" + billno + "\",\"desc\":\"\",\"amount\":\"" + amount + "\"}}";
			logger.info("IGPJ转账游戏下分,post请求url"+apiurl+"参数"+data);
			String msg = sendPost(apiurl, data);
			if(msg.equals(ERROR))  return ERROR;
			JSONObject json = JSONObject.fromObject(msg);
			if (StringUtils.isBlank(msg)) {
				logger.error("用户【"+ username +"】调用IGPJ游戏下分,订单号【"+billno+"】发起HTTP请求响应结果为空");
				return "process";
			}
			logger.info("用户【"+ username +"】调用IGPJ游戏下分,订单号【"+billno+"】发起HTTP请求响应结果:{}",msg);
			if (!"0".equals(json.getString("errorCode"))) {
				FileLog f = new FileLog();
				Map<String, String> map = new HashMap<>();
				map.put("apiurl", apiurl);
				map.put("data", data);
				map.put("msg", msg);
				map.put("Function", "WITHDRAW");
				f.setLog("IGPJ", map);
				return "process";
			}
			return "success";
		} catch (Exception e) {
            logger.error(e.getMessage(),e);
			return "process";
		}
	}

	public String CHECK_REF(String billno) {
		logger.info("查询IGPJ游戏转账订单,订单号:{}",billno);
		// 登录创建账号
		String data = "{\"hashCode\":\"" + hashcode + "\",\"command\":\"CHECK_REF\",\"params\":{\"ref\":\"" + billno
				+ "\"}}";
		logger.info("查询IGPJ游戏转账订单,post请求url"+apiurl+"参数"+data);
		try {
			String msg = sendPost(apiurl, data);
			if (StringUtils.isBlank(msg)) {
				logger.error("查询IGPJ游戏转账订单,订单号【"+billno+"】发起HTTP请求响应结果为空");
				return "process";
			}
			logger.info("查询IGPJ游戏转账订单,订单号【"+billno+"】发起HTTP请求响应结果:{}",msg);
			JSONObject json = JSONObject.fromObject(msg);
			String errorcode = json.getString("errorCode");
			if ("0".equals(errorcode) || "6601".equals(errorcode) || "6617".equals(errorcode)) {
				FileLog f = new FileLog();
				Map<String, String> map = new HashMap<>();
				map.put("apiurl", apiurl);
				map.put("data", data);
				map.put("msg", msg);
				map.put("Function", "CHECK_REF");
				f.setLog("IGPJ", map);
			}
			return errorcode;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "process";
		}
	}

	/**
	 * 发送请求到server端
	 * 
	 * @param tagUrl 请求数据地址
	 * @param Data   请求数据
	 * @return null发送失败，否则返回响应内容
	 */
	public static String sendPost(String tagUrl, String Data) {
		logger.info("IGPJ彩票请求URL:" + tagUrl + "  Data:" + Data);
		HttpClient client = new HttpClient();
		// 创建post请求方法
		PostMethod myPost = new PostMethod(tagUrl);
		String responseString = null;
		int statusCode = 0;
		try {
			// 设置请求头部类型
			myPost.setRequestHeader("Content-Type", "application/json");
			myPost.setRequestHeader("charset", "utf-8");
			myPost.setRequestBody(Data);
			// 这里的超时单位是毫秒。这里的http.socket.timeout相当于SO_TIMEOUT
			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
			client.getHttpConnectionManager().getParams().setSoTimeout(10000);
			// 设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
			statusCode = client.executeMethod(myPost);
			if (statusCode == HttpStatus.SC_OK) {
				InputStream inputStream = myPost.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				StringBuffer stringBuffer = new StringBuffer();
				String str = "";
				while ((str = br.readLine()) != null) {
					stringBuffer.append(str);
				}
				responseString = stringBuffer.toString();
				logger.info("[IGPJ] HTTP请求服务器响应报文:{}" + responseString);
			} else {
				logger.info("[IGPJ] HTTP请求响应状态码异常：{}",statusCode);
				if(String.valueOf(statusCode).substring(0,1).equals("2"))  return   null;
				responseString = "error";
			}
		} catch (Exception e) {
		   logger.error("[IGPJ] HTTP请求失败：",e);
		} finally {
			myPost.releaseConnection();
			client.getHttpConnectionManager().closeIdleConnections(0);
		}
		return responseString;
	}


}
