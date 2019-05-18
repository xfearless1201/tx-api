package com.cn.tianxia.api.utils.xm;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MD5s {
	private static final String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

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

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String MD5Encode(String origin, String charsetname) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			if ((charsetname == null) || ("".equals(charsetname)))
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes()));
			else
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charsetname)));
		} catch (Exception localException) {
		}
		return resultString;
	}

	public static String Sign(String content, String key) {
		String signStr = "";
		signStr = content + "&key=" + key;
		return MD5s.MD5(signStr).toUpperCase();
	}

	/**
	 * 加密
	 * 
	 * @param origin
	 *            数据源
	 * @param charsetname
	 *            编码方式
	 * @param EncodeType
	 *            加密方式,如：MD5,SHA-256
	 * @return
	 */
	public static String EncodeStr(String origin, String charsetname,
			String EncodeType) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance(EncodeType);
			if ((charsetname == null) || ("".equals(charsetname)))
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes()));
			else
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charsetname)));
		} catch (Exception localException) {
		}
		return resultString;
	}
	public static JSONObject sign(JSONObject reqData, String signKey) {
		try {
			if ("".equals(signKey) || null == signKey) {
				signKey = "";
			}
			Map<String, String> map = new HashMap<String, String>();
			map = JSON.toJavaObject(reqData, Map.class);
			String noSignStr = FormatBizQueryParaMap(map, false);// 未签名的字符串
			String sign = Sign(noSignStr, signKey);// 签名
			reqData.put("sign", sign);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return reqData;
	}
	public static String FormatBizQueryParaMap(Map<String, String> paraMap,
			boolean urlencode) throws Exception {
		String buff = "";
		try {
			List infoIds = new ArrayList(paraMap.entrySet());

			Collections.sort(infoIds,
					new Comparator<Map.Entry<String, String>>() {
						public int compare(Map.Entry<String, String> o1,
								Map.Entry<String, String> o2) {
							return ((String) o1.getKey()).toString().compareTo(
									(String) o2.getKey());
						}
					});
			for (int i = 0; i < infoIds.size(); i++) {
				Map.Entry item = (Map.Entry) infoIds.get(i);

				if (item.getKey() != "") {
					String key = String.valueOf(item.getKey());
					String val = String.valueOf(item.getValue());
					if (urlencode) {
						val = URLEncoder.encode(val, "utf-8");
					}
					if ("".equals(val)) {
						continue;
					}
					buff = buff + key + "=" + val + "&";
					// buff = buff + key.toLowerCase() + "=" + val + "&";
				}
			}
			if (!buff.isEmpty())
				buff = buff.substring(0, buff.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buff;
	}
}