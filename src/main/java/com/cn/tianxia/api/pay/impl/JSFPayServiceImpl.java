/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    JSFPayServiceImpl.java 
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
 *    Create at:   2019年04月14日 15:18 
 *
 *    Revision: 
 *
 *    2019/4/14 15:18 
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
 *  * @ClassName JSFPayServiceImpl
 *  * @Description TODO(聚闪付支付)
 *  * @Author Roman
 *  * @Date 2019年04月14日 15:18
 *  * @Version 1.0.0
 *  
 **/

public class JSFPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(JSFPayServiceImpl.class);

    private static final String ret__failed = "Notify is failed";

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
     * 查询地址
     */
    private String queryUrl;


    /**
     * 构造器，初始化参数
     */
    public JSFPayServiceImpl() {
    }

    public JSFPayServiceImpl(Map<String, String> data) {
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

            logger.info("[JSF]聚闪付支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));
            String response = HttpUtils.generatorForm(data, payUrl);
            logger.info("[JSF]聚闪付支付扫码支付发起HTTP请求响应结果:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.error("[JSF]聚闪付支付下单失败：生成请求form为空");
                PayResponse.error("[JSF]聚闪付支付下单失败：生成请求form为空");
            }
            return PayResponse.sm_form(payEntity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[JSF]聚闪付支付扫码支付下单失败" + e.getMessage());
        }
    }

    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[JSF]聚闪付支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        logger.info("[JSF]聚闪付支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data, 2);
            logger.info("[JSF]聚闪付支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JSF]聚闪付支付回调验签生成加密串异常:{}", e.getMessage());
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
        logger.info("[JSF]聚闪付支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            String orderNo = entity.getOrderNo();

//            uid	商户ID	string(50)	您的商户唯一标识，注册后在基本资料里获得	是	是
            data.put("uid", mchId);

//            price	金额	string(50)	单位：元。精确小数点后2位	是	是
            data.put("price", amount);
//            paytype	支付渠道	int	1：支付宝；4：微信； 持续更新中，详见开发者中心	是	是
            data.put("paytype", entity.getPayCode());

//            notify_url	异步回调地址	string(255)	用户支付成功后，我们服务器会主动发送一个post消息到这个网址。由您自定义。不要urlencode并且不带任何参数。例：http://www.xxx.com/notify_url	是	是
            data.put("notify_url", notifyUrl);

//            return_url	同步跳转地址	string(255)	用户支付成功后，我们会让用户浏览器自动跳转到这个网址。由您自定义。不要urlencode并且不带任何参数。例：http://www.xxx.com/return_url	是	是
            data.put("return_url", entity.getRefererUrl());

//            user_order_no	商户自定义订单号	string(50)	我们会据此判别是同一笔订单还是新订单。我们回调时，会带上这个参数。例：201010101041	是	是
            data.put("user_order_no", orderNo);

            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("sign", sign);

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JSF]聚闪付支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param type 1支付      2回调      3查询
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data, int type) throws Exception {
        logger.info("[JSF]聚闪付支付生成支付签名串开始==================START========================");
        StringBuffer sb = new StringBuffer();
        if (type == 1) {
//            uid + price + paytype + notify_url + return_url + user_order_no + token
            sb.append(data.get("uid"))
                    .append(data.get("price"))
                    .append(data.get("paytype"))
                    .append(data.get("notify_url"))
                    .append(data.get("return_url"))
                    .append(data.get("user_order_no"));
        } else if (type == 2) {
//           user_order_no + orderno + tradeno + price + realprice + token
            if (data.containsKey("tradeno")){
                sb.append(data.get("user_order_no"))
                        .append(data.get("orderno"))
                        .append(data.get("tradeno"))
                        .append(data.get("price"))
                        .append(data.get("realprice"));
            }else {
                sb.append(data.get("user_order_no"))
                        .append(data.get("orderno"))
                        .append(data.get("price"))
                        .append(data.get("realprice"));
            }
        } else {
            //uid+user_order_no+ orderno + token
            sb.append(data.get("uid"))
                    .append(data.get("user_order_no"));
        }
        //生成待签名串
        sb.append(key);
        String singStr = sb.toString();
        logger.info("[JSF]聚闪付支付生成待签名串:" + singStr);
        //生成加密串
        String sign = Objects.requireNonNull(MD5Utils.md5toUpCase_32Bit(sb.toString())).toLowerCase();
        logger.info("[JSF]聚闪付支付生成加密签名串:" + sign);
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

        logger.info("[JSF]聚闪付支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("user_order_no");
        // 第三方订单号
        String tradeNo = infoMap.get("orderno");
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
        //查询订单信息
        boolean orderStatus = getOrderStatus(orderNo);
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
        processNotifyVO.setPayment("JSF");

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
            map.put("uid", mchId);
            map.put("user_order_no", orderNo);
            map.put("sign", generatorSign(map, 3));

            logger.info("[JSF]聚闪付支付订单查询接口请求参数{}", JSONObject.fromObject(map));
            //发送请求
            String response = HttpUtils.toPostJsonStr(JSONObject.fromObject(map), queryUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[JSF]聚闪付支付订单查询接口响应信息{}", respJson);
            if (respJson.containsKey("status") && "3".equals(respJson.getString("status"))) {

                logger.info("[JSF]聚闪付支付订单查询成功,订单" + orderNo + "已支付。");
                return true;

            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JSF]聚闪付支付订单查询异常");
            return false;
        }
    }
}


