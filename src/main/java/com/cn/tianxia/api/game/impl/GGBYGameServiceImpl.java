package com.cn.tianxia.api.game.impl;
 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName GGBYServiceImpl
 * @Description GGBY游戏接口
 * @author Hardy
 * @Date 2019年2月9日 下午4:27:50
 * @version 1.0.0
 */
public class GGBYGameServiceImpl{
    //日志
	private static final Logger logger = LoggerFactory.getLogger(GGBYGameServiceImpl.class);
	
	private static String api_url ;
	private static String api_deskey;
	private static String api_md5key;
	private static String cagent;
	private static String actype;
	private static String ishttps="1";//正式环境
	
	public GGBYGameServiceImpl(Map<String, String> pmap) {
		PlatFromConfig pf=new PlatFromConfig();
		pf.InitData(pmap, "GGBY");
		JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
		api_url=jo.getString("api_url").toString();
		api_deskey=jo.getString("api_deskey").toString();
		api_md5key=jo.getString("api_md5key").toString();
		cagent=jo.getString("cagent").toString();
		actype=jo.getString("actype").toString();
		
		if(jo.containsKey("ishttps")){
			ishttps="0";//测试环境
		}
	}
	/**
	 * 检测并创建游戏账号
	 */
	
	public String CheckOrCreateGameAccout(String loginname,String password) {  
		String xmlString=""; 
		Document doc = null;
		xmlString="cagent="+cagent+"/\\\\/loginname="+loginname+"/\\\\/method=ca/\\\\/actype="+actype
				+"/\\\\/password="+password+"/\\\\/cur=CNY";
		String tagUrl=getAGUrl(api_url,xmlString);
		xmlString=sendGet( tagUrl);
		String info="";
		String msg="";
		JSONObject json=new JSONObject().fromObject(xmlString); 
			info=json.getString("code");
			msg=json.getString("msg");
		if(!"0".equals(info)){
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();
			map.put("tagUrl", tagUrl); 
			map.put("msg", json.toString());
			map.put("Function", "CheckOrCreateGameAccout");
			f.setLog("GGBY", map);
			return msg;
		}else{
			return info;			
		}
	}
	/**
	 * 查询余额
	 */
	
	public String GetBalance(String loginname, String password) { 
		String xmlString=""; 
		Document doc = null;
		xmlString="cagent="+cagent+"/\\\\/loginname="+loginname+"/\\\\/method=gb/\\\\/password="+password+"/\\\\/cur=CNY";
		String tagUrl=getAGUrl(api_url,xmlString);
		xmlString=sendGet(tagUrl);
		String info="";
		String msg="";
		String balance=""; 
		JSONObject json=new JSONObject().fromObject(xmlString); 
		info=json.getString("code");
		msg=json.getString("msg");
		if(!"0".equals(info)){
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();
			map.put("tagUrl", tagUrl); 
			map.put("msg", json.toString());
			map.put("Function", "GetBalance");
			f.setLog("GGBY", map);
			return "维护中";
		}else{
			balance=json.getString("dbalance");
			return balance;			
		}
	}
	/**
	 * 转账
	 */
	
	public String TransferCredit( String loginname, String billno, String credit, String type, String password,String ip) { 
		try{
			String xmlString=""; 
			Document doc = null;
			xmlString="cagent="+cagent+"/\\\\/method=tc/\\\\/loginname="+loginname+"/\\\\/billno="+cagent+billno
					+ "/\\\\/type="+type+"/\\\\/credit="+credit+"/\\\\/password="+password+"/\\\\/cur=CNY/\\\\/ip=="+ip;
			String tagUrl=getAGUrl(api_url,xmlString);
			xmlString=sendGet(tagUrl);
			String info="";
			String msg="";
			JSONObject json=new JSONObject().fromObject(xmlString);
			logger.info("[GGBY] 发起Http转账请求第三方响应报文：{}",json.toString());
				info=json.getString("code");
				msg=json.getString("msg");
			if(!"0".equals(info)){
				FileLog f=new FileLog(); 
				Map<String,String> map =new HashMap<>();
				map.put("tagUrl", tagUrl); 
				map.put("msg", json.toString());
				map.put("Function", "TransferCredit");
				f.setLog("GGBY", map);
				return "error";
			}
			return "success";   
		}catch(Exception e){
			return "error";
		}
	}
	/**
	 * 检查订单状态
	 */
	
	public String QueryOrderStatus(String billno) {
		try{ 
			String xmlString=""; 
			Document doc = null;
			xmlString="cagent="+cagent+"/\\\\/method=qx/\\\\/billno="+billno;
			String tagUrl=getAGUrl(api_url,xmlString);
			xmlString=sendGet( tagUrl);
			String info="";
			String msg="";
			JSONObject json=new JSONObject().fromObject(xmlString); 
				info=json.getString("code");
				msg=json.getString("msg");
			if(!"0".equals(info)){
				FileLog f=new FileLog(); 
				Map<String,String> map =new HashMap<>();  
				map.put("tagUrl", tagUrl); 
				map.put("msg", json.toString());
				map.put("Function", "QueryOrderStatus");
				f.setLog("GGBY", map); 
			}
			return info;			
		}catch(Exception e){
			return "-1";
		}
		
	}
	/**
	 * 获取游戏跳转连接
	 */
	
	public String forwardGame(String loginname, String password, String sid, String ip) { 
		String xmlString=""; 
		Document doc = null;
		xmlString="cagent="+cagent+"/\\\\/loginname="+loginname+"/\\\\/password="+password
			+"/\\\\/method=fw/\\\\/sid="+sid+"/\\\\/lang=zh-CN/\\\\/gametype=0/\\\\/ip="+ip+"/\\\\/ishttps="+ishttps; 
		String tagUrl=getAGUrl(api_url,xmlString); 
		xmlString=sendGet( tagUrl); 
		String info="";
		String msg="";
		String url="";
		JSONObject json=new JSONObject().fromObject(xmlString); 
			info=json.getString("code");
			msg=json.getString("msg");
		if(!"0".equals(info)){
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>(); 
			map.put("tagUrl", tagUrl); 
			map.put("msg", json.toString());
			map.put("Function", "forwardGame");
			f.setLog("GGBY", map);
			return msg;
		}else{
			url=json.getString("url");
			return url;			
		}
	}
	
	
	/**   
     * 发送xml请求到server端   
     * @param url xml请求数据地址   
     * @param xmlString 发送的xml数据流   
     * @return null发送失败，否则返回响应内容   
     */      
	public static String sendGet(String tagUrl){        
		URL url = null;
		HttpURLConnection httpConn = null;
		InputStream in = null;
		String responseString ="";
		try {
			url = new URL(tagUrl);
			httpConn = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			httpConn.setConnectTimeout(30000);
			httpConn.setReadTimeout(30000);
			httpConn.setRequestMethod("GET");
			httpConn.setRequestProperty("GGaming", "WEB_GG_GI_" + cagent);// cagent请参考上线说明,文件头为必传
			in = httpConn.getInputStream();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in));  
        	StringBuffer stringBuffer = new StringBuffer();  
        	String str= "";  
        	while((str = br.readLine()) != null){  
        	stringBuffer.append(str );  
        	}   
            responseString = stringBuffer.toString();
			return responseString;
		} catch (Exception e) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>(); 
			map.put("tagUrl", tagUrl); 
			map.put("msg", e.getMessage());
			map.put("Function", "sendGet");
			f.setLog("GGBY", map);  
			e.printStackTrace();
		} finally {
			try {
				httpConn.disconnect();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return responseString;
		}
    }
	
	public static String getAGUrl(String url,String xmlString){
		String param = "";
		String tagUrl = "";
		String key = "";
		//System.out.println(url + "params="+xmlString);
		DESEncrypt d = new DESEncrypt(api_deskey);
		try {
			param=d.encrypt(xmlString);
			key=d.getMd5(param+api_md5key);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		tagUrl=url + "params=" + param + "&key=" + key;
		//System.out.println("-----------GetURL------------"); 
		return tagUrl;
	}

}
