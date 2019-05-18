package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * 功能概要：AGService实现类
 */
public class AGINGameServiceImpl{
    private static String api_url;
    private static String api_url_game;
    private static String api_deskey;
    private static String api_md5key;
    private static String api_cagent;
    private static String actype;
    private static String TransferStatus;

    private final static Logger logger = LoggerFactory.getLogger(AGINGameServiceImpl.class);

    public AGINGameServiceImpl(Map<String, String> pmap) {
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(pmap, "AGIN");
        JSONObject jo = new JSONObject().fromObject(pf.getPlatform_config());
        api_url = jo.getString("api_url").toString();
        api_url_game = jo.getString("api_url_game").toString();
        api_deskey = jo.getString("api_deskey").toString();
        api_md5key = jo.getString("api_md5key").toString();
        api_cagent = jo.getString("api_cagent").toString();
        actype = jo.getString("actype").toString();
        try {
            TransferStatus = jo.getString("TransferStatus").toString();
        } catch (Exception e) {
            TransferStatus = "0";
        }
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
        logger.info("AGIN【检测并创建游戏账号】请求参数==========>" + tagUrl);
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
        logger.info("AGIN【检测并创建游戏账号】响应参数<==========" + info);
        if ("error".equals(info)) {
            FileLog f = new FileLog();
            Map<String, String> map = new HashMap<>();
            map.put("tagUrl", tagUrl);
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
        logger.info("AGIN【查询余额】请求参数==========>" + tagUrl);
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
        logger.info("AGIN【查询余额】响应参数<==========" + info);
        if ("error".equals(info)) {
            FileLog f = new FileLog();
            Map<String, String> map = new HashMap<>();
            map.put("tagUrl", tagUrl);
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
        if (!"1".equals(TransferStatus)) {
            return "system";
        }
        String xmlString = "";
        Document doc = null;
        xmlString = "cagent=" + api_cagent + "/\\\\/method=tc/\\\\/loginname=" + loginname + "/\\\\/billno=" + billno
                + "/\\\\/type=" + type + "/\\\\/credit=" + credit + "/\\\\/actype=" + actype + "/\\\\/password="
                + password + "/\\\\/cur=" + cur;
        String tagUrl = getAGUrl(api_url, xmlString);
        logger.info("AGIN【预备转账】请求参数==========>" + tagUrl);
        xmlString = sendPost(api_cagent, tagUrl);
        if(xmlString.equals("error")) return  "error";
        String info = "";
        String msg = "";
        logger.info("AGIN 预备转账发起HTTP请求响应报文：{}",xmlString);
        try {
            doc = DocumentHelper.parseText(xmlString);
            Element root = doc.getRootElement();
            logger.info("AGIN 预备转账发起HTTP请求解析响应报文：{}",root.toString());
            info = root.attributeValue("info");
            msg = root.attributeValue("msg");
        } catch (DocumentException e) {
            e.printStackTrace();
            logger.info(e.getMessage(),e);
            return "error";
        }
        logger.info("AGIN【预备转账】响应参数<==========" + info);
        if (!"0".equals(info)) {
            FileLog f = new FileLog();
            Map<String, String> map = new HashMap<>();
            map.put("tagUrl", tagUrl);
            map.put("loginname", loginname);
            map.put("actype", actype);
            map.put("billno", billno);
            map.put("type", type);
            map.put("credit", credit);
            map.put("msg", xmlString);
            map.put("Function", "PrepareTransferCredit");
            f.setLog(api_cagent, map);
            return msg;
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
        logger.info("AGIN【确认转账】请求参数==========>" + tagUrl);
        xmlString = sendPost(api_cagent, tagUrl);
        if(xmlString.equals("error")) return  "error";
        String info = "";
        String msg = "";
        try {
            doc = DocumentHelper.parseText(xmlString);
            Element root = doc.getRootElement();
            info = root.attributeValue("info");
            msg = root.attributeValue("msg");
            logger.info("[AGIN] 发起Http请求转账部分响应报文: info ="+ info + "msg ="+ msg);
        } catch (DocumentException e) {
             e.printStackTrace();
             logger.error("[AGIN] 解析HTTP请求响应报文错误,异常错误信息：",e);
        }
        logger.info("AGIN【确认转账】响应参数<==========" + info);
        if (!"0".equals(info)) {
            FileLog f = new FileLog();
            Map<String, String> map = new HashMap<>();
            map.put("tagUrl", tagUrl);
            map.put("loginname", loginname);
            map.put("actype", actype);
            map.put("billno", billno);
            map.put("type", type);
            map.put("credit", credit);
            map.put("msg", xmlString);
            map.put("Function", "TransferCreditConfirm");
            f.setLog(api_cagent, map);
            return msg;
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
        logger.info("AGIN【检查订单状态】请求参数==========>" + tagUrl);
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
            logger.error("[AGIN] 解析响应部分报文异常,异常信息：",e);
            return "1";
        }
        logger.info("AGIN【检查订单状态】响应参数<==========" + info);
        if ("error".equals(info)) {
            FileLog f = new FileLog();
            Map<String, String> map = new HashMap<>();
            map.put("actype", actype);
            map.put("billno", billno);
            map.put("msg", msg);
            map.put("Function", "QueryOrderStatus");
            f.setLog(api_cagent, map);
            return msg;
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
        logger.info("AGIN【获取游戏跳转连接】请求参数==========>" + xmlString);
        xmlString = getAGUrl(api_url_game, xmlString);
        logger.info("AGIN【获取游戏跳转连接】响应参数<==========" + xmlString);
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
        logger.info("AGIN【获取游戏跳转连接】请求参数==========>" + xmlString);
        xmlString = getAGUrl(api_url_game, xmlString);
        logger.info("AGIN【获取游戏跳转连接】响应参数<==========" + xmlString);
        return xmlString;
    }

    /**
     * 发送xml请求到server端
     * 
     * @param tagUrl xml请求数据地址
     * @return null发送失败，否则返回响应内容
     */
    public static String sendPost(String gtype, String tagUrl) {
        // 创建httpclient工具对象
        HttpClient client = new HttpClient();
        // 创建post请求方法
        PostMethod myPost = new PostMethod(tagUrl);
        myPost.addRequestHeader("User-Agent", "WEB_LIB_GI_" + gtype);
        String responseString = null;
        // 设置请求头部类型
        myPost.setRequestHeader("Content-Type", "text/xml");
        myPost.setRequestHeader("charset", "utf-8");
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        client.getHttpConnectionManager().getParams().setSoTimeout(20000);
        try {
            int statusCode = 0;
            try{
                // 设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
                statusCode = client.executeMethod(myPost);
            }catch (Exception e){
                //握手失败，服务方拒绝处理该请求
                logger.info("[AGIN] 发起HTTP请求握手失败！,失败原因：",e);
                if(e.getMessage().equals("Read timed out")) return  ""; return "error";
            }
            // 只有请求成功200了，才做处理
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = myPost.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str = "";
                while ((str = br.readLine()) != null) {
                    stringBuffer.append(str);
                }
                responseString = stringBuffer.toString();
            } else {
                logger.info("[AGIN] 发起HTTP请求响应状态码异常！异常状态码：{}",statusCode);
                if(String.valueOf(statusCode).substring(0,1).equals("2")) return  null;
                return "error";
            }

        } catch (Exception e) {
            logger.error("[AGIN] 读取HTTP响应报文异常！异常错误信息：",e);
        } finally {
            myPost.releaseConnection();
            //立刻释放连接
            client.getHttpConnectionManager().closeIdleConnections(0);
        }
        return responseString;
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
