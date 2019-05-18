/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    FNTPayServiceImpl.java
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:     ROMAN
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
 *  * @ClassName FNTPayServiceImpl
 *  * @Description 529Pay支付实现类
 *  * @Author ROMAN
 *  * @Date 2019年02月03日 11:33
 *  * @Version 1.0.0
 *  
 **/
public class FNTPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(FNTPayServiceImpl.class);

    private static final String ret__failed = "fail";

    private static final String ret__success = "OK";


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
     * 查询地址
     */
    private String queryUrl;


    /**
     * 构造器，初始化参数
     */
    public FNTPayServiceImpl() {
    }

    public FNTPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("mchId")) {
                this.mchId = data.get("mchId");
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
            if (data.containsKey("queryUrl")) {
                this.queryUrl = data.get("queryUrl");
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
        logger.info("[592]592Pay支付扫码支付开始================START============");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);
            logger.info("[592]592Pay支付扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());

            //发起HTTP请求
            String response = HttpUtils.generatorForm(data, payUrl);

            if (StringUtils.isBlank(response)) {
                logger.info("[592]592Pay支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[592]592Pay支付扫码支付发起HTTP请求无响应结果");
            }
            logger.info("[592]592Pay支付扫码支付发起HTTP请求响应结果:{}", response);

            return PayResponse.sm_form(payEntity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[592]592Pay支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[592]592Pay支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[592]592Pay支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        data.remove("attach");
        logger.info("[592]592Pay支付回调验签获取原签名串:{}", sourceSign);
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[592]592Pay支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[592]592Pay支付回调验签异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[592]592Pay支付组装支付请求参数开始==============START==================");
        try {
            //创建参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            String orderNo = entity.getOrderNo();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date timeStart = new Date();

            // 商户号
            dataMap.put("pay_memberid", mchId);

            // 商户订单号
            dataMap.put("pay_orderid", orderNo);

            // pay_applydate	提交时间	是	是	时间格式：2016-12-26 18:18:18
            dataMap.put("pay_applydate", sdf.format(timeStart));

            //   pay_bankcode	银行编码	是	是	参考后续说明
            dataMap.put("pay_bankcode", entity.getPayCode());

            // pay_notifyurl	服务端通知	是	是	服务端返回地址.（POST返回数据）
            dataMap.put("pay_notifyurl", notifyUrl);

            // pay_callbackurl	页面跳转通知	是	是	页面跳转返回地址（POST返回数据）
            dataMap.put("pay_callbackurl", entity.getRefererUrl());

            //  pay_amount	订单金额	是	是	商品金额
            dataMap.put("pay_amount", amount);

            //生成签名串
            String sign = generatorSign(dataMap);
            dataMap.put("pay_md5sign", sign);
            dataMap.put("pay_productname", "TOP-UP");

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[592]592Pay支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[592]592Pay支付组装支付请求参数异常");
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
        logger.info("[592]592Pay支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(signStr);
        logger.info("[592]592Pay支付生成加密签名串:{}", sign);
        return sign;
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
        //参数验签，从配置中获取
        this.key = config.getString("key");
        this.mchId = config.getString("mchId");
        this.queryUrl = config.getString("queryUrl");
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[592]592Pay支付回调请求参数:{}", infoMap);
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

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
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        //查询订单信息
        boolean orderStatus = getOrderStatus(orderNo);
        if (!orderStatus) {
            logger.info(orderNo + "此订单尚未支付成功！");
            return ret__failed;
        }
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(orderNo);
        processNotifyVO.setTrade_no(tradeNo);
        processNotifyVO.setTrade_status(tradeStatus);
        processNotifyVO.setT_trade_status(tTradeStatus);
        processNotifyVO.setRealAmount(realAmount);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("FNT");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }

    /**
     * 功能描述:查询订单状态
     *
     * @param orderNo 订单号
     * @return: boolean
     **/
    private boolean getOrderStatus(String orderNo) {
        try {
            //封装请求参数
            Map<String, String> map = new HashMap<>();
//            MerchantNo	String/64	平台分配
            map.put("pay_memberid", mchId);

//            OutTradeNo	String/64	商户订单号
            map.put("pay_orderid", orderNo);

//            签名
            map.put("pay_md5sign", generatorSign(map));

            logger.info("[592]592Pay支付订单查询接口订单{}请求参数{}", orderNo, map);
            //发送请求
            String response = HttpUtils.toPostForm(map, queryUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[592]592Pay支付订单查询接口响应信息{}", respJson);
            if (respJson.containsKey("returncode") && "00".equals(respJson.getString("returncode"))) {
                if ("SUCCESS".equalsIgnoreCase(respJson.getString("trade_state"))) {

                    logger.info("[592]592Pay支付订单查询成功,订单" + orderNo + "已支付。");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[592]592Pay支付订单查询异常");
            return false;
        }
    }
}