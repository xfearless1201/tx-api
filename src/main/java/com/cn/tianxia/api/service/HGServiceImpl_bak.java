package com.cn.tianxia.api.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;
import com.cn.tianxia.api.utils.SecurityUtil;

import net.sf.json.JSONObject;

public class HGServiceImpl_bak {

	/*private final String PublicKey="0099HJSmsd4hgJHSYGhfbhBDJDFBJD8890";	
	private final String PrivateKey="SCKEYhjshgsdgy37GHhsd567";	
	private final String DefaultCompanyCode="A0099";	
	private final String DefaultPrefixCode="x1d";	
	private final String PassAccessKey="MjSBcsS6kz";
	private final String onlinekey="22222";*/
	
	private String PublicKey;
	private String PrivateKey;
	private String DefaultCompanyCode;
	private String DefaultPrefixCode;
	private String PassAccessKey;
	private String onlinekey;
	
	public HGServiceImpl_bak(Map<String, String> pmap) {
		PlatFromConfig pf=new PlatFromConfig();
		pf.InitData(pmap, "HG");
		JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
		PublicKey=jo.getString("PublicKey").toString();
		PrivateKey=jo.getString("PrivateKey").toString();
		DefaultCompanyCode=jo.getString("DefaultCompanyCode").toString();
		DefaultPrefixCode=jo.getString("DefaultPrefixCode").toString();
		PassAccessKey=jo.getString("PassAccessKey").toString();
		onlinekey=jo.getString("onlinekey").toString();
	}
	
	public String getLogin(String username,String model){
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
			String url="http://api.uv128.com/A0099_0000/soccer_api_sys_v2/get-access-key.php?r=";
			map.put("data2", (url+data)); 
			data=readContentFromGet(url+data);
			Document doc =  DocumentHelper.parseText(data);
			Element root = doc.getRootElement();
			if("success".equals(root.getName())){ 
				Iterator<Element> iterator = root.elementIterator();  
		        while(iterator.hasNext()){  
		            Element e = iterator.next();  
		            if("access_key".equals(e.getName())){
		            	String loginurl="";
		            	if(!"MB".equals(model)){
		            		 loginurl="http://3.uv128.com/A0099_0050/d6/direct-login.php?activekey="+e.getTextTrim()+"&acc="+DefaultPrefixCode+username+"&langs=2";		            		
		            	}else{
		            		 loginurl="http://3.uv128.com/A0099_0050/mobile/direct-login.php?activekey="+e.getTextTrim()+"&acc="+DefaultPrefixCode+username+"&langs=2";		
		            	}
		            	return loginurl;
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
			String url="http://api.uv128.com/A0099_0000/soccer_api_sys_v2/get-wallet-ccl.php?r=";
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
		SecurityUtil s=new SecurityUtil();
		Map<String,String> map =new HashMap<>(); 
		String data="GET_DEPOSIT|"+PassAccessKey+"|"+billno+"|"+DefaultPrefixCode+username+"|RMB|"+amount+"|"+onlinekey+"=@=";  
		map.put("data", data); 
		try {
			data=s.encrypt(data, PrivateKey+DefaultPrefixCode+username);
			data+="^"+DefaultPrefixCode+username+"^"+username+PublicKey;
			map.put("data1", data); 
			data=s.encrypt(data, PublicKey);
			String url="http://api.uv128.com/A0099_0000/soccer_api_sys_v2/get_wallet_deposit.php?r=";
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
			String url="http://api.uv128.com/A0099_0000/soccer_api_sys_v2/get_wallet_withdraw.php?r=";
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
	
	public static void main(String[] args) {
		HGServiceImpl_bak h=new HGServiceImpl_bak(null);
		Document doc = null;
		String username="tx00001018";
		String data="";
		data=h.getLogin(username,"MB"); 
		//System.out.println(data);  
		//data=h.WITHDRAW(username, "1234567890008", "1"); 
		//data=h.DEPOSIT(username, "1234567890007", "1"); 
		//String data=h(username, "1234567890001", "1");
		data=h.getBalance(username);
		//System.out.println(data);  
	} 
	
	public String readContentFromGet(String getURL) throws IOException {
		// 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编码 
		URL getUrl = new URL(getURL);
		// 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
		// 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
		HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
		// 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
		// 服务器
		connection.connect();
		// 取得输入流，并使用Reader读取
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
		StringBuffer str= new StringBuffer();
		String lines;
		while ((lines = reader.readLine()) != null) {
			str.append( new String(lines.getBytes(), "utf-8")); 
		}
		reader.close();
		// 断开连接
		connection.disconnect();
		return str.toString();
	}
}
