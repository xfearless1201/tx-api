package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

public class APAYPayServiceImpl implements PayService {
	private final static Logger logger = LoggerFactory.getLogger(APAYPayServiceImpl.class);
	/**支付地址*/
	private String payUrl;
	/**商户编号*/
	private String payMemberid;
	/**商户接收支付成功数据的地址*/
	private String payNotifyUrl;
	/**商户密钥*/
	private String md5Key;
	/**一层支付方式*/
	private String payType;
	private String timeNum;

	public APAYPayServiceImpl(Map<String,String> data) {
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
			if(data.containsKey("payType")){
				this.payType = data.get("payType");
			}
			if(data.containsKey("timeNum")){
				this.timeNum = data.get("timeNum");
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
		logger.info("APAY支付扫码支付开始======================START==================");
		try {
			//封装请求参数
			Map<String, String> data = sealRequest(payEntity);
			//生成签名串
			String sign = generatorSign(data);
			data.put("sign", sign);
			String reqData = JSONObject.fromObject(data).toString();
			logger.info("APAY支付请求参数:"+reqData);
			//生成请求表单
			String resStr = HttpUtils.toPostForm(reqData, payUrl);
			logger.info("APAY支付支付响应信息:"+resStr);
			if(StringUtils.isBlank(resStr)){
				logger.info("APAY支付支付扫码支付发起HTTP请求无响应结果");
				return PayResponse.error("APAY支付支付扫码支付发起HTTP请求无响应结果");
			}
			JSONObject resObj = JSONObject.fromObject(resStr);
			if(resObj.containsKey("status")&&"1".equals(resObj.getString("status"))){
				resObj = resObj.getJSONObject("data");
				return PayResponse.sm_link(payEntity, resObj.getString("qrCodeAddress"), "下单成功");
			}
			return PayResponse.error("APAY支付下单失败"+resStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("APAY支付生成异常:"+e.getMessage());
			return PayResponse.error("APAY支付下单失败");
		}
	}

	@Override
	public String callback(Map<String, String> data) {
		try {
			String sourceSign = data.get("sign");
			StringBuffer sb = new StringBuffer();
			sb.append("SystemTradeNo=").append(data.get("SystemTradeNo")).append("&");
			sb.append("merchantTradeNo=").append(data.get("merchantTradeNo")).append("&");
			sb.append("orderStatus=").append(data.get("orderStatus")).append("&");
			sb.append("totalAmount=").append(data.get("totalAmount")).append("&");
			sb.append("key=").append(md5Key);
			String signStr = sb.toString();
			logger.info("APAY支付回调生成待签名串"+signStr);
			String sign = MD5Utils.md5toUpCase_32Bit(signStr);
			logger.info("APAY支付回调生成签名串"+sign);
			if(sign.equals(sourceSign.toUpperCase())) return "success";
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("APAY支付回调生成签名串异常"+e.getMessage());
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
		DecimalFormat df = new DecimalFormat("0.00");
		Map<String,String> data = new HashMap<>();
		data.put("payType", payType);//一层支付方式
		data.put("totalAmount", df.format(payEntity.getAmount()));//金额 单位：元
		data.put("outTradeNo", payEntity.getOrderNo());//订单号
		data.put("merchantNumber", payMemberid);//商户号
		data.put("subject", "Pay");//商品主题
		data.put("secondPayType", payEntity.getPayCode());//二级支付方式
		data.put("timeStamp", String.valueOf(System.currentTimeMillis()+timeNum));//二级支付方式
		data.put("notifyUrl", payNotifyUrl);
		return data;
	}
	/**
	 * 
	 * @Description 生成签名串
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String generatorSign(Map<String,String> data) throws Exception{
		Map<String,String> sortmap = MapUtils.sortByKeys(data);
		StringBuffer sb = new StringBuffer();
		Iterator<String> iterator = sortmap.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			String val = sortmap.get(key);
			if(StringUtils.isBlank(val) || key.equalsIgnoreCase("sign")) continue;
			sb.append(key).append("=").append(val).append("&");
		}
		sb.append("key=").append(md5Key);
		//生成待签名串
		String signStr = sb.toString();
		logger.info("APAY支付生成待签名串:{}",signStr);
		String sign = MD5Utils.md5toUpCase_32Bit(signStr);
		logger.info("APAY支付生成加密签名串:{}",sign);
		return sign;
	}
}
