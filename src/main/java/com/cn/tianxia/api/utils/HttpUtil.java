package com.cn.tianxia.api.utils;

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
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
 
@Service
public class HttpUtil {  
	
	private static String api_url = "http://gi.tianxgame.com:81/doBusiness.do?";
	private static String api_url_post = "http://gci.tianxgame.com:81/forwardGame.do?";
	private static String api_deskey="uR7R44Ni";
	private static String api_md5key="8XSW0SVZPp0X";
	
    public static void main(String[] args) {   
    	/*String xmlstring="cagent=S76_AGIN/\\\\/loginname=tx197382645/\\\\/method=lg/\\\\/actype=0/\\\\/password=197382645/\\\\/oddtype=B/\\\\/cur=CNY";
    	String tagUrl=getAGUrl(api_url,"S76_AGIN",xmlstring);
    	xmlstring=sendPost("S76_AGIN",tagUrl);

    	String msg="";
    	Document doc = null;
		try {
			doc = DocumentHelper.parseText(xmlstring);
			Element root = doc.getRootElement();
			msg=root.attributeValue("info"); 
			
		} catch (DocumentException e) { 
			e.printStackTrace();
		}
    	
    	//System.out.println(msg);*/
    	
    	sendGet( "http://888.worldgameapi.com/app/WebService/JSON/display.php/Login?website=LWIN999&username=user1&uppername=dtxg13&password=abc123&lang=zh-cn&page_site=live&page_present=live&key=dsfreews51685aeaa6a9b2de00120eb45f0d0b03j");
    }
    
    /**   
     * 发送xml请求到server端   
     * @param url xml请求数据地址   
     * @param xmlString 发送的xml数据流   
     * @return null发送失败，否则返回响应内容   
     */      
	public static String sendPost(String cagent,String tagUrl){        
        //创建httpclient工具对象     
        HttpClient client = new HttpClient();      
        //创建post请求方法     
        PostMethod myPost = new PostMethod(tagUrl);      
        myPost.addRequestHeader("User-Agent", "WEB_LIB_GI_"+cagent); 
        //设置请求超时时间     
        client.setConnectionTimeout(3000*1000);    
        client.setTimeout(30*1000);
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
            }      
        }catch (Exception e) {   
            e.printStackTrace();      
        }finally{  
             myPost.releaseConnection();   
        }  
        //System.out.println(responseString);
        return responseString;      
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
                    // 打印响应内容长度    
                    //System.out.println("Response content length: " + entity.getContentLength());  
                    // 打印响应内容    
                    //System.out.println("Response content: " + EntityUtils.toString(entity));  
                    //responseString=EntityUtils.toString(entity);
                }  
                //System.out.println("------------------------------------");  
            }catch (Exception e) {
				e.printStackTrace();
			} finally {  
                response.close();  
                httpget.releaseConnection();
            }  
        } catch (ClientProtocolException e) {  
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
	
	public static String getAGUrl(String url,String cagent,String xmlString){
		String param = "";
		String tagUrl = "";
		String key = "";
		DESEncrypt d = new DESEncrypt(api_deskey);
		try {
			param=d.encrypt(xmlString);
			key=d.getMd5(param+api_md5key);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		tagUrl=url + "params=" + param + "&key=" + key;
		//System.out.println(tagUrl);
		return tagUrl;
	}
	
	public static String getaa(String url,String cagent,String xmlString){ 
		String tagUrl = ""; 
		 
		
		
		return tagUrl;
	}
}  
