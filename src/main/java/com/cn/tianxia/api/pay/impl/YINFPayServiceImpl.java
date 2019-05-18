package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.RandomUtils;
import com.cn.tianxia.api.utils.pay.XmlUtils;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName YINFPayServiceImpl
 * @Description 盈付支付
 * @author Hardy
 * @Date 2018年12月29日 上午10:28:02
 * @version 1.0.0
 */
public class YINFPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(YINFPayServiceImpl.class);
    
    private String mchId;
    
    private String payUrl;
    
    private String notifyUrl;
    
    private String md5Key;

    //构造器,初始化参数
    public YINFPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("mchId")){
                this.mchId = data.get("mchId");
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
        logger.info("[YINF]盈付支付扫码支付开始=============START=============");
        try {
            
            //获取请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[YINF]盈付支付扫码支付生成原始请求参数报文:{}",JSONObject.fromObject(data).toString());
            //生成xml参数
            String xmlStr = createXmlParams(data);
            logger.info("[YINF]盈付支付扫码支付生成xml请求参数报文:{}",xmlStr);
            //发起支付请求
            String response = HttpUtils.toPostForm(xmlStr, payUrl);
            if(StringUtils.isBlank(response)){
                logger.info("[YINF]盈付支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[YINF]盈付支付扫码支付发起HTTP请求无响应结果");
            }
            //解析响应结果
            JSONObject jsonObject = XmlUtils.parseXml(response);
            if(jsonObject.containsKey("result_code") && "0".equals(jsonObject.getString("status"))){
                //成功
                String codeUrl = jsonObject.getJSONObject("pay_info").getString("codeUrl");
                if(StringUtils.isBlank(payEntity.getMobile())){
                    return PayResponse.sm_qrcode(payEntity, codeUrl, "下单成功");
                }
                return PayResponse.sm_link(payEntity, codeUrl, "下单成功");
            }
            return PayResponse.error("下单失败:"+response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YINF]盈付支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[YINF]盈付支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[YINF]盈付支付回调验签开始=================START==============");
        try {
            String sourceSign = data.get("sign");
            logger.info("[YINF]盈付支付回调验签原签名串:{}",sourceSign);
            String sign = generatorSign(data);
            logger.info("[YINF]盈付支付回调验签生成签名串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YINF]盈付支付回调验签异常:{}",e.getMessage());
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
        logger.info("[YINF]盈付支付组装支付请求参数开始=============START================");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date timeStart = new Date();//订单生成时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timeStart);
            calendar.add(Calendar.MINUTE, 10);
            Date timeExpire = calendar.getTime();//超时时间,在订单时间上加10分钟
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("##").format(entity.getAmount()*100);//订单金额，以分为单位
            data.put("service",entity.getPayCode());//接口类型
            data.put("version","2.0");//版本号,version 默认值是 2.0
            data.put("sign_type","MD5");//签名方式,取值：MD5 默认：MD5
            data.put("mch_id",mchId);//商户号,由盈支付分配
            data.put("out_trade_no",entity.getOrderNo());//商户订单号,商户系统内部的订单号 ,32 个字符内、 可包含字母,确保 在商户系统唯一
            data.put("body","TOP-UP");//商品描述
            data.put("total_fee",amount);//总金额,总金额，以分为单位，不允许包含任何字、 符号
            data.put("mch_create_ip",entity.getIp());//终端 IP,订单生成的机器 IP
            data.put("notify_url",notifyUrl);//通知地址,接收盈支付支付系统的通知的 URL，
            data.put("callback_url",entity.getRefererUrl());//前台地址,交易完成后跳转的 URL，
            data.put("time_start",sdf.format(timeStart));//订单生成时间,格式为 yyyyMMddHHmmss，如 2009 年 12 月 25 日 9 点 10 分 10 秒表示为
            data.put("time_expire",sdf.format(timeExpire));//订单超时时间,订单失效时间，格式为 yyyyMMddHHmmss，如 2009 年 12 月 27 日 9 点 10 分 10 秒表示为
            data.put("goods_tag","");//商品标记,微信平台配置的商品标记，用于 优惠券或者满减使用
            data.put("nonce_str",RandomUtils.generateString(10));//随机字符串,不长于 32 位
//            data.put("bank_abbr","");//银行简称,如：CMB（代扣必填）
//            data.put("id_no","");//身份证号,（代扣必填）
//            data.put("card_no","");//银行卡号,（代扣必填）
//            data.put("card_name","");//持卡人名称,（代扣必填）
//            data.put("phone_number","");//手机号码,银行预留手机号（代扣必填）
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YINF]盈付支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[YINF]盈付支付组装支付请求参数异常");
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
        logger.info("[YINF]盈付支付生成签名串开始==================START=================");
        try {
            StringBuffer sb = new StringBuffer();
            //排序
            Map<String,String> sortmap = MapUtils.sortByKeys(data);
            Iterator<String> iterator = sortmap.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                
                String val = sortmap.get(key);
                
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                
                sb.append(key).append("=").append(val).append("&");
            }
            
            sb.append("key=").append(md5Key);
            
            String signStr = sb.toString();
            logger.info("[YINF]盈付支付生成待加密签名串：{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);//大写
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YINF]盈付支付生成签名串异常:{}",e.getMessage());
            throw new Exception("[YINF]盈付支付生成签名串异常");
        }
    }
    
    /**
     * 
     * @Description 生成xml请求参数
     * @param data
     * @return
     * @throws Exception
     */
    private String createXmlParams(Map<String,String> data) throws Exception{
        logger.info("[YINF]盈付支付生成xml请求参数开始================START===================");
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("<xml>");
            Iterator<String> iterator = data.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = data.get(key);
                if(StringUtils.isBlank(val)) continue;
                sb.append("<").append(key).append(">");
                sb.append(val);
                sb.append("</").append(key).append(">");
            }
            sb.append("</xml>");
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YINF]盈付支付生成xml请求参数异常:{}",e.getMessage());
            throw new Exception("[YINF]盈付支付生成xml请求参数异常");
        }
    }

}
