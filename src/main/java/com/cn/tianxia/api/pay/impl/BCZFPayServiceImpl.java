/**
 * @Title: BCPayServiceImpl.java
 * @Package com.cn.tianxia.pay.impl
 * @Description: TODO(用一句话描述该文件做什么)
 * @author: seven
 * @date: 2018年12月4日 下午5:11:33
 * @version V1.0
 */
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
import java.util.Iterator;
import java.util.Map;

/**
 *  * @ClassName BCZFPayServiceImpl
 *  * @Description TODO(保诚支付)
 *  * @Author Roman
 *  * @Date 2019年03月28日 12:26
 *  * @Version 1.0.0
 *  
 **/
public class BCZFPayServiceImpl extends PayAbstractBaseService implements PayService {


    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(BCZFPayServiceImpl.class);

    private static final String ret__failed = "FAIL";

    private static final String ret__success = "SUCCESS";

    /**
     * 商户号
     */
    private String mchId;

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
    private String key;


    /**
     * 构造器，初始化参数
     */
    public BCZFPayServiceImpl() {
    }

    public BCZFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("mch_id")) {
                this.mchId = data.get("mch_id");
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
            logger.info("[BCZF]保诚支付扫码请求参数报文:{}", JSONObject.fromObject(data));
            String response;
            JSONObject jsonObject;
            if ("pay_str".equals(data.get("pay_format"))) {
                response = HttpUtils.toPostForm(data, payUrl);

                jsonObject = JSONObject.fromObject(response);
                logger.info("[BCZF]保诚支付扫码响应参数报文:{}", jsonObject);
                if (jsonObject.containsKey("code") && "0000".equalsIgnoreCase(jsonObject.getString("code"))) {
                    //下单成功
                    String payUrl = jsonObject.getString("qrcode");
                    if ("2".equals(entity.getPayType())) {
                        //微信可直接跳转至第三方收银台页面
                        return PayResponse.sm_link(entity, payUrl, "扫码支付下单成功");
                    }
                    return PayResponse.sm_qrcode(entity, payUrl, "扫码支付下单成功");
                }
            } else {
                response = HttpUtils.generatorForm(data, payUrl);
                if (StringUtils.isBlank(response)) {
                    logger.info("[BCZF]保诚支付扫码发起HTTP请求无响应结果");
                    return PayResponse.error("[BCZF]保诚支付扫码发起HTTP请求无响应结果");
                }
                return PayResponse.sm_form(entity, response, "扫码支付下单成功");
            }
            return PayResponse.error("下单失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[BCZF]保诚支付扫码下单异常" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[BCZF]保诚支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


//            pay_memberid 必填 是 商户号	 平台分配的商户编号
            dataMap.put("pay_memberid", mchId);

//           pay_orderid	商户订单号	商户自行生成的订单号, 需要保证系统唯一, 订单号最长32位
            dataMap.put("pay_orderid", orderNo);

//           pay_amount	充值金额	单位: 人民币元, 精确到分, 例如: 100.21
            dataMap.put("pay_amount", amount);

//           pay_applydate  	订单提交时间	订单的提交时间, 使用北京时间, 格式要求: 2019-01-25 08:35:18
            dataMap.put("pay_applydate", orderTime);

//          pay_bankcode	支付产品编号	见 2.支付产品列表
            dataMap.put("pay_bankcode", entity.getPayCode());

//			pay_notifyurl   异步通知地址	商户服务端接收支付结果通知地址
            dataMap.put("pay_notifyurl", notifyUrl);

//           pay_callbackurl	页面跳转地址	使用平台页面支付完成后会调转到此页面
            dataMap.put("pay_callbackurl", entity.getRefererUrl());

//           pay_format		响应格式	见 3.响应格式列表
            if ("902".equals(entity.getPayCode()) || "904".equals(entity.getPayCode())
                    || "906".equals(entity.getPayCode()) || "910".equals(entity.getPayCode())) {
                dataMap.put("pay_format", "pay_str");//响应字符串,需自行生成二维码
            } else {
                dataMap.put("pay_format", "jump_page");//跳转到支付页面
            }

//			pay_clientip         客户端IP地址	客户端的真实IP地址
            dataMap.put("pay_clientip", entity.getIp());

            //以上字段参与签名,生成待签名串
            String sign = generatorSign(dataMap);
//           pay_md5sign	MD5签名字段	签名字符串须转大写, 上述参与签名参数进行ASCII排序后拼接 &key=平台分配秘钥, 见 6.请求签名说明
            dataMap.put("pay_md5sign", sign);
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[BCZF]保诚支付封装请求参数异常:" + e.getMessage());
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
        logger.info("[BCZF]保诚支付生成支付签名串开始==================START========================");
        try {
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = sortMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = sortMap.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.append("key=").append(key);
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[BCZF]保诚支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[BCZF]保诚支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[BCZF]保诚支付生成支付签名串异常:" + e.getMessage());
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
        logger.info("[BCZF]保诚支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[BCZF]保诚支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BCZF]保诚支付生成加密串异常:{}", e.getMessage());
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

        logger.info("[BCZF]保诚支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签，从配置中获取
        this.key = config.getString("key");
        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("orderid");
        // 第三方订单号
        String tradeNo = infoMap.get("transaction_id");
        //订单状态
        String tradeStatus = infoMap.get("returncode");
        // 表示成功状态
        String tTradeStatus = "0000";
        //实际支付金额
        String orderAmount = String.valueOf(infoMap.get("amount"));
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
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
        processNotifyVO.setPayment("BCZF");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}


