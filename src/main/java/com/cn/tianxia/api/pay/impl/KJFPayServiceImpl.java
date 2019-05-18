package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.kjf.MD5;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName KJFPayServiceImpl
 * @Description 快捷付支付
 * @author Hardy
 * @Date 2018年12月24日 下午3:40:05
 * @version 1.0.0
 */
public class KJFPayServiceImpl implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(KJFPayServiceImpl.class);
    private String payUrl;//支付地址
    private String md5Key;//秘钥
    private String merchantId;//商户号
    private String service;//接口名称
    private String notifyUrl;//回调地址
    private String isOpen;//是否开启

    //构造器,初始化参数
    public KJFPayServiceImpl(Map<String, String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("md5Key")){
                this.md5Key = data.get("md5Key");
            }
            if(data.containsKey("merchantId")){
                this.merchantId = data.get("merchantId");
            }
            if(data.containsKey("service")){
                this.service = data.get("service");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            if(data.containsKey("isOpen")){
                this.isOpen = data.get("isOpen");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[KJF]快捷付网银支付开始================START====================");
        try {
            Map<String,String> data = sealRequest(payEntity, 0);
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[KJF]快捷付网银支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            if(payEntity.getPayCode().equals("903") && StringUtils.isNotBlank(payEntity.getMobile())){
                //手机支付宝
                if("1".equals(isOpen)){
                    payUrl = payUrl.replace("http", "https");
                }
            }
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[KJF]快捷付网银支付生成form表单请求结果:{}",formStr);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[KJF]快捷付网银支付异常:{}",e.getMessage());
            return PayResponse.error("[KJF]快捷付网银支付异常");
        }
    }
    
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[KJF]快捷付扫码支付开始================START====================");
        try {
            Map<String,String> data = sealRequest(payEntity, 0);
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[KJF]快捷付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            if(payEntity.getPayCode().equals("903") && StringUtils.isNotBlank(payEntity.getMobile())){
                //手机支付宝
                if("1".equals(isOpen)){
                    payUrl = payUrl.replace("http", "https");
                }
            }
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[KJF]快捷付扫码支付生成form表单请求结果:{}",formStr);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[KJF]快捷付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[KJF]快捷付扫码支付异常");
        }
    }

    /**
     * 异步通知回调验签
     * 
     * @param infoMap
     * @return
     */
    @Override
    public String callback(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (StringUtils.isEmpty(value) || "sign".equalsIgnoreCase(key)) {
                continue;
            }
            sb.append(key + "=" + value + "&");
        }
        String signatureStr = sb.substring(0, sb.length() - 1) + this.md5Key;
        logger.info("签名字符串 = " + signatureStr);
        String sign1 = MD5.GetMD5Code(signatureStr);
        String sign0 = params.get("Sign");
        if (sign1.equals(sign0)) {
            logger.info("回调验签成功");
            return "success";
        }
        logger.info("回调验签失败");
        return "fail";
    }
    
    
    /**
     * 
     * @Description 组装支付请求参数
     * @param entity
     * @param type 1网银支付 其他 扫码支付
     * @return 
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity,int type) throws Exception{
        logger.info("[KJF]快捷付支付组装支付请求参数开始================START=====================");
        try {
            
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("service", service);
            data.put("merchantId", merchantId);
            data.put("notifyUrl", notifyUrl);
            data.put("returnUrl", entity.getRefererUrl());
            data.put("signType", "MD5");
            data.put("inputCharset","UTF-8");
            data.put("subject","TOP-UP");
            data.put("body", "TOP-UP");
            data.put("outOrderId", entity.getOrderNo());
            data.put("transAmt",amount);
            if(type == 1){
                //网银支付
                data.put("defaultBank", entity.getPayCode());
                data.put("payMethod","905");//网银直连
            }else{
                data.put("payMethod",entity.getPayCode());
            }
            data.put("channel", "B2C");
            data.put("cardAttr", "01");
            data.put("attach","TOP-UP");
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[KJF]快捷付支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[KJF]快捷付支付组装支付请求参数异常");
        }
    }
    
    /**
     * 
     * @Description 生成签名串
     * @param data
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> data) throws Exception{
        logger.info("[KJF]快捷付生成签名串开始====================START======================");
        try {
            StringBuffer sb = new StringBuffer();
            //排序
            Map<String,String> sortmap = MapUtils.sortByKeys(data);
            Iterator<String> iterator = sortmap.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = sortmap.get(key);
                if(org.apache.commons.lang3.StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key) 
                        || "signType".equalsIgnoreCase(key))continue;
                sb.append("&").append(key).append("=").append(val);
            }
            
            String signStr = sb.append(md5Key).toString().replaceFirst("&", "");
            logger.info("[KJF]快捷付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[KJF]快捷付生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[KJF]快捷付生成签名串异常:{}",e.getMessage());
            throw new Exception("[KJF]快捷付生成签名串异常");
        }
    }

}
