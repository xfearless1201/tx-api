package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName JDBZPayServiceImpl
 * @Description 聚多宝支付渠道:支付宝  支付宝H5   快捷
 * @Date 2019/4/11 10 36
 **/
public class JDBZPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(JDBZPayServiceImpl.class);

    private String merchId;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String secret;//密钥
    private String version;//版本号

    private static String ret__success = "success";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private boolean verifySuccess = true;//回调验签默认状态为true

    public JDBZPayServiceImpl() {
    }

    public JDBZPayServiceImpl(Map<String, String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("merchId")){
                this.merchId = data.get("merchId");
            }
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }

            if(data.containsKey("secret")){
                this.secret = data.get("secret");
            }
            if(data.containsKey("version")){
                this.version = data.get("version");
            }
        }
    }
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        this.merchId = config.getString("merchId");
        this.notifyUrl = config.getString("notifyUrl");
        this.secret = config.getString("secret");
        this.version = config.getString("version");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[JDBZ]聚多宝支付    商户返回信息：" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("sdpayno");//第三方订单号，流水号
        String order_no = dataMap.get("sdorderno");//支付订单号
        String amount = dataMap.get("total_fee");//实际支付金额
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[JDBZ]聚多宝支付        获取的{} 流水单号为空",trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[JDBZ]聚多宝支付      回调订单金额为空");
            return ret__failed;
        }

        String trade_status = dataMap.get("status");  //第三方支付状态，1 支付成功
        String t_trade_status = "1";//第三方成功状态

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
        processNotifyVO.setPayment("JDBZ");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[JDBZ]聚多宝支付    回调验签失败");
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
        logger.info("[JDBZ]聚多宝支付  扫码支付开始=================================start======================================");
        try {
            Map<String, String> dataMap = sealRequest(payEntity, 2);
            logger.info("[JDB]聚多宝扫码支付请求参数：{}", JSONObject.fromObject(dataMap));
            String responseData = HttpUtils.generatorForm(dataMap, payUrl);
            logger.info("[JDB]聚多宝扫码支付请求参数：{}", responseData);
            return PayResponse.sm_form(payEntity, responseData,"下单成功");

        }catch (Exception e){
            e.printStackTrace();
            return PayResponse.error("扫码支付 异常：" + e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sign = generatorSign(data,2);
        String sourceSign = data.remove("sign");
        if(sign.equalsIgnoreCase(sourceSign)){
            return  ret__success;
        }
        return ret__failed;
    }

    private Map<String, String> sealRequest(PayEntity payEntity, int type){
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());

        dataMap.put("version", version);//1.0版本号
        dataMap.put("customerid", merchId);//11002商户号
        dataMap.put("sdorderno", payEntity.getOrderNo());//4商户订单号不超过30
        dataMap.put("total_fee", amount);//付款金额必须保留2位小数点

        if(1 == type){
            dataMap.put("bankcode",  payEntity.getPayCode());//使用网银时必填附录2
            dataMap.put("paytype", "bank");//alipay支付类型详细看附录1
        }else {
            dataMap.put("paytype", payEntity.getPayCode());//alipay支付类型详细看附录1
        }
        dataMap.put("notifyurl", notifyUrl);//后台接收通知地址
        dataMap.put("returnurl", payEntity.getRefererUrl());//付款成功后跳转地址
        dataMap.put("remark", "TOP-UP");//附加参数原样返回
        dataMap.put("sign", generatorSign(dataMap, 1));//MD5加密验证

        logger.info("[JDBZ]聚多宝支付    HTTP请求参数：" + JSONObject.fromObject(dataMap));
        return dataMap;
    }

    /**
     *
     * @param data
     * @return
     */
    private String generatorSign(Map<String, String> data, int type){
        try{

        StringBuffer sb = new StringBuffer();
        if(1 == type){//支付
            //version={value}&customerid={value}&total_fee={value}&sdorderno={value}&notifyurl
            //={value}&returnurl={value}&apikey
            sb.append("version=").append(data.get("version")).append("&");
            sb.append("customerid=").append(data.get("customerid")).append("&");
            sb.append("total_fee=").append(data.get("total_fee")).append("&");
            sb.append("sdorderno=").append(data.get("sdorderno")).append("&");
            sb.append("notifyurl=").append(data.get("notifyurl")).append("&");
            sb.append("returnurl=").append(data.get("returnurl")).append("&");

        }else if(2 == type){//回调
            //customerid={value}&status={value}&sdpayno={value}&sdorderno={value}&total_fee={value}&paytype={value}&{apikey}
            sb.append("customerid=").append(data.get("customerid")).append("&");
            sb.append("status=").append(data.get("status")).append("&");
            sb.append("sdpayno=").append(data.get("sdpayno")).append("&");
            sb.append("sdorderno=").append(data.get("sdorderno")).append("&");
            sb.append("total_fee=").append(data.get("total_fee")).append("&");
            sb.append("paytype=").append(data.get("paytype")).append("&");

        }else {//订单查询
            //customerid={value}&sdorderno={value}&reqtime={value}&{apikey}
            sb.append("customerid=").append(data.get("customerid")).append("&");
            sb.append("sdorderno=").append(data.get("sdorderno")).append("&");
            sb.append("reqtime=").append(data.get("reqtime")).append("&");
        }

        sb.append(secret);

        logger.info("[JDBZ]聚多宝支付  加密前参数：" + sb.toString());
        return MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
