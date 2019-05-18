package com.cn.tianxia.api.pay.impl;

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
import com.cn.tianxia.api.utils.pay.TestParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import cn.hutool.core.lang.UUID;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName TestPayServiceImpl
 * @Description TODO(这里用一句话描述这个类的作用)
 * @author Bing
 * @Date 2019年5月12日 下午6:03:37
 * @version 1.0.0
 */
public class TESTPayServiceImpl extends PayAbstractBaseService implements PayService {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(TESTPayServiceImpl.class);
    /**回调失败响应信息*/
    private static final String ret__failed = "fail";
    /**回调成功响应信息*/
    private static final String ret__success = "success";
    /**支付地址*/
    private String payUrl;
    /**商户编号*/
    private String merchId;
    /**商户密钥*/
    private String secret;
    /**回调地址*/
    private String notifyUrl;
    /**订单查询地址*/
    private String queryOrderUrl;
    
    public TESTPayServiceImpl() {}

    public TESTPayServiceImpl(Map<String, String> data) {
        if (data != null) {
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("merchId")) {
                this.merchId = data.get("merchId");
            }
            if (data.containsKey("secret")) {
                this.secret = data.get("secret");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("queryOrderUrl")) {
                this.queryOrderUrl = data.get("queryOrderUrl");
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
        try {
            // 获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);
            logger.info("[TEST]测试扫码支付请求参数:" + JSONObject.fromObject(data));
            // 发起支付请求
            String resStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[TEST]测试扫码支付响应信息:" + resStr);
            if(StringUtils.isBlank(resStr)){
                logger.info("[TEST]测试扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[TEST]测试扫码支付发起HTTP请求无响应结果");
            }
            return PayResponse.sm_form(payEntity, resStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[TEST]测试扫码支付异常:" + e.getMessage());
            return PayResponse.error("[TEST]测试扫码支付异常");
        }
    }

    /**
     * @param map
     * @return
     * @Description 回调验签
     */
    @Override
    public String callback(Map<String, String> data) {
        try {
            String sourceSign = data.remove("sign");
            data.remove("ext");
            String sign = generatorSign(data);
            logger.info("[TEST]测试扫码支付回调生成签名串"+sign);
            if(sourceSign.equalsIgnoreCase(sign)) 
                return "success";
            return "fail";
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TEST]测试扫码支付回调生成签名串异常"+e.getMessage());
            return "fail";
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[TEST]测试扫码支付封装支付请求参数开始===========================START=================");
        try {
            // 创建存储参数对象
            Map<String, String> data = new HashMap<>();
            data.put("merchId", "aa123456");// 平台分配商户号
            data.put("order", String.valueOf(System.currentTimeMillis()));// 订单号
            data.put("amount", "100.00");// 订单金额 单位：元
            data.put("uid", entity.getuId());// 提交时间,时间格式：2016-12-26 18:18:18
            data.put("notifyurl", "http://txw.tx8899.com/XPJ/V2Notify.do/XPJ/TEST");// 服务端通知
            data.put("type", "alipay");// 支付方式
            data.put("product", "recharge");// 页面跳转通知
            data.put("sign", generatorSign(data));// 签名
            logger.info("[TEST]测试扫码支付封装签名参数:" + JSONObject.fromObject(data).toString());
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[TEST]测试扫码支付封装请求参数异常:" + e.getMessage());
            throw new Exception("[TEST]测试扫码支付封装支付请求参数异常!");
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
            sb.append("key=").append("af32229bb27f72fc52e58f07081b5d68");
            String signStr = sb.toString();
            logger.info("[TEST]测试扫码支付生成待加密签名串：" + signStr);
            String sign = MD5Utils.md5(signStr.getBytes());
            logger.info("[TEST]测试扫码支付生成MD5加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[TEST]测试扫码支付生成签名异常:" + e.getMessage());
            throw new Exception("[TEST]测试扫码支付生成签名串异常!");
        }
    }
    /**
     * 订单查询接口
     *
     * @param orderNo
     * @return
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    public boolean serchOrder(String orderNo, String merchId) {
        try {
            Map<String, String> param = new HashMap<>();
            param.put("merchId", merchId);//商户号
            param.put("order", orderNo);//商户订单号
            param.put("sign", generatorSign(param));
            logger.info("[TEST]测试扫码支付回调查询订单{}请求参数：{}", orderNo, JSONObject.fromObject(param));
            String resStr = "{\"status\":\"00\",\"code\":\"00\",\"data\":{\"status\":\"00\",\"code\":\"00\"}}";
            logger.info("[TEST]测试扫码支付回调查询订单{}响应信息：{}", orderNo, JSONObject.fromObject(resStr));
            if (StringUtils.isBlank(resStr)) {
                logger.info("[TEST]测试扫码支付回调查询订单发起HTTP请求无响应,订单号{}", orderNo);
                return false;
            }
            JSONObject resJson = JSONObject.fromObject(resStr);
            if (!"00".equals(resJson.getString("code"))) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YXIN]银鑫扫码支付回调查询订单{}异常{}", orderNo, e.getMessage());
            return false;
        }

    }
    private boolean verifyCallback(Map<String,String> data) {
        try {
            String sourceSign = data.remove("sign");
            data.remove("ext");
            String sign = generatorSign(data);
            logger.info("[TEST]测试扫码支付回调生成签名串"+sign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TEST]测试扫码支付回调生成签名串异常"+e.getMessage());
            return false;
        }
    }
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        Map<String,String> infoMap = TestParamsUtils.getNotifyParams(request);  //获取回调请求参数
        logger.info("[TEST]测试扫码支付回调请求参数："+JSONObject.fromObject(infoMap));
        if (MapUtils.isEmpty(infoMap)) {
            logger.error("TESTNotify获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.secret = "af32229bb27f72fc52e58f07081b5d68";//从配置中获取
        
        String order_amount = infoMap.get("amount");//单位：元
        if(StringUtils.isBlank(order_amount)){
            logger.info("TESTNotify获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount);
        String order_no = infoMap.get("orderno");// 平台订单号
        String trade_no = infoMap.get("porderno");// 第三方订单号
        String trade_status = infoMap.get("status");//订单状态:00为成功
        String t_trade_status = "success";// 表示成功状态
        String merchId = infoMap.get("merchId");// 平台订单号
        if(!serchOrder(order_no, merchId)) {
            logger.info("[TEST]测试扫码支付回调查询订单失败");
            return ret__failed;
        }
        
        boolean verifyRequest = verifyCallback(infoMap);
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
        processNotifyVO.setPayment("TEST");
        processNotifyVO.setConfig(config);
        return super.processSuccessNotify(processNotifyVO,verifyRequest);
    }
}
