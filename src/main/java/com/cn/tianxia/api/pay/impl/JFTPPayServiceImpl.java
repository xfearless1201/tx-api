package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * @ClassName JFTPPayServiceImpl
 * @Description 俊付通支付
 * @author Hardy
 * @Date 2018年12月29日 下午5:30:04
 * @version 1.0.0
 */
public class JFTPPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(JFTPPayServiceImpl.class);
    
    private String pid;//商户号
    
    private String payUrl;//支付地址
    
    private String secret;//秘钥
    
    private String productUrl;//产品编码url
    
    //构造器,初始化参数
    public JFTPPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("pid")){
                this.pid = data.get("pid");
            }
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("secret")){
                this.secret = data.get("secret");
            }
            if(data.containsKey("productUrl")){
                this.productUrl = data.get("productUrl");
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
        logger.info("[JFTP]竣付通支付扫码支付开始==============START===============");
        try {
            //获取请求参数
            Map<String,String> data = sealRequest(payEntity, 0);
            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("p8_sign", sign);
            logger.info("[JFTP]竣付通支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起支付请求
            String formStr = HttpUtils.generatorFormGet(data, payUrl);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JFTP]竣付通支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[JFTP]竣付通支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[JFTP]竣付通支付回调验签开始===========START==================");
        try {
            //获取原签名串
            String sourceSign = data.get("p10_sign");
            logger.info("[JFTP]竣付通支付回调验签原签名串:{}",sourceSign);
            String sign = generatorSign(data, 0);
            logger.info("[JFTP]竣付通支付回调验签生成加密签名串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JFTP]竣付通支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }
    
    /**
     * 
     * @Description 组装支付请求参数
     * @param entity
     * @param type 1 网银 2 扫码
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity,int type) throws Exception{
        logger.info("[JFTP]竣付通支付组装支付请求参数开始===================START==============");
        try {
            Map<String,String> data = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("p1_yingyongnum",pid);//商户在竣付通平台的应用 ID。 必填
            data.put("p2_ordernumber",entity.getOrderNo());//用户订单号。不可重复，
            data.put("p3_money",amount);//订单金额。支持两种格式，精确到元或精确到分，
            data.put("p6_ordertime",sdf.format(new Date()));//商户订单创建时间。格式 yyyymmddhhmmss。如 20170919105912。  
            data.put("p9_signtype","1");//签名方式。可选值 1。1 代表 MD5 方式。
            if(type == 1){
                data.put("p10_bank_card_code",entity.getPayCode());//网银或卡类编码，点卡支付填写卡类编码，网银支付则填写银行编码。银行编码如：CCB，卡类编码：
            }else{
                data.put("p7_productcode",entity.getPayCode());//终端支付方式，固定值“ZFB”。    必填
            }
            data.put("p14_customname",entity.getuId());//付款人在商户系统中的帐号。请务必填写真实信息，否则将影响后续查单结果。 
            data.put("p16_customip",entity.getIp().replace(".", "_"));//付款人 ip 地址，规定以 192_168_0_253 格式，如果以“192.168.0.253”可能会发生签名错误。   
            if(StringUtils.isBlank(entity.getMobile())){
                data.put("p25_terminal","1");//终端设备类型，可选值 1、2、3 1代表 pc2代 表 ios3代表 android。   
            }else{
                data.put("p25_terminal","3");
            }
            data.put("p26_ext1","1.1");//商户标识：1.1  必填
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JFTP]竣付通支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[JFTP]竣付通支付组装支付请求参数异常");
        }
    }
    
    /**
     * 
     * @Description 签名
     * @param data
     * @param type 1 支付 2 回调
     * @return
     * @throws Exception
     */
    public String generatorSign(Map<String,String> data,int type) throws Exception{
        logger.info("[JFTP]竣付通支付生成签名开始===================START==================");
        try {
            StringBuffer sb = new StringBuffer();
            if(type == 1){
                //支付
                sb.append(pid).append("&");
                sb.append(data.get("p2_ordernumber")).append("&");
                sb.append(data.get("p3_money")).append("&");
                sb.append(data.get("p6_ordertime")).append("&");
                sb.append(data.get("p7_productcode")).append("&");
            }else{
                //回调
                //p1_yingyongnum+"&"+p2_ordernumber+"&"+p3_money+"&"+p4_zfstate+"&"+p5_orderid+
                //"&"+p6_productcode+"&"+p7_bank_card_code+"&"+p8_charset+"&"+p9_signtype+
                //"&"+p11_pdesc+"&"+p13_zfmoney+"&"+key);
                sb.append(data.get("p1_yingyongnum")).append("&");
                sb.append(data.get("p2_ordernumber")).append("&");
                sb.append(data.get("p3_money")).append("&");
                sb.append(data.get("p4_zfstate")).append("&");
                sb.append(data.get("p5_orderid")).append("&");
                sb.append(data.get("p6_productcode")).append("&");
                sb.append(data.get("p7_bank_card_code")).append("&");
                sb.append(data.get("p8_charset")).append("&");
                sb.append(data.get("p9_signtype")).append("&");
                sb.append(data.get("p11_pdesc")).append("&");
                sb.append(data.get("p13_zfmoney")).append("&");
            }
            sb.append(secret);
            String signStr = sb.toString();
            logger.info("[JFTP]竣付通支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[JFTP]竣付通支付生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JFTP]竣付通支付生成签名异常:{}",e.getMessage());
            throw new Exception("[JFTP]竣付通支付生成签名异常");
        }
    }

}
