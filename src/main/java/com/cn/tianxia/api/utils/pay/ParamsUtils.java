package com.cn.tianxia.api.utils.pay;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.qyf.ToolKit;

import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

/**
 * 
 * @ClassName ParamsUtils
 * @Description 参数工具类
 * @author Hardy
 * @Date 2018年9月29日 下午5:58:14
 * @version 1.0.0
 */
public class ParamsUtils {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(ParamsUtils.class);
    
    /**
     * 
     * @Description 获取流参数
     * @param request
     * @return
     */
    public static Map<String,String> getReqParamsByIO(HttpServletRequest request){
        Map<String,String> data = new HashMap<>();
        try {
            //从流里面过去请求参数
            InputStream is = request.getInputStream();
            //建立接收流缓冲，准备处理         
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));      
            StringBuffer sb = new StringBuffer();           
            //读入流，并转换成字符串          
            String readLine;
            while ((readLine = reader.readLine()) != null) {               
                sb.append(readLine);           
            }          
            reader.close();            
            String reqParams = sb.toString();
            logger.info("从request中获取流参数报文:{}",reqParams);
            
            if(null==reqParams||"".equals(reqParams)) {
                return null;
            }
            
            JSONObject jsonObject = JSONObject.fromObject(reqParams);
            if(jsonObject != null && !jsonObject.isEmpty()){
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String val = jsonObject.getString(key);
                    if(StringUtils.isBlank(val)) continue;
                    data.put(key, val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("解析流参数异常:" + e.getMessage());
        }
        
        return data;
    }
    
    /**
     * 
     * @Description 获取请求参数
     * @param request
     * @return
     */
    public static Map<String,String> getParameterNames(HttpServletRequest request){
        Map<String,String> data = new HashMap<>();
        try {
            Enumeration enu = request.getParameterNames();
            if(null == enu){
                return null;
            }
            while (enu.hasMoreElements()) {
                String key = (String) enu.nextElement();
                data.put(key, request.getParameter(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析请求参数异常:"+e.getMessage());
        }
        return data;
    }
    
    public static Map<String,String> getParameterMap(HttpServletRequest request){
        Map<String,String> data = new HashMap<>();
        try {
            Map<String, String[]> params = request.getParameterMap();
            if(params == null || params.isEmpty()){
                return null;
            }
            Iterator<String> iterator = params.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = params.get(key)[0].trim();
                
                if(StringUtils.isBlank(val)) continue;
                
                data.put(key, val);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析请求参数异常:"+e.getMessage());
        }
        
        return data;
    }
    
    public static Map<String,String> getNotifyParams(HttpServletRequest request){
        Map<String,String> data = new HashMap<>();
        try {
            data = getParameterNames(request);
            if(data == null || data.isEmpty()){
                data = getParameterMap(request);
                if(data == null || data.isEmpty()){
                    data = getReqParamsByIO(request);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("解析请求参数异常:{}",e.getMessage());
        }
        
        return data;
    }
    
    public static String formatMapToJson(Map<String,String> data) throws Exception{
        try {
            StringBuffer sb = new StringBuffer();
            if (data != null && !data.isEmpty()){
                Iterator<String> iterator = data.keySet().iterator();
                sb.append("{");
                while (iterator.hasNext()){
                    String key = iterator.next();
                    String val = data.get(key);
                    sb.append("\"").append(key).append("\"");
                    sb.append(":");
                    sb.append("\"").append(val).append("\"");
                    if (iterator.hasNext()) {
                        sb.append(",");
                    }
                }
                sb.append("}");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Map转Json字符串参数异常:"+e.getMessage());
            throw new Exception("Map转Json字符串参数异常!");
        }
    }
    
    /**
     * 
     * @Description 获取竣付通支付回调请求参数
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String,String> getJFTPNotifyParams(HttpServletRequest request){
        try {
            String p1_yingyongnum = request.getParameter("p1_yingyongnum");
            String p2_ordernumber = request.getParameter("p2_ordernumber");
            String p3_money = request.getParameter("p3_money");
            String p4_zfstate = request.getParameter("p4_zfstate");
            String p5_orderid = request.getParameter("p5_orderid");
            String p6_productcode = request.getParameter("p6_productcode");
            String p7_bank_card_code = request.getParameter("p7_bank_card_code");
            String p8_charset = request.getParameter("p8_charset");
            String p9_signtype = request.getParameter("p9_signtype");
            String p10_sign = request.getParameter("p10_sign");
            String p11_pdesc = request.getParameter("p11_pdesc");
            String p12_remark = request.getParameter("p12_remark");
            String p13_zfmoney = request.getParameter("p13_zfmoney");
            Map<String,String> data = new HashMap<>();
            data.put("p1_yingyongnum",p1_yingyongnum);//
            data.put("p2_ordernumber",p2_ordernumber);//
            data.put("p3_money",p3_money);//
            data.put("p4_zfstate",p4_zfstate);//
            data.put("p5_orderid",p5_orderid);//
            data.put("p6_productcode",p6_productcode);//
            data.put("p7_bank_card_code",p7_bank_card_code);//
            data.put("p8_charset",p8_charset);//
            data.put("p9_signtype",p9_signtype);//
            data.put("p10_sign",p10_sign);//
            data.put("p11_pdesc",p11_pdesc);//
            data.put("p12_remark",p12_remark);//
            data.put("p13_zfmoney",p13_zfmoney);//
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String,String> getParams(HttpServletRequest request){
      logger.info("getParams(getParams(HttpServletRequest request = {} "+ request.toString());
        Map<String,String> map = new HashMap<>();
      try{
          map.put("memberid",request.getParameter("memberid"));
          map.put("orderid",request.getParameter("orderid"));
          map.put("amount",request.getParameter("amount"));
          map.put("transactionId",request.getParameter("transaction_id"));
          map.put("returncode",request.getParameter("returncode"));
          map.put("sign",request.getParameter("sign"));
          map.put("attach",request.getParameter("attach"));
          map.put("datetime",request.getParameter("datetime"));
          logger.info("BFH 宝付惠支付解析参数   map:{}"+ map);
         return  map;
      }catch (Exception e){
          logger.error("BFH 宝付惠支付解析参数异常",e);
          throw e;
      }
    }



    /**
     *
     * 获取GPAY支付回调参数
     * @param request
     * @return
     */
    public static Map<String,String> getGPAYNotifyParams(HttpServletRequest request) {
        Map<String,String> data = new HashMap<>();
        try {
            //从流里面过去请求参数
            InputStream is = request.getInputStream();
            //建立接收流缓冲，准备处理
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            //读入流，并转换成字符串
            String readLine;
            while ((readLine = reader.readLine()) != null) {
                sb.append(readLine);
            }
            reader.close();
            String reqParams = sb.toString();
            JSONObject jsonObject = JSONObject.fromObject(reqParams);
            data.put("sign",jsonObject.getString("sign"));
            JSONObject dataJson = jsonObject.getJSONObject("data");
            if(dataJson != null && !dataJson.isEmpty()){
                Iterator<String> iterator = dataJson.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String val = dataJson.getString(key);
                    if(StringUtils.isBlank(val))
                        continue;
                    data.put(key, val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("解析流参数异常:" + e.getMessage());
        }

        return data;
    }
    
    /**
     * 
     * @Description 宏达回到通知获取参数
     * @param request
     * @return
     */
    public static Map<String,String> getHDZFNotifyParams(HttpServletRequest request) {
        logger.info("HDZFNotify获取回调请求参数开始===========START===============");
        Map<String,String> data = new HashMap<>();
        try {
            data.put("type",request.getParameter("type"));//支付通道alipay
            data.put("money",request.getParameter("money"));//支付金额
            data.put("extend",request.getParameter("extend"));//扩展信息
            data.put("out_order_id",request.getParameter("out_order_id"));//商户订单号
            if (StringUtils.isNotBlank(request.getParameter("no"))){
                data.put("no",request.getParameter("no"));//支付宝交易号
            }
            data.put("pid",request.getParameter("pid"));//商户id
            data.put("sign",request.getParameter("sign"));//数字签名
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("HDZFNotify获取回调请求参数异常:{}",e.getMessage());
        }
        return data;
    }

    /**
     *
     * 获取D15支付回调参数
     * @param request
     * @return
     */
    public static Map<String,String> getDFIFNotifyParams(HttpServletRequest request,String privateKey) {
        try {
            String requestData = request.getParameter("data");
            byte[] result = ToolKit.decryptByPrivateKey(new BASE64Decoder().decodeBuffer(requestData), privateKey);
            String resultData = new String(result, ToolKit.CHARSET);// 解密数据

            JSONObject jsonObj = JSONObject.fromObject(resultData);
            return jsonObj;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("解析流参数异常:" + e.getMessage());
        }
        return null;
    }

    /**
     *
     * 获取城市互联支付回调参数
     * @param request
     * @return
     */
    public static Map<String,String> getCSHLNotifyParams(HttpServletRequest request) {
        try {
            String message = request.getParameter("message");
            String signature = request.getParameter("signature");
            byte[] result = new BASE64Decoder().decodeBuffer(message);
            String resultData = new String(result, StandardCharsets.UTF_8);// 解密数据
            logger.info("[CSHL]城市互联回调参数解析,message:{},signature:{}",resultData,signature);
            JSONObject jsonObj = JSONObject.fromObject(resultData);
            jsonObj.put("sign",signature);
            return jsonObj;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("解析城市互联参数异常:" + e.getMessage());
        }
        return null;
    }
    
    /**
     * 
     * @Description 解析简付支付回调请求参数
     * @param request
     * @return
     */
    public static Map<String,String> getJIANNotifyParams(HttpServletRequest request){
        try {
            Map<String,String> data = new HashMap<>();
            data.put("fxid",request.getParameter("fxid"));//
            data.put("fxddh",request.getParameter("fxddh"));//
            data.put("fxorder",request.getParameter("fxorder"));//
            data.put("fxattch",request.getParameter("fxattch"));//
            data.put("fxdesc",request.getParameter("fxdesc"));//
            data.put("fxfee",request.getParameter("fxfee"));//
            data.put("fxstatus",request.getParameter("fxstatus"));//
            data.put("fxtime",request.getParameter("fxtime"));//
            data.put("fxsign",request.getParameter("fxsign"));//
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("解析简付支付回调请求参数异常:{}",e.getMessage());
        }
        return null;
    }

    /**
     *
     * @Description 解析安心付支付回调参数
     *
     */
    public static Map<String,String> getAXPNotifyParams(HttpServletRequest request) {
        Map<String,String> data = new HashMap<>();
        try {
            Enumeration enu = request.getParameterNames();
            String jsonData =  (String) enu.nextElement();
            logger.info("[AXP]安心付回调请求参数字符串：{}",jsonData);
            JSONObject jsonObject = JSONObject.fromObject(jsonData);
            if(jsonObject != null && !jsonObject.isEmpty()){
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String val = jsonObject.getString(key);
                    if(StringUtils.isBlank(val))
                        continue;
                    data.put(key, val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析请求参数异常:"+e.getMessage());
        }
        return data;
    }
    /**
    *
    * @Description 解析速龙支付回调参数
    *
    */
    public static Map<String,Object> getSLZFNotifyParams(HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();
        try {
            String reqXml = "";
            Map<String, String[]> params = request.getParameterMap();
            Iterator<String> keys = params.keySet().iterator();
            while(keys.hasNext()){
                String key = keys.next();
                if("".equals(params.get(key)[0].trim())){
                    reqXml = key;
                    break;
                }
            }
            if(StringUtils.isBlank(reqXml)){
                return null;
            }
            //xml转json
            data = XmlUtils.xml2Json(reqXml);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析请求参数异常:"+e.getMessage());
        } 
        return data;
    }

    /**
     *
     * @Description 获取盈付支付回调参数
     * @param request
     * @return
     */
    public static Map<String,String> getYINFNotifyParams(HttpServletRequest request){
        Map<String,String> data = new HashMap<>();
        try {
            //从流里面过去请求参数
            InputStream is = request.getInputStream();
            //建立接收流缓冲，准备处理
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            //读入流，并转换成字符串
            String readLine;
            while ((readLine = reader.readLine()) != null) {
                sb.append(readLine);
            }
            reader.close();
            String reqParams = sb.toString();
            logger.info("从request中获取流参数报文:{}",reqParams);

            JSONObject jsonObject = XmlUtils.parseXml(reqParams);
            if(jsonObject != null && !jsonObject.isEmpty()){
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String val = jsonObject.getString(key);
                    if(StringUtils.isBlank(val)) continue;
                    data.put(key, val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("解析流参数异常:" + e.getMessage());
        }

        return data;
    }
    /**
     * 
     * @Description 获取流参数
     * @param request
     * @return
     */
    public static String getHPAYNotifyParams(HttpServletRequest request){
        try {
            //从流里面过去请求参数
            InputStream is = request.getInputStream();
            //建立接收流缓冲，准备处理         
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));      
            StringBuffer sb = new StringBuffer();           
            //读入流，并转换成字符串          
            String readLine;
            while ((readLine = reader.readLine()) != null) {               
                sb.append(readLine);           
            }          
            reader.close();            
            String reqParams = sb.toString();
            logger.info("从request中获取流参数报文:{}",reqParams);
            
            if(null==reqParams||"".equals(reqParams)) {
                return null;
            }
            return URLDecoder.decode(reqParams, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("解析流参数异常:" + e.getMessage());
            return null;
        }
    }
}
