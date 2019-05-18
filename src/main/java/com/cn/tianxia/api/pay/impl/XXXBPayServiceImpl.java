/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    XXXBPayServiceImpl.java 
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
 *    Create at:   2019年04月01日 11:17 
 *
 *    Revision: 
 *
 *    2019/4/1 11:17 
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *  * @ClassName XXXBPayServiceImpl
 *  * @Description TODO(新小熊宝支付)
 *  * @Author Roman
 *  * @Date 2019年04月01日 11:17
 *  * @Version 1.0.0
 *  
 **/

public class XXXBPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(XXXBPayServiceImpl.class);

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
     * 接口调用方式
     */
    private String type;

    /**
     * 构造器，初始化参数
     */
    public XXXBPayServiceImpl() {
    }

    public XXXBPayServiceImpl(Map<String, String> data) {
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
            if (data.containsKey("type")) {
                this.type = data.get("type");
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
            logger.info("[XXXB]新小熊宝支付扫码支付请求参数报文:{}", data);

            String response = HttpUtils.generatorForm(data, payUrl);
            logger.info("[XXXB]新小熊宝支付扫码支付响应:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.info("[XXXB]新小熊宝支付扫码支付下单失败，无响应结果");
                return PayResponse.error("[XXXB]新小熊宝支付扫码支付下单失败，无响应结果");
            }
            return PayResponse.sm_form(entity, response, "扫码支付下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[XXXB]新小熊宝支付扫码支付下单异常" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[XXXB]新小熊宝支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();

//            type	接口调用方式	string	请必须填form 或者 json
            dataMap.put("type", type);

//            merchantId	商户uid	string	必填。您的商户唯一标识，注册后在商家管理后台获得。
            dataMap.put("merchantId", mchId);

//            money	订单金额	decimal	必填。用户支付的订单金额。请传入整数金额，不支持小数点
            dataMap.put("money", amount);

//            timestamp	时间戳	long	必填。精确到毫秒
            dataMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

//            notifyURL	回调地址	string(255)	必填。支付成功后系统会对该地址发起回调，通知支付成功的消息。
            dataMap.put("notifyURL", notifyUrl);

//            returnURL	支付结果展示地址	string(255)	必填。用户支付成功，将从支付页面跳转returnURL所在的页面。没有就传空字符
            dataMap.put("returnURL", entity.getRefererUrl());

//            merchantOrderId	商户自定义订单号	string(32)	必填。商户自定的订单号，该订单号将后在后台展示。
            dataMap.put("merchantOrderId", orderNo);

//            paytype	支付类型	string(32)	必填。默认为微信支付。选择参数：WX、QQ、ALIPAY、ALI_SOLID
            dataMap.put("paytype", entity.getPayCode());

            //生成待签名串
            String sign = generatorSign(dataMap, 1);
//            sign	签名	string(32)	必填。把参数和秘钥，按指定的顺序，用&符号连接在一起。做md5-32位加密，取字符串小写。得到key。（注意：&是必须存在的）ring/64	是	签名(MD5加密)
            dataMap.put("sign", sign);
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XXXB]新小熊宝支付封装请求参数异常:" + e.getMessage());
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

//            money&merchantId&notifyURL&returnURL&merchantOrderId&timestamp&密钥
            StringBuffer sb = new StringBuffer();
            if (type == 1) {
                sb.append(data.get("money")).append("&")
                        .append(data.get("merchantId")).append("&")
                        .append(data.get("notifyURL")).append("&")
                        .append(data.get("returnURL")).append("&")
                        .append(data.get("merchantOrderId")).append("&")
                        .append(data.get("timestamp")).append("&");
            } else if (type == 2) {
//            orderNo&merchantOrderNo&money&payAmount&密钥
                sb.append(data.get("orderNo")).append("&")
                        .append(data.get("merchantOrderNo")).append("&")
                        .append(data.get("money")).append("&")
                        .append(data.get("payAmount")).append("&");
            } else {
//                merchantOrderNo&merchantId&timestamp&密钥
                sb.append(data.get("merchantOrderNo")).append("&")
                        .append(data.get("merchantId")).append("&")
                        .append(data.get("timestamp")).append("&");
            }
            sb.append(key);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[XXXB]新小熊宝支付生成待签名串:" + signStr);
            //生成加密串
            String sign = Objects.requireNonNull(MD5Utils.md5toUpCase_32Bit(signStr)).toLowerCase();
            logger.info("[XXXB]新小熊宝支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XXXB]新小熊宝支付生成支付签名串异常:" + e.getMessage());
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
        logger.info("[XXXB]新小熊宝支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data, 2);
            logger.info("[XXXB]新小熊宝支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XXXB]新小熊宝支付回调生成加密串异常:{}", e.getMessage());
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
            map.put("merchantId", mchId);
            map.put("timestamp", String.valueOf(System.currentTimeMillis()));
            map.put("merchantOrderNo", orderNo);
            map.put("sign", generatorSign(map, 3));

            logger.info("[XXXB]新小熊宝支付订单查询接口订单请求参数{}", JSONObject.fromObject(map));
            //发送请求
            String response = HttpUtils.toPostForm(map, queryUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[XXXB]新小熊宝支付订单查询接口响应信息{}", respJson);
            if (respJson.containsKey("orderStatus") && "2".equals(respJson.getString("orderStatus"))) {
                logger.info("[XXXB]新小熊宝支付订单查询成功,订单" + orderNo + "已支付。");
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XXXB]新小熊宝支付订单查询异常");
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
        this.mchId = config.getString("mchId");
        this.queryUrl = config.getString("queryUrl");

        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[XXXB]新小熊宝支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }


        // 平台商订单号
        String orderNo = infoMap.get("merchantOrderNo");

        boolean verifyRequest = verifyCallback(infoMap);

        // 支付商订单号
        String tradeNo = infoMap.get("orderNo");
        //订单状态
        String tradeStatus = "success";
        // 表示成功状态
        String tTradeStatus = "success";
        //实际支付金额
        String orderAmount = infoMap.get("payAmount");
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        //调用查询接口查询订单信息
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
        processNotifyVO.setPayment("XXXB");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}


