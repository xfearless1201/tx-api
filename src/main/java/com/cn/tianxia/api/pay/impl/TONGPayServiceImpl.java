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
 * @ClassName TONGPayServiceImpl
 * @Description 通支付
 * @Date 2018年12月22日 下午4:52:51
 */
public class TONGPayServiceImpl implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(TONGPayServiceImpl.class);

    private String pid;//商户号

    private String payUrl;//支付地址

    private String notifyUrl;//回调地址

    private String secret;//秘钥

    //构造器,初始化参数
    public TONGPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("pid")) {
                this.pid = data.get("pid");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("secret")) {
                this.secret = data.get("secret");
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
        logger.info("[TONG]通支付扫码支付开始================START=================");
        try {
            //生成请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[TONG]通支付扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[TONG]通支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[TONG]通支付扫码支付发起HTTP请求无响应结果");
            }
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[TONG]通支付扫码支付发起HTTP请求响应结果:{}", jsonObject);

            if (jsonObject.containsKey("code") && "1".equals(jsonObject.getString("code"))) {
                //成功
                String payurl = jsonObject.getString("payurl");
                return PayResponse.sm_link(payEntity, payurl, "下单成功");
            }
            return PayResponse.error("下单失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TONG]通支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[TONG]通支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[TONG]通支付回调验签开始===============START=================");
        try {
            //获取原签名串
            String sourceSign = data.get("sign");
            String sign = generatorSign(data);
            logger.info("[TONG]通支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
            if (sourceSign.equalsIgnoreCase(sign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TONG]通支付回调验签异常:{}", e.getMessage());
        }
        return null;
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[TONG]通支付组装支付请求参数开始===================START==============");
        try {
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("pid", pid);//商户ID
            data.put("type", entity.getPayCode());//支付方式
            data.put("out_trade_no", entity.getOrderNo());//商户订单号
            data.put("notify_url", notifyUrl);//异步通知地址
            data.put("return_url", entity.getRefererUrl());//跳转通知地址
            data.put("name", "TOP-UP");//商品名称
            data.put("attach", entity.getUsername());//附加数据
            data.put("money", amount);//商品金额
            data.put("format", "json");//返回格式
            data.put("sign_type", "MD5");
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TONG]通支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[TONG]通支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 签名
     */
    private String generatorSign(Map<String, String> data) throws Exception {
        logger.info("[TONG]通支付生成签名开始===================START==================");
        try {
            StringBuffer sb = new StringBuffer();
            Map<String, String> sortmap = MapUtils.sortByKeys(data);
            for (String key : sortmap.keySet()) {
                String val = sortmap.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)
                        || "sign_type".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append("&").append(key).append("=").append(val);
            }
            String signStr = sb.append(secret).toString().replaceFirst("&", "");
            logger.info("[TONG]通支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[TONG]通支付生成加密签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TONG]通支付生成签名异常:{}", e.getMessage());
            throw new Exception("[TONG]通支付生成签名异常");
        }
    }

}
