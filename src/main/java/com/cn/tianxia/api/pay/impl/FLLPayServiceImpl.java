package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
 * @Auther: zed
 * @Date: 2019/2/1 19:37
 * @Description: 付拉拉支付实现类
 */
public class FLLPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(FLLPayServiceImpl.class);

    private String partner;
    private String key;
    private String payUrl;
    private String notifyUrl;

    public FLLPayServiceImpl(Map<String,String> map) {
        if(map != null && !map.isEmpty()){
            if(map.containsKey("partner")){
                this.partner = map.get("partner");
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
            Map<String,String> param = sealRequest(payEntity);

            logger.info("[FLL]付拉拉支付扫码请求参数:{}",JSONObject.fromObject(param).toString());
            String response = HttpUtils.toPostJson(param,payUrl);
            logger.info("[FLL]付拉拉支付扫码响应信息:{}",response);
            if (StringUtils.isBlank(response)) {
                logger.error("[FLL]付拉拉支付下单失败：返回结果为空");
                PayResponse.error("[FLL]付拉拉支付下单失败：返回结果为空");
            }

            StringBuilder repairString = new StringBuilder(response);  //返回的信息签名多了一个字符
            repairString.deleteCharAt(0);

            JSONObject resultJson = JSONObject.fromObject(repairString.toString());

            if (resultJson.containsKey("code") && resultJson.getString("code").equals("1")){

                String payurl = resultJson.getString("payurl");
                return PayResponse.sm_qrcode(payEntity,payurl,"[FLL]付拉拉下单成功");

            }

            return PayResponse.error("[FLL]付拉拉支付下单失败：" + resultJson.getString("msg"));

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[FLL]付拉拉支付扫码支付下单失败"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[FLL]付拉拉支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data))
            return "success";
        return "fail";
    }

    private boolean verifyCallback(String sign,Map<String,String> data) {

        String localSign;
        try {
            localSign = generatorSign(data);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[FLL]付拉拉支付生成支付签名串异常:"+ e.getMessage());
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
        logger.info("[FLL]付拉拉支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());

            data.put("pid",partner);
            data.put("type",entity.getPayCode());
            data.put("out_trade_no",entity.getOrderNo());
            data.put("notify_url",notifyUrl);
            data.put("return_url",entity.getRefererUrl());
            data.put("name","top_Up");
            data.put("attach","recharge");
            data.put("money",amount);
            data.put("format","json");
            data.put("sign",generatorSign(data));
            data.put("sign_type","MD5");
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[FLL]付拉拉支付封装请求参数异常:"+e.getMessage());
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
        logger.info("[FLL]付拉拉支付生成支付签名串开始==================START========================");
        try {
            StringBuilder strBuilder = new StringBuilder();
            TreeMap<String,String> sortedMap = new TreeMap<>(data);

            for (Map.Entry<String,String> entry:sortedMap.entrySet()) {
                if (StringUtils.isBlank(entry.getValue()) || "sign".equals(entry.getKey()) || "sign_type".equals(entry.getKey()))
                    continue;
                strBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }

            strBuilder.deleteCharAt(strBuilder.length() - 1);  //删除最后一个&

            strBuilder.append(key);

            logger.info("[FLL]付拉拉支付生成待签名串:"+strBuilder.toString());

            String md5Value = MD5Utils.md5toUpCase_32Bit(strBuilder.toString());
            if (StringUtils.isBlank(md5Value)) {
                logger.error("[FLL]付拉拉支付生成签名异常：生成签名为空");
                throw new Exception("生成支付签名串异常!");
            }

            logger.info("[FLL]付拉拉支付生成加密签名串:"+md5Value.toLowerCase());

            return md5Value.toLowerCase();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[FLL]付拉拉支付生成支付签名串异常:"+e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }
}
