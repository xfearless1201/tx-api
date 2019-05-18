package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
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
import com.cn.tianxia.api.utils.pay.RandomUtils;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName BPZFPayServiceImpl
 * @Description BP支付
 * @author Hardy
 * @Date 2018年12月19日 下午5:15:43
 * @version 1.0.0
 */
public class BPZFPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(BPZFPayServiceImpl.class);
    
    private String merno;//商户号
    
    private String payUrl;//支付地址
    
    private String notifyUrl;//回调地址
    
    private String secret;//秘钥

    //构造器,初始化参数
    public BPZFPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("merno")){
                this.merno = data.get("merno");
            }
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            if(data.containsKey("secret")){
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
        logger.info("[BPZF]BP支付扫码支付开始===================START================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            String sign = generatorSign(data, 1);
            data.put("sign",sign);
            logger.info("[BPZF]BP支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = HttpUtils.get(data, payUrl);
            if(StringUtils.isBlank(response)){
                logger.info("[BPZF]BP支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[BPZF]BP支付扫码支付发起HTTP请求无响应结果");
            }
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.containsKey("status") && "0".equals(jsonObject.getString("status"))){
                //支付成功
                String code_url = jsonObject.getString("code_url");
                
                if(StringUtils.isBlank(payEntity.getMobile())){
                    //PC端
                    return PayResponse.sm_qrcode(payEntity, code_url, "下单成功");
                }
                
                return PayResponse.sm_link(payEntity, code_url, "下单成功");
            }
            
            return PayResponse.error("下单失败:"+response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BPZF]BP支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[BPZF]BP支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[BPZF]BP支付回调验签开始=================START=============");
        try {
            
            //获取原签名串
            String sourceSign = data.get("sign");
            logger.info("[BPZF]BP支付回调验签获取原签名串:{}",sourceSign);
            String sign = generatorSign(data, 0);
            logger.info("[BPZF]BP支付回调验签生成签名串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BPZF]BP支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }
    
    /**
     * 
     * @Description 组装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[BPZF]BP支付组装支付请求参数开始==================START=====================");
        try {
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("##").format(entity.getAmount()*100);
            data.put("mer_id",merno);//商户号 分配的商户号
            data.put("out_trade_no",entity.getOrderNo());//商户订单号 不能重复
            data.put("pay_type",entity.getPayCode());//支付类型  002：微信扫码
            data.put("goods_name","TOP-UP");//商品描述    不可以为中文
            data.put("total_fee",amount);//交易金额 单位：分
            data.put("callback_url",entity.getRefererUrl());//前台回调URL   用于在支付完成后回调的url
            data.put("notify_url",notifyUrl);//后台同步URL 支付完成后，渠道需要同步支付成功数据，此参数为同步地址，同步参数参见 1.2
//            data.put("attach","");//透传参数    
//            data.put("term_type","");//终端类型 0：Android，1：ios。可能针对不同终端有不同跳转url
            data.put("nonce_str",RandomUtils.generateString(8));//随机字符串    不重复，加入签名
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BPZF]BP支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[BPZF]BP支付组装支付请求参数异常");
        }
    }
    
    /**
     * 
     * @Description 生成签名串
     * @param data
     * @param type 1 网银 其他 扫码
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> data,int type) throws Exception{
        logger.info("[BPZF]BP支付生成签名串开始===================START===================");
        try {
            //签名字符串示例:
            //mer_id=100200&nonce_str=159888222&out_trade_no=1598872366&total_fee=100&key=z19eqj8rbwxmux1fu7aqmzjn5hcsbb5q
            StringBuffer sb = new StringBuffer();
            if(type == 1){
                sb.append("mer_id=").append(data.get("mer_id")).append("&");
                sb.append("nonce_str=").append(data.get("nonce_str")).append("&");
                sb.append("out_trade_no=").append(data.get("out_trade_no")).append("&");
                sb.append("total_fee=").append(data.get("total_fee")).append("&");
            }else{
                //回调签名
                //后台同步签名是用mer_id=XXX&out_trade_no=XXX&pay_type=XXX&real_fee=XXX&total_fee=XXX&key= XXX 然后md5加密而成的
                sb.append("mer_id=").append(data.get("mer_id")).append("&");
                sb.append("out_trade_no=").append(data.get("out_trade_no")).append("&");
                sb.append("pay_type=").append(data.get("pay_type")).append("&");
                sb.append("real_fee=").append(data.get("real_fee")).append("&");
                sb.append("total_fee=").append(data.get("total_fee")).append("&");
            }
            sb.append("key=").append(secret);
            
            String signStr = sb.toString();
            logger.info("[BPZF]BP支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[BPZF]BP支付生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BPZF]BP支付生成签名串异常:{}",e.getMessage());
            throw new Exception("[BPZF]BP支付生成签名串异常");
        }
    }
    

}
