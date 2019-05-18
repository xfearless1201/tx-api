package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

/**
 *
 * @ClassName KFZFPayServiceImpl
 * @Description 快付支付
 * @author Jacky
 * @Date 2019年1月30日 下午16:58:58
 * @version 1.0.0
 */
public class KFZFPayServiceImpl implements PayService{

    private final static Logger logger = LoggerFactory.getLogger(KFZFPayServiceImpl.class);

    private String appId /*= "1048942986"*/;
    private String apiUrl /*= "http://116.62.61.188/api/pay/order"*/;
    private String notifyUrl /*= "http://txw.tx8899.com/XJC/Notify/KFZFNotify.do"*/;
    private String secret /*= "0729fcc53033c4d247c45ba18abd92e8"*/;

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    public KFZFPayServiceImpl(Map<String,String> map) {
        if(map.containsKey("appId")){
            this.appId = map.get("appId");
        }
        if(map.containsKey("apiUrl")){
            this.apiUrl = map.get("apiUrl");
        }
        if(map.containsKey("notifyUrl")){
            this.notifyUrl = map.get("notifyUrl");
        }
        if(map.containsKey("secret")){
            this.secret = map.get("secret");
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("快付支付扫码支付开始================START============");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("快付支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = HttpUtils.toPostForm(data, apiUrl);

            if(StringUtils.isBlank(response)){
                logger.info("快付支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("星际快付支付扫码支付发起HTTP请求无响应结果");
            }
            logger.info("快付支付扫码支付发起HTTP请求响应结果:{}",response);

            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.containsKey("status") && "1".equals(jsonObject.getString("status"))){
                //下单成功
                String jsondata = jsonObject.getString("data");
                JSONObject returnJosn = JSONObject.fromObject(jsondata);
                String pageUrl = returnJosn.getString("pay_url");

                if(StringUtils.isBlank(payEntity.getMobile())){
                    //PC端
                    return PayResponse.sm_qrcode(payEntity, pageUrl, "扫码支付下单成功");
                }

                return PayResponse.sm_link(payEntity, pageUrl, "H5支付下单成功");
            }
            return PayResponse.error("下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("快付支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("快付支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("快付 支付回调验签开始============START==============");
        try {

            String sourceSign = data.get("sign");
            logger.info("快付支付回调验签获取原签名串:{}",sourceSign);

            String sign = generatorSign(data);
            logger.info("快付支付回调验签生成签名串:{}",sign);

            if(sourceSign.equalsIgnoreCase(sign)) return "success";

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("快付支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }


    /**
     * 组装参数
     * @param entity
     * @return
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("快付支付组装支付请求参数开始==============START==============");
        try {
            Map<String,String> data = new HashMap<>();

            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额,单位元,保留两位小数
            data.put("merchant_no",appId);//商户号
            data.put("merchant_order_no",entity.getOrderNo());
            data.put("pay_type",entity.getPayCode());//支付类型 支付类别	支付类别：1：支付宝H5 2：支付宝扫码3：微信H54：微信扫码 5：QQ支付6：银联快捷 7：银联扫码 8：银联网关
            data.put("return_url",entity.getRefererUrl());//支付成功跳转链接
            data.put("notify_url",notifyUrl);//异步通知地址，订单支付成功后平台通过此地址通知商户
            data.put("time",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//订单创建时间，格式yyyy-MM-dd HH:mm:ss
            data.put("trade_amount",amount);//单位元
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("快付支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("快付支付组装支付请求参数异常");
        }
    }

    /**
     * 加密
     * @param map
     * @return
     */
    private String generatorSign(Map<String,String> map) throws Exception{
        logger.info("快付支付生成签名开始============START=============");
        try {

            //加入秘钥
            StringBuffer stringBuffer = new StringBuffer();
            //排序
            Map<String,String> data = MapUtils.sortByKeys(map);
            Iterator<String> iterator = data.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = data.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                stringBuffer.append("&").append(key).append("=").append(val);
            }
            stringBuffer.append("&").append("key").append("=").append(secret);
            String signStr = stringBuffer.toString().replaceFirst("&", "");
            logger.info("快付支付生成待签名串:{}",signStr);

            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();//小写
            logger.info("快付支付生成签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("快付支付生成签名异常:{}",e.getMessage());
            throw new Exception("快付支付生成签名异常");
        }
    }
}
