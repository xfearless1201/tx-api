/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    JJBPayServiceImpl.java 
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
 *    Create at:   2019年04月17日 16:15 
 *
 *    Revision: 
 *
 *    2019/4/17 16:15 
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
 *  * @ClassName JJBPayServiceImpl
 *  * @Description TODO(聚金宝支付)
 *  * @Author Roman
 *  * @Date 2019年04月17日 16:15
 *  * @Version 1.0.0
 *  
 **/

public class JJBPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(JJBPayServiceImpl.class);

    private static final String RET_FAILED = "Notify is failed";

    private static final String RET_SUCCESS = "opstate=0";

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
     * 订单查询地址
     */
    private String queryUrl;


    /**
     * 构造器，初始化参数
     */
    public JJBPayServiceImpl() {
    }

    public JJBPayServiceImpl(Map<String, String> data) {
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
            logger.info("[JJB]聚金宝支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));

            String response = HttpUtils.generatorFormGet(data, payUrl);
            logger.info("[JJB]聚金宝支付扫码支付发起HTTP请求响应结果:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.error("[JJB]聚金宝支付扫码支付下单失败，无响应结果");
                PayResponse.error("[JJB]聚金宝支付扫码支付下单失败，无响应结果");
            }
            return PayResponse.sm_form(entity, response, "扫码支付下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[JJB]聚金宝支付扫码支付下单异常" + e.getMessage());
        }
    }

    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[JJB]聚金宝支付回调验签开始==============START===========");
        try {

            //获取回调通知原签名串
            String sourceSign = new String(data.get("sign").getBytes(), "UTF-8");

            logger.info("[JJB]聚金宝支付回调验签获取原签名串:{}", sourceSign);
            //生成验签签名串

            String sign = generatorSign(data, 2);
            logger.info("[JJB]聚金宝支付回调验签生成加密串:{}", sign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JJB]聚金宝支付生成加密串异常:{}", e.getMessage());
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
        logger.info("[JJB]聚金宝支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());


//            商户  ID	parter	N	Y	商户 id，由聚金宝平台分配
            dataMap.put("parter", mchId);

//            银行类型	type	N	Y	银行类型，具体请参考附录
            dataMap.put("type", entity.getPayCode());

//            金额	value	N	Y	单位元（人民币），2位小数，最小支付金额请于商务沟通
            dataMap.put("value", amount);

//            商户订单号	orderid	N	Y	商户系统订单号，该订单号将作为接口的返回数据。该值需在商户系统内唯一，聚金宝系统暂时不检查该值是否唯一
            dataMap.put("orderid", orderNo);

//            下行异步通知地址	callbackurl	N	Y	下行异步通知过程的返回地址，需要以 http://开头且没有任何参数
            dataMap.put("callbackurl", notifyUrl);

            //          生成签名串
            String sign = generatorSign(dataMap, 1);
            //            MD5签名	sign	Y	N	32位小写  MD5签名值，GB2312编码

            dataMap.put("sign", sign);


            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JJB]聚金宝支付封装请求参数异常:" + e.getMessage());
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
        logger.info("[JJB]聚金宝支付生成支付签名串开始==================START========================");
        try {

            StringBuffer sb = new StringBuffer();
            if (type == 1) {
                /*              parter={}&type={}&value={}&orderid ={}&callbackurl={}key */
                sb.append("parter=").append(data.get("parter")).append("&")
                        .append("type=").append(data.get("type")).append("&")
                        .append("value=").append(data.get("value")).append("&")
                        .append("orderid=").append(data.get("orderid")).append("&")
                        .append("callbackurl=").append(data.get("callbackurl"));
            } else if (type == 2) {
//                orderid={}&opstate={}&ovalue={}key
                sb.append("orderid=").append(data.get("orderid")).append("&")
                        .append("opstate=").append(data.get("opstate")).append("&")
                        .append("ovalue=").append(data.get("ovalue"));
            } else {
//               orderid={}&parter={}key
                sb.append("orderid=").append(data.get("orderid")).append("&")
                        .append("parter=").append(mchId);
            }
            sb.append(key);
            //生成待签名串
            String singStr = sb.toString();
            logger.info("[JJB]聚金宝支付生成待签名串:" + singStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
            logger.info("[JJB]聚金宝支付生成加密签名串:" + sign);
            return new String(sign.getBytes(), "GB2312");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JJB]聚金宝支付生成支付签名串异常:" + e.getMessage());
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

        logger.info("[JJB]聚金宝支付回调请求参数:{}", infoMap);
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return RET_FAILED;
        }
        //参数验签，从配置中获取
        this.key = config.getString("key");
        this.queryUrl = config.getString("queryUrl");
        this.mchId = config.getString("mchId");

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("orderid");

        // 第三方订单号
        String tradeNo = infoMap.get("sysorderid");
        //订单状态
        String tradeStatus = infoMap.get("opstate");
        // 表示成功状态
        String tTradeStatus = "0";
        //实际支付金额
        String orderAmount = infoMap.get("ovalue");
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return RET_FAILED;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();

        //调用查询接口查询订单信息
        boolean orderStatus = getOrderStatus(orderNo);
        if (!orderStatus) {
            logger.info(orderNo + "此订单尚未支付成功！");
            return RET_FAILED;
        }
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
        processNotifyVO.setPayment("JJB");
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
//            MerchantNo	String/64	平台分配
            map.put("parter", mchId);

//            OutTradeNo	String/64	商户订单号
            map.put("orderid", orderNo);

//            签名
            map.put("sign", generatorSign(map, 3));

            logger.info("[JJB]聚金宝支付订单查询接口请求参数{}", JSONObject.fromObject(map));
            //发送请求
            String response = HttpUtils.toPostForm(map, queryUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[JJB]聚金宝支付订单查询接口响应信息{}", respJson);
            if (respJson.containsKey("opstate") && "0".equals(respJson.getString("opstate"))) {

                logger.info("[JJB]聚金宝支付订单查询成功,订单" + orderNo + "已支付。");
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JJB]聚金宝支付订单查询异常");
            return false;
        }
    }

}


