package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

/**
 * 聚北支付
 */

public class JUBEIPayServiceImpl implements PayService{
	private final static Logger logger = LoggerFactory.getLogger(JUBEIPayServiceImpl.class);
	/**商户号*/
	private String payMemberid;
	/**支付地址*/
	private String payUrl;
	/**密钥*/
	private String md5Key;
	/**回调地址*/
	private String payNotifyUrl;
	/**版本号*/
	private String version;

	public JUBEIPayServiceImpl(Map<String,String> data) {
		if(data!=null){
			if(data.containsKey("payUrl")){
				this.payUrl = data.get("payUrl");
			}
			if(data.containsKey("payMemberid")){
				this.payMemberid = data.get("payMemberid");
			}
			if(data.containsKey("md5Key")){
				this.md5Key = data.get("md5Key");
			}
			if(data.containsKey("payNotifyUrl")){
				this.payNotifyUrl = data.get("payNotifyUrl");
			}
			if(data.containsKey("version")){
				this.version = data.get("version");
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
		logger.info("聚北扫码支付开始======================START==================");
		try {
			//封装请求参数
			Map<String, String> data = sealRequest(payEntity);
			String sign = generatorSign(data);
			data.put("sign", sign);
			logger.info("聚北扫码支付请求参数："+JSONObject.fromObject(data));
			String resStr = HttpUtils.toPostJsonStr(JSONObject.fromObject(data), payUrl);
			logger.info("聚北扫码支付响应信息："+resStr);
			if(StringUtils.isBlank(resStr)){
				logger.info("聚北扫码支付发起http请求无响应："+resStr);
				PayResponse.error("聚北扫码支付发起http请求无响应");
			}
			JSONObject resJsonObj = JSONObject.fromObject(resStr);
			if(resJsonObj.containsKey("result_code" )&& "SUCCESS".equals(resJsonObj.getString("result_code"))){
				if(StringUtils.isNotBlank(payEntity.getMobile())){
					return PayResponse.sm_link(payEntity, resJsonObj.getString("code_url"), "下单成功");
				}
				return PayResponse.sm_qrcode(payEntity, resJsonObj.getString("code_url"), "下单成功");
			}
			return PayResponse.error("聚北扫码支付下单失败:"+resJsonObj.get("return_msg"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("聚北扫码支付请求异常:"+e.getMessage());
			return PayResponse.error("聚北扫码支付请求异常，请稍后重试");
		}
	}
	@Override
	public String callback(Map<String, String> data) {
		try {
			String sourceSign = data.get("sign");
			logger.info("[JUBEI]聚北支付回调源签名串："+sourceSign);
			String sign = generatorSign(data);
			if(sign.equalsIgnoreCase(sourceSign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JUBEI]聚北支付回调验签异常:"+e.getMessage());
        }
        return "fail";
	}
	/**
     *
     * @Description 封装支付请求参数
     * @param
     * @param
     * @return
     * @throws Exception
     */
	public Map<String, String> sealRequest(PayEntity payEntity){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String amount = new DecimalFormat("0.00").format(payEntity.getAmount());//订单金额，单位为元
		String uuid=UUID.randomUUID().toString();//随机字符串
		Map<String,String> data = new HashMap<>();
		data.put("method",payEntity.getPayCode());//接口名称alipay.h5,  支付类型编码.在商家配置信息中配置
		data.put("version",version);//版本信息1.0，在商家配置信息中配置
		data.put("nonce_str",uuid.replace("-", ""));//随机字符串随机字符串，不大于32位。推荐随机数生成算法
		data.put("mch_id",payMemberid);//商户号平台分配的商户号
		data.put("mch_order_no",payEntity.getOrderNo());//商户订单号商户系统内部订单号，要求32个字符内，只能是数字、大小写字母，且在同一个商户号下唯一
		data.put("body","recharge");//商品名称商品简单描述
		data.put("cur_code","CNY");//币种货币类型，符合ISO4217标准的三位字母代码。目前仅支持人民币，CNY
		data.put("total_amount",amount);//总金额总金额(单位元，两位小数)
		data.put("spbill_create_ip",payEntity.getIp());//终端IP终端IP，请填写支付用户的真实IP
		data.put("mch_req_time",sdf.format(new Date()));//订单提交时间订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010请使用UTC+8北京时间支付宝接口文档12
		data.put("notify_url",payNotifyUrl);//通知地址后台通知地址，用于接收支付成功通知
		data.put("sign_type","MD5");//通知地址后台通知地址，用于接收支付成功通知
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
			if(StringUtils.isBlank(val) ||  "sign".equalsIgnoreCase(key)) continue;
			sb.append(key).append("=").append(val).append("&");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(md5Key);
        //生成待签名串
		String signStr = sb.toString();
        logger.info("[JUBEI]聚北支付生成待签名串:{}",signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(signStr);
        logger.info("[JUBEI]聚北支付生成加密签名串:{}",sign);
        return sign;
    }
}
