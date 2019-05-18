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
 * @ClassName WDTPayServiceImpl
 * @Description 新万达支付对接支付宝渠道
 * @Date 2019/3/23 17 57
 **/
public class WDTPayServiceImpl extends PayAbstractBaseService implements PayService {

    private static final Logger logger = LoggerFactory.getLogger(WDTPayServiceImpl.class);
    private static String ret__success = "OK";  //OK 才算成功 其他的都不算成功
    private static String ret__failed = "fail";   //失败返回字符串
    private String uid;//商户号
    private String md5key;//密钥
    private String payUrl;//支付地址
    private String notifyUrl;//回调地址
    private boolean verifySuccess = true;//回调验签默认状态为true


    public WDTPayServiceImpl() {
    }

    public WDTPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("uid")) {
                this.uid = data.get("uid");
            }
            if (data.containsKey("md5key")) {
                this.md5key = data.get("md5key");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
        }
    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        logger.info("[WDT]新万达支付回调开始=================start==========================");
        this.uid = config.getString("uid");
        this.md5key = config.getString("md5key");
        this.notifyUrl = config.getString("notifyUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[WDT] 新万达支付回调请求参数报文：" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("platform_trade_no");//第三方订单号
        String order_no = dataMap.get("orderid");//支付订单号
        String amount = dataMap.get("realprice");//商户订单总金额，订单总金额以元为单位，精确到小数点后两位
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);  //回调ip

        String trade_status = "00";
        String t_trade_status = "00";

        if (StringUtils.isEmpty(trade_no)) {
            logger.info("[WDT] 新万达支付  获取的 流水单号为空");
            return ret__failed;
        }
        if (StringUtils.isEmpty(amount)) {
            logger.info("[WDT] 新万达支付  回调订单金额为空");
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
        processNotifyVO.setPayment("WDT");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[WDT] 新万达支付  回调验签失败");
            return ret__failed;
        }
        return processSuccessNotify(processNotifyVO, verifySuccess);
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[WDT] 新万达支付扫码支付开始================start=======================");
        try {
            Map<String, String> data = sealRequest(payEntity);

            String response = HttpUtils.generatorForm(data, payUrl);//发起HTTP请求
            if (StringUtils.isEmpty(response)) {
                return PayResponse.error("[WDT] 新万达支付  HTTP 请求返回参数为空");
            }
            logger.info("[WDT] 新万达支付HTTP请求返回参数:" + response);

            return PayResponse.sm_form(payEntity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[WDT] 新万达支付   扫码支付异常：" + e.getMessage());
        }
    }

    /**
     * 验签
     *
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {
        logger.info("[WDT]新万达支付回调验签开始===================start===============================");
        String sign = generatorSign(data, 2);
        String sourceSign = data.remove("key");
        logger.info("[WDT]新万达支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
        if (sign.equals(sourceSign)) {
            return ret__success;
        }
        return ret__failed;
    }

    /**
     * 参数组装
     *
     * @param entity
     * @return
     */
    public Map<String, String> sealRequest(PayEntity entity) {
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(entity.getAmount());

        dataMap.put("uid", uid);//商户uid
        dataMap.put("price", amount);//价格
        dataMap.put("istype", entity.getPayCode());//支付渠道
        dataMap.put("notify_url", notifyUrl);//通知回调网址
        dataMap.put("return_url", entity.getRefererUrl());//跳转网址
        dataMap.put("orderid", entity.getOrderNo());//商户自定义订单号
        dataMap.put("orderuid", entity.getuId());//商户自定义客户号
        dataMap.put("goodsname", "TOP-UP");//商品名称
        dataMap.put("attach", "TOP-UP");//附加内容
        dataMap.put("key", generatorSign(dataMap, 1));//秘钥

        logger.info("[WDT] 新万达支付  请求参数" + dataMap);
        return dataMap;
    }

    /**
     * 生成签名
     *
     * @param data
     * @return
     */
    public String generatorSign(Map<String, String> data, int type) {
        logger.info("[WDT] 新万达支付生成签名=================start===============================");
        try {
            StringBuffer sb = new StringBuffer();
            if (1 == type) {//发起支付签名
                //key的拼接顺序：如用到了所有参数，就按这个顺序拼接：
                // goodsname +“+”+ istype +“+”+ notify_url +“+”+ orderid+“+”+ orderuid +“+”+ price
                // +“+”+ return_url +“+”+ token +“+”+ uid
                sb.append(data.get("goodsname")).append("+");
                sb.append(data.get("istype")).append("+");
                sb.append(data.get("notify_url")).append("+");
                sb.append(data.get("orderid")).append("+");
                sb.append(data.get("orderuid")).append("+");
                sb.append(data.get("price")).append("+");
                sb.append(data.get("return_url")).append("+");
                sb.append(md5key).append("+");
                sb.append(data.get("uid"));

            } else {//回调签名
                //key的拼接顺序：如用到了所有参数，就按这个顺序拼接：
                // orderid +“+”+ orderuid +“+”+ platform_trade_no +“+”+ price +“+”+ realprice +“+”+ token
                sb.append(data.get("orderid")).append("+");
                sb.append(data.get("orderuid")).append("+");
                sb.append(data.get("platform_trade_no")).append("+");
                sb.append(data.get("price")).append("+");
                sb.append(data.get("realprice")).append("+");
                sb.append(md5key);

            }
            logger.info("[WDT] 新万达支付  生成签名前的参数：" + sb.toString());
            return MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WDT] 新万达支付   扫码支付   生成签名 异常" + e.getMessage());
            return e.getMessage();
        }
    }
}
