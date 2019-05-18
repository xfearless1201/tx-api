package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import org.apache.http.message.BasicNameValuePair;
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
import java.util.*;

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName SPPayServiceImpl
 * @Description 7pay支付 渠道：支付宝、微信
 * @Date 2019/4/9 17 59
 **/
public class SPPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(SPPayServiceImpl.class);
    private String memberid;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String md5key;//密钥

    private static String ret__success = "success";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private boolean verifySuccess = true;//回调验签默认状态为true

    public SPPayServiceImpl() {
    }

    public SPPayServiceImpl(Map<String, String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("memberid")){
                this.memberid = data.get("memberid");
            }
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            if(data.containsKey("md5key")){
                this.md5key = data.get("md5key");
            }
        }
    }
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        this.memberid = config.getString("memberid");
        this.notifyUrl = config.getString("notifyUrl");
        this.md5key = config.getString("md5key");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[SP]7pay支付    商户返回信息：" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("ordersn");//第三方订单号，流水号
        String order_no = dataMap.get("out_trade_no");//支付订单号
        String amount = dataMap.get("needAmount");//实际支付金额,以分为单位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[SP]7pay支付      获取的{} 流水单号为空",trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[SP]7pay支付      回调订单金额为空");
            return ret__failed;
        }

        String trade_status = "1";  //第三方支付状态，对方未有些参数
        String t_trade_status = "1";//第三方成功状态


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
        processNotifyVO.setPayment("SP");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[SP]7pay支付    回调验签失败");
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
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {

        try{
            Map<String, String> dataMap = sealRequest(payEntity);

            //发起请求
            String responseData = toPostForm(dataMap, payUrl);

            if(StringUtils.isBlank(responseData)){
                return PayResponse.error("[SP]7pay支付 HTTP 请求返回空");
            }

            //解析返回结果
            JSONObject jb = JSONObject.fromObject(responseData);
            logger.info("[SP]7pay支付  HTTP请求返回参数:" + jb);

            if(jb.containsKey("code") && "1".equalsIgnoreCase(jb.getString("code"))){
                JSONObject result= jb.getJSONObject("result");
                return PayResponse.sm_link(payEntity, result.getString("link") ,"下单成功");
            }
            return PayResponse.error("下单失败"+jb);
        }catch (Exception e){
            e.printStackTrace();
            return PayResponse.error("下单异常" + e.getMessage());
        }
    }

    /**
     * 回调验签
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {
        try{
            String sign = generatorSign(data);
            String sourceSign = data.remove("sign");
            logger.info("[SP]7pay支付回调生成签名串：{}--源签名串：{}", sign , sourceSign );

            if(sign.equalsIgnoreCase(sourceSign)){
                return ret__success;
            }
            return ret__failed;
        }catch (Exception e){
            e.getMessage();
            return ret__failed;
        }
    }

    /**
     * 参数组装
     * @param payEntity
     * @return
     */
    private Map<String, String> sealRequest(PayEntity payEntity){
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());

        dataMap.put("money", amount);//金额
        dataMap.put("mch_id", memberid);//商户ID
        dataMap.put("remark", "TOP-UP");//订单备注
        dataMap.put("out_trade_no", payEntity.getOrderNo());//订单号
        dataMap.put("notifyurl", notifyUrl);//异步通知地址
        dataMap.put("returnurl", payEntity.getRefererUrl());//同步通知地址
        dataMap.put("attach", payEntity.getuId());//贵站的充值会员ID或会员帐号
        dataMap.put("type", payEntity.getPayCode());//支付渠道:weixin微信alipay支付宝
        dataMap.put("sign", generatorSign(dataMap));//

        logger.info("[SP]7pay支付 HTTP请求参数：" + JSONObject.fromObject(dataMap));
        return dataMap;
    }

    /**
     * 签名
     * @param data
     * @return
     */
    private String generatorSign(Map<String, String> data){
        //示例 stringA = "key1=value1&key2=value2";第一步排序后获取到的字符串
        //然后进行拼接密钥 stringSignTemp = stringA+"&key=您的收款密钥"
        //最终加密字符串为 "key1=value1&key2=value2&key=您的收款密钥"进行MD5运算后转大写
        Map<String, String> treeMap = new TreeMap<>(data);
        StringBuffer sb = new StringBuffer();
        try{
            Iterator<String> iterator = treeMap.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String val = treeMap.get(key);
                if("sign".equalsIgnoreCase(key)){
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.append("key=").append(md5key);
            logger.info("[SP]7pay支付 签名前的参数：" + sb.toString());
            return MD5Utils.md5toUpCase_32Bit(sb.toString());
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     *  增加  HEADER参数
     * @Description 发起流参数
     * @param url
     * @param data
     * @return
     * @throws Exception
     */
    public static String toPostForm(Map<String,String> data,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry : data.entrySet()){
                    NameValuePair v = new BasicNameValuePair(entry.getKey(),entry.getValue());
                    nvps.add(v);
                }
                StringEntity entity = new UrlEncodedFormEntity(nvps,Consts.UTF_8);
                entity.setContentEncoding("UTF-8");
                httppost.setEntity(entity);
                httppost.setHeader("format", "json");
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
               // httppost.setHeader("format", "json");
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),Consts.UTF_8));
                StringBuffer sb = new StringBuffer();
                String content = null;
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
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
        };
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[] { tm }, null);

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context,
                NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        return connectionManager;
    }
}
