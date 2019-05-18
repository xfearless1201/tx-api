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
 * @author Vicky
 * @version 1.0.0
 * @ClassName PAPayServiceImpl
 * @Description 平安支付对接渠道：支付宝扫码、H5、微信扫码、微信H5、网银支付 PC/H5/APP同步
 * @Date 2019/3/19 09 10
 **/
public class PAPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(PAPayServiceImpl.class);
    private static String ret__success = "OK";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private String sid;//商户号
    private String md5key;//密钥
    private String payUrl;//支付地址
    private String notifyUrl;//回调地址
    private String queryOrderUrl;//订单查询地址
    private boolean verifySuccess = true;//回调验签默认状态为true


    public PAPayServiceImpl() {
    }

    public PAPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("sid")) {
                this.sid = data.get("sid");
            }
            if (data.containsKey("md5key")) {
                this.md5key = data.get("md5key");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("queryOrderUrl")) {
                this.queryOrderUrl = data.get("queryOrderUrl");
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
        logger.info("[PA]平安支付  回调======================start================================");
        //取配置中的参数值
        this.sid = config.getString("sid");
        this.md5key = config.getString("md5key");
        this.notifyUrl = config.getString("notifyUrl");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[PA]平安支付  商户返回信息：" + dataMap);

        String trade_no = dataMap.get("transaction_id");//第三方订单号，流水号
        String order_no = dataMap.get("orderid");//支付订单号
        String amount = dataMap.get("amount");//商户订单总金额，订单总金额以元为单位，精确到小数点后两位
        String ip = org.apache.commons.lang.StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);  //回调ip


        if (StringUtils.isBlank(trade_no)) {
            logger.info("[PA]平安支付   获取的 流水单号为空");
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[PA]平安支付   回调订单金额为空");
            return ret__failed;
        }

        //订单查询
        try {
            Map<String, String> queryOrderMap = new HashMap<>();
            queryOrderMap.put("pay_memberid", sid);
            queryOrderMap.put("pay_orderid", order_no);
            queryOrderMap.put("pay_md5sign", generatorSign(queryOrderMap));

            String queryResponse = HttpUtils.toPostForm(queryOrderMap, queryOrderUrl);
            if (StringUtils.isBlank(queryResponse)) {
                return ret__failed;
            }

            JSONObject jb = JSONObject.fromObject(queryResponse);
            logger.info("[PA]平安支付   回调订单查询结果：" + jb);

            if (!"00".equals(jb.getString("returncode"))) {
                logger.info("[JM]积木支付回调  订单查询结果:'交易状态'为" + jb.getString("returncode"));
                return ret__failed;
            }
            if (!"SUCCESS".equals(jb.getString("trade_state"))) {
                logger.info("[JM]积木支付回调  订单查询  支付结果: 订单状态为" + jb.getString("trade_state"));
                return ret__failed;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String trade_status = dataMap.get("returncode");  //第三方支付状态，00 表成功
        String t_trade_status = trade_status;//第三方成功状态

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
        processNotifyVO.setPayment("PA");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[PA]平安支付    回调验签失败");
            return ret__failed;
        }
        return processSuccessNotify(processNotifyVO, verifySuccess);
    }

    /**
     * 网银支付
     *
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[PA]平安支付  网银支付======================start================================");
        try {
            Map<String, String> dataMap = sealRequest(payEntity, 1);
            String sign = generatorSign(dataMap);
            dataMap.put("pay_md5sign", sign);//签名字段
            dataMap.put("pay_productname", "TOP-UP");//商品名称
            logger.info("[PA]平安支付  网银支付  请求参数：" + dataMap);
            String response = HttpUtils.toPostForm(dataMap, payUrl);
            if (StringUtils.isBlank(response)) {
                return PayResponse.error("网银支付HTTP 请求 返回为空");
            }
            logger.info("[PA]平安支付  网银支付HTTP 请求 返回参数" + response);
            return PayResponse.wy_form(payEntity.getPayUrl(), response);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[PA]平安支付  网银支付 异常" + e.getMessage());
        }
    }

    /**
     * 扫码支付
     *
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[PA]平安支付  扫码支付======================start================================");
        try {
            Map<String, String> dataMap = sealRequest(payEntity, 2);
            String sign = generatorSign(dataMap);
            dataMap.put("pay_md5sign", sign);//签名字段
            dataMap.put("pay_productname", "TOP-UP");//商品名称
            logger.info("[PA]平安支付  扫码支付  请求参数：" + dataMap);
            String response = HttpUtils.generatorForm(dataMap, payUrl);
            if (StringUtils.isBlank(response)) {
                return PayResponse.error("扫码支付HTTP 请求 返回为空");
            }
            logger.info("[PA]平安支付  扫码支付HTTP 请求 返回参数" + response);
            return PayResponse.sm_form(payEntity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[PA]平安支付  扫码支付 异常" + e.getMessage());
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
        logger.info("[PA]平安支付  回调验签======================start================================");

        String sign = generatorSign(data);
        String sourceSign = data.remove("sign");
        if (sign.equals(sourceSign)) {
            return ret__success;
        }
        logger.info("[PA]平安支付  回调验签失败：sign=" + sign + "，sourceSign=" + sourceSign);
        return ret__failed;
    }

    /**
     * 生成签名
     *
     * @param data
     * @return
     */
    public String generatorSign(Map<String, String> data) {
        logger.info("[PA]平安支付  生成签名======================start================================");
        try {
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = sortMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = sortMap.get(key);
                if (StringUtils.isBlank(value) || "sign".equals(key)) {
                    continue;
                }
                sb.append(key).append("=").append(value).append("&");
            }
            sb.append("key=").append(md5key);
            logger.info("[PA]平安支付  生成签名的参数" + sb.toString());
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString());
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[PA]平安支付  签名生成异常");
            return ret__failed;
        }
    }

    /**
     * 参数组装
     *
     * @param entity
     * @return
     */
    public Map<String, String> sealRequest(PayEntity entity, int type) {
        logger.info("[PA]平安支付  参数组装======================start================================");
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(entity.getAmount());
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        dataMap.put("pay_memberid", sid);//商户ID
        dataMap.put("pay_orderid", entity.getOrderNo());//订单号
        dataMap.put("pay_amount", amount);//金额
        dataMap.put("pay_applydate", time);//订单提交时间
        if (1 == type) {
            dataMap.put("pay_bankcode", "925");//网银通道编号
        } else {
            dataMap.put("pay_bankcode", entity.getPayCode());//通道编号
        }
        dataMap.put("pay_notifyurl", notifyUrl);//服务端返回地址
        dataMap.put("pay_callbackurl", entity.getRefererUrl());//页面返回地址

        return dataMap;
    }
}
