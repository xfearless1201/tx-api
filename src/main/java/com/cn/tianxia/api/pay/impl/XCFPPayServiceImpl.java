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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName XCFPPayServiceImpl
 * @Description 鑫财富支付
 * @Date 2018年12月30日 下午2:51:01
 */
public class XCFPPayServiceImpl implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(XCFPPayServiceImpl.class);

    private String merId;// 商户号

    private String payUrl;// 支付地址

    private String notifyUrl;// 回调地址

    private String md5Key;// 秘钥

    // 构造器,初始化参数
    public XCFPPayServiceImpl(Map<String, String> data) {
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
        }
    }


    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[XCFP]鑫财富支付网银支付开始=================START==================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity, 1);
            //生成签名
            String sign = generatorSign(data);
            data.put("pay_md5sign", sign);
            //发起支付请求
            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[XCFP]鑫财富支付网银支付发起HTTP请求无响应结果");
                return PayResponse.wy_write("[XCFP]鑫财富支付网银支付发起HTTP请求无响应结果");
            }
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[XCFP]鑫财富支付网银支付发起HTTP请求响应结果:{}", jsonObject);

            if (jsonObject.containsKey("code") && "200".equals(jsonObject.getString("code"))) {
                String payUrl = jsonObject.getJSONObject("data").getString("payUrl");
                return PayResponse.wy_link(payUrl);
            }
            return PayResponse.wy_write("下单失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XCFP]鑫财富支付网银支付异常:{}", e.getMessage());
            return PayResponse.wy_write("[XCFP]鑫财富支付网银支付异常");
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[XCFP]鑫财富支付扫码支付开始=================START==================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity, 0);
            //生成签名
            String sign = generatorSign(data);
            data.put("pay_md5sign", sign);
            //发起支付请求
            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[XCFP]鑫财富支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[XCFP]鑫财富支付扫码支付发起HTTP请求无响应结果");
            }
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[XCFP]鑫财富支付扫码支付发起HTTP请求响应结果:{}", jsonObject);
            if (jsonObject.containsKey("code") && "200".equals(jsonObject.getString("code"))) {
                String payUrl = jsonObject.getJSONObject("data").getString("payUrl");
                if (StringUtils.isBlank(payEntity.getMobile())) {
                    // PC端
                    return PayResponse.sm_qrcode(payEntity, payUrl, "下单成功");
                }

                return PayResponse.sm_link(payEntity, payUrl, "下单成功");
            }
            return PayResponse.error("下单失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XCFP]鑫财富支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[XCFP]鑫财富支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[XCFP]鑫财富支付回调验签开始===================START================");
        try {
            //获取服务器原签名串
            String sourceSign = data.get("sign");
            String sign = generatorSign(data);
            logger.info("[XCFP]鑫财富支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
            if (sourceSign.equalsIgnoreCase(sign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XCFP]鑫财富支付回调验签异常:{}", e.getMessage());
        }
        return "faild";
    }

    /**
     * @param entity
     * @param type   1 网银支付  2 扫码支付
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity, int type) throws Exception {
        logger.info("[XCFP]鑫财富支付组装支付请求参数开始===================START==============");
        try {
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("pay_memberid", merId);//商户号
            data.put("pay_orderid", entity.getOrderNo());//订单号,上送订单号唯一, 字符长度20
            data.put("pay_applydate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//提交时间,时间格式：2016-12-26 18:18:18
            if (type == 1) {
                data.put("pay_bankcode", "907");//银行编码
            } else {
                data.put("pay_bankcode", entity.getPayCode());//银行编码
            }
            data.put("pay_notifyurl", notifyUrl);//服务端通知
            data.put("pay_callbackurl", entity.getRefererUrl());//页面跳转通知
            data.put("pay_amount", amount);//订单金额
            data.put("pay_productname", "top_Up");//商品名称
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XCFP]鑫财富支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[XCFP]鑫财富支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 签名
     */
    private String generatorSign(Map<String, String> data) throws Exception {
        logger.info("[XCFP]鑫财富支付生成签名开始===================START==================");
        try {

            StringBuffer sb = new StringBuffer();
            //排序
            Map<String, String> sortmap = MapUtils.sortByKeys(data);
            Iterator<String> iterator = sortmap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = sortmap.get(key);
                if (StringUtils.isBlank(val) || "pay_md5sign".equalsIgnoreCase(key)
                        || "pay_productname".equalsIgnoreCase(key) || "sign".equalsIgnoreCase(key)
                        || "attach".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            String signStr = sb.append("key=").append(md5Key).toString();
            logger.info("[XCFP]鑫财富支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[XCFP]鑫财富支付生成加密签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XCFP]鑫财富支付生成签名异常:{}", e.getMessage());
            throw new Exception("[XCFP]鑫财富支付生成签名异常");
        }
    }

}
