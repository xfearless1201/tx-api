/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    ESTPayServiceImpl.java 
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
 *    Create at:   2019年02月03日 11:33
 *
 *    Revision:
 *
 *    2019/2/3 11:33
 *        - first revision
 *
 *****************************************************************/
package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
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
 *  * @ClassName ESTPayServiceImpl
 *  * @Description TODO(这里用一句话描述这个类的作用)
 *  * @Author Administrator
 *  * @Date 2019年02月03日 11:33
 *  * @Version 1.0.0
 *  
 **/
public class ESTPayServiceImpl implements PayService {


    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ESTPayServiceImpl.class);

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


    private String key;


    /**
     * 构造器，初始化参数
     */
    public ESTPayServiceImpl(Map<String, String> data) {
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
        logger.info("[EST]易盛通支付扫码支付开始================START============");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);

            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);

            logger.info("[EST]易盛通支付扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());
            logger.info("请求地址：" + payUrl);
            //发起HTTP请求
            String response = HttpUtils.toPostJsonStr(JSONObject.fromObject(data), payUrl);

            if (StringUtils.isBlank(response) || response.contains("FAIL")) {
                logger.info("[EST]易盛通支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[EST]易盛通支付扫码支付发起HTTP请求无响应结果");
            }
            logger.info("[EST]易盛通支付扫码支付发起HTTP请求响应结果:{}", response);

            JSONObject jsonObject = JSONObject.fromObject(response);
            if (jsonObject.containsKey("result_code") && "0000".equals(jsonObject.getString("sub_code"))) {
                //下单成功
                String payurl = jsonObject.getString("payinfo");

                if(StringUtils.isBlank(payEntity.getMobile())){
                    //PC端
                    return PayResponse.sm_qrcode(payEntity, payurl, "扫码支付下单成功");
                }
                return PayResponse.sm_link(payEntity, payurl, "扫码支付下单成功");
            }
            return PayResponse.error("下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[EST]易盛通支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[EST]易盛通支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[EST]易盛通支付回调验签开始==============START===========");

        return "success";
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[EST]易盛通支付组装支付请求参数开始==============START==================");
        try {
            //创建参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            String orderNo = entity.getOrderNo();
            // 商户号
            dataMap.put("mch_id", mchId);
            // 商户订单号
            dataMap.put("tradeno", orderNo);
            //订单标题
            dataMap.put("subject", "Pay");
            //订单总金额  以元为单位，最低到分（66.66）
            dataMap.put("total_amount", amount);
            //支付完成后结果通知url,支付成功回调路径；json格式post提交商户后台
            dataMap.put("notify_url", notifyUrl);
            //支付方IP
            dataMap.put("pay_ip", entity.getIp());
            //连接方式         0：APP  1：PC  2：JSAPI   3：H5
            dataMap.put("opsys", "1");
            //版本号   当前版本号V1.0.0
            dataMap.put("version", "V1.0.0");
            //接口名称  第三方提供的
            dataMap.put("method", "userCodePosPay");
            // 支付方式的标识
            dataMap.put("pay_type", entity.getPayCode());

            if ("alipayH5".equals(entity.getPayCode())) {
                //支付宝用户ID
                dataMap.put("user_id", entity.getuId());
            }


            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[EST]易盛通支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[EST]易盛通支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        Map<String, String> sortMap = MapUtils.sortByKeys(data);
        StringBuffer sb = new StringBuffer();
        Iterator<String> iterator = sortMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String val = sortMap.get(key);
            if (org.apache.commons.lang.StringUtils.isBlank(val) || key.equalsIgnoreCase("sign")) {
                continue;
            }
            sb.append(key).append("=").append(val).append("&");
        }
        sb.append("key=").append(key);
        //生成待签名串
        String signStr = sb.toString();
        logger.info("[EST]易盛通支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
        logger.info("[EST]易盛通支付生成加密签名串:{}", sign);
        return sign;
    }
}