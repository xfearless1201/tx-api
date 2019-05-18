package com.cn.tianxia.api.pay.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.MD5Util;
import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.utils.ly.HttpMethod;
import com.cn.tianxia.api.utils.ly.HttpSendModel;
import com.cn.tianxia.api.utils.ly.SimpleHttpResponse;
import com.cn.tianxia.api.utils.mkt.HttpUtil;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.qyf.ToolKit;
import com.cn.tianxia.api.utils.ys.DateUtil;

import net.sf.json.JSONObject;


/**
 * 澳门银河新接入云付支付
 */

public class YFZFPayServiceImpl implements PayService {

	private String payUrl;

	private String paySecret;

	private String notifyUrl;

	private String merNo;

	private String merKey;

	private final static Logger logger = LoggerFactory.getLogger(YFZFPayServiceImpl.class);

	// private String bank_url = "http://api.all-linepay.com/pay.do";
	public YFZFPayServiceImpl(Map<String, String> pmap) {
		JSONObject jo = JSONObject.fromObject(pmap);
		if (null != pmap) {
		    if(jo.containsKey("payUrl")){
                payUrl = jo.get("payUrl").toString();// 支付地址
            }
            if(jo.containsKey("paySecret")){
                paySecret = jo.get("paySecret").toString();// 商户密钥
            }
            if(jo.containsKey("merNo")){
                merNo = jo.get("merNo").toString();// 商户号
            }
            if(jo.containsKey("merKey")){
                merKey=jo.get("merKey").toString();//商户产品密钥
            }
            if(jo.containsKey("notifyUrl")){
                notifyUrl=jo.get("notifyUrl").toString();//支付回调地址
            }
		}
	}

	/**
	 * pc端二维码支付
	 *
	 * @param scanMap
	 * @return
	 */
	public JSONObject pcH5ScanPay(Map<String, String> scanMap) {
		String sign = "";

		List<String> keys = new ArrayList<String>(scanMap.keySet());
		Collections.sort(keys);

		StringBuilder sb = new StringBuilder();

		String key, value;
		for (int i = 0; i < keys.size(); i++) {
			key = keys.get(i);
			value = scanMap.get(key);

			if (sb.length() == 0) {
				sb.append(key + "=" + value);
			} else {
				sb.append("&" + key + "=" + value);
			}
		}

		if (sb.length() > 0) {
			sb.append("&");
		}
		sb.append("paySecret=");
		sb.append(paySecret);

		sign = ToolKit.MD5(sb.toString(), "UTF-8");

		scanMap.put("sign", sign);

		logger.info("签名字符原串：" + sb.toString());

		String resultstr = "";
		try {
			resultstr = RequestForm(payUrl, scanMap);
			logger.info("响应数据：" + resultstr);

			if (isJson(resultstr)) {
				return getReturnJson("success","",resultstr.toString());
			} else {
				return getReturnJson("error", "", resultstr.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			return getReturnJson("error", "", resultstr);
		}
	}

	/**
	 * HTTP post 请求
	 * 
	 * @param Url
	 * @param Parms
	 * @return
	 */
	public String RequestForm(String Url, Map<String, String> Parms) {
		if (Parms.isEmpty()) {
			return "参数不能为空！";
		}
		String PostParms = "";
		int PostItemTotal = Parms.keySet().size();
		int Itemp = 0;
		for (String key : Parms.keySet()) {
			PostParms += key + "=" + Parms.get(key);
			Itemp++;
			if (Itemp < PostItemTotal) {
				PostParms += "&";
			}
		}
		logger.info("【请求参数】：" + PostParms);
		HttpSendModel httpSendModel = new HttpSendModel(Url + "?" + PostParms);
		logger.info("【后端请求】：" + Url + "?" + PostParms);
		httpSendModel.setMethod(HttpMethod.GET);
		SimpleHttpResponse response = null;
		try {
			response = HttpUtil.doRequest(httpSendModel, "UTF-8");
		} catch (Exception e) {
			return e.getMessage();
		}
		return response.getEntityString();
	}

	//网银支付
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		return null;
	}

	//扫码支付
	@Override
	public JSONObject smPay(PayEntity payEntity) {
		String serialNo=generateserialNo();
		Double transAmt = payEntity.getAmount();//交易金额
		String orderNo = payEntity.getOrderNo();//商户订单号
		String transDate=DateUtil.getCurrentDate("yyyyMMdd");
		String transTime=DateUtil.getCurrentDate("yyyyMMddHHmmss");
		String merIp=payEntity.getIp();
		String orderDesc="订单商品内容描述";//订单商品内容描述

		String pay_code = payEntity.getPayCode();
		String mobile = payEntity.getMobile();
		String userName = payEntity.getUsername();

		Map<String, String> scanMap = new HashMap<>();
		scanMap.put("transId",pay_code);
		scanMap.put("serialNo",serialNo);
		scanMap.put("transAmt", String.valueOf(transAmt));
		scanMap.put("orderNo", orderNo);
		scanMap.put("transDate",transDate);
		scanMap.put("transTime",transTime);
		scanMap.put("merIp",merIp);
		scanMap.put("notifyUrl", this.notifyUrl);
		scanMap.put("orderDesc", orderDesc);
		scanMap.put("merNo",this.merNo);
		scanMap.put("merKey",this.merKey);

		String sign = "";

		List<String> keys = new ArrayList<String>(scanMap.keySet());
		Collections.sort(keys);

		StringBuilder sb = new StringBuilder();

		String key, value;
		for (int i = 0; i < keys.size(); i++) {
			key = keys.get(i);
			value = scanMap.get(key);

			if (sb.length() == 0) {
				sb.append(key + "=" + value);
			} else {
				sb.append("&" + key + "=" + value);
			}
		}

		if (sb.length() > 0) {
			sb.append("&");
		}
		sb.append("paySecret=");
		sb.append(paySecret);

		sign = ToolKit.MD5(sb.toString(), "UTF-8");

		scanMap.put("sign", sign);

		logger.info("签名字符原串：" + sb.toString());

		String resultstr = RequestForm(payUrl, scanMap);
		logger.info("响应数据：" + resultstr);
		JSONObject r_json = JSONObject.fromObject(resultstr);
		if (r_json.containsKey("status") && "SUSPENDING".equals(r_json.getString("status"))) {
			// pc端
			if (StringUtils.isEmpty(mobile)) {
				return PayUtil.returnPayJson("success", "2", "支付接口请求成功!", userName, transAmt, orderNo,
						r_json.getString("authCode"));
			} else {
				// 手机端
				return PayUtil.returnPayJson("success", "4", "支付接口请求成功!", userName, transAmt, orderNo,
						r_json.getString("authCode"));
			}
		} else {
			return PayUtil.returnPayJson("error", "2", r_json.getString("respDesc"), userName, transAmt, orderNo, "");
		}

	}

	private static String generateserialNo() {
		String currTime = DateUtil.getCurrentDate("yyMMddHHmmssSSS");
		String strRandom = DateUtil.getRandom(4) + "";
		String serialNo = currTime + strRandom;

		return serialNo;
	}

	/**
	 * 结果返回
	 * 
	 * @param status
	 * @param qrCode
	 * @param msg
	 * @return
	 */
	private JSONObject getReturnJson(String status, String qrCode, String msg) {
		JSONObject json = new JSONObject();
		json.put("status", status);
		json.put("qrCode", qrCode);
		json.put("msg", msg);
		return json;
	}

	/**
	 * 判断是否是json结构
	 */
	public static boolean isJson(String value) {
		try {
			JSONObject.fromObject(value);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否是xml结构
	 */
	public static boolean isXML(String value) {
		try {
			DocumentHelper.parseText(value);
		} catch (DocumentException e) {
			return false;
		}
		return true;
	}

	@Override
	public String callback(Map<String, String> paramMap){
	    logger.info("[YFZF]云付支付回调验签开始=====================START==================");
	    try {
	        String serviceSign = paramMap.get("sign");
	        logger.info("回调验签原签名串:{}",serviceSign);
	        // 排序
	        Map<String,String> sortmap = MapUtils.sortByKeys(paramMap);
	        Iterator<String> iterator = sortmap.keySet().iterator();
	        StringBuffer sb = new StringBuffer();
	        while(iterator.hasNext()){
	            String key = iterator.next();
	            String val = sortmap.get(key);
	            if(key.equalsIgnoreCase("sign") || org.apache.commons.lang.StringUtils.isBlank(val)) continue;
	            sb.append(key + "=" + val + "&");
	        }
	        sb.append("paySecret=").append(paySecret);
	        String signStr = sb.toString();
	        logger.info("[YFZF]云付支付生成待签名串:{}",signStr);
	        String sign = MD5Utils.md5toUpCase_32Bit(signStr);
	        logger.info("[YFZF]云付支付生成回调签名串:{}",sign);
	        if (serviceSign.equalsIgnoreCase(sign)) {
	            logger.info("签名成功");
	            return "success";
	        }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YFZF]云付支付回调通知验签异常:{}",e.getMessage());
        }
		
	    return "faild";
	}


	public static String getSign(StringBuffer stringBuffer, String paySecret) {
		String argPreSign = stringBuffer.append("&paySecret=")
				.append(paySecret).toString();
		String signStr = MD5Util.encode(argPreSign).toUpperCase();

		return signStr;
	}

}
