package com.cn.tianxia.api.game.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName PTGameServiceImpl
 * @Description PT游戏接口实现类
 * @author Hardy
 * @Date 2019年2月9日 下午4:36:46
 * @version 1.0.0
 */
public class PTGameServiceImpl{
	
    private static final Logger logger = LoggerFactory.getLogger(PTGameServiceImpl.class);
    
	private static String _prefix ;
	private static String _kisokName ;
	private static String _adminName ;
	private static String _entityKey ;
	private static String _sslPassword;
	private static String _apiUrl;
	
	public PTGameServiceImpl(Map<String, String> pmap) {
		PlatFromConfig pf=new PlatFromConfig();
		pf.InitData(pmap, "PT");
		JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
		_prefix=jo.getString("_prefix").toString();
		_kisokName=jo.getString("_kisokName").toString();
		_adminName=jo.getString("_adminName").toString();
		_entityKey=jo.getString("_entityKey").toString();
		_sslPassword=jo.getString("_sslPassword").toString();
		_apiUrl =jo.getString("_apiUrl").toString();
	}
	
	
	public String CreatePlayer(String username, String password) { 
		String returnresult="";
		int msg;
	       try{ 
	    	   String url = _apiUrl + "/player/create/playername/"+username
	    			   +"/adminname/"+_adminName
	    			   +"/kioskname/"+_kisokName
	    			   +"/custom02/"+_kisokName
	    			   +"/password/"+password
	    			   +"/trackingid/"+_kisokName;  
	           returnresult = CallAPI(url); 
	           msg=returnresult.indexOf("New player has been created");
	           if(msg<0){
	        	   msg=returnresult.indexOf("19");
	        	   if(msg<0){
					FileLog f=new FileLog(); 
					Map<String,String> map =new HashMap<>();  
					map.put("url", url);
					map.put("msg", returnresult);
					map.put("Function", "CreatePlayer"); 
					f.setLog("PT", map); 
					returnresult="error";
					return returnresult;  
	        	   }
	           }
	           returnresult="success";
	           return returnresult;  
	       } 
	       catch (Exception ex)  {  
	           return returnresult; 
	       } 
	   } 
	
	public String UpdateUser(String username, String password) { 
		String returnresult="";
	       try{ 
	    	   String url = _apiUrl+"/player/update/playername/"+username 
	    			   +"/password/"+password;
	           returnresult = CallAPI(url);
	           if(returnresult.indexOf("errorcode")>0){
	        	   FileLog f=new FileLog(); 
					Map<String,String> map =new HashMap<>();  
					map.put("url", url);
					map.put("msg", returnresult);
					map.put("Function", "UpdateUser"); 
					f.setLog("PT", map);
	           } 
	           return returnresult;  
	       } 
	       catch (Exception ex)  {  
	           return returnresult; 
	       } 
	   }
	
	public String GetPlayerInfo(String username) { 
		String returnresult="";
	       try{ 
	    	   String url = _apiUrl+"/player/info/playername/"+username;
	           returnresult = CallAPI(url);
	           if(returnresult.indexOf("errorcode")>0){
	        	   FileLog f=new FileLog(); 
					Map<String,String> map =new HashMap<>();  
					map.put("url", url);
					map.put("msg", returnresult);
					map.put("Function", "GetPlayerInfo"); 
					f.setLog("PT", map); 
	           } 
	           return returnresult;  
	       } 
	       catch (Exception ex)  {  
	           return returnresult; 
	       } 
	   } 
	
	public String Loginout(String username) { 
		String returnresult="";
	       try{ 
	    	   String url = _apiUrl+"/player/logout/playername/"+username;
	           returnresult = CallAPI(url);
	           if(returnresult.indexOf("errorcode")>0){
	        	   FileLog f=new FileLog(); 
					Map<String,String> map =new HashMap<>();  
					map.put("url", url);
					map.put("msg", returnresult);
					map.put("Function", "Loginout"); 
					f.setLog("PT", map); 
	           } 
	           return returnresult;  
	       } 
	       catch (Exception ex)  {  
	           return returnresult; 
	       } 
	   }
	
	public String Logingame(String gameCode) {  
    	   String url = "http://cache.download.banner.happypenguin88.com/casinoclient.html?game="+gameCode
    			   +"code}&language=zh"; 
           return url;   
	   }
	
	
	public String Deposit(String username,String amount,String billno) { 
		String returnresult="";
	       try{ 
	    	   String url = _apiUrl+"/player/deposit/playername/"+username
	    			   +"/amount/"+amount
	    			   +"/adminname/"+_adminName
	    			   +"/externaltranid/"+billno;
			   logger.info("PT 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（上分）请求链接:{}", url);
	           returnresult = CallAPI(url);
			   logger.info("PT 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（上分）返回结果:{}", returnresult);
	           if(returnresult.indexOf("errorcode")>0){
	        	   FileLog f=new FileLog(); 
					Map<String,String> map =new HashMap<>();  
					map.put("url", url);
					map.put("msg", returnresult);
					map.put("Function", "Deposit"); 
					f.setLog("PT", map); 
	           } 
	           return returnresult;  
	       } 
	       catch (Exception ex)  {  
	           return returnresult; 
	       } 
	   }
	
	public String Withdraw(String username,String amount,String billno) { 
			String returnresult="";
		       try{ 
		    	   String url = _apiUrl+"/player/withdraw/playername/"+username
		    			   +"/amount/"+amount
		    			   +"/adminname/"+_adminName
		    			   +"/externaltranid/"+billno
		    			   +"/isForce/1";
				   logger.info("PT 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（下分）请求链接:{}", url);
				   returnresult = CallAPI(url);
				   logger.info("PT 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（下分）返回结果:{}", returnresult);
		           if(returnresult.indexOf("errorcode")>0){
		        	   FileLog f=new FileLog(); 
						Map<String,String> map =new HashMap<>();  
						map.put("url", url);
						map.put("msg", returnresult);
						map.put("Function", "Withdraw"); 
						f.setLog("PT", map); 
		           } 
		           return returnresult;  
		       } 
		       catch (Exception ex)  {  
		           return returnresult; 
		       } 
		   }
	
	public String CheckTransaction(String billno) { 
		String returnresult="";
	       try{ 
	    	   String url = _apiUrl+"/player/checktransaction/externaltranid/"+billno;
	           returnresult = CallAPI(url);
	           if(returnresult.indexOf("errorcode")>0){
	        	   FileLog f=new FileLog(); 
					Map<String,String> map =new HashMap<>();  
					map.put("url", url);
					map.put("msg", returnresult);
					map.put("Function", "CheckTransaction"); 
					f.setLog("PT", map); 
	           } 
	           return returnresult;  
	       } 
	       catch (Exception ex)  {  
	           return returnresult; 
	       } 
	   }
	
	public String getTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		df.setTimeZone(TimeZone.getTimeZone("GMT+0")); 
		return df.format(new Date());// new Date()为获取当前系统时间		
	}
	
	@SuppressWarnings("finally")
    private String CallAPI(String url) {
		HttpsURLConnection connection = null;
		String resp="";
		try { 
			KeyStore ks = KeyStore.getInstance("PKCS12"); 
			// get certificate file from test/resources as InputFileStream &
			// load to existing keystore
			
			Properties pro = new Properties();
			InputStream in;
			try {
				in = this.getClass().getResourceAsStream("/file.properties");
				pro.load(in);
			} catch (Exception e) { 
			}
			String keysrc =pro.getProperty("keysrc"); 
			URL fileURL = new File(keysrc).toURI().toURL();
			File file = new File(fileURL.getFile());

			FileInputStream fis = new FileInputStream(file);
			ks.load(fis, _sslPassword.toCharArray());

			// Create KeyManagerFactory using loaded keystore
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, _sslPassword.toCharArray());
			KeyManager[] kms = kmf.getKeyManagers();

			// Crete TrustManager to bypass trusted certificate check
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}

			} };

			// Hostname verification bypass method
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Set connection properties to use bypass certificate/hostname
			// check methods
			SSLContext sslContext = null;
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kms, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			// Send API call together with entity key for validation
			connection = (HttpsURLConnection) new URL(url).openConnection();
			connection.setRequestProperty("X_ENTITY_KEY", _entityKey); 
			InputStream response = connection.getInputStream();
			resp = IOUtils.toString(response);
			//System.out.println(resp);
			
		} catch (Exception e) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();  
			map.put("url", url);
			map.put("msg", "API调用出错");
			map.put("Function", "CallAPI");
			map.put("Exception", e.getMessage());
			f.setLog("PT", map);
			e.printStackTrace();
		}finally{
			connection.disconnect(); 
			return resp;
		}
	}

}
