package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.utils.ly.HttpMethod;
import com.cn.tianxia.api.utils.ly.HttpSendModel;
import com.cn.tianxia.api.utils.ly.SimpleHttpResponse;
import com.cn.tianxia.api.utils.mkt.HttpUtil;
import com.cn.tianxia.api.utils.qyf.ToolKit;

import net.sf.json.JSONObject;

/**
 * @ClassName XPAYPayServiceImpl
 * @Description XPAY支付
 * @author zw
 * @Date 2018年8月11日 下午5:55:39
 * @version 1.0.0
 */
public class XPAYPayServiceImpl implements PayService {

    private String pay_memberid;

    private String pay_url;

    private String pay_md5key;

    private String pay_notifyurl;

    private final static Logger logger = LoggerFactory.getLogger(XPAYPayServiceImpl.class);

    public XPAYPayServiceImpl(Map<String, String> pmap) {
        if (pmap != null) {
            if (pmap.containsKey("pay_memberid")) {
                this.pay_memberid = pmap.get("pay_memberid");
            }
            if (pmap.containsKey("pay_url")) {
                this.pay_url = pmap.get("pay_url");
            }
            if (pmap.containsKey("pay_md5key")) {
                this.pay_md5key = pmap.get("pay_md5key");
            }
            if (pmap.containsKey("pay_notifyurl")) {
                this.pay_notifyurl = pmap.get("pay_notifyurl");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        String form = scanPay(payEntity);
        return PayUtil.returnWYPayJson("success", "form", form, pay_url, "");
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        String out_trade_no = payEntity.getOrderNo();// 商户订单号
        double total_fee = payEntity.getAmount();// 123;//"12300";//支付金额，分
        String userName = payEntity.getUsername();
        // String mobile = payEntity.getMobile();

        String form = scanPay(payEntity);
        return PayUtil.returnPayJson("success", "1", "支付接口请求成功!", userName, total_fee, out_trade_no, form);
    }

    /**
     * @Description 扫码收银台模式
     * @param payEntity
     * @return
     */
    public String scanPay(PayEntity payEntity) {
        // 参数名称 必填 加入签名 长度定义 说明
        // String pay_memberid; // 是 是 30 商户在支付平台的唯一标 识
        String pay_orderid = payEntity.getOrderNo(); // 是 是 32 商户系统产生的唯一订单号
        DecimalFormat df = new DecimalFormat("#############");
        String pay_amount = df.format(payEntity.getAmount()); // 是 是 30 以“元”为单位，仅允许两位小数，必须大于零
        String pay_applydate = new SimpleDateFormat("YYYYMMDDHHMMSS").format(new Date()); // 是 是 14 商户系统生成的订单日期
        /**
         * 是 是 10 ALIPAY_WAP：支付宝 WAP ALIPAY：支付宝扫码 BANK：网银 BANK_WAP：网银快捷 WECHAT：微信扫码 WECHAT_WAP：微信 WAP QQ：QQ
         * 扫码QQ_WAP：QQWAP JD：京东扫码JD_WAP：京东 WAP BARCODE：条码充值
         **/
        String pay_channelCode = payEntity.getPayCode();

        // String pay_notifyurl;

        // String pay_bankcode;// 否 否 15 空值

        // String pay_reserved;// 否 否 240 英文或中文字符串,支付完成后，按照原样返回给商户 产品名称

        // String pay_productname;// 否 否 120 一般填写商户的商品名 称，英文或中文字符串

        // String pay_productnum;// 否 否 2 一般填写商户的商品数量
        // String isMobile;// 否 否 5 若是移动端请填写 true 字 符串预设是 false 字符串

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("pay_memberid", pay_memberid);
        paramsMap.put("pay_orderid", pay_orderid);
        paramsMap.put("pay_amount", pay_amount);
        paramsMap.put("pay_applydate", pay_applydate);
        paramsMap.put("pay_channelCode", pay_channelCode);
        paramsMap.put("pay_notifyurl", pay_notifyurl);
        /** 商户对交易数据的签名，（最后转出大写）签 名方式参照签名档例子。 **/
        // String pay_md5sign = ToolKit.MD5(buildPrePayParams(paramsMap, this.pay_md5key.toUpperCase()), "UTF-8");

        String ss = "pay_memberid^" + pay_memberid + "&pay_orderid^" + pay_orderid + "&pay_amount^" + pay_amount
                + "&pay_applydate^" + pay_applydate + "&pay_channelCode^" + pay_channelCode + "&pay_notifyurl^"
                + pay_notifyurl + "&key=" + this.pay_md5key;
        logger.info("待签名字符原串:" + ss);
        String pay_md5sign = ToolKit.MD5(ss, "UTF-8");

        paramsMap.put("pay_md5sign", pay_md5sign);

        // String rst = RequestForm(pay_url, paramsMap);
        //
        // System.out.println(rst);

        return HtmlFrom(this.pay_url, paramsMap);

        // return null;
    }

    /**
     * HTTP post 请求
     * 
     * @param Url
     * @param Parms
     * @return
     */
    public String RequestForm(String Url, Map<String, Object> Parms) {
        if (Parms.isEmpty()) {
            return "参数不能为空！";
        }
        String PostParms = "";
        int PostItemTotal = Parms.keySet().size();
        int Itemp = 0;
        for (String key : Parms.keySet()) {
            PostParms += key + "=" + Parms.get(key);
            Itemp++;
            if (Itemp < PostItemTotal) {
                PostParms += "&";
            }
        }
        logger.info("【XPAY请求参数】：" + PostParms);
        HttpSendModel httpSendModel = new HttpSendModel(Url + "?" + PostParms);
        logger.info("【XPAY后端请求】：" + Url + "?" + PostParms);
        httpSendModel.setMethod(HttpMethod.POST);
        SimpleHttpResponse response = null;
        try {
            response = HttpUtil.doRequest(httpSendModel, "UTF-8");
        } catch (Exception e) {
            return e.getMessage();
        }
        return response.getEntityString();
    }

    /**
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param sb
     * @param payParams
     * @param md5Key
     */
    public static String buildPrePayParams(Map<String, Object> payParams, String md5Key) {
        StringBuilder sb = new StringBuilder((payParams.size() + 1) * 10);
        List<String> keys = new ArrayList<String>(payParams.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            String str = String.valueOf(payParams.get(key));
            if (str == null || str.length() == 0) {
                // 空串不参与sign计算
                continue;
            }
            sb.append(key).append("=");
            sb.append(str);
            sb.append("&");
        }
        sb.setLength(sb.length() - 1);
        sb.append("&key=" + md5Key);
        logger.info("待签名字符:" + sb.toString());
        return sb.toString();
    }

    /**
     * @Description 创建form表单
     * @param Url
     * @param Parms
     * @return
     */
    public String HtmlFrom(String Url, Map<String, Object> Parms) {
        if (Parms.isEmpty()) {
            return "参数不能为空！";
        }
        String FormString = "<body onLoad=\"document.actform.submit()\">正在处理请稍候.....................<form  id=\"actform\" name=\"actform\" method=\"post\" action=\""
                + Url + "\">";
        for (String key : Parms.keySet()) {
            FormString += "<input name=\"" + key + "\" type=\"hidden\" value='" + Parms.get(key) + "'>\r\n";
        }

        FormString += "</form></body>";
        logger.info("XPAY表单:" + FormString);
        return FormString;
    }

    @Override
    public String callback(Map<String, String> map) {
        String serverSign = map.get("sign");
        StringBuffer sb = new StringBuffer();
        if (map.containsKey("amount")) {
            sb.append("amount^").append(map.get("amount"));
        }
        if (map.containsKey("datetime")) {
            sb.append("&datetime^").append(map.get("datetime"));
        }
        if (map.containsKey("memberid")) {
            sb.append("&memberid^").append(map.get("memberid"));
        }
        if (map.containsKey("orderid")) {
            sb.append("&orderid^").append(map.get("orderid"));
        }
        if (map.containsKey("returncode")) {
            sb.append("&returncode^").append(map.get("returncode"));
        }
        sb.append("&key=" + this.pay_md5key);

        logger.info("验签待签名字符串:" + sb.toString());
        String localSign = ToolKit.MD5(sb.toString(), "UTF-8");

        logger.info("本地签名:" + localSign + "      服务器签名:" + serverSign);
        if (serverSign.equalsIgnoreCase(localSign)) {
            return "success";
        }
        return "";
    }

}
