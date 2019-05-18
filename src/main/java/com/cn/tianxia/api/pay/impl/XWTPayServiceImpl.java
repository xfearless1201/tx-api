package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

/**
 * 第2个新万通支付，注意，已经有一个新万通支付
 * @author TX
 */
public class XWTPayServiceImpl implements PayService{
	private final static Logger logger = LoggerFactory.getLogger(XWTPayServiceImpl.class);
	
	/*
	 * 商户号
	 */
	private String pay_memberid;
	/*
	 * 支付地址
	 */
	private String payUrl;
	/*
	 * 回调地址
	 */
	private String notifyUrl;
	/*
	 * 密钥
	 */
	private String secret;
	
	public XWTPayServiceImpl(Map<String,String> map) {
		if(map.containsKey("pay_memberid")){
			this.pay_memberid = map.get("pay_memberid");
		}
		if(map.containsKey("payUrl")){
			this.payUrl = map.get("payUrl");
		}
		if(map.containsKey("notifyUrl")){
			this.notifyUrl = map.get("notifyUrl");
		}
		if(map.containsKey("secret")){
			this.secret = map.get("secret");
		}
	}
	
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		logger.info("XWT新2万通网银支付 对接 开始........");
		logger.info("XWT新2万通网银支付 订单号:{}",payEntity.getOrderNo());
		String formStr = "";
		try{
			Map<String,String> map = sealRequest(payEntity,0);
			String sign = generatorSign(map);
			map.put("pay_md5sign", sign);
			
			logger.info("XWT新2万通网银支付请求参数:{}",map);
			formStr = HttpUtils.generatorForm(map,payUrl);
			if(StringUtils.isEmpty(formStr)){
				return PayResponse.error("XWT新2万通支付请求无响应");
			}
			logger.info("XWT新2万通网银支付生成请求表单:{}",formStr);
			return PayResponse.wy_form(payEntity.getPayUrl(), formStr);
		}catch(Exception e){
			e.printStackTrace();
			logger.info("XWT新2万通支付出现异常...{}",e.getMessage());
			return PayResponse.error("XWT新2万通出现错误!错误返回信息:"+formStr);
		}
	}

	/**
	 * 扫码支付
	 */
	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("XWT新2万通支付 对接 开始........订单号:{}",payEntity.getOrderNo());
		String formStr = "";
		try{
			Map<String,String> map = sealRequest(payEntity,1);
			String sign = generatorSign(map);
			map.put("pay_md5sign", sign);
			
			logger.info("XWT新2万通支付请求参数:{}",map);
			formStr = HttpUtils.generatorForm(map,payUrl);
			if(StringUtils.isEmpty(formStr)){
				return PayResponse.error("XWT新2万通支付请求无响应");
			}
			logger.info("XWT新2万通生成请求表单:{}",formStr);
	        return PayResponse.sm_form(payEntity, formStr, "下单成功!");
		}catch(Exception e){
			e.printStackTrace();
			logger.info("XWT新2万通支付出现异常...{}",e.getMessage());
			return PayResponse.error("XWT新2万通出现错误!错误返回信息:"+formStr);
		}
	}

	@Override
	public String callback(Map<String, String> data) {
		logger.info("新2万通支付开始回调 对比 sign...{}",data);
		String sourceSign = data.get("sign");
		logger.info("新2万通支付的sign = {}",sourceSign);
		try{
			String sign = generatorSign(data);
			logger.info("新2万通支付签名后:{}",sign);
			if(sourceSign.equals(sign)){
				return "success";
			}
			return "fail";
		}catch(Exception e){
			logger.info("新2万通支付 回调函数生成 sign 错误");
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * 组装参数
	 * @param payEntity
	 * @return
	 */
	private Map<String,String> sealRequest(PayEntity payEntity,int type){
		logger.info("XWT新2万通开始组装参数...");
		
		SimpleDateFormat simple = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss"); 
		
		TreeMap<String,String> treeMap = new TreeMap<>();
		String amount = new DecimalFormat("#.##").format(payEntity.getAmount());
		treeMap.put("pay_memberid", pay_memberid);
		treeMap.put("pay_orderid", payEntity.getOrderNo());
		treeMap.put("pay_amount", amount);
		treeMap.put("pay_applydate", simple.format(new Date()));//当前时间
		if(type == 0){
			treeMap.put("pay_bankcode", "907");//支付类型
		}else{
			treeMap.put("pay_bankcode", payEntity.getPayCode());//支付类型
		}
		treeMap.put("pay_notifyurl", notifyUrl);
		treeMap.put("pay_callbackurl", payEntity.getRefererUrl());
		logger.info("XWT新2万通组装参数值:{}", treeMap);
		return treeMap;
	}
	
	/**
	 * 加密 
	 * @param map
	 * @return
	 */
	private String generatorSign(Map<String,String> map) throws Exception{
		logger.info("新2万通支付加密的参数:{}",map);
		StringBuilder sb = new StringBuilder();
		//排血
		Map<String,String> sortmap = MapUtils.sortByKeys(map);
		Iterator<String> iterator = sortmap.keySet().iterator();
		while(iterator.hasNext()){
		    String key = iterator.next();
		    String val = sortmap.get(key);
		    if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
		    sb.append("&").append(key).append("=").append(val);
		}
		//如果请求路径是 wtzf138 加密包含 & ，如果其他就不包含 
		if(payUrl.contains("wtzf138") || payUrl.endsWith("orders_2") || payUrl.contains("wtzf666.com")){
			sb.append("&key=").append(secret);
		}else{
			sb.append("key=").append(secret);
		}
		
		String signStr = sb.toString().replaceFirst("&", "");
		logger.info("新2万通支付加密前参数:{}",signStr);
		String md5 = MD5Utils.md5toUpCase_32Bit(signStr);
		logger.info("新2万通支付加密后值:{}",md5);
		return md5;
	}
}
