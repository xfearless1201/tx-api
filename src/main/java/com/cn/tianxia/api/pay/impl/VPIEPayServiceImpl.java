/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    VPIEPayServiceImpl.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:    Roman 
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年03月31日 16:30 
 *
 *    Revision: 
 *
 *    2019/3/31 16:30 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 *  * @ClassName VPIEPayServiceImpl
 *  * @Description TODO(微派支付)
 *  * @Author Roman
 *  * @Date 2019年03月31日 16:30
 *  * @Version 1.0.0
 *  
 **/

public class VPIEPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(VPIEPayServiceImpl.class);

    private static final String ret__failed = "fail";

    private static final String ret__success = "success";


    /**
     * 商户号
     */
    private String mchId;

    /**
     * 支付请求地址
     */

    private String payUrl;

    /**
     * 密钥
     */
    private String key;

    /**
     * 构造器，初始化参数
     */
    public VPIEPayServiceImpl() {
    }

    public VPIEPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("mchId")) {
                this.mchId = data.get("mchId");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("key")) {
                this.key = data.get("key");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity entity) {
        logger.info("[VPIE]微派支付扫码支付开始================START============");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(entity);
            logger.info("[VPIE]微派支付扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());

            //发起HTTP请求
            String response = HttpUtils.toPostForm(data, payUrl + entity.getPayCode());

            if (StringUtils.isBlank(response)) {
                logger.info("[VPIE]微派支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[VPIE]微派支付扫码支付发起HTTP请求无响应结果");
            }
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[VPIE]微派支付扫码支付响应结果:{}", jsonObject);
            if (jsonObject.containsKey("ret_state") && "success".equalsIgnoreCase(jsonObject.getString("ret_state"))) {
                //下单成功
                String payUrl = jsonObject.getString("pay_url");

                return PayResponse.sm_link(entity, payUrl, "扫码支付下单成功");
            }
            return PayResponse.error("[VPIE]微派支付下单失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[VPIE]微派支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[VPIE]微派支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[VPIE]微派支付回调验签开始==============START===========");

        String sourceSign = data.remove("sign");
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[VPIE]微派支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[VPIE]微派支付回调验签异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[VPIE]微派支付组装支付请求参数开始==============START==================");
        try {
            //创建参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //商户订单号
            String orderNo = entity.getOrderNo();

//            version	接口版本号	是	String	填写固定值2.0
            dataMap.put("version", "2.0");

//            format	返回数据格式	是	String	填写固定值 json
            dataMap.put("format", "json");

//            body	商品详情	是	String	用户支付时显示的购买产品名
            dataMap.put("body", "top_Up");

//            total_fee	商品金额	是	String	用 户 支 付 的 金 额 单位为 元
            dataMap.put("total_fee", amount);

//            app_id	appid	是	String	平台分配的appid
            dataMap.put("app_id", mchId);

//            out_trade_no	商户订单号	是	String	需要保证唯一性
            dataMap.put("out_trade_no", orderNo);

//            callback_url	跳转地址	是	String	支付成功后用户端页面跳转地址
            dataMap.put("callback_url", entity.getRefererUrl());

//            user_ip	用户IP	是	String
            dataMap.put("user_ip", entity.getIp());

//            device_info	设备信息	否	String	ANDROID;IOS;PC
            if (StringUtils.isBlank(entity.getMobile())) {
                dataMap.put("device_info", "PC");
            }

            //生成签名串
            String sign = generatorSign(dataMap);
            dataMap.put("sign", sign);
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[VPIE]微派支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[VPIE]微派支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        Map<String, String> sortMap = MapUtils.sortByKeys(data);
        StringBuffer sb = new StringBuffer();
        for (String key : sortMap.keySet()) {
            String val = sortMap.get(key);
            if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                continue;
            }
            sb.append(key).append("=").append(val).append("&");
        }

        //生成待签名串
        String signStr = sb.substring(0, sb.length() - 1) + key;
        logger.info("[VPIE]微派支付生成待加密签名串:{}", signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(signStr);
        logger.info("[VPIE]微派支付生成加密签名串:{}", sign);
        return sign;
    }

    /**
     * 回调方法
     *
     * @param request  第三方请求request
     * @param response response
     * @param config   平台对应支付商配置信息
     * @return
     */
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        //参数验签，从配置中获取
        this.key = config.getString("key");
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[VPIE]微派支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("cpparam");
        // 第三方订单号
        String tradeNo = infoMap.get("orderNo");
        //订单状态
        String tradeStatus = infoMap.get("status");
        // 表示成功状态
        String tTradeStatus = "success";
        //实际支付金额
        String orderAmount = infoMap.get("price");
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(orderNo);
        processNotifyVO.setTrade_no(tradeNo);
        processNotifyVO.setTrade_status(tradeStatus);
        processNotifyVO.setT_trade_status(tTradeStatus);
        processNotifyVO.setRealAmount(realAmount);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("VPIE");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}
