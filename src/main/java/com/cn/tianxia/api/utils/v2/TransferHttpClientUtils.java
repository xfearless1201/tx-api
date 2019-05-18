package com.cn.tianxia.api.utils.v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.exception.HttpClientException;

/**
 * 
 * @ClassName HttpUtils
 * @Description httpclient 工具类Client，用于转账调用
 * @author Hardy
 * @Date 2018年9月12日 下午12:25:34
 * @version 1.0.0
 */
public class TransferHttpClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(TransferHttpClientUtils.class);

    private final static int CONNECT_TIMEOUT = 5*1000;//与远程服务器连接超时时间
    private final static int CONNECTION_REQUEST_TIMEOUT = 5*1000;//从连接池中获取连接的超时时间
    //socket读数据超时时间：从服务器获取响应数据的超时时间
    private static final int MAX_TIMEOUT = 5*1000;

    public static final String UTF_8 = "UTF-8";

    private static CloseableHttpClient getCloseableHttpClient(Integer timeout) throws Exception {
        return HttpClients.custom()
                .setConnectionManager(createConnectionManager())
                .setDefaultRequestConfig(getRequestConfig(timeout))
                .build();
    }
    private static RequestConfig getRequestConfig(Integer timeout) {
        return RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setSocketTimeout(timeout)
                .build();
    }
    private static PoolingHttpClientConnectionManager createConnectionManager() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
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
        context.init(null, new TrustManager[] { tm }, null);

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context,NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        // 设置连接池大小
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(connectionManager.getMaxTotal());
        // Validate connections after 1 sec of inactivity
        connectionManager.setValidateAfterInactivity(1000);
        return connectionManager;
    }

    /******************  GET  *******************/

    public static String doGet(String url,Integer timeout) throws Exception {
        return doGetBase(url,timeout,null,null);
    }

    public static String doGetToken(String url,Integer timeout,String acctoken) throws Exception {
        return doGetBase(url,timeout,null,acctoken);
    }

    public static String doGetJsonContentType(String url,Integer timeout) throws Exception {
        return doGetBase(url,timeout,"application/json",null);
    }

    /**
     * 功能描述: get请求
     *
     * @Author: Horus
     * @Date: 2019/1/20 14:11
     * @param url
     * @param timeout 三个连接超时时间,默认为5000ms
     * @return: java.lang.String
     **/
    private static String doGetBase(String url,Integer timeout,String contentType,String acctoken) throws Exception {
        if(timeout == null){
            timeout = MAX_TIMEOUT;
        }
        //创建HTTPCLIENT链接对象
        CloseableHttpClient httpclient = getCloseableHttpClient(timeout);

        HttpGet httpGet = new HttpGet(url);
        if(contentType != null){
            httpGet.setHeader("Content-Type", contentType);
        }
        if(acctoken != null){
            httpGet.setHeader("Authorization", "Bearer " + acctoken);
        }

        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            return getResponseResult(response);
        } catch (SocketTimeoutException e) {//只处理响应超时，需要订单轮询
            logger.info("转账模块发起HTTP-GET请求异常!");
            throw new HttpClientException("转账模块发起HTTP-GET请求异常!",e);
        } finally {
            if(response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /******************  POST  *******************/

    public static String toPostJson(String url,String data,int socketTimeout) throws Exception{
        HttpClient client = new HttpClient();
        // 创建post请求方法
        PostMethod myPost = new PostMethod(url);
        String responseString = null;
        int statusCode;
        // 设置请求头部类型
        myPost.setRequestHeader("Content-Type", "application/json");
        myPost.setRequestHeader("charset", "utf-8");
        myPost.setRequestBody(data);
        // 这里的超时单位是毫秒。这里的http.socket.timeout相当于SO_TIMEOUT
        client.getParams().setIntParameter("http.socket.timeout", socketTimeout);

        // 设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
        try {
            statusCode = client.executeMethod(myPost);
            // 只有请求成功200了，才做处理
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = myPost.getResponseBodyAsStream();
                if(inputStream != null){
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer stringBuffer = new StringBuffer();
                    String str = "";
                    while ((str = br.readLine()) != null) {
                        stringBuffer.append(str);
                    }
                    responseString = stringBuffer.toString();
                }
            }
        } catch (SocketTimeoutException e) {//只处理响应超时，需要订单轮询
            e.printStackTrace();
            logger.info("HTTP请求异常:{}",e.getMessage());
            throw new HttpClientException("转账模块发起HTTP-toPostJson请求异常!",e);
        } finally {
            myPost.releaseConnection();
        }
        return responseString;
    }

    public static String doPost(String url, Map<String, Object> paramsMap,Integer timeout) throws Exception {
        return doPostBase(url,paramsMap,timeout);
    }

    private static String doPostBase(String url, Map<String, Object> paramsMap,Integer timeout) throws Exception {
        if(timeout == null){
            timeout = MAX_TIMEOUT;
        }
        //创建HTTPCLIENT链接对象
        CloseableHttpClient httpclient = getCloseableHttpClient(timeout);

        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if(paramsMap != null){//设置参数
            Iterator iterator = paramsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                nvps.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, UTF_8));
        }
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            return getResponseResult(response);
        } catch (SocketTimeoutException e) {//只处理响应超时，需要订单轮询
            logger.info("转账模块发起HTTP-POST请求异常!");
            throw new HttpClientException("转账模块发起POST请求异常!",e);
        } finally {
            if(response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 功能描述: post请求   AG AGIN
     *  无参数
     * @Author: Horus
     * @Date: 2019/1/20 14:11
     * @param url
     * @param timeout 三个连接超时时间,默认为5000ms
     * @param platformKey
     * @return: java.lang.String
     **/
    public static String postXml(String url,Integer timeout,String platformKey) throws Exception {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "text/xml; charset=UTF-8");
        headerMap.put("User-Agent", "WEB_LIB_GI_" + platformKey);
        return postXmlBase(url,headerMap,null,null,timeout);
    }

    /**
     * 功能描述: post请求   MG
     * 字符串参数
     * @Author: Horus
     * @Date: 2019/1/24 11:55
     * @param url
     * @param param
     * @return: java.lang.String
     **/
    public static String postXml(String url,String param) throws Exception {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/xml; charset=UTF-8");
        return postXmlBase(url,headerMap,param,null,null);
    }

    public static String postXml(String url,Map<String, Object> headerMap,String param) throws Exception {
        return postXmlBase(url,headerMap,param,null,null);
    }

    public static String postXml(String url, Map<String, Object> headerMap, Map<String, Object> paramsMap) throws Exception {
        return postXmlBase(url,headerMap,null,paramsMap,null);
    }

    private static String postXmlBase(String url, Map<String, Object> headerMap, String paramsStr,Map<String, Object> paramsMap,Integer timeout) throws Exception {
        if(timeout == null){
            timeout = MAX_TIMEOUT;
        }
        //创建HTTPCLIENT链接对象
        CloseableHttpClient httpClient = getCloseableHttpClient(timeout);

        HttpPost httpPost = new HttpPost(url);
        // 设置头部参数。
        if (headerMap != null) {
            Iterator iterator = headerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                httpPost.setHeader(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        if(paramsStr != null){
            // 设置包体参数。 StringEntity是HttpEntity子类
            StringEntity se = new StringEntity(paramsStr);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/xml");
            httpPost.setEntity(se);
        }
        if(paramsMap != null){
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Iterator iterator = paramsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                nvps.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps,UTF_8));
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            return getResponseResult(response);
        } catch (SocketTimeoutException e) {//只处理响应超时，需要订单轮询
            e.printStackTrace();
            throw new HttpClientException("转账模块发起POSTXML请求异常!",e);
        } finally {
            if(response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /******************  PUT  *******************/
    public static String doPut(String url, Map<String, Object> headerMap, String paramsStr) throws Exception {
        return doPutBase(url,headerMap,paramsStr,null,null);
    }

    public static String doPut(String url, Map<String, Object> headerMap, Map<String, Object> paramsMap) throws Exception {
        return doPutBase(url,headerMap,null,paramsMap,null);
    }

    private static String doPutBase(String url, Map<String, Object> headerMap, String paramsStr, Map<String, Object> paramsMap,Integer timeout) throws Exception {
        if(timeout == null){
            timeout = MAX_TIMEOUT;
        }
        //创建HTTPCLIENT链接对象
        CloseableHttpClient httpClient = getCloseableHttpClient(timeout);
        HttpPut httpPut = new HttpPut(url);
        // 设置头部参数。
        if (headerMap != null) {
            Iterator iterator = headerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                httpPut.setHeader(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        if(paramsStr != null){
            StringEntity se = new StringEntity(paramsStr);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/json");
            httpPut.setEntity(se);
        }
        if(paramsMap != null){
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Iterator iterator = paramsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                nvps.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
            }
            httpPut.setEntity(new UrlEncodedFormEntity(nvps,UTF_8));
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPut);
            return getResponseResult(response);
        } catch (SocketTimeoutException e) {//只处理响应超时，需要订单轮询
            e.printStackTrace();
            throw new HttpClientException("转账模块发起PUT请求异常!",e);
        }  finally {
            if(response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static String getResponseResult(CloseableHttpResponse response) throws IOException {
        String result = null;
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream in = entity.getContent();
                result = IOUtils.toString(in, "UTF-8");
                in.close();//关闭连接等待复用
            }
        }
        return result;
    }


}
