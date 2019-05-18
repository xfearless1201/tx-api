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
 * @ClassName YJZFPayServiceImpl
 * @Description 云捷支付
 * @author Hardy
 * @Date 2019年2月16日 上午11:53:58
 * @version 1.0.0
 */
public class YJZFPayServiceImpl implements PayService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(YJZFPayServiceImpl.class);
    
    private String customerid;//商户编号 
    
    private String payUrl;//支付地址
    
    private String notifyUrl;//回调地址
    
    private String apikey;//签名key

    public YJZFPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("customerid")){
                this.customerid = data.get("customerid");
            }
            
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            
            if(data.containsKey("apikey")){
                this.apikey = data.get("apikey");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[YJZF]云捷支付网银支付开始===============START============");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity, 1);
            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("sign", sign);
            logger.info("[YJZF]云捷支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[YJZF]云捷支付扫码支付生成form表单请求结果:{}",formStr);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YJZF]云捷支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[YJZF]云捷支付扫码支付异常");
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[YJZF]云捷支付扫码支付开始===============START============");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity, 2);
            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("sign", sign);
            logger.info("[YJZF]云捷支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[YJZF]云捷支付扫码支付生成form表单请求结果:{}",formStr);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YJZF]云捷支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[YJZF]云捷支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[YJZF]云捷支付回调验签开始===============START============");
        try {
            //获取签名串
            String sourceSign = data.remove("sign");
            logger.info("[YJZF]云捷支付回调验签获取原签名串:{}",sourceSign);
            String sign = generatorSign(data, 2);
            logger.info("[YJZF]云捷支付回调验签生成回调签名串:{}",sign);
            if(sign.equalsIgnoreCase(sourceSign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YJZF]云捷支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }
    
    /**
     * 
     * @Description 组装支付请求参数
     * @param entity
     * @param type 1 网银 2扫码
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity,int type)throws Exception{
        logger.info("[YJZF]云捷支付组装支付请求参数开始============START==================");
        try {
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("version","1.0");//版本号默认1.0
            data.put("customerid",customerid);//商户编号商户后台获取
            data.put("sdorderno",entity.getOrderNo());//商户订单号可用时间戳加随机数，不要超过18位
            data.put("total_fee",amount);//订单金额精确到小数点后两位，例如10.24
            if(type == 1){
                data.put("paytype","bank");//支付编号详见支付表类目
                data.put("bankcode",entity.getPayCode());//银行编号详见银行编码，一般跳转收银台，
            }else{
                data.put("paytype",entity.getPayCode());//支付编号详见支付表类目
            }
            data.put("paytype",entity.getPayCode());//支付编号详见支付表类目
            data.put("notifyurl",notifyUrl);//异步通知URL不能带有任何参数
            data.put("returnurl",entity.getRefererUrl());//同步跳转URL不能带有任何参数
//            data.put("remark","");//订单备注说明可以带上平台用户名或者充值 单号 可用于评定充值会员
//            data.put("is_qrcode","");//二维码/URL地址如果只想获取被扫二维码，请设置is_qrcode=1，已经废弃不适合非二维码支付或者跳转 is_qrcode=3 时获取支付地址返回json格式
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YJZF]云捷支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[YJZF]云捷支付组装支付请求参数异常");
        }
    }

    /**
     * 
     * @Description 生成签名串
     * @param data
     * @param type
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> data,int type) throws Exception{
        logger.info("[YJZF]云捷支付生成签名开始============START==================");
        try {
            StringBuffer sb = new StringBuffer();
            if(type == 1){
                //支付签名
                //签名规则
                //version={value}&customerid={value}&total_fee={value}&sdorderno={value}&
                //notifyurl={value}&returnurl={value}&{apikey}
                sb.append("version=").append(data.get("version")).append("&");
                sb.append("customerid=").append(data.get("customerid")).append("&");
                sb.append("total_fee=").append(data.get("total_fee")).append("&");
                sb.append("sdorderno=").append(data.get("sdorderno")).append("&");
                sb.append("notifyurl=").append(data.get("notifyurl")).append("&");
                sb.append("returnurl=").append(data.get("returnurl")).append("&");
            }else{
                //回调签名
                //签名规则
                //customerid={value}&status={value}&sdpayno={value}&sdorderno={value}&
                //total_fee={value}&paytype={value}&{apikey}
                sb.append("customerid=").append(data.get("customerid")).append("&");
                sb.append("status=").append(data.get("status")).append("&");
                sb.append("sdpayno=").append(data.get("sdpayno")).append("&");
                sb.append("sdorderno=").append(data.get("sdorderno")).append("&");
                sb.append("total_fee=").append(data.get("total_fee")).append("&");
                sb.append("paytype=").append(data.get("paytype")).append("&");
            }
            String signStr = sb.append(apikey).toString();
            logger.info("[YJZF]云捷支付生成待签名串:{}",signStr);
            //生成小写的32位密文
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YJZF]云捷支付生成签名异常:{}",e.getMessage());
            throw new Exception("[YJZF]云捷支付生成签名异常");
        }
    }
    
}
