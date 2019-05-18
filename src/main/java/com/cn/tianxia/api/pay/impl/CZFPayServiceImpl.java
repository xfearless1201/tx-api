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
 *  * @ClassName CZFPayServiceImpl
 *  * @Description TODO(畅支付)
 *  * @Author Roman
 *  * @Date 2019年04月23日 16:27
 *  * @Version 2.0.0
 *  
 **/
public class CZFPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(CZFPayServiceImpl.class);

    private static final String ret__failed = "Notify Is Failed";

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
    public CZFPayServiceImpl() {
    }

    public CZFPayServiceImpl(Map<String, String> data) {
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
            data.remove("mch_key");
            logger.info("[CZF]畅支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));

            String response = HttpUtils.toPostForm(data, payUrl + entity.getPayCode());
            logger.info("[CZF]畅支付扫码支付请求地址:{}", payUrl + entity.getPayCode());
            if (StringUtils.isBlank(response)) {
                logger.info("[CZF]畅支付扫码支付发起HTTP请求无响应");
                return PayResponse.error("[CZF]畅支付扫码支付发起HTTP请求无响应");
            }

            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[CZF]畅支付扫码支付响应参数：{}", respJson);
            if (respJson.containsKey("code") && "1".equals(respJson.getString("code"))) {
                //PC
                String payUrl;
                if (StringUtils.isBlank(entity.getMobile())) {
                    if ("3".equals(entity.getPayType())) {//支付宝
                        payUrl = respJson.getJSONObject("data").getString("redirect_pay_url");
                        return PayResponse.sm_link(entity, payUrl, "扫码支付下单成功");
                    } else {//微信
                        payUrl = respJson.getJSONObject("data").getString("code_url");
                        return PayResponse.sm_qrcode(entity, payUrl, "扫码支付下单成功");
                    }
                } else {
                    //MB
                    payUrl = respJson.getJSONObject("data").getString("pay_info");
                    return PayResponse.sm_link(entity, payUrl, "扫码支付下单成功");
                }
            }
            return PayResponse.error("[CZF]畅支付下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[CZF]畅支付扫码支付异常" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[CZF]畅支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount() * 1000);
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

//            sign_type 是 string(10) 加密类型，取值：md5默认：md5
            dataMap.put("sign_type", "md5");

//            mch_id 是 string(32) 商户ID，由平台分配
            dataMap.put("mch_id", mchId);

//            mch_order 是 string(50) 订单号商家系统创建-可用于查询订单
            dataMap.put("mch_order", orderNo);

//            amt 是 int 订单金额，单位厘 备注说明：1元 = 1000厘，需 是10的整数倍（即末尾必须是0的数值）；卡密 API充值，卡密网页充值不是真实支付金额，要以 回调为准
            dataMap.put("amt", amount);

//            remark 是 string(32) 订单内容
            dataMap.put("remark", "top_Up");

//            created_at 是 int 商家创建订单时间-为时间戳
            dataMap.put("created_at", String.valueOf(System.currentTimeMillis()));

//            client_ip 是 string(16) 订单生成的机器 IP
            dataMap.put("client_ip", entity.getIp());

//            notify_url 是 string(255) 订单回调通知URL-必须能正常访问
            dataMap.put("notify_url", notifyUrl);

            dataMap.put("mch_key", key);

            String sign = generatorSign(dataMap);
//            sign 是 string(32) MD5签名结果，详见“sign安全规范”
            dataMap.put("sign", sign);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[CZF]畅支付封装请求参数异常:" + e.getMessage());
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
            //生成待签名串
            String signStr = sb.substring(0, sb.length() - 1);
            logger.info("[CZF]畅支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[CZF]畅支付生成签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[CZF]畅支付生成支付签名串异常:" + e.getMessage());
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
        logger.info("[CZF]畅支付回调验签开始==============START===========");

        data.put("mch_key", key);
        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        logger.info("[CZF]畅支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[CZF]畅支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[CZF]畅支付生成加密串异常:{}", e.getMessage());
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

        logger.info("[CZF]畅支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("mch_order");
        // 第三方订单号
        String tradeNo = "CZF" + System.currentTimeMillis();
        //订单状态
        String tradeStatus = infoMap.get("status");
        // 表示成功状态
        String tTradeStatus = "2";
        //实际支付金额
        String orderAmount = infoMap.get("amt");
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
        processNotifyVO.setRealAmount(realAmount / 1000);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("CZF");
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
            map.put("mch_id", mchId);
            map.put("mch_order", orderNo);
            map.put("created_at", String.valueOf(System.currentTimeMillis()));
            map.put("sign_type", "md5");
            map.put("mch_key", key);
            map.put("sign", generatorSign(map));

            //发送请求
            map.remove("mch_key");
            logger.info("[CZF]畅支付回调订单查询接口请求参数{}", JSONObject.fromObject(map));
            String response = HttpUtils.toPostForm(map, queryUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[CZF]畅支付回调订单查询接口响应信息{}", respJson);
            if (respJson.containsKey("code") && "1".equals(respJson.getString("code"))) {
                if ("2".equals(respJson.getJSONObject("data").getString("status"))) {

                    logger.info("[CZF]畅支付回调订单查询成功,订单" + orderNo + "已支付。");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[CZF]畅支付回调订单查询异常");
            return false;
        }
    }
}


