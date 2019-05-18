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
 * @ClassName HUIPPayServiceImpl
 * @Description 汇付支付
 * @author Hardy
 * @Date 2019年1月14日 下午6:18:39
 * @version 1.0.0
 */
public class HUIPPayServiceImpl implements PayService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(HUIPPayServiceImpl.class);
    
    private String appKey;//系统分配的应用唯一标识,接口请求过程用到
    private String appSecret;//签名校验key
    private String payUrl;//支付请求地址
    private String notifyUrl;//回调请求地址

    //构造器,初始化参数
    public HUIPPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("appKey")){
                this.appKey = data.get("appKey");
            }
            if(data.containsKey("appSecret")){
                this.appSecret = data.get("appSecret");
            }
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
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
        logger.info("[HUIP]汇付支付扫码支付开始==============START===============");
        try {
        	logger.info("商户号："+appKey+" 密钥："+appSecret+" 支付地址"+payUrl);
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("pay_md5sign", sign);
            logger.info("[HUIP]汇付支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = HttpUtils.generatorForm(data, payUrl);
            if(StringUtils.isBlank(response)){
                logger.info("[HUIP]汇付支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[HUIP]汇付支付扫码支付发起HTTP请求无响应结果");
            }
            logger.info("[HUIP]汇付支付扫码支付发起HTTP请求响应结果:{}",response);
            if(StringUtils.isNotBlank(response)){
                return PayResponse.sm_form(payEntity, response, "下单成功");
            }
            return PayResponse.error("下单失败:"+response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HUIP]汇付支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[HUIP]汇付支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[HUIP]汇付支付回调验签开始=============START==============");
        try {
            //获取原签名串
            String sourceSign = data.remove("sign");
            logger.info("[HUIP]汇付支付回调验签获取原签名串:{}",sourceSign);
            String sign = generatorSign(data);
            logger.info("[HUIP]汇付支付回调验签生成加密签名串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HUIP]汇付支付回调验签异常:{}",e.getMessage());
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
        logger.info("[HUIP]汇付支付组装支付请求参数开始=============START=============");
        try {
            //创建存储对象
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            data.put("pay_memberid",appKey);//商户号
            data.put("pay_orderid",entity.getOrderNo());//订单号
            data.put("pay_applydate",sdf.format(new Date()));//订单日期
            data.put("pay_bankcode",entity.getPayCode());//通道编码
            data.put("pay_notifyurl",notifyUrl);//回调地址
            data.put("pay_amount",amount);//价格，单位元 
            data.put("pay_callbackurl",entity.getRefererUrl());//前端回调地址，支付完成后自动跳转至该页面
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HUIP]汇付支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[HUIP]汇付支付组装支付请求参数异常");
        }
    }
    
    /**
     * 
     * @Description 生成签名串
     * @param data
     * @return
     * @throws Exception
     */
    public String generatorSign(Map<String, String> data) throws Exception{
        logger.info("[HUIP]汇付支付生成签名串开始==============START================");
        try {
            StringBuffer sb = new StringBuffer();
            Map<String,String> map = MapUtils.sortByKeys(data);
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = map.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                sb.append(key).append("=").append(val).append("&");
            }
            sb.append("key=").append(appSecret);
            String signStr = sb.toString();
            logger.info("[HUIP]汇付支付生成待加密签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);//大写
            logger.info("[HUIP]汇付支付生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HUIP]汇付支付生成签名串异常:{}",e.getMessage());
            throw new Exception("[HUIP]汇付支付生成签名串异常");
        }
    }

}
