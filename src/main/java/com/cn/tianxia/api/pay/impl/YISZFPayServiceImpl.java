package com.cn.tianxia.api.pay.impl;

import java.security.NoSuchAlgorithmException;
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
 * @ClassName: YISZFPayServiceImpl
 * @Description: 易收支付
 * @Author: Zed
 * @Date: 2019-01-06 09:24
 * @Version:1.0.0
 **/

public class YISZFPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(YISZFPayServiceImpl.class);

    private String customerid = "10930";
    private String key = "6cbbf06e3cc1e3ac0240194de7060ab2d0fbd4d0 ";
    private String payUrl = "http://www.ipszf365.com/apisubmit";
    private String notifyUrl = "http://txw.tx8899.com/TAS/Notify/YISZFNotify.do";

    public YISZFPayServiceImpl(Map<String,String> map) {
        if(map != null && !map.isEmpty()){
            if(map.containsKey("customerid")){
                this.customerid = map.get("customerid");
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
        try {
            Map<String,String> param = sealRequest(payEntity,1);

            String sign = generatorSign(param);

            param.put("sign",sign);
            logger.info("[YISZF]易收支付网银请求参数:{}",JSONObject.fromObject(param).toString());
            String formStr = HttpUtils.generatorForm(param,payUrl);

            if (StringUtils.isBlank(formStr)) {
                logger.error("[YISZF]易收支付下单失败：生成表单结果为空");
                PayResponse.error("[YISZF]易收支付下单失败：生成表单结果为空");
            }

            return PayResponse.wy_form(payEntity.getPayUrl(),formStr);

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[YISZF]易收支付网银支付下单失败"+e.getMessage());
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            Map<String,String> param = sealRequest(payEntity,2);

            String sign = generatorSign(param);

            param.put("sign",sign);
            logger.info("[YISZF]易收支付扫码请求参数:{}",JSONObject.fromObject(param).toString());
            String formStr = HttpUtils.generatorForm(param,payUrl);

            if (StringUtils.isBlank(formStr)) {
                logger.error("[YISZF]易收支付下单失败：生成表单结果为空");
                PayResponse.error("[YISZF]易收支付下单失败：生成表单结果为空");
            }

            return PayResponse.sm_form(payEntity,formStr,"下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[YISZF]易收支付扫码支付下单失败"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[YISZF]易收支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data))
            return "success";
        return "fail";
    }

    private boolean verifyCallback(String sign,Map<String,String> data) {

//    {value}要替换成接收到的值，{apikey}要替换成平台分配的接入密钥，可在商户后台获取;
// customerid={value}&status={value}&sdpayno={value}&sdorderno={value}&total_fee={value}&paytype={value}&{apikey};
// 使用md5签名上面拼接的字符串即可生成小写的32位密文

        StringBuffer sb = new StringBuffer();
        sb.append("customerid=").append(data.get("customerid"));
        sb.append("&status=").append(data.get("status"));
        sb.append("&sdpayno=").append(data.get("sdpayno"));
        sb.append("&sdorderno=").append(data.get("sdorderno"));
        sb.append("&total_fee=").append(data.get("total_fee"));
        sb.append("&paytype=").append(data.get("paytype")).append("&");
        sb.append(key);
        String localSign;
        try {
            localSign = MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("[YISZF]易收支付生成支付签名串异常:"+ e.getMessage());
            return false;
        }
        return sign.equalsIgnoreCase(localSign);
    }

    /**
     *
     * @Description 封装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity,int type) throws Exception{
        logger.info("[YISZF]易收支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());

            data.put("version","1.0");//varchar(5)	Y	默认1.0
            data.put("customerid",customerid);//	int(8)	Y	商户后台获取
            data.put("sdorderno",entity.getOrderNo());//varchar(20)	Y	无
            data.put("total_fee",amount);//decimal(10,2)	Y	精确到小数点后两位，例10.00
            if (type == 1){
                data.put("paytype","wangyin");//网银
                data.put("bankcode","");//varchar(10)	Y/N	网银直连必填，其他支付方式可为空,详情见下文
            } else {
                data.put("paytype",entity.getPayCode());//varchar(10)	Y	详见paytype参数值说明
            }
            data.put("notifyurl",notifyUrl);//varchar(50)	Y	不能带有任何参数
            data.put("returnurl",entity.getRefererUrl());//varchar(50)	Y	不能带有任何参数
            data.put("remark","top_up");//varchar(50)	Y	可为空
            data.put("get_code","");//tinyint(1)	Y	值1为获取，值0不获取，可为空。
//            data.put("accNo	    ","");//varchar(32)	Y/N	此参数,快捷必需传;其他不用传

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YISZF]易收支付封装请求参数异常:"+e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     *
     * @Description 生成支付签名串
     * @param data
     * @return
     * @throws Exception
     */
    public String generatorSign(Map<String,String> data) throws Exception{
        logger.info("[YISZF]易收支付生成支付签名串开始==================START========================");
        try {
            //签名规则:
//            {value}要替换成接收到的值，{apikey}要替换成平台分配的接入密钥，可在商户后台获取;
//            version={value}&customerid={value}&total_fee={value}&sdorderno={value}¬ifyurl={value}&returnurl={value}&{apikey};
//            使用md5签名上面拼接的字符串即可生成小写的32位密文
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("version=").append(data.get("version"));
            strBuilder.append("&customerid=").append(data.get("customerid"));
            strBuilder.append("&total_fee="+data.get("total_fee"));
            strBuilder.append("&sdorderno=").append(data.get("sdorderno"));
            strBuilder.append("&notifyurl="+data.get("notifyurl"));
            strBuilder.append("&returnurl="+data.get("returnurl"));
            strBuilder.append("&"+key);
            logger.info("[YISZF]易收支付生成待签名串:"+strBuilder.toString());
            String md5Value = MD5Utils.md5toUpCase_32Bit(strBuilder.toString());
            if (StringUtils.isBlank(md5Value)) {
                logger.error("[YISZF]易收支付生成签名异常：生成签名为空");
                throw new Exception("生成支付签名串异常!");
            }
            logger.info("[YISZF]易收支付生成加密签名串:"+md5Value.toLowerCase());
            return md5Value.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YISZF]易收支付生成支付签名串异常:"+e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }

    public static void main(String[] args) {
        PayEntity testPay = new PayEntity();
        testPay.setAmount(100);
        testPay.setOrderNo("mk00000000123456");
        testPay.setPayCode("912");
        testPay.setRefererUrl("http://localhost:8080/xxx");
        //testPay.setMobile("mobile");
        YISZFPayServiceImpl service = new YISZFPayServiceImpl(null);
        service.smPay(testPay);
    }



}
