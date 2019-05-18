/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    JQPayServiceImpl.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:     roman
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年02月11日 11:44 
 *
 *    Revision: 
 *
 *    2019/2/11 11:44 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.RandomUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  * @ClassName JQPayServiceImpl
 *  * @Description TODO(这里用一句话描述这个类的作用)
 *  * @Author roman
 *  * @Date 2019年02月11日 11:44
 *  * @Version 1.0.0
 *  
 **/
public class JQPayServiceImpl implements PayService {


    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(JQPayServiceImpl.class);

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 支付请求地址
     */

    private String payUrl;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 秘钥
     */
    private String key;


    /**
     * 构造器，初始化参数
     */
    public JQPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("mch_id")) {
                this.mchId = data.get("mch_id");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notify_url")) {
                this.notifyUrl = data.get("notify_url");
            }
            if (data.containsKey("key")) {
                this.key = data.get("key");
            }
        }
    }


    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[JQPay]聚前支付扫码支付开始================START============");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);

            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[JQPay]聚前支付扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());

            //发起HTTP请求
            String response = HttpUtils.toPostForm(data, payUrl);

            if (StringUtils.isBlank(response)) {
                logger.info("[JQPay]聚前支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[JQPay]聚前支付扫码支付发起HTTP请求无响应结果");
            }
            logger.info("[JQPay]聚前支付扫码支付发起HTTP请求响应结果:{}", response);
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if (jsonObject.containsKey("result_code") && "SUCCESS".equals(jsonObject.getString("result_code"))) {
                //下单成功
                String payurl = jsonObject.getString("code_url");

                return PayResponse.sm_qrcode(payEntity, payurl, "扫码支付下单成功");
            }
            return PayResponse.error("下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JQPay]聚前支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[JQPay]聚前支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[JQPay]聚前支付回调验签开始==============START===========");
        try {
            //获取回调通知原签名串
            String sourceSign = data.get("sign");
            logger.info("[JQPay]聚前支付回调验签获取原签名串:{}", sourceSign);
            String sign = generatorSign(data);
            logger.info("[JQPay]聚前支付回调验签生成加密串:{}", sign);
            if (sourceSign.equalsIgnoreCase(sign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JQPay]聚前支付回调验签异常:{}", e.getMessage());
        }
        return "faild";
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[JQPay]聚前支付组装支付请求参数开始==============START==================");
        try {
            //创建参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            String orderNo = entity.getOrderNo();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date timeStart = new Date();
//            接口名称 method 是 String(32) alipay.qr  alipay.h5
            dataMap.put("method", "alipay.h5");
//            版本信息 version 是 String(8) 1.0
            dataMap.put("version", "1.0");
//            签名 sign 是 String(1024) 签名值

//            随机字符串 nonce_str 是 String(32)随机字符串，不大于 32 位。推荐随机数生成算法
            dataMap.put("nonce_str", RandomUtils.generateString(32));
//            商户号 mch_id 是 String(20) 平台分配的商户号
            dataMap.put("mch_id", mchId);
//            商户订单号 mch_order_no 是 String(32)商户系统内部订单号，要求 32 个字符内，只能是数字、大小写字母，且在同一个商户号下唯一
            dataMap.put("mch_order_no", orderNo);
//            商品名称 body 是 String(128) 商品简单描述
            dataMap.put("body", "top_up");
//            币种 cur_code 是 String(10)货币类型，符合 ISO4217 标准的三位字母代码。目前仅支持人民币，CNY
            dataMap.put("cur_code", "CNY");
//            总金额 total_amount 是Decimal(16,2)总金额(单位元，两位小数)
            dataMap.put("total_amount", amount);
//            终端 IP spbill_create_ip 是 String(20)终端 IP，请填写支付用户的真实IP
            dataMap.put("spbill_create_ip", entity.getIp());
//            订单提交时间 mch_req_time 是 String(14)订单生成时间，格式为yyyyMMddHHmmss，如 2009年 12 月 25 日 9 点 10 分 10 秒表示为 20091225091010 请使用UTC+8 北京时间
            dataMap.put("mch_req_time", sdf.format(timeStart));
//            通知地址 notify_url 是 String(128)后台通知地址，用于接收支付成功通知
            dataMap.put("notify_url", notifyUrl);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JQPay]聚前支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[JQPay]聚前支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        List<String> paramKeys = new ArrayList<String>(data.keySet());
        Collections.sort(paramKeys);
        StringBuffer sb = new StringBuffer();
        for (String paramKey : paramKeys) {
            Object paramValue = data.get(paramKey);
            if (paramValue == null || "sign".equals(paramKey)) {
                continue;
            }
            String value = paramValue.toString();
            if (value.trim().length() > 0) {
                sb.append(paramKey).append("=").append(paramValue).append("&");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        String signStr = sb.append(key).toString();
        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toUpperCase();

        return sign;
    }
}