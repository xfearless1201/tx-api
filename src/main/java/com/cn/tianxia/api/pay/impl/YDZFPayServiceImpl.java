package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @ClassName YDZFPayServiceImpl
 * @Description 易达支付
 * @author Jacky
 * @Date 2019年2月17日 下午11:00:58
 * @version 1.0.0
 */
public class YDZFPayServiceImpl implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(YDZFPayServiceImpl.class);

    private String API_URL ;

    private String API_KEY ;

    private String MERCHANT_NO ;

    private String NOTIFY_URL ;


    public YDZFPayServiceImpl(Map<String,String> map) {
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
        logger.info("[YDZF]易达支付扫码支付开始============START======================");
        try {
            Map<String,String> data = sealRequest(payEntity);
            String sign = generatorSign(data);
            data.put("sign", sign);
            data.put("sign_type","MD5");
            logger.info("易达支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            String formStr = HttpUtils.generatorForm(data, API_URL);
            logger.info("易达支付生成form表单请求结果:{}",formStr);
            logger.info("[YDZF]易达支付PC扫码请求成功！");
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("易达支付扫码参数组装异常:{}",e.getMessage());
            return PayResponse.error("易达支付扫码参数组装异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("YDZF 易达回调验签方法 callback(Map<String, String> data = {}  -start ", data);
        try {
            String sourceSign = data.get("sign");
            logger.info("YDZF 易达支付回调验签获取签名原串：{}",sourceSign);
            data.remove("sign");
            data.remove("sign_type");
            String sign = generatorSign(data);
            logger.info("YDZF 易达支付回调验签生成签名串:{}",sign);

            if(sourceSign.equalsIgnoreCase(sign)) return "success";

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("YDZF 易达支付回调验签异常:{}",e.getMessage());
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
        logger.info("YDZF 易达支付回参数组装 sealRequest(PayEntity entity = {} -start "+ entity);
        try {
            Map<String,String>  dataMap = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额,单位元,保留两位小数
            dataMap.put("pid",MERCHANT_NO);
            dataMap.put("type",entity.getPayCode());//支付类型 alipay2 ,wechat2
            dataMap.put("out_trade_no",entity.getOrderNo());//商户订单号
            dataMap.put("notify_url",NOTIFY_URL);
            dataMap.put("return_url",entity.getRefererUrl());
            dataMap.put("name","YDZF");
            dataMap.put("attach","TXKJ");
            dataMap.put("money",amount);//金额
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("YDZF 易达支付回组装参数失败!",e);
            throw new Exception("YDZF 易达支付回组装支付请求参数异常",e);
        }
    }


    /**
     * 加密
     * @param map
     * @return
     */
    private String generatorSign(Map<String,String> map) throws Exception{
        logger.info("YDZF 易达支付加密方法 generatorSign(Map<String,String> map = {}  -start " + map);
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
            stringBuffer.append(API_KEY);

            String signStr = stringBuffer.toString().replaceFirst("&", "");
            logger.info("YDZF 易达支付生成待签名串:{}",signStr);
            //生成MD5并转换大写
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("YDZF 易达支付生成待签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("YDZF 易达支付生成待签名串:{}",e.getMessage());
            throw new Exception("YDZF 易达支付生成签名异常",e);
        }
    }
}
