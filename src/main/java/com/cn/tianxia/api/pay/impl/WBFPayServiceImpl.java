package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName SKPPayServiceImpl
 * @Description 微宝付支付
 * @Date 2019年02月19日 上午09:30:42
 */
public class WBFPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(WBFPayServiceImpl.class);

    private String paySecret;//支付密钥

    private String wyScanPayUrl;//网银扫码支付请求地址

    private String quickPayUrl;//快捷支付请求地址

    private String notifyUrl;//回调地址

    private String md5key;//签名key


    //构造器,初始化参数
    public WBFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("paySecret")) {
                this.paySecret = data.get("paySecret");
            }
            if (data.containsKey("wyScanPayUrl")) {
                this.wyScanPayUrl = data.get("wyScanPayUrl");
            }
            if (data.containsKey("quickPayUrl")) {
                this.quickPayUrl = data.get("quickPayUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("md5key")) {
                this.md5key = data.get("md5key");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    /**
     * 扫码支付
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[WBF]微宝付支付扫码支付开始=================START================");
        try {

            String payUrl;
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名
            String sign = ganeratorSign(data);
            data.put("sign", sign);
            if ("5".equals(payEntity.getPayType())) {
                //银联扫码
                payUrl = wyScanPayUrl;
                //生成请求表单
                String resStr = HttpUtils.toPostForm(data, payUrl);
                if (StringUtils.isBlank(resStr)) {
                    logger.info("[WBF]微宝付支付扫码支付发起HTTP请求无响应结果");
                    return PayResponse.error("[WBF]微宝付支付扫码支付发起HTTP请求无响应结果");
                }
                //Json解析响应结果
                JSONObject resJsonObj = JSONObject.fromObject(resStr);
                logger.info("[WBF]微宝付支付响应信息:" + resJsonObj);
                if (resJsonObj.containsKey("resultCode") && "0000".equals(resJsonObj.getString("resultCode"))) {
                    return PayResponse.sm_qrcode(payEntity, resJsonObj.getString("payMessage"), "下单成功");
                }
            } else if ("7".equals(payEntity.getPayType())) {
                //快捷支付
                payUrl = quickPayUrl;
                logger.info("[WBF]微宝付支付快捷支付请求参数报文:{}", JSONObject.fromObject(data).toString());
                //发起HTTP请求
                String formStr = HttpUtils.generatorForm(data, payUrl);
                logger.info("[WBF]微宝付支付快捷支付生成form表单结果:{}", formStr);
                return PayResponse.sm_form(payEntity, formStr, "下单成功");
            }
            return PayResponse.error("[WBF]微宝付支付扫码支付失败");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WBF]微宝付支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[WBF]微宝付支付扫码支付异常");
        }
    }

    /**
     * 回调
     *
     * @param data
     * @return
     */

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[WBF]微宝付支付回调验签开始================START==============");
        try {
            //获取服务器签名串
            String sourceSign = data.remove("sign");
            //生成签名串
            String sign = ganeratorSign(data);
            logger.info("[WBF]微宝付支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
            if (sourceSign.equalsIgnoreCase(sign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WBF]微宝付支付回调验签异常:{}", e.getMessage());
        }
        return "fail";
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[WBF]微宝付支付组装支付请求参数开始==============START==================");
        try {
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            Date orderTime = new Date();// 订单时间
            String orderTimeStr = new SimpleDateFormat("yyyyMMddHHmmss").format(orderTime);// 订单时间
            data.put("payKey", md5key);//支付key,平台分配
            data.put("orderPrice", amount);//订单金额,[0.01-9999999999.99]
            data.put("outTradeNo", entity.getOrderNo());//订单编号,30不可重复
            data.put("productType", entity.getPayCode());//产品类型,90000103
            data.put("orderTime", orderTimeStr);//下单时间,yyyyMMDDHHMMSS
            data.put("productName", "TOP-UP");//商品名称,
            data.put("orderIp", entity.getIp());//下单IP,50
            data.put("remark", "pay");//支付备注
            data.put("notifyUrl", notifyUrl);//后台消息通知url,
            data.put("returnUrl", entity.getRefererUrl());//页面通知url,
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WBF]微宝付支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[WBF]微宝付支付组装支付请求参数异常");
        }
    }


    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名
     */
    private String ganeratorSign(Map<String, String> data) throws Exception {
        logger.info("[WBF]微宝付支付加密开始=================START==================");
        //回调签名：payMessage=https://qr.95516.com/00010001/62021015266846777036131893614760& payMessageType=0&resultCode=0000&paySecret=5ade00245e0c44bd9e712767b0cb9d18
        try {
            StringBuffer sb = new StringBuffer();
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            Iterator<String> iterator = sortMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = sortMap.get(key);
                sb.append(key).append("=").append(val).append("&");
            }
            sb.delete(sb.length() - 1, sb.length());
            String signStr = sb.append("&paySecret=").append(paySecret).toString();
            logger.info("[WBF]微宝付支付生成待加密串：" + signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[WBF]微宝付支付生成加密串：" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WBF]微宝付支付生产加密异常:{}", e.getMessage());
            throw new Exception("[WBF]微宝付支付生产加密异常");
        }
    }

}
