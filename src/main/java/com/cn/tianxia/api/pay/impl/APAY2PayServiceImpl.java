/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    APAY2PayServiceImpl.java 
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
 *    Create at:   2019年03月27日 10:39 
 *
 *    Revision: 
 *
 *    2019/3/27 10:39 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
 *  * @ClassName APAY2PayServiceImpl
 *  * @Description TODO(APAY支付2)
 *  * @Author Roman
 *  * @Date 2019年03月27日 10:39
 *  * @Version 1.0.0
 *  
 **/

public class APAY2PayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(APAY2PayServiceImpl.class);

    private static final String ret__failed = "FAIL";

    private static final String ret__success = "SUCCESS";

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 支付请求地址
     */

    private String payUrl;

    /**
     * 订单查询地址
     */

    private String queryUrl;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 密钥
     */
    private String key;

    /**
     * 接入类型
     */
    private String ChannelType;


    /**
     * 构造器，初始化参数
     */
    public APAY2PayServiceImpl() {
    }

    public APAY2PayServiceImpl(Map<String, String> data) {
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
            if (data.containsKey("queryUrl")) {
                this.queryUrl = data.get("queryUrl");
            }
            if (data.containsKey("ChannelType")) {
                this.ChannelType = data.get("ChannelType");
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

            String response = HttpUtils.toPostForm(data, payUrl);
            logger.info("[APAY2]APAY支付2扫码支付响应:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.info("[APAY2]APAY支付2扫码支付下单失败，无响应结果");
                return PayResponse.error("[APAY2]APAY支付2扫码支付下单失败，无响应结果");
            }
            JSONObject jsonObject = JSONObject.fromObject(response);
            if (jsonObject.containsKey("Code") && "1000".equalsIgnoreCase(jsonObject.getString("Code"))) {
                //下单成功
                String payUrl = jsonObject.getJSONObject("Data").getString("PayHtml");
                return PayResponse.sm_link(entity, payUrl, "扫码支付下单成功");
            }
            return PayResponse.error("[APAY2]APAY支付2下单失败:" + response);

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[APAY2]APAY支付2扫码支付下单异常" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[APAY2]APAY支付2封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);
            //订单号
            String orderNo = entity.getOrderNo();


//            MerchantNo	String/64	是	商户号，平台分配
            dataMap.put("MerchantNo", mchId);

//            OutTradeNo	String/64	是	商户订单号，与商户号一起保持唯一
            dataMap.put("OutTradeNo", orderNo);

//            ChannelType	Int	是	接入类型，10：支付宝红包
            dataMap.put("ChannelType", ChannelType);

//            PayWay	Int	是	支付方式，1：支付宝
            dataMap.put("PayWay", entity.getPayCode());

//            Body	String/100	是	商品名称
            dataMap.put("Body", "top-Up");

//            Amount	Int	是	支付金额，分为单位（如1元=100）
            dataMap.put("Amount", amount);

//            NotifyUrl	String/200	是	异步通知URL（必传），与后台配置的一致。
            dataMap.put("NotifyUrl", notifyUrl);

            //生成待签名串
            String sign = generatorSign(dataMap, 1);
//            Sign	String/64	是	签名(MD5加密)
            dataMap.put("Sign", sign);
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[APAY2]APAY支付2封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param type 1:   支付     2:  回调        3:  查询
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data, int type) throws Exception {
        try {

//            MD5(Amount + Body + ChannelType + MerchantNo + OutTradeNo + PayWay + 商户秘钥).ToLower()
            StringBuffer sb = new StringBuffer();
            if (type == 1) {
                sb.append(data.get("Amount"))
                        .append(data.get("Body"))
                        .append(data.get("ChannelType"))
                        .append(data.get("MerchantNo"))
                        .append(data.get("OutTradeNo"))
                        .append(data.get("PayWay"));
            } else if (type == 2) {
//                签名：  MD5 (OrderNo + MerchantNo + Amount + OutTradeNo + Status + (商户秘钥)).ToLower ()
                sb.append(data.get("OrderNo"))
                        .append(data.get("MerchantNo"))
                        .append(data.get("Amount"))
                        .append(data.get("OutTradeNo"))
                        .append(data.get("Status"));
            } else {
//                MD5(MerchantNo + OutTradeNo + (商户秘钥)).ToLower ()
                sb.append(data.get("MerchantNo"))
                        .append(data.get("OutTradeNo"));
            }
            sb.append(key);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[APAY2]APAY支付2生成待签名串:" + signStr);
            //生成加密串
            String sign = Objects.requireNonNull(MD5Utils.md5toUpCase_32Bit(signStr)).toLowerCase();
            logger.info("[APAY2]APAY支付2生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[APAY2]APAY支付2生成支付签名串异常:" + e.getMessage());
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
        logger.info("[APAY2]APAY支付2回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("Sign");
        logger.info("[APAY2]APAY支付2回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data, 2);
            logger.info("[APAY2]APAY支付2回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[APAY2]APAY支付2生成加密串异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
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
            map.put("MerchantNo", mchId);

//            OutTradeNo	String/64	商户订单号
            map.put("OutTradeNo", orderNo);

//            签名
            map.put("Sign", generatorSign(map, 3));

            logger.info("[APAY2]APAY支付2订单查询接口订单{}请求参数{}", orderNo, map);
            //发送请求
            String response = HttpUtils.toPostForm(map, queryUrl);
            logger.info("[APAY2]APAY支付2订单查询接口响应信息{}", response);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            if (respJson.containsKey("Code") && "1000".equals(respJson.getString("Code"))) {
                if ("1".equals(respJson.getJSONObject("Data").getString("Status"))) {

                    logger.info("[APAY2]APAY支付2订单查询成功,订单" + orderNo + "已支付。");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[APAY2]APAY支付2订单查询异常");
            return false;
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
        this.mchId = config.getString("mch_id");
        this.queryUrl = config.getString("queryUrl");

        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[APAY2]APAY支付2回调请求参数:{}", infoMap);
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }


        // 平台商订单号
        String orderNo = infoMap.get("OutTradeNo");

        //调用查询接口查询订单信息
        boolean orderStatus = getOrderStatus(orderNo);
        if (!orderStatus) {
            logger.info(orderNo + "此订单尚未支付成功！");
            return ret__failed;
        }
        boolean verifyRequest = verifyCallback(infoMap);

        // 支付商订单号
        String tradeNo = infoMap.get("OrderNo");
        //订单状态
        String tradeStatus = infoMap.get("Status");
        // 表示成功状态
        String tTradeStatus = "1";
        //实际支付金额
        String orderAmount = infoMap.get("Amount");
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
        processNotifyVO.setRealAmount(realAmount / 100);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("APAY2");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}


