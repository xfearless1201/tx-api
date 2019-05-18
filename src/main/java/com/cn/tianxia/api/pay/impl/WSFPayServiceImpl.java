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
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName YHWPayServiceImpl
 * @Description yhh 对接 银河微扫码支付支付渠道：QQ 、微信、支付宝（PC、手机扫码）
 * @Date 2019/3/7 15 14
 **/
public class WSFPayServiceImpl extends PayAbstractBaseService implements PayService {
    protected static final Logger logger = LoggerFactory.getLogger(WSFPayServiceImpl.class);

    public String mch_id;
    public String key;
    public String payUrl;
    public String notifyUrl;
    public String notifyIp;

    private String ret__success = "success";
    private String ret__failed = "fail";

    public WSFPayServiceImpl() {
    }

    public WSFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("mch_id")) {
                this.mch_id = data.get("mch_id");
            }
            if (data.containsKey("key")) {
                this.key = data.get("key");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("notifyIp")) {
                this.notifyIp = data.get("notifyIp");
            }
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
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[WSF]微扫付支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签，从配置中获取
        this.key = config.getString("key");
        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("orderid");
        // 第三方订单号
        String tradeNo = "WSF" + System.currentTimeMillis();
        //订单状态
        String tradeStatus = infoMap.get("opstate");
        // 表示成功状态
        String tTradeStatus = "1";
        //实际支付金额
        String orderAmount = infoMap.get("ovalue");
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
        processNotifyVO.setPayment("WSF");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }

    /**
     * 银联支付
     *
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    /**
     * 扫码支付
     *
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            logger.info("[WSF]银河微扫付支付  扫码支付开始==========================start=======================================");
            Map<String, String> dataMap = sealRequest(payEntity);
            String sign = generatorSign(dataMap);
            dataMap.put("sign", sign);
            String response = HttpUtils.generatorFormGet(dataMap, payUrl);
            logger.info("[WSF]银河微扫付支付  发起http请求，响应结果：{}", response);
            if (StringUtils.isEmpty(response)) {
                logger.info("发起HTTP请求异常");
                PayResponse.error("发起HTTP请求异常");
            }
            return PayResponse.sm_form(payEntity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[WSF]银河微扫付支付  扫码支付异常：" + e.getMessage());
        }

    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[WSF]微扫付支付回调验签开始==============START===========");
        String sourceSign = data.get("sign");
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[WSF]微扫付支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WSF]微扫付支付回调生成加密串异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
    }

    public String generatorSign(Map<String, String> dataMap) {

        try {
            StringBuffer sb = new StringBuffer();
            Map<String, String> sortMap = MapUtils.sortByKeys(dataMap);
            for (String key : sortMap.keySet()) {
                String value = sortMap.get(key);
                if (StringUtils.isBlank(value) || "sign".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(value).append("&");
            }
            URLDecoder.decode(sb.toString(), "UTF-8");
            sb.append("key=").append(key);
            logger.info("[WSF]银河微扫付支付生成待加密串：{}", sb.toString());
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
            logger.info("[WSF]银河微扫付支付生成加密串：{}", sign);

            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public Map<String, String> sealRequest(PayEntity payEntity) {

        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());//订单金额，单位为元

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("parter", mch_id);//接口调用ID
        dataMap.put("value", amount);//金额，元为单位
        dataMap.put("type", payEntity.getPayCode());//支付类型：wx=微信,wxwap=微信WAP,ali=支付宝,aliwap=支付宝WAP,qq=QQ,qqwap=QQWAP
        dataMap.put("orderid", payEntity.getOrderNo());//商家订单号
        dataMap.put("notifyurl", notifyUrl);//异步通知地址
        dataMap.put("ip", payEntity.getIp());//客户端ip
        dataMap.put("sendtime", String.valueOf(System.currentTimeMillis()));//订单提交时间,时间戳类型
        dataMap.put("callbackurl", payEntity.getRefererUrl());//支付成功后跳转到该页面
        return dataMap;
    }
}
