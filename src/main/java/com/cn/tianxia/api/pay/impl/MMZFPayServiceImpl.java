/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    MMZFPayServiceImpl.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:     Administrator 
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年02月21日 14:48 
 *
 *    Revision: 
 *
 *    2019/2/21 14:48 
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
 *  * @ClassName MMZFPayServiceImpl
 *  * @Description TODO(这里用一句话描述这个类的作用)
 *  * @Author Roman
 *  * @Date 2019年02月21日 14:48
 *  * @Version 1.0.0
 *  
 **/

public class MMZFPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(MMZFPayServiceImpl.class);

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
    private String token;


    /**
     * 构造器，初始化参数
     */
    public MMZFPayServiceImpl() {
    }

    public MMZFPayServiceImpl(Map<String, String> data) {
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
            if (data.containsKey("token")) {
                this.token = data.get("token");
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
            Map<String, String> data = sealRequest(payEntity);

            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("key", sign);
            data.remove("token");

            logger.info("[MMZF]M码支付扫码支付请求参数报文:{}", data);
            String response = HttpUtils.generatorForm(data, payUrl);
            logger.info("[MMZF]M码支付扫码支付发起HTTP请求响应结果:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.error("[MMZF]M码支付下单失败：生成请求form为空");
                PayResponse.error("[MMZF]M码支付下单失败：生成请求form为空");
            }
            return PayResponse.sm_form(payEntity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[MMZF]M码支付扫码支付下单失败" + e.getMessage());
        }
    }

    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[MMZF]M码支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("key");
        logger.info("[MMZF]M码支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data, 0);
            logger.info("[MMZF]M码支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[MMZF]M码支付回调验签生成加密串异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
    }


    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[MMZF]M码支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            String orderNo = entity.getOrderNo();

//            1	uid	商户uid	int(10)	必填。您的商户唯一标识，注册后在设置里获得。
            data.put("uid", mchId);

//            2	price	价格	float	必填。单位：元。精确小数点后2位
            data.put("price", amount);

//            3	istype	支付渠道	int	必填。1：支付宝；2：微信支付
            data.put("istype", entity.getPayCode());

//            4	notify_url	通知回调网址	string(255)	必填。用户支付成功后，我们服务器会主动发送一个post消息到这个网址。由您自定义。不要urlencode。例：http://www .aaa.com/qpay_notify
            data.put("notify_url", notifyUrl);

//            5	return_url	跳转网址	string(255)	必填。用户支付成功后，我们会让用户浏览器自动跳转到这个网址。由您自定义。不要urlencode。例：http://www.aaa .com/qpay_return
            data.put("return_url", entity.getRefererUrl());

//            6	orderid	商户自定义订单号	string(50)	必填。我们会据此判别是同一笔订单还是新订单。我们回调时，会带上这个参数。例：201710192541
            data.put("orderid", orderNo);

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[MMZF]M码支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data, int type) throws Exception {
        logger.info("[MMZF]M码支付生成支付签名串开始==================START========================");
        StringBuffer sb = new StringBuffer();
        if (type == 1) {
            //请求签名
//            key的拼接顺序：如用到了所有参数，就按这个顺序拼接： istype + notify_url + orderid + price + return_url + token + uid
                  sb.append(data.get("istype"))
                    .append(notifyUrl)
                    .append(data.get("orderid"))
                    .append(data.get("price"))
                    .append(data.get("return_url"))
                    .append(token)
                    .append(mchId);
        } else {
            //回调签名
//            orderid + platform_trade_no + price + realprice + token
                  sb.append(data.get("orderid"))
                    .append(data.get("platform_trade_no"))
                    .append(data.get("price"))
                    .append(data.get("realprice"))
                    .append(token);
        }
        //生成待签名串
        String singStr = sb.toString();
        logger.info("[MMZF]M码支付生成待签名串:" + singStr);
        //生成加密串
        String sign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
        logger.info("[MMZF]M码支付生成加密签名串:" + sign);
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
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[MMZF]M码支付回调请求参数:{}", infoMap);
        if (org.apache.commons.collections.MapUtils.isEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签，从配置中获取
        this.token = config.getString("token");
        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("orderid");
        // 第三方订单号
        String tradeNo = infoMap.get("platform_trade_no");
        //订单状态
        String tradeStatus = "success";
        // 表示成功状态
        String tTradeStatus = "success";
        //实际支付金额
        String orderAmount = infoMap.get("realprice");
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
        processNotifyVO.setPayment("MMZF");
        processNotifyVO.setConfig(config);
        
        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}
