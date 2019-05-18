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

import net.sf.json.JSONObject;

/**
 * @ClassName: JIANPayServiceImpl
 * @Description: 简付支付(第三方系统升级覆盖旧版)
 * @Author: Zed
 * @Date: 2019-01-07 10:04
 * @Version:1.0.0
 **/

public class JIANPayServiceImpl implements PayService {

    private static final Logger logger = LoggerFactory.getLogger(JIANPayServiceImpl.class);

    private String fxid = "4566";
    private String key = "654E5775E731CB9A2582328F120CB0AE";
    private String payUrl = "http://api.jfqbpay.cn";
    private String notifyUrl = "http://txw.tx8899.com/TAS/Notify/YISZFNotify.do";

    public JIANPayServiceImpl(Map<String,String> map) {
        if(map != null && !map.isEmpty()){
            if(map.containsKey("fxid")){
                this.fxid = map.get("fxid");
            }
            if(map.containsKey("notifyUrl")){
                this.notifyUrl = map.get("notifyUrl");
            }
            if(map.containsKey("key")){
                this.key = map.get("key");
            }
            if(map.containsKey("payUrl")){
                this.payUrl = map.get("payUrl");
            }
        }

    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[JIAN]简付支付扫码支付开始===============START==============");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data,1);
            data.put("fxsign",sign);
            logger.info("[JIAN]简付支付扫码支付请求参数:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = HttpUtils.toPostForm(data, payUrl);
            if(StringUtils.isBlank(response)){
                logger.info("[JIAN]简付支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[JIAN]简付支付扫码支付发起HTTP请求无响应结果");
            }
            logger.info("[JIAN]简付支付扫码支付发起HTTP请求响应结果:{}",response);
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.containsKey("status") && "1".equals(jsonObject.getString("status"))){
                String qrcode = jsonObject.getString("payurl");
                if(StringUtils.isBlank(payEntity.getMobile())){
                    //PC端
                    return PayResponse.sm_qrcode(payEntity, qrcode, "下单成功");
                    
                }
                return PayResponse.sm_link(payEntity, qrcode, "下单成功");
            }
            return PayResponse.error("下单失败:"+response);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[JIAN]易收支付扫码支付下单失败"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[JIAN]简付支付回调验签开始==============START===============");
        try {
            String sourceSign = data.remove("fxsign");
            logger.info("[JIAN]简付支付回调验签获取原签名串:{}",sourceSign);
            String sign = generatorSign(data, 2);
            logger.info("[JIAN]简付支付回调验签生成签名串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JIAN]简付支付回调验签异常:{}",e.getMessage());
        }
        return "fail";
    }

    /**
     *
     * @Description 封装支付请求参数(系统升级为新的请求参数)
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[JIAN]简付支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//单位为元
            data.put("fxid",fxid);//商务号是唯一号，由Ccpay提供
            data.put("fxddh",entity.getOrderNo());//商户订单号是仅允许字母或数字类型,不超过22个字符，不要有中文
            data.put("fxdesc","TOP-UP");//商品名称是utf-8编码
            data.put("fxfee",amount);//支付金额是请求的价格(单位：元) 可以0.01元
            data.put("fxnotifyurl",notifyUrl);//异步通知地址是异步接收支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
            data.put("fxbackurl",entity.getRefererUrl());//同步通知地址是支付成功后跳转到的地址，不参与签名。
            data.put("fxpay",entity.getPayCode());//请求类型 【支付宝wap：zfbwap】【支付宝扫码：zfbsm】【微信扫码：wxsm】【微信跳转：weixintz】【微信H5：wechatH5】是请求支付的接口类型。
            data.put("fxip",entity.getIp());//支付用户IP地址是用户支付时设备的IP地址
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JIAN]简付支付封装请求参数异常:"+e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * 
     * @Description 生成支付签名串
     * @param data
     * @param type 1 支付签名  2 回调签名
     * @return
     * @throws Exception
     */
    public String generatorSign(Map<String,String> data,int type) throws Exception{
        logger.info("[JIAN]简付支付生成签名串开始==================START========================");
        try {
            StringBuilder sb = new StringBuilder();
            if(type == 1){
              //签名规则:签名【md5(商务号+商户订单号+支付金额+异步通知地址+商户秘钥)】是通过签名算法计算得出的签名值。
                sb.append(fxid).append(data.get("fxddh")).append(data.get("fxfee"));
                sb.append(notifyUrl).append(key);
            }else{
              //签名规则:签名【md5(订单状态+商务号+商户订单号+支付金额+商户秘钥)】
                sb.append(data.get("fxstatus")).append(fxid).append(data.get("fxddh"));
                sb.append(data.get("fxfee")).append(key);
            }
            String signStr = sb.toString();
            logger.info("[JIAN]简付支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[JIAN]简付支付生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JIAN]简付支付生成支付签名串异常:"+e.getMessage());
            throw new Exception("[JIAN]简付支付生成签名串异常!");
        }
    }
}
