package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.tx.MD5Utils;

import net.sf.json.JSONObject;
/**
 * iipays 支付
 * @author TX
 */
public class IIZFPayServiceImpl implements PayService{
	private final static Logger logger = LoggerFactory.getLogger(IIZFPayServiceImpl.class);
	/*
	 * 版本号
	 */
	private String version;
	/**
	 * 回调路径
	 */
	private String notify_url;
	/**
	 * 支付地址
	 */
	private String pay_url;
	/**
	 * 商户编号
	 */
	private String customerid;
	/**
	 * 密钥
	 */
	private String secret;
	
	public IIZFPayServiceImpl(Map<String,String> map){
		if(map.containsKey("version")){
			this.version = map.get("version");
		}
		if(map.containsKey("notify_url")){
			this.notify_url = map.get("notify_url");
		}
		if(map.containsKey("pay_url")){
			this.pay_url = map.get("pay_url");
		}
		if(map.containsKey("customerid")){
			this.customerid = map.get("customerid");
		}
		if(map.containsKey("secret")){
			this.secret = map.get("secret");
		}
	}
	/**
	 * 银联
	 */
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		return null;
	}
	
	/**
	 * 扫码支付
	 */
	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("iipays支付 开始扫码支付开始................");
		Map<String,String> map = parameter(payEntity);
		logger.info("iipays支付 组装参数:{}",map);
		String sign = generatorSign(map,0);
		
		map.put("sign", sign);
		
		String formStr = HttpUtils.generatorForm(map,pay_url);
		
		return PayResponse.sm_form(payEntity, formStr, "支付成功");
	}

	/**
	 * 回调
	 */
	@Override
	public String callback(Map<String, String> data) {
		logger.info("iipays 支付回调开始:{}",data);
		String sourceSign = data.get("sign");
		logger.info("获取的签名:{}",sourceSign);
		if(StringUtils.isEmpty(sourceSign)){
			logger.info("iipays 支付回调获取签名为null");
			return null;
		}
		String sign = generatorSign(data,1);
		logger.info("iipays 支付回调生成签名结果:{}",sign);
		if(StringUtils.isEmpty(sign)){
			logger.info("iipays 支付回调生成签名为null");
			return null;
		}
		if(sourceSign.equals(sign)){
			return "success";
		}
		
		return "fail";
	}

	private Map<String,String> parameter(PayEntity payEntity){
		logger.info("iipays 开始组装参数.........订单号:{}",payEntity.getOrderNo());
		String money = new DecimalFormat("#.##").format(payEntity.getAmount());
		Map<String,String> map = new LinkedHashMap<String,String>();
		map.put("version", version);
		map.put("customerid", customerid);
		map.put("total_fee", money);
		map.put("sdorderno", payEntity.getOrderNo());
		map.put("notifyurl", notify_url);
		map.put("returnurl", payEntity.getRefererUrl());
		map.put("paytype", payEntity.getPayCode());
		logger.info("iipays 组装参数完成:{}",map);
		return map;
	}
	/**
	 * 支付签名顺序:version={value}&customerid={value}&total_fee={value}&sdorderno={value}&notifyurl={value}&returnurl={value}&{apikey}
	 * 回调签名顺序:customerid={value}&status={value}&sdpayno={value}&sdorderno={value}&total_fee={value}&paytype={value}&{apikey}
	 * @param map
	 * @return
	 */
	private String generatorSign(Map<String,String> map,int type){
		logger.info("iipays支付 开始签名:{}",map);
		StringBuilder sb = new StringBuilder();
		if(type == 0){
			for(Entry<String,String> entry : map.entrySet()){
				
				if("paytype".equals(entry.getKey()) || StringUtils.isEmpty(entry.getValue()) || "sign".equalsIgnoreCase(entry.getKey())){
					continue;
				}
				
				sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
			sb.append(secret);
		}else if(type == 1){
			sb.append("customerid=").append(map.get("customerid"))
			.append("&status=").append(map.get("status"))
			.append("&sdpayno=").append(map.get("sdpayno"))
			.append("&sdorderno=").append(map.get("sdorderno"))
			.append("&total_fee=").append(map.get("total_fee"))
			.append("&paytype=").append(map.get("paytype"))
			.append("&").append(secret);
		}
		
		logger.info("iipays支付 签名参数:{}",sb);
		String md5Value = MD5Utils.md5(sb.toString());
		logger.info("iipays支付签名结果:{}", md5Value);
		return md5Value;
	}
}
