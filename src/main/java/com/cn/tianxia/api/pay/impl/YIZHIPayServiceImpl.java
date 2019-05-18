package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName YIZHIPayServiceImpl
 * @Description 易智支付
 * @author Hardy
 * @Date 2018年10月30日 上午10:26:39
 * @version 1.0.0
 */
public class YIZHIPayServiceImpl implements PayService{
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(YIZHIPayServiceImpl.class);
    
    private String partner;//商户ID
    
    private String payUrl;//支付地址
    
    private String notifyUrl;//回调地址
    
    private String secret;//秘钥
    
    private String method;//接口名称

    //构造器,初始化参数
    public YIZHIPayServiceImpl(Map<String,String> data) {
        if(data != null && !data.isEmpty()){
            if(data.containsKey("partner")){
                this.partner = data.get("partner");
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
            if(data.containsKey("method")){
                this.method = data.get("method");
            }
        }
    }

    /**
     * 网银
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[YIZHI]易智支付网银支付开始===================START======================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("sign",sign);//MD5签名,32位小写MD5签名值，GB2312编码
            logger.info("[YIZHI]易智支付生成支付请求报文:{}",JSONObject.fromObject(data).toString());
            //发起支付请求
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[YIZHI]易智支付生成form表单结果:{}",formStr);
            return PayUtil.returnWYPayJson("success","form",formStr,payEntity.getPayUrl(),"pay");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YIZHI]易智支付网银支付异常:{}",e.getMessage());
            return PayUtil.returnWYPayJson("error","form","[YIZHI]易智支付异常:"+e.getMessage(),"","pay");
        }
    }

    /**
     * 扫码支付
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[YIZHI]易智支付扫码支付开始===================START======================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("sign",sign);//MD5签名,32位小写MD5签名值，GB2312编码
            logger.info("[YIZHI]易智支付生成支付请求报文:{}",JSONObject.fromObject(data).toString());
            //发起支付请求
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[YIZHI]易智支付生成form表单结果:{}",formStr);
            return PayUtil.returnPayJson("success","1","下单成功!","",0,"",formStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YIZHI]易智支付扫码支付异常:{}",e.getMessage());
            return PayUtil.returnPayJson("error","","[YIZHI]易智支付扫码支付异常!","",0,"",e.getMessage());
        }
    }

    /**
     * 回调
     */
    @Override
    public String callback(Map<String, String> data) {
        logger.info("[YIZHI]易智支付回调验签开始========================START=======================");
        try {
            //获取回调验签原签名串
            String sourceSign = data.get("sign");
            logger.info("[YIZHI]易智支付回调验签原签名串:{}",sourceSign);
            //生成签名串
            String sign = generatorSign(data, 0);
            logger.info("[YIZHI]易智支付回调验签生成签名串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YIZHI]易智支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }

    
    /**
     * 
     * @Description 封装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[YIZHI]易智支付封装支付请求参数开始====================START=================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("version","3.0");//版本号,固定值3.0
            data.put("method",method);//接口名称,yzfapp.online.interface
            data.put("partner",partner);//商户ID,商户id,由易智付分配
            data.put("banktype",entity.getPayCode());//银行类型,银行类型，具体参考附录1,default为跳转到易智付接口进行选择支付
            data.put("paymoney",amount);//金额,单位元（人民币）
            data.put("ordernumber",entity.getOrderNo());//商户订单号,商户系统订单号，该订单号将作为易智付接口的返回数据。该值需在商户系统内唯一，易智付系统暂时不检查该值是否唯一
            data.put("callbackurl",notifyUrl);//下行异步通知地址,下行异步通知的地址，需要以http://开头且没有任何参数
            data.put("hrefbackurl",entity.getRefererUrl());//下行同步通知地址,下行同步通知过程的返回地址(在支付完成后易智付接口将会跳转到的商户系统连接地址)。
            data.put("attach","TOP-UP");//备注信息,备注信息，下行中会原样返回。若该值包含中文，请注意编码
            data.put("isshow","1");//是否显示收银台,该参数为支付宝扫码、微信、QQ钱包专用，默认为1，跳转到网关页面进行扫码，如设为0，则网关只返回二维码图片地址供用户自行调用
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YIZHI]易智支付封装支付请求参数异常:"+e.getMessage());
            throw new Exception("[YIZHI]易智支付封装支付请求参数异常");
        }
    }
    
    /**
     * 
     * @Description 生成签名串
     * @param data
     * @param signType 签名类型
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> data,int signType) throws Exception{
        logger.info("[YIZHI]易智支付生成支付签名开始=======================START======================");
        try {
            
            StringBuffer sb = new StringBuffer();
            if(signType == 1){
                //支付签名
                sb.append("version=").append(data.get("version")).append("&");
                sb.append("method=").append(data.get("method")).append("&");
                sb.append("partner=").append(data.get("partner")).append("&");
                sb.append("banktype=").append(data.get("banktype")).append("&");
                sb.append("paymoney=").append(data.get("paymoney")).append("&");
                sb.append("ordernumber=").append(data.get("ordernumber")).append("&");
                sb.append("callbackurl=").append(data.get("callbackurl"));
            }else{
                sb.append("partner=").append(data.get("partner")).append("&");
                sb.append("ordernumber=").append(data.get("ordernumber")).append("&");
                sb.append("orderstatus=").append(data.get("orderstatus")).append("&");
                sb.append("paymoney=").append(data.get("paymoney"));
            }
            sb.append(secret);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[YIZHI]易智支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[YIZHI]易智支付生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YIZHI]易智支付生成支付签名异常:"+e.getMessage());
            throw new Exception("[YIZHI]易智支付生成支付签名异常");
        }
    }
}
