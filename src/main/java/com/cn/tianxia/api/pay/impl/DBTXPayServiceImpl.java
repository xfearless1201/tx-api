package com.cn.tianxia.api.pay.impl;


import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

/**
 * 大宝天下  支付
 *
 * @author TX
 */
public class DBTXPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(DBTXPayServiceImpl.class);
    /**
     * 商户id
     */
    private String pay_memberid;
    /**
     * 商户密钥
     */
    private String key;
    /**
     * 回调函数
     */
    private String notifyUrl;
    /**
     * 支付地址
     */
    private String pay_url;


    public DBTXPayServiceImpl(Map<String, String> data) {

        if (data.containsKey("pay_memberid")) {
            this.pay_memberid = data.get("pay_memberid");
        }
        if (data.containsKey("pay_url")) {
            this.pay_url = data.get("pay_url");
        }
        if (data.containsKey("notifyUrl")) {
            this.notifyUrl = data.get("notifyUrl");
        }
        if (data.containsKey("key")) {
            this.key = data.get("key");
        }
    }


    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[DBTX]大宝天下支付扫码支付开始======================START==================");
        try {
            //封装请求参数
            Map<String, String> data = sealRequest(payEntity);
            logger.info("[DBTX]大宝天下支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //生成form请求表单
            String formStr = HttpUtils.generatorForm(data, pay_url);
            logger.info("[DBTX]大宝天下支付扫码支付生成form请求表单结果:{}",formStr);
            return PayResponse.sm_form(payEntity, formStr, "扫码支付下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[DBTX]大宝天下支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[DBTX]大宝天下支付扫码支付异常:"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[DBTX]大宝天下回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data))
            return "success";
        return "fail";
    }

    private boolean verifyCallback(String sign,Map<String,String> data) {


        String localSign;
        try {
            localSign = generatorSign(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("[DBTX]大宝天下生成支付签名串异常:"+ e.getMessage());
            return false;
        }
        return sign.equalsIgnoreCase(localSign);
    }

    /**
     * 组装参数
     *
     * @param payEntity
     * @return
     */
    private Map<String, String> sealRequest(PayEntity payEntity) {
        logger.info("[DBTX]大宝天下支付组装请求参数开始------------");
        try {

            TreeMap<String, String> treeMap = new TreeMap<>();
            String amount = new DecimalFormat("0.00").format(payEntity.getAmount());
            treeMap.put("pay_memberid", pay_memberid);
            treeMap.put("pay_orderid", payEntity.getOrderNo());
            treeMap.put("pay_amount", amount);
            treeMap.put("pay_applydate", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));//当前时间
            treeMap.put("pay_bankcode", payEntity.getPayCode());//支付类型
            treeMap.put("pay_notifyurl", notifyUrl);
            treeMap.put("pay_callbackurl", payEntity.getRefererUrl());
            treeMap.put("pay_md5sign", generatorSign(treeMap));
            treeMap.put("pay_productname", "top_Up");
            logger.info("[DBTX]大宝天下支付组装参数值:{}", treeMap);
            return treeMap;
        } catch (Exception e) {
            logger.error("[DBTX]大宝天下支组装请求参数出错:" + e.getMessage());
            return null;
        }
    }


    /**
     * @throws NoSuchAlgorithmException
     */
    public String generatorSign(Map<String, String> params) throws NoSuchAlgorithmException {
        logger.info("[DBTX]大宝天下请求签名开始----------------------");
        StringBuffer sb = new StringBuffer();
        if (params == null || params.size() <= 0) {
            return "";
        }
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (StringUtils.isBlank(value) || "sign".equals(key) || "attach".equals(key)) {
                continue;
            }
            sb.append(key).append("=").append(value).append("&");
        }

        sb.append("key=").append(key);

        logger.info("[DBTX]大宝天下签名加密串:{}", sb);
        String md5 = MD5Utils.md5toUpCase_32Bit(sb.toString());
        logger.info("[DBTX]大宝天下生成签名:{}", md5);
        return md5;
    }
}
