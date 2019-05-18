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
import java.util.*;

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName YGPayServiceImpl
 * @Description 阳光支付渠道：支付宝
 * @Date 2019/4/22 16 09
 **/
public class YGPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(YGPayServiceImpl.class);
    private static String ret__success = "OK";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private String merchId;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String secret;//密钥
    private String queryOrderUrl;//订单查询接口
    private boolean verifySuccess = true;//回调验签默认状态为true

    public YGPayServiceImpl() {
    }

    public YGPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
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
    }

    /**
     * 回调
     *
     * @param request
     * @param response
     * @param config
     * @return
     */
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        this.merchId = config.getString("merchId");
        this.notifyUrl = config.getString("notifyUrl");
        this.secret = config.getString("secret");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[YG]阳光支付     商户返回信息：" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("transaction_id");//第三方订单号，流水号
        String order_no = dataMap.get("orderid");//支付订单号
        String amount = dataMap.get("amount");//实际支付金额,以分为单位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[YG]阳光支付     获取的{} 流水单号为空", trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[YG]阳光支付    回调订单金额为空");
            return ret__failed;
        }

        String trade_status = dataMap.get("returncode");  //第三方支付状态，1 支付成功
        String t_trade_status = "00";//第三方成功状态

        try {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("pay_memberid", merchId);//商户ID
            queryMap.put("pay_orderid", order_no);//订单号
            queryMap.put("pay_md5sign", generatorSign(queryMap));//MD5签名字段

            String queryData = HttpUtils.toPostForm(queryMap, queryOrderUrl);

            if (StringUtils.isBlank(queryData)) {
                logger.info("[YG]阳光支付 订单查询返回：空");
                return ret__failed;
            }

            logger.info("[YG]阳光支付 订单查询返回信息" + JSONObject.fromObject(queryData));

            JSONObject jb = JSONObject.fromObject(queryData);
            if (!"00".equals(jb.getString("returncode"))) {
                logger.info("[YG]阳光支付 订单查询 订单交易状态：" + jb.getString("returncode"));
                return ret__failed;
            }
            if (!"SUCCESS".equalsIgnoreCase(jb.getString("trade_state"))) {
                logger.info("[YG]阳光支付 订单查询 订单支付状态：" + jb.getString("trade_state"));
                return ret__failed;
            }
        } catch (Exception e) {
            logger.info("[YG]阳光支付 订单查询异常：" + e.getMessage());
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
        processNotifyVO.setPayment("YG");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[YG]阳光支付     回调验签失败");
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
     *
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            Map<String, String> dataMap = sealRequest(payEntity);
            dataMap.put("pay_returntype", "2");//商户ID
            String responseData = HttpUtils.toPostForm(dataMap, payUrl);
            if (StringUtils.isBlank(responseData)) {
                return PayResponse.error("扫码支付响应为空");
            }
            logger.info("[YG]阳光支付 扫码支付响应信息：" + JSONObject.fromObject(responseData));

            JSONObject jb = JSONObject.fromObject(responseData);
            if (jb.containsKey("status") && "success".equalsIgnoreCase(jb.getString("status"))) {
                JSONObject data = jb.getJSONObject("data");
                return PayResponse.sm_link(payEntity, data.getString("pay_url"), "下单成功");
            }
            return PayResponse.error("扫码支付响下单失败");
        } catch (Exception e) {
            e.getStackTrace();
            return PayResponse.error("扫码支付异常：" + e.getMessage());
        }
    }

    /**
     * 回调验签
     *
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {
        String sign = generatorSign(data);
        String sourceSign = data.remove("sign");
        if (sign.equals(sourceSign)) {
            return ret__success;
        }
        return ret__failed;
    }

    /**
     * 参数组装
     *
     * @param payEntity
     * @return
     */
    private Map<String, String> sealRequest(PayEntity payEntity) {
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        dataMap.put("pay_memberid", merchId);//商户ID
        dataMap.put("pay_orderid", payEntity.getOrderNo());//订单号
        dataMap.put("pay_amount", amount);//金额
        dataMap.put("pay_applydate", time);//订单提交时间
        dataMap.put("pay_bankcode", payEntity.getPayCode());//银行编号
        dataMap.put("pay_notifyurl", notifyUrl);//服务端返回地址
        dataMap.put("pay_callbackurl", payEntity.getRefererUrl());//页面返回地址

        dataMap.put("pay_md5sign", generatorSign(dataMap));//MD5签名字段

        logger.info("[YG]阳光支付    请求参数" + JSONObject.fromObject(dataMap));
        return dataMap;
    }

    /**
     * 生成签名
     *
     * @param data
     * @return
     */
    private String generatorSign(Map<String, String> data) {
        try {
            StringBuffer sb = new StringBuffer();
            Map<String, String> treeMap = new TreeMap<>(data);
            Iterator<String> iterator = treeMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = treeMap.get(key);
                if ("sign".equalsIgnoreCase(key) || StringUtils.isBlank(val) || "null".equalsIgnoreCase(val)) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.append("key=").append(secret);

            logger.info("[YG]阳光支付    请求参数" + sb.toString());
            return MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (Exception e) {
            e.getStackTrace();
            return "生成签名异常：" + e.getMessage();
        }
    }
}
