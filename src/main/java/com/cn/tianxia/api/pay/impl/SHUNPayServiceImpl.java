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

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName SHUNPayServiceImpl
 * @Description 顺付支付
 * @Date 2018年12月28日 下午3:31:53
 */
public class SHUNPayServiceImpl implements PayService {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(SHUNPayServiceImpl.class);

    private String merno;// 商户号

    private String payUrl;// 支付请求地址

    private String md5Key;// 加密key

    // 构造器，初始化参数
    public SHUNPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merno")) {
                this.merno = data.get("merno");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
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
        logger.info("[SHUN]顺付支付扫码支付开始==============START===============");
        try {
            Map<String, String> data = sealRequest(payEntity);
            String sign = generatorSign(data, 1);
            data.put("sign", sign);
            if (StringUtils.isNotBlank(payEntity.getMobile())) {
                String formStr = HttpUtils.generatorForm(data, payUrl);
                return PayResponse.sm_form(payEntity, formStr, "下单成功");
            } else {
                // 发起HTTP请求
                String response = HttpUtils.toPostForm(data, payUrl);
                if (StringUtils.isBlank(response)) {
                    logger.info("[SHUN]顺付支付扫码支付发起HTTP请求无响应结果");
                    return PayResponse.error("[SHUN]顺付支付扫码支付发起HTTP请求无响应结果");
                }
                JSONObject jsonObject = JSONObject.fromObject(response);
                logger.info("[SHUN]顺付支付扫码支付发起HTTP请求响应结果:{}", jsonObject);

                if (jsonObject.containsKey("code") && "200".equals(jsonObject.getString("code"))) {
                    // 支付成功
                    JSONObject dataJson = JSONObject.fromObject(jsonObject.getString("data"));
                    if (dataJson.containsKey("url")) {
                        String url = dataJson.getString("url");
                        return PayResponse.sm_link(payEntity, url, "下单成功");
                    }
                }
                return PayResponse.error("下单失败:" + jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SHUN]顺付支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[SHUN]顺付支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[SHUN]顺付支付回调验签开始===============START================");
        try {

            String sourceSign = data.get("sign");
            String sign = generatorSign(data, 0);
            logger.info("[SHUN]顺付支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
            if (sourceSign.equalsIgnoreCase(sign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SHUN]顺付支付回调验签异常:{}", e.getMessage());
        }
        return "faild";
    }

    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[SHUN]顺付支付组装支付请求参数开始====================START=================");
        try {
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("##").format(entity.getAmount());// 订单金额
            data.put("version", "1.0");// 版本号 1.0
            data.put("MerchaantNo", merno);// 游戏方商户号
            data.put("type", entity.getPayCode());// 充值方式 alapi
            data.put("amount", amount);// 充值金额
            data.put("payer", entity.getOrderNo());// 付款帐号,订单号
            if (StringUtils.isNotBlank(entity.getMobile())) {
                // 移动端
                data.put("mobile", "1");// 取得充值连结后自动跳转充值(选择性参数) 1
            }
            data.put("customSign", entity.getuId());// 商户自定义验证字串，会在回调时传回(选择性参数)
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SHUN]顺付支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[SHUN]顺付支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    private String generatorSign(Map<String, String> data, int type) throws Exception {
        logger.info("[SHUN]顺付支付生成签名串开始================START================");
        try {
            StringBuffer sb = new StringBuffer();
            if (type == 1) {
                // 支付签名
                sb.append(data.get("version")).append(merno).append(data.get("type"));
                sb.append(data.get("payer")).append(md5Key);
            } else {
                // 回调签名
                String amount = data.get("amount");
                amount = new DecimalFormat("##").format(Double.parseDouble(amount));
                sb.append(data.get("depositNumber")).append(amount);
                sb.append(data.get("note")).append(md5Key);
            }
            String signStr = sb.toString();// 待签名串
            logger.info("[SHUN]顺付支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[SHUN]顺付支付生成加密签名串:{}", sign);
            return sign;

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SHUN]顺付支付生成签名串异常:{}", e.getMessage());
            throw new Exception("[SHUN]顺付支付生成签名串异常");
        }
    }

}
