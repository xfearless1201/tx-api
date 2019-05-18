package com.cn.tianxia.api.game.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.Encrypt;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName LYQPServiceImpl
 * @Description 乐游棋牌游戏接口实现类
 * @author Hardy
 * @Date 2019年2月9日 下午4:34:37
 * @version 1.0.0
 */
public class LYQPGameServiceImpl{
    
    private static final Logger logger = LoggerFactory.getLogger(LYQPGameServiceImpl.class);

    private static String api_url;
    private static String api_deskey;
    private static String api_md5key;
    private static String api_cagent;
    private static String lineCode;

    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private static final String PROCESS = "process";

    public LYQPGameServiceImpl(Map<String, String> pmap) {
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(pmap, "LYQP");
        JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());
        api_url = jo.getString("api_url");
        api_deskey = jo.getString("api_deskey");
        api_md5key = jo.getString("api_md5key");
        api_cagent = jo.getString("api_cagent");
        lineCode = jo.getString("lineCode");
    }

    public static void main(String[] args) {
        Map<String, String> pmap = new HashMap<String, String>();
        pmap.put("LYQP",
                "{'api_url':'https://api.leg668.com:189/channelHandle?'," +
                        "'pull_url':'https://record.leg668.com:190/getRecordHandle?'," +
                        "'api_deskey':'b3648fe062704394'," +
                        "'api_md5key':'38215242322443c9'," +
                        "'api_cagent':'70041'," +
                        "'KindID':'0'," +
                        "'lineCode':'100'}");
        LYQPGameServiceImpl k = new LYQPGameServiceImpl(pmap);

        //1.登录游戏
        //String url=k.checkOrCreateGameAccout("bl1huanghao93","127.0.0.1","0");
        //String msg = JSONObject.fromObject(url).getJSONObject("d").getString("url");

        //2.转入
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date now =new Date();
        String time=sdf.format(now);

        String msg = k.channelHandleOn("bl1huanghao93", "70041"+time+"bl1huanghao93", "200", "2");
        System.out.println(msg);
    }

    /**
     * 此接口用以验证游戏账号，如果账号不存在则创建游戏账号。并为账号上分。
     */

    public String checkOrCreateGameAccout(String loginname, String ip, String GameID) {
        logger.info("LYQP乐游棋牌检查或创建用户开始----------------------");
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String orderid = api_cagent + sf.format(new Date()) + loginname;

        Map<String, String> map = new HashMap<>();
        map.put("s", "0");
        map.put("account", loginname);
        map.put("money", "0");
        map.put("orderid", orderid);
        map.put("ip", ip);
        map.put("lineCode", lineCode);
        map.put("KindID", GameID);

        logger.info("LYQP乐游棋牌检查或创建用户,用户名【" + loginname + "】,请求参数:{}", map);

        String url = this.getLYQPUrl(map);
        String datastr = sendPost(url);

        logger.info("LYQP乐游棋牌检查或创建用户,用户名【" + loginname + "】,第三方响应参数:{}", datastr);

        if ("".equals(datastr) || datastr == null || "null".equals(datastr)) {
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
                f.setLog("LYQP", param);
                return "error";
            }
        }
        return datastr;
    }

    /**
     * 查询可下分余额
     */

    public String queryUnderTheBalance(String loginname) {
        logger.info("LYQP乐游棋牌查询用户可下分余额开始----------------------");
        Map<String, String> map = new HashMap<>();
        map.put("s", "1");
        map.put("account", loginname);

        logger.info("LYQP乐游棋牌查询用户可下分余额,用户名【" + loginname + "】,请求参数:{}", map);

        String url = this.getLYQPUrl(map);
        String datastr = sendPost(url);

        logger.info("LYQP乐游棋牌查询用户可下分余额,用户名【" + loginname + "】,第三方响应参数:{}", datastr);

        if ("".equals(datastr) || datastr == null || "null".equals(datastr)) {
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
                f.setLog("LYQP", param);
                return "error";
            }
        }
        return datastr;
    }

    /**
     * 上分flag：2 下分 flag：3
     */

    public String channelHandleOn(String loginname, String orderid, String money, String flag) {

        logger.info("LYQP乐游棋牌上下分开始------------------");

        Map<String, String> map = new HashMap<>();
        map.put("s", flag);
        map.put("account", loginname);
        map.put("money", money);
        map.put("orderid", orderid);

        logger.info("LYQP乐游棋牌上下分请求第三方参数:{}",map.toString());

        String url = this.getLYQPUrl(map);
        String datastr = sendPost(url);

        logger.info("LYQP乐游棋牌上下分第三方响应参数:{}", datastr);
        try {
            Thread.sleep(2000);
            return polling(orderid);
        } catch (Exception e) {
            logger.error("LYQP乐游棋牌轮询订单,订单号【"+ orderid +"】异常:{}",e.getMessage());
            return PROCESS;
        }
    }

    private String polling(String orderid) throws Exception{
        logger.info("LYQP乐游棋牌开始轮询订单,确定订单的最终状态......");
        int i = 0;
        for (;;) {
            logger.info("LYQP乐游棋牌轮询订单,订单号【"+ orderid +"】,第"+ i +"次查询.......");
            String queryResult = orderQuery(orderid);
            if (SUCCESS.equals(queryResult)) {
                return SUCCESS;
            } else if (FAIL.equals(queryResult)) {
                return FAIL;
            } else {
                if (i == 2) {
                    return PROCESS;
                }
            }
            logger.info("LYQP乐游棋牌轮询订单,订单号【"+ orderid +"】,第"+ i +"次查询结束.");
            i++;
            Thread.sleep(1500);
        }
    }

    /**
     * 订单查询
     */

    public String orderQuery(String orderid) {

        logger.info("LYQP乐游棋牌转账订单查询开始-------------");

        Map<String, String> map = new HashMap<>();
        map.put("s", "4");
        map.put("orderid", orderid);

        logger.info("LYQP乐游棋牌转账订单查询,订单号【" + orderid + "】请求参数参数:{}", map.toString());

        String url = this.getLYQPUrl(map);
        String datastr = sendPost(url);

        logger.info("LYQP乐游棋牌转账订单查询,订单号【" + orderid + "】第三方返回结果:{}", datastr);

        if ("".equals(datastr) || datastr == null || "null".equals(datastr)) {
            return PROCESS;
        } else {
            //状态码 -1 不存在 0 成功 2 失败 3 处理中
            JSONObject js = JSONObject.fromObject(datastr);
            String status = js.getJSONObject("d").get("status") + "";
            if ("0".equals(status)) {
                return SUCCESS;
            } else if ("-1".equals(status) || "2".equals(status)) {
                return FAIL;
            } else {
                return PROCESS;
            }
        }
    }

    public String getLYQPUrl(Map<String, String> map) {
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
    	 // 创建httpclient工具对象
        HttpClient client = new HttpClient();
        client.setTimeout(40*1000);
        // 创建get请求方法
        GetMethod myGet = new GetMethod(tagUrl);
        String responseString = null;
        try {
            // 设置请求头部类型
            myGet.setRequestHeader("Content-Type", "application/json");
            myGet.setRequestHeader("charset", "utf-8");
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
                FileLog f = new FileLog();
                Map<String, String> map = new HashMap<>();
                map.put("statusCode", statusCode + "");
                map.put("ResponseBody", myGet.getResponseBodyAsString());
                map.put("tagUrl", tagUrl);
                map.put("Function", "sendPost");
                f.setLog("LYQP", map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            myGet.releaseConnection();
            client.getHttpConnectionManager().closeIdleConnections(0);
        }
        return responseString;
    }

}
