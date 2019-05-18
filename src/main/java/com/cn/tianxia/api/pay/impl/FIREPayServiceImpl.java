package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

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
 * @ClassName FIREPayServiceImpl
 * @Description 火火支付
 * @author Hardy
 * @Date 2018年12月19日 下午3:31:59
 * @version 1.0.0
 */
public class FIREPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(FIREPayServiceImpl.class);
    
    private String payUrl;//支付地址
    
    private String notifyUrl;//回调地址
    
    private String merno;//商户号
    
    private String secret;//秘钥

    //构造器,初始化参数
    public FIREPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            if(data.containsKey("merno")){
                this.merno = data.get("merno");
            }
            if(data.containsKey("secret")){
                this.secret = data.get("secret");
            }
        }
    }

    /**
     * 网银支付
     */
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
        logger.info("[FIRE]火火支付扫码支付开始==============start===============");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data,1);
            data.put("key", sign);
            
            logger.info("[FIRE]火火支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //生成form表单
            String formStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[FIRE]火火支付扫码支付生成form请求表单结果:{}",formStr);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[FIRE]火火支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[FIRE]火火支付扫码支付异常");
        }
    }

    /**
     * 回调通知
     */
    @Override
    public String callback(Map<String, String> data) {
        logger.info("[FIRE]火火支付回调验签开始==============START===============");
        try {
            String sourceSign = data.get("key");
            logger.info("[FIRE]火火支付回调验签获取原签名串:{}",sourceSign);
            String sign = generatorSign(data, 0);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[FIRE]火火支付回调验签异常:{}",e.getMessage());
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
        logger.info("[FIRE]火火支付组装支付请求参数开始================START===============");
        try {
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("uid",merno);//商户uid  int(10) 必填。您的商户唯一标识，在“账户信息”-“支付信息”中获取。
            data.put("price",amount);//价格   float   必填。单位：元。精确小数点后2位
            data.put("notify_url",notifyUrl);//通知回调网址  string(255) 必填。用户支付成功后，我们服务器会主动发送一个post消息到这个网址。由您自定义。不要urlencode。例：http://www .aaa.com/qpay_notify
            data.put("return_url",entity.getRefererUrl());//跳转网址    string(255) 必填。用户支付成功后，我们会让用户浏览器自动跳转到这个网址。由您自定义。不要urlencode。例：http://www.aaa .com/qpay_return
            data.put("orderid",entity.getOrderNo());//商户自定义订单号   string(50)  必填。我们会据此判别是同一笔订单还是新订单。我们回调时，会带上这个参数。例：201710192541
            data.put("orderuid",entity.getUsername());//商户自定义客户号  string(100) 选填。我们会显示在您后台的订单列表中，方便您看到是哪个用户的付款，方便后台对账。强烈建议填写。可以填用户名，也可以填您数据库中的用户uid。例：xxx, xxx@aaa.com
            data.put("goodsname","TOP-UP");//商品名称 string(100) 选填。您的商品名称，用来显示在后台的订单名称。如未设置，我们会使用后台商品管理中对应的商品名称
//            data.put("attach","TOP-UP");//附加内容    string(2048)    选填。回调时将会根据传入内容原样返回
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[FIRE]火火支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[FIRE]火火支付组装支付请求参数异常");
        }
    }

    /**
     * 
     * @Description 生成签名串
     * @param data
     * @param type 1 网银  其他 扫码
     * @return
     * @throws Exception
     */
    private String generatorSign(Map<String,String> data,int type) throws Exception{
        logger.info("[FIRE]火火支付生成签名开始===============START=============");
        try {
            //把使用到的所有参数，连Token一起，按 参数名 字母升序排序。把 参数值 拼接在一起。做md5-32位加密，取字符串小写。得到key。网址类型的参数值不要urlencode。
            //format  返回格式    string(32)  固定值：json。选填（场景二时传入），回调时将会返回json数据值
            //key的拼接顺序：如用到了所有参数，就按这个顺序拼接：goodsname + notify_url + orderid + orderuid + price + return_url + token + uid
            //注意：Token在安全上非常重要，一定不要显示在任何网页代码、网址参数中。只可以放在服务端。计算key时，先在服务端计算好，把计算出来的key传出来。严禁在客户端计算key，严禁在客户端存储Token。
            StringBuffer sb = new StringBuffer();
            if(type == 1){
                sb.append(data.get("goodsname")).append(data.get("notify_url"));
                sb.append(data.get("orderid")).append(data.get("orderuid"));
                sb.append(data.get("price")).append(data.get("return_url"));
                sb.append(secret).append(data.get("uid"));
            }else{
                //就按这个顺序拼接：orderid + orderuid + platform_trade_no + price + realprice + token
                sb.append(data.get("orderid")).append(data.get("orderuid"));
                sb.append(data.get("platform_trade_no")).append(data.get("price"));
                sb.append(data.get("realprice")).append(secret);
            }
            String signStr = sb.toString();
            logger.info("[FIRE]火火支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[FIRE]火火支付生成签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[FIRE]火火支付生成签名串异常:{}",e.getMessage());
            throw new Exception("[FIRE]火火支付生成签名串异常");
        }
    }
    
}
