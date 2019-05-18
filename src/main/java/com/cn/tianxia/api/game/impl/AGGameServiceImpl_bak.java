package com.cn.tianxia.api.game.impl;

import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 功能概要：AGService实现类
 */
public class AGGameServiceImpl_bak {
    /*
     * private static String api_url = "http://gi.tianxgame.com:81/doBusiness.do?"; private static String api_url_game =
     * "http://gci.tianxgame.com:81/forwardGame.do?"; private static String api_deskey="uR7R44Ni"; private static String
     * api_md5key="8XSW0SVZPp0X"; private static String api_cagent="S76_AGIN"
     */

    private static String api_url;
    private static String api_url_game;
    private static String api_deskey;
    private static String api_md5key;
    private static String api_cagent;
    private static String actype;

    private final static Logger logger = LoggerFactory.getLogger(AGGameServiceImpl_bak.class);

    public AGGameServiceImpl_bak(Map<String, String> pmap) {
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(pmap, "AG");
        JSONObject jo = new JSONObject().fromObject(pf.getPlatform_config());
        api_url = jo.getString("api_url").toString();
        api_url_game = jo.getString("api_url_game").toString();
        api_deskey = jo.getString("api_deskey").toString();
        api_md5key = jo.getString("api_md5key").toString();
        api_cagent = jo.getString("api_cagent").toString();
        actype = jo.getString("actype").toString();
    }

    /**
     * 检测并创建游戏账号
     */

    public String CheckOrCreateGameAccout(String loginname, String password, String oddtype, String cur) {
        String xmlString = "";
        Document doc = null;
        xmlString = "cagent=" + api_cagent + "/\\\\/loginname=" + loginname + "/\\\\/method=lg/\\\\/actype=" + actype
                + "/\\\\/password=" + password + "/\\\\/oddtype=" + oddtype + "/\\\\/cur=" + cur;
        String tagUrl = getAGUrl(api_url, xmlString);
        logger.info("AG【检测并创建游戏账号】请求参数==========>" + tagUrl);
        xmlString = sendPost(api_cagent, tagUrl);
        String info = "";
        String msg = "";
        try {
            doc = DocumentHelper.parseText(xmlString);
            Element root = doc.getRootElement();
            info = root.attributeValue("info");
            msg = root.attributeValue("msg");
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        logger.info("AG【检测并创建游戏账号】响应参数<==========" + info);
        if ("error".equals(info)) {
            FileLog f = new FileLog();
            Map<String, String> map = new HashMap<>();
            map.put("tagUrl", "tagUrl");
            map.put("loginname", loginname);
            map.put("actype", actype);
            map.put("oddtype", oddtype);
            map.put("msg", xmlString);
            map.put("Function", "CheckOrCreateGameAccout");
            f.setLog(api_cagent, map);
            return msg;
        } else {
            return info;
        }
    }

    /**
     * 查询余额
     */

    public String GetBalance(String loginname, String password, String cur) {
        String xmlString = "";
        Document doc = null;
        xmlString = "cagent=" + api_cagent + "/\\\\/loginname=" + loginname + "/\\\\/method=gb/\\\\/actype=" + actype
                + "/\\\\/password=" + password + "/\\\\/cur=" + cur;
        String tagUrl = getAGUrl(api_url, xmlString);
        logger.info("AG【查询余额】请求参数==========>" + tagUrl);
        xmlString = sendPost(api_cagent, tagUrl);
        String info = "";
        String msg = "";
        try {
            doc = DocumentHelper.parseText(xmlString);
            Element root = doc.getRootElement();
            info = root.attributeValue("info");
            msg = root.attributeValue("msg");
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        logger.info("AG【查询余额】响应参数<==========" + info);
        if ("error".equals(info)) {
            FileLog f = new FileLog();
            Map<String, String> map = new HashMap<>();
            map.put("tagUrl", "tagUrl");
            map.put("loginname", loginname);
            map.put("actype", actype);
            map.put("msg", msg);
            map.put("Function", "GetBalance");
            f.setLog(api_cagent, map);
            return msg;
        } else {
            return info;
        }
    }

    /**
     * 预备转账
     */

    public String PrepareTransferCredit(String loginname, String billno, String type, String credit, String password,
            String cur) {
        String xmlString = "";
        Document doc = null;
        xmlString = "cagent=" + api_cagent + "/\\\\/method=tc/\\\\/loginname=" + loginname + "/\\\\/billno=" + billno
                + "/\\\\/type=" + type + "/\\\\/credit=" + credit + "/\\\\/actype=" + actype + "/\\\\/password="
                + password + "/\\\\/cur=" + cur;
        String tagUrl = getAGUrl(api_url, xmlString);
        logger.info("AG【预备转账】请求参数==========>" + tagUrl);
        xmlString = sendPost(api_cagent, tagUrl);
        if(xmlString.equals("error") || StringUtils.isEmpty(xmlString)) return  "error";

        logger.info("[AG] 发起预备转账Http请求响应报文：{}",xmlString);
        String info = "";
        String msg = "";
        try {
            doc = DocumentHelper.parseText(xmlString);
            logger.info("[AG] 发起预备转账Http请求响应解密报文：{}",doc);
            Element root = doc.getRootElement();
            info = root.attributeValue("info");
            msg = root.attributeValue("msg");
        } catch (DocumentException e) {
            logger.error(e.getMessage(),e);
            return "error";
        }
        logger.info("AG【预备转账】响应参数<==========" + info);
        if (!"0".equals(info)) {
            return "error";
        } else {
            return info;
        }
    }

    /**
     * 确认转账
     */

    public String TransferCreditConfirm(String loginname, String billno, String type, String credit, String flag,
            String password, String cur) {
        String xmlString = "";
        Document doc = null;
        xmlString = "cagent=" + api_cagent + "/\\\\/loginname=" + loginname + "/\\\\/method=tcc/\\\\/billno=" + billno
                + "/\\\\/type=" + type + "/\\\\/credit=" + credit + "/\\\\/actype=" + actype + "/\\\\/flag=" + flag
                + "/\\\\/password=" + password + "/\\\\/cur=" + cur;
        String tagUrl = getAGUrl(api_url, xmlString);
        logger.info("AG【确认转账】请求参数==========>" + tagUrl);
        xmlString = sendPost(api_cagent, tagUrl);
        if(xmlString.equals("error")) return  "error";
        logger.info("[AG] 发起确认转账Http请求响应报文：{}",xmlString);
        String info = "";
        String msg = "";
        if (StringUtils.isBlank(xmlString)) {
            logger.error("AG【确认转账】请求返回信息为空");
            return "process";
        }
        try {
            doc = DocumentHelper.parseText(xmlString);
            Element root = doc.getRootElement();
            info = root.attributeValue("info");
            msg = root.attributeValue("msg");
            logger.info("[AG] 发起确认转账Http请求响应解密部分报文：info ="+ info + "msg = " + msg);
        } catch (DocumentException e) {
            logger.error(e.getMessage(),e);
            return "process";
        }
        if (!"0".equals(info)) {
            return "process";
        } else {
            return info;
        }
    }

    /**
     * 检查订单状态
     */

    public String QueryOrderStatus(String billno, String cur) {
        String xmlString = "";
        Document doc = null;
        xmlString = "cagent=" + api_cagent + "/\\\\/billno=" + billno + "/\\\\/method=qos" + "/\\\\/actype=" + actype
                + "/\\\\/cur=" + cur;
        String tagUrl = getAGUrl(api_url, xmlString);
        logger.info("AG【检查订单状态】请求参数==========>" + tagUrl);
        xmlString = sendPost(api_cagent, tagUrl);
        String info = "";
        String msg = "";
        if (StringUtils.isBlank(xmlString)) {
            logger.error("AG【确认转账】响应参数为空");
            return "error";
        }
        try {
            doc = DocumentHelper.parseText(xmlString);
            Element root = doc.getRootElement();
            info = root.attributeValue("info");
            msg = root.attributeValue("msg");
        } catch (DocumentException e) {
            e.printStackTrace();
            return "error";
        }
        logger.info("AG【检查订单状态】响应参数<==========" + info);
        if (!"0".equals(info)) {
            FileLog f = new FileLog();
            Map<String, String> map = new HashMap<>();
            map.put("actype", actype);
            map.put("billno", billno);
            map.put("msg", msg);
            map.put("Function", "QueryOrderStatus");
            f.setLog(api_cagent, map);
            return "1";
        } else {
            return info;
        }
    }

    /**
     * 获取游戏跳转连接
     */

    public String forwardGame(String loginname, String password, String dm, String sid, String gameType,
            String handicap) {
        String xmlString = "";
        xmlString = "cagent=" + api_cagent + "/\\\\/loginname=" + loginname + "/\\\\/actype=" + actype
                + "/\\\\/password=" + password + "/\\\\/dm=" + dm + "/\\\\/sid=" + sid + "/\\\\/lang=1/\\\\/gameType="
                + gameType + "/\\\\/oddtype=" + handicap + "/\\\\/cur=CNY";
        logger.info("AG【获取游戏跳转连接】请求参数==========>" + xmlString);
        xmlString = getAGUrl(api_url_game, xmlString);
        logger.info("AG【获取游戏跳转连接】响应参数<==========" + xmlString);
        return xmlString;
    }

    /**
     * 获取游戏跳转连接
     */

    public String forwardMobileGame(String loginname, String password, String dm, String sid, String gameType,
            String handicap) {
        String xmlString = "";
        UUID uuid = UUID.randomUUID();
        xmlString = "cagent=" + api_cagent + "/\\\\/loginname=" + loginname + "/\\\\/actype=" + actype
                + "/\\\\/password=" + password + "/\\\\/dm=" + dm + "/\\\\/sid=" + sid + "/\\\\/lang=1/\\\\/gameType="
                + gameType + "/\\\\/oddtype=" + handicap + "/\\\\/cur=CNY/\\\\/mh5=y/\\\\/session_token="
                + uuid.toString();
        logger.info("AG【获取游戏跳转连接】请求参数==========>" + xmlString);
        xmlString = getAGUrl(api_url_game, xmlString);
        logger.info("AG【获取游戏跳转连接】响应参数<==========" + xmlString);
        return xmlString;
    }

    /**
     * 发送xml请求到server端
     * @return null发送失败，否则返回响应内容
     */
    public static String sendPost(String gtype, String tagUrl) {
        logger.info("[AGIN]游戏发起Http请求开始,gtype:{},tagUrl:{}",gtype,tagUrl);
        HttpClient client = null;
        PostMethod myPost = null;
        client = new HttpClient();
        myPost = new PostMethod(tagUrl);
        try {
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            client.getHttpConnectionManager().getParams().setSoTimeout(20000);
            myPost.addRequestHeader("User-Agent", "WEB_LIB_GI_" + gtype);
            myPost.setRequestHeader("Content-Type", "text/xml");
            myPost.setRequestHeader("charset", "utf-8");
            // 设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
            int statusCode = client.executeMethod(myPost);
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = myPost.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str = "";
                while ((str = br.readLine()) != null) {
                    stringBuffer.append(str);
                }
                return stringBuffer.toString();
            }else{
                logger.info("[AGIN]游戏发起HTTP请求异常,错误状态码：{}",statusCode);
                if(String.valueOf(statusCode).substring(0,1).equals("2")) return  null;
                return  "error";
            }

        } catch (Exception e) {
            logger.info("[AGIN]游戏发起Http请求异常:{}", e.getMessage());
            e.printStackTrace();
            if(e.getMessage().equals("Read timed out")) return  ""; return "error";
        } finally {
            if(myPost != null){
                myPost.releaseConnection();
            }
            client.getHttpConnectionManager().closeIdleConnections(0);
        }
    }

    public static String getAGUrl(String url, String xmlString) {
        String param = "";
        String tagUrl = "";
        String key = "";
        DESEncrypt d = new DESEncrypt(api_deskey);
        try {
            param = d.encrypt(xmlString);
            key = d.getMd5(param + api_md5key);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        tagUrl = url + "params=" + param + "&key=" + key;
        return tagUrl;
    }
}
