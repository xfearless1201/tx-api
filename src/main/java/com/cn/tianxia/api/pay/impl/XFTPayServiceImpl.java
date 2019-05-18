package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayConstant;
import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.yx.SHAUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: XFTPayServiceImpl
 * @Description: 信付通支付
 * @Author: Zed
 * @Date: 2018-12-23 16:29
 * @Version:1.0.0
 **/

public class XFTPayServiceImpl implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(XFTPayServiceImpl.class);

    /**
     * 支付地址
     **/
    private String api_url = "https://ebank.xfuoo.com/payment/v1/order/100000000005053-";

    /**
     * 商户号
     **/
    private String merchantId = "100000000005053";

    /**
     * md5key
     **/
    private String key = "u76frli2n84vh2d8p5ddcikrx9q1szlvmxf0brrfg2palne0iwryywddtqd6nv66";

    /**
     * notifyUrl
     **/
    private String notifyUrl = "http://txw8899.com/AMJ/Notify/XFTNotify.do";

    public XFTPayServiceImpl(Map<String, String> pmap) {
        if (pmap != null) {
            if (pmap.containsKey("api_url")) {
                this.api_url = pmap.get("api_url");
            }
            if (pmap.containsKey("merchantId")) {
                this.merchantId = pmap.get("merchantId");
            }
            if (pmap.containsKey("key")) {
                this.key = pmap.get("key");
            }
            if (pmap.containsKey("notifyUrl")) {
                this.notifyUrl = pmap.get("notifyUrl");
            }
        }
    }

    public static void main(String[] args) {
        PayEntity entity = new PayEntity();
        entity.setOrderNo("xftbl12345678911");
        entity.setAmount(100);
        entity.setRefererUrl("http://localhost:8080/xxx");
        entity.setPayCode("CMB");
        XFTPayServiceImpl xftPayService = new XFTPayServiceImpl(null);
        xftPayService.wyPay(entity);
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[XFT]信付通支付网银支付开始===================START=================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity, 1);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);//验签字段 是   MD5加密
            logger.info("[XFT]信付通支付请求报文:" + JSONObject.fromObject(data).toString());
            String formStr = HttpUtils.generatorForm(data, api_url + data.get("orderNo"));
            return PayResponse.wy_form(payEntity.getPayUrl(), formStr);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XFT]信付通支付网银支付异常:" + e.getMessage());
            return PayResponse.error("[XFT]信付通支付网银支付异常:" + e.getMessage());
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[XFT]信付通支付扫码支付开始===================START=================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity, 2);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);//验签字段 是   MD5加密
            logger.info("[XFT]信付通扫码支付请求报文:" + JSONObject.fromObject(data).toString());
            String formStr = HttpUtils.generatorForm(data, api_url + data.get("orderNo"));
            return PayResponse.sm_form(payEntity, formStr, "下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XFT]信付通支付扫码支付异常:" + e.getMessage());
            return PayResponse.error("[XFT]信付通支付扫码支付异常:" + e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[XFT]信付通支付回调验签失败：回调签名为空！");
            return "fail";
        }
        String localSign;
        try {
            localSign = generatorSign(data);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XFT]信付通支付回调验签失败:" + e.getMessage());
            return "fail";
        }

        logger.info("本地签名:" + localSign + "      服务器签名:" + sourceSign);
        if (sourceSign.equalsIgnoreCase(localSign)) {
            return "success";
        }
        return "fail";
    }

    /**
     * @param payEntity
     * @return
     */
    private Map<String, String> sealRequest(PayEntity payEntity, int payType) throws Exception {
        logger.info("[XFT]信付通支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(payEntity.getAmount());//交易金额
            data.put("totalFee", amount);//	M	订单金额，单位为RMB元
            data.put("orderNo", payEntity.getOrderNo());//	M	商户订单号，务必确保在系统中唯一
            data.put("merchantId", merchantId);//	M	支付平台分配的商户ID
//            data.put("buyerEmail","");  //	O	买家 Email
            data.put("charset", "UTF-8");  //	M	参数编码字符集
            if (payType == 1) {
                data.put("isApp", "web");
                data.put("paymethod", "bankPay");  //	M	支付方式，directPay：直连模式；bankPay：收银台模式
                data.put("defaultbank", "BANKPAY");  //	C	网银代码，当支付方式为bankPay时，该值为空；支付方式为directPay时该值必传，值见银行列表

            } else {
                if (PayConstant.CHANEL_KJ.equals(payEntity.getPayType())) {
                    //当支付方式为快捷支付时候，请注意参数： isApp = web defaultbank = QUICKPAY paymethod = bankPay
                    data.put("isApp", "web");
                    data.put("paymethod", "bankPay");  //	M	支付方式，directPay：直连模式；bankPay：收银台模式
                    data.put("defaultbank", payEntity.getPayCode());  //	C	网银代码，当支付方式为bankPay时，该值为空；支付方式为directPay时该值必传，值见银行列表
                } else if (PayConstant.CHANEL_YL.equals(payEntity.getPayType())) {
                    data.put("paymethod", "directPay");
                    data.put("defaultbank", payEntity.getPayCode());
                    data.put("isApp", "web");  // 手机银联扫码  isApp 不能传H5
                } else {
                    data.put("paymethod", "directPay");  //	M	支付方式，directPay：直连模式；bankPay：收银台模式
                    data.put("defaultbank", payEntity.getPayCode());  //	C	网银代码，当支付方式为bankPay时，该值为空；支付方式为directPay时该值必传，值见银行列表
                    if (StringUtils.isBlank(payEntity.getMobile())) {
                        data.put("isApp", "app");//	C	接入方式，当该值传“app”时，表示app接入，返回二维码地址，需商户自行生成二维码；值为“web”时，表示web接入，直接在收银台页面上显示二维码；值为“H5”时，表示手机端html5接入，会在手机端唤醒支付app
                    } else {
                        data.put("isApp", "H5");
                    }
                }
            }
            data.put("paymentType", "1");  //	M	支付类型，固定值为1
//            data.put("riskItem","");//	O	风控字段，默认为空
            data.put("service", "online_pay");//	M	固定值online_pay，表示网上支付
//            data.put("sellerEmail","");//	O	卖家Email
            data.put("title", "top_up");//	M	商品的名称，请勿包含字符
            data.put("body", "e_goods");//	M	商品的具体描述
            data.put("returnUrl", payEntity.getRefererUrl());//	M	支付成功跳转URL，仅适用于支付成功后立即返回商户界面。我司处理完请求后，将立即跳转并把处理结果返回给这个URL
            data.put("notifyUrl", notifyUrl);//	M	商户支付成功后，该地址将收到支付成功的异步通知信息，该地址收到的异步通知作为发货依据
            data.put("signType", "SHA");//	M	签名方式 ：SHA


            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XFT]信付通支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        logger.info("[XFT]信付通支付生成支付签名串开始==================START========================");
        try {
            //签名规则:
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            StringBuilder strBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : sortMap.entrySet()) {

                if ("sign".equals(entry.getKey()) || "signType".equals(entry.getKey())
                        || StringUtils.isBlank(entry.getValue())) {
                    continue;
                }
                strBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            strBuilder.deleteCharAt(strBuilder.length() - 1);
            strBuilder.append(key);
            logger.info("[XFT]信付通支付生成待签名串:" + strBuilder.toString());
            String md5Value = SHAUtils.sign(strBuilder.toString(), "UTF-8");
            logger.info("[XFT]信付通支付生成加密签名串:" + md5Value);
            return md5Value;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XFT]信付通支付生成支付签名串异常:" + e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }
}
