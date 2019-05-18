package com.cn.tianxia.api.utils.txkj;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


public class SignUtil {
	public static String sortData(Map<String, String> sourceMap) {
		Map<String, String> sortedMap = new TreeMap<String, String>();
		sortedMap.putAll(sourceMap);
		Set<Entry<String, String>> entrySet = sortedMap.entrySet();
		StringBuffer sbf = new StringBuffer();
		for (Entry<String, String> entry : entrySet) {
			if ((entry.getValue()!=null)&&(entry.getValue().length()>0)) {
				sbf.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
		}
		String returnStr = sbf.toString();
		if (returnStr.endsWith("&")) {
			returnStr = returnStr.substring(0, returnStr.lastIndexOf('&'));
		}
		return returnStr;
	}
	public static String sortDataWithoutSign(Map<String, String> sourceMap) {
		Map<String, String> sortedMap = new TreeMap<String, String>();
		sortedMap.putAll(sourceMap);
		Set<Entry<String, String>> entrySet = sortedMap.entrySet();
		StringBuffer sbf = new StringBuffer();
		for (Entry<String, String> entry : entrySet) {
			if ((entry.getValue()!=null)&&(entry.getValue().length()>0)  && !"sign".equals(entry.getKey()) && !"sign_type".equals(entry.getKey())) {
				sbf.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
		}
		String returnStr = sbf.toString();
		if (returnStr.endsWith("&")) {
			returnStr = returnStr.substring(0, returnStr.lastIndexOf('&'));
		}
		return returnStr;
	}
	
	public static void putIfNotNull(Map<String, String> paramMap,String key,String value){
		if(null != value && !"".equals(value)) {
			paramMap.put(key, value);
		}
	}
}
