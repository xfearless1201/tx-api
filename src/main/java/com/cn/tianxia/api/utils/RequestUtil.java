package com.cn.tianxia.api.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class RequestUtil {
	public static Map<String, String[]> getRequestMap(ServletRequest request) throws Exception, Exception {
		Class clazz = request.getClass();
		Field requestField = clazz.getDeclaredField("request");
		requestField.setAccessible(true);
		Object innerRequest = requestField.get(request);// 获取到request对象

		// 设置尚未初始化 (否则在获取一些参数的时候，可能会导致不一致)
		Field field = innerRequest.getClass().getDeclaredField("parametersParsed");
		field.setAccessible(true);
		field.setBoolean(innerRequest, false);

		Field coyoteRequestField = innerRequest.getClass().getDeclaredField("coyoteRequest");
		coyoteRequestField.setAccessible(true);
		Object coyoteRequestObject = coyoteRequestField.get(innerRequest);// 获取到coyoteRequest对象

		Field parametersField = coyoteRequestObject.getClass().getDeclaredField("parameters");
		parametersField.setAccessible(true);
		Object parameterObject = parametersField.get(coyoteRequestObject);// 获取到parameter的对象
		// 获取hashtable来完成对参数变量的修改
		Field hashTabArrField = parameterObject.getClass().getDeclaredField("paramHashValues");
		hashTabArrField.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<String, String[]> map = (Map<String, String[]>) hashTabArrField.get(parameterObject);
		/* map.put("fuck" , new String[] {"fuck you"}); */
		// 也可以通过下面的方法，不过下面的方法只能添加参数，如果有相同的key，会追加参数，即，同一个key的结果集会有多个
		// Method method =
		// parameterObject.getClass().getDeclaredMethod("addParameterValues" ,
		// String.class , String[].class);
		// method.invoke(parameterObject , "fuck" , new String[] {"fuck you!" ,
		// "sssss"});
		return map;
	}

	public static String readRequest(HttpServletRequest request) throws IOException {
		StringBuilder sb = new StringBuilder();
		try {
			String line;
			while ((line = request.getReader().readLine()) != null) {
				sb.append(line);
			}
		} finally {
			request.getReader().close();
		}
		return sb.toString();
	}

	/***
	 * 获取 request 中 json 字符串的内容
	 * 
	 * @param request
	 * @return : <code>byte[]</code>
	 * @throws IOException
	 */
	public static String getRequestJsonString(HttpServletRequest request) throws IOException {
		String submitMehtod = request.getMethod();
		// GET
		try {
			if (submitMehtod.equals("GET")) {
				return new String(request.getQueryString().getBytes("iso-8859-1"), "utf-8").replaceAll("%22", "\"");
				// POST
			} else {
				return getRequestPostStr(request);
			}
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 描述:获取 post 请求内容
	 * 
	 * <pre>
	 *  
	 * 举例：
	 * </pre>
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String getRequestPostStr(HttpServletRequest request) throws IOException {
		byte buffer[] = getRequestPostBytes(request);
		String charEncoding = request.getCharacterEncoding();
		if (charEncoding == null) {
			charEncoding = "UTF-8";
		}
		if (buffer == null) {
			return "";
		}
		return new String(buffer, charEncoding);
	}

	public static String getRequestGetStr(HttpServletRequest request) throws IOException {
		String qstr = request.getQueryString();
		if (qstr == null || "".equals(qstr)) {
			return "";
		}
		byte buffer[] = qstr.getBytes("iso-8859-1");
		String str = new String(buffer, "utf-8").replaceAll("%22", "\"");
		return str;
	}

	/**
	 * 描述:获取 post 请求的 byte[] 数组
	 * 
	 * <pre>
	 *  
	 * 举例：
	 * </pre>
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
		int contentLength = request.getContentLength();
		if (contentLength < 0) {
			return null;
		}
		byte buffer[] = new byte[contentLength];
		for (int i = 0; i < contentLength;) {

			int readlen = request.getInputStream().read(buffer, i, contentLength - i);
			if (readlen == -1) {
				break;
			}
			i += readlen;
		}
		return buffer;
	}

}
