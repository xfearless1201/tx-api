package com.cn.tianxia.api.pay.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.utils.mjf.MJFToolKit;

import net.sf.json.JSONObject;

/**
 * 明捷付
 * 
 * @author hb
 * @date 2018-05-03
 */
public class MJFPayServiceImpl implements PayService {
	/**商户号*/
	private String merchNo;
	/**商户密钥*/
	private String key;
	/**支付地址*/
	private String reqUrl;
	/**后台回调*/
	private String callBackUrl;
	/**支付商版本号(固定值V2.0.0.0)*/
	private String version;
	/**商品名称*/
	private String goodsName;
	/**编码*/
	private String charset;
	/**支付公钥*/
	private String PAY_PUBLIC_KEY;
	
	private final static Logger logger = LoggerFactory.getLogger(MJFPayServiceImpl.class);
	
	public MJFPayServiceImpl(Map<String, String> pmap) {
		if(pmap != null) {
			//商户号
			if(pmap.containsKey("merchNo")) {
				this.merchNo = pmap.get("merchNo");
			}
			//商户密钥
			if(pmap.containsKey("key")) {
				this.key = pmap.get("key");
			}
			//支付地址
			if(pmap.containsKey("reqUrl")) {
				this.reqUrl = pmap.get("reqUrl");
			}
			//后台回调地址
			if(pmap.containsKey("callBackUrl")) {
				this.callBackUrl = pmap.get("callBackUrl");
			}
			//支付商版本号
			if(pmap.containsKey("version")) {
				this.version = pmap.get("version");
			}
			//商品名称
			if(pmap.containsKey("goodsName")) {
				this.goodsName = pmap.get("goodsName");
			}
			//编码
			if(pmap.containsKey("charset")) {
				this.charset = pmap.get("charset");
			}
			//支付公钥
			if(pmap.containsKey("PAY_PUBLIC_KEY")) {
				this.PAY_PUBLIC_KEY = pmap.get("PAY_PUBLIC_KEY");
			}
		}
	}

	/**
	 * 支付地址：http://39.105.8.4:9803/api/pay 代付地址：http://39.105.8.4:9803/api/remit
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	@Override
	public JSONObject smPay(PayEntity payEntity) {
		
		String resultJsonStr = null;
		String mobile = payEntity.getMobile();
		String userName = payEntity.getUsername();
		double amount = payEntity.getAmount() ;
		String orderNum = payEntity.getOrderNo();//createOrderNum();
		try {

			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("version", this.version);
			metaSignMap.put("merchNo", this.merchNo);
			metaSignMap.put("goodsName", this.goodsName);
			metaSignMap.put("callBackUrl", this.callBackUrl);// 后台回调地址
			metaSignMap.put("charset", this.charset);
			
			metaSignMap.put("randomNum", MJFToolKit.getRandomStr(4));// 4位随机数
			metaSignMap.put("netwayCode", payEntity.getPayCode());// WX:微信支付,ZFB:支付宝支付,QQ
			metaSignMap.put("orderNum", orderNum);//长度20
			metaSignMap.put("amount", String.valueOf((int)amount*100));// 单位:分
			metaSignMap.put("callBackViewUrl", payEntity.getRefererUrl());// 回显地址
			metaSignMap.put("sign", MJFToolKit.MD5(mapToJson(metaSignMap) + key, "UTF-8"));//参数签名
			String paycode=payEntity.getPayCode();
			if("WX".equals(paycode)){
			    this.reqUrl="http://wx.mjzfpay.com:90/api/pay";
			}else if("ZFB".equals(paycode)){
			    this.reqUrl="http://zfb.mjzfpay.com:90/api/pay";
			}else if("QQ".equals(paycode)){
                this.reqUrl="http://qq.mjzfpay.com:90/api/pay";
            }else if("UNION_WALLET".equals(paycode)){
                this.reqUrl="http://union.mjzfpay.com:90/api/pay";
            }else if("JD".equals(paycode)){
                this.reqUrl="http://jd.mjzfpay.com:90/api/pay";
            }else if("WX_WAP".equals(paycode)){
                this.reqUrl="http://wxwap.mjzfpay.com:90/api/pay";
            }else if("ZFB_WAP".equals(paycode)){
                this.reqUrl="http://zfbwap.mjzfpay.com:90/api/pay";
            }else if("QQ_WAP".equals(paycode)){
                this.reqUrl="http://qqwap.mjzfpay.com:90/api/pay";
            }else if("JD_WAP".equals(paycode)){
                this.reqUrl="http://jdwap.mjzfpay.com:90/api/pay";
            }
			
			byte[] dataStr = MJFToolKit.encryptByPublicKey(MJFToolKit.mapToJson(metaSignMap).getBytes(MJFToolKit.CHARSET), this.PAY_PUBLIC_KEY);
			String param = Base64.getEncoder().encodeToString(dataStr);
			String reqParam = "data=" + URLEncoder.encode(param, MJFToolKit.CHARSET) + "&merchNo=" + metaSignMap.get("merchNo") + "&version=" + metaSignMap.get("version");
			resultJsonStr = MJFToolKit.request(this.reqUrl, reqParam);
			logger.info("MJF支付返回参数："+resultJsonStr);
			JSONObject resultJson  = JSONObject.fromObject(resultJsonStr);
			//支付正确(1.支付状态="00"，2.sign校验成功)
			if ("00".equals(resultJson.getString("stateCode")) && checkSign(resultJson)) {
				// pc端
				if (StringUtils.isEmpty(mobile)) {
					return PayUtil.returnPayJson("success", "2", "支付接口请求成功!", userName, amount, orderNum,
							resultJson.getString("qrcodeUrl"));
				} else {
					// 手机端
					return PayUtil.returnPayJson("success", "4", "支付接口请求成功!", userName, amount, orderNum,
							resultJson.getString("qrcodeUrl"));
				}
			} else {
				return PayUtil.returnPayJson("error", "2", resultJson.getString("msg"), userName, amount, orderNum, "");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JSONObject json = new JSONObject();
			json.put("res_type", "1");
			json.put("status", "error");
			json.put("acount", String.valueOf(amount));
			json.put("user_name", userName);
			json.put("order_no", orderNum);			
			json.put("msg", resultJsonStr);
			json.put("html", "");

			return json;
		}
		
	}
	
	//校验签名
	private boolean checkSign(JSONObject r_json) {
		String resultSign = r_json.getString("sign");
		r_json.remove("sign");
		String targetString = MJFToolKit.MD5(r_json.toString() + key, "UTF-8");
		if (targetString.equals(resultSign)) {
			logger.info("签名校验成功");
			return true;
		}
		return false;
	}
	

	private static String mapToJson(Map<String, String> map) {
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		StringBuffer json = new StringBuffer();
		json.append("{");
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			json.append("\"").append(key).append("\"");
			json.append(":");
			json.append("\"").append(value).append("\"");
			if (it.hasNext()) {
				json.append(",");
			}
		}
		json.append("}");
		logger.info("mapToJson=" + json.toString());
		return json.toString();
	}


	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		return null;
	}
	
	/**
	 * 签名验证
	 * @param paramMap
	 * @return
	 */
	@Override
	public String callback(Map<String, String> paramMap) {
		System.out.println(paramMap);
		
		String sign = paramMap.remove("sign");
		logger.info("待验证签名:"+sign);
		String checkSign = MJFToolKit.MD5(mapToJson(paramMap) + this.key, "UTF-8");
		logger.info("本地签名:"+checkSign);
		if (sign.equals(checkSign)) {
			logger.info("签名验证成功");
			return "success";
		}

		logger.info("签名失败");
		return "";
	}

}
