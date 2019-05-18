package com.cn.tianxia.api.common;

import java.util.Arrays;
import java.util.List;

import com.cn.tianxia.api.utils.pay.RandomUtils;
import com.cn.tianxia.api.utils.ys.DateUtil;

import net.sf.json.JSONObject;

/**
 * @author zw
 */
public class PayUtil {

    /**
     * 生成订单号
     *
     * @param provider 支付商编号
     * @param cagent   平台商编号
     */
    public static String generatorPayOrderNo(String provider, String cagent) {
        String orderNo;
        //18位订单
        String[] strArr18 = {"YJZF", "ZHZF"};
        //20位订单
        String[] strArr20 = {"WT", "DAF", "NWT", "NOMQ", "BJYX", "ABH", "YIFA", "YBT", "SYB", "SLJH", "STZF", "XINFA","QJF", "PA", "TITI", 
                "TT2", "YTBP", "JHNF", "ZGZF", "CORAL", "XFB", "EASY", "JIAN", "TXZF", "ZHUI", "HPZF","FYZF", "YXIN", "IIZF", "WTXX", "YICZF", 
                "SHAN", "DBTX", "HDZF", "JIDA", "XHZF", "SLZF", "YONGH","NJZF", "KM", "MQZF", "HPAY", "TTJH", "CN","NS", "DCZF", "JDBZ","TDI",
                "XQL","LMF","JJB", "JK", "HYF", "KY","EXZF"};

        List<String> asList = Arrays.asList(strArr18);
        boolean result = asList.stream().anyMatch(e -> e.equals(provider));
        if (result) {
            orderNo = createOrderNo(cagent, provider, 0);
            return orderNo;
        }
        asList = Arrays.asList(strArr20);
        result = asList.stream().anyMatch(e -> e.equals(provider));
        if (result) {
            orderNo = createOrderNo(cagent, provider, 1);
            return orderNo;
        }
        //默认生成32位订单
        orderNo = createOrderNo(cagent, provider, 2);
        return orderNo;
    }

    /**
     * 生成订单号
     *
     * @param provider 支付商编号
     * @param cagent   平台商编号
     * @param type     0 : 18位     1： 20位     2:   32位
     */
    public static String createOrderNo(String cagent, String provider, int type) {
        // ---------------生成支付订单号 开始------------------------
        StringBuilder sb = new StringBuilder();
        String builder = String.valueOf(
                sb.append(cagent.toUpperCase())
                        .append(provider.toLowerCase())
                        .append(System.currentTimeMillis()).append(RandomUtils.generateNumberStr(2)));
        if (type == 0) {
            return builder.substring(0, 18);
        } else if (type == 1) {
            return builder.substring(0, 20);
        } else {
            // 17位 当前时间 yyyyMMddHHmmssSSS
            String currTime = DateUtil.getCurrentDate("yyyyMMddHHmmssSSS");
            String strTime;
            int length = 4;
            if (provider.length() > length) {
                //2位
                strTime = currTime.substring(15, currTime.length());
            } else {
                //3位
                strTime = currTime.substring(14, currTime.length());
            }
            // 5位随机数
            String strRandom = RandomUtils.generateNumberStr(5);
            // 8位序列号,可以自行调整。
            String strReq = strTime + strRandom;

            return cagent.toUpperCase() + provider.toLowerCase() + currTime + strReq;
        }
    }
    
    /**
     * 扫码接口返回
     */
    public static JSONObject returnPayJson(String status, String type, String msg, String username, double amount,
                                           String orderNo, String retStr) {
        JSONObject json = new JSONObject();
        json.put("res_type", type);
        json.put("status", status);
        json.put("msg", msg);
        json.put("acount", String.valueOf(amount));
        json.put("user_name", username);
        json.put("order_no", orderNo);

        if ("1".equals(type)) {
            // 表单提交方式
            json.put("html", retStr);
        } else if ("2".equals(type)) {
            // 二维码图片生成
            json.put("qrcode", retStr);
        } else if ("3".equals(type)) {
            // 二维码图片连接
            json.put("qrcode_url", retStr);
        } else if ("4".equals(type)) {
            // 跳转连接
            json.put("html", retStr);
        }
        return json;
    }

    /**
     * 网银返回 type分为三种 1form 表单提交 2jsp jsp页面提交表单 3 link 详见QYZF 跳转连接
     * *********************XM GT JH JHZ 使用jsp类型
     */
    public static JSONObject returnWYPayJson(String status, String type, String content, String payUrl,
                                             String jspName) {
        JSONObject json = new JSONObject();
        json.put("status", status);
        json.put("type", type);
        if ("form".equals(type)) {
            // 表单提交方式
            json.put("form", content);
            json.put("redirect", "redirect:http://" + payUrl + "/pay.action");
        } else if ("jsp".equals(type)) {
            // jsp页面提交
            json.put("jsp_name", jspName);
            json.put("jsp_content", content);
        } else if ("link".equals(type)) {
            // 跳转连接
            json.put("link", content);
        } else if ("jumpPay".equals(type)) {
            json.put("jumpPay", content);
            json.put("redirect", "redirect:http://" + payUrl);
        }
        return json;
    }

}


