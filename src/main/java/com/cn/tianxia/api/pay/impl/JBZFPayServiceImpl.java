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
 *
 * @ClassName JBZFPayServiceImpl
 * @Description 聚宝支付
 * @author Jacky
 * @Date 2019年1月31日 下午16:58:58
 * @version 1.0.0
 */
public class JBZFPayServiceImpl implements PayService {

    private String api_url /*= "https://gateway.jbpayvip.com/api/gateway"*/;

    private String notify_url/* = "http://txw.tx8899.com/TWY/Notify/JBZFNotify.do"*/;

    private String api_key /*= "m6ZQlERgeXre17hxjz2jFiyuh8OrZGfD"*/;

    private String merchant_no/* = "M0100"*/;

    private final static Logger logger = LoggerFactory.getLogger(JBZFPayServiceImpl.class);

    public JBZFPayServiceImpl(Map<String,String> map) {
        if(map.containsKey("api_url")){
            this.api_url = map.get("api_url");
        }
        if(map.containsKey("api_key")){
            this.api_key = map.get("api_key");
        }
        if(map.containsKey("merchant_no")){
            this.merchant_no = map.get("merchant_no");
        }
        if(map.containsKey("notify_url")){
            this.notify_url = map.get("notify_url");
        }
    }

    /**
     * 网银
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("聚宝支付网银渠道  wyPay(PayEntity payEntity = {}",payEntity);
        try {
            Map<String,String> data = sealRequest(payEntity, 1);
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("聚宝支付网银渠道请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String formStr = HttpUtils.generatorForm(data, api_url);
            logger.info("聚宝支付网银渠道支付生成form表单请求结果:{}",formStr);
            return PayResponse.wy_form(payEntity.getPayUrl(), formStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("聚宝支付网银渠道异常:{}",e.getMessage());
            return PayResponse.error("聚宝支付网银渠道支付异常");
        }
}

   @Override
   public JSONObject  smPay(PayEntity payEntity){
       logger.info("聚宝支付支付宝扫码渠道  wyPay(PayEntity payEntity = {}",payEntity);
       try {
           Map<String,String> data = sealRequest(payEntity, 0);
           String sign = generatorSign(data);
           data.put("sign", sign);
           logger.info("聚宝支付支付宝渠道请求参数报文:{}",JSONObject.fromObject(data).toString());
           //发起HTTP请求
           String formStr = HttpUtils.generatorForm(data, api_url);
           logger.info("聚宝支付网银渠道支付生成form表单请求结果:{}",formStr);
           return PayResponse.sm_form(payEntity, formStr, "下单成功");
       } catch (Exception e) {
           e.printStackTrace();
           logger.info("聚宝支付支付宝渠道异常:{}",e.getMessage());
           return PayResponse.error("聚宝支付支付宝渠道支付异常");
       }
   }
   
    @Override
    public String callback(Map<String, String> data) {
        logger.info("聚宝支付回调验签方法 callback(Map<String, String> data = {}  -start ", data);
        try {
            String sourceSign = data.get("sign");
            logger.info("聚宝支付回调验签获取签名原串：{}",sourceSign);
            String sign = generatorSign(data);
            logger.info("聚宝支付回调验签生成签名串:{}",sign);

            if(sourceSign.equalsIgnoreCase(sign)) return "success";

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("聚宝支付回调验签异常:{}",e.getMessage());
            logger.error(e.getMessage(),e);
        }
        return "faild";
    }

    /**
     * 组装参数
     * @param entity
     * @return
     */
    private Map<String,String> sealRequest(PayEntity entity,int type) throws Exception{
        logger.info("JB 聚宝支付参数组装 sealRequest(PayEntity entity = {} -start "+ entity);
        try {
            Map<String,String> dataMap = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额,单位元,保留两位小数
            dataMap.put("customerno",merchant_no);//商户号
            if(type == 1){
                dataMap.put("channeltype","onlinebank");//渠道类型
                dataMap.put("bankcode",entity.getPayCode());//支付编码
            }else{
                dataMap.put("channeltype",entity.getPayCode());//渠道类型
            }
            dataMap.put("customerbillno",entity.getOrderNo());
            dataMap.put("orderamount",amount);//金额
            dataMap.put("customerbilltime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//时间 yyyy-MM-dd HH:ss:mm
            dataMap.put("notifyurl",notify_url);//回调地址
            dataMap.put("ip",entity.getIp());
            dataMap.put("devicetype","web");//下单设备类型
            dataMap.put("customeruser","200");//商户客户识别码
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("JB   聚宝支付组装参数失败!",e);
            throw new Exception("[JB] 聚宝支付组装支付请求参数异常",e);
        }
    }

    /**
     * 加密
     * @param map
     * @return
     */
    private String generatorSign(Map<String,String> map) throws Exception{
        logger.info("聚宝支付加密方法 generatorSign(Map<String,String> map = {} -start " + map);
        try {
            //加入秘钥
            StringBuffer stringBuffer = new StringBuffer();
            Map<String,String> data = MapUtils.sortByKeys(map);
            Iterator<String> iterator = data.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = data.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                stringBuffer.append("&").append(key).append("=").append(val);
            }
            stringBuffer.append("&").append("key").append("=").append(api_key);

            String signStr = stringBuffer.toString().replaceFirst("&", "");
            logger.info("聚宝支付生成待签名串:{}",signStr);
            //生成MD5并转换大写
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("聚宝支付生成待签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("聚宝支付支付生成待签名串:{}",e.getMessage());
            throw new Exception("GB  冠宝支付生成签名异常",e);
        }
    }
}
