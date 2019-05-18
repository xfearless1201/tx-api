package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.JSONUtils;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.utils.pay.XTUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName XTPayServiceImpl
 * @Description 信通支付
 * @Date 2018年10月2日 下午4:28:39
 */
public class XTPayServiceImpl extends PayAbstractBaseService implements PayService {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(XTPayServiceImpl.class);
    /**
     * 回调失败响应信息
     */
    private static final String ret__failed = "fail";
    /**
     * 回调成功响应信息
     */
    private static final String ret__success = "success";
    /**
     * 商户号
     */
    private String merchId;
    /**
     * 秘钥
     */
    private String secret;
    /**
     * 回调地址
     */
    private String notifyUrl;
    /**
     * 支付地址
     */
    private String payUrl;
    /**
     * 订单查询地址
     */
    private String queryOrderUrl;

    public XTPayServiceImpl() {
    }

    // 构造器
    public XTPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merchId")) {
                this.merchId = data.get("merchId");
            }
            if (data.containsKey("secret")) {
                this.secret = data.get("secret");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("queryOrderUrl")) {
                this.queryOrderUrl = data.get("queryOrderUrl");
            }
        }
    }

    /**
     * 网银支付
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    /**
     * 扫码支付
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[XT]信通支付扫码支付开始===============START========================");
        try {
            // 生成请求参数
            Map<String, String> data = sealRequest(payEntity);
            logger.info("[XT]信通扫码支付请求参数：{}", JSONObject.fromObject(data));
            String resStr;
            if (StringUtils.isNotBlank(payEntity.getMobile())) {
                resStr = HttpUtils.generatorForm(data, payUrl);
                logger.info("[XT]信通扫码支付响应信息：{}", resStr);
                return PayResponse.sm_form(payEntity, resStr, "下单成功");
            } else {
                resStr = HttpUtils.toPostForm(data, payUrl);
            }
            logger.info("[XT]信通扫码支付响应信息：{}", resStr);
            JSONObject resJson = JSONObject.fromObject(resStr);
            if (JSONUtils.compare(resJson, "status", "0")) {
                return PayResponse.sm_qrcode(payEntity, resJson.getString("payImg"), "下单成功");
            }
            return PayResponse.error("[XT]信通扫码支付失败：" + resJson.getString("Msg"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XT]信通扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[XT]信通扫码支付异常");
        }
    }

    /**
     * 回调
     */
    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    /**
     * @param
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity payEntity) throws Exception {
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());
        Map<String, String> data = new HashMap<>();
        data.put("p0_Cmd", "Buy");// 固定值“Buy”.
        data.put("p1_MerId", merchId);// 商户号
        data.put("p2_Order", payEntity.getOrderNo());//订单号
        data.put("p3_Amt", amount);// 单位:元，精确到分
        data.put("p4_Cur", "CNY");// 交易币种 固定值“CNY”.
        data.put("p5_Pid", "recharge");//商品名称
        data.put("p6_Pcat", "1");// 商品种类.
        data.put("p7_Pdesc", "recharge");//商品描述
        data.put("p8_Url", notifyUrl);//回调地址
        data.put("pa_MP", "XT");//返回时原样返回
        data.put("pd_FrpId", payEntity.getPayCode());//支付通道
        data.put("pr_NeedResponse", "1");//应答机制 固定值 1
        data.put("hmac", generatorSign(data, "0"));// 签名数据
        return data;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名
     */
    private String generatorSign(Map<String, String> data, String type) throws Exception {
        StringBuilder sb = new StringBuilder();
        if ("0".equals(type)) {
            sb.append("p0_Cmd=").append(data.get("p0_Cmd")).append("&");
            sb.append("p1_MerId=").append(data.get("p1_MerId")).append("&");
            sb.append("p2_Order=").append(data.get("p2_Order")).append("&");
            sb.append("p3_Amt=").append(data.get("p3_Amt")).append("&");
            sb.append("p4_Cur=").append(data.get("p4_Cur")).append("&");
            sb.append("p5_Pid=").append(data.get("p5_Pid")).append("&");
            sb.append("p6_Pcat=").append(data.get("p6_Pcat")).append("&");
            sb.append("p7_Pdesc=").append(data.get("p7_Pdesc")).append("&");
            sb.append("p8_Url=").append(data.get("p8_Url")).append("&");
            sb.append("pa_MP=").append(data.get("pa_MP")).append("&");
            sb.append("pd_FrpId=").append(data.get("pd_FrpId")).append("&");
            sb.append("pr_NeedResponse=").append(data.get("pr_NeedResponse"));
        }
        if ("1".equals(type)) {
            // 排序
            Map<String, String> treeMap = new TreeMap<>(data);
            for (String key : treeMap.keySet()) {
                String val = treeMap.get(key);
                if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        if ("2".equals(type)) {
            sb.append(data.get("p1_MerId"));
            sb.append(data.get("r0_Cmd"));
            sb.append(data.get("r1_Code"));
            sb.append(data.get("r2_TrxId"));
            sb.append(data.get("r3_Amt"));
            sb.append(data.get("r4_Cur"));
            sb.append(data.get("r5_Pid"));
            sb.append(data.get("r6_Order"));
            sb.append(data.get("r7_Uid"));
            sb.append(data.get("r8_MP"));
            sb.append(data.get("r9_BType"));
        }
        String signStr = sb.toString();
        logger.info("[XT]信通扫码支付生成待签名串：{}", signStr);
        String sign = XTUtils.hmacSign(signStr, secret);
        logger.info("[XT]信通扫码支付生成签名串：{}", sign);
        return sign;
    }

    /**
     * 订单查询接口
     *
     * @param orderNo
     * @return
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    public boolean serchOrder(String orderNo) {
        try {
            String uuid = UUID.randomUUID().toString();
            Map<String, String> param = new HashMap<>();
            param.put("p1_MerId", merchId);//商户号
            param.put("p2_Order", orderNo);//商户订单号
            param.put("nonce", uuid.replace("-", ""));//随机数 32位
            param.put("sign", generatorSign(param, "1"));
            logger.info("[XT]信通扫码支付回调查询订单{}请求参数：{}", orderNo, JSONObject.fromObject(param));
            String resStr = HttpUtils.toPostForm(param, queryOrderUrl);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[XT]信通扫码支付回调查询订单发起HTTP请求无响应,订单号{}", orderNo);
                return false;
            }
            JSONObject resJson = JSONObject.fromObject(resStr);
            logger.info("[XT]信通扫码支付回调查询订单{}响应信息：{}", orderNo, resJson);
            if (!"success".equals(resJson.getString("status"))) {
                return false;
            }
            if (!"00".equals(resJson.getString("respCode"))) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("XT]信通扫码支付回调查询订单{}异常{}", orderNo, e.getMessage());
            return false;
        }

    }

    /**
     * 回调验签
     *
     * @param data
     * @return
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    private boolean verifyCallback(Map<String, String> data) {
        try {
            String sourceSign = data.get("hmac");
            String sign = generatorSign(data, "2");
            logger.info("[XT]信通扫码支付生成签名串：{}--源签名串：{}", sign, sourceSign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XT]信通扫码支付回调生成签名串异常{}", e.getMessage());
            return false;
        }
    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);  //获取回调请求参数
        logger.info("[XT]信通扫码支付回调请求参数：" + JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("XTNotify获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.secret = config.getString("secret");//从配置中获取
        this.merchId = config.getString("merchId");//从配置中获取
        this.queryOrderUrl = config.getString("queryOrderUrl");//从配置中获取

        String order_amount = infoMap.get("r3_Amt");//单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info("XTNotify获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount);
        String order_no = infoMap.get("r6_Order");// 平台订单号
        String trade_no = infoMap.get("r2_TrxId");// 第三方订单号
        String trade_status = infoMap.get("r1_Code");//订单状态:1为成功
        String t_trade_status = "1";// 表示成功状态

        /*订单查询*/
        if (!serchOrder(order_no)) {
            logger.info("[XT]信通扫码支付回调查询订单{}失败", order_no);
            return ret__failed;
        }
        /*回调验签*/
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
        processNotifyVO.setPayment("XT");
        processNotifyVO.setConfig(config);
        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}
