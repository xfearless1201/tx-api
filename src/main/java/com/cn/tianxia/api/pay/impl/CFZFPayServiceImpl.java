package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName CFZFPayServiceImpl
 * @Description 财富支付
 * @author Hardy
 * @Date 2018年10月29日 上午11:22:49
 * @version 1.0.0
 */
public class CFZFPayServiceImpl implements PayService{
    
  //日志
    private static final Logger logger = LoggerFactory.getLogger(YIFAPayServiceImpl.class);
    private String mchid;//商户ID
    private String payUrl;//支付地址
    private String notifyUrl;//通知地址
    private String secret;//秘钥
    
    public CFZFPayServiceImpl(Map<String,String> data) {
        if(data != null && !data.isEmpty()){
            if(data.containsKey("mchid")){
                this.mchid = data.get("mchid");
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

    /**
     * 网银支付
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * 扫码支付
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[CFZF]财富支付扫码支付开始======================START============================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //获取签名串，财富支付分两个对接文档，因此签名方式又两种，根据不同的文档类型进行不同的签名方式
            String sign = generatorSign(data);
            data.put("sign",sign);
            logger.info("[CFZF]财富支付请求报文:{},[CFZF]财富支付请求地址:{}",JSONObject.fromObject(data).toString(),payUrl);
            //发起HTTP请求
            String response = HttpUtils.toPostForm(data, payUrl);
            if(StringUtils.isBlank(response)){
                logger.info("[CFZF]财富支付发起HTTP请求无响应结果,请联系第三方支付商资讯请求地址是否通畅!");
                return PayUtil.returnPayJson("error", "2", "下单失败:发起HTTP请求无响应结果,资讯第三方支付商!", "", 0, "", response);
            }
            logger.info("[CFZF]财富支付发起HTTP请求响应结果:"+response);
            //解析支付结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.containsKey("error") && jsonObject.getString("error").equals("0")){
                //0 ：订单提交成功
                String qrcode = jsonObject.getString("qrcode").replace("\\", "");//跳转地址
                if(StringUtils.isBlank(payEntity.getMobile())){
                    qrcode = jsonObject.getString("pay_url").replace("\\", "");//二维码地址
                }
                return PayUtil.returnPayJson("success","4","下单成功!",payEntity.getUsername(),payEntity.getAmount(),payEntity.getOrderNo(),qrcode);
            }
            return PayUtil.returnPayJson("error", "2", "下单失败",payEntity.getUsername(),payEntity.getAmount(),payEntity.getOrderNo(),response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[CFZF]财富支付扫码支付异常!");
            return PayUtil.returnPayJson("error", "2", "下单失败!", "", 0, "", "");
        }
    }

    /**
     * 支付回调
     */
    @Override
    public String callback(Map<String, String> data) {
        logger.info("[CFZF]财富支付回调验签开始===================START===================");
        try {
            //获取回调通知原签名串
            String sourceSign = data.get("sign");
            //生成回调通知签名串
            String sign = generatorSign(data);
            logger.info("[CFZF]财富支付回调原签名串:{},[CFZF]财富支付回调生成签名串:{}",sourceSign,sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[CFZF]财富支付回调验签异常:"+e.getMessage());
        }
        return "faild";
    }
    
    
    /**
     * 
     * @Description 组装支付请求参数
     * @param entity
     * @param type 1 参数一  2 参数二
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[CFZF]财富支付组装支付请求参数开始=======================START====================");
        try {
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("##").format(entity.getAmount()*100);//订单金额,金额为分
            data.put("mchid",mchid);//商户mchid,您的商户唯一标识。
            data.put("amount",amount);//价格,单位：分。例如：1.12元 则为 112
            data.put("pay_type",entity.getPayCode());//支付方式,1：支付宝；2：微信支付
            data.put("notify_url",notifyUrl);//通知回调网址,异步通知地址。例：https://www.xxx.com/notify ,若有带参数需加urlencode。
            data.put("return_url",entity.getRefererUrl());//跳转网址,成功跳转地址。例：https://www.xxx.com/return ,若有带参数需加urlencode。
            data.put("trade_out_no",entity.getOrderNo());//商户自定义订单号,例：201710192541
            if(StringUtils.isBlank(entity.getMobile())){
                //PC端
                data.put("comefrom","pc");//来源,可选值：wap 或 pc 。
            }else{
                data.put("comefrom","wap");//来源,可选值：wap 或 pc 。
            }
            data.put("create_type","1");//创建方式,1 固定为1，可不提交
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[CFZF]财富支付组装支付请求参数异常:"+e.getMessage());
            throw new Exception("组装支付请求参数异常!");
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
        logger.info("[CFZF]财富支付生成签名串开始===========================START=========================");
        try {
            StringBuffer sb = new StringBuffer();
            //签名规则:把必填参数，连Token一起，按参数名字母升序排序。并把参数值拼接在一起。做md5-32位加密，取字符串小写。得到sign。网址类型的参数值不要urlencode。
            data.put("token", secret);
            Map<String,String> sortmap = MapUtils.sortByKeys(data);
            Iterator<String> iterator = sortmap.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = sortmap.get(key);
                
                if(StringUtils.isBlank(val) || key.equalsIgnoreCase("sign") 
                        || key.equalsIgnoreCase("comefrom") || key.equalsIgnoreCase("create_type") 
                        || key.equalsIgnoreCase("error")) continue;
                
                sb.append("&").append(key).append("=").append(val);
            }
            String signStr = sb.toString().replaceFirst("&", "");
            logger.info("[CFZF]财富支付生成支付待签名串:"+signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[CFZF]财富支付生成支付签名串:"+sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[CFZF]财富支付生成签名串异常:"+e.getMessage());
            throw new Exception("生成签名串异常!");
        }
    }


}
