  
package com.cn.tianxia.api.utils.jfk;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;


  
/**  
 * @project  
 * @description 单向https请求处理
 * @author 
 * @modify  
 * @version  
 */
public class SimpleHttpsClient {
	
	private Map<Integer, Integer> registerPortList = new HashMap<Integer, Integer>();
	
	public SimpleHttpsClient() {
		Protocol.registerProtocol("https", new Protocol("https", new SimpleHttpsSocketFactory(), 443));
		registerPort(443);
	}

	/**
	 * �ύPOST
	 * @param url
	 * @param
	 * @return
	 */
	public HttpSendResult postRequest(String url, Map<String, String> params, int timeout) {
		HttpSendResult result = new HttpSendResult();
		PostMethod postMethod = new PostMethod(url);
		NameValuePair[] data = this.createNameValuePair(params);
		postMethod.setRequestBody(data);
		Integer port = this.getPort(url);
		if (isRegisterPort(port)) {
			Protocol myhttps = new Protocol("https", new SimpleHttpsSocketFactory(), port);
			Protocol.registerProtocol("https ", myhttps);
			registerPort(port);
		}

		HttpClient client = new HttpClient();
		client.getParams().setSoTimeout(timeout);
		client.getParams().setContentCharset("utf-8");
		try {
			int status = client.executeMethod(postMethod);
			InputStream is = postMethod.getResponseBodyAsStream();
			String responseBody = IOUtils.toString(is, "UTF-8");
			result.setStatus(status);
			result.setResponseBody(responseBody);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			postMethod.releaseConnection();
		}

		return result;
	}

	/**
	 * 
	* @Title: appendUrlString
	* @Description: 拼接http get "?"
	* @param url
	* @param getParamString
	* @return    String
	* @throws
	 */
	private String appendUrlString(String url,String getParamString)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		if(StringUtils.isNotEmpty(getParamString))
		{
			if (url.indexOf("?") != -1 && url.indexOf("=") != -1) {
				sb.append("&");
			} else {
				sb.append("?");
			}
			
			sb.append(getParamString);
		}
		return sb.toString();
			
	}
	
	
	  
	/**  
	* (non-Javadoc)  
	* @see common.util.httpclient.impl.SimpleHttpsClient#getRequest(java.lang.String, java.lang.String, int)  
	*/  
	
	public HttpSendResult getRequest(String url, String getParamString,
			int timeout) {
		return getRequest(url, getParamString, timeout, false);
	}

	/**
	 * getRequest
	 * @param url
	 * @param params
	 * @return
	 */
	
	public HttpSendResult getRequest(String url, String getParamString, int timeout, boolean followRedirects) {
		HttpSendResult result = new HttpSendResult();
		Integer port = this.getPort(url);
		if (isRegisterPort(port)) {
			Protocol myhttps = new Protocol("https", new SimpleHttpsSocketFactory(), port);
			Protocol.registerProtocol("https ", myhttps);
			registerPort(port);
		}

		url = this.appendUrlString(url, getParamString);

		HttpClient httpclient = new HttpClient();
		httpclient.getParams().setSoTimeout(timeout);
		GetMethod httpget = new GetMethod(url);
		try {
			// 设置自动跳转
			httpget.setFollowRedirects(followRedirects);
			int status = httpclient.executeMethod(httpget);
			result.setStatus(status);
			if(status == 301 || status == 302){
				Header locationHeader = httpget.getResponseHeader("location");
				System.out.println("location header:" + locationHeader.getValue());
				result.setResponseBody(locationHeader.getValue());
			}else{
				InputStream is = httpget.getResponseBodyAsStream();
				String responseBody = IOUtils.toString(is, "UTF-8");
				result.setResponseBody(responseBody);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			httpget.releaseConnection();
		}

		return result;
	}
	

	private boolean isRegisterPort(Integer port) {
		return registerPortList.get(port) != null;
	}

	private void registerPort(Integer port) {
		registerPortList.put(port, port);
	}

	private Integer getPort(String uri) {
		try {
			URL url = new URL(uri);
			int port = url.getPort();
			if (port == -1) {
				if (uri.indexOf("https://") == 0) {
					port = 443;
				} else {
					port = 80;
				}
			}
			return port;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private NameValuePair[] createNameValuePair(Map<String, String> params) {
		NameValuePair[] pairs = new NameValuePair[params.size()];
		int index = 0;
		for (String key : params.keySet()) {
			pairs[index++] = new NameValuePair(key, params.get(key));
		}

		return pairs;
	}

	private String appendUrlParam(String url, Map<String, String> params) {
		String result = "";
		if (url.indexOf("?") != -1 && url.indexOf("=") != -1) {
			result = url + "&";
		} else {
			result = url + "?";
		}

		for (String key : params.keySet()) {
			result = result + key + "=" + params.get(key) + "&";
		}

		if (result.charAt(result.length() - 1) == '&') {
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}

	private class SimpleHttpsSocketFactory implements ProtocolSocketFactory {
		private SSLContext sslcontext = null;

		private SSLContext createEasySSLContext() {
			try {
				X509TrustManager trustMgr = new X509TrustManager() {

					public void checkClientTrusted(X509Certificate ax509certificate[], String s)
							throws CertificateException {
					}

					public void checkServerTrusted(X509Certificate ax509certificate[], String s)
							throws CertificateException {
					}

					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

				};
				TrustManager trustMgrs[] = { trustMgr };

				SSLContext context = SSLContext.getInstance("SSL");
				context.init(null, trustMgrs, null);
				return context;
			} catch (Exception e) {
				e.printStackTrace();
				throw new HttpClientError(e.toString());
			}
		}

		/**
		 * Retrieves SSL context.
		 * 
		 * @return SSLContext.
		 */
		private SSLContext getSSLContext() {
			if (this.sslcontext == null) {
				this.sslcontext = createEasySSLContext();
			}
			return this.sslcontext;
		}

		/**
		 * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int,java.net.InetAddress,int)
		 */
		public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort)
				throws IOException, UnknownHostException {

			return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
		}

		public Socket createSocket(final String host, final int port, final InetAddress localAddress,
				final int localPort, final HttpConnectionParams params) throws IOException,
				UnknownHostException, ConnectTimeoutException {
			if (params == null) {
				throw new IllegalArgumentException("Parameters may not be null");
			}
			int timeout = params.getConnectionTimeout();
			SocketFactory socketfactory = getSSLContext().getSocketFactory();
			if (timeout == 0) {
				return socketfactory.createSocket(host, port, localAddress, localPort);
			} else {
				Socket socket = socketfactory.createSocket();
				SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
				SocketAddress remoteaddr = new InetSocketAddress(host, port);
				socket.bind(localaddr);
				socket.connect(remoteaddr, timeout);
				return socket;
			}
		}

		public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
			return getSSLContext().getSocketFactory().createSocket(host, port);
		}

		public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
				throws IOException, UnknownHostException {
			return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		public boolean equals(Object obj) {
			return ((obj != null) && obj.getClass().equals(SSLSocketFactory.class));
		}

		public int hashCode() {
			return SimpleHttpsSocketFactory.class.hashCode();
		}
	}
}
