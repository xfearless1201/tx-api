package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.JSONUtils;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName JDBServiceImpl
 * @Description JDB电子游戏
 * @author zw
 * @Date 2018年7月11日 下午1:58:31
 * @version 1.0.0
 */
public class JDBGameServiceImpl{

    private String API_URL;
    private String DC;
    private String IV;
    private String KEY;
    private String Agent;
    // private String parent;

    /** 创建用户 **/
    private static final String C_METHOD_CREATE_USER = "createUser";
    /** 转入游戏 **/
    private static final String C_METHOD_TRANSIN = "transIn";
    /** 转出游戏 **/
    private static final String C_METHOD_TRANSOUT = "transOut";
    /** 查询余额 **/
    private static final String C_METHOD_BALANCE = "getBalance";
    /** 登录游戏 **/
    private static final String C_METHOD_LOGIN = "login";
    /** 游戏列表 **/
    private static final String C_METHOD_GAME_LIST = "gameList";

    private static final String ERROR = "error";

    private static final String SUCCESS = "success";

    private final static Logger logger = LoggerFactory.getLogger(JDBGameServiceImpl.class);

    public static void main(String[] args) {
        Map<String, String> pmap = new HashMap<>();
        pmap.put("API_URL", "https://www.jygrq.com/apiRequest.do");
        pmap.put("DC", "TX");
        pmap.put("IV", "cf5287821896e00c");
        pmap.put("KEY", "219d04ffd1f47d3c");
        pmap.put("Agent", "txag");

        Map<String, String> pmap1 = new HashMap<>();
        pmap1.put("JDB", JSONObject.fromObject(pmap).toString());
        JDBGameServiceImpl jdb = new JDBGameServiceImpl(pmap1);

        String uid = "bl1huanghao93";
        String serialNo = "TX" + System.currentTimeMillis();
        int amount = 100000000;
        String model = "MB";
        String gameId = "7001";
        // System.out.println(jdb.createUser(uid));
        // System.out.println(jdb.getBalance(uid));
        System.out.println(jdb.transIn(uid, serialNo, amount));
        // System.out.println(jdb.transOut(uid, serialNo, amount));
        //System.out.println(jdb.login(uid, gameId, model));
        // System.out.println(jdb.getGameList());
    }

    public JDBGameServiceImpl(Map<String, String> pmap) {
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(pmap, "JDB");
        JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());
        API_URL = jo.getString("API_URL");
        KEY = jo.getString("KEY");
        DC = jo.getString("DC");
        IV = jo.getString("IV");
        KEY = jo.getString("KEY");
        Agent = jo.getString("Agent");
    }

    /**
     * @Description 登录游戏
     * @param uid
     * @param model
     * @return
     */
    public String login(String uid, String gameId, String model) {
        JSONObject json = new JSONObject();
        int action = 11;
        long ts = System.currentTimeMillis();
        json.put("action", action);
        json.put("ts", ts);
        json.put("parent", Agent);
        json.put("uid", uid);
        json.put("lang", "ch");

        /** 游戏型态 0: 老虎机 7: 捕鱼机 9: 水果机 **/
        if (StringUtils.isNotBlank(gameId)) {
            json.put("mType", gameId);
            // 判断游戏类型
            if ("7001".equals(gameId) || "7002".equals(gameId) || "7003".equals(gameId)) {
                json.put("gType", "7");
            } else if ("9001".equals(gameId) || "9002".equals(gameId) || "9003".equals(gameId) || "9004".equals(gameId)
                    || "9006".equals(gameId) || "9007".equals(gameId)) {
                json.put("gType", "9");
            } else {
                json.put("gType", "0");
            }
        }

        /**
         * 1: 包含游戏大厅（默认值） ※若未带入 gType 及 mType，则直接到游戏大厅 ※若带入 gType 及 mType 时，直接进入游戏。 2: 不包含游戏大厅，隐藏游戏中的关闭钮 ※gType 及 mType
         * 为必填字段。
         */
        json.put("windowMode", "1");

        if ("MB".equals(model)) {
            json.put("isAPP", true);
        }

        String res = sendPost(API_URL, json, C_METHOD_LOGIN);
        if (ERROR.equals(res)) {
            return ERROR;
        }

        try {
            JSONObject r_json = JSONObject.fromObject(res);
            String GameUrl = r_json.getString("path");
            return GameUrl;

        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    /**
     * @Description 创建用户
     * @param uid
     * @return
     */
    public String createUser(String uid) {
        JSONObject json = new JSONObject();
        int action = 12;
        long ts = System.currentTimeMillis();
        json.put("action", action);
        json.put("ts", ts);
        json.put("parent", Agent);
        json.put("uid", uid);
        json.put("name", uid);
        // 账户初始额度（预设为 0）
        json.put("credit_allocated", 0);

        String res = sendPost(API_URL, json, C_METHOD_CREATE_USER);
        if (ERROR.equals(res)) {
            return ERROR;
        }

        return SUCCESS;
    }

    /**
     * @Description 获取用户余额
     * @param uid
     * @return
     */
    public String getBalance(String uid) {
        JSONObject json = new JSONObject();
        int action = 15;
        long ts = System.currentTimeMillis();
        json.put("action", action);
        json.put("ts", ts);
        json.put("parent", Agent);
        json.put("uid", uid);

        String res = sendPost(API_URL, json, C_METHOD_BALANCE);
        if (ERROR.equals(res)) {
            return ERROR;
        }

        try {
            JSONObject r_json = JSONObject.fromObject(res);
            JSONArray js = JSONUtils.toJSONArray(r_json.get("data"));
            String balance = JSONObject.fromObject(js.get(0)).getString("balance");
            return balance;

        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    /**
     * @Description 转入游戏
     * @param uid
     * @param serialNo
     * @param amount
     * @return
     */
    public String transIn(String uid, String serialNo, int amount) {
        JSONObject json = new JSONObject();
        int action = 19;
        long ts = System.currentTimeMillis();
        json.put("action", action);
        json.put("ts", ts);
        json.put("parent", Agent);
        json.put("uid", uid);

        // 0: 不全部提领（預設值） 1: 全部提领
        json.put("allCashOutFlag", "0");
        json.put("amount", amount);
        json.put("serialNo", serialNo);

        String res = sendPost(API_URL, json, C_METHOD_TRANSIN);
        if (ERROR.equals(res)) {
            return ERROR;
        }

        return SUCCESS;
    }

    /**
     * @Description 转入平台
     * @param uid
     * @param serialNo
     * @param amount
     * @return
     */
    public String transOut(String uid, String serialNo, int amount) {
        JSONObject json = new JSONObject();
        int action = 19;
        long ts = System.currentTimeMillis();
        json.put("action", action);
        json.put("ts", ts);
        json.put("parent", Agent);
        json.put("uid", uid);

        // 0: 不全部提领（預設值） 1: 全部提领
        json.put("allCashOutFlag", "0");
        json.put("amount", -amount);
        json.put("serialNo", serialNo);

        String res = sendPost(API_URL, json, C_METHOD_TRANSOUT);
        if (ERROR.equals(res)) {
            return ERROR;
        }

        return SUCCESS;
    }

    /**
     * @Description 查询游戏列表
     * @return
     */
    public String getGameList() {

        JSONObject json = new JSONObject();
        int action = 49;
        long ts = System.currentTimeMillis();
        json.put("action", action);
        json.put("ts", ts);
        json.put("parent", Agent);

        String res = sendPost(API_URL, json, C_METHOD_GAME_LIST);
        if (ERROR.equals(res)) {
            return ERROR;
        }

        return res;
    }

    /**
     * @Description post请求
     * @param api_url
     * @param v
     * @param actionStr
     * @return
     */
    public String sendPost(String api_url, JSONObject v, String actionStr) {
        HttpClient httpClient = new HttpClient();

        PostMethod postMethod = new PostMethod(api_url);

        String urlParms = "";
        String responseString = "";
        try {
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

            httpClient.getParams().setContentCharset("UTF-8");
            httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
            logger.info("【JDB后端请求待加密数据】：" + v.toString());
            String x = encrypt(v.toString(), KEY, IV);

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();

            nvps.add(new NameValuePair("dc", DC));
            nvps.add(new NameValuePair("x", x));

            String param = "";
            StringBuffer sr = new StringBuffer("");
            for (NameValuePair str : nvps) {
                sr.append(str.getName() + "=");
                sr.append(str.getValue() + "&");
            }

            param = sr.toString().substring(0, sr.length() - 1);
            urlParms = api_url + "?" + param;
            logger.info("【JDB后端请求】：" + urlParms);

            NameValuePair[] data1 = nvps.toArray(new NameValuePair[nvps.size()]);

            postMethod.setRequestBody(data1);
            int status = httpClient.executeMethod(postMethod);

            logger.info("【JDB响应状态】：" + status);
            if (status == 200) {
                InputStream inputStream = postMethod.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str = "";
                while ((str = br.readLine()) != null) {
                    stringBuffer.append(str);
                }
                responseString = stringBuffer.toString();
                logger.info("【JDB响应】：" + responseString);

                JSONObject reultJson = JSONObject.fromObject(responseString);

                if (reultJson.containsKey("status") && "0000".equals(reultJson.getString("status"))) {
                    return responseString;
                } else {
                    setFile(actionStr, urlParms, responseString);
                    return ERROR;
                }
            } else {
                setFile(actionStr, urlParms, responseString);
                return ERROR;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            setFile(actionStr, urlParms, responseString);
            return ERROR;
        }finally {
            postMethod.releaseConnection();
            httpClient.getHttpConnectionManager().closeIdleConnections(0);
        }
    }

    /**
     * 保存文件记录
     * 
     * @param action
     * @param urlParms
     * @param result
     */
    private void setFile(String action, String urlParms, String result) {
        FileLog f = new FileLog();
        Map<String, String> pam = new HashMap<>();
        pam.put("method", action);
        pam.put("requesParams", urlParms);
        pam.put("responseParams", result);
        f.setLog("JDB", pam);
    }

    /**
     * @Description AES加密方法
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        int blockSize = cipher.getBlockSize();

        byte[] dataBytes = data.getBytes("UTF-8");
        int plainTextLength = dataBytes.length;
        if (plainTextLength % blockSize != 0) {
            plainTextLength = plainTextLength + (blockSize - plainTextLength % blockSize);
        }
        byte[] plaintext = new byte[plainTextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
        byte[] encrypted = cipher.doFinal(plaintext);
        return Base64.encodeBase64URLSafeString(encrypted);
    }



}
