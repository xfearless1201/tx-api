package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.game.OBService;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;
import com.cn.tianxia.api.utils.TripleDES;

import net.sf.json.JSONObject;

public class OBGameServiceImpl implements OBService {
	/*private static String ALLBET_DES_KEY = "zVfrekEyVtYbhd3VZJ+HzVN3oNNpcN1h";
    private static String ALLBET_MD5_KEY = "8K0OJBeHwxL3SFdo9taP14MYhGtLb9BDz3hcLYSDvuY=";
    private static String ALLBET_PROPERTY_ID = "3968714";
    private static String ALLBET_API_URL = "https://api3.abgapi.net"; 
    private static String agent = "ryuy7a";  //代理号
    private static String vipHandicaps="12";
    private static String orHandicaps="11";
    private static String orHallRebate="0"; 
*/    
	private static final Logger logger = LoggerFactory.getLogger(OBGameServiceImpl.class);
	private static String ALLBET_DES_KEY;
    private static String ALLBET_MD5_KEY ;
    private static String ALLBET_PROPERTY_ID;
    private static String ALLBET_API_URL;
    private static String agent;
    private static String vipHandicaps;
    private static String orHandicaps;
    private static String orHallRebate;
    private static String hz="txw";
    
    public OBGameServiceImpl(Map<String, String> pmap) {
		PlatFromConfig pf=new PlatFromConfig();
		pf.InitData(pmap, "OB");
		JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
		ALLBET_DES_KEY=jo.getString("ALLBET_DES_KEY").toString();
		ALLBET_MD5_KEY=jo.getString("ALLBET_MD5_KEY").toString();
		ALLBET_PROPERTY_ID=jo.getString("ALLBET_PROPERTY_ID").toString();
		ALLBET_API_URL=jo.getString("ALLBET_API_URL").toString();
		agent=jo.getString("agent").toString();
		vipHandicaps=jo.getString("vipHandicaps").toString();
		orHandicaps=jo.getString("orHandicaps").toString();
		orHallRebate=jo.getString("orHallRebate").toString();
    }
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.OBService#queryhandicap()
	 */
    @Override
	public String queryhandicap() {
    	String cmd_str = "/query_handicap";
        String realParam = "random="+new SecureRandom().nextLong()+"&agent=" + agent;   
        String msg =sendPost(cmd_str, realParam);
        JSONObject json = new JSONObject();
        json = json.fromObject(msg);
		if (!"OK".equals(json.getString("error_code"))) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>(); 
			map.put("apiurl", ALLBET_API_URL+cmd_str);
			map.put("data", realParam); 
			map.put("msg", msg); 
			map.put("Function", "queryhandicap");
			f.setLog("OB", map);  
		}
		return msg;
	}
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.OBService#check_or_create(java.lang.String, java.lang.String)
	 */
    @Override
	public String check_or_create(String username,String password) {
    	String cmd_str = "/check_or_create";
        String realParam = "random=" +new SecureRandom().nextLong()+ "&agent=" + agent;
        realParam += "&client="+username+"&password="+password+"&vipHandicaps="+vipHandicaps+"&orHandicaps="+orHandicaps+"&orHallRebate="+orHallRebate;
        String msg =sendPost(cmd_str, realParam);
        if(msg.indexOf("CLIENT_EXIST")>0||msg.indexOf("OK")>0){
        	return "success";
        }else{
        	FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>(); 
			map.put("apiurl", ALLBET_API_URL+cmd_str);
			map.put("data", realParam); 
			map.put("msg", msg); 
			map.put("Function", "check_or_create");
			f.setLog("OB", map);  
        } 
		return "faild";
	}
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.OBService#logout_game(java.lang.String)
	 */
    @Override
	public String logout_game(String username) {
    	String cmd_str = "/logout_game";
        String realParam = "random=" +new SecureRandom().nextLong()+ "&client="+username; 
        String msg =sendPost(cmd_str, realParam);
        JSONObject json = new JSONObject();
        json = json.fromObject(msg);
		if (!"OK".equals(json.getString("error_code"))) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>(); 
			map.put("apiurl", ALLBET_API_URL+cmd_str);
			map.put("data", realParam); 
			map.put("msg", msg); 
			map.put("Function", "logout_game");
			f.setLog("OB", map);  
		}
		return msg;
	}
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.OBService#get_balance(java.lang.String, java.lang.String)
	 */
    @Override
	public String get_balance(String username,String password) {
    	String cmd_str = "/get_balance";
        String realParam = "random=" +new SecureRandom().nextLong()+ "&client="+username+"&password="+password; 
        String msg =sendPost(cmd_str, realParam);
		logger.info("OG 用户【"+ username +"】 ,发起查询余额返回结果:{}", msg);
        JSONObject json = new JSONObject();
        json = json.fromObject(msg);
		if (!"OK".equals(json.getString("error_code"))) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>(); 
			map.put("apiurl", ALLBET_API_URL+cmd_str);
			map.put("data", realParam); 
			map.put("msg", msg); 
			map.put("Function", "get_balance");
			f.setLog("OB", map);  
		}
		return msg;
	}
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.OBService#agent_client_transfer(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public String agent_client_transfer(String username,String billno,String openflag,String credit) {
    	String cmd_str = "/agent_client_transfer";
        String realParam = "random=" +new SecureRandom().nextLong()+ "&agent=" + agent
        		+ "&sn=" +ALLBET_PROPERTY_ID+ billno + "&client="+username+"&operFlag="+openflag+"&credit="+credit;
		logger.info("OB 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（上下分）请求参数:{}", realParam);
        String msg =sendPost(cmd_str, realParam);
		logger.info("OB 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（上下分）响应结果:{}", msg);
        JSONObject json = JSONObject.fromObject(msg);
		if (!"OK".equals(json.getString("error_code"))) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>(); 
			map.put("apiurl", ALLBET_API_URL+cmd_str);
			map.put("data", realParam); 
			map.put("msg", msg); 
			map.put("Function", "agent_client_transfer");
			f.setLog("OB", map);  
		}
		return msg;
	}
  
    @Override
    public String queryOrder(long random,String sn) {
        String cmd_str = "/query_transfer_state";
        String realParam = "random=" +random+ "&sn=" + sn;
        String msg =sendPost(cmd_str, realParam);
        JSONObject json = new JSONObject();
        json = json.fromObject(msg);
        if (json.getInt("transferState")!=1) {
            FileLog f=new FileLog(); 
            Map<String,String> map =new HashMap<>(); 
            map.put("apiurl", ALLBET_API_URL+cmd_str);
            map.put("data", realParam); 
            map.put("msg", msg); 
            map.put("Function", "query_transfer_state");
            f.setLog("OB", map);  
        }
        return msg;
    }
    
   /* (non-Javadoc)
 * @see com.cn.tianxia.service.OBService#forward_game(java.lang.String, java.lang.String)
 *
 *                    http://www.allbetgaming.net/h5
 */
    @Override
	public String forward_game(String username,String password,String model) {
    	String cmd_str = "/forward_game";
        String realParam = "random=" +new SecureRandom().nextLong()
        		+ "&client="+username+"&password="+password;
		if ("mobile".equals(model)){
			 realParam = "random=" +new SecureRandom().nextLong()
					+ "&client="+username+"&password="+password+"&targetSite="+"http://www.allbetgaming.net/h5/";
		}
        String msg =sendPost(cmd_str, realParam);
        JSONObject json = new JSONObject();
        json = json.fromObject(msg);
		if (!"OK".equals(json.getString("error_code"))) {
			FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>(); 
			map.put("apiurl", ALLBET_API_URL+cmd_str);
			map.put("data", realParam); 
			map.put("msg", msg); 
			map.put("Function", "forward_game");
			f.setLog("OB", map);  
		}
		return msg;
	}
    
    /**   
     * 发送请求到server端   
     * @param fname 请求数据地址
     * @param queryString 发送的数据流   
     * @return null发送失败，否则返回响应内容   
     */      
	public static String sendPost(String fname,String queryString){         
        //创建httpclient工具对象     
        HttpClient client = new HttpClient();      
        //创建post请求方法     
        PostMethod myPost = new PostMethod(ALLBET_API_URL+fname);       
        //设置请求超时时间      
        String responseString = null;      
        try{       
        	String data = TripleDES.encrypt(queryString, ALLBET_DES_KEY, null);
        	myPost.setParameter("propertyId", ALLBET_PROPERTY_ID);
        	myPost.setParameter("data", data);
        	myPost.setParameter("sign", Base64.encodeBase64String(DigestUtils.md5((data+ALLBET_MD5_KEY)))); 
        	client.setConnectionTimeout(20*1000);    
            client.setTimeout(20*1000);
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
            	responseString=myPost.getResponseBodyAsString();
            	if(responseString.indexOf("CLIENT_EXIST")<0){
            		FileLog f=new FileLog(); 
        			Map<String,String> map =new HashMap<>(); 
        			map.put("apiurl", ALLBET_API_URL+fname);
        			map.put("data", queryString); 
        			map.put("statusCode", statusCode+""); 
        			map.put("msg", responseString);  
        			map.put("Function", "sendPost");
        			f.setLog("OB", map);  
            	}
            	
            }
        }catch (Exception e) {   
            e.printStackTrace();      
        }finally{  
             myPost.releaseConnection();
             client.getHttpConnectionManager().closeIdleConnections(0);
        }   
        return responseString;      
    }
}
