package com.cn.tianxia.api.utils.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName HttpUtils
 * @Description httpclient 工具类
 * @author Hardy
 * @Date 2018年9月12日 下午12:25:34
 * @version 1.0.0
 */
public class HttpUtils {
    
    /**
     * 
     * @Description 发起流参数
     * @param url
     * @param data
     * @return
     * @throws Exception 
     */
    public static String post(Map<String,String> data,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null && data.size() > 0){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> d : data.entrySet()) {
                    NameValuePair v = new BasicNameValuePair(d.getKey(),d.getValue());
                    nvps.add(v);
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
                httppost.setEntity(formEntity);
            }
            
            HttpResponse response = httpclient.execute(httppost);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),Consts.UTF_8));
                StringBuffer sb = new StringBuffer();
                
                String content = null;
                while((content = reader.readLine()) != null){
                    sb.append(content);
                }
                return sb.toString();
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    
    public static String toPostJson(String data,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            // 设置包体参数。
            StringEntity se = new StringEntity(data);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/json");
            httppost.setEntity(se);
            CloseableHttpResponse response = httpclient.execute(httppost);
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
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    
    public static String toPostJson(Map<String,String> data,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry : data.entrySet()){
                    NameValuePair v = new BasicNameValuePair(entry.getKey(),entry.getValue());
                    nvps.add(v);
                }
                httppost.setEntity(new UrlEncodedFormEntity(nvps,Consts.UTF_8));
                httppost.setHeader("Content-Type", "application/json");
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
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
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    
    /**
     * 
     * @Description 发起参数为json类型的post请求
     * @param data
     * @param url
     * @return
     * @throws Exception
     */
    public static String toPostJsonStr(JSONObject data,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null && !data.isEmpty()){
                StringEntity entity = new StringEntity(data.toString(),"utf-8");//解决中文乱码问题   
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type", "application/json");
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
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
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    public static String toPostXml(Map<String,String> data,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry : data.entrySet()){
                    NameValuePair v = new BasicNameValuePair(entry.getKey(),entry.getValue());
                    nvps.add(v);
                }
                StringEntity entity = new UrlEncodedFormEntity(nvps,Consts.UTF_8);
                entity.setContentEncoding("UTF-8");
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type", "text/xml");
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
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
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    public static String toPostXml(String xmlStr,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();

            HttpPost httppost = new HttpPost(url);
            // 设置包体参数。
            StringEntity se = new StringEntity(xmlStr);
            se.setContentEncoding("UTF-8");
            se.setContentType("text/xml");
            httppost.setEntity(se);
            CloseableHttpResponse response = httpclient.execute(httppost);
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
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    public static String toPostForm(String data,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();

            HttpPost httppost = new HttpPost(url);
            // 设置包体参数。
            StringEntity se = new StringEntity(data);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/x-www-form-urlencoded");
            httppost.setEntity(se);
            CloseableHttpResponse response = httpclient.execute(httppost);
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
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    
    public static String toPostForm(Map<String,String> data,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry : data.entrySet()){
                    NameValuePair v = new BasicNameValuePair(entry.getKey(),entry.getValue());
                    nvps.add(v);
                }
                StringEntity entity = new UrlEncodedFormEntity(nvps,Consts.UTF_8);
                entity.setContentEncoding("UTF-8");
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
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
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    /**
     * 
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param data
     * @param url
     * @param defKey 自定义请求头键
     * @param defValue 自定义请求头值
     * @return
     * @throws Exception
     */
    public static String toPostForm(Map<String,String> data,String url,String defKey,String defValue) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry : data.entrySet()){
                    NameValuePair v = new BasicNameValuePair(entry.getKey(),entry.getValue());
                    nvps.add(v);
                }
                StringEntity entity = new UrlEncodedFormEntity(nvps,Consts.UTF_8);
                entity.setContentEncoding("UTF-8");
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                if(StringUtils.isNotBlank(defKey)&&StringUtils.isNotBlank(defValue)){
                    httppost.setHeader(defKey, defValue);
                }
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
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
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    
    /**
     * 
     * @Description 自定义设置字符集
     * @param data
     * @param url
     * @param charset
     * @return
     * @throws Exception
     */
    public static String toPostForm(Map<String,String> data,String url,String charset) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry : data.entrySet()){
                    NameValuePair v = new BasicNameValuePair(entry.getKey(),entry.getValue());
                    nvps.add(v);
                }
                StringEntity entity = new UrlEncodedFormEntity(nvps,Consts.UTF_8);
                entity.setContentEncoding(charset);
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
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
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    
    public static String toPostForm(String data,String url,String charset) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null){
                StringEntity entity = new StringEntity(data);
                entity.setContentEncoding(charset);
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
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
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    
    
    /**
     * 
     * @Description get请求
     * @param data
     * @param url
     * @return
     */
    public static String get(Map<String,String> data,String url)throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            String params = "";
            if(data != null){
                StringBuffer sb = new StringBuffer();
                Iterator<String> iterator = data.keySet().iterator();
                while(iterator.hasNext()){
                    String key = iterator.next();
                    String val = data.get(key);
                    if(StringUtils.isBlank(val)){
                        val = "";
                    }
                    sb.append("&").append(key).append("=").append(val);
                }
                
                params = sb.toString().replaceFirst("&", "?");
            }
            HttpGet httpGet = new HttpGet(url+params);
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
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    /**
     * 
     * @Description get请求
     * @param url
     * @return
     */
    public static String get(String url)throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpGet httpGet = new HttpGet(url);
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
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }
    
    /**
     * 
     * @Description 发送流请求
     * @param reqParams
     * @param url
     * @return
     * @throws Exception
     */
    public static String toPostIO(String reqParams,String url) throws Exception{
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //1.获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            //2.中文有乱码的需要将PrintWriter改为如下
            //out=new OutputStreamWriter(conn.getOutputStream(),"UTF-8")
            // 发送请求参数
            out.print(reqParams);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(out!=null){
                out.close();
            }
            if(in!=null){
                in.close();
            }
        }
        return result;
    }
    
    /**
     * 
     * @Description 生成支付表单
     * @param data
     * @param payUr
     * @return
     */
    public static String generatorForm(Map<String,String> data,String payUrl) {
        String FormString = "<body onLoad=\"document.actform.submit()\">正在处理请稍候....................."
                + "<form  id=\"actform\" name=\"actform\" method=\"post\" action=\""
                + payUrl + "\">";
        if(data != null && !data.isEmpty()){
            for (String key : data.keySet()) {
                if (StringUtils.isNotBlank(data.get(key)))
                    FormString += "<input name=\"" + key + "\" type=\"hidden\" value='" + data.get(key) + "'>\r\n";
            }
        }
        FormString += "</form></body>";
        return FormString;
    }
    /**
     * 
     * @Description 生成支付表单
     * @param data
     * @param payUr
     * @return
     */
    public static String generatorFormGet(Map<String,String> data,String payUrl) {
        String FormString = "<body onLoad=\"document.actform.submit()\">正在处理请稍候....................."
                + "<form  id=\"actform\" name=\"actform\" method=\"get\" action=\""
                + payUrl + "\">";
        if(data != null && !data.isEmpty()){
            for (String key : data.keySet()) {
                if (StringUtils.isNotBlank(data.get(key)))
                    FormString += "<input name=\"" + key + "\" type=\"hidden\" value='" + data.get(key) + "'>\r\n";
            }
        }
        FormString += "</form></body>";
        return FormString;
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
}
