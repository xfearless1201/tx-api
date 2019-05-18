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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class XHFPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(XHFPayServiceImpl.class);
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

    public XHFPayServiceImpl(Map<String, String> data) {
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
        logger.info("新汇付支付扫码支付开始======================START==================");
        try {
            //封装请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("pay_md5sign", sign);
            logger.info("新汇付支付请求参数:" + JSONObject.fromObject(data));
            //生成请求表单
            String resStr = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[XHF]新汇支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[XHF]新汇支付扫码支付发起HTTP请求无响应结果");
            }
            JSONObject resObj = JSONObject.fromObject(resStr);
            logger.info("[XHF]新汇支付响应信息:" + resObj);
            if (resObj.containsKey("url") && resObj.containsKey("msg")) {
                return PayResponse.sm_qrcode(payEntity, resObj.getString("url"), resObj.getString("msg"));
            }
            return PayResponse.error("下单失败" + resObj);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("新汇付扫码支付异常:" + e.getMessage());
            return PayResponse.error("新汇付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        try {
            String sourceSign = data.remove("sign");
            String sign = generatorSign(data);
            logger.info("[XHF]新汇支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
            if (sign.equals(sourceSign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XHF]新汇支付回调验签异常:" + e.getMessage());
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
        Map<String, String> data = new HashMap<>();
        data.put("pay_memberid", payMemberid);
        data.put("pay_orderid", payEntity.getOrderNo());
        data.put("pay_amount", String.valueOf(payEntity.getAmount()));
        data.put("pay_applydate", toDate);
        data.put("pay_bankcode", payEntity.getPayCode());
        data.put("pay_notifyurl", payNotifyUrl);
        data.put("pay_producturl", "");
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
        StringBuffer sb = new StringBuffer();
        for (String key : sortmap.keySet()) {
            String val = sortmap.get(key);
            if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)
                    || "attach".equalsIgnoreCase(key)) {
                continue;
            }
            sb.append(key).append("=").append(val).append("&");
        }
        sb.append("key=").append(md5Key);
        //生成待签名串
        String signStr = sb.toString();
        logger.info("[XHFZF]新汇付支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(signStr);
        logger.info("[XHFZF]新汇付支付生成加密签名串:{}", sign);
        return sign;
    }
}
