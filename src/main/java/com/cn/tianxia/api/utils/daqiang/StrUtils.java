package com.cn.tianxia.api.utils.daqiang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import net.sf.json.JSONObject;

public class StrUtils {
	/*
	 * 产生订单号 商户号+时间+6随机数组成(共20位)
	 */
	public static String produceOrderNo(String MerNo) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
		Date d = new Date();
		String str1 = df.format(d).toString();
		String str2 = "";
		for (int i = 0; i < 6; i++) {
			str2 += (int) (Math.random() * 10);
		}
		String str3 = MerNo + str1;
		return str3;
	}

	/**
	 * 判断字符串是否为空
	 * <ul>
	 * <li>isEmpty(null) = true</li>
	 * <li>isEmpty("") = true</li>
	 * <li>isEmpty(" ") = true</li>
	 * <li>isEmpty("abc") = false</li>
	 * </ul>
	 * 
	 * @param value
	 *            目标字符串
	 * @return true/false
	 */
	public static boolean isEmpty(String value) {
		int strLen;
		if (value == null || (strLen = value.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(value.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 先排序，map转Str(key=val&key=val......)
	 * 
	 * @param packageParams
	 * @return
	 */
	public static String createRetStr(Map<String, String> packageParams) {
		List<String> list = new ArrayList<String>(packageParams.keySet());
		Collections.sort(list);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			String k = list.get(i);
			String v = packageParams.get(list.get(i));
			
			if (!StrUtils.isEmpty(v) && k != "sign") {
				sb.append(k + "=" + v + "&");
			}
		}

		String sss = sb.toString().substring(sb.toString().length() - 1, sb.toString().length());
		if (sss.equals("&")) {
			sss = sb.toString().substring(0, sb.toString().length() - 1);
		}
		return sss;
	}

	/**
	 * 先排序，map转Str(key=val,key=val......)
	 * 
	 * @param packageParams
	 * @return
	 */
	public static String createReqparam(Map<String, String> payDetailsParam) {
		List<String> list = new ArrayList<String>(payDetailsParam.keySet());
		Collections.sort(list);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			String k = list.get(i);
			String v = payDetailsParam.get(list.get(i));

			if (null != v && !"".equals(v)) {
				sb.append(k + "=" + v + "&");
			}
		}

		String sss = sb.toString().substring(sb.toString().length() - 1, sb.toString().length());
		if (sss.equals("&")) {
			sss = sb.toString().substring(0, sb.toString().length() - 1);
		}
		return sss;
	}

	/**
	 * 过滤空键、值，并排序
	 * 
	 * @param sArray
	 * @return
	 */
	public static Map<String, Object> paraFilter(Map<String, Object> sArray) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (sArray == null || sArray.size() <= 0) {
			return result;
		}
		for (String key : sArray.keySet()) {
			Object value = sArray.get(key);
			if (value == null || value.equals("")) {
				continue;
			}
			result.put(key, value);
		}
		return result;
	}

	/**
	 * 拼接字符key=val&key=val......
	 * 
	 * @param params
	 * @return
	 */
	public static String createLinkString(Map<String, Object> params, String keyValue) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			Object value = params.get(key);
			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr + "&key=" + keyValue;
	}

	/**
	 * 获取签名字符串
	 * 
	 * @return
	 */
	public static String getSignStr(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		// Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			Object value = params.get(key);
			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value + "";
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}

	/**
	 * <一句话功能简述> <功能详细描述>request转字符串
	 * 
	 * @param request
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String parseRequst(HttpServletRequest request) {
		String body = "";
		try {
			ServletInputStream inputStream = request.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
			while (true) {
				String info = br.readLine();
				if (info == null) {
					break;
				}
				if (body == null || "".equals(body)) {
					body = info;
				} else {
					body += info;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return body;
	}

	/**
	 * 获得Request转Map
	 * 
	 * @param paraMap
	 * @return
	 */
	public static Map<String, String> getResultMap(Map paraMap) {
		// 返回值Map
		Map returnMap = new HashMap();
		Iterator entries = paraMap.entrySet().iterator();
		Map.Entry entry;
		String name = "";
		String value = "";
		while (entries.hasNext()) {
			entry = (Map.Entry) entries.next();
			name = (String) entry.getKey();
			Object valueObj = entry.getValue();
			if (null == valueObj) {
				value = "";
			} else if (valueObj instanceof String[]) {
				String[] values = (String[]) valueObj;
				for (int i = 0; i < values.length; i++) {
					value = values[i] + ",";
				}
				value = value.substring(0, value.length() - 1);
			} else {
				value = valueObj.toString();
			}
			returnMap.put(name, value);
		}
		return returnMap;
	}

	/**
	 * 解析XML字符串
	 * 
	 * @param xml
	 * @return
	 * @throws DocumentException
	 */
	public static Map<String, Object> parseXmlStr(String xml) throws DocumentException {
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		return parseElement(root);
	}

	/**
	 * 解析Element
	 * 
	 * @param root
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> parseElement(Element root) {
		Iterator<Element> rootItor = root.elementIterator();
		Map<String, Object> rMap = new HashMap<>();
		while (rootItor.hasNext()) {
			Element tmpElement = rootItor.next();
			String name = tmpElement.getName();
			String content = tmpElement.getText();
			rMap.put(name, content);
		}
		return rMap;
	}

	/**
	 * String转成Map
	 * 
	 * @param rasStr
	 */
	public static Map<String, String> StrToMap(String rasStr) {
		Map<String, String> map = null;
		if (rasStr != null && rasStr != "") {
			map = new LinkedHashMap<String, String>();
			String[] s1 = rasStr.split("&");
			String[] s2 = new String[2];
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < s1.length; i++) {
				s2 = s1[i].split("=", 2);
				map.put(s2[0], s2[1]);
				if (!s2[0].equals("sign")) {
					sb.append(s2[0] + "=" + s2[1] + "&");
				}
			}
		}
		return map;
	}

	/**
	 * JSonObject转
	 * 
	 * @param object
	 * @return
	 */
	public static HashMap<String, String> JsonToMap(Object object) {
		HashMap<String, String> data = new HashMap<String, String>();
		// 将json字符串转换成jsonObject
		JSONObject jsonObject = JSONObject.fromObject(object);
		Iterator it = jsonObject.keys();
		// 遍历jsonObject数据，添加到Map对象
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			String value = (String) jsonObject.get(key);
			data.put(key, value);
		}
		return data;
	}

	/**
	 * 返回时间字符串 yyyy年MM月dd日 类型
	 * 
	 * @param dateTime
	 * @return
	 */
	public String getDateymd(Date dateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(dateTime).toString();
	}

	/**
	 * 返回时间字符串HHmmss类型
	 * 
	 * @param dateTime
	 * @return
	 */
	public String getDatehms(Date dateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		return sdf.format(dateTime).toString();
	}

	/**
	 * 返回时间字符串 yyyy年MM月dd日 hh时mm分ss秒类型
	 * 
	 * @param dateTime
	 * @return
	 */
	public static String getDateymdhms(Date dateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(dateTime).toString();
	}

	public static String parseXML(Map<String, String> parameters) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<ScanPayRequest>");
		Set es = parameters.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (null != v && !"".equals(v) && !"appkey".equals(k)) {
				sb.append("<" + k + ">" + parameters.get(k) + "</" + k + ">\n");
			}
		}
		sb.append("</ScanPayRequest>");
		// sb.append("</xml>");
		return sb.toString();
	}
}
