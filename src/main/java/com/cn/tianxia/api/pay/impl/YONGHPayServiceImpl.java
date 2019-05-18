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
 * @ClassName YONGHPayServiceImpl
 * @Description 永恒支付对接渠道： 云闪，网关，支付宝 wap，H5
 * @Date 2019/4/1 09 31
 **/
public class YONGHPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(YONGHPayServiceImpl.class);
    private static String ret__success = "OK";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private String memberid;
    private String payUrl;
    private String notifyUrl;
    private String queryOrderUrl;
    private String md5key;
    private boolean verifySuccess = true;//回调验签默认状态为true

    public YONGHPayServiceImpl() {
    }

    public YONGHPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("memberid")) {
                this.memberid = data.get("memberid");
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
            if (data.containsKey("md5key")) {
                this.md5key = data.get("md5key");
            }
        }
    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        this.memberid = config.getString("memberid");
        this.notifyUrl = config.getString("notifyUrl");
        this.md5key = config.getString("md5key");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[YONGH]永恒支付    商户返回信息：" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("transaction_id");//第三方订单号，流水号
        String order_no = dataMap.get("orderid");//支付订单号
        String amount = dataMap.get("amount");//实际支付金额，精确到小数点后两位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[YONGH]永恒支付      获取的{} 流水单号为空", trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[YONGH]永恒支付    回调订单金额为空");
            return ret__failed;
        }

        String trade_status = dataMap.get("returncode");  //第三方支付状态，00 支付成功
        String t_trade_status = "00";//第三方成功状态

        //订单查询
        try {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("pay_memberid", memberid);
            queryMap.put("pay_orderid", order_no);
            queryMap.put("pay_md5sign", generatorSign(queryMap));

            String queryData = HttpUtils.toPostForm(queryMap, queryOrderUrl);
            if (StringUtils.isBlank(queryData)) {
                logger.info("[YONGH]永恒支付   订单查询 结果为 空");
                return ret__failed;
            }
            logger.info("[YONGH]永恒支付   订单查询 结果：" + JSONObject.fromObject(queryData));

            JSONObject jb = JSONObject.fromObject(queryData);

            if (jb.containsKey("returncode") && "00".equals(jb.get("returncode"))) {
                if ("NOTPAY".equalsIgnoreCase(jb.getString("trade_state"))) {
                    logger.info("[YONGH]永恒支付   交易状态为：" + jb.getString("trade_state"));
                    return ret__failed;
                }
            } else {
                return ret__failed;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ret__failed;
        }

        //写入数据库
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setRealAmount(Double.parseDouble(amount));//以分为单位
        processNotifyVO.setIp(ip);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setInfoMap(JSONObject.fromObject(dataMap).toString());
        processNotifyVO.setT_trade_status(t_trade_status);
        processNotifyVO.setConfig(config);
        processNotifyVO.setPayment("YONGH");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[YONGH]永恒支付     回调验签失败");
            return ret__failed;
        }
        return processSuccessNotify(processNotifyVO, verifySuccess);
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[YONGH]永恒支付  网银支付   开始====================start==================");
        try {
            Map<String, String> dataMap = sealRequest(payEntity, 1);
            String responseData = HttpUtils.toPostForm(dataMap, payUrl);

            logger.info("[YONGH]永恒支付 网银支付 HTTP请求返回数据 " + JSONObject.fromObject(responseData));
            if (StringUtils.isBlank(responseData)) {
                return PayResponse.error("[YONGH]永恒支付 HTTP请求返回为空");
            }

            JSONObject jb = JSONObject.fromObject(responseData);
            if (jb.containsKey("status") && "ok".equalsIgnoreCase(jb.getString("status"))) {

                JSONObject urlData = jb.getJSONObject("data");
                String url = urlData.getString("pay_url");

                return PayResponse.wy_link(url);
            } else {
                return PayResponse.error("下单失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("网银支付下单异常：" + e.getMessage());
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[YONGH]永恒支付  扫码支付   开始====================start==================");
        try {
            Map<String, String> dataMap = sealRequest(payEntity, 2);

            String responseData = HttpUtils.toPostForm(dataMap, payUrl);
            if (StringUtils.isBlank(responseData)) {
                return PayResponse.error("[YONGH]永恒支付 HTTP请求返回为空");
            }
            logger.info("[YONGH]永恒支付 扫码支付 HTTP请求返回数据 " + JSONObject.fromObject(responseData));

            JSONObject jb = JSONObject.fromObject(responseData);
            if (jb.containsKey("status") && "ok".equalsIgnoreCase(jb.getString("status"))) {

                JSONObject urlData = jb.getJSONObject("data");
                String url = urlData.getString("pay_url");
                return PayResponse.sm_link(payEntity, url, "下单成功");
            } else {
                return PayResponse.error("下单失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("扫码下单异常：" + e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sign = generatorSign(data);
        String sourceSign = data.remove("sign");
        if (sign.equals(sourceSign)) {
            return ret__success;
        }
        return ret__failed;
    }


    public Map<String, String> sealRequest(PayEntity entity, int type) {
        Map<String, String> dataMap = new HashMap<>();

        String amount = new DecimalFormat("00").format(entity.getAmount());//以元为单位.只接受整数

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        dataMap.put("pay_memberid", memberid);//商户号,平台分配商户号
        dataMap.put("pay_orderid", entity.getOrderNo());//订单号,上送订单号唯一, 字符长度20
        dataMap.put("pay_applydate", time);//提交时间,时间格式：2016-12-26 18:18:18
        if (1 == type) {//网银
            dataMap.put("pay_bankcode", "1199");//银行编码,参考后续说明
        } else {
            dataMap.put("pay_bankcode", entity.getPayCode());//银行编码,参考后续说明
        }
        dataMap.put("pay_notifyurl", notifyUrl);//服务端通知,服务端返回地址.（POST返回数据）
        dataMap.put("pay_callbackurl", entity.getRefererUrl());//页面跳转通知,页面跳转返回地址（POST返回数据）
        dataMap.put("pay_amount", amount);//订单金额,商品金额
        dataMap.put("pay_md5sign", generatorSign(dataMap));//MD5签名,请看MD5签名字段格式
        dataMap.put("pay_attach", "recharge");//附加字段,此字段在返回时按原样返回 (中文需要url编码)
        dataMap.put("pay_productname", "TOP-UP");//商品名称,
        dataMap.put("format", "json");//返回数据格式,可以不传，如果format=json, 则返回 json 数据，否则直接 302 跳转到支付地址

        logger.info("[YONGH]永恒支付 HTTP请求参数：" + JSONObject.fromObject(dataMap));
        return dataMap;
    }

    public String generatorSign(Map<String, String> data) {
        try {
            Map<String, String> mapTree = new TreeMap(data);
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = mapTree.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = mapTree.get(key);
                if ("sign".equalsIgnoreCase(key) || "attach".equalsIgnoreCase(key) || "format".equalsIgnoreCase(key) || val.isEmpty()) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.append("key=").append(md5key);

            logger.info("[YONGH]永恒支付 生成签名参数：" + sb.toString());
            return MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            return "签名生成异常";
        }
    }
}
