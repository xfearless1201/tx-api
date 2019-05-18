package com.cn.tianxia.api.pay.impl;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
import com.cn.tianxia.api.utils.pay.XmlUtils;

import net.sf.json.JSONObject;
/**
 * 速龙支付
 * @author TX
 */
public class SLZFPayServiceImpl implements PayService{
	private final Logger logger = LoggerFactory.getLogger(SLZFPayServiceImpl.class);
	/**支付地址*/
	private String wyPayUrl;
	/**支付地址*/
	private String payUrl;
	/**商户编号*/
	private String payMemberid;
	/**商户接收支付成功数据的地址*/
	private String payNotifyUrl;
	/**商户密钥*/
	private String md5Key;
	
	public SLZFPayServiceImpl(Map<String,String> data) {
		if(data!=null){
			if(data.containsKey("wyPayUrl")){
				this.wyPayUrl = data.get("wyPayUrl");
			}
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
		logger.info("[SLZF]速龙网银支付开始======================START==================");
		try {
			//封装请求参数
			Map<String, String> data = sealRequest(payEntity,"0");
			//生成签名串
			String sign = generatorSign(data);
			data.put("sign", sign);
			String reqXml = getReqXml(data);
			logger.info("[SLZF]速龙网银支付请求参数:"+reqXml);
			//生成请求表单
			String resStr = HttpUtils.toPostXml(reqXml, wyPayUrl);
			logger.info("[SLZF]速龙网银支付响应信息:"+resStr);
			if(StringUtils.isBlank(resStr)){
				logger.info("[SLZF]速龙网银支付发起HTTP请求无响应结果");
				return PayResponse.error("[SLZF]速龙网银支付发起HTTP请求无响应结果");
			}
		    return PayResponse.wy_form(payEntity.getPayUrl(), resStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[SLZF]速龙扫码支付生成异常:"+e.getMessage());
			return PayResponse.error("[SLZF]速龙扫码支付下单失败");
		}
	}

	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("[SLZF]速龙扫码支付开始======================START==================");
		try {
			//封装请求参数
			Map<String, String> data = sealRequest(payEntity,"1");
			//生成签名串
			String sign = generatorSign(data);
			data.put("sign", sign);
			String reqXml = getReqXml(data);
			logger.info("[SLZF]速龙扫码支付请求参数:"+JSONObject.fromObject(data));
			//生成请求表单
			String resStr = "";
			if("alipay".equals(payEntity.getPayCode())||"wechat".equals(payEntity.getPayCode())){
			    resStr = HttpUtils.toPostXml(reqXml, wyPayUrl);
			}else{
			    resStr = HttpUtils.toPostXml(reqXml, payUrl);
			}
			logger.info("[SLZF]速龙扫码支付响应信息:"+resStr);
			if(StringUtils.isBlank(resStr)){
				logger.info("[SLZF]速龙扫码支付发起HTTP请求无响应结果");
				return PayResponse.error("[SLZF]速龙扫码支付发起HTTP请求无响应结果");
			}
			if("alipay".equals(payEntity.getPayCode())){
			    return PayResponse.sm_form(payEntity, resStr, "下单成功");
			}
			if("wechat".equals(payEntity.getPayCode())){
			    resStr = getScriptValue(resStr);
                return PayResponse.sm_link(payEntity, resStr, "下单成功");
            }
			JSONObject resJsonObj = XmlUtils.xml2Json(resStr);
			if(resJsonObj.containsKey("retcode") && "00".equals(resJsonObj.getString("retcode"))){
			    resJsonObj = resJsonObj.getJSONObject("data");
			    return PayResponse.sm_qrcode(payEntity, resJsonObj.getString("payurl"), "下单成功");
			}
		    return PayResponse.error("[SLZF]速龙扫码支付下单失败");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[SLZF]速龙扫码支付生成异常:"+e.getMessage());
			return PayResponse.error("[SLZF]速龙扫码支付异常");
		}
	}

	@Override
	public String callback(Map<String, String> data) {
		try {
			String sourceSign = data.remove("sign");
			logger.info("[SLZF]速龙扫码支付回调源签名串"+sourceSign);
			String sign = generatorSign(data);
			logger.info("[SLZF]速龙扫码支付回调生成签名串"+sign);
			if(sign.equals(sourceSign)) return "success";
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[SLZF]速龙扫码支付回调生成签名串异常"+e.getMessage());
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
	public Map<String, String> sealRequest(PayEntity payEntity,String type) throws Exception{
			DecimalFormat df = new DecimalFormat("0");
			Map<String,String> data = new HashMap<>();
			String uid = UUID.randomUUID().toString();
			data.put("customerid", payMemberid);//商户号
			data.put("orderid", payEntity.getOrderNo());//订单号
			data.put("total_fee", df.format(payEntity.getAmount()));//金额
			data.put("notify_url", payNotifyUrl);//异步通知地址
			data.put("nonce_str", uid.replace("-", ""));//随机字符串
			if("0".equals(type)){
				data.put("trade_type", "bank");//交易类型
			}else{
				data.put("trade_type", payEntity.getPayCode());//交易类型
				if("code".equals(payEntity.getPayCode())||"qq".equals(payEntity.getPayCode())){
					data.put("buyername", payEntity.getUsername());//支付类型
					data.put("subject", "Pay");//订单标题
					data.put("client_ip", payEntity.getIp());//订单标题
				}
			}
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
        sb.deleteCharAt(sb.length()-1);
        String signStr = sb.toString();
        //转译签名串
        signStr = URLDecoder.decode(signStr, "UTF-8");
        StringBuffer appendKey = new StringBuffer(signStr);
        appendKey.append("&key=").append(md5Key);
    	//生成待签名串
    	logger.info("[SLZF]速龙支付生成待签名串:{}",appendKey.toString());
    	String sign = MD5Utils.md5toUpCase_32Bit(appendKey.toString());
    	logger.info("[SLZF]速龙支付生成加密签名串:{}",sign);
    	return sign;
    }
    
    public String getReqXml(Map<String, String> data) throws Exception{
    	StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		Set<String> keySet = data.keySet();
		for (String key : keySet) {
			sb.append("<"+key+">").append("<![CDATA["+data.get(key)+"]]>").append("</"+key+">");
		}
		sb.append("</xml>");
		return sb.toString();
    }
    /**
     * 获取script标签内容
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param reqStr
     * @return
     */
    public String getScriptValue(String reqStr){
        String fromStr = reqStr.replace("<script type='text/javascript'>window.location.href='", "");
        String endStr = fromStr.replace("'</script>", "");
        return endStr;
    }
}
