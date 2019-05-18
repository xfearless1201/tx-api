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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Vicky
 * @version 1.2.0
 * @ClassName SYPayServiceImpl
 * @Description 随意付支付  渠道 微信支付宝PC H5
 * @Date 2019/4/24 18 52
 * <p>
 * 注意： 1、金额必须为整数，否则金额将四舍五入
 * 2、微信H5 只支付苹果浏览器；
 * 3、支付宝D0扫码H5（个人转账）50-5000任意金额
 * 4、微信D0H5（话费）20、30、50、100 、200、300、500
 * 5、微信D0扫码（话费）9.95，19.9，20，29.85，30，49.75，50，99.5，100
 **/
public class SYPayServiceImpl extends PayAbstractBaseService implements PayService {

    private static final Logger logger = LoggerFactory.getLogger(SYPayServiceImpl.class);
    private static String ret__success = "success";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private String merchId;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String secret;//密钥
    private String queryOrderUrl;
    private String wycode;//网银编码
    private String kjcode;//快捷编码
    private boolean verifySuccess = true;//回调验签默认状态为true

    public SYPayServiceImpl() {
    }

    public SYPayServiceImpl(Map<String, String> data) {
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
            if (data.containsKey("wycode")) {
                this.wycode = data.get("wycode");
            }
            if (data.containsKey("kjcode")) {
                this.kjcode = data.get("kjcode");
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
        this.secret = config.getString("secret");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[SY]随意付支付扫码支付回调请求参数：" + JSONObject.fromObject(dataMap));

        if (!MapUtils.isNotEmpty(dataMap)) {
            logger.error("SYNotify获取回调请求参数为空");
            return ret__failed;
        }

        String trade_no = dataMap.get("r0_requestNo");//第三方订单号，流水号
        String order_no = dataMap.get("r1_orderId");//支付订单号
        String amount = dataMap.get("r3_amount");//订单金额,单位元
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[SY]随意付支付回调请求参数,获取的{} 流水单号为空", trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[SY]随意付支付回调请求参数,订单金额为空");
            return ret__failed;
        }

        String trade_status = dataMap.get("r4_status");  //第三方支付状态，1 支付成功
        String t_trade_status = "s";//第三方成功状态

        //订单查询
        try {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("p0_shopId", merchId);
            queryMap.put("p1_orderId", order_no);
            queryMap.put("sign", generatorSign(queryMap));

            logger.info("[SY]随意付支付回调查询订单{}请求参数：{}", order_no, JSONObject.fromObject(queryMap));
            String ponse = HttpUtils.toPostForm(queryMap, queryOrderUrl);

            if (StringUtils.isBlank(ponse)) {
                logger.info("[SY]随意付支付回调查询订单发起HTTP请求无响应");
                return ret__failed;
            }
            logger.info("[SY]随意付支付回调查询订单{}响应信息：{}", order_no, JSONObject.fromObject(ponse));

            JSONObject jb = JSONObject.fromObject(ponse);
            if (jb.containsKey("returnCode") && "0".equals(jb.getString("returnCode"))) {
                JSONObject json = jb.getJSONObject("content");
                if (!"s".equals(json.getString("r4_status"))) {
                    logger.info("[SY]随意付支付回调查询订单,订单支付状态为:{}", json.getString("r4_status"));
                    return ret__failed;
                }
            } else {
                logger.info("[SY]随意付支付回调查询订单,请求状态为:{}", jb.getString("returnCode"));
                return ret__failed;
            }

        } catch (Exception e) {
            e.getStackTrace();
            logger.info("[SY]随意付支付回调查询订单{}异常{}：", order_no, e.getMessage());
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
        processNotifyVO.setPayment("SY");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[SY]随意付支付回调验签失败");
            return ret__failed;
        }
        return processSuccessNotify(processNotifyVO, verifySuccess);
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[SY]随意付网银支付开始===============START========================");
        try {

            Map<String, String> dataMap = sealRequest(payEntity, 1);

            String responseData = HttpUtils.toPostForm(dataMap, payUrl);

            JSONObject json = JSONObject.fromObject(responseData);
            logger.info("[SY]随意付支付网银支付响应信息：{}", json);

            if (json.containsKey("returnCode") && "0".equals(json.getString("returnCode"))) {

                return PayResponse.wy_link(json.getString("content"));
            }
            return PayResponse.error("[SY]随意付支付下单失败：" + json.getString("message"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SY]随意付支付网银支付异常:{}", e.getMessage());
            return PayResponse.error("[SY]随意付支付网银支付异常" + e.getMessage());
        }
    }

    /**
     * 扫码支付
     *
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[SY]随意付支付扫码支付开始===============START========================");
        try {
            Map<String, String> dataMap = sealRequest(payEntity, 2);

            String responseData = HttpUtils.toPostForm(dataMap, payUrl);

            JSONObject json = JSONObject.fromObject(responseData);
            logger.info("[SY]随意付支付扫码支付响应信息：{}", json);

            if (json.containsKey("returnCode") && "0".equals(json.getString("returnCode"))) {

                if (kjcode.equals(payEntity.getPayCode())) {

                    return PayResponse.sm_link(payEntity, json.getString("content"), "下单成功");
                } else {

                    if (StringUtils.isBlank(payEntity.getMobile())) {
                        return PayResponse.sm_qrcode(payEntity, json.getString("content"), "下单成功");
                    }

                    return PayResponse.sm_link(payEntity, json.getString("content"), "下单成功");
                }
            }
            return PayResponse.error("[SY]随意付支付下单失败：" + json.getString("message"));
        } catch (Exception e) {
            e.getStackTrace();
            logger.info("[SY]随意付支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[SY]随意付支付扫码支付异常:" + e.getMessage());
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

        String sign = generatorSign(data);
        String sourceSign = data.remove("sign");
        logger.info("[SY]随意付支付扫码支付生成签名串：{}--源签名串：{}", sign, sourceSign);

        if (sign.equals(sourceSign)) {
            return "success";
        }
        return "fail";
    }

    /**
     * 组装参数
     *
     * @param payEntity
     * @return
     */
    private Map<String, String> sealRequest(PayEntity payEntity, int type) {
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("##").format(payEntity.getAmount());
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        dataMap.put("p0_shopId", merchId);//商户号
        dataMap.put("p1_orderId", payEntity.getOrderNo());//商户交易号（订单号）,商户自己平台的订单号
        dataMap.put("p2_orderCommodity", "TOP-UP");//商品名称
        dataMap.put("p3_amount", amount);//订单金额,单位元,只能整数
        if (1 == type) {
            dataMap.put("p4_typeCode", wycode);//支付码,参考商户后台支付通道列表里面的支付码
            dataMap.put("p9_bankCode", payEntity.getPayCode());//银行编码 "网银必须传,扫码可不传"
        } else {
            dataMap.put("p4_typeCode", payEntity.getPayCode());//支付码,参考商户后台支付通道列表里面的支付码
        }
        dataMap.put("p5_time", time);//请求时间
        dataMap.put("p6_ip", payEntity.getIp());//提交IP地址
        dataMap.put("p7_frontUrl", payEntity.getRefererUrl());//前端跳转地址
        dataMap.put("p8_notifyUrl", notifyUrl);//异步通知地址

        dataMap.put("p13_remark", "recharge");//订单备注信息
        dataMap.put("sign", generatorSign(dataMap));//签名（唯一不参与签名的字段,参考签名方法）

        logger.info("[SY]随意付支付扫码支付请求参数：{}", JSONObject.fromObject(dataMap));
        return dataMap;
    }

    /**
     * 生成签名
     *
     * @param data
     * @return
     */
    private String generatorSign(Map<String, String> data) {
        //"所有参数,除了sign,其它参数都参与签名"
        //"所有参数都按照ASCII码正序排序，按照key1=value1&key2=value2&......keyn=valuen的方式排序，得到待签名字符串"
        //"以上拼接的字符串，后面拼接上payKey(密钥的值)后md5加密，得到sign值"

        try {
            Map<String, String> treeMap = new TreeMap<>(data);
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = treeMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = treeMap.get(key);
                if (StringUtils.isBlank(val) || "sign".equals(key)) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }

            sb.replace(sb.length() - 1, sb.length(), secret);

            String strString = sb.toString();
            logger.info("[SY]随意付支付扫码支付生成待签名串：{}", strString);
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();

            logger.info("[SY]随意付支付扫码支付生成签名串：{}", sign);
            return sign;
        } catch (Exception e) {
            e.getStackTrace();
            logger.info("[SY]随意付支付扫码支付生成签名串异常：{}", e.getMessage());
            return "[SY]随意付支付扫码支付生成签名串异常";
        }
    }
}
