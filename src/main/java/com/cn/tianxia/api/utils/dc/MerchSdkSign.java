/**
 * Project Name:payment
 * File Name:SignUtils.java
 * Package Name:cn.swiftpass.utils.payment.sign
 * Date:2014-6-27下午3:22:33
 *
 */

package com.cn.tianxia.api.utils.dc;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.alibaba.fastjson.JSONObject;

/**
 * ClassName:SignUtils
 * Function: 签名用的工具箱
 * Date:     2014-6-27 下午3:22:33
 * @author
 */
public class MerchSdkSign {

	/** <一句话功能简述>
	 * <功能详细描述>验证返回参数
	 * @param params
	 * @param key
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean checkSign(Map<String,String> params,String key){
		boolean result = false;
		if(params.containsKey("sign")){
			String sign = params.get("sign");
			params.remove("sign");
			StringBuilder buf = new StringBuilder((params.size() +1) * 10);
			MerchSdkSign.buildPrePayParams(buf,params,false);
			String preStr = buf.toString();
			String signRecieve = MD5.sign(preStr, "&key=" + key, "utf-8");
			result = sign.equalsIgnoreCase(signRecieve);
		}
		return result;
	}

	/** <一句话功能简述>
	 * <功能详细描述>验证返回参数
	 * @param params
	 * @param key
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean checkSign(Map<String,String> params,String signName ,String key){
		boolean result = false;
		if(params.containsKey(signName)){
			String sign = params.get(signName);
			TRACE.info("接收到的签名值:"+sign);
			params.remove(signName);

			String signRecieve=getSign(params, key);

			TRACE.info("签名值:"+signRecieve);

			result = sign.equalsIgnoreCase(signRecieve);
		}
		return result;
	}
	public static boolean checkSign(JSONObject params,String key){
		boolean result = false;
		if(params.containsKey("sign")){
			String sign = params.getString("sign");
			TRACE.info("接收签名="+sign);
			params.remove("sign");
			StringBuilder buf = new StringBuilder((params.size() +1) * 10);
			MerchSdkSign.buildPayParams(buf,params,false);
			String preStr = buf.toString();
			String signRecieve = MD5.sign(preStr, "&key=" + key, "utf-8");
			TRACE.info("签名结果="+signRecieve);

			result = sign.equalsIgnoreCase(signRecieve);
		}
		return result;
	}
	public static boolean checkSign(JSONObject params,String signName,String key){
		boolean result = false;
		if(params.containsKey(signName)){
			String sign = params.getString(signName);
			TRACE.info("接收到的签名值:"+sign);
			params.remove(signName);

			String signRecieve=getSign(params, key);

			TRACE.info("签名值:"+signRecieve);

			result = sign.equalsIgnoreCase(signRecieve);
		}else
		{
			TRACE.info("缺失签名值:"+signName);
		}
		return result;

	}
	public static String getSign(Map<String,String> params,String key){

		StringBuilder buf = new StringBuilder((params.size() +1) * 10);
		MerchSdkSign.buildPrePayParams(buf,params,false);
		//TRACE.info("签名数据:"+buf.toString()+"&key=" + key);
		return  MD5.sign(buf.toString(), "&key=" + key, "utf-8");

	}
	public static String getSign(JSONObject params,String key){

		StringBuilder buf = new StringBuilder((params.size() +1) * 10);
		MerchSdkSign.buildPrePayParams(buf,params,false);
		//TRACE.info("签名数据:"+buf.toString()+"&key=" + key);
		return  MD5.sign(buf.toString(), "&key=" + key, "utf-8");

	}
	/**
	 * 实体对象转为Map 再签名
	 * @param params
	 * @param key
	 * @return
	 */
 
	protected final static Logger TRACE = LoggerFactory.getLogger("TRACE");

	public static String getPaySign(List<String> params ,String key)
	{
		StringBuilder buf = new StringBuilder((params.size() +1) * 10);
		MerchSdkSign.buildPayParams(buf,params,false);
		//String preStr = buf.toString();
		return  MD5.sign(buf.toString(),  key, "utf-8");
	}
	public static String getPaySignV2(List<String> params ,String key)
	{
		StringBuilder buf = new StringBuilder((params.size() +1) * 10);
		MerchSdkSign.buildPayParamsV2(buf,params,false);
		String preStr = buf.toString();
		return  MD5.sign(preStr,  key, "utf-8").substring(0,16).toUpperCase();
	}


	public static boolean checkSign(List<String> params ,String key,String sign){
		boolean result = false;
		String oSign = getPaySign(params,key);
		TRACE.info("检验签名数据="+oSign);
		result = oSign.equalsIgnoreCase(sign);
		return result;
	}
	public static boolean checkSignV2(List<String> params ,String key,String sign){
		boolean result = false;
		String oSign = getPaySignV2(params,key);
		TRACE.info("检验签名数据="+oSign);
		result = oSign.equalsIgnoreCase(sign);
		return result;
	}

	/**
	 * 过滤参数
	 * @author
	 * @param sArray
	 * @return
	 */
	public static Map<String, String> paraFilter(Map<String, String> sArray) {
		Map<String, String> result = new HashMap<String, String>(sArray.size());
		if (sArray == null || sArray.size() <= 0) {
			return result;
		}
		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.isEmpty() || key.equalsIgnoreCase("sign")) {
				continue;
			}
			result.put(key, value);
		}
		return result;
	}

	/** <一句话功能简述>
	 * <功能详细描述>将map转成String
	 * @param payParams
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String payParamsToString(Map<String, String> payParams){
		return payParamsToString(payParams,false);
	}

	public static String payParamsToString(Map<String, String> payParams,boolean encoding){
		return payParamsToString(new StringBuilder(),payParams,encoding);
	}
	/**
	 * @author
	 * @param payParams
	 * @return
	 */
	public static String payParamsToString(StringBuilder sb,Map<String, String> payParams,boolean encoding){
		buildPrePayParams(sb,payParams,encoding);
		return sb.toString();
	}

	/**
	 * @author
	 * @param payParams
	 * @return
	 */
	public static void buildPrePayParams(StringBuilder sb,Map<String, String> payParams,boolean encoding){
		List<String> keys = new ArrayList<String>(payParams.keySet());
		Collections.sort(keys);
		for(String key : keys){
			String str = payParams.get(key);
			if (str == null || str.length() == 0)
			{
				//空串不参与sign计算
				continue;
			}
			sb.append(key).append("=");
			if(encoding){
				sb.append(urlEncode(str));
			}else{
				sb.append(str);
			}
			sb.append("&");
		}
		sb.setLength(sb.length() - 1);
	}
	public static void buildPrePayParams(StringBuilder sb,JSONObject payParams,boolean encoding){
		List<String> keys = new ArrayList<String>(payParams.keySet());
		Collections.sort(keys);
		for(String key : keys){
			String str = payParams.getString(key);
			if (str == null || str.length() == 0)
			{
				//空串不参与sign计算
				continue;
			}
			sb.append(key).append("=");
			if(encoding){
				sb.append(urlEncode(str));
			}else{
				sb.append(str);
			}
			sb.append("&");
		}
		sb.setLength(sb.length() - 1);
	}

	/**
	 * @author
	 * @param payParams
	 * @return
	 */
	public static void buildPayParams(StringBuilder sb,List<String> keys ,boolean encoding){
		for(String key : keys){
			if(encoding){
				sb.append(urlEncode(key));
			}else{
				sb.append(key);
			}
		}
		sb.setLength(sb.length() - 1);
	}
	public static void buildPayParamsV2(StringBuilder sb,List<String> keys ,boolean encoding){
		for(String key : keys){
			if(encoding){
				sb.append(urlEncode(key));
			}else{
				sb.append(key);
			}
		}
		sb.setLength(sb.length());
	}

	public static void buildPayParams(StringBuilder sb,JSONObject payParams,boolean encoding){
		List<String> keys = new ArrayList<String>(payParams.keySet());
		Collections.sort(keys);
		for(String key : keys){
			String str = payParams.getString(key);
			if (str == null || str.length() == 0)
			{
				//空串不参与sign计算
				continue;
			}
			sb.append(key).append("=");
			if(encoding){
				sb.append(urlEncode(str));
			}else{
				sb.append(str);
			}
			sb.append("&");
		}
		sb.setLength(sb.length() - 1);
	}

	public static String urlEncode(String str){
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (Throwable e) {
			return str;
		}
	}


	public static Element readerXml(String body,String encode) throws DocumentException {
		SAXReader reader = new SAXReader(false);
		InputSource source = new InputSource(new StringReader(body));
		source.setEncoding(encode);
		Document doc = reader.read(source);
		Element element = doc.getRootElement();
		return element;
	}

}

