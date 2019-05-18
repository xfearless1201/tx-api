/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    XNNPayServiceImpl.java 
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
 *    Create at:   2019年04月21日 11:45 
 *
 *    Revision: 
 *
 *    2019/4/21 11:45 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

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
 *  * @ClassName HAIPayServiceImpl
 *  * @Description TODO(海阳支付)
 *  * @Author Roman
 *  * @Date 2019年04月21日 11:45
 *  * @Version 1.0.0
 *  
 **/

public class HAIPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(HAIPayServiceImpl.class);

    private static final String ret__failed = "Notify is failed";

    private static final String ret__success = "success";

    /**
     * 商户号
     */
    private String mchId;

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
    private String key;


    /**
     * 构造器，初始化参数
     */
    public HAIPayServiceImpl() {
    }

    public HAIPayServiceImpl(Map<String, String> data) {
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
        logger.info("[HAI]海阳支付扫码支付开始============START======================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(entity);
            logger.info("[HAI]海阳支付扫码支付请求参数:{}", JSONObject.fromObject(data));

            //发送请求
            String response = HttpUtils.generatorFormGet(data, payUrl);
            logger.info("[HAI]海阳支付扫码支付响应:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.info("[HAI]海阳支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[HAI]海阳支付扫码支付发起HTTP请求无响应结果");
            }
            return PayResponse.sm_form(entity, response, "扫码支付下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[HAI]海阳支付扫码支付下单失败" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[HAI]海阳支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new TreeMap<>();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount()*100);
            //订单号
            String orderNo = entity.getOrderNo();

//            mchid	商户ID	是	String(32)	商户在平台的 商户ID号
            dataMap.put("mchid", mchId);

//            mchno	商户订单号	是	String(32)	商户自己生成的订单号 由英文、数字、_、- 、组成，不能含有特殊符号 如：2999218888886、no2999218888886 ，长度不能超过32位
            dataMap.put("mchno", orderNo);

//            tradetype	订单类型	是	String(32)	weixin 表示发起微信扫码支付 weixinh5 表示发起微信h5支付 alipay 表示发起支付宝扫码支付 alipayh5 表示发起支付宝h5支付
            dataMap.put("tradetype", entity.getPayCode());

//            totalfee	支付金额	是	String(32)	订单需要支付的金额，单位：分（人民币）如10元,输入1000
            dataMap.put("totalfee", amount);

//            descrip	订单描述	是	String(225)	长度不能超过127位 可以由中文、英文、数字、_、- 、组成不能含有特殊符号如： XX充值中心-XX会员充值 含有中文需要utf-8编码
            dataMap.put("descrip", "top_Up");
            dataMap.put("attach", "top_Up");

//            clientip	终端IP	是	String(64)	长度不能超过46位， 订单生成的机器 IP
            dataMap.put("clientip", entity.getIp());

//            notifyurl	异步通知地址	是	String(225)	接收平台异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。如： http://www.xxxx.com/wxpay/pay.php
            dataMap.put("notifyurl", notifyUrl);
            dataMap.put("returnurl", notifyUrl);

            //以上字段参与签名,生成待签名串
            String sign = generatorSign(dataMap,1);
            dataMap.put("sign", sign);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HAI]海阳支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param type 1:支付   2:回调    3:查询
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data, int type) throws Exception {
        logger.info("[HAI]海阳支付生成支付签名串开始==================START========================");
        try {

            StringBuffer sb = new StringBuffer();
            if (type == 1) {
               /* mchid=10000&mchno=201803051730&tradetype=alipayh5
                        &totalfee=1000&descrip=xxxx&attach=xxxx&clientip=127.0.0.1&
                        notifyurl=http://xxxx.cn/wxpay/pay.php&returnurl=
                http://xxxx.cn/wxpay/pay.php&key=c4b70b766ea78fe1689f4e4e1afa291a*/
                sb.append("mchid=").append(data.get("mchid")).append("&")
                        .append("mchno=").append(data.get("mchno")).append("&")
                        .append("tradetype=").append(data.get("tradetype")).append("&")
                        .append("totalfee=").append(data.get("totalfee")).append("&")
                        .append("descrip=").append(data.get("descrip")).append("&")
                        .append("attach=").append(data.get("attach")).append("&")
                        .append("clientip=").append(data.get("clientip")).append("&")
                        .append("notifyurl=").append(data.get("notifyurl")).append("&")
                        .append("returnurl=").append(data.get("returnurl")).append("&");
            } else if (type == 2) {
//                resultcode=1&transactionid=201803051730&mchid=10000&mchno=201803051730&tradetype=weixin&totalfee=60.00
//                  &attach=yyyxx&key=c4b70b766ea78fe1689f4e4e1afa291a
                sb.append("resultcode=").append(data.get("resultcode")).append("&")
                        .append("transactionid=").append(data.get("transactionid")).append("&")
                        .append("mchid=").append(data.get("mchid")).append("&")
                        .append("mchno=").append(data.get("mchno")).append("&")
                        .append("tradetype=").append(data.get("tradetype")).append("&")
                        .append("totalfee=").append(data.get("totalfee")).append("&")
                        .append("attach=").append(data.get("attach")).append("&");
            }
            sb.append("key=").append(key);
            //生成待签名串
            String singStr = sb.toString();
            logger.info("[HAI]海阳支付生成待签名串:" + singStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString());
            logger.info("[HAI]海阳支付生成签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HAI]海阳支付生成支付签名串异常:" + e.getMessage());
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
        logger.info("[HAI]海阳支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        logger.info("[HAI]海阳支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data,2);
            logger.info("[HAI]海阳支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HAI]海阳支付生成加密串异常:{}", e.getMessage());
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

        logger.info("[HAI]海阳支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签，从配置中获取
        this.key = config.getString("key");
        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("mchno");
        // 第三方订单号
        String tradeNo = infoMap.get("transactionid");
        //订单状态
        String tradeStatus = infoMap.get("resultcode");
        // 表示成功状态
        String tTradeStatus = "1";
        //实际支付金额
        String orderAmount =infoMap.get("totalfee");
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
        processNotifyVO.setPayment("HAI");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}



