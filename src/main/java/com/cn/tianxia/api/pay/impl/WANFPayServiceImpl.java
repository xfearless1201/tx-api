package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HmacUtils;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName WANFPayService
 * @Description 万福支付
 * @Date 2018年12月17日 下午9:54:32
 */
public class WANFPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(WANFPayServiceImpl.class);

    private String secret;//秘钥 
    private String payUrl;//支付地址
    private String notifyUrl;//回调地址
    private String merno;//商户号

    //构造器,初始化参数
    public WANFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("secret")) {
                this.secret = data.get("secret");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("merno")) {
                this.merno = data.get("merno");
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
        logger.info("[WANF]万福支付扫码支付开始==============START==============");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[WANF]万福支付扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());
            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[WANF]万福支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[WANF]万福支付扫码支付发起HTTP请求无响应结果");
            }

            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[WANF]万福支付扫码支付响应参数报文:{}", jsonObject);

            if (jsonObject.containsKey("code") && "1".equals(jsonObject.getString("code"))) {
                //成功
                JSONObject result = JSONObject.fromObject(jsonObject.get("data"));
                String url = result.getString("url");
                return PayResponse.sm_link(payEntity, url, "下单成功");
            }
            return PayResponse.error("下单失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WANF]万福支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[WANF]万福支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[WANF]万福支付回调验签开始==================START==================");
        try {
            //获取原签名串
            String sourceSign = data.get("sign");
            //生成回调签名串
            String sign = generatorSign(data);
            logger.info("[WANF]万福支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);

            if (sourceSign.equalsIgnoreCase(sign)) {
                return "success";
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WANF]万福支付回调验签异常:{}", e.getMessage());
        }
        return "faild";
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[WANF]万福支付组装支付请求参数开始==================START==================");
        try {
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("price", amount);//金额
            data.put("order_no", entity.getOrderNo());//您的订单号
            data.put("type", entity.getPayCode());//支付类型
            data.put("notifyurl", notifyUrl);//异步通知URL
            data.put("returnurl", entity.getRefererUrl());//同步跳转URL
            data.put("merchant", merno);//商户号
            data.put("client_ip", entity.getIp());//充值用户的IP
            data.put("user", entity.getuId());//扩展字段
            data.put("extend", "TOP-UP");//扩展字段
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WANF]万福支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[WANF]万福支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    private String generatorSign(Map<String, String> data) throws Exception {
        logger.info("[WANF]万福支付生成签名开始===============START======================");
        try {
            //签名规则:
            //直接把请求数据中的所有元素(除sign本身)按照“key值=value值”的格式拼接起来，
            //并且把这些拼接以后的元素以“&”字符再连接起来（把每一项按常规顺序排列[Standard ASCII，不改变类型]），值为空的去除），
            //url之类用urldecode解码。 然后用商户设定的secretkey，执行hmacSha256计算，以Base64转码的结果(大写)为签名串sign。
            StringBuffer sb = new StringBuffer();
            Map<String, String> sortmap = MapUtils.sortByKeys(data);
            for (String key : sortmap.keySet()) {
                String val = sortmap.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append("&").append(key).append("=").append(val);
            }
            String signStr = sb.toString().replaceFirst("&", "");
            logger.info("[WANF]万福支付生成待签名串:{}", signStr);
            String hmacStr = HmacUtils.sha256_HMAC(signStr, secret);
            logger.info("[WANF]万福支付生成hmac加密串:{}", hmacStr);
//            String sign = new Base64().encodeToString(hmacStr.getBytes()).toUpperCase();
            String sign = Base64.getEncoder().encodeToString(hmacStr.getBytes()).toUpperCase();
            logger.info("[WANF]万福支付生成加密签名串：{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[WANF]万福支付生成签名异常:{}", e.getMessage());
            throw new Exception("[WANF]万福支付生成签名异常");
        }
    }

}
