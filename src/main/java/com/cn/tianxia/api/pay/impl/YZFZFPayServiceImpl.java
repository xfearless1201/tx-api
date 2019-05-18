package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.jhz.MD5Utils;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.yj.XmlUtils;

import net.sf.json.JSONObject;

/**
 * 新YZF支付
 * @author TX
 */
public class YZFZFPayServiceImpl implements PayService {
	private final static Logger logger = LoggerFactory.getLogger(YZFZFPayServiceImpl.class);
	/**商户编号**/
	private String mch_id;
	/**回调地址**/
	private String notify_url;
	/**支付地址**/
	private String payUrl;
	/***秘钥**/
	private String secret;
	
	public YZFZFPayServiceImpl(Map<String,String> map){
		if(map.containsKey("mch_id")){
			this.mch_id = map.get("mch_id");
		}
		if(map.containsKey("notify_url")){
			this.notify_url = map.get("notify_url");
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
		return null;
	}
	
	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("新YZF支付 开始扫描支付请求");
		try{
			Map<String,String> map = sealParameter(payEntity,0);
		
			String sign = generatorSign(map);
			map.put("sign", sign);
			logger.info("新YZF支付 请求的参数:{}", map);
			
			String xml = XmlUtils.toXml(map,true);
			logger.info("新YZF支付生成 xml 字符:{}",xml);
			
			String response = HttpUtils.toPostForm(xml, payUrl);
			logger.info("新YZF支付 请求返回的内容:{}",response);
			if(StringUtils.isEmpty(response)){
				return PayResponse.error("新YZF支付 请求返回为null,请联系支付商");
			}
			
			Map<String,String> responseMap = XmlUtils.xmlStr2Map(response);
			if(responseMap.containsKey("status") && "0".equals(responseMap.get("status"))){
				String pay_info = responseMap.get("pay_info");
				return PayResponse.sm_link(payEntity, pay_info, "新YZF支付成功");
			}
			
			return PayResponse.error(response);
		}catch(Exception e){
			e.printStackTrace();
			logger.info("新YZF支付 出现错误");
			return PayResponse.error("新YZF支付出现代码错误");
		}
	}

	/**
	 * 回调函数
	 */
	@Override
	public String callback(Map<String, String> data) {
		try{
			logger.info("新YZF支付 回调函数参数:{} ", data);
			String sourceSign = data.get("sign");
			logger.info("新YZF支付 原加密参数:{}",sourceSign);
			String sign = generatorSign(data);
			if(sign.equalsIgnoreCase(sourceSign)){
				return "success";
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.info("新YZF支付 回调函数错误,错误信息:{}", e.getMessage());
		}
		
		return "fail";
	}

	/**
	 * 组装参数
	 * @param payEntity
	 * @param type
	 */
	private Map<String,String> sealParameter(PayEntity payEntity, int type){
		logger.info("新YZF支付 开始组装参数");
		String money = new DecimalFormat("#.##").format(payEntity.getAmount()*100);
		
		Map<String,String> map = new TreeMap<String,String>();
		map.put("mch_id", mch_id);
		map.put("out_trade_no", payEntity.getOrderNo());
		map.put("pay_type", payEntity.getPayCode());
		map.put("body", "TOP-UP");
		map.put("total_fee", money);
		map.put("notify_url", notify_url);
		map.put("mch_create_ip", "58.64.40.26");
		map.put("device_info", "AND_WAP");
		map.put("mch_app_name", "TOP-UP");
		map.put("mch_app_id", payEntity.getRefererUrl());
		
		logger.info("新YZF支付 开始组装参数结果:{}", map);
		return map;
	}
	
	//加密
	private String generatorSign(Map<String,String> map) throws Exception{
		logger.info("新YZF支付 开始加密参数:{}", map);
		Map<String,String> sortMap = MapUtils.sortByKeys(map);
		
		logger.info("新YZF支付 开始加密参数:{}", sortMap);
		StringBuilder sb = new StringBuilder();
		for(Entry<String,String> entry : sortMap.entrySet()){
			if("sign".equals(entry.getKey()) || StringUtils.isEmpty(entry.getValue())){
				continue;
			}
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		
		sb.append("key=").append(secret);
		logger.info("新YZF支付 加密前参数:{} ",sb);
		String md5Value = MD5Utils.md5(sb.toString());
		logger.info("新YZF支付 加密参数:{}", md5Value);
		return md5Value;
	}
	
}
