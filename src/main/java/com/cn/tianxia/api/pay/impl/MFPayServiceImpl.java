/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    MFPayServiceImpl.java 
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
 *    Create at:   2019年03月20日 17:07 
 *
 *    Revision: 
 *
 *    2019/3/20 17:07 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
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
 *  * @ClassName MFPayServiceImpl
 *  * @Description TODO(码付支付)
 *  * @Author Roman
 *  * @Date 2019年03月20日 17:07
 *  * @Version 1.0.0
 *  
 **/

public class MFPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(MFPayServiceImpl.class);

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
     * 构造器，初始化参数
     */
    public MFPayServiceImpl() {
    }

    public MFPayServiceImpl(Map<String, String> data) {
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
            logger.info("[MFPay]码付支付扫码支付请求参数:{}", data);

            String response = HttpUtils.toPostForm(data, payUrl);
            logger.info("[MFPay]码付支付扫码支付响应:{}", response);

            if (StringUtils.isBlank(response)) {
                logger.info("[MFPay]码付支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[MFPay]码付支付扫码支付发起HTTP请求无响应结果");
            }
            JSONObject jsonObject = JSONObject.fromObject(response);
            if (jsonObject.containsKey("code") && "0".equals(jsonObject.getString("code"))) {
                //下单成功
                String payUrl = jsonObject.getJSONObject("data").getString("qrcode");
                return PayResponse.sm_qrcode(entity, payUrl, "扫码支付下单成功");
            }
            return PayResponse.error("下单失败:" + response);
//            return PayResponse.sm_form(entity, payUrl, "扫码支付下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[MFPay]码付支付扫码支付下单失败" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[MFPay]码付支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();


//            uid	商户uid	string(10)	必填。您的商户唯一标识，注册后在设置里获得。
            dataMap.put("uid", mchId);

//            2	money	价格	string(10)	必填。单位：元。精确小数点后2位
            dataMap.put("money", amount);

//            3	pay_way	支付渠道	string(1)	必填。1：支付宝；2：微信支付
            dataMap.put("pay_way", entity.getPayCode());

//            4	format	支付类型	string(1)	必填。1：json返回 推荐；2：网页支付
            dataMap.put("format", "1");

//            4	notify_url	通知回调网址	string(255)	必填。用户支付成功后，我们服务器会主动发送一个post消息到这个网址。由您自定义。不要urlencode。例：http://www .aaa.com/qpay_notify
            dataMap.put("notify_url", notifyUrl);

//            5	return_url	跳转网址	string(255)	必填。用户支付成功后，我们会让用户浏览器自动跳转到这个网址。由您自定义。不要urlencode。例：http://www.aaa .com/qpay_return
            dataMap.put("return_url", entity.getRefererUrl());

//            6	order_id	商户自定义订单号	string(50)	必填。我们会据此判别是同一笔订单还是新订单。我们回调时，会带上这个参数。例：201710192541
            dataMap.put("order_id", orderNo);

//            7	way_id	收款账号ID	string(100)	必填。随机填0，指定收款账号就填您的后台收款账号ID
            dataMap.put("way_id", "0");

            //以上字段参与签名,生成待签名串
            String sign = generatorSign(dataMap);
//           10 key	新秘钥	string(32)	必填。把使用到的所有参数按参数名字母升序排序(ascii码正序)，连后台给你的旧key密钥一起（key放最后，这里的key是后台的密钥），把参数值拼接在一起（空参数不参与签名）。做md5-32位加密，取字符串小写。得到新的key。网址类型的参数值不要urlencode。
            dataMap.put("key", sign);
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[MFPay]码付支付封装请求参数异常:" + e.getMessage());
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
        logger.info("[MFPay]码付支付生成支付签名串开始==================START========================");
        try {
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = sortMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = sortMap.get(key);
                if (StringUtils.isBlank(val) || key.equalsIgnoreCase("key")) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.append("key=").append(key);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[MFPay]码付支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[MFPay]码付支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[MFPay]码付支付生成支付签名串异常:" + e.getMessage());
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
        logger.info("[MFPay]码付支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("key");
        logger.info("[MFPay]码付支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[MFPay]码付支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[MFPay]码付支付生成加密串异常:{}", e.getMessage());
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

        logger.info("[MFPay]码付支付回调请求参数:{}", infoMap);
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签，从配置中获取
        this.key = config.getString("key");
        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("order_id");
        // 第三方订单号
        String tradeNo = infoMap.get("orderno");
        //订单状态
        String tradeStatus = "success";
        // 表示成功状态
        String tTradeStatus = "success";
        //实际支付金额
        String orderAmount = String.valueOf(infoMap.get("real_money"));
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
        processNotifyVO.setPayment("MF");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}


