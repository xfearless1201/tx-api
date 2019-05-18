/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    DGPayServiceImpl.java 
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
 *    Create at:   2019年03月14日 16:39 
 *
 *    Revision: 
 *
 *    2019/3/14 16:39 
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
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 *  * @ClassName DGPayServiceImpl
 *  * @Description TODO(冬瓜支付)
 *  * @Author Roman
 *  * @Date 2019年03月14日 16:39
 *  * @Version 1.0.0
 *  
 **/
public class DGPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DGPayServiceImpl.class);

    private static final String RET_FAILED = "fail";

    private String RET_SUCCESS = "success";

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
    public DGPayServiceImpl() {
    }

    public DGPayServiceImpl(Map<String, String> data) {
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
        return null;
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity entity) {
        logger.info("[DGPay]冬瓜支付扫码支付开始============START======================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(entity);

            //发送请求
            String response = HttpUtils.get(data, payUrl + mchId + "/" + entity.getPayCode());
            logger.info("[DGPay]冬瓜支付扫码支付响应:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.info("[DGPay]冬瓜支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[DGPay]冬瓜支付扫码支付发起HTTP请求无响应结果");
            }
            JSONObject jsonObject = JSONObject.fromObject(response);
            if (jsonObject.containsKey("ret") && "true".equals(jsonObject.getString("ret"))) {
                //下单成功
                JSONObject dataObject = (JSONObject) jsonObject.get("data");
                String payurl = dataObject.getString("payUrl");
                return PayResponse.sm_link(entity, payurl, "扫码支付下单成功");
            }
            return PayResponse.error("下单失败:" + response);

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[DGPay]冬瓜支付扫码支付下单失败" + e.getMessage());
        }
    }

    /**
     * @param
     * @return
     * @Description 回调验签
     */
    private boolean verifyCallback(JSONObject reqJson, String sourceSign) {
        logger.info("[DGPay]冬瓜支付回调验签开始==============START===========");
        logger.info("[DGPay]冬瓜支付回调验签获取原签名串:{}", sourceSign);
        StringBuffer sb = new StringBuffer();
        sb.append(reqJson).append(key);
        String signStr = sb.toString();
        //生成验签签名串
        String sign = null;
        try {
            sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[DGPay]冬瓜支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[DGPay]冬瓜支付生成加密串异常:{}", e.getMessage());
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
        logger.info("[DGPay]冬瓜支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);
            //订单号
            String orderNo = entity.getOrderNo();

//            title：备注，可以是app自己的订单id，32个字符以内
            dataMap.put("title", orderNo);
//            amount：金额，整数类型，单位:  分。
            dataMap.put("amount", amount);
//            clientip：待支付的客户的ip
            dataMap.put("clientip", entity.getIp());
//            timestamp：时间戳 timestamp
            dataMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
//            callback：url地址，用于动态回调订单信息通知平台，可选，如果没有该参数，则使用初始化配置的回调url
            dataMap.put("callback", notifyUrl);

            logger.info("[DGPay]冬瓜支付封装签名参数:" + JSONObject.fromObject(dataMap).toString());
            // 以上字段参与签名,生成待签名串
//            sign：签名，md5(appid+title+amount+timestamp+私钥
            String sign = generatorSign(dataMap);
            dataMap.put("sign", sign);
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[DGPay]冬瓜支付封装请求参数异常:" + e.getMessage());
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
        logger.info("[DGPay]冬瓜支付生成支付签名串开始==================START========================");
        try {
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            StringBuffer sb = new StringBuffer();
            for (String key : sortMap.keySet()) {
                String val = String.valueOf(sortMap.get(key));
                if (StringUtils.isBlank(val) || key.equalsIgnoreCase("sign")) {
                    continue;
                }
                sb.append(val);
            }
            sb.append(key);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[DGPay]冬瓜支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[DGPay]冬瓜支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[DGPay]冬瓜支付生成支付签名串异常:" + e.getMessage());
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
        //回调完成后返回此Json数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ret", true);
        this.RET_SUCCESS = jsonObject.toString();

        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        logger.info("[DGPay]冬瓜支付回调请求参数:{}", infoMap);
        JSONObject data = JSONObject.fromObject(infoMap.get("data"));
        String sign = infoMap.get("sign");
        logger.info("源签名串："+sign);
        logger.info("[DGPay]冬瓜支付回调获取data参数值:{}", data);
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return RET_FAILED;
        }
        //参数验签，从配置中获取
        this.key = config.getString("key");
        this.mchId = config.getString("mch_id");
        boolean verifyRequest = verifyCallback(data,sign);

        // 平台订单号
        String orderNo = data.getString("title");
        // 第三方订单号
        String tradeNo = data.getString("transId");
        //订单状态
        String tradeStatus = "success";
        // 表示成功状态
        String tTradeStatus = "success";
        //实际支付金额
        String orderAmount = data.getString("amount");
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
        processNotifyVO.setRealAmount(realAmount / 100);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(data).toString());
        processNotifyVO.setPayment("DG");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}


