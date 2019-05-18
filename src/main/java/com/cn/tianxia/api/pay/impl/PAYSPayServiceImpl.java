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

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * PAYS支付
 *
 * @author Bing
 */
public class PAYSPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(PAYSPayServiceImpl.class);
    /**
     * 支付地址
     */
    private String payUrl;
    /**
     * 商户编号
     */
    private String payMemberid;
    /**
     * 回调地址
     */
    private String payNotifyUrl;
    /**
     * 商户密钥
     */
    private String md5Key;

    public PAYSPayServiceImpl(Map<String, String> map) {
        if (map.containsKey("payUrl")) {
            this.payUrl = map.get("payUrl");
        }
        if (map.containsKey("payMemberid")) {
            this.payMemberid = map.get("payMemberid");
        }
        if (map.containsKey("payNotifyUrl")) {
            this.payNotifyUrl = map.get("payNotifyUrl");
        }
        if (map.containsKey("md5Key")) {
            this.md5Key = map.get("md5Key");
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("PAYS支付支付扫码支付开始======================START==================");
        try {
            //封装请求参数
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("PAYS支付支付请求参数:" + JSONObject.fromObject(data).toString());
            //发送请求
            String resStr = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(resStr)) {
                logger.info("PAYS支付支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("PAYS支付支付扫码支付发起HTTP请求无响应结果");
            }
            JSONObject resObj = JSONObject.fromObject(resStr);
            logger.info("PAYS支付支付响应信息:" + resObj);

            if (resObj.containsKey("ret_code") && "0000".equals(resObj.getString("ret_code"))) {
                if (StringUtils.isNotBlank(payEntity.getMobile())) {
                    return PayResponse.sm_link(payEntity, resObj.getString("payment_codes_h5"), "下单成功");
                }
                return PayResponse.sm_qrcode(payEntity, resObj.getString("payment_codes"), "下单成功");
            }
            return PayResponse.error("下单失败:" + resObj);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("PAYS支付支付生成异常:" + e.getMessage());
            return PayUtil.returnWYPayJson("error", "form", "", "", "");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        try {
            String sourceSign = data.get("sign");
            Map<String, String> map = new HashMap<>();
            map.put("oid_partner", data.get("oid_partner")); //商家唯一id
            map.put("confirm_money", data.get("confirm_money"));//实际支付金额
            map.put("no_order", data.get("no_order")); //订单号
            map.put("money_order", data.get("money_order")); //订单金额
            map.put("userid_goods", data.get("userid_goods")); //商户的用户id
            map.put("pay_type", data.get("pay_type")); //支付类型
            map.put("pay_state", data.get("pay_state")); //订单支付状态代码
            map.put("sign_type", data.get("sign_type")); //签名类型
            map.put("server_time", data.get("server_time")); //服务器时间搓
            map.put("qrcode_createtime", data.get("qrcode_createtime")); //订单创建时间
            map.put("qrcode_endtime", data.get("qrcode_endtime")); //订单完成时间
            map.put("name_goods", data.get("name_goods")); //商品名
            String sign = generatorSign(map);
            //生成加密签名串
            logger.info("PAYS支付支付验签生成加密签名串:{}", sign);
            if (sign.equals(sourceSign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("PAYS支付支付回调验签异常:" + e.getMessage());
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
        Map<String, String> data;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String toDate = sdf.format(new Date());
            DecimalFormat df = new DecimalFormat("0.00");
            data = new HashMap<>();
            data.put("oid_partner", payMemberid);//商户号
            data.put("callback_url_result", payNotifyUrl);//异步通知地址
            data.put("return_url", payNotifyUrl);//同步通知地址
            data.put("no_order", payEntity.getOrderNo());//商户订单号
            data.put("time_order", toDate);//订单时间
            data.put("money_order", df.format(payEntity.getAmount()));//金额
            data.put("name_goods", "Pay");//商品名称
            data.put("userid_goods", payEntity.getuId());//充值用户Id
            data.put("info_order", "Pay");//充值用户Id
            data.put("pay_type", payEntity.getPayCode());//支付类型
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("PAYS支付拼装请求参数异常");
            return null;
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    @SuppressWarnings("deprecation")
    public String generatorSign(Map<String, String> data) throws Exception {
        data.put("md5_key", "458e8eac6f178f8dd5f027c323aa5b73");
        Map<String, String> sortmap = MapUtils.sortByKeys(data);
        StringBuffer sb = new StringBuffer();
        Iterator<String> iterator = sortmap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String val = sortmap.get(key);
            if (StringUtils.isBlank(val) || key.equalsIgnoreCase("sign")) continue;
            sb.append(key).append("=").append(val).append("&");
        }
        data.remove("md5_key");
        //生成待签名串
        String signStr = sb.toString();
        signStr = URLEncoder.encode(signStr.substring(0, signStr.length() - 1));
        logger.info("PAYS支付支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5(signStr.getBytes());
        logger.info("PAYS支付支付生成加密签名串:{}", sign);
        return sign;
    }
}
