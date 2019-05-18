package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

/**
 * @ClassName XHGServiceImpl
 * @Description 皇冠体育新接口
 * @author zw
 * @Date 2018年5月17日 上午11:19:15
 * @version 1.0.0
 */
public class XHGServiceImpl{

    private String api_url;
    private String passkey;
    private String prefix;
    private String UsersiteUrl;

    /** ACCESS_KEY function name **/
    private static final String FN_ACCESS_KEY = "ACCESS_KEY";

    /** GET_CCL function name **/
    private static final String FN_GET_CCL = "GET_CCL";

    /** GET_DEPOSIT function name **/
    private static final String FN_GET_DEPOSIT = "GET_DEPOSIT";

    /** GET_WITHDRAW function name **/
    private static final String FN_GET_WITHDRAW = "GET_WITHDRAW";

    /** GET_TOKEN function name **/
    private static final String FN_GET_TOKEN = "GET_TOKEN";

    private final static Logger logger = LoggerFactory.getLogger(XHGServiceImpl.class);

    public XHGServiceImpl(Map<String, String> pmap) {
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(pmap, "HG");
        JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());
        api_url = jo.getString("api_url");
        passkey = jo.getString("passkey");
        prefix = jo.getString("prefix");
        UsersiteUrl = jo.getString("UsersiteUrl");
    }

//    public static void main(String[] args) {
//        Map<String, String> init = new HashMap<>();
//        init.put("api_url", "http://txia33.sapi99.com/");
//        init.put("passkey", "UhsNV75b3E");
//        init.put("prefix", "aag");
//        init.put("UsersiteUrl", "http://txu33.uv178.com/");
//        System.out.println("JSON配置:" + JSONObject.fromObject(init));
//        Map<String, String> map1 = new HashMap<>();
//        map1.put("HG", JSONObject.fromObject(init).toString());
//
//        XHGServiceImpl hg = new XHGServiceImpl(map1);
//        String username = "bl1lixin001";
//        String billno = "TX" + System.currentTimeMillis();
//        hg.getLogin(username,"PC");
//    }

    /**
     * @Description 此API用于获取token。其他API如登入,存款,提款等都需要token才能施行
     * @param functionName
     * @return
     */
    public String getToken(String functionName) {
        Map<String, String> headParams = new HashMap<>();
        headParams.put("passkey", passkey);
        String result = sendData(api_url + FN_GET_TOKEN + "/" + prefix + "/" + functionName, headParams, FN_GET_TOKEN,
                "token");
        if (isNullOrEmpty(result) || "error".equals(result)) {
            return "error";
        }
        return result;
    }

    /**
     * @Description 登录游戏
     * @param username
     * @param model
     * @return
     */
    public String getLogin(String username, String model) {
        username = username.toLowerCase();
        String result = getToken(FN_ACCESS_KEY);
        if (isNullOrEmpty(result) || "error".equals(result)) {
            return "error";
        }

        Map<String, String> headParams = new HashMap<>();
        headParams.put("token", result);
        String loginUrl = api_url + FN_ACCESS_KEY + "/" + prefix + "/" + prefix + username + "/RMB";

        String access_key = sendData(loginUrl, headParams, FN_ACCESS_KEY, "access_key");
        if (isNullOrEmpty(access_key) || "error".equals(access_key)) {
            return "error";
        }

        String forwordUrl = "";

        if ("PC".equals(model)) {
            forwordUrl = UsersiteUrl + "whg/direct-login.php?" + "activekey=" + access_key + "&acc=" + prefix + username
                    + "&langs=2";
        } else {
            forwordUrl = UsersiteUrl + "mobile/direct-login.php?" + "activekey=" + access_key + "&acc=" + prefix
                    + username + "&langs=2";
        }
        logger.info("getLogin(String username = "+username+", String model) -end return" + forwordUrl);
        return forwordUrl;

    }

    /**
     * @Description 转入游戏
     * @param username
     * @param billno
     * @param amount
     * @return
     */
    public String DEPOSIT(String username, String billno, String amount) {
        username = username.toLowerCase();
        String result = getToken(FN_GET_DEPOSIT);
        if (isNullOrEmpty(result) || "error".equals(result)) {
            return "error";
        }

        Map<String, String> headParams = new HashMap<>();
        headParams.put("token", result);

        String depositUrl = api_url + FN_GET_DEPOSIT + "/" + prefix + "/" + billno + "/" + prefix + username + "/RMB/"
                + amount;

        logger.info("XHG 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（上分）请求链接：{}",depositUrl);
        String type = sendData(depositUrl, headParams, FN_GET_DEPOSIT, "type");
        logger.info("XHG 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（上分）返回结果：{}",type);
        if (isNullOrEmpty(type) || "error".equals(type)) {
            return "error";
        }

        return type;
    }

    /**
     * @Description 游戏转出
     * @param username
     * @param billno
     * @param amount
     * @return
     */
    public String WITHDRAW(String username, String billno, String amount) {
        username = username.toLowerCase();
        String result = getToken(FN_GET_WITHDRAW);
        if (isNullOrEmpty(result) || "error".equals(result)) {
            return "error";
        }

        Map<String, String> headParams = new HashMap<>();
        headParams.put("token", result);

        String depositUrl = api_url + FN_GET_WITHDRAW + "/" + prefix + "/" + billno + "/" + prefix + username + "/RMB/"
                + amount;
        logger.info("XHG 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（下分）请求链接：{}",depositUrl);
        String type = sendData(depositUrl, headParams, FN_GET_WITHDRAW, "type");
        logger.info("XHG 用户【"+ username +"】 ,订单编号【"+ billno +"】 发起第三方转账（下分）返回结果：{}",type);
        if (isNullOrEmpty(type) || "error".equals(type)) {
            return "error";
        }

        return type;
    }

    /**
     * @Description 获取游戏余额 GET_CCL
     * @param username
     * @return
     */
    public String getBalance(String username) {
        String balance = "error";
        username = username.toLowerCase();
        String result = getToken(FN_GET_CCL);
        if (isNullOrEmpty(result) || "error".equals(result)) {
            return "error";
        }

        Map<String, String> headParams = new HashMap<>();
        headParams.put("token", result);

        String depositUrl = api_url + FN_GET_CCL + "/" + prefix + "/" + prefix + username;
        logger.info("XHG 用户【"+ username +"】 , 发起第三方查询余额请求链接：{}",depositUrl);
        balance = sendData(depositUrl, headParams, FN_GET_CCL, "credit_left");
        logger.info("XHG 用户【"+ username +"】 , 发起第三方查询余额返回结果：{}",balance);
        if (isNullOrEmpty(balance) || "error".equals(balance)) {
            return "error";
        }

        return balance;
    }

    /**
     * @Description 发送数据
     * @param Url
     * @param headParams
     * @param action
     * @param keyParse
     * @return
     */
    public String sendData(String Url, Map<String, String> headParams, String action, String keyParse) {
        String param = "";
        StringBuffer sr = new StringBuffer("");
        Set<String> set = headParams.keySet();
        for (String str : set) {
            sr.append(str + "=");
            sr.append(headParams.get(str) + "&");
        }

        param = sr.toString().substring(0, sr.length() - 1);
        String urlParms = Url + " header===>>> " + param;
        logger.info("【HG后端请求】：" + urlParms);
        // String result = httpGet(urlParms, action);
        String result = "";
        try {
            result = readContentFromGet(Url, headParams);
            logger.info("【HG响应】：" + result);
            if (isNullOrEmpty(result)) {
                setFile(action, Url, headParams, result);
                return "error";
            } else {
                String value = getElmentValue(result, keyParse);
                if (isNullOrEmpty(value) || "error".equals(value)) {
                    setFile(action, Url, headParams, result);
                    return "error";
                }
                return value;
            }
        } catch (Exception e) {
            setFile(action, Url, headParams, result);
            return "error";
        }
    }

    /**
     * @Description 解析响应值
     * @param result
     * @param keyName
     * @return
     * @throws Exception
     */
    public String getElmentValue(String result, String keyName) throws Exception {
        String rst = "error";

        JSONObject json = new JSONObject();

        Document doc = DocumentHelper.parseText(result);
        Element root = doc.getRootElement();
        if ("success".equals(root.getName())) {
            json.put("success", "");
            @SuppressWarnings("unchecked")
            Iterator<Element> iterator = root.elementIterator();
            while (iterator.hasNext()) {
                Element e = iterator.next();
                if (keyName.equals(e.getName())) {
                    return e.getStringValue();
                }
            }
        }
        return rst;
    }

    /**
     * @Description 使用Curl（服务器和服务器的通讯方式）请求压缩数据
     * @param getURL
     * @param headParams
     * @return
     * @throws IOException
     */
    public String readContentFromGet(String getURL, Map<String, String> headParams) throws IOException {
        // 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编码
        URL getUrl = new URL(getURL);
        // 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
        // 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        // 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
        connection.setRequestProperty("Accept-encoding", "gzip");
        // connection.setRequestProperty("passkey", "g352PDHWcZ");

        if (headParams != null) {
            for (String key : headParams.keySet()) {
                connection.setRequestProperty(key, headParams.get(key));
            }
        }

        // 服务器
        connection.connect();
        // 取得输入流，并使用Reader读取
        GZIPInputStream in = new GZIPInputStream(connection.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));// 设置编码,否则中文乱码
        StringBuffer str = new StringBuffer();
        String lines;
        while ((lines = reader.readLine()) != null) {
            str.append(lines);
        }
        reader.close();
        // 断开连接
        connection.disconnect();

        return str.toString();
    }

    /**
     * @Description 错误记录
     * @param action
     * @param result
     */
    private void setFile(String action, String getURL, Map<String, String> headParams, String result) {
        FileLog f = new FileLog();
        Map<String, String> pam = new HashMap<>();
        pam.put("method", action);
        pam.put("URL", getURL);
        pam.put("headParams", headParams.toString());
        pam.put("responseParams", result);
        f.setLog("HG", pam);
    }

    /**
     * @Description 是否为空
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        if (str == null || "".equals(str))
            return true;
        else
            return false;
    }

}
