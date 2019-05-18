/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    JMPayServiceImpl.java 
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
 *    Create at:   2019年03月16日 17:05 
 *
 *    Revision: 
 *
 *    2019/3/16 17:05 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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
 *  * @ClassName JMPayServiceImpl
 *  * @Description TODO(这里用一句话描述这个类的作用)
 *  * @Author Roman
 *  * @Date 2019年03月16日 17:05
 *  * @Version 1.0.0
 *  
 **/

public class JMPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(JMPayServiceImpl.class);

    private static final String RET_FAILED = "fail";

    private static final String RET_SUCCESS = "success";

    /**
     * 商户号
     */
    public String mchId;

    /**
     * 支付请求地址
     */

    public String payUrl;

    /**
     * 回调地址
     */
    public String notifyUrl;

    /**
     * 密钥
     */
    public String key;

    /**
     * 订单查询地址
     */
    public String queryOrderUrl;

    /**
     * 构造器，初始化参数
     */
    public JMPayServiceImpl() {
    }

    public JMPayServiceImpl(Map<String, String> data) {
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
            if (data.containsKey("queryOrderUrl")) {
                this.queryOrderUrl = data.get("queryOrderUrl");
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
        logger.info("[JM]积木支付扫码支付开始============START======================");
        try {
            //获取支付请求参数
            JSONObject data = sealRequest(entity);
            logger.info("[JM]积木支付扫码支付请求参数:{}", data);
            //发送请求

            String response = HttpUtils.toPostJsonStr(data, payUrl);
            logger.info("[JM]积木支付扫码支付响应:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.info("[JM]积木支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[JM]积木支付扫码支付发起HTTP请求无响应结果");
            }
            JSONObject jsonObject = JSONObject.fromObject(response);
            if (jsonObject.containsKey("rst") && "true".equals(jsonObject.getString("rst"))) {
                //下单成功
                JSONObject dataObject = (JSONObject) jsonObject.get("cont");
                String payUrl = dataObject.getString("pay_url");
                return PayResponse.sm_link(entity, payUrl, "扫码支付下单成功");
            }
            return PayResponse.error("下单失败:" + response);


        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[JM]积木支付扫码支付下单失败" + e.getMessage());
        }
    }

    /**
     * @param data 回调请求参数
     * @return: boolean
     **/
    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[JM]积木支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        logger.info("[JM]积木支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[JM]积木支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JM]积木支付生成加密串异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
    }

    /**
     * @param
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private JSONObject sealRequest(PayEntity entity) throws Exception {
        logger.info("[JM]积木支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
//            Map<String, String> dataMap = new HashMap<>();
            Map<String,String> dataMap = new TreeMap();
            JSONObject reqJson = new JSONObject();
            //订单金额
            long amount = Long.parseLong(new DecimalFormat("##").format(entity.getAmount()*100));
            //订单号
            String orderNo = entity.getOrderNo();
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());// 订单时间

//            mer_no	网商号	String	是	商户编号
            dataMap.put("mer_no", mchId);

//            pay_type	支付方式	String	是	1（支付宝),2(银联,云闪付),3(微信)
            dataMap.put("pay_type", entity.getPayCode());

//            order_no	订单号	String	是	商户订单编号
            dataMap.put("order_no", orderNo);

//            amt	订单金额	long	是	下单金额,单位分 支付宝金额上限5W下限1元,银联云闪付金额上限2999下限100
            dataMap.put("amt",""+ amount);

//            cur	货币代码	String	是	目前只支持人民币:CNY
            dataMap.put("cur", "CNY");

//            notify_url	异步通知地址	String	否	用户订单支付结果异步通知
            dataMap.put("notify_url", notifyUrl);

//            sync_call_back_url	同步回调地址	String	否	支付后页面跳转地址
            dataMap.put("sync_call_back_url", entity.getRefererUrl());

//            sign_type	签名类型	String	是	目前只支持MD5
            dataMap.put("sign_type", "MD5");

            logger.info("[JM]积木支付封装签名参数:" + JSONObject.fromObject(dataMap).toString());
            // 以上字段参与签名,生成待签名串
            String sign = generatorSign(dataMap);

            // sign	签名	String	是	数字签名
            dataMap.put("sign", sign);
            reqJson.put("cont",dataMap);
            return reqJson;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JM]积木支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        logger.info("[JM]积木支付生成支付签名串开始==================START========================");
        try {
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = sortMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = String.valueOf(sortMap.get(key));
                if (StringUtils.isBlank(val) || key.equalsIgnoreCase("sign")) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb = sb.replace(sb.length()-1,sb.length(),"");
            sb.append(key);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[JM]积木支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[JM]积木支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JM]积木支付生成支付签名串异常:" + e.getMessage());
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

        logger.info("[JM]积木支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return RET_FAILED;
        }
        //参数验签，从配置中获取
        this.key = config.getString("key");
        this.mchId = config.getString("mchId");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("order_no");
        // 第三方订单号
        String tradeNo = "JM"+System.currentTimeMillis();

        //订单查询
        try {
            JSONObject queryMap = new JSONObject();
            queryMap.put("mer_no",mchId);
            queryMap.put("order_no",orderNo);
            queryMap.put("sign_type","MD5");
            String sign = generatorSign(queryMap);
            queryMap.put("sign",sign);

            JSONObject queryJb = new JSONObject();
            queryJb.put("cont",queryMap);
            logger.info("[JM]积木支付回调  订单查询 参数：" + queryJb);
            String query = HttpUtils.toPostJsonStr(queryJb,queryOrderUrl);
            logger.info("[JM]积木支付回调  订单查询结果:" + query);
            if (StringUtils.isBlank(query)){
                logger.info("[JM]积木支付回调  订单查询结果为空");
                return ret__failed;
            }
            JSONObject jb = JSONObject.fromObject(query);
            boolean flag = (boolean) jb.get("rst");
            if(!flag){
                logger.info("[JM]积木支付回调  订单查询结果:'交易状态'为" + flag);
                return ret__failed;
            }

            JSONObject status = JSONObject.fromObject(jb.get("cont"));
            if(!"2".equals(status.getString("pay_status"))){
                logger.info("[JM]积木支付回调  订单查询  支付结果: 订单状态为" + status.getString("pay_status"));
                return ret__failed;
            }


        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JM]积木支付回调  订单查询 异常");
            return ret__failed;
        }


        //订单状态
        String tradeStatus = infoMap.get("pay_status");
        // 表示成功状态
        String tTradeStatus = "2";
        //实际支付金额
        String orderAmount = infoMap.get("amt");
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
        processNotifyVO.setRealAmount(realAmount/100);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("JM");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}


