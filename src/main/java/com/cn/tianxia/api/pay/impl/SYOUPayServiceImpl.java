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
import java.util.Map;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName SYOUPayServiceImpl
 * @Description 顺优支付接口
 * @Date 2018年12月18日 上午11:07:04
 */
public class SYOUPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(SYOUPayServiceImpl.class);

    private String secret;//秘钥 
    private String wyPayUrl;//网银支付地址
    private String smPayUrl;//扫码支付地址
    private String notifyUrl;//回调地址
    private String merno;//商户号

    //构造器,初始化参数
    public SYOUPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("secret")) {
                this.secret = data.get("secret");
            }
            if (data.containsKey("wyPayUrl")) {
                this.wyPayUrl = data.get("wyPayUrl");
            }
            if (data.containsKey("smPayUrl")) {
                this.smPayUrl = data.get("smPayUrl");
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
        logger.info("[SYOU]顺优支付网银支付开始====================START======================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity, 1);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[SYOU]顺优支付网银支付请求参数报文:{}", JSONObject.fromObject(data).toString());
            String formStr = HttpUtils.generatorForm(data, wyPayUrl);
            logger.info("[SYOU]顺优支付网银支付生成form表单结果:{}", formStr);
            return PayResponse.wy_form(payEntity.getPayUrl(), formStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SYOU]顺优支付网银支付异常:{}", e.getMessage());
            return PayResponse.error("[SYOU]顺优支付网银支付异常");
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[SYOU]顺优支付扫码支付开始================START=================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity, 0);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[SYOU]顺优支付扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());
            int type = checkPayChannel(payEntity.getPayCode());
            if (type == 1) {
                //H5直连
                String formStr = HttpUtils.generatorForm(data, wyPayUrl);
                logger.info("[SYOU]顺优支付扫码支付生成form表单结果:{}", formStr);
                return PayResponse.sm_form(payEntity, formStr, "下单成功");

            }
            String response = HttpUtils.toPostForm(data, smPayUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[SYOU]顺优支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[SYOU]顺优支付扫码支付发起HTTP请求无响应结果");
            }
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[SYOU]顺优支付扫码支付发起HTTP请求响应结果:{}", jsonObject);

            if (jsonObject.containsKey("tradeResult") && "1".equals(jsonObject.getString("tradeResult"))) {
                //创建成功
                String payInfo = jsonObject.getString("payInfo");
                if (StringUtils.isBlank(payEntity.getMobile())) {
                    //PC端
                    return PayResponse.sm_qrcode(payEntity, payInfo, "下单成功");
                }
                return PayResponse.sm_link(payEntity, payInfo, "下单成功");
            }
            return PayResponse.error("[SYOU]顺优支付扫码支付失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SYOU]顺优支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[SYOU]顺优支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[SYOU]顺优支付回调验签开始==================START===============");
        try {

            //获取原签名串
            String sourceSign = data.get("sign");
            String sign = generatorSign(data);
            logger.info("[SYOU]顺优支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
            if (sourceSign.equalsIgnoreCase(sign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SYOU]顺优支付回调验签异常:{}", e.getMessage());
        }
        return "faild";
    }

    /**
     * @param entity
     * @param type   1 网银  其他 扫码
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity, int type) throws Exception {
        logger.info("[SYOU]顺优支付组装支付请求参数开始=====================START==================");
        try {
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("##").format(entity.getAmount());
            if (type == 1) {
                data.put("version", "2.0");//版本号    固定值 1.0
            } else {
                data.put("version", "1.0");//版本号    固定值 1.0
            }
            data.put("sign_type", "MD5");//签名方式 固定值MD5，不参与签名
            data.put("mer_no", merno);//商户代码    平台分配唯一
            data.put("back_url", notifyUrl);//后台通知地址
            if (type == 1) {
                //网银支付
                data.put("page_url", entity.getRefererUrl());
                data.put("bank_code", entity.getPayCode());//支付类型  001、微信wap;
                data.put("gateway_type", "000");//支付类型  001、微信wap;
            } else {
                data.put("gateway_type", entity.getPayCode());//支付类型  001、微信wap;
            }
//            data.put("mer_return_msg","");//回传参数    
            data.put("mer_order_no", entity.getOrderNo());//商家订单号 保证每笔订单唯一
            data.put("currency", "156");//交易币种  固定值156
//            data.put("trade_msg","");//交易请求信息   根据支付类型不同，约定不同的传值
            data.put("trade_amount", amount);//交易金额  整数，以元为单位，不允许有小数点
            data.put("order_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//订单时间    时间格式：yyyy-MM-dd HH:mm:ss
            data.put("client_ip", entity.getIp());//客户端ip    交易请求IP地址
            data.put("goods_name", "TOP-UP");//商品名称    不超过50字节
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SYOU]顺优支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[SYOU]顺优支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    private String generatorSign(Map<String, String> data) throws Exception {
        logger.info("[SYOU]顺优支付生成签名串开始===================START==================");
        try {

            //签名规则:对于某个接口的请求参数，将所有需要签名的字段按照ASCII顺序自然排序，并按照按照key1=value1&key2=value2…的格式拼接字符串，
            //并在字符串后面拼接给商户配置的私钥用key=进行拼接，生成签名摘要。
            //注意，字段值为空不进行拼接，即最后生成的签名串格式示例为：key1=&key2=value2……&key=xxx。(xxx为商户的私钥)

            StringBuffer sb = new StringBuffer();
            //排序
            Map<String, String> sortmap = MapUtils.sortByKeys(data);
            for (String key : sortmap.keySet()) {
                String val = sortmap.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key) || "sign_type".equalsIgnoreCase(key)
                        || "signType".equalsIgnoreCase(key)) {
                    continue;
                }

                sb.append(key).append("=").append(val).append("&");
            }

            sb.append("key=").append(secret);

            String signStr = sb.toString();
            logger.info("[SYOU]顺优支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[SYOU]顺优支付生成加密签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SYOU]顺优支付生成签名串异常:{}", e.getMessage());
            throw new Exception("[SYOU]顺优支付生成签名串异常");
        }
    }

    /**
     * @param payCode
     * @return
     * @Description 获取支付请求地址
     */
    private int checkPayChannel(String payCode) {
        if (payCode.equals("008") || payCode.equals("014")
                || "015".equals(payCode) || "016".equals(payCode) || "017".equals(payCode)) {
            return 1;
        } else {
            return 0;
        }
    }
}
