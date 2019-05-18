package com.cn.tianxia.api.pay.impl;

import java.security.NoSuchAlgorithmException;
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
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

/**
 * 富豪支付
 * @author TX
 */
public class FHZFPayServiceImpl implements PayService {
	private final static Logger logger = LoggerFactory.getLogger(FHZFPayServiceImpl.class);
	/**
	 * 商户号
	 */
	private String partner;
	/**
	 * 回调函数
	 */
	private String notify_url;
	/**
	 * 请求地址
	 */
	private String pay_url;
	/**
	 * 密钥
	 */
	private String secret;
	
	public FHZFPayServiceImpl(Map<String,String> map){
		if(map.containsKey("partner")){
			this.partner = map.get("partner");
		}
		if(map.containsKey("notify_url")){
			this.notify_url = map.get("notify_url");
		}
		if(map.containsKey("pay_url")){
			this.pay_url = map.get("pay_url");
		}
		if(map.containsKey("secret")){
			this.secret = map.get("secret");
		}
	}
	
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		return null;
	}

	/**
	 * 扫码支付
	 */
	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("富豪支付扫码支付开始.............{}",payEntity.getOrderNo());
		try{
			Map<String,String> map = sealRequest(payEntity);
			logger.info("富豪支付请求的参数:{}",map);
			String sign = generatorSign(map);
			map.put("sign", sign);
			
			logger.info("富豪支付请求参数:{},请求地址:{}",map,pay_url);
			String response = HttpUtils.toPostForm(map, pay_url);
			logger.info("富豪支付请求返回结果:{}",response);
			if(StringUtils.isBlank(response)){
				logger.info("请求单号:{},请求路径:{}",payEntity.getOrderNo(),pay_url);
				return PayResponse.error("富豪支付请求无响应，请联系第三方支付!");
			}
			
			JSONObject object = JSONObject.fromObject(response);
			if(object.containsKey("is_success") && "T".equals(object.getString("is_success"))){
				logger.info("富豪支付支付路径:{}",object.getString("result"));
				return PayResponse.sm_link(payEntity, object.getString("result"), "支付成功!");
			}
			return PayResponse.error("富豪支付错误,"+response);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("富豪支付错误");
			return PayResponse.error("富豪支付出现内部错误");
		}
	}

	/**
	 * 回调函数
	 */
	@Override
	public String callback(Map<String, String> data) {
		logger.info("富豪支付开始回调:{}",data);
		String sourceSign = data.get("sign");
		logger.info("富豪支付获取的 sign 值:{}",sourceSign);
		
		String sign = generatorSign(data);
		logger.info("富豪支付回调函数签名:{}",sign);
		if(sign.equalsIgnoreCase(sourceSign)){
			return "success";
		}
		return "fail";
	}
	
	/**
	 * 组装参数
	 * @param payEntity
	 * @return
	 */
	private Map<String,String> sealRequest(PayEntity payEntity){
		logger.info("富豪支付请求组装参数,订单号为:{}",payEntity.getOrderNo());
		String amount = new DecimalFormat("#.##").format(payEntity.getAmount());
		Map<String,String> linked = new LinkedHashMap<String,String>();
		linked.put("amount", amount);
		linked.put("notify_url", notify_url);
		linked.put("partner", partner);
		if(StringUtils.isBlank(payEntity.getMobile())){
			linked.put("pay_type", "sm");
		}else{
			linked.put("pay_type", "h5");
		}
		
		linked.put("request_time", String.valueOf(System.currentTimeMillis()));
		linked.put("trade_no", payEntity.getOrderNo());
		logger.info("富豪支付请求参数:{}",linked);
		return linked;
	}
	
	/**
	 * 加密sign
	 * @param map
	 * @throws NoSuchAlgorithmException 
	 */
	private String generatorSign(Map<String,String> map){
		try {
			logger.info("富豪支付开始加密.....{}",map);
			StringBuilder sb = new StringBuilder();
			for(Entry<String,String> entry : map.entrySet()){
				if("sign".equals(entry.getKey()) || StringUtils.isBlank(entry.getValue())){
					continue;
				}
				sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
			sb.append(secret);
			logger.info("富豪支付加密前参数:{}",sb);
			String sign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
			logger.info("富豪支付加密后值:{}",sign);
			return sign;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			logger.info("生成加密签名串异常");
			return null;
		}
	}
}
