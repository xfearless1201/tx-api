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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Auther: zed
 * @Date: 2019/1/20 11:04
 * @Description: 资海支付
 */
public class ZIHAIPayServiceImpl implements PayService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(ZIHAIPayServiceImpl.class);

    private String pay_memberid;//商户号

    private String payUrl;//支付请求地址

    private String notifyUrl;//回调地址

    private String md5Key;//签名秘钥

    //构造器,初始化参数
    public ZIHAIPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("pay_memberid")){
                this.pay_memberid = data.get("pay_memberid");
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[ZIHAI]资海支付扫码支付开始==============START====================");
        try{
            Map<String,String> map = sealRequest(payEntity);

            String sign = generatorSign(map);
            map.put("pay_md5sign", sign);

            String formStr = HttpUtils.generatorForm(map, payUrl);
            logger.info("[ZIHAI]资海支付扫码支付请求参数={}",formStr);

            return PayResponse.sm_form(payEntity,formStr,"下单成功");

        }catch(Exception e){
            e.printStackTrace();
            logger.error("[ZIHAI]资海支付扫码支付出现错误，错误内容:{}",e.getMessage());
            return PayResponse.error("[ZIHAI]资海支付扫码支付出现错误:"+ e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (org.apache.commons.lang.StringUtils.isBlank(sourceSign)) {
            logger.info("[ZIHAI]资海支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data)) {
            return "success";
        }
        return "fail";
    }

    private boolean verifyCallback(String sign,Map<String,String> data) {


        String localSign;
        try {
            localSign = generatorSign(data);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[ZIHAI]资海支付生成支付签名串异常:"+ e.getMessage());
            return false;
        }
        return sign.equalsIgnoreCase(localSign);
    }

    /**
     *
     * @Description 组装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity)throws Exception{
        logger.info("[ZIHAI]资海支付组装支付请求参数开始===============START=================");
        try {
            DateFormat payDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String amount = new DecimalFormat("##").format(entity.getAmount());//订单金额,单位分

            Map<String,String> map = new HashMap<>();
            map.put("pay_memberid", pay_memberid);
            map.put("pay_orderid", entity.getOrderNo());
            map.put("pay_applydate", payDate.format(new Date()));
            map.put("pay_notifyurl", notifyUrl);
            map.put("pay_callbackurl", entity.getRefererUrl());
            map.put("pay_productname","TOP_UP");
            map.put("pay_amount", amount);
            map.put("pay_bankcode", entity.getPayCode());

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZIHAI]资海支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[ZIHAI]资海支付组装支付请求参数异常");
        }
    }

    /**
     *
     * @Description 生成签名串
     * @param data
     * @return
     * @throws Exception
     */
    public String generatorSign(Map<String,String> data) throws Exception{
        logger.info("[ZIHAI]资海支付生成签名串开始===============START===================");
        try {

            Map<String,String> sortMap = MapUtils.sortByKeys(data);
            StringBuffer signStr = new StringBuffer();

            Iterator<String> iterator = sortMap.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = sortMap.get(key);
                if(StringUtils.isEmpty(val)|| "sign".equalsIgnoreCase(key) || "pay_md5sign".equalsIgnoreCase(key)
                        || "pay_productname".equalsIgnoreCase(key) || "attach".equalsIgnoreCase(key)){
                    continue;
                }
                //支付加密方式
                signStr.append(key).append("=").append(val).append("&");
            }

            signStr.append("key=").append(md5Key);
            logger.info("[ZIHAI]资海支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr.toString());
            if (StringUtils.isBlank(sign)) {
                logger.error("[ZIHAI]资海支付生成签名串为空");
                return null;
            }
            logger.info("[ZIHAI]资海支付生成加密签名串:{}",sign.toLowerCase());
            return sign.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZIHAI]资海支付生成签名串异常:{}",e.getMessage());
            throw new Exception("[ZIHAI]资海支付生成签名串异常");
        }
    }
}
