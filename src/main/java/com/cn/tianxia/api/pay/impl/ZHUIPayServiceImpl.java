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
 * @ClassName ZHUIPayServiceImpl
 * @Description 众惠支付
 * @author Hardy
 * @Date 2019年1月14日 下午8:53:47
 * @version 1.0.0
 */
public class ZHUIPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(ZHUIPayServiceImpl.class);
    
    private String channelId;//商户号
    
    private String payUrl;//支付请求地址
    
    private String notifyUrl;//回调地址
    
    private String md5Key;//签名秘钥
    
    //构造器,初始化参数
    public ZHUIPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("channelId")){
                this.channelId = data.get("channelId");
            }
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            if(data.containsKey("md5Key")){
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
        logger.info("[ZHUI]众惠支付扫码支付开始==============START====================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("[ZHUI]众惠支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //生成表单
            String formStr = HttpUtils.generatorForm(data, payUrl);
            if(StringUtils.isBlank(formStr)){
            	logger.info("[ZHUI]众惠支付扫码支付HTTP请求无响应");
            	return PayResponse.error("[ZHUI]众惠支付扫码支付HTTP请求无响应");
            }
            logger.info("[ZHUI]众惠扫码支付响应信息："+formStr);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZHUI]众惠支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[ZHUI]众惠支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        // TODO Auto-generated method stub
        return null;
    }

    
    /**
     * 
     * @Description 组装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity)throws Exception{
        logger.info("[ZHUI]众惠支付组装支付请求参数开始===============START=================");
        try {
            //创建存储对象
            Map<String,String> data = new HashMap<>();
            
            String amount = new DecimalFormat("##").format(entity.getAmount()*100);
            
            data.put("channelId",channelId);//商户号,平台运营提供   非空  
            data.put("orderNo",entity.getOrderNo());//订单号,商户订单号  非空  12345678243
            data.put("totalFee",amount);//订单金额,分为单位 非空  100
            data.put("payType",entity.getPayCode());//支付类型,参考附录  非空  11
            data.put("timeStamp",String.valueOf(System.currentTimeMillis()));//时间戳,时间戳以北京时间毫秒单位5分钟有效期   非空  1543486421123
            data.put("showUrl",entity.getRefererUrl());//前台回调地址,支付完成跳转地址    非空  http://www.baidu.com
            data.put("notifyUrl",notifyUrl);//异步回调地址,支付结果通知地址  非空  http://www.baidu.com
            data.put("orderDesc","TOP-UP");//订单订单描述,商品说明  非空  Iphone xs
//            data.put("spbillCreateIp","");//Ip地址,微信支付IP必须真实 可空  127.0.0.1
//            data.put("resultType","");//返回类型,为1：返回html页面；为0返回json字符串 不送则默认为html页面；为2则是扫码页面  可空 
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZHUI]众惠支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[ZHUI]众惠支付组装支付请求参数异常");
        }
    }
    
    /**
     * 
     * @Description 生成签名串
     * @param data
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> data) throws Exception{
        logger.info("[ZHUI]众惠支付生成签名串开始===============START===================");
        try {
            
            StringBuffer sb = new StringBuffer();
            //排序
            Map<String,String> map = MapUtils.sortByKeys(data);
            Iterator<String> iterator = map.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = map.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                
                sb.append(key).append("=").append(val).append("&");
            }
            
            String signStr = sb.append("key=").append(md5Key).toString();
            logger.info("[ZHUI]众惠支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[ZHUI]众惠支付生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZHUI]众惠支付生成签名串异常:{}",e.getMessage());
            throw new Exception("[ZHUI]众惠支付生成签名串异常");
        }
    }
}
