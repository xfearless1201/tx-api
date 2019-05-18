/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    QLPayServiceImpl.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:    Roman 
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年03月12日 20:16 
 *
 *    Revision: 
 *
 *    2019/3/12 20:16 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 *  * @ClassName QLPayServiceImpl
 *  * @Description TODO(麒麟支付)
 *  * @Author Roman
 *  * @Date 2019年03月12日 20:16
 *  * @Version 1.0.0
 *  
 **/

public class QLPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(QLPayServiceImpl.class);

    private static final String RET_FAILED = "fail";

    private static final String RET_SUCCESS = "OK";

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
     * 密钥
     */
    private String key;


    /**
     * 构造器，初始化参数
     */
    public QLPayServiceImpl() {
    }

    public QLPayServiceImpl(Map<String, String> data) {
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
    public JSONObject wyPay(PayEntity entity) {
        logger.info("[QL]麒麟支付网银支付开始============START======================");
        try {
            //获取请求参数
            Map<String, String> data = sealRequest(entity);
            logger.info("麒麟支付网银支付请求参数{}", data);
            //发送请求
            String response = HttpUtils.toPostForm(data, payUrl);

            logger.info("麒麟支付网银支付响应结果{}", response);
            if (StringUtils.isBlank(response)) {
                logger.info("[QL]麒麟支付网银支付发起请求无响应结果");
                return PayResponse.error("[QL]麒麟支付网银支付发起请求无响应结果");
            }
            return PayResponse.wy_form(entity.getPayUrl(), response);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[QL]麒麟支付网银支付下单失败" + e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity entity) {
        logger.info("[QL]麒麟支付扫码支付开始============START======================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(entity);

            //发送请求
            String response = HttpUtils.generatorForm(data, payUrl);
            logger.info("[QL]麒麟支付扫码支付响应:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.info("[QL]麒麟支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[QL]麒麟支付扫码支付发起HTTP请求无响应结果");
            }
            return PayResponse.sm_form(entity, response, "扫码支付下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[QL]麒麟支付扫码支付下单失败" + e.getMessage());
        }
    }

    /**
     * 功能描述:回调验签
     *
     * @param data 回调请求参数
     * @Date: 2019年03月12日 20:59:51
     * @return: boolean
     **/
    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[QL]麒麟支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        data.remove("attach");
        logger.info("[QL]麒麟支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[QL]麒麟支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[QL]麒麟支付生成加密串异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
    }

    /**
     * @param
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[QL]麒麟支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());// 订单时间

            dataMap.put("pay_memberid", mchId);// 平台分配商户号
            dataMap.put("pay_orderid", orderNo);// 订单号
            dataMap.put("pay_applydate", orderTime);// 提交时间,时间格式：2016-12-26 18:18:18
            dataMap.put("pay_bankcode", entity.getPayCode());// 银行编码
            dataMap.put("pay_notifyurl", notifyUrl);// 服务端通知
            dataMap.put("pay_callbackurl", entity.getRefererUrl());// 页面跳转通知
            dataMap.put("pay_amount", amount);// 订单金额 单位：元
            logger.info("[QL]麒麟支付封装签名参数:" + JSONObject.fromObject(dataMap).toString());
            // 以上字段参与签名,生成待签名串
            String sign = generatorSign(dataMap);
            dataMap.put("pay_md5sign", sign);
            dataMap.put("pay_productname", "top_Up");// 商品名称
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[QL]麒麟支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        logger.info("[QL]麒麟支付生成支付签名串开始==================START========================");
        try {
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = sortMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = sortMap.get(key);
                if (StringUtils.isBlank(val) || key.equalsIgnoreCase("sign")) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.append("key=").append(key);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[QL]麒麟支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[QL]麒麟支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[QL]麒麟支付生成支付签名串异常:" + e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }

    /**
     * 回调方法
     *
     * @param request  第三方请求request
     * @param response response
     * @param config   平台对应支付商配置信息
     * @return
     */
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[QL]麒麟支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return RET_FAILED;
        }
        //参数验签，从配置中获取
        this.key = config.getString("key");
        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("orderid");
        // 第三方订单号
        String tradeNo = infoMap.get("transaction_id");
        //订单状态
        String tradeStatus = infoMap.get("returncode");
        // 表示成功状态
        String tTradeStatus = "00";
        //实际支付金额
        String orderAmount = infoMap.get("amount");
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return RET_FAILED;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        //成功返回
        processNotifyVO.setRet__success(RET_SUCCESS);
        //失败返回
        processNotifyVO.setRet__failed(RET_FAILED);
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(orderNo);
        processNotifyVO.setTrade_no(tradeNo);
        processNotifyVO.setTrade_status(tradeStatus);
        processNotifyVO.setT_trade_status(tTradeStatus);
        processNotifyVO.setRealAmount(realAmount);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("QL");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}

