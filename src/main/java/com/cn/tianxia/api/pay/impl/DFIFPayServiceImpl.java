package com.cn.tianxia.api.pay.impl;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.RandomUtils;
import com.cn.tianxia.api.utils.qyf.ToolKit;

import net.sf.json.JSONObject;

/**
 * @ClassName: DFIFPayServiceImpl
 * @Description: D15支付（支付宝）
 * @Author: Zed
 * @Date: 2019-01-11 10:57
 * @Version:1.0.0
 **/

public class DFIFPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(DFIFPayServiceImpl.class);

    private String account;
    private String key;
    private String payUrl;
    private String notifyUrl;
    private String PAY_PUBLIC_KEY;
    private String PAY_PRIVATE_KEY;
    private static String CHARSET = "UTF-8";

    public DFIFPayServiceImpl(Map<String,String> data, String type) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey(type)) {
                JSONObject jsonObject = JSONObject.fromObject(data.get(type));
                if (jsonObject.containsKey("payUrl")) {
                    this.payUrl = jsonObject.getString("payUrl");
                }
                if (jsonObject.containsKey("account")) {
                    this.account = jsonObject.getString("account");
                }
                if (jsonObject.containsKey("notifyUrl")) {
                    this.notifyUrl = jsonObject.getString("notifyUrl");
                }
                if (jsonObject.containsKey("key")) {
                    this.key = jsonObject.getString("key");
                }
                if (jsonObject.containsKey("PAY_PUBLIC_KEY")) {
                    this.PAY_PUBLIC_KEY = jsonObject.getString("PAY_PUBLIC_KEY");
                }
                if (jsonObject.containsKey("PAY_PRIVATE_KEY")) {
                    this.PAY_PRIVATE_KEY = jsonObject.getString("PAY_PRIVATE_KEY");
                }
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
            String param = sealRequest(payEntity);

            String resStr = HttpUtils.toPostForm(param, payUrl);

            if (StringUtils.isBlank(resStr)) {
                logger.error("[D15PAY]D15支付下单失败：请求返回结果为空");
                PayResponse.error("[D15PAY]D15支付下单失败：请求返回结果为空");
            }
            JSONObject jsonObject = JSONObject.fromObject(resStr);

            if (jsonObject.containsKey("flag") && jsonObject.getString("flag").equals("00")) {
                String payUrl = jsonObject.getString("payUri");
                return PayResponse.sm_link(payEntity, payUrl, "下单成功");
            }

            return PayResponse.error(jsonObject.getString("msg"));

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[D15PAY]D15支付扫码支付下单失败" + e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {

        Map<String, String> metaSignMap = new TreeMap<>();
        metaSignMap.put("account", data.get("account"));
        metaSignMap.put("type", data.get("type"));
        metaSignMap.put("orderId", data.get("orderId"));
        metaSignMap.put("amount", data.get("amount"));
        metaSignMap.put("trade", data.get("trade"));
        metaSignMap.put("result", data.get("result"));// 支付状态
        metaSignMap.put("time", data.get("time"));// yyyyMMddHHmmss
        String jsonStr = ToolKit.mapToJson(metaSignMap);
        String sig = ToolKit.MD5(jsonStr + key, ToolKit.CHARSET);
        if (StringUtils.isBlank(sig)) {
            logger.error("[D15PAY]D15支付回调验签失败：回调生成签名为空");
            return "fail";
        }
        if (sig.equalsIgnoreCase(data.get("sig"))) {
            return "success";
        }
        return "fail";
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private String sealRequest(PayEntity entity) throws Exception {
        logger.info("[D15PAY]D15支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
//            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0").format(entity.getAmount() * 100);

            Map<String, String> data = new TreeMap<>();
            data.put("orderId", entity.getOrderNo());
            data.put("version", "V1.1");
            data.put("charset", "UTF-8");
            data.put("random", RandomUtils.generateString(4));

            data.put("account", account);
            data.put("type", entity.getPayCode());
            data.put("amount", amount);
            data.put("trade", "top_up");
            data.put("backUri", notifyUrl);
            data.put("skipUri", entity.getRefererUrl());

            // 参数列表转json字符串加签名密钥，使用MD5加密UTF-8编码生成签名，并将签名加入参数列表
            String metaSignJsonStr = ToolKit.mapToJson(data);
            logger.info("[D15PAY]签名前请求参数：{}", metaSignJsonStr);
            String sig = ToolKit.MD5(metaSignJsonStr + key, ToolKit.CHARSET);
            logger.info("sig=" + sig);
            data.put("sig", sig);

            // 公钥加密、BASE64位加密、URL编码加密并拼接商户号和版本号
            byte[] dataStr = ToolKit.encryptByPublicKey(ToolKit.mapToJson(data).getBytes(ToolKit.CHARSET),
                    PAY_PUBLIC_KEY);
            String param = Base64.getEncoder().encodeToString(dataStr);
            String reqParam = "data=" + URLEncoder.encode(param, ToolKit.CHARSET) + "&account=" + data.get("account")
                    + "&version=" + data.get("version");
            logger.info("[D15PAY]D15支付请求参数：{}", reqParam);

            return reqParam;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[D15PAY]D15支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }
}
