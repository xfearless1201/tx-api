/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    GXYFPayServiceImpl.java 
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
 *    Create at:   2019年04月09日 10:54 
 *
 *    Revision: 
 *
 *    2019/4/9 10:54 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.pay.*;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 *  * @ClassName GXYFPayServiceImpl
 *  * @Description TODO(共享亿付支付)
 *  * @Author Roman
 *  * @Date 2019年04月09日 10:54
 *  * @Version 1.0.0
 *  
 **/

public class GXYFPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(GXYFPayServiceImpl.class);

    private static final String ret__failed = "fail";

    private static final String ret__success = "success";


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
    public GXYFPayServiceImpl() {
    }

    public GXYFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
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
        return null;
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            //获取支付请求参数
            JSONObject data = sealRequest(payEntity);
            String content = AesEncryptionUtil.encrypt(String.valueOf(data), "kafu-ef465sd1", "5effe26250e19130");
            Map<String, String> map = new HashMap<>();
            map.put("content", content);

            logger.info("[GXYF]共享亿付支付扫码支付请求参数报文:{}", data);

            String response = HttpUtils.generatorForm(map, payUrl);
            logger.info("[GXYF]共享亿付支付扫码支付发起HTTP请求响应结果:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.error("[GXYF]共享亿付支付下单失败：HTTP请求无响应");
                PayResponse.error("[GXYF]共享亿付支付下单失败：HTTP请求无响应");
            }
            return PayResponse.sm_form(payEntity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[GXYF]共享亿付支付扫码支付下单失败" + e.getMessage());
        }
    }

    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[GXYF]共享亿付支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        logger.info("[GXYF]共享亿付支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[GXYF]共享亿付支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[GXYF]共享亿付支付回调验签生成加密串异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
    }


    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private JSONObject sealRequest(PayEntity entity) throws Exception {
        logger.info("[GXYF]共享亿付支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            JSONObject data = new JSONObject();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            String orderNo = entity.getOrderNo();

            data.put("apikey", key);//            商户密钥key 商户后台获取
            data.put("order_id", orderNo);//            平台订单号
            data.put("order_price", amount);//            order_price	订单价格（单位:元）
            data.put("notify_url", notifyUrl);
            data.put("return_url", entity.getRefererUrl());
            data.put("type", entity.getPayCode());

            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[GXYF]共享亿付支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        logger.info("[GXYF]共享亿付支付生成支付签名串开始==================START========================");
        try {
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            StringBuffer sb = new StringBuffer();
            for (String key : sortMap.keySet()) {
                String val = String.valueOf(sortMap.get(key));
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            //生成待签名串
            String signStr = sb.substring(0, sb.length() - 1);
            logger.info("[GXYF]共享亿付支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[GXYF]共享亿付支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[GXYF]共享亿付支付生成支付签名串异常:" + e.getMessage());
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
        //参数验签，从配置中获取
        this.key = config.getString("key");

        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        String content = infoMap.get("content");
        String decrypt = AesEncryptionUtil.decrypt(content, "kafu-ef465sd1", "5effe26250e19130");

        Gson gson = new Gson();
        Map map = new HashMap<>();
        map = gson.fromJson(decrypt, map.getClass());
        logger.info("[GXYF]共享亿付支付回调请求参数:{}", JSONObject.fromObject(map));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(map);

        // 平台订单号
        String orderNo = String.valueOf(map.get("order_id"));
        // 第三方订单号
        String tradeNo = String.valueOf(map.get("apiorder_id"));
        //订单状态
        String tradeStatus = String.valueOf(map.get("status"));
        // 表示成功状态
        String tTradeStatus = "ok";
        //实际支付金额
        String orderAmount = String.valueOf(map.get("order_price"));
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
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
        processNotifyVO.setInfoMap(JSONObject.fromObject(map).toString());
        processNotifyVO.setPayment("GXYF");

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}


