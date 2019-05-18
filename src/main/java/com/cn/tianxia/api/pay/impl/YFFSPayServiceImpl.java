package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
 * 
 * @ClassName YFFSPayServiceImpl
 * @Description 溢发支付2
 * @author Hardy
 * @Date 2018年12月19日 下午5:05:46
 * @version 1.0.0
 */
public class YFFSPayServiceImpl implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(YFFSPayServiceImpl.class);
    
    /** 支付地址 **/
    private String api_url;

    /** h5支付地址 **/
    private static String h5_url;

    /** 商户号 **/
    private String merCode;

    /** md5key **/
    private String md5Key;

    /** 商品名 **/
    private String productDesc;

    /** 有效时间 **/
    private String validityNum;

    /** 通知回调地址 **/
    private String callbackUrl;

    public YFFSPayServiceImpl(Map<String, String> pmap) {
        if (pmap != null) {
            if (pmap.containsKey("api_url")) {
                this.api_url = pmap.get("api_url");
            }
            if (pmap.containsKey("h5_url")) {
                this.h5_url = pmap.get("h5_url");
            }
            if (pmap.containsKey("merCode")) {
                this.merCode = pmap.get("merCode");
            }
            if (pmap.containsKey("md5Key")) {
                this.md5Key = pmap.get("md5Key");
            }
            if (pmap.containsKey("productDesc")) {
                this.productDesc = pmap.get("productDesc");
            }
            if (pmap.containsKey("validityNum")) {
                this.validityNum = pmap.get("validityNum");
            }
            if (pmap.containsKey("callbackUrl")) {
                this.callbackUrl = pmap.get("callbackUrl");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        // String form = scanPay(payEntity);
        // return PayUtil.returnWYPayJson("success", "form", form, pay_url, "");
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[YFFS]溢发2支付扫码支付开始====================START=====================");
        try {
            //获取请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[YFFS]溢发2支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            if(StringUtils.isNotBlank(payEntity.getMobile())){
                this.api_url = h5_url;
            }
            String response = HttpUtils.toPostForm(data, api_url);
            if(StringUtils.isBlank(response)){
                logger.info("[YFFS]溢发2支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[YFFS]溢发2支付扫码支付发起HTTP请求无响应结果");
            }
            logger.info("[YFFS]溢发2支付扫码支付发起HTTP请求响应结果:{}",response);
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            
            if(jsonObject.containsKey("resultCode") && "000000".equals(jsonObject.getString("resultCode"))){
                //成功
                String qrCodeUrl = jsonObject.getString("qrCodeUrl");
                if(StringUtils.isBlank(payEntity.getMobile())){
                    return PayResponse.sm_qrcode(payEntity, qrCodeUrl, "下单成功");
                }
                return PayResponse.sm_link(payEntity, qrCodeUrl, "下单成功");
            }
            
            return PayResponse.error("下单失败:"+response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YFFS]溢发2支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[YFFS]溢发2支付扫码支付异常");
        }
    }
    
    @Override
    public String callback(Map<String, String> data) {
        logger.info("[YFFS]溢发2支付回调验签开始================START================");
        try {
            String serverSign = data.remove("sign");
            String localSign = generatorSign(data);
            logger.info("本地签名:" + localSign + "      服务器签名:" + serverSign);
            if (serverSign.equalsIgnoreCase(localSign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YFFS]溢发2支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }
    
    /**
     * 
     * @Description 组装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[YFFS]溢发2支付组装支付请求参数开始==================START===============");
        try {
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("##").format(entity.getAmount()*100);
            data.put("merCode",merCode);//商户 ID String(20) 必须 给商户分配的唯一标识
            data.put("orderNo",entity.getOrderNo());//订单号 String（50） 必须 自定义，需要唯一
            data.put("orderAmount",amount);//金额 Number(12) 必须 金额以分为单位
            data.put("callbackUrl",callbackUrl);//回调地址 String（500） 必须 异步通知地址
            data.put("showUrl",entity.getRefererUrl());//跳转地址 String（500） 必须 同步地址
            if(StringUtils.isBlank(entity.getMobile())){
                data.put("validityNum",validityNum);//5分钟
            }else{
                data.put("cancelUrl",entity.getRefererUrl());//取消地址 String(500) 必须 取消地址
            }
            data.put("payType",entity.getPayCode());//支付类型 String（6） 必须 获取支付类型
            data.put("productDesc","TOP-UP");//描述 String（120） 必须 描述
            data.put("dateTime",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//交易时间 String（56） 必须 格式：yyyyMMddHHm
//            data.put("sign","");//签名 String 必须 使用商户 key 对报文签名后的值
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YFFS]溢发2支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[YFFS]溢发2支付组装支付请求参数异常");
        }
    }
    
    /**
     * 
     * @Description 生成签名串
     * @param data
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> data) throws Exception{
        logger.info("[YFFS]溢发2支付生成签名串开始================START================");
        try {
            
            StringBuffer sb = new StringBuffer();
            Map<String,String> sortmap = MapUtils.sortByKeys(data);
            Iterator<String> iterator = sortmap.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = sortmap.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                
                sb.append("&").append(key).append("=").append(val);
            }
            sb.append(md5Key);
            String signStr = sb.toString().replaceFirst("&", "");
            logger.info("[YFFS]溢发2支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[YFFS]溢发2支付生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YFFS]溢发2支付生成签名串异常:{}",e.getMessage());
            throw new Exception("[YFFS]溢发2支付生成签名串异常");
        }
    }
}
