package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
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
 * @ClassName EASYPayServiceImpl
 * @Description 通支付2
 * @author Hardy
 * @Date 2019年1月25日 下午3:03:53
 * @version 1.0.0
 */
public class EASYPayServiceImpl implements PayService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(EASYPayServiceImpl.class);
    
    private String fxid;//商户号
    
    private String payUrl;//支付请求地址
    
    private String notifyUrl;//回调请求地址
    
    private String md5Key;//签名key
    
    //构造器,初始化参数
    public EASYPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("fxid")){
                this.fxid = data.get("fxid");
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
        logger.info("[EASY]通支付2扫码支付开始=============START=================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("fxsign",sign);
            
            logger.info("[EASY]通支付2扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            
            //发起hTTP请求
            String response = HttpUtils.toPostForm(data, payUrl);
            
            if(StringUtils.isBlank(response)){
                logger.info("[EASY]通支付2扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[EASY]通支付2扫码支付发起HTTP请求无响应结果");
            }
            logger.info("[EASY]通支付2扫码支付发起HTTP请求响应结果:{}",response);
            
            
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.containsKey("status") && "1".equals(jsonObject.getString("status"))){
                //下单成功
                String payurl = jsonObject.getString("payurl");
                
                if(StringUtils.isBlank(payEntity.getMobile())){
                    //PC端，扫码
                    return PayResponse.sm_qrcode(payEntity, payurl, "扫码支付下单成功");
                }
                
                return PayResponse.sm_link(payEntity, payurl, "H5支付下单成功");
            }
            
            return PayResponse.error("下单失败:" + response);
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[EASY]通支付2扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[EASY]通支付2扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[EASY]通支付2回调验签开始==============START==============");
        try {
            
            String sourceSign = data.get("fxsign");
            logger.info("[EASY]通支付2回调验签获取原签名串:{}",sourceSign);
            String sign = generatorSign(data, 2);
            logger.info("[EASY]通支付2回调验签生成签名串:{}",sign);
            
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[EASY]通支付2回调验签异常:{}",e.getMessage());
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
        logger.info("[EASY]通支付2组装支付请求参数开始=====================START===============");
        try {
            
            Map<String,String> data = new HashMap<>();
            
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额(单位：元) 可以0.01元
            
            data.put("fxid",fxid);//商务号
            data.put("fxddh",entity.getOrderNo());//商户订单号
            data.put("fxdesc","TOP-UP");//商品名称
            data.put("fxfee",amount);//支付金额(单位：元) 可以0.01元
            data.put("fxnotifyurl",notifyUrl);//异步通知地址
            data.put("fxbackurl",entity.getRefererUrl());//同步通知地址
            data.put("fxpay",entity.getPayCode());//请求类型 【微信公众号：wxgzh】【微信扫码：wxsm】【支付宝扫码：zfbsm】 
//            data.put("fxnotifystyle","");//异步数据类型
//            data.put("fxattch","");//附加信息
//            data.put("fxsmstyle","");//扫码模式
//            data.put("fxbankcode","");//银行类型
//            data.put("fxfs","");//反扫付款码数字
//            data.put("fxuserid","");//快捷模式绑定商户id
//            data.put("fxsign","");//签名【md5(商务号+商户订单号+支付金额+异步通知地址+商户秘钥)】
            data.put("fxip",entity.getIp());//支付用户IP地址
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[EASY]通支付2组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[EASY]通支付2组装支付请求参数异常");
        }
    }

    
    /**
     * 
     * @Description 生成签名串
     * @param data
     * @param type 1 支付签名  2 回调签名
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> data,int type) throws Exception{
        logger.info("[EASY]通支付2生成签名串开始=================START==============");
        try {
            
            StringBuffer sb = new StringBuffer();
            if(type == 1){
                //支付签名规则 签名【md5(商务号+商户订单号+支付金额+异步通知地址+商户秘钥)】
                sb.append(fxid).append(data.get("fxddh"));
                sb.append(data.get("fxfee")).append(data.get("fxnotifyurl"));
            }else{
                //回调请求签名规则:签名【md5(订单状态+商务号+商户订单号+支付金额+商户秘钥)
                sb.append(data.get("fxstatus")).append(data.get("fxid"));
                sb.append(data.get("fxddh")).append(data.get("fxfee"));
            }
            String signStr = sb.append(md5Key).toString();
            logger.info("[EASY]通支付2生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[EASY]通支付2生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[EASY]通支付2生成签名串异常:{}",e.getMessage());
            throw new Exception("[EASY]通支付2生成签名串异常");
        }
    }
}
