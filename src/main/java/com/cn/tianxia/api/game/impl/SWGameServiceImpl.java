/****************************************************************** 
 *
 * Powered By tianxia-online. 
 *
 * Copyright (c) 2018-2020 Digital Telemedia 天下网络 
 * http://www.d-telemedia.com/ 
 *
 * Package: com.cn.tianxia.service 
 *
 * Filename: SWServiceImpl.java
 *
 * Description: TODO(用一句话描述该文件做什么) 
 *
 * Copyright: Copyright (c) 2018-2020 
 *
 * Company: 天下网络科技 
 *
 * @author: Elephone
 *
 * @version: 1.0.0
 *
 * Create at: 2018年10月16日 9:59 
 *
 * Revision: 
 *
 * 2018/10/16 9:59 
 * - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.game.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * @ClassName SWServiceImpl
 * @Description skywind falcon
 * @Author Elephone
 * @Date 2018年10月16日 9:59
 * @Version 1.0.0
 **/
public class SWGameServiceImpl{
    private static Logger logger = LoggerFactory.getLogger(SWGameServiceImpl.class);
    private String country;
    private String url;
    private String username;
    private String password;
    private String secretKey;
    private String currency;
    private String language;
    private boolean isTest;
    private String playmode;


    public SWGameServiceImpl(Map<String, String> pmap) {
        PlatFromConfig pf=new PlatFromConfig();
        pf.InitData(pmap, "SW");
        JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());
        country=jo.getString("country").toString();
        url=jo.getString("url").toString();
        username=jo.getString("username").toString();
        password=jo.getString("password").toString();
        secretKey=jo.getString("secretKey").toString();
        currency=jo.getString("currency").toString();
        language=jo.getString("language").toString();
        isTest= Boolean.parseBoolean(jo.getString("isTest"));
        playmode=jo.getString("playmode").toString();
    }

    public static void main(String[] args) {
        Map<String, Object> init = new HashMap<>();
        init.put("url", "https://api.gcpstg.m27613.com");
        init.put("country", "CN");
        init.put("username", "TIAN_XIA_KE_JI_TEST");
        init.put("password", "ucUhR89BhqBB");
        init.put("secretKey", "31a01c7d-d6ad-4765-815d-16ca01f63bbf");
        init.put("currency", "CNY");
        init.put("language", "zh-cn");
        init.put("isTest", true);
        init.put("playmode", "fun");

        //测试用户注册
        Map<String, String> map1 = new HashMap<>();
        map1.put("SW", JSONObject.fromObject(init).toString());
        SWGameServiceImpl sw = new SWGameServiceImpl(map1);

        String username = "bl1wilson";
        String serial = "TX" + System.currentTimeMillis();
        int credit = 1000000;
        //创建用户
        //System.out.println(sw.createUser(username));
        //根据用户登录游戏
        //System.out.println(sw.loginGame("sw_888t",username));
        //转入
        //System.out.println(sw.transferTo(username,10,serial));
        //转出
        //System.out.println(sw.transferFrom(username,10,serial));
        //查询余额
        //System.out.println(sw.getBalance(username));
        //查询转账订单成功或失败
        System.out.println(sw.queryOrderStatus("TX1539764001148"));
    }

    /**
     *功能描述: 每次请求接口之前获取请求头
     *
     *@Author: Wilson
     *@Date: 2018年10月16日 21:04:25
     * @param
     *@return: java.util.Map<java.lang.String , java.lang.String> 
     **/
    public Map<String,String> getAccessToken(){
        Map<String,Object> paramsMap=new HashMap<>();
        paramsMap.put("username",username);
        paramsMap.put("password",password);
        paramsMap.put("secretKey",secretKey);

        String token = null;
        Map<String,String> header=new HashMap<>();
        try {
            JSONObject res = JSONObject.fromObject(doPost(url + "/v1/login", null, paramsMap));
            if (null!=res&&!res.containsKey("message")){
                token=res.getString("accessToken");
                header.put("X-ACCESS-TOKEN",token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return header;
    }

    /**
     *功能描述:创建游戏账号
     *
     *@Author: Wilson
     *@Date: 2018年10月16日 21:14:11
     * @param username
     *@return: java.lang.String
     **/
    public String createUser(String username) {
        Map<String, Object> paramsMap =new HashMap<>();
        paramsMap.put("code", username);
        paramsMap.put("country", country);
        paramsMap.put("currency", currency);
        paramsMap.put("isTest", isTest);
        paramsMap.put("language", language);
        paramsMap.put("status", "normal");

        String response = "error";
        try {
            JSONObject res = JSONObject.fromObject(doPost(url + "/v1/players", getAccessToken(), paramsMap));
            logger.info("SW电子响应创建账号消息<========"+res.toString());
            if(null!=response&&!res.containsKey("message")){
                response="success";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     *功能描述: 根据玩家获取游戏链接
     *
     *@Author: Wilson
     *@Date: 2018年10月16日 21:23:35
     * @param gameCode
     *@return: java.lang.String
     **/
    public String loginGame(String gameCode,String ag_username){
        String res = "error";
        try {
            logger.info("SW电子请求参数========>"+ag_username+"   "+gameCode);
            JSONObject response = JSONObject.fromObject(doGet(url + "/v1/players/" + ag_username + "/games/" + gameCode + "?playmode=" + playmode, null, getAccessToken()));
            logger.info("SW电子响应登录消息<========"+response.toString());
            if(null!=response&&!response.containsKey("message")){
                res=response.getString("url");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     *功能描述: 转入游戏
     *
     *@Author: Wilson
     *@Date: 2018年10月17日 14:02:06
     * @param ag_username
     * @param amount
     * @param extTrxId
     *@return: java.lang.String
     **/
    public String transferTo(String ag_username,double amount,String extTrxId){
        Map<String, Object> paramsMap =new HashMap<>();
        paramsMap.put("playerCode", ag_username);
        paramsMap.put("currency", currency);
        paramsMap.put("amount", amount);
        paramsMap.put("extTrxId", extTrxId);
        paramsMap.put("isTest", isTest);

        String res = "error";
        try {
            logger.info("SW 用户【"+ username +"】 ,订单编号【"+ extTrxId +"】 发起第三方转账（上分）请求参数：{}",paramsMap);
            JSONObject response = JSONObject.fromObject(doPost(url + "/v1/payments/transfers/in", getAccessToken(), paramsMap));
            logger.info("SW 用户【"+ username +"】 ,订单编号【"+ extTrxId +"】 发起第三方转账（上分）响应结果：{}",response.toString());
            if (null != response && !response.containsKey("message")){
                res="success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return res;
        }
        return res;
    }

    /**
     *功能描述: 从游戏中转出
     *
     *@Author: Wilson
     *@Date: 2018年10月17日 14:06:47
     * @param ag_username
     * @param amount
     * @param extTrxId
     *@return: java.lang.String
     **/
    public String transferFrom(String ag_username,double amount,String extTrxId){
        Map<String, Object> paramsMap =new HashMap<>();
        paramsMap.put("playerCode", ag_username);
        paramsMap.put("currency", currency);
        paramsMap.put("amount", amount);
        paramsMap.put("extTrxId", extTrxId);
        paramsMap.put("isTest", isTest);
        String res = "error";
        try {
            logger.info("SW 用户【"+ username +"】 ,订单编号【"+ extTrxId +"】 发起第三方转账（下分）请求参数：{}",paramsMap);
            JSONObject response = JSONObject.fromObject(doPost(url + "/v1/payments/transfers/out", getAccessToken(), paramsMap));
            logger.info("SW 用户【"+ username +"】 ,订单编号【"+ extTrxId +"】 发起第三方转账（下分）响应结果：{}",response.toString());
            if (null != response && !response.containsKey("message")){
                res="success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return res;
        }
        return res;
    }

    /**
     *功能描述: 查询玩家余额
     *
     *@Author: Wilson
     *@Date: 2018年10月17日 15:08:58
     * @param ag_username
     *@return: java.lang.String
     **/
    public String getBalance(String ag_username){
        String balances = "error";
        try {
            logger.info("SW 用户【"+ username +"】 , 发起查询余额请求");
            JSONObject response = JSONObject.fromObject(doGet(url + "/v1/players/" + ag_username, null, getAccessToken()));
            logger.info("SW 用户【"+ username +"】 , 发起查询余额响应结果:{}",response.toString());
            if(response.containsKey("balances")){
                balances = JSONObject.fromObject(JSONObject.fromObject(response.getString("balances")).getString("CNY")).getString("main");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return balances;
        }
        return balances;
    }

    /**
     *功能描述: 查询转账订单状态(成功或失败)
     *
     *@Author: Wilson
     *@Date: 2018年10月17日 15:12:39
     * @param billno
     *@return: java.lang.String
     **/
    public String queryOrderStatus(String billno){
        String res="error";
        try {
            JSONObject response = JSONObject.fromObject(doGet(url + "/v1/payments/" + billno, null, getAccessToken()));
            logger.info("SW电子响应查询订单状态消息<========"+response.toString());
            if (response.containsKey("status")){
                String status=response.getString("status");
                if("absent".equals(status)){
                    res="faild";
                }else if("committed".equals(status)){
                    res="success";
                }else if("processing".equals(status)){
                    res="process";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return res;
        }
        return res;
    }

    /**
     *功能描述: 查询游戏列表
     *
     *@Author: Wilson
     *@Date: 2018年10月17日 15:07:16
     * @param limit
     *@return: java.lang.String
     **/
    public String getGameList(String limit) throws  Exception{
        String res = doGet(url + "/v1/games/info/search?limit="+limit, null,getAccessToken());
        return res;
    }

    public static String doPost(String url, Map<String, String> headerMap, Map<String, Object> paramsMap) {
        org.apache.http.client.HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            // 设置头部参数。
            if (headerMap != null) {
                Iterator iterator = headerMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    httpPost.setHeader(entry.getKey().toString(), entry.getValue().toString());
                }
            }
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if(paramsMap != null){//设置参数
                Iterator iterator = paramsMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    nvps.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            }

            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "UTF-8");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String doGet(String url, Map<String, Object> paramsMap, Map<String, String> headerMap) throws Exception {
        org.apache.http.client.HttpClient httpClient = null;
        HttpGet httpGet = null;
        String result = null;
        try {
            httpClient = HttpClients.createDefault();

            httpGet = new HttpGet();
            httpGet.setURI(new URI(url));
            // 设置头部参数。
            if (headerMap != null) {
                Iterator iterator = headerMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    httpGet.setHeader(entry.getKey().toString(), entry.getValue().toString());
                }
            }
            //设置请求参数
            if(null != paramsMap){
                //HttpParams
            }
            HttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "UTF-8");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw  ex;
        }finally {
            httpGet.releaseConnection();
        }
        return result;
    }

}
