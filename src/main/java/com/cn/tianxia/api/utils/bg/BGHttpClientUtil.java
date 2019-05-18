package com.cn.tianxia.api.utils.bg;

import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * http客户端工具类 
 * @date 2015年11月21日 下午12:02:24
 */
@Service
public final class BGHttpClientUtil { 
	
	private static Logger logger = LoggerFactory.getLogger(BGHttpClientUtil.class); 
	
	private static CloseableHttpClient client;
	
	private static long threshold = 30000L;

	/**
	 * 防止非法实例化
	 */
	private BGHttpClientUtil() { }
	
	private static void init() {
		
		PoolingHttpClientConnectionManager m = new PoolingHttpClientConnectionManager();
		// Create socket configuration
        SocketConfig socketConfig = SocketConfig.custom()
            .setTcpNoDelay(true)
            .setSoKeepAlive(true)
            .setSoReuseAddress(true)
            .build();
        // Configure the connection manager to use socket configuration either
        // by default or for a specific host.
        m.setDefaultSocketConfig(socketConfig); 

        // Create message constraints
        MessageConstraints messageConstraints = MessageConstraints.custom()
            .setMaxHeaderCount(40)
            .setMaxLineLength(400)
            .build();
        // Create connection configuration
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE)
            .setCharset(Consts.UTF_8)
            .setMessageConstraints(messageConstraints) 
            .build();
        // Configure the connection manager to use connection configuration either
        // by default or for a specific host. 
        m.setDefaultConnectionConfig(connectionConfig); 

        // Configure total max or per route limits for persistent connections
        // that can be kept in the pool or leased by the connection manager.
        m.setMaxTotal(15240);
        m.setDefaultMaxPerRoute(10240);   

        // Use custom cookie store if necessary.
        CookieStore cookieStore = new BasicCookieStore();
        // Use custom credentials provider if necessary.
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        // Create global request configuration
        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setCookieSpec(CookieSpecs.BEST_MATCH) 
            .setStaleConnectionCheckEnabled(true)
            .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
            .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)) 
            .setSocketTimeout(30000)
            .setConnectTimeout(6000)
            .build();
        
        ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {

            @Override
            public long getKeepAliveDuration(HttpResponse res, HttpContext cxt) {
                
            	long keepAlive = super.getKeepAliveDuration(res, cxt);
                if (keepAlive == -1) {
                    // Keep connections alive 10 seconds if a keep-alive value
                    // has not be explicitly set by the server
                    keepAlive = 120000;
                }
                return keepAlive;
            } 
        };
        
        SSLConnectionSocketFactory sslfac = null;
		try {
			sslfac = new SSLConnectionSocketFactory(SSLContexts.custom()
			        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
			        .build());
		} catch (Exception ex) { 
			ex.printStackTrace();
		}

        ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();
        
        // Create an HttpClient with the given custom dependencies and configuration.
        CloseableHttpClient httpclient = HttpClients.custom()
            .setConnectionManager(m)
            .setConnectionReuseStrategy(connStrategy)
            .setDefaultCookieStore(cookieStore)
            .setDefaultCredentialsProvider(credentialsProvider) 
            .setDefaultRequestConfig(defaultRequestConfig)
            .setKeepAliveStrategy(keepAliveStrat) 
            .setSSLSocketFactory(sslfac) 
            .build();
 
        client = httpclient;
	}
	
	private static CloseableHttpClient initClient() {
		
		if (null == client) {
			synchronized (BGHttpClientUtil.class) {
				if (null == client) {
					init();
				}
			}
		}
		return client;
	}
	
	/**
	 * 执行 http get请求
	 * @param url - URL
	 * @return
	 */
	public static String get(String url) {
		return get(url, null);
	}
	
	/**
	 * 执行 http get请求
	 * @param url - URL
	 * @param charset - 请求字符集
	 * @return
	 */
	public static String get(String url, String charset) {
		 
		HttpGet m = new HttpGet(url);
		
		CloseableHttpClient c = initClient(); 
		if (null != charset) {
			m.setHeader("Accept-Charset", charset + ",*");
		}
		CloseableHttpResponse res = null;
		long t1 = System.currentTimeMillis(); 
		try { 
			res = c.execute(m);
			StatusLine sl = res.getStatusLine();
			if (sl.getStatusCode() == HttpStatus.SC_OK) {
				String rtv = EntityUtils.toString(res.getEntity());

				long t2 = System.currentTimeMillis(); 
				long et = t2 - t1;
				if (et > threshold && threshold > 0) {
					String msg = "http-threshold-get, het[%s], threshold[%s], url[%s]";
					msg = String.format(msg, et, threshold, url);
					logger.warn(msg);
				} 
				return rtv;
			} else {
				throw new RuntimeException("error StatusCode:" + sl.getStatusCode());
			}
		} catch (Exception ex) {
			checkExceptionWithPoolShutdown(ex);
			throw new RuntimeException(ex);
		} finally {
	    	if (null != res) {
	    		try {
	    			res.close();
	    		} catch (Exception ex) { }
	    	}
		} 
	}
	
	/**
	 * 执行 http get请求
	 * @param url - URL
	 * @param charset - 请求字符集
	 * @return
	 */
	public static byte[] getReponse(String url) {
		 
		HttpGet m = new HttpGet(url);
		
		CloseableHttpClient c = initClient();  
		CloseableHttpResponse res = null;
		long t1 = System.currentTimeMillis(); 
		try { 
			res = c.execute(m);
			StatusLine sl = res.getStatusLine();
			if (sl.getStatusCode() == HttpStatus.SC_OK) {
				byte[] data = EntityUtils.toByteArray(res.getEntity());
				long t2 = System.currentTimeMillis(); 
				long et = t2 - t1;
				if (et > threshold && threshold > 0) {
					String msg = "http-threshold-get, het[%s], threshold[%s], url[%s]";
					msg = String.format(msg, et, threshold, url);
					logger.warn(msg);
				} 
				return data;
			} else {
				throw new RuntimeException("error StatusCode:" + sl.getStatusCode());
			}
		} catch (Exception ex) {
			checkExceptionWithPoolShutdown(ex);
			throw new RuntimeException(ex);
		} finally {
	    	if (null != res) {
	    		try {
	    			res.close();
	    		} catch (Exception ex) { }
	    	}
		} 
	}
	
	/**
	 * 执行 http post请求
	 * @param url
	 * @param content
	 * @return
	 */
	public static String post(String url, String content) {
		return post(url, null, content, null, null);
	}
	
	public static String post(String url, Map<String, String> headers, String content) {
		
		return post(url, headers, content, null, null);
	}
	
	public static String postJson(String url, Map<String, String> headers, String content) {
		
		return postJson(url, headers, content, null, null);
	}
	
	/**
	 * 执行 http post请求(指定编码)
	 * @param url
	 * @param content
	 * @param charset
	 * @return
	 */
	public static String post(String url, Map<String, String> headers, String content, String cookie, String charset) {
		
		HttpPost m = new HttpPost(url);
		
		if (null != headers) {
			for (Entry<String, String> ee : headers.entrySet()) {
				m.setHeader(ee.getKey(), ee.getValue());
			}
		}
		
		Charset c = (null != charset) ? Charset.forName(charset) : Consts.UTF_8;
		StringEntity entity = new StringEntity(content, ContentType.create("application/x-www-form-urlencoded", c));
		m.setEntity(entity);
		
		CloseableHttpClient cc = initClient(); 
		if (null != charset) {
			m.setHeader("Accept-Charset", charset + ",*");
		}
		if (null != cookie) {  
			m.setHeader("Cookie", cookie);
		}
		CloseableHttpResponse res = null;
		long t1 = System.currentTimeMillis(); 
		try { 
			res = cc.execute(m);
			StatusLine sl = res.getStatusLine();
			if (sl.getStatusCode() == HttpStatus.SC_OK) {
				String rtv = EntityUtils.toString(res.getEntity());
				
				long t2 = System.currentTimeMillis(); 
				long et = t2 - t1;
				if (et > threshold && threshold > 0) {
					String msg = "http-threshold-post, het[%s], threshold[%s], url[%s]\n content[%s]";
					msg = String.format(msg, et, threshold, url, content);
					logger.warn(msg);
				} 
				return rtv;
			} else {
				throw new RuntimeException("error StatusCode:" + sl.getStatusCode());
			}
		} catch (Exception ex) {
			checkExceptionWithPoolShutdown(ex);
			throw new RuntimeException(ex);
		} finally {
	    	if (null != res) {
	    		try {
	    			res.close();
	    		} catch (Exception ex) { }
	    	}
		} 
	}  
	

	
	/**
	 * 执行 http post请求(指定编码)
	 * @param url
	 * @param content
	 * @param charset
	 * @return
	 */
	public static String postJson(String url, Map<String, String> headers, String json, String cookie, String charset) {
		
		HttpPost m = new HttpPost(url);
		
		if (null != headers) {
			for (Entry<String, String> ee : headers.entrySet()) {
				m.setHeader(ee.getKey(), ee.getValue());
			}
		}
		
		Charset c = (null != charset) ? Charset.forName(charset) : Consts.UTF_8;

		StringEntity entity = new StringEntity(json,
				ContentType.create("application/json", c));
		m.setEntity(entity);
		
		CloseableHttpClient cc = initClient(); 
		if (null != charset) {
			m.setHeader("Accept-Charset", charset + ",*");
		}
		if (null != cookie) {  
			m.setHeader("Cookie", cookie);
		}
		CloseableHttpResponse res = null;
		long t1 = System.currentTimeMillis(); 
		try { 
			res = cc.execute(m);
			StatusLine sl = res.getStatusLine();
			if (sl.getStatusCode() == HttpStatus.SC_OK) {
				String rtv = EntityUtils.toString(res.getEntity());

				long t2 = System.currentTimeMillis(); 
				long et = t2 - t1;
				if (et > threshold && threshold > 0) {
					String msg = "http-threshold-postjson, het[%s], threshold[%s], url[%s]\n json[%s]";
					msg = String.format(msg, et, threshold, url, json);
					logger.warn(msg);
				} 
				return rtv;
			} else {
				throw new RuntimeException("error StatusCode:" + sl.getStatusCode());
			}
		} catch (Exception ex) {
			checkExceptionWithPoolShutdown(ex);
			throw new RuntimeException(ex);
		} finally {
	    	if (null != res) {
	    		try {
	    			res.close();
	    		} catch (Exception ex) { }
	    	}
		} 
	}  
	
	public static String put(String url, Map<String, String> headers, String content) {
		
		return put(url, headers, content, null, null);
	}
	
	/**
	 * 执行 http post请求(指定编码)
	 * @param url
	 * @param content
	 * @param charset
	 * @return
	 */
	public static String put(String url, Map<String, String> headers, String content, String cookie, String charset) {
		
		HttpPut m = new HttpPut(url);
		
		if (null != headers) {
			for (Entry<String, String> ee : headers.entrySet()) {
				m.setHeader(ee.getKey(), ee.getValue());
			}
		}
		
		Charset c = (null != charset) ? Charset.forName(charset) : Consts.UTF_8;
		StringEntity entity = new StringEntity(content, ContentType.create("application/x-www-form-urlencoded", c));
		m.setEntity(entity);
		
		CloseableHttpClient cc = initClient(); 
		if (null != charset) {
			m.setHeader("Accept-Charset", charset + ",*");
		}
		if (null != cookie) {  
			m.setHeader("Cookie", cookie);
		}
		CloseableHttpResponse res = null;
		long t1 = System.currentTimeMillis(); 
		try { 
			res = cc.execute(m);
			StatusLine sl = res.getStatusLine();
			if (sl.getStatusCode() == HttpStatus.SC_OK) {
				String rtv = EntityUtils.toString(res.getEntity());
				long t2 = System.currentTimeMillis(); 
				long et = t2 - t1;
				if (et > threshold && threshold > 0) {
					String msg = "http-threshold-put, het[%s], threshold[%s], url[%s]\n content[%s]";
					msg = String.format(msg, et, threshold, url, content);
					logger.warn(msg);
				} 
				return rtv;
			} else {
				throw new RuntimeException("error StatusCode:" + sl.getStatusCode());
			}
		} catch (Exception ex) {
			checkExceptionWithPoolShutdown(ex);
			throw new RuntimeException(ex);
		} finally {
	    	if (null != res) {
	    		try {
	    			res.close();
	    		} catch (Exception ex) { }
	    	}
		} 
	}  
	
	public static String get(String url, Map<String, String> headers, String charset) {
		return get(url, headers, null, charset);
	}
	
	/**
	 * 执行 http get请求(指定编码)
	 * @param url
	 * @param content
	 * @param charset
	 * @return
	 */
	public static String get(String url, Map<String, String> headers, String cookie, String charset) {
		
		HttpGet m = new HttpGet(url);
		
		if (null != headers) {
			for (Entry<String, String> ee : headers.entrySet()) {
				m.setHeader(ee.getKey(), ee.getValue());
			}
		}
		  
		CloseableHttpClient cc = initClient(); 
		if (null != charset) {
			m.setHeader("Accept-Charset", charset + ",*");
		}
		if (null != cookie) {  
			m.setHeader("Cookie", cookie);
		}
		CloseableHttpResponse res = null;
		long t1 = System.currentTimeMillis(); 
		try { 
			res = cc.execute(m);
			StatusLine sl = res.getStatusLine();
			if (sl.getStatusCode() == HttpStatus.SC_OK) {
				String rtv = EntityUtils.toString(res.getEntity());
				long t2 = System.currentTimeMillis(); 
				long et = t2 - t1;
				if (et > threshold && threshold > 0) {
					String msg = "http-threshold-get, het[%s], threshold[%s], url[%s]";
					msg = String.format(msg, et, threshold, url);
					logger.warn(msg);
				} 
				return rtv;
			} else {
				throw new RuntimeException("error StatusCode:" + sl.getStatusCode());
			}
		} catch (Exception ex) {
			checkExceptionWithPoolShutdown(ex);
			throw new RuntimeException(ex);
		} finally {
	    	if (null != res) {
	    		try {
	    			res.close();
	    		} catch (Exception ex) { }
	    	}
		} 
	}  
	
	
	/**
	 * 执行 http get请求(指定编码)
	 * @param url
	 * @param content
	 * @param charset
	 * @return
	 */
	public static String postMap(String url, Map<String, String> params) {
		
		HttpPost m = new HttpPost(url);
		Charset c = Consts.UTF_8;
		 ArrayList<BasicNameValuePair> postParameters = new ArrayList<BasicNameValuePair>();
		 
		 if (null != params && params.size() > 0) {
			 for (Entry<String, String> e : params.entrySet()) {
				    postParameters.add(new BasicNameValuePair(e.getKey(), e.getValue()));
			 }
		 } 
		m.setEntity(new UrlEncodedFormEntity(postParameters, c)); 
		
		CloseableHttpClient cc = initClient(); 
		if (null != c) {
			// m.setHeader("Accept-Charset", c + ",*");
		} 
		CloseableHttpResponse res = null;
		long t1 = System.currentTimeMillis(); 
		try { 
			res = cc.execute(m);
			cc.close();
			StatusLine sl = res.getStatusLine();
			if (sl.getStatusCode() == HttpStatus.SC_OK) {
				String rtv = EntityUtils.toString(res.getEntity());

				long t2 = System.currentTimeMillis(); 
				long et = t2 - t1;
				if (et > threshold && threshold > 0) {
					String msg = "http-threshold-postmap, het[%s], threshold[%s], url[%s]\n params[%s]";
					msg = String.format(msg, et, threshold, url, params);
					logger.warn(msg);
				} 
				return rtv;
			} else {
				throw new RuntimeException("error StatusCode:" + sl.getStatusCode());
			}
		} catch (Exception ex) {
			checkExceptionWithPoolShutdown(ex);
			throw new RuntimeException(ex);
		} finally {
	    	if (null != res) {
	    		try {
	    			res.close();
	    		} catch (Exception ex) { }
	    	}
		} 
	}  
	
	/**
	 * post json 内容
	 * @param url
	 * @param json
	 * @return
	 */
	public static String postJson(String url, String json) {
		 
		HttpPost m = new HttpPost(url); 
		StringEntity entity = new StringEntity(json,
				ContentType.create("application/json", Consts.UTF_8));
		m.setEntity(entity); 
		
		CloseableHttpClient cc = initClient(); 
		CloseableHttpResponse res = null;
		
		long t1 = System.currentTimeMillis(); 
		
		try { 
			res = cc.execute(m);
			StatusLine sl = res.getStatusLine();
			if (sl.getStatusCode() == HttpStatus.SC_OK) {
				String rtv = EntityUtils.toString(res.getEntity());
				
				long t2 = System.currentTimeMillis(); 

				long et = t2 - t1;
				if (et > threshold && threshold > 0) {
					String msg = "http-threshold-postjson, het[%s], threshold[%s], url[%s]\n json[%s]";
					msg = String.format(msg, et, threshold, url, json);
					logger.warn(msg);
				} 
				return rtv;
			} else {
				throw new RuntimeException("url:" + url + ", error StatusCode:" + sl.getStatusCode());
			}
		} catch (Exception ex) {
			checkExceptionWithPoolShutdown(ex);
			throw new RuntimeException(ex);
		} finally {
	    	if (null != res) {
	    		try {
	    			res.close();
	    		} catch (Exception ex) { }
	    	}
		} 
	} 
	
	public static String postXml(String url, String xml) {
		
		HttpPost m = new HttpPost(url); 
		
		m.setHeader("Content-Type", "text/xml; charset=utf-8");
		//m.setHeader("SOAPAction", "http://tempuri.org/loginXML");
		
		StringEntity entity = new StringEntity(xml,
		        ContentType.create("text/xml", Consts.UTF_8));
		m.setEntity(entity); 
		 
		CloseableHttpClient cc = initClient();  
		CloseableHttpResponse res = null;

		long t1 = System.currentTimeMillis(); 
		try { 
			res = cc.execute(m);
			StatusLine sl = res.getStatusLine();
			if (sl.getStatusCode() == HttpStatus.SC_OK) {
				String rtv = EntityUtils.toString(res.getEntity());

				long t2 = System.currentTimeMillis(); 

				long et = t2 - t1;
				if (et > threshold && threshold > 0) {
					String msg = "http-threshold-postxml, het[%s], threshold[%s], url[%s]\n xml[%s]";
					msg = String.format(msg, et, threshold, url, xml);
					logger.warn(msg);
				} 
				return rtv;
			} else {
				throw new RuntimeException("error StatusCode:" + sl.getStatusCode());
			}
		} catch (Exception ex) {
			checkExceptionWithPoolShutdown(ex);
			throw new RuntimeException(ex);
		} finally {
	    	if (null != res) {
	    		try {
	    			res.close();
	    		} catch (Exception ex) { }
	    	}
		} 
	} 
	
	public static String postSoapXml(String url, String xml) {
		
		HttpPost m = new HttpPost(url); 
		
		m.setHeader("Content-Type", "application/soap+xml; charset=utf-8");
		m.setHeader("SOAPAction", "http://tempuri.org/loginXML");
		
		StringEntity entity = new StringEntity(xml,
		        ContentType.create("text/xml", Consts.UTF_8));
		m.setEntity(entity); 
		 
		CloseableHttpClient cc = initClient();  
		CloseableHttpResponse res = null;
		long t1 = System.currentTimeMillis(); 
		try { 
			res = cc.execute(m);
			StatusLine sl = res.getStatusLine();
			if (sl.getStatusCode() == HttpStatus.SC_OK) {
				String rtv = EntityUtils.toString(res.getEntity());
				long t2 = System.currentTimeMillis(); 

				long et = t2 - t1;
				if (et > threshold && threshold > 0) {
					String msg = "http-threshold-postsoap, het[%s], threshold[%s], url[%s]\n xml[%s]";
					msg = String.format(msg, et, threshold, url, xml);
					logger.warn(msg);
				} 
				return rtv;
			} else {
				throw new RuntimeException("error StatusCode:" + sl.getStatusCode());
			}
		} catch (Exception ex) {
			checkExceptionWithPoolShutdown(ex);
			throw new RuntimeException(ex);
		} finally {
	    	if (null != res) {
	    		try {
	    			res.close();
	    		} catch (Exception ex) { }
	    	}
		} 
	} 
	
	/**
	 * 获取http client
	 * @return
	 */
	public static HttpClient getClient() {
		return initClient();
	}
	
	private static void checkExceptionWithPoolShutdown(Exception ex) {
		
		if (null != ex) {
			String msg = ex.getMessage();
			if (null != msg && msg.contains("Connection pool shut down")) {
				HttpClient tmp = client;
				client = null;
				try { 
					tmp.getConnectionManager().shutdown(); 
				} catch (Throwable thr) { }
			}
		}
	}
}
