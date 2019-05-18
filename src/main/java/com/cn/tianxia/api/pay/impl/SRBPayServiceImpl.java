package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName SRBPayServiceImpl
 * @Description 商入宝支付
 * @author Hardy
 * @Date 2018年9月28日 下午3:59:43
 * @version 1.0.0
 */
public class SRBPayServiceImpl implements PayService{
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(SRBPayServiceImpl.class);
    
    private String uid;//您的商户唯一标识，注册后在设置里获得

    private String notifyUrl;//通知回调网址
    
    private String payUrl;//支付URL
    
    private String secret;//秘钥

    private String goodsname;//商品名称

    //构造器,初始化基本参数信息
    public SRBPayServiceImpl(Map<String,String> data) {
        if(data != null && !data.isEmpty()){
            if(data.containsKey("uid")){
                this.uid = data.get("uid");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("secret")){
                this.secret = data.get("secret");
            }
            if(data.containsKey("goodsname")){
                this.goodsname = data.get("goodsname");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 扫码支付
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[SRB]商入宝扫码支付开始==================START================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名
            String sign = generatorSign(data);
            //去掉token
            data.remove("token");
            //加入签名
            data.put("key", sign);
            //发起请求
            String response = HttpUtils.generatorForm(data, payUrl);
            logger.info("[SRB]商入宝生成FORM表单请求结果:"+response);
            return PayUtil.returnPayJson("success", "1", "form表单提交成功!", "", 0, "", response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[SRB]商入宝扫码支付异常:"+e.getMessage());
            return PayUtil.returnPayJson("error", "2", "下单异常", "", 0, "", e.getMessage());
        }
    }
    
    /**
     * 
     * @Description 封装请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[SRB]商入宝组装支付请求参数开始==================START========================");
        try {
            //创建存储请求参数对象
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("uid",uid);//商户uid
            data.put("price",amount);//价格   float   必填。单位：元。精确小数点后2位
            data.put("istype",entity.getPayCode());//支付渠道    int 必填。1：支付宝；2：微信支付；3：QQ钱包支付
            data.put("notify_url",notifyUrl);//通知回调网址
            data.put("return_url",entity.getRefererUrl());//跳转网址
            data.put("orderid",entity.getOrderNo());//商户自定义订单号
            data.put("orderuid",entity.getuId());//商户自定义客户号
            data.put("token",secret);//秘钥
            data.put("goodsname",goodsname);//商品名称
            data.put("version","2");//协议版本号
            data.put("isgo_alipay","1");//是否自动打开支付宝  int 选填。当前为1，当发起支付宝支付的时候传入才有效果，1表示自动打开，0表示不自动打开
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[SRB]商入宝组装支付请求参数异常:"+e.getMessage());
            throw new Exception("组装请求参数失败!");
        }
    }
    
    /**
     * 
     * @Description 生成签名串
     * @param data
     * @return
     * @throws Exception
     */
    public String generatorSign(Map<String,String> data) throws Exception{
        logger.info("[SRB]商入宝生成签名串开始==================START==========================");
        try {
            //排序请求参数
            Map<String,String> treemap = new TreeMap<>(new Comparator<String>() {
                //升序 
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            treemap.putAll(data);
            
            //生成待签名串
            //使用到的所有参数，连Token一起，按参数名字母升序排序。把参数名和参数值拼接在一起。做md5-32位加密，
            //取字符串小写。得到key。网址类型的参数值不要urlencode。
            StringBuffer sb = new StringBuffer();
            Iterator<String> keys = treemap.keySet().iterator();
            while(keys.hasNext()){
                String key = keys.next();
                String val = treemap.get(key);
                if(StringUtils.isBlank(val) || key.equals("key") || key.equals("isgo_alipay")) continue;
                
                sb.append("&").append(key).append("=").append(val);
            }
            
            String signStr = sb.toString().replaceFirst("&","");
            logger.info("[SRB]商入宝生成待签名串:"+signStr);
            //签名
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[SRB]商入宝生成加密签名串:"+sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[SRB]商入宝生成签名串异常:"+e.getMessage());
            throw new Exception("生成签名串失败!");
        }
    }
    
    /**
     * 
     * @Description 支付回调接口
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String,String> data) {
        logger.info("[SRB]商入宝支付回调验签开始===================START=====================");
        try {
            //获取原本的key
            String sourceKey = data.get("key").toLowerCase();
            logger.info("[SRB]商入宝支付回调原签名串:"+sourceKey);
            //把token put in
            data.put("token", secret);//签名用
            String key = generatorSign(data).toLowerCase();
            logger.info("[SRB]商入宝支付回调生成签名串:"+key);
            if(key.equals(sourceKey)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[SRB]商入宝支付回调验签异常:"+e.getMessage());
        }
        return "";
    }
}
