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
 * @ClassName LMZFPaySeriviceImpl
 * @Description 乐美支付
 * @author Hardy
 * @Date 2018年12月8日 下午3:27:59
 * @version 1.0.0
 */
public class LMZFPayServiceImpl implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(LMZFPayServiceImpl.class);
    private String userId;//商户号
    private String notifyUrl;
    private String fsecret;
    private String bsecret;
    private String payUrl;//支付地址
    
    public LMZFPayServiceImpl(Map<String,String> map){
        if(map != null && !map.isEmpty()){
            if(map.containsKey("userId")){
                this.userId = map.get("userId");
            }
            if(map.containsKey("notifyUrl")){
                this.notifyUrl = map.get("notifyUrl");
            }
            if(map.containsKey("fsecret")){
                this.fsecret = map.get("fsecret");
            }
            if(map.containsKey("bsecret")){
                this.bsecret = map.get("bsecret");
            }
            if(map.containsKey("payUrl")){
                this.payUrl = map.get("payUrl");
            }
        }
    }
    
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[LMZF]乐美支付网银支付开始==================START================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity, 0);
            //生成签名串
            String sign = generatorSign(data,1);
            data.put("sign",sign);
            logger.info("[LMZF]乐美支付网银支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String formStr = HttpUtils.generatorForm(data, payUrl);
            return PayResponse.wy_form(payEntity.getPayUrl(), formStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LMZF]乐美支付网银支付异常:{}",e.getMessage());
            return PayResponse.error("[LMZF]乐美支付网银支付异常");
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[LMZF]乐美支付扫码支付开始==============START==============");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity, 0);
            //生成签名串
            String sign = generatorSign(data,1);
            data.put("sign",sign);
            logger.info("[LMZF]乐美支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = HttpUtils.toPostForm(data, payUrl);
            if(StringUtils.isBlank(response)){
                logger.info("[LMZF]乐美支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[LMZF]乐美支付扫码支付发起HTTP请求无响应结果");
            }
            logger.info("[LMZF]乐美支付扫码支付发起HTTP请求响应结果:{}",response);
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.containsKey("Resultcode") && "0".equals(jsonObject.getString("Resultcode"))){
                logger.info("[LMZF]乐美支付扫码支付成功状态值:{}",jsonObject.getString("Resultcode"));
              //支付充值成功
                String PayUrl = jsonObject.getString("PayUrl");
                return PayResponse.sm_link(payEntity, PayUrl, "下单成功");
            }
            
            return PayResponse.error("下单失败:"+response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LMZF]乐美支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[LMZF]乐美支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[LMZF]乐美支付回调验签开始==============START=============={}",data);
        try {
            String sourceSign = data.get("sign");
            logger.info("[LMZF]乐美支付回调验签原签名串:{}",sourceSign);
            String sign = generatorSign(data,0);
            logger.info("[LMZF]乐美支付回调验签MD5加密串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LMZF]乐美支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }

    
    /**
     * 
     * @Description 组装支付请求参数
     * @param entity
     * @param type 支付类型 1 网银 
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity,int type) throws Exception{
        logger.info("[LMZF]乐美支付组装支付请求参数开始================START==================");
        try {
            //创建参数存储对象
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额
            data.put("UserID",userId);//商号,商户编号
            data.put("OrderID",entity.getOrderNo());//在商户系统中保持唯一 商户订单号
            data.put("FaceValue",amount);//单位为RMB-元。精确到小数点后两位。如49.65    支付金额
            if(type == 1){
                data.put("ChannelID","5000");//(参数详见通道限额表通道编号)  充值类型
                data.put("ResultType","0");//默认值为0   数据返回类型
            }else{
                data.put("ChannelID",entity.getPayCode());//(参数详见通道限额表通道编号)  充值类型  
                data.put("ResultType","1");//默认值为0   数据返回类型
            }
            data.put("Subject","TOP-UP");//可为空    产品名称
//            data.put("Notic","");//可为空  附加信息
            data.put("Description","");//可为空    产品描述
            data.put("TimeStamp",String.valueOf(System.currentTimeMillis()));// 时间戳
            data.put("Version","V2.0");//   接口版本号
            data.put("IP",entity.getIp());//APP和网页支付提交用户端ip 用户IP
            
            data.put("NotifyUrl",notifyUrl);//通知地址 异步通知地址
            data.put("ResultUrl",entity.getRefererUrl());//跳转地址 同步通知地址
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LMZF]乐美支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[LMZF]乐美支付组装支付请求参数异常");
        }
    }
    
    
    /**
     * 
     * @Description 生成签名
     * @param data
     * @param type 1 支付
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> data,int type) throws Exception{
        logger.info("[LMZF]乐美支付生成签名开始================START==================");
        try {
            
            //参数排序
            Map<String,String> sortmap = MapUtils.sortByKeys(data);
            Iterator<String> iterator = sortmap.keySet().iterator();
            StringBuffer sb = new StringBuffer();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = sortmap.get(key);
                if(StringUtils.isBlank(val) || key.equalsIgnoreCase("sign")) continue;
                sb.append(key).append("=").append(val).append("&");
            }
            if(type == 1){
                sb.append("key=").append(fsecret);
            }else{
                sb.append("key=").append(bsecret);
            }
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[LMZF]乐美支付生成待签名串:{}",signStr);
            //进行MD5加密
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[LMZF]乐美支付生成签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LMZF]乐美支付生成签名异常:{}",e.getMessage());
            throw new Exception("[LMZF]乐美支付生成签名异常");
        }
    }
}
