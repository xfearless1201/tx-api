package com.cn.tianxia.api.pay.impl;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

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
 * 
 * @author TX
 * 风携支付 FX
 */
public class FXPayServiceImpl implements PayService {

	private final static Logger logger = LoggerFactory.getLogger(FXPayServiceImpl.class);
	
	private String fx_merchant_id;//商户编号
	private String secret;//key
	private String payUrl;//支付地址
	private String notify_url;//回调地址
	private String fx_back_url;//支付成功后跳转到的地址
	private String fx_return_url;//是否响应收银台地址
	
	public FXPayServiceImpl(Map<String, String> map){
		if(map.containsKey("fx_merchant_id")){
			this.fx_merchant_id = map.get("fx_merchant_id");
		}
		if(map.containsKey("secret")){
			this.secret = map.get("secret");
		}
		if(map.containsKey("payUrl")){
			this.payUrl = map.get("payUrl");
		}
		if(map.containsKey("notify_url")){
			this.notify_url = map.get("notify_url");
		}
		if(map.containsKey("fx_back_url")){
			this.fx_back_url = map.get("fx_back_url");
		}
		if(map.containsKey("fx_return_url")){
			this.fx_return_url = map.get("fx_return_url");
		}
	}
	
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		
		return null;
	}

	/**
	 * 支付宝扫码
	 */
	@Override
	public JSONObject smPay(PayEntity payEntity) {
		try{
			Map<String,String> map = parameter(payEntity);//获取参数
			String sign = generatorSign(map,1);// sign
			map.put("fx_sign", sign);
			
			logger.info("[风携支付] 请求完整参数:{}",map);
			String result = HttpUtils.toPostForm(map, payUrl);//请求支付
			logger.info("[风携支付] 请求返回的结果:{} ",result);
			
			if(StringUtils.isBlank(result)){
				logger.info("[风携支付]请求无返回值");
				PayResponse.error("[风携支付]请求无返回值");
			}
			
			JSONObject json = JSONObject.fromObject(result);
			if(json.containsKey("fx_status") && 200==json.getInt("fx_status")){//成功
				logger.info("[风携支付]支付状态成功,跳转到支付页面:{}", json.getString("fx_cashier_url"));
				return PayResponse.sm_link(payEntity, json.getString("fx_cashier_url"), "支付成功!");
			}
			
			return PayResponse.error(json.toString());
		}catch(Exception e){
			logger.info("[风携支付]报错,报错信息:{}",e.getMessage());
			return PayResponse.error("风携支付出现代码问题");
		}
	}

	/**
	 * 回调函数
	 */
	@Override
	public String callback(Map<String, String> data) {
		
		logger.info("[风携支付]回调函数的参数");
		String sourceSign = data.get("fx_sign");
		logger.info("[风携支付]回调函数返回的签名:{}",sourceSign);
		String sign = generatorSign(data,2);
		logger.info("[风携支付]验签生成签名串:{}", sign);
		if(sourceSign.equalsIgnoreCase(sign)) return "success";
		return "fail";
	}

	private Map<String,String> parameter(PayEntity payEntity){
		
		Map<String,String> map = new LinkedHashMap<String,String>();
		map.put("fx_merchant_id", fx_merchant_id);
		map.put("fx_order_id", payEntity.getOrderNo());
		map.put("fx_order_amount", String.valueOf(payEntity.getAmount()));
		map.put("fx_pay", payEntity.getPayCode());
		map.put("fx_notify_url", notify_url);//回调路径
		map.put("fx_back_url", fx_back_url);
		map.put("fx_return_url", fx_return_url);//是否响应收银台地址
		
		logger.info("[风携支付]请求的参数:{}",map);
		
		return map;
	}

	/**
	 * 返回签名
	 * @param map
	 * @return
	 *  md5(md5(商务号|商户订单号|支付金额|异步通知地址|商户秘钥))  请求支付接口加密签名
	 *  md5(md5(商户号|商户订单号|平台订单号|支付金额|商户请求金额|商户秘钥|订单状态)) 回调加密签名
	 */
	private String generatorSign(Map<String,String> map,Integer type){
		try {
			logger.info("[风携支付]支付签名参数:{}",map);
			StringBuffer strBuff = new StringBuffer();
			strBuff.append(map.get("fx_merchant_id")).append("|")
			.append(map.get("fx_order_id")).append("|");
			if(map.containsKey("fx_transaction_id")){
				strBuff.append(map.get("fx_transaction_id")).append("|");
			}
			strBuff.append(map.get("fx_order_amount")).append("|");
			if(map.containsKey("fx_original_amount")){
				strBuff.append(map.get("fx_original_amount")).append("|");
			}
			if(map.containsKey("fx_notify_url")){
				strBuff.append(map.get("fx_notify_url")).append("|");
			}
			strBuff.append(secret);
			
			if(type == 2){
				strBuff.append("|").append(map.get("fx_status_code"));
			}
			logger.info("[风携支付]签名加密之前的参数: {} ", strBuff);
			String md5Value = MD5Utils.md5toUpCase_32Bit(strBuff.toString()).toLowerCase();
			return MD5Utils.md5toUpCase_32Bit(md5Value).toLowerCase();//两次加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			logger.info("[风携支付]生成签名数据异常");
			return null;
		}
	}

}
