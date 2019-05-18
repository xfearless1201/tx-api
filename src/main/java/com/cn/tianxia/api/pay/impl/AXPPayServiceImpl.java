package com.cn.tianxia.api.pay.impl;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
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
 * @Auther: zed
 * @Date: 2019/2/3 10:36
 * @Description: 安心付支付
 */
public class AXPPayServiceImpl implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(AXPPayServiceImpl.class);

    private String mchid = "580849008560";
    private String key = "879bdfa7d9389a91e9f4728f0c2c6022";
    private String payUrl = "http://api.anxfu.com/waporder/order_add";
    private String notifyUrl = "http://txw.tx8899.com/YLH/Notify/AXPNotify.do";

    public AXPPayServiceImpl(Map<String,String> map) {
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
            Map<String,String> param = sealRequest(payEntity);

            logger.info("[AXP]安心付支付扫码请求参数:{}",JSONObject.fromObject(param).toString());

            String response = HttpUtils.get(param,payUrl);

            if (StringUtils.isBlank(response)) {
                logger.error("[AXP]安心付支付扫码下单失败：请求第三方返回结果为空");
                return PayResponse.error("[AXP]安心付支付扫码下单失败：请求第三方返回结果为空");
            }

            JSONObject result = JSONObject.fromObject(response);

            if (result.containsKey("ok") && result.getString("ok").equals("true")) {
                String qrCodeUrl = result.getString("data");
                return PayResponse.sm_link(payEntity,qrCodeUrl,"下单成功");
            }

            return PayResponse.error("下单失败：" + result.get("msg"));

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[AXP]安心付支付扫码支付下单失败"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[AXP]安心付支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data))
            return "success";
        return "fail";
    }

    private boolean verifyCallback(String sign,Map<String,String> data) {

        try {
            StringBuffer sb = new StringBuffer();
            sb.append(data.get("order_id"));
            sb.append(data.get("orderNo"));
            sb.append(data.get("money"));
            sb.append(data.get("mch"));
            sb.append(data.get("pay_type"));
            sb.append(data.get("time"));
            sb.append(MD5Utils.md5toUpCase_32Bit(key).toLowerCase());
            String localSign;
            localSign = MD5Utils.md5toUpCase_32Bit(sb.toString());
            return sign.equalsIgnoreCase(localSign);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("[AXP]安心付支付生成支付签名串异常:"+ e.getMessage());
            return false;
        }
    }

    /**
     *
     * @Description 封装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private LinkedHashMap<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[AXP]安心付支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            LinkedHashMap<String,String> data = new LinkedHashMap<>();
            //订单金额
            String amount = new DecimalFormat("0").format(entity.getAmount() * 100);

            data.put("mch",mchid);//商户号
            data.put("pay_type",entity.getPayCode());//商户号
            data.put("money",amount);//支付金额  分为单位
            data.put("time",String.valueOf(System.currentTimeMillis()/1000));//订单时间 10位时间戳 精确到秒
            data.put("order_id",entity.getOrderNo());//订单号
            data.put("return_url",entity.getRefererUrl());//同步通知地址
            data.put("notify_url",notifyUrl);//异步通知地址
            data.put("sign",generatorSign(data));
            data.put("extra","top_Up");

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[AXP]安心付支付封装请求参数异常:"+e.getMessage());
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
        logger.info("[AXP]安心付支付生成支付签名串开始==================START========================");
        try {
            //签名规则:
//            mchid=10000&mchno=201803051730&tradetype=alipayh5&totalfee=1000&descrip=xxxx&attach=xxxx
//                    &clientip=127.0.0.1&notifyurl=http://xxxx.cn/wxpay/pay.php&returnurl=
//            http://xxxx.cn/wxpay/pay.php&key=c4b70b766ea78fe1689f4e4e1afa291a

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(data.get("order_id"))
                    .append(data.get("money"))
                    .append(data.get("pay_type"))
                    .append(data.get("time"))
                    .append(data.get("mch"));
            strBuilder.append(MD5Utils.md5toUpCase_32Bit(key).toLowerCase());
            logger.info("[AXP]安心付支付生成待签名串:"+strBuilder.toString());
            String md5Value = MD5Utils.md5toUpCase_32Bit(strBuilder.toString());
            if (StringUtils.isBlank(md5Value)) {
                logger.error("[AXP]安心付支付生成签名异常：生成签名为空");
                throw new Exception("生成支付签名串异常!");
            }
            logger.info("[AXP]安心付支付生成加密签名串:"+md5Value.toLowerCase());
            return md5Value.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[AXP]安心付支付生成支付签名串异常:"+e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }
}
