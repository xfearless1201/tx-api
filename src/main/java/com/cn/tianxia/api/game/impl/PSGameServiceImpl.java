package com.cn.tianxia.api.game.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * @ClassName PSGameServiceImpl
 * @Description PS电子游戏
 * @author zw
 * @Date 2018年5月26日 上午9:31:17
 * @version 1.0.0
 */
public class PSGameServiceImpl{
    
    private static final Logger logger = LoggerFactory.getLogger(PSGameServiceImpl.class);

    private String api_url;
    private String host_id;
    private String lang;

    /** 创建用户 **/
    private static final String C_METHOD_CREATE_USER = "createUser";

    /** 转入游戏 **/
    private static final String C_METHOD_TRANSIN = "transIn";
    /** 转出游戏 **/
    private static final String C_METHOD_TRANSOUT = "transOut";
    /** 查询余额 **/
    private static final String C_METHOD_BALANCE = "balance";

    private static final String ERROR = "error";

    private static final String SUCCESS = "success";

    public PSGameServiceImpl(Map<String, String> pmap) {
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(pmap, "PS");
        JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());
        api_url = jo.getString("api_url").toString();
        host_id = jo.getString("host_id").toString();
        lang = jo.getString("lang").toString();
    }

    public static void main(String[] args) {
        Map<String, String> init = new HashMap<>();
        init.put("api_url", "https://stage-api.iplaystar.net");
        init.put("host_id", "6e37c21a35e9d9dc4587f51b1c902b6e");
        init.put("lang", "zh-CN");

        System.out.println("JSON配置:" + JSONObject.fromObject(init));
        Map<String, String> map1 = new HashMap<>();
        map1.put("PS", JSONObject.fromObject(init).toString());

        PSGameServiceImpl ps = new PSGameServiceImpl(map1);
        String username = "BL1zouwei522";
        String txn_id = "TX" + System.currentTimeMillis();
        String amount = "10000";
        String game_id = "PSS-ON-00019";
        String access_token = "123456";
        String subgame_id = "";
        // String lang = "zh-CN";
        String return_url = "http://baidu.com";

        // ps.createUser(username);
        // System.out.println(ps.balance(username));
        // System.out.println(ps.transIn(username, txn_id, amount));
        // System.out.println(ps.transOut(username, txn_id, amount));

        System.out.println(ps.login(username, subgame_id, game_id, access_token, return_url));

        // ps.getPSGamelist();

        // try {
        // System.out.println(new
        // String(doGet("https://stage-api.iplaystar.net/feed/gamehistory/?host_id=6e37c21a35e9d9dc4587f51b1c902b6e&start_dtm=2018-05-25T02:00:00&end_dtm=2018-05-29T02:01:00&type=0")));
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    /**
     * @Description 获取游戏列表
     * @return
     */
    public String getPSGamelist() {

        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("host_id", host_id);
        map.put("type", "0");
        map.put("category", "1");

        String post_url = api_url + "/feed/gamelist" + getParams(map);
        logger.info("【PS后端请求】：" + post_url);
        String response = "";
        try {
            response = new String(doGet(post_url));
            logger.info("【PS后端响应】：" + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * @Description 登录游戏
     * @param username
     * @param subgame_id
     * @param game_id
     * @param access_token
     * @param return_url
     * @return
     */
    public String login(String username, String subgame_id, String game_id, String access_token, String return_url) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("host_id", host_id);
        map.put("game_id", game_id);
        map.put("lang", lang);
        map.put("access_token", access_token);
        // map.put("member_id", username);
        if ((!"".equals(subgame_id)) && null != subgame_id) {
            map.put("subgame_id", subgame_id);
        }

        map.put("return_url", return_url);

        String post_url = api_url + "/launch/" + getParams(map) + "/lobby/index.htm";

        logger.info("【URL】：" + post_url);

        return post_url;
    }

    /**
     * @Description 注册用户
     * @param username
     * @return
     */
    public String createUser(String username) {
        Map<String, String> map = new HashMap<>();
        map.put("host_id", host_id);
        map.put("member_id", username);
        String post_url = api_url + "/funds/createplayer/" + getParams(map);
        logger.info("【PS后端请求】：" + post_url);

        String response = "";
        try {
            response = new String(doGet(post_url));
            logger.info("【PS后端响应】：" + response);
            JSONObject jo = JSONObject.fromObject(response);

            if (jo.containsKey("status_code") && "0".equals(jo.getString("status_code"))) {
                return SUCCESS;
            }

        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_CREATE_USER, post_url, map, response);
            return ERROR;
        }

        return ERROR;
    }

    /**
     * @Description 查询余额
     * @param username
     * @return
     */
    public String balance(String username) {
        Map<String, String> map = new HashMap<>();
        map.put("host_id", host_id);
        map.put("member_id", username);
        String post_url = api_url + "/funds/getbalance/" + getParams(map);
        logger.info("PS 用户【"+ username +"】 , 发起第三方查询余额请求链接:{}", post_url);

        String response = "";
        try {
            response = new String(doGet(post_url));
            logger.info("PS 用户【"+ username +"】 , 发起第三方查询余额响应结果:{}", response);
            JSONObject jo = JSONObject.fromObject(response);

            if (jo.containsKey("status_code") && "0".equals(jo.getString("status_code"))) {
                String balance = jo.get("balance").toString();
                //DecimalFormat df=new DecimalFormat("##########.00");
                Double  s_amount= Double.valueOf(balance)/100;
                return  String.valueOf(s_amount);
            }

        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_BALANCE, post_url, map, response);
            return ERROR;
        }

        return ERROR;
    }

    /**
     * @Description 转入游戏
     * @param username
     * @param txn_id
     * @param amount
     * @return
     */
    public String transIn(String username, String txn_id, String amount) {
        DecimalFormat df=new DecimalFormat("##########");
        Double  s_amount= Double.valueOf(amount)*100;
        
        Map<String, String> map = new HashMap<>();
        map.put("host_id", host_id);
        map.put("member_id", username);
        map.put("txn_id", txn_id);
        map.put("amount",  df.format(s_amount));
        String post_url = api_url + "/funds/deposit/" + getParams(map);
        logger.info("PS 用户【"+ username +"】 ,订单编号【"+ txn_id +"】 发起第三方转账（上分）请求链接:{}", post_url);

        String response = "";
        try {
            response = new String(doGet(post_url));
            logger.info("PT 用户【"+ username +"】 ,订单编号【"+ txn_id +"】 发起第三方转账（上分）返回结果:{}", response);
            JSONObject jo = JSONObject.fromObject(response);

            if (jo.containsKey("status_code") && "0".equals(jo.getString("status_code"))) {
                // String balance = jo.get("balance").toString();
                return SUCCESS;
            }

        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_TRANSIN, post_url, map, response);
            return ERROR;
        }

        return ERROR;
    }

    /**
     * @Description 转出游戏
     * @param username
     * @param txn_id
     * @param amount
     * @return
     */
    public String transOut(String username, String txn_id, String amount) {
        DecimalFormat df=new DecimalFormat("##########");
        Double  s_amount= Double.valueOf(amount)*100;
        
        Map<String, String> map = new HashMap<>();
        map.put("host_id", host_id);
        map.put("member_id", username);
        map.put("txn_id", txn_id);
        map.put("amount", df.format(s_amount));
        String post_url = api_url + "/funds/withdraw/" + getParams(map);
        logger.info("PS 用户【"+ username +"】 ,订单编号【"+ txn_id +"】 发起第三方转账（下分）请求链接:{}", post_url);

        String response = "";
        try {
            response = new String(doGet(post_url));
            logger.info("PT 用户【"+ username +"】 ,订单编号【"+ txn_id +"】 发起第三方转账（下分）返回结果:{}", response);
            JSONObject jo = JSONObject.fromObject(response);

            if (jo.containsKey("status_code") && "0".equals(jo.getString("status_code"))) {
                // String balance = jo.get("balance").toString();
                return SUCCESS;
            }

        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_TRANSOUT, post_url, map, response);
            return ERROR;
        }

        return ERROR;
    }

    /**
     * @Description Url参数
     * @param map
     * @return
     */
    private String getParams(Map<String, String> map) {
        String param = "?";
        StringBuffer sr = new StringBuffer("");
        Set<String> set = map.keySet();
        for (String str : set) {
            sr.append(str + "=");
            sr.append(map.get(str) + "&");
        }

        param += sr.toString().substring(0, sr.length() - 1);
        return param;
    }

    /**
     * @Description 错误日志
     * @param
     * @param reqUrl
     * @param requestParams
     * @param result
     */
    private void setFile(String actionStr, String reqUrl, Map<String, String> requestParams, String result) {
        FileLog f = new FileLog();
        Map<String, String> pam = new HashMap<>();
        pam.put("method", actionStr);
        pam.put("URL", reqUrl);
        pam.put("requestParams", requestParams.toString());
        pam.put("responseParams", result);
        f.setLog("PS", pam);
    }

    private static final class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private static HttpsURLConnection getHttpsURLConnection(String uri, String method) throws IOException {
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager() }, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SSLSocketFactory ssf = ctx.getSocketFactory();

        URL url = new URL(uri);
        HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
        httpsConn.setSSLSocketFactory(ssf);
        httpsConn.setHostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
        httpsConn.setRequestMethod(method);
        httpsConn.setDoInput(true);
        httpsConn.setDoOutput(true);
        return httpsConn;
    }

    private static byte[] getBytesFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] kb = new byte[1024];
        int len;
        while ((len = is.read(kb)) != -1) {
            baos.write(kb, 0, len);
        }
        byte[] bytes = baos.toByteArray();
        baos.close();
        is.close();
        return bytes;
    }

    private static void setBytesToStream(OutputStream os, byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        byte[] kb = new byte[1024];
        int len;
        while ((len = bais.read(kb)) != -1) {
            os.write(kb, 0, len);
        }
        os.flush();
        os.close();
        bais.close();
    }

    public static byte[] doGet(String uri) throws IOException {
        HttpsURLConnection httpsConn = getHttpsURLConnection(uri, "GET");
        return getBytesFromStream(httpsConn.getInputStream());
    }

    public static byte[] doPost(String uri, String data) throws IOException {
        HttpsURLConnection httpsConn = getHttpsURLConnection(uri, "POST");
        setBytesToStream(httpsConn.getOutputStream(), data.getBytes());
        return getBytesFromStream(httpsConn.getInputStream());
    }

}
