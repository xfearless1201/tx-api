package com.cn.tianxia.api.game.impl;


import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.cn.tianxia.api.utils.PlatFromConfig;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import sun.misc.BASE64Encoder;

/**
 *
 * @ClassName IGPJServiceImpl
 * @Description NWG 新世界棋牌
 * @author Jacky
 * @Date 2019年3月11日 下午4:31:46
 * @version 1.0.0
 */
public class NWGGameServiceImpl {

    private final static Logger logger = LoggerFactory.getLogger(NWGGameServiceImpl.class);

    private  String agent ;
    private  String deskey ;
    private  String md5key ;
    private  String api_url;

    private static final String param_agent = "agent=";
    private static final String param_timestamp = "timestamp=";
    private static final String param_param = "param=";
    private static final String param_key = "key=";

    private static final String  ERROR = "error";
    private static final String  SUCCESS = "success";
    private static final String  FAILD = "faild";
    private static final String  PROCESS = "process";

    public NWGGameServiceImpl(Map<String,String> map){
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(map, "NWG");
        net.sf.json.JSONObject jo = net.sf.json.JSONObject.fromObject(pf.getPlatform_config());
        if(jo != null&& !jo.isEmpty()){
            if(jo.containsKey("api_url")){
                this.api_url = jo.getString("api_url");
            }
            if(jo.containsKey("deskey")){
                this.deskey = jo.getString("deskey");
            }
            if(jo.containsKey("md5key")){
                this.md5key = jo.getString("md5key");
            }
            if(jo.containsKey("agent")){
                this.agent = jo.getString("agent");
            }
        }
    }


    static SimpleDateFormat  simpleDateFormat;
    static StringBuilder     stringBuilder;

    static {
        simpleDateFormat  = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        stringBuilder     = new StringBuilder();
    }


    public String checkOrCreateAccount(GameForwardVO gameForwardVO) throws  Exception{
        logger.info("checkOrCreateAccount(GameForwardVO gameForwardVO = {} -start",gameForwardVO);
        if(StringUtils.isEmpty(gameForwardVO.getAg_username()) || StringUtils.isEmpty(gameForwardVO.getGameId())){
            throw  new Exception("[NWG] 游戏账号或游戏ID不能为空！");
        }
        String gameUrl = null;
        try{
            String timestamp = String.valueOf(System.currentTimeMillis());
             stringBuilder = new StringBuilder().append("s=0").append("&")
                    .append("account=").append(gameForwardVO.getAg_username()).append("&")
                    .append("money=0").append("&").append("orderid=").append(agent+ simpleDateFormat.format(new Date())+ gameForwardVO.getAg_username())
                    .append("&").append("ip=").append(gameForwardVO.getIp()).append("&").append("lineCode=1").append("&").append("KindID=").append(gameForwardVO.getGameId()) ;
            logger.info("[NWG] 代加密参数串:{}",stringBuilder.toString());

            String url = formatUrl(timestamp,AESEncrypt(stringBuilder.toString(),deskey),MD5(agent + timestamp + md5key)).toString();
            logger.info("[NWG] HTTP请求加密串：{}",url);
            String returnParams = NWGGet(url);
            logger.info("[NWG] 发起HTTP请求响应报文：{}",returnParams);
            if(returnParams == null){
                gameUrl = ERROR;
            }
            JSONObject jsonObject = JSONObject.parseObject(returnParams);

            if(jsonObject.containsKey("d")){
               JSONObject jsonObject1 =  JSONObject.parseObject(jsonObject.get("d").toString());
               if(jsonObject1.containsKey("code")&& jsonObject1.get("code").toString().equals("0")){
                    gameUrl = jsonObject1.getString("url");
               }else{
                    gameUrl = ERROR;
               }
            }

        }catch (Exception e){
            logger.info("[NWG] 获取游戏链接错误!",e.getMessage());
            throw  e;
        }
        logger.info("checkOrCreateAccount()  -return:{}",gameUrl);
        return  gameUrl;

    }

    public String getBalance(String account) throws  Exception{
        logger.info("[NWG] getBalance(String account = {} -start",account);
        if(StringUtils.isEmpty(account))  throw  new Exception("玩家账号不能为空");

        String balance = ERROR;
        try{
            String timestamp = String.valueOf(System.currentTimeMillis());
            stringBuilder = new StringBuilder().append("s=1").append("&").append("account=").append(account);
            String url = formatUrl(timestamp,AESEncrypt(stringBuilder.toString(),deskey),MD5(agent+ timestamp +md5key)).toString();

            String retuanJson = NWGGet(url);
            if(retuanJson !=null && retuanJson != ERROR){

                JSONObject jsonObject = JSONObject.parseObject(retuanJson);
                if(jsonObject.containsKey("d")){

                    JSONObject data = JSONObject.parseObject(jsonObject.getString("d"));
                    if(data.getString("code").equals("0")){
                        balance = data.getString("money");
                    }
                }
            }
        }catch (Exception e){
            logger.error("[NWG] 获取游戏余额失败：",e.getMessage());
            throw  e;
        }
        logger.info("[NWG] getBalance(String account) return:{}",balance);
        return  balance;
    }


    public String transferIn(GameTransferVO gameTransferVO)throws  Exception{
       return transfer(gameTransferVO,"2");
    }

    public String transferOut(GameTransferVO gameTransferVO)throws  Exception{
        return  transfer(gameTransferVO,"3");
    }


    public String transfer(GameTransferVO gameTransferVO ,String  s)throws  Exception{
        logger.info("[NWG] transferIn(GameTransferVO gameTransferVO =  {} -start",gameTransferVO);
        if(StringUtils.isEmpty(gameTransferVO.getAg_username())||
                StringUtils.isEmpty(gameTransferVO.getMoney() )||
                        StringUtils.isEmpty(gameTransferVO.getBillno()))  throw  new Exception("[NWG] 游戏上下分失败,上下分所需参数不足！");
        try{
            String timestamp = String.valueOf(System.currentTimeMillis());

            stringBuilder = new StringBuilder().append("s=").append(s).append("&")
                    .append("account=").append(gameTransferVO.getAg_username()).append("&")
                    .append("money=").append(gameTransferVO.getMoney()).append("&")
                    .append("orderid=").append(gameTransferVO.getBillno());

           String responseString = NWGGet(formatUrl(timestamp,AESEncrypt(stringBuilder.toString(),deskey),
                   MD5(agent +timestamp + md5key)).toString());

           if(responseString == ERROR)  return  FAILD;
           if(responseString == null){
              return  loopOrder(gameTransferVO.getBillno());
           }

           JSONObject json = JSONObject.parseObject(responseString);
           if(json.containsKey("d")){
              JSONObject data = JSONObject.parseObject(json.getString("d"));

              if(data.getString("code").equals("0")){
                 return  loopOrder(gameTransferVO.getBillno());
              }
           }
            return  FAILD;
        }catch (Exception e){
            logger.error("[NWG] 游戏上下分错误：",e.getMessage());
            throw e;
        }

    }

    private String loopOrder(String orderNo) throws Exception{
        logger.info("[NWG] loopOrder(String orderNo = {} -start",orderNo);
        int loop = 0;
        if(querOderStatus(orderNo) == SUCCESS) return SUCCESS;
        String orderStatus;
        while (loop < 2){
            Thread.sleep(3000);
               loop++;
               orderStatus = querOderStatus(orderNo);
               if(orderStatus == SUCCESS)  return  SUCCESS;
               if(orderStatus == FAILD)    return  FAILD;
               if(loop > 1)                return  PROCESS;

        }
        return  PROCESS;
    }


    public String querOderStatus(String  orderNo)throws  Exception{
        logger.info("[NWG] querOderStatus(String  orderNo = {} -start",orderNo);

        String timestamp = String.valueOf(System.currentTimeMillis());
        stringBuilder = new StringBuilder("s=4").append("&").append("orderid=").append(orderNo);

        String responseString = NWGGet(formatUrl(timestamp,AESEncrypt(stringBuilder.toString(),deskey),MD5(agent + timestamp + md5key)).toString());

        if(responseString == null || responseString == ERROR)  return  PROCESS;
        JSONObject jsonObject = JSONObject.parseObject(responseString);

        if(jsonObject.containsKey("d")){

            JSONObject data = JSONObject.parseObject(jsonObject.getString("d"));
            if(data.getString("code").equals("0"))   return  SUCCESS;
            if(data.getString("code").equals("-1")||data.getString("code").equals("2"))  return  FAILD;
            if(data.getString("code").equals("3"))   return  PROCESS;
        }
        return  PROCESS;
    }



    private static String MD5(String sourceStr) throws  Exception {
        logger.info("NWGMD5(String sourceStr = {} -start", sourceStr);
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0 , length  = b.length; offset < length ; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("[NWG] 加密错误"+ e.getMessage(),e);
            throw  e;
        } catch (UnsupportedEncodingException e) {
            logger.error("[MWG] 转换字节码错误！"+ e.getMessage(),e);
            throw  e;
        }
        return result;
    }


    private static String AESEncrypt(String value,String key) throws Exception {
        logger.info("[NWG] AESEncrypt(String value,String key = {},{} -start" , value, key);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        byte[] raw = key.getBytes("UTF-8");

        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));

        String base64 = new BASE64Encoder().encode(encrypted);

        return URLEncoder.encode(base64, "UTF-8");

    }


    StringBuilder formatUrl(String timestamp,String param,String key){

        return  stringBuilder = new StringBuilder(api_url).append(param_agent).append(agent).append("&")
                .append(param_timestamp).append(timestamp).append("&")
                .append(param_param).append(param).append("&")
                .append(param_key).append(key);
    }



    public String NWGGet(String tagUrl) throws Exception {
        logger.info("NWGGet(String tagUrl  = {} -start", tagUrl);
        HttpClient client = new HttpClient();
        GetMethod myGet = new GetMethod(tagUrl);
        String responseString = null;
        try {
            myGet.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            myGet.setRequestHeader("charset", "utf-8");
            int statusCode = client.executeMethod(myGet);
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = myGet.getResponseBodyAsStream();
                responseString = IOUtils.toString(inputStream,Consts.UTF_8);
                logger.info("[NWG] HTTP请求握手成功！响应报文：{}",responseString);
            } else {
              logger.info("[NWG] 发起HTTP请求握手失败！失败状态码: {}", statusCode);
              responseString = ERROR;
            }
        } catch (Exception e) {
           logger.error("[NWG] 发起HTTP请求错误！",e);
            responseString = ERROR;
        } finally {
            myGet.releaseConnection();
            client.getHttpConnectionManager().closeIdleConnections(0);
        }
        return responseString;
    }

}
