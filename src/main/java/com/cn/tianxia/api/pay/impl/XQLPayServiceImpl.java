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
 * @version 1.0.0
 * @ClassName XQLPayServiceImpl
 * @Description X麒麟（和之前配置的麒麟不是一个支付 是新支付）渠道  支付宝
 * @Date 2019/4/13 11 38
 **/
public class XQLPayServiceImpl extends PayAbstractBaseService implements PayService {

    private static final Logger logger = LoggerFactory.getLogger(XQLPayServiceImpl.class);
    private static String ret__success = "OK";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private String merchId;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String secret;//密钥
    private String queryOrderUrl;//订单查询地址
    private boolean verifySuccess = true;//回调验签默认状态为true

    public XQLPayServiceImpl() {
    }

    public XQLPayServiceImpl(Map<String, String> data) {
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

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        this.merchId = config.getString("merchId");
        this.notifyUrl = config.getString("notifyUrl");
        this.secret = config.getString("secret");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[XQL]X麒麟支付回调请求参数:" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("transaction_id");//第三方订单号，流水号
        String order_no = dataMap.get("orderid");//支付订单号
        String amount = dataMap.get("amount");//实际支付金额
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[XQL]X麒麟支付获取的{} 流水单号为空", trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[XQL]X麒麟支付回调订单金额为空");
            return ret__failed;
        }

        String trade_status = dataMap.get("returncode");  //第三方支付状态，对方没有“支付状态”这个字段
        String t_trade_status = "00";//第三方成功状态

        //订单查询
        try {
            Map<String, String> queryMap = new HashMap<>();

            queryMap.put("pay_memberid", merchId);//商户ID
            queryMap.put("pay_orderid", order_no);//商户ID
            queryMap.put("pay_md5sign", generatorSign(queryMap));//商户ID

            logger.info("[XQL]X麒麟支付订单查询请求参数：" + JSONObject.fromObject(queryMap));

            String queryData = HttpUtils.toPostForm(queryMap, queryOrderUrl);

            if (StringUtils.isBlank(queryData)) {
                return ret__failed;
            }
            logger.info("[XQL]X麒麟支付订单查询响应参数：" + JSONObject.fromObject(queryData));

            JSONObject jb = JSONObject.fromObject(queryData);
            if (!"00".equals(jb.get("returncode"))) {
                return ret__failed;
            }
            if (jb.containsKey("trade_state") && "NOTPAY".equalsIgnoreCase(jb.getString("trade_state"))) {
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
        processNotifyVO.setPayment("XQL");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[XQL]X麒麟支付   回调验签失败");
            return ret__failed;
        }
        return processSuccessNotify(processNotifyVO, verifySuccess);
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    /**
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[XQL]X麒麟支付 扫码支付  开始=================start===============================");
        try {
            Map<String, String> dataMap = sealRequest(payEntity);

            String responseData = HttpUtils.generatorForm(dataMap, payUrl);

            if (StringUtils.isBlank(responseData)) {
                return PayResponse.error("HTTP 请求返回为空");
            }
            return PayResponse.sm_form(payEntity, responseData, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("扫码支付异常：" + e.getMessage());
        }

    }

    @Override
    public String callback(Map<String, String> data) {

        data.remove("attach");

        String sign = generatorSign(data);
        String sourceSign = data.remove("sign");

        logger.info("回调验签，服务器签名：" + sourceSign + "，生成签名：" + sign);

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
    private Map<String, String> sealRequest(PayEntity payEntity) {
        Map<String, String> dataMap = new HashMap<>();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());

        dataMap.put("pay_memberid", merchId);//商户号
        dataMap.put("pay_orderid", payEntity.getOrderNo());//订单号
        dataMap.put("pay_applydate", time);//提交时间
        dataMap.put("pay_bankcode", payEntity.getPayCode());//银行编码
        dataMap.put("pay_notifyurl", notifyUrl);//服务端通知
        dataMap.put("pay_callbackurl", payEntity.getRefererUrl());//页面跳转通知
        dataMap.put("pay_amount", amount);//订单金额
        dataMap.put("pay_md5sign", generatorSign(dataMap));//MD5签名
        dataMap.put("pay_productname", "TOP-UP");//商品名称

        logger.info("[XQL]X麒麟支付  HTTP请求参数：" + JSONObject.fromObject(dataMap));
        return dataMap;
    }

    /**
     * 签名
     *
     * @param data
     * @return
     */
    private String generatorSign(Map<String, String> data) {
        try {
            StringBuffer sb = new StringBuffer();
            TreeMap<String, String> treeMap = new TreeMap<>(data);
            Iterator<String> iterator = treeMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = treeMap.get(key);
                if ("sign".equalsIgnoreCase(key) || "pay_attach".equalsIgnoreCase(key)
                        || "attach".equalsIgnoreCase(key) || "pay_productname".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.append("key=").append(secret);

            logger.info("[XQL]X麒麟支付  加密前参数：" + sb.toString());
            return MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "签名异常：" + e.getMessage();
        }
    }
}
