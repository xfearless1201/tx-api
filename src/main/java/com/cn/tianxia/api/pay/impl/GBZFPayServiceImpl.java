package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

public class GBZFPayServiceImpl implements PayService {
	private final static Logger logger = LoggerFactory.getLogger(GBZFPayServiceImpl.class);
	/**支付地址*/
	private String payUrl;
	/**商户编号*/
	private String payMemberid;
	/**商户接收支付成功数据的地址*/
	private String payNotifyUrl;
	/**商户密钥*/
	private String md5Key;
	
	public GBZFPayServiceImpl(Map<String,String> data) {
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
		logger.info("[GBZF]国宝支付扫码支付开始======================START==================");
		try {
			//封装请求参数
			Map<String, String> data = sealRequest(payEntity);
			//生成签名串
			String sign = generatorSign(data);
			data.put("sign", sign);
			JSONObject reqJsonObj = JSONObject.fromObject(data);
			logger.info("[GBZF]国宝支付请求参数:"+reqJsonObj.toString());
			//生成请求表单
			String resStr = HttpUtils.toPostJsonStr(reqJsonObj, payUrl);
			logger.info("[GBZF]国宝支付响应信息:"+resStr);
			if(StringUtils.isBlank(resStr)){
				logger.info("[GBZF]国宝支付扫码支付发起HTTP请求无响应结果");
				return PayResponse.error("[GBZF]国宝支付扫码支付发起HTTP请求无响应结果");
			}
			JSONObject resObj = JSONObject.fromObject(resStr);

			if ((resObj.containsKey("code")) && "0".equals(resObj.getString("code"))) {
		        String payUrl = resObj.getString("payUrl");
		        return PayResponse.sm_link(payEntity, payUrl, "下单成功");
		    }
			return PayResponse.error("[GBZF]国宝支付下单失败"+resStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[GBZF]国宝支付生成异常:"+e.getMessage());
			return PayResponse.error("[GBZF]国宝支付下单失败");
		}
	}

	@Override
	public String callback(Map<String, String> data) {
		try {
			String sourceSign = data.remove("sign");
			String sign = generatorSign(data);
			logger.info("[GBZF]国宝支付回调生成签名串"+sign);
			if(sign.equalsIgnoreCase(sourceSign)) return "success";
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[GBZF]国宝支付回调生成签名串异常"+e.getMessage());
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String toDate = sdf.format(new Date());
			DecimalFormat df = new DecimalFormat("0");
			Map<String,String> data = new HashMap<>();
			data.put("rtick", toDate);//时间戳
			data.put("version", "1.0.0");//版本号
			data.put("signType", "MD5");//加密方式
			data.put("merchNo", payMemberid);//商户号
			data.put("merchOrderNo", payEntity.getOrderNo());//商户订单号
			data.put("tradeType", payEntity.getPayCode());//交易方式
			data.put("settleType", "0");//结算类型
			data.put("tradeAmount", df.format(payEntity.getAmount()*100));//交易金额 单位：分
			data.put("currencyType", "CNY");//币种
			data.put("notifyUrl", payNotifyUrl);//回调地址
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[GBZF]国宝支付获取请求参数异常"+e.getMessage());
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
			sb.append(md5Key);
			//生成待签名串
			String signStr = sb.toString();
			logger.info("[GBZF]国宝支付生成待签名串:{}",signStr);
			String sign = DigestUtils.md5Hex(signStr.getBytes("UTF-8")).toUpperCase();
			logger.info("[GBZF]国宝支付生成加密签名串:{}",sign);
			return sign;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[GBZF]国宝支付生成加密签名串失败"+e.getMessage());
			return null;
		}
    }
}
