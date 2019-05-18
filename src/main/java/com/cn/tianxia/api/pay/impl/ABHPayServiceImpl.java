package com.cn.tianxia.api.pay.impl;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
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
import com.cn.tianxia.api.utils.pay.DESUtils;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName ABHPayServiceImpl
 * @Description 阿里宝盒支付
 * @author Hardy
 * @Date 2018年10月14日 下午1:43:24
 * @version 1.0.0
 */
public class ABHPayServiceImpl implements PayService{
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(ABHPayServiceImpl.class);
    
    private String branchId;//机构编号
    private String merCode;//商户号
    private String settType;//结算类型
    private String payUrl;//支付地址
    private String notifyUrl;//异步通知地址
    private String secret;//秘钥
    private String desKey;//参数加密key

    //构造器,初始化参数
    public ABHPayServiceImpl(Map<String,String> data) {
        if(data != null && !data.isEmpty()){
            if(data.containsKey("branchId")){
                this.branchId = data.get("branchId");
            }
            if(data.containsKey("merCode")){
                this.merCode = data.get("merCode");
            }
            if(data.containsKey("settType")){
                this.settType = data.get("settType");
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
            if(data.containsKey("desKey")){
                this.desKey = data.get("desKey");
            }
        }
    }

    /**
     * 网银支付
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[ABH]阿里宝盒支付网银支付开始===================START=================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity,1);
            //生成签名串
            String sign = generatorSign(data);
            data.put("signature",sign);//验签字段 是   MD5加密
            //请求报文规则:数据传输加密,所有的请求均将接口请求数据均使用3DES将请求数据进行加密进行POST请求。 
            //例如：请求数据为:param1=001，param2=002，param3=003
            //先将请求数据转为json字符串 Jsonstr={“param1”:”001”,”param2”:”002”,”param3”:”003”}
            //再将Jsonstr利用密钥钥进行3DES加密 
            String reqParams = formatMapToString(data);
            logger.info("[ABH]阿里宝盒支付请求报文:"+reqParams);
            //进行DES加密
            reqParams = URLEncoder.encode(DESUtils.encrypt(reqParams, desKey),"UTF-8");
            logger.info("[ABH]阿里宝盒支付请求密文:"+reqParams);
            //发起HTTP-POST请求
            String response = HttpUtils.toPostJson(URLEncoder.encode(reqParams,"UTF-8"), payUrl+"/h5WkPay");
            if(StringUtils.isBlank(response)){
                logger.error("[ABH]阿里宝盒支付失败,请求无响应结果!");
                return PayUtil.returnWYPayJson("error", "","", "", "");
            }
            
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.containsKey("respCode") && jsonObject.getString("respCode").equalsIgnoreCase("F5")){
                //respCode 成功FS   处理中F5 失败FF
                //下单成功
                String formStr = jsonObject.getString("formStr");//.replaceAll("\\\\", "");//
                return PayUtil.returnWYPayJson("success", "form", formStr, payEntity.getPayUrl(), "pay");
            }
            //下单失败
            String respMsg = jsonObject.getString("respMsg");
            return PayUtil.returnWYPayJson("error", "form", "下单失败:"+respMsg, payEntity.getPayUrl(), "pay");
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[ABH]阿里宝盒支付网银支付异常:"+e.getMessage());
            return PayUtil.returnWYPayJson("error", "[ABH]阿里宝盒扫码支付异常!", "", "", "");
        }
    }

    /**
     * 扫码支付
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[ABH]阿里宝盒支付扫码支付开始===================START=================");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity,0);
            //生成签名串
            String sign = generatorSign(data);
            data.put("signature",sign);//验签字段 是   MD5加密
            //请求报文规则:数据传输加密,所有的请求均将接口请求数据均使用3DES将请求数据进行加密进行POST请求。 
            //例如：请求数据为:param1=001，param2=002，param3=003
            //先将请求数据转为json字符串 Jsonstr={“param1”:”001”,”param2”:”002”,”param3”:”003”}
            //再将Jsonstr利用密钥钥进行3DES加密 
            String reqParams = formatMapToString(data);
            logger.info("[ABH]阿里宝盒支付请求报文:"+reqParams);
            //进行DES加密
            reqParams = URLEncoder.encode(DESUtils.encrypt(reqParams, desKey),"UTF-8");
            logger.info("[ABH]阿里宝盒支付请求密文:"+reqParams);
            //发起HTTP-POST请求
            String response = HttpUtils.toPostJson(URLEncoder.encode(reqParams,"UTF-8"), payUrl+"/scanPay");
            if(StringUtils.isBlank(response)){
                logger.error("[ABH]阿里宝盒支付失败,请求无响应结果!");
                return PayUtil.returnPayJson("error", "2", "下单失败,发起HTTP请求无响应结果!", "", 0, "", "展示请求响应结果:"+response);
            }
            
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.containsKey("respCode") && jsonObject.getString("respCode").equalsIgnoreCase("F5")){
                //respCode 成功FS   处理中F5 失败FF
                //下单成功
                String qrCodeURL = jsonObject.getString("qrCodeURL");//
                if(StringUtils.isBlank(payEntity.getMobile())){
                    //PC端
                    return PayUtil.returnPayJson("success", "2", "下单成功!", payEntity.getUsername(), payEntity.getAmount(), payEntity.getOrderNo(), qrCodeURL);
                }
                return PayUtil.returnPayJson("success", "4", "下单成功!", payEntity.getUsername(), payEntity.getAmount(), payEntity.getOrderNo(), qrCodeURL);
            }
            //下单失败
            String respMsg = jsonObject.getString("respMsg");
            return PayUtil.returnPayJson("error", "2", "下单失败:"+respMsg, payEntity.getUsername(), payEntity.getAmount(), payEntity.getOrderNo(),response);
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[ABH]阿里宝盒支付扫码支付异常:"+e.getMessage());
            return PayUtil.returnPayJson("error", "2", "[ABH]阿里宝盒扫码支付异常!", "",0, "",e.getMessage());
        }
    }
    
    /**
     * 回调
     */
    @Override
    public String callback(Map<String, String> data) {
        logger.info("[ABH]阿里宝盒支付回调验签开始======================START=======================");
        try {
            //获取回调签名原串
            String signature = data.get("signature");
            //生成回调签名
            String sign = generatorSign(data);
            
            if(signature.equalsIgnoreCase(sign)) return "success";
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[ABH]阿里宝盒支付回调验签异常:"+e.getMessage());
        }
        return "faild";
    }
    
    
    /**
     * 
     * @Description 封装支付请求参数
     * @param entity
     * @param type 1 网银  0 扫码
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity,Integer type) throws Exception{
        logger.info("[ABH]阿里宝盒支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单时间
            Date orderTime = new Date();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount()*100);//交易1金额 是   分为单位
            if(entity.getPayCode().equals("100017") && StringUtils.isBlank(entity.getMobile())){
                //手机银联支付需要减一分钱，因为银联H5不能是正整数
                amount = new DecimalFormat("##").format(entity.getAmount()*100-1);//交易1金额 是   分为单位
            }
            data.put("branchId",branchId);//机构编号  是   1001
            data.put("merCode",merCode);//商户号    是   171107105912001
            if(type == 1){
                data.put("bizType","100011");//交易类型   是   扫码：
                data.put("bankCode", entity.getPayCode());
                data.put("commodityName", "TOP-UP");//商品名称
                data.put("phoneNo", "13902531425");//持卡人手机号,随便填的
                data.put("cerdType", "01");//证件类型 01：身份证  02：军官证 03：护照 04：回乡证 05：台胞证 06：警官证 07：士兵证 99：其它证件
                data.put("cerdId", "422322198312160013");//证件号,随便填
                data.put("acctNo", "6217731500583238");//银行卡号,随便填
//                data.put("cvn2", "1162");//信用卡时必填 乱填的
//                data.put("expDate", "1112");//乱填的
            }else{
                data.put("bizType",entity.getPayCode());//交易类型   是   扫码：
                data.put("subject","TOP-UP");//订单标题   是   扫码
            }
            data.put("settType",settType);//结算类型  是   T1：T+1结算 T0：T+0结算
            data.put("orderId",entity.getOrderNo());//订单号    是   20位长度唯一订单标识
            data.put("transDate",new SimpleDateFormat("yyyyMMdd").format(orderTime));//交易日期 是   yyyyMMdd
            data.put("transTime",new SimpleDateFormat("HHmmss").format(orderTime));//交易时间 是   hhmmss
            data.put("transAmt",amount);//交易1金额 是   分为单位
            data.put("notifyUrl",entity.getRefererUrl());//后台回调通知地址 是   后台回调通知地址
            data.put("returnUrl",notifyUrl);//页面通知地址   否   页面通知地址 h5支付必传
//            data.put("authCode","");//授权码   否   被扫时必传
//            data.put("ipAddress",entity.getIp());//IP地址 否   127.0.0.1
//            data.put("uniqueId","");//用户唯一ID    否   手机号
//            data.put("ext1","");//备用    否   备用
//            data.put("ext2","");//备用    否   备用
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[ABH]阿里宝盒支付封装请求参数异常:"+e.getMessage());
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
        logger.info("[ABH]阿里宝盒支付生成支付签名串开始==================START========================");
        try {
             //签名规则:
            //把除签名字段和集合字段以外的所有字段（不包括值为null的）内容按照报文字段字典顺序，
            //依次按照“字段名=字段值”的方式用“&”符号连接，最后加上机构工作密钥，使用MD5算法计算数字签名，填入签名字段。接受方应按响应步骤验证签名。
            //例子:签名样式如下：acctNo=123456789&customerName=张三&cvn2=111&expDate=1612
            //&merNo=135708127590000&orderNo=20160306113223302&phoneNo=13312345678&transAmt=1&SDFSG235TS45SFDSF345工作密钥
            //注意事项：没有值的参数无需传递，也无需包含到待签名数据中。签名时将字符转变成字节流时统一使用utf-8。
            Map<String,String> treemap = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            treemap.putAll(data);
            
            //生成待签名串
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = treemap.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = treemap.get(key);
                
                if(StringUtils.isBlank(val) || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("signature")) continue;
                
                sb.append(key).append("=").append(val).append("&");
            }
            
            //生成待签名串
            String signStr = sb.toString()+secret;
            logger.info("[ABH]阿里宝盒支付生成待签名串:"+signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[ABH]阿里宝盒支付生成加密签名串:"+sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[ABH]阿里宝盒支付生成支付签名串异常:"+e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }
    
    
    /**
     * 
     * @Description 格式化map参数
     * @param data
     * @return
     * @throws Exception
     */
    public String formatMapToString(Map<String,String> data) throws Exception{
        logger.info("[ABH]阿里宝盒支付请求参数类型转换开始====================START====================");
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("{");
            Iterator<String> iterator = data.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = data.get(key);
                sb.append('"').append(key).append('"').append(":");
                if(StringUtils.isNoneBlank(val)){
                    sb.append('"').append(val).append('"');
                }else{
                    sb.append('"').append('"');
                }
                if(iterator.hasNext()){
                    sb.append(",");
                }
            }
            sb.append("}");
            String reqParams = sb.toString();
            return reqParams;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[ABH]阿里宝盒支付请求参数类型转换异常:"+e.getMessage());
            throw new Exception("请求参数转换类型异常!");
        }
    }
}
