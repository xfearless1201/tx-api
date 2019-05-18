package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.game.SBService;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;
 

public class SBGameServiceImpl implements SBService {
	
    private static final Logger logger = LoggerFactory.getLogger(SBGameServiceImpl.class);
    
    String api_token;
    String api_authorize;
    String api_deauthorize;
    String api_credit;
    String api_debit;
    String api_balance;
    String api_gamelist;
    String client_id;
    String client_secret;
    String grant_type;
    String scope;
    String lobby_url;
    String api_url;
    String is_test;
    String is_testplayer;//20180531
    public SBGameServiceImpl(Map<String, String> pmap) {
		PlatFromConfig pf=new PlatFromConfig();
		pf.InitData(pmap, "SB");
		JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config()); 
		client_id=jo.getString("client_id").toString();
		client_secret=jo.getString("client_secret").toString();
		grant_type=jo.getString("grant_type").toString();
		scope=jo.getString("scope").toString();
		lobby_url=jo.getString("lobby_url").toString();
		api_url=jo.getString("api_url").toString(); 
		try{
			is_test=jo.getString("is_test").toString();
		}catch(Exception e){
			is_test="0";
		}
		try{
		    is_testplayer=jo.getString("is_testplayer").toString();
		}catch(Exception e)
		{
		    is_testplayer="false";
		}
		api_token =api_url+"/api/oauth/token";   //获取token
		api_authorize =api_url+"/api/player/authorize";  //用户授权
		api_deauthorize =api_url+"/api/player/deauthorize";  //用户取消授权
		api_credit =api_url+"/api/wallet/credit";  //转账
		api_debit =api_url+"/api/wallet/debit";  //转账
		api_balance=api_url+"/api/player/balance";
		api_gamelist =api_url+"/api/games?lang=zh-CN&platformtype=0";   //获取游戏列表
    }
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.SBService#getAccToken()
	 */
    @Override
	public String getAccToken() {
    	String data_token = "grant_type="+grant_type+"&scope="+scope+"&client_id="+client_id+"&client_secret="+client_secret;

    	//创建httpclient工具对象
        HttpClient client = new HttpClient();
        //创建post请求方法
        PostMethod myPost = new PostMethod(api_token);
//        链接超时
        client.getHttpConnectionManager().getParams().setConnectionTimeout(10*1000);
//        读取超时
        client.getHttpConnectionManager().getParams().setSoTimeout(10*1000);
        String responseString = null;
        try{
            //设置请求头部类型
    		myPost.addRequestHeader("Content-Type","application/x-www-form-urlencoded");
    		myPost.setRequestBody(data_token);

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
    			map.put("url", api_token);
    			map.put("data", data_token);
    			map.put("statusCode", statusCode+"");
    			map.put("msg", myPost.getResponseBodyAsString());
    			map.put("Function", "getAccToken");
    			f.setLog("SHENBO", map);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
             myPost.releaseConnection();
             client.getHttpConnectionManager().closeIdleConnections(0);
        }
        //System.out.println(responseString);
        return responseString;
	}

    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.SBService#getUserToken(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public String getUserToken(String ip,String username,String userid,String acctoken,String handicap,String model) {
		String platformtype = "0";
		if("MB".equalsIgnoreCase(model) || "".equals(model)){
			platformtype = "1";
		}
    	 //创建httpclient工具对象     
        HttpClient client = new HttpClient();      
        //创建post请求方法     
        PostMethod myPost = new PostMethod(api_authorize);      
        //设置请求超时时间     
        String responseString = null;        
        try{      
            //设置请求头部类型      
    		myPost.addRequestHeader("Accept","application/json");  
    		myPost.addRequestHeader("Authorization","Bearer "+acctoken);     
    		myPost.addRequestHeader("Content-Type","application/x-www-form-urlencoded"); 
            myPost.setParameter("ipaddress", ip);
            myPost.setParameter("username", username);
            myPost.setParameter("userid", userid);
            myPost.setParameter("lang", "zh-CN");
            myPost.setParameter("cur", "RMB");
            myPost.setParameter("betlimitid", handicap);
            myPost.setParameter("platformtype", platformtype);
//            myPost.setParameter("istestplayer", "false");   
            myPost.setParameter("istestplayer", is_testplayer);
            //设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式      
            int statusCode = client.executeMethod(myPost);     
            //只有请求成功200了，才做处理  
            //System.out.println(statusCode);
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
    			map.put("url", api_authorize);
    			map.put("data", myPost.getParameters().toString());
    			map.put("statusCode", statusCode+"");
    			map.put("msg", myPost.getResponseBodyAsString());
    			map.put("Function", "getUserToken");
    			f.setLog("SHENBO", map);  
            }
        }catch (Exception e) {   
            e.printStackTrace();      
        }finally{  
             myPost.releaseConnection();   
        }   
        //System.out.println(responseString);
        return responseString;       
	}
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.SBService#getBalance(java.lang.String, java.lang.String)
	 */
    @Override
	public String getBalance(String userid,String token) {   
   	 
    	CloseableHttpClient httpclient = HttpClients.createDefault(); 
		String responseString="";
        try {  
            // 创建httpget.
			logger.info("SB 用户【"+ userid +"】 , 发起查询余额请求");
            HttpGet httpget = new HttpGet(api_balance+"?cur=RMB&userid="+userid);
            httpget.setHeader("Authorization", "Bearer "+token);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);  
            try {  
                // 获取响应实体    
                HttpEntity entity = response.getEntity();
                if (entity != null) {  
                    // 打印响应内容
                    responseString=EntityUtils.toString(entity);
                }
				logger.info("SB 用户【"+ userid +"】 , 发起查询余额响应结果;{}",responseString);
            } finally {
                response.close();  
                httpget.releaseConnection();
            }  
        } catch (ClientProtocolException e) {  
        	FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();   
			map.put("url", api_balance);
			map.put("data","?cur=RMB&userid="+userid);
			map.put("msg", "CloseableHttpClientError");
			map.put("Function", "getBalance");
			f.setLog("SHENBO", map);  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭连接,释放资源    
            try {   
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        } 
       return responseString;
	}
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.SBService#WalletCredit(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public String WalletCredit(String userid,String billno,String amt,String timestamp,String acctoken) {   
   	 //创建httpclient工具对象     
       HttpClient client = new HttpClient();      
       //创建post请求方法     
       PostMethod myPost = new PostMethod(api_credit);      
       //设置请求超时时间     
       String responseString = null;        
       try{      
           //设置请求头部类型      
   		myPost.addRequestHeader("Accept","application/json");  
   		myPost.addRequestHeader("Authorization","Bearer "+acctoken);     
   		myPost.addRequestHeader("Content-Type","application/x-www-form-urlencoded");  
           myPost.setParameter("userid", userid);
           myPost.setParameter("amt", amt);
           myPost.setParameter("cur", "RMB");
           myPost.setParameter("txid",billno); 
           myPost.setParameter("timestamp",timestamp);
		   logger.info("SB 用户【"+ userid +"】 ,订单编号【"+ billno +"】 发起第三方转账（上分）请求参数：{}", Arrays.toString(myPost.getParameters()));
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
			   logger.info("SB 用户【"+ userid +"】 ,订单编号【"+ billno +"】 发起第三方转账（上分）响应结果：{}", responseString);
           }else{
        	   FileLog f=new FileLog(); 
   				Map<String,String> map =new HashMap<>();   
   				map.put("url", api_credit);
   				map.put("data",myPost.getParameters().toString());
   				map.put("statusCode", statusCode+"");
   				map.put("msg", myPost.getResponseBodyAsString());
   				map.put("Function", "WalletCredit");
   				f.setLog("SHENBO", map);   
           }
       }catch (Exception e) {   
           e.printStackTrace();      
       }finally{  
            myPost.releaseConnection();   
       }   
       return responseString;
	}

    @Override
	public String WalletDebit(String userid,String billno,String amt,String timestamp,String acctoken) {   
      	 //创建httpclient工具对象     
          HttpClient client = new HttpClient();      
          //创建post请求方法     
          PostMethod myPost = new PostMethod(api_debit);      
          //设置请求超时时间     
          String responseString = null;        
          try{
              //设置请求头部类型
			  myPost.addRequestHeader("Accept","application/json");
			  myPost.addRequestHeader("Authorization","Bearer "+acctoken);
			  myPost.addRequestHeader("Content-Type","application/x-www-form-urlencoded");
			  myPost.setParameter("userid", userid);
			  myPost.setParameter("amt", amt);
			  myPost.setParameter("cur", "RMB");
			  myPost.setParameter("txid",billno);
			  myPost.setParameter("timestamp",timestamp);

			  logger.info("SB 用户【"+ userid +"】 ,订单编号【"+ billno +"】 发起第三方转账（下分）请求参数：{}", Arrays.toString(myPost.getParameters()));
			  //设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
              int statusCode = client.executeMethod(myPost);     
              //只有请求成功200了，才做处理  
              //System.out.println(statusCode);
              if(statusCode == HttpStatus.SC_OK){       
              	InputStream inputStream = myPost.getResponseBodyAsStream();  
              	BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));  
              	StringBuffer stringBuffer = new StringBuffer();  
              	String str= "";  
              	while((str = br.readLine()) != null){  
              	stringBuffer.append(str );  
              	}   
                  responseString = stringBuffer.toString();
				  logger.info("SB 用户【"+ userid +"】 ,订单编号【"+ billno +"】 发起第三方转账（下分）响应结果：{}", responseString);
              }else{
            	  FileLog f=new FileLog(); 
     				Map<String,String> map =new HashMap<>();   
     				map.put("url", api_debit);
       				map.put("data",myPost.getParameters().toString());
     				map.put("statusCode", statusCode+"");
     				map.put("msg", myPost.getResponseBodyAsString());
     				map.put("Function", "WalletDebit");
     				f.setLog("SHENBO", map);   
              }
          }catch (Exception e) {   
              e.printStackTrace();      
          }finally{  
               myPost.releaseConnection();   
          }   
          return responseString;
   	}

    @Override
	public String getGameUrl(String usertoken,String gameID) {
    	String data_authorize = "";
    	if("1".endsWith(is_test)){
    		if("1".equals(gameID)){
        		data_authorize=lobby_url+"/SBlobbyT5?token="+usertoken;
        	} else if("2".equals(gameID)){
        		data_authorize=lobby_url+"/RTlobbyT5?token="+usertoken;
        	}else if("3".equals(gameID)){
        		data_authorize=lobby_url+"/SBmlobbyT5?token="+usertoken;
        	}else if("4".equals(gameID)){
        		data_authorize=lobby_url+"/RTmlobbyT5?token="+usertoken;
        	}
    	}else{
    		if("1".equals(gameID)){
        		data_authorize=lobby_url+"/SBlobby?token="+usertoken;
        	} else if("2".equals(gameID)){
        		data_authorize=lobby_url+"/RTlobby?token="+usertoken;
        	}else if("3".equals(gameID)){
        		data_authorize=lobby_url+"/SBmlobby?token="+usertoken;
        	}else if("4".equals(gameID)){
        		data_authorize=lobby_url+"/RTmlobby?token="+usertoken;
        	}
    	}
    	
    	return data_authorize;
	} 
}
