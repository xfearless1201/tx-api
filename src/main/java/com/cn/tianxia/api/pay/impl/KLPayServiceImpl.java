/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    KLPayServiceImpl.java 
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
 *    Create at:   2019年03月22日 14:54 
 *
 *    Revision: 
 *
 *    2019/3/22 14:54 
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
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.utils.pay.RC4;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 *  * @ClassName KLPayServiceImpl
 *  * @Description TODO(口令支付)
 *  * @Author Roman
 *  * @Date 2019年03月22日 14:54
 *  * @Version 1.0.0
 *  
 **/

public class KLPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(KLPayServiceImpl.class);

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
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 密钥
     */
    private String key;

    /**
     * 网页类型
     */
    private String contentType;

    /**
     * 轮询
     */
    private String robin;

    /**
     * 跳转链接
     */
    private String requestUrl;


    /**
     * 构造器，初始化参数
     */
    public KLPayServiceImpl() {
    }

    public KLPayServiceImpl(Map<String, String> data) {
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
            if (data.containsKey("contentType")) {
                this.contentType = data.get("contentType");
            }
            if (data.containsKey("robin")) {
                this.robin = data.get("robin");
            }
            if (data.containsKey("requestUrl")) {
                this.requestUrl = data.get("requestUrl");
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
            logger.info("[KLpay]口令支付扫码支付请求参数:{}", data);

            String response = HttpUtils.toPostForm(data, payUrl);
            logger.info("[KLpay]口令支付扫码支付响应:{}", response);

            if (StringUtils.isBlank(response)) {
                logger.info("[KLpay]口令支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[KLpay]口令支付扫码支付发起HTTP请求无响应结果");
            }
            JSONObject jsonObject = JSONObject.fromObject(response);
            if (jsonObject.containsKey("code") && "200".equals(jsonObject.getString("code"))) {
                //下单成功
                String payUrl = jsonObject.getJSONObject("data").getString("order_id");
                return PayResponse.sm_link(entity, requestUrl + payUrl, "扫码支付下单成功");
            }
            return PayResponse.error("下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[KLpay]口令支付扫码支付下单失败" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[KLpay]口令支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();

//            account_id	商户ID、在平台首页右边获取商户ID	10000
            dataMap.put("account_id", mchId);

//            content_type	请求过程中返回的网页类型，text或json	json
            dataMap.put("content_type", contentType);

//            thoroughfare	alipay_auto（银行转账）	alipay_auto
            dataMap.put("thoroughfare", "alipay_auto");

//            out_trade_no	订单信息，在发起订单时附加的信息，如用户名，充值订单号等字段参数	2018062668945
            dataMap.put("out_trade_no", orderNo);

//            robin	轮训，2：开启轮训，1：进入单通道模式	2
            dataMap.put("robin", robin);

//            amount	支付金额，在发起时用户填写的支付金额	1.00
            dataMap.put("amount", amount);

//            callback_url	异步通知地址，在支付完成时，本平台服务器系统会自动向该地址发起一条支付成功的回调请求	http://x3s6.com/callback_url/pay.do
            dataMap.put("callback_url", notifyUrl);


//            success_url	支付成功后网页自动跳转地址，仅在网页类型为text下有效，json会将该参数返回	http://x3s6.com/index/doc/getQrcode.do
            dataMap.put("success_url", entity.getRefererUrl());

//            error_url	支付失败时，或支付超时后网页自动跳转地址，仅在网页类型为text下有效，json会将该参数返回	http://x3s6.com/index/doc/getQrcode.do
            dataMap.put("error_url", entity.getRefererUrl());

            //以上字段参与签名,生成待签名串
            String sign = generatorSign(dataMap);
//            sign	签名算法，在支付时进行签名算法，详见《支付签名算法》	d92eff67b3be05f5e61502e96278d01b
            dataMap.put("sign", sign);
//            type	支付类型，银行转账：2	1
            dataMap.put("type", entity.getPayCode());

//            keyId	设备KEY，在公开版列表里面Important参数下的DEVICE Key一项，如果该请求为轮训模式，则本参数无效，本参数为单通道模式	785D239777C4DE7739
            dataMap.put("keyId", key);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[KLpay]口令支付封装请求参数异常:" + e.getMessage());
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
        logger.info("[KLpay]口令支付生成支付签名串开始==================START========================");
        try {

            String dataStr = data.get("amount") + data.get("out_trade_no");
            logger.info("[KLpay]口令支付生成签名串参数:" + data);

            String md5Crypt = MD5Utils.md5(dataStr.getBytes());
            logger.info("[KLpay]口令支付生成MD5加密串:" + md5Crypt);

            byte[] rc4String = RC4.encry_RC4_byte(md5Crypt, key);
            logger.info("[KLpay]口令支付生成RC4加密串:" + rc4String);
            String sign = MD5Utils.md5(rc4String);
            logger.info("[KLpay]口令支付生成最终签名:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[KLpay]口令支付生成支付签名串异常:" + e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }

    /**
     * 功能描述:回调验签
     *
     * @param data
     * @return: boolean
     **/
    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[KLpay]口令支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        logger.info("[KLpay]口令支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[KLpay]口令支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[KLpay]口令支付生成加密串异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
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

        logger.info("[KLpay]口令支付回调请求参数:{}", infoMap);
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签，从配置中获取
        this.key = config.getString("key");
        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("out_trade_no");
        // 第三方订单号
        String tradeNo = infoMap.get("trade_no");
        //订单状态
        String tradeStatus = infoMap.get("status");
        // 表示成功状态
        String tTradeStatus = "success";
        //实际支付金额
        String orderAmount = String.valueOf(infoMap.get("amount"));
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
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("KL");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}


