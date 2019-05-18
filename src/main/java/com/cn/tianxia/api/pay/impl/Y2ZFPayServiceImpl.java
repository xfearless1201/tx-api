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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName Y2ZFPayServiceImpl
 * @Description "新"  易支付新的支付商：易支付，与另一家同名而已
 * @Date 2019-02-28 10 33
 **/
public class Y2ZFPayServiceImpl extends PayAbstractBaseService implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(Y2ZFPayServiceImpl.class);

    private static String ret__success = "success";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    public String appid;//商户号
    public String key;//密钥
    public String payUrl;//支付地址
    public String notifyUrl;//回调地址
    public String searchOrderUrl;//支付查询地址：http://api.epayok.xyz/quer
    private boolean verifySuccess = true;//回调验签默认状态为true

    public Y2ZFPayServiceImpl() {
    }

    public Y2ZFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("appid")) {
                this.appid = data.get("appid");
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
            if (data.containsKey("searchOrderUrl")) {
                this.searchOrderUrl = data.get("searchOrderUrl");
            }
        }
    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        logger.info("[Y2ZF]易支付扫码 支付回调开始======================START==================");
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[Y2ZF]易支付回调请求参数:" + JSONObject.fromObject(dataMap));
        this.key = config.getString("key");
        this.notifyUrl = config.getString("notifyUrl");
        this.searchOrderUrl = config.getString("searchOrderUrl");

        String trade_no = dataMap.get("order");   //第三方订单号，流水号,客户平台生成的只有一个订单号，可以认为和流水号是同一个
        try {
            if (StringUtils.isBlank(trade_no)) {
                logger.info("[Y2ZF]易支付  获取的支付商订单号为空");
                return "获取的支付商订单号为空";
            }
            Map<String, String> orderMap = new HashMap<>();
            orderMap.put("num", trade_no);
            logger.info("[Y2ZF]易支付  订单查询的参数：" + JSONObject.fromObject(orderMap));
            String search = HttpUtils.get(orderMap, searchOrderUrl);
            if (StringUtils.isBlank(search)) {
                logger.info("[Y2ZF]易支付  订单查询信息为空");
                return "[Y2ZF]易支付  订单查询信息为空";
            }
            logger.info("[Y2ZF]易支付  回调订单查询结果：" + JSONObject.fromObject(search));
            JSONObject jb = JSONObject.fromObject(search);

            if (jb.containsKey("code") && "1".equals(jb.getString("code"))) {
                logger.info("[Y2ZF]易支付2 订单查询到的支付状态：订单支付成功");
                String order_no = dataMap.get("orderNo");  //我司支付订单号
                JSONObject info = JSONObject.fromObject(dataMap);

                String amount = dataMap.get("acmoney");//商户订单总金额，订单总金额以元为单位，精确到小数点后两位

                String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);  //回调ip
                String trade_status = "1";  //第三方支付状态
                String t_trade_status = "1";   //第三方成功状态
                //判断订单金额是否为空
                if (amount.isEmpty()) {
                    logger.info("[Y2ZF]易支付 回调订单金额为空");
                    return ret__failed;
                }
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
                //回调验签
                if (!"success".equals(callback(dataMap))) {
                    verifySuccess = false;
                    logger.info("[Y2ZF]易支付 回调验签失败");
                    return "fail";
                }
                logger.info("[Y2ZF]易支付 回调验签成功");
                return processSuccessNotify(processNotifyVO, verifySuccess);
            }
            logger.info("[Y2ZF]易支付2 订单查询到的支付状态：订单未完成或订单已过期");
            return "[Y2ZF]易支付2  订单查询到的支付状态：订单未完成或订单已过期";
        } catch (Exception e) {
            e.printStackTrace();
            return "[Y2ZF]易支付2  订单查询异常" + e.getMessage();
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[Y2ZF]易支付 银联支付开始======================START==================");
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[Y2ZF]易支付 扫码支付开始======================START==================");
        try {
            Map<String, String> dataMap = sealRequest(payEntity);
            logger.info("[Y2ZF]易支付 请求参数：" + JSONObject.fromObject(dataMap));
            dataMap.put("orderNo", payEntity.getOrderNo());
            dataMap.put("callback", notifyUrl);//回调地址
            String sign = generatorSign(dataMap);
            logger.info("[Y2ZF]易支付 生成的签名：" + sign);
            String response = HttpUtils.generatorFormGet(dataMap, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[Y2ZF]易支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[Y2ZF]易支付扫码支付发起HTTP请求无响应结果");
            }
            return PayResponse.sm_form(payEntity, response, "下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[Y2ZF]易支付 扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[Y2ZF]易支付 扫码支付异常");
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
        String callback = data.remove("callback");
        try {
            callback = URLEncoder.encode(callback, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        data.put("callback", callback);
        logger.info("[Y2ZF]易支付扫码 支付回调验签======================START==================");
        //校验方式，对除sign外的GET参数，按参数名进行升序排列，并与appkey链接成串，取MD5值与sign进行比对
        String sign = generatorSign(data);
        String sourceSign = data.get("sign");
        if (sign.equals(sourceSign)) {
            logger.info("[Y2ZF]易支付 验签成功");
            return "success";
        }
        logger.info("[Y2ZF]易支付 验签失败");
        return "fail";
    }

    /**
     * 生成签名
     *
     * @param dataMap
     * @return
     */
    public String generatorSign(Map<String, String> dataMap) {
        logger.info("[Y2ZF]易支付扫码支付 参数组装======================START==================");
        StringBuffer sb = new StringBuffer();
        try {
            //签名前参数排序
            Map<String, String> map = MapUtils.sortByKeys(dataMap);
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = map.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                sb.append(key).append("=").append(val).append("&");
            }
            logger.info("[Y2ZF]易支付 生成的签名前 参数从小到大排序" + sb);
            sb.replace(sb.length() - 1, sb.length(), "");
            sb.append(key);
            logger.info("[Y2ZF]易支付 生成的签名前 参数最后接入商户密钥 key:" + key + ",参数：" + sb);
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
            logger.info("[Y2ZF]易支付 生成的签名：" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[Y2ZF]易支付 生成的签名失败");
            return "[Y2ZF]易支付 生成的签名失败";
        }
    }

    /**
     * 参数组装
     *
     * @return
     */
    public Map<String, String> sealRequest(PayEntity entity) throws Exception {
        try {
            Map<String, String> dataMap = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            dataMap.put("appid", appid);//你在本站的
            dataMap.put("type", entity.getPayCode());//付款类型 1：微信，2：支付宝
            dataMap.put("money", amount);//付款
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("封装支付请求参数异常");
        }
    }
}
