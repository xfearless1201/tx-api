package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * @ClassName: YIFBPayServiceImpl
 * @Description: 易付宝支付
 * @Author: Zed
 * @Date: 2018-12-27 14:20
 * @Version:1.0.0
 **/

public class YIFBPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(YIFBPayServiceImpl.class);

    /** 支付地址 **/
    private String api_url = "http://yifubao.ok515.com/Pay_Index.html";

    /** 商户号 **/
    private String merchantId = "181292815";

    /** md5key **/
    private String md5Key = "fhlyp7puagjh5f6jd4569on35fhw1t0p";

    /** notifyUrl **/
    private String notifyUrl = "http://txw8899.com/QUC/Notify/YIFBNotify.do";

    public YIFBPayServiceImpl(Map<String, String> pmap) {
        if (pmap != null) {
            if (pmap.containsKey("api_url")) {
                this.api_url = pmap.get("api_url");
            }
            if (pmap.containsKey("merchantId")) {
                this.merchantId = pmap.get("merchantId");
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
            paramsMap.put("pay_md5sign", sign);

            logger.info("[YIFB]易付宝支付请求参数:" + JSONObject.fromObject(paramsMap).toString());
            String res = HttpUtils.toPostForm(paramsMap,api_url);

            if (StringUtils.isBlank(res)) {
                logger.error("[YIFB]易付宝支付扫码支付请求异常,返回结果为空!");
                return PayResponse.error("[YIFB]易付宝支付扫码支付请求异常,返回结果为空!");
            }
            logger.info("[YIFB]易付宝支付响应参数字符:" + res);
            JSONObject resposeJson = JSONObject.fromObject(res);

            if (resposeJson.containsKey("msg") && "0".equals(resposeJson.getString("msg"))) {
                String qrCodeUrl = resposeJson.getString("url");
                return PayResponse.sm_link(payEntity,qrCodeUrl,"下单成功");
            }
            return PayResponse.error("[YIFB]易付宝支付扫码支付下单失败:"+ resposeJson.getString("msg"));

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YIFB]易付宝支付扫码支付下单失败:{}",e.getMessage());
            return PayResponse.error("[YIFB]易付宝支付扫码支付下单失败:"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[YIFB]易付宝支付回调验签失败：回调签名为空！");
            return "fail";
        }
        String localSign;
        try {
            localSign = generatorSign(data);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YIFB]易付宝支付签名异常："+ e.getMessage());
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
        logger.info("[YIFB]易付宝支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(payEntity.getAmount());//交易金额 分为单位
            data.put("pay_bankcode",payEntity.getPayCode());
            data.put("pay_memberid",merchantId);
            data.put("pay_orderid",payEntity.getOrderNo());//20位订单号 时间戳").append(6位随机字符串组成
            data.put("pay_applydate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//yyyy-MM-dd HH:mm:ss
            data.put("pay_notifyurl",notifyUrl);//通知地址
            data.put("pay_callbackurl",notifyUrl);//回调地址
            data.put("pay_amount",amount);
            data.put("pay_productname","top_up");
            data.put("urltype","1");
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YIFB]易付宝支付封装请求参数异常:",e.getMessage());
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
        logger.info("[YIFB]易付宝支付生成支付签名串开始==================START========================");
        try {

            StringBuilder sb = new StringBuilder();
            SortedMap<String,String> sortedMap = new TreeMap<>(data);
            for (String key:sortedMap.keySet()) {
                if (StringUtils.isBlank(sortedMap.get(key)) || "sign".equalsIgnoreCase(key) || "pay_md5sign".equalsIgnoreCase(key)
                        || "pay_productname".equalsIgnoreCase(key) || "urltype".equalsIgnoreCase(key) || "attach".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(sortedMap.get(key)).append("&");
            }
//            sb.append("pay_amount=").append(data.get("pay_amount"))
//                    .append("&pay_applydate=").append(data.get("pay_applydate"))
//                    .append("&pay_bankcode=").append(data.get("pay_bankcode"))
//                    .append("&pay_callbackurl=").append(data.get("pay_callbackurl"))
//                    .append("&pay_memberid=").append(data.get("pay_memberid"))
//                    .append("&pay_notifyurl=").append(data.get("pay_notifyurl"))
//                    .append("&pay_orderid=").append(data.get("pay_orderid"));
                    sb.append("key=").append(md5Key);
            logger.info("[YIFB]易付宝支付待签名字符:" + sb.toString());
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString());
            if (StringUtils.isBlank(sign)) {
                logger.error("[YIFB]易付宝支付生成签名串为空！");
                return null;
            }
            logger.info("[YIFB]易付宝支付生成加密签名串:"+ sign.toLowerCase());
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YIFB]易付宝支付生成支付签名串异常:"+ e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }

    public static void main(String[] args) {
        PayEntity entity = new PayEntity();
        entity.setPayCode("934");
        entity.setAmount(100);
        entity.setOrderNo("yfbbl11234567");
        YIFBPayServiceImpl service = new YIFBPayServiceImpl(null);
        service.smPay(entity);
    }
}
