package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.RandomUtils;
import com.cn.tianxia.api.utils.xyz.HttpUtil;
import com.cn.tianxia.api.utils.xyz.XMLUtils;

import net.sf.json.JSONObject;

/*
 * XYZ 对接信誉支付
 * WECHAT: 微信扫码、WECHAT_WAP: 微信WAP、WECHAT_OFFICE_ACCOUNT: 微信公众号、ALIPAY: 支付宝扫码、QQ: QQ扫码、QQ_WAP: QQ WAP、NET: 网关支付
 */
public class XYZPayServiceImpl implements PayService{
	private final static Logger logger = LoggerFactory.getLogger(XYZPayServiceImpl.class);
	private String mer_id;
	private String notify_url;//通知地址
	private String payUrl;//支付路径
	private String tokenUrl;//token接口
	private String secret;//密钥
	
	public XYZPayServiceImpl(Map<String, String> map){

		if(map.containsKey("payUrl")){
			this.payUrl = map.get("payUrl");
		}
		if(map.containsKey("mer_id")){
			this.mer_id = map.get("mer_id");
		}
		if(map.containsKey("notify_url")){
			this.notify_url = map.get("notify_url");
		}
		if(map.containsKey("secret")){
			this.secret = map.get("secret");
		}
		if(map.containsKey("tokenUrl")){
			this.tokenUrl = map.get("tokenUrl");
		}
	}
	
	/*
	 * 网银
	 */
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
	
		try{
			//获取支付请求参数
			Map<String,String> data = sealRequest(payEntity, 1);
			//生产签名串
			String sign = generatorSign(data);
			data.put("sign", sign);
			logger.info("[信誉支付]支付请求报文:{}",JSONObject.fromObject(data).toString());
			
			//生成XML请求报文
			String xmlParams = XMLUtils.createXMlRequest(data);

			//获取token
			Map<String,String> tokenmap = new HashMap<String,String>();
			tokenmap.put("appid",mer_id);
			tokenmap.put("secretid",secret);
			String tokenRep = HttpUtil.httpGet(tokenUrl, tokenmap);
			if(StringUtils.isBlank(tokenRep)){
				logger.info("[信誉支付]获取支付请求token失败,发起HTTP请求无响应结果:{},token请求地址:{}",
						JSONObject.fromObject(tokenmap).toString(),tokenUrl);
				return PayUtil.returnWYPayJson("error","form","获取支付请求token失败,发起HTTP请求无响应结果为空!",payEntity.getPayUrl(),"pay");
			}
			
			//解析token响应结果
			JSONObject tokenJson = XMLUtils.formatXMlToJson(tokenRep);
			if(tokenJson.containsKey("token") && StringUtils.isNotBlank(tokenJson.getString("token"))){
				String token = tokenJson.getString("token");
				logger.info("[信誉支付]token为:{}",token);
				//发起支付请求
				String xmlRep = HttpUtil.toPostIO(xmlParams, payUrl+"/union/net",token);
				if(StringUtils.isBlank(xmlRep)){
					logger.info("[信誉支付]发起第三方支付请求失败,发起HTTP请求无响应结果!");
					return PayUtil.returnWYPayJson("error","form","[信誉支付]发起第三方支付请求失败,发起HTTP请求无响应结果!",payEntity.getPayUrl(),"pay");
				}
				//解析响应结果
				JSONObject jsonObject = XMLUtils.formatXMlToJson(xmlRep);
				
				if(jsonObject.containsKey("status") && jsonObject.getString("status").equals("0") 
						&& jsonObject.containsKey("result_code") && jsonObject.getString("result_code").equals("0")){
					//接口请求成功
					String pay_info = jsonObject.getString("pay_info");
					logger.info("[信誉支付]返回支付结果:{}", pay_info);
					return PayUtil.returnWYPayJson("success","link",pay_info,"","pay");
				}
				
				return PayUtil.returnWYPayJson("error","form","[信誉支付]发起第三方支付请求失败,发起HTTP请求无响应结果!",payEntity.getPayUrl(),"pay");
			}

			return PayUtil.returnWYPayJson("error", "form", "[信誉支付]支付失败,token为空!", "", "");
		}catch(Exception e){
			e.printStackTrace();
			return PayUtil.returnWYPayJson("error", "form", "[信誉支付]支付失败,token为空!", "", "");
		}
	}

	/*
	 * 扫码
	 */
	@Override
	public JSONObject smPay(PayEntity payEntity) {
		
		try{
			//获取支付请求参数
			Map<String,String> data = sealRequest(payEntity, 0);
			logger.info("[信誉支付]扫码支付请求报文:{}",JSONObject.fromObject(data).toString());
			//生产签名串
			String sign = generatorSign(data);
			data.put("sign", sign);
			
			//生成XML请求报文
			String xmlParams = XMLUtils.createXMlRequest(data);
			logger.info("[信誉支付]扫码支付生产的 xml请求明文内容:{}",xmlParams);
			//获取token
			Map<String,String> tokenmap = new HashMap<String,String>();
			tokenmap.put("appid",mer_id);
			tokenmap.put("secretid",secret);
			String tokenRep = HttpUtil.httpGet(tokenUrl, tokenmap);
			if(StringUtils.isBlank(tokenRep)){
				logger.info("[信誉支付]获取支付请求token失败,发起HTTP请求无响应结果:{},token请求地址:{}",
						JSONObject.fromObject(tokenmap).toString(),tokenUrl);
				return PayUtil.returnPayJson("error", "2", "[信誉支付]扫码下单失败，获取 token 失败", "", 0, "", "");
			}
			//解析token响应结果
			JSONObject tokenJson = XMLUtils.formatXMlToJson(tokenRep);
			if(tokenJson.containsKey("token") && StringUtils.isNotBlank(tokenJson.getString("token"))){
				String token = tokenJson.getString("token");
				logger.info("[信誉支付]token为:{}",token);
				//获取url资源地址
				String resourceUrl = payEntity.getPayCode().replace(".", "/");
				//发起支付请求
				String xmlRep = HttpUtil.toPostIO(xmlParams, payUrl+"/"+resourceUrl,token);
				if(StringUtils.isBlank(xmlRep)){
					logger.info("[信誉支付]发起第三方支付请求失败,发起HTTP请求无响应结果!");
					return PayUtil.returnPayJson("error", "", "[信誉支付]下单失败,发起HTTP请求无响应结果", "", 0, "", "");
				}
				//解析响应结果
				JSONObject jsonObject = XMLUtils.formatXMlToJson(xmlRep);
				logger.info("[信誉支付]支付订单号:{},请求返回结果:{},",payEntity.getOrderNo(),xmlRep);
				
				if(jsonObject.containsKey("status") && jsonObject.getString("status").equals("0") 
						&& jsonObject.containsKey("result_code") && jsonObject.getString("result_code").equals("0")){
					//接口请求成功
					logger.info("[信誉支付]扫码支付是否手机端结果:{}",payEntity.getMobile());
					String code_url = "";
					logger.info("信誉支付mobile 值:{}",payEntity.getMobile());
					if(StringUtils.isNotBlank(payEntity.getMobile())){
						logger.info("信誉支付进入手机端.........");
					    //移动端
						code_url = jsonObject.getString("pay_info");
						logger.info("信誉支付手机移动端进行跳转路径:{}",code_url);
						return PayResponse.sm_link(payEntity,code_url,"下单成功!");
						//return PayUtil.returnPayJson("success","2","[信誉支付]扫码下单成功",payEntity.getUsername(),payEntity.getAmount(),payEntity.getOrderNo(),code_url);
					}else{
						logger.info("信誉支付进入pc扫码 或者 银联..........");
					    //PC端
					    if(payEntity.getPayCode().equalsIgnoreCase("union.quick")){
					        //快捷支付
					        code_url = jsonObject.getString("pay_info");
					        return PayUtil.returnPayJson("success","4","[信誉支付]扫码下单成功",payEntity.getUsername(),payEntity.getAmount(),payEntity.getOrderNo(),code_url);
					    }else{
					        code_url = jsonObject.getString("code_img_url");
					    }
					}
					return PayResponse.sm_link(payEntity, code_url, "下单成功");//线上二维码图片
					//return PayUtil.returnPayJson("success","3","[信誉支付]扫码下单成功",payEntity.getUsername(),payEntity.getAmount(),payEntity.getOrderNo(),code_url);
				}
				
				return PayUtil.returnPayJson("error", "2", "[XYZ]信誉支付下单失败!", payEntity.getUsername(), 0, "",jsonObject.toString());
			}
			return PayUtil.returnPayJson("error", "2", "[信誉支付]下单支付失败,token 失效", "", 0, "", tokenJson.toString());//tokenJson 失效
			
		}catch(Exception e){
			e.printStackTrace();
			return PayUtil.returnPayJson("error", "2", "[信誉支付]下单支付出现错误", "", 0, "", "");
		}
	}
	
	/**
	 * 回调
	 */
	@Override
	public String callback(Map<String, String> data) {
		logger.info("[XYZ]信誉支付回调验签开始===================START===================");
        try {
            //获取验签原签名串
            //验签字符串,MD5（shop_id + user_id + order_no +sign_key+money+type）；字符串相加再计算MD5一次，32位小写；shop_id 和sign_key登陆商家后台可以查看；
            String sourceSign = data.get("sign");
            
            logger.info("[XYZ]信誉支付验签原签名串:{}", sourceSign);
            
            //生成验签签名
            String sign = generatorSign(data);
            logger.info("[XYZ]信誉支付验签生成签名串:{}", sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XYZ]信誉支付回调验签异常:{}", e.getMessage());
        }
        return "fail";
	}
	
	
	/**
	 * 组装支付请求参数
	 * @param entity
	 * @param type 1 网银 2 扫码 
	 * @return
	 * @throws Exception
	 */
	private Map<String,String> sealRequest(PayEntity entity,Integer type) throws Exception{
		try {
			logger.info("[XYZ]信誉支付 开始组装参数");
			//创建支付请求参数存储对象
			Map<String,String> map = new HashMap<>();
			String amount = new DecimalFormat("##").format(entity.getAmount()*100);//订单金额,单位分
			map.put("version", "1.0");
			map.put("charset","UTF-8");
			map.put("sign_type", "MD5");
			map.put("mch_id", mer_id);
			map.put("out_trade_no",entity.getOrderNo());
			map.put("body","TOP-UP");
			map.put("total_fee",amount);//金额
			map.put("mch_create_ip",entity.getIp());//ip
			map.put("notify_url", notify_url);
			map.put("nonce_str", RandomUtils.generateLowerString(8));//随机参数,8位 
			map.put("imit_credit_pay", "0");//是否支持信用卡
			if(type == 1){
				//网银支付
				map.put("bank_id",entity.getPayCode());//银行编码
			}
			
			logger.info("[XYZ]信誉支付 请求的参数:{}",JSONObject.fromObject(map).toString());
			
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[XYZ]信誉支付组装支付请求参数异常!");
			throw new Exception("[XYZ]信誉支付组装支付请求参数异常!");
		}
	}
	
	/**
	 * 生产签名串
	 * @param data
	 * @param signType 1 支付  0 回调
	 * @return
	 * @throws Exception
	 */
	private String generatorSign(Map<String,String> data) throws Exception{
		try {
			logger.info("[XYZ]信誉支付生产签名串====");
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
			logger.info("[XYZ]信誉支付待签名串:{}",signStr);
			//进行MD5加密
			String sign = MD5Utils.md5toUpCase_32Bit(signStr);//大写
			logger.info("[XYZ]信誉支付生产加密签名串:{}",sign);
			return sign;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[XYZ]信誉支付 生产签名串异常:{}",e.getCause());
			throw new Exception("[XYZ]信誉支付 生产签名串异常!");
		}
	}
}
