package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/1 19:07
 * @Description: 豪富支付实现类
 */
public class HAOFUPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(HAOFUPayServiceImpl.class);

    private String partner;
    private String key;
    private String basePayUrl;
    private String notifyUrl;

    public HAOFUPayServiceImpl(Map<String,String> map) {
        if(map != null && !map.isEmpty()){
            if(map.containsKey("partner")){
                this.partner = map.get("partner");
            }
            if(map.containsKey("notifyUrl")){
                this.notifyUrl = map.get("notifyUrl");
            }
            if(map.containsKey("key")){
                this.key = map.get("key");
            }
            if(map.containsKey("basePayUrl")){
                this.basePayUrl = map.get("basePayUrl");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        try {
            Map<String,String> param = sealRequest(payEntity);

            logger.info("[HAOFU]豪富支付网银请求参数:{}",JSONObject.fromObject(param).toString());
            String formStr = HttpUtils.generatorForm(param,basePayUrl);

            if (StringUtils.isBlank(formStr)) {
                logger.error("[HAOFU]豪富支付下单失败：生成表单结果为空");
                PayResponse.error("[HAOFU]豪富支付下单失败：生成表单结果为空");
            }

            return PayResponse.wy_form(payEntity.getPayUrl(),formStr);

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[HAOFU]豪富支付网银支付下单失败"+e.getMessage());
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            Map<String,String> param = sealRequest(payEntity);
            logger.info("[HAOFU]豪富支付扫码请求参数:{}",JSONObject.fromObject(param).toString());
            String formStr = HttpUtils.toPostForm(param,basePayUrl + payEntity.getPayCode()); //基础请求地址拼上支付类型
            logger.info("[HAOFU]豪富支付响应信息："+formStr);
            if (StringUtils.isBlank(formStr)) {
                logger.error("[HAOFU]豪富支付下单失败：返回结果为空");
                PayResponse.error("[HAOFU]豪富支付下单失败：返回结果为空");
            }
            JSONObject resJsonObj = JSONObject.fromObject(formStr);
            if(resJsonObj.containsKey("is_success") && resJsonObj.getString("is_success").equals("T")){
            	return PayResponse.sm_link(payEntity, resJsonObj.getString("result"), "下单成功");
            }
            return PayResponse.error("[HAOFU]豪富支付下单失败：" + resJsonObj.getString("fail_msg"));
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[HAOFU]豪富支付扫码支付下单失败"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.get("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[HAOFU]豪富支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data))
            return "success";
        return "fail";
    }

    private boolean verifyCallback(String sign,Map<String,String> data) {
    	try {
			logger.info("[HAOFU]豪富支付回调请求签名串："+sign);
			String localSign = generatorSign(data);
			return sign.equalsIgnoreCase(localSign);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[HAOFU]豪富支付回调生成签名串异常");
			return false;
		}
    }

    /**
     *
     * @Description 封装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[HAOFU]豪富支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());

            data.put("partner",partner);// 32 是 是 商户合作号，由平台注册提供
            data.put("amount",amount);// 32 是 是 金额(单位:元,支持两位小数)
            data.put("request_time",String.valueOf(System.currentTimeMillis()/1000)); // 10 是 是 时间戳,精确到秒
            data.put("trade_no",entity.getOrderNo());// 32 是 是 订单号
            if(StringUtils.isNotBlank(entity.getMobile())){
            	data.put("pay_type","h5");// 2 否 是 唤醒参数:h5扫码参数:sm 不传默认为扫码	
            }else{
            	data.put("pay_type","sm");// 2 否 是 唤醒参数:h5扫码参数:sm 不传默认为扫码
            }
            data.put("notify_url",notifyUrl);// 64 是 是 异步通知地址
            data.put("sign",generatorSign(data));// 256 是 否 签名字符串

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HAOFU]豪富支付封装请求参数异常:"+e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     *
     * @Description 生成支付签名串
     * @param data
     * @return
     * @throws Exception
     */
    public String generatorSign(Map<String,String> data) throws Exception{
        logger.info("[HAOFU]豪富支付生成支付签名串开始==================START========================");
        try {
            //签名规则:
//           •1.值为空的参数不参与签名
//           •2.sign不参与签名
//           •3.签名格式:p1=v1&p2=v2&p3=v3&......&[md5key]
//           •4.排序规则:参数key按照ASCII升序
//           •5.sign统一使用小写字母
            StringBuilder strBuilder = new StringBuilder();
            TreeMap<String,String> sortedMap = new TreeMap<>(data);

            for (Map.Entry<String,String> entry:sortedMap.entrySet()) {
                if (StringUtils.isBlank(entry.getValue()) || "sign".equals(entry.getKey()))
                    continue;
                strBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            strBuilder.append(key);

            logger.info("[HAOFU]豪富支付生成待签名串:"+strBuilder.toString());

            String md5Value = MD5Utils.md5toUpCase_32Bit(strBuilder.toString());
            if (StringUtils.isBlank(md5Value)) {
                logger.error("[HAOFU]豪富支付生成签名异常：生成签名为空");
                throw new Exception("生成支付签名串异常!");
            }

            logger.info("[HAOFU]豪富支付生成加密签名串:"+md5Value.toLowerCase());

            return md5Value.toLowerCase();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HAOFU]豪富支付生成支付签名串异常:"+e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }
}
