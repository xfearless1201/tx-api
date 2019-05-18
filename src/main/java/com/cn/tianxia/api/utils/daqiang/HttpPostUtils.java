package com.cn.tianxia.api.utils.daqiang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONObject;

public class HttpPostUtils {
	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param, String charSet) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new OutputStreamWriter(conn.getOutputStream(), charSet);
			// 发送请求参数
			out.write(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charSet));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * API httpclientPost请求方法
	 * 
	 * @param url
	 *            地址
	 * @param reqStr
	 *            参数应该是 name1=value1&name2=value2 的形式。
	 * @param charSet
	 *            编码格式
	 * @return 响应内容
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String httpClientPost(String url, String reqStr, String charSet)
			throws ClientProtocolException, IOException {
		// 1.创建client请求
		CloseableHttpClient client = HttpClientBuilder.create().build();
		// 2.打开URL
		HttpPost httpPost = new HttpPost(url);
		// 3.设置类型
		httpPost.setHeader("content-type", "application/x-www-form-urlencoded;charset=utf-8");
		// 4.设置请求参数
		StringEntity params = new StringEntity(reqStr,charSet);
		httpPost.setEntity(params);
		
		//System.out.println("executing request: " + httpPost.getRequestLine());
		// 5.执行
		HttpResponse response = client.execute(httpPost);
		
		String result = EntityUtils.toString(response.getEntity(), charSet);

		return result;
	}
}
