package com.cn.tianxia.api.pay.impl;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName ZGJHPayServiceImpl
 * @Description 掌柜聚合支付对接渠道支付宝扫码 和支付宝H5
 * @Date 2019/4/11 19 30
 **/
public class ZGJHPayServiceImpl extends PayAbstractBaseService implements PayService {

    private static final Logger logger = LoggerFactory.getLogger(ZGJHPayServiceImpl.class);

    private String merchId;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String secret;//密钥
    private String queryOrderUrl;//订单查询地址

    private static String ret__success = "OK";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private boolean verifySuccess = true;//回调验签默认状态为true

    public ZGJHPayServiceImpl() {
    }

    public ZGJHPayServiceImpl(Map<String, String> data,  String type) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey(type)) {
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
                if (jsonObject.containsKey("queryOrderUrl")) {
                    this.queryOrderUrl = jsonObject.getString("queryOrderUrl");
                }
                if (jsonObject.containsKey("secret")) {
                    this.secret = jsonObject.getString("secret");
                }
            }
        }
    }
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[ZGJH]掌柜聚合支付扫码支付回调请求参数：" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("payOrderId");//第三方订单号，流水号
        String order_no = dataMap.get("orderId");//支付订单号
        String amount = dataMap.get("orderMoney");//实际支付金额
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[ZGJH]掌柜聚合支付扫码支付回调请求参数,获取的{} 流水单号为空",trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[ZGJH]掌柜聚合支付扫码支付回调请求参数,订单金额为空");
            return ret__failed;
        }

        RechargeDao rechargeDao = (RechargeDao) SpringContextUtils.getBeanByClass(RechargeDao.class);
        RechargeEntity rechargeEntity = rechargeDao.selectByOrderNo(order_no);
        String type = getPayConfigType(String.valueOf(rechargeEntity.getPayType()));//获取支付类型
        config = config.getJSONObject(type);

        this.merchId = config.getString("merchId");
        this.notifyUrl = config.getString("notifyUrl");
        this.secret = config.getString("secret");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        String trade_status = "1";  //第三方支付状态，对方没有“支付状态”这个字段
        String t_trade_status = "1";//第三方成功状态

        //订单查询
        try{
            Map<String, String> queryMap = new HashMap<>();
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            queryMap.put("appId", merchId);//商户ID
            queryMap.put("userId", dataMap.get("userId"));//商户会员ID
            queryMap.put("orderId", order_no);//商户订单ID
            queryMap.put("date", time);//查询时间，为系统当前时间(yyyy-MM-dd hh:mm:ss)
            queryMap.put("sign", generatorSign(queryMap));//签名
            logger.info("[ZGJH]掌柜聚合支付扫码支付回调查询订单{}请求参数：{}",order_no, JSONObject.fromObject(queryMap));

            String queryData = HttpUtils.toPostForm(queryMap, queryOrderUrl);

            if(StringUtils.isBlank(queryData)){
                logger.info("[ZGJH]掌柜聚合支付扫码支付回调查询订单发起HTTP请求无响应");
                return ret__failed;
            }
            logger.info("[ZGJH]掌柜聚合支付扫码支付回调查询订单{}响应信息：{}", order_no, JSONObject.fromObject(queryData));

            JSONObject jb = JSONObject.fromObject(queryData);
            if(jb.containsKey("mvpStatus") && jb.getBoolean("mvpStatus")){

                JSONObject jbData = jb.getJSONObject("data");
                //2.付款成功、3.同步成功、
                logger.info("[ZGJH]掌柜聚合支付扫码支付回调查询订单,订单状态：" + jbData.getString("status"));

                if(!"2".equals(jbData.getString("status"))){
                    if(!"3".equals(jbData.getString("status"))){
                        return ret__failed;
                    }
                }
            }else {
                return ret__failed;
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.info("[ZGJH]掌柜聚合支付扫码支付回调查询订单{}异常{}：",order_no,e.getMessage());
            return ret__failed;
        }

        //写入数据库
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setRealAmount(Double.parseDouble(amount));
        processNotifyVO.setIp(ip);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setInfoMap(JSONObject.fromObject(dataMap).toString());
        processNotifyVO.setT_trade_status(t_trade_status);
        processNotifyVO.setConfig(config);
        processNotifyVO.setPayment("ZGJH");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[ZGJH]掌柜聚合支付扫码支付回调验签失败");
            return ret__failed;
        }
        return processSuccessNotify(processNotifyVO, verifySuccess);
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    /**
     * 扫码支付
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try{
            Map<String, String> dataMap = sealRequest(payEntity);
            String responseData = HttpUtils.toPostForm(dataMap, payUrl);

            if(StringUtils.isBlank(responseData)){
                return PayResponse.error("[ZGJH]掌柜聚合支付扫码支付发起HTTP请求无响应");
            }

            logger.info("[ZGJH]掌柜聚合支付扫码支付发起HTTP请求返回信息：" + JSONObject.fromObject(responseData));

            JSONObject jb = JSONObject.fromObject(responseData);

            if(jb.containsKey("mvpStatus") && jb.getBoolean("mvpStatus")) {
                //解析数据data
                JSONObject jbData = jb.getJSONObject("data");

                if(StringUtils.isBlank(payEntity.getMobile())){
                    return PayResponse.sm_link(payEntity, jbData.getString("url"), "下单成功");
                }else {
                    return PayResponse.sm_link(payEntity, jbData.getString("H5"), "下单成功");
                }



            }else {
                return PayResponse.error("扫码支付下单失败："+JSONObject.fromObject(responseData));
            }

        }catch (Exception e){
            e.printStackTrace();
            return PayResponse.error("扫码支付异常:" + e.getMessage());
        }
    }

    /**
     * 回调验签
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {
        String sign = generatorSign(data);
        String sourceSign = data.remove("sign");

        logger.info("[ZGJH]掌柜聚合支付回调生成签名串：{}--源签名串：{}", sign , sourceSign );
        if(sign.equals(sourceSign)){
            return ret__success;
        }
        return ret__failed;
    }

    /**
     * 参数组装
     * @param payEntity
     * @return
     */
    private Map<String, String> sealRequest(PayEntity payEntity){
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());

        dataMap.put("appId", merchId);//商户ID
        dataMap.put("userId", payEntity.getuId());//商户会员ID
        dataMap.put("orderId",  payEntity.getOrderNo());//商户订单ID
        dataMap.put("amount", amount);//预充值金额/元
        dataMap.put("sign", generatorSign(dataMap));//签名

        logger.info("[ZGJH]掌柜聚合支付扫码支付请求参数：" + JSONObject.fromObject(dataMap));
        return dataMap;
    }

    /**
     * 签名
     * @param data
     * @return
     */
    private String generatorSign(Map<String, String> data){
        try{
            StringBuffer sb = new StringBuffer();
            TreeMap<String, String> treeMap = new TreeMap<>(data);

            Iterator<String> iterator = treeMap.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String val = treeMap.get(key);

               if(StringUtils.isBlank(val) || "null".equalsIgnoreCase(val)  || "sign".equals(key)){
                   continue;
               }
               sb.append(key).append("=").append(val).append("&");
            }
            sb.append("key=").append(secret);

            logger.info("[ZGJH]掌柜聚合支付扫码支付生成待签名串：" + sb.toString());
            return MD5Utils.md5toUpCase_32Bit(sb.toString());
        }catch (Exception e){
            e.printStackTrace();
            return "扫码支付异常：" + e.getMessage();
        }
    }



}
