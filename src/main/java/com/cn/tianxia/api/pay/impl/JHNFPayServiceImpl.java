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
import com.cn.tianxia.api.utils.pay.JHNFUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

/**
 *
 * @ClassName SKPPayServiceImpl
 * @Description 聚汇民付支付
 * @author Hardy Vicky
 * @Date 2019年02月11日 下午14:12:42
 * @version 1.0.0
 */
public class JHNFPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(JHNFPayServiceImpl.class);

    private String sid;//商户号

    private String payUrl;//支付请求地址

    private String notifyUrl;//回调地址

    private String md5Key;//签名key

    private String robin;//轮训

    private String keyId;

    //构造器,初始化参数
    public JHNFPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("sid")){
                this.sid = data.get("sid");
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

            if(data.containsKey("robin")){
                this.robin = data.get("robin");
            }

            if(data.containsKey("keyId")){
                this.keyId = data.get("keyId");
            }
        }
    }

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
        logger.info("[JHNF]聚汇民付支付扫码支付开始============START===========");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名穿
            String sign = generatorSign(data);
            data.put("sign",sign);//是string签名算法，在支付时进行签名算法，详见《支付签名算法》d92eff67b3be05f5e61502
            logger.info("支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String formStr = HttpUtils.generatorForm(data,payUrl);
            return PayResponse.sm_form(payEntity,formStr,"下单失败");

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JHNF]聚汇民付支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[JHNF]聚汇民付支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[JHNF]聚汇民付支付回调验签开始==============START===========");
        try {
            String sourceSign = data.get("sign");
            logger.info("[JHNF]聚汇民付回调原签名串:"+sourceSign);
            String sign = generatorSign(data);
            logger.info("[JHNF]聚汇民付支付回调:本地签名:" + sign + "      服务器签名:" + sourceSign);
            if(sign.equalsIgnoreCase(sourceSign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JHNF]聚汇民付支付回调验签异常:"+e.getMessage());
        }

        return "fail";
    }

    /**
     *
     * @Description 组装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[JHNF]聚汇民付支付组装支付请求参数开始==============START==================");
        try {
            //创建参数存储对象
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额，单位为元
            data.put("account_id",sid);//是string商户ID、在平台首页右边获取商户ID10000
            data.put("content_type","text");//是string请求过程中返回的网页类型，text (扫码支付 )或 json（H5支付）json
            data.put("thoroughfare",entity.getPayCode());//是string初始化支付通道，目前通道：wechat_auto（商户版微信）、alipay_auto（商户版支付宝）、service_auto（服务版微信/支付宝）、 unionpay_auto（云闪付）wechat_auto
            if("service_auto".indexOf(entity.getPayCode()) > 0){
                String serviceType = entity.getPayCode().split(".")[1];
                data.put("type",serviceType);//是string支付类型，该参数在服务版下有效（service_auto），其他可为空参数，微信：1，支付宝：21
            }
            data.put("out_trade_no",entity.getOrderNo());//是string订单信息，在发起订单时附加的信息，如用户名，充值订单号等字段参数2018062668945 最多二十位
            data.put("robin",robin);//是string轮训，2：开启轮训，1：进入单通道模式2
            data.put("keyId",keyId);//是string设备KEY，在商户版列表里面Important参数下的DEVICE Key一项，如果该请求为轮训模式，则本参数无效，本参数为单通道模式785D239777C4DE7739
            data.put("amount",amount);//是string支付金额，在发起时用户填写的支付金额，精确到分1.00
            data.put("callback_url",notifyUrl);//是string异步通知地址，在支付完成时，本平台服务器系统会自动向该地址发起一条支付成功的回调请求, 对接方接收到回调后，必须返回 success ,否则默认为回调失败,回调信息会补发3次。http://www.baidu.com
            data.put("success_url",entity.getRefererUrl());//是string支付成功后网页自动跳转地址，仅在网页类型为text下有效，json会将该参数返回http://www.baidu.com
            data.put("error_url",entity.getRefererUrl());//是string支付失败时，或支付超时后网页自动跳转地址，仅在网页类型为text下有效，json会将该参数返回http://www.baidu.com
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JHNF]聚汇民付支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[JHNF]聚汇民付支付组装支付请求参数异常");
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
        logger.info("[JHNF]聚汇民付支付生成签名串开始=================START==================");
        try{
            StringBuilder sb = new StringBuilder();
            //支付加密串
            sb.append(data.get("amount")).append(data.get("out_trade_no"));
            String signStr = sb.toString();
            logger.info("[JHNF]生待签名串:{}",signStr);
            String md5tolowerStr = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("生成第一步待加密串:{}",md5tolowerStr);
            byte[] rc4bytes = JHNFUtils.encry_RC4_byte(md5tolowerStr,md5Key);
            String sign = MD5Utils.md5(rc4bytes).toLowerCase();
            return sign;
        }catch(Exception e){
            e.printStackTrace();
            logger.info("[JHNF]生产签名异常:{}",e.getMessage());
            throw  new Exception("生产签名异常");
        }
    }
}
