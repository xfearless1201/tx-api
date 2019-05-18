package com.cn.tianxia.api.pay.impl;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * @ClassName: HDZFPayServiceImpl
 * @Description: 宏达支付
 * @Author: Zed
 * @Date: 2019-01-10 09:38
 * @Version:1.0.0
 **/

public class HDZFPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(HDZFPayServiceImpl.class);

    /**
     * 支付地址
     **/
    private String api_url = "http://22w.deaiju.com/pay/createorder";

    /**
     * 商户号
     **/
    private String pid = "10094";

    /**
     * md5key
     **/
    private String KEY = "1d8420d445e8441173009701a6e39f4a";


    public HDZFPayServiceImpl(Map<String, String> pmap) {
        if (pmap != null) {
            if (pmap.containsKey("api_url")) {
                this.api_url = pmap.get("api_url");
            }
            if (pmap.containsKey("pid")) {
                this.pid = pmap.get("pid");
            }
            if (pmap.containsKey("KEY")) {
                this.KEY = pmap.get("KEY");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        JSONObject r_json = Pay(payEntity);
        return r_json;
    }

    /**
     * @param payEntity
     * @return
     * @Description 二维码支付接口
     */
    public JSONObject Pay(PayEntity payEntity) {
        try {
            Map<String, String> paramsMap = sealRequest(payEntity);
            String sign = generatorSign(paramsMap);
            paramsMap.put("sign", sign);
            String postJson = JSONObject.fromObject(paramsMap).toString();
            logger.info("[HDZF]宏达支付请求参数:" + postJson);
            String res = HttpUtils.toPostForm(paramsMap, api_url);

            if (StringUtils.isBlank(res)) {
                logger.error("[HDZF]宏达支付扫码支付请求异常,返回结果为空!");
                return PayResponse.error("[HDZF]宏达支付扫码支付请求异常,返回结果为空!");
            }
            logger.info("[HDZF]宏达支付响应参数字符:" + res);
            JSONObject resposeJson = JSONObject.fromObject(res);

            if (resposeJson.containsKey("error") && "0".equals(resposeJson.getString("error"))) {
                JSONObject dataJson = resposeJson.getJSONObject("data");
                String qrCodeUrl = dataJson.getString("payurl");
                String qrCodeUrlEncode = regxChinese(qrCodeUrl);
                return PayResponse.sm_qrcode(payEntity, qrCodeUrlEncode, "下单成功");
            }
            return PayResponse.error("[HDZF]宏达支付扫码支付下单失败:" + resposeJson.getString("msg"));
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[HDZF]宏达支付扫码支付下单失败" + e.getMessage());
        }
    }

    /**
     * @param payEntity
     * @return
     */
    private Map<String, String> sealRequest(PayEntity payEntity) throws Exception {
        logger.info("[HDZF]宏达支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(payEntity.getAmount());
            data.put("pid", pid);
            data.put("money", amount);          //订单金额单位元
            data.put("channel", payEntity.getPayCode());
            data.put("out_order_id", payEntity.getOrderNo());
            data.put("extend", "top_up");
            data.put("ip", payEntity.getIp());
            if (StringUtils.isBlank(payEntity.getMobile())) {
                data.put("terminal", "pc");
            } else {
                data.put("terminal", "h5");
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HDZF]宏达支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        logger.info("[HYZF]宏达支付生成支付签名串开始==================START========================");
        try {
            //签名规则
            //md5(pid+money+out_order_id+extend+apikey)

            StringBuilder sb = new StringBuilder();
            sb.append(data.get("pid")).append(data.get("money")).append(data.get("out_order_id"))
                    .append(data.get("extend")).append(KEY);
            logger.info("[HDZFZF]宏达支付待签名字符:" + sb.toString());
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString());
            if (StringUtils.isBlank(sign)) {
                logger.error("[HYZF]宏达支付生成签名串为空！");
                return null;
            }
            logger.info("[HYZF]宏达支付生成加密签名串:" + sign.toLowerCase());
            return sign.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HYZF]宏达支付生成支付签名串异常:" + e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }

    @Override
    public String callback(Map<String, String> data) {

        String sourceSign = data.remove("sign");

        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[HYZF]宏达支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if (verifyCallback(sourceSign, data))
            return "success";
        return "fail";
    }

    private boolean verifyCallback(String sign, Map<String, String> data) {
        StringBuffer sb = new StringBuffer();
        //        异步返回数字签名规则：
//        md5(pid+type+no+money+ extend+out_order_id+apikey)
        sb.append(data.get("pid"));
        sb.append(data.get("type"));
        if (data.containsKey("no")) {
            sb.append(data.get("no"));
        }
        sb.append(data.get("money"));
        sb.append(data.get("extend"));
        sb.append(data.get("out_order_id"));
        sb.append(KEY);
        String localSign;
        try {
            localSign = MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("[HYZF]宏达支付生成支付签名串异常:" + e.getMessage());
            return false;
        }
        return sign.equalsIgnoreCase(localSign);
    }


    public static String regxChinese(String source) throws Exception {
        String copySource = source;
        // 要匹配的字符
        String reg_charset = "[\\u4e00-\\u9fa5]";

        Pattern p = Pattern.compile(reg_charset);
        Matcher m = p.matcher(source);
        while (m.find()) {
            copySource = copySource.replaceAll(m.group(0), URLEncoder.encode(m.group(0), "utf-8"));
        }
        return copySource;
    }
}
