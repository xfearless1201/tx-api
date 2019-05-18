package com.cn.tianxia.api.pay.impl;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.JEERSAUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/17 10:08
 * @Description: 捷付支付实现类
 */
public class JEEPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(JEEPayServiceImpl.class);

    private String merchantCode = "JA3290216A2";
    private String payUrl = "https://api.yoopayment.com/rsa/deposit";
    private String merchantPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCn4T9R7Fc2FOa7UG85tcXswjCSjqxWqIdwkmvcR7UqNCrW9nn2VNNNloYCq8QFEJ8FK/5tvmmBUXLnmzz02kCilszZXe7IUG33nzK5aYbW0WJke7LEIZUYGP7pOkVCD+3I7pLaH4j+o3rCUCGkMgAXVGlkKbq4Q3ILrV3Wyupmgfy/MmHToqxn65nNoMQeIcVu1RwaBFDMpQGGgaH0DUzmbHtXcBK0dSLV+Qn7VbqcWGTTB2o4xw9lsEHvIy7Uqh9lP4NpQAtJsH7haX2/8Za2meStvHTLoK00q5htGGtx5vfG7vMHgQUleHfN+N0uA1SmNd333b55nJeZ2zeq2YOTAgMBAAECggEAVevcwosZn55W8Oub4Yd9A03oGjpXTgr3NtBZz+YLfMwyWM7RYRfNBdrSP6+1pXn6SWVY1MYtrXgIPS2gpxjFF/HiaiW/Plqbza53AZpW3r9Pgmok9mjRrAGvBaNDKyqH4tzn4CdfsCPvgmAMt8K3dTlIr3EEFaa/SeazLmmwSkHK/8GWJ5aDdsT586fmgDfdBSJal2Ae4/N7UBXy4j66yvGCtlcyMGi/Q0LH+vB0fb1Zb7h6WXDVwFcyUq5xUHjcxt+dR4uulnsVgX7yU9EDwgUSu4h0GzhJBjYZfEmfCMoTg8SfEOO5dS6ZqW+KLK7TAKuPeIJTHLyi3L/7XNNrMQKBgQDtIad9ce5gqyZE5ApiS0LErpX+XMDVM+CLT8xNzgZmHHs1V/uV/0Yolx4XhBVAzDpRak/UlCw2qr0B2jmeHCIRUqp39ekLMp7ho9REmbrV5SAe/qCObqAqjHA/R3ZL1cxoBjtCnD3Sh+66ZNPJM8qLQEg5qwdEm2jYH3DhzkShrwKBgQC1PPHWEeWL0q/s6TT6f27QqQyjhNsl9nFGsuPko+pwz/whCzWX9TIIbRdHK5z7kxOA+/04bHQFICVvS6j7QnmTIoACWBXhlyunzJOnAGyUMUDzw0OmtopM9+NtZmRSqoz7Quv+SWJ4z0Tn89QSli+xBlKhWoQsTR2SYF4nTIZpXQKBgHvBk6MBgdILoHZVuSGhe6AgKYHNsInUgDzUkaCNhINoG9k0KMYYqunmLDtDoL/nlgwFetJL3mNNakT8OhPNRO5bgUIIIe7JJWhHUzHWNU8KPGBQrUSIcaijELXFuvRLCVnE3sqdthoY/TtkAa/BCtTv4IHQQ4PHw8j/Z69BMkpbAoGALUrPvQm37dt3L6OQI9HY5bh5ehvnkHMoF9z46tiz7AFuJuvgNtuF52kr2hNiJdS5mRBgVct0qxs/f7LOxgv0yfpjNzLANSJMPKD5fVCz0c0FSGXR8EXKBjYk1eAyzh3lIZ210FSSkVJrC/R5WTPDM2A7Iiu2wEN9UmlW3kdXh9kCgYEAjM3C51tWqYkkYjVfr46TMdQNZuQSsuTgM8wfKzKDtwFFzabjiVbYgMqKWdN7jyU2ckw+YaPagqV0NzJbLasAgOpAVQ7hmXY0WPqGOkFwfIvTmPct7Rgb+6867uuktCn027JJtUeD20kZziKSf0I9frJg1CNBUFQpK4rTTevLlTw=";
   // private String paymentPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArIbjWokWp/AkpKfhgDoOgQGEXNZvw44ieKDwDnZOCWO/tAqD0qiVUh5cy9PKMZqqwbpb9YNjJSwPSqWs8CzUWJmFknNqz0cKu14NxGKA/ED3U5ujf/0zFJTfLCIAGxpwfV0gZgkKJpxXzWUbnnrrU1r6SAIMjg1Fvww8oo0cl1HUabKIEi7JDNPOdBjXWXSWwyUFtkoWOqWfaVApvmiHn/1wHeV/jnVQcBYPBnvw5w84tJ80+Wg+4xOzCKsOTIMMIRJULLrgt3TZd6uJNNteV1tPxpVai6rOWqrYfq40RQ3Y+SjxaDKNKWsrhHwMAQ1vE+CK+tz9FKWi30xnzDym/wIDAQAB";
    private String paymentPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnrRfHSKD/6Z5XZds+NNegp8tKFIQw2k9MLd/dYVtLBb89cMtzXGWCVIeD8AGjfWFqU7Z33Bhn3sK/S9j3sMVs6vffj8wGtiFgcmTdmaXZzyK3fCBJbWSYTp4HpBmEdPN/TBvLEFOY0kwIISvS5NbJbZg/3cG1OJTWNqZzDYKD2z8ZAolLjYx8ATVCH51QpoAqjJQs/h1tj9Xyv6H6T/kJkSrlsfPwqBz0MR/Cak+ElhjLIS5aq9sKgQ7iH9n22QaCJmDJD+7Qx7RMOo9V4YhQBbh3nLOdjKeYZyLsoPva4jNR4DCE8cmBMbV2Hbyu+3+gPYQs664sORSdhOvW7oB0QIDAQAB";
    private String notifyUrl = "http://txw.tx8899.com/XJC/Notify/JEENotify.do";

    public JEEPayServiceImpl(Map<String,String> map) {
        if(MapUtils.isNotEmpty(map)){
            if(map.containsKey("merchantCode")){
                this.merchantCode = map.get("merchantCode");
            }
            if(map.containsKey("notifyUrl")){
                this.notifyUrl = map.get("notifyUrl");
            }
            if(map.containsKey("merchantPrivateKey")){
                this.merchantPrivateKey = map.get("merchantPrivateKey");
            }
            if(map.containsKey("paymentPublicKey")){
                this.paymentPublicKey = map.get("paymentPublicKey");
            }
            if(map.containsKey("payUrl")){
                this.payUrl = map.get("payUrl");
            }
        }
    }


    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        try {
            String data = sealRequest(payEntity,1);

            String sign = JEERSAUtils.sign(data,merchantPrivateKey);

            String sb = "merchant_code=" + URLEncoder.encode(merchantCode, "UTF-8") + "&" +
                    "data=" + URLEncoder.encode(data, "UTF-8") + "&" +
                    "sign=" + URLEncoder.encode(sign, "UTF-8");
            logger.info("[JEE]捷付支付网银请求参数:{}", sb);

            String response = HttpUtils.toPostForm(sb,payUrl);

            if (StringUtils.isBlank(response)) {
                logger.error("[JEE]捷付支付下单失败：返回结果为空");
                PayResponse.error("[JEE]捷付支付下单失败：返回结果为空");
            }

            JSONObject result = JSONObject.fromObject(response);

            if (result.containsKey("status") && "1".equals(result.getString("status"))) {

                String encodeData = result.getString("data");

                String decodeData = JEERSAUtils.decryptByPrivateKey(encodeData,merchantPrivateKey);

                JSONObject jsonData = JSONObject.fromObject(decodeData);

                String qrCodeUrl = null;

                if (jsonData.containsKey("qr_image_url") && null != jsonData.get("qr_image_url") && !"null".equals(jsonData.getString("qr_image_url"))) {
                    qrCodeUrl = jsonData.getString("qr_image_url");
                } else if (jsonData.containsKey("transaction_url") && null != jsonData.get("transaction_url") && !"null".equals(jsonData.getString("transaction_url"))) {
                    qrCodeUrl = jsonData.getString("transaction_url");
                } else {
                    return PayResponse.error("[JEE]捷付支付网银支付下单失败:获取二维码链接为空");
                }

                return PayResponse.wy_link(qrCodeUrl);

            }

            return PayResponse.error("[JEE]捷付支付网银支付下单失败:error_code=" + result.getString("error_code"));

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[JEE]捷付支付网银支付下单失败"+e.getMessage());
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            String data = sealRequest(payEntity,2);

            String sign = JEERSAUtils.sign(data,merchantPrivateKey);

            String sb = "merchant_code=" + URLEncoder.encode(merchantCode, "UTF-8") + "&" +
                    "data=" + URLEncoder.encode(data, "UTF-8") + "&" +
                    "sign=" + URLEncoder.encode(sign, "UTF-8");
            logger.info("[JEE]捷付支付扫码请求参数:{}", sb);

            String response = HttpUtils.toPostForm(sb,payUrl);

            if (StringUtils.isBlank(response)) {
                logger.error("[JEE]捷付网银支付下单失败：返回结果为空");
                PayResponse.error("[JEE]捷付网银支付下单失败：返回结果为空");
            }

            JSONObject result = JSONObject.fromObject(response);

            if (result.containsKey("status") && "1".equals(result.getString("status"))) {

                String encodeData = result.getString("data");

                String decodeData = JEERSAUtils.decryptByPrivateKey(encodeData,merchantPrivateKey);

                JSONObject jsonData = JSONObject.fromObject(decodeData);

                logger.info("[JEE]捷付支付扫码支付第三方返回信息：" + jsonData.toString());

                String qrCodeUrl = null;

                if (jsonData.containsKey("qr_image_url") && null != jsonData.get("qr_image_url") && !"null".equals(jsonData.getString("qr_image_url"))) {
                    qrCodeUrl = jsonData.getString("qr_image_url");
                } else if (jsonData.containsKey("transaction_url") && null != jsonData.get("transaction_url") && !"null".equals(jsonData.getString("transaction_url"))) {
                    qrCodeUrl = jsonData.getString("transaction_url");
                } else {
                    return PayResponse.error("[JEE]捷付支付扫码支付下单失败:获取二维码链接为空");
                }

                if ("7".equals(payEntity.getPayType())) {  //快捷支付直接跳转链接
                    return PayResponse.sm_link(payEntity,qrCodeUrl,"下单成功");
                }

                if (StringUtils.isNotBlank(payEntity.getMobile())) {
                    return PayResponse.sm_link(payEntity,qrCodeUrl,"下单成功");
                }

                return PayResponse.sm_qrcode(payEntity,qrCodeUrl,"下单成功");

            }

            return PayResponse.error("[JEE]捷付支付扫码支付下单失败:error_code=" + result.getString("error_code"));

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[JEE]捷付支付扫码支付下单失败"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[JEE]捷付支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data))
            return "success";
        return "fail";
    }

    private boolean verifyCallback(String sign,Map<String,String> data) {

//   stringSignTemp="orderid=orderid&opstate=opstate&ovalue=ovalue"+key
//sign=MD5(stringSignTemp).toLowerCase()

        StringBuffer sb = new StringBuffer();
        sb.append("orderid=").append(data.get("orderid"));
        sb.append("&opstate=").append(data.get("opstate"));
        sb.append("&ovalue=").append(data.get("ovalue"));
        String localSign;
        try {
            localSign = MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("[JEE]捷付支付生成支付签名串异常:"+ e.getMessage());
            return false;
        }
        return sign.equalsIgnoreCase(localSign);
    }

    /**
     *
     * @Description 封装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private String sealRequest(PayEntity entity,int type) throws Exception{
        logger.info("[JEE]捷付支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());

            if (type == 1) {
                data.put("bank_code",entity.getPayCode());   //网银支付
                data.put("service_type","1");
            } else {
                data.put("bank_code","");              //扫码支付
                data.put("service_type",entity.getPayCode());
            }
            data.put("amount",amount);
            data.put("merchant_user",entity.getuId());
            data.put("risk_level","1");
            data.put("merchant_order_no",entity.getOrderNo());
            data.put("platform","PC");
            data.put("callback_url",notifyUrl);

            String dataJson = JSONObject.fromObject(data).toString();

            logger.info("[JEE]捷付支付加密前请求参数:{}",dataJson);

            return JEERSAUtils.encryptByPublicKey(dataJson,paymentPublicKey);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JEE]捷付支付封装请求参数异常:"+e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    public static Map<String,String> getNotifyParams(String data,String merchantPrivateKey) {
        try {
            String decrypted = JEERSAUtils.decryptByPrivateKey(data,merchantPrivateKey);
            JSONObject notifyParams = JSONObject.fromObject(decrypted);
            return notifyParams;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JEE]获取回调请求参数异常:" + e.getMessage());
            return null;
        }
    }

    public static boolean verifyRequest(String data,String sign,String paymentPublicKey) {
        return JEERSAUtils.validateByPublicKey(data,sign,paymentPublicKey);
    }
}
