package com.cn.tianxia.api.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.gst.StringUtils;

public abstract class ESWAbstractService implements ESWService {
    private static final String METHOD = "method";
    private static final String REQUESPARAMS = "requesParams";
    private static final String RESPONSEPARAMS = "responseParams";
    private final static Logger logger = LoggerFactory.getLogger(ESWAbstractService.class);

    protected static final String ERROR = "error";

    public String sendGet(String Url, Map<String, String> Params, String action, String PLATFORM_KEY) {
        StringBuilder param = new StringBuilder();
        Set<String> set = Params.keySet();
        for (String str : set) {
            param.append(str).append("=");
            param.append(Params.get(str)).append("&");
        }
        String urlParms = Url + "?" + param.deleteCharAt(param.length() - 1);
        return sendGet(urlParms,action,PLATFORM_KEY);
    }

    public String sendGet(String Url, String action, String PLATFORM_KEY) {
        logger.info("【" + PLATFORM_KEY + "】：参数" + Url);
        String result;
        try {
            result = httpGet(Url, action, PLATFORM_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        logger.info("【" + PLATFORM_KEY + "】响应：" + result);
        if (StringUtils.isNullOrEmpty(result)) {
            return null;
        } else {
            return result;
        }
    }

    /**
     * 保存文件记录
     *
     * @param action
     * @param urlParms
     * @param result
     * @param PLATFORM_KEY
     */
    protected void setFile(String action, String urlParms, String result, String PLATFORM_KEY) {
        FileLog fileLog = new FileLog();
        Map<String, String> param = new HashMap<>();
        param.put(METHOD, action);
        param.put(REQUESPARAMS, urlParms);
        param.put(RESPONSEPARAMS, result);
        fileLog.setLog(PLATFORM_KEY, param);
    }

    /**
     * 发送get请求到server端
     *
     * @param tagUrl 请求数据地址
     * @return null发送失败，否则返回响应内容
     */
    public String httpGet(String tagUrl, String action, String PLATFORM_KEY) {
        // 创建httpclient工具对象
        HttpClient client = new HttpClient();
        // 创建get请求方法
        GetMethod myGet = new GetMethod(tagUrl);
        String responseString = null;
        try {
            // 设置请求头部类型
            myGet.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            myGet.setRequestHeader("charset", "utf-8");
            // 设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
            int statusCode = client.executeMethod(myGet);
            // 只有请求成功200了，才做处理
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = myGet.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer result = new StringBuffer();
                String readStr;
                while ((readStr = br.readLine()) != null) {
                    result.append(readStr);
                }
                responseString = result.toString();
            } else {
                setFile(action, tagUrl, responseString, PLATFORM_KEY);
            }
        } catch (Exception e) {
            setFile(action, tagUrl, responseString, PLATFORM_KEY);
        } finally {
            myGet.releaseConnection();
        }
        return responseString;
    }

}
