package com.cn.tianxia.api.game.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.HttpClientUtil;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * MG游戏平台
 * 
 * @author Administrator
 *
 */ 
public class MGGameServiceImpl{
	private static Logger logger = LoggerFactory.getLogger(MGGameServiceImpl.class);
	
	private static String CID;
	private static String CRTYPE;
	private static String NEID;
	private static String NETYPE;
	private static String TARTYPE;
	private static String CURRENCY;
	private static String PARTNERID;
	private static String apiaccount ;
	private static String apipassword;
	private static String url;
	private static String SH;
	private static String SHpassword;
	private static String wz;

	public MGGameServiceImpl(Map<String, String> pmap) {
		PlatFromConfig pf=new PlatFromConfig();
		pf.InitData(pmap, "MG");
		JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
		CID=jo.getString("CID").toString();
		CRTYPE=jo.getString("CRTYPE").toString();
		NEID=jo.getString("NEID").toString();
		NETYPE=jo.getString("NETYPE").toString();
		TARTYPE=jo.getString("TARTYPE").toString();
		CURRENCY=jo.getString("CURRENCY").toString();
		PARTNERID=jo.getString("PARTNERID").toString();
		apiaccount=jo.getString("apiaccount").toString();
		apipassword=jo.getString("apipassword").toString();
		url=jo.getString("url").toString();
		SH=jo.getString("SH").toString();
		SHpassword=jo.getString("SHpassword").toString();
		wz=jo.getString("wz").toString();
	}

	
	public JSONObject createAccount(String username,String password, Map paramMap) {
		JSONObject returnResult = new JSONObject();
		String uri="";
		JSONObject params=null;
		JSONObject resultJson=null;
		try {
			JSONObject jsonToken = (JSONObject)this.getSessionGUID(username, password, paramMap, false);
			if(jsonToken.getString("Code").equals("error")){
				logger.error("MG获取Token失败->"+jsonToken.toString());
				returnResult.put("Code", "error");
				returnResult.put("Message", jsonToken.getString("Message"));
				return returnResult;
			}  
			uri = url+"/lps/secure/network/"+jsonToken.getString("id")+"/downline";
			//System.out.println("MG创建帐号请求URI："+uri);
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("X-Requested-With", "X-Api-Client");
			header.put("X-Api-Call", "X-Api-Call");
			header.put("X-Api-Auth", jsonToken.getString("Token"));
			params = new JSONObject();
			params.put("crId", NEID);
			params.put("crType", NETYPE);
			params.put("neId", NEID);
			params.put("neType", NETYPE);
			params.put("tarType", TARTYPE);
			params.put("username", username);
			params.put("name", username);
			params.put("password", password);
			params.put("confirmPassword", password);
			params.put("currency", CURRENCY);
			params.put("language", "zh");
			params.put("email", "");
			params.put("mobile", "");
			JSONObject enable = new JSONObject();
			//enable.put("enable", false);
			params.put("casino", "{\"enable\":true}");
			params.put("poker", "{\"enable\":false}");
			//System.out.println("MG创建帐号参数->"+params.toString());
			String result = HttpClientUtil.doPut(uri, header, params.toString());
			//System.out.println("MG创建帐号结果->"+result);
			resultJson = JSONObject.fromObject(result);
			if(resultJson.getBoolean("success") || resultJson.getString("message").equals("Username existed.")||result.indexOf("true")>0){
				returnResult.put("Code", "success");
				returnResult.put("Message", "Success.");
				return returnResult;
			}else{
				FileLog f=new FileLog(); 
    			Map<String,String> map =new HashMap<>(); 
    			map.put("uri", uri); 
    			map.put("params", params.toString()); 
    			map.put("msg",  resultJson.getString("message"));
    			map.put("Function", "createAccount");
    			f.setLog("MG", map);
				returnResult.put("Code", "error");
				returnResult.put("Message", resultJson.getString("message"));
				return returnResult;
			}
		} catch (Exception e) {
			logger.error("MG创建帐号异常", e);
			e.printStackTrace();
			returnResult.put("Code", "error");
			returnResult.put("Message", e.getMessage());
			return returnResult;
		}finally{
			/*FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>(); 
			map.put("uri", uri); 
			map.put("params", params.toString()); 
			map.put("msg",  resultJson.getString("message"));
			map.put("Function", "createAccount");
			f.setLog("MG", map);
			returnResult.put("Code", "error");
			returnResult.put("Message", resultJson.getString("message"));
			f.setLog("MG", map);*/
		}
	}

	
	public JSONObject loginGame(String username,String password, Map paramMap) {
		JSONObject returnResult = new JSONObject();
		String LaunchResult="";
		String uri = url+"/member-api-web/member-api";
		StringBuffer sb = new StringBuffer("<mbrapi-launchurl-call ");
		try {
			JSONObject LoginToken = (JSONObject)this.resetloginattempts(username, password, paramMap);
			if(LoginToken.getString("Code").equals("error")){
				logger.error("登录MG游戏获取用户Token失败->"+LoginToken.toString());
				returnResult.put("Code", "error");
				returnResult.put("Message", LoginToken.getString("Message"));
				return returnResult;
			} 
			sb.append("timestamp=\"").append(getTime()).append(" UTC\" ");
			sb.append("apiusername=\"").append(apiaccount).append("\" ");
			sb.append("apipassword=\"").append(apipassword).append("\" ");
			sb.append("token=\"").append(LoginToken.getString("Token")).append("\" ");
			sb.append("language=\"").append(paramMap.get("language")).append("\" ");
			sb.append("gameId=\"").append(paramMap.get("gameId")).append("\" ");
			sb.append("bankingUrl=\"").append(wz).append("\" ");
			sb.append("lobbyUrl=\"").append(wz).append("\" ");
			sb.append("logoutRedirectUrl=\"").append(wz).append("\" ");
			sb.append("demoMode=\"").append(paramMap.get("demoMode")).append("\" ");//true为试玩
			sb.append("/>");
			//System.out.println("MG Launch Game URL->"+sb.toString());
			LaunchResult = HttpClientUtil.doPostXml(uri, null, sb.toString());
			//System.out.println("MG Launch Game URL Result->"+LaunchResult);
			if(StringUtils.isNotBlank(LaunchResult)){
				Document document = DocumentHelper.parseText(LaunchResult);
				Element ele = document.getRootElement();
				if(ele.attributeValue("status").equals("0")){
					returnResult.put("Code", "success");
					returnResult.put("LaunchUrl", ele.attributeValue("launchUrl"));
					return returnResult;
				}else{
					FileLog f=new FileLog(); 
					Map<String,String> map =new HashMap<>(); 
					map.put("uri", uri); 
	    			map.put("params", sb.toString()); 
					map.put("msg",  LaunchResult);
					map.put("Function", "loginGame");
					f.setLog("MG", map);
					logger.error("[Error]MG Launch Game URL->"+ele.attributeValue("status"));
				}
			} 
			returnResult.put("Code", "error");
			returnResult.put("Message", "LaunchResult is null.");
			return returnResult;
		} catch (Exception e) {
			logger.error("MG登录游戏异常", e);
			e.printStackTrace();
			returnResult.put("Code", "error");
			returnResult.put("Message", e.getMessage());
			return returnResult;
		}finally{
			/*FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>(); 
			map.put("uri", uri); 
			map.put("params", sb.toString()); 
			map.put("msg",  LaunchResult);
			map.put("Function", "loginGame");
			f.setLog("MG", map);*/
		}
	}

	
	public JSONObject logoutGame(String username,String password, Map paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public JSONObject deposit(String username,String password, String amount, Map paramMap) {
		JSONObject returnResult = new JSONObject();
		try {
			JSONObject LoginToken = (JSONObject)this.resetloginattempts(username, password, paramMap);
			if(LoginToken.getString("Code").equals("error")){
				logger.error("登录MG游戏获取用户Token失败->"+LoginToken.toString());
				returnResult.put("Code", "error");
				returnResult.put("Message", LoginToken.getString("Message"));
				return returnResult;
			}
			String uri = url+"/member-api-web/member-api";
			StringBuffer sb = new StringBuffer("<mbrapi-changecredit-call ");
			sb.append("timestamp=\"").append(getTime()).append(" UTC\" ");
			sb.append("apiusername=\"").append(apiaccount).append("\" ");
			sb.append("apipassword=\"").append(apipassword).append("\" ");
			sb.append("token=\"").append(LoginToken.getString("Token")).append("\" ");
			sb.append("product=\"").append("casino").append("\" ");
			sb.append("operation=\"").append("topup").append("\" ");
			sb.append("amount=\"").append(amount).append("\" ");
			sb.append("tx-id=\"").append(paramMap.get("OrderId")).append("\" ");
			sb.append("/>");
			logger.info("MG转入游戏参数,request->{}" , sb.toString());
			String result = HttpClientUtil.doPostXml(uri, null, sb.toString());
			logger.info("MG游戏转入请求返回结果，result->{}" , result);
			if(StringUtils.isNotBlank(result)){
				Document document = DocumentHelper.parseText(result);
				Element ele = document.getRootElement();
				if(ele.attributeValue("status").equals("0")){
					returnResult.put("Code", "success");
					returnResult.put("Message", "MG转入游戏成功，金额->"+amount);
					return returnResult;
				}else{
					FileLog f=new FileLog(); 
					Map<String,String> map =new HashMap<>(); 
					map.put("uri", uri); 
	    			map.put("params", sb.toString()); 
					map.put("msg",  result);
					map.put("Function", "deposit");
					f.setLog("MG", map);
					logger.error("MG游戏转入失败->{}",ele.attributeValue("status"));
					returnResult.put("Code", "faild");
					returnResult.put("Message", "MG转入游戏失败->" + ele.attributeValue("status"));
					return returnResult;
				}
			}
			logger.error("MG转入游戏异常->第三方返回结果为空.");
			returnResult.put("Code", "error");
			returnResult.put("Message", "MG转入游戏API结果为空.");
			return returnResult;
		} catch (Exception e) {
			logger.error("MG转入异常", e);
			returnResult.put("Code", "error");
			returnResult.put("Message", e.getMessage());
			return returnResult;
		}
	}

	
	public JSONObject queryBalance(String username,String password, Map paramMap) {
		JSONObject returnResult = new JSONObject();
		try {
			JSONObject LoginToken = (JSONObject)this.resetloginattempts(username, password, paramMap);
			if(LoginToken.getString("Code").equals("error")){
				logger.error("登录MG游戏获取用户Token失败->"+LoginToken.toString());
				returnResult.put("Code", "error");
				returnResult.put("Message", LoginToken.getString("Message"));
				return returnResult;
			}
			String uri = url+"/member-api-web/member-api";
			StringBuffer sb = new StringBuffer("<mbrapi-account-call ");
			sb.append("timestamp=\"").append(getTime()).append(" UTC\" ");
			sb.append("apiusername=\"").append(apiaccount).append("\" ");
			sb.append("apipassword=\"").append(apipassword).append("\" ");
			sb.append("token=\"").append(LoginToken.getString("Token")).append("\" ");
			sb.append("/>");
			logger.info("MG获取游戏余额参数，request->{}" , sb.toString());
			String result = HttpClientUtil.doPostXml(uri, null, sb.toString());
			logger.info("MG获取游戏余额返回结果，result->{}" , result);
			if(StringUtils.isNotBlank(result)){
				Document document = DocumentHelper.parseText(result);
				Element ele = document.getRootElement();
				if(ele.attributeValue("status").equals("0")){
					Element wallets = ele.element("wallets");
					Element accountWallet = wallets.element("account-wallet");
					returnResult.put("Code", "success");
					returnResult.put("Balance", accountWallet.attributeValue("credit-balance"));
					returnResult.put("Message", "Success!");
					return returnResult;
				}else{
					FileLog f=new FileLog(); 
					Map<String,String> map =new HashMap<>(); 
					map.put("uri", uri); 
	    			map.put("params", sb.toString()); 
					map.put("msg",  result);
					map.put("Function", "queryBalance");
					f.setLog("MG", map);
					logger.error("MG获取余额失败->"+ele.attributeValue("status"));
					returnResult.put("Code", "faild");
					returnResult.put("Message", "MG获取游戏余额失败->" + ele.attributeValue("status"));
				}
			}
			logger.error("MG获取游戏API余额异常->返回结果为空");
			returnResult.put("Code","error");
			returnResult.put("Message", "MG获取游戏余额API结果为空.");
			return returnResult;
		} catch (Exception e) {
			logger.error("MG获取游戏余额异常", e);
			returnResult.put("Code", "error");
			returnResult.put("Message", e.getMessage());
			return returnResult;
		}
	}

	
	public JSONObject withdrawal(String username,String password, String amount, Map paramMap) {
		JSONObject returnResult = new JSONObject();
		try {
			JSONObject LoginToken = (JSONObject)this.resetloginattempts(username,password, paramMap);
			if(LoginToken.getString("Code").equals("error")){
				logger.error("登录MG游戏获取用户Token失败->"+LoginToken.toString());
				returnResult.put("Code", "error");
				returnResult.put("Message", LoginToken.getString("Message"));
				return returnResult;
			}
			String uri = url+"/member-api-web/member-api";
			StringBuffer sb = new StringBuffer("<mbrapi-changecredit-call ");
			sb.append("timestamp=\"").append(getTime()).append(" UTC\" ");
			sb.append("apiusername=\"").append(apiaccount).append("\" ");
			sb.append("apipassword=\"").append(apipassword).append("\" ");
			sb.append("token=\"").append(LoginToken.getString("Token")).append("\" ");
			sb.append("product=\"").append("casino").append("\" ");
			sb.append("operation=\"").append("withdraw").append("\" ");
			sb.append("amount=\"").append(amount).append("\" ");
			sb.append("tx-id=\"").append(paramMap.get("OrderId")).append("\" ");
			sb.append("/>");
			logger.info("MG转出游戏参数,request ->{}",sb.toString());
			String result = HttpClientUtil.doPostXml(uri, null, sb.toString());
			logger.info("MG转出游戏结果,result ->{}", result);
			if(StringUtils.isNotBlank(result)){
				Document document = DocumentHelper.parseText(result);
				Element ele = document.getRootElement();
				if(ele.attributeValue("status").equals("0")){
					returnResult.put("Code", "success");
					returnResult.put("Message", "MG转出游戏成功，金额->"+amount);
					return returnResult;
				}else{
					FileLog f=new FileLog(); 
					Map<String,String> map =new HashMap<>(); 
					map.put("uri", uri); 
	    			map.put("params", sb.toString()); 
					map.put("msg", result);
					map.put("Function", "withdrawal");
					f.setLog("MG", map);
					logger.error("MG游戏转出失败->{}", ele.attributeValue("status"));
					returnResult.put("Code", "faild");
					returnResult.put("Message", "MG转出游戏失败->" + ele.attributeValue("status"));
					return returnResult;
				}
			}
			logger.error("MG转出游戏异常->返回结果为空");
			returnResult.put("Code", "error");
			returnResult.put("Message", "MG转出游戏API结果为空.");
			return returnResult;
		} catch (Exception e) {
			logger.error("MG转出异常", e);
			returnResult.put("Code", "error");
			returnResult.put("Message", e.getMessage());
			return returnResult;
		}
	}

	
	public JSONObject betRecord(String username,String password, Map paramMap) {
		JSONObject returnResult = new JSONObject();
		try {
			JSONObject jsonToken = (JSONObject)this.transferCreditConfirm(username, password, paramMap);
			if(jsonToken.getString("Code").equals("error")){
				logger.error("MG获取Token失败->"+jsonToken.toString());
				returnResult.put("Code", "error");
				returnResult.put("Message", jsonToken.getString("Message"));
				return returnResult;
			}
			String uri = url +  "/lps/secure/hortx/94969870?";
			uri += "start="+paramMap.get("starttime");//yyyy:MM:dd:HH:mm:ss
			uri += "&end="+paramMap.get("endtime");//yyyy:MM:dd:HH:mm:ss
			uri += "&timezone=Asia/Shanghai";
			//System.out.println("MG获取注单URI->"+uri);
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("X-Requested-With", "X-Api-Client");
			header.put("X-Api-Call", "X-Api-Client");
			header.put("X-Api-Auth", jsonToken.getString("Token"));
			String result = HttpClientUtil.doGet(uri, header);
			//System.out.println("MG获取注单结果->"+result);
		} catch (Exception e) {
			logger.error("MG获取游戏注单异常",e);
		}
		return null;
	}

	
	public JSONObject edit(String username,String password, Map paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取用户登录Token
	 */
	
	public Object resetloginattempts(String username,String password, Map paramMap) {
		JSONObject returnResult = new JSONObject();
		try {
			String uri = url+"/member-api-web/member-api";
			//System.out.println("登录游戏URI->"+uri); 
			StringBuffer sb = new StringBuffer("<mbrapi-login-call ");
			sb.append("timestamp=\"").append(getTime()).append(" UTC\" ");
			sb.append("apiusername=\"").append(apiaccount).append("\" ");
			sb.append("apipassword=\"").append(apipassword).append("\" ");
			sb.append("username=\"").append(username).append("\" ");
			sb.append("password=\"").append(password).append("\" ");
			sb.append("ipaddress=\"").append(paramMap.get("ClientIP")).append("\" ");
			sb.append("partnerId=\"").append(PARTNERID).append("\" ");
			sb.append("currencyCode=\"").append(CURRENCY).append("\"");
			sb.append("/>");
			System.out.println("MG登录获取Token参数->"+sb.toString());
			String result = HttpClientUtil.doPostXml(uri, null, sb.toString());
			System.out.println("MG登录获取Token结果->"+result);
			if(StringUtils.isNotBlank(result)){ 
				Document document = DocumentHelper.parseText(result);
				Element ele = document.getRootElement();
				if(ele.attributeValue("status").equals("0")){
					returnResult.put("Code", "success");
					returnResult.put("Token", ele.attributeValue("token"));
					returnResult.put("CasinoId", ele.attributeValue("casinoId"));
					return returnResult;
				}else{
					FileLog f=new FileLog(); 
					Map<String,String> map =new HashMap<>(); 
					map.put("uri", uri); 
	    			map.put("params", sb.toString()); 
					map.put("msg",  result);
					map.put("Function", "resetloginattempts");
					f.setLog("MG", map);
					logger.error("MG调用登录游戏API异常->status："+ele.attributeValue("status"));
				}
			}
			returnResult.put("Code", "error");
			returnResult.put("Message", "result is null.");
			return returnResult;
		} catch (Exception e) {
			logger.error("MG登录游戏异常", e);
			e.printStackTrace();
			returnResult.put("Code", "error");
			returnResult.put("Message", e.getMessage());
			return returnResult;
		}
	}

	
	public Object getSessionGUID(String username,String password, Map paramMap, boolean isCache) {
		JSONObject returnResult = new JSONObject();
		try {
			String uri = url+"/lps/j_spring_security_check";
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("X-Requested-With", "X-Api-Client");
			header.put("X-Api-Call", "X-Api-Client");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("j_username", SH);
			params.put("j_password", SHpassword);
			String result = HttpClientUtil.doPost(uri, header, params);
			//System.out.println("MG获取Token结果->"+result);
			JSONObject resultJson = JSONObject.fromObject(result);
			if(resultJson.containsKey("message")){
				FileLog f=new FileLog(); 
				Map<String,String> map =new HashMap<>(); 
				map.put("uri", uri); 
    			map.put("params", params.toString()); 
				map.put("msg",  result);
				map.put("Function", "getSessionGUID");
				f.setLog("MG", map);
				returnResult.put("Code", "error");
				returnResult.put("Message", resultJson.getString("message"));
				return returnResult;
			}
			returnResult.put("Code", "success");
			returnResult.put("Token", resultJson.getString("token"));
			returnResult.put("id", resultJson.getString("id"));
			return returnResult;
		} catch (Exception e) {
			logger.error("MG获取SessionID异常", e);
			returnResult.put("Code", "error");
			returnResult.put("Message", e.getMessage());
			return returnResult;
		}
	}
 
	public JSONObject transferCreditConfirm(String username,String password, Map paramMap) {
		JSONObject returnResult = new JSONObject();
		try {
			String uri = url+"/lps/j_spring_security_check";
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("X-Requested-With", "X-Api-Client");
			header.put("X-Api-Call", "X-Api-Client");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("j_username", SH);
			params.put("j_password", SHpassword);
			String result = HttpClientUtil.doPost(uri, header, params);
			//System.out.println("MG获取Token结果->"+result);
			JSONObject resultJson = JSONObject.fromObject(result);
			if(resultJson.containsKey("message")){
				FileLog f=new FileLog(); 
				Map<String,String> map =new HashMap<>(); 
				map.put("uri", uri); 
    			map.put("params", params.toString()); 
				map.put("msg",  result);
				map.put("Function", "transferCreditConfirm");
				f.setLog("MG", map);
				returnResult.put("Code", "error");
				returnResult.put("Message", resultJson.getString("message"));
				return returnResult;
			}
			returnResult.put("Code", "success");
			returnResult.put("Token", resultJson.getString("token"));
			returnResult.put("id", resultJson.getString("id"));
			return returnResult;
		} catch (Exception e) {
			logger.error("MG获取SessionID异常", e);
			returnResult.put("Code", "error");
			returnResult.put("Message", e.getMessage());
			return returnResult;
		}
	}

	public Object queryOrderStatus(String username,String password, Map paramMap) {
		return null;
	}
	 
	
	public String getTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");//设置日期格式
		df.setTimeZone(TimeZone.getTimeZone("GMT+0")); 
		return df.format(new Date());// new Date()为获取当前系统时间		
	}
	 
	public static void main(String[] args) {
		String content = "<mbrapi-account-resp status=\"0\" language=\"en\"><wallets product=\"123\"><account-wallet product=\"casino\" credit-balance=\"100.000000\" cash-balance=\"100.000000\" cash-soft=\"0.000000\" cash-hard=\"0.000000\"/></wallets></mbrapi-account-resp>";
		try {
			Document document = DocumentHelper.parseText(content);
			Element ele = document.getRootElement();
			Element wallets = ele.element("wallets");
			Element accountWallet = wallets.element("account-wallet");
			//System.out.println(ele.attributeValue("status")+"-------->"+accountWallet.attributeValue("credit-balance"));
			 
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
