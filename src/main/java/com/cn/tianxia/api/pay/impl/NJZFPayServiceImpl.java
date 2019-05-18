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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author
 */
public class NJZFPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 回调失败响应信息
     */
    private static final String ret__failed = "fail";
    /**
     * 回调成功响应信息
     */
    private static final String ret__success = "OK";
    private final Logger logger = LoggerFactory.getLogger(NJZFPayServiceImpl.class);
    /**
     * 支付地址
     */
    private String wyPayUrl;
    /**
     * 支付地址
     */
    private String searchOrderUrl;
    /**
     * 商户编号
     */
    private String payMemberid;
    /**
     * 商户接收支付成功数据的地址
     */
    private String payNotifyUrl;
    /**
     * 商户密钥
     */
    private String md5Key;
    /**
     * 版本号
     */
    private String version;

    public NJZFPayServiceImpl() {
    }

    public NJZFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("wyPayUrl")) {
                this.wyPayUrl = data.get("wyPayUrl");
            }
            if (data.containsKey("searchOrderUrl")) {
                this.searchOrderUrl = data.get("searchOrderUrl");
            }
            if (data.containsKey("payMemberid")) {
                this.payMemberid = data.get("payMemberid");
            }
            if (data.containsKey("payNotifyUrl")) {
                this.payNotifyUrl = data.get("payNotifyUrl");
            }
            if (data.containsKey("md5Key")) {
                this.md5Key = data.get("md5Key");
            }
            if (data.containsKey("version")) {
                this.version = data.get("version");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[NJZF]诺捷网银支付开始======================START==================");
        try {
            //封装请求参数
            Map<String, String> data = sealRequest(payEntity, "0");
            logger.info("[NJZF]诺捷网银支付请求参数:" + JSONObject.fromObject(data));
            //生成请求表单
            String resStr = HttpUtils.generatorForm(data, wyPayUrl);
            logger.info("[NJZF]诺捷网银支付响应信息:" + resStr);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[NJZF]诺捷网银支付发起HTTP请求无响应结果");
                return PayResponse.error("[NJZF]诺捷网银支付发起HTTP请求无响应结果");
            }
            return PayResponse.wy_form(payEntity.getPayUrl(), resStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[NJZF]诺捷网银支付生成异常:" + e.getMessage());
            return PayResponse.error("[NJZF]诺捷网银支付下单失败");
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    /**
     * @param
     * @param
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    public Map<String, String> sealRequest(PayEntity payEntity, String type) throws Exception {
        DecimalFormat df = new DecimalFormat("0");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Map<String, String> data = new TreeMap<>();
        if ("0".equals(type)) {
            data.put("Version", version);//版本号
            data.put("MerchantCode", payMemberid);//商户号
            data.put("OrderId", payEntity.getOrderNo());//订单号
            data.put("Amount", df.format(payEntity.getAmount()));//金额
            data.put("AsyNotifyUrl", payNotifyUrl);//异步回调地址
            data.put("SynNotifyUrl", payNotifyUrl);//同步回调地址
            data.put("OrderDate", dateFormat.format(new Date()));//订单时间
            data.put("TradeIp", payEntity.getIp());//客户Ip
            data.put("PayCode", payEntity.getPayCode());//接口编码
            data.put("SignValue", generatorSign(data, "0"));//签名串
        }
        if ("1".equals(type)) {
            Map<String, String> param = new TreeMap<>();
            data.put("Encrypt", "1");
            data.put("MerchantCode", payMemberid);
            data.put("OrderId", payEntity.getOrderNo());
            data.put("sign", generatorSign(data, "1"));
            JSONObject jsonData = JSONObject.fromObject(data);
            logger.info("[NJZF]诺捷支付回调查询订单请求参数data{}", jsonData);
            param.put("data", URLEncoder.encode(jsonData.toString(), "UTF-8"));
            param.put("Encryptkey", "1");
            param.put("Merchantaccount", payMemberid);
            return param;
        }
        return data;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(Map<String, String> data, String type) throws Exception {
        StringBuffer sb = new StringBuffer();
        if ("0".equals(type)) {
            sb.append("Version=[").append(data.get("Version")).append("]");
            sb.append("MerchantCode=[").append(data.get("MerchantCode")).append("]");
            sb.append("OrderId=[").append(data.get("OrderId")).append("]");
            sb.append("Amount=[").append(data.get("Amount")).append("]");
            sb.append("AsyNotifyUrl=[").append(data.get("AsyNotifyUrl")).append("]");
            sb.append("SynNotifyUrl=[").append(data.get("SynNotifyUrl")).append("]");
            sb.append("OrderDate=[").append(data.get("OrderDate")).append("]");
            sb.append("TradeIp=[").append(data.get("TradeIp")).append("]");
            sb.append("PayCode=[").append(data.get("PayCode")).append("]");
            sb.append("TokenKey=[").append(md5Key).append("]");
        }
        if ("1".equals(type)) {
            sb.append(data.get("Encrypt")).append(data.get("MerchantCode"));
            sb.append(data.get("OrderId")).append(md5Key);
        }
        if ("2".equals(type)) {
            sb.append("Version=[").append(data.get("Version")).append("]");
            sb.append("MerchantCode=[").append(data.get("MerchantCode")).append("]");
            sb.append("OrderId=[").append(data.get("OrderId")).append("]");
            sb.append("OrderDate=[").append(data.get("OrderDate")).append("]");
            sb.append("TradeIp=[").append(data.get("TradeIp")).append("]");
            sb.append("SerialNo=[").append(data.get("SerialNo")).append("]");
            sb.append("Amount=[").append(data.get("Amount")).append("]");
            sb.append("PayCode=[").append(data.get("PayCode")).append("]");
            sb.append("State=[").append(data.get("State")).append("]");
            sb.append("FinishTime=[").append(data.get("FinishTime")).append("]");
            sb.append("TokenKey=[").append(md5Key).append("]");
        }
        String signStr = sb.toString();
        //生成待签名串
        logger.info("[NJZF]诺捷支付生成待签名串:{}", signStr);
        String sign = MD5Utils.md5toUpCase_32Bit(signStr);
        logger.info("[NJZF]诺捷支付生成加密签名串:{}", sign);
        return sign;
    }

    public boolean serchOrder(String orderNo) {
        try {
            PayEntity payEntity = new PayEntity();
            payEntity.setOrderNo(orderNo);
            Map<String, String> param = sealRequest(payEntity, "1");
            logger.info("[NJZF]诺捷支付回调查询订单{}请求参数:{}", orderNo, JSONObject.fromObject(param));
            String resStr = HttpUtils.toPostForm(param, searchOrderUrl);
            logger.info("[NJZF]诺捷支付回调查询订单{}响应信息:{}", orderNo, resStr);

            if (StringUtils.isBlank(resStr)) {
                logger.info("[NJZF]诺捷支付回调订单查询发起HTTP请求无响应,订单号{}", orderNo);
                return false;
            }
            JSONObject resJson = JSONObject.fromObject(resStr);
            resStr = URLDecoder.decode(resJson.getString("data"), "UTF-8");
            logger.info("[NJZF]诺捷支付回调查询订单{}解码后的响应信息:{}", orderNo, resStr);
            resJson = JSONObject.fromObject(resStr);
            if (!"1".equals(resJson.getString("Status"))) {
                logger.info("[NJZF]诺捷支付回调订单{}查询错误信息{}", orderNo, resJson.getString("ErrInfo"));
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[NJZF]诺捷支付回调订单{}查询异常{}", orderNo, e.getMessage());
            return false;
        }

    }

    private boolean verifyCallback(Map<String, String> data) {
        try {
            String sourceSign = data.remove("SignValue");
            String sign = generatorSign(data, "2");
            logger.info("[NJZF]诺捷支付回调生成签名串" + sign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[NJZF]诺捷支付回调生成签名串异常" + e.getMessage());
            return false;
        }
    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);  //获取回调请求参数
        logger.info("[NJZF]诺捷支付回调请求参数：" + JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("NJZFNotify获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.md5Key = config.getString("md5Key");//从配置中获取
        this.payMemberid = config.getString("payMemberid");
        this.searchOrderUrl = config.getString("searchOrderUrl");

        String order_amount = infoMap.get("Amount");//单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info("NJZFNotify获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount);
        String order_no = infoMap.get("OrderId");// 平台订单号
        String trade_no = infoMap.get("SerialNo");// 第三方订单号
        String trade_status = infoMap.get("State");//订单状态:00为成功
        String t_trade_status = trade_status;// 表示成功状态

        boolean result = serchOrder(order_no);
        if (!result) {
            logger.info("[NJZF]诺捷支付回调订单{}查询失败", order_no);
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(infoMap);

        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setRet__success(ret__success);    //成功返回
        processNotifyVO.setRet__failed(ret__failed);      //失败返回
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);    //支付状态
        processNotifyVO.setT_trade_status(t_trade_status);     //第三方成功状态
        processNotifyVO.setRealAmount(realAmount);
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());    //回调参数
        processNotifyVO.setPayment("NJZF");
        processNotifyVO.setConfig(config);
        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}
