package com.cn.tianxia.api.pay.impl;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
 * 乐百支付
 * @author TX
 */
public class LBZFPayServiceImpl implements PayService{
	private final static Logger logger = LoggerFactory.getLogger(LBZFPayServiceImpl.class);
	
	private String pay_memberid;
	private String notifyUrl;
	private String secret;
	private String payUrl;

	public LBZFPayServiceImpl(Map<String,String> map){
		if(map.containsKey("pay_memberid")){
			this.pay_memberid = map.get("pay_memberid");
		}
		if(map.containsKey("notifyUrl")){
			this.notifyUrl = map.get("notifyUrl");
		}
		if(map.containsKey("payUrl")){
			this.payUrl = map.get("payUrl");
		}
		if(map.containsKey("secret")){
			this.secret = map.get("secret");
		}
	}
	
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		logger.info("乐百支付 扫描支付 开始..............");
		try{
			Map<String,String> map = sealRequest(payEntity, 1);
			String sign = generatorSign(map);
			map.put("pay_md5sign", sign);
			
			String formStr = HttpUtils.generatorForm(map, payUrl);
			logger.info("乐百支付 扫描支付请求参数={}",formStr);
			
			return PayResponse.wy_form(payEntity.getPayUrl(), formStr);
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error("乐百支付出现错误，错误内容:{}",e.getMessage());
			return PayResponse.error("乐百支付出现错误");
		}
	}

	/**
	 * 扫描支付
	 */
	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("乐百支付 扫描支付 开始..............");
		try{
			Map<String,String> map = sealRequest(payEntity, 0);
			String sign = generatorSign(map);
			map.put("pay_md5sign", sign);
			
			String formStr = HttpUtils.generatorForm(map, payUrl);
			logger.info("乐百支付 扫描支付请求参数={}",formStr);
			
			return PayResponse.sm_form(payEntity, formStr, "乐百支付扫描成功");
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error("乐百支付出现错误，错误内容:{}",e.getMessage());
			return PayResponse.error("乐百支付出现错误");
		}
	}

	@Override
	public String callback(Map<String, String> data) {
		logger.info("乐百支付 开始回调参数:{}", data);
		try{
			String signData = data.get("sign");
			logger.info("乐百支付 原签名秘钥:{}",signData);
			
			String sign = generatorSign(data);
			if(sign.equalsIgnoreCase(signData)){
				return "success";
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("乐百支付错误信息:{}",e.getMessage());
		}
		return "fail";
	}

	private Map<String,String> sealRequest(PayEntity payEntity , int type){
		
		DateFormat payDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String amount = new DecimalFormat("##").format(payEntity.getAmount());//订单金额,单位分
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("pay_memberid", pay_memberid);
		map.put("pay_orderid", payEntity.getOrderNo());
		map.put("pay_applydate", payDate.format(new Date()));
		map.put("pay_notifyurl", notifyUrl);
		map.put("pay_callbackurl", payEntity.getRefererUrl());
		map.put("pay_amount", amount);
		if(type == 1){
			map.put("pay_bankcode", "907");
		}else{
			map.put("pay_bankcode", payEntity.getPayCode());
		}
		
		return map;
	}
	private String generatorSign(Map<String,String> map) throws Exception{
		logger.info("乐百支付开始生成 秘钥,秘钥参数:{}",map);
		Map<String,String> sortMap = MapUtils.sortByKeys(map);
		StringBuffer sb = new StringBuffer();
		
		Iterator<String> iterator = sortMap.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			String val = sortMap.get(key);
			if("sign".equals(key) || StringUtils.isEmpty(val)){
				continue;
			}
			//支付加密方式
			sb.append(key).append("=").append(val).append("&");
		}
		
		sb.append("key=").append(secret);
		logger.info("乐百支付生成秘钥参数:{}",sb);
		String key = MD5Utils.md5toUpCase_32Bit(sb.toString());
		logger.info("乐百支付生成秘钥结果:{}",key);
		
		return key;
	}
}
