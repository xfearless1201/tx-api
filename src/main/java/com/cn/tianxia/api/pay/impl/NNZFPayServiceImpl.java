/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    NNZFPayServiceImpl.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:     Administrator 
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年02月24日 10:44 
 *
 *    Revision: 
 *
 *    2019/2/24 10:44 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.pay.*;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  * @ClassName NNZFPayServiceImpl
 *  * @Description TODO(牛牛支付)
 *  * @Author Roman
 *  * @Date 2019年02月24日 10:44
 *  * @Version 1.0.0
 *  
 **/

public class NNZFPayServiceImpl extends PayAbstractBaseService implements PayService {


    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(NNZFPayServiceImpl.class);

    private static final String ret__failed = "fail";

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
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 密钥
     */
    private String key;

    /**
     * 订单查询地址
     */
    private String queryUrl;


    /**
     * 构造器，初始化参数
     */
    public NNZFPayServiceImpl() {
    }

    public NNZFPayServiceImpl(Map<String, String> data) {
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
    public JSONObject smPay(PayEntity payEntity) {
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);

            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);

            logger.info("[NNZF]牛牛支付扫码支付请求参数报文:{}", data);
            String response = HttpUtils.toPostForm(data, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.error("[NNZF]牛牛支付下单失败：生成请求form为空");
                PayResponse.error("[NNZF]牛牛支付下单失败：生成请求form为空");
            }
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[NNZF]牛牛支付扫码支付发起HTTP请求响应结果:{}", jsonObject);
            if (jsonObject.containsKey("code") && "0".equals(jsonObject.getString("code"))) {
                //下单成功
                String payurl = jsonObject.getString("http_url");

                return PayResponse.sm_link(payEntity, payurl, "扫码支付下单成功");
            }
            return PayResponse.error("下单失败:" + jsonObject.getString("msg"));

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[NNZF]牛牛支付扫码支付下单失败" + e.getMessage());
        }
    }

    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[NNZF]牛牛支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.get("sign");
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data);
            logger.info("[NNZF]牛牛支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[NNZF]牛牛支付生成加密串异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[NNZF]牛牛支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            String orderNo = entity.getOrderNo();

//            login_id	商户号	INT	否	签约牛牛搬运的唯一商户编号
            dataMap.put("login_id", mchId);

//            create_time	请求时间	String	否	格式：unix_timestamp
            dataMap.put("create_time", String.valueOf(System.currentTimeMillis() / 1000L));

//            create_ip	客户端IP	String(16)	否	不是商户请求的服务器IP,指的是商户的客户IPv4地址
            dataMap.put("create_ip", entity.getIp());

//            nonce	随机数	INT(11)	否
            dataMap.put("nonce", RandomUtils.generateNumberStr(10));

//            sign_type	签名类型	string	否	目前只支持MD5
            dataMap.put("sign_type", "MD5");

//            pay_type	支付类型	String(16)	否	见附件“支付类型“
            dataMap.put("pay_type", entity.getPayCode());

//            order_type	订单类型	Int(1)	否	1为充值订单 2提现订单
            dataMap.put("order_type", "1");

//            order_sn	商户订单编号	String(32)	否	由商户内部生成的唯一订单编号, 最长不超过32字符只能由数字或字母组成
            dataMap.put("order_sn", orderNo);

//            amount	交易金额	Double(10,2)	否	单位：元，两位小数,不能小于1.0，具体金额上限和下限根据实际情况制定
            dataMap.put("amount", amount);

//            send_currency	货币类型	String(8)	否	客户打款币种，目前只支持人民币：cny
            dataMap.put("send_currency", "cny");

//            recv_currency	货币类型	String(8)	否	商户接收币种，目前只支持人民币：cny
            dataMap.put("recv_currency", "cny");

//            extra	附加字段	String(128)	可空
            dataMap.put("extra", "TOP-UP");

//            notify_url	后台通知回调URL	String(128)	否	需要带上http://或https://
            dataMap.put("notify_url", notifyUrl);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[NNZF]牛牛支付封装请求参数异常:" + e.getMessage());
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
        logger.info("[NNZF]牛牛支付生成支付签名串开始==================START========================");
        try {

//          根据参数名称（sign这个参数除外）将所有请求参数按照字母先后顺序排序: key=value .... key= value 
//          例如：将foo=1,bar=2,baz=3 排序为bar=2,baz=3,foo=1，参数名和参数值链接后，得到拼装字符串bar=2&baz=3&foo=1。
//          系统支持加密方式:MD5。将api_secret=API_SECRET 拼接到参数字符串尾部，并进行md5加密后。 签名参数(sign)不用加入签名中。
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
            sb.append("api_secret=").append(key);
            //生成待签名串
            String singStr = sb.toString();
            logger.info("[NNZF]牛牛支付生成待签名串:" + singStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
            logger.info("[NNZF]牛牛支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[NNZF]牛牛支付生成支付签名串异常:" + e.getMessage());
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

        logger.info("[NNZF]牛牛支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签，从配置中获取
        this.key = config.getString("key");
        this.queryUrl = config.getString("queryUrl");
        this.mchId = config.getString("mch_id");

        boolean verifyRequest = verifyCallback(infoMap);

        // 第三方订单号
        String tradeNo = infoMap.get("order_id");
        //调用查询接口查询订单信息
        boolean orderStatus = getOrderStatus(tradeNo);
        if (!orderStatus) {
            return ret__failed;
        }

        // 平台订单号
        String orderNo = infoMap.get("order_sn");

        //订单状态
        String tradeStatus = infoMap.get("pay_state");
        // 表示成功状态
        String tTradeStatus = "1";
        //实际支付金额
        String orderAmount = infoMap.get("amount");
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
        processNotifyVO.setPayment("NNZF");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }

    /**
     * 功能描述:查询订单
     *
     * @param orderNo 订单号
     * @return: boolean
     **/
    private boolean getOrderStatus(String orderNo) {
        try {
            //封装请求参数
            Map<String, String> map = new HashMap<>();

//            login_id	商户号	INT	否	签约牛牛搬运的唯一商户编号
            map.put("login_id", mchId);

//            create_time	请求时间	String	否	格式：unix_timestamp
            map.put("create_time", String.valueOf(System.currentTimeMillis() / 1000L));

//            nonce	随机数	INT(11)	否
            map.put("nonce", RandomUtils.generateNumberStr(10));
//            sign_type	签名类型	string	否	目前只支持MD5
            map.put("sign_type", "MD5");

//            order_id	牛牛平台的订单号	Int(11)	否	商户必须在创建订单时保存这个id,以便今后查询用
            map.put("order_id", orderNo);

//            sign	签名	String	否	签名信息.签名方法与创建订单时的方法是一样的
            map.put("sign", generatorSign(map));

            logger.info("[NNZF]牛牛支付订单查询接口请求参数{}", map);
            //发送请求
            String response = HttpUtils.toPostForm(map, queryUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[NNZF]牛牛支付订单查询接口响应信息{}", respJson);
            if (respJson.containsKey("code") && "0".equals(respJson.getString("code"))) {
                if ("1".equals(respJson.getString("order_state"))) {
                    logger.info("[NNZF]牛牛支付订单查询成功,订单" + respJson.getString("order_sn") + "已支付。");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[NNZF]牛牛支付订单查询异常");
            return false;
        }
    }
}
