/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    YSFPayServiceImpl.java 
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
 *    Create at:   2019年04月17日 22:19 
 *
 *    Revision: 
 *
 *    2019/4/17 22:19 
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  * @ClassName YSFPayServiceImpl
 *  * @Description TODO(易闪付支付)
 *  * @Author Roman
 *  * @Date 2019年04月17日 22:19
 *  * @Version 1.0.0
 *  
 **/

public class YSFPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(YSFPayServiceImpl.class);

    private static final String RET_FAILED = "Notify is failed";

    private static final String RET_SUCCESS = "success";

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 支付请求地址
     */

    private String payUrl;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 密钥
     */
    private String key;


    /**
     * 构造器，初始化参数
     */
    public YSFPayServiceImpl() {
    }

    public YSFPayServiceImpl(Map<String, String> data) {
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
        }
    }

    @Override
    public JSONObject wyPay(PayEntity entity) {
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
            logger.info("[YSF]易闪付支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));

            String response = HttpUtils.generatorForm(data, payUrl);
            logger.info("[YSF]易闪付支付扫码支付发起HTTP请求响应结果:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.error("[YSF]易闪付支付扫码支付下单失败，无响应结果");
                PayResponse.error("[YSF]易闪付支付扫码支付下单失败，无响应结果");
            }
            return PayResponse.sm_form(entity, response, "扫码支付下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[YSF]易闪付支付扫码支付下单异常" + e.getMessage());
        }
    }

    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[YSF]易闪付支付回调验签开始==============START===========");
        try {

            //获取回调通知原签名串
            String sourceSign = data.remove("sign");
            logger.info("[YSF]易闪付支付回调验签获取原签名串:{}", sourceSign);
            //生成验签签名串
            String sign = generatorSign(data, 2);
            logger.info("[YSF]易闪付支付回调验签生成加密串:{}", sign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YSF]易闪付支付生成加密串异常:{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[YSF]易闪付支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

//            商户ID	merchant_no	否	商户id，商户系统分配
            dataMap.put("merchant_no", mchId);

//            支付类型	pay_type	否	支付类型支付宝扫码:2
            dataMap.put("pay_type", entity.getPayCode());

//            金额	order_amount	否	单位元(人民币)，最小支付金额为2，例如：2,  整数支付金额,不能带小数点 支付金额：100~2000
            dataMap.put("order_amount", amount);

//            商户订单号	order_no	否	商户系统订单号，该订单号将作为接口的返回数据。该值需在商户系统内唯一
            dataMap.put("order_no", orderNo);

//            异步通知地址	notify_url 	否	异步通知过程的返回地址，需要以http://开头且没有任何参数(如存在特殊字符请转码,注:不支持参数)
            dataMap.put("notify_url", notifyUrl);

//            请求时间	order_time	否	系统请求时间，精确到秒，格式为：yyyy-MM-dd HH:mm:ss注：北京时间例如：2017-01-01 12:45:52
            dataMap.put("order_time", orderTime);

            //          生成签名串
            String sign = generatorSign(dataMap, 1);
//            MD5签名	sign	否	32位小写MD5签名值,编码方式为：UTF-8

            dataMap.put("sign", sign);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YSF]易闪付支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param data
     * @param type 1:支付   2:回调    3:查询
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data, int type) throws Exception {
        logger.info("[YSF]易闪付支付生成支付签名串开始==================START========================");
        try {

            StringBuffer sb = new StringBuffer();
            if (type == 1) {
//                merchant_no={0}&pay_type={1}&order_amount={2}&order_no={3}&notify_url={4}&order_time={5}&key={6}
                sb.append("merchant_no=").append(data.get("merchant_no")).append("&")
                        .append("pay_type=").append(data.get("pay_type")).append("&")
                        .append("order_amount=").append(data.get("order_amount")).append("&")
                        .append("order_no=").append(data.get("order_no")).append("&")
                        .append("notify_url=").append(data.get("notify_url")).append("&")
                        .append("order_time=").append(data.get("order_time")).append("&");
            } else if (type == 2) {
//                order_no={0}&result={1}&amount={2}&system_no={3}&complete_time={4}&key ={5}
                sb.append("order_no=").append(data.get("order_no")).append("&")
                        .append("result=").append(data.get("result")).append("&")
                        .append("amount=").append(data.get("amount")).append("&")
                        .append("system_no=").append(data.get("system_no")).append("&")
                        .append("complete_time=").append(data.get("complete_time")).append("&");
            }
            sb.append("key=").append(key);
            //生成待签名串
            String singStr = sb.toString();
            logger.info("[YSF]易闪付支付生成待签名串:" + singStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
            logger.info("[YSF]易闪付支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YSF]易闪付支付生成支付签名串异常:" + e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
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
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[YSF]易闪付支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return RET_FAILED;
        }
        //参数验签，从配置中获取
        this.key = config.getString("key");
        this.mchId = config.getString("mchId");

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("order_no");

        // 第三方订单号
        String tradeNo = infoMap.get("system_no");
        //订单状态
        String tradeStatus = infoMap.get("result");
        // 表示成功状态
        String tTradeStatus = "1";
        //实际支付金额
        String orderAmount = infoMap.get("amount");
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return RET_FAILED;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();

        //成功返回
        processNotifyVO.setRet__success(RET_SUCCESS);
        //失败返回
        processNotifyVO.setRet__failed(RET_FAILED);
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(orderNo);
        processNotifyVO.setTrade_no(tradeNo);
        processNotifyVO.setTrade_status(tradeStatus);
        processNotifyVO.setT_trade_status(tTradeStatus);
        processNotifyVO.setRealAmount(realAmount);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("YSF");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }

}



