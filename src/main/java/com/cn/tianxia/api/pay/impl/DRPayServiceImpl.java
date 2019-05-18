/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    DRPayServiceImpl.java 
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
 *    Create at:   2019年04月22日 15:02 
 *
 *    Revision: 
 *
 *    2019/4/22 15:02 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 *  * @ClassName DRPayServiceImpl
 *  * @Description TODO(大仁支付)
 *  * @Author Roman
 *  * @Date 2019年04月22日 15:02
 *  * @Version 1.0.0
 *  
 **/

public class DRPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DRPayServiceImpl.class);

    private static final String ret__failed = "Notify Is Failed";

    private static final String ret__success = "000000";

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 支付请求地址
     */
    private String payUrl;

    /**
     * 订单查询地址
     */
    private String queryUrl;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 密钥
     */
    private String key;

    /**
     * 密钥
     */
    private String tranType;

    /**
     * 构造器，初始化参数
     */
    public DRPayServiceImpl() {
    }

    public DRPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("mchId")) {
                this.mchId = data.get("mchId");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("key")) {
                this.key = data.get("key");
            }
            if (data.containsKey("queryUrl")) {
                this.queryUrl = data.get("queryUrl");
            }
            if (data.containsKey("tranType")) {
                this.tranType = data.get("tranType");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity entity) {
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(entity);
            logger.info("[DR]大仁支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));

            String response = HttpUtils.generatorFormGet(data, payUrl);
            logger.info("[DR]大仁支付扫码支付响应参数：{}", response);
            if (StringUtils.isBlank(response)) {
                logger.info("[DR]大仁支付扫码支付发起HTTP请求无响应");
                return PayResponse.error("[DR]大仁支付扫码支付发起HTTP请求无响应");
            }
            return PayResponse.sm_form(entity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[DR]大仁支付扫码支付异常" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[DR]大仁支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

//            merCode	商户编号	我方平台注册或申请获得
            dataMap.put("merCode", mchId);

//            tranNo	订单号	使用uuid生成订单号以免重复
            dataMap.put("tranNo", orderNo);

//            tranType	交易类型	扫码(电脑端)：00 网银(电脑端): 01 快捷(电脑端/手机端)：02 H5(手机端)：03
            dataMap.put("tranType", tranType);

//            tranAmt	交易金额	单位：分
            dataMap.put("tranAmt", amount);

//            collectWay	支付方式	电脑端微信(微信扫码)：WXZF 电脑端支付宝(支付宝扫码)：ZFBZF 电脑端QQ支付(QQ扫码)：QQZF 电脑端京东支付(京东扫码)：JDZF 电脑端银联支付(银联扫码)：UPZF 电脑端网银：WEB 电脑端快捷支付(快捷支付)：QUICK 手机端微信(微信H5)：WXH5 手机端支付宝(支付宝H5)：ZFBH5 手机端QQ支付(QQH5)：QQZF5 手机端京东支付(京东H5)：JDH5 手机端银联支付(银联H5)：UPH5 手机端快捷支付(快捷H5)：QUICK
            dataMap.put("collectWay", entity.getPayCode());

//            tranTime	交易时间	格式：yyyyMMddHHmmss
            dataMap.put("tranTime", orderTime);

//            noticeUrl	回调地址	支付结果的通知地址
            dataMap.put("noticeUrl", notifyUrl);

            //生成待签名串
            String sign = generatorSign(dataMap);
//            sign	签名	详见1.1安全规范

            dataMap.put("sign", sign);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[DR]大仁支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        try {
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            StringBuffer sb = new StringBuffer();
            for (String key : sortMap.keySet()) {
                String val = sortMap.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.replace(sb.length() - 1, sb.length(), "");
            sb.append(key);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[DR]大仁支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[DR]大仁支付生成签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[DR]大仁支付生成支付签名串异常:" + e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }

    /**
     * 功能描述:回调验签
     *
     * @param data
     * @return: boolean
     **/
    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[DR]大仁支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        logger.info("[DR]大仁支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[DR]大仁支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[DR]大仁支付生成加密串异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
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
        this.mchId = config.getString("mchId");
        this.queryUrl = config.getString("queryUrl");
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[DR]大仁支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("tranNo");
        // 第三方订单号
        String tradeNo = "DR" + System.currentTimeMillis();
        //订单状态
        String tradeStatus = infoMap.get("status");
        // 表示成功状态
        String tTradeStatus = "0";
        //实际支付金额
        String orderAmount = infoMap.get("tranAmt");
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        //查询订单信息  不可用
//        boolean orderStatus = getOrderStatus(orderNo);
//        if (!orderStatus) {
//            logger.info(orderNo + "此订单尚未支付成功！");
//            return ret__failed;
//        }
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(orderNo);
        processNotifyVO.setTrade_no(tradeNo);
        processNotifyVO.setTrade_status(tradeStatus);
        processNotifyVO.setT_trade_status(tTradeStatus);
        processNotifyVO.setRealAmount(realAmount / 100);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("DR");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }

    /**
     * 功能描述:查询订单状态
     *
     * @param orderNo 订单号
     * @return: boolean
     **/
    private boolean getOrderStatus(String orderNo) {
        try {
            //封装请求参数
            Map<String, String> map = new HashMap<>();
            map.put("merCode", mchId);
            map.put("tranNo", orderNo);
            map.put("tranTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            map.put("orderId", orderNo);

//            签名
            map.put("sign", generatorSign(map));

            logger.info("[DR]大仁支付回调订单查询接口请求参数{}", JSONObject.fromObject(map));
            //发送请求
            String response = HttpUtils.toPostForm(map, queryUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[DR]大仁支付回调订单查询接口响应信息{}", respJson);
            if (respJson.containsKey("respCode") && "0".equals(respJson.getString("respCode"))) {
                if ("0".equalsIgnoreCase(respJson.getString("status"))) {

                    logger.info("[DR]大仁支付回调订单查询成功,订单" + orderNo + "已支付。");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[DR]大仁支付回调订单查询异常");
            return false;
        }
    }
}


