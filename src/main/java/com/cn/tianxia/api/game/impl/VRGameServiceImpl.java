package com.cn.tianxia.api.game.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.AesUtil;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName VRGameServiceImpl
 * @Description VR游戏接口实现类
 * @author Hardy
 * @Date 2019年2月9日 下午4:38:21
 * @version 1.0.0
 */
public class VRGameServiceImpl{
    
    private static final Logger logger = LoggerFactory.getLogger(VRGameServiceImpl.class);
    
	String apiurl="http://fe.vrbetdemo.com/";
	String key="T2RFT6BXD2PPLJ0660J44X2846N40N64";
	String version="1.0";
	String id="TX";
	String odds=null;
	public VRGameServiceImpl(Map<String, String> pmap){
		if(pmap!=null){
			PlatFromConfig pf=new PlatFromConfig();
			pf.InitData(pmap, "VR");
			JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
			apiurl=jo.getString("apiurl");
			key=jo.getString("key");
			version=jo.getString("version");
			id=jo.getString("id");
			try{
				//获取赔率
				odds=jo.getString("odds");
			}catch(Exception e){
				
			}
		}
	}
	/**
	 * 创建用户
	 * @param userName
	 * @return
	 */
	public String CreateUser(String userName){
     logger.info("用户【"+ userName +"】，调用VR游戏检查或创建游戏账号业务开始");
		String api=apiurl+"Account/CreateUser";
		try{

			JSONObject jo=new JSONObject();
			jo.put("playerName", userName);
			String data=jo.toString();
			String aesdata=AesUtil.encrypt(key,data);
			Map<String, Object> paramsMap =new HashMap<>();
			paramsMap.put("version", version);
			paramsMap.put("id", id);
			paramsMap.put("data",aesdata);
			logger.info("VR  创建用户请求参数：{}",paramsMap);
			String aesstr=doPost(api, paramsMap);
			if(StringUtils.isEmpty(aesstr)){
				logger.info("用户【"+ userName +"】，调用VR游戏检查或创建游戏账号发起HTTP请求无响应结果！");
				return "error";
			}
			logger.info("用户【"+ userName +"】，调用VR游戏检查或创建游戏账号发起HTTP请求响应结果：{}",aesstr);
			String str=AesUtil.decrypt(key, aesstr);
			JSONObject jsonObject =new JSONObject().fromObject(str);
			if(!"0".equals(jsonObject.getString("errorCode"))&&!"18".equals(jsonObject.getString("errorCode"))){
				FileLog f = new FileLog();
				Map<String, String> map = new HashMap<>();
				map.put("tagUrl", api);
				map.put("Data", data);
				map.put("Function", "CreateUser");
				map.put("response", str);
				f.setLog("VR", map);
				return "error";
			}
			return "success";
		}catch(Exception e){
			e.printStackTrace();
			logger.info("VR 发起HTTP请求第三方失败！",e.getMessage());
			return "error";
		}
	}
	
	/**
	 * 登陆游戏
	 * @param userName 用户名
	 * @param channelId 游戏频道 
	 * @return
	 */
	public String LoginGame(String userName,String channelId,String handicap){
		SimpleDateFormat myFmt=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date now=new Date(); 
		 Calendar cal=Calendar.getInstance();
		 cal.setTime(now);
		 cal.add(Calendar.HOUR, -8); 
		 now=cal.getTime();
		String date=myFmt.format(now);
		String api=apiurl+"Account/LoginValidate";
		StringBuffer sb=new StringBuffer(); 
		sb.append("playerName="+ userName);
		sb.append("&loginTime="+ date);
		sb.append("&channelId="+ channelId);
		if(!handicap.isEmpty()){
			sb.append("&playerOdds="+ handicap);
		}
		
//		if(odds!=null){
//			try{
//				String cagent=userName.substring(0, 3).toUpperCase();
//				String odd="";
//				JSONObject jo=JSONObject.fromObject(odds);
//				if(jo.has(cagent)){
//					odd=jo.getString(cagent);
//				}else{
//					odd=jo.getString("default");
//				}
//				sb.append("&playerOdds="+ odd);
//			}catch(Exception e){ 
//			}
//		} 
		/*sb.append("&departureUrl="+ returl);
		sb.append("&walletDepositUrl="+ DepositUrl);
		sb.append("&walletWithdrawUrl="+ WithdrawUrl);
		sb.append("&walletUrl="+ walletUrl);  */
		String data=sb.toString(); 
		String aesdata=AesUtil.encrypt(key,data); 
		try {
			aesdata=URLEncoder.encode(aesdata, "utf-8");
		} catch (UnsupportedEncodingException e) { 
		}  
		String aesstr=api+"?version="+version+"&id="+id+"&data="+aesdata; 
		return aesstr;
	}
	
	
	/**
	 * 转出余额
	 * @param billno 单据号
	 * @param userName 用户名
	 * @param amount 金额
	 * @return
	 */
	public String Withdraw(String billno,String userName,float amount){
		logger.info("VR 用户【"+ userName +"】 订单编号【"+billno +"】 发起转出余额接口开始！");
		String api=apiurl+"UserWallet/Transaction";
		SimpleDateFormat myFmt=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date now=new Date();
		Calendar cal=Calendar.getInstance();
		 cal.setTime(now);
		 cal.add(Calendar.HOUR, -8); 
		 now=cal.getTime();
		String date=myFmt.format(now);
		JSONObject jo=new JSONObject();
		String data = null;
		String str  = null;
		try{
			jo.put("playerName", userName);
			jo.put("serialNumber", billno);
			jo.put("type", 1);
			jo.put("amount", amount);
			jo.put("createTime", date);
			data=jo.toString();
			String aesdata=AesUtil.encrypt(key,data);

			Map<String, Object> paramsMap =new HashMap<>();
			paramsMap.put("version", version);
			paramsMap.put("id", id);
			paramsMap.put("data",aesdata);
			logger.info("VR 发起HTTP请求第三方查询余额接口参数：{}",paramsMap);
			String aesstr=doPost(api, paramsMap);
			if(org.apache.commons.lang3.StringUtils.isBlank(aesstr)){
			    logger.info("VR 发起HTTP请求第三方查询余额接口无响应结果");
			    return "error";
			}
			logger.info("VR 发起HTTP请求第三方查询余额接口响应结果：{}",aesstr);
			str=AesUtil.decrypt(key, aesstr);
			logger.info("VR 发起HTTP请求第三方查询余额接口响应解析结果：{}",str);
			JSONObject jsonObject = new JSONObject().fromObject(str);
			if(jsonObject.containsKey("state") && "0".equals(jsonObject.getString("state"))){
			    return "success";
			}else if(!"10".equals(jo.getString("state"))){
                FileLog f = new FileLog();
                Map<String, String> map = new HashMap<>();  
                map.put("tagUrl", api);
                map.put("Data", data);
                map.put("Function", "Withdraw");
                map.put("response", str);
                f.setLog("VR", map);
                return "error";
            } else{
                return "余额不足";
            }
		}catch(Exception e){
			e.printStackTrace();
			logger.error("VR  调用VR查询余额接口异常，",e.getMessage());
			return "error";
		}
		
	}
	
	/**
	 * 转入游戏
	 * @param billno 单据号
	 * @param userName 用户名
	 * @param amount 金额
	 * @return
	 */
	public String Deposit(String billno,String userName,float amount){
	    logger.info("VR 用户【"+ userName +"】 订单编号【"+billno +"】 发起转入（上分）接口开始！");
		try{
	        String api=apiurl+"UserWallet/Transaction";
	        JSONObject jo=new JSONObject();
	        SimpleDateFormat myFmt=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	        Date now=new Date();
	        Calendar cal=Calendar.getInstance();
	        cal.setTime(now);
	        cal.add(Calendar.HOUR, -8);
	        now=cal.getTime();
	        String date=myFmt.format(now);
	        jo.put("playerName", userName);
	        jo.put("serialNumber", billno);
	        jo.put("type", 0);
	        jo.put("amount", amount);
	        jo.put("createTime", date);
	        String data=jo.toString();
			logger.info("VR 用户【"+ userName +"】 ,订单编号【"+ billno +"】 发起第三方转账（上分）待加密参数请求报文：{}",data);
			String aesdata=AesUtil.encrypt(key,data);
			logger.info("VR 用户【"+ userName +"】 ,订单编号【"+ billno +"】 发起第三方转账（上分）加密后参数请求报文：{}",aesdata);
			Map<String, Object> paramsMap =new HashMap<>();
			paramsMap.put("version", version);
			paramsMap.put("id", id);
			paramsMap.put("data",aesdata);
			logger.info("VR 用户【"+ userName +"】 ,订单编号【"+ billno +"】 发起第三方转账（上分）业务参数：{}",paramsMap.toString());
			String aesstr=doPost(api, paramsMap);
			if(StringUtils.isEmpty(aesstr)){
				logger.info("VR 用户【"+ userName +"】 ,订单编号【"+ billno +"】，调用VR游戏上分转账发起HTTP请求无响应结果！");
				return "error";
			}
			logger.info("VR 用户【"+ userName +"】 ,订单编号【"+ billno +"】发起第三方转账（上分）业务响应结果返回参数：{}",aesstr);
			String str = AesUtil.decrypt(key, aesstr);
			logger.info("VR 用户【"+ userName +"】 ,订单编号【"+ billno +"】发起第三方转账（上分）业务响应结果解析参数：{}",str);
			JSONObject jsonObject = new JSONObject().fromObject(str);
			if(jsonObject.containsKey("state") && "0".equals(jsonObject.getString("state"))){
			    return "success";
			}else{
                FileLog f = new FileLog();
                Map<String, String> map = new HashMap<>();  
                map.put("tagUrl", api);
                map.put("Data", data);
                map.put("Function", "Deposit");
                map.put("response", str);
                f.setLog("VR", map);
                return "error";
            }
		}catch(Exception e){
			e.printStackTrace();
			logger.error("VR 发起转账上分失败！",e);
			return "error";
		}
	}
	
	
	/**
	 * 获取余额
	 * @param userName
	 * @return
	 */
	public String getBalance(String userName){
		logger.info("VR 用户【"+ userName +"】，调用VR查询余额接口业务开始");
		String api=apiurl+"UserWallet/Balance";
		JSONObject jo=new JSONObject();
		jo.put("playerName", userName);
		String data=jo.toString();
		logger.info("VR 用户查询余额接口待加密请求参数：{}",data);
		String aesdata=AesUtil.encrypt(key,data);
		
		Map<String, Object> paramsMap =new HashMap<>();
		paramsMap.put("version", version);
		paramsMap.put("id", id);
		paramsMap.put("data",aesdata);
		String balance="0";
		try{
			logger.info("VR 用户查询余额接口请求参数：{}",paramsMap);
			String aesstr=doPost(api, paramsMap);
			if (StringUtils.isEmpty(aesstr)){
				logger.info("用户【"+ userName +"】，调用VR游戏查余额接口发起HTTP请求无响应结果！");
				return "error";
			}
			String str=AesUtil.decrypt(key, aesstr);
			logger.info("VR 发第三方HTTP请求查询余额接口响应参数解析报文：{}",str);
			jo=new JSONObject().fromObject(str);
			balance=jo.getString("balance");
		}catch(Exception e){
			e.printStackTrace();
			logger.info("VR 查询用户【"+ userName +"】接口异常:",e);
		} 
		return balance;
	}
	
	
	/**
	 * 获取注单
	 * @param startTime 查询的起始时间，使用 UTC(+0)的时间表示法: 2016-10-13T10:04:34Z
	 * @param endTime 查询的结束时间，使用 UTC(+0)的时间表示法: 2016-10-14T10:04:34Z
	 * @param recordPage 查询的页数(从 0 开始)
	 * @param recordCountPerPage 查询的数量
	 * @param isUpdateTime 时间条件设定，此变量用来设定 startTime 与
											endTime 的比对目标。isUpdateTime 设定为
											true 时，比对的目标为投注纪录里的最后更新 
											时间(updateTime)若设定为false或是没有设定 
											时，则采用投注纪录的下注时间(createTime)
											为比对目标。 
	 * @param state 投注单状态(-1 表示全部, 0:未颁奖, 1:撤单, 2:未中奖, 3:中奖)
	 * @return
	 */
	public String getBetRecord(String startTime,String endTime,int recordPage,int recordCountPerPage,boolean isUpdateTime,int state){
		String api=apiurl+"MerchantQuery/Bet";
		JSONObject jo=new JSONObject();
		jo.put("startTime", startTime);
		jo.put("endTime", endTime);
		jo.put("channelId", -1);
		//jo.put("issueNumber", "123");
		//jo.put("playerName", "");
		//jo.put("serialNumber", "");
		jo.put("state", state);
		jo.put("isUpdateTime", isUpdateTime);
		jo.put("recordCountPerPage", recordCountPerPage);
		jo.put("recordPage", recordPage);
		String data=jo.toString();
		System.out.println(jo.toString());
		//String data="{ \"startTime\": \"2016-10-13T10:04:34Z\", \"endTime\": \"2016-10-14T10:04:34Z\", \"channelId\": 1, \"issueNumber\": \"20161014282\", \"playerName\": \"\", \"serialNumber\": \"\", \"state\": -1, \"isUpdateTime\": false, \"recordCountPerPage\": 10000, \"recordPage\": 0 }";
		System.out.println(data);
		String aesdata=AesUtil.encrypt(key,data);
		System.out.println(aesdata); 
		Map<String, Object> paramsMap =new HashMap<>();
		paramsMap.put("version", version);
		paramsMap.put("id", id);
		paramsMap.put("data",aesdata);
		String aesstr=doPost(api, paramsMap);
		System.out.println(aesstr);
		String str=AesUtil.decrypt(key, aesstr);
		System.out.println(str);
		return "";
	}


	
	public static String doPost(String url, Map<String, Object> paramsMap) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = HttpClients.createDefault(); 
			
			httpPost = new HttpPost(url); 
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if(paramsMap != null){//设置参数
				Iterator iterator = paramsMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					nvps.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			}
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) { 
				if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
					FileLog f = new FileLog();
					Map<String, String> map = new HashMap<>();
					map.put("statusCode", response.getStatusLine().getStatusCode() + "");
					map.put("ResponseBody", response.getEntity().toString());
					map.put("tagUrl", url);
					map.put("Data", paramsMap.get("data").toString());
					map.put("Function", "sendPost");
					f.setLog("VR", map);
					throw new Exception("请求错误URL->"+url+"->请求状态码->"+response.getStatusLine().getStatusCode());
				}
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, "UTF-8");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally {
			httpPost.releaseConnection();
		}
		return result;
	}
	
	
	
	public static void main(String[] args) {
		String returl="http://www.tx1888.com";
		String username="bl1test001";
		VRGameServiceImpl v=new VRGameServiceImpl(null);
		//System.out.println(v.CreateUser(username));
		//System.out.println(v.LoginGame(username, "0", returl, returl, returl, returl));
		//System.out.println(v.Deposit("asd12345678901", username, 100.5f));
		//System.out.println(v.getBalance(username));
		System.out.println(v.getBetRecord("2017-10-07T00:00:00Z", "2017-10-07T23:59:59Z", 0, 100, true, -1));
	}

}
