package com.cn.tianxia.api.utils.pay;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SHANUtils {
    
   
    private static final Logger logger = LoggerFactory.getLogger(SHANUtils.class);

    public static HttpURLConnection createUrl(String url) {
        URL httpurl = null;
        HttpURLConnection http = null;
        try {
            httpurl = new URL(url);
            http = (HttpURLConnection) httpurl.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        http.setConnectTimeout(20000);
        http.setReadTimeout(20000);
        return http;
    }

    public static String sendHttpReq(String url, String jsonString, String charset) {

        StringBuffer buffer = new StringBuffer();
        HttpURLConnection httpurl = null;
        String charEncoding = "UTF-8";
        if (!charset.equals("")) {
            charEncoding = charset;
        }
        try {
            httpurl = createUrl(url);
            httpurl.setDoOutput(true);
            httpurl.setRequestMethod("POST");
            httpurl.setRequestProperty("Content-Type", "application/json");
            httpurl.setRequestProperty("Accept-Charset", "utf-8");
            DataOutputStream out = new DataOutputStream(httpurl.getOutputStream());
            out.write(jsonString.getBytes("utf-8"));
            out.flush();
            out.close();

            InputStream in = httpurl.getInputStream();
            int code = httpurl.getResponseCode();
            if (code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, charEncoding));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            } else {
                logger.error("no response");
            }
        } catch (Exception e) {
            return null;
        } finally {
            closeHttpRequest(httpurl);
        }
        return buffer.toString();
    }

    public static void closeHttpRequest(HttpURLConnection httpReq) {
        if (httpReq != null) {
            httpReq.disconnect();
        }
    }
    
    public final static String parse(String s, String charset) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            logger.info("生成待签名串:{}",s);
            byte[] strTemp = s.getBytes(charset);
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    
    public final static String createHtml(String tn){
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("<!DOCTYPE html>");
            sb.append("<html lang=\"en\">");
            sb.append("<head>");
            sb.append("<meta charset=\"UTF-8\">");
            sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
            sb.append("<title>天下支付</title>"); 
            sb.append("<script src=\"../scripts/jquery.min.js\"></script>");
            sb.append("<script src=\"../scripts/paymentjs.min.js\"></script>");
            sb.append("</head>");
            sb.append("<body>");
            //写入js
            sb.append("<script type=\"text/javascript\">");
            sb.append("paymentjs.createPayment(");
            sb.append(tn);
            sb.append(", function(result, err) { });");
            sb.append("</script>");
            sb.append("</body>");
            sb.append("</html>");
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;    
    }
}
