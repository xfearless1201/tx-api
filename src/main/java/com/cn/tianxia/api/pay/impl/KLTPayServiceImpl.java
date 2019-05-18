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

public class KLTPayServiceImpl implements PayService {

	private final static Logger logger = LoggerFactory.getLogger(KLTPayServiceImpl.class);
	
	private String merchId;//商户号
	
	private String pcPayUrl;//支付请求地址
	
	private String mbPayUrl;//移动支付地址
	
	private String notifyUrl;//回调地址
	
	private String md5Key;//秘钥

	//构造器,出事化参数
	public KLTPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("merchId")){
                this.merchId = data.get("merchId");
            }
            if(data.containsKey("pcPayUrl")){
                this.pcPayUrl = data.get("pcPayUrl");
            }
            
            if(data.containsKey("mbPayUrl")){
                this.mbPayUrl = data.get("mbPayUrl");
            }
            
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            if(data.containsKey("md5Key")){
                this.md5Key = data.get("md5Key");
            }
        }
    }

    @Override
	public JSONObject wyPay(PayEntity payEntity) {
		logger.info("[KLT]开联通支付网银支付开始=======================START=========");
        try {
            //获取支付参数
            Map<String,String> data = sealRequest(payEntity, 1);
            //生成签名
            String sign = generatorSign(data,1);
            data.put("sign", sign);
            logger.info("[KLT]开联通支付网银支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            String payUrl = pcPayUrl;
            if(StringUtils.isNotBlank(payEntity.getMobile())){
                payUrl = mbPayUrl;
            }
            logger.info("[KLT]开联通支付网银支付请求地址:{}",payUrl);
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[KLT]开联通支付网银支付生成form表单结果:{}",formStr);
            return PayResponse.wy_form(payEntity.getPayUrl(), formStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[KLT]开联通支付网银支付异常:{}",e.getMessage());
            return PayResponse.wy_write("[KLT]开联通支付网银支付异常");
        }
	}

	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("[KLT]开联通支付扫码支付开始===============START=================");
	    try {
	        //获取支付参数
            Map<String,String> data = sealRequest(payEntity, 0);
            //生成签名
            String sign = generatorSign(data,1);
            data.put("sign", sign);
            logger.info("[KLT]开联通支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            String payUrl = pcPayUrl;
            if(StringUtils.isNotBlank(payEntity.getMobile())){
                payUrl = mbPayUrl;
            }
            logger.info("[KLT]开联通支付扫码支付请求地址:{}",payUrl);
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[KLT]开联通支付扫码支付生成form表单结果:{}",formStr);
	        return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[KLT]开联通支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[KLT]开联通支付扫码支付异常");
        }
	}

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[KLT]开联通支付回调验签开始===========START============");
        try {
            String sourceSign = data.get("sign");
            logger.info("[KLT]开联通支付回调验签原签名串:{}",sourceSign);
            String sign = generatorSign(data,2);
            logger.info("[KLT]开联通支付回调 验签生成签名串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[KLT]开联通支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }
    
    
    /**
     * 
     * @Description 封装支付请求参数
     * @param entity
     * @param type 1 网银支付 2扫码支付
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity,int type) throws Exception{
        logger.info("[KLT]开联通支付组装支付请求参数开始================start==============");
        try {
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);//已分为单位
            data.put("inputCharset","1");//默认填1；1代表UTF-8 
            data.put("pickupUrl",entity.getRefererUrl());//客户的取货地址，建议填写需http://格式的完整路径，可以加?id=123这类自定义参数
            data.put("receiveUrl",notifyUrl);//交易结果后台通知地址，必填需http://格式的完整路径，可以加?id=123这类自定义参数  
            data.put("version","v1.0");//固定为v1.0
            data.put("language","1");//1代表简体中文
            data.put("signType","0");//默认填0，固定选择值：0、1；0表示订单上送和交易结果通知都使用MD5进行签名
            data.put("merchantId",merchId);//数字串，商户在开联申请开户的商户号
            data.put("orderNo",entity.getOrderNo());//商户订单号，只允许使用字母、数字、- 、_,并以字母或数字开头；每商户提交的订单号，必须在当天的该商户所有交易中唯一
            data.put("orderAmount",amount);//金额，整型数字，金额与币种有关(生产环境金额大于1毛）如果是人民币，则单位是分，即10元提交时金额应为1000
            data.put("orderDatetime",new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));//商户订单时间，日期格式：yyyyMMDDhhmmss，例如：20121116020101
            data.put("orderCurrency","156");//156代表人民币 
            data.put("productName","TOP-UP");//英文或中文字符串，请勿首尾有空格字符
            if(StringUtils.isNotBlank(entity.getMobile())){
                //移动端
                data.put("productPrice",amount);//整型数字
                data.put("productNum","1");//整型数字，默认传1
            }
            if(type == 1){
                data.put("payType","1");//用户在支付时可以使用的支付方式，固定选择值：
                data.put("issuerId",entity.getPayCode());//
            }else{
                data.put("payType",entity.getPayCode());//用户在支付时可以使用的支付方式，固定选择值：
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[KLT]开联通支付组装请求参数异常:{}",e.getMessage());
            throw new Exception("[KLT]开联通支付组装请求参数异常");
        }
    }
    
    
    /**
     * 
     * @Description 生成签名串
     * @param data
     * @param type 1 支付    2 回调
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> data,int type) throws Exception{
        logger.info("[KLT]开联通支付生成签名串开始===============START==============");
        try {
            StringBuffer sb = new StringBuffer();
            //排序
            Map<String,String> sortmap = MapUtils.sortByKeys(data);
            Iterator<String> iterator = sortmap.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = sortmap.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key) || "null".equalsIgnoreCase(val)) continue;
                sb.append(key).append("=").append(val).append("&");
            }
            String signStr = sb.append("key=").append(md5Key).toString();
            logger.info("[KLT]开联通支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[KLT]开联通支付生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[KLT]开联通支付生成签名串异常:{}",e.getMessage());
            throw new Exception("[KLT]开联通支付生成签名串异常");
        }
    }
}
