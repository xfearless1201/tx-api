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
 * @ClassName: HYZFPayServiceImpl
 * @Description: 虎云支付
 * @Author: Zed
 * @Date: 2018-12-17 10:33
 * @Version:1.0.0
 **/

public class HYZFPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(HYZFPayServiceImpl.class);
    private String customerid = "110040";//商户号
    private String notifyUrl = "http://txw.tx8899.com/AMJ/Notify/HYZFNotify.do";
    private String key = "9f8fc1268e02c58aaef50cf4d1e9944fe05074b5";
    private String payUrl = "https://aa.39team.com/apisubmit";    //支付地址

    public HYZFPayServiceImpl(Map<String,String> map){
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
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            logger.info("[HYZF]虎云支付扫码支付==================START====================");
            Map<String, String> params = sealRequest(payEntity);
            String sign = generatorSign(params);
            params.put("sign",sign);
            logger.info("[HYZF]虎云支付扫码支付请求参数;{}",params.toString());

            String form = HttpUtils.generatorForm(params,payUrl);

            if (StringUtils.isBlank(form)) {
                logger.error("[HYZF]虎云支付扫码支付构建表单异常,表单为空!");
                return PayResponse.error("[HYZF]虎云支付扫码支付构建表单异常,表单为空!");
            }
            return PayResponse.sm_form(payEntity,form,"下单成功!");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HYZF]虎云支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[HYZF]虎云支付扫码支付异常:"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        try {
            boolean validSign = checkSign(data);
            if (validSign) {
                logger.info("[HYZF]虎云支付回调验签成功");
                return "success";
            }
            return "fail";
        } catch (Exception e) {
            logger.error("[JFZF]发家支付回调验签异常:" + e.getMessage());
            return "fail";
        }
    }

    /**
     *
     * @Description 封装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[HYZF]虎云支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat(".00").format(entity.getAmount());//交易金额 分为单位
            data.put("version","1.0");
            data.put("customerid",customerid);//商户号
            data.put("sdorderno",entity.getOrderNo());//订单号
            data.put("userid",entity.getuId());  //商户平台用户id
            data.put("total_fee",amount);// 交易金额
            data.put("paytype",entity.getPayCode());// 交易金额
            data.put("notifyurl",notifyUrl);//后台回调通知地址
            data.put("returnurl",entity.getRefererUrl());//页面通知地址

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HYZF]虎云支付封装请求参数异常:"+e.getMessage());
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
        logger.info("[HYZF]虎云支付生成支付签名串开始==================START========================");
        try {
            //签名规则:
            // version={value}&customerid={value}&userid={value}&total_fee={value}&sdorderno={value}&notifyurl={value}&returnurl={value}&{apikey}
            //使用md5签名上面拼接的字符串即可生成小写的32位密文

            StringBuffer sb = new StringBuffer();
            sb.append("version=").append(data.get("version"))
                    .append("&customerid=").append(data.get("customerid"))
                    .append("&userid=").append(data.get("userid"))
                    .append("&total_fee=").append(data.get("total_fee"))
                    .append("&sdorderno=").append(data.get("sdorderno"))
                    .append("&notifyurl=").append(data.get("notifyurl"))
                    .append("&returnurl=").append(data.get("returnurl"))
                    .append("&").append(key);
            logger.info("[HYZF]虎云支付生成待签名串:"+sb.toString());
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString());
            if (StringUtils.isBlank(sign)) {
                logger.error("[HYZF]虎云支付生成签名串为空！");
                return null;
            }
            logger.info("[HYZF]虎云支付生成加密签名串:"+sign.toLowerCase());
            return sign.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HYZF]虎云支付生成支付签名串异常:"+e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }

    /**
     *
     * @Description 验证回调签名串
     * @param data
     * @return
     * @throws Exception
     */
    public boolean checkSign(Map<String,String> data) throws Exception{
        logger.info("[HYZF]虎云支付回调验证签名生成签名开始==================START========================");
        try {
            String sourceSign = data.remove("sign");
            logger.info("[HYZF]虎云支付回调验签原签名:{}",sourceSign);
            //签名规则:
//            customerid={value}&status={value}&sdpayno={value}&sdorderno={value}&total_fee={value}&paytype={value}&{apikey}
//            使用md5签名上面拼接的字符串即可生成小写的32位密文
            StringBuffer sb = new StringBuffer();
            sb.append("customerid=").append(data.get("customerid"))
                    .append("&status=").append(data.get("status"))
                    .append("&sdpayno=").append(data.get("sdpayno"))
                    .append("&sdorderno=").append(data.get("sdorderno"))
                    .append("&total_fee=").append(data.get("total_fee"))
                    .append("&paytype=").append(data.get("paytype"))
                    .append("&").append(key);
            logger.info("[HYZF]虎云支付回调验签生成待签名串:"+sb.toString());
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString());
            if (StringUtils.isBlank(sign)) {
                logger.error("[HYZF]虎云支付回调验签生成签名串为空！");
                return false;
            }
            logger.info("[HYZF]虎云支付回调验签生成加密签名串:"+sign.toLowerCase());
            return sign.equalsIgnoreCase(sourceSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HYZF]虎云支付回调验签生成签名异常:"+e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }
}
