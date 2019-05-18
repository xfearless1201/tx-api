package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
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
 * @ClassName LOVEPayServiceImpl
 * @Description 爱付支付接口
 * @author Hardy
 * @Date 2019年1月11日 下午8:34:14
 * @version 1.0.0
 */
public class LOVEPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(LOVEPayServiceImpl.class);
    
    private String merchantNo;//商户号
    
    private String payUrl;//支付地址
    
    private String notifyUrl;//回调地址
    
    private String md5Key;//签名key

    //构造器,初始化参数
    public LOVEPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            this.merchantNo = StringUtils.isBlank(data.get("merchantNo"))?null:data.get("merchantNo");
            this.payUrl = StringUtils.isBlank(data.get("payUrl"))?null:data.get("payUrl");
            this.notifyUrl = StringUtils.isBlank(data.get("notifyUrl"))?null:data.get("notifyUrl");
            this.md5Key = StringUtils.isBlank(data.get("md5Key"))?null:data.get("md5Key");
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[LOVE]爱付支付网银支付开始============START=============");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[LOVE]爱付支付网银支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起支付请求
            String response = HttpUtils.toPostForm(data, payUrl);
            if(StringUtils.isBlank(response)){
                logger.info("[LOVE]爱付支付网银支付发起HTTP请求无响应结果");
                return PayResponse.error("[LOVE]爱付支付网银支付发起HTTP请求无响应结果");
            }
            
            logger.info("[LOVE]爱付支付网银支付发起HTTP请求响应结果:{}",response);
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response).getJSONObject("data");
            if(jsonObject.containsKey("status") && "100".equals(jsonObject.getString("status"))){
                //发起支付成功
                String deposit_url = jsonObject.getString("deposit_url");
                return PayResponse.wy_link(deposit_url);
            }
            return PayResponse.wy_write("下单失败:"+response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LOVE]爱付支付网银支付异常:{}",e.getMessage());
            return PayResponse.wy_write("[LOVE]爱付支付网银支付异常:"+e.getMessage());
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[LOVE]爱付支付扫码支付开始============START=============");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[LOVE]爱付支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起支付请求
            String response = HttpUtils.toPostForm(data, payUrl);
            if(StringUtils.isBlank(response)){
                logger.info("[LOVE]爱付支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[LOVE]爱付支付扫码支付发起HTTP请求无响应结果");
            }
            
            logger.info("[LOVE]爱付支付扫码支付发起HTTP请求响应结果:{}",response);
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response).getJSONObject("data");
            if(jsonObject.containsKey("status") && "100".equals(jsonObject.getString("status"))){
                //发起支付成功
                String deposit_url = jsonObject.getString("deposit_url");
                return PayResponse.sm_link(payEntity, deposit_url, "下单成功");
            }
            return PayResponse.error("下单失败:"+response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LOVE]爱付支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[LOVE]爱付支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[LOVE]爱付支付回调验签开始=============START==================");
        try {
            //获取回调验签签名串
            String sourceSign = data.get("sign");
            logger.info("[LOVE]爱付支付回调验签获取原签名串:{}",sourceSign);
            String sign = generatorSign(data);
            logger.info("[LOVE]爱付支付回调验签生成签名串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LOVE]爱付支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }
    
    
    /**
     * 
     * @Description 封装支付请求参数
     * @param entity 
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[LOVE]爱付支付组装支付请求参数开始============START==============");
        try {
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("merchant_no",merchantNo);//商户号
            data.put("amount",amount);//申请入款金额(元)
            data.put("merchant_billno",entity.getOrderNo());//商户订单号,唯一
            data.put("merchant_remark",entity.getuId());//备注,通知及查询时均会返回
            data.put("bank",entity.getPayCode());//银行,见附录
            data.put("return_url",entity.getRefererUrl());//跳转地址
            data.put("notify_url",notifyUrl);//通知地址
//            data.put("payer_name","");//付款人,转账收款时需要
            data.put("sign_way","md5");//加密方式，md5
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LOVE]爱付支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[LOVE]爱付支付组装支付请求参数异常");
        }
    }
    
    /**
     * 
     * @Description 生成签名串
     * @param map
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> map) throws Exception{
        logger.info("[LOVE]爱付支付生成签名串开始===============START===================");    
        try {
            //签名规则:
            //步骤一. 将所有请求参数按照键名顺序排序以 组成query string格式，不要进行urlencode
            //amount=100&bank=WECHAT&merchant_billno=xxx&merchant_no=123&merchant_remark=yyy
            //&notify_url=http://baidu.com&return_url=http://baidu.com&sign_way=md5
            //步骤二. 第一步所得字符串的末尾拼上md5秘钥
            //假设md5秘钥为md5_key,则待加密字符串将为amount=100&bank=WECHAT&merchant_billno=xxx
            //&merchant_no=123&merchant_remark=yyy&notify_url=http://baidu.com&return_url=http://baidu.com&sign_way=md5md5_key
            //将第二步所得字符串进行32位小写md5得到签名
            
            StringBuffer sb = new StringBuffer();
            Map<String,String> data = MapUtils.sortByKeys(map);
            Iterator<String> iterator = data.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = data.get(key);
                if("sign".equalsIgnoreCase(key)) continue;
                sb.append("&").append(key).append("=").append(val);
            }
            
            String signStr = sb.append(md5Key).toString().replaceFirst("&", "");
            logger.info("[LOVE]爱付支付生成待加密签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[LOVE]爱付支付生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LOVE]爱付支付生成签名串异常:{}",e.getMessage());
            throw new Exception("[LOVE]爱付支付生成签名串异常");
        }
    } 

}
