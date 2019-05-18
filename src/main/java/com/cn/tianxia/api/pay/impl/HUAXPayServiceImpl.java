package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
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
 * @ClassName: HUAXPayServiceImpl
 * @Description: 华信支付
 * @Author: Zed
 * @Date: 2018-12-28 13:37
 * @Version:1.0.0
 **/

public class HUAXPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(HUAXPayServiceImpl.class);

    /** 支付地址 **/
    private String api_url = "http://120.76.62.68:7070/pay/order";

    /** 商户号 **/
    private String merchantNo = "eWGnGGXaQFEwnnZ";

    /** md5key **/
    private String md5Key = "3BC5AE90363E1CEAC1C14F841AF9D4A2";

    /** notifyUrl **/
    private String notifyUrl = "http://txw8899.com/TXY/Notify/HUAXNotify.do";

    public HUAXPayServiceImpl(Map<String, String> pmap) {
        if (pmap != null) {
            if (pmap.containsKey("api_url")) {
                this.api_url = pmap.get("api_url");
            }
            if (pmap.containsKey("merchantNo")) {
                this.merchantNo = pmap.get("merchantNo");
            }
            if (pmap.containsKey("md5Key")) {
                this.md5Key = pmap.get("md5Key");
            }
            if (pmap.containsKey("notifyUrl")) {
                this.notifyUrl = pmap.get("notifyUrl");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            Map<String, String> paramsMap = sealRequest(payEntity);
            String sign = generatorSign(paramsMap);
            paramsMap.put("sign", sign);
            logger.info("[HUAX]华信支付请求参数:" + JSONObject.fromObject(paramsMap).toString());
            String res = HttpUtils.toPostForm(paramsMap,api_url);

            if (StringUtils.isBlank(res)) {
                logger.error("[HUAX]华信支付扫码支付请求异常,返回结果为空!");
                return PayResponse.error("[HUAX]华信支付扫码支付请求异常,返回结果为空!");
            }
            logger.info("[HUAX]华信支付响应参数字符:" + res);
            JSONObject resposeJson = JSONObject.fromObject(res);

            if (resposeJson.containsKey("code") && "0".equals(resposeJson.getString("code"))) {

                JSONObject payData = JSONObject.fromObject(resposeJson.getJSONObject("data"));

                String qrCodeUrl = payData.getString("data");

                if (StringUtils.isNotBlank(payEntity.getMobile())) {
                    return PayResponse.sm_link(payEntity,qrCodeUrl,"下单成功");
                }
                return PayResponse.sm_qrcode(payEntity,qrCodeUrl,"下单成功");
            }
            return PayResponse.error("[HUAX]华信支付扫码支付下单失败:"+ resposeJson.getString("msg"));

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HUAX]华信支付扫码支付下单失败:{}",e.getMessage());
            return PayResponse.error("[HUAX]华信支付扫码支付下单失败:"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[HUAX]华信支付回调验签失败：回调签名为空！");
            return "fail";
        }
        String localSign;
        try {
            localSign = generatorSign(data);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HUAX]华信支付签名异常："+ e.getMessage());
            return "fail";
        }

        logger.info("本地签名:" + localSign + "      服务器签名:" + sourceSign);
        if (sourceSign.equalsIgnoreCase(localSign)) {
            return "success";
        }
        return "fail";
    }

    /**
     *
     * @param payEntity
     * @return
     */
    private Map<String,String> sealRequest(PayEntity payEntity) throws Exception {
        logger.info("[HUAX]华信支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0").format(payEntity.getAmount()*100);//交易金额 分为单位

            data.put("outOrderNo",payEntity.getOrderNo());//	String	Y	商户订单号，长度10-32
            data.put("amount",amount);	//Long	Y	交易金额，单位分，无小数点
            data.put("userId",payEntity.getuId());	//用户编号
            data.put("merchantNo",merchantNo);//	String	Y	商户用户唯一标识
            data.put("payMethod",payEntity.getPayCode());//	Int	Y	支付方法见附录1
            data.put("justPayUrl","1");//	Int	N 只需要支付URL地址 0 不需要 1 需要  如果指定此参数，则dataType将只返回3
            data.put("callbackUrl",notifyUrl);//	String	N	订单成功回调地址（默认使用商户配置的，将会覆盖商户配置的回调地址）

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HUAX]华信支付封装请求参数异常:",e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     *
     * @Description 生成支付签名串
     * @param data
     * @return
     * @throws Exception
     */
    public String generatorSign(Map<String,String> data) throws Exception{
        logger.info("[HUAX]华信支付生成支付签名串开始==================START========================");
        try {

            StringBuilder sb = new StringBuilder();
            SortedMap<String,String> sortedMap = new TreeMap<>(data);
            for (String key:sortedMap.keySet()) {
                if (StringUtils.isBlank(sortedMap.get(key)) || "sign".equalsIgnoreCase(key) || "pay_md5sign".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(sortedMap.get(key)).append("&");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append(md5Key);
            logger.info("[HUAX]华信支付待签名字符:" + sb.toString());
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString());
            if (StringUtils.isBlank(sign)) {
                logger.error("[HUAX]华信支付生成签名串为空！");
                return null;
            }
            logger.info("[HUAX]华信支付生成加密签名串:"+ sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HUAX]华信支付生成支付签名串异常:"+ e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }
}
