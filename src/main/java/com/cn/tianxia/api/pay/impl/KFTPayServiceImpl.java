package com.cn.tianxia.api.pay.impl;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 * @author Vicky
 * @version 1.2.0
 * @ClassName KFTPayServiceImpl
 * @Description 快付支付2  新接支付
 * @Date 2019/3/14 09 14
 **/
public class KFTPayServiceImpl extends PayAbstractBaseService implements PayService {
    protected static final Logger logger = LoggerFactory.getLogger(KFTPayServiceImpl.class);
    private static String ret__success = "success";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    public String merchantNo;//商户id
    public String key;//密钥
    public String notifyUrl;//回调地址
    public String payUrl;//支付地址
    public String tokenUrl;//获取token地址
    public String searchOrderUrl;//订单查询地址
    public String time = new SimpleDateFormat("yyyyMMdd").format(new Date());//时间戳
    public String nonce = UUID.randomUUID().toString().substring(0, 8);//随机字符
    RestTemplate restTemplate = new RestTemplate();
    private boolean verifySuccess = true;//回调验签默认状态为true

    public KFTPayServiceImpl() {
    }

    public KFTPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merchantNo")) {
                this.merchantNo = data.get("merchantNo");
            }
            if (data.containsKey("key")) {
                this.key = data.get("key");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("tokenUrl")) {
                this.tokenUrl = data.get("tokenUrl");
            }
            if (data.containsKey("searchOrderUrl")) {
                this.searchOrderUrl = data.get("searchOrderUrl");
            }
        }
    }

    /**
     * 回调
     *
     * @param request
     * @param response
     * @param config
     * @return
     */
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        logger.info("[KFT]快付支付2 支付回调开始======================START==================");
        this.key = config.getString("key");
        this.notifyUrl = config.getString("notifyUrl");
        this.searchOrderUrl = config.getString("searchOrderUrl");
        this.merchantNo = config.getString("merchantNo");
        this.tokenUrl = config.getString("tokenUrl");
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[KFT]快付支付2 商户返回信息====" + dataMap);

        String trade_no = dataMap.get("no");   //第三方订单号，流水号,客户平台生成的只有一个订单号，可以认为和流水号是同一个
        String order_no = dataMap.get("outTradeNo");  //我司支付订单号
        if (StringUtils.isBlank(order_no)) {
            logger.info("[KFT]快付支付2  获取的 订单号为空");
            return "获取的订单号为空";
        }

        //订单查询
        Map<String, String> searchOrderMap = new HashMap<>();
        searchOrderMap.put("accessToken", getToken());
        searchOrderMap.put("param", order_no);
        Map<String, Object> resultMap = restTemplate.postForObject(searchOrderUrl, searchOrderMap, Map.class);
        logger.info("[KFT]快付支付2 回调 订单查询结果:" + resultMap.get("value"));
        boolean flag =(boolean) resultMap.get("success");

        if (flag) {
            logger.info("[KFT]快付支付2 回调 订单查询value的参数:" + resultMap.get("value"));
            //返回参数解析
            JSONObject info = JSONObject.fromObject(dataMap);

            String amount = dataMap.get("money");//商户订单总金额，订单总金额以元为单位，精确到小数点后两位
            String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);  //回调ip
            String trade_status = info.getString("success");  //第三方支付状态
            String t_trade_status = null;
            if ("true".equals(trade_status)) {
                t_trade_status = "true";   //第三方成功状态
            }
            //判断订单金额是否为空
            if (StringUtils.isBlank(amount)) {
                logger.info("[KFT]快付支付2 回调订单金额为空");
                return ret__failed;
            }
            ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
            processNotifyVO.setOrder_no(order_no);
            processNotifyVO.setRealAmount(Double.parseDouble(amount) / 100);
            processNotifyVO.setIp(ip);
            processNotifyVO.setTrade_no(trade_no);
            processNotifyVO.setTrade_status(trade_status);
            processNotifyVO.setRet__failed(ret__failed);
            processNotifyVO.setRet__success(ret__success);
            processNotifyVO.setInfoMap(JSONObject.fromObject(dataMap).toString());
            processNotifyVO.setT_trade_status(t_trade_status);
            processNotifyVO.setConfig(config);
            processNotifyVO.setPayment("KFT");

            //回调验签
            if (!"success".equals(callback(dataMap))) {
                verifySuccess = false;
                logger.info("[KFT]快付支付2 回调验签失败");
                return "fail";
            }

            return processSuccessNotify(processNotifyVO, verifySuccess);
        }
        return "订单未支付或支付失败";
    }


    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accessToken", getToken());
            jsonObject.put("param", sealRequest(payEntity));
            Map<String, Object> resultMap = restTemplate.postForObject(payUrl + payEntity.getPayCode(), jsonObject, Map.class);
            logger.info("[KFT]快付支付2  支付请求  响应：" + resultMap);
            String url = String.valueOf(resultMap.get("value"));
            return PayResponse.sm_link(payEntity, url, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[KFT]快付支付2   扫码支付 异常");
        }
    }


    /**
     * 回调验签
     *
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {
        String sign = generatorSign(2, data);
        String sourceSign = data.remove("sign");
        if (sign.equals(sourceSign)) {
            return "success";
        }
        return "fail";
    }

    /**
     * 生成签名
     *
     * @param
     * @return
     */
    public String generatorSign(int type, Map<String, String> dataMap) {
        //大写,
        //token 为空时实例：
        //merchantNo=XM000000000000001&nonce=xxx&timestamp=111&key=9ae76c1d28254ff2a289942dc85700a9
        try {
            StringBuffer sb = new StringBuffer();
            if (1 == type) {
                sb.append("merchantNo=").append(merchantNo).append("&");//
                sb.append("nonce=").append(nonce).append("&");//
                sb.append("timestamp=").append(time).append("&");//
                sb.append("key=").append(key);//
            } else if (2 == type) {
                sb.append("merchantNo=").append(dataMap.get("merchantNo")).append("&");//
                sb.append("no=").append(dataMap.get("no")).append("&");
                sb.append("nonce=").append(dataMap.get("nonce")).append("&");//
                sb.append("timestamp=").append(dataMap.get("timestamp")).append("&");//
                sb.append("key=").append(key);//
            }
            logger.info("[KFT]快付支付2  生成的签名前的参数：" + sb.toString());
            return MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 获取token
     *
     * @return
     */
    public String getToken() {
        try {
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("merchantNo", merchantNo);//商户号
            dataMap.put("nonce", nonce);//随机字符（与获取签名时的保持一致）
            dataMap.put("timestamp", time);//时间戳（与获取签名时的保持一致）
            String sign = generatorSign(1, dataMap);
            dataMap.put("sign", sign);//签名
            logger.info("[KFT]快付支付2 获取accessToken 的请求参数： " + dataMap);
            Map<String, Object> resultMap = restTemplate.postForObject(tokenUrl, dataMap, Map.class);
            if (!resultMap.containsKey("success")) {
                System.out.println("错误！");
            }
            if ((boolean) resultMap.get("success")) {
                Map<String, Object> valueMap = (Map<String, Object>) resultMap.get("value");
                String accessToken = (String) valueMap.get("accessToken");
                logger.info("[KFT]快付支付2 获取accessToken " + accessToken);
                return accessToken;
            }
            return "获取Token失败！";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 参数组装
     *
     * @param entity
     * @return
     */
    // public JSONObject sealRequest(PayEntity entity){
    public JSONObject sealRequest(PayEntity entity) {
        JSONObject jsonObject = new JSONObject();
        //String amount = new DecimalFormat().format();
        long amount = (long) (entity.getAmount() * 100);
        jsonObject.put("outTradeNo", entity.getOrderNo());//商户订单号
        jsonObject.put("money", amount);//金额(分)
        jsonObject.put("type", "T0");//T0/T1付款类型
        jsonObject.put("body", "TOP-UP");//商品描述
        jsonObject.put("detail", "pay");//商品详情
        jsonObject.put("notifyUrl", notifyUrl);//后台通知地址
        jsonObject.put("successUrl", entity.getRefererUrl());//商品ID
        jsonObject.put("callbackSuccessUrl", entity.getRefererUrl());//商品ID
        jsonObject.put("productId", "123456");//商品ID
        return jsonObject;
    }

}
