package com.cn.tianxia.api.game.impl;
 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject; 

/**
 * 功能概要：AGIN试玩实现类
 *  
 */ 
public class AGINFreeServiceImpl{ 
	/*private static String api_url = "http://gi.tianxgame.com:81/doBusiness.do?";
	private static String api_url_game = "http://gci.tianxgame.com:81/forwardGame.do?";
	private static String api_deskey="uR7R44Ni";
	private static String api_md5key="8XSW0SVZPp0X";
	private static String api_cagent="S76_AGIN" */
	
	private static String api_url ;
	private static String api_url_game ;
	private static String api_deskey;
	private static String api_md5key;
	private static String api_cagent;
	private static String actype="0";

	private final static Logger logger = LoggerFactory.getLogger(AGINFreeServiceImpl.class);
	
	public AGINFreeServiceImpl(Map<String, String> pmap,String gameKey) {
		PlatFromConfig pf=new PlatFromConfig();
		pf.InitData(pmap, gameKey);
		JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
		api_url=jo.getString("api_url").toString();
		api_url_game=jo.getString("api_url_game").toString();
		api_deskey=jo.getString("api_deskey").toString();
		api_md5key=jo.getString("api_md5key").toString();
		api_cagent=jo.getString("api_cagent").toString(); 
	}
	
	
	/**
	 * 检测并创建游戏账号
	 */
	
	public String CheckOrCreateGameAccout(String loginname,  String password,
			String oddtype, String cur) {  
		String xmlString=""; 
		Document doc = null;
		xmlString="cagent="+api_cagent+"/\\\\/loginname="+loginname+"/\\\\/method=lg/\\\\/actype="+actype
				+"/\\\\/password="+password+"/\\\\/oddtype="+oddtype+"/\\\\/cur="+cur;
        String tagUrl=getAGUrl(api_url,xmlString);
        logger.info("AGINFree【检测并创建游戏账号】请求参数==========>"+tagUrl);
        xmlString=sendPost(api_cagent, tagUrl);
		String info="";
		String msg="";
		try {
			doc = DocumentHelper.parseText(xmlString);
			Element root = doc.getRootElement();
			info=root.attributeValue("info"); 
			msg=root.attributeValue("msg"); 
		} catch (DocumentException e) { 
			e.printStackTrace();
		}

		logger.info("AGINFree【检测并创建游戏账号】响应参数<=========="+info);
		
		if("error".equals(info)){
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();
			map.put("tagUrl", "tagUrl"); 
			map.put("loginname", loginname);
			map.put("actype", actype);
			map.put("oddtype", oddtype);
			map.put("msg", xmlString);
			map.put("Function", "CheckOrCreateGameAccout");
			f.setLog(api_cagent, map);
			return msg;
		}else{
			return info;			
		}
	}
	/**
	 * 查询余额
	 */
	
	public String GetBalance(String loginname,  String password,String cur) { 
		String xmlString=""; 
		Document doc = null;
		xmlString="cagent="+api_cagent+"/\\\\/loginname="+loginname+"/\\\\/method=gb/\\\\/actype="+actype
				+"/\\\\/password="+password+"/\\\\/cur="+cur;
		String tagUrl=getAGUrl(api_url,xmlString);
        logger.info("AGINFree【查询余额】请求参数==========>"+tagUrl);
		xmlString=sendPost(api_cagent, tagUrl);
		String info="";
		String msg="";
		try {
			doc = DocumentHelper.parseText(xmlString);
			Element root = doc.getRootElement();
			info=root.attributeValue("info"); 
			msg=root.attributeValue("msg"); 
		} catch (DocumentException e) { 
			e.printStackTrace();
		}
        logger.info("AGINFree【查询余额】响应参数<=========="+info);
		if("error".equals(info)){
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();
			map.put("tagUrl", "tagUrl"); 
			map.put("loginname", loginname);
			map.put("actype", actype);
			map.put("msg", msg);
			map.put("Function", "GetBalance");
			f.setLog(api_cagent, map);
			return msg;
		}else{
			return info;			
		}
	}
	/**
	 * 预备转账
	 */
	
	public String PrepareTransferCredit(String loginname, String billno, String type,
			String credit,  String password, String cur) { 
		String xmlString=""; 
		Document doc = null;
		xmlString="cagent="+api_cagent+"/\\\\/method=tc/\\\\/loginname="+loginname+"/\\\\/billno="+billno
				+ "/\\\\/type="+type+"/\\\\/credit="+credit+"/\\\\/actype="+actype+"/\\\\/password="+password+"/\\\\/cur="+cur;
		String tagUrl=getAGUrl(api_url,xmlString);
        logger.info("AGINFree【预备转账】请求参数==========>"+tagUrl);
		xmlString=sendPost(api_cagent, tagUrl);
		String info="";
		String msg="";
		try {
			doc = DocumentHelper.parseText(xmlString);
			Element root = doc.getRootElement();
			info=root.attributeValue("info"); 
			msg=root.attributeValue("msg"); 
		} catch (DocumentException e) { 
			e.printStackTrace();
		}
        logger.info("AGINFree【预备转账】响应参数<=========="+info);
		if(!"0".equals(info)){
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();
			map.put("tagUrl", "tagUrl"); 
			map.put("loginname", loginname);
			map.put("actype", actype);
			map.put("billno", billno);
			map.put("type", type);
			map.put("credit", credit);
			map.put("msg", xmlString);
			map.put("Function", "PrepareTransferCredit");
			f.setLog(api_cagent, map);
			return msg;
		}else{
			return info;			
		}
	}
	/**
	 * 确认转账
	 */
	
	public String TransferCreditConfirm(String loginname, String billno, String type,
			String credit,  String flag, String password, String cur) { 
		String xmlString=""; 
		Document doc = null;
		xmlString="cagent="+api_cagent+"/\\\\/loginname="+loginname+"/\\\\/method=tcc/\\\\/billno="+billno
				+ "/\\\\/type="+type+"/\\\\/credit="+credit+"/\\\\/actype="+actype+"/\\\\/flag="+flag+"/\\\\/password="+password+"/\\\\/cur="+cur;
		String tagUrl=getAGUrl(api_url,xmlString);
        logger.info("AGINFree【确认转账】请求参数==========>"+tagUrl);
		xmlString=sendPost(api_cagent, tagUrl);
		String info="";
		String msg="";
		try {
			doc = DocumentHelper.parseText(xmlString);
			Element root = doc.getRootElement();
			info=root.attributeValue("info"); 
			msg=root.attributeValue("msg"); 
		} catch (DocumentException e) { 
			e.printStackTrace();
		}
        logger.info("AGINFree【确认转账】响应参数<=========="+info);
		if(!"0".equals(info)){
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();
			map.put("tagUrl", "tagUrl"); 
			map.put("loginname", loginname);
			map.put("actype", actype);
			map.put("billno", billno);
			map.put("type", type);
			map.put("credit", credit);
			map.put("msg", xmlString);
			map.put("Function", "TransferCreditConfirm");
			f.setLog(api_cagent, map);
			return msg;
		}else{
			return info;			
		}
	}
	/**
	 * 检查订单状态
	 */
	
	public String QueryOrderStatus(String billno,  String cur) {
		String xmlString=""; 
		Document doc = null;
		xmlString="cagent="+api_cagent+"/\\\\/billno="+billno+"/\\\\/method=qos"+
				 "/\\\\/actype="+actype+"/\\\\/cur="+cur;
		String tagUrl=getAGUrl(api_url,xmlString);
        logger.info("AGINFree【检查订单状态】请求参数==========>"+tagUrl);
		xmlString=sendPost(api_cagent, tagUrl);
		String info="";
		String msg="";
		try {
			doc = DocumentHelper.parseText(xmlString);
			Element root = doc.getRootElement();
			info=root.attributeValue("info"); 
			msg=root.attributeValue("msg"); 
		} catch (DocumentException e) { 
			e.printStackTrace();
		}
        logger.info("AGINFree【检查订单状态】响应参数<=========="+info);
		if("error".equals(info)){
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>(); 
			map.put("actype", actype);
			map.put("billno", billno); 
			map.put("msg", msg);
			map.put("Function", "QueryOrderStatus");
			f.setLog(api_cagent, map);
			return msg; 
		}else{
			return info;			
		}
	}
	/**
	 * 获取游戏跳转连接
	 */
	
	public String forwardGame(String loginname, String password, String dm, String sid, String gameType) { 
		String xmlString="";  
		xmlString="cagent="+api_cagent+"/\\\\/loginname="+loginname+"/\\\\/actype="+actype+"/\\\\/password="+password
			+"/\\\\/dm="+dm+"/\\\\/sid="+sid+"/\\\\/lang=1/\\\\/gameType="+gameType+"/\\\\/oddtype=A/\\\\/cur=CNY";
        logger.info("AGINFree【获取游戏跳转连接】请求参数==========>"+xmlString);
        xmlString=getAGUrl(api_url_game, xmlString);
        logger.info("AGINFree【获取游戏跳转连接】响应参数<=========="+xmlString);
		return xmlString;
	}
	
	/**
	 * 获取游戏跳转连接
	 */
	
	public String forwardMobileGame(String loginname, String password, String dm, String sid, String gameType) { 
		String xmlString="";  
		UUID uuid = UUID.randomUUID();
		xmlString="cagent="+api_cagent+"/\\\\/loginname="+loginname+"/\\\\/actype="+actype+"/\\\\/password="+password
			+"/\\\\/dm="+dm+"/\\\\/sid="+sid+"/\\\\/lang=1/\\\\/gameType="+gameType+"/\\\\/oddtype=A/\\\\/cur=CNY/\\\\/mh5=y/\\\\/session_token="+uuid.toString();
        logger.info("AGINFree【获取游戏跳转连接】请求参数==========>"+xmlString);
		xmlString=getAGUrl(api_url_game, xmlString);
        logger.info("AGINFree【获取游戏跳转连接】响应参数<=========="+xmlString);
		return xmlString;
	}
	
	
	/**
	 *功能描述: 发送xml请求到server端
	 *
	 *@Author: Wilson
	 *@Date: 2018年10月31日 16:20:28
	 * @param gtype
	* @param tagUrl
	 *@return: java.lang.String
	 **/
	public static String sendPost(String gtype,String tagUrl){        
        //创建httpclient工具对象     
        HttpClient client = new HttpClient();      
        //创建post请求方法     
        PostMethod myPost = new PostMethod(tagUrl);      
        myPost.addRequestHeader("User-Agent", "WEB_LIB_GI_"+gtype); 
        //设置请求超时时间     
        client.setConnectionTimeout(20*1000);    
        client.setTimeout(20*1000);
        String responseString = null;      
        try{      
            //设置请求头部类型     
            myPost.setRequestHeader("Content-Type","text/xml");    
            myPost.setRequestHeader("charset","utf-8");    
            //设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式      
            int statusCode = client.executeMethod(myPost);     
            //只有请求成功200了，才做处理  
            if(statusCode == HttpStatus.SC_OK){       
            	InputStream inputStream = myPost.getResponseBodyAsStream();  
            	BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));  
            	StringBuffer stringBuffer = new StringBuffer();  
            	String str= "";  
            	while((str = br.readLine()) != null){  
            	stringBuffer.append(str );  
            	}   
                responseString = stringBuffer.toString();
            }else{
            	FileLog f=new FileLog(); 
    			Map<String,String> map =new HashMap<>(); 
    			map.put("statusCode", statusCode+"");
    			map.put("ResponseBody", myPost.getResponseBodyAsString()); 
    			map.put("tagUrl", tagUrl);
    			map.put("Function", "sendPost");
    			f.setLog(gtype, map);
            }
        }catch (Exception e) {   
            e.printStackTrace();      
        }finally{  
             myPost.releaseConnection();   
        }  
        return responseString;      
    }
	
	public static String getAGUrl(String url,String xmlString){
		String param = "";
		String tagUrl = "";
		String key = "";
		DESEncrypt d = new DESEncrypt(api_deskey);
		try {
			param=d.encrypt(xmlString);
			key=d.getMd5(param+api_md5key);
		} catch (Exception e1) { 
			e1.printStackTrace();
		}
		tagUrl=url + "params=" + param + "&key=" + key; 
		return tagUrl;
	}
 

}
