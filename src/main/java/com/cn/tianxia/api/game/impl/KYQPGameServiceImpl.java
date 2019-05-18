package com.cn.tianxia.api.game.impl;

import com.alibaba.fastjson.JSON;
import com.cn.tianxia.api.utils.Encrypt;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 
 * @ClassName KYQPServiceImpl
 * @Description 开元棋牌游戏接口实现类
 * @author Hardy
 * @Date 2019年2月9日 下午4:33:58
 * @version 1.0.0
 */
public class KYQPGameServiceImpl{

    private static Logger logger = LoggerFactory.getLogger(KYQPGameServiceImpl.class);
    private String api_url;
    private String api_deskey;
    private String api_md5key;
    private String api_cagent;
    private String lineCode;



    private static final String  SUCCESS = "success";
    private static final String  FAILD   = "faild";
    private static final String  PROCESS = "process";

    public KYQPGameServiceImpl(Map<String, String> pmap) {
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(pmap, "KYQP");
        JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());
        api_url = jo.getString("api_url").toString();
        api_deskey = jo.getString("api_deskey").toString();
        api_md5key = jo.getString("api_md5key").toString();
        api_cagent = jo.getString("api_cagent").toString();
        lineCode = jo.getString("lineCode").toString();
    }

    /**
     * 此接口用以验证游戏账号，如果账号不存在则创建游戏账号。并为账号上分。
     */

    public String checkOrCreateGameAccout(String loginname, String ip, String GameID) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String orderid = api_cagent + sf.format(new Date()) + loginname;

        Map<String, String> map = new HashMap<String, String>();
        map.put("s", "0");
        map.put("account", loginname);
        map.put("money", "0");
        map.put("orderid", orderid);
        map.put("ip", ip);
        map.put("lineCode", lineCode);
        map.put("KindID", GameID);

        String url = this.getKYQPUrl(map);
        String datastr = sendPost(url);
        if (StringUtils.isEmpty(datastr) || datastr.equals(FAILD)) {
            return "error";
        } else {
            JSONObject js = JSONObject.fromObject(datastr);
            if (!"0".equals(js.getJSONObject("d").get("code") + "")) {
                FileLog f = new FileLog();
                Map<String, String> param = new HashMap<>();
                param.put("loginname", loginname);
                param.put("url", url);
                param.put("datastr", datastr);
                param.put("Function", "CheckOrCreateGameAccout");
                f.setLog("KYQP", param);
                return "error";
            }
        }
        return datastr;
    }

    /**
     * 查询可下分余额
     */

    public String queryUnderTheBalance(String loginname) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("s", "1");
        map.put("account", loginname);

        String url = this.getKYQPUrl(map);
        String datastr = sendPost(url);
        if (StringUtils.isEmpty(datastr)||datastr.equals(FAILD)) {
            return "error";
        } else {
            JSONObject js = JSONObject.fromObject(datastr);
            if (!"0".equals(js.getJSONObject("d").get("code") + "")) {
                FileLog f = new FileLog();
                Map<String, String> param = new HashMap<>();
                param.put("loginname", loginname);
                param.put("url", url);
                param.put("datastr", datastr);
                param.put("Function", "queryUnderTheBalance");
                f.setLog("KYQP", param);
                return "error";
            }
        }
        return datastr;
    }

    /**
     * 上分flag：2 下分 flag：3
     */

    public String channelHandleOn(String loginname, String orderid, String money, String flag)throws  Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("s", flag);
        map.put("account", loginname);
        map.put("orderid", orderid);
        map.put("money", money);
        logger.info("KYQP  游戏上下分部分请求报文"+JSON.toJSONString(map));
        String url = this.getKYQPUrl(map);

        String datastr = sendPost(url);
        if(datastr.equals(FAILD)) return  FAILD;
        logger.info("KYQP 用户【"+loginname+"】订单号【"+orderid+"】游戏上下分响应报文："+ datastr);
        String orderStatus = null;
        if (StringUtils.isEmpty(datastr)) {
            logger.info("KYQP 用户【"+loginname+"】订单号【"+orderid+"】游戏上下分响应失败：响应报文为空！");
            orderStatus =  circulationOrder(orderid);
            logger.info(" circulationOrder(String orderNo)  -return:{}",orderStatus);
            return orderStatus;
        } else {
            JSONObject js = JSONObject.fromObject(datastr);
            logger.info("KYQP 用户【"+loginname+"】订单号【"+orderid+"】游戏上下分响应成功 响应报文："+ js.toString());

            if("0".equals(js.getJSONObject("d").get("code").toString())) {
                orderStatus = newOrderQuery(orderid);
                if(orderStatus.equals("0")){
                    logger.info("KYQP 用户【"+loginname+"】订单号【"+orderid+"】游戏上下分处理成功！");
                    return SUCCESS;


                }else if(orderStatus.equals("2")){
                    logger.info("KYQP 用户【"+loginname+"】订单号【"+orderid+"】游戏上下分处理失败！");
                    return FAILD;

                }else{
                    orderStatus =  circulationOrder(orderid);
                    logger.info(" circulationOrder(String orderNo)  -return:{}",orderStatus);
                    return orderStatus;
                }

            } else {
                logger.info("KYQP 用户【"+loginname+"】订单号【"+orderid+"】游戏上下分响应成功：error");
                orderStatus =  circulationOrder(orderid);
                logger.info(" circulationOrder(String orderNo)  -return:{}",orderStatus);
                return orderStatus;
            }
        }
    }

    private String circulationOrder(String orderNo)throws  Exception{
        logger.info("circulationOrder(String orderNo = {}",orderNo);
        int i = 0;
        while (true){
            Thread.sleep(2000);//延时2秒等待第三方处理
            i ++ ;
            String status = newOrderQuery(orderNo);
            if (status.equals("0")){
                return SUCCESS;
            }else if (status.equals("2")){
                return FAILD;
            }
            if(i > 1){
                //人工审核订单，订单处理时间太长
                return PROCESS;
            }
        }

    }

    /**
     * 订单查询
     */
    public String orderQuery(String orderid) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("s", "4");
        map.put("orderid", orderid);

        String url = this.getKYQPUrl(map);
        String datastr = sendPost(url);
        if (StringUtils.isEmpty(datastr) || datastr.equals(FAILD)) {
            return "error";
        } else {
            JSONObject js = JSONObject.fromObject(datastr);
            String code = js.getJSONObject("d").get("code") + "";
            String status = js.getJSONObject("d").get("status") + "";
            boolean result  = "0".equals(code)&&"0".equals(status);
            if (!result) {
                FileLog f = new FileLog();
                Map<String, String> param = new HashMap<>();
                param.put("orderid", orderid);
                param.put("url", url);
                param.put("datastr", datastr);
                param.put("Function", "orderQuery");
                f.setLog("KYQP", param);
                return "error";
            }
        }
        return datastr;
    }

    /**
     * 上下分查询订单接口
     * @param orderid
     * @return
     */
    public String newOrderQuery(String orderid) {
        logger.info("开元棋牌 newOrderQuery(String orderid = {}  -start" + orderid);
        Map<String, String> map = new HashMap<String, String>();
        map.put("s", "4");
        map.put("orderid", orderid);

        String url = this.getKYQPUrl(map);
        String datastr = sendPost(url);
        if(datastr.equals(FAILD))  return "3";
        logger.info("KYQP  订单号【"+orderid+"】发起HTTP请求查询订单信息请求响应报文:" + datastr);
        if(StringUtils.isEmpty(datastr)){
            logger.info("开元棋牌 newOrderQuery(String orderid = {}:服务器无响应,查询订单无结果！"+ orderid);
            /**
             * 状态码status（-1:不存在、0:成功、2:失败、3:处理中）
             */
            return "3";
        }
        JSONObject js = JSONObject.fromObject(datastr);
        logger.info("KYQP  订单号【"+orderid+"】发起HTTP请求查询订单信息请求响应成功,响应消息:" + js);
        String code = js.getJSONObject("d").get("code").toString();
        String status = js.getJSONObject("d").get("status").toString();
        if (code.equals("0") && status.equals("0") ){
            logger.info("KYQP 订单号【"+orderid+"】发起HTTP请求查询订单信息请求响应成功,订单结果：success");
            return "0";
        }
        /**
         * 状态码 status（-1:不存在、0:成功、2:失败、3:处理中）
         * code 不为“0” 服务方不受理该请求
         */
        if(status.equals("2")){
            logger.info("KYQP 查询游戏订单响应的订单状态信息:{}"+ ("code:"+ code+"status:"+ status));
            //请求第三方错误,上分请求无效，服务方不处理，返回上分失败错误
            return "2";
        }else if (status.equals("3")|| !code.equals("0")|| status.equals("-1")){
            logger.info("newOrderQuery(String orderid = {} 开元棋牌订单查询响应状态为处理中"+ orderid);
            return "3";
        }
        logger.info("KYQP 订单号【"+orderid+"】发起HTTP请求查询订单信息请求异常！");
        return  null;

    }

    public String getKYQPUrl(Map<String, String> map) {
        long timestamp = System.currentTimeMillis();
        String param = getParam(map);
        String key = Encrypt.MD5(api_cagent + timestamp + api_md5key);
        return api_url + "agent=" + api_cagent + "&timestamp=" + timestamp + "&param=" + param + "&key=" + key;
    }

    public String getParam(Map<String, String> map) {
        String param = "";
        StringBuffer sr = new StringBuffer("");
        Set<String> set = map.keySet();
        for (String str : set) {
            sr.append(str + "=");
            sr.append(map.get(str) + "&");
        }
        try {
            param = Encrypt.AESEncrypt(sr.toString().substring(0, sr.length() - 1), api_deskey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }

    /**
     * 发送xml请求到server端
     * 
     * @param tagUrl
     *            请求数据地址
     * @return null发送失败，否则返回响应内容
     */
    public static String sendPost(String tagUrl) {
        HttpClient client = new HttpClient();
        GetMethod myGet = new GetMethod(tagUrl);
        String responseString = null;
        try {
            // 设置请求头部类型
            myGet.setRequestHeader("Content-Type", "application/json");
            myGet.setRequestHeader("charset", "utf-8");
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            client.getHttpConnectionManager().getParams().setSoTimeout(10000);

            // 设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
            int statusCode = client.executeMethod(myGet);
            // 只有请求成功200了，才做处理
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = myGet.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str = "";
                while ((str = br.readLine()) != null) {
                    stringBuffer.append(str);
                }
                responseString = stringBuffer.toString();
            } else {
                logger.info("[KYQP] HTTP请求响应状态码异常：{}",statusCode);
                if(String.valueOf(statusCode).substring(0,1).equals("2"))  return  null;
                return  FAILD;
            }
        } catch (Exception e) {
            logger.error("[KYQP] HTTP请求异常！",e);
            if(e.getMessage().equals("Read timed out")) return  null; return FAILD;
        } finally {
            myGet.releaseConnection();
            client.getHttpConnectionManager().closeIdleConnections(0);
        }
        return responseString;
    }

    

}
