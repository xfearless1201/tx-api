package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.JSONUtils;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName BSPayServiceImpl
 * @Description 百盛支付
 * @Date 2018年9月28日 下午2:20:43
 */
public class BSPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(BSPayServiceImpl.class);
    /**
     * 回调失败响应信息
     */
    private static final String ret__failed = "fail";
    /**
     * 回调成功响应信息
     */
    private static final String ret__success = "200";
    /**
     * 商户号
     */
    private String merchId;
    /**
     * 秘钥
     */
    private String secret;
    /**
     * 回调地址
     */
    private String notifyUrl;
    /**
     * 支付地址
     */
    private String payUrl;
    /**
     * 订单查询地址
     */
    private String queryOrderUrl;

    public BSPayServiceImpl() {
    }

    public BSPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merchId")) {
                this.merchId = data.get("merchId");
            }
            if (data.containsKey("secret")) {
                this.secret = data.get("secret");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("queryOrderUrl")) {
                this.queryOrderUrl = data.get("queryOrderUrl");
            }
        }
    }

    /**
     * @param payEntity
     * @return
     * @Description 网银支付
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    /**
     * @param payEntity
     * @return
     * @Description 扫码支付
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[BS]百盛扫码扫码支付开始===============start================");
        try {
            Map<String, String> data = sealRequest(payEntity);
            logger.info("[BS]百盛扫码支付请求参数：{}", JSONObject.fromObject(data));
            String resStr = null;
            if (StringUtils.isNotBlank(payEntity.getMobile()) && "3".equals(payEntity.getPayType())) {
                resStr = HttpUtils.generatorForm(data, payUrl);
                return PayResponse.sm_form(payEntity, resStr, "下单成功");
            } else {
                resStr = HttpUtils.toPostForm(data, payUrl);
            }
            logger.info("[BS]百盛扫码支付响应信息：{}", JSONObject.fromObject(resStr));
            if (StringUtils.isBlank(resStr)) {
                logger.error("[BS]百盛扫码支付发起HTTP请求无响应结果!");
                return PayResponse.error("[BS]百盛扫码支付发起HTTP请求无响应结果!");
            }
            logger.info("[BS]百盛扫码支付发起HTTP请求响应结果:" + resStr);
            //解析响应结果
            JSONObject resJson = JSONObject.fromObject(resStr);
            if (JSONUtils.compare(resJson, "Code", "200")) {
                if ("3".equals(payEntity.getPayType())) {
                    return PayResponse.sm_link(payEntity, resJson.getString("QrCodeUrl"), "下单成功");
                }
                return PayResponse.sm_qrcode(payEntity, resJson.getString("QrCodeUrl"), "下单成功");
            }
            return PayResponse.error("[BS]百盛扫码支付下单失败:" + resJson.getString("Message"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BS]百盛扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[BS]百盛扫码支付扫码支付异常");
        }
    }


    /**
     * @param data
     * @return
     * @Description 回调验签
     */
    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String amount = new DecimalFormat("##").format(entity.getAmount() * 100);
        Map<String, String> data = new HashMap<>();
        data.put("MerchantId", merchId);//商户号
        data.put("Timestamp", sdf.format(new Date()));//请求时间
        data.put("PaymentTypeCode", entity.getPayCode());//入款类型
        data.put("OutPaymentNo", entity.getOrderNo());//订单号
        data.put("PaymentAmount", amount);//入款金额，单位为分
        data.put("NotifyUrl", notifyUrl);//回调地址
        data.put("PassbackParams", entity.getRefererUrl());//通知地址
        data.put("Sign", generatorSign(data));//通知地址
        return data;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生产签名串
     */
    private String generatorSign(Map<String, String> data) throws Exception {
        try {
            StringBuffer sb = new StringBuffer();
            Map<String, String> map = new TreeMap<>(data);
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = map.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                sb.append(key).append("=").append(val).append("&");
            }
            sb.deleteCharAt(sb.length() - 1).append(secret);
            String signStr = sb.toString();
            logger.info("[BS]百盛扫码支付生成待签名串:" + signStr);
            String sign = MD5Utils.md5(signStr.getBytes());
            logger.info("[BS]百盛扫码支付生成MD5加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[BS]百盛扫码支付生产签名串异常:" + e.getMessage());
            throw new Exception("生产MD5签名串失败!");
        }
    }

    /**
     * 订单查询接口
     *
     * @param orderNo
     * @return
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    public boolean serchOrder(String orderNo) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map<String, String> param = new HashMap<>();
            param.put("MerchantId", merchId);//商户号
            param.put("OutPaymentNo", orderNo);//商户订单号
            param.put("Timestamp", sdf.format(new Date()));//
            param.put("Sign", generatorSign(param));
            logger.info("[BS]百盛扫码支付回调查询订单{}请求参数：{}", orderNo, JSONObject.fromObject(param));
            String resStr = HttpUtils.toPostForm(param, queryOrderUrl);
            logger.info("[BS]百盛扫码支付回调查询订单{}响应信息：{}", orderNo, JSONObject.fromObject(resStr));
            if (StringUtils.isBlank(resStr)) {
                logger.info("[BS]百盛扫码支付回调查询订单发起HTTP请求无响应,订单号{}", orderNo);
                return false;
            }
            JSONObject resJson = JSONObject.fromObject(resStr);
            if (!"200".equals(resJson.getString("Code"))) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BS]百盛扫码支付回调查询订单{}异常{}", orderNo, e.getMessage());
            return false;
        }

    }

    /**
     * 回调验签
     *
     * @param data
     * @return
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    private boolean verifyCallback(Map<String, String> data) {
        try {
            String sourceSign = data.get("Sign");
            String sign = generatorSign(data);
            logger.info("[BS]百盛扫码支付生成签名串：{}--源签名串：{}", sign, sourceSign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BS]百盛扫码支付回调生成签名串异常{}", e.getMessage());
            return false;
        }
    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);  //获取回调请求参数
        logger.info("[BS]百盛扫码支付回调请求参数：" + JSONObject.fromObject(infoMap));
        if (MapUtils.isEmpty(infoMap)) {
            logger.error("BSNotify获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.secret = config.getString("secret");//从配置中获取
        this.merchId = config.getString("merchId");//从配置中获取
        this.queryOrderUrl = config.getString("queryOrderUrl");//从配置中获取

        String order_amount = infoMap.get("PaymentAmount");//单位：分
        if (StringUtils.isBlank(order_amount)) {
            logger.info("BSNotify获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount);
        String order_no = infoMap.get("OutPaymentNo");// 平台订单号
        String trade_no = infoMap.get("PaymentNo");// 第三方订单号
        String trade_status = infoMap.get("PaymentState");
        String t_trade_status = "S";// 表示成功状态

        /**订单查询*/
        if (!serchOrder(order_no)) {
            logger.info("[BS]百盛扫码支付回调查询订单{}失败", order_no);
            return ret__failed;
        }
        /**回调验签*/
        boolean verifyRequest = verifyCallback(infoMap);

        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setRet__success(ret__success);    //成功返回
        processNotifyVO.setRet__failed(ret__failed);      //失败返回
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);    //支付状态
        processNotifyVO.setT_trade_status(t_trade_status);     //第三方成功状态
        processNotifyVO.setRealAmount(realAmount / 100);
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());    //回调参数
        processNotifyVO.setPayment("BS");
        processNotifyVO.setConfig(config);
        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}
