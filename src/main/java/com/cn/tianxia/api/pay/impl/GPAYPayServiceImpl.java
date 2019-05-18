package com.cn.tianxia.api.pay.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.RSAUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;

import net.sf.json.JSONObject;

/**
 * @ClassName: GPAYPayServiceImpl
 * @Description: GPAY支付
 * @Author: Zed
 * @Date: 2018-12-30 19:44
 * @Version:1.0.0
 **/

public class GPAYPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(GPAYPayServiceImpl.class);

    /** 支付地址 **/
    private String api_url = "http://api.gpayroot.com/order/unify_order";

    /** 商户号 **/
    private String merchantNo = "77";

    /** md5key **/
    private String secret = "7NduQC3Mmt8h2TkD";

    /** notifyUrl **/
    private String notifyUrl = "http://tx.txw8899.com/TAS/Notify/GPAYNotify.do";

    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMcM9dpvi3IKYYhQf1BLmW1vDcU5mOywAjT/eGgSvASOAcnO/ePGirwaVbOgdlOwISyaCQlnlhHISjEvg6g/SzrT5Pc9X9o2gvw5hsP5W584X2Vi5ZNF6jEPCYB/8ZlNnd3TlXPN23bJ056CEhF6vs0C/R7bdRaecBcQLtY4iyMwIDAQAB";

    public GPAYPayServiceImpl(Map<String, String> pmap) {
        if (pmap != null) {
            if (pmap.containsKey("api_url")) {
                this.api_url = pmap.get("api_url");
            }
            if (pmap.containsKey("merchantNo")) {
                this.merchantNo = pmap.get("merchantNo");
            }
            if (pmap.containsKey("secret")) {
                this.secret = pmap.get("secret");
            }
            if (pmap.containsKey("notifyUrl")) {
                this.notifyUrl = pmap.get("notifyUrl");
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
            JSONObject params = sealRequest(payEntity);
            logger.info("[GPAY]GPAY支付请求参数:" + params.toString());
            String res = HttpUtils.toPostJson(params.toString(), api_url);

            if (StringUtils.isBlank(res)) {
                logger.error("[HAIF]海付支付扫码支付请求异常,返回结果为空!");
                return PayResponse.error("[HAIF]海付支付扫码支付请求异常,返回结果为空!");
            }
            logger.info("[HAIF]海付支付响应参数字符:" + res);
            JSONObject resposeJson = JSONObject.fromObject(res);

            if (resposeJson.containsKey("code") && 1 == resposeJson.getInt("code")) {

                JSONObject data = resposeJson.getJSONObject("data");

                String qrCodeUrl = data.getString("payUrl");
                if (StringUtils.isNotBlank(payEntity.getMobile())) {
                    return PayResponse.sm_link(payEntity, qrCodeUrl, "下单成功");
                }
                return PayResponse.sm_qrcode(payEntity, qrCodeUrl, "下单成功");
            }
            return PayResponse.error("[HAIF]海付支付扫码支付下单失败:" + resposeJson.getString("message"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[GPAY]GPAY支付扫码支付下单失败:{}",e.getMessage());
            return PayResponse.error("[GPAY]GPAY支付扫码支付下单失败:"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        try {
            String sourceSign = data.remove("sign");
            logger.info("[GPAY]GPAY支付回调验签原签名串;",data.toString());
            logger.info("[GPAY]GPAY支付回调验签原签名字段：{}",sourceSign);
            boolean validSign = RSAUtil.verifyMap(data,sourceSign,publicKey);
            if (validSign) {
                logger.info("[GPAY]GPAY支付回调验签成功");
                return "success";
            }
            return "fail";
        } catch (Exception e) {
            logger.error("[GPAY]GPAY支付回调验签异常:" + e.getMessage());
            return "fail";
        }
    }

    /**
     *
     * @param payEntity
     * @return
     */
    private JSONObject sealRequest(PayEntity payEntity) throws Exception {
        logger.info("[GPAY]GPAY支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            JSONObject data = new JSONObject();
            //订单金额

            data.put("matchId",Integer.valueOf(merchantNo));
            data.put("secret",secret);
            data.put("bizCode",payEntity.getOrderNo());
            data.put("money",payEntity.getAmount() * 100);
            data.put("channel",payEntity.getPayCode());
            data.put("payType","QR2");
            data.put("notifyUrl",notifyUrl);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[GPAY]GPAY支付封装请求参数异常:",e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }
}
