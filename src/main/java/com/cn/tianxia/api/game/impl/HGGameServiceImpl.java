package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;
import com.cn.tianxia.api.utils.SecurityUtil;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName HGServiceImpl
 * @Description 皇冠体育
 * @author Hardy
 * @Date 2019年2月9日 下午4:29:33
 * @version 1.0.0
 */
public class HGGameServiceImpl{

	private static final Logger logger = LoggerFactory.getLogger(HGGameServiceImpl.class);
	
	private String PublicKey;
	private String PrivateKey;
	private String DefaultCompanyCode;
	private String DefaultPrefixCode;
	private String PassAccessKey;
	private String onlinekey;
	private String apiurl;
	private String loginurl;
	private String mobileloginurl;

	
	public HGGameServiceImpl(Map<String, String> pmap) {
		PlatFromConfig pf=new PlatFromConfig();
		pf.InitData(pmap, "HG");
		JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
		PublicKey=jo.getString("PublicKey").toString();
		PrivateKey=jo.getString("PrivateKey").toString();
		DefaultCompanyCode=jo.getString("DefaultCompanyCode").toString();
		DefaultPrefixCode=jo.getString("DefaultPrefixCode").toString();
		PassAccessKey=jo.getString("PassAccessKey").toString();
		onlinekey=jo.getString("onlinekey").toString();
		apiurl=jo.getString("apiurl").toString();
		loginurl=jo.getString("loginurl").toString();
		mobileloginurl=jo.getString("mobileloginurl").toString();
	}
	
	public String getLogin(String username,String model){
		username=username.toLowerCase();
		Map<String,String> map =new HashMap<>(); 
		SecurityUtil s=new SecurityUtil();
		String data="ACTIVE_KEY|"+PassAccessKey+"|"+DefaultPrefixCode+"|"+DefaultPrefixCode+username+"|RMB|"+onlinekey+"=@=";  
		map.put("data", data); 
		try {
			data=s.encrypt(data, PrivateKey+DefaultPrefixCode+username);
			data+="^"+DefaultPrefixCode+username;
			map.put("data1", data); 
			data=s.encrypt(data, PublicKey);
			data+="^"+DefaultPrefixCode+username;
			String url=apiurl+"/soccer_api_sys_v2/get-access-key.php?r=";
			map.put("data2", (url+data)); 
			data=readContentFromGet(url+data);
			Document doc =  DocumentHelper.parseText(data);
			Element root = doc.getRootElement();
			if("success".equals(root.getName())){ 
				Iterator<Element> iterator = root.elementIterator();  
		        while(iterator.hasNext()){  
		            Element e = iterator.next();  
		            if("access_key".equals(e.getName())){
		            	String loginurls="";
		            	if(!"MB".equals(model)){
		            		 loginurls=loginurl+"?activekey="+e.getTextTrim()+"&acc="+DefaultPrefixCode+username+"&langs=2";		            		
		            	}else{
		            		 loginurls=mobileloginurl+"?activekey="+e.getTextTrim()+"&acc="+DefaultPrefixCode+username+"&langs=2";		
		            	}
		            	return loginurls;
		            }
		        }  
				return "success";
			}else{
				FileLog f=new FileLog();  
				map.put("username", username); 
				map.put("msg", data); 
				map.put("Function", "getLogin");
				f.setLog("HG", map);
				return "error";
			}
		} catch (Exception e) {
			FileLog f=new FileLog(); 
			map.put("username", username);  
			map.put("msg", "HttpURLConnection Error"); 
			map.put("Function", "WITHDRAW");
			f.setLog("HG", map);
			return "error";
		}   
	}
	
	public String getBalance(String username){
		username=username.toLowerCase();
		SecurityUtil s=new SecurityUtil();
		Map<String,String> map =new HashMap<>(); 
		String data="GET_CCL|"+PassAccessKey+"|"+DefaultPrefixCode+username+"|"+onlinekey+"=@=";  
		//System.out.println(data);
		map.put("data", data); 
		try {
			data=s.encrypt(data, PrivateKey+DefaultPrefixCode+username);
			data+="^"+DefaultPrefixCode+username;
			//System.out.println(data);
			map.put("data1", data); 
			data=s.encrypt(data, PublicKey);
			data+="^"+DefaultPrefixCode+username;
			String url=apiurl+"/soccer_api_sys_v2/get-wallet-ccl.php?r=";
			map.put("data2", url+data); 
			//System.out.println(data);
			data=readContentFromGet(url+data);
			//System.out.println(data);
			Document doc =  DocumentHelper.parseText(data);
			Element root = doc.getRootElement();
			if("success".equals(root.getName())){ 
				Iterator<Element> iterator = root.elementIterator();  
		        while(iterator.hasNext()){  
		            Element e = iterator.next();  
		            if("credit_left".equals(e.getName())){ 
		            	return e.getTextTrim();
		            }
		        }  
				return "success";
			}else{
				FileLog f=new FileLog();  
				map.put("username", username); 
				map.put("msg", data); 
				map.put("Function", "getBalance");
				f.setLog("HG", map);
				return "error";
			}
		} catch (Exception e) {
			FileLog f=new FileLog();  
			map.put("username", username);  
			map.put("msg", "HttpURLConnection Error"); 
			map.put("Function", "WITHDRAW");
			f.setLog("HG", map);
			return "error";
		}  
	}
	
	public String DEPOSIT(String username,String billno,String amount){
		username=username.toLowerCase();
		SecurityUtil s=new SecurityUtil();
		Map<String,String> map =new HashMap<>(); 
		String data="GET_DEPOSIT|"+PassAccessKey+"|"+billno+"|"+DefaultPrefixCode+username+"|RMB|"+amount+"|"+onlinekey+"=@=";  
		map.put("data", data); 
		try {
			data=s.encrypt(data, PrivateKey+DefaultPrefixCode+username);
			data+="^"+DefaultPrefixCode+username+"^"+username+PublicKey;
			map.put("data1", data); 
			data=s.encrypt(data, PublicKey);
			String url=apiurl+"/soccer_api_sys_v2/get_wallet_deposit.php?r=";
			map.put("data2", url+data); 
			data=readContentFromGet(url+data);
			Document doc =  DocumentHelper.parseText(data);
			Element root = doc.getRootElement();
			logger.info("HG 发起游戏Http转账请求响应报文:{}",root.toString());
			if("success".equals(root.getName())){  
				return "success";
			}else{
				FileLog f=new FileLog();  
				map.put("username", username); 
				map.put("billno", billno); 
				map.put("amount", amount); 
				map.put("msg", data); 
				map.put("Function", "DEPOSIT");
				f.setLog("HG", map);
				return "error";
			}
		} catch (Exception e) {
			FileLog f=new FileLog();  
			map.put("username", username);  
			map.put("msg", "HttpURLConnection Error"); 
			map.put("Function", "WITHDRAW");
			f.setLog("HG", map);
			return "error";
		}  
	}
	
	public String WITHDRAW(String username,String billno,String amount){
		username=username.toLowerCase();
		SecurityUtil s=new SecurityUtil();
		Map<String,String> map =new HashMap<>(); 
		String data="GET_WITHDRAW|"+PassAccessKey+"|"+billno+"|"+DefaultPrefixCode+username+"|RMB|"+amount+"|"+onlinekey+"=@=";  
		map.put("data", data); 
		try {
			data=s.encrypt(data, PrivateKey+DefaultPrefixCode+username);
			data+="^"+DefaultPrefixCode+username+"^"+username+PublicKey;
			map.put("data1", data); 
			data=s.encrypt(data,PublicKey);
			//data+="^"+DefaultPrefixCode+username;
			String url=apiurl+"/soccer_api_sys_v2/get_wallet_withdraw.php?r=";
			map.put("data2", url+data); 
			data=readContentFromGet(url+data);
			Document doc =  DocumentHelper.parseText(data);
			Element root = doc.getRootElement();
			if("success".equals(root.getName())){  
				return "success";
			}else{
				FileLog f=new FileLog(); 
				
				map.put("username", username); 
				map.put("billno", billno); 
				map.put("amount", amount); 
				map.put("msg", data); 
				map.put("Function", "WITHDRAW");
				f.setLog("HG", map);
				return "error";
			}
		} catch (Exception e) {
			FileLog f=new FileLog();  
			map.put("username", username);  
			map.put("msg", "HttpURLConnection Error"); 
			map.put("Function", "WITHDRAW");
			f.setLog("HG", map);
			return "error";
		}  
	}
	
	/**
	 * 获取即时注单
	 * @param latesttid : 0 or latest_tid value
	 * @return
	 */
	public String GET_INSTANT_DATA(String latesttid){
		SecurityUtil s=new SecurityUtil();
		String data="GET_INSTANT_DATA|"+PassAccessKey+"|1|"+latesttid+"|"+onlinekey+"=@=";  
		//System.out.println(data);
		try {
			data=s.encrypt(data, PrivateKey+PassAccessKey);
			//System.out.println(data);
			data+="^"+PassAccessKey+"^"+PassAccessKey+PublicKey;
			//System.out.println(data);
			data=s.encrypt(data, PublicKey); 
			//System.out.println(data);
			String url=apiurl+"/soccer_api_sys_v2/get-instant-data.php?r=";
			//System.out.println(url);
			data=readContentFromGet(url+data);
			//System.out.println(data);
			Document doc =  DocumentHelper.parseText(data);
			Element root = doc.getRootElement(); 
			return data; 
		} catch (Exception e) {
		}
		return data;  
	}
	
	public static void main(String[] args) {
		HGGameServiceImpl h=new HGGameServiceImpl(null);
		Document doc = null;
		String username="tx00001018";
		String data="";
		data=h.GET_INSTANT_DATA("1");
		//System.out.println(data);  
		//data=h.WITHDRAW(username, "1234567890008", "1"); 
		//data=h.DEPOSIT(username, "1234567890007", "1"); 
		//String data=h(username, "1234567890001", "1");
		//data=h.getBalance(username);
		////System.out.println(data);  
	} 
	
	public String readContentFromGet(String getURL) throws IOException {
		// 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编码 
		URL getUrl = new URL(getURL);
		// 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
		// 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
		HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
		// 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
		connection.setRequestProperty("Accept-encoding", "gzip");
		// 服务器
		connection.connect();
		// 取得输入流，并使用Reader读取
		GZIPInputStream in = new GZIPInputStream(connection.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));// 设置编码,否则中文乱码
		StringBuffer str= new StringBuffer();
		String lines;
		while ((lines = reader.readLine()) != null) { 
			str.append(lines); 
		}
		reader.close();
		// 断开连接
		connection.disconnect(); 
		
		return str.toString();
	}

}
