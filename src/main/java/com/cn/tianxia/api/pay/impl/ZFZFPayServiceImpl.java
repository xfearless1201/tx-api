/******************************************************************
 *
 *    Powered By tianxia-online.
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技
 *    http://www.d-telemedia.com/
 *
 *    Package:     com.cn.tianxia.pay.impl
 *
 *    Filename:    BGPayServiceImpl.java
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
 *    Create at:   2019年05月07日 11:56
 *
 *    Revision:
 *
 *    2019/5/7 11:56
 *        - first revision
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.JSONUtils;
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
import java.util.HashMap;
import java.util.Map;

/**
 *  * @ClassName ZFZFPayServiceImpl
 *  * @Description TODO(掌付支付)
 *  * @Author Roman
 *  * @Date 2019年05月14日 17:30
 *  * @Version 1.0.0
 *  
 **/

public class ZFZFPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ZFZFPayServiceImpl.class);

    private static final String ret__failed = "Notify Is Failed";

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
    public ZFZFPayServiceImpl() {
    }

    public ZFZFPayServiceImpl(Map<String, String> data) {
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
            if (data.containsKey("queryOrderUrl")) {
                this.queryOrderUrl = data.get("queryOrderUrl");
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
            logger.info("[ZFZF]掌付支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));

            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[ZFZF]掌付支付扫码支付发起HTTP请求无响应");
                return PayResponse.error("[ZFZF]掌付支付扫码支付发起HTTP请求无响应");
            }
            //成功
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[ZFZF]掌付支付扫码支付响应参数：{}", jsonObject);
            if (jsonObject.containsKey("status") && "101".equals(jsonObject.getString("status"))) {
                String payUrl = jsonObject.getString("url");
                return PayResponse.sm_link(entity, payUrl, "下单成功");
            }
            return PayResponse.error("[ZFZF]掌付支付下单失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[ZFZF]掌付支付扫码支付异常" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[ZFZF]掌付支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


//          mchno	整数型	商户号,后台可以获取
            dataMap.put("mchno", merchId);

//			money	整数型	以元为单位,不能带小数
            dataMap.put("money", amount);

//			orderno	商户订单号	商户平台保证唯一
            dataMap.put("orderno", orderNo);

//			paytype	支付类型	见表1.6
            dataMap.put("paytype", entity.getPayCode());

//			notifyurl	异步回调地址	成功付款后回调通知地址
            dataMap.put("notifyurl", notifyUrl);

//            透传参数
            dataMap.put("attach", "top_Up)");

            //生成待签名串
            String sign = generatorSign(dataMap);
//            sign	签名
            dataMap.put("sign", sign);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[ZFZF]掌付支付封装请求参数异常:" + e.getMessage());
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
            sb.replace(sb.length() - 1, sb.length(), secret);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[ZFZF]掌付支付生成待加密串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[ZFZF]掌付支付生成签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[ZFZF]掌付支付生成支付签名串异常:" + e.getMessage());
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
        logger.info("[ZFZF]掌付支付回调验签开始=========================START===========================");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[ZFZF]掌付支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZFZF]掌付支付回调生成加密串异常:{}", e.getMessage());
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
        //参数验签，从配置中获取
        this.secret = config.getString("secret");
        this.merchId = config.getString("merchId");
        this.queryOrderUrl = config.getString("queryOrderUrl");
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[ZFZF]掌付支付回调请求参数报文:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("orderno");
        // 第三方订单号
        String tradeNo = infoMap.get("orderid");
        //订单状态
        String tradeStatus = String.valueOf(infoMap.get("status"));
        // 表示成功状态
        String tTradeStatus = "101";
        //实际支付金额
        String orderAmount = String.valueOf(infoMap.get("payamt"));
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        //查询订单信息
        String paytype = infoMap.get("paytype");
        boolean orderStatus = getOrderStatus(orderNo, orderAmount, paytype);
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
        processNotifyVO.setPayment("ZFZF");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }

    /**
     * 功能描述:查询订单状态
     *
     * @param orderNo 订单号
     * @return: boolean
     **/
    private boolean getOrderStatus(String orderNo, String amount, String payType) {
        try {
            //封装请求参数
            Map<String, String> map = new HashMap<>();
            map.put("mchno", merchId);
            map.put("orderno", orderNo);
            map.put("money", amount);
            map.put("paytype", payType);
            map.put("sign", generatorSign(map));

            logger.info("[ZFZF]掌付支付回调订单查询接口请求参数{}", JSONObject.fromObject(map));
            //发送请求
            String response = HttpUtils.toPostForm(map, queryOrderUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[ZFZF]掌付支付回调订单查询接口响应信息{}", respJson);
            if (JSONUtils.compare(respJson, "status", "101")) {
                logger.info("[ZFZF]掌付支付回调订单查询成功,订单" + orderNo + "已支付。");
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZFZF]掌付支付回调订单查询异常");
            return false;
        }
    }
}





