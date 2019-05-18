/******************************************************************
 *
 *    Powered By tianxia-online.
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技
 *    http://www.d-telemedia.com/
 *
 *    Package:     com.cn.tianxia.pay.impl
 *
 *    Filename:    PEPayServiceImpl.java
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
 *    Create at:   2019年03月26日 11:19
 *
 *    Revision:
 *
 *    2019/3/26 11:19
 *        - first revision
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
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
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.utils.tx.RSAUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import com.google.gson.Gson;

import net.sf.json.JSONObject;

/**
 *  * @ClassName PEPayServiceImpl
 *  * @Description TODO(这里用一句话描述这个类的作用)
 *  * @Author Roman
 *  * @Date 2019年03月26日 11:19
 *  * @Version 1.0.0
 *  
 **/

public class PEPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(PEPayServiceImpl.class);

    private static final String ret__failed = "fail";

    private static final String ret__success = "success";

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 支付请求地址
     */

    private String payUrl;

    /**
     * 密钥
     */
    private String key;


    /**
     * 构造器，初始化参数
     */
    public PEPayServiceImpl() {
    }

    public PEPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("mch_id")) {
                this.mchId = data.get("mch_id");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("key")) {
                this.key = data.get("key");
            }
        }
    }


    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity entity) {
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(entity);

            //发送HTTP请求
            String response = HttpUtils.generatorFormGet(data, payUrl);
            logger.info("[PEPAY]太子支付扫码支付响应:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.info("[PEPAY]太子支付扫码支付下单失败，无响应结果");
                return PayResponse.error("[PEPAY]太子支付扫码支付下单失败，无响应结果");
            }
            return PayResponse.sm_form(entity, response, "扫码支付下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[PEPAY]太子支付扫码支付下单异常" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[PEPAY]太子支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();

//           商户号
            dataMap.put("ptId", mchId);

//            商家订单号
            dataMap.put("sjdd", orderNo);

//            支付方式 暂时有4种 1、微信支付 2支付宝支付 3 银联支付 4 云闪付
            dataMap.put("zffs", entity.getPayCode());

//            请求支付金额
            dataMap.put("money", amount);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[PEPAY]太子支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * 功能描述:回调验签
     *
     * @param data
     * @return: boolean
     **/
    private boolean verifyCallback(Map<String, String> data) {
        return true;
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
        this.mchId = config.getString("mch_id");
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        JSONObject jsonObject = JSONObject.fromObject(infoMap.get("data"));
        String context = jsonObject.getString("context");

        Map<String, String> map = new HashMap<>();
        try {
            String dataStr = RSAUtils.decryptByPublicKey(context, key);

            Gson gson = new Gson();

            map = gson.fromJson(dataStr, map.getClass());

        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("[PEPAY]太子支付回调请求参数:{},解密后{}", JSONObject.fromObject(infoMap),JSONObject.fromObject(map));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }


        // 平台商订单号
        String orderNo = map.get("sjdd");
        boolean verifyRequest = verifyCallback(infoMap);

        // 支付商订单号
        String tradeNo = "PEPAY" + System.currentTimeMillis();
        //订单状态
        String tradeStatus = "success";
        // 表示成功状态
        String tTradeStatus = "success";
        //实际支付金额
        String orderAmount = String.valueOf(map.get("money"));
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        //成功返回
        processNotifyVO.setRet__success(ret__success);
        //失败返回
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(orderNo);
        processNotifyVO.setTrade_no(tradeNo);
        processNotifyVO.setTrade_status(tradeStatus);
        processNotifyVO.setT_trade_status(tTradeStatus);
        processNotifyVO.setRealAmount(realAmount);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(map).toString());
        processNotifyVO.setPayment("PE");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}



