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
 * 
 * @ClassName JIDAPayServiceImpl
 * @Description 即达支付
 * @author Hardy
 * @Date 2018年12月31日 下午4:40:30
 * @version 1.0.0
 */
public class JIDAPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(JIDAPayServiceImpl.class);
    
    private String merno;//商户号
    
    private String payUrl;//支付请求地址
    
    private String notifyUrl;//回调地址
    
    private String secret;//秘钥
    
    //构造器,初始化参数
    public JIDAPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("merno")){
                this.merno = data.get("merno");
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
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[JIDA]即达支付网银支付开始==============START============");
        try {
            
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity, 1);
            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("sign", sign);
            logger.info("[JIDA]即达支付网银支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            String formStr = HttpUtils.generatorForm(data, payUrl);
            return PayResponse.wy_form(payUrl, formStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JIDA]即达支付网银支付异常:{}",e.getMessage());
            return PayResponse.error("[JIDA]即达支付网银支付异常");
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[JIDA]即达支付扫码支付开始==============START============");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity, 0);
            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("sign", sign);
            String reqParams = JSONObject.fromObject(data).toString();
            logger.info("[JIDA]即达支付扫码支付请求参数报文:{}",reqParams);
            //发起HTTP请求
            String formStr = HttpUtils.generatorForm(data, payUrl);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JIDA]即达支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[JIDA]即达支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[JIDA]即达支付回调验签开始================START=============");
        try {
            
            String sourceSign = data.get("sign");
            logger.info("[JIDA]即达支付回调验签获取原签名串:{}",sourceSign);
            String sign = generatorSign(data, 0);
            logger.info("[JIDA]即达支付回调验签获取签名串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JIDA]即达支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }
    
    
    /**
     * 
     * @Description 组装支付请求参数
     * @param entity
     * @param type 1 网银  2 扫码
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity,int type) throws Exception{
        logger.info("[JIDA]即达支付组装支付请求参数开始=============START===============");
        try {
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("version","1.0");//版本号,1.0    默认1.0
            data.put("customerid",merno);//商户ID,商户后台获取
            data.put("sdorderno",entity.getOrderNo());//商户订单号,AA100000数字或字母，不允许中文、不允许重复
//            data.put("remark","TOP-UP");//商户备注,备注信息异步通知含有该参数，作用自行考虑
            data.put("total_fee",amount);//订单金额,100.01  精确到小数点后两位，例如10.24
            data.put("notifyurl",notifyUrl);//异步通知地址,http://www.xxx.com/notify/    用于向该地址提交订单支付状态
            data.put("returnurl",entity.getRefererUrl());//同步跳转地址,http://www.xxx.com/returl/    用户支付状态改变后跳转该地址，并给予参数做相应响应
            if(type == 1){
                data.put("paytype","bank");//支付通道编号,alipay  详见附录4 银行编码
                data.put("bankcode",entity.getPayCode());//银行编号,ICBC查看银行编号，用于网关PC支付必填的编号参数
            }else{
                data.put("paytype",entity.getPayCode());//支付通道编号,alipay  详见附录4 银行编码
            }
//            data.put("get_code","");//获取微信二维码,无如果只想获取被扫二维码，请设置get_code=1
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JIDA]即达支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[JIDA]即达支付组装支付请求参数异常");
        }
    }
    
   
    /**
     * 
     * @Description 生成签名
     * @param data
     * @param type
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> data,int type) throws Exception{
        logger.info("[JIDA]即达支付生成签名开始=====================START======================");
        try {
            StringBuffer sb = new StringBuffer();
            if(type == 1){
                //支付签名规则
                //version={value}&customerid={value}&total_fee={value}&sdorderno={value}&notifyurl={value}&returnurl={value}&{apikey}
                sb.append("version=").append(data.get("version")).append("&");
                sb.append("customerid=").append(data.get("customerid")).append("&");
                sb.append("total_fee=").append(data.get("total_fee")).append("&");
                sb.append("sdorderno=").append(data.get("sdorderno")).append("&");
                sb.append("notifyurl=").append(data.get("notifyurl")).append("&");
                sb.append("returnurl=").append(data.get("returnurl")).append("&");
            }else{
                //回调签名规则
                //customerid={value}&status={value}&sdpayno={value}&sdorderno={value}&total_fee={value}&paytype={value}&{apikey}
                sb.append("customerid=").append(merno).append("&");
                sb.append("status=").append(data.get("status")).append("&");
                sb.append("sdpayno=").append(data.get("sdpayno")).append("&");
                sb.append("sdorderno=").append(data.get("sdorderno")).append("&");
                sb.append("total_fee=").append(data.get("total_fee")).append("&");
                sb.append("paytype=").append(data.get("paytype")).append("&");
            }
            String signStr = sb.append(secret).toString();
            logger.info("[JIDA]即达支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[JIDA]即达支付生成加密串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JIDA]即达支付生成签名异常:{}",e.getMessage());
            throw new Exception("[JIDA]即达支付生成签名异常");
        }
    }

}
