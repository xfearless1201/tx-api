package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

/**
 *
 * @ClassName JBZFPayServiceImpl
 * @Description 快乐付支付
 * @author Jacky
 * @Date 2019年2月4日 下午15:58:58
 * @version 1.0.0
 */
public class KLFPayServiceImpl implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(KLFPayServiceImpl.class);

    private String api_url = "http://fu87z.cn/api/shopApi/order/createorder2";
    private String api_key = "f3UCr3dVWoYJ8ESWuwhyrUOGIm4j3tsQ";
    private String merchant_no = "40201";
    private String notify_url = "http://txw.tx8899.com//Notify/JBZFNotify.do";


    public KLFPayServiceImpl(Map<String,String> map) {
        if(map.containsKey("api_url")){
            this.api_url = map.get("api_url");
        }
        if(map.containsKey("api_key")){
            this.api_key = map.get("api_key");
        }
        if(map.containsKey("merchant_no")){
            this.merchant_no = map.get("merchant_no");
        }
        if(map.containsKey("notify_url")){
            this.notify_url = map.get("notify_url");
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("快乐付支付扫码支付开始================START============");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data,1);
            data.put("sign", sign);
            logger.info("快乐付支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //组装支付表单
            String response = HttpUtils.generatorForm(data, api_url);

            if(StringUtils.isBlank(response)){
                logger.info("快乐付支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("快乐付支付扫码支付发起HTTP请求无响应结果");
            }
            logger.info("快乐付支付扫码支付发起HTTP请求响应结果:{}",response);
            return PayResponse.sm_form(payEntity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("快乐付支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("快乐付支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("快乐付支付回调验签方法 callback(Map<String, String> data = {}  -start ", data);
        try {
            String sourceSign = data.get("sign");
            logger.info("快乐付支付回调验签获取签名原串：{}",sourceSign);
            String sign = generatorSign(data,2);
            logger.info("快乐付支付回调验签生成签名串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("快乐付支付回调验签异常:{}",e.getMessage());
            logger.error(e.getMessage(),e);
        }
        return "faild";
    }

    /**
     *
     * 组装参数
     * @param entity
     * @return
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("快乐付支付参数组装 sealRequest(PayEntity entity = {} -start "+ entity);
        try {
            Map<String,String> dataMap = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额,单位元,保留两位小数
            dataMap.put("shopAccountId",    merchant_no);//商户号
            dataMap.put("shopUserId",   entity.getuId());//商家用户id
            dataMap.put("amountInString",   amount);//金额
            dataMap.put("payChannel", entity.getPayCode());//渠道  ⽀付宝：alipay, ⽀付宝转银⾏：bank
            dataMap.put("shopNo",   entity.getOrderNo());//订单号
            dataMap.put("shopCallbackUrl",  notify_url);//回调地址
           // dataMap.put("target ",  "2")  ; //跳转⽅式 1，⼿机跳转 2、⼆维码展示

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("快乐付支付参数组装失败!",e);
            throw new Exception("快乐付支付参数组装支付请求参数异常",e);
        }
    }

    /**
     * 加密
     * @param map
     * @return
     */
    private String generatorSign(Map<String,String> map,int type) throws Exception{
        logger.info("快乐付支付加密方法 generatorSign(Map<String,String> map = {} -start " + map);
        try {
            //type = 1 请求加密  2 = 回调加密
            StringBuffer stringBuffer = new StringBuffer();
            if(type == 1 ){
                stringBuffer.append(map.get("shopAccountId")).append(map.get("shopUserId")).
                        append(map.get("amountInString")).append(map.get("shopNo")).
                        append(map.get("payChannel")).append(api_key);
            }else{
               //shopAccountId + shopUserId +trade_no +KEY + money +type
                stringBuffer.append(merchant_no).append(map.get("user_id")).
                        append(map.get("trade_no")).append(api_key).
                        append(map.get("money")).append(map.get("type"));
            }
            String signStr = stringBuffer.toString().replaceFirst("&", "");
            logger.info("快乐付支付生成待签名串:{}",signStr);
            //生成MD5并转换大写
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("快乐付支付生成待签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("快乐付支付生成待签名串:{}",e.getMessage());
            throw new Exception("快乐付支付生成签名异常",e);
        }
    }
}
