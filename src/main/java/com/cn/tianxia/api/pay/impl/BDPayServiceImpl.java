/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    BDPayServiceImpl.java 
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
 *    Create at:   2019年05月15日 11:57 
 *
 *    Revision: 
 *
 *    2019/5/15 11:57 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.JSONUtils;
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
 *  * @ClassName BDPayServiceImpl
 *  * @Description TODO(BDPAY支付)
 *  * @Author Roman
 *  * @Date 2019年05月15日 11:57
 *  * @Version 1.0.0
 *  
 **/

public class BDPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(BDPayServiceImpl.class);

    private static final String ret__failed = "Notify Is Failed";

    private static final String ret__success = "success";

    /**
     * 商户号
     */
    private String merchId;

    /**
     * 支付地址
     */
    private String payUrl;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 密钥
     */
    private String secret;

    /**
     * 订单查询地址
     */
    private String queryOrderUrl;


    /**
     * 构造器，初始化参数
     */
    public BDPayServiceImpl() {
    }

    public BDPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merchId")) {
                this.merchId = data.get("merchId");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("secret")) {
                this.secret = data.get("secret");
            }
            if (data.containsKey("queryOrderUrl")) {
                this.queryOrderUrl = data.get("queryOrderUrl");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity entity) {
        logger.info("[BDPAY]BDPAY支付网银支付开始============START======================");
        try {
            //获取请求参数
            Map<String, String> data = sealRequest(entity, 1);
            logger.info("[BDPAY]BDPAY支付网银支付请求参数{}", data);
            //发送请求
            String response = HttpUtils.toPostForm(data, payUrl);

            logger.info("[BDPAY]BDPAY支付网银支付响应结果:{}", JSONObject.fromObject(response));
            if (StringUtils.isBlank(response)) {
                logger.info("[BDPAY]BDPAY支付网银支付发起请求无响应结果");
                return PayResponse.error("[BDPAY]BDPAY支付网银支付发起请求无响应结果");
            }
            return PayResponse.wy_form(entity.getPayUrl(), response);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[BDPAY]BDPAY支付网银支付下单失败" + e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity entity) {
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(entity, 0);
            logger.info("[BDPAY]BDPAY支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));

            String response = HttpUtils.generatorForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[BDPAY]BDPAY支付扫码支付发起HTTP请求无响应");
                return PayResponse.error("[BDPAY]BDPAY支付扫码支付发起HTTP请求无响应");
            }
            return PayResponse.sm_form(entity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[BDPAY]BDPAY支付扫码支付异常" + e.getMessage());
        }
    }

    /**
     * @param type 0:扫码  1：网银
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity, int type) throws Exception {
        logger.info("[BDPAY]BDPAY支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


//          fxid	商户ID	是	唯一号，由平台提供
            dataMap.put("fxid", merchId);

//          fxorderid	商户订单号	是	仅允许字母或数字类型,12~22个字符，不要有中文
            dataMap.put("fxorderid", orderNo);

//          fxamount	金额	是	金额(单位：元)，支持两位小数
            dataMap.put("fxamount", amount);

//          fxnotifyurl	异步通知地址	是	异步接收存款结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
            dataMap.put("fxnotifyurl", notifyUrl);

//          fxreturnurl	同步通知地址	是	存款成功后跳转到的地址，不参与签名。
            dataMap.put("fxreturnurl", entity.getRefererUrl());

//          fxbankcode	银行编码	否	用于网银直连模式，请求的银行编号，参考附录-4.2,仅网银接口可用。
//          fxpaytype	请求类型	是	请求支付的接口类型，查看附录-4.1
            if (type == 1) {
                dataMap.put("fxbankcode", entity.getPayCode());
                dataMap.put("fxpaytype", "bank_udun");
            } else {
                dataMap.put("fxpaytype", entity.getPayCode());
            }

            //生成待签名串
            String sign = generatorSign(dataMap, 1);
//          fxsign	签名  注意：加密字符串拼接不带键名	是	通过签名算法计算得出的签名值。
            dataMap.put("fxsign", sign);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[BDPAY]BDPAY支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param type 1 : 支付   2：回调   3：查询
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data, int type) throws Exception {
        try {
            StringBuffer sb = new StringBuffer();
            if (type == 1) {
//                【md5(商户ID+商户订单号+金额+异步通知地址+商户秘钥)】
                sb.append(merchId)
                        .append(data.get("fxorderid"))
                        .append(data.get("fxamount"))
                        .append(data.get("fxnotifyurl"));
            } else if (type == 2) {
//                【md5(订单状态+商户ID+商户订单号+金额+商户秘钥)】
                sb.append(data.get("fxstatus"))
                        .append(data.get("fxid"))
                        .append(data.get("fxorderid"))
                        .append(data.get("fxamount"));
            } else {
//                【md5(商户ID+商户订单号+商户动作+商户秘钥)】
                sb.append(data.get("fxid"))
                        .append(data.get("fxorderid"))
                        .append(data.get("fxaction"));
            }
            sb.append(secret);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[BDPAY]BDPAY支付生成待加密串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[BDPAY]BDPAY支付生成签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[BDPAY]BDPAY支付生成支付签名串异常:" + e.getMessage());
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
        logger.info("[BDPAY]BDPAY支付回调验签开始=========================START===========================");

        //获取回调通知原签名串
        String sourceSign = data.remove("fxsign");
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data, 2);
            logger.info("[BDPAY]BDPAY支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BDPAY]BDPAY支付回调生成加密串异常:{}", e.getMessage());
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
        this.secret = config.getString("secret");
        this.merchId = config.getString("merchId");
        this.queryOrderUrl = config.getString("queryOrderUrl");
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[BDPAY]BDPAY支付回调请求参数报文:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("fxorderid");
        // 第三方订单号
        String tradeNo = infoMap.get("fxtranid");
        //订单状态
        String tradeStatus = infoMap.get("fxstatus");
        // 表示成功状态
        String tTradeStatus = "succ";
        //实际支付金额
        String orderAmount = String.valueOf(infoMap.get("fxamount_succ"));
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        //查询订单信息
        boolean orderStatus = getOrderStatus(orderNo);
        if (!orderStatus) {
            logger.info(orderNo + "此订单尚未支付成功！");
            return ret__failed;
        }
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
        processNotifyVO.setPayment("BD");
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
            map.put("fxid", merchId);
            map.put("fxaction", "depositquery");
            map.put("fxorderid", orderNo);
            map.put("fxsign", generatorSign(map, 3));

            logger.info("[BDPAY]BDPAY支付回调订单查询接口请求参数{}", JSONObject.fromObject(map));
            //发送请求
            String response = HttpUtils.toPostForm(map, queryOrderUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[BDPAY]BDPAY支付回调订单查询接口响应信息{}", respJson);
            if (JSONUtils.compare(respJson, "fxstatus", "succ")) {
                logger.info("[BDPAY]BDPAY支付回调订单查询成功,订单" + orderNo + "已支付。");
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BDPAY]BDPAY支付回调订单查询异常");
            return false;
        }
    }
}






