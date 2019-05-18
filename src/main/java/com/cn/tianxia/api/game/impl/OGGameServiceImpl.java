package com.cn.tianxia.api.game.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.game.OGService;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;
 
public class OGGameServiceImpl implements OGService {
	static private char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=" .toCharArray();  
	
	private static final Logger logger = LoggerFactory.getLogger(OGGameServiceImpl.class);
	
	/*String agent = "dailishang";
    String UserKey = "1111"; 
    String testUrl = "http://cashapi.673ing.com/cashapi/DoBusiness.aspx"; */
    //测试用cashapi.673ing.com 正式用cashapi.n80tu2.com
	
	String agent;
    String UserKey;
    String testUrl;
    String transferUrl;  //转账接口
    
    public OGGameServiceImpl(Map<String, String> pmap) {
		PlatFromConfig pf=new PlatFromConfig();
		pf.InitData(pmap, "OG");
		JSONObject jo=JSONObject.fromObject(pf.getPlatform_config());
		agent=jo.getString("agent").toString();
		UserKey=jo.getString("UserKey").toString();
		testUrl=jo.getString("testUrl").toString();
		transferUrl = jo.getString("transferUrl").toString();
    }
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.OGService#CheckMem(java.lang.String, java.lang.String)
	 */
    @Override
	public String CheckMem(String username,String password){
    	String reRS ="";
        String  paramstr = "";
        String key = "";
		
        paramstr = "agent=" + agent + "$username=" + username + "$password=" + password + "$method=caie";
        try {
			paramstr = new String(encode(paramstr.getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        key = getMd5(paramstr + UserKey);
        reRS = sendGet(testUrl + "?params=" + paramstr + "&key=" + key);
        if(reRS.indexOf("<result>1</result>")<0){
        	FileLog f=new FileLog(); 
    		Map<String,String> map =new HashMap<>(); 
    		map.put("apiurl", testUrl);
    		map.put("data", paramstr); 
    		map.put("msg", reRS); 
    		map.put("Function", "CheckMem");
    		f.setLog("OG", map);  
        }
        
        return reRS;
    }
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.OGService#Logingame(java.lang.String, java.lang.String)
	 */
    @Override
	public String Logingame(String username,String password,String gameID){
    	String reRS ="";
        String  paramstr = "";
        String key = "";
        
        if("mobile".equals(gameID)){
        	paramstr = "agent=" + agent + "$username=" + username + "$password=" + password +
             		"$domain=video.ss838.com$iframe=0$gametype=21$gamekind=0$method=tg$platformname=oriental";
        }else{
        	 paramstr = "agent=" + agent + "$username=" + username + "$password=" + password +
             		"$domain=video.ss838.com$iframe=0$gametype=1$gamekind=0$method=tg$platformname=oriental";
        }
		 
        try {
			paramstr = new String(encode(paramstr.getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        key = getMd5(paramstr + UserKey);
        reRS = testUrl + "?params=" + paramstr + "&key=" + key;
        
        return reRS;
    }
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.OGService#getBalance(java.lang.String, java.lang.String)
	 */
    @Override
	public String getBalance(String username,String password){
    	String reRS ="";
        String  paramstr = "";
        String key = "";
		
        paramstr = "agent=" + agent + "$username=" + username + "$password=" + password + "$method=gb";
        try {
			paramstr = new String(encode(paramstr.getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        key = getMd5(paramstr + UserKey);
        reRS = sendGet(testUrl + "?params=" + paramstr + "&key=" + key);
		logger.info("OG 用户【"+ username +"】 ,发起查询余额接口返回结果:{}", reRS);
        return reRS;
    }
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.OGService#CreateMem(java.lang.String, java.lang.String)
	 */
    @Override
	public String CreateMem(String username,String password){
    	String reRS ="";
        String  paramstr = "";
        String key = "";
		
        paramstr = "agent=" + agent + "$username=" + username + "$password=" + password + 
        		"$limit=1,1,1,1,1,1,1,1,1,1,1,1,1,1$limitvideo=38$limitroulette=5$method=caca";
        try {
			paramstr = new String(encode(paramstr.getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        key = getMd5(paramstr + UserKey);
        reRS = sendGet(testUrl + "?params=" + paramstr + "&key=" + key);
        if(reRS.indexOf("<result>1</result>")<0){
        	FileLog f=new FileLog(); 
    		Map<String,String> map =new HashMap<>(); 
    		map.put("apiurl", testUrl);
    		map.put("data", paramstr); 
    		map.put("msg", reRS); 
    		map.put("Function", "CreateMem");
    		f.setLog("OG", map);  
        }
        return reRS;
    }
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.OGService#DEPOSIT(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public String DEPOSIT(String username,String password,String billno,String credit){
    	String reRS ="";
        String  paramstr = "";
        String key = "";
        
        paramstr = "agent=" + agent + "$username=" + username + "$password=" + password + 
        		"$billno=" + billno + "$type=IN$credit="+credit+"" + "$method=ptc";
        try {
			paramstr = new String(encode(paramstr.getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		logger.info("OG 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（上分）请求参数:{}", paramstr);
		key = getMd5(paramstr + UserKey);
		reRS = sendGet(transferUrl + "?params=" + paramstr + "&key=" + key);
		logger.info("OG 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（上分）返回结果:{}", reRS);
        
        //System.out.println(reRS);
        if (reRS.indexOf("<result>") == -1){
        	//特殊情况，比如返回404等等
        	try {
				Thread.sleep(5000);
				 paramstr = "agent=" + agent + "$username=" + username + "$password=" + password + "$billno=" + billno +
						 "$type=IN$credit="+credit+"$flag=1" + "$method=ctc";
					paramstr = new String(encode(paramstr.getBytes("utf-8"))); 
			        key = getMd5(paramstr + UserKey);
			        reRS = sendGet(transferUrl + "?params=" + paramstr + "&key=" + key);  
			        
			        try {
			        	Document doc  = DocumentHelper.parseText(reRS);
						Element root = doc.getRootElement();
						String result=root.attributeValue("result");  
						if("1".equals(result)){
							return "success";
						}else{
							FileLog f=new FileLog(); 
			    			Map<String,String> map =new HashMap<>(); 
			    			map.put("apiurl", transferUrl);
			        		map.put("data", paramstr); 
			    			map.put("msg", reRS);  
			    			map.put("Function", "DEPOSIT");
			    			f.setLog("OG", map);  
							return "faild";
						}
					} catch (DocumentException e) { 
						e.printStackTrace();
					}  
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        }else{
        	String str=reRS.substring(reRS.indexOf("<result>")+8,reRS.indexOf("</result>")); 
        	//System.out.println(str);
        	if("1".equals(str)){
				return "success";
			}else{
				FileLog f=new FileLog(); 
    			Map<String,String> map =new HashMap<>(); 
    			map.put("apiurl", transferUrl);
        		map.put("data", paramstr); 
    			map.put("msg", reRS);  
    			map.put("Function", "DEPOSIT");
    			f.setLog("OG", map);  
				return "faild";
			} 
        }
        
        return "faild";
    }
    
    /* (non-Javadoc)
	 * @see com.cn.tianxia.service.OGService#WITHDRAW(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public String WITHDRAW(String username,String password,String billno,String credit){
    	String reRS ="";
        String  paramstr = "";
        String key = "";
		
        paramstr = "agent=" + agent + "$username=" + username + "$password=" + password + 
        		"$billno=" + billno + "$type=OUT$credit="+credit+"" + "$method=ptc";
        try {
			paramstr = new String(encode(paramstr.getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("OG 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（下分）请求参数:{}", paramstr);
        key = getMd5(paramstr + UserKey);
        reRS = sendGet(transferUrl + "?params=" + paramstr + "&key=" + key);
		logger.info("OG 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（下分）返回结果:{}", reRS);
        //System.out.println(reRS);
        if (reRS.indexOf("<result>") == -1){
        	//特殊情况，比如返回404等等
        	try {
				Thread.sleep(5000);
				 paramstr = "agent=" + agent + "$username=" + username + "$password=" + password + "$billno=" + billno +
						 "$type=OUT$credit="+credit+"$flag=1" + "$method=ctc";
					paramstr = new String(encode(paramstr.getBytes("utf-8"))); 
			        key = getMd5(paramstr + UserKey);
			        reRS = sendGet(transferUrl + "?params=" + paramstr + "&key=" + key);  
			        
			        try {
			        	Document doc  = DocumentHelper.parseText(reRS);
						Element root = doc.getRootElement();
						String result=root.attributeValue("result");  
						if("1".equals(result)){
							return "success";
						}else{
							FileLog f=new FileLog(); 
			    			Map<String,String> map =new HashMap<>(); 
			    			map.put("apiurl", transferUrl);
			        		map.put("data", paramstr); 
			    			map.put("msg", reRS);  
			    			map.put("Function", "WITHDRAW");
			    			f.setLog("OG", map);  
							return "faild";
						}
					} catch (DocumentException e) { 
						e.printStackTrace();
					}  
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        }else{
        	String str=reRS.substring(reRS.indexOf("<result>")+8,reRS.indexOf("</result>")); 
        	//System.out.println(str);
        	if("1".equals(str)){
				return "success";
			}else{
				FileLog f=new FileLog(); 
    			Map<String,String> map =new HashMap<>(); 
    			map.put("apiurl", transferUrl);
        		map.put("data", paramstr); 
    			map.put("msg", reRS);  
    			map.put("Function", "WITHDRAW");
    			f.setLog("OG", map);  
				return "faild";
			} 
        } 
        return "faild";
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
            // 创建httpget.    
            HttpGet httpget = new HttpGet(tagUrl);
            //System.out.println("executing request " + httpget.getURI());  
            // 执行get请求.    
            CloseableHttpResponse response = httpclient.execute(httpget);  
            try {  
                // 获取响应实体    
                HttpEntity entity = response.getEntity(); 
                 
                //System.out.println("--------------------------------------");  
                // 打印响应状态    
                //System.out.println(response.getStatusLine());  
                if (entity != null) {  
                    // 打印响应内容    
                    ////System.out.println("Response content: " + EntityUtils.toString(entity));  
                    responseString=EntityUtils.toString(entity);
                }  
                //System.out.println("------------------------------------");  
            } finally {  
                response.close();  
                httpget.releaseConnection();
            }  
        } catch (ClientProtocolException e) {  
        	FileLog f=new FileLog(); 
			Map<String,String> map =new HashMap<>();  
			map.put("tagUrl", tagUrl); 
			map.put("msg", "CloseableHttpClient_Error");  
			map.put("Function", "sendGet");
			f.setLog("OG", map);  
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
	
	
	//静态方法，便于作为工具类  
    public static String getMd5(String plainText) {  
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.update(plainText.getBytes());  
            byte b[] = md.digest();  
  
            int i;  
  
            StringBuffer buf = new StringBuffer("");  
            for (int offset = 0; offset < b.length; offset++) {  
                i = b[offset];  
                if (i < 0)  
                    i += 256;  
                if (i < 16)  
                    buf.append("0");  
                buf.append(Integer.toHexString(i));  
            }  
            //32位加密  
            return buf.toString();  
            // 16位的加密  
            //return buf.toString().substring(8, 24);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
            return null;  
        }  
  
    }
    
    static public char[] encode(byte[] data) {  
        char[] out = new char[((data.length + 2) / 3) * 4];  
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {  
            boolean quad = false;  
            boolean trip = false;  
            int val = (0xFF & (int) data[i]);  
            val <<= 8;  
            if ((i + 1) < data.length) {  
                val |= (0xFF & (int) data[i + 1]);  
                trip = true;  
            }  
            val <<= 8;  
            if ((i + 2) < data.length) {  
                val |= (0xFF & (int) data[i + 2]);  
                quad = true;  
            }  
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];  
            val >>= 6;  
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];  
            val >>= 6;  
            out[index + 1] = alphabet[val & 0x3F];  
            val >>= 6;  
            out[index + 0] = alphabet[val & 0x3F];  
        }  
        return out;  
    }
    
    
	/**
	 * 查询订单
	 * @param userName
	 * @param password
	 * @param billno
	 * @param inOut
	 * @return
	 * @throws Exception 
	 */
	public String orderQuery(String userName,String password,String billno,String inOut) throws Exception{
        String paramstr = "agent=" + agent + "$username=" + userName + "$password=" + password + 
        		"$billno=" + billno + "$type="+inOut+ "$method=ctc";
			paramstr = new String(encode(paramstr.getBytes("utf-8")));
		String key = getMd5(paramstr + UserKey);
		String reRS = sendGet(transferUrl + "?params=" + paramstr + "&key=" + key);
		return reRS;
	}	
}
