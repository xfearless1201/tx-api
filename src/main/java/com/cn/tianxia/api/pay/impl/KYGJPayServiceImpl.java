/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    KYGJPayServiceImpl.java 
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
 *    Create at:   2019年05月02日 16:40 
 *
 *    Revision: 
 *
 *    2019/5/2 16:40 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.domain.txdata.v2.RechargeDao;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.project.v2.RechargeEntity;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.SpringContextUtils;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 *  * @ClassName KYGJPayServiceImpl
 *  * @Description TODO(卡云管家支付)
 *  * @Author Roman
 *  * @Date 2019年05月02日 16:40
 *  * @Version 1.0.0
 *  
 **/

public class KYGJPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(KYGJPayServiceImpl.class);

    private static final String ret__failed = "Notify is failed";

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
     * 银行编码
     */
    private String code;

    /**
     * 订单查询地址
     */
    private String queryOrderUrl;


    /**
     * 构造器，初始化参数
     */
    public KYGJPayServiceImpl() {
    }

    public KYGJPayServiceImpl(Map<String, String> data, String type) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey(type)) {
                JSONObject jsonObject = JSONObject.fromObject(data.get(type));
                if (jsonObject.containsKey("merchId")) {
                    this.merchId = jsonObject.getString("merchId");
                }
                if (jsonObject.containsKey("payUrl")) {
                    this.payUrl = jsonObject.getString("payUrl");
                }
                if (jsonObject.containsKey("notifyUrl")) {
                    this.notifyUrl = jsonObject.getString("notifyUrl");
                }
                if (jsonObject.containsKey("secret")) {
                    this.secret = jsonObject.getString("secret");
                }
                if (jsonObject.containsKey("code")) {
                    this.code = jsonObject.getString("code");
                }
                if (jsonObject.containsKey("queryOrderUrl")) {
                    this.queryOrderUrl = jsonObject.getString("queryOrderUrl");
                }
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
        logger.info("[KYGJ]卡云管家支付扫码支付开始============START======================");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(entity);
            data.replace("CallBackUrl", URLDecoder.decode(data.get("CallBackUrl"), "UTF-8"));
            logger.info("[KYGJ]卡云管家支付扫码支付请求参数:{}", JSONObject.fromObject(data));

            //发送请求
            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[KYGJ]卡云管家支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[KYGJ]卡云管家支付扫码支付发起HTTP请求无响应结果");
            }
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[KYGJ]卡云管家支付扫码支付响应:{}", jsonObject);
            if (jsonObject.containsKey("Code") && "0".equals(jsonObject.getString("Code"))) {
                //下单成功
                String payurl = jsonObject.getJSONObject("Data").getString("Url");

                return PayResponse.sm_link(entity, payurl, "扫码支付下单成功");
            }
            return PayResponse.error("下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[KYGJ]卡云管家支付扫码支付下单失败" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[KYGJ]卡云管家支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new TreeMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

//            CustomerId	是	String(30)	商户订单号，确保商户内唯一
            dataMap.put("CustomerId", orderNo);

//            Mode	是	String(1)	支付通道代码（详见附录1）
            dataMap.put("Mode", entity.getPayCode());

//            BankCode	是	String(20)	银行编码 （详见附录2）
            dataMap.put("BankCode", code);

//            Money	是	Float(2)	订单金额，单位为元，保留两位小数
            dataMap.put("Money", amount);

//            UserId	是	String(10)	商户号merno
            dataMap.put("UserId", merchId);

            dataMap.put("Message", "top_Up");

//            CallBackUrl	是	String(128)	异步通知地址
            dataMap.put("CallBackUrl", notifyUrl);

//            Sign	是	String(32)	签名,详见支付请求签名规则

            //以上字段参与签名,生成待签名串
            String sign = generatorSign(dataMap);
            dataMap.put("Sign", sign);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[KYGJ]卡云管家支付封装请求参数异常:" + e.getMessage());
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
        logger.info("[KYGJ]卡云管家支付生成支付签名串开始==================START========================");
        try {
            if (data.containsKey("CallBackUrl")){
                data.put("CallBackUrl", URLEncoder.encode(data.get("CallBackUrl"), "UTF-8"));
            }if (data.containsKey("Time")){
                data.put("Time", URLEncoder.encode(data.get("Time"), "UTF-8"));
            }
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            StringBuffer sb = new StringBuffer();
            for (String key : sortMap.keySet()) {
                String val = sortMap.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.append("Key=").append(secret);
            //生成待签名串
            String signStr = sb.toString();

            logger.info("[KYGJ]卡云管家支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[KYGJ]卡云管家支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[KYGJ]卡云管家支付生成支付签名串异常:" + e.getMessage());
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
        logger.info("[KYGJ]卡云管家支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("Sign");
        logger.info("[KYGJ]卡云管家支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[KYGJ]卡云管家支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[KYGJ]卡云管家支付生成加密串异常:{}", e.getMessage());
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
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[KYGJ]卡云管家支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        // 平台订单号
        String orderNo = infoMap.get("CustomerId");
        //参数验签，从配置中获取
        RechargeDao rechargeDao = (RechargeDao) SpringContextUtils.getBeanByClass(RechargeDao.class);
        RechargeEntity rechargeEntity = rechargeDao.selectByOrderNo(orderNo);
        String type = getPayConfigType(String.valueOf(rechargeEntity.getPayType()));//获取支付类型
        config = config.getJSONObject(type);
        this.secret = config.getString("secret");
        this.queryOrderUrl = config.getString("queryOrderUrl");
        this.merchId = config.getString("merchId");
        boolean verifyRequest = verifyCallback(infoMap);

        // 第三方订单号
        String tradeNo = infoMap.get("OrderId");
        //订单状态
        String tradeStatus = String.valueOf(infoMap.get("Status"));
        // 表示成功状态
        String tTradeStatus = "1";
        //实际支付金额
        String orderAmount = infoMap.get("Money");
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
        //成功返回
        processNotifyVO.setRet__success(ret__success);
        //失败返回
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(orderNo);
        processNotifyVO.setTrade_no(tradeNo);
        processNotifyVO.setTrade_status(tradeStatus);
        processNotifyVO.setT_trade_status(tTradeStatus);
        processNotifyVO.setRealAmount(realAmount);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("KYGJ");
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
            map.put("CustomerId", orderNo);
            map.put("OrderType", "1");
            map.put("UserId", merchId);
            map.put("Sign", generatorSign(map));

            logger.info("[KYGJ]卡云管家支付回调订单查询接口请求参数{}", JSONObject.fromObject(map));
            //发送请求
            String response = HttpUtils.toPostForm(map, queryOrderUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[KYGJ]卡云管家支付回调订单查询接口响应信息{}", respJson);
            if (respJson.containsKey("Code") && "0".equals(respJson.getString("Code"))) {
                    logger.info("[KYGJ]卡云管家支付回调订单查询成功,订单" + orderNo + "已支付。");
                    return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[KYGJ]卡云管家支付回调订单查询异常");
            return false;
        }
    }
}



