/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    NYPayServiceImpl.java 
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
 *    Create at:   2019年03月24日 10:36 
 *
 *    Revision: 
 *
 *    2019/3/24 10:36 
 *        - first revision 
 *
 *****************************************************************/

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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  * @ClassName NYPayServiceImpl
 *  * @Description TODO(南阳支付)
 *  * @Author Roman
 *  * @Date 2019年03月24日 10:36
 *  * @Version 1.0.0
 *  
 **/

public class NYPayServiceImpl extends PayAbstractBaseService implements PayService {


    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(NYPayServiceImpl.class);

    private static final String RET_FAILED = "fail";

    private static final String RET_SUCCESS = "SUCCESS";

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
    public NYPayServiceImpl() {
    }

    public NYPayServiceImpl(Map<String, String> data) {
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
    public JSONObject wyPay(PayEntity entity) {
        logger.info("[NYPAY]南阳支付网银支付开始============START======================");
        try {
            //获取请求参数
            Map<String, String> data = sealRequest(entity);
            logger.info("[NYPAY]南阳支付网银支付请求参数{}", data);
            //发送请求
            String response = HttpUtils.toPostForm(data, payUrl);

            logger.info("[NYPAY]南阳支付网银支付响应结果{}", response);
            if (StringUtils.isBlank(response)) {
                logger.info("[NYPAY]南阳支付网银支付下单失败，无响应结果");
                return PayResponse.error("[NYPAY]南阳支付网银支付下单失败，无响应结果");
            }
            return PayResponse.wy_form(entity.getPayUrl(), response);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[NYPAY]南阳支付网银支付下单异常" + e.getMessage());
        }

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
            logger.info("[NYPAY]南阳支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));

            String response = HttpUtils.generatorForm(data, payUrl);
            logger.info("[NYPAY]南阳支付扫码支付发起HTTP请求响应结果:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.error("[NYPAY]南阳支付扫码支付下单失败，无响应结果");
                PayResponse.error("[NYPAY]南阳支付扫码支付下单失败，无响应结果");
            }
            return PayResponse.sm_form(entity, response, "扫码支付下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[NYPAY]南阳支付扫码支付下单异常" + e.getMessage());
        }
    }

    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[NYPAY]南阳支付回调验签开始==============START===========");
        try {

            //获取回调通知原签名串
            String sourceSign = new String(data.get("sign").getBytes(), "UTF-8");
            //生成验签签名串
            String sign = generatorSign(data, 2);
            logger.info("[NYPAY]南阳支付回调生成签名串：{}--源签名串：{}", sign , sourceSign );
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[NYPAY]南阳支付生成加密串异常:{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[NYPAY]南阳支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

//            pay_memberid 商户号 是 是 平台分配商户号
            dataMap.put("pay_memberid", mchId);

//            pay_orderid 订单号 是 是 上送订单号唯一, 字符长度 20
            dataMap.put("pay_orderid", orderNo);

//            pay_bankcode 银行编码 是 是 参考后续说明
            dataMap.put("pay_bankcode", entity.getPayCode());

//            pay_notifyurl 服务端通知 是 是 服务端返回地址（. POST 返回数据）
            dataMap.put("pay_notifyurl", notifyUrl);

//            pay_amount 订单金额 是 是 商品金额（单位元）
            dataMap.put("pay_amount", amount);

//          生成签名串
            String sign = generatorSign(dataMap, 1);
//            pay_version 系统接口版本 是 否 固定值:vb1.0
            dataMap.put("pay_version", "vb1.0");

//            pay_applydate 提交时间 是 否 时间格式(yyyyMMddHHmmss)：20161226181818
            dataMap.put("pay_applydate", orderTime);

//            pay_md5sign MD5 签名 是 否 请看 MD5 签名字段格式
            dataMap.put("pay_md5sign", sign);
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[NYPAY]南阳支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param data
     * @param type 其他:订单查询  1:支付  2:回调
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data, int type) throws Exception {
        logger.info("[NYPAY]南阳支付生成支付签名串开始==================START========================");
        try {

            StringBuffer sb = new StringBuffer();
            if (type == 1) {
/*              stringSignTemp="pay_memberid=pay_memberid&pay_bankcode=pay_bankcode&
              pay_amount=pay_amount&pay_orderid=pay_orderid&pay_notifyurl=pay_notifyurl"+key         */
                sb.append("pay_memberid=").append(data.get("pay_memberid")).append("&")
                        .append("pay_bankcode=").append(data.get("pay_bankcode")).append("&")
                        .append("pay_amount=").append(data.get("pay_amount")).append("&")
                        .append("pay_orderid=").append(data.get("pay_orderid")).append("&")
                        .append("pay_notifyurl=").append(data.get("pay_notifyurl"))
                        .append(key);
            } else if (type == 2) {
//                stringSignTemp="orderid=orderid&opstate=opstate&ovalue=ovalue"+key
                sb.append("orderid=").append(data.get("orderid")).append("&")
                        .append("opstate=").append(data.get("opstate")).append("&")
                        .append("ovalue=").append(data.get("ovalue"))
                        .append(key);
            } else {
//                orderid={}&parter={}key
                sb.append("orderid=").append(data.get("orderid")).append("&")
                        .append("parter=").append(mchId)
                        .append(key);
            }
            //生成待签名串
            String singStr = sb.toString();
            logger.info("[NYPAY]南阳支付生成待签名串:" + singStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
            logger.info("[NYPAY]南阳支付生成加密签名串:" + sign);
            return new String(sign.getBytes(), "GB2312");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[NYPAY]南阳支付生成支付签名串异常:" + e.getMessage());
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

        logger.info("[NYPAY]南阳支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return RET_FAILED;
        }
        //参数验签，从配置中获取
        this.key = config.getString("key");
        this.queryUrl = config.getString("queryUrl");
        this.mchId = config.getString("mch_id");

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("orderid");

        //调用查询接口查询订单信息
        boolean orderStatus = getOrderStatus(orderNo);
        if (!orderStatus) {
            logger.info(orderNo + "此订单尚未支付成功！");
            return RET_FAILED;
        }

        // 第三方订单号
        String tradeNo = infoMap.get("sysorderid");
        //订单状态
        String tradeStatus = infoMap.get("opstate");
        // 表示成功状态
        String tTradeStatus = "0";
        //实际支付金额
        String orderAmount = infoMap.get("ovalue");
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
        processNotifyVO.setRealAmount(realAmount);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("NY");
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

//            商户订单号	orderid	Y	需查询的商户系统订单号
            map.put("orderid", orderNo);

//            商户ID	parter	Y	商户id，由分配。
            map.put("parter", mchId);

//            sign	签名	String	否	签名信息.签名方法与创建订单时的方法是一样的
            map.put("sign", generatorSign(map, 0));

            logger.info("[NYPAY]南阳支付订单查询接口请求参数{}", JSONObject.fromObject(map));
            //发送请求
            String response = getStatus(map, queryUrl);
            if (StringUtils.isBlank(response)) {
                logger.error("[NYPAY]南阳支付订单" + orderNo + "查询失败，无响应结果");
                PayResponse.error("[NYPAY]南阳支付订单查询失败，无响应结果");
            }

            Map<String, Object> respMap = transStringToMap(Objects.requireNonNull(response), "&", "=");
            logger.info("[NYPAY]南阳支付订单" + orderNo + "查询接口响应信息{}", JSONObject.fromObject(respMap));
            //解析响应参数
            if (respMap.containsKey("opstate") && "0".equals(respMap.get("opstate"))) {
                logger.info("[NYPAY]南阳支付订单查询成功,订单" + orderNo + "已支付。");
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[NYPAY]南阳支付订单查询异常");
            return false;
        }
    }

    /**
     * get请求查询订单信息
     **/
    private String getStatus(Map<String, String> data, String url) throws Exception {

        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            String params = "";
            if (data != null) {
                StringBuffer sb = new StringBuffer();
                Iterator<String> iterator = data.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String val = data.get(key);
                    if (org.apache.commons.lang.StringUtils.isBlank(val)) {
                        val = "";
                    }
                    sb.append("&").append(key).append("=").append(val);
                }

                params = sb.toString().replaceFirst("&", "?");
            }
            HttpGet httpGet = new HttpGet(url + params);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "GB2312"));
                StringBuffer sb = new StringBuffer();
                String content = null;
                while ((content = reader.readLine()) != null) {
                    sb.append(content);
                }
                return sb.toString();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            if (httpclient != null) {
                httpclient.close();
            }
        }
    }

    private static PoolingHttpClientConnectionManager createConnectionManager() throws Exception {
        TrustManager tm = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }
        };
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{tm}, null);

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context,
                NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        return connectionManager;
    }

    /**
     * 将字符串转为map
     **/
    private static Map<String, Object> transStringToMap(String mapString, String separator, String pairSeparator) {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] fSplit = mapString.split(separator);
        for (String aFSplit : fSplit) {
            if (aFSplit == null || aFSplit.length() == 0) {
                continue;
            }
            String[] sSplit = aFSplit.split(pairSeparator);
            String value = aFSplit.substring(aFSplit.indexOf('=') + 1, aFSplit.length());
            map.put(sSplit[0], value);
        }
        return map;
    }


}

