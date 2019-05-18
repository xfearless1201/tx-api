package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.XUNCUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName XUNCPayServiceImpl
 * @Description 迅驰支付
 * @Date 2019年1月10日 上午10:19:32
 */
public class XUNCPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(XUNCPayServiceImpl.class);

    private String appId;//商户号

    private String payUrl;//支付请求地址

    private String notifyUrl;//回调地址

    private String md5Key;//签名秘钥

    public XUNCPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            this.appId = StringUtils.isNotBlank(data.get("appId")) ? data.get("appId") : null;
            this.payUrl = StringUtils.isNotBlank(data.get("payUrl")) ? data.get("payUrl") : null;
            this.notifyUrl = StringUtils.isNotBlank(data.get("notifyUrl")) ? data.get("notifyUrl") : null;
            this.md5Key = StringUtils.isNotBlank(data.get("md5Key")) ? data.get("md5Key") : null;
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[XUNC]迅驰支付扫码支付开始==============START===============");
        try {

            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[XUNC]迅驰支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));
            String response = XUNCUtils.send(payUrl + "?type=" + payEntity.getPayCode(), data);
            if (StringUtils.isBlank(response)) {
                logger.info("[XUNC]迅驰支付扫码支付发起HTTP请求无响应结果:{}", response);
                return PayResponse.error("[XUNC]迅驰支付扫码支付发起HTTP请求无响应结果");
            }
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[XUNC]迅驰支付扫码支付发起HTTP请求响应结果:{}", jsonObject);
            if (jsonObject.containsKey("Status") && "1".equals(jsonObject.getString("Status"))) {
                //订单创建成功
                String payurl = jsonObject.getJSONObject("Result").getString("payurl");
                if (StringUtils.isBlank(payEntity.getMobile())) {
                    //PC端
                    return PayResponse.sm_qrcode(payEntity, payurl, "下单成功");
                }
                return PayResponse.sm_link(payEntity, payurl, "下单成功");
            }
            return PayResponse.error("下单失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XUNC]迅驰支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[XUNC]迅驰支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[XUNC]迅驰支付回调验签开始==============START==============");
        try {

            //获取原签名串
            String sourceSign = data.get("sign");
            logger.info("[XUNC]迅驰支付回调验签获取原签名串:{}", sourceSign);
            String sign = generatorSign(data);
            logger.info("[XUNC]迅驰支付回调验签生成签名串:{}", sign);
            if (sourceSign.equalsIgnoreCase(sign)) {
                return "success";
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XUNC]迅驰支付回调验签异常:{}", e.getMessage());
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
        logger.info("[XUNC]迅驰支付组装支付请求参数开始==============START=================");
        try {

            Map<String, String> data = new HashMap<>();

            String amount = new DecimalFormat("##").format(entity.getAmount());

            data.put("price", amount);//金额   单位为元 1.00
            data.put("order_id", entity.getOrderNo());//订单号   商户自有订单号
            data.put("mark", "TOP-UP");//备注
            data.put("notify_url", notifyUrl);//通知地址    订单成功的回调通知接口
            data.put("app_id", appId);//商户id    M001
            data.put("time", String.valueOf(System.currentTimeMillis()));//时间戳   1544176095
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XUNC]迅驰支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[XUNC]迅驰支付组装支付请求参数异常");
        }
    }


    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    private String generatorSign(Map<String, String> data) throws Exception {
        logger.info("[XUNC]迅驰支付生成签名串开始==============START================");
        try {
            StringBuffer sb = new StringBuffer();
            //签名规则:app_id=app_id&mark=mark&notify_url=notify_url&order_id=order_id&price=price&time=time&key=key
            Map<String, String> sortmap = MapUtils.sortByKeys(data);
            for (String key : sortmap.keySet()) {
                String val = sortmap.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                    continue;
                }

                sb.append(key).append("=").append(val).append("&");
            }

            String signStr = sb.append("key=").append(md5Key).toString().toUpperCase();
            logger.info("[XUNC]迅驰支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[XUNC]迅驰支付生成加密签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XUNC]迅驰支付生成签名串异常:{}", e.getMessage());
            throw new Exception("[XUNC]迅驰支付生成签名串异常");
        }
    }

}
