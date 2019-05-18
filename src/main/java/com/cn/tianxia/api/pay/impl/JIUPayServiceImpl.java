package com.cn.tianxia.api.pay.impl;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/1/17 09:30
 * @Description: 九久支付
 */
public class JIUPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(JIUPayServiceImpl.class);

    private String mchid;
    private String key;
    private String payUrl;
    private String notifyUrl;

    public JIUPayServiceImpl(Map<String,String> map) {
        if(map != null && !map.isEmpty()){
            if(map.containsKey("mchid")){
                this.mchid = map.get("mchid");
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
        try {
            LinkedHashMap<String, String> param = sealRequest(payEntity);

            String sign = generatorSign(param);

            param.put("sign",sign);
            logger.info("[JIU]九久支付扫码请求参数:{}",JSONObject.fromObject(param).toString());
            String requestUrl = generatorPayUrl(param,payUrl);

            if (StringUtils.isBlank(requestUrl)) {
                logger.error("[JIU]九久支付下单失败：生成请求链接为空");
                PayResponse.error("[JIU]九久支付下单失败：生成请求链接为空");
            }

            logger.info("[JIU]九久支付生成请求链接：{}",requestUrl);

            return PayResponse.sm_link(payEntity,requestUrl,"下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[JIU]九久支付扫码支付下单失败"+e.getMessage());
        }
    }

    private String generatorPayUrl(LinkedHashMap<String, String> param, String payUrl) {
        Iterator<Map.Entry<String,String>> iterator = param.entrySet().iterator();
        StringBuilder builder = new StringBuilder(payUrl);
        builder.append("?");
        while (iterator.hasNext()) {
            Map.Entry<String,String> entry = iterator.next();
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[JIU]九久支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data))
            return "success";
        return "fail";
    }

    private boolean verifyCallback(String sign,Map<String,String> data) {

//        sign加密时要按照下面示例：
//        resultcode=1&transactionid=201803051730&mchid=10000&mchno=201803051730&
//                tradetype=weixin&totalfee=60.00&attach=yyyxx&key=c4b70b766ea78fe1689f4e4e1afa291a key值为商户在平台的 通信KEY


        StringBuffer sb = new StringBuffer();
        sb.append("resultcode").append("=").append(data.get("resultcode"));
        sb.append("&").append("transactionid").append("=").append(data.get("transactionid"));
        sb.append("&").append("mchid").append("=").append(data.get("mchid"));
        sb.append("&").append("mchno").append("=").append(data.get("mchno"));
        sb.append("&").append("tradetype").append("=").append(data.get("tradetype"));
        sb.append("&").append("totalfee").append("=").append(data.get("totalfee"));
        sb.append("&").append("attach").append("=").append(data.get("attach"));
        sb.append("&").append("key").append("=").append(key);
        String localSign;
        try {
            localSign = MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("[JIU]九久支付生成支付签名串异常:"+ e.getMessage());
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
    private LinkedHashMap<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[JIU]九久支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            LinkedHashMap<String,String> data = new LinkedHashMap<>();
            //订单金额
            String amount = new DecimalFormat("0").format(entity.getAmount() * 100);

            data.put("mchid",mchid);//商户ID
            data.put("mchno",entity.getOrderNo());//商户订单号
            data.put("tradetype",entity.getPayCode());//订单类型
            data.put("totalfee",amount);//支付金额  分为单位
            data.put("descrip","TOP_UP");//订单描述
            data.put("attach","");//附加数据
            data.put("clientip",entity.getIp());//终端IP
            data.put("notifyurl",notifyUrl);//异步通知地址
            data.put("returnurl",entity.getRefererUrl());//同步通知地址

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JIU]九久支付封装请求参数异常:"+e.getMessage());
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
    public String generatorSign(LinkedHashMap<String,String> data) throws Exception{
        logger.info("[JIU]九久支付生成支付签名串开始==================START========================");
        try {
            //签名规则:
//            mchid=10000&mchno=201803051730&tradetype=alipayh5&totalfee=1000&descrip=xxxx&attach=xxxx
//                    &clientip=127.0.0.1&notifyurl=http://xxxx.cn/wxpay/pay.php&returnurl=
//            http://xxxx.cn/wxpay/pay.php&key=c4b70b766ea78fe1689f4e4e1afa291a

            StringBuilder strBuilder = new StringBuilder();
            for (Map.Entry entry : data.entrySet()) {
                strBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            strBuilder.append("key").append("=").append(key);
            logger.info("[JIU]九久支付生成待签名串:"+strBuilder.toString());
            String md5Value = MD5Utils.md5toUpCase_32Bit(strBuilder.toString());
            if (StringUtils.isBlank(md5Value)) {
                logger.error("[JIU]九久支付生成签名异常：生成签名为空");
                throw new Exception("生成支付签名串异常!");
            }
            logger.info("[JIU]九久支付生成加密签名串:"+md5Value.toLowerCase());
            return md5Value.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JIU]九久支付生成支付签名串异常:"+e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }

}
