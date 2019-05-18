package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

/**
 * VG棋牌游戏
 * 
 * @author zw
 *
 */
public class VGGameServiceImpl{

	private String api_url;
	private String md5_key;
	private String channel;
	private String agent;
	private String trygame_url;

	private final static Logger logger = LoggerFactory.getLogger(VGGameServiceImpl.class);
	static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public VGGameServiceImpl(Map<String, String> pmap) {
		PlatFromConfig pf = new PlatFromConfig();
		pf.InitData(pmap, "VG");
		JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());
		api_url = jo.getString("api_url").toString();
		md5_key = jo.getString("md5_key").toString();
		channel = jo.getString("channel").toString();
		agent = jo.getString("agent").toString();
		trygame_url = jo.getString("trygame_url").toString();
	}

	public static void main(String[] args) {
		Map<String, String> init = new HashMap<>();
		init.put("api_url", "http://ew68.cn/webapi/interface.aspx");
		init.put("md5_key", "owuTh292*#e34Yh0");
		init.put("channel", "TX");
		init.put("agent", "");
		init.put("trygame_url", "http://ew68.cn/webapi/trygame.aspx");

		System.out.println("JSON配置:" + JSONObject.fromObject(init));
		Map<String, String> map1 = new HashMap<>();
		map1.put("VG", JSONObject.fromObject(init).toString());
		VGGameServiceImpl vg = new VGGameServiceImpl(map1);
		String username = "lix001";
		String serial = "TX" + System.currentTimeMillis();
		int credit = 1000000;
		// System.out.println(vg.createUser(username));
//		 System.out.println(vg.loginWithChannel(username, "1000", "1", ""));
		// System.out.println(vg.deposit(username, credit, serial));
		// System.out.println(vg.withdraw(username, credit, serial));
		// System.out.println(vg.balance(username));
		// System.out.println(vg.trygame(username, "1000", "1"));
//		System.out.println(vg.gameRecord("0,1,2,3,4,5,6,7,8,9"));
	}

	/**
	 * 创建用户
	 * 
	 * @param userName
	 * @return
	 */
	public String createUser(String userName) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		String username = userName.toUpperCase();// TODO
		String action = "create";
		map.put("username", username);
		map.put("action", action);
		map.put("channel", channel);
		map.put("agent", agent);
		String md5Str = username + action + channel + agent + md5_key;
		String verifyCode = MD5(md5Str, "UTF-8");
		map.put("verifyCode", verifyCode);
		String res = sendGet(api_url, map, action);
		if ("error".equals(res)) {
			return "error";
		}
		return "success";
	}

	/**
	 * 登录游戏
	 * 
	 * @param userName
	 *            用户名
	 * @param gameType
	 *            游戏类型 1=斗地主 2=麻将 3=牛牛 4=百人牛牛 1000=游戏大厅
	 * @param gameversion
	 *            游戏版本 1=PC 2=移动端
	 * @param orientation
	 *            游戏大厅版式 1=横版 2=竖版
	 * @return
	 */
	public String loginWithChannel(String userName, String gameType, String gameversion, String orientation) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		String username = userName.toUpperCase();// TODO
		String action = "loginWithChannel";
		map.put("username", username);
		map.put("action", action);
		map.put("channel", channel);
		map.put("gameType", gameType);
		map.put("gameversion", gameversion);// pc 移动端
		map.put("orientation", orientation);// 横屏 竖屏
		String md5Str = username + action + channel + gameType + gameversion + md5_key;
		String verifyCode = MD5(md5Str, "UTF-8");
		map.put("verifyCode", verifyCode);

		String res = sendGet(api_url, map, action);
		if ("error".equals(res)) {
			return "error";
		}

		JSONObject json = JSONObject.fromObject(res);
		String url = URLDecoder.decode(json.getString("result"));
		return url;
	}

	/**
	 * 转入游戏
	 * 
	 * @param userName
	 *            用户名
	 * @param credit
	 *            金额
	 * @param serial
	 *            订单号
	 * @return
	 */
	public String deposit(String userName, int credit, String serial) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();

		String username = userName.toUpperCase();// TODO
		String action = "deposit";
		map.put("username", username);
		map.put("action", action);
		map.put("channel", channel);

		DecimalFormat dec = new DecimalFormat("###########");
		String amount = dec.format(credit);
		map.put("amount", amount);
		map.put("serial", serial);

		String md5Str = username + action + channel + amount + serial + md5_key;
		String verifyCode = MD5(md5Str, "UTF-8");
		map.put("verifyCode", verifyCode);

		logger.info("VG 用户【"+ username +"】 ,订单编号【"+ serial +"】 发起第三方转账（上分）请求参数：{}",map.toString());
		String res = sendGet(api_url, map, action);
		logger.info("VG 用户【"+ username +"】 ,订单编号【"+ serial +"】 发起第三方转账（上分）返回结果：{}",res);
		if ("error".equals(res)) {
			return "error";
		}

		return "success";
	}

	/**
	 * 转出游戏
	 * 
	 * @param userName
	 *            用户名
	 * @param credit
	 *            金额
	 * @param serial
	 *            订单号
	 * @return
	 */
	public String withdraw(String userName, int credit, String serial) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();

		String username = userName.toUpperCase();// TODO
		String action = "withdraw";
		map.put("username", username);
		map.put("action", action);
		map.put("channel", channel);

		DecimalFormat dec = new DecimalFormat("###########");
		String amount = dec.format(credit);
		map.put("amount", amount);
		map.put("serial", serial);

		String md5Str = username + action + channel + amount + serial + md5_key;
		String verifyCode = MD5(md5Str, "UTF-8");
		map.put("verifyCode", verifyCode);

		logger.info("VG 用户【"+ username +"】 ,订单编号【"+ serial +"】 发起第三方转账（下分）请求参数：{}",map.toString());
		String res = sendGet(api_url, map, action);
		logger.info("VG 用户【"+ username +"】 ,订单编号【"+ serial +"】 发起第三方转账（下分）返回结果：{}",res);
		if ("error".equals(res)) {
			return "error";
		}

		return "success";
	}

	/**
	 * 试玩接口
	 * 
	 * @param userName
	 *            用户名
	 * @param gameType
	 *            游戏类型1=斗地主 2=麻将 3=牛牛4 =百人牛牛 1000=游戏大厅
	 * @param gameversion
	 *            游戏版本 1=flash 2=h5
	 * @return
	 */
	public String trygame(String userName, String gameType, String gameversion) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		// TODO此接口尚未对接完成待游戏商确定
		String username = userName.toUpperCase();
		String action = "trygame";
		// map.put("username", username);
		// map.put("action", action);
		map.put("channel", channel);
		map.put("gameType", gameType);
		map.put("gameversion", gameversion);// pc 移动端
		// map.put("orientation", orientation);// 横屏 竖屏
		String md5Str = channel + gameType + gameversion + md5_key;
		String verifyCode = MD5(md5Str, "UTF-8");
		map.put("verifyCode", verifyCode);

		String res = sendGet(trygame_url, map, action);
		if ("error".equals(res)) {
			return "error";
		}

		JSONObject json = JSONObject.fromObject(res);
		String url = URLDecoder.decode(json.getString("result"));
		return url;
	}

	/**
	 * 查询用户余额
	 * 
	 * @param userName
	 *            用户名
	 * @return
	 */
	public String balance(String userName) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();

		String username = userName.toUpperCase();// TODO
		String action = "balance";
		map.put("username", username);
		map.put("action", action);
		map.put("channel", channel);

		String md5Str = username + action + channel + md5_key;
		String verifyCode = MD5(md5Str, "UTF-8");
		map.put("verifyCode", verifyCode);
		logger.info("VG 用户【"+ username +"】 , 发起查询余额请求参数：{}",map.toString());
		String res = sendGet(api_url, map, action);
		logger.info("VG 用户【"+ username +"】 , 发起查询余额返回结果：{}",res);
		if ("error".equals(res)) {
			return "error";
		}

		JSONObject json = JSONObject.fromObject(res);
		String url = URLDecoder.decode(json.getString("result"));
		return url;

	}

	/**
	 * 获取游戏记录
	 * 
	 * @param ids
	 *            处理成功的数据行的比赛编号id的集合(如id=1,2,3,4,5,6,7,8,9)要求使用英文逗号隔开
	 * @return
	 */
	public String gameRecord(String ids) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		String action = "GameData";
		map.put("ids", ids);
		map.put("channel", channel);
		String md5Str = ids + channel + md5_key;
		String verifyCode = MD5(md5Str, "UTF-8");
		map.put("verifyCode", verifyCode.toUpperCase());

		String res = sendGet("http://ew68.cn/webapi/gamerecord.aspx", map, action);
		if ("error".equals(res)) {
			return "error";
		}

		return res;
	}

	/**
	 * 请求响应处理方法
	 * 
	 * @param Url
	 * @param Parms
	 * @param action
	 * @return
	 */
	public String sendGet(String Url, Map<String, String> Parms, String action) {
		String param = "";
		StringBuffer sr = new StringBuffer("");
		Set<String> set = Parms.keySet();
		for (String str : set) {
			sr.append(str + "=");
			sr.append(Parms.get(str) + "&");
		}

		param = sr.toString().substring(0, sr.length() - 1);
		String urlParms = Url + "?" + param;
		logger.info("【VG后端请求】：" + urlParms);
		// String result = httpGet(urlParms, action);
		String result = "";
		try {
			result = httpGet(urlParms, action);
			logger.info("【VG响应】：" + result);
			if (StringUtils.isEmpty(result)) {
				return "error";
			} else {
				result = result.replace(":", "%3A").replace("&", "%26");
				System.out.println(result);
				XMLSerializer xmlSerializer = new XMLSerializer();
				String xmlString = xmlSerializer.read(result).toString();
				JSONObject json = JSONObject.fromObject(xmlString);
				if (json.containsKey("errcode") && "0".equals(json.getString("errcode"))) {
					if ("loginWithChannel".equals(action) || "balance".equals(action) || "trygame".equals(action)) {
						return json.toString();
					}
					return "success";
				} else {
					setFile(action, urlParms, result);
					return "error";
				}
			}
		} catch (Exception e) {
			setFile(action, urlParms, result);
			return "error";
		}
	}

	/**
	 * 保存文件记录
	 * 
	 * @param action
	 * @param urlParms
	 * @param result
	 */
	private void setFile(String action, String urlParms, String result) {
		FileLog f = new FileLog();
		Map<String, String> pam = new HashMap<>();
		pam.put("method", action);
		pam.put("requesParams", urlParms);
		pam.put("responseParams", result);
		f.setLog("VG", pam);
	}

	/**
	 * 发送xml请求到server端
	 * 
	 * @param tagUrl
	 *            请求数据地址
	 * @return null发送失败，否则返回响应内容
	 */
	public String httpGet(String tagUrl, String action) {
		// 创建httpclient工具对象
		HttpClient client = new HttpClient();
		// 创建get请求方法
		GetMethod myGet = new GetMethod(tagUrl);
		String responseString = null;
		try {
			// 设置请求头部类型
			myGet.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			myGet.setRequestHeader("charset", "utf-8");
			// 设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
			int statusCode = client.executeMethod(myGet);
			// 只有请求成功200了，才做处理
			if (statusCode == HttpStatus.SC_OK) {
				InputStream inputStream = myGet.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				StringBuffer stringBuffer = new StringBuffer();
				String str = "";
				while ((str = br.readLine()) != null) {
					stringBuffer.append(str);
				}
				responseString = stringBuffer.toString();
			} else {
				setFile(action, tagUrl, responseString);
			}
		} catch (Exception e) {
			setFile(action, tagUrl, responseString);
			e.printStackTrace();
		} finally {
			myGet.releaseConnection();
			client.getHttpConnectionManager().closeIdleConnections(0);
		}
		return responseString;
	}

	/**
	 * MD5加密
	 * 
	 * @param s
	 * @param encoding
	 * @return
	 */
	public final static String MD5(String s, String encoding) {
		try {
			byte[] btInput = s.getBytes(encoding);
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
				str[k++] = HEX_DIGITS[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
