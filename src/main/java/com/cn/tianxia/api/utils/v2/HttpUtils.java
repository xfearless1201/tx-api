package com.cn.tianxia.api.utils.v2;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @ClassName HttpUtils
 * @Description httpclient 工具类
 * @author Hardy
 * @Date 2018年9月12日 下午12:25:34
 * @version 1.0.0
 */
@SuppressWarnings("rawtypes")
public class HttpUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    
    /**
     * 功能描述:
     * GET请求
     * @Author: Hardy
     * @Date: 2018年09月10日 18:57:26
     * @param url
     * @return: java.lang.String
     **/
    public static String get(String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            //创建HTTPCLIENT链接对象
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Content-Type", "application/json");
            CloseableHttpResponse response = httpclient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),Consts.UTF_8));
                StringBuffer sb = new StringBuffer();
                String content = null;
                while((content = reader.readLine()) != null){
                    sb.append(content);
                }
                return sb.toString();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("发起HTTP-GET请求异常!");
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    
    /**
     * 
     * @Description post请求
     * @param url
     * @param connectionTimeout 连接超时时间
     * @return
     * @throws Exception
     */
    public static String get(String url,int connectionTimeout) throws Exception{
        CloseableHttpClient httpclient = null;
        RequestConfig requestConfig = null;
        try {
            //创建HTTPCLIENT链接对象
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            requestConfig = 
                    RequestConfig.custom()
                    .setConnectTimeout(connectionTimeout).build();
            
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            httpGet.setHeader("Content-Type", "application/json");
            CloseableHttpResponse response = httpclient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),Consts.UTF_8));
                StringBuffer sb = new StringBuffer();
                String content = null;
                while((content = reader.readLine()) != null){
                    sb.append(content);
                }
                return sb.toString();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("发起HTTP-GET请求异常!");
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    
    /**
     * 
     * @Description post请求
     * @param type
     * @param url
     * @return
     * @throws Exception
     */
    public static String toPostText(String type,String url) throws Exception{
        try{
            //创建httpclient工具对象     
            HttpClient client = new HttpClient();      
            //创建post请求方法     
            PostMethod post = new PostMethod(url);      
            post.addRequestHeader("User-Agent", "WEB_LIB_GI_"+type); 
            String responseString = null;      
            //设置请求头部类型     
            post.setRequestHeader("Content-Type","text/xml");    
            post.setRequestHeader("charset","utf-8");    
            //设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式      
            int statusCode = client.executeMethod(post);     
            //只有请求成功200了，才做处理  
            if(statusCode == HttpStatus.SC_OK){       
                InputStream inputStream = post.getResponseBodyAsStream();  
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));  
                StringBuffer stringBuffer = new StringBuffer();  
                String str= "";  
                while((str = br.readLine()) != null){  
                stringBuffer.append(str );  
                }   
                responseString = stringBuffer.toString();
            }
            return responseString;   
        }catch (Exception e) {   
           e.printStackTrace();
           throw new Exception("发起HTTP请求异常:"+e.getMessage());
        }
    }

    public static String get(String url, Map<String, Object> paramsMap, Map<String, String> headerMap) throws Exception{
        org.apache.http.client.HttpClient httpClient = null;
        HttpGet httpGet = null;
        String result = null;
        try {
            httpClient = HttpClients.createDefault();

            httpGet = new HttpGet();
            httpGet.setURI(new URI(url));
            // 设置头部参数。
            if (headerMap != null) {
                Iterator iterator = headerMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    httpGet.setHeader(entry.getKey().toString(), entry.getValue().toString());
                }
            }
            //设置请求参数
            if(null != paramsMap){
                //HttpParams
            }
            HttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "UTF-8");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("发起HTTP请求异常:"+ex.getMessage());
        }
        return result;
    }


	public static String post(String url, Map<String, String> headerMap, Map<String, Object> paramsMap) throws Exception {
        org.apache.http.client.HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            httpClient = HttpClients.createDefault();

            httpPost = new HttpPost(url);
            // 设置头部参数。
            if (headerMap != null) {
                Iterator iterator = headerMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    httpPost.setHeader(entry.getKey().toString(), entry.getValue().toString());
                }
            }
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if(paramsMap != null){//设置参数
                Iterator iterator = paramsMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    nvps.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            }

            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "UTF-8");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("发起HTTP请求异常:"+ ex.getMessage());
        }
        return result;
    }
    
    
    public static String toPostJson(String reqParam,String url,int connectionTimeout) throws Exception{
        CloseableHttpClient httpclient = null;
        RequestConfig requestConfig = null;
        try {
            //创建HTTPCLIENT链接对象
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            requestConfig = 
                    RequestConfig.custom()
                    .setConnectTimeout(connectionTimeout).build();
            // 设置包体参数。
            StringEntity se = new StringEntity(reqParam);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/json");
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(se);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),Consts.UTF_8));
                StringBuffer sb = new StringBuffer();
                String content = null;
                while((content = reader.readLine()) != null){
                    sb.append(content);
                }
                return sb.toString();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("发起HTTP-GET请求异常!");
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    
    private static PoolingHttpClientConnectionManager createConnectionManager() throws Exception {
        TrustManager tm = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
        };
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[] { tm }, null);

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context,
                NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        return connectionManager;
    }
    
    /**
     * @throws Exception 
     * 
     * @Title: doPost   
     * @Description: TODO(发送post请求)   
     * @param: @param url
     * @param: @param paramsMap
     * @param: @return      
     * @return: String      
     * @throws
     */
	public static String doPost(String url, Map<String, Object> paramsMap) throws Exception {
    	org.apache.http.client.HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = HttpClients.createDefault(); 
			
			httpPost = new HttpPost(url); 
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if(paramsMap != null){//设置参数
				Iterator iterator = paramsMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					nvps.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			}
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) { 
				if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
					throw new Exception("请求错误URL->"+url+"->请求状态码->"+response.getStatusLine().getStatusCode());
				}
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, "UTF-8");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			  throw new Exception("发起HTTP请求异常:"+ex.getMessage());
		}
		return result;
	}
    
    
    /**   
     * 发送xml请求到server端   
     * @param url xml请求数据地址   
     * @param xmlString 发送的xml数据流   
     * @return null发送失败，否则返回响应内容   
     * @throws Exception 
     */      
	public static String sendGet(String type,String tagUrl) throws Exception{        
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
			httpConn.setRequestProperty("GGaming", "WEB_GG_GI_" + type);// cagent请参考上线说明,文件头为必传
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
			e.printStackTrace();
			throw new Exception("发起HTTP请求异常:"+e.getMessage());
		}finally {
            if(httpConn != null){
                httpConn.disconnect();
            }
        }
    }
	/**
	 * 请求响应处理方法
	 * 
	 * @param Url
	 * @param Parms
	 * @param action
	 * @return
	 * @throws Exception 
	 */
	public static String sendGet(String Url, Map<String, String> Parms) throws Exception {
		String param = "";
		StringBuffer sr = new StringBuffer("");
		Set<String> set = Parms.keySet();
		for (String str : set) {
			sr.append(str + "=");
			sr.append(Parms.get(str) + "&");
		}

		param = sr.toString().substring(0, sr.length() - 1);
		String urlParms = Url + "?" + param;
		// 创建httpclient工具对象
		HttpClient client = new HttpClient();
		// 创建get请求方法
		GetMethod myGet = new GetMethod(urlParms);
		String responseString = null;
		try {
			// 设置请求头部类型
			myGet.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			myGet.setRequestHeader("charset", "utf-8");
			// 设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
			int statusCode = client.executeMethod(myGet);
			// 只有请求成功200了，才做处理
			if (statusCode == HttpStatus.SC_OK) {
				InputStream inputStream = myGet.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				StringBuffer stringBuffer = new StringBuffer();
				String str = "";
				while ((str = br.readLine()) != null) {
					stringBuffer.append(str);
				}
				responseString = stringBuffer.toString();
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("发起HTTP请求异常:"+e.getMessage());
		}
		return responseString;
	}
	
	/**
	 * 
	 * @Title: doPost   
	 * @Description: TODO(发起post请求)   
	 * @param: @param strURL
	 * @param: @param req
	 * @param: @return
	 * @param: @throws Exception      
	 * @return: String      
	 * @throws
	 */
	public static String doPost(String strURL, String req) throws Exception {
		String result = null;
		BufferedReader in = null;
		BufferedOutputStream out = null;
		try {
			URL url = new URL(strURL);
			URLConnection con = url.openConnection();
			HttpURLConnection httpUrlConnection  =  (HttpURLConnection) con;
			httpUrlConnection.setRequestMethod("POST");
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			out = new BufferedOutputStream(con.getOutputStream());
			byte outBuf[] = req.getBytes("utf-8");
			out.write(outBuf);
			out.close();

			in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
			StringBuffer sb = new StringBuffer();
			String data = null;

			while ((data = in.readLine()) != null) {
				sb.append(data);
			}
			result = sb.toString();		
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("发起HTTP请求异常:"+ex.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
		}
		if (result == null)
			return "";
		else
			return result;
	}
	
	
	/**
	 * 
	 * @Description 原始的HTTP-GET请求
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public static final String httpUrlConnectionGet(String params) throws Exception{
	    URL uUrl = null;  
        HttpURLConnection conn = null;  
        BufferedWriter out = null;  
        BufferedReader in = null;  
        try {  
            //创建和初始化连接  
            uUrl = new URL(params);  
            conn = (HttpURLConnection) uUrl.openConnection();  
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            //设置连接超时时间  
            conn.setConnectTimeout(3000);  
            //设置读取超时时间  
            conn.setReadTimeout(3000);  
            //接收返回结果  
            StringBuilder result = new StringBuilder();  
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));  
            if(in != null){  
                String line = "";  
                while ((line = in.readLine()) != null) {  
                    result.append(line);  
                }  
            }  
            return result.toString();  
        } catch (Exception e) {  
            logger.error("调用接口["+params+"]失败！请求URL："+params+"，参数："+params,e);  
            //处理错误流，提高http连接被重用的几率  
            try {  
                byte[] buf = new byte[100];  
                InputStream es = conn.getErrorStream();  
                if(es != null){  
                    while (es.read(buf) > 0) {;}  
                    es.close();  
                }  
            } catch (Exception e1) {  
                e1.printStackTrace();  
            }  
        } finally {  
            try {  
                if (out!=null) {  
                    out.close();  
                }  
            }catch (Exception e) {  
                e.printStackTrace();  
            }  
            try {  
                if (in !=null) {  
                    in.close();  
                }  
            }catch (Exception e) {  
                e.printStackTrace();  
            }  
            //关闭连接  
            if (conn != null){  
                conn.disconnect();  
            }     
        }  
        return null;  
	}
}
