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
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.utils.pay.SHANUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 * @author Vicky
 * @version 1.2.0
 * @ClassName HYFPayServiceImpl
 * @Description 汇易付支付  渠道 网银
 * @Date 2019/4/25 14 57
 **/
public class HYFPayServiceImpl extends PayAbstractBaseService implements PayService {

    private static final Logger logger = LoggerFactory.getLogger(HYFPayServiceImpl.class);

    private String merchId;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String secret;//请求密钥
    private String md5key;//响应密钥
    private String queryOrderUrl;

    private static String ret__success = "success";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private boolean verifySuccess = true;//回调验签默认状态为true

    public HYFPayServiceImpl() {
    }

    public HYFPayServiceImpl(Map<String, String> data) {
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
                if(data.containsKey("md5key")){
                    this.md5key = data.get("md5key");
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
        this.md5key = config.getString("md5key");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[HYF]汇易付支付网银支付回调请求参数：" + JSONObject.fromObject(dataMap));

        if (!MapUtils.isNotEmpty(dataMap)) {
            logger.error("HYFNotify获取回调请求参数为空");
            return ret__failed;
        }

        String trade_no = dataMap.get("trade_no");//第三方订单号，流水号
        String order_no = dataMap.get("out_trade_no");//支付订单号
        String amount = dataMap.get("amount");//实际支付金额,以分为单位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[HYF]汇易付支付网银支付回调请求参数,获取的{} 流水单号为空",trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[HYF]汇易付支付网银支付回调请求参数,订单金额为空");
            return ret__failed;
        }

        String trade_status = "00";  //第三方支付状态，支付商无此字段
        String t_trade_status = "00";//第三方成功状态

        //订单查询
        try{
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("out_trade_no", order_no);//商户订单号
            queryMap.put("merchant_id", merchId);//商户id
            queryMap.put("biz_code", "10101");//业务代码
            queryMap.put("query_time", time);//查询时间
            queryMap.put("sign", generatorSign(queryMap, 1));//签名

            logger.info("[HYF]汇易付支付网银支付回调查询订单{}请求参数：{}", order_no, JSONObject.fromObject(queryMap));
            String ponse = SHANUtils.sendHttpReq(queryOrderUrl, JSONObject.fromObject(queryMap).toString(), "UTF-8");

            if(StringUtils.isBlank(ponse)){
                logger.info("[HYF]汇易付支付网银回调查询订单发起HTTP请求无响应");
                return ret__failed;
            }
            logger.info("[HYF]汇易付支付网银支付回调查询订单{}响应信息：{}", order_no, JSONObject.fromObject(ponse));

            JSONObject json = JSONObject.fromObject(ponse);
            if(json.containsKey("code") && "20000".equals(json.getString("code"))){
                JSONObject data = json.getJSONObject("data");
                //2：交易成功; 3：交易完成（商户端回调返回成功后）
                if(!"2".equals(data.getString("status"))){
                   // if(!"3".equals(data.getString("status"))){ return ret__failed;}
                        logger.info("[HYF]汇易付支付网银支付回调查询订单,订单支付状态为:{}", data.getString("status"));
                        return ret__failed;
                }
            }else {
                logger.info("[HYF]汇易付支付网银支付回调查询订单,请求状态为:{}",json.getString("code"));
                return ret__failed;
            }

        }catch (Exception e){
            e.getStackTrace();
            logger.info("[HYF]汇易付支付网银支付回调查询订单{}异常{}：",order_no,e.getMessage());
            return ret__failed;
        }

        //写入数据库
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setRealAmount(Double.parseDouble(amount)/100);
        processNotifyVO.setIp(ip);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setInfoMap(JSONObject.fromObject(dataMap).toString());
        processNotifyVO.setT_trade_status(t_trade_status);
        processNotifyVO.setConfig(config);
        processNotifyVO.setPayment("HYF");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[HYF]汇易付支付网银回调验签失败");
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
         logger.info("[HYF]汇易付支付网银支付开始===============START========================");
        try{
            Map<String, String> dataMap = sealRequest(payEntity, 1);
            dataMap.put("bank_code", payEntity.getPayCode());//银行代码

            String responseData =  SHANUtils.sendHttpReq(payUrl, JSONObject.fromObject(dataMap).toString(), "UTF-8");
            logger.info("[HYF]汇易付支付网银支付响应信息：{}", JSONObject.fromObject(responseData));
            if(StringUtils.isBlank(responseData)){
                logger.info("[HYF]汇易付支付网银发起HTTP请求无响应");
                return PayResponse.error("[HYF]汇易付支付网银支付发起HTTP请求无响应");
            }

            JSONObject json = JSONObject.fromObject(responseData);
            if(json.containsKey("code") && "20000".equals(json.getString("code"))){
                JSONObject data = json.getJSONObject("data");
                return PayResponse.wy_write(data.getString("pay_html"));
            }

            return PayResponse.error("[HYF]汇易付支付网银支付下单失败："+ JSONObject.fromObject(responseData));
        }catch (Exception e){
            e.getStackTrace();
            logger.info("[HYF]汇易付支付网银支付异常:{}", e.getMessage());
            return PayResponse.error("[HYF]汇易付支付网银支付异常");
        }
    }

    /**
     * 扫码支付
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
       return null;
    }

    /**
     * 回调验签
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {

        String sign = generatorSign(data, 2);
        String sourceSign = data.remove("sign");
        logger.info("[HYF]汇易付支付网银支付生成签名串：{}--源签名串：{}", sign, sourceSign);

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
        String amount = new DecimalFormat("##").format(payEntity.getAmount()*100);

        dataMap.put("out_trade_no", payEntity.getOrderNo());//商户订单号
        dataMap.put("amount", amount);//商户订单金额
        dataMap.put("subject", "TOP-UP");//订单标题
        dataMap.put("merchant_id", merchId);//商户id
        if(1 == type){
            dataMap.put("biz_code", "10001");//业务代码
        }else {
            dataMap.put("biz_code", payEntity.getPayCode());//业务代码,10001：网关支付
        }
        dataMap.put("sign", generatorSign(dataMap, 1));//签名
        dataMap.put("notify_url", notifyUrl);//异步回调地址
        dataMap.put("body", "recharge");//订单详细信息
        dataMap.put("extra", "recharge");//订单附加信息


        logger.info("[HYF]汇易付支付网银支付请求参数：{}", JSONObject.fromObject(dataMap));
        return dataMap;
    }

    /**
     * 生成签名
     * @param data
     * @return
     */
    private String generatorSign(Map<String, String> data, int type){
        //amount=500&biz_code=10001&merchant_id=MC56845458679564289&out_trade_no=67106096586721280&subject=网关测试+key

        try{
            Map<String, String> treeMap = new TreeMap<>(data);
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = treeMap.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String val = treeMap.get(key);
                if(StringUtils.isBlank(val) || "sign".equals(key)){
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }

          if(1 == type){//请求
              sb.replace(sb.length()-1, sb.length(), secret);
          }else {//响应
              sb.replace(sb.length()-1, sb.length(), md5key);
          }

            String strString = sb.toString();
            logger.info("[HYF]汇易付支付网银支付生成待签名串：{}",strString);
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();

            logger.info("[HYF]汇易付支付网银支付生成签名串：{}",sign);
            return sign;
        }catch (Exception e){
            e.getStackTrace();
            logger.info("[HYF]汇易付支付网银支付生成签名串：{}",e.getMessage());
            return "扫码支付异常";
        }
    }
}
