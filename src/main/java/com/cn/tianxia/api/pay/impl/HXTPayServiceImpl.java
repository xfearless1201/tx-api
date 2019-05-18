package com.cn.tianxia.api.pay.impl;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
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
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/1/17 16:47
 * @Description: 华夏通支付
 */
public class HXTPayServiceImpl extends PayAbstractBaseService implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(HXTPayServiceImpl.class);

    private static final String ret__failed = "fail";

    private static final String ret__success = "success";

    private String uid;
    private String token;
    private String payUrl;
    private String notifyUrl;

    public HXTPayServiceImpl() {}

    public HXTPayServiceImpl(Map<String,String> map) {
        if(map != null && !map.isEmpty()){
            if(map.containsKey("uid")){
                this.uid = map.get("uid");
            }
            if(map.containsKey("notifyUrl")){
                this.notifyUrl = map.get("notifyUrl");
            }
            if(map.containsKey("token")){
                this.token = map.get("token");
            }
            if(map.containsKey("payUrl")){
                this.payUrl = map.get("payUrl");
            }
        }

    }


    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            Map<String,String> param = sealRequest(payEntity);

            String sign = generatorSign(param);

            param.put("sign",sign);
            logger.info("[HXT]华夏通支付扫码请求参数:{}",JSONObject.fromObject(param).toString());
            String form = HttpUtils.generatorForm(param, payUrl);

            if (StringUtils.isBlank(form)) {
                logger.error("[HXT]华夏通支付下单失败：生成请求form为空");
                PayResponse.error("[HXT]华夏通支付下单失败：生成请求form为空");
            }

            return PayResponse.sm_form(payEntity,form,"下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[HXT]华夏通支付扫码支付下单失败"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    private boolean verifyCallback(Map<String,String> data) {

//        sign加密时要按照下面示例：
//        （拼接顺序：user_order_no + orderno + tradeno + price + realprice + token）

        String sign = data.remove("sign");

        StringBuffer sb = new StringBuffer();
        sb.append(data.get("user_order_no"))
                .append(data.get("orderno"))
                .append(data.get("tradeno"))
                .append(data.get("price"))
                .append(data.get("realprice"))
                .append(token);
        String localSign;
        try {
            localSign = MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("[HXT]华夏通支付生成支付签名串异常:"+ e.getMessage());
            return false;
        }
        return sign.equalsIgnoreCase(localSign);
    }

    /**
     *
     * @Description 封装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[HXT]华夏通支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());

            data.put("uid",uid);//商户ID
            data.put("price",amount);//支付金额 元为单位
            data.put("paytype",entity.getPayCode());//6：cnt支付宝；7：cnt微信支付
            data.put("notify_url",notifyUrl);
            data.put("return_url",entity.getRefererUrl());
            data.put("user_order_no",entity.getOrderNo());
            data.put("note","TOP_UP");//异步通知地址
            data.put("cuid",entity.getuId());//同步通知地址
            data.put("tm",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HXT]华夏通支付封装请求参数异常:"+e.getMessage());
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
        logger.info("[HXT]华夏通支付生成支付签名串开始==================START========================");
        try {
            //签名规则:
//            做md5-32位加密，取字符串小写。网址类型的参数值不要urlencode（例：uid + price + paytype + notify_url + return_url + user_order_no + token）

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(data.get("uid"))
                    .append(data.get("price"))
                    .append(data.get("paytype"))
                    .append(data.get("notify_url"))
                    .append(data.get("return_url"))
                    .append(data.get("user_order_no"))
                    .append(token);
            logger.info("[HXT]华夏通支付生成待签名串:"+strBuilder.toString());
            String md5Value = MD5Utils.md5toUpCase_32Bit(strBuilder.toString());
            if (StringUtils.isBlank(md5Value)) {
                logger.error("[HXT]华夏通支付生成签名异常：生成签名为空");
                throw new Exception("生成支付签名串异常!");
            }
            logger.info("[HXT]华夏通支付生成加密签名串:"+md5Value.toLowerCase());
            return md5Value.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HXT]华夏通支付生成支付签名串异常:"+e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }

    /**
     * 回调方法
     * @param request 第三方请求request
     * @param response  response
     * @param config  平台对应支付商配置信息
     * @return
     */
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {

        Map<String,String> infoMap = ParamsUtils.getNotifyParams(request);  //获取回调请求参数

        if (MapUtils.isEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.token = config.getString("token");    //从配置中获取
        boolean verifyRequest = verifyCallback(infoMap);

        String order_no = infoMap.get("user_order_no");// 平台订单号
        String trade_no = infoMap.get("orderno");// 第三方订单号
        String trade_status = "success";//订单状态
        String t_trade_status = "success";// 表示成功状态
        String order_amount = infoMap.get("realprice");
        if(StringUtils.isBlank(order_amount)){
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request))?"127.0.0.1":IPTools.getIp(request);
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setRet__success(ret__success);    //成功返回
        processNotifyVO.setRet__failed(ret__failed);      //失败返回
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);    //支付状态
        processNotifyVO.setT_trade_status(t_trade_status);     //第三方成功状态
        processNotifyVO.setRealAmount(realAmount);
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());    //回调参数
        processNotifyVO.setPayment("HXT");

        return super.processSuccessNotify(processNotifyVO,verifyRequest);

    }
}
