package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.utils.xm.MD5s;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.*;

public class RXPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(RXPayServiceImpl.class);
    // 接口版本
    private String version;
    // 接口名称
    private String method;
    // 商户号
    private String partner;
    // 异步通知地址
    private String callbackurl;
    // 备注信息
    private String attach;
    // 商品名称
    private String goodsname;
    // MD5签名
    private String sign;
    // 支付地址
    private String payurl;

    public RXPayServiceImpl(Map<String, String> pmap) {
        JSONObject jo = JSONObject.fromObject(pmap);
        if (null != pmap) {
            version = jo.get("version").toString();
            method = jo.get("method").toString();
            partner = jo.get("partner").toString();
            callbackurl = jo.get("callbackurl").toString();
            attach = jo.get("attach").toString();
            sign = jo.get("sign").toString();
            payurl = jo.get("payurl").toString();
            goodsname = jo.get("goodsname").toString();
        }
    }

    /**
     * md5
     *
     * @param paramMap
     * @param sign
     * @return
     */
    private static String getSign(Map<String, String> paramMap, String sign) {
        LinkedHashMap<String, Object> smap = new LinkedHashMap<String, Object>(paramMap);
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, Object> m : smap.entrySet()) {
            Object value = m.getValue();
            if (value != null && StringUtils.isNotBlank(String.valueOf(value))) {
                stringBuffer.append(m.getKey()).append("=").append(m.getValue()).append("&");
            }
        }
        stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());

        String argPreSign = stringBuffer.append(sign).toString();
        logger.info("仁信字符原串:" + argPreSign);
        String signStr = MD5s.EncodeStr(argPreSign, "GB2312", "MD5").toLowerCase();
        logger.info("仁信MD5key:" + signStr);
        return signStr;
    }

    /**
     * 扫码接口
     *
     * @param scanMap
     * @return
     */
    public String bankPay(Map<String, String> scanMap) {
        // 同步URL
        String hrefbackurl = scanMap.get("hrefbackurl");
        // 订单号
        String ordernumber = scanMap.get("ordernumber");
        // 订单金额
        Double paymoney = Double.parseDouble(scanMap.get("paymoney"));
        // 精确到小数点后两位，例如 10.24
        DecimalFormat df = new DecimalFormat("#.00");
        String dec_paymoney = df.format(paymoney);
        // 银行编号
        String banktype = scanMap.get("banktype");

        Map<String, String> paramMap = new LinkedHashMap<String, String>();
        paramMap.put("version", version);
        paramMap.put("method", method);
        paramMap.put("partner", partner);
        paramMap.put("banktype", banktype);
        paramMap.put("paymoney", dec_paymoney + "");
        paramMap.put("ordernumber", ordernumber);
        paramMap.put("callbackurl", callbackurl);
        String md5_sign = getSign(paramMap, sign);
        paramMap.put("hrefbackurl", hrefbackurl);
        paramMap.put("goodsname", goodsname);
        paramMap.put("attach", attach);
        paramMap.put("sign", md5_sign);
        // 是否显示收银台
        paramMap.put("isshow", "1");

        String FormString = "<body onLoad=\"document.actform.submit()\">正在处理请稍候.....................<form  id=\"actform\" name=\"actform\" method=\"post\" action=\""
                + payurl + "\">";
        for (String key : paramMap.keySet()) {
            FormString += "<input name=\"" + key + "\" type=\"hidden\" value='" + paramMap.get(key) + "'>\r\n";
        }
        logger.info("仁信网银支付表单:" + FormString + "</form></body>");
        return FormString += "</form></body>";
    }

    /**
     * 扫码接口
     *
     * @param scanMap
     * @return
     */
    public String scanPay(Map<String, String> scanMap) {
        // 同步URL
        String hrefbackurl = scanMap.get("hrefbackurl");
        // 订单号
        String ordernumber = scanMap.get("ordernumber");
        // 订单金额
        Double paymoney = Double.parseDouble(scanMap.get("paymoney"));
        // 精确到小数点后两位，例如 10.24
        DecimalFormat df = new DecimalFormat("#.00");
        String dec_paymoney = df.format(paymoney);
        // 银行编号
        String banktype = scanMap.get("banktype");

        Map<String, String> paramMap = new LinkedHashMap<String, String>();
        paramMap.put("version", version);
        paramMap.put("method", method);
        paramMap.put("partner", partner);
        paramMap.put("banktype", banktype);
        paramMap.put("paymoney", dec_paymoney + "");
        paramMap.put("ordernumber", ordernumber);
        paramMap.put("callbackurl", callbackurl);
        String md5_sign = getSign(paramMap, sign);
        paramMap.put("hrefbackurl", hrefbackurl);
        paramMap.put("goodsname", goodsname);
        paramMap.put("attach", attach);
        paramMap.put("sign", md5_sign);
        // 是否显示收银台
        paramMap.put("isshow", "1");
        // try {
        // JSONObject json = JSONObject.fromObject(postByHttpClient(payurl,
        // paramMap));
        // if (json.containsKey("status") &&
        // "1".equals(json.getString("status")) && json.containsKey("qrurl")
        // && StringUtils.isNotBlank(json.getString("qrurl"))) {
        // return getReturnJson("success", json.getString("qrurl"),
        // "二维码地址获取成功！");
        // } else {
        // return getReturnJson("error", "", json.getString("message"));
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // return getReturnJson("error", "", "获取二维码失败！");
        // }

        String FormString = "<body onLoad=\"document.actform.submit()\">正在处理请稍候.....................<form  id=\"actform\" name=\"actform\" method=\"post\" action=\""
                + payurl + "\">";
        for (String key : paramMap.keySet()) {
            FormString += "<input name=\"" + key + "\" type=\"hidden\" value='" + paramMap.get(key) + "'>\r\n";
        }
        logger.info("仁信扫码支付表单:" + FormString + "</form></body>");
        return FormString += "</form></body>";
    }

    public JSONObject getReturnJson(String status, String qrCode, String msg) {
        JSONObject json = new JSONObject();
        json.put("status", status);
        json.put("qrCode", qrCode);
        json.put("msg", msg);
        return json;
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     * @return
     */
    public String postByHttpClient(String url, Map<String, String> params) {
        try {
            HttpPost httpPost = new HttpPost(url);
            CloseableHttpClient client = HttpClients.createDefault();
            String respContent = null;

            // 表单方式
            List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
            if (params != null && params.size() > 0) {
                for (String key : params.keySet()) {
                    pairList.add(new BasicNameValuePair(key, params.get(key)));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, "utf-8"));

            HttpResponse resp = client.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() == 200) {
                HttpEntity he = resp.getEntity();
                respContent = EntityUtils.toString(he, "UTF-8");
            }
            return respContent;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }

    }

    /**
     * 回调方法
     *
     * @param infoMap
     * @return
     */
    @Override
    public String callback(Map<String, String> infoMap) {
        Map<String, String> paramMap = new LinkedHashMap<String, String>();
        paramMap.put("partner", infoMap.get("partner"));
        paramMap.put("ordernumber", infoMap.get("ordernumber"));
        paramMap.put("orderstatus", infoMap.get("orderstatus"));
        paramMap.put("paymoney", infoMap.get("paymoney"));
        String serverSign = infoMap.get("sign");

        String md5_sign = getSign(paramMap, sign);

        if (md5_sign.equals(serverSign)) {
            logger.info("仁信支付签名成功！");
            return "success";
        }
        logger.info("仁信支付签名失败！");
        return "";
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        Double amount = payEntity.getAmount();
        String order_no = payEntity.getOrderNo();
        String refereUrl = payEntity.getRefererUrl();
        String pay_code = payEntity.getPayCode();
//		String userName = payEntity.getUsername();
        String pay_url = payEntity.getPayUrl();

        Map<String, String> bankMap = new HashMap<>();
        bankMap.put("hrefbackurl", refereUrl);
        bankMap.put("ordernumber", order_no);// 订单号
        bankMap.put("paymoney", String.valueOf(amount));// 金额
        bankMap.put("banktype", pay_code);
        String html = bankPay(bankMap);
        if (StringUtils.isEmpty(html)) {
            logger.info("仁信支付表单生成异常！");
            return PayUtil.returnWYPayJson("error", "", "", "", "");
        }
        return PayUtil.returnWYPayJson("success", "form", html, pay_url, "");
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        Double amount = payEntity.getAmount();
        String order_no = payEntity.getOrderNo();
        String refereUrl = payEntity.getRefererUrl();
        String pay_code = payEntity.getPayCode();
        String userName = payEntity.getUsername();

        Map<String, String> scanMap = new HashMap<>();
        scanMap.put("hrefbackurl", refereUrl);
        scanMap.put("ordernumber", order_no);// 订单号
        scanMap.put("paymoney", String.valueOf(amount));// 金额
        scanMap.put("banktype", pay_code);

        String form = scanPay(scanMap);
        if (StringUtils.isEmpty(form)) {
            return PayUtil.returnPayJson("error", "1", "扫码表单生成异常！", userName, payEntity.getAmount(), order_no, "");
        }
        return PayUtil.returnPayJson("success", "1", "支付接口请求成功!", userName, payEntity.getAmount(), order_no, form);
    }

}
