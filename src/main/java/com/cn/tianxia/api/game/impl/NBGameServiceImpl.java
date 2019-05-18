package com.cn.tianxia.api.game.impl;

import java.text.DecimalFormat;
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
 * @ClassName NBGameServiceImpl
 * @Description NB体育
 * @author zw
 * @Date 2018年5月29日 上午10:54:27
 * @version 1.0.0
 */
public class NBGameServiceImpl{

    private String createUrl;

    private String loginUrl;

    private String balanceUrl;

    private String transferUrl;

    private String isOnlineUrl;

    private String userExitUrl;

    private String forwordGameUrl;

    private String currency = "CNY";

    /** 错误常量 **/
    private static final String C_ERROR = "error";
    /** 错误常量 **/
    private static final String C_SUCCESS = "success";
    /** 创建用户 **/
    private static final String C_METHOD_CREATE_USER = "createUser";
    /** 登录游戏 **/
    private static final String C_METHOD_LOGIN = "login";
    /** 游戏转账 **/
    private static final String C_METHOD_TRANSFER = "transfer";
    /** 查询余额 **/
    private static final String C_METHOD_BALANCE = "balance";
    /** 用户在线 **/
    private static final String C_METHOD_ONLINE = "isOnline";
    /** 用户退出 **/
    private static final String C_METHOD_USER_EXIT = "userExit";

    private final static Logger logger = LoggerFactory.getLogger(NBGameServiceImpl.class);

    public NBGameServiceImpl(Map<String, String> pmap) {
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(pmap, "NB");
        JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());
        createUrl = jo.getString("createUrl");
        loginUrl = jo.getString("loginUrl");
        balanceUrl = jo.getString("balanceUrl");
        transferUrl = jo.getString("transferUrl");
        isOnlineUrl = jo.getString("isOnlineUrl");
        userExitUrl = jo.getString("userExitUrl");
        currency = jo.getString("currency");
        forwordGameUrl = jo.getString("forwordGameUrl");
    }

    public static void main(String[] args) {
        Map<String, String> init = new HashMap<>();
        init.put("createUrl", "http://54.169.239.45:9090/nbwallet/createmember");
        init.put("loginUrl", "http://54.169.239.45:9090/nbwallet/loginnb");
        init.put("balanceUrl", "http://54.169.239.45:9090/nbwallet/checkuserbalance");
        init.put("transferUrl", "http://54.169.239.45:9090/nbwallet/fundtransfer");
        init.put("isOnlineUrl", "http://54.169.239.45:9090/nbwallet/isonline");
        init.put("userExitUrl", "http://54.169.239.45:9090/nbwallet/userexit");
        init.put("currency", "CNY");
        init.put("forwordGameUrl", "http://uat.txkjweb.nbbets.com/template/walletmodel/txkj/index.html?token=");

        System.out.println("JSON配置:" + JSONObject.fromObject(init));
        Map<String, String> map1 = new HashMap<>();
        map1.put("NB", JSONObject.fromObject(init).toString());

        String userId = "lixin001";
        String userName = "lixin001";
        String nickName = "lixin001";
        String realName = "lixin001";
        String currency = "CNY";
        Double maxTransfer = (double) 50000;
        Double minTransfer = (double) 1;
        int direction = 1;
        int transferAll = 0;

        NBGameServiceImpl nb = new NBGameServiceImpl(map1);
        // System.out.println(nb.createUser(userName));
//         System.out.println(nb.login(userName));
         System.out.println(nb.balance(userName));
//        System.out.println(nb.transfer(userName, direction, transferAll, "2000"));
        // System.out.println(nb.isOnline(userName));
        // System.out.println(nb.userExit(userName));
    }

    /**
     * @Description 创建用户
     * @param username
     * @return
     */
    public String createUser(String username) {
        Map<String, Object> map = new HashMap<>();
        String userId = username;
        String userName = username;
        String nickName = username;
        String realName = username;
        String currency = "CNY";
        Double maxTransfer = (double) 100000;
        Double minTransfer = (double) 1;
        map.put("userId", userId);
        map.put("userName", userName);
        map.put("nickName", nickName);
        map.put("realName", realName);
        map.put("currency", currency);
        map.put("maxTransfer", maxTransfer);
        map.put("minTransfer", minTransfer);
        String respContent = "";
        try {
            respContent = sendPost(C_METHOD_CREATE_USER, createUrl, map);
            if (isError(respContent)) {
                return C_ERROR;
            }
            JSONObject jo = JSONObject.fromObject(respContent);
            if (jo.containsKey("code") && "200".equals(jo.getString("code"))) {
                return C_SUCCESS;
            }

        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_CREATE_USER, createUrl, map, respContent);
            return C_ERROR;
        }
        return C_ERROR;
    }

    /**
     * @Description 登录游戏
     * @param username
     * @return
     */
    public String login(String userName) {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", userName);
        String respContent = "";
        try {
            respContent = sendPost(C_METHOD_LOGIN, loginUrl, map);
            if (isError(respContent)) {
                return C_ERROR;
            }

            JSONObject jo = JSONObject.fromObject(respContent);
            if (jo.containsKey("code") && "200".equals(jo.getString("code"))) {
                if (C_SUCCESS.equals(isOnline(userName))) {
                    return forwordGameUrl + JSONObject.fromObject(jo.get("data")).getString("token");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_LOGIN, loginUrl, map, respContent);
            return C_ERROR;
        }
        return C_ERROR;
    }

    /**
     * @Description 查询余额
     * @param username
     * @return
     */
    public String balance(String userName) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userName);
        map.put("t", System.currentTimeMillis());
        String respContent = "";
        try {
            respContent = sendPost(C_METHOD_BALANCE, balanceUrl, map);
            logger.info("NB 用户【"+ userName +"】 ,发起查询余额返回结果:{}", respContent);

            if (isError(respContent)) {
                return C_ERROR;
            }
            JSONObject jo = JSONObject.fromObject(respContent);
            if (jo.containsKey("code") && "200".equals(jo.getString("code"))) {
                JSONObject data = (JSONObject) jo.get("data");
                DecimalFormat df = new DecimalFormat("########0.0000");
                return df.format(data.getDouble("balance"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_LOGIN, balanceUrl, map, respContent);
            return C_ERROR;
        }
        return C_ERROR;
    }

    /**
     * @Description 转账
     * @param userName
     * @param direction
     *            1为商户转到NB平台,0为NB平台到商户
     * @param transferAll
     *            为“0”表示转出指定金额，“1”为转出全部
     * @param amount
     *            金额
     * @return
     */
    public String transfer(String userName, int direction, int transferAll, String amount) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userName);
        map.put("direction", direction);
        map.put("transferAll", transferAll);
        map.put("amount", amount);
        map.put("currency", currency);

        logger.info("NB 用户【"+ userName +"】 , 发起第三方转账（上下分）请求参数:{}", map.toString());
        String respContent = "";
        try {
            respContent = sendPost(C_METHOD_TRANSFER, transferUrl, map);
            logger.info("NB 用户【"+ userName +"】 , 发起第三方转账（上下分）响应结果:{}", respContent);
            if (isError(respContent)) {
                return C_ERROR;
            }
            JSONObject jo = JSONObject.fromObject(respContent);
            if (jo.containsKey("code") && "200".equals(jo.getString("code"))) {
                return C_SUCCESS;
            }

        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_TRANSFER, transferUrl, map, respContent);
            return C_ERROR;
        }
        return C_ERROR;
    }

    /**
     * @Description 用户在线
     * @param userName
     * @return
     */
    public String isOnline(String userName) {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", userName);
        String respContent = "";
        try {
            respContent = sendPost(C_METHOD_ONLINE, isOnlineUrl, map);
            if (isError(respContent)) {
                return C_ERROR;
            }

            JSONObject jo = JSONObject.fromObject(respContent);
            if (jo.containsKey("code") && "200".equals(jo.getString("code"))) {
                return C_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_ONLINE, isOnlineUrl, map, respContent);
            return C_ERROR;
        }
        return C_ERROR;
    }

    /**
     * @Description 用户退出，变更用户登录状态。（即：心跳接口）
     * @param userName
     * @return
     */
    public String userExit(String userName) {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", userName);
        String respContent = "";
        try {
            respContent = sendPost(C_METHOD_USER_EXIT, userExitUrl, map);
            if (isError(respContent)) {
                return C_ERROR;
            }

            JSONObject jo = JSONObject.fromObject(respContent);
            if (jo.containsKey("code") && "200".equals(jo.getString("code"))) {
                return C_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFile(C_METHOD_USER_EXIT, userExitUrl, map, respContent);
            return C_ERROR;
        }
        return C_ERROR;
    }

    /**
     * @Description post 请求
     * @param actionStr
     * @param reqUrl
     * @param requestParams
     * @return
     */
    @SuppressWarnings("resource")
    public String sendPost(String actionStr, String reqUrl, Map<String, Object> requestParams) {
        HttpPost httpPost = new HttpPost(reqUrl);
        @SuppressWarnings("deprecation")
        HttpClient httpClient = new DefaultHttpClient();
        String respContent = "error";
        try {
            JSONObject jsonParam = JSONObject.fromObject(requestParams);
            StringEntity entity;

            logger.info("【NB后端请求】：" + reqUrl + "?json=" + jsonParam.toString());

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
            logger.info("【NB响应】：" + respContent);
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
     * @Description 是否异常
     * @param respContent
     * @return
     */
    private boolean isError(String respContent) {
        if ("error".equals(respContent) || "".equals(respContent)) {
            return true;
        }
        return false;
    }

    /**
     * @Description 错误日志
     * @param action
     * @param reqUrl
     * @param requestParams
     * @param result
     */
    private void setFile(String actionStr, String reqUrl, Map<String, Object> requestParams, String result) {
        FileLog f = new FileLog();
        Map<String, String> pam = new HashMap<>();
        pam.put("method", actionStr);
        pam.put("URL", reqUrl);
        pam.put("requestParams", requestParams.toString());
        pam.put("responseParams", result);
        f.setLog("NB", pam);
    }

}
