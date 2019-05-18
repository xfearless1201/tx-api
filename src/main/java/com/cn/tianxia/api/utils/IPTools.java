package com.cn.tianxia.api.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.tianxia.api.utils.v2.HttpUtils;

/**
 * JDBC 的工具类 其中包含: 获取数据库连接, 关闭数据库资源等方法.
 */
public class IPTools {

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    public static String getIp(HttpServletRequest request) {
        String ipAddress = getIpAddress(request);
        if (StringUtils.isBlank(ipAddress)){
            return "127.0.0.1";
        }
        return ipAddress;
    }

    public static String getAddress(HttpServletRequest request) {
        String address = "未知";
        try {
            // 获取登录IP地址
            String ip = "";
            String url = "http://pv.sohu.com/cityjson?ie=utf-8";
            String result = HttpUtils.toPostJson("", url, 3000);
            String subResult = result.substring(19, result.length() - 1);
            // 解析返回内容
            Map<String, Object> strMap = JSON.parseObject(subResult, Map.class);
            if (StringUtils.isNotBlank(subResult)) {
                ip = strMap.get("cip").toString();
            } else {
                ip = "127.0.0.1";
            }
            String reg6 = "(?i)^((([\\da-f]{1,4}:){7}[\\da-f]{1,4})|(([\\da-f]{1,4}:){1,7}:)|(([\\da-f]{1,4}:){6}:[\\da-f]{1,4})|(([\\da-f]{1,4}:){5}(:[\\da-f]{1,4}){1,2})|(([\\da-f]{1,4}:){4}(:[\\da-f]{1,4}){1,3})|(([\\da-f]{1,4}:){3}(:[\\da-f]{1,4}){1,4})|(([\\da-f]{1,4}:){2}(:[\\da-f]{1,4}){1,5})|([\\da-f]{1,4}:(:[\\da-f]{1,4}){1,6})|(:(:[\\da-f]{1,4}){1,7})|(([\\da-f]{1,4}:){6}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([\\da-f]{1,4}:){5}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([\\da-f]{1,4}:){4}(:[\\da-f]{1,4}){0,1}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([\\da-f]{1,4}:){3}(:[\\da-f]{1,4}){0,2}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([\\da-f]{1,4}:){2}(:[\\da-f]{1,4}){0,3}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|([\\da-f]{1,4}:(:[\\da-f]{1,4}){0,4}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(:(:[\\da-f]{1,4}){0,5}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}))$";
            if (ip.matches(reg6)) {
                address = "ip6:" + IPTools.getIp(request).toString();
            } else {
                IpLocation1 location = IpLocation1.getInstance();
                address = location.findLocation(ip);
            }

        } catch (Exception e) {
        }
        return address;
    }

    /**
     * @Description 获取IP的中文地址
     * @param request
     * @return
     */
    public static String getIpCnAddress(HttpServletRequest request) {
        String address = "未知";
        try{
            String urlStr = "http://whois.pconline.com.cn/ipJson.jsp";
            String reqParams = "ip="+getIp(request)+"&json=true";
            String response = getResult(urlStr, reqParams, "GBK");
            if(StringUtils.isNotBlank(response)){
                //解析响应结果
                JSONObject jsonObject = JSON.parseObject(response);
                if(jsonObject.containsKey("addr")){
                    address = jsonObject.getString("addr");
                }else{
                    if(jsonObject.containsKey("pro")){
                        address += jsonObject.getString("pro");
                    }
                    if(jsonObject.containsKey("city")){
                        address += jsonObject.getString("city");
                    }
                    if(jsonObject.containsKey("region")){
                        address += jsonObject.getString("region");
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return address;
    }

    /**
     * @param urlStr
     *            请求的地址
     * @param content
     *            请求的参数 格式为：name=xxx&pwd=xxx
     * @param encoding
     *            服务器端请求编码。如GBK,UTF-8等
     * @return
     */
    public static String getResult(String urlStr, String content, String encoding) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();// 新建连接实例
            connection.setConnectTimeout(2000);// 设置连接超时时间，单位毫秒
            connection.setReadTimeout(2000);// 设置读取数据超时时间，单位毫秒
            connection.setDoOutput(true);// 是否打开输出流 true|false
            connection.setDoInput(true);// 是否打开输入流true|false
            connection.setRequestMethod("POST");// 提交方法POST|GET
            connection.setUseCaches(false);// 是否缓存true|false
            connection.connect();// 打开连接端口
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());// 打开输出流往对端服务器写数据
            out.writeBytes(content);// 写数据,也就是提交你的表单 name=xxx&pwd=xxx
            out.flush();// 刷新
            out.close();// 关闭输出流
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));// 往对端写完数据对端服务器返回数据
            // ,以BufferedReader流来读取
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();// 关闭连接
            }
        }
        return null;
    }

    /**
     * unicode 转换成 中文
     * 
     * @author fanhui 2007-3-15
     * @param theString
     * @return
     */
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + aChar - '0';
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + aChar - 'a';
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + aChar - 'A';
                            break;
                        default:
                            throw new IllegalArgumentException("Malformed      encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }
    
}
