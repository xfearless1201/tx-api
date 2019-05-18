package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;
import com.cn.tianxia.api.utils.gst.StringUtils;
import com.cn.tianxia.api.utils.jf.DESUtil;
import com.cn.tianxia.api.utils.jf.MD5;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName JFGameServiceImpl
 * @Description JF游戏
 * @author Hardy
 * @Date 2019年2月9日 下午4:33:14
 * @version 1.0.0
 */
public class JFGameServiceImpl{
    private Logger logger = LoggerFactory.getLogger(JFGameServiceImpl.class);
    // 请求地址 //http://port2.zzyxpot.com
    private static String apiUrl ;
    // 登录游戏地址
    private static String loginUrl ;
    // 代理号
    private static String agent ;
    // 盘口, 设定新玩家可下注的范围
    private static String handicap ;
    // 货币种类
    private static String currency ;
    // 语言
    private static String Lang;
    // des加密值
    private static String desKey ;
    // 用户新增md5key
    private static String createKey ;
    // 用户转账md5key
    private static String transferKey ;
    // 用户余额查询md5key
    private static String balanceKey ;
    // 用户转账状态md5key
    private static String chkOrderKey ;
    // 修改会员信息md5key
    private static String upadKey ;
    // 进入游戏md5key
    private static String loginKey;
    // 下注记录md5key
    private static String getRecordKey;
    // 牛牛报表md5key
    private static String getRptGameNinKey;
    
    public JFGameServiceImpl(Map<String, String> pmap){
        if(pmap!=null){
            PlatFromConfig pf=new PlatFromConfig();
            //TODO 测试
//          pf.setPlatform_config(JSONObject.fromObject(pmap).toString());
            pf.InitData(pmap, "JF");
            JSONObject jo=new JSONObject().fromObject(pf.getPlatform_config());
            apiUrl=jo.getString("apiUrl");
            loginUrl=jo.getString("loginUrl");
            agent=jo.getString("agent");
            handicap=jo.getString("handicap");
            currency=jo.getString("currency");
            Lang=jo.getString("Lang");
            desKey=jo.getString("desKey");
            createKey=jo.getString("createKey");
            transferKey=jo.getString("transferKey");
            balanceKey=jo.getString("balanceKey");
            chkOrderKey=jo.getString("chkOrderKey");
            upadKey=jo.getString("upadKey");
            loginKey=jo.getString("loginKey");
            getRecordKey=jo.getString("getRecordKey");
            getRptGameNinKey=jo.getString("getRptGameNinKey");
        }
    }

    /**
     * 创建用户
     * 
     * @param userName
     * @param password
     * @param nickName
     * @return
     */
    public String CreateUser(String userName, String password, String nickName) {
        // 拼接参数格式
        long timestamp = System.currentTimeMillis();
        String paramString = null;
        String DES = "";
        try {
            StringBuilder param = new StringBuilder();
            param.append("agent=");
            param.append(agent);
            param.append("&username=");
            param.append(userName);
            param.append("&Handicap=");
            param.append(handicap);
            if (!StringUtils.isNullOrEmpty(password)) {
                param.append("&password=");
                param.append(password);
            }
            param.append("&currency=");
            param.append(currency);
            if (!StringUtils.isNullOrEmpty(nickName)) {
                param.append("&nickname=");
                param.append(nickName);
            }
            paramString = new DESUtil(desKey).encrypt(param.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd");
        String B = MD5.GetMD5Code(agent + userName + createKey + myFmt.format(new Date()));
        String key = getKey(8, B, 2);
        // 构建请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("param", DES);
        paramMap.put("key", key);
        String result = "";
        String cont = "param=" + paramString + "&key=" + key;
        result = sendPost(apiUrl + agent + "/CreateUser", cont, "CreateUser");
        if (StringUtils.isNullOrEmpty(result)) {
            saveFile(apiUrl + agent + "/GetRptGameNiu", cont, "result", result);
            return "";
        }
        // 解析返回值
        /*2017年10月25日 20:06:42 接口文档更新 会员登录无需加代理前缀*/
//      JSONObject json = new JSONObject();
//      json = JSONObject.fromObject(result);
//      // 此用户名用于登录游戏
//      String loginName = "";
//      if (json.getString("Success").equals("true") && json.getString("Code").equals("1")) {
//          logger.info("JF创建用户成功！ -->" + result);
//          loginName = json.getString("Username");
//          return loginName;
//      } else if (json.getString("Code").equals("200")) {// 用户已经存在
//          loginName = json.getString("Username");
//          logger.info("JF创建用户已经存在 -->:" + result);
//          return loginName;
//      } else {
//          logger.info("JF创建用户失败 -->:" + result);
//          return "";
//      }
        
        return result;
    }

    /**
     * 转账
     * 
     * @param loginName
     * @param password
     * @param billNo
     * @param action
     * @param credit
     * @return
     */
    public String Transfer(String loginName, String password, String billNo, String action, String credit) {
        long timestamp = System.currentTimeMillis();
        StringBuilder param = new StringBuilder();
        param.append("agent=");
        param.append(agent);
        param.append("&username=");
        param.append(loginName);
        param.append("&password=");
        param.append(password);
        param.append("&action=").append(action);
        param.append("&credit=").append(credit);
        param.append("&billNo=").append(billNo);
        param.append("&timestamp=");
        param.append(timestamp);
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd");
        String B = MD5.GetMD5Code(agent + loginName + billNo + transferKey  + myFmt.format(new Date()));
        String key = getKey(6, B, 5);
        String paramString = null;
        try {
            paramString = new DESUtil(desKey).encrypt(param.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String cont = "param=" + paramString + "&key=" + key;
        logger.info("JF 用户【"+ loginName +"】 ,订单编号【"+ billNo +"】 发起第三方转账（上下分）请求参数:{}", cont);
        String result = sendPost(apiUrl + agent + "/Transfer", cont, "Transfer");
        logger.info("JF 用户【"+ loginName +"】 ,订单编号【"+ billNo +"】 发起第三方转账（上下分）返回结果:{}", result);
        if (StringUtils.isNullOrEmpty(result)) {
            saveFile(apiUrl + agent + "/Transfer", cont, "result", result);
            return "";
        }
        // 解析返回值
        JSONObject json = new JSONObject();
        json = JSONObject.fromObject(result);

        if (json.getString("Success").equals("true") && json.getString("Code").equals("1")) {
            // loginName = json.getString("Username");
            return "success";
        } else {
            logger.info("JF转账失败！ -->");
            saveFile(apiUrl + agent + "/Transfer", cont, "result", result);
            return "";
        }
    }

    /**
     * 查询订单状态
     * 
     * @param billNo
     * @return
     */
    public String ChkOrderStatus(String billNo) {
        long timestamp = System.currentTimeMillis();
        StringBuilder param = new StringBuilder();
        param.append("agent=");
        param.append(agent);
        param.append("&billNo=").append(billNo);
        param.append("&timestamp=");
        param.append(timestamp);

        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd");
        String B = MD5.GetMD5Code(agent + billNo + chkOrderKey + myFmt.format(new Date()));
        String key = getKey(9, B, 2);

        String paramString = null;
        try {
            paramString = new DESUtil(desKey).encrypt(param.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String cont = "param=" + paramString + "&key=" + key;
        String result = sendPost(apiUrl + agent + "/ChkOrderStatus", cont, "ChkOrderStatus");
        
        
        if (StringUtils.isNullOrEmpty(result)) {
            saveFile(apiUrl + agent + "/ChkOrderStatus", cont, "result", result);
            return "";
        }
        
        // 解析返回值
        JSONObject json = new JSONObject();
        json = JSONObject.fromObject(result);

        if (json.getString("Success").equals("true") && json.getString("Code").equals("1")) {
            return result;
        } else {
            logger.info("JF转账状态失败！ -->");
            saveFile(apiUrl + agent + "/ChkOrderStatus", cont, "result", result);
            return "";
        }
    }

    /**
     * 登录游戏
     * 
     * @param loginName
     * @param password
     * @param Model
     * @return
     */
    public String loginGame(String loginName, String password, String Model) {
        long timestamp = System.currentTimeMillis();
        StringBuilder param = new StringBuilder();
        param.append("agent=");
        param.append(agent);
        param.append("&username=");
        param.append(loginName);
        if (!StringUtils.isNullOrEmpty(password)) {
            param.append("&password=");
            param.append(password);
        }
        param.append("&timestamp=");
        param.append(timestamp);
        if (!StringUtils.isNullOrEmpty(Model)) {
            param.append("&Model=");
            param.append(Model);
        }
        if (!StringUtils.isNullOrEmpty(Lang)) {
            param.append("&Lang=");
            param.append(Lang);
        }
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd");
        String B = MD5.GetMD5Code(agent + loginName + loginKey + myFmt.format(new Date()));
        String key = getKey(2, B, 8);

        String paramString = null;
        try {
            paramString = new DESUtil(desKey).encrypt(param.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String link = loginUrl + agent + "/Web/Login" + "?param=" + paramString + "&key=" + key;
        return link;
    }

    /**
     * 查询用户余额
     * 
     * @param loginName
     * @param password
     * @return
     */
    public String GetBalance(String loginName, String password) {
        long timestamp = System.currentTimeMillis();
        StringBuilder param = new StringBuilder();
        param.append("agent=");
        param.append(agent);
        param.append("&username=");
        param.append(loginName);
        if (!StringUtils.isNullOrEmpty(password)) {
            param.append("&password=");
            param.append(password);
        }
        param.append("&timestamp=");
        param.append(timestamp);

        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd");
        String B = MD5.GetMD5Code(agent + loginName + balanceKey + myFmt.format(new Date()));
        String key = getKey(5, B, 7);

        
        String paramString = null;
        try {
            paramString = new DESUtil(desKey).encrypt(param.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String cont = "param=" + paramString + "&key=" + key;
        logger.info("JF 用户【"+ loginName +"】 ,发起查询余额请求参数:{}", cont);
        String result = sendPost(apiUrl + agent + "/GetBalance", cont, "GetBalance");
        logger.info("JF 用户【"+ loginName +"】 ,发起查询余额响应结果:{}", result);

        if (StringUtils.isNullOrEmpty(result)) {
            saveFile(apiUrl + agent + "/GetBalance", cont, "result", result);
            return "维护中";
        }

        // 解析返回值
        JSONObject json = new JSONObject();
        json = JSONObject.fromObject(result);

        if (json.getString("Success").equals("true") && json.getString("Code").equals("1")) {
            logger.info("JF查询余额成功！ -->");
            return json.getString("Balance");
        } else {
            logger.info("JF查询余额失败！ -->");
            saveFile(apiUrl + agent + "/GetBalance", cont, "result", result);
            return "维护中";
        }
    }

    /**
     * 获取下注记录
     * 
     * @param username
     *            会员名称
     * @param starttime
     *            开始时间(yyyy-MM-dd HH:mm:ss)
     * @param endtime
     *            结束时间(yyyy-MM-dd HH:mm:ss)
     * @param gametype
     *            游戏类型说明
     * @param gamecode
     *            游戏代码
     * @param page
     *            页码
     * @param pagesize
     *            每页数量（一次最多取 500 条记录）
     * @return
     */
    public String BetRecord(String username, String starttime, String endtime, String gametype, String gamecode,
            String page, String pagesize) {
        long timestamp = System.currentTimeMillis();
        StringBuilder param = new StringBuilder();
        param.append("agent=");
        param.append(agent);
        if (!StringUtils.isNullOrEmpty(username)) {
            param.append("&username=");
            param.append(username);
        }
        if (!StringUtils.isNullOrEmpty(gamecode)) {
            param.append("&gamecode=");
            param.append(gamecode);
        }
        param.append("&starttime=");
        param.append(starttime);
        param.append("&endtime=");
        param.append(endtime);
        param.append("&gametype=");
        param.append(gametype);
        param.append("&page=");
        param.append(page);
        param.append("&pagesize=");
        param.append(pagesize);
        param.append("&timestamp=");
        param.append(timestamp);

        String paramString = null;
        try {
            paramString = new DESUtil(desKey).encrypt(param.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd");
        String B = MD5.GetMD5Code(agent + starttime + endtime + getRecordKey  + myFmt.format(new Date()));
        String key = getKey(9, B, 5);
        String result = "";
        String cont = "param=" + paramString + "&key=" + key;
        result = sendPost(apiUrl + agent + "/BetRecord", cont, "BetRecord");

        if (StringUtils.isNullOrEmpty(result)) {
            saveFile(apiUrl + agent + "/BetRecord", cont, "result", result);
            return "";
        }
        // 解析返回值
        JSONObject json = new JSONObject();
        json = JSONObject.fromObject(result);
        if (json.getString("Success").equals("true") && json.getString("Code").equals("1")) {
            return result;
        } else {
            logger.info("JF查询注单失败！ -->");
            saveFile(apiUrl + agent + "/BetRecord", cont, "result", result);
            return "";
        }
    }

    /**
     * 获取牛牛日报表
     * 
     * @param username
     *            会员名称
     * @param starttime
     *            开始时间(yyyy-MM-dd)
     * @param endtime
     *            结束时间(yyyy-MM-dd)
     * @param gamecode
     *            游戏代码
     * @param page
     *            页码
     * @param pagesize
     *            每页数量（一次最多取 500 条记录）
     * @return
     */
    public String GetRptGameNiu(String username, String starttime, String endtime, String gamecode, String page,
            String pagesize) {
        long timestamp = System.currentTimeMillis();
        StringBuilder param = new StringBuilder();
        param.append("agent=");
        param.append(agent);
        if (!StringUtils.isNullOrEmpty(username)) {
            param.append("&username=");
            param.append(username);
        }
        if (!StringUtils.isNullOrEmpty(gamecode)) {
            param.append("&gamecode=");
            param.append(gamecode);
        }
        param.append("&starttime=");
        param.append(starttime);
        param.append("&endtime=");
        param.append(endtime);
        param.append("&page=");
        param.append(page);
        param.append("&pagesize=");
        param.append(pagesize);
        param.append("&timestamp=");
        param.append(timestamp);
        
        String paramString = null;
        try {
            paramString = new DESUtil(desKey).encrypt(param.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd");
        String B = MD5.GetMD5Code(agent + starttime + endtime + getRptGameNinKey  + myFmt.format(new Date()));
        String key = getKey(4, B, 3);
        String result = "";
        String cont = "param=" + paramString + "&key=" + key;
        result = sendPost(apiUrl + agent + "/GetRptGameNiu", cont, "GetRptGameNiu");

        if (StringUtils.isNullOrEmpty(result)) {
            saveFile(apiUrl + agent + "/GetRptGameNiu", cont, "result", result);
            return "";
        }

        // 解析返回值
        JSONObject json = new JSONObject();
        json = JSONObject.fromObject(result);
        if (json.getString("Success").equals("true") && json.getString("Code").equals("1")) {
        
            return result;
        } else {
            saveFile(apiUrl + agent + "/GetRptGameNiu", cont, "result", result);
            logger.info("JF查询牛牛注单失败！ -->");
            return "";
        }
    }
    /**
     * 更新用户资料
     * @param username
     * @param password
     * @param nickname
     * @return
     */
    public String UpdUserInfo(String username, String password, String nickname) {
        // 拼接参数格式
        long timestamp = System.currentTimeMillis();
        String paramString = null;
        String DES = "";
        try {
            StringBuilder param = new StringBuilder();
            param.append("agent=");
            param.append(agent);
            if (!StringUtils.isNullOrEmpty(password)) {
                param.append("&username=");
                param.append(username);
            }
            param.append("&Handicap=");
            param.append(handicap);

            if (!StringUtils.isNullOrEmpty(password)) {
                param.append("&password=");
                param.append(password);
            }
            if (!StringUtils.isNullOrEmpty(nickname)) {
                param.append("&nickname=");
                param.append(nickname);
            }
        
            paramString = new DESUtil(desKey).encrypt(param.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd");
        String B = MD5.GetMD5Code(agent + username + upadKey + myFmt.format(new Date()));
        String key = getKey(5, B, 7);
        // 构建请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("param", DES);
        paramMap.put("key", key);

        String result = "";

        String cont = "param=" + paramString + "&key=" + key;
        result = sendPost(apiUrl + agent + "/UpdUserInfo", cont, "UpdUserInfo");
        
        if (StringUtils.isNullOrEmpty(result)) {
            saveFile(apiUrl + agent + "/UpdUserInfo", cont, "result", result);
            return "";
        }

        // 解析返回值
        JSONObject json = new JSONObject();
        json = JSONObject.fromObject(result);

        // 此用户名用于登录游戏
//      String loginName = "";
        if (json.getString("Success").equals("true") && json.getString("Code").equals("1")) {
            
            return result;
        } else {
            logger.info("JF更新用户失败 -->" );
            saveFile(apiUrl + agent + "/UpdUserInfo", cont, "result", result);
            return "";
        }
    }

    /**
     * post 请求
     * 
     * @param postUrl
     * @param context
     * @param method
     * @return
     */
    public String sendPost(String postUrl, String context, String method) {
        logger.info("post请求参数 -->   URL:" + postUrl + "    --->>send data:" + context);
        java.net.URL url;
        java.net.HttpURLConnection connection = null;

        StringBuffer response = new StringBuffer();
        try {
            // surl为请求地址
            url = new URL(postUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(context.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-CN");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(5 * 1000);
            connection.setReadTimeout(5 * 1000);

            // Send request
            java.io.DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            if (context == null)
                context = "";
            wr.writeBytes(context);
            wr.flush();
            wr.close();

            // Get Response
            java.io.InputStream is = connection.getInputStream();
            java.io.BufferedReader rd = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String line;

            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            logger.info("JF请求返回值 --> " + response.toString());
            return response.toString();

        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            saveFile(postUrl, context, method, response.toString());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 获取指定格式key
     * 
     * @param leftLength
     * @param key
     * @param rightLength
     * @return
     */
    public String getKey(int leftLength, String key, int rightLength) {
        String leftStr = genRandomNum(leftLength);
        String rightStr = genRandomNum(rightLength);
    
        return leftStr + key + rightStr;
    }

    /**
     * 保存文件日志
     * 
     * @param apiUrl
     * @param data
     * @param data
     * @param response
     */
    private void saveFile(String apiUrl, String data, String function, String response) {
        FileLog f = new FileLog();
        Map<String, String> map = new HashMap<>();
        map.put("tagUrl", apiUrl);
        map.put("Data", data);
        map.put("Function", function);
        map.put("response", response);
        f.setLog("JF", map);
    }

    /**
     * 生成随即密码
     * 
     * @param pwd_len
     *            生成的密码的总长度
     * @return 密码的字符串
     */
    public static String genRandomNum(int pwd_len) {
        // 35是因为数组是从0开始的，26个字母+10个数字
        final int maxNum = 36;
        int i; // 生成的随机数
        int count = 0; // 生成的密码的长度
        char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
                't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while (count < pwd_len) {
            // 生成随机数，取绝对值，防止生成负数，
            i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }
        return pwd.toString();
    }

}
