package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName WMPPayServiceImpl
 * @Description 完美支付
 * @Date 2019年1月3日 下午3:54:24
 */
public class WMPPayServiceImpl implements PayService {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(WMPPayServiceImpl.class);

    private String merId;// 商户号

    private String payUrl;// 支付地址

    private String notifyUrl;// 回调通知地址

    private String md5Key;// 秘钥

    private String type;//请求类型,1=公开版,用户自己提供收款账号 2=服务版,由平台提供收款账号

    // 构造器,初始化参数
    public WMPPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merId")) {
                this.merId = data.get("merId");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("md5Key")) {
                this.md5Key = data.get("md5Key");
            }
            if (data.containsKey("type")) {
                this.type = data.get("type");
            } else {
                this.type = "2";
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
        logger.info("[WMP]完美支付扫码支付开始=====================START===================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("sign", sign);
            logger.info("[WMP]完美支付扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String formStr = HttpUtils.generatorForm(data, payUrl);
            return PayResponse.sm_form(payEntity, formStr, "生成form表单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WMP]完美支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[WMP]完美支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[WMP]完美支付回调验签开始===================START=====================");
        try {

            String sourceSign = data.get("sign");
            logger.info("[WMP]完美支付获取回调验签原签名串:{}", sourceSign);
            String sign = generatorSign(data, 0);
            logger.info("[WMP]完美支付生成回调签名串:{}", sign);
            if (sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WMP]完美支付回调验签异常:{}", e.getMessage());
        }
        return "faild";
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[WMP]完美支付组装支付请求参数开始==================START======================");
        try {

            Map<String, String> data = new HashMap<>();

            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);

            data.put("mchId", merId);//商户ID,10000
            data.put("type", type);//请求类型,1=公开版,用户自己提供收款账号 2=服务版,由平台提供收款账号
            data.put("channelId", entity.getPayCode());//渠道ID,alipay
            data.put("order", entity.getOrderNo());//商户订单号,201809061234
            data.put("amount", amount);//支付金额,10000
            data.put("notifyUrl", notifyUrl);//支付结果回调URL    http://Zfb.ep567.com/api/order/wechatCallback
            data.put("successUrl", entity.getRefererUrl());//支付成功跳转地址,http://www.baidu.com/success.htm
            data.put("errorUrl", entity.getRefererUrl());//支付失败跳转地址,http://www.baidu.com/error.htm
            data.put("extra", "{'userid':" + entity.getuId() + "}");//附加参数,{‘userid’:’1204564’}
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WMP]完美支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[WMP]完美支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @param type
     * @return
     * @throws Exception
     * @Description 签名
     */
    private String generatorSign(Map<String, String> data, int type) throws Exception {
        logger.info("[WMP]完美支付签名开始============START=============");
        try {
            //签名规则sign=sha256(key + mchId + order + amount)
            StringBuffer sb = new StringBuffer();
            if (type == 1) {
                sb.append(md5Key).append(merId);
                sb.append(data.get("order"));
            } else {
                sb.append(merId).append(md5Key);
                sb.append(data.get("orderNum"));
            }
            sb.append(data.get("amount"));

            String signStr = sb.toString();
            logger.info("[WMP]完美支付生成待签名串:{}", signStr);
            String sign = MD5Utils.sha256ToUpCase(signStr);
            logger.info("[WMP]完美支付生成签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WMP]完美支付签名异常:{}", e.getMessage());
            throw new Exception("[WMP]完美支付签名异常");
        }
    }
}
