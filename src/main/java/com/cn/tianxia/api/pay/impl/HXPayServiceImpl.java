package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
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
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.RandomUtils;

import net.sf.json.JSONObject;

/**
 * 汇鑫支付
 * @author TX
 */
public class HXPayServiceImpl implements PayService{

	private final static Logger logger = LoggerFactory.getLogger(HXPayServiceImpl.class);
	
	private String mch_id;
	private String secret;
	private String notify_url;
	private String payUrl;
	public HXPayServiceImpl(Map<String,String> map){
		if(map.containsKey("mch_id")){
			this.mch_id = map.get("mch_id");
		}
		if(map.containsKey("secret")){
			this.secret = map.get("secret");
		}
		if(map.containsKey("notify_url")){
			this.notify_url = map.get("notify_url");
		}
		if(map.containsKey("payUrl")){
			this.payUrl = map.get("payUrl");
		}
	}
	
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		logger.info("[汇鑫支付]开始网银支付...");
		Map<String,String> map = sealRequest(payEntity);
		
		String sign = generatorSign(map);
		map.put("sign", sign);
		logger.info("[汇鑫支付]请求参数:{}",map);
		try {
			String result = HttpUtils.toPostForm(map, payUrl);
			if(StringUtils.isEmpty(result)){
				logger.info("[汇鑫支付]请求第三方支付错误,无返回值...");
				return PayResponse.error("[汇鑫支付]无返回值!");
			}
			
			JSONObject obj = JSONObject.fromObject(result);
			if("200".equals(obj.getString("status"))){
				return PayResponse.sm_link(payEntity, obj.getString("pay_url"),"[汇鑫支付]下单成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("[汇鑫支付]开始扫码支付...");
		Map<String,String> map = sealRequest(payEntity);
		logger.info("[汇鑫支付]扫码支付签名开始..");
		String sign = generatorSign(map);
		map.put("sign", sign);
		String message = "";//下单返回信息
		logger.info("[汇鑫支付]请求参数:{}",map);
		try {
			String result = HttpUtils.toPostForm(map, payUrl);
			if(StringUtils.isEmpty(result)){
				logger.info("[汇鑫支付]请求第三方支付错误,无返回值...");
				return PayResponse.error("[汇鑫支付]无返回值!");
			}
			
			JSONObject obj = JSONObject.fromObject(result);
			logger.info("[汇鑫支付]支付请求返回结果:{}",obj);
			message = obj.getString("message");
			if("200".equals(obj.getString("status"))){
				return PayResponse.sm_qrcode(payEntity, obj.getString("pay_url"),"[汇鑫支付]下单成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[汇鑫支付]订单号:{} 下单失败 ",payEntity.getOrderNo());
		}
		return PayResponse.error("[汇鑫支付]下单失败,失败原因:"+message);
	}

	@Override
	public String callback(Map<String, String> data) {
		try{
			String sourceSign = data.get("sign");
			logger.info("[汇鑫支付]回调函数验签原签名串:{}",sourceSign);
			
			String sign = generatorSign(data);//签名
			logger.info("[汇鑫支付]回调函数加密之后签名:{}",sign);
			if(sourceSign.equalsIgnoreCase(sign)) return "success";
		}catch(Exception e){
			e.printStackTrace();
			logger.error("[汇鑫支付]回调函数验签异常:{}",e.getMessage());
		}
		
		return "fail";
	}

	private Map<String,String> sealRequest(PayEntity payEntity){
		String amount = new DecimalFormat("##").format(payEntity.getAmount()*100);//订单金额，精确到分
		Map<String,String> map = new HashMap<String,String>();
		map.put("mch_id", mch_id);
		map.put("pay_type", payEntity.getPayCode());//支付类型
		map.put("total_fee", amount);
		map.put("notify_url", notify_url);//回调函数
		map.put("ip", payEntity.getIp());//IP 58.64.40.26 "58.64.40.26" 
		map.put("nonce_str", RandomUtils.generateLowerString(20));//随机号
		map.put("out_trade_no", payEntity.getOrderNo());//订单号
		logger.info("[汇鑫支付]请求参数:{}",map);
		return map;
	}
	
	//签名错误
	private String generatorSign(Map<String,String> map){
		
		try {
			Map<String,String> sortMap = MapUtils.sortByKeys(map);
			StringBuilder strBuilder = new StringBuilder();
			for(Entry<String,String> entry : sortMap.entrySet()){
				
				if(StringUtils.isEmpty(entry.getValue()) || "sign".equals(entry.getKey())){
					continue;
				}
				
				strBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
			
			strBuilder.append("key=").append(secret);
			logger.info("[汇鑫支付]签名前参数:{}",strBuilder);
			String md5Value = MD5Utils.md5toUpCase_32Bit(strBuilder.toString());
			logger.info("[汇鑫支付]签名加密后:{}", md5Value);
			return md5Value;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[汇鑫支付]签名发生错误:{}",e.getMessage());
		}
		
		return null;
	}
}
