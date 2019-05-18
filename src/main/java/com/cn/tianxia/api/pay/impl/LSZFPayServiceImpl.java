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
 * @ClassName LSZFPayServiceImpl
 * @Description 联盛支付
 * @author Hardy
 * @Date 2019年1月24日 下午8:25:53
 * @version 1.0.0
 */
public class LSZFPayServiceImpl implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(LSZFPayServiceImpl.class);
    private String appId;
    private String payUrl;
    private String notifyUrl;
    private String secret;
    
    public LSZFPayServiceImpl(Map<String,String> map) {
        if(map.containsKey("appId")){
            this.appId = map.get("appId");
        }
        if(map.containsKey("payUrl")){
            this.payUrl = map.get("payUrl");
        }
        if(map.containsKey("notifyUrl")){
            this.notifyUrl = map.get("notifyUrl");
        }
        if(map.containsKey("secret")){
            this.secret = map.get("secret");
        }
    }
    
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[LSZF]联盛支付扫码支付开始================START============");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[LSZF]联盛支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = HttpUtils.toPostForm(data, payUrl);
            
            if(StringUtils.isBlank(response)){
                logger.info("[LSZF]联盛支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[LSZF]联盛支付扫码支付发起HTTP请求无响应结果");
            }
            logger.info("[LSZF]联盛支付扫码支付发起HTTP请求响应结果:{}",response);
            
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.containsKey("code") && "1000".equals(jsonObject.getString("code"))){
                //下单成功
                String pageUrl = jsonObject.getString("pageUrl");
                
                if(StringUtils.isBlank(payEntity.getMobile())){
                    //PC端
                    return PayResponse.sm_qrcode(payEntity, pageUrl, "扫码支付下单成功");
                }
                
                return PayResponse.sm_link(payEntity, pageUrl, "H5支付下单成功");
            }
            return PayResponse.error("下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LSZF]联盛支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[LSZF]联盛支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[LSZF]联盛支付回调验签开始============START==============");
        try {
            
            String sourceSign = data.get("sign");
            logger.info("[LSZF]联盛支付回调验签获取原签名串:{}",sourceSign);
            
            String sign = generatorSign(data);
            logger.info("[LSZF]联盛支付回调验签生成签名串:{}",sign);
            
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LSZF]联盛支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }
    
    
    /**
     * 组装参数
     * @param payEntity
     * @return
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[LSZF]联盛支付组装支付请求参数开始==============START==============");
        try {
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额,单位元,保留两位小数
            data.put("orderId",entity.getOrderNo());//商户订单ID
            data.put("appId",appId);//平台分配给商户的应用ID
            data.put("amount",amount);//订单金额，保留两位小数
            data.put("remark",entity.getUsername());//订单备注（未确保准确到账，请填写付款支付宝绑定身份的真实姓名）
            data.put("time",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//订单创建时间，格式yyyy-MM-dd HH:mm:ss
            data.put("notify",notifyUrl);//异步通知地址，订单支付成功后平台通过此地址通知商户
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LSZF]联盛支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[LSZF]联盛支付组装支付请求参数异常");
        }
    }
    
    /**
     * 加密 
     * @param map
     * @return
     */
    private String generatorSign(Map<String,String> map) throws Exception{
        logger.info("[LSZF]联盛支付生成签名开始============START=============");
        try {
            
            //加入秘钥
            map.put("secret", secret);
            StringBuffer sb = new StringBuffer();
            //排序
            Map<String,String> data = MapUtils.sortByKeys(map);
            Iterator<String> iterator = data.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = data.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                
                sb.append("&").append(key).append("=").append(val);
            }
            String signStr = sb.toString().replaceFirst("&", "");
            logger.info("[LSZF]联盛支付生成待签名串:{}",signStr);

            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();//小写
            logger.info("[LSZF]联盛支付生成签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LSZF]联盛支付生成签名异常:{}",e.getMessage());
            throw new Exception("[LSZF]联盛支付生成签名异常");
        }
    }

}
