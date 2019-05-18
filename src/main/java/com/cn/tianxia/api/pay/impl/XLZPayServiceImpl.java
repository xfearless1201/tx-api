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

import java.util.HashMap;
import java.util.Map;

/**
 * AMH 嫌来赚支付
 */

public class XLZPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(XLZPayServiceImpl.class);
    /**
     * 商户号
     */
    private String payMemberid;
    /**
     * 支付地址
     */
    private String payUrl;
    /**
     * 密钥
     */
    private String md5Key;
    /**
     * 回调地址
     */
    private String payNotifyUrl;

    public XLZPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("payMemberid")) {
                this.payMemberid = data.get("payMemberid");
            }
            if (data.containsKey("md5Key")) {
                this.md5Key = data.get("md5Key");
            }
            if (data.containsKey("payNotifyUrl")) {
                this.payNotifyUrl = data.get("payNotifyUrl");
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
        logger.info("AMH嫌来赚支付支付扫码支付开始======================START==================");
        try {
            //封装请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[XLZ]嫌来赚支付请求参数:" + JSONObject.fromObject(data));
            //生成请求表单
            String resStr = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[XLZ]嫌来赚支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[XLZ]嫌来赚支付扫码支付发起HTTP请求无响应结果");
            }
            //Json解析响应结果
            JSONObject resJsonObj = JSONObject.fromObject(resStr);
            logger.info("[XLZ]嫌来赚支付响应信息:" + resJsonObj);
            if (resJsonObj.containsKey("status") && "0".equals(resJsonObj.getString("status"))) {
                //第二次解析
                JSONObject urlJson = resJsonObj.getJSONObject("urls");
                return PayResponse.sm_link(payEntity, urlJson.getString("orderUrl"), "下单成功");
            }
            return PayResponse.error("[XLZ]嫌来赚扫码支付失败" + resJsonObj);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("嫌来赚扫码支付异常:" + e.getMessage());
            return PayResponse.error("[XLZ]嫌来赚扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        try {
            String sourceSign = data.get("sign");
            logger.info("[XLZ]嫌来赚支付回调原签名串:" + sourceSign);
            String sign = generatorSign(data);
            logger.info("[XLZ]嫌来赚支付回调:本地签名:" + sign + "      服务器签名:" + sourceSign);
            if (sign.equalsIgnoreCase(sourceSign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XLZ]嫌来赚支付回调验签异常:" + e.getMessage());
        }
        return "fail";
    }

    /**
     * @param
     * @param
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    public Map<String, String> sealRequest(PayEntity payEntity) {
        Map<String, String> data = new HashMap<>();
        data.put("mchId", payMemberid);
        data.put("transactionId", payEntity.getOrderNo());
        data.put("amount", String.valueOf(payEntity.getAmount() * 100));//以分为单位，以元只能输入固定金额： 50,98,100,199,200,300,500,588,999,1000,1999,2000,3000,4999,5000
        data.put("channel", payEntity.getPayCode());
        data.put("memo", "Pay");
        data.put("callbackUrl", payNotifyUrl);
        data.put("ip", payEntity.getIp());
        return data;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        Map<String, String> sortmap = MapUtils.sortByKeys(data);
        StringBuffer sb = new StringBuffer();
        sb.append(md5Key);
        for (String key : sortmap.keySet()) {
            String val = sortmap.get(key);
            if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                continue;
            }
            sb.append("&").append(key).append("=").append(val);
        }
        //生成待签名串
        String signStr = sb.toString();
        logger.info("[XLZ]嫌来赚支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(signStr);
        logger.info("[XLZ]嫌来赚支付生成加密签名串:{}", sign);
        return sign;
    }
}
