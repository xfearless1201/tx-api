package com.cn.tianxia.api.pay.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName CMPayServiceImpl
 * @Description 创美支付渠道未提供
 * @Date 2019/4/17 10 37
 **/
public class CMPayServiceImpl extends PayAbstractBaseService implements PayService {

    private static final Logger logger = LoggerFactory.getLogger(CMPayServiceImpl.class);

    private String merchId;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String secret;//密钥
    private String queryOrderUrl;//订单查询地址

    private static String ret__success = "opstate=0";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private boolean verifySuccess = true;//回调验签默认状态为true

    public CMPayServiceImpl() {
    }

    public CMPayServiceImpl(Map<String, String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("merchId")){
                this.merchId = data.get("merchId");
            }
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            if(data.containsKey("queryOrderUrl")){
                this.queryOrderUrl = data.get("queryOrderUrl");
            }
            if(data.containsKey("secret")){
                this.secret = data.get("secret");
            }
        }
    }

    /**
     * 回调
     * @param request
     * @param response
     * @param config
     * @return
     */
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        this.merchId = config.getString("merchId");
        this.notifyUrl = config.getString("notifyUrl");
        this.queryOrderUrl = config.getString("queryOrderUrl");
        this.secret = config.getString("secret");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);

        logger.info("[CM]创美支付    商户返回信息：" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("sysorderid");//第三方订单号，流水号
        String order_no = dataMap.get("orderid");//支付订单号
        String amount = dataMap.get("ovalue");//商户订单总金额，订单总金额以元为单位，精确到小数点后两位
        String trade_status = dataMap.get("opstate");  //第三方支付状态，success = 支付成功
        String t_trade_status = "0";//第三方成功状态
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);  //回调ip

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[CM]创美支付    获取的 流水单号为空");
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[CM]创美支付    回调订单金额为空");
            return ret__failed;
        }

        //订单查询
        try{
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("parter", merchId);
            queryMap.put("orderid", order_no);
            String sign = generatorSign(queryMap, 3);
            queryMap.put("sign", sign);

            String queryData = toPostForm(queryMap, queryOrderUrl);

            logger.info("[CM]创美支付 订单查询结果： " + queryData);

            //优化版
            int c = Arrays.stream(queryData.split("&")).filter(a -> a.startsWith("opstate"))
                    .mapToInt(b -> Integer.parseInt(b.split("=")[1])).sum();
            if(c != 0){
                return ret__failed;
            }

        }catch (Exception e){
            e.printStackTrace();
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
        processNotifyVO.setPayment("CM");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[CM]创美支付   回调验签失败");
            return ret__failed;
        }
        return processSuccessNotify(processNotifyVO, verifySuccess);
    }

    /**
     * 网银支付
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[[CM]创美支付   网银支付   开始=======================start==========================]");
        try{
            Map<String, String> dataMap = sealRequest(payEntity);
            String responseData = generatorFormGet(dataMap, payUrl);

            return PayResponse.wy_form(payUrl,responseData);
        }catch (Exception e){
            e.getStackTrace();
            return PayResponse.error("[CM]创美支付   网银支付  下单异常：" + e.getMessage());
        }
    }

    /**
     * 扫码支付
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[[CM]创美支付   扫码支付   开始=======================start==========================]");
        try{
            Map<String, String> dataMap = sealRequest(payEntity);
            String responseData = generatorFormGet(dataMap, payUrl);

            return PayResponse.sm_form(payEntity, responseData, "下单成功");
        }catch (Exception e){
            e.getStackTrace();
            return PayResponse.error("[CM]创美支付  扫码下单异常：" + e.getMessage());
        }
    }

    /**
     * 回调验签
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {

        String sign = generatorSign(data, 2);
        String sourceSign = data.remove("sign");

        if(sign.equalsIgnoreCase(sourceSign)){
            return ret__success;
        }
        return ret__failed;
    }

    /**
     * 参数组装
     * @param payEntity
     * @return
     */
    private Map<String, String> sealRequest(PayEntity payEntity){
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());

        dataMap.put("parter", merchId);//商户ID
        dataMap.put("type", payEntity.getPayCode());//银行类型
        dataMap.put("value", amount);//金额
        dataMap.put("orderid", payEntity.getOrderNo());//商户订单号
        dataMap.put("callbackurl", notifyUrl);//下行异步通知地址
        //dataMap.put("hrefbackurl","");//下行同步通知地址
        dataMap.put("payerIp", payEntity.getIp());//支付用户IP
        dataMap.put("attach", "TOP-UP");//备注消息
        dataMap.put("sign", generatorSign(dataMap, 1));//签名

        logger.info("[CM]创美支付  HTTP  请求参数：" + JSONObject.fromObject(dataMap));

        return dataMap;
    }

    /**
     * 生成签名
     * @param data
     * @param type
     * @return
     */
    private String generatorSign(Map<String, String> data, int type){

        StringBuffer sb = new StringBuffer();
       // Map<String, String> treeMap = new TreeMap<>(data);
        //treeMap = ((TreeMap<String, String>) treeMap).descendingMap();//倒序排序

        try{
            if(1 == type){
                //扫码：parter={}&type={}&value={}&orderid ={}&callbackurl={}key
                sb.append("parter=").append(data.get("parter")).append("&");
                sb.append("type=").append(data.get("type")).append("&");
                sb.append("value=").append(data.get("value")).append("&");
                sb.append("orderid=").append(data.get("orderid")).append("&");
                sb.append("callbackurl=").append(data.get("callbackurl")).append("&");

            }else if(2 == type){
                //回调：orderid={}&opstate={}&ovalue={}key
                sb.append("orderid=").append(data.get("orderid")).append("&");
                sb.append("opstate=").append(data.get("opstate")).append("&");
                sb.append("ovalue=").append(data.get("ovalue")).append("&");
            }else {
                //订单查询：orderid={}&parter={}key
                sb.append("orderid=").append(data.get("orderid")).append("&");
                sb.append("parter=").append(data.get("parter")).append("&");
            }
            sb.replace(sb.length()-1,sb.length(),secret);

            logger.info("[CM]创美支付 加密前参数：" + sb.toString());
            return MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
        }catch (Exception e){
           e.getStackTrace();
           return "生成签名异常："+e.getMessage();
        }
    }

    /**
     *
     * @Description 生成支付表单
     * @param data
     * @param payUrl
     * @return
     */
    public static String generatorFormGet(Map<String,String> data,String payUrl) {
        String FormString = "<body onLoad=\"document.actform.submit()\">正在处理请稍候....................."
                + "<form  id=\"actform\" name=\"actform\" method=\"get\" action=\""
                + payUrl + "\">";
        if(data != null && !data.isEmpty()){
            for (String key : data.keySet()) {
                if (org.apache.commons.lang.StringUtils.isNotBlank(data.get(key)))
                    FormString += "<input name=\"" + key + "\" type=\"hidden\" value='" + data.get(key) + "'>\r\n";
            }
        }
        FormString += "</form></body>";
        return FormString;
    }

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
                entity.setContentEncoding("GB2312");
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),"GB2312"));
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
