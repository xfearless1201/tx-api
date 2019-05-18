package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.jhz.MD5Utils;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.RandomUtils;

import net.sf.json.JSONObject;

/**
 * 商盟支付
 * @author TX
 */
public class SMZFPayServiceImpl implements PayService {
	private final static Logger logger = LoggerFactory.getLogger(SMZFPayServiceImpl.class);
	/**商户号**/
	private String app_id;
	/***支付地址**/
	private String payUrl;
	/***回调地址**/
	private String notifyUrl;
	/**秘钥**/
	private String secret;
	
	public SMZFPayServiceImpl(Map<String,String> map){
		if(map.containsKey("app_id")){
			this.app_id = map.get("app_id");
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
	
	/**
	 * 银联扫描..
	 */
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		logger.info("商盟支付 开始银联扫描支付请求....");
		try{
			Map<String,String> map = sealParameter(payEntity,1);
			
			String sign = generatorSign(map);
			map.put("sign", sign);
			logger.info("商盟支付 银联扫描请求的参数:{}", map);
			String response = HttpUtils.toPostForm(map, payUrl);
			logger.info("商盟支付 银联扫描请求地址={}返回结果:{}",payUrl,response);
			if(StringUtils.isEmpty(response)){
				return PayResponse.error("商盟支付 银联扫描请求返回为null,请联系支付商");
			}
			
			JSONObject jsonObject = JSONObject.fromObject(response);
			logger.info("jsonObject = {}", jsonObject);
			if(jsonObject.containsKey("code") && "200".equals(jsonObject.getString("code"))){
				JSONObject data = jsonObject.getJSONObject("data");
				logger.info("商盟支付 银联扫描请求检索返回值:{}", data);
				return PayUtil.returnWYPayJson("success", "link", data.getString("h5_pay_url"), "", "pay");
			}
			
			return PayResponse.error(response);
		}catch(Exception e){
			e.printStackTrace();
			logger.info("商盟支付 出现错误");
			return PayResponse.error("商盟银联扫描支付出现代码错误");
		}
	}

	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("商盟支付 开始扫描支付请求");
		try{
			Map<String,String> map = sealParameter(payEntity,0);
		
			String sign = generatorSign(map);
			map.put("sign", sign);
			logger.info("商盟支付 请求的参数:{}", map);
			String response = HttpUtils.toPostForm(map, payUrl);
			logger.info("商盟支付 请求地址={}返回结果:{}",payUrl,response);
			if(StringUtils.isEmpty(response)){
				return PayResponse.error("商盟支付 请求返回为null,请联系支付商");
			}
			
			JSONObject jsonObject = JSONObject.fromObject(response);
			logger.info("jsonObject = {}", jsonObject);
			if(jsonObject.containsKey("code") && "200".equals(jsonObject.getString("code"))){
				JSONObject data = jsonObject.getJSONObject("data");
				logger.info("商盟支付 请求检索返回值:{}", data);
				return PayResponse.sm_link(payEntity, data.getString("h5_pay_url"), "商盟支付成功");
			}
			
			return PayResponse.error(response);
		}catch(Exception e){
			e.printStackTrace();
			logger.info("商盟支付 出现错误");
			return PayResponse.error("商盟支付出现代码错误");
		}
	}

	@Override
	public String callback(Map<String, String> data) {
		logger.info("商盟支付 开始回调函数, 回调参数值:{}",data);
		try{
			String sourceSign = data.get("sign");
			logger.info("商盟支付 获取加密值:{}", sourceSign);
			String sign = generatorSign(data);
			logger.info("商盟支付 组装参数加密值:{}",sign);
			if(sign.equalsIgnoreCase(sourceSign)){
				return "success";
			}
			
		}catch(Exception e){
			e.printStackTrace();
			logger.info("商盟支付 回调出现错误,错误提示:{}", e.getMessage());
		}
		return "fail";
	}

	private Map<String,String> sealParameter(PayEntity payEntity, int type){
		logger.info("商盟支付 开始组装参数");
		String money = new DecimalFormat("#.##").format(payEntity.getAmount()*100);
		
		Map<String,String> map = new TreeMap<String,String>();
		map.put("app_id", app_id);
		map.put("order_id", payEntity.getOrderNo());
		map.put("remark", "TOP-UP");
		map.put("user_id", payEntity.getuId());
		map.put("client_ip", payEntity.getIp());//payEntity.getIp()
		map.put("price", money);
		//银联
		if(type == 1){
			map.put("pay_type", "8");
			map.put("extend", payEntity.getPayCode());//银行编码
		}else{
			map.put("pay_type", payEntity.getPayCode());
		}
		map.put("ts", System.currentTimeMillis()+"");
		map.put("rand", RandomUtils.generateString(6));//6位随机数
		logger.info("商盟支付 开始组装参数结果:{}", map);
		return map;
	}
	
	private String generatorSign(Map<String,String> data) throws Exception{
		try {
			logger.info("[SMZF]商盟支付生产签名串====参数结果:{}", data);
			//参数排序
			Map<String,String> sortmap = MapUtils.sortByKeys(data);
			
			StringBuffer sb = new StringBuffer();
			
			Iterator<String> iterator = sortmap.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String val = sortmap.get(key);
				if(StringUtils.isBlank(val) || key.equalsIgnoreCase("sign")) continue;
				
				sb.append(key).append("=").append(val).append("&");
			}
			sb.append("key=").append(secret);
			
			//生产待签名串
			String signStr = sb.toString();
			logger.info("[SMZF]商盟支付待签名串:{}",signStr);
			//进行MD5加密
			String sign = MD5Utils.md5(signStr);
			logger.info("[SMZF]商盟支付生产加密签名串:{}",sign);
			return sign;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[SMZF]商盟支付 生产签名串异常:{}",e.getCause());
			throw new Exception("[SMZF]商盟支付 生产签名串异常!");
		}
	}
}
