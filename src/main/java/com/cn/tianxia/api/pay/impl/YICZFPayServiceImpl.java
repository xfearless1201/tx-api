package com.cn.tianxia.api.pay.impl;

import java.security.NoSuchAlgorithmException;
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

import net.sf.json.JSONObject;

/**
 * @ClassName: YICZFPayServiceImpl
 * @Description: 宜橙支付
 * @Author: Zed
 * @Date: 2019-01-06 10:52
 * @Version:1.0.0
 **/

public class YICZFPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(YICZFPayServiceImpl.class);

    private String memberid;
    private String key;
    private String payUrl;
    private String notifyUrl;

    public YICZFPayServiceImpl(Map<String,String> map) {
        if(map != null && !map.isEmpty()){
            if(map.containsKey("memberid")){
                this.memberid = map.get("memberid");
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
            Map<String,String> param = sealRequest(payEntity);

            String sign = generatorSign(param);

            param.put("pay_md5sign",sign);
            logger.info("[YICZF]宜橙支付网银请求参数:{}",JSONObject.fromObject(param).toString());
            String formStr = HttpUtils.generatorForm(param,payUrl);

            if (StringUtils.isBlank(formStr)) {
                logger.error("[YICZF]宜橙支付下单失败：生成表单结果为空");
                PayResponse.error("[YICZF]宜橙支付下单失败：生成表单结果为空");
            }

            return PayResponse.wy_form(payEntity.getPayUrl(),formStr);

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[YICZF]宜橙支付网银支付下单失败"+e.getMessage());
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            Map<String,String> param = sealRequest(payEntity);

            String sign = generatorSign(param);

            param.put("pay_md5sign",sign);
            logger.info("[YICZF]宜橙支付扫码请求参数:{}",JSONObject.fromObject(param).toString());
            String formStr = HttpUtils.generatorForm(param,payUrl);

            if (StringUtils.isBlank(formStr)) {
                logger.error("[YICZF]宜橙支付下单失败：生成表单结果为空");
                PayResponse.error("[YICZF]宜橙支付下单失败：生成表单结果为空");
            }

            return PayResponse.sm_form(payEntity,formStr,"下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[YICZF]宜橙支付扫码支付下单失败"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[YICZF]宜橙支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data))
            return "success";
        return "fail";
    }

    private boolean verifyCallback(String sign,Map<String,String> data) {

//   stringSignTemp="orderid=orderid&opstate=opstate&ovalue=ovalue"+key
//sign=MD5(stringSignTemp).toLowerCase()

        StringBuffer sb = new StringBuffer();
        sb.append("orderid=").append(data.get("orderid"));
        sb.append("&opstate=").append(data.get("opstate"));
        sb.append("&ovalue=").append(data.get("ovalue"));
        sb.append(key);
        String localSign;
        try {
            localSign = MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("[YICZF]宜橙支付生成支付签名串异常:"+ e.getMessage());
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
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[YICZF]宜橙支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());

            data.put("pay_version","vb1.0");//  系统接口版本 是 否 固定值:vb1.0
            data.put("pay_memberid",memberid);//   商户号 是 是 平台分配商户号
            data.put("pay_orderid",entity.getOrderNo());//  订单号 是 是 上送订单号唯一, 字符长度 20
            data.put("pay_applydate",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//  提交时间 是 否 时间格式(yyyyMMddHHmmss)：
            data.put("pay_bankcode",entity.getPayCode());//  银行编码 是 是 参考后续说明
            data.put("pay_notifyurl",notifyUrl);//  服务端通知 是 是 服务端返回地址（. POST 返回数据）
            data.put("pay_amount",amount);//  订单金额 是 是 商品金额（单位元）
            //data.put("pay_md5sign     ","");//  MD5 签名 是 否 请看 MD5 签名字段格式
            data.put("pay_attach","");//  附加字段 否 否 此字段在返回时按原样返回 注：扫码接口，如果要获取二维码链接，这个字段的值请传 codeUrl，即 pay_attach=codeUrl
//            data.put("pay_agent       ","");//  代理编号 否 否 代理编号，可不填
//            data.put("pay_productname ","");//  商品名称 否 否
//            data.put("pay_productnum  ","");//  商户品数量 否 否
//            data.put("pay_productdesc ","");//  商品描述 否 否
//            data.put("pay_producturl  ","");//  商品链接地址 否 否

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YICZF]宜橙支付封装请求参数异常:"+e.getMessage());
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
        logger.info("[YICZF]宜橙支付生成支付签名串开始==================START========================");
        try {
            //签名规则:
//            stringSignTemp="pay_memberid=pay_memberid&pay_bankcode=pay_bankcode&pay_amount=pay_amoun
//            t&pay_orderid=pay_orderid&pay_notifyurl=pay_notifyurl"+key
//            pay_md5sign=MD5(stringSignTemp).toLowerCase()
//            签名采用 32 位小写 MD5 签名值，GB2312 编码
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("pay_memberid=").append(data.get("pay_memberid"));
            strBuilder.append("&pay_bankcode=").append(data.get("pay_bankcode"));
            strBuilder.append("&pay_amount="+data.get("pay_amount"));
            strBuilder.append("&pay_orderid=").append(data.get("pay_orderid"));
            strBuilder.append("&pay_notifyurl="+data.get("pay_notifyurl"));
            strBuilder.append(key);
            logger.info("[YICZF]宜橙支付生成待签名串:"+strBuilder.toString());
            String md5Value = MD5Utils.md5toUpCase_32Bit(strBuilder.toString());
            if (StringUtils.isBlank(md5Value)) {
                logger.error("[YICZF]宜橙支付生成签名异常：生成签名为空");
                throw new Exception("生成支付签名串异常!");
            }
            logger.info("[YICZF]宜橙支付生成加密签名串:"+md5Value.toLowerCase());
            return md5Value.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YICZF]宜橙支付生成支付签名串异常:"+e.getMessage());
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
        YICZFPayServiceImpl service = new YICZFPayServiceImpl(null);
        service.smPay(testPay);
    }

}
