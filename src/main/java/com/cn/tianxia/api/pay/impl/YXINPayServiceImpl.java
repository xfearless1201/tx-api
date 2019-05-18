package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.JSONUtils;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName YXINPayServiceImpl
 * @Description 银鑫扫码支付
 * @Date 2019年1月25日 下午12:05:19
 */
public class YXINPayServiceImpl extends PayAbstractBaseService implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(YXINPayServiceImpl.class);
    /**
     * 回调失败响应信息
     */
    private static final String ret__failed = "fail";
    /**
     * 回调成功响应信息
     */
    private static final String ret__success = "success";
    /**
     * 商户号
     */
    private String merchId;
    /**
     * 秘钥
     */
    private String secret;
    /**
     * 回调地址
     */
    private String notifyUrl;
    /**
     * 支付地址
     */
    private String payUrl;
    /**
     * 订单查询地址
     */
    private String queryOrderUrl;

    public YXINPayServiceImpl() {
    }

    //构造器,初始化参数
    public YXINPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merchId")) {
                this.merchId = data.get("merchId");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("queryOrderUrl")) {
                this.queryOrderUrl = data.get("queryOrderUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("secret")) {
                this.secret = data.get("secret");
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
        try {
            Map<String, String> data = sealRequest(payEntity);
            logger.info("[YXIN]银鑫扫码支付请求参数：{}", JSONObject.fromObject(data));
            String resStr = HttpUtils.toPostForm(data, payUrl + payEntity.getPayCode());
            if (StringUtils.isBlank(resStr)) {
                logger.info("[YXIN]银鑫扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[YXIN]银鑫扫码支付扫码支付发起HTTP请求无响应结果");
            }
            JSONObject resJson = JSONObject.fromObject(resStr);
            logger.info("[YXIN]银鑫扫码支付响应信息：{},支付地址：{}", resJson, payUrl + payEntity.getPayCode());

            if (JSONUtils.compare(resJson, "code", "0")) {
                String qcurl = resJson.getJSONObject("data").getString("url");
                return PayResponse.sm_link(payEntity, qcurl, "支付下单成功");
            }
            return PayResponse.error("[YXIN]银鑫扫码支付下单失败:" + resJson.getString("msg"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YXIN]银鑫扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[YXIN]银鑫扫码支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    /**
     * @param
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity payEntity) throws Exception {
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());
        Map<String, String> data = new HashMap<>();
        data.put("merchantName", merchId);//商户号
        //data.put("channelId","");//通道ID,如果不传自动获取通道
        data.put("orderId", payEntity.getOrderNo());//订单号
        data.put("amount", amount);//支付金额
        data.put("noticeUrl", notifyUrl);//回调地址
        data.put("sign", generatorSign(data, "0"));//回调地址
        data.put("signType", "MD5");//加密类型
        return data;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名
     */
    private String generatorSign(Map<String, String> data, String type) throws Exception {
        StringBuilder sb = new StringBuilder();
        if ("0".equals(type) || "1".equals(type)) {
            Map<String, String> map = new TreeMap<>(data);
            for (String key : map.keySet()) {
                String val = map.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
        }
        if ("2".equals(type)) {
            sb.append("amount=").append(data.get("amount")).append("&");
            sb.append("orderId=").append(data.get("orderId")).append("&");
        }
        sb.append("key=").append(secret);
        String signStr = sb.toString();
        logger.info("[YXIN]银鑫扫码支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5(signStr.getBytes());
        logger.info("[YXIN]银鑫扫码支付生成签名串:{}", sign);
        return sign;
    }

    /**
     * 订单查询接口
     *
     * @param orderNo
     * @return
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    public boolean serchOrder(String orderNo) {
        try {
            Map<String, String> param = new HashMap<>();
            param.put("merchantName", merchId);//商户号
            param.put("orderId", orderNo);//商户订单号
            param.put("sign", generatorSign(param, "1"));
            param.put("signType", "MD5");//
            logger.info("[YXIN]银鑫扫码支付回调查询订单{}请求参数：{}", orderNo, JSONObject.fromObject(param));
            String resStr = HttpUtils.toPostForm(param, queryOrderUrl);
            logger.info("[YXIN]银鑫扫码支付回调查询订单{}响应信息：{}", orderNo, JSONObject.fromObject(resStr));
            if (StringUtils.isBlank(resStr)) {
                logger.info("[YXIN]银鑫扫码支付回调查询订单发起HTTP请求无响应,订单号{}", orderNo);
                return false;
            }
            JSONObject resJson = JSONObject.fromObject(resStr);
            if (!"0".equals(resJson.getString("code"))) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YXIN]银鑫扫码支付回调查询订单{}异常{}", orderNo, e.getMessage());
            return false;
        }

    }

    /**
     * 回调验签
     *
     * @param data
     * @return
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    private boolean verifyCallback(Map<String, String> data) {
        try {
            String sourceSign = data.get("sign");
            String sign = generatorSign(data, "2");
            logger.info("[YXIN]银鑫扫码支付生成签名串：{}--源签名串：{}", sign, sourceSign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YXIN]银鑫扫码支付回调生成签名串异常{}", e.getMessage());
            return false;
        }
    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);  //获取回调请求参数
        logger.info("[YXIN]银鑫扫码支付回调请求参数：" + JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("YXINNotify获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.secret = config.getString("secret");//从配置中获取
        this.merchId = config.getString("merchId");//从配置中获取
        this.queryOrderUrl = config.getString("queryOrderUrl");//从配置中获取

        String order_amount = infoMap.get("amount");//单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info("YXINNotify获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount);
        String order_no = infoMap.get("orderId");// 平台订单号
        String trade_no = "YXIN" + System.currentTimeMillis();// 第三方订单号
        String trade_status = "00";//订单状态
        String t_trade_status = trade_status;// 表示成功状态

        /**订单查询*/
        if (!serchOrder(order_no)) {
            logger.info("[YXIN]银鑫扫码支付回调查询订单{}失败", order_no);
            return ret__failed;
        }
        /**回调验签*/
        boolean verifyRequest = verifyCallback(infoMap);

        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setRet__success(ret__success);    //成功返回
        processNotifyVO.setRet__failed(ret__failed);      //失败返回
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);    //支付状态
        processNotifyVO.setT_trade_status(t_trade_status);     //第三方成功状态
        processNotifyVO.setRealAmount(realAmount);
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());    //回调参数
        processNotifyVO.setPayment("YXIN");
        processNotifyVO.setConfig(config);
        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}

