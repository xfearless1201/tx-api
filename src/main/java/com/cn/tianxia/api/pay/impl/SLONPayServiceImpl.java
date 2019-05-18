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
 * @ClassName SLONPayServiceImpl
 * @Description 顺隆支付
 * @author Hardy
 * @Date 2019年1月25日 下午5:55:01
 * @version 1.0.0
 */
public class SLONPayServiceImpl implements PayService {

    //日志
    private final static Logger logger = LoggerFactory.getLogger(SLONPayServiceImpl.class);
    private String memberid;
    private String payUrl;
    private String notifyUrl;
    private String md5Key;

    public SLONPayServiceImpl(Map<String,String> map) {
        if(map.containsKey("memberid")){
            this.memberid = map.get("memberid");
        }
        if(map.containsKey("payUrl")){
            this.payUrl = map.get("payUrl");
        }
        if(map.containsKey("notifyUrl")){
            this.notifyUrl = map.get("notifyUrl");
        }
        if(map.containsKey("md5Key")){
            this.md5Key = map.get("md5Key");
        }
    }

    
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[SLON]顺隆支付扫码支付开始================START============");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("pay_md5sign", sign);
            logger.info("[SLON]顺隆支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //生成form请求表单
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[SLON]顺隆支付扫码支付生成form请求表单结果:{}",formStr);
            return PayResponse.sm_form(payEntity, formStr, "扫码支付下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SLON]顺隆支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[SLON]顺隆支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[SLON]顺隆支付回调验签开始============START==============");
        try {
            
            String sourceSign = data.get("sign");
            logger.info("[SLON]顺隆支付回调验签获取原签名串:{}",sourceSign);
            
            String sign = generatorSign(data);
            logger.info("[SLON]顺隆支付回调验签生成签名串:{}",sign);
            
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SLON]顺隆支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }

    /**
     * 组装参数
     * @param payEntity
     * @return
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[SLON]顺隆支付组装支付请求参数开始==============START==============");
        try {
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("pay_memberid",memberid);//商户号是是平台分配商户号
            data.put("pay_orderid",entity.getOrderNo());//订单号是是上送订单号唯一, 字符长度20
            data.put("pay_applydate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//提交时间是是时间格式：2016-12-26 18:18:18
            data.put("pay_bankcode",entity.getPayCode());//银行编码是是参考后续说明
            data.put("pay_notifyurl",notifyUrl);//服务端通知是是服务端返回地址.（POST返回数据）
            data.put("pay_callbackurl",entity.getRefererUrl());//页面跳转通知是是页面跳转返回地址（POST返回数据）
            data.put("pay_amount",amount);//订单金额是是商品金额
            data.put("pay_productname","TOP-UP");//商品名称是否
//            data.put("pay_attach","");//附加字段否否此字段在返回时按原样返回 (中文需要url编码)
//            data.put("pay_productnum","");//商户品数量否否
//            data.put("pay_productdesc","");//商品描述否否
//            data.put("pay_user","");//支付用户标识，如用户用户名或者用UID唯一标识否否
//            data.put("pay_producturl","");//商户链接地址否否
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SLON]顺隆支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[SLON]顺隆支付组装支付请求参数异常");
        }
    }
    
    /**
     * 加密 
     * @param map
     * @return
     */
    public String generatorSign(Map<String,String> map) throws Exception{
        logger.info("[SLON]顺隆支付生成签名开始============START=============");
        try {
            
            StringBuffer sb = new StringBuffer();
            //排序
            Map<String,String> data = MapUtils.sortByKeys(map);
            Iterator<String> iterator = data.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = data.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key) 
                        || "pay_productname".equalsIgnoreCase(key) || "attach".equalsIgnoreCase(key)) continue;
                
                sb.append(key).append("=").append(val).append("&");
            }
            
            String signStr = sb.append("key=").append(md5Key).toString();
            logger.info("[SLON]顺隆支付生成待签名串:{}",signStr);

            String sign = MD5Utils.md5toUpCase_32Bit(signStr);//大写
            logger.info("[SLON]顺隆支付生成签名串:{}",sign);
            
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SLON]顺隆支付生成签名异常:{}",e.getMessage());
            throw new Exception("[SLON]顺隆支付生成签名异常");
        }
    }
    
    
}
