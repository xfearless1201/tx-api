package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.JSONUtils;
import com.cn.tianxia.api.utils.jczf.RSA;
import com.cn.tianxia.api.utils.pay.HttpUtils;
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
import java.util.Map;

/**
 *  * @ClassName JCZFPayServiceImpl
 *  * @Description TODO(新金彩支付)
 * * @author zw
 * * @Date 2018年7月23日 下午4:24:29
 * * @version 1.0.0
 *  * @Author Roman
 *  * @Date 2019年05月16日 12:25
 *  * @Version 2.0.0
 *  
 **/
public class JCZFPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(JCZFPayServiceImpl.class);

    private static final String ret__failed = "Notify Is Failed";

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
     * 私钥
     */
    private String secret;

    /**
     * 订单查询地址
     */
    private String queryOrderUrl;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 构造器，初始化参数
     */
    public JCZFPayServiceImpl() {
    }

    public JCZFPayServiceImpl(Map<String, String> data) {
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
            if (data.containsKey("publicKey")) {
                this.publicKey = data.get("publicKey");
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
            logger.info("[JCZF]新金彩支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));

            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[JCZF]新金彩支付扫码支付发起HTTP请求无响应");
                return PayResponse.error("[JCZF]新金彩支付扫码支付发起HTTP请求无响应");
            }
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[JCZF]新金彩支付扫码支付响应参数报文:{}", jsonObject);

            if (jsonObject.containsKey("status") && "1".equals(jsonObject.getString("status"))
                    || "2".equals(jsonObject.getString("status"))) {
                String payUrl = jsonObject.getString("content");
                if (StringUtils.isBlank(entity.getMobile())) {
                    return PayResponse.sm_qrcode(entity, payUrl, "下单成功");
                }
                return PayResponse.sm_link(entity, payUrl, "下单成功");
            }
            return PayResponse.error("下单失败" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[JCZF]新金彩支付扫码支付异常" + e.getMessage());
        }
    }

    /**
     * @param
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[JCZF]新金彩支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

//            商户编号	company_oid	是	int	商户编号是商户在平台上开设的商户号码
            dataMap.put("company_oid", merchId);

//            商户唯一订单号	order_id	是	Varchar(32)	商户系统唯一订单号，建议加点前缀如商户号，避免和其他商户都使用时间戳，导致后续跟踪问题不便
            dataMap.put("order_id", orderNo);

//            订单名称	order_name	是	Varchar(64)
            dataMap.put("order_name", "top_Up");

//            交易金额	amount	是	int	该笔订单的资金总额，单位：分。大于手续费金额的整数。如：1000
            dataMap.put("amount", amount);

//            服务器异步通知地址	notify_url	是	Varchar (128)	用户支付成功后通知商户服务端的地址。
            dataMap.put("notify_url", notifyUrl);

//            支付方式	pay_type	是	Varchar(1)
            dataMap.put("pay_type", entity.getPayCode());

            //生成待签名串
//            String sign = generatorSign(dataMap);
//            签名	sign	是	Varchar(255)	RSA加密签名，见安全签名机制
            dataMap.put("sign", RSA.sign(dataMap, secret));

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JCZF]新金彩支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * 功能描述:回调验签
     *
     * @param
     * @return: boolean
     **/
    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[JCZF]新金彩支付回调验签开始=========================START===========================");
        try {
            RSA.checkSign(data, publicKey);
            logger.info("[JCZF]新金彩支付回调验签成功");
            return true;
        } catch (Exception e) {
            logger.info("[JCZF]新金彩支付回调验签失败");
            return false;
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
        //参数验签，从配置中获取
        this.secret = config.getString("secret");
        this.merchId = config.getString("merchId");
        this.queryOrderUrl = config.getString("queryOrderUrl");
        this.publicKey = config.getString("publicKey");
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[JCZF]新金彩支付回调请求参数报文:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("order_id");
        // 第三方订单号
        String tradeNo = infoMap.get("order_abc");
        //订单状态
        String tradeStatus = String.valueOf(infoMap.get("status"));
        // 表示成功状态
        String tTradeStatus = "1";
        //实际支付金额
        String orderAmount = String.valueOf(infoMap.get("amount"));
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        //查询订单信息
        boolean orderStatus = getOrderStatus(orderNo, tradeNo);
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
        processNotifyVO.setRealAmount(realAmount / 100);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("JCZF");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }

    /**
     * 功能描述:查询订单状态
     *
     * @param orderNo 订单号
     * @return: boolean
     **/
    private boolean getOrderStatus(String orderNo, String tradeNo) {
        try {
            //封装请求参数
            Map<String, String> map = new HashMap<>();
            map.put("company_oid", merchId);
            map.put("order_id", orderNo);
            map.put("order_abc", tradeNo);
            map.put("sign", RSA.sign(map, secret));

            logger.info("[JCZF]新金彩支付回调订单查询接口请求参数{}", JSONObject.fromObject(map));
            //发送请求
            String response = HttpUtils.toPostForm(map, queryOrderUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[JCZF]新金彩支付回调订单查询接口响应信息{}", respJson);
            if (JSONUtils.compare(respJson, "status", "1")) {
                if (JSONUtils.compare(respJson, "order_status", "1")) {
                    logger.info("[JCZF]新金彩支付回调订单查询成功,订单" + orderNo + "已支付。");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JCZF]新金彩支付回调订单查询异常");
            return false;
        }
    }
}






