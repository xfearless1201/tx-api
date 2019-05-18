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

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName XLPayServiceImpl
 * @Description 雪梨支付 渠道：支付宝手机端 扫码 /h5     支付宝pc端 扫码
 * @Date 2019/4/18 11 33
 **/
public class XLPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(XLPayServiceImpl.class);
    private static String ret__success = "success";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private String merchId;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String secret;//密钥
    private String queryOrderUrl;//订单查询地址
    private boolean verifySuccess = true;//回调验签默认状态为true

    public XLPayServiceImpl() {
    }

    public XLPayServiceImpl(Map<String, String> data) {
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
            if (data.containsKey("queryOrderUrl")) {
                this.queryOrderUrl = data.get("queryOrderUrl");
            }
            if (data.containsKey("secret")) {
                this.secret = data.get("secret");
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
        this.merchId = config.getString("merchId");
        this.notifyUrl = config.getString("notifyUrl");
        this.queryOrderUrl = config.getString("queryOrderUrl");
        this.secret = config.getString("secret");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);

        logger.info("[XL]雪梨支付    商户返回信息：" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("trade_no");//第三方订单号，流水号
        String order_no = dataMap.get("shop_no");//支付订单号
        String amount = dataMap.get("money");//商户订单总金额，订单总金额以元为单位，精确到小数点后两位
        String trade_status = dataMap.get("status");  //第三方支付状态，0 = 支付成功
        String t_trade_status = "0";//第三方成功状态
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);  //回调ip

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[XL]雪梨支付     获取的 流水单号为空");
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[XL]雪梨支付    回调订单金额为空");
            return ret__failed;
        }

        //订单查询
        try {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("shop_id", merchId);
            queryMap.put("out_trade_no", order_no);
            queryMap.put("sign", generatorSign(queryMap, 3));

            String queryData = HttpUtils.toPostForm(queryMap, queryOrderUrl);
            if (StringUtils.isBlank(queryData)) {
                return ret__failed;
            }
            JSONObject jb = JSONObject.fromObject(queryData);
            logger.info("[XL]雪梨支付  订单查询返回参数：" + jb);

            //	支付状态: 0表示支付成功;1表示等待支付; 2表示支付超时;3表示支付失败;
            if (!"0".equals(jb.getString("status"))) {
                logger.info("[XL]雪梨支付  订单查询  支付状态，支付不成功：" + jb.get("status"));
                return ret__failed;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ret__failed;
        }

        //写入数据库
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setRealAmount(Double.parseDouble(amount));
        processNotifyVO.setIp(ip);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setInfoMap(JSONObject.fromObject(dataMap).toString());
        processNotifyVO.setT_trade_status(t_trade_status);
        processNotifyVO.setConfig(config);
        processNotifyVO.setPayment("XL");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[XL]雪梨支付   回调验签失败");
            return ret__failed;
        }
        return processSuccessNotify(processNotifyVO, verifySuccess);
    }

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
        logger.info("[XL]雪梨支付 扫码支付   开始============================start==============================");
        try {
            Map<String, String> dataMap = sealRequest(payEntity);
            dataMap.put("sign", generatorSign(dataMap, 1));//验签字符串，MD5(shopAccountId&shopUserId&amountInString&shopNo&payChannel &KEY);字段之间使用”&”符号链接;字符串拼接计算MD5值;shopAccountId 和KEY登陆商家后台可以查看;
            logger.info("[XL]雪梨支付   HTTP 请求参数：" + JSONObject.fromObject(dataMap));

            String responseData = HttpUtils.generatorForm(dataMap, payUrl);
            if (StringUtils.isBlank(responseData)) {
                return PayResponse.error("HTTP 请求返回 为空");
            }
            return PayResponse.sm_form(payEntity, responseData, "下单成功");
        } catch (Exception e) {
            e.getStackTrace();
            return PayResponse.error("扫码支付异常" + e.getMessage());
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

        String sign = generatorSign(data, 2);
        String sourceSign = data.remove("sign");
        if (sign.equals(sourceSign)) {
            return ret__success;
        }
        return ret__failed;
    }

    /**
     * 参数组装
     *
     * @param payEntity
     * @return
     */
    private Map<String, String> sealRequest(PayEntity payEntity) {
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());

        dataMap.put("shopAccountId", merchId);//商户ID (商户后台获取)
        dataMap.put("shopUserId", payEntity.getuId());//商户自己平台用户ID,记录作用 没有可为空
        dataMap.put("amountInString", amount);//订单⾦金金额，单位元，如:0.01表示⼀一分 钱;
        dataMap.put("payChannel", payEntity.getPayCode());//⽀支付宝: alipay(AA收款) 支付宝原生:mwpay 微信:wechat 支付宝转银行:bank 支付宝转银行:bank
        dataMap.put("shopNo", payEntity.getOrderNo());//商户订单号，⻓度不超过40;不允许出现”&”符号
        dataMap.put("shopCallbackUrl", notifyUrl);//订单支付成功回调地址(具体参数详见接口2，如果为空平台会调用商家在WEB端设置的订单回调地址;否则，平台会调用该地址，WEB端设置的地址不会被调用);
        dataMap.put("returnUrl", payEntity.getRefererUrl());//⼆维码扫码支付模式下:支付成功⻚页面‘返回商家端’按钮点击后的跳转地址; 如果商家采用自有界⾯面，则忽略该参数;
        dataMap.put("target", "1");//

        return dataMap;
    }

    /**
     * 签名
     *
     * @return
     */
    private String generatorSign(Map<String, String> data, int type) {

        try {
            StringBuffer sb = new StringBuffer();

            if (1 == type) {//支付
                //MD5(shopAccountId&shopUserId&amountInString&shopNo&payChannel &KEY
                sb.append(data.get("shopAccountId")).append("&");
                sb.append(data.get("shopUserId")).append("&");
                sb.append(data.get("amountInString")).append("&");
                sb.append(data.get("shopNo")).append("&");
                sb.append(data.get("payChannel")).append("&");
                sb.append(secret);

            } else if (2 == type) {//回调
                //md5(shopAccountId + user_id + trade_no +KEY+money+type)
                sb.append(merchId).append(data.get("user_id"));
                sb.append(data.get("trade_no")).append(secret);
                sb.append(data.get("money")).append(data.get("type"));

            } else {//订单查询
                //MD5(shop_id + order_no/out_trade_no + KEY)
                sb.append(data.get("shop_id")).append(data.get("out_trade_no"));
                sb.append(secret);
            }

            logger.info("[XL]雪梨支付 生成签名前参数：" + sb.toString());
            return MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
        } catch (Exception e) {
            e.getStackTrace();
            return "签名生成异常：" + e.getMessage();
        }
    }
}
