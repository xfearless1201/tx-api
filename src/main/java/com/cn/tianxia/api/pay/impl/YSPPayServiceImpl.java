package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
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
 * @ClassName YSPPayServiceImpl
 * @Description 银商付支付
 * @author Hardy
 * @Date 2019年2月2日 上午10:24:58
 * @version 1.0.0
 */
public class YSPPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(YSPPayServiceImpl.class);
    
    private String apiCode;//商户号
    
    private String payUrl;//支付请求地址
    
    private String notifyUrl;//回调地址
    
    private String apiKey;//签名秘钥
    
    private String jsonUrl;
    
    //构造器,初始化参数
    public YSPPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("apiCode")){
                this.apiCode = data.get("apiCode");
            }
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            if(data.containsKey("apiKey")){
                this.apiKey = data.get("apiKey");
            }
            if(data.containsKey("jsonUrl")){
                this.jsonUrl = data.get("jsonUrl");
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
        logger.info("[YSP]银商付支付扫码支付开始============START==========");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[YSP]银商付支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            if(StringUtils.isBlank(jsonUrl)){
                //直连
                String formStr = HttpUtils.generatorForm(data, payUrl);
                logger.info("[YSP]银商付支付生成form请求表单结果:{}",formStr);
                return PayResponse.sm_form(payEntity, formStr, "生成form表单生成");
            }else{
                //发起HTTP请求
                String response = HttpUtils.toPostForm(data, payUrl);
                if(StringUtils.isBlank(response)){
                    logger.info("[YSP]银商付支付发起HTTP请求无响应结果");
                    return PayResponse.error("[YSP]银商付支付发起HTTP请求无响应结果");
                }
                logger.info("[YSP]银商付支付发起HTTP请求响应结果:{}",response);
                
                //解析响应结果
                JSONObject jsonObject = JSONObject.fromObject(response);
                if(jsonObject.containsKey("payurl") && StringUtils.isNotBlank(jsonObject.getString("payurl"))){
                    String pay_url = jsonObject.getString("payurl");
                    if(StringUtils.isBlank(payEntity.getMobile())){
                        return PayResponse.sm_qrcode(payEntity, pay_url, "下单成功");
                    }
                    return PayResponse.sm_link(payEntity, pay_url, "下单成功");
                }
                return PayResponse.error("下单失败:"+response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YSP]银商付支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[YSP]银商付支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[YSP]银商付支付回调验签开始=============START=================");
        try {
            
            String sourceSign = data.get("sign");
            logger.info("[YSP]银商付支付回调验签获取原签名串:{}",sourceSign);
            String sign = generatorSign(data);
            logger.info("[YSP]银商付支付回调验签生成签名串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YSP]银商付支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }

    
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[YSP]银商付支付组装支付请求参数开始=================START==============");
        try {
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            
            //时间戳
            String time = String.valueOf(System.currentTimeMillis()).substring(0, 10);
            
            if(StringUtils.isBlank(jsonUrl)){
                //非json对象
                data.put("return_type","html");//e  返回数据类型  是   字符串 必填参数json， html（详情请看，返回说明）
            }else{
                data.put("return_type","json");//e  返回数据类型  是   字符串 必填参数json， html（详情请看，返回说明）
            }
            data.put("api_code",apiCode);//商户号   是   字符串 必须
            data.put("is_type",entity.getPayCode());//支付类型   是   字符串 必须，支付渠道：
            data.put("price",amount);//订单定价 是   float，保留2位小数    必须，保留2位小数，不能传0
            data.put("order_id",entity.getOrderNo());//您的自定义单号   是   字符串，最长50位   必须，在商户系统中保持唯一
            data.put("time",time);//发起时间是 时间戳，最长10位   必须 时间戳
            data.put("mark","TOP-UP");//描述是字符串，最长100位 必须 粗略说明支付目的（例如 购买食杂）
            data.put("return_url",entity.getRefererUrl());//成功后网页跳转地址   是   字符串，最长255位  必须，成功后网页跳转地址（例如 http://www.qq.com）
            data.put("notify_url",notifyUrl);//通知状态异步回调接收地址    是   字符串，最长255位  必须
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YSP]银商付支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[YSP]银商付支付组装支付请求参数异常");
        }
    }
    
    /**
     * 
     * @Description 生成签名串
     * @param data
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> data ) throws Exception{
        logger.info("[YSP]银商付支付生成签名串开始===================START=================");
        try {
            
            //签名规则:必须，一定存在。我们把使用到的所有参数，按照参数值首字母ASCII升序排序，
            //并以url传参格式拼接在一起，最后加上您的商户秘钥。一起做md5-32位加密，取字符串大写。得到sign。
            //您需要在您的服务端按照同样的算法，自己验证此sign是否正确。只在正确时，执行您自己逻辑中代码。
            StringBuffer sb = new StringBuffer();
            Map<String,String> map = MapUtils.sortByKeys(data);
            Iterator<String> iterator = map.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = map.get(key);
                
                if("sign".equalsIgnoreCase(key) || "messages".equalsIgnoreCase(key)) continue;
                
                sb.append(key).append("=").append(val).append("&");
            }
            
            String signStr = sb.append("key=").append(apiKey).toString();
            logger.info("[YSP]银商付支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);//大写串
            logger.info("[YSP]银商付支付生成签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YSP]银商付支付生成签名串异常:{}",e.getMessage());
            throw new Exception("[YSP]银商付支付生成签名串异常");
        }
    }
}
