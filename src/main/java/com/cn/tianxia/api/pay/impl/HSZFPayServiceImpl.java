package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
 * @Auther: Bing
 * @Date: 2019/1/22 10:25
 * @Description: 灰少支付
 */
public class HSZFPayServiceImpl extends PayAbstractBaseService implements PayService {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(HSZFPayServiceImpl.class);
    /**回调失败响应信息*/
    private static final String ret__failed = "fail";
    /**回调成功响应信息*/
    private static final String ret__success = "OK";
    /**支付地址*/
    private String payUrl;
    /**商户编号*/
    private String payMemberid;
    /**商户密钥*/
    private String secretKey;
    /**回调地址*/
    private String payNotifyUrl;
    
    public HSZFPayServiceImpl() {}

    public HSZFPayServiceImpl(Map<String, String> data) {
        if (data != null) {
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("payMemberid")) {
                this.payMemberid = data.get("payMemberid");
            }
            if (data.containsKey("secretKey")) {
                this.secretKey = data.get("secretKey");
            }
            if (data.containsKey("payNotifyUrl")) {
                this.payNotifyUrl = data.get("payNotifyUrl");
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
        logger.info("[HSZF]灰少支付扫码支付开始====================START========================");
        logger.info("支付地址："+payUrl);
        try {
            // 获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);
            logger.info("[HSZF]灰少扫码支付请求参数:" + JSONObject.fromObject(data));
            // 发起支付请求
            String resStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[HSZF]灰少扫码支付响应信息:" + resStr);
            if(StringUtils.isBlank(resStr)){
                logger.info("[HSZF]灰少扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[HSZF]灰少扫码支付发起HTTP请求无响应结果");
            }
            return PayResponse.sm_form(payEntity, resStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HSZF]灰少支付扫码支付异常:" + e.getMessage());
            return PayResponse.error("[HSZF]灰少支付扫码支付异常");
        }
    }

    /**
     * @param map
     * @return
     * @Description 回调验签
     */
    @Override
    public String callback(Map<String, String> map) {
        return null;
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[HSZF]灰少扫码支付封装支付请求参数开始===========================START=================");
        try {
            // 创建存储参数对象
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("0").format(entity.getAmount());// 订单金额
            data.put("gmid", payMemberid);// 平台分配商户号
            data.put("gmorder", entity.getOrderNo());// 订单号
            data.put("price", amount);// 订单金额 单位：元
            data.put("uid", entity.getuId());// 提交时间,时间格式：2016-12-26 18:18:18
            data.put("notifyurl", payNotifyUrl);// 服务端通知
            data.put("type", entity.getPayCode());// 支付方式
            data.put("ware", "recharge");// 页面跳转通知
            data.put("sign", generatorSign(data));// 签名
            logger.info("[HSZF]灰少支付封装签名参数:" + JSONObject.fromObject(data).toString());
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HSZF]灰少支付封装请求参数异常:" + e.getMessage());
            throw new Exception("[HSZF]灰少支付封装支付请求参数异常!");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名
     */
    private String generatorSign(Map<String, String> data) throws Exception {
        try {
            // 排序
            Map<String, String> treeMap = new TreeMap<>(data);
            StringBuffer sb = new StringBuffer();
            for (String key : treeMap.keySet()) {
                String val = treeMap.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                sb.append(key).append("=").append(val).append("&");
            }
            // 加上签名秘钥
            sb.append("key=").append(secretKey);
            String signStr = sb.toString();
            logger.info("[HSZF]灰少支付生成待加密签名串：" + signStr);
            String sign = MD5Utils.md5(signStr.getBytes());
            logger.info("[HSZF]灰少支付生成MD5加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HSZF]灰少扫码支付生成签名异常:" + e.getMessage());
            throw new Exception("生成签名串异常!");
        }
    }
    private boolean verifyCallback(Map<String,String> data) {
        try {
            String sourceSign = data.remove("sign");
            data.remove("ext");
            String sign = generatorSign(data);
            logger.info("[HSZF]灰少扫码支付回调生成签名串"+sign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HSZF]灰少扫码支付回调生成签名串异常"+e.getMessage());
            return false;
        }
    }
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        Map<String,String> infoMap = ParamsUtils.getNotifyParams(request);  //获取回调请求参数
        logger.info("[HSZF]灰少扫码支付回调请求参数："+JSONObject.fromObject(infoMap));
        if (MapUtils.isEmpty(infoMap)) {
            logger.error("HSZFNotify获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.secretKey = config.getString("secretKey");//从配置中获取
        boolean verifyRequest = verifyCallback(infoMap);
        String order_amount = infoMap.get("totalfee");//单位：元
        if(StringUtils.isBlank(order_amount)){
            logger.info("HSZFNotify获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount);
        String order_no = infoMap.get("gmorder");// 平台订单号
        String trade_no = infoMap.get("ptorder");// 第三方订单号
        String trade_status = "00";//订单状态:00为成功
        String t_trade_status = "00";// 表示成功状态
        
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
        processNotifyVO.setPayment("HSZF");
        processNotifyVO.setConfig(config);
        return super.processSuccessNotify(processNotifyVO,verifyRequest);
    }
}
