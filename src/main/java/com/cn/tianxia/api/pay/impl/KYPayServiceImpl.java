package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import net.sf.json.JSONObject;

/**
 * @author Vicky
 * @version 1.2.0
 * @ClassName KYPayServiceImpl
 * @Description  快银支付  渠道 网银
 * @Date 2019/4/25 14 57
 **/
public class KYPayServiceImpl extends PayAbstractBaseService implements PayService {

    private static final Logger logger = LoggerFactory.getLogger(KYPayServiceImpl.class);

    private String merchId;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String secret;//请求密钥
    private String version;//版本
    private String queryOrderUrl;

    private static String ret__success = "10000";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private boolean verifySuccess = true;//回调验签默认状态为true

    public KYPayServiceImpl() {
    }

    public KYPayServiceImpl(Map<String, String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(MapUtils.isNotEmpty(data)){
                if(data.containsKey("merchId")){
                    this.merchId = data.get("merchId");
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
                if(data.containsKey("version")){
                    this.version = data.get("version");
                }
                if(data.containsKey("queryOrderUrl")){
                    this.queryOrderUrl = data.get("queryOrderUrl");
                }
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
        this.merchId = config.getString("merchId");
        this.notifyUrl = config.getString("notifyUrl");
        this.secret = config.getString("secret");
        this.version = config.getString("version");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[KY]快银支付扫码支付回调请求参数：" + JSONObject.fromObject(dataMap));

        if (!MapUtils.isNotEmpty(dataMap)) {
            logger.error("KYFNotify获取回调请求参数为空");
            return ret__failed;
        }

        String trade_no = dataMap.get("sysorderid");//第三方订单号，流水号
        String order_no = dataMap.get("orderid");//支付订单号
        String amount = dataMap.get("amount");//实际支付金额,以分为单位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[KY]快银支付扫码支付回调请求参数,获取的{} 流水单号为空",trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[KY]快银支付扫码支付回调请求参数,订单金额为空");
            return ret__failed;
        }

        String trade_status = dataMap.get("code");  //第三方支付状态，支付商无此字段
        String t_trade_status = "10000";//第三方成功状态

        //订单查询
        try{
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("merchant", merchId);//商户id
            queryMap.put("version", version);//商户订单号
            queryMap.put("orderid", order_no);//商户订单号
            queryMap.put("signtype", "1");//商户id
            queryMap.put("sign", generatorSign(queryMap));//商户订单号

            logger.info("[KY]快银支付扫码支付回调查询订单{}请求参数：{}", order_no, JSONObject.fromObject(queryMap));

            String responseData = HttpUtils.toPostForm(queryMap, queryOrderUrl);//响应信息

            if(StringUtils.isBlank(responseData)){
                logger.info("[KY]快银支付扫码支付回调查询订单发起HTTP请求无响应");
                return ret__failed;
            }
            logger.info("[KY]快银支付扫码支付回调查询订单{}响应信息：{}", order_no, JSONObject.fromObject(responseData));

            //解析
            JSONObject json = JSONObject.fromObject(responseData);
            if(!"10000".equals(json.getString("code"))){
                logger.info("[KY]快银支付扫码支付回调查询订单,请求状态为:{}",json.getString("code"));
                return ret__failed;
            }

        }catch (Exception e){
            e.getStackTrace();
            logger.info("[KY]快银支付扫码支付回调查询订单{}异常{}：",order_no,e.getMessage());
            return ret__failed;
        }

        //写入数据库
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
        processNotifyVO.setPayment("KY");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[KY]快银支付扫码支付回调验签失败");
            return ret__failed;
        }
        return processSuccessNotify(processNotifyVO, verifySuccess);
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
        try {
            Map<String, String> dataMap = sealRequest(payEntity, 2);
            dataMap.put("sign",generatorSign(dataMap));//商户签名数据,32,32位小写MD5签名值
            dataMap.put("ip", payEntity.getIp());//付款人IP,15,付款人IP地址，商户传递获取到的客户端IP
            dataMap.put("productname", "TOP-UP");//商品名称,128,商户的商品名称，英文或中文字符串
            dataMap.put("productdesc", "recharge");//商品描述,300,英文或中文字符串
           // dataMap.put("returntype","json");//返回数据类型,300,使用快银收银台请不传该参数或将该参数值设置为空

            logger.info("[KY]快银支付扫码支付请求参数：{}", JSONObject.fromObject(dataMap));

            String responseData = HttpUtils.generatorForm(dataMap, payUrl);//响应信息

            if(StringUtils.isBlank(responseData)){
                return PayResponse.error("[KY]快银支付扫码支付发起HTTP请求无响应");
            }
           // logger.info("[KY]快银支付扫码支付发起HTTP请求返回信息：" + JSONObject.fromObject(responseData));
            logger.info("[KY]快银支付扫码支付发起HTTP请求返回信息：" + responseData);
            return PayResponse.sm_form(payEntity,responseData,"下单成功");
        }catch (Exception e){
            e.getStackTrace();
            return PayResponse.error("[KY]快银支付扫码支付异常:" + e.getMessage());
        }
    }

    /**
     * 回调验签
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {

        String sign = generatorSign(data);
        String sourceSign = data.remove("sign");
        logger.info("[KY]快银支付扫码支付生成签名串：{}--源签名串：{}", sign, sourceSign);

        if(sign.equals(sourceSign)){
            return "success";
        }
        return "fail";
    }

    /**
     * 组装参数
     * @param payEntity
     * @return
     */
    private Map<String, String> sealRequest(PayEntity payEntity, int type){
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());
        String time = new SimpleDateFormat("YYYYMMddHHmmSS").format(new Date());

        dataMap.put("merchant", merchId);//商户编号	,20,商户唯一标识
        dataMap.put("version", version);//网关版本,6,固定值：1.0

        if(1 == type){//网银
            dataMap.put("paytype", "1004");//接口名称,20,请参考文档底部编码表
            dataMap.put("code", payEntity.getPayCode());//支付方式,20,请参考文档底部编码表,微信,支付宝,QQ钱包等可不传值
        }else {
            dataMap.put("paytype", payEntity.getPayCode());//接口名称,20,请参考文档底部编码表
        }
        dataMap.put("orderid", payEntity.getOrderNo());//商户订单号,30,商户系统产生的唯一订单号
        dataMap.put("amount", amount);//商户订单金额,10,保留小数点后2位，单位元
        dataMap.put("ordertime", time);//订单时间,14,格式：YYYYMMDDHHMMSS,例如：20180213151801
        dataMap.put("attach", "pay");//扩展字段,128,英文或中文字符串支付完成后，按照原样返回给商户
        dataMap.put("returnurl", payEntity.getRefererUrl());//同步通知地址,128,服务器同步通知商户接口路径，以http://开头且没有任何参数
        dataMap.put("notifyurl", notifyUrl);//异步通知地址,128,服务器异步通知商户接口路径，以http://开头且没有任何参数(交易结果以异步通知为准)
        dataMap.put("signtype", "1");//签名类型,1,1:MD5,2:RSA(目前仅提供MD5)


        return dataMap;
    }

    /**
     * 生成签名
     * @param data
     * @return
     */
    private String generatorSign(Map<String, String> data){
        //签名数据示例 (按照从 a 到 z 的顺序排序，若遇到相同首字母，则看第二个字母，以此类推，KEY 为商户密钥)
        //
        //MD5(amount={0}code={1}donetime={2}merchant={3}oamount={4}orderid={5}signtype={6}sysorderid={7}version={8}KEY)

        try{
            Map<String, String> treeMap = new TreeMap<>(data);
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = treeMap.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String val = treeMap.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key) || "attach".equalsIgnoreCase(key)
                         || "msg".equalsIgnoreCase(key)){
                    continue;
                }
                sb.append(key).append("=").append(val);
            }
            sb.append(secret);

            String strString = sb.toString();
            logger.info("[KY]快银支付扫码支付生成待签名串：{}",strString);
            String sign = MD5Utils.md5toUpCase_32Bit(strString).toLowerCase();

            logger.info("[KY]快银支付扫码支付生成签名串：{}",sign);
            return sign;
        }catch (Exception e){
            e.getStackTrace();
            logger.info("[KY]快银支付扫码支付生成签名串：{}",e.getMessage());
            return "扫码支付异常";
        }
    }
}
