/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    XLZFPayServiceImpl.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:     Administrator 
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年02月18日 10:42 
 *
 *    Revision: 
 *
 *    2019/2/18 10:42 
 *        - first revision 
 *
 *****************************************************************/
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
 *  * @ClassName XLZFPayServiceImpl
 *  * @Description TODO(信连支付)
 *  * @Author Roman
 *  * @Date 2019年02月18日 10:42
 *  * @Version 1.0.0
 *  
 **/
public class XLZFPayServiceImpl implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(XLZFPayServiceImpl.class);

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 支付请求地址
     */

    private String payUrl;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 秘钥
     */
    private String key;


    /**
     * 构造器，初始化参数
     */
    public XLZFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("mch_id")) {
                this.mchId = data.get("mch_id");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
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
        logger.info("[XLZF]信连支付扫码支付开始================START============");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);

            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("sign", sign);

            logger.info("[XLZF]信连支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));
            //发起HTTP请求
            String response = HttpUtils.post(data, payUrl);

            if (StringUtils.isBlank(response) || response.contains("FAIL")) {
                logger.info("[XLZF]信连支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[XLZF]信连支付扫码支付发起HTTP请求无响应结果");
            }

            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[XLZF]信连支付扫码支付发起HTTP请求响应结果:{}", jsonObject);
            if (jsonObject.containsKey("error") && "0".equals(jsonObject.getString("error"))) {
                // 下单成功
                String payurl = jsonObject.getString("qrcode");
                return PayResponse.sm_link(payEntity, payurl, "扫码支付下单成功");
            }
            return PayResponse.error("下单失败:" + jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XLZF]信连支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[XLZF]信连支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[XLZF]信连支付回调验签开始==============START===========");
        try {
            //获取回调通知原签名串
            String sourceSign = data.get("sign");
            String sign = generatorSign(data, 0);
            logger.info("[XLZF]信连支付回调生成签名串：{}--源签名串：{}", sign , sourceSign );
            if (sourceSign.equalsIgnoreCase(sign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XLZF]信连支付回调验签异常:{}", e.getMessage());
        }
        return "fail";
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[XLZF]信连支付组装支付请求参数开始==============START==================");
        try {
            //创建参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);
            String orderNo = entity.getOrderNo();

//            mchid	商户 mchid	int(5)	必填	您的商户唯一标识。
            dataMap.put("mchid", mchId);

//            amount	价格	int	必填	单位：分。例如：1.12元 则为 112
            dataMap.put("amount", amount);

//            pay_type	支付方式	int	必填	1：支付宝；2：微信支付
            dataMap.put("pay_type", "1");

//            notify_url	通知回调网址	string(255)	必填	异步通知地址。例：https://www.xxx.com/notify ,若有带参数需加urlencode。
            dataMap.put("notify_url", notifyUrl);

//            return_url	跳转网址	string(255)	必填	成功跳转地址。例：https://www.xxx.com/return ,若有带参数需加urlencode。
            dataMap.put("return_url", entity.getRefererUrl());

//            trade_out_no	商户自定义订单号	string(50)	必填	例：201710192541
            dataMap.put("trade_out_no", orderNo);
//            sign	秘钥	string(32)	必填	把必填参数，连Token一起，按参数名字母升序排序。并把参数值拼接在一起。做md5-32位加密，取字符串小写。得到sign。网址类型的参数值不要urlencode。

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XLZF]信连支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[XLZF]信连支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(Map<String, String> data, int type) throws Exception {
        StringBuffer sb = new StringBuffer();
        if (type == 1) {
            //请求签名
//            amount = 100 & mchid = 80000 & notify_url = http://pay.fiking.vip/pay/notify.php
//             &pay_type=1&return_url=http://pay.fiking.vip/pay/return.php
//           &token=e10adc3949ba59abbe56e057f20f883e&trade_out_no=20180808000000
            sb.append("amount=").append(data.get("amount")).append("&");
            sb.append("mchid=").append(mchId).append("&");
            sb.append("notify_url=").append(notifyUrl).append("&");
            sb.append("pay_type=").append(data.get("pay_type")).append("&");
            sb.append("return_url=").append(data.get("return_url")).append("&");
            sb.append("token=").append(key).append("&");
            sb.append("trade_out_no=").append(data.get("trade_out_no"));
        } else {
            //回调签名
//            amount=100&pay_sn=20180808000000&real_amount=100&token=e10adc3949ba59abbe56e057f20f883e&trade_out_no=20180808000000
            sb.append("amount=").append(data.get("amount")).append("&");
            sb.append("pay_sn=").append(data.get("pay_sn")).append("&");
            sb.append("real_amount=").append(data.get("real_amount")).append("&");
            sb.append("token=").append(key).append("&");
            sb.append("trade_out_no=").append(data.get("trade_out_no"));
        }
        //生成待签名串
        String signStr = sb.toString();
        logger.info("[XLZF]信连支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
        logger.info("[XLZF]信连支付生成加密签名串:{}", sign);
        return sign;
    }
}
