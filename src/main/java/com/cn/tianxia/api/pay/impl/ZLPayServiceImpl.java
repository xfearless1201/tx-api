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
import java.util.Iterator;
import java.util.Map;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName ZLPayServiceImpl
 * @Description 站蓝支付
 * @Date 2018年11月8日 下午8:21:29
 */
public class ZLPayServiceImpl implements PayService {

    //日志 
    private static final Logger logger = LoggerFactory.getLogger(ZLPayServiceImpl.class);

    private String merchantNo;//商户号

    private String payUrl;//支付请求地址

    private String notifyUrl;//回调请求地址

    private String secret;//秘钥

    //构造器,初始化参数
    public ZLPayServiceImpl(Map<String, String> data) {
        if (data != null && !data.isEmpty()) {
            if (data.containsKey("merchantNo")) {
                this.merchantNo = data.get("merchantNo");
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
        logger.info("[ZL]站蓝支付扫码支付开始===================START=======================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSing(data);
            data.put("sign", sign);
            logger.info("[ZL]站蓝支付扫码支付生成请求报文:{}", JSONObject.fromObject(data));
            //发起HTTP请求
            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                return PayResponse.error("[ZL]站蓝支付扫码支付发起HTTP请求无响应结果!");
            }

            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[ZL]站蓝支付扫码支付发起HTTP请求无响应结果:{}", jsonObject);
            if (jsonObject.containsKey("resp_code") && "0000".equals(jsonObject.getString("resp_code"))) {
                //下单成功
                String payResult = jsonObject.getString("data");
                if (StringUtils.isBlank(payEntity.getMobile())) {
                    //PC端
                    return PayResponse.sm_qrcode(payEntity, payResult, "下单成功!");
                }

                return PayResponse.sm_link(payEntity, payResult, "下单成功");
            }
            return PayResponse.error("下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZL]站蓝支付扫码支付:{}", e.getMessage());
            return PayResponse.error("[ZL]站蓝支付扫码支付异常!");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[ZL]站蓝支付回调验签开始===============================START==========================");
        try {
            //获取验签原签名串
            String sourceSign = data.get("sign");
            logger.info("[ZL]站蓝支付回调验签获取服务器原签名串:{}", sourceSign);
            //生成签名串
            String sign = generatorSing(data);
            logger.info("[ZL]站蓝支付回调验签生成加密签名串:{}", sign);
            if ("sign".equalsIgnoreCase(sourceSign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZL]站蓝支付回调验签异常:{}", e.getMessage());
        }
        return "success";
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[ZL]站蓝支付组装支付请求参数开始=========================START==================");
        try {
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("merchant_no", merchantNo);//商户编号
            data.put("amount", amount);//订单金额,单位：元，保留小数点后两位
            data.put("currency", "156");//币种,填：156
            data.put("order_no", entity.getOrderNo());//订单号   36位
            data.put("pay_code", entity.getPayCode());//产品类型,20000:支付宝扫码 30000:微信扫码
            data.put("pay_ip", entity.getIp());//支付ip
            data.put("request_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//下单时间,格式yyyy-MM-dd HH:mm:ss
            data.put("product_name", "TOP-UP");//商品名称
            data.put("return_url", entity.getRefererUrl());//页面通知地址,页面通知地址(暂不可用)
            data.put("notify_url", notifyUrl);//后台异步通知地址
            data.put("remark", "TOP-UP");//备注
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZL]站蓝支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[ZL]站蓝支付组装支付请求参数异常!");
        }
    }


    private String generatorSing(Map<String, String> data) throws Exception {
        logger.info("[ZL]站蓝支付生成加密签名串开始==========================START=======================");
        try {
            Map<String, String> sortmap = MapUtils.sortByKeys(data);
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = sortmap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = sortmap.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;

                sb.append(key).append("=").append(val).append("&");
            }
            sb.append("key=").append(secret);
            //待加密签名串
            String signStr = sb.toString();
            logger.info("[ZL]站蓝支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[ZL]站蓝支付生成加密签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZL]站蓝支付生成加密签名串异常:{}", e.getMessage());
            throw new Exception("[ZL]站蓝支付生成加密签名串异常");
        }
    }
}
