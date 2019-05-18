/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    YOUPayServiceImpl.java 
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
 *    Create at:   2019年04月11日 12:06 
 *
 *    Revision: 
 *
 *    2019/4/11 12:06 
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
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 *  * @ClassName YOUPayServiceImpl
 *  * @Description TODO(友付支付)
 *  * @Author Roman
 *  * @Date 2019年04月11日 12:06
 *  * @Version 1.0.0
 *  
 **/

public class YOUPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(YOUPayServiceImpl.class);

    private static final String ret__failed = "false";

    private static final String ret__success = "true";

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
     * 密钥
     */
    private String key;

    /**
     * 存款方式
     */
    private String aliType;
    private String wxType;
    private String ylType;


    /**
     * 构造器，初始化参数
     */
    public YOUPayServiceImpl() {
    }

    public YOUPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merchId")) {
                this.mchId = data.get("merchId");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("secret")) {
                this.key = data.get("secret");
            }
            if (data.containsKey("queryOrderUrl")) {
                this.queryUrl = data.get("queryOrderUrl");
            }
            if (data.containsKey("aliType")) {
                this.aliType = data.get("aliType");
            }
            if (data.containsKey("wxType")) {
                this.wxType = data.get("wxType");
            }
            if (data.containsKey("ylType")) {
                this.ylType = data.get("ylType");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity entity) {
        logger.info("[YOU]友付支付网银支付开始============START======================");
        try {
            //获取请求参数
            Map<String, String> data = sealRequest(entity);
            logger.info("[YOU]友付支付网银支付请求参数{}", data);
            //发送请求
            String response = HttpUtils.generatorForm(data, payUrl);

            logger.info("[YOU]友付支付网银支付响应结果{}", response);
            if (StringUtils.isBlank(response)) {
                logger.info("[YOU]友付支付网银支付发起请求无响应结果");
                return PayResponse.error("[YOU]友付支付网银支付发起请求无响应结果");
            }
            return PayResponse.wy_form(entity.getPayUrl(), response);
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[YOU]友付支付网银支付下单失败" + e.getMessage());
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
            logger.info("[YOU]友付支付扫码支付请求参数:{}", JSONObject.fromObject(data));

            String resStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[YOU]友付支付扫码支付响应信息：{}", resStr);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[YOU]友付支付扫码支付下单失败，无响应结果");
                return PayResponse.error("[YOU]友付支付扫码支付下单失败，无响应结果");
            }
            return PayResponse.sm_form(entity, resStr, "扫码支付下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[YOU]友付支付扫码支付下单异常" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[YOU]友付支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();
            dataMap.put("cid", mchId);//商户号
            dataMap.put("uid", entity.getuId());//用户id
            dataMap.put("time", String.valueOf(System.currentTimeMillis() / 1000));//Unix时间戳=时间戳/1000，即时间戳=Unix时间戳*1000
            dataMap.put("amount", amount);//支付金额
            dataMap.put("order_id", orderNo);//订单号
            dataMap.put("ip", entity.getIp());//ip地址
            dataMap.put("sign", generatorSign(dataMap, 1));
            if ("3".equals(entity.getPayType())) {//支付宝
                dataMap.put("type", aliType);
            } else if ("1".equals(entity.getPayType())) {//网银
                dataMap.put("type", "online");
            } else if ("5".equals(entity.getPayType())){//银联扫码
                dataMap.put("type", ylType);
            }else {
                dataMap.put("type", wxType); //微信
            }
            dataMap.put("tflag", entity.getPayCode());//存款银行，可选，如果没有传入 type 参数，则此参数无效。


            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YOU]友付支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param type 1:   支付     2:  回调   3:查询
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data, int type) throws Exception {
        try {
            StringBuffer sb = new StringBuffer();
            if (type == 1) {
                sb.append("cid=").append(data.get("cid")).append("&");
                sb.append("uid=").append(data.get("uid")).append("&");
                sb.append("time=").append(data.get("time")).append("&");
                sb.append("amount=").append(data.get("amount")).append("&");
                sb.append("order_id=").append(data.get("order_id")).append("&");
                sb.append("ip=").append(data.get("ip"));
            } else if (type == 2) {
//              签名：  order_id=xxx&amount=xxx&verified_time=xxx
                sb.append("order_id=").append(data.get("order_id")).append("&");
                sb.append("amount=").append(data.get("amount")).append("&");
                sb.append("verified_time=").append(data.get("verified_time"));
            } else {
                JSONObject jsonObject = JSONObject.fromObject(data);
                sb.append(jsonObject.toString());
            }
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[YOU]友付支付生成待签名串:" + signStr);
            //生成加密串
            SecretKeySpec signKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signKey);
            byte[] rawHmac = mac.doFinal(signStr.getBytes());
            Base64.Encoder encoder = Base64.getEncoder();
            String sign = encoder.encodeToString(rawHmac);
            logger.info("[YOU]友付支付生成加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YOU]友付支付生成支付签名串异常:" + e.getMessage());
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
        logger.info("[YOU]友付支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("qsign");
        logger.info("[YOU]友付支付回调验签获取原签名串:{}", sourceSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data, 2);
            logger.info("[YOU]友付支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YOU]友付支付生成加密串异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
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
            map.put("cid", mchId);
            map.put("order_id", orderNo);
            map.put("time", String.valueOf(System.currentTimeMillis()/1000));

            String sign = generatorSign(map, 3);
            logger.info("[YOU]友付支付订单查询接口请求参数{}", JSONObject.fromObject(map));

            //发送请求
            String response = toPostJsonStr(JSONObject.fromObject(map), queryUrl, sign);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[YOU]友付支付订单查询接口响应信息{}", respJson);
            if (respJson.containsKey("code") && "200".equals(respJson.getString("code"))) {
                if ("verified".equalsIgnoreCase(respJson.getJSONObject("order").getString("status"))) {

                    logger.info("[YOU]友付支付订单查询成功,订单" + orderNo + "已支付。");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YOU]友付支付订单查询异常");
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
        this.key = config.getString("secret");
        this.mchId = config.getString("merchId");
        this.queryUrl = config.getString("queryOrderUrl");

        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[YOU]友付支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }
        //验签
        boolean verifyRequest = verifyCallback(infoMap);

        // 平台商订单号
        String orderNo = infoMap.get("order_id");
        // 支付商订单号
        String tradeNo = "YOU" + System.currentTimeMillis();
        //订单状态
        String tradeStatus = infoMap.get("status");
        // 表示成功状态
        String tTradeStatus = "verified";
        //实际支付金额
        String orderAmount = infoMap.get("amount");
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
        //成功返回
        processNotifyVO.setRet__success(ret__success);
        //失败返回
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(orderNo);
        processNotifyVO.setTrade_no(tradeNo);
        processNotifyVO.setTrade_status(tradeStatus);
        processNotifyVO.setT_trade_status(tTradeStatus);
        processNotifyVO.setRealAmount(realAmount / 100);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("YOU");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }

    /**
     *
     * @Description 发起参数为json类型的post请求
     * @param data
     * @param url
     * @return
     * @throws Exception
     */
    public static String toPostJsonStr(JSONObject data,String url,String sign) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null && !data.isEmpty()){
                StringEntity entity = new StringEntity(data.toString(),"utf-8");//解决中文乱码问题
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type", "application/json");
                httppost.addHeader("Content-Hmac", sign);
                RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
                httppost.setConfig(requestConfig);
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),Consts.UTF_8));
                StringBuffer sb = new StringBuffer();
                String content;
                while((content = reader.readLine()) != null){
                    sb.append(content);
                }
                return sb.toString();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }finally {
            if(httpclient != null){
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
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
            }
        };
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{tm}, null);

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context,
                NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();

        return new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
    }

}


