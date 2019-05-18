package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class QGZFPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(QGZFPayServiceImpl.class);
    /**
     * 商户号
     */
    private String payMemberid;
    /**
     * 支付地址
     */
    private String payUrl;
    /**
     * 密钥
     */
    private String md5Key;
    /**
     * 回调地址
     */
    private String payNotifyUrl;

    public QGZFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("payMemberid")) {
                this.payMemberid = data.get("payMemberid");
            }
            if (data.containsKey("md5Key")) {
                this.md5Key = data.get("md5Key");
            }
            if (data.containsKey("payNotifyUrl")) {
                this.payNotifyUrl = data.get("payNotifyUrl");
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
        logger.info("[QGZF]钱柜支付扫码支付开始======================START==================");
        try {
            //封装请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[QGZF]钱柜支付请求参数:" + JSONObject.fromObject(data).toString());
            //生成请求表单
            String resStr = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[QGZF]钱柜支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[QGZF]钱柜支付扫码支付发起HTTP请求无响应结果");
            }
            JSONObject resObj = JSONObject.fromObject(resStr);
            logger.info("[QGZF]钱柜支付响应信息:" + resObj);

            if (resObj.containsKey("code") && resObj.getInt("code") == 200) {
                resObj = JSONObject.fromObject(resObj.getString("data"));
                if (StringUtils.isNotBlank(payEntity.getMobile())) {
                    return PayResponse.sm_link(payEntity, resObj.getString("pay_url"), "下单成功");
                }
                return PayResponse.sm_qrcode(payEntity, resObj.getString("pay_url"), "下单成功");
            }
            return PayResponse.error("下单失败" + resObj);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[QGZF]钱柜支付生成异常:" + e.getMessage());
            return PayUtil.returnWYPayJson("error", "form", "", "", "");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        try {
            String sourceSign = data.remove("sign");
            String sign = generatorSign(data);
            logger.info("[QGZF]钱柜支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
            if (sign.equals(sourceSign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[QGZF]钱柜支付回调验签异常:" + e.getMessage());
        }
        return "fail";
    }

    /**
     * @param
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    public Map<String, String> sealRequest(PayEntity payEntity) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
        String toDate = dateFormat.format(new Date());
        DecimalFormat df = new DecimalFormat("0.00");
        Map<String, String> data = new HashMap<>();
        data.put("merchant_code", payMemberid);
        data.put("pay_money", df.format(payEntity.getAmount()));
        data.put("pay_code", payEntity.getPayCode());
        data.put("out_trade_no", payEntity.getOrderNo());
        data.put("order_time", toDate);
        data.put("order_ip", payEntity.getIp());
        data.put("notify_url", payNotifyUrl);
        return data;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        Map<String, String> sortmap = MapUtils.sortByKeys(data);
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        Iterator<String> iterator = sortmap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String val = sortmap.get(key);
            if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                continue;
            }
            sb1.append(key).append(val);
        }
        //生成待签名串
        String signStr = sb2.append(md5Key).append(sb1.toString()).toString();
        logger.info("[QGZF]钱柜支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(MD5Utils.md5(signStr.getBytes()) + sb1.toString());
        logger.info("[QGZF]钱柜支付生成加密签名串:{}", sign);
        return sign;
    }
}
