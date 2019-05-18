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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class YHBPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(YHBPayServiceImpl.class);
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
    private String md5Key;

    public YHBPayServiceImpl(Map<String, String> data, String type) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey(type)) {
                JSONObject jsonObject = JSONObject.fromObject(data.get(type));
                if (jsonObject.containsKey("payUrl")) {
                    this.payUrl = jsonObject.getString("payUrl");
                }
                if (jsonObject.containsKey("payMemberid")) {
                    this.payMemberid = jsonObject.getString("payMemberid");
                }
                if (jsonObject.containsKey("payNotifyUrl")) {
                    this.payNotifyUrl = jsonObject.getString("payNotifyUrl");
                }
                if (jsonObject.containsKey("md5Key")) {
                    this.md5Key = jsonObject.getString("md5Key");
                }
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
        logger.info("[YHB]亿汇宝扫码支付扫码支付开始======================START==================");
        try {
            //封装请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("pay_md5sign", sign);
            logger.info("[YHB]亿汇宝扫码支付请求参数:" + JSONObject.fromObject(data).toString());
            //生成请求表单
            String resStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[YHB]亿汇宝扫码支付响应信息:" + resStr);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[YHB]亿汇宝扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[YHB]亿汇宝扫码支付发起HTTP请求无响应结果");
            }
            return PayResponse.sm_form(payEntity, resStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YHB]亿汇宝扫码支付生成异常:" + e.getMessage());
            return PayResponse.error("[YHB]亿汇宝扫码支付下单失败");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        try {
            String sourceSign = data.remove("sign");
            String sign = generatorSign(data);
            logger.info("[YHB]亿汇宝扫码支付回调生成签名串" + sign);
            if (sign.equals(sourceSign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YHB]亿汇宝扫码支付回调生成签名串异常" + e.getMessage());
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
        Map<String, String> data = new HashMap<>();
        data.put("pay_memberid", payMemberid);//商户号
        data.put("pay_orderid", payEntity.getOrderNo());//订单号
        data.put("pay_amount", df.format(payEntity.getAmount()));//金额
        data.put("pay_applydate", sdf.format(new Date()));//提交时间
        data.put("pay_bankcode", payEntity.getPayCode());//银行编码
        data.put("pay_notifyurl", payNotifyUrl);//异步回调地址
        data.put("pay_callbackurl", payNotifyUrl);
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
            sb.append(key).append("=").append(val).append("&");
        }
        sb.append("key=").append(md5Key);
        //生成待签名串
        String signStr = sb.toString();
        logger.info("[YHB]亿汇宝扫码支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(signStr);
        logger.info("[YHB]亿汇宝扫码支付生成加密签名串:{}", sign);
        return sign;
    }
}
