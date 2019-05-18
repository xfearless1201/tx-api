package com.cn.tianxia.api.game.impl;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * @ClassName GYGameServiceImpl
 * @Description GY彩票
 * @author zw
 * @Date 2018年5月25日 下午5:29:02
 * @version 1.0.0
 */
public class GYGameServiceImpl{

    /** 创建会员 **/
    private static final String COMMAND_CREATE_ACCOUNT = "third/reg";

    /** 彩票类进入游戏链接 **/
    private static final String COMMAND_LOGIN_GAME = "third/login";

    /** 查询余额 **/
    private static final String COMMAND_QUERY_BALANCE = "third/balance";

    /** 转账转入游戏厅 **/
    private static final String COMMAND_TRANSFER_IN = "third/transIn";

    /** 转账从游戏厅转出钱 **/
    private static final String COMMAND_TRANSFER_OUT = "third/transOut";
    
    /** 查询用户投注记录 **/
    private static final String COMMAND_BETS = "third/bets";

    /** 错误常量 **/
    private static final String C_ERROR = "error";
    /** 错误常量 **/
    private static final String C_SUCCESS = "success";
    /** 创建用户 **/
    private static final String C_METHOD_CREATE_USER = "createUser";
    /** 登录游戏 **/
    private static final String C_METHOD_LOGIN = "login";
    /** 转入游戏 **/
    private static final String C_METHOD_TRANSIN = "transIn";
    /** 转出游戏 **/
    private static final String C_METHOD_TRANSOUT = "transOut";
    /** 查询余额 **/
    private static final String C_METHOD_BALANCE = "balance";
    /** 查询用户投注记录 **/
    private static final String C_METHOD_BETS = "bets";

    /** 接口访问地址 **/
    private String WEB_URL;
    /** 加密密文 **/
    private String KEY;
    /** 商户号 **/
    private String MECHANT_ID;
    /** 密 钥 **/
    private String LOGINPIN;
    /** PC端游戏地址 **/
    private String pc_url;
    /** 手机端游戏地址 **/
    private String mb_url;

    private final static Logger logger = LoggerFactory.getLogger(GYGameServiceImpl.class);

    public GYGameServiceImpl(Map<String, String> pmap) {
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(pmap, "GY");
        JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());
        WEB_URL = jo.getString("WEB_URL");
        KEY = jo.getString("KEY");
        MECHANT_ID = jo.getString("MECHANT_ID");
        LOGINPIN = jo.getString("LOGINPIN");
        pc_url = jo.getString("pc_url");
        mb_url = jo.getString("mb_url");
    }

    public static void main(String[] args) {
        Map<String, String> init = new HashMap<>();
        init.put("WEB_URL", "http://www.91kxcai.com/king-web");
        init.put("MECHANT_ID", "ceshi");
        init.put("LOGINPIN", "abc!@#ZXY");
        init.put("KEY", "qwe123asd");
        init.put("pc_url", "http://www.91kxcai.com/");
        init.put("mb_url", "http://wap.91kxcai.com/");

        System.out.println("JSON配置:" + JSONObject.fromObject(init));
        Map<String, String> map1 = new HashMap<>();
        map1.put("GY", JSONObject.fromObject(init).toString());

        String USERNAME = "aaaa11";//
        String PASSWORD = "123456";//

        GYGameServiceImpl gy = new GYGameServiceImpl(map1);
        // System.out.println(gy.sendPost());
        // gy.createUser(USERNAME, PASSWORD);
         System.out.println(gy.login(USERNAME, PASSWORD, "1"));
        // System.out.println(gy.transIn(USERNAME, PASSWORD, "100"));
        // System.out.println(gy.transOut(USERNAME, PASSWORD, "10"));
//        System.out.println(gy.balance(USERNAME, PASSWORD));
    }

    /**
     * @Description 注册用户
     * @param username
     *            用户姓名
     * @param password
     *            用户密码
     * @return
     */
    public String createUser(String username, String password) {
        String resultStr = C_ERROR;

        String times = String.valueOf(System.currentTimeMillis());
        String priKey = KEY + MECHANT_ID + "_" + times;
        String sign = sha256(priKey);

        Map<String, String> requestParams = new HashMap<>();

        requestParams.put("bussName", MECHANT_ID);
        requestParams.put("userName", username);
        requestParams.put("passWord", password);
        requestParams.put("loginPin", LOGINPIN);
        requestParams.put("nickName", username);
        requestParams.put("times", times);
        requestParams.put("sign", sign);

        String reqUrl = WEB_URL + "/" + COMMAND_CREATE_ACCOUNT;
        String responseJson = sendPost(C_METHOD_CREATE_USER, reqUrl, requestParams);

        if (C_ERROR.equals(responseJson)) {
            return resultStr;
        }

        try {
            JSONObject json = JSONObject.fromObject(responseJson);
            if (json.containsKey("code") && "0".equals(json.getString("code")) && json.containsKey("msg")
                    && "SUCCESS".equals(json.getString("msg"))) {
                return C_SUCCESS;
            } else {
                setFile(C_METHOD_CREATE_USER, reqUrl, requestParams, responseJson);
                return resultStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_CREATE_USER, reqUrl, requestParams, responseJson);
            logger.info("GY彩票_" + C_METHOD_CREATE_USER + "参数解析错误");
            return resultStr;
        }
    }

    /**
     * @Description 登录游戏地址
     * @param username
     *            用户姓名
     * @param passowrd
     *            用户密码
     * @param model
     *            手机端1 pc端0
     * @return
     */
    public String login(String username, String passowrd, String model) {
        String resultStr = C_ERROR;
        String times = String.valueOf(System.currentTimeMillis());
        String priKey = KEY + MECHANT_ID + "_" + times;
        String sign = sha256(priKey);

        // 手机端1 pc端0
        String clientType = "0";

        if (!"PC".equals(model)) {
            clientType = "1";
        }

        String reqUrl = WEB_URL + "/" + COMMAND_LOGIN_GAME;

        Map<String, String> requestParams = new HashMap<>();

        requestParams.put("bussName", MECHANT_ID);
        requestParams.put("userName", username);
        requestParams.put("passWord", passowrd);
        requestParams.put("loginPin", LOGINPIN);
        requestParams.put("times", times);
        requestParams.put("clientType", clientType);
        requestParams.put("sign", sign);

        String responseJson = sendPost(C_METHOD_LOGIN, reqUrl, requestParams);

        if (C_ERROR.equals(responseJson)) {
            return resultStr;
        }

        try {
            JSONObject json = JSONObject.fromObject(responseJson);
            if (json.containsKey("code") && "0".equals(json.getString("code")) && json.containsKey("msg")
                    && "SUCCESS".equals(json.getString("msg"))) {

                String data = json.getString("data");

                if ("PC".equals(model)) {
                    return pc_url + data;
                } else {
                    return mb_url + data;
                }
            } else {
                setFile(C_METHOD_LOGIN, reqUrl, requestParams, responseJson);
                return resultStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_LOGIN, reqUrl, requestParams, responseJson);
            logger.info("GY彩票_" + C_METHOD_LOGIN + "参数解析错误");
            return resultStr;
        }
    }

    /**
     * @Description 转入游戏
     * @param username
     * @param password
     * @param money
     * @param orderId
     * @return
     */
    public String transIn(String username, String password, String orderId, String money) {
        String resultStr = C_ERROR;
        String times = orderId;
        String priKey = KEY + "_" + times + "_" + LOGINPIN + "_" + username + "_" + MECHANT_ID + "_" + money;
        String sign = sha256(priKey);
        String reqUrl = WEB_URL + "/" + COMMAND_TRANSFER_IN;

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("bussName", MECHANT_ID);
        requestParams.put("userName", username);
        requestParams.put("passWord", password);
        requestParams.put("loginPin", LOGINPIN);
        requestParams.put("times", times);
        requestParams.put("money", money);
        requestParams.put("sign", sign);

        String responseJson = sendPost(C_METHOD_TRANSIN, reqUrl, requestParams);

        if (C_ERROR.equals(responseJson)) {
            return resultStr;
        }

        try {
            JSONObject json = JSONObject.fromObject(responseJson);
            logger.info("[GY] 发起Http转账请求，第三方响应报文：{}",json.toString());
            if (json.containsKey("code") && "0".equals(json.getString("code")) && json.containsKey("msg")
                    && "SUCCESS".equals(json.getString("msg"))) {
                return C_SUCCESS;

            } else {
                setFile(C_METHOD_TRANSIN, reqUrl, requestParams, responseJson);
                return resultStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_TRANSIN, reqUrl, requestParams, responseJson);
            logger.info("GY彩票_" + C_METHOD_TRANSIN + "参数解析错误");
            return resultStr;
        }

    }

    /**
     * @Description 转账转出
     * @param username
     * @param password
     * @param orderId
     * @param money
     * @return
     */
    public String transOut(String username, String password, String orderId, String money) {
        String resultStr = C_ERROR;
        // String money = "100";
        String times = orderId;
        String priKey = KEY + "_" + times + "_" + LOGINPIN + "_" + username + "_" + MECHANT_ID + "_" + money;
        String sign = sha256(priKey);

        String reqUrl = WEB_URL + "/" + COMMAND_TRANSFER_OUT;

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("bussName", MECHANT_ID);
        requestParams.put("userName", username);
        requestParams.put("passWord", password);
        requestParams.put("loginPin", LOGINPIN);
        requestParams.put("times", times);
        requestParams.put("money", money);
        requestParams.put("sign", sign);

        String responseJson = sendPost(C_METHOD_TRANSOUT, reqUrl, requestParams);
        logger.info("[GY] 发起Http转账请求，第三方响应报文：{}",responseJson);
        if (C_ERROR.equals(responseJson)) {
            return resultStr;
        }

        try {
            JSONObject json = JSONObject.fromObject(responseJson);
            if (json.containsKey("code") && "0".equals(json.getString("code")) && json.containsKey("msg")
                    && "SUCCESS".equals(json.getString("msg"))) {

                return C_SUCCESS;

            } else {
                setFile(C_METHOD_TRANSOUT, reqUrl, requestParams, responseJson);
                return resultStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_TRANSOUT, reqUrl, requestParams, responseJson);
            logger.info("GY彩票_" + C_METHOD_TRANSOUT + "参数解析错误");
            return resultStr;
        }

    }

    /**
     * @Description 查询用户账户余额
     * @param username
     * @param password
     * @return
     */
    public String balance(String username, String password) {
        String resultStr = C_ERROR;

        String times = String.valueOf(System.currentTimeMillis());
        String priKey = KEY + "_" + times + "_" + LOGINPIN + "_" + username + "_" + MECHANT_ID;
        String sign = sha256(priKey);

        String reqUrl = WEB_URL + "/" + COMMAND_QUERY_BALANCE;

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("bussName", MECHANT_ID);
        requestParams.put("userName", username);
        requestParams.put("passWord", password);
        requestParams.put("loginPin", LOGINPIN);
        requestParams.put("times", times);
        requestParams.put("sign", sign);

        String responseJson = sendPost(C_METHOD_BALANCE, reqUrl, requestParams);

        if (C_ERROR.equals(responseJson)) {
            return resultStr;
        }

        try {
            JSONObject json = JSONObject.fromObject(responseJson);
            if (json.containsKey("code") && "0".equals(json.getString("code")) && json.containsKey("msg")
                    && "SUCCESS".equals(json.getString("msg"))) {

                String balance = json.get("data").toString();
                return balance;

            } else {
                setFile(C_METHOD_BALANCE, reqUrl, requestParams, responseJson);
                return resultStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_BALANCE, reqUrl, requestParams, responseJson);
            logger.info("GY彩票_" + C_METHOD_BALANCE + "参数解析错误");
            return resultStr;
        }

    }

    /**
     * 
     * @Description (查询用户投注记录)
     * @return
     */
    private String betsList() {
        String resultStr = C_ERROR;

        String times = String.valueOf(System.currentTimeMillis());
        String curPage = "1";
        String pageSize = "20";
        String lotType = "SSC";

        String priKey = KEY + "_" + times + "_" + MECHANT_ID + "_" + curPage + "_" + lotType + "_" + pageSize;
        String sign = sha256(priKey);

        String reqUrl = WEB_URL + "/" + COMMAND_BETS;
        Map<String, String> requestParams = new HashMap<>();

        requestParams.put("bussName", MECHANT_ID);
        requestParams.put("times", times);
        requestParams.put("lotType", lotType);
        requestParams.put("stTime", "2018-03-06 17:28:12");
        requestParams.put("endTime", "2018-03-06 17:32:19");
        requestParams.put("curPage", "1");
        requestParams.put("pageSize", "20");
        // true 查询重结算过的注单
        requestParams.put("reSettle", "false");
        requestParams.put("sign", sign);

        String responseJson = sendPost(C_METHOD_BETS, reqUrl, requestParams);

        if (C_ERROR.equals(responseJson)) {
            return resultStr;
        }

        try {
            JSONObject json = JSONObject.fromObject(responseJson);
            if (json.containsKey("code") && "0".equals(json.getString("code")) && json.containsKey("msg")
                    && "SUCCESS".equals(json.getString("msg"))) {

                String betList = json.get("data").toString();
                return betList;

            } else {
                setFile(C_METHOD_BETS, reqUrl, requestParams, responseJson);
                return resultStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_BETS, reqUrl, requestParams, responseJson);
            logger.info("GY用户投注记录_" + C_METHOD_BETS + "参数解析错误");
            return resultStr;
        }

    }
    /**
     * @Description post 请求
     * @param actionStr
     * @param reqUrl
     * @param requestParams
     * @return
     */
    @SuppressWarnings("resource")
    public String sendPost(String actionStr, String reqUrl, Map<String, String> requestParams) {
        HttpPost httpPost = new HttpPost(reqUrl);
        @SuppressWarnings("deprecation")
        HttpClient httpClient = new DefaultHttpClient();
        String respContent = "error";
        try {
            JSONObject jsonParam = JSONObject.fromObject(requestParams);
            StringEntity entity;

            logger.info("【GY后端请求】：" + reqUrl + "?json=" + jsonParam.toString());

            entity = new StringEntity(jsonParam.toString(), "utf-8");

            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            HttpResponse resp = httpClient.execute(httpPost);

            if (resp.getStatusLine().getStatusCode() == 200) {
                HttpEntity he = resp.getEntity();
                respContent = EntityUtils.toString(he, "UTF-8");
            } else {
                setFile(actionStr, reqUrl, requestParams, respContent);
            }
            logger.info("【GY响应】：" + respContent);
            return respContent;
        } catch (Exception e) {
            e.printStackTrace();
            setFile(actionStr, reqUrl, requestParams, respContent);
            return respContent;
        } finally {
            httpPost.releaseConnection();
        }
    }

    /**
     * @Description sha256加密
     * @param str
     * @return
     */
    public static String sha256(String str) {
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes("UTF-8"));
            byte byteData[] = sh.digest();
            StringBuilder sb = new StringBuilder(2 * byteData.length);
            for (byte b : byteData) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * @Description 错误日志
     * @param action
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
        f.setLog("GY", pam);
    }

    
 
}
