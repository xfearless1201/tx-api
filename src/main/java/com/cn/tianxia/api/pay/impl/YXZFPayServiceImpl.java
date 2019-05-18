package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class YXZFPayServiceImpl extends PayAbstractBaseService implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(YXZFPayServiceImpl.class);
    /**
     * 回调失败响应信息
     */
    private static final String ret__failed = "fail";
    /**
     * 回调成功响应信息
     */
    private static final String ret__success = "success";
    /**
     * 支付地址
     */
    private String payUrl;
    /**
     * 商户编号
     */
    private String AppId;
    /**
     * 商户密钥
     */
    private String md5Key;

    /**
     * 无参构造方法
     */
    public YXZFPayServiceImpl() {
    }

    /**
     * 构造方法
     */
    public YXZFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("AppId")) {
                this.AppId = data.get("AppId");
            }
            if (data.containsKey("md5Key")) {
                this.md5Key = data.get("md5Key");
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
        logger.info("[YXZF]云悉支付扫码支付开始======================START==================");
        try {
            //封装请求参数
            JSONObject data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data, "0");
            data.put("sign", sign);
            logger.info("[YXZF]云悉支付请求参数:" + data.toString());
            //生成请求表单
            String resStr = HttpUtils.toPostJsonStr(data, payUrl);
            logger.info("[YXZF]云悉支付响应信息:" + resStr);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[YXZF]云悉扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[YXZF]云悉扫码支付发起HTTP请求无响应结果");
            }
            JSONObject resJsonObj = JSONObject.fromObject(resStr);
            if (!resJsonObj.containsKey("code") || !"100".equals(resJsonObj.getString("code"))) {
                return PayResponse.error("[YXZF]云悉扫码支付请求失败");
            }
            resJsonObj = resJsonObj.getJSONObject("data");
            if (resJsonObj.containsKey("result_code") || "0000".equals(resJsonObj.getString("result_code"))) {
                if (StringUtils.isNotBlank(payEntity.getMobile())) {
                    return PayResponse.sm_link(payEntity, resJsonObj.getString("code_url"), resJsonObj.getString("result_msg"));
                }
                return PayResponse.sm_qrcode(payEntity, resJsonObj.getString("code_url"), resJsonObj.getString("result_msg"));
            }
            return PayResponse.error("[YXZF]云悉扫码支付失败");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YXZF]云悉支付生成异常:" + e.getMessage());
            return PayResponse.error("[YXZF]云悉支付下单异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    /**
     * @param
     * @param
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    public JSONObject sealRequest(PayEntity payEntity) {
        JSONObject outJsonObj = new JSONObject();
        JSONObject inJsonObj = new JSONObject();
        outJsonObj.put("appid", AppId);//商户号
        outJsonObj.put("method", payEntity.getPayCode());
        String uid = UUID.randomUUID().toString();
        DecimalFormat df = new DecimalFormat("0");
        inJsonObj.put("store_id", "");//商户号
        inJsonObj.put("total", df.format(payEntity.getAmount() * 100));//金额 单位：分
        inJsonObj.put("nonce_str", uid.replace("-", ""));
        inJsonObj.put("out_trade_no", payEntity.getOrderNo());//订单号
        inJsonObj.put("body", "");//商品名称
        outJsonObj.put("data", inJsonObj);
        return outJsonObj;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(JSONObject data, String type) throws Exception {
        String dataStr = "";
        if ("0".equals(type)) {
            dataStr = data.getString("data");
        } else {
            dataStr = data.toString();
        }
        Gson gson = new Gson();
        Map<String, String> value = gson.fromJson(dataStr, Map.class);
        Map<String, String> sortmap = new TreeMap<>(value);
        StringBuffer sb = new StringBuffer();
        Iterator<String> iterator = sortmap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String val = sortmap.get(key);
            if (StringUtils.isBlank(val) || key.equalsIgnoreCase("sign")) continue;
            sb.append(key).append("=").append(val).append("&");
        }
        sb.append("key=").append(md5Key);
        //生成待签名串
        String signStr = sb.toString();
        logger.info("[YXZF]云悉支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(signStr);
        logger.info("[YXZF]云悉支付生成加密签名串:{}", sign);
        return sign;
    }

    private boolean verifyCallback(Map<String, String> data) {
        try {
            String sourceSign = data.remove("sign");
            JSONObject callbackJson = JSONObject.fromObject(data);
            String sign = generatorSign(callbackJson, "1");
            logger.info("[YXZF]云悉支付回调生成签名串" + sign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YXZF]云悉支付回调生成签名串异常" + e.getMessage());
            return false;
        }
    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);  //获取回调请求参数
        logger.info("云悉支付回调请求参数：" + infoMap);
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("YXZFNotify获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.md5Key = config.getString("md5Key");    //从配置中获取
        boolean verifyRequest = verifyCallback(infoMap);
        String order_amount = infoMap.get("total_fee");//单位：分
        if (StringUtils.isBlank(order_amount)) {
            logger.info("YXZFNotify获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount) / 100;
        String order_no = infoMap.get("u_out_trade_no");// 平台订单号
        String trade_no = infoMap.get("out_trade_no");// 第三方订单号
        String trade_status = infoMap.get("status");//订单状态
        String t_trade_status = "1";// 表示成功状态

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
        processNotifyVO.setPayment("YXZF");

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}
