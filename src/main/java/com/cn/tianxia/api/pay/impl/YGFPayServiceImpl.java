/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    YGFPayServiceImpl.java 
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
 *    Create at:   2019年05月03日 16:01 
 *
 *    Revision: 
 *
 *    2019/5/3 16:01 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.domain.txdata.v2.RechargeDao;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.project.v2.RechargeEntity;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.JSONUtils;
import com.cn.tianxia.api.utils.SpringContextUtils;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.utils.pay.RandomUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 *  * @ClassName YGFPayServiceImpl
 *  * @Description TODO(勇哥支付)
 *  * @Author Roman
 *  * @Date 2019年05月03日 16:01
 *  * @Version 1.0.0
 *  
 **/

public class YGFPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(YGFPayServiceImpl.class);

    private static final String ret__failed = "Notify is failed";

    private static final String ret__success = "success";

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
     * 订单查询地址
     */
    private String queryOrderUrl;


    /**
     * 构造器，初始化参数
     */
    public YGFPayServiceImpl() {
    }

    public YGFPayServiceImpl(Map<String, String> data, String type) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey(type)) {
                JSONObject jsonObject = JSONObject.fromObject(data.get(type));
                if (jsonObject.containsKey("merchId")) {
                    this.merchId = jsonObject.getString("merchId");
                }
                if (jsonObject.containsKey("payUrl")) {
                    this.payUrl = jsonObject.getString("payUrl");
                }
                if (jsonObject.containsKey("notifyUrl")) {
                    this.notifyUrl = jsonObject.getString("notifyUrl");
                }
                if (jsonObject.containsKey("secret")) {
                    this.secret = jsonObject.getString("secret");
                }
                if (jsonObject.containsKey("queryOrderUrl")) {
                    this.queryOrderUrl = jsonObject.getString("queryOrderUrl");
                }
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
        logger.info("[YGF]勇哥支付扫码支付开始============START======================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(entity);
            logger.info("[YGF]勇哥支付扫码支付请求参数:{}", JSONObject.fromObject(data));

            //发送请求
            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[YGF]勇哥支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[YGF]勇哥支付扫码支付发起HTTP请求无响应结果");
            }
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[YGF]勇哥支付扫码支付响应:{}", jsonObject);
            if (jsonObject.containsKey("code") && "0".equals(jsonObject.getString("code"))) {
                //下单成功
                String payurl = jsonObject.getString("qr_code");

                return PayResponse.sm_link(entity, payurl, "扫码支付下单成功");
            }
            return PayResponse.error("下单失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[YGF]勇哥支付扫码支付下单失败" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[YGF]勇哥支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new TreeMap<>();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


//            service	接口名称	是	String(32)	pay.alipay.trade.precreate   支付宝 pay.weixin.scan.trade.precreate   微信
            dataMap.put("service", entity.getPayCode()+".trade.precreate");

//            mch_id	商户编号	是	String(32)	平台提供的商户编号
            dataMap.put("mch_id", merchId);

//            nonce_str	随机字符串	是	String(32)	随机字符串，
            dataMap.put("nonce_str", RandomUtils.generateLowerString(18));

//            out_trade_no	商户订单号	是	String(32)	商户系统内部的订单号 ,32个字符内、 可包含字母,确保在商户系统唯一
            dataMap.put("out_trade_no", orderNo);

//            body	商品的描述	是	String(127)	商品描述
            dataMap.put("body", "top_Up");

//            total_fee	总金额	是	Int	总金额，以分为单位，不允许包含任何字、符号
            dataMap.put("total_fee", amount);

//            spbill_create_ip	终端IP	是	String(16)	订单生成的机器 IP
            dataMap.put("spbill_create_ip", entity.getIp());

//            return_url	返回URL	否	String(255)	支付完成后返回的URL
            dataMap.put("return_url", entity.getRefererUrl());

//            notify_url	通知地址	是	String(255)	接收平台通知的URL，需给绝对路径，255字符内格式如:http://wap.mch.com/tenpay.asp，确保平台能通过互联网访问该地址
            dataMap.put("notify_url", notifyUrl);


//            sign	签名	是	String(32)	查看安全规范

            //以上字段参与签名,生成待签名串
            String sign = generatorSign(dataMap);
            dataMap.put("sign", sign);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YGF]勇哥支付封装请求参数异常:" + e.getMessage());
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
        logger.info("[YGF]勇哥支付生成支付签名串开始==================START========================");
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

            logger.info("[YGF]勇哥支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[YGF]勇哥支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YGF]勇哥支付生成支付签名串异常:" + e.getMessage());
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
        logger.info("[YGF]勇哥支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[YGF]勇哥支付生成签名串：{}--源签名串：{}", sign, sourceSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YGF]勇哥支付生成加密串异常:{}", e.getMessage());
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

        logger.info("[YGF]勇哥支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        // 平台订单号
        String orderNo = infoMap.get("out_trade_no");
        //参数验签，从配置中获取
        RechargeDao rechargeDao = (RechargeDao) SpringContextUtils.getBeanByClass(RechargeDao.class);
        RechargeEntity rechargeEntity = rechargeDao.selectByOrderNo(orderNo);
        String type = getPayConfigType(String.valueOf(rechargeEntity.getPayType()));//获取支付类型
        config = config.getJSONObject(type);
        logger.info("[YGF]勇哥支付获取商家配置:{}",JSONObject.fromObject(config));
        this.secret = config.getString("secret");
        this.queryOrderUrl = config.getString("queryOrderUrl");
        this.merchId = config.getString("merchId");
        boolean verifyRequest = verifyCallback(infoMap);

        // 第三方订单号
        String tradeNo = infoMap.get("transaction_id");
        //订单状态
        String tradeStatus = "1";
        // 表示成功状态
        String tTradeStatus = "1";
        //实际支付金额
        String orderAmount = infoMap.get("total_fee");
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
        processNotifyVO.setPayment("YGF");
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
            map.put("service", "pay.trade.query");
            map.put("mch_id", merchId);
            map.put("nonce_str", RandomUtils.generateLowerString(18));
            map.put("out_trade_no", orderNo);
            map.put("sign", generatorSign(map));

            logger.info("[YGF]勇哥支付回调订单查询接口请求参数{}", JSONObject.fromObject(map));
            //发送请求
            String response = HttpUtils.toPostForm(map, queryOrderUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[YGF]勇哥支付回调订单查询接口响应信息{}", respJson);
            if (respJson.containsKey("code") && "0".equals(respJson.getString("code"))) {
                if (JSONUtils.compare(respJson, "trade_state", "1") ||
                        JSONUtils.compare(respJson, "trade_state", "2")) {
                    logger.info("[YGF]勇哥支付回调订单查询成功,订单" + orderNo + "已支付。");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YGF]勇哥支付回调订单查询异常");
            return false;
        }
    }
}



