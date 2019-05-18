package com.cn.tianxia.api.game.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

public class BBINGameServiceImpl{ 
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");   
	String tempKey;
	/*String URL="http://linkapi.worldgameapi.com/app/WebService/JSON/display.php/";
	String gameUrl="http://888.worldgameapi.com/app/WebService/JSON/display.php/";    
    String uppername = "dtxg13"; 
    String website = "LWIN999";
    String lang = "zh-cn";
    String page_site = "live"; 
    String CreateMemberKeyB = "qYI0s9qmp";
    String LoginKeyB = "jVT56kw";
    String LogoutKeyB = "2c4URy4";
    String CheckUsrBalanceKeyB = "F7rhvnElc";
    String TransferKeyB = "53IkD3JMon";
    String BetRecordKeyB = "wIPOb81es7";
    String PlayGameKeyB = "Vw66w6oYd";*/
	
	String URL;
	String gameUrl;
    String uppername;
    String website;
    String lang ;
    String page_site;
    String CreateMemberKeyB;
    String LoginKeyB;
    String LogoutKeyB;
    String CheckUsrBalanceKeyB;
    String TransferKeyB;
    String BetRecordKeyB;
    String PlayGameKeyB;
    String CheckTransferKeyB;

	private final static Logger logger = LoggerFactory.getLogger(BBINGameServiceImpl.class);
    
    public BBINGameServiceImpl(Map<String, String> pmap) {
		PlatFromConfig pf=new PlatFromConfig();
		pf.InitData(pmap, "BBIN");
		JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
		URL=jo.getString("URL").toString();
		gameUrl=jo.getString("gameUrl").toString();
		uppername=jo.getString("uppername").toString();
		website=jo.getString("website").toString();
		lang=jo.getString("lang").toString();
		page_site=jo.getString("page_site").toString();
		CreateMemberKeyB=jo.getString("CreateMemberKeyB").toString();
		LoginKeyB=jo.getString("LoginKeyB").toString();
		LogoutKeyB=jo.getString("LogoutKeyB").toString();
		CheckUsrBalanceKeyB=jo.getString("CheckUsrBalanceKeyB").toString();
		TransferKeyB=jo.getString("TransferKeyB").toString();
		BetRecordKeyB=jo.getString("BetRecordKeyB").toString();
		PlayGameKeyB=jo.getString("PlayGameKeyB").toString();
		CheckTransferKeyB=jo.getString("CheckTransferKeyB").toString();
    }
	
	 
	public String CreateMember(String username ,String password) {
		String encryptKey;
		String key;
		String tagUrl;
		Date now = new Date();
		DESEncrypt d=new DESEncrypt("");  
		java.util.Calendar Cal=java.util.Calendar.getInstance();   
		Cal.setTime(now);   
		Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);   
		String todayStr=sdf.format(Cal.getTime());     
		tempKey = website + username + CreateMemberKeyB + todayStr; 
        encryptKey = d.getMd5(tempKey);
        key = "dsfre" + encryptKey.toLowerCase() + "hj";
        tagUrl = URL+"CreateMember?website=" + website + "&username=" + username +
        		"&uppername=" + uppername 
        		//+ "&password=" + password 
        		+ "&key=" + key;
        logger.info("BBIN【创建游戏账号】请求参数==========>"+tagUrl);
        String msg = sendGet(tagUrl);
		JSONObject json;
		json = JSONObject.fromObject(msg);
        logger.info("BBIN【创建游戏账号】响应参数<=========="+json.toString());
		if (!"true".equals(json.get("result").toString().toLowerCase())&&msg.indexOf("The account is repeated") < 0) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();
			map.put("url", tagUrl);
			map.put("loginname", username); 
			map.put("msg", msg);
			map.put("Function", "CreateMember");
			f.setLog("BBIN", map);
		}
		return msg;
	}
	
	public String CreateMobileMember(String username ,String password) {
		String encryptKey;
		String key;
		String tagUrl;
		Date now = new Date();
		DESEncrypt d=new DESEncrypt("");  
		java.util.Calendar Cal=java.util.Calendar.getInstance();   
		Cal.setTime(now);   
		Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);   
		String todayStr=sdf.format(Cal.getTime());     
		tempKey = username + "928734D2" + todayStr;
        encryptKey = d.getMd5(tempKey);
        key =  encryptKey.toLowerCase();
        tagUrl = "http://www.ibossc.com/CreateUser.ashx"+"?username=" + username 
        		//+"&password=" + password 
        		+ "&siteid=1000090&key=" + key;
        logger.info("BBIN【创建手机游戏账号】请求参数==========>"+tagUrl);
        String msg = sendGet(tagUrl);
        try{ 
    		JSONObject json;
    		json = JSONObject.fromObject(msg);
            logger.info("BBIN【创建手机游戏账号】响应参数<=========="+json.toString());
    		if (!"true".equals(json.get("result").toString().toLowerCase())&&msg.indexOf("The account is repeated")<0) {
    			FileLog f=new FileLog(); 
    			Map<String,String> map =new HashMap<>();
    			map.put("url", tagUrl);
    			map.put("loginname", username); 
    			map.put("msg", msg);
    			map.put("Function", "CreateMobileMember");
    			f.setLog("BBIN", map);
    		}
        }catch(Exception e){
        	FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();
			map.put("url", tagUrl);
			map.put("loginname", username); 
			map.put("msg", msg);
			map.put("error", e.getMessage());
			map.put("Function", "CreateMobileMember");
			f.setLog("BBIN", map);
        } 
		return msg;
	}
	 
	public String Login(String username ,String password,String pagesite) {
		String encryptKey;
		String key;
		String tagUrl;
		Date now = new Date();
		DESEncrypt d=new DESEncrypt("");  
		java.util.Calendar Cal=java.util.Calendar.getInstance();   
		Cal.setTime(now);   
		Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);   
		String todayStr=sdf.format(Cal.getTime());     
		tempKey = website + username + LoginKeyB + todayStr;
        encryptKey =  d.getMd5(tempKey);  // Ltlottery  live
        key = "dsfreews" + encryptKey.toLowerCase() + "j"; 
     // pagesite: Ltlottery  live
        tagUrl = gameUrl+"Login?website=" + website + "&username=" + username + "&uppername=" 
                + uppername 
                //+ "&password=" + password 
                + "&lang=" + lang + "&page_site="+pagesite+"&page_present=live&key=" + key;
        logger.info("BBIN【登录】响应参数<=========="+tagUrl);
		return tagUrl;
	}
	 
	public String Login2(String username ,String password) {
		String encryptKey;
		String key;
		String tagUrl;
		Date now = new Date();
		DESEncrypt d=new DESEncrypt("");  
		java.util.Calendar Cal=java.util.Calendar.getInstance();   
		Cal.setTime(now);   
		Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);   
		String todayStr=sdf.format(Cal.getTime());     
		tempKey = website + username + LoginKeyB + todayStr;
        encryptKey =  d.getMd5(tempKey); 
        key = "dsfreews" + encryptKey.toLowerCase() + "j";
        tagUrl = gameUrl+"Login2?website=" + website + "&username=" + username + "&uppername=" 
        + uppername 
        //+ "&password=" + password 
        + "&lang=" + lang + "&key=" + key+"&use_https=1";
        logger.info("BBIN【登录2】响应参数<=========="+tagUrl);
		return tagUrl;
	}
	 
	public String Logout(String username ,String password) {
		String encryptKey;
		String key;
		String tagUrl;
		Date now = new Date();
		DESEncrypt d=new DESEncrypt("");  
		java.util.Calendar Cal=java.util.Calendar.getInstance();   
		Cal.setTime(now);   
		Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);   
		String todayStr=sdf.format(Cal.getTime());     
		tempKey = website + username + LogoutKeyB + todayStr;
        encryptKey = d.getMd5(tempKey);  
        key = "dsfe" + encryptKey.toLowerCase() + "hdfeej";
        tagUrl = gameUrl+"Logout?website=" + website + "&username=" + username + "&uppername=" + uppername + "&key=" + key;
        logger.info("BBIN【登出游戏】请求参数==========>"+tagUrl);
        String msg = sendGet(tagUrl);
		return msg;
	}
	 
	public String PlayGame(String username ,String password,String gameid) {
		String encryptKey;
		String key;
		String tagUrl;
		Date now = new Date();
		DESEncrypt d=new DESEncrypt("");  
		java.util.Calendar Cal=java.util.Calendar.getInstance();   
		Cal.setTime(now);   
		Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);   
		String todayStr=sdf.format(Cal.getTime());     
		tempKey = website + username + PlayGameKeyB + todayStr;
        encryptKey = d.getMd5(tempKey);  
        key = "dsfredf" + encryptKey.toLowerCase() + "w"; 
    	tagUrl = gameUrl+"PlayGame?website=" + website + "&username=" + username +
		"&gamekind=" + 5 + "&gametype=" + gameid + "&lang="+lang+ "&key=" + key;
        logger.info("BBIN【PlayGame】响应参数<=========="+tagUrl);
		return tagUrl;
	}
	 
	public String CheckUsrBalance(String username ,String password) {
		String encryptKey;
		String key;
		String tagUrl;
		Date now = new Date();
		DESEncrypt d=new DESEncrypt("");  
		java.util.Calendar Cal=java.util.Calendar.getInstance();   
		Cal.setTime(now);   
		Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);   
		String todayStr=sdf.format(Cal.getTime());     
		tempKey = website + username + CheckUsrBalanceKeyB + todayStr;
        encryptKey = d.getMd5(tempKey);  
        key = "dsfredfsc" + encryptKey.toLowerCase() + "hdewsj";
        tagUrl = URL+"CheckUsrBalance?website=" + website + "&username=" + username + 
        		"&uppername=" + uppername 
        		//+ "&password=" + password 
        		+ "&key=" + key;
        logger.info("BBIN【查询余额】请求参数==========>"+tagUrl);
        String msg = sendGet(tagUrl);
		JSONObject json;
		json = JSONObject.fromObject(msg);
        logger.info("BBIN【查询余额】响应参数<=========="+json.toString());
		if (!"true".equals(json.get("result").toString().toLowerCase())) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();
			map.put("url", tagUrl);
			map.put("loginname", username); 
			map.put("msg", msg);
			map.put("Function", "CheckUsrBalance");
			f.setLog("BBIN", map);
		}
		return msg;
	}
	 
	public String Transfer(String username ,String password,String remitno,String action,String remit) {
		String encryptKey;
		String key;
		String tagUrl;
		Date now = new Date();
		DESEncrypt d=new DESEncrypt("");  
		java.util.Calendar Cal=java.util.Calendar.getInstance();   
		Cal.setTime(now);   
		Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);   
		String todayStr=sdf.format(Cal.getTime());     
		tempKey = website + username + remitno + TransferKeyB + todayStr;
        encryptKey = d.getMd5(tempKey);  
        key = "ds" + encryptKey.toLowerCase() + "hjedsxc";
        tagUrl = URL+"Transfer?website=" + website 
                + "&username=" + username 
                + "&uppername=" + uppername
                + "&remitno=" + remitno
                + "&action=" + action
                + "&remit=" + remit 
                + "&key=" + key;
        logger.info("BBIN【转账】请求参数==========>"+tagUrl);
        String msg = sendGet(tagUrl);
		JSONObject json;
		json = JSONObject.fromObject(msg);
        logger.info("BBIN【转账】响应参数<=========="+json.toString());
		if (!"true".equals(json.get("result").toString().toLowerCase())) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();
			map.put("url", tagUrl);
			map.put("loginname", username); 
			map.put("msg", msg);
			map.put("remitno", remitno);
			map.put("action", action);
			map.put("remit", remit);
			map.put("Function", "Transfer");
			f.setLog("BBIN", map);
		}
		return msg;
	}

	public String newTransfer(String username ,String password,String remitno,String action,String remit) {
		String encryptKey;
		String key;
		String tagUrl;
		Date now = new Date();
		DESEncrypt d=new DESEncrypt("");
		java.util.Calendar Cal=java.util.Calendar.getInstance();
		Cal.setTime(now);
		Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);
		String todayStr=sdf.format(Cal.getTime());
		tempKey = website + username + remitno + TransferKeyB + todayStr;
		encryptKey = d.getMd5(tempKey);
		key = "ds" + encryptKey.toLowerCase() + "hjedsxc";
		tagUrl = URL+"Transfer?website=" + website
				+ "&username=" + username
				+ "&uppername=" + uppername
				+ "&remitno=" + remitno
				+ "&action=" + action
				+ "&remit=" + remit
				+ "&key=" + key;
		logger.info("BBIN【转账】请求参数==========>"+tagUrl);
		String msg = sendGet(tagUrl);
		if(msg.equals("error")) return  "error";
		JSONObject json = JSONObject.fromObject(msg);
		if(json == null){
			logger.info("BBIN上下分请求成功,第三方服务无响应结果!");
			//请求第三方返回无结果
			return "process";
		}
		logger.info("BBIN【转账】响应参数<=========="+json.toString());
		JSONObject  data =  JSONObject.fromObject(json.get("data"));
		logger.info("BBIN服务上下分响应结果:  data{}"+ data.toString());
		if ("true".equals(json.get("result").toString().toLowerCase()) && data.get("Code").toString().equals("11100")){
			logger.info("BBIN 上下分请求成功  响应状态码:{}"+ data.get("Code").toString());
			//请求成功，
			return "success";
		}
		logger.info("BBIN 上下分请求成功  响应状态码:{}"+ data.get("Code").toString());
		return  "faild";
	}
	 
	public String BetRecord(String rounddate,String username ,String password) {
		
		String starttime = "00:00:00";
        String endtime = "23:59:59";
        String gamekind = "5";
        String gametype = "";
        String page = "1";
        String pagelimit = "100";
		
		String encryptKey;
		String key;
		String tagUrl;
		Date now = new Date();
		DESEncrypt d=new DESEncrypt("");  
		java.util.Calendar Cal=java.util.Calendar.getInstance();   
		Cal.setTime(now);   
		Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);   
		String todayStr=sdf.format(Cal.getTime());     
		 tempKey = website + username + BetRecordKeyB + todayStr;
         encryptKey = d.getMd5(tempKey);  
         key = "d" + encryptKey.toLowerCase() + "hdewsqj"; 
        tagUrl = URL+"BetRecord?website=" + website 
                + "&username=" + username 
                + "&uppername=" + uppername
                + "&rounddate=" + rounddate
                + "&starttime=" + starttime
                + "&endtime=" + endtime
                + "&gamekind=" + gamekind
                + "&subgamekind=2"
                + "&gametype=" + gametype
                + "&page=" + page
                + "&pagelimit=" + pagelimit
                + "&key=" + key;
		return sendGet(tagUrl);
	}
	 
		public String BetRecord3() {
		
		String startDay3 = "2015/09/02";
	    String endDay3 = "2015/09/02";
	    String starttime3 = "00:01:00";
	    String endtime3 = "00:05:00";
	    String gamekind3 = "3";
	    String gametype3 = "";
	    String page3 = "1";
		
		String encryptKey;
		String key;
		String tagUrl;
		Date now = new Date();
		DESEncrypt d=new DESEncrypt("");  
		java.util.Calendar Cal=java.util.Calendar.getInstance();   
		Cal.setTime(now);   
		Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);   
		String todayStr=sdf.format(Cal.getTime());     
		tempKey = website + BetRecordKeyB + todayStr;
        encryptKey = d.getMd5(tempKey);  
        key = "d" + encryptKey.toLowerCase() + "hdewsqj";
        tagUrl = URL+"BetRecordByModifiedDate3?website=" + website
                + "&uppername=" + uppername
                + "&start_date=" + startDay3
                + "&end_date=" + endDay3
                + "&starttime=" + starttime3
                + "&endtime=" + endtime3
                + "&gamekind=" + gamekind3
                + "&page=" + page3
                + "&key=" + key
                + "&gametype=" + gametype3;
		return sendGet(tagUrl);
	}
		
		/**   
	     * 发送xml请求到server端   
	     * @param tagUrl 请求数据地址   
	     * @return null发送失败，否则返回响应内容   
	     */      
		public static String sendGet(String tagUrl){        
			CloseableHttpClient httpclient = HttpClients.createDefault();
			String responseString="";
	        try {  
	            HttpGet httpget = new HttpGet(tagUrl);
				CloseableHttpResponse response = httpclient.execute(httpget);
	            try {
	            	int status = response.getStatusLine().getStatusCode();
	            	logger.info("[BBIN] HTTPGET请求响应状态码：{}",status);
	            	if(status == HttpStatus.SC_OK){
						HttpEntity entity = response.getEntity();
						logger.info("[BBIN] 发起HTTP请求响应实体：{}",entity.toString());

						if (entity != null) {
							responseString=EntityUtils.toString(entity);
						}
						return responseString;
					}
					 if(String.valueOf(status).startsWith("2")) return  "";return  "error";

	            } finally {
	                response.close();
	                httpget.releaseConnection();
	            }
	        }  catch (Exception e) {
	            logger.info("[BBIN] HTTP请求异常：",e);
	            if(e.getMessage().equals("Read timed out")) return ""; return "error";
	        } finally {
	            // 关闭连接,释放资源
	            try {
	                httpclient.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }

	    }
		
		//确认转账状态
		public Boolean checkTransfer(String billno,String username){  
		   	 //登录创建账号
			String encryptKey;
			String key;
			String tagUrl;
			Date now = new Date();
			DESEncrypt d=new DESEncrypt("");  
			java.util.Calendar Cal=java.util.Calendar.getInstance();   
			Cal.setTime(now);   
			Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);   
			String todayStr=sdf.format(Cal.getTime());  
			tempKey = website  + CheckTransferKeyB + todayStr;
	        encryptKey = d.getMd5(tempKey);  
	        key = "dsxad" + encryptKey.toLowerCase() + "weka";
	        tagUrl = URL+"CheckTransfer?website="+website+"&transid="+billno+"&key="+key; 
	        String msg = sendGet(tagUrl);
			JSONObject json;
			json = JSONObject.fromObject(msg);
			
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>(); 
			map.put("url", tagUrl);
			map.put("msg", json.getString("result")); 
			map.put("Function", "CheckTransfer");
			map.put("Exception","");
			map.put("responseString", msg);
			f.setLog("BBIN", map);  
			
			if(json==null||json.get("result")==null||json.getBoolean("result")){
		      return false;   
		   }else if(json.get("data")!=null&&"1".equals(json.getJSONObject("data").get("Status"))){
			   return true;
		   }
			return false;
		}
		
		public Boolean newCheckTransfer(String billno,String username){
	        //登录创建账号
	        DESEncrypt d=new DESEncrypt("");
	        java.util.Calendar Cal=java.util.Calendar.getInstance();
	        Cal.add(java.util.Calendar.HOUR_OF_DAY,-12);
	        String todayStr=sdf.format(Cal.getTime());
	        tempKey = website  + CheckTransferKeyB + todayStr;
	        String encryptKey = d.getMd5(tempKey);
	        String  key = "dsxad" + encryptKey.toLowerCase() + "weka";
	        String tagUrl = URL+"CheckTransfer?website="+website+"&transid="+billno+"&key="+key;
	        String msg = sendGet(tagUrl);
	        //msg等于空账字符(请求错误)则直接抛异常出去
	        if (msg == null){
	            //第三方服务器午响应
	            logger.info("BBIN 请求查询用户订单无响应  订单编号: {} "+ billno);
	            return false;
	        }
	        JSONObject json;
	        json = JSONObject.fromObject(msg);

	        if(!json.get("result").toString().equals("true")){
	            //查询请求成功，响应处理失败
	            logger.info("BBIN 天下科技发起查询订单状态请求，响应失败,请求响应结果：{}"+json.toString());
	            return  false;
	        }
	        JSONObject jsonData = JSONObject.fromObject(json.get("data"));
	        //定单查询结果为-1 处理中或失败  由上层轮询三次查询，三次结果都为-1视为失败
	        if (json.get("result").toString().equals("true") && jsonData.get("Status").toString().equals("1")){
	            logger.info("BBIN 响应查询请求成功，响应结果：{}"+json.toString());
	            return  true;

	        }
	        return  false;

	    }

}
