package com.cn.tianxia.api.utils.xyz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XYZ 对接信誉支付工具类
 */
public class HttpUtil {
	private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	public static String httpGet(String url,Map<String,String> map) {
		if (url == null || url.length() == 0) {
			logger.info("httpGet, url is null");
			return null;
		}
		StringBuilder urlBuilder = new StringBuilder(url);
		if(!HttpUtil.isEmpty(map)){
			urlBuilder.append("?");
			Map.Entry<String, String> entry = null;
			for(Iterator<Map.Entry<String, String>> itr = map.entrySet().iterator();itr.hasNext();){
				entry = itr.next();
				urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
			urlBuilder.setLength(urlBuilder.length() - 1);
		}
		
		logger.info("[信誉支付获取token] = " + (url+urlBuilder));
		
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(urlBuilder.toString());

		try {
			HttpResponse resp = httpClient.execute(httpGet);
			if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.info("httpGet fail, status code = "
						+ resp.getStatusLine().getStatusCode());
				return null;
			}
			HttpEntity entity = resp.getEntity();
			String result = EntityUtils.toString(entity);
			logger.info("Get = " + result);
			return result;
		} catch (Exception e) {
			logger.debug("httpGet exception, e = " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static String toPostIO(String reqParams,String url,String token) throws Exception{
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
        	
        	logger.info("[XYZ]信誉支付 post 请求路径: {},请求参数:{} ",url,reqParams);
        	
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            if(StringUtils.isNotBlank(token)){
            	conn.setRequestProperty("Authorization", "Bearer " + token);
            }
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
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
	
	public static boolean isEmpty(Map<?, ?> m) {
		return null == m || 0 == m.size();
	}
	
	public static final String MD5(String s) {
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes("UTF-8");

			MessageDigest mdInst = MessageDigest.getInstance("MD5");

			mdInst.update(btInput);

			byte[] md = mdInst.digest();

			int j = md.length;
			char[] str = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];
				str[(k++)] = hexDigits[(byte0 & 0xF)];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*public static String getParameterNames(HttpServletRequest request){
       String str = "";
        try {
            Enumeration enu = request.getParameterNames();
            while (enu.hasMoreElements()) {
                str = (String) enu.nextElement();
                logger.info("信誉支付回调 获取参数str = {}",str);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析请求参数异常:{}",e.getMessage());
        }
        return str;
    }*/
	
	public static String getRequestBody(HttpServletRequest request) {
		logger.info("信誉支付回调 开始获取参数...........");
        StringBuilder body = new StringBuilder();
        try {
            BufferedReader br = request.getReader();
            while(true){
                String info = br.readLine();
                if(info == null){
                    break;
                }
                body.append(info);
            }
            br.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("信誉支付回调 获取参数值:{}",body);
        return body.toString();
    }
	
	public static void main(String[] args) {
		
		String md5 = "bank_id=ICBC&charset=UTF-8&imit_credit_pay=0&mch_create_ip=127.0.0.1&mch_id=100290&nonce_str=943E85DDE4DB7D12F2E71F34FF60B2A4&notify_url=http://localhost:85/JJF/Notify/XYZNotify.do&out_trade_no=XYZbl1201811011404191404198766&sign_type=MD5&total_fee=10.0&version=1.0&key=35f6acbb3f4340518615deab26a9c093";
		System.out.println(HttpUtil.MD5(md5).toUpperCase());
	}
}