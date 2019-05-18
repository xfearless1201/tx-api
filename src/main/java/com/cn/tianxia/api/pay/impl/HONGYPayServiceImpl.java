package com.cn.tianxia.api.pay.impl;

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
 * 鸿运支付
 * @author Meredith
 *
 */
public class HONGYPayServiceImpl implements PayService {
	private final static Logger logger = LoggerFactory.getLogger(HONGYPayServiceImpl.class);
	/**支付地址*/
	private String payUrl;
	/**商户编号*/
	private String payMemberid;
	/**商户接收支付成功数据的地址*/
	private String payNotifyUrl;
	/**商户密钥*/
	private String md5Key;
	
	public HONGYPayServiceImpl(Map<String,String> data) {
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
		logger.info("[HONGY]鸿运支付扫码支付开始======================START==================");
		try {
			//封装请求参数
			Map<String, String> data = sealRequest(payEntity);
			//生成签名串
			String sign = generatorSign(data);
			data.put("pay_productname", "Pay");//商品名称
			data.put("pay_md5sign", sign);
			logger.info("[HONGY]鸿运支付请求参数:"+JSONObject.fromObject(data).toString());
			//生成请求表单
			String resStr = HttpUtils.generatorForm(data, payUrl);
			logger.info("[HONGY]鸿运支付响应信息:"+resStr);
			if(StringUtils.isBlank(resStr)){
				logger.info("[HONGY]鸿运支付扫码支付发起HTTP请求无响应结果");
				return PayResponse.error("[HONGY]鸿运支付扫码支付发起HTTP请求无响应结果");
			}
			return PayResponse.sm_form(payEntity, resStr, "下单成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[HONGY]鸿运支付生成异常:"+e.getMessage());
			return PayResponse.error("[HONGY]鸿运支付下单失败");
		}
	}

	@Override
	public String callback(Map<String, String> data) {
		try {
			data.remove("attach");
			String sourceSign = data.remove("sign");
			String sign = generatorSign(data);
			logger.info("[HONGY]鸿运支付回调生成签名串"+sign);
			if(sign.equalsIgnoreCase(sourceSign)) return "success";
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[HONGY]鸿运支付回调生成签名串异常"+e.getMessage());
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String toDate = sdf.format(new Date());
			DecimalFormat df = new DecimalFormat("0.00");
			Map<String,String> data = new HashMap<>();
			data.put("pay_memberid", payMemberid);//商户号
			data.put("pay_orderid", payEntity.getOrderNo());//商户订单号
			data.put("pay_applydate", toDate);//请求时间
			data.put("pay_bankcode", payEntity.getPayCode());//银行编码
			data.put("pay_notifyurl", payNotifyUrl);//回调地址
			data.put("pay_callbackurl", payNotifyUrl);//回调地址
			data.put("pay_amount", df.format(payEntity.getAmount()));//交易金额 单位：元
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[HONGY]鸿运支付获取请求参数异常"+e.getMessage());
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
			logger.info("[HONGY]鸿运支付生成待签名串:{}",signStr);
			String sign = MD5Utils.md5toUpCase_32Bit(signStr);
			logger.info("[HONGY]鸿运支付生成加密签名串:{}",sign);
			return sign;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[HONGY]鸿运支付生成加密签名串失败"+e.getMessage());
			return null;
		}
    }
}
