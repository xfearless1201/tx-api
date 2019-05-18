package com.cn.tianxia.api.utils.xyz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

public class XMLUtils {

	private final static Logger logger = LoggerFactory.getLogger(XMLUtils.class);
	private static DocumentBuilderFactory factory = null;
	private static DocumentBuilder builder = null;
	private static Document document;
	
	//静态模块
	static{//初始化doc工具类
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生产XML请求报文
	 * @param data
	 * @return
	 */
	public static String createXMlRequest(Map<String,String> data){
		StringBuffer sb = new StringBuffer("<xml>");
		for(Map.Entry<String, String> map : data.entrySet()){
			String key = map.getKey();
			String val = map.getValue();
			sb.append("<").append(key).append(">");
			if(key.equalsIgnoreCase("total_fee")){
				sb.append(Integer.parseInt(val));
			}else{
				sb.append(val);
			}
			sb.append("</").append(key).append(">");
		}
		sb.append("</xml>");
		
		return sb.toString();
	}
	
	/**
	 * 解析xml文件
	 * @param xml
	 * @return
	 */
	public static JSONObject formatXMlToJson(String xml) throws Exception{
		//创建解析结果存储对象
		JSONObject data = new JSONObject();
		Document document = DocumentHelper.parseText(xml);
		Element element = document.getRootElement();
		List<Element> list = element.elements();
		for (Element ele : list) {
			String key = ele.getName();//参数名
			String val = ele.getStringValue();//获取值
			data.put(key, val);
		}
		return data;
	}
	
	public static HashMap<String,String> formatXMlToMap(String xml) throws Exception{
		//创建解析结果存储对象
		HashMap<String,String> data = new HashMap<String,String>();
		Document document = DocumentHelper.parseText(xml);
		Element element = document.getRootElement();
		List<Element> list = element.elements();
		for (Element ele : list) {
			String key = ele.getName();//参数名
			String val = ele.getStringValue();//获取值
			data.put(key, val);
		}
		return data;
	}
	
}
