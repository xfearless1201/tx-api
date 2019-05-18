package com.cn.tianxia.api.pay.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

/**
 * 华菱盛业支付
 * @author Administrator
 *
 */
public class HLSYPayServiceImpl implements PayService {
	private final static Logger logger = LoggerFactory.getLogger(HLSYPayServiceImpl.class);
	
	private String p1_MerchantNo;// 商户编号
	private String p5_ProductName;// 商品名称
	private String p6_NotifyUrl;// 通知地址
	private String H5_pay_url;// H5支付地址
	private String wxPayUrl;//微信支付地址
	private String MD5Key;
	public HLSYPayServiceImpl(Map<String, String> pmap) {
		if (pmap != null) {
			if (pmap.containsKey("p1_MerchantNo")) {
				p1_MerchantNo = pmap.get("p1_MerchantNo");
			}
			if (pmap.containsKey("p5_ProductName")) {
				p5_ProductName = pmap.get("p5_ProductName");
			}
			if (pmap.containsKey("p6_NotifyUrl")) {
				p6_NotifyUrl = pmap.get("p6_NotifyUrl");
			}
			if (pmap.containsKey("H5_pay_url")) {
				H5_pay_url = pmap.get("H5_pay_url");
			}
			if (pmap.containsKey("wxPayUrl")) {
			    wxPayUrl = pmap.get("wxPayUrl");
            }
			if (pmap.containsKey("MD5Key")) {
				MD5Key = pmap.get("MD5Key");
			}
		}
	}
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		Double amount = payEntity.getAmount();
		String order_no = payEntity.getOrderNo();
		String refereUrl = payEntity.getRefererUrl();
		String pay_code = payEntity.getPayCode();
		String userName = payEntity.getUsername();
		String pay_url = payEntity.getPayUrl();
		String mobile = payEntity.getMobile();
		String ip = payEntity.getIp();
		  // 不能带小数
//        DecimalFormat df = new DecimalFormat("#");
//        String price = String.valueOf(df.format(payEntity.getAmount()*100)) ;
//		Map<String, String> reqMap = new HashMap<String, String>();
//		reqMap.put("p1_MerchantNo", "");// 商户编号
//		reqMap.put("p2_OrderNo", order_no);// 商户订单号
//		reqMap.put("p3_Amount", price);// 转账金额，单位:分
//		reqMap.put("p4_BankCode", pay_code);// 银行编码
//		reqMap.put("p5_AccountNo", "");// 账户号码	收款人的实体银行账户号码
//		reqMap.put("p6_AccountName", "");// 账户名称
//		reqMap.put("p7_PhoneNumber", mobile);// 手机号码
//		reqMap.put("p8_CertificateID", "");// 身份证号码
//		reqMap.put("p9_BankBranch", "");// 分支行名称
//		reqMap.put("p10_Province", "");// 省份
//		reqMap.put("p11_City", "");// 城市
//		reqMap.put("p12_Note", "");// 摘要、附言
//		reqMap.put("sign", "");// 密文
		
		String html = "";// PayBank(String.valueOf(amount), pay_code, order_no, reqMap);
		return PayUtil.returnWYPayJson("success", "form", html, pay_url, "");
	}

	@Override
	public JSONObject smPay(PayEntity payEntity) {
		try {
			Map<String, String> data = sealRequest(payEntity);
			data.put("sign", getSign(data));  // sign
			logger.info("[HLSY]华菱盛业扫码支付请求数据:" + JSONObject.fromObject(data));
			String payurl = this.H5_pay_url;
			if("wx".equalsIgnoreCase(payEntity.getPayCode())){
			    payurl = this.wxPayUrl;
			}
			String resStr = HttpUtils.toPostForm(data, payurl);
			logger.info("[HLSY]华菱盛业扫码支付响应信息："+resStr);
			JSONObject resJsonObj = JSONObject.fromObject(resStr);
			if(resJsonObj.containsKey("retCode")&&resJsonObj.getString("retCode").equals("00000")){
				if(StringUtils.isNotBlank(payEntity.getMobile())){
					return PayResponse.sm_link(payEntity, resJsonObj.getString("qrCode"), "下单成功");
				}
				return PayResponse.sm_qrcode(payEntity, resJsonObj.getString("qrCode"), "下单成功");
			}
			return PayResponse.error("[HLSY]华菱盛业扫码支付下单失败");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[HLSY]华菱盛业扫码支付生成异常:"+e.getMessage());
			return PayResponse.error("[HLSY]华菱盛业扫码支付下单失败");
		}
	}
	/**
     * 
     * @Description 封装支付请求参数
     * @param entity
     * @param 
     * @return
     * @throws Exception
     */
	public Map<String, String> sealRequest(PayEntity payEntity){
		try {
			DecimalFormat df = new DecimalFormat("0");
			Map<String,String> data = new HashMap<>();
			data.put("p1_MerchantNo", p1_MerchantNo);    // 商户编号
			data.put("p2_OrderNo", payEntity.getOrderNo());   // 商户订单号
			data.put("p3_Amount", df.format(payEntity.getAmount()*100));  // 订单金额
			data.put("p4_Cur", "1");   // 币种	1=人民币	否	1
			data.put("p5_ProductName", URLEncoder.encode(p5_ProductName, "UTF-8"));
			data.put("p6_NotifyUrl", p6_NotifyUrl);  // 通知地址
			data.put("p7_ReturnUrl", payEntity.getRefererUrl());  // 回调地址
			return data;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("[HLSY]华菱盛业支付拼装请求参数异常"+e.getMessage());
			return null;
		}
	}
	/**
     * 
     * @Description 生成签名串
     * @param data
     * @return
	 * @throws Exception
     */
	private String getSign(Map<String, String> infoMap) {
	   StringBuilder sb = new StringBuilder();
	    sb.append(StringUtils.trim(infoMap.get("p1_MerchantNo")));
        sb.append(StringUtils.trim(infoMap.get("p2_OrderNo")));
        sb.append(StringUtils.trim(infoMap.get("p3_Amount")));
        sb.append(StringUtils.trim(infoMap.get("p4_Cur")));
        sb.append(StringUtils.trim(infoMap.get("p5_ProductName")));
        sb.append(StringUtils.trim(infoMap.get("p6_NotifyUrl")));
        sb.append(StringUtils.trim(infoMap.get("p7_ReturnUrl")));
        sb.append(this.MD5Key);
        String signatureStr = sb.toString();
        logger.info("[HLSY]华菱盛业扫码支付生成待签名串：" + signatureStr);
        String sign = null;
        try {
        	sign = MD5Utils.md5(signatureStr.getBytes());
            logger.info("[HLSY]华菱盛业扫码支付生成签名串：" + sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("签名生成失败");
        }
        return sign;
	}

	@Override
	public String callback(Map<String, String> data) {
		try {
			JSONObject reqJson = JSONObject.fromObject(data);
			String sourceSign = data.get("sign");
			JSONObject reqJsonObj = new JSONObject();
			reqJsonObj.put("merchantNo", data.get("merchantNo"));
			reqJsonObj.put("orderNo", data.get("orderNo"));
			reqJsonObj.put("officialOrderNo", data.get("officialOrderNo"));
			reqJsonObj.put("trxorderNo", data.get("trxorderNo"));
			reqJsonObj.put("amount", reqJson.getInt("amount"));
			reqJsonObj.put("status", data.get("status"));
			reqJsonObj.put("finalTime", data.get("finalTime"));
			String jsonStr = reqJsonObj.toString();
			StringBuffer sb = new StringBuffer();
			sb.append(jsonStr).append(MD5Key);
			String signStr = sb.toString();
			logger.info("[HLSY]华菱盛业扫码支付回调生成待签名串:{}",signStr);
			String sign = MD5Utils.md5(signStr.getBytes());
			logger.info("[HLSY]华菱盛业扫码支付回调生成加密签名串:{}",sign);
			if(sign.equalsIgnoreCase(sourceSign)) return "success";
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[HLSY]华菱盛业扫码支付回调生成签名串异常"+e.getMessage());
		}
		return "fail";
	}
}
