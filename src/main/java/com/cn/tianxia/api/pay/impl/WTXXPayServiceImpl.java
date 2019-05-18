package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.tx.MD5Utils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 万通XX 支付
 *
 * @author TX
 */
public class WTXXPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(WTXXPayServiceImpl.class);
    /**
     * 商户编号
     **/
    private String uid;
    /**
     * 支付地址
     **/
    private String payUrl;
    /**
     * 回调地址
     **/
    private String notifyUrl;
    /**
     * 秘钥
     **/
    private String secret;

    public WTXXPayServiceImpl(Map<String, String> map) {
        if (map.containsKey("uid")) {
            this.uid = map.get("uid");
        }
        if (map.containsKey("payUrl")) {
            this.payUrl = map.get("payUrl");
        }
        if (map.containsKey("notifyUrl")) {
            this.notifyUrl = map.get("notifyUrl");
        }
        if (map.containsKey("secret")) {
            this.secret = map.get("secret");
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            Map<String, String> map = sealRequest(payEntity);
            String sign = generatorSign(map);
            map.put("key", sign);

            String response = HttpUtils.toPostJson(map, payUrl);

            if (StringUtils.isEmpty(response)) {
                return PayResponse.error("万通XX支付请求无响应");
            }

            JSONObject json = JSONObject.fromObject(response);
            logger.info("万通XX支付请求返回值:{}, 请求参数={}", json, JSONObject.fromObject(map));
            if (json.containsKey("code") && "1".equals(json.getString("code"))) {
                JSONObject result = json.getJSONObject("data").getJSONObject("result");
                logger.info("万通XX支付成功时候返回值:{} ", result);
                return PayResponse.sm_link(payEntity, result.getString("url"), "万通XX支付成功");
            }
            return PayResponse.error(response);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("万通XX支付出现错误");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("万通XX 支付开始回调.....");
        String sourceSign = data.get("key");
        logger.info("万通XX 支付回调获取签名值:{} ", sourceSign);

        try {
            String key = generatorSign(data);
            if (key.equalsIgnoreCase(sourceSign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "fail";
    }

    private String generatorSign(Map<String, String> map) throws Exception {
        logger.info("万通XX 支付 签名参数:{}", map);
        Map<String, String> sortMap = MapUtils.sortByKeys(map);
        StringBuilder strBuilder = new StringBuilder();

        for (Entry<String, String> entry : sortMap.entrySet()) {
            if ("key".equals(entry.getKey()) || StringUtils.isEmpty(entry.getValue())) {
                continue;
            }
            strBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        String param = strBuilder.substring(0, strBuilder.length() - 1);
        logger.info("万通XX支付加密之前的参数:{}", (param + secret));
        String key = MD5Utils.md5(param + secret);
        logger.info("万通XX支付加密之后的key:{}", key);
        return key;
    }

    private Map<String, String> sealRequest(PayEntity payEntity) {
        logger.info("万通XX支付");
        String price = new DecimalFormat("##").format(payEntity.getAmount() * 100);////订单金额,单位分
        Map<String, String> map = new HashMap<String, String>();
        map.put("uid", uid);
        map.put("orderid", payEntity.getOrderNo());
        map.put("price", price);
        map.put("paytype", payEntity.getPayCode());
        map.put("notifyurl", notifyUrl);
        logger.info("万通XX支付请求参数:{}", map);
        return map;
    }
}
