package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: zed
 * @Date: 2019/1/22 14:06
 * @Description: 万达支付
 */
public class WDZFPayServiceImpl implements PayService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(WDZFPayServiceImpl.class);

    private String Key;//秘钥
    private String payUrl;//支付地址
    private String notifyUrl;//回调地址
    private String userId;//商户号

    //构造器,初始化参数
    public WDZFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("Key")) {
                this.Key = data.get("Key");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("userId")) {
                this.userId = data.get("userId");
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
        logger.info("[WDZF]万达支付扫码支付开始==============START==============");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);
            logger.info("[WDZF]万达支付扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());
            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[WDZF]万达支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[WDZF]万达支付扫码支付发起HTTP请求无响应结果");
            }

            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[WDZF]万达支付扫码支付响应参数报文:{}", jsonObject);
            if (jsonObject.containsKey("code") && "200".equals(jsonObject.getString("code"))) {
                //成功
                JSONObject result = JSONObject.fromObject(jsonObject.get("data"));
                String url = result.getString("payUrl");
                return PayResponse.sm_link(payEntity, url, "下单成功");
            }
            return PayResponse.error("下单失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WDZF]万达支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[WDZF]万达支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[WDZF]万达支付回调验签开始==================START==================");
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[WDZF]万达支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if (verifyCallback(sourceSign, data)) {
            return "success";
        }
        return "fail";
    }

    public boolean verifyCallback(String sign, Map<String, String> data) {

        //签名规则:CustomerId=1&OrderId=1548415346355&Money=10.00&Status=1&Message=1&Type=1&Key=4526f2ef2f9f16dd

        StringBuffer sb = new StringBuffer();
        sb.append("CustomerId=").append(data.get("customerId"));
        sb.append("&OrderId=").append(data.get("orderId"));
        sb.append("&Money=").append(data.get("money"));
        sb.append("&Status=").append(data.get("status"));
        sb.append("&Message=").append(data.get("message"));
        sb.append("&Type=").append(data.get("type"));
        sb.append("&Key=").append(Key);
        String localSign;
        try {
            localSign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
            logger.info("[WDZF]万达支付回调生成签名串：{}", sign);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("[WDZF]万达支付生成支付签名串异常:" + e.getMessage());
            return false;
        }
        return sign.equalsIgnoreCase(localSign);
    }


    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[WDZF]万达支付组装支付请求参数开始==================START==================");
        try {
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("CustomerId", entity.getOrderNo());// 商户订单号，确保应用内唯一
            data.put("Mode", "8");//8:支付宝支付
            data.put("BankCode", entity.getPayCode());//  银行编码 例如,支付宝:ALIPAY  详见 **附录(银行编码)**
            data.put("Money", amount);// 订单金额，单位为元，保留两位小数
            data.put("UserId", userId);// 支付平台分配的商户号merno
            data.put("Message", "top_Up");// 订单备注，在回调通知中将原样返回
            data.put("CallBackUrl", notifyUrl);//  异步通知地址，用于接收支付结果回调。相关规则详见2.2 异步回调通知机制
            data.put("ReturnUrl", entity.getRefererUrl());//  支付返回地址，支付成功时同步跳转到此地址
            data.put("Sign", generatorSign(data));// 数据签名,详见3.1签名算法
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WDZF]万达支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[WDZF]万达支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    private String generatorSign(Map<String, String> data) throws Exception {
        logger.info("[WDZF]万达支付生成签名开始===============START======================");
        try {
            //签名规则:
            //直接把请求数据中的所有元素(除sign本身)按照“key值=value值”的格式拼接起来，
            //并且把这些拼接以后的元素以“&”字符再连接起来（把每一项按常规顺序排列[Standard ASCII，不改变类型]），值为空的去除），
            //url之类用urldecode解码。 然后用商户设定的secretkey，执行hmacSha256计算，以Base64转码的结果(大写)为签名串sign。
            StringBuffer sb = new StringBuffer();
            sb.append("BankCode=").append(data.get("BankCode"))
                    .append("&CallBackUrl=").append(data.get("CallBackUrl"))
                    .append("&CustomerId=").append(data.get("CustomerId"))
                    .append("&Message=").append(data.get("Message"))
                    .append("&Mode=").append(data.get("Mode"))
                    .append("&Money=").append(data.get("Money"))
                    .append("&ReturnUrl=").append(data.get("ReturnUrl"))
                    .append("&UserId=").append(data.get("UserId"))
                    .append("&Key=").append(Key);
            String signStr = sb.toString();
            logger.info("[WDZF]万达支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[WDZF]万达支付生成加密签名串：{}", sign.toLowerCase());
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WDZF]万达支付生成签名异常:{}", e.getMessage());
            throw new Exception("[WDZF]万达支付生成签名异常");
        }
    }
}
