/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    GSPayServiceImpl.java 
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
 *    Create at:   2019年04月14日 11:48 
 *
 *    Revision: 
 *
 *    2019/4/14 11:48 
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
 *  * @ClassName GSPayServiceImpl
 *  * @Description TODO(金阳支付)
 *  * @Author Roman
 *  * @Date 2019年04月14日 11:48
 *  * @Version 1.0.0
 *  
 **/

public class GSPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(GSPayServiceImpl.class);

    private static final String ret__failed = "Notify is failed";

    private static final String ret__success = "ok";

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
     * 构造器，初始化参数
     */
    public GSPayServiceImpl() {
    }

    public GSPayServiceImpl(Map<String, String> data) {
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
            logger.info("[GS]金阳支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));

            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[GS]金阳支付扫码支付下单失败，无响应结果");
                return PayResponse.error("[GS]金阳支付扫码支付下单失败，无响应结果");
            }
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[GS]金阳支付扫码支付响应参数：{}", jsonObject);
            if (jsonObject.containsKey("rspCode") && "1".equals(jsonObject.getString("rspCode"))) {
                //下单成功
                String payUrl = jsonObject.getJSONObject("data").getString("r6_qrcode");
                return PayResponse.sm_qrcode(entity, payUrl, "扫码支付下单成功");
            }
            return PayResponse.error("下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[GS]金阳支付扫码支付下单异常" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[GS]金阳支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

//            商户ID	p1_mchtid	是	int		商户ID,由金阳支付分配
            dataMap.put("p1_mchtid", mchId);

//            支付方式	p2_paytype	是	String(20)	WEIXIN	支付网关(参见附录说明4.3)
            dataMap.put("p2_paytype", entity.getPayCode());

//            支付金额	p3_paymoney	是	decimal	0.01	订单金额最小0.01(以元为单位）
            dataMap.put("p3_paymoney", amount);

//            商户平台唯一订单号	p4_orderno	是	String(50)		商户系统内部订单号，要求50字符以内，同一商户号下订单号唯一
            dataMap.put("p4_orderno", orderNo);

//            商户异步回调通知地址	p5_callbackurl	是	String(200)		商户异步回调通知地址
            dataMap.put("p5_callbackurl", notifyUrl);

//            商户同步通知地址	p6_notifyurl	否	String(200)		商户同步通知地址
            dataMap.put("p6_notifyurl", "");

//            版本号	p7_version	是	String(4)	v2.8	V2.8
            dataMap.put("p7_version", "v2.8");

//            签名加密方式	p8_signtype	是	int	1	签名加密方式
            dataMap.put("p8_signtype", "1");

//            备注信息，上行中attach原样返回	p9_attach	否	String(128)		备注信息，上行中attach原样返回  URL Encode （UTF-8）
            dataMap.put("p9_attach", "top_Up");

//            分成标识	p10_appname	否	Strng(25)		分成标识
            dataMap.put("p10_appname", "");

//            是否显示收银台	p11_isshow	是	int	0	是否显示PC收银台
            dataMap.put("p11_isshow", "0");

//            商户的用户下单IP	p12_orderip	否	String(20)	192.168.10.1	商户的用户下单IP
            dataMap.put("p12_orderip", "");

//            商户系统用户唯一标识	p13_memberid	否	String(20)	123456	商户用户标识，快捷(FASTPAY)、银联前台快捷(UNIONFASTPAY)，必传，且参与签名，非快捷支付不传、不参与签名
            dataMap.put("p13_memberid", "");

            //生成待签名串
            String sign = generatorSign(dataMap, 1);
            dataMap.put("sign", sign);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[GS]金阳支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param type 1 支付 2回调 3查询
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data, int type) throws Exception {
        try {
            StringBuffer sb = new StringBuffer();
            if (type == 1) {
                sb.append("p1_mchtid=").append(data.get("p1_mchtid")).append("&")
                        .append("p2_paytype=").append(data.get("p2_paytype")).append("&")
                        .append("p3_paymoney=").append(data.get("p3_paymoney")).append("&")
                        .append("p4_orderno=").append(data.get("p4_orderno")).append("&")
                        .append("p5_callbackurl=").append(data.get("p5_callbackurl")).append("&")
                        .append("p6_notifyurl=").append(data.get("p6_notifyurl")).append("&")
                        .append("p7_version=").append(data.get("p7_version")).append("&")
                        .append("p8_signtype=").append(data.get("p8_signtype")).append("&")
                        .append("p9_attach=").append(data.get("p9_attach")).append("&")
                        .append("p10_appname=").append(data.get("p10_appname")).append("&")
                        .append("p11_isshow=").append(data.get("p11_isshow")).append("&")
                        .append("p12_orderip=").append(data.get("p12_orderip"));
            } else if (type == 2) {
                sb.append("partner=").append(data.get("partner")).append("&")
                        .append("ordernumber=").append(data.get("ordernumber")).append("&")
                        .append("orderstatus=").append(data.get("orderstatus")).append("&")
                        .append("paymoney=").append(data.get("paymoney"));
            } else {
                sb.append("p1_mchtid=").append(data.get("p1_mchtid")).append("&")
                        .append("p2_signtype=").append(data.get("p2_signtype")).append("&")
                        .append("p3_orderno=").append(data.get("p3_orderno")).append("&")
                        .append("p4_version=").append(data.get("p4_version"));
            }
            sb.append(key);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[GS]金阳支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[GS]金阳支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[GS]金阳支付生成支付签名串异常:" + e.getMessage());
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
        logger.info("[GS]金阳支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        logger.info("[GS]金阳支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data, 2);
            logger.info("[GS]金阳支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[GS]金阳支付生成加密串异常:{}", e.getMessage());
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

        logger.info("[GS]金阳支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("ordernumber");
        // 第三方订单号
        String tradeNo = infoMap.get("sysnumber");
        //订单状态
        String tradeStatus = infoMap.get("orderstatus");
        // 表示成功状态
        String tTradeStatus = "1";
        //实际支付金额
        String orderAmount = infoMap.get("paymoney");
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
        processNotifyVO.setPayment("GS");
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
            map.put("p1_mchtid", mchId);
            map.put("p2_signtype", "1");
            map.put("p3_orderno", orderNo);
            map.put("p4_version", "v2.8");
            map.put("sign", generatorSign(map, 3));

            logger.info("[GS]金阳支付订单查询接口请求参数{}", JSONObject.fromObject(map));
            //发送请求
            String response = HttpUtils.toPostForm(map, queryUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[GS]金阳支付订单查询接口响应信息{}", respJson);
            if (respJson.containsKey("rspCode") && "1".equals(respJson.getString("rspCode"))) {
                if ("1".equals(respJson.getJSONObject("data").getString("r5_orderstate"))) {
                    logger.info("[GS]金阳支付订单查询成功,订单" + orderNo + "已支付。");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[GS]金阳支付订单查询异常");
            return false;
        }
    }
}

