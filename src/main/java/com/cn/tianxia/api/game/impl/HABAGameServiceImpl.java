package com.cn.tianxia.api.game.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;
import com.cn.tianxia.api.ws.DepositPlayerMoneyRequest;
import com.cn.tianxia.api.ws.GameClientDbDTO;
import com.cn.tianxia.api.ws.GameRequest;
import com.cn.tianxia.api.ws.GameResponse;
import com.cn.tianxia.api.ws.GameTranslationDTO;
import com.cn.tianxia.api.ws.HostedSoap;
import com.cn.tianxia.api.ws.HostedSoapProxy;
import com.cn.tianxia.api.ws.LoginOrCreatePlayerRequest;
import com.cn.tianxia.api.ws.LoginUserResponse;
import com.cn.tianxia.api.ws.LogoutPlayerRequest;
import com.cn.tianxia.api.ws.LogoutPlayerResponse;
import com.cn.tianxia.api.ws.MoneyResponse;
import com.cn.tianxia.api.ws.PlayerCompletedGamesDTO;
import com.cn.tianxia.api.ws.QueryPlayerRequest;
import com.cn.tianxia.api.ws.QueryPlayerResponse;
import com.cn.tianxia.api.ws.ReportRequest;
import com.cn.tianxia.api.ws.WithdrawPlayerMoneyRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName HabaGameServiceImpl
 * @Description HABA电子
 * @author Hardy
 * @Date 2019年2月9日 下午4:29:08
 * @version 1.0.0
 */
public class HABAGameServiceImpl{
	
	private static Logger logger = LoggerFactory.getLogger(HABAGameServiceImpl.class);
	
	/*private static final String API_KEY = "D024BEDB-CEC6-4479-A823-0CB2153C8E4E"; 
	private static final String BRAND_ID = "92bc6a4f-16f4-e611-80d9-000d3a802d1d"; 
	private static final String LOCALE = "zh-CN"; 
	private static final String CURRENCY = "CNY";
	private static String HostedSoapProxy="https://ws-test.insvr.com/hosted.asmx?WSDL";*/
	
	private static String API_KEY ; 
	private static String BRAND_ID ; 
	private static String LOCALE ; 
	private static String CURRENCY ; 
	private static String HostedSoapProxy;
	//图片地址
	//http://app-a.insvr.com/img.ashx?bgid=1f138ec5-372e-411d-9dd9-fcd875e6f86c&w=120&t=r
	/**
	 * 初始化
	 */
	private static HostedSoap soap = null; 
	public HABAGameServiceImpl(Map<String, String> pmap) {
		PlatFromConfig pf=new PlatFromConfig();
		pf.InitData(pmap, "HABA");
		JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
		API_KEY=jo.getString("API_KEY").toString();
		BRAND_ID=jo.getString("BRAND_ID").toString();
		LOCALE=jo.getString("LOCALE").toString();
		CURRENCY=jo.getString("CURRENCY").toString();
		HostedSoapProxy=jo.getString("HostedSoapProxy").toString();
		
		try { 
			if(soap == null){
				soap = new HostedSoapProxy(HostedSoapProxy);
			} 
		} catch (Exception e) {
			logger.error("Habanero初始化SOAP：", e);
		}
	}
	
	
	/**
	 * 获取游戏列表
	 * @param address IP地址
	 * @return
	 */
	public GameClientDbDTO[] getGameList(String address){
		GameRequest gameRequest = new GameRequest();
		try {
			gameRequest.setAPIKey(API_KEY);
			gameRequest.setBrandId(BRAND_ID);
			gameRequest.setLocale(LOCALE);
			gameRequest.setPlayerHostAddress(address);
			GameResponse gameResponse= soap.getGames(gameRequest);
			GameClientDbDTO[] g = gameResponse.getGames();
			return g;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Habanero获取游戏列表异常：", e);
		}
		return null;
	}
	
	/**
	 * 登录并且创建帐号
	 * @param params username:用户帐号，password：用户密码
	 * @return playerCreated true 标识创建帐号成功 false，标识创建帐号失败或已存在
	 */
	public LoginUserResponse loginOrCreatePlayer(String username,String password,Map<String, Object> params){
		try {
			LoginOrCreatePlayerRequest req = new LoginOrCreatePlayerRequest();
			req.setBrandId(BRAND_ID);
			req.setAPIKey(API_KEY);
			req.setCurrencyCode(CURRENCY); 
			req.setUsername(username);
			req.setPassword(password);
			return soap.loginOrCreatePlayer(req);
		} catch (Exception e) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();  
			map.put("username", username); 
			map.put("msg", "Habanero登录并且创建帐号失败:"+e);
			map.put("Function", "loginOrCreatePlayer");
			f.setLog("HABA", map);
			e.printStackTrace();
			logger.error("Habanero登录并且创建帐号失败", e);
		}
		return null;
	}
	
	/**
	 * 存款(转入)
	 * 当帐号不存在的时候将自动创建帐号
	 * @param params
	 * @return
	 */
	public MoneyResponse depositPlayerMoney(String username,String password,Map<String, Object> params){
		try {
			DepositPlayerMoneyRequest req = new DepositPlayerMoneyRequest();
			req.setBrandId(BRAND_ID);
			req.setAPIKey(API_KEY);
			req.setLocale(LOCALE);
			req.setCurrencyCode(CURRENCY); 
			req.setUsername(username);
			req.setPassword(password);
			req.setAmount(new BigDecimal(params.get("amount").toString()));
			req.setRequestId(params.get("requestId").toString());//随机码
			logger.info("habanero游戏转入：username："+username+"->password:"+req.getPassword()+ "->amount："+req.getAmount()+ "->requestId："+req.getRequestId());
			return soap.depositPlayerMoney(req);
		} catch (Exception e) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();  
			map.put("username", username); 
			map.put("billno",params.get("requestId").toString());
			map.put("Amount", params.get("amount").toString());
			map.put("msg", "Habanero存款接口调用失败:"+e);
			map.put("Function", "depositPlayerMoney");
			f.setLog("HABA", map);
			logger.error("Habanero存款接口调用失败", e);
		}
		return null;
	}
	
	/**
	 * 提款(转出)
	 * @param params{amount}为负数
	 * @return
	 */
	public MoneyResponse withdrawPlayerMoney(String username,String password,Map<String, Object> params){
		try {
			WithdrawPlayerMoneyRequest req = new WithdrawPlayerMoneyRequest();
			req.setBrandId(BRAND_ID);
			req.setAPIKey(API_KEY);
			req.setLocale(LOCALE);
			req.setCurrencyCode(CURRENCY); 
			req.setUsername(username);
			req.setPassword(password);
			req.setAmount(new BigDecimal(params.get("amount").toString()));
			req.setRequestId(params.get("requestId").toString());//随机码
			//req.setWithdrawAll(false);
			logger.info("habanero游戏转出：username："+username+"->password:"+req.getPassword()+ "->amount："+req.getAmount()+ "->requestId："+req.getRequestId());
			return soap.withdrawPlayerMoney(req);
		} catch (Exception e) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();  
			map.put("username", username); 
			map.put("billno",params.get("requestId").toString());
			map.put("Amount", params.get("amount").toString());
			map.put("msg", "Habanero提款接口调用失败:"+e);
			map.put("Function", "withdrawPlayerMoney");
			f.setLog("HABA", map);
			logger.error("Habanero提款接口调用失败", e);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 注销玩家
	 * @param params
	 * @return
	 */
	public LogoutPlayerResponse logoutPlayer(String username,String password,Map<String, Object> params){
		try {
			LogoutPlayerRequest req = new LogoutPlayerRequest();
			req.setBrandId(BRAND_ID);
			req.setAPIKey(API_KEY); 
			req.setUsername(username);
			req.setPassword(password);
			return soap.logoutPlayer(req);
		} catch (Exception e) {
			logger.error("Habanero注销玩家接口调用失败", e);
		}
		return null;
	}
	
	/**
	 * 查询玩家信息：余额信息等等
	 * @param params
	 * @return
	 */
	public QueryPlayerResponse queryPlayer(String username,String password,Map<String, Object> params){
		try {
			QueryPlayerRequest req = new QueryPlayerRequest();
			req.setBrandId(BRAND_ID);
			req.setAPIKey(API_KEY); 
			req.setUsername(username);
			req.setPassword(password);
			logger.info("habanero获取用户信息：username："+username+"->password:"+req.getPassword());
			return soap.queryPlayer(req);
		} catch (Exception e) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();  
			map.put("username", username);  
			map.put("msg", "Habanero查询玩家信息接口调用失败:"+e);
			map.put("Function", "QueryPlayerResponse");
			f.setLog("HABA", map);
			logger.error("Habanero查询玩家信息接口调用失败", e);
		}
		return null;
	}
	
	/**
	 * 获取已完成的游戏结果
	 * @param params
	 * @return
	 */
	public PlayerCompletedGamesDTO[] getBrandCompletedGameResults(Map<String, Object> params){
		try {
			ReportRequest req = new ReportRequest();
			req.setBrandId(BRAND_ID);
			req.setAPIKey(API_KEY);
			//日期格式yyyyMMddHHmmss
			req.setDtStartUTC(params.get("startdate").toString());
			req.setDtEndUTC(params.get("enddate").toString());
			return soap.getBrandCompletedGameResults(req);
		} catch (Exception e) {
			logger.error("Habanero获取已完成的游戏结果失败：", e);
		}
		return null;
	}
	
	public static void main(String[] args) {
		Map<String, Object> params = new HashMap<String, Object>();
		String username="txtianxia021";
		String password="a1234567";
		//params.put("username", "tru12345");
		//params.put("password", "6d73a922602dc26f92326e455eff523fb8171423a43219a2");
		//params.put("amount", -100);
		//params.put("requestId", "TX123456789004");
		//params.put("startdate", "20170301120000");
		//params.put("enddate", "20170302120000");
		HABAGameServiceImpl h = new HABAGameServiceImpl(null);
		//LoginUserResponse l=h.loginOrCreatePlayer(username, password, params);
		
		//System.out.println(l.getMessage());
		//PlayerCompletedGamesDTO[] p=h.getBrandCompletedGameResults(params);
		////System.out.println(p.toString());
		//MoneyResponse mr=h.depositPlayerMoney(username, password, params);
		//MoneyResponse mr=h.withdrawPlayerMoney(username, password, params);
		////System.out.println(mr.isSuccess());
		////System.out.println(mr.getAmount());
		////System.out.println(mr.getRealBalance());
		//查询玩家信息
		/*QueryPlayerResponse qp = h.queryPlayer(username,password,params);
		//System.out.println("found:"+qp.isFound());
		//System.out.println("playerId:"+qp.getPlayerId());
		//System.out.println("brandId:"+qp.getBrandId());
		//System.out.println("brandName:"+qp.getBrandName());
		//System.out.println("realBalance:"+qp.getRealBalance());
		//System.out.println("currencyCode:"+qp.getCurrencyCode());
		//System.out.println("token:"+qp.getToken());
		//System.out.println("hasBonus:"+qp.isHasBonus());
		//System.out.println("bonusBalance:"+qp.getBonusBalance());
		//System.out.println("bonusSpins:"+qp.getBonusSpins());
		//System.out.println("bonusGameId:"+qp.getBonusGameId());
		//System.out.println("bonusPercentage:"+qp.getBonusPercentage());
		//System.out.println("bonusWagerRemaining:"+qp.getBonusWagerRemaining());
		//System.out.println("message:"+qp.getMessage());*/
		//存款
//		MoneyResponse mr = h.depositPlayerMoney(params);
//		//System.out.println("success:"+mr.isSuccess());
//		//System.out.println("amount:"+mr.getAmount());
//		//System.out.println("realBalance:"+mr.getRealBalance());
//		//System.out.println("currencyCode:"+mr.getCurrencyCode());
//		//System.out.println("transactionId:"+mr.getTransactionId());
//		//System.out.println("message:"+mr.getMessage());
		
		//登录并且创建帐号
		/*LoginUserResponse lu = h.loginOrCreatePlayer(username,password,params);
		//System.out.println("authenticated:"+lu.isAuthenticated());
		//System.out.println("playerId:"+lu.getPlayerId());
		//System.out.println("brandId:"+lu.getBrandId());
		//System.out.println("brandName:"+lu.getBrandName());
		//System.out.println("token:"+lu.getToken());
		//System.out.println("realBalance:"+lu.getRealBalance());
		//System.out.println("currencyCode:"+lu.getCurrencyCode());
		//System.out.println("playerCreated:"+lu.isPlayerCreated());
		////System.out.println("currentSessionId:"+lu.getCurrentSessionId());
		//System.out.println("hasBonus:"+lu.isHasBonus());
		//System.out.println("bonusBalance:"+lu.getBonusBalance());
		//System.out.println("bonusSpins:"+lu.getBonusSpins());
		//System.out.println("bonusGameId:"+lu.getBonusGameId());
		//System.out.println("bonusPercentage:"+lu.getBonusPercentage());
		//System.out.println("bonusWagerRemaining:"+lu.getBonusWagerRemaining());
		//System.out.println("message:"+lu.getMessage());*/
		
		//获取游戏列表
		
		try {

			File f = new File("C:\\HABA\\1.txt");
			if (!f.exists()) {
				f.createNewFile();
			}
			GameClientDbDTO[] gc = h.getGameList("192.168.0.5");

			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8");
			BufferedWriter o = new BufferedWriter(osw);
			JSONArray ja=new JSONArray();
			int i = 1;
			for (GameClientDbDTO g : gc) {
				JSONObject jo=new JSONObject();
				System.out.println(i++);
				System.out.println("-----------game-------------");
				System.out.println(g.getName());
				System.out.println(g.getKeyName());
				System.out.println(g.getBrandGameId()); 
				GameTranslationDTO[] gtd = g.getTranslatedNames();
				for (GameTranslationDTO gdtg : gtd) {
					System.out.println("-----------gdt-----------");
					System.out.println(gdtg.getLocale());
					System.out.println(gdtg.getTranslation());
					if("zh-CN".equals(gdtg.getLocale())){
						jo.put("platform_type", "8");
						jo.put("game_type", g.getKeyName());
						jo.put("game_value", gdtg.getTranslation());
						ja.add(jo); 
					} 
				}
			}
			o.newLine();
			o.write(ja.toString());
			o.close();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//游戏地址拼接
		//https://app-test.insvr.com/play?brandid=92bc6a4f-16f4-e611-80d9-000d3a802d1d&keyname=SGFenghuang&token=985746462f4627c274694f92a95e43a30bf05f6ecinsuhra&mode=real&locale=cn&lobbyurl=192.168.0.5
	}



}
