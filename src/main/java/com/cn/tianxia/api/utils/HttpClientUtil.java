package com.cn.tianxia.api.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

/**
 * 利用HttpClient进行post请求的工具类(为了避免需要证书,忽略校验过程)。
 * 
 * @author sum
 *
 */
@Service
@SuppressWarnings("all")
public class HttpClientUtil {
	/**
	 * httpClient 提交post请求。
	 * 
	 * @param url
	 *            请求地址
	 * @param headerMap
	 *            头部参数
	 * @return
	 */
	public static String doGet(String url, Map<String, Object> headerMap) {
		HttpClient httpClient = null;
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
					Entry entry = (Entry) iterator.next();
					httpGet.setHeader(entry.getKey().toString(), entry.getValue().toString());
				}
			}
			HttpResponse response = httpClient.execute(httpGet);
			if (response != null) {
				//System.out.println("请求状态码："+response.getStatusLine().getStatusCode());
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
		}
		return result;
	}
	
	public static String doGet(String url, Map<String, Object> paramsMap,Map<String, Object> headerMap) {
		HttpClient httpClient = null;
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
					Entry entry = (Entry) iterator.next();
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
		}
		return result;
	}

	/**
	 * httpClient 提交put请求。
	 * 
	 * @param url
	 *            请求地址
	 * @param headerMap
	 *            头部参数
	 * @param parameterMap
	 *            包体参数
	 * @return
	 */
	public static String doPut(String url, Map<String, Object> headerMap, String param) {
		HttpClient httpClient = null;
		HttpPut httpPut = null;
		String result = null;
		try {
			httpClient = HttpClients.createDefault(); 
			
			httpPut = new HttpPut();
			httpPut.setURI(new URI(url));
			// 设置头部参数。
			if (headerMap != null) {
				Iterator iterator = headerMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					//System.out.println(entry.getKey().toString()+"--------"+entry.getValue().toString());
					httpPut.setHeader(entry.getKey().toString(), entry.getValue().toString());
				}
			}
			StringEntity se = new StringEntity(param);
			se.setContentEncoding("UTF-8");
			se.setContentType("application/json");
			httpPut.setEntity(se);
			HttpResponse response = httpClient.execute(httpPut);
			if (response != null) {
				//System.out.println("请求状态码："+response.getStatusLine().getStatusCode());
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
		}
		return result;
	}

	/**
	 * httpClient 提交post请求。
	 * 
	 * @param url
	 *            请求地址
	 * @param headerMap
	 *            头部参数
	 * @param parameterMap
	 *            包体参数
	 * @return
	 */
	public static String doPost(String url, Map<String, String> headerMap, String param) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = HttpClients.createDefault(); 
			
			httpPost = new HttpPost(url);
			// 设置头部参数。
			if (headerMap != null) {
				Iterator iterator = headerMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					httpPost.setHeader(entry.getKey().toString(), entry.getValue().toString());
				}
			}
			// 设置包体参数。 
			StringEntity se = new StringEntity(param);
			se.setContentEncoding("UTF-8");
			se.setContentType("text/plain");
			httpPost.setEntity(se);
			HttpResponse response = httpClient.execute(httpPost);
			//System.out.println("response.getParams()->"+response.getStatusLine().getStatusCode());
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, "UTF-8");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	/**
	 * httpClient 提交post请求。
	 * 
	 * @param url
	 *            请求地址
	 * @param headerMap
	 *            头部参数
	 * @param parameterMap
	 *            包体参数
	 * @return
	 */
	public static String doPostXml(String url, Map<String, Object> headerMap, String param) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = HttpClients.createDefault(); 
			
			httpPost = new HttpPost(url);
			// 设置头部参数。
			if (headerMap != null) {
				Iterator iterator = headerMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					httpPost.setHeader(entry.getKey().toString(), entry.getValue().toString());
				}
			}
			httpPost.setHeader("Content-Type", "application/xml");
			// 设置包体参数。 
			StringEntity se = new StringEntity(param);
			se.setContentEncoding("UTF-8");
			se.setContentType("application/xml");
			httpPost.setEntity(se);
			HttpResponse response = httpClient.execute(httpPost);
			//System.out.println("response.getParams()->"+response.getStatusLine().getStatusCode());
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, "UTF-8");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	public static String doPost(String url, Map<String, Object> headerMap, Map<String, Object> paramsMap) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = HttpClients.createDefault(); 
			
			httpPost = new HttpPost(url);
			// 设置头部参数。
			if (headerMap != null) {
				Iterator iterator = headerMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					httpPost.setHeader(entry.getKey().toString(), entry.getValue().toString());
				}
			}
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
				//System.out.println("请求状态码："+response.getStatusLine().getStatusCode());
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
		}
		return result;
	}
	
	public static String doPost(String url, Map<String, String> paramsMap) {
		HttpClient httpClient = null;
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
				//System.out.println("请求状态码："+response.getStatusLine().getStatusCode());
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
		}
		return result;
	}
	
	

	public static String doPostHttp(String url, Map<String, Object> headerMap, String param) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = new DefaultHttpClient();
			
			httpPost = new HttpPost(url);
			// 设置头部参数。
			if (headerMap != null) {
				Iterator iterator = headerMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					httpPost.setHeader(entry.getKey().toString(), entry.getValue().toString());
				}
			}
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			if(StringUtils.isNotBlank(param)){
				// 设置包体参数。 
				StringEntity se = new StringEntity(param);
				se.setContentEncoding("UTF-8");
				se.setContentType("text/plain");
				httpPost.setEntity(se);
			}
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				//System.out.println("请求状态码："+response.getStatusLine().getStatusCode());
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
		}
		return result;
	}
	
	public static String doPostShot(String url, Map<String, Object> headerMap, Map<String, Object> paramsMap) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = HttpClients.createDefault(); 
			RequestConfig requestConfig = RequestConfig.custom()    
			        .setConnectTimeout(3000).setConnectionRequestTimeout(1000)    
			        .setSocketTimeout(3000).build();    
			httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			// 设置头部参数。
			if (headerMap != null) {
				Iterator iterator = headerMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					httpPost.setHeader(entry.getKey().toString(), entry.getValue().toString());
				}
			}
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
				System.out.println("请求状态码："+response.getStatusLine().getStatusCode());
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
		}
		return result;
	}
}
