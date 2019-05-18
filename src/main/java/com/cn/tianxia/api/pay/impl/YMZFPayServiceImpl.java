package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author administrator
 * @version 1.0.0
 * @ClassName YMZFPayServiceImpl
 * @Description 易码支付
 * @Date
 **/
public class YMZFPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(YMZFPayServiceImpl.class);
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
    /**
     * 版本号
     */
    private String version;

    public YMZFPayServiceImpl(Map<String, String> data) {
        if (!MapUtils.isNotEmpty(data)) {
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
            if (data.containsKey("version")) {
                this.version = data.get("version");
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
        logger.info("[YMZF]易码扫码支付扫码支付开始======================START==================");
        try {
            //封装请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[YMZF]易码扫码支付请求参数:" + JSONObject.fromObject(data).toString());
            //生成请求表单
            String resStr = HttpUtils.toPostForm(data, payUrl, "ver", version);
            logger.info("[YMZF]易码扫码支付响应信息:" + resStr);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[YMZF]易码扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[YMZF]易码扫码支付发起HTTP请求无响应结果");
            }
            JSONObject resJsonObj = JSONObject.fromObject(resStr);
            if (resJsonObj.containsKey("code") && "1".equals(resJsonObj.getString("code"))) {
                resJsonObj = resJsonObj.getJSONObject("result");
                return PayResponse.sm_link(payEntity, resJsonObj.getString("qrurl"), "下单成功");
            }
            return PayResponse.error("[YMZF]易码扫码支付下单失败");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YMZF]易码扫码支付生成异常:" + e.getMessage());
            return PayResponse.error("[YMZF]易码扫码支付生成异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        try {
            String sourceSign = data.remove("sign");
            String sign = generatorSign(data);
            logger.info("[YMZF]易码扫码支付回调生成签名串" + sign);
            if (sign.equals(sourceSign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YMZF]易码扫码支付回调生成签名串异常" + e.getMessage());
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
        DecimalFormat df = new DecimalFormat("0.00");
        Map<String, String> data = new HashMap<>();
        data.put("mch_id", payMemberid);//商户号
        data.put("out_trade_no", payEntity.getOrderNo());//订单号
        data.put("money", df.format(payEntity.getAmount()));//金额
        data.put("remark", "Pay");//订单备注
        data.put("type", payEntity.getPayCode());//支付通道
        data.put("notifyurl", payNotifyUrl);//异步通知地址
        data.put("returnurl", payNotifyUrl);//同步返回地址
        data.put("attach", payEntity.getUsername());//用户名
        return data;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        Map<String, String> sortMap = MapUtils.sortByKeys(data);
        StringBuffer sb = new StringBuffer();
        for (String key : sortMap.keySet()) {
            String val = sortMap.get(key);
            if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                continue;
            }
            sb.append(key).append("=").append(val).append("&");
        }
        sb.append("key=").append(md5Key);
        //生成待签名串
        String signStr = sb.toString();
        logger.info("[YMZF]易码扫码支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(signStr);
        logger.info("[YMZF]易码扫码支付生成加密签名串:{}", sign);
        return sign;
    }
}
