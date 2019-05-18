package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

/**
 * @ClassName RUYIPayServiceImpl
 * @Description 如意支付
 * @author Hardy
 * @Date 2018年12月30日 上午10:16:10
 * @version 1.0.0
 */
public class RUYIPayServiceImpl implements PayService {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(RUYIPayServiceImpl.class);

    private String merId;// 商户号

    private String payUrl;// 支付地址

    private String notifyUrl;// 回调地址

    private String md5Key;// 秘钥

    // 构造器,初始化参数
    public RUYIPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merId")) {
                this.merId = data.get("merId");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("md5Key")) {
                this.md5Key = data.get("md5Key");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[RUYI]如意支付 网银支付开始=================START================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity, 1);
            //生成签名
            String sign = generatorSign(data, 1);
            data.put("signMsg", sign);
            logger.info("[RUYI]如意支付网银支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[RUYI]如意支付网银支付生成form表单结果:{}",formStr);
            return PayResponse.wy_form(payUrl, formStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[RUYI]如意支付网银支付异常:{}",e.getMessage());
            return PayResponse.wy_write("[RUYI]如意支付网银支付异常");
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[RUYI]如意支付扫码支付开始=================START================");
        try {
            
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity, 0);
            //生成签名
            String sign = generatorSign(data, 1);
            data.put("signMsg", sign);
            logger.info("[RUYI]如意支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[RUYI]如意支付扫码支付生成form表单结果:{}",formStr);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[RUYI]如意支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[RUYI]如意支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[RUYI]如意支付回调验签开始================START==============");
        try {
            
            //获取服务器签名串
            String sourceSign = data.get("signMsg");
            logger.info("[RUYI]如意支付回调验签获取第三方服务器签名串:{}",sourceSign);
            //生成签名串
            String sign = generatorSign(data, 0);
            logger.info("[RUYI]如意支付回调验签生成签名串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[RUYI]如意支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }

    /**
     * @Description 组装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String, String> sealRequest(PayEntity entity, int type) throws Exception {
        logger.info("[RUYI]如意支付组装支付请求参数开始===================START==============");
        try {
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("merId", merId);// 商户号 Y
            data.put("merOrdId", entity.getOrderNo());// 商户网站唯一订单号 Y
            data.put("merOrdAmt", amount);// 订单金额，格式：10.00 Y
            if (type == 1) {
                // 网银支付
                data.put("payType", "10");//
                data.put("bankCode", entity.getPayCode());// 银行代码，参考附录银行代码 Y
            } else {
                data.put("payType", entity.getPayCode());//
                data.put("bankCode", getSmBankCode(entity.getPayCode()));// 银行代码，参考附录银行代码 Y
            }
            data.put("remark", "TOP-UP");// 备注信息，可以随机填写 Y
            data.put("returnUrl", entity.getRefererUrl());// 页面返回地址 Y
            data.put("notifyUrl", notifyUrl);// 后台异步通知 Y
            data.put("signType", "MD5");// 签名方式: MD5, 默认 MD5 Y
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[RUYI]如意支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[RUYI]如意支付组装支付请求参数异常");
        }
    }

    
    /**
     * @Description 签名
     * @param data
     * @param type
     *            1 支付 2 回调
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String, String> data,int type) throws Exception {
        logger.info("[RUYI]如意支付生成签名开始===================START==================");
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("merId=").append(merId).append("&");
            sb.append("merOrdId=").append(data.get("merOrdId")).append("&");
            sb.append("merOrdAmt=").append(data.get("merOrdAmt")).append("&");
            if(type == 1){
                //支付签名规则:md5(merId=xxx&merOrdId=xxx&merOrdAmt=xxx&payType=xxx&bankCode=xxx&remark=xxx
                //&returnUrl=xxx&notifyUrl=xxx&signType=MD5&merKey=xxx)
                sb.append("payType=").append(data.get("payType")).append("&");
                sb.append("bankCode=").append(data.get("bankCode")).append("&");
                sb.append("remark=").append(data.get("remark")).append("&");
                sb.append("returnUrl=").append(data.get("returnUrl")).append("&");
                sb.append("notifyUrl=").append(data.get("notifyUrl")).append("&");
            }else{
                //回调签名规则:MD5(merId=xxx&merOrdId=xxx&merOrdAmt=xxx&sysOrdId=xxx&tradeStatus=xxx&r
                //emark=xxx&signType=xxx&merKey=xxx)
                sb.append("sysOrdId=").append(data.get("sysOrdId")).append("&");
                sb.append("tradeStatus=").append(data.get("tradeStatus")).append("&");
                sb.append("remark=").append(data.get("remark")).append("&");
            }
            sb.append("signType=").append(data.get("signType")).append("&");    
            String signStr = sb.append("merKey=").append(md5Key).toString();
            logger.info("[RUYI]如意支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[RUYI]如意支付生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[RUYI]如意支付生成签名异常:{}", e.getMessage());
            throw new Exception("[RUYI]如意支付生成签名异常");
        }
    }
    
    
    
    /**
     * 
     * @Description 获取支付渠道编码
     * @param key
     * @return
     */
    private String getSmBankCode(String key) {
        String bankcode = "";
        switch (key) {
            case "20":
                // 快捷支付
                bankcode = "QUICK";
                break;
            case "30":
                // 微信扫码支付
                bankcode = "WECHATQR";
                break;
            case "31":
                // 微信wap支付
                bankcode = "WECHATWAP";
                break;
            case "40":
                // 支付宝扫码支付
                bankcode = "ALIPAYQR";
                break;
            case "41":
                // 支付宝 WAP 支付
                bankcode = "ALIPAYWAP";
                break;
            case "50":
                // QQ 钱包扫码支付
                bankcode = "QQWALLET";
                break;
            case "51":
                // QQ 钱包 WAP
                bankcode = "QQWAP";
                break;
            case "60":
                // 京东钱包扫码支付
                bankcode = "JDWALLET";
                break;
            case "61":
                // 京东钱包 WAP 支付
                bankcode = "JDWAP";
                break;
            case "11":
                // 银联扫码支付
                bankcode = "UNIONQR";
                break;
            default:
                break;
        }
        
        return bankcode;
    }
}
