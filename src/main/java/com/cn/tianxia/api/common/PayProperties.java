package com.cn.tianxia.api.common;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PayProperties {

	/**
	 * pc支付类型
	 */
	public static Map<String, String> scanTypeMap = new HashMap<>();

	/**
	 * Mobile支付类型
	 */
	public static Map<String, String> scanMobileTypeMap = new HashMap<>();

	@SuppressWarnings("rawtypes")
	public Map<String, String> readProperties() {
		Properties pro = new Properties();
		InputStream in;
		in = this.getClass().getResourceAsStream("/conf/scanpay.properties");
		try {
			pro.load(in);
		} catch (Exception e) {
			System.out.println("load file faile." + e);
		}

		@SuppressWarnings("unchecked")
		Map<String, String> map = new HashMap<String, String>((Map) pro);
		Set propertySet = map.entrySet();
		for (Object o : propertySet) {
			Map.Entry entry = (Map.Entry) o;
			scanTypeMap.put(entry.getKey().toString(), entry.getValue().toString());
		}

		return scanTypeMap;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, String> readMobileProperties() {
		Properties pro = new Properties();
		InputStream in;
		in = this.getClass().getResourceAsStream("/conf/scanmobilepay.properties");
		try {
			pro.load(in);
		} catch (Exception e) {
			System.out.println("load file faile." + e);
		}

		@SuppressWarnings("unchecked")
		Map<String, String> map = new HashMap<String, String>((Map) pro);
		Set propertySet = map.entrySet();
		for (Object o : propertySet) {
			Map.Entry entry = (Map.Entry) o;
			scanMobileTypeMap.put(entry.getKey().toString(), entry.getValue().toString());
		}

		return scanMobileTypeMap;
	}
}
