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
 * @ClassName YDZFPayServiceImpl
 * @Description 宝付惠支付
 * @author Jacky
 * @Date 2019年2月17日 下午11:00:58
 * @version 1.0.0
 */
public class BFHPayServiceImpl implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(BFHPayServiceImpl.class);

    private String API_URL ;

    private String API_KEY ;

    private String MERCHANT_NO ;

    private String NOTIFY_URL;

    public BFHPayServiceImpl(Map<String,String> map) {
        if(map.containsKey("API_URL")){
            this.API_URL = map.get("API_URL");
        }
        if(map.containsKey("API_KEY")){
            this.API_KEY = map.get("API_KEY");
        }
        if(map.containsKey("MERCHANT_NO")){
            this.MERCHANT_NO = map.get("MERCHANT_NO");
        }
        if(map.containsKey("NOTIFY_URL")){
            this.NOTIFY_URL = map.get("NOTIFY_URL");
        }
    }


    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("宝付惠支付 smPay(PayEntity payEntity ={}   -start" + payEntity);
        try {
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("pay_md5sign", sign);
            logger.info("BFH 宝付惠支付扫码支付请求参数报文： {}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = HttpUtils.toPostForm(data, API_URL);

            if(StringUtils.isBlank(response)){
                logger.info("BFH 宝付惠支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("BFH 宝付惠支付支付发起HTTP请求无响应结果");
            }
            logger.info("BFH 宝付惠支付扫码支付发起HTTP请求响应结果:{}",response);

            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.containsKey("status") && "success".equals(jsonObject.getString("status"))){
                //下单成功
                String pageUrl = jsonObject.getString("qrcode");//冠宝支付URL
                if(StringUtils.isBlank(payEntity.getMobile())){
                    //PC端
                    return PayResponse.sm_qrcode(payEntity, pageUrl, "BFH 宝付惠支付扫码支付下单成功");
                }
                return PayResponse.sm_link(payEntity, pageUrl, "BFH 宝付惠支付H5支付下单成功");
            }
            logger.error("BFH 宝付惠支付下单失败！  失败返回:{}",jsonObject);
            return PayResponse.error("BFH 宝付惠支付下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(),e);
            logger.info("BFH 宝付惠支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("BFH 宝付惠支付扫码支付异常");
        }
    }


    @Override
    public String callback(Map<String, String> data) {
        logger.info("BFH 宝付惠回调验签方法 callback(Map<String, String> data = {}  -start ", data);
        try {
            String sourceSign = data.get("sign");
            logger.info("BFH 宝付惠回调验签获取签名原串：{}",sourceSign);
            data.remove("sign");
            data.remove("attach");
            String sign = generatorSign(data);
            logger.info("BFH 宝付惠回调验签生成签名串:{}",sign);

            if(sourceSign.equalsIgnoreCase(sign)) return "success";

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("BFH 宝付惠回调验签异常:{}",e.getMessage());
            logger.error(e.getMessage(),e);
        }
        return "faild";
    }

    /**
     * 组装参数
     * @param entity
     * @return
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("BFH 宝付惠支付回参数组装 sealRequest(PayEntity entity = {} -start "+ entity);
        try {

            Map<String,String>  dataMap = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额,单位元,保留两位小数
            dataMap.put("pay_memberid",MERCHANT_NO);
            dataMap.put("pay_orderid",entity.getOrderNo());//订单编号
            dataMap.put("pay_applydate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            dataMap.put("pay_bankcode",entity.getPayCode());//支付编码
            dataMap.put("pay_notifyurl",NOTIFY_URL);//回调地址
            dataMap.put("pay_callbackurl",entity.getRefererUrl());// 跳转地址
            dataMap.put("pay_amount",amount);//金额
            dataMap.put("pay_productname","BFHZF");
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("BFH 宝付惠支付回组装参数失败!",e);
            throw new Exception("BFH 宝付惠支付组装支付请求参数异常",e);
        }
    }


    /**
     * 加密
     * @param map
     * @return
     */
    private String generatorSign(Map<String,String> map) throws Exception{
        logger.info("BFH 宝付惠支付加密方法 generatorSign(Map<String,String> map = {}  -start " + map);
        try {
            //加入秘钥
            StringBuffer stringBuffer = new StringBuffer();
            Map<String,String> data = MapUtils.sortByKeys(map);
            Iterator<String> iterator = data.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = data.get(key);
                if(StringUtils.isBlank(val) || "pay_productname".equalsIgnoreCase(key)) continue;
                stringBuffer.append("&").append(key).append("=").append(val);
            }
            stringBuffer.append("&").append("key").append("=").append(API_KEY);

            String signStr = stringBuffer.toString().replaceFirst("&", "");
            logger.info("BFH 宝付惠支付生成待签名串:{}",signStr);
            //生成MD5并转换大写
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toUpperCase();
            logger.info("BFH 宝付惠支付生成待签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("BFH 宝付惠支付生成待签名串:{}",e.getMessage());
            throw new Exception("BFH 宝付惠支付生成签名异常",e);
        }
    }
}
