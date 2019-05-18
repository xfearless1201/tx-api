/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.util 
 *
 *    Filename:    LogMsgUtil.java 
 *
 *    Description:  记录调用第三方接口日志报文工具类
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:     Horus
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年01月04日 16:50 
 *
 *    Revision: 
 *
 *    2019/1/4 16:50 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 *  * @ClassName LogMsgUtil
 *  * @Description 记录调用第三方接口日志报文工具类
 *  * @Author Horus
 *  * @Date 2019年01月04日 16:50
 *  * @Version 1.0.0
 *  
 **/
public class LogMsgUtil {

    public final static String REQ_METHOD_POST = "POST";
    public final static String REQ_METHOD_GET = "GET";

    public final static String TYPE_IN = "IN";
    public final static String TYPE_OUT = "OUT";

    public final static String MODE_CALLBACK = "callbackLog";
    public final static String MODE_CREATE_ORDER = "createOrderLog";
    public final static String MODE_TRANSFER = "transferLog";

    private static Logger logger = LoggerFactory.getLogger(LogMsgUtil.class);

    /**
     * 功能描述:回调通知日志报文回调通知日志报文
     *
     * @Author: Horus
     * @Date: 2019/1/4 19:37
     * @param platCode 平台商编码
     * @param payCode 支付商编码
     * @param orderNo 订单号
     * @param reqMethod 请求方式
     * @param reqDate 请求时间
     * @param reqParams 请求参数报文
     * @param resParams 响应参数报文
     * @return: void
     **/
    public static void writeCallback(String platCode,String payCode,String orderNo,String reqMethod,String reqDate,String reqParams,String resParams){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("payCode",payCode);

        jsonObject.put("reqMethod",reqMethod);
        jsonObject.put("orderNo",orderNo);
        jsonObject.put("reqDate",reqDate);
        jsonObject.put("reqParams",reqParams);
        jsonObject.put("resParams",resParams);
        write(platCode,payCode,jsonObject,MODE_CALLBACK);
    }

    /**
     * 功能描述:创建订单日志报文
     *
     * @Author: Horus
     * @Date: 2019/1/4 19:34
     * @param platCode 平台商编码
     * @param payCode 支付商编码
     * @param orderNo 订单号
     * @param reqMethod 请求方式
     * @param reqDate 请求时间
     * @param resDate 响应时间
     * @param reqUrl 请求地址
     * @param reqParams 请求参数报文
     * @param resParams 响应参数报文
     * @return: void
     **/
    public static void writeCreateOrder(String platCode,String payCode,String orderNo,String reqMethod,String reqDate,
                                       String resDate,String reqUrl,String reqParams,String resParams){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("payCode",payCode);

        jsonObject.put("reqMethod",reqMethod);
        jsonObject.put("orderNo",orderNo);
        jsonObject.put("reqDate",reqDate);
        jsonObject.put("resDate",resDate);
        jsonObject.put("reqUrl",reqUrl);
        jsonObject.put("reqParams",reqParams);
        jsonObject.put("resParams",resParams);
        write(platCode,payCode,jsonObject,MODE_CREATE_ORDER);
    }

    /**
     * 功能描述: 转入/转出日志报文
     *
     * @Author: Horus
     * @Date: 2019/1/4 19:25 
     * @param platCode 平台商编码
     * @param gameCode 游戏商编码
     * @param orderNo  订单号
     * @param type     转账类型
     * @param reqMethod 请求方式
     * @param reqDate 请求时间
     * @param reqParams 请求参数报文
     * @param resParams 响应参数报文
     * @return: void
     **/
    public static void writeTransfer(String platCode,String gameCode,String orderNo,String type,
                                     String reqMethod,String reqDate,String reqParams,String resParams){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gameCode",gameCode);

        jsonObject.put("orderNo",orderNo);
        jsonObject.put("type",type);
        jsonObject.put("reqMethod",reqMethod);
        jsonObject.put("reqDate",reqDate);
        jsonObject.put("reqParams",reqParams);
        jsonObject.put("resParams",resParams);
        write(platCode,gameCode,jsonObject,MODE_TRANSFER);
    }

    /**
     * 功能描述:
     *
     * @Author: Horus
     * @Date: 2019/1/4 19:51
     * @param platCode 平台商编码
     * @param secondFile 游戏商编码/支付商编码
     * @param jsonObject 其他相关参数
     * @param mode
     * @return: void
     **/
    private static void write(String platCode,String secondFile,JSONObject jsonObject,String mode){
        if(isNullOrEmpty(platCode)){
            throw new RuntimeException("平台商编码不能为空");
        }
        if(isNullOrEmpty(secondFile)){
            throw new RuntimeException("游戏商编码/支付商编码不能为空");
        }
        jsonObject.put("platCode",platCode);
        Properties pro = new Properties();
        InputStream in = null;
        BufferedWriter bw = null;
        try{
            in = LogMsgUtil.class.getResourceAsStream("conf/file.properties");
            pro.load(in);
            String logMsgAddress = pro.getProperty("logMsgAddress");
            //一级文件 报文来源
            StringBuilder address = new StringBuilder(logMsgAddress+mode);
            mkdir(address);
            //二级文件 平台商编码
            address.append(File.separator+platCode);
            mkdir(address);
            //三级文件 游戏商编码/支付商编码
            address.append(File.separator+secondFile);
            mkdir(address);
            //四级文件 日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            address.append(File.separator+sdf.format(new Date())+".txt");
            File timefile = new File(address.toString());
            if(!timefile.exists()){
                timefile.createNewFile();
            }

            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(timefile, true),"UTF-8"));
            bw.write(jsonObject.toString());
            bw.newLine();
        }catch (Exception e){
            logger.error("记录调用第三方接口日志报文出错："+e.getMessage());
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void mkdir(StringBuilder address) {
        File file = new File(address.toString());
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    private static boolean isNullOrEmpty(String str) {
        if (str == null || "".equals(str))
            return true;
        else
            return false;
    }

   /* public static void main(String[] args) {
        writeTransfer("xjc","XXB","1234", TYPE_IN,"GET","2018-01-05 17:24:23","{}","{}");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a","AA");
        jsonObject.put("b","BB");
        writeCreateOrder("JDB","JB2","2222",REQ_METHOD_POST,"2018-01-05 17:24:23","2018-01-05 17:24:23","https://www.jianshu.com/p/2735b6a538af",jsonObject.toString(),jsonObject.toString());
        writeCreateOrder("JDB","JB2","2222",REQ_METHOD_POST,"2018-01-05 17:24:23","2018-01-05 17:24:23","https://mp.weixin.qq.com/wxopen/wacodepage?action=getcodepage&token=200279275&lang=zh_CN",jsonObject.toString(),jsonObject.toString());

        writeCallback("IG","JJJ","!@#",REQ_METHOD_GET,"2018",jsonObject.toString(),jsonObject.toString());
        writeCallback("IGPJ","JJJ","!@#",REQ_METHOD_GET,"2018",jsonObject.toString(),jsonObject.toString());
    }*/
}
