package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class XHZFPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(XHZFPayServiceImpl.class);
    /**
     * 支付地址
     */
    private String payUrl;
    /**
     * 商户编号
     */
    private String payMemberid;
    /**
     * 商户接收支付成功数据的地址
     */
    private String payNotifyUrl;
    /**
     * 商户密钥
     */
    private String key;

    public XHZFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("payMemberid")) {
                this.payMemberid = data.get("payMemberid");
            }
            if (data.containsKey("payNotifyUrl")) {
                this.payNotifyUrl = data.get("payNotifyUrl");
            }
            if (data.containsKey("key")) {
                this.key = data.get("key");
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
        logger.info("[XHZF]新汇一代扫码支付开始======================START==================");
        try {
            //封装请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[XHZF]新汇一代扫码支付请求参数:" + JSONObject.fromObject(data));
            //生成请求表单
            String resStr = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[XHZF]新汇一代扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[XHZF]新汇一代扫码支付发起HTTP请求无响应结果");
            }
            JSONObject resObj = JSONObject.fromObject(resStr);
            logger.info("[XHZF]新汇一代扫码支付响应信息:" + resObj);
            if (resObj.containsKey("status") && resObj.getBoolean("status")) {
                if (StringUtils.isNotBlank(payEntity.getMobile())) {
                    return PayResponse.sm_link(payEntity, resObj.getString("url"), resObj.getString("msg"));

                }
                return PayResponse.sm_qrcode(payEntity, resObj.getString("url"), resObj.getString("msg"));
            }
            return PayResponse.error("[XHZF]新汇一代扫码支付下单失败:" + resObj.getString("msg"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XHZF]新汇一代扫码支付生成异常:" + e.getMessage());
            return PayResponse.error("[XHZF]新汇一代扫码支付生成异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        try {
            String sourceSign = data.get("sign");
            String sign = generatorSign(data);
            logger.info("[XHZF]新汇一代扫码支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
            if (sign.equalsIgnoreCase(sourceSign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XHZF]新汇一代扫码支付回调生成签名串异常" + e.getMessage());
        }
        return null;
    }

    /**
     * @param
     * @param
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    public Map<String, String> sealRequest(PayEntity payEntity) {
        DecimalFormat df = new DecimalFormat("0.00");
        Map<String, String> data = new HashMap<>();
        data.put("mchid", payMemberid);//商户号
        data.put("order_id", payEntity.getOrderNo());//订单号
        data.put("channel_id", payEntity.getPayCode());//通道编码
        data.put("total_amount", df.format(payEntity.getAmount()));//金额 单位：元
        data.put("return_url", payNotifyUrl);
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
        for (String key : sortmap.keySet()) {
            String val = sortmap.get(key);
            if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                continue;
            }
            sb.append(key).append(val);
        }
        sb.append(key);
        //生成待签名串
        String signStr = sb.toString();
        logger.info("[XHZF]新汇一代扫码支付生成待签名串:{}", signStr);
        String sign = DigestUtils.sha1Hex(signStr).toUpperCase();
        logger.info("[XHZF]新汇一代扫码支付生成加密签名串:{}", sign);
        return sign;
    }

}
