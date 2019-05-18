package com.cn.tianxia.api.pay.impl;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.pay.AESPayUtil;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 *  @ClassName HLPayServiceImpl
 *  @Description  TXK 对接一下秒卡通支付快捷支付
 *  @Author Vicky
 *  @Date 2019年02月25日 17:50
 *  @Version 1.0.0
 **/
public class MKTZFPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(MKTZFPayServiceImpl.class);

    private static String ret__success = "success";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串

    private boolean verifySuccess = true;

    public String payUrl;//支付地址
    public String md5Key;//密钥
    public String notifyUrl;//回调地址
    public String sid;//商户id
    public String searchOrderUrl;//订单查询接口：http://pay.mktpay.vip/gateway/query.html
    // public String pay_type;//支付方式，目前商户接收“1”

    public MKTZFPayServiceImpl() {
    }
    public MKTZFPayServiceImpl(Map<String,String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("sid")) {
                this.sid = data.get("sid");
            }
            if (data.containsKey("md5Key")) {
                this.md5Key = data.get("md5Key");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
        }
    }

    /**
     * 回调
     * @param request
     * @param response
     * @param config
     * @return
     */
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        this.md5Key = config.getString("md5Key");
        this.sid = config.getString("sid");
        this.searchOrderUrl = config.getString("searchOrderUrl");

        logger.info("[MKTZF]秒卡通支付 回调开始=======================Start==============================");
        Map<String,String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[MKTZF]秒卡通支付 回调请求参数："+JSONObject.fromObject(dataMap));
        String order_no = dataMap.get("order_no");  //支付订单号
        String amount = dataMap.get("order_amount");//商户订单总金额，订单总金额以元为单位，精确到小数点后两位
        String ip = StringUtils.isBlank(IPTools.getIp(request))?"127.0.0.1":IPTools.getIp(request);  //回调ip
        String trade_no = dataMap.get("trade_no");   //第三方订单号，流水号
        String trade_status = dataMap.get("trade_status");  //第三方支付状态
        String t_trade_status = "success";   //第三方成功状态
        if(false == searchOrder(dataMap)){
            logger.info("[MKTZF]秒卡通支付 回调前 订单查询失败");
            return "fail";
        }

        //回调验签
        if (!"success".equals(callback(dataMap))) {
            verifySuccess=false;
            logger.info("[MKTZF]秒卡通支付 回调验签失败");
            return "fail";
        }

        //判断订单金额是否为空
        if (StringUtils.isBlank(amount)){
            logger.info("[MKTZF]秒卡通支付 回调订单金额为空");
            return "fail";
        }
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setRealAmount(Double.parseDouble(amount));
        processNotifyVO.setIp(ip);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setInfoMap(JSONObject.fromObject(dataMap).toString());
        processNotifyVO.setT_trade_status(t_trade_status);
        processNotifyVO.setConfig(config);
        processNotifyVO.setPayment("MKTZF");

        return processSuccessNotify(processNotifyVO,verifySuccess);
    }

    /**
     * 回调前，进入客户提供的查询地址去查订单状态:为确保支付返回的数据为第三方提供
     * @return
     */
    public boolean searchOrder(Map<String, String> dataMap){
        logger.info("[MKTZF]秒卡通支付 回调前订单查询=======================Start==============================");
        try {
            Map<String, String> map = new HashMap<>();
            map.put("input_charset", "UTF-8");
            map.put("merchant_code", sid);
            map.put("order_no", dataMap.get("order_no"));
            map.put("trade_no", dataMap.get("trade_no"));
            String sign = generatorSign(map);
            map.put("sign", sign);
            logger.info("[MKTZF]秒卡通支付查询接口请求参数："+JSONObject.fromObject(map));
            String data = HttpUtils.toPostForm(map, searchOrderUrl);
            logger.info("[MKTZF]秒卡通支付查询接口响应参数："+data);
            if(StringUtils.isBlank(data)){
                logger.info("[MKTZF]秒卡通支付  订单查询请求响应为空："+data);
                return false;
            }
            JSONObject jb = JSONObject.fromObject(data);
            if(jb.containsKey("is_success") && "true".equalsIgnoreCase(jb.getString("is_success"))){
                if("success".equalsIgnoreCase(jb.getString("trade_status"))){
                    logger.info("[MKTZF]秒卡通支付 回调前订单查询  成功，订单状态为成功");
                    return true;
                }
            }
            logger.info("[MKTZF]秒卡通支付 回调前订单查询   失败:目前订单状态为支付中或者支付失败");
            return false;
        }catch (Exception e){
            e.printStackTrace();
            logger.info("[MKTZF]秒卡通支付 回调前订单查询   异常"+ e.getMessage());
            return false;
        }
    }


    /**
     * 网银支付
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    /**
     * 扫码支付
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[MKTZF]秒卡通支付 扫码开始=======================Start==============================");
        //参数引入
        Map<String,String> dataMap = sealRequest(payEntity);
        //生成签名
        String sign = generatorSign(dataMap);
        logger.info("[MKTZF]秒卡通支付 支付地址："+payUrl);
        dataMap.put("sign",sign);
        try {
            //支付请求响应
            String response = HttpUtils.generatorForm(dataMap,payUrl);
            logger.info("[MKTZF]秒卡通支付  请求响应："+response);
            if (StringUtils.isBlank(response)) {
                return PayResponse.error("下单失败");
            }
            return PayResponse.sm_form(payEntity,response,"下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[MKTZF]秒卡通支付  下单失败");
            return PayResponse.error("下单失败");
        }
    }

    /**
     * 支付回调
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {
        logger.info("[MKTZF]秒卡通支付 回调验签==========================Start=================================");
        data.remove("return_params");
        String sign = generatorSign(data);
        logger.info("[MKTZF]秒卡通支付 回调验签:请求签名"+sign);
        String sourceSign = data.remove("sign");
        logger.info("[MKTZF]秒卡通支付 回调验签:服务器签名"+sourceSign);
        if(sign.equals(sourceSign)){
            logger.info("[MKTZF]秒卡通支付 回调验签成功");
            return "success";
        }
        return "fail";
    }

    /**
     * 参数封装
     * @param entity
     * @return
     */
    private Map<String,String> sealRequest(PayEntity entity){
        logger.info("[MKTZF]秒卡通支付 参数组装==========================Start=================================");
        Map<String,String> dataMap =  new HashMap<>();
        String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额，单位为元
        //金额进行AES加密
        String amountSign =AESPayUtil.encrypt(amount,md5Key);
        logger.info("[MKTZF]秒卡通支付 金额加密，前："+amount+",    后："+amountSign);
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String toDate = dateFormat.format(new Date());
        String input_charset="UTF-8";
        dataMap.put("input_charset", input_charset);//参数字符集编码,商户系统与支付平台间交互信息时使用的字符集编码，商户需对每个参数进行编码，保证中文不会出现乱码，可使用 URLEncoder.encode方法对每个参数进行编码，编码的字符集需填写到这个字段，因此请求支付接口时，必须指定字符集编码，目前支持UTF-8
        dataMap.put("order_no",entity.getOrderNo());//商户订单号,由商户系统生成的唯一订单编号，最大长度为32位
        dataMap.put("merchant_code",sid);//商户号,商户注册签约后，支付平台分配的唯一标识号
        dataMap.put("inform_url",notifyUrl);//服务器异步通知地址,支付成功后，支付平台会主动通知商家系统，因此商家必须指定接收通知的地址
        dataMap.put("pay_type",entity.getPayCode());//支付方式,1：网银支付，只接收1
        dataMap.put("order_amount",amountSign);//商户订单总金额,订单总金额以元为单位，精确到小数点后两位，金额要使用AES加密，key和签名的key一样。
        dataMap.put("order_time",toDate);//商户订单时间,字符串格式要求为： yyyy-MM-dd HH:mm:ss 例如：2016-07-30 18:18:18
        logger.info("[MKTZF]秒卡通支付 参数："+dataMap);
        return dataMap;
    }

    /**
     * 签名格式
     * @param dataMap
     * @return
     */
    private String generatorSign(Map<String,String> dataMap){
        logger.info("[MKTZF]秒卡通支付 签名开始==========================Start=================================");
        StringBuffer sb = new StringBuffer();
        try {
            //签名前参数排序
            Map<String,String> map = MapUtils.sortByKeys(dataMap);
            Iterator<String> iterator = map.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = map.get(key);
                if(StringUtils.isBlank(val) || key.equalsIgnoreCase("sign")) continue;
                sb.append(key).append("=").append(val).append("&");
            }
            logger.info("[MKTZF]秒卡通支付 生成的签名前 参数从小到大排序"+sb);
            sb.append("key=").append(md5Key);
            logger.info("[MKTZF]秒卡通支付 生成的签名前 参数最后接入商户密钥 key:"+md5Key+",参数："+sb);
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
            logger.info("[MKTZF]秒卡通支付 生成的签名："+sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[MKTZF]秒卡通支付 生成签名失败");
            return "fail";
        }
    }

}
