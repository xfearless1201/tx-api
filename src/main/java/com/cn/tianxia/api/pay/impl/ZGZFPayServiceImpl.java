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
 * @ClassName ZGZFPayServiceImpl
 * @Description 追光支付
 * @author Hardy
 * @Date 2019年2月16日 上午11:13:17
 * @version 1.0.0
 */
public class ZGZFPayServiceImpl implements PayService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(ZGZFPayServiceImpl.class);
    
    private String memberid;//平台商户号 
    
    private String payUrl;//支付地址
    
    private String notifyUrl;//回调地址
    
    private String md5Key;//签名秘钥

    public ZGZFPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("memberid")){
                this.memberid = data.get("memberid");
            }
            
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            
            if(data.containsKey("md5Key")){
                this.md5Key = data.get("md5Key");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[ZGZF]追光支付网银支付开始================START================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity,1);
            //生成签名串
            String sign = generatorSign(data);
            data.put("pay_md5sign",sign);
            logger.info("[ZGZF]追光支付网银支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //生成form表单请求
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[ZGZF]追光支付网银支付生成form表单结果:{}",formStr);
            return PayResponse.wy_form(payEntity.getPayUrl(), formStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZGZF]追光支付扫码支付异常:{}",e.getMessage());
            return PayResponse.wy_write("[ZGZF]追光支付扫码支付异常");
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[ZGZF]追光支付扫码支付开始================START================");
        try {
            
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity,2);
            //生成签名串
            String sign = generatorSign(data);
            data.put("pay_md5sign",sign);
            logger.info("[ZGZF]追光支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //生成form表单请求
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[ZGZF]追光支付扫码支付生成form表单结果:{}",formStr);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZGZF]追光支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[ZGZF]追光支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[ZGZF]追光支付回调验签开始================START================");
        try {
            //获取回调签名
            String sourceSign = data.remove("sign");
            logger.info("[ZGZF]追光支付回调验签获取服务器传参的签名:{}",sourceSign);
            //生成回调加密签名
            String sign = generatorSign(data);
            logger.info("[ZGZF]追光支付回调验签生成签名:{}",sign);
            
            if(sign.equalsIgnoreCase(sourceSign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZGZF]追光支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }

    
    private Map<String,String> sealRequest(PayEntity entity,int type)throws Exception{
        logger.info("[ZGZF]追光支付组装支付请求参数开始================START================");
        try {
            Map<String,String> data = new HashMap<>();
            
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额
            
            data.put("pay_memberid",memberid);//商户号是是平台分配商户号
            data.put("pay_orderid",entity.getOrderNo());//订单号是是上送订单号唯一, 字符长度20
            data.put("pay_applydate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//提交时间是是时间格式：2016-12-26 18:18:18
            if(type == 1){
                //网银支付
                data.put("pay_bankcode","907");//银行编码是是参考后续说明
            }else{
                data.put("pay_bankcode",entity.getPayCode());//银行编码是是参考后续说明 
            }
            data.put("pay_notifyurl",notifyUrl);//服务端通知是是服务端返回地址.（POST返回数据）
            data.put("pay_callbackurl",entity.getRefererUrl());//页面跳转通知是是页面跳转返回地址（POST返回数据）
            data.put("pay_amount",amount);//订单金额是是商品金额
//            data.put("pay_attach","");//附加字段否否此字段在返回时按原样返回 (中文需要url编码)
            data.put("pay_productname","TOP-UP");//商品名称是否
//            data.put("pay_productnum","");//商户品数量否否
//            data.put("pay_productdesc","");//商品描述否否
//            data.put("pay_producturl","");//商户链接地址否否
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZGZF]追光支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[ZGZF]追光支付组装支付请求参数异常");
        }
    }
    
    
    /**
     * 
     * @Description 生成签名
     * @param map
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> map) throws Exception{
        logger.info("[ZGZF]追光支付生成签名开始================START================");
        try {
            
            StringBuffer sb = new StringBuffer();
            //参数排序进行签名
            Map<String,String> data = MapUtils.sortByKeys(map);
            Iterator<String> iterator = data.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = data.get(key);
                if(StringUtils.isBlank(val) || "pay_md5sign".equalsIgnoreCase(key) 
                        || "pay_productname".equalsIgnoreCase(key) || "attach".equalsIgnoreCase(key)) continue;
                sb.append(key).append("=").append(val).append("&");
            }
            String signStr = sb.append("key=").append(md5Key).toString();
            logger.info("[ZGZF]追光支付生成待签名加密串:{}",signStr);
            //签名
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[ZGZF]追光支付生成签名加密串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZGZF]追光支付生成签名异常:{}",e.getMessage());
            throw new Exception("[ZGZF]追光支付生成签名异常");
        }
    }
}
