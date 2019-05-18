package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName HPZFPayServiceImpl
 * @Description 恒付支付
 * @Date 2019年1月23日 下午5:41:18
 */
public class HPZFPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(HPZFPayServiceImpl.class);

    private String merchaantNo;//商户号

    private String payUrl;//支付地址

    private String notifyUrl;//回调地址

    private String md5Key;//md5签名key

    //构造器,初始化参数
    public HPZFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merchaantNo")) {
                this.merchaantNo = data.get("merchaantNo");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
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
        logger.info("[HPZF]恒付支付扫码支付开始=============START=============");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);

            logger.info("[HPZF]恒付支付生成扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                return PayResponse.error("[HPZF]恒付支付扫码支付发起HTTP请求无响应结果");
            }

            logger.info("[HPZF]恒付支付扫码支付发起HTTP请求响应结果:{}", response);
            JSONObject jsonObject = JSONObject.fromObject(response);
            if (jsonObject.containsKey("code") && "200".equals(jsonObject.getString("code"))) {
                //跳转地址
                String PayUrl = jsonObject.getJSONObject("data").getString("payUrl");
                return PayResponse.sm_link(payEntity, PayUrl, "扫码支付下单成功");
            }
            return PayResponse.error("扫码支付下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HPZF]恒付支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[HPZF]恒付支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[HPZF]恒付支付回调验签开始================START===================");
        try {

            String sourceSign = data.get("sign");
            logger.info("[HPZF]恒付支付回调验签获取原签名串:{}", sourceSign);

            String sign = generatorSign(data, 2);
            logger.info("[HPZF]恒付支付回调验签生成加密签名串:{}", sign);
            if (sourceSign.equalsIgnoreCase(sign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HPZF]恒付支付回调验签异常:{}", e.getMessage());
        }
        return "faild";
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[HPZF]恒付支付组装支付请求参数开始==================START==============");
        try {
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额,单位元,保留两位小数
            data.put("version", "1.0");//String(30) Y   版本号（写死 1.0）
            data.put("MerchaantNo", merchaantNo);//String(30) Y  商户号
            data.put("type", entity.getPayCode());//String(20) Y   支付宝:ALIPAY
            data.put("url", notifyUrl);//String(10) Y  回调地址
            data.put("userRemark", entity.getOrderNo());//String(10) Y 提案号
            if (StringUtils.isBlank(entity.getMobile())) {
                data.put("fromType", "0");//String(40) N   平台（0,pc；1,手机端）
            } else {
                data.put("fromType", "1");//String(40) N   平台（0,pc；1,手机端）
            }
            data.put("depositAmount", amount);//String(128) Y 金额
            data.put("ReturnUrl", entity.getRefererUrl());//String(128) N   支付返回地址，支付成功时同步跳转到此地址
            data.put("Payer", entity.getUsername());//String(128) N  真实姓名
            //生成签名串
            String sign = generatorSign(data, 1);
            //put签名串到参数集合中
            data.put("Sign", sign);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HPZF]恒付支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[HPZF]恒付支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @param type 1 支付签名 2 回调签名
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    private String generatorSign(Map<String, String> data, int type) throws Exception {
        logger.info("[HPZF]恒付支付生成签名串开始===============START==============");
        try {
            StringBuffer sb = new StringBuffer();
            if (type == 1) {
                sb.append(data.get("version"))
                        .append(data.get("MerchaantNo"))
                        .append(data.get("depositAmount"))
                        .append(data.get("type"))
                        .append(data.get("userRemark"))
                        .append(md5Key);
            } else {
                sb.append(data.get("depositNumber"))
                        .append(data.get("userReamrk"))
                        .append(data.get("amount"))
                        .append(data.get("Note"))
                        .append(data.get("time"))
                        .append(md5Key);
            }
            String signStr = sb.toString();
            logger.info("[HPZF]恒付支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[HPZF]恒付支付生成加密签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HPZF]恒付支付生成签名串异常:{}", e.getMessage());
            throw new Exception("[HPZF]恒付支付生成签名串异常");
        }
    }
}
