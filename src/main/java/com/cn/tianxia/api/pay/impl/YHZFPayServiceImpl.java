package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.dc.MerchSdkSign;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.RandomUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName YHZFPayServiceImpl
 * @Description 银河支付
 * @author zw
 * @Date 2018年6月11日 下午7:19:21
 * @version 1.0.0
 */
public class YHZFPayServiceImpl implements PayService {
    //日志
    private final static Logger logger = LoggerFactory.getLogger(YHZFPayServiceImpl.class);

    private String api_url;

    private String customer_id;

    private String customer_key;

    private String notify_url;
    
    private String isOpen;

    public YHZFPayServiceImpl(Map<String, String> data) {
        if(MapUtils.isNotEmpty(data)){
            this.api_url = StringUtils.isBlank(data.get("api_url"))?null:data.get("api_url");
            this.customer_id = StringUtils.isBlank(data.get("customer_id"))?null:data.get("customer_id");
            this.customer_key = StringUtils.isBlank(data.get("customer_key"))?null:data.get("customer_key");
            this.notify_url = StringUtils.isBlank(data.get("notify_url"))?null:data.get("notify_url");
            this.isOpen = StringUtils.isBlank(data.get("isOpen"))?null:data.get("isOpen");
        }
    }

    /**
     * @Description 验签
     * @param request
     * @return
     */
    @Override
    public String callback(Map<String, String> request) {

        String serviSign = request.get("sign");

        String[] mapKey = new String[] {"customer_id", "order_id", "out_transaction_id", "pay_result", "pay_time",
                "total_fee" };
        Map<String, String> SignMap = new HashMap<>();

        for (String string : mapKey) {
            String value = request.get(string);
            SignMap.put(string, value);
        }

        String localSign = MerchSdkSign.getSign(SignMap, customer_key).toLowerCase();

        logger.info("本地签名:" + localSign + "      服务器签名:" + serviSign);

        if (serviSign.equals(localSign)) {
            logger.info("签名成功！");
            return "success";
        }
        logger.info("签名失败!");
        return "";
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[YHZF]银河支付扫码支付开始==============START=================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            logger.info("[YHZF]银河支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = HttpUtils.toPostJsonStr(JSONObject.fromObject(data),api_url);
            if(StringUtils.isBlank(response)){
                logger.info("[YHZF]银河支付发起HTTP请求无响应结果");
                return PayResponse.error("[YHZF]银河支付发起HTTP请求无响应结果");
            }
            logger.info("[YHZF]银河支付发起HTTP请求响应结果:{}",response);
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if (jsonObject.containsKey("code") && "0".equals(jsonObject.getString("code"))) {
                String payurl = jsonObject.getString("url");
                if(StringUtils.isBlank(payEntity.getMobile())){
                    //PC端
                    return PayResponse.sm_qrcode(payEntity, payurl, "下单成功");
                }
                return PayResponse.sm_link(payEntity, payurl, "下单成功");
            }      
            return PayResponse.error("下单失败:"+response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YHZF]银河支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[YHZF]银河支付扫码支付异常");
        }
    }

    /**
     * 
     * @Description 封装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    public Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[YFZF]云付支付封装支付请求参数开始================START===============");
        try {
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);//单位分
            data.put("customer_id", customer_id);
            data.put("nonce_str",RandomUtils.generateString(16));
            data.put("order_id", entity.getOrderNo());
            data.put("total_fee", amount);
            String sign = MerchSdkSign.getSign(data, customer_key).toLowerCase();
            data.put("sign", sign);
            data.put("notify_url", notify_url);
            data.put("callback_url", entity.getRefererUrl());
            data.put("client_ip", entity.getIp());
            data.put("pay_type", entity.getPayCode());
            data.put("user_id", entity.getuId());
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YFZF]云付支付封装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[YFZF]云付支付封装支付请求参数异常");
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        // TODO Auto-generated method stub
        return null;
    }
}
