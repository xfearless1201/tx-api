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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class XBFPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(XBFPayServiceImpl.class);
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

    public XBFPayServiceImpl(Map<String, String> data) {
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
            if (data.containsKey("md5Key")) {
                this.md5Key = data.get("md5Key");
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
        logger.info("[XBF]新宝付扫码支付开始======================START==================");
        try {
            //封装请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("Sign", sign);
            logger.info("[XBF]新宝付扫码支付请求参数:" + JSONObject.fromObject(data).toString());
            //生成请求表单
            String resStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[XBF]新宝付扫码支付响应信息:" + resStr);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[XBF]新宝付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[XBF]新宝付扫码支付发起HTTP请求无响应结果");
            }
            return PayResponse.sm_form(payEntity, resStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XBF]新宝付扫码支付生成异常:" + e.getMessage());
            return PayResponse.error("[XBF]新宝付扫码支付下单失败");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        try {
            String sourceSign = data.remove("sign");
            StringBuffer sb = new StringBuffer();
            sb.append("sErrorCode=").append(data.get("sErrorCode")).append("&");
            sb.append("bType=").append(data.get("bType")).append("&");
            sb.append("ForUserId=").append(data.get("ForUserId")).append("&");
            sb.append("LinkID=").append(data.get("LinkID")).append("&");
            sb.append("Moneys=").append(data.get("Moneys")).append("&");
            sb.append("AssistStr=").append(data.get("AssistStr")).append("&");
            sb.append("keyValue=").append(md5Key);
            String signStr = sb.toString().toLowerCase();
            logger.info("[XBF]新宝付扫码支付回调生成待签名串" + signStr);
            String sign = MD5Utils.md5(signStr.getBytes("gb2312"));
            logger.info("[XBF]新宝付扫码支付回调生成签名串" + sign);
            if (sign.equals(sourceSign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XBF]新宝付扫码支付回调生成签名串异常" + e.getMessage());
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
        String uid = UUID.randomUUID().toString();
        Map<String, String> data = new HashMap<>();
        data.put("LinkID", payEntity.getOrderNo());//订单号
        data.put("ForUserId", payMemberid);//商户号
        data.put("Channelid", payEntity.getPayCode());//银行编码
        data.put("Moneys", df.format(payEntity.getAmount()));//金额
        data.put("AssistStr", uid.replace("-", ""));//附加字段
        data.put("ReturnUrl", payNotifyUrl);//回调地址
        data.put("NotifyUrl", payNotifyUrl);//异步回调地址
        return data;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("LinkID=").append(data.get("LinkID")).append("&");
        sb.append("ForUserId=").append(data.get("ForUserId")).append("&");
        sb.append("Channelid=").append(data.get("Channelid")).append("&");
        sb.append("Moneys=").append(data.get("Moneys")).append("&");
        sb.append("AssistStr=").append(data.get("AssistStr")).append("&");
        sb.append("ReturnUrl=").append(data.get("ReturnUrl")).append("&");
        sb.append("Key=").append(md5Key);
        //生成待签名串
        String signStr = sb.toString();
        logger.info("[XBF]新宝付扫码支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5(signStr.getBytes("gb2312"));
        logger.info("[XBF]新宝付扫码支付生成加密签名串:{}", sign);
        return sign;
    }
}
