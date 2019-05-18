package com.cn.tianxia.api.pay.impl;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

public class HYPayServiceImpl implements PayService {
	private final static Logger logger = LoggerFactory.getLogger(HYPayServiceImpl.class);
	/**支付地址*/
	private String payUrl;
	/**商户编号*/
	private String payMemberid;
	/**商户接收支付成功数据的地址*/
	private String payNotifyUrl;
	/**商户密钥*/
	private String md5Key;
	
	public HYPayServiceImpl(Map<String,String> data) {
		if(data!=null){
			if(data.containsKey("payUrl")){
				this.payUrl = data.get("payUrl");
			}
			if(data.containsKey("payMemberid")){
				this.payMemberid = data.get("payMemberid");
			}
			if(data.containsKey("payNotifyUrl")){
				this.payNotifyUrl = data.get("payNotifyUrl");
			}
			if(data.containsKey("md5Key")){
				this.md5Key = data.get("md5Key");
			}
		}
	}
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("[HY]黄岩支付扫码支付开始======================START==================");
		try {
			//封装请求参数
			Map<String, String> data = sealRequest(payEntity);
			//生成签名串
			String sign = generatorSign(data);
			data.put("sign", sign);
			JSONObject reqJsonObj = JSONObject.fromObject(data);
			logger.info("[HY]黄岩支付请求参数:"+reqJsonObj.toString());
			//生成请求表单
			String resStr = HttpUtils.toPostJsonStr(reqJsonObj, payUrl);
			logger.info("[HY]黄岩支付支付响应信息:"+resStr);
			if(StringUtils.isBlank(resStr)){
				logger.info("[HY]黄岩支付支付扫码支付发起HTTP请求无响应结果");
				return PayResponse.error("[HY]黄岩支付支付扫码支付发起HTTP请求无响应结果");
			}
			JSONObject resObj = JSONObject.fromObject(resStr);
			if(resObj.containsKey("code")&&resObj.getInt("code")==0){
			    if(StringUtils.isNotBlank(payEntity.getMobile())) {
			        return PayResponse.sm_link(payEntity, resObj.getString("url"), "下单成功");
			    }
				return PayResponse.sm_qrcode(payEntity, resObj.getString("url"), "下单成功");
			}
			return PayResponse.error("[HY]黄岩支付下单失败"+resObj.getString("error"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[HY]黄岩支付生成异常:"+e.getMessage());
			return PayResponse.error("[HY]黄岩支付下单失败");
		}
	}

	@Override
	public String callback(Map<String, String> data) {
		try {
			String sourceSign = data.get("sign");
			StringBuilder sb = new StringBuilder();
			sb.append("customer_id=").append(data.get("customer_id")).append("&");
			sb.append("order_id=").append(data.get("order_id")).append("&");
			sb.append("out_transaction_id=").append(data.get("out_transaction_id")).append("&");
			sb.append("pay_result=").append(data.get("pay_result")).append("&");
			sb.append("pay_time=").append(data.get("pay_time")).append("&");
			sb.append("total_fee=").append(data.get("total_fee")).append("&");
			sb.append("key=").append(md5Key);
			String signStr = sb.toString();
			logger.info("[HY]黄岩支付回调生成待签名串"+signStr);
			String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
			logger.info("[HY]黄岩支付回调生成签名串"+sign);
			if(sign.equals(sourceSign)) return "success";
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			logger.info("[HY]黄岩支付回调生成签名串异常"+e.getMessage());
		}
		return null;
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
			String uid = UUID.randomUUID().toString();
			Map<String,String> data = new HashMap<>();
			data.put("customer_id", payMemberid);//商户号
			data.put("order_id", payEntity.getOrderNo());//订单号
			data.put("total_fee", df.format(payEntity.getAmount()*100));//总金额,单位：分 
			data.put("nonce_str", uid.replace("-", ""));//随机字符串，32位
			data.put("client_ip", payEntity.getIp());//客户ip
			data.put("pay_type", payEntity.getPayCode());//支付渠道
			data.put("user_id", payEntity.getuId());//用户标识
			data.put("notify_url", payNotifyUrl);//回调地址
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[HY]黄岩支付获取请求参数异常"+e.getMessage());
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
    public String generatorSign(Map<String,String> data) throws Exception{
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("customer_id=").append(data.get("customer_id")).append("&");
			sb.append("nonce_str=").append(data.get("nonce_str")).append("&");
			sb.append("order_id=").append(data.get("order_id")).append("&");
			sb.append("total_fee=").append(data.get("total_fee")).append("&");
			sb.append("key=").append(md5Key);
			//生成待签名串
			String signStr = sb.toString();
			logger.info("[HY]黄岩支付生成待签名串:{}",signStr);
			String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
			logger.info("[HY]黄岩支付生成加密签名串:{}",sign);
			return sign;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[HY]黄岩支付生成加密签名串失败"+e.getMessage());
			return null;
		}
    }
}
