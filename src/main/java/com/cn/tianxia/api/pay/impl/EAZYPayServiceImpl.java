package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

public class EAZYPayServiceImpl implements PayService{
    private static final Logger logger = LoggerFactory.getLogger(EAZYPayServiceImpl.class);
    /**商户号*/
    private String payMemberid;
    /**支付地址*/
    private String payUrl;
    /**秘钥*/
    private String md5Key;
    /**回调地址*/
    private String payNotifyUrl;

    public EAZYPayServiceImpl(Map<String, String> data)
    {
        if (data != null) {
            if (data.containsKey("payUrl")) {
                this.payUrl = ((String)data.get("payUrl"));
            }
            if (data.containsKey("payMemberid")) {
                this.payMemberid = ((String)data.get("payMemberid"));
            }
            if (data.containsKey("md5Key")) {
                this.md5Key = ((String)data.get("md5Key"));
            }
            if (data.containsKey("payNotifyUrl"))
                this.payNotifyUrl = ((String)data.get("payNotifyUrl"));
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity)
    {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity)
    {
        logger.info("EAZY扫码支付开始======================START==================");
        try
        {
            Map<String, String> data = sealRequest(payEntity);
            logger.info("EAZY扫码支付请求参数:" + JSONObject.fromObject(data));
            String resStr = HttpUtils.generatorForm(data, this.payUrl);
            logger.info("EAZY扫码支付响应信息:" + resStr);
            if (StringUtils.isBlank(resStr)) {
                logger.info("EAZY扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("EAZY扫码支付发起HTTP请求无响应结果");
            }
            return PayResponse.sm_form(payEntity, resStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("EAZY扫码支付生成异常:" + e.getMessage());
        }return PayUtil.returnWYPayJson("error", "form", "", "", "");
    }

    public String callback(Map<String, String> data)
    {
        try {
            String sourceSign = (String)data.remove("sign");
            logger.info("EAZY支付回调原签名串:" + sourceSign);
            Map<String, String> map = new HashMap<>();
            map.put("amount", data.get("amount"));
            map.put("out_trade_no", data.get("out_trade_no"));
            map.put("key_id", this.md5Key);
            String sign = generatorSign(map);
            logger.info("EAZY支付回调:本地签名:" + sign);
            if (sign.equalsIgnoreCase(sourceSign)) return "success"; 
        }
        catch (Exception e) { e.printStackTrace();
        logger.info("EAZY支付回调验签异常:" + e.getMessage());
        }
        return "fail";
    }
    /**
     * 
     * @Description 封装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    public Map<String, String> sealRequest(PayEntity payEntity)throws Exception{
        DecimalFormat df = new DecimalFormat("0.00");
        Map<String, String> data = new HashMap<>();

        data.put("amount", df.format(payEntity.getAmount()));
        data.put("out_trade_no", payEntity.getOrderNo());
        data.put("key_id", this.md5Key);
        data.put("sign", generatorSign(data));
        data.remove("key_id");

        data.put("content_type", "text");
        data.put("account_id", this.payMemberid);
        data.put("thoroughfare", payEntity.getPayCode());
        data.put("callback_url", this.payNotifyUrl);
        data.put("success_url", payEntity.getRefererUrl());
        data.put("error_url", payEntity.getRefererUrl());
        data.put("sign_type", "2");
        return data;
    }
    /**
     * 
     * @Description 生成签名串
     * @param data
     * @return
     * @throws Exception
     */
    public String generatorSign(Map<String, String> data)throws Exception{
        Map<String,String> sortmap = MapUtils.sortByKeys(data);
        StringBuffer sb = new StringBuffer();
        Iterator<String> iterator = sortmap.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            String val = sortmap.get(key);
            if(StringUtils.isBlank(val) || key.equalsIgnoreCase("sign")) continue;
                sb.append(key).append("=").append(val).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        String signStr = sb.toString();
        logger.info("EAZY支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5(signStr.getBytes());
        logger.info("EAZY支付生成加密签名串:{}", sign);
        return sign;
    }
}