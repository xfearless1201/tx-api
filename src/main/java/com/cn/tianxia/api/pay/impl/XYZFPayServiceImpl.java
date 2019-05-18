package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class XYZFPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(XYZFPayServiceImpl.class);
    /**
     * 支付地址
     */
    private String payUrl;
    /**
     * 商户编号
     */
    private String payMemberid;
    /**
     * 商户接收支付成功数据的地址
     */
    private String payNotifyUrl;
    /**
     * 商户密钥
     */
    private String md5Key;

    public XYZFPayServiceImpl(Map<String, String> data) {
        if (data != null) {
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("payMemberid")) {
                this.payMemberid = data.get("payMemberid");
            }
            if (data.containsKey("payNotifyUrl")) {
                this.payNotifyUrl = data.get("payNotifyUrl");
            }
            if (data.containsKey("md5Key")) {
                this.md5Key = data.get("md5Key");
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
        logger.info("[XYZF]新艺支付扫码支付开始======================START==================");
        try {
            //封装请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[XYZF]新艺支付请求参数:" + data);
            //生成请求表单
            logger.info("支付地址：" + payUrl);
            String resStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[XYZF]新艺支付支付响应信息:" + resStr);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[XYZF]新艺支付支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[XYZF]新艺支付支付扫码支付发起HTTP请求无响应结果");
            }
            return PayResponse.sm_form(payEntity, resStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XYZF]新艺支付生成异常:" + e.getMessage());
            return PayResponse.error("[XYZF]新艺支付下单失败");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        try {
            String sourceSign = data.get("sign");
            StringBuilder sb = new StringBuilder();
            sb.append("status=").append(data.get("status")).append("&");
            sb.append("shid=").append(data.get("shid")).append("&");
            sb.append("bb=").append(data.get("bb")).append("&");
            sb.append("zftd=").append(data.get("zftd")).append("&");
            sb.append("ddh=").append(data.get("ddh")).append("&");
            sb.append("je=").append(data.get("je")).append("&");
            sb.append("ddmc=").append(data.get("ddmc")).append("&");
            sb.append("ddbz=").append(data.get("ddbz")).append("&");
            sb.append("ybtz=").append(data.get("ybtz")).append("&");
            sb.append("tbtz=").append(data.get("tbtz")).append("&");
            sb.append(md5Key);
            String signStr = sb.toString();
            logger.info("[XYZF]新艺支付回调生成待签名串" + signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[XYZF]新艺支付回调生成签名串" + sign);
            if (sign.equals(sourceSign)) {
                return "success";
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.info("[XYZF]新艺支付回调生成签名串异常" + e.getMessage());
        }
        return null;
    }

    /**
     * @param
     * @param
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    public Map<String, String> sealRequest(PayEntity payEntity) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            Map<String, String> data = new HashMap<>();
            data.put("bb", "1.0");//版本号
            data.put("shid", payMemberid);//商户号
            data.put("ddh", payEntity.getOrderNo());//订单号
            data.put("je", df.format(payEntity.getAmount()));//总金额,单位：分
            data.put("zftd", payEntity.getPayCode());//支付渠道
            data.put("ybtz", payNotifyUrl);//异步回调地址
            data.put("tbtz", payNotifyUrl);//同步跳转地址
            data.put("ddmc", "Pay");//订单名称
            data.put("ddbz", "Pay");//订单备注
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XYZF]新艺支付获取请求参数异常" + e.getMessage());
            return null;
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("shid=").append(data.get("shid")).append("&");
            sb.append("bb=").append(data.get("bb")).append("&");
            sb.append("zftd=").append(data.get("zftd")).append("&");
            sb.append("ddh=").append(data.get("ddh")).append("&");
            sb.append("je=").append(data.get("je")).append("&");
            sb.append("ddmc=").append(data.get("ddmc")).append("&");
            sb.append("ddbz=").append(data.get("ddbz")).append("&");
            sb.append("ybtz=").append(data.get("ybtz")).append("&");
            sb.append("tbtz=").append(data.get("tbtz")).append("&");
            sb.append(md5Key);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[XYZF]新艺支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[XYZF]新艺支付生成加密签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XYZF]新艺支付生成加密签名串失败" + e.getMessage());
            return null;
        }
    }

}
