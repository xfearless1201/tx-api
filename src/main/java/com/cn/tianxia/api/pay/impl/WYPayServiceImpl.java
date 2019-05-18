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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName WYPayServiceImpl
 * @Description 忘忧支付渠道是 支付宝，手机扫码跟H5自动唤醒支付宝
 * @Date 2019/3/26 16 52
 **/
public class WYPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(WYPayServiceImpl.class);
    private static String ret__success = "success";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private String user_id;
    private String payUrl;
    private String notifyUrl;
    private String appkey;
    private String secret;
    private String queryOrderUrl;
    private boolean verifySuccess = true;//回调验签默认状态为true

    public WYPayServiceImpl() {
    }

    public WYPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("user_id")) {
                this.user_id = data.get("user_id");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("appkey")) {
                this.appkey = data.get("appkey");
            }
            if (data.containsKey("secret")) {
                this.secret = data.get("secret");
            }
            if (data.containsKey("queryOrderUrl")) {
                this.queryOrderUrl = data.get("queryOrderUrl");
            }
        }
    }

    /**
     * 支付回调
     *
     * @param request
     * @param response
     * @param config
     * @return
     */
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {

        this.appkey = config.getString("appkey");
        this.secret = config.getString("secret");
        this.notifyUrl = config.getString("notifyUrl");
        this.user_id = config.getString("user_id");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[WY]忘忧支付   商户返回信息：" + dataMap);

        String trade_no = dataMap.get("sys_trade_no");//第三方订单号，流水号
        String order_no = dataMap.get("out_trade_no");//支付订单号
        String amount = dataMap.get("real_amount");//实际支付金额，精确到小数点后两位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);  //回调ip


        if (StringUtils.isBlank(trade_no)) {
            logger.info("[WY]忘忧支付    获取的{} 流水单号为空", trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[WY]忘忧支付   回调订单金额为空");
            return ret__failed;
        }

        String trade_status = dataMap.get("status");  //第三方支付状态，success = 支付成功
        String t_trade_status = "success";//第三方成功状态

        //订单查询
        try {
            Map<String, String> queryMap = new HashMap<>();
            String time = new SimpleDateFormat("yyyymmddhh").format(new Date());
            queryMap.put("user_id", user_id);
            queryMap.put("out_trade_no", order_no);
            queryMap.put("sign_type", "simple");
            queryMap.put("notice", time);
            queryMap.put("sign", generatorSign(dataMap, 2));
            logger.info("[WY]忘忧支付  订单查询 请求参数：" + queryMap);
            String queryData = HttpUtils.toPostForm(queryMap, queryOrderUrl);
            if (StringUtils.isBlank(queryData)) {
                return ret__failed;
            }
            JSONObject jb = JSONObject.fromObject(queryData);
            logger.info("[WY]忘忧支付  订单查询 结果：" + jb);

            if (!"200".equalsIgnoreCase(jb.getString("code"))) {
                return ret__failed;
            }
            JSONObject dataJson = jb.getJSONObject("data");
            if (!"3".equalsIgnoreCase(dataJson.getString("status"))) {
                logger.info("[WY]忘忧支付  订单查询 结果订单未支付成功，订单状态为：" + dataJson.getString("status"));
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
        processNotifyVO.setPayment("WY");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[WY]忘忧支付    回调验签失败");
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
        try {
            Map<String, String> dataMap = sealRequest(payEntity);
            String response = HttpUtils.generatorForm(dataMap, payUrl);
            logger.info("[WY]忘忧支付  扫码支付 HTTP请求返回信息：" + response);
            if (StringUtils.isBlank(response)) {
                return PayResponse.error("[WY]忘忧支付  扫码支付 异常：");
            }

            return PayResponse.sm_form(payEntity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[WY]忘忧支付  扫码支付 异常：" + e.getMessage());
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
        logger.info("[WY]忘忧支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
        if (sign.equals(sourceSign)) {
            return "success";
        }
        return "fail";
    }

    /**
     * 参数组装
     *
     * @param payEntity
     * @return
     */
    public Map<String, String> sealRequest(PayEntity payEntity) {
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());
        dataMap.put("user_id", user_id);//商户
        dataMap.put("content_type", "text");//页面类型
        dataMap.put("payway", payEntity.getPayCode());//支付通道
        dataMap.put("out_trade_no", payEntity.getOrderNo());//商户订单号
        dataMap.put("out_remark", "TOP-UP");//订单备注信息
        dataMap.put("amount", amount);//订单金额
        dataMap.put("return_url", payEntity.getRefererUrl());//回调地址
        dataMap.put("notify_url", notifyUrl);//异步通知地址
        dataMap.put("sign", generatorSign(dataMap, 1));//签名字符串
        logger.info("[WY]忘忧支付   扫码支付 参数：" + JSONObject.fromObject(dataMap));
        return dataMap;
    }

    /**
     * 加密
     *
     * @param data
     * @return
     */
    public String generatorSign(Map<String, String> data, int type) {
        try {
            /*
             * 第一步： 获取订单信息数组中的金额，把订单金额进行格式化，保留两位小数。比如把100格式化为100.00
             * 第二步： 获取订单信息数组里面的商户订单号。如:12345678
             * 第三步： 把第一步的值和第二步的值连接起来，计算MD5值，如：
             *         MD5(100.0012345678)  结果为：7b3201558131d3b3
             * 第四步： 把商户appkey，第三步获取的值，商户的secret，按顺序连接起来，进行MD5运算。
             *          例如：商户KEY等于appkey,商户secret等于secret,那么最终的签名字符串为：
             *          md5(appkey7b3201558131d3b3secret),MD5运算后值为" d59c6e1034515d42"
             */
            StringBuffer sb1 = new StringBuffer();
            if (1 == type) {//下单或订单查询时的加密方法
                sb1.append(data.get("amount")).append(data.get("out_trade_no"));
                String firstSign = MD5Utils.md5toUpCase_32Bit(sb1.toString()).toLowerCase();

                StringBuffer sb2 = new StringBuffer();
                sb2.append(appkey).append(firstSign).append(secret);
                return MD5Utils.md5toUpCase_32Bit(sb2.toString()).toLowerCase();
            } else {//回调
                String amount = data.get("money");//商户订单总金额，订单总金额以元为单位，精确到小数点后两位
                String money = new DecimalFormat("0.00").format(Double.parseDouble(amount));

                sb1.append(money).append(data.get("out_trade_no"));
                String firstSign = MD5Utils.md5toUpCase_32Bit(sb1.toString()).toLowerCase();
                logger.info("第一次加密的参数：" + sb1.toString() + "，money：" + data.get("money") + "，out_trade_no：" + data.get("out_trade_no"));

                StringBuffer sb2 = new StringBuffer();
                sb2.append(appkey).append(firstSign).append(secret);
                logger.info("第二次加密的参数：" + sb2.toString() + "，appkey：" + appkey + "，secret：" + secret);

                return MD5Utils.md5toUpCase_32Bit(sb2.toString()).toLowerCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WY]忘忧支付   加密异常：" + e.getMessage());
            return e.getMessage();
        }
    }


}
