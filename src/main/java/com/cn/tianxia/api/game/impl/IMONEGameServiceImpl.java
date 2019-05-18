package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.PlatFromConfig;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;


/**
 *
 * @ClassName IGPJServiceImpl
 * @Description IM游戏
 * @author Jacky
 * @Date 2019年3月11日 下午4:31:46
 * @version 1.0.0
 */
public class IMONEGameServiceImpl {

    private final static Logger logger = LoggerFactory.getLogger(IMONEGameServiceImpl.class);

    private static final String  ERROR = "error";
    private static final String  SUCCESS = "success";
    private static final String  FAILD = "faild";
    private static final String  PROCESS = "process";

    private  String apiUrl;
    private  String merchantNo;
    private  String createAccountUrl;
    private  String checkExistsUrl;
    private  String transferUrl;
    private  String getBalanceUrl;
    private  String queryOrderStatus;
    private  String forwardGame;
    private  String mobileForwardGame;


    public IMONEGameServiceImpl(Map<String,String> data) {
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(data, "IM");
        JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());
        if(jo != null && !jo.isEmpty()){
            if(jo.containsKey("apiUrl")){
                this.apiUrl = jo.getString("apiUrl");
            }
            if(jo.containsKey("merchantNo")){
                this.merchantNo = jo.getString("merchantNo");
            }
            if(jo.containsKey("createAccountUrl")){
                this.createAccountUrl = jo.getString("createAccountUrl");
            }
            if(jo.containsKey("checkExistsUrl")){
                this.checkExistsUrl = jo.getString("checkExistsUrl");
            }
            if(jo.containsKey("transferUrl")){
                this.transferUrl = jo.getString("transferUrl");
            }
            if(jo.containsKey("getBalanceUrl")){
                this.getBalanceUrl = jo.getString("getBalanceUrl");
            }
            if(jo.containsKey("queryOrderStatus")){
                this.queryOrderStatus = jo.getString("queryOrderStatus");
            }
            if(jo.containsKey("forwardGame")){
                this.forwardGame = jo.getString("forwardGame");
            }
            if(jo.containsKey("mobileForwardGame")){
                this.mobileForwardGame = jo.getString("mobileForwardGame");
            }

        }
    }



    public boolean createAccount(GameCheckOrCreateVO gameCheckOrCreateVO)throws Exception{
        logger.info("[IM] createAccount(GameCheckOrCreateVO gameCheckOrCreateVO =  -start {}",gameCheckOrCreateVO.toString());
        if(StringUtils.isEmpty(gameCheckOrCreateVO.getPassword())||StringUtils.isEmpty(gameCheckOrCreateVO.getGamename())){
            logger.info("[IM] 创建用户所需参数不足！账号或密码不能为空！");
            throw  new Exception("[IM] 账号或密码不能为空！");
        }
        Map<String,String> map = new HashMap<>();
        try{
            map.put("MerchantCode",merchantNo);
            map.put("PlayerId",gameCheckOrCreateVO.getGamename());
            map.put("Currency","CNY");
            map.put("Password",gameCheckOrCreateVO.getPassword());
            map.put("Country","CN");

            String returnJson = toPost(JSONObject.fromObject(map),apiUrl + createAccountUrl);
            logger.info("[IM] 发起HTTP请求创建用户响应报文：{}",returnJson);

            if(StringUtils.isEmpty(returnJson)){
                logger.info("[IM] 发起创建用户HTTP请求无响应！");
                Thread.sleep(2000);
                return checkExists(gameCheckOrCreateVO.getGamename());
            }

            if (returnJson.equals(ERROR))  return  false;

            JSONObject  jsonObject = JSONObject.fromObject(returnJson);
            String code = jsonObject.get("Code").toString();
            String message =jsonObject.get("Message").toString();
            if(code.equals("0")){
                logger.info("[IM] 玩家创建成功！用户：【"+gameCheckOrCreateVO.getGamename()+"】");
                return  true;
            }else {
                logger.info("[IM] 玩家创建失败！用户：【"+gameCheckOrCreateVO.getGamename()+"】 失败信息：{}",message);
                return  false;
            }

        }catch (Exception e){
            logger.info("[IM]  创建玩家失败",e);
            throw  e;
        }
    }

    public boolean checkExists(String playerld)throws  Exception {
      logger.info("[IM] checkExists(String playerld =  -start {}",playerld);
      if (StringUtils.isEmpty(playerld)) throw new Exception("[IM] 账号不能为空！");

      Map<String,String> map =  new HashMap<>();
      try{
          map.put("MerchantCode",merchantNo);
          map.put("PlayerId",playerld);
          String returnJson = toPost(JSONObject.fromObject(map),apiUrl+ checkExistsUrl);
          logger.info("[IM] 发起HTTP请求检查用户接口响应报文：{}",returnJson);

          if(StringUtils.isEmpty(returnJson) ){
              logger.info("[IM] 发起检查用户HTTP请求无响应！");
              return false;
          }
          JSONObject jsonObject = JSONObject.fromObject(returnJson);
          String code = jsonObject.get("Code").toString();
          if(code.equals("0") || code.equals("503")){
             return  true;
          }else {
              return false;
          }
      }catch (Exception e){
          logger.error("[IM] 检查用户失败",e);
          throw  e;
      }
    }

    public boolean checkOrCreateAccount(GameCheckOrCreateVO gameCheckOrCreateVO) throws  Exception{
        logger.info("[IM]  checkOrCreateAccount(GameCheckOrCreateVO gameCheckOrCreateVO =  -start {}",gameCheckOrCreateVO);
        boolean b = false ;
        try{
            if(checkExists(gameCheckOrCreateVO.getGamename())){
                logger.info("[IM 检查账号【"+gameCheckOrCreateVO.getGamename()+"】已存在！");
                b =  true;
            }else{
                logger.info("[IM] 检查用户【"+gameCheckOrCreateVO.getGamename()+"】不存在! 调用户创建用户方法！");
                b = createAccount(gameCheckOrCreateVO);
            }
        }catch (Exception e){
            logger.error("检查用户或创建用户失败!",e);
            throw e;
        }
        logger.info("[IM] 检查用户返回结果：{}",b);
        return  b;
    }


    public String getBalance(String playerld )throws Exception{
        logger.info("[IM] getBalance(String playerld =  -start {}",playerld);
        if(StringUtils.isEmpty(playerld)) throw  new Exception("[IM] 调用获取玩家余额接口用户账号为空！");

        Map<String,String> map = new HashMap<>();
        String balance = ERROR;
        try{
            map.put("MerchantCode",merchantNo);
            map.put("PlayerId",playerld);
            map.put("ProductWallet","301");
            String returnJson = toPost(JSONObject.fromObject(map),apiUrl + getBalanceUrl);
            logger.info("[IM] 发起HTTP请求查询用户余额,服务方响应报文：{}",returnJson);
            if (StringUtils.isEmpty(returnJson)){
                logger.info("[IM] 用户【"+playerld+"】 发起HTTP请求查询游戏余额响应无结果");
            }
            JSONObject jsonObject = JSONObject.fromObject(returnJson);
            String code = jsonObject.get("Code").toString();
            logger.info("[IM] 查询玩家余额响应code码：{}",code);

            if(code.equals("0"))
                balance = jsonObject.get("Balance").toString();
            else
                logger.info("[IM] 查询玩家【"+playerld+"】账号余额失败！失败异常CODE：{},失败明细:{}",code,jsonObject.get("Message").toString());

        }catch (Exception e){
            logger.info("[IM] 获取玩家余额异常！",e);
            throw  e;
        }
        return  balance;
    }


    public String transferIn(GameTransferVO gameTransferVO)throws  Exception{
        logger.info("[IM]  transferIn(GameTransferVO gameTransferVO = {}",gameTransferVO);
        String status = transfer(gameTransferVO);
        logger.info("[IM] transferIn(GameTransferVO) -- return:"+status);
        return  status;
    }


    public String transferOut(GameTransferVO gameTransferVO)throws  Exception{
        logger.info("[IM]  transferOut(GameTransferVO gameTransferVO = {}",gameTransferVO);
        gameTransferVO.setMoney("-"+gameTransferVO.getMoney());
        //电竞401  ESPORTSBULL，体育301  imsb
        String status = transfer(gameTransferVO);
        logger.info("[IM] transferOut(GameTransferVO) -- return:"+status);
        return  status;
    }



    public String transfer(GameTransferVO gameTransferVO)throws  Exception {
        logger.info("[IM] transfer(GameTransferVO gameTransferVO = -start {}",gameTransferVO);
        Map<String,String> map = new HashMap<>();
        try{
            map.put("MerchantCode",merchantNo);
            map.put("PlayerId",gameTransferVO.getAg_username());
            map.put("ProductWallet","301");//写死一个钱包入口 电子与体育钱包都是共用一个
            map.put("TransactionId",gameTransferVO.getBillno());
            map.put("Amount",gameTransferVO.getMoney());

            String returnJson = toPost(JSONObject.fromObject(map),apiUrl + transferUrl);
            logger.info("[IM] 游戏上下分发起HTTP请求,服务响应报文：{}",returnJson);
            if(returnJson == null){
                logger.info("[IM] 发起HTTP请求游戏上下分响应无结果！开始轮训查询订单结果!");
                return  orderCirculation(gameTransferVO.getAg_username(),gameTransferVO.getBillno());
            }
            if (returnJson.equals(ERROR)) return  FAILD;
            JSONObject jsonObject =JSONObject.fromObject(returnJson);

            String code   = jsonObject.get("Code").toString();
            String status = jsonObject.get("Status").toString();
            logger.info("[IM] 游戏上下分订单响应code："+ code);
            if(code.equals("0") && status.equals("Approved")){
                logger.info("[IM] 用户【"+gameTransferVO.getAg_username()+"】请求上下分业务成功！");
                return SUCCESS;
            }

            if(code.equals("517") || code.equals("520")||status.equals("Processed")){
                return  orderCirculation(gameTransferVO.getAg_username(),gameTransferVO.getBillno());
            }else{

                logger.info("[IM] 用户【"+gameTransferVO.getAg_username()+"】游戏上下分失败,失败code：{},失败原因：{}",code , jsonObject.get("Message").toString());
                return FAILD;
            }

        }catch (Exception e){
            logger.error("[IM] 用户【"+gameTransferVO.getAg_username()+"】游戏上下分失败！",e);
            throw  e;
        }
    }



    private String orderCirculation(String playerld ,String  orderNo) throws Exception{
        logger.info("[IM] orderCirculation(String playerld ,String  orderNo = {},{}",playerld,orderNo);

        String status = queryTransferOrder(playerld,orderNo);
        if(status.equals(SUCCESS))  return  SUCCESS;

        if(status.equals(FAILD))    return  FAILD;
        int i = 0;
        while (i < 3){
            i ++ ;
            Thread.sleep(3000);
            logger.info("[IM] 开始轮训订单【"+orderNo+"】 第"+i+"次！");

            status =  queryTransferOrder(playerld,orderNo);
            logger.info("[IM] 开始轮训订单【"+orderNo+"】 返回结果：{}",status);

            if(status.equals(SUCCESS))    return  SUCCESS;
            if(status.equals(FAILD))      return  FAILD;
            if(status.equals(PROCESS))    if(i > 2)   return  PROCESS;
        }
        logger.info("[IM] 程序异常,无法识别status！  status = {}",status);
        return  PROCESS;
    }




    public String queryTransferOrder(String playerld ,String  orderNo)throws  Exception{
        logger.info("[IM] queryTransferOrder(String playerld ,String  orderNo = -start {},{}",playerld,orderNo);

        if (StringUtils.isEmpty(playerld) || StringUtils.isEmpty(orderNo))  throw  new Exception("查询订单接口参数不足！账号或订单号不能为空！");
        Map<String,String> map = new HashMap<>();
        try{
            map.put("MerchantCode",merchantNo);
            map.put("PlayerId",playerld);
            map.put("TransactionId",orderNo);
            map.put("ProductWallet","301");

            String returnJson = toPost(JSONObject.fromObject(map),apiUrl + queryOrderStatus);
            logger.info("[IM] 发起查询订单请求响应报文：{}",returnJson);
            if (StringUtils.isEmpty(returnJson) || returnJson.equals(ERROR))  return PROCESS;

            JSONObject jsonObject = JSONObject.fromObject(returnJson);
            if (jsonObject.containsKey("Status") && jsonObject.containsKey("Code")){
                String status = jsonObject.get("Status").toString();
                if(status.equals("Approved"))   return  SUCCESS;
                if(status.equals("Declined "))  return  FAILD;
                if(status.equals("Processed"))  return  PROCESS;
            }else{
                logger.info("[IM] 查询订单状态响应报文没有包含status 与 code ！");
                return  PROCESS;
            }
           return  PROCESS;
        }catch (Exception e){
            logger.info("查询订单状态失败！",e);
            throw  e;
        }
    }




    public String forwardGame(GameForwardVO gameForwardVO) throws  Exception{
        logger.info("[IM] forwardGame(GameForwardVO gameForwardVO =  -start {}",gameForwardVO);
        String gameUrl = null;
        Map<String ,String>  map = new HashMap<>();
        try{
            map.put("MerchantCode",merchantNo);
            map.put("PlayerId",gameForwardVO.getAg_username());
            map.put("GameCode",gameForwardVO.getGameId());
            map.put("Language","ZH-CN");
            map.put("IpAddress",gameForwardVO.getIp());
            if(gameForwardVO.getGameId().equals("ESPORTSBULL")){
                map.put("ProductWallet","401");
            }else{
                map.put("ProductWallet","301");
            }

            logger.info("[IM] 获取游戏链接请求参数:{}",map);
            String url = null;
            if(gameForwardVO.getModel().equals("pc")){
                url = apiUrl+ forwardGame;
            }else{
                url = apiUrl+ mobileForwardGame;
            }

            String returnJson = toPost(JSONObject.fromObject(map), url);
            logger.info("[IM] 发起HTTP请求获取游戏链接响应报文：{}",returnJson);
            if(returnJson == null|| returnJson.equals(ERROR)) return  ERROR;

            JSONObject jsonObject = JSONObject.fromObject(returnJson);
            String code = jsonObject.get("Code").toString();
            if(code.equals("0")){
                gameUrl= jsonObject.getString("GameUrl");
            }else{
                logger.info("[IM] 用户【"+gameForwardVO.getAg_username()+"】获取获取游戏URL失败,失败错误码："+code +" 失败原因："+ jsonObject.get("Message").toString());
                return ERROR;
            }

        }catch (Exception e){
            logger.error("用户【"+gameForwardVO.getAg_username()+"】获取游戏链接失败！",e);
            gameUrl = ERROR;
        }
        return  gameUrl;
    }

    /**
     *
     * @Description 发起参数为json类型的post请求
     * @param data
     * @param url
     * @return
     * @throws Exception
     */
    public static String toPost(JSONObject data,String url) throws Exception{
        logger.info("[IM] toPostJsonStr(JSONObject data,String url = {},{}",data , url);
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom().setConnectionManager(createConnectionManager()).build();
            HttpPost httppost = new HttpPost(url);
            if(data != null && !data.isEmpty()){
                StringEntity entity = new StringEntity(data.toString(),"utf-8");//解决中文乱码问题
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type", "application/json");
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
            logger.info("[IM]  发起HTTP请求服务响应状态码：{}",response.getStatusLine().getStatusCode());
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),Consts.UTF_8));
                StringBuffer sb = new StringBuffer();
                String content = null;
                while((content = reader.readLine()) != null){
                    sb.append(content);
                }
                logger.info("[IM]  发起HTTP请求服务响应报文：{}",sb.toString());
                return sb.toString();
            }
            logger.info("[IM] HTTP响应状态码为异常状态！");
            return "error";
        } catch (Exception e) {
            logger.info("[IM] 发起HTTP请求错误！",e);
            throw  e;
        }finally {
            if(httpclient != null){
                httpclient.close();
            }
        }
    }

    private static PoolingHttpClientConnectionManager createConnectionManager() throws Exception {
        logger.info("createConnectionManager()  -start");
        TrustManager tm = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
        };
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[] { tm }, null);

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context,
                NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        return connectionManager;
    }
}
