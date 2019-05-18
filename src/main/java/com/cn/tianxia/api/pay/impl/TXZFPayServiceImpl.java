package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
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

public class TXZFPayServiceImpl extends PayAbstractBaseService implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(TXZFPayServiceImpl.class);
    /**
     * 回调失败响应信息
     */
    private static final String ret__failed = "fail";
    /**
     * 回调成功响应信息
     */
    private static final String ret__success = "success";
    /**
     * 支付key
     */
    private String payKey;
    /**
     * 秘钥
     */
    private String paySecret;
    /**
     * 回调地址
     */
    private String notifyUrl;
    /**
     * 支付地址
     */
    private String payUrl;
    /**
     * 查询地址
     */
    private String searchOrderUrl;
    /**
     * 网银产品类型
     */
    private String bankProductType;

    private String wyPayUrl;

    public TXZFPayServiceImpl() {
    }

    public TXZFPayServiceImpl(Map<String, String> pmap) {
        if (MapUtils.isNotEmpty(pmap)) {
            if (pmap.containsKey("payKey")) {
                payKey = pmap.get("payKey");
            }
            if (pmap.containsKey("paySecret")) {
                paySecret = pmap.get("paySecret");
            }
            if (pmap.containsKey("notifyUrl")) {
                notifyUrl = pmap.get("notifyUrl");
            }
            if (pmap.containsKey("payUrl")) {
                payUrl = pmap.get("payUrl");
            }
            if (pmap.containsKey("searchOrderUrl")) {
                searchOrderUrl = pmap.get("searchOrderUrl");
            }
            if (pmap.containsKey("bankProductType")) {
                bankProductType = pmap.get("bankProductType");
            }
            if (pmap.containsKey("wyPayUrl")) {
                wyPayUrl = pmap.get("wyPayUrl");
            }
        }
    }

    /**
     * 网银支付
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[TXZF]天天支付网银支付开始================START======================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity, "1");
            logger.info("[TXZF]天天支付生成网银支付请求报文:{}", JSONObject.fromObject(data));
            //生成请求表单
            String resStr = HttpUtils.generatorForm(data, wyPayUrl);
            logger.info("[TXZF]天天支付生成网银支付响应信息:{}", resStr);
            return PayResponse.wy_form(payEntity.getPayUrl(), resStr);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TXZF]天天支付网银支付异常:{}", e.getMessage());
            return PayResponse.error("[TXZF]天天支付网银支付异常");
        }
    }


    /**
     * 扫码支付
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[TXZF]天天支付扫码支付开始=========================START===============================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity, "0");
            logger.info("[TXZF]天天支付生成扫码支付请求报文:{}", JSONObject.fromObject(data).toString());
            // 快捷支付
            if ("7".equals(payEntity.getPayType())) {
                String formStr = HttpUtils.generatorForm(data, wyPayUrl);
                logger.info("[TXZF]天天支付生成form表单请求结果:{}", formStr);
                return PayResponse.sm_form(payEntity, formStr, "下单成功");
            }
            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.error("[TXZF]天天支付发起HTTP请求无响应结果!");
                return PayUtil.returnPayJson("error", "2", "[TXZF]天天支付发起HTTP无响应结果!", "", 0, "", response);
            }
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[TXZF]天天支付发起HTTP响应结果:" + jsonObject);
            if (JSONUtils.compare(jsonObject, "resultCode", "0000")) {
                String payMessage = jsonObject.getString("payMessage");
                if ("2".equals(payEntity.getPayType())) {
                    return PayResponse.sm_link(payEntity, payMessage, "下单成功");
                }
                if (StringUtils.isNotBlank(payEntity.getMobile())) {
                    return PayResponse.sm_link(payEntity, payMessage, "下单成功");
                }
                return PayResponse.sm_qrcode(payEntity, payMessage, "下单成功");
            }
            return PayResponse.error("[TXZF]天天支付下单失败" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TXZF]天天支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[TXZF]天天支付扫码支付异常");
        }

    }

    /**
     * 回调
     */
    @Override
    public String callback(Map<String, String> infoMap) {
        String sourceSign = infoMap.remove("sign");
        String localSign = generatorSign(infoMap);
        logger.info("[TXZF]天天支付回调生成签名串：{}--源签名串：{}", localSign, sourceSign);
        if (sourceSign.equalsIgnoreCase(localSign)) {
            return "success";
        }
        return "fail";
    }


    /**
     * @param entity
     * @param type   1 网银 0 扫码
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity, String type) throws Exception {
        logger.info("[TXZF]天天支付组装支付请求参数开始=======================START==========================");
        DecimalFormat df = new DecimalFormat("0.00");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Map<String, String> data = new HashMap<>();
        data.put("payKey", payKey);
        data.put("orderPrice", df.format(entity.getAmount()));
        data.put("outTradeNo", entity.getOrderNo());
        data.put("productName", "recharge");
        data.put("orderIp", entity.getIp());
        data.put("returnUrl", notifyUrl);
        data.put("notifyUrl", notifyUrl);
        data.put("orderTime", sdf.format(new Date()));
        if ("1".equals(type)) {
            data.put("productType", bankProductType);
        }
        if ("0".equals(type)) {
            data.put("productType", entity.getPayCode());
        }
        data.put("sign", generatorSign(data));
        return data;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名
     */
    private String generatorSign(Map<String, String> data) {
        logger.info("[TXZF]天天支付生成签名串开始==================START=====================");
        try {
            //参数排序
            Map<String, String> sortmap = MapUtils.sortByKeys(data);
            //组装签名串
            StringBuffer sb = new StringBuffer();
            for (String key : sortmap.keySet()) {
                String val = sortmap.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.append("paySecret=").append(paySecret);

            String signStr = sb.toString();
            logger.info("[TXZF]天天支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[TXZF]天天支付生成加密签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TXZF]天天支付生成签名串异常:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 查询订单接口
     *
     * @param orderNo
     * @return
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    public boolean serchOrder(String orderNo) {
        try {
            Map<String, String> param = new HashMap<>();
            param.put("payKey", payKey);
            param.put("outTradeNo", orderNo);
            param.put("sign", generatorSign(param));
            logger.info("[TXZF]天天支付回调查询订单{}请求参数:{}", orderNo, JSONObject.fromObject(param));
            String resStr = HttpUtils.toPostForm(param, searchOrderUrl);

            if (StringUtils.isBlank(resStr)) {
                logger.info("[TXZF]天天支付回调订单查询发起HTTP请求无响应,订单号{}", orderNo);
                return false;
            }
            JSONObject resJson = JSONObject.fromObject(resStr);
            logger.info("[TXZF]天天支付回调订单查询响应信息:{}", resJson);
            if (!"SUCCESS".equals(resJson.getString("orderStatus"))) {
                logger.info("[TXZF]天天支付回调订单{}查询错误信息{}", orderNo, resJson.getString("errMsg"));
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TXZF]天天支付回调订单{}查询异常{}", orderNo, e.getMessage());
            return false;
        }

    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);  //获取回调请求参数
        logger.info("[TXZF]天天支付回调请求参数：" + JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("TXZFNotify获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.payKey = config.getString("payKey");//从配置中获取
        this.paySecret = config.getString("paySecret");
        this.searchOrderUrl = config.getString("searchOrderUrl");

        String order_amount = infoMap.get("orderPrice");//单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info("TXZFNotify获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount);
        String order_no = infoMap.get("outTradeNo");// 平台订单号
        String trade_no = infoMap.get("trxNo");// 第三方订单号
        String trade_status = infoMap.get("tradeStatus");//订单状态:00为成功
        String t_trade_status = "SUCCESS";// 表示成功状态

        boolean result = serchOrder(order_no);
        if (!result) {
            logger.info("[TXZF]天天支付回调订单{}查询失败", order_no);
            return ret__failed;
        }

        boolean verifyRequest = false;
        if ("success".equals(callback(infoMap))) {
            verifyRequest = true;
        }
        ;

        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setRet__success(ret__success);    //成功返回
        processNotifyVO.setRet__failed(ret__failed);      //失败返回
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);    //支付状态
        processNotifyVO.setT_trade_status(t_trade_status);     //第三方成功状态
        processNotifyVO.setRealAmount(realAmount);
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());    //回调参数
        processNotifyVO.setPayment("TXZF");
        processNotifyVO.setConfig(config);
        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}
