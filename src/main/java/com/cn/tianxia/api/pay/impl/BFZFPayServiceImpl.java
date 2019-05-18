package com.cn.tianxia.api.pay.impl;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
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
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

/**
 * @ClassName: BFZFPayServiceImpl
 * @Description: 宝富支付
 * @Author: Zed
 * @Date: 2019-01-06 14:17
 * @Version:1.0.0
 **/

public class BFZFPayServiceImpl implements PayService {
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

    private static final Logger logger = LoggerFactory.getLogger(BFZFPayServiceImpl.class);

    public BFZFPayServiceImpl(Map<String, String> data) {

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

    /**
     * 网银支付
     *
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[BFZF]宝富支付网银支付======================START=======================");
        try {

            Map<String, String> params = sealRequest(payEntity, 0);

            String sign = generatorSign(params);

            params.put("pay_md5sign", sign);

            String form = HttpUtils.generatorForm(params, pay_url);
            logger.info("[BFZF]宝富支付网银支付生成表单:{}", form);
            return PayResponse.wy_form(payEntity.getPayUrl(), form);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PayResponse.error("宝富支付错误");
    }

    /**
     * 扫码支付
     *
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[BFZF]宝富支付扫码支付======================START=======================");
        try {

            Map<String, String> params = sealRequest(payEntity, 1);

            String sign = generatorSign(params);

            params.put("pay_md5sign", sign);

            String form = HttpUtils.generatorForm(params, pay_url);
            logger.info("[BFZF]宝富支付网银支付生成表单:{}", form);
            return PayResponse.sm_form(payEntity, form, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PayResponse.error("宝富支付错误");
    }

    /**
     * @param data
     * @return
     * @Description 验签
     */
    public String callback(Map<String, String> data) {
        logger.info("[BFZF]宝富支付回调参数:{}", data);
        String sign = "";
        try {
            data.remove("attach");
            sign = data.remove("sign");
            logger.info("宝富支付获取的sign:{}", sign);
            String localMd5 = generatorSign(data);
            if (localMd5.equalsIgnoreCase(sign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[BFZF]宝富支付回调验签异常:" + e.getMessage());
        }
        return "faild";
    }

    /**
     * 组装参数
     *
     * @param payEntity
     * @return
     */
    private Map<String, String> sealRequest(PayEntity payEntity, int type) {
        logger.info("[BFZF]宝富支付开始组装参数...");

        SimpleDateFormat simple = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        TreeMap<String, String> treeMap = new TreeMap<>();
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());
        treeMap.put("pay_memberid", pay_memberid);
        treeMap.put("pay_orderid", payEntity.getOrderNo());
        treeMap.put("pay_amount", amount);
        treeMap.put("pay_applydate", simple.format(new Date()));//当前时间
        if (type == 0) {
            treeMap.put("pay_bankcode", "907");//支付类型
        } else {
            treeMap.put("pay_bankcode", payEntity.getPayCode());//支付类型
        }
        treeMap.put("pay_notifyurl", notifyUrl);
        treeMap.put("pay_callbackurl", payEntity.getRefererUrl());
        logger.info("[BFZF]宝富支付组装参数值:{}", treeMap);
        return treeMap;
    }


    /**
     * @throws NoSuchAlgorithmException
     */
    private String generatorSign(Map<String, String> params) throws Exception {
        Map<String,String> sortmap = MapUtils.sortByKeys(params);
        StringBuffer sb = new StringBuffer();
        Iterator<String> iterator = sortmap.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            String val = sortmap.get(key);
            if(StringUtils.isBlank(val) || key.equalsIgnoreCase("sign")) continue;
            sb.append(key).append("=").append(val).append("&");
        }
        sb.append("key=").append(key);
        logger.info("宝富支付 加密前参数:{}", sb);
        String md5 = MD5Utils.md5toUpCase_32Bit(sb.toString());
        logger.info("宝富支付加密后:{}", md5);
        return md5;
    }

}

