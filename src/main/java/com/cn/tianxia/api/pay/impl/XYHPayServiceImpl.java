/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    XYHPayServiceImpl.java 
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
 *    Create at:   2019年05月03日 11:01 
 *
 *    Revision: 
 *
 *    2019/5/3 11:01 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 *  * @ClassName XYHPayServiceImpl
 *  * @Description TODO(新银河支付)
 *  * @Author Roman
 *  * @Date 2019年05月03日 11:01
 *  * @Version 1.0.0
 *  
 **/

public class XYHPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(XYHPayServiceImpl.class);

    private static final String ret__failed = "Notify is failed";

    private static final String ret__success = "SUCCESS";

    /**
     * 商户号
     */
    private String merchId;

    /**
     * 支付地址
     */
    private String payUrl;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 密钥
     */
    private String secret;


    /**
     * 构造器，初始化参数
     */
    public XYHPayServiceImpl() {
    }

    public XYHPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merchId")) {
                this.merchId = data.get("merchId");
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
        return null;
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity entity) {
        logger.info("[XYH]新银河支付扫码支付开始============START======================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(entity);
            logger.info("[XYH]新银河支付扫码支付请求参数:{}", JSONObject.fromObject(data));

            //发送请求
            String response = HttpUtils.generatorForm(data, payUrl);
            logger.info("[XYH]新银河支付扫码支付响应:{}", response);

            if (StringUtils.isBlank(response)) {
                logger.info("[XYH]新银河支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[XYH]新银河支付扫码支付发起HTTP请求无响应结果");
            }

            return PayResponse.sm_form(entity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[XYH]新银河支付扫码支付下单失败" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[XYH]新银河支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new TreeMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


//            return_type	返回数据类型	是	字符串	必填参数json， html（详情请看，返回说明）
            dataMap.put("return_type", "html");

//            api_code	商户号	是	字符串	必须
            dataMap.put("api_code", merchId);

//            is_type	支付类型	是	字符串	必须，支付渠道：alipay支付宝
            dataMap.put("is_type", entity.getPayCode());

//            price	订单定价	是	float，保留2位小数	必须，保留2位小数，不能传0
            dataMap.put("price", amount);

//            order_id	您的自定义单号	是	字符串，最长50位	必须，在商户系统中保持唯一
            dataMap.put("order_id", orderNo);

//            time	发起时间	是	时间戳，最长10位	必须 时间戳
            dataMap.put("time", String.valueOf(System.currentTimeMillis() / 1000));

//            mark	描述	是	字符串，最长100位	必须 粗略说明支付目的（例如 购买食杂）
            dataMap.put("mark", "top_Up");

//            return_url	成功后网页跳转地址	是	字符串，最长255位	必须，成功后网页跳转地址（例如 http://www.qq.com）
            dataMap.put("return_url", entity.getRefererUrl());

//            notify_url	通知状态异步回调接收地址	是	字符串，最长255位	必须
            dataMap.put("notify_url", notifyUrl);


            //以上字段参与签名,生成待签名串
            String sign = generatorSign(dataMap);
            dataMap.put("sign", sign);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XYH]新银河支付封装请求参数异常:" + e.getMessage());
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
        logger.info("[XYH]新银河支付生成支付签名串开始==================START========================");
        try {
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            StringBuffer sb = new StringBuffer();
            for (String key : sortMap.keySet()) {
                String val = sortMap.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.append("key=").append(secret);
            //生成待签名串
            String signStr = sb.toString();

            logger.info("[XYH]新银河支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[XYH]新银河支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XYH]新银河支付生成支付签名串异常:" + e.getMessage());
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
        logger.info("[XYH]新银河支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[XYH]新银河支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XYH]新银河支付生成加密串异常:{}", e.getMessage());
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

        logger.info("[XYH]新银河支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签，从配置中获取
        this.secret = config.getString("secret");
        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("order_id");
        // 第三方订单号
        String tradeNo = infoMap.get("paysapi_id");
        //订单状态
        String tradeStatus = infoMap.get("code");
        // 表示成功状态
        String tTradeStatus = "1";
        //实际支付金额
        String orderAmount = String.valueOf(infoMap.get("real_price"));
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
        processNotifyVO.setPayment("XYH");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}




