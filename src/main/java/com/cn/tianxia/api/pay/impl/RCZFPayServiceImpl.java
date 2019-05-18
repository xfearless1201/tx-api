package com.cn.tianxia.api.pay.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.utils.rczf.HttpUtil;
import com.cn.tianxia.api.utils.rczf.SignUtils;

import net.sf.json.JSONObject;

/***
 * 融灿支付
 * 
 * @author hb
 * @date 2018-06-03
 */
public class RCZFPayServiceImpl implements PayService {

	private final static Logger logger = LoggerFactory.getLogger(RCZFPayServiceImpl.class);
	
	private String privateKey ;//= "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMMm1rHzTn99rMDQv0CW6jcA8spjCFejkZBcdyp5kPqGuII3QJXKH9TV68Uz+7A3aFeJvTJ7xVT4/2+L3xq3FpEqqHBkCTfDkHf7GB4AhzJymBdXkJC7wYLvhYGNf59mvUqnm6SU0XktOZb6itKvnLIyupQFJqDKE54HeLMBQxYrAgMBAAECgYAobb5ipT4o6VdFprlIXztsY7TourV6uncoig9h7Eddr1VAHMQzg+kuRZcPhqJoskHaiL16XOvXm7IHYNm6hh2VXh1ZMtrKaV7bA7FLBmKSNV39Y/MIGzgLMxS96mt2JnxD2LRWDV1KyzQlmBNSf5lijhuQcttm+a7BAV7NSnNJCQJBAPsRn3+GMcRvY8Vt/VcldS/PVDtiAkmTogIluKwCQnrAiAr+PG/tLRY5PLTnh/DgAmZYJCyM06c2UtO/OgQEV+0CQQDG/BBMsC1YRch+qrx9J5P3+yX9MceXkRGvXBuiWspNmBDmaVoCGNgjd0cN3XgOXZ1i3gri4TqSSUQI3qlxYjN3AkAefZJoM0zh9UEhnezxY2wq5TvuhkWO1+4J4rjdstyN+cnLw/plAWHDXCoiMigRObMw6K1j96pQmUlPy95o1Ho1AkA+RdhcB67JN12dtpUyndZC/0hOSuvp1S6xsKO9VaiGTBbN5R6UFW5e+w8zmaHe7RE6Rb8mbdJEwcUW+YgRwefVAkEA+uSciKgIfuYIrytDmDH2IFpBHIkZCKwgK4tREspKvfNhq6hrJ+VTcc8umOY0XdSujBsakvnQHYVjIx+OyESu0A==";
	private String publicKey ;//= "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC0sJMGLD0UQUYObjsMHBGUYQEVEOCkBCNzzkYWSM0RYToK49hLpmxpNLbNcSMSUwOs6AfzDW9Tbpcotjg4JiphZqrBjG4Vj2acQPxBp06oJBYdvoCM42AFFLthHNDTmP+O7OYPrwiTTSYPlIUO8HyojhfQ6Dc9guiit7L98FWhmQIDAQAB";
	private String payUrl ;//= "https://api.rcpays.com/gateway/bank";
	private String merchantCode ;//= "M0000001";// 商户号
	private String remark ;//= "pay";// 订单描述
	private String notifyUrl ;//= "http://www.baidu.com";//回调地址
	private String charset ;//= "UTF-8";//编码
	
	public RCZFPayServiceImpl() {}
	
	public RCZFPayServiceImpl(Map<String, String> pmap) {
		if(pmap != null) {
			if (pmap.containsKey("privateKey")) {
				this.privateKey = pmap.get("privateKey");
			}
			if (pmap.containsKey("publicKey")) {
				this.publicKey = pmap.get("publicKey");
			}
			if (pmap.containsKey("payUrl")) {
				this.payUrl = pmap.get("payUrl");
			}
			if (pmap.containsKey("merchantCode")) {
				this.merchantCode = pmap.get("merchantCode");
			}
			if (pmap.containsKey("remark")) {
				this.remark = pmap.get("remark");
			}
			if (pmap.containsKey("notifyUrl")) {
				this.notifyUrl = pmap.get("notifyUrl");
			}
			if (pmap.containsKey("charset")) {
				this.charset = pmap.get("charset");
			}
		}
	}

	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		
		String orderNo = payEntity.getOrderNo();//new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());// 商户订单号
		String amount = String.valueOf((int)(payEntity.getAmount()*100));// 金额
		String channel = "BANK";// 通道
		String bankCode = "CASHIER";// 收银台支付方式
		String returnUrl = payEntity.getRefererUrl();
		String extraReturnParam = "";// 公用回传参数可空
		String signType = "RSA";// 签名方式（不参与签名）

		// 拼接签名字符串，顺序固定
		String signStr = "charset=" + charset + "&merchantCode=" + merchantCode + "&orderNo=" + orderNo + "&amount="
				+ amount + "&channel=" + channel + "&bankCode=" + bankCode + "&remark=" + remark + "&notifyUrl="
				+ notifyUrl + "&returnUrl=" + returnUrl + "&extraReturnParam=" + extraReturnParam;
		logger.info("生成签名字符串signStr="+signStr);
		
		String sign = SignUtils.Signaturer(signStr, privateKey);
		logger.info("签名字符串sign="+signStr);

		Map<String, String> params = new HashMap<>();
		params.put("merchantCode", merchantCode);
		params.put("orderNo", orderNo);
		params.put("remark", remark);
		params.put("notifyUrl", notifyUrl);
		params.put("charset", charset);
		params.put("amount", amount);
		params.put("channel", channel);
		params.put("bankCode", bankCode);
		params.put("returnUrl", returnUrl);
		params.put("extraReturnParam", extraReturnParam);
		params.put("signType", signType);
		params.put("sign", sign);

		String formStr = buildForm(params,payUrl);
		logger.info("form表单："+formStr);
		
		return PayUtil.returnWYPayJson("success", "jsp", formStr, payUrl, "payhtml");
	}

	@Override
	public JSONObject smPay(PayEntity payEntity) {
		this.payUrl = "https://api.rcpays.com/gateway/scanpay";
		
		String orderNo = payEntity.getOrderNo();//new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());// 商户订单号
		String amount = String.valueOf((int)(payEntity.getAmount()*100));// 金额
		String channel = payEntity.getPayCode();//"UNION_APP_QR";// 银联扫码通道
		//String bankCode = "CASHIER";// 收银台支付方式
		String returnUrl = payEntity.getRefererUrl();
		String extraReturnParam = "";// 公用回传参数可空
		String signType = "RSA";// 签名方式（不参与签名）

		// 拼接签名字符串，顺序固定
		String signStr = "charset=" + this.charset + "&merchantCode=" + merchantCode + "&orderNo=" + orderNo + "&amount="
				+ amount + "&channel=" + channel + "&remark=" + remark + "&notifyUrl="
				+ notifyUrl + "&returnUrl=" + returnUrl + "&extraReturnParam=" + extraReturnParam;
		logger.info("生成签名字符串signStr="+signStr);
		
		String sign = SignUtils.Signaturer(signStr, privateKey);
		logger.info("签名字符串sign="+signStr);

		Map<String, String> params = new HashMap<>();
		params.put("charset", this.charset);
		params.put("merchantCode", this.merchantCode);
		params.put("orderNo", orderNo);
		params.put("amount", amount);
		params.put("channel", channel);
		params.put("remark", this.remark);
		params.put("notifyUrl", this.notifyUrl);
		params.put("returnUrl", returnUrl);
		params.put("extraReturnParam", extraReturnParam);
		
		params.put("signType", signType);
		params.put("sign", sign);
		
		String returnStr = HttpUtil.RequestForm(this.payUrl, params);
		JSONObject returnJson = JSONObject.fromObject(returnStr);
		
		String userName = payEntity.getUsername();
		
		return PayUtil.returnPayJson("success", "2", "支付接口请求成功", userName, payEntity.getAmount(), orderNo, returnJson.getString("qrCode"));//qrCode
	}

	// 判断字符串是否是json结构
	private boolean isJson(String str) {
		try {
			JSONObject.fromObject(str);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public String buildForm(Map<String, String> paramMap, String payUrl) {
		// 待请求参数数组
		String FormString = "<body onLoad=\"document.actform.submit()\">正在处理请稍候.....................<form  id=\"actform\" name=\"actform\" method=\"post\" action=\""
				+ payUrl + "\">";
		for (String key : paramMap.keySet()) {
			FormString += "<input name=\"" + key + "\" type=\"hidden\" value='" + paramMap.get(key) + "'>\r\n";
		}
		FormString += "</form></body>";

		return FormString;
	}
	
	public static void main(String[] args) {
		
		String str = "merchantCode=M0000001&orderNo=345304509&amount=100&successAmt=100&payOrderNo=000000001&orderStatus=Success&extraReturnParam=test&signType=RSA&sign=RumYsEVPcE8q4chP8Q/gOkIGMXZEwSs1WqpOr4LnfMH8wo+6HuePJ41slVSgTdyURbkZzKyJbYBu44kJSujshcmvksWMwb0lxFv+f8UiXwY8LAHAGUqL7/q7IY1tDxogXdclMvjK0H3dZuvfsY7NYVwPaz5qrdwKUcLM0fiypkM=";
		
		Map<String, String> params = new HashMap<>();
		String[] ss = str.split("&");
		for(String kv:ss) {
			if(kv.startsWith("sign=")) {
				params.put("sign", kv.replace("sign=", ""));
			}else {
				String[] kvs = kv.split("=");
				params.put(kvs[0], kvs[1]);
			}
		}
		System.out.println(params);
		
		new RCZFPayServiceImpl(). callback(params);
	}

	/**
	 * 回调验签
	 * @param infoMap
	 * @return
	 */
	@Override
	public String callback(Map<String, String> infoMap) {
		String signStr = 
				"merchantCode="+infoMap.get("merchantCode")+
				"&orderNo="+infoMap.get("orderNo")+
				"&amount="+infoMap.get("amount")+
				"&successAmt="+infoMap.get("successAmt")+
				"&payOrderNo="+infoMap.get("payOrderNo")+
				"&orderStatus="+infoMap.get("orderStatus")+
				"&extraReturnParam="+infoMap.get("extraReturnParam");
		logger.info("签名字符串 signStr = "+signStr);
		String sign = SignUtils.Signaturer(signStr, privateKey);
		logger.info("生成签名sign ="+sign);
		boolean result = SignUtils.validataSign(signStr, infoMap.get("sign"), publicKey);
		if(result) {
			logger.info("验签成功");
			return "success";
		}
		logger.info("验签失败");
		return "fail";
		
	}

}
