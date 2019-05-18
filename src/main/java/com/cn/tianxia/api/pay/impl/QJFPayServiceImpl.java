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
 * @ClassName QJFPayServiceImpl
 * @Description 全聚付支付
 * @author Jacky
 * @Date 2019年2月6日 上午11:58:58
 * @version 1.0.0
 */
public class QJFPayServiceImpl implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(QJFPayServiceImpl.class);

    private String app_id /*= "190282277"*/;
    private String api_url /*= "http://www.q33688.com/Pay_Index.html"*/;
    private String notify_url /*= "http://txw.tx8899.com/TYY/Notify/QJFNotify.do"*/;
    private String secret /*= "1d3i20wk2cma45zp3sp6z3vaxvfxk2r7"*/;

    public QJFPayServiceImpl(Map<String,String> map) {
        if(map.containsKey("app_id")){
            this.app_id = map.get("app_id");
        }
        if(map.containsKey("api_url")){
            this.api_url = map.get("api_url");
        }
        if(map.containsKey("notify_url")){
            this.notify_url = map.get("notify_url");
        }
        if(map.containsKey("secret")){
            this.secret = map.get("secret");
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

/*    public static void main(String[] arg)throws  Exception{
        QJFPayServiceImpl  qjfPayService = new QJFPayServiceImpl(new HashMap<String, String>());
        PayEntity payEntity = new PayEntity();
        payEntity.setOrderNo(UUID.randomUUID().toString().substring(0,20));
        payEntity.setAmount(255);
        payEntity.setRefererUrl("http://txw.tx8899.com/TYY/Notify/QJFNotify.do");
        payEntity.setPayCode("903");
        qjfPayService.smPay(payEntity);
    }*/

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[QJF]全聚付支付宝扫码渠道  wyPay(PayEntity payEntity = {}",payEntity);
        try {
            Map<String,String> data = sealRequest(payEntity);
            String sign = generatorSign(data,1);
            data.put("pay_md5sign", sign);
            logger.info("[QJF]全聚付支付支付宝渠道请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String formStr = HttpUtils.generatorForm(data, api_url);
            logger.info("[QJF]全聚付支付生成form表单请求结果:{}",formStr);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[QJF]全聚付支付渠道异常:{}",e.getMessage());
            return PayResponse.error("[QJF]全聚付支付渠道支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[QJF]全聚付支付回调验签开始============START==============");
        try {

            String sourceSign = data.get("sign");
            logger.info("[QJF]全聚付支付回调验签获取原签名串:{}",sourceSign);

            String sign = generatorSign(data,2);
            logger.info("[QJF]全聚付支付回调验签生成签名串:{}",sign);

            if(sourceSign.equalsIgnoreCase(sign)) return "success";

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[QJF]全聚付支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }


    /**
     * 组装参数
     * @param entity
     * @return
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[QJF]全聚付支付组装支付请求参数开始==============START==============");
        try {
            Map<String,String> dataMap = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额,单位元,保留两位小数
            dataMap.put("pay_memberid",app_id);//商户编号
            dataMap.put("pay_orderid",entity.getOrderNo());//商户订单号   20位
            dataMap.put("pay_applydate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//订单时间    时间格式：2014-01-02 18:18:18
            dataMap.put("pay_bankcode",entity.getPayCode());//支付通道  微信 支付宝  微信公众号
            dataMap.put("pay_notifyurl",notify_url);//回调地址
            dataMap.put("pay_callbackurl",entity.getRefererUrl());//页面返回地址
            dataMap.put("pay_amount",amount);//金额
            dataMap.put("pay_productname","Pay");//商品名
            return  dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[QJF]全聚付支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[QJF]全聚付支付组装支付请求参数异常");
        }
    }

    /**
     * 加密
     * @param map
     * @return
     */
    private String generatorSign(Map<String,String> map,int type ) throws Exception{
        logger.info("[QJF]全聚付支付生成签名开始============START=============");
        try {
            Map<String,String> paramsMap = new HashMap<>();
            if(type == 1){
                //支付请求参与签名参数
                paramsMap.put("pay_memberid",map.get("pay_memberid"));//商户编号
                paramsMap.put("pay_orderid",map.get("pay_orderid"));//商户订单号   20位
                paramsMap.put("pay_applydate",map.get("pay_applydate"));//订单时间    时间格式：2014-01-02 18:18:18
                paramsMap.put("pay_bankcode",map.get("pay_bankcode"));//支付通道  微信 支付宝  微信公众号
                paramsMap.put("pay_notifyurl",map.get("pay_notifyurl"));//回调地址
                paramsMap.put("pay_callbackurl",map.get("pay_callbackurl"));//页面返回地址
                paramsMap.put("pay_amount",map.get("pay_amount"));//金额
            }else{
                //回调参与签名参数
                paramsMap.put("memberid",map.get("memberid"));//商户编码
                paramsMap.put("orderid",map.get("orderid"));//商户订单号
                paramsMap.put("amount",map.get("amount"));//支付金额
                paramsMap.put("transaction_id",map.get("transaction_id"));//交易流水号
                paramsMap.put("datetime",map.get("datetime"));// 支付成功时间
                paramsMap.put("returncode",map.get("returncode"));//交易状态
                paramsMap.put("attach",map.get("attach"));//扩展返回
            }
            //加入秘钥
            StringBuffer stringBuffer = new StringBuffer();
            //排序
            Map<String,String> data = MapUtils.sortByKeys(paramsMap);
            Iterator<String> iterator = data.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = data.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                stringBuffer.append("&").append(key).append("=").append(val);
            }
            stringBuffer.append("&").append("key").append("=").append(secret);
            String signStr = stringBuffer.toString().replaceFirst("&", "");
            logger.info("[QJF]全聚付支付生成待签名串:{}",signStr);

            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toUpperCase();
            logger.info("[QJF]全聚付支付生成签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[QJF]全聚付支付生成签名异常:{}",e.getMessage());
            throw new Exception("[QJF]全聚付支付生成签名异常");
        }
    }
}
