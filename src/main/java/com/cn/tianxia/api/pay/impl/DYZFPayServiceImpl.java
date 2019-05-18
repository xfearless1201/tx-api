/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    DYPayServiceImpl.java 
 *
 *    Description: 店员支付
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:     roman
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年02月15日 15:33 
 *
 *    Revision: 
 *
 *    2019/2/15 15:33 
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
 *  * @ClassName DYPayServiceImpl
 *  * @Description 店员支付
 *  * @Author roman
 *  * @Date 2019年02月15日 15:33
 *  * @Version 1.0.0
 *  
 **/
public class DYZFPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DYZFPayServiceImpl.class);

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
     * 订单查询地址
     */
    private String queryUrl;

    /**
     * 密钥
     */
    private String key;

    /**
     * 构造器，初始化参数
     */

    public DYZFPayServiceImpl() {
    }

    public DYZFPayServiceImpl(Map<String, String> data) {
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
        logger.info("[DYZF]店员支付扫码支付开始================START============");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);

            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("sign", sign);

            logger.info("[DYZF]店员支付扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());
            logger.info("请求地址：" + payUrl);
            //发起HTTP请求
            String response = HttpUtils.generatorForm(data, payUrl);
            logger.info("[DYZF]店员支付扫码支付发起HTTP请求响应结果:{}", response);
            if (StringUtils.isBlank(response) || response.contains("FAIL")) {
                logger.info("[DYZF]店员支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[DYZF]店员支付扫码支付发起HTTP请求无响应结果");
            }

            return PayResponse.sm_form(payEntity, response, "扫码支付下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[DYZF]店员支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[DYZF]店员支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[DYZF]店员支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.get("sign");
        logger.info("[DYZF]店员支付回调验签获取原签名串:{}", sourceSign);
        String sign = null;
        try {
            sign = generatorSign(data, 0);
            logger.info("[DYZF]店员支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[DYZF]店员支付回调验签异常:{}", e.getMessage());
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
        logger.info("[DYZF]店员支付组装支付请求参数开始==============START==================");
        try {
            //创建参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            String orderNo = entity.getOrderNo();
            // 商户号
            dataMap.put("shopAccountId", mchId);

            // 商家⽤户ID 否
            dataMap.put("shopUserId", entity.getuId());

            //订单⾦额，单位元，如：0.01表示⼀分钱；
            dataMap.put("amountInString", amount);

            // 支付方式的标识
            dataMap.put("payChannel", entity.getPayCode());

            //商家订单号，⻓度不超过40
            dataMap.put("shopNo", orderNo);


            //支付完成后结果通知url,支付成功回调路径；json格式post提交商户后台
            dataMap.put("shopCallbackUrl", notifyUrl);

            //支付方IP ⼆维码扫码⽀付模式下：⽀付成功页⾯‘返回商家端’按钮点击后的跳转地址; 如果商家采⽤⾃有界⾯，则忽略该参数；
            dataMap.put("returnUrl", entity.getRefererUrl());

            //  跳转⽅式 1，⼿机跳转 2、⼆维码展示
            if (StringUtils.isBlank(entity.getMobile())) {
                dataMap.put("target", "2");
            } else {
                dataMap.put("target", "1");
            }


            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[DYZF]店员支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[DYZF]店员支付组装支付请求参数异常");
        }
    }

    /**
     * @param type 0:回调      1:支付       2:查询
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(Map<String, String> data, int type) throws Exception {
        StringBuffer sb = new StringBuffer();
        if (type == 1) {

//     请求签名       MD5（shopAccountId + shopUserId + amountInString + shopNo + payChannel + KEY）
            sb.append(data.get("shopAccountId")).append(data.get("shopUserId")).append(data.get("amountInString")).
                    append(data.get("shopNo")).append(data.get("payChannel")).append(key);
        } else if (type == 0) {
//     回调签名       MD5（shopAccountId + shopUserId + trade_no +KEY+money+type）
            sb.append(mchId).append(data.get("user_id")).append(data.get("trade_no")).
                    append(key).append(data.get("money")).append(data.get("type"));
        } else {
            sb.append(data.get("shop_id")).append(data.get("order_no")).append(key);
        }
        //生成待签名串
        String signStr = sb.toString();
        logger.info("[DYZF]店员支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
        logger.info("[DYZF]店员支付生成加密签名串:{}", sign);
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

        logger.info("[DYZF]店员支付回调请求参数:{}", infoMap);
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(infoMap);
        // 平台订单号
        String orderNo = infoMap.get("shop_no");
        // 第三方订单号
        String tradeNo = infoMap.get("trade_no");
        //订单状态
        String tradeStatus = infoMap.get("status");
        // 表示成功状态
        String tTradeStatus = "0";
        //实际支付金额
        String orderAmount = infoMap.get("money");
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //查询订单信息
        boolean orderStatus = getOrderStatus(tradeNo);
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
        processNotifyVO.setPayment("DYZF");
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
            map.put("order_no", orderNo);
            map.put("shop_id", mchId);
            map.put("sign", generatorSign(map, 2));

            //发送请求
            String response = HttpUtils.toPostJsonStr(JSONObject.fromObject(map), queryUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[DYZF]店员支付订单查询接口响应信息{}", respJson);
            if (respJson.containsKey("status") && "0".equals(respJson.getString("status"))) {
                logger.info("[DYZF]店员支付订单查询成功,订单" + respJson.getString("shop_no") + "已支付。");
                return true;

            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[DYZF]店员支付订单查询异常");
            return false;
        }
    }
}
