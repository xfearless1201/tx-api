package com.cn.tianxia.api.pay.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.utils.daqiang.HttpPostUtils;
import com.cn.tianxia.api.utils.daqiang.SignUtils;
import com.cn.tianxia.api.utils.daqiang.StrUtils;

import net.sf.json.JSONObject;

/**
 * @ClassName DQPayServiceImpl
 * @Description 大强支付
 * @author zw
 * @Date 2018年8月22日 下午3:04:11
 * @version 1.0.0
 */
public class DQPayServiceImpl implements PayService {

    private String merchantNo;

    private String md5Key;

    private String payUrl;

    private String version;// 版本

    private String goodsInfo;// 商品描述

    private String noticeUrl;// 通知地址

    private String signType;// 签名类型

    private final static Logger logger = LoggerFactory.getLogger(DQPayServiceImpl.class);

    public DQPayServiceImpl(Map<String, String> pmap) {
        if (pmap != null) {
            if (pmap.containsKey("merchantNo")) {
                this.merchantNo = pmap.get("merchantNo");
            }
            if (pmap.containsKey("md5Key")) {
                this.md5Key = pmap.get("md5Key");
            }
            if (pmap.containsKey("payUrl")) {
                this.payUrl = pmap.get("payUrl");
            }
            if (pmap.containsKey("version")) {
                this.version = pmap.get("version");
            }
            if (pmap.containsKey("goodsInfo")) {
                this.goodsInfo = pmap.get("goodsInfo");
            }
            if (pmap.containsKey("noticeUrl")) {
                this.noticeUrl = pmap.get("noticeUrl");
            }
            if (pmap.containsKey("signType")) {
                this.signType = pmap.get("signType");
            }
        }
    }
    
    /**
     * 网银支付
     */
    @Override
    public JSONObject wyPay(PayEntity entity) {
        logger.info("[DQ]大强网银支付开始================START================");
        try {
            Map<String, String> xyMap = getIPXY(entity.getIp());
            String longitude = xyMap.get("y");// Varchar(20) 必填 加上提高成功率
            String latitude = xyMap.get("x");// Varchar(20) 必填 加上提高成功率
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            //封装支付请求参数
            Map<String,String> map = new HashMap<String,String>();
            map.put("version",version);//接口版本号,固定值:1.0
            map.put("merchantNo",merchantNo);//商户号,商户平台提供
            map.put("memberOrderId",entity.getOrderNo());//商户唯一订单号
            map.put("payType","B2C");//支付方式
            map.put("createTime",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//订单时间,格式:yyyyMMddHHmmss
            map.put("orderAmount",amount);//订单金额:单位： 元 兩位小數
            map.put("bankCode",entity.getPayCode());//（网银必填） 取值参照网银代码表 4.1
//            map.put("bankCode","PSBC");//（网银必填） 取值参照网银代码表 4.1
            map.put("goodsInfo",goodsInfo);//商品描述
            map.put("longitude",longitude);//经度,加上提高成功率
            map.put("latitude",latitude);//纬度,加上提高成功率
            map.put("clientIP",entity.getIp());//客户IP,加上提高成功率
            map.put("noticeUrl",noticeUrl);//异步通知地址
            map.put("signType",signType);//0 MD5 默认 1 RSA
            map.put("ext","");//扩展字段
            map.put("key", md5Key);
            //生成待签名串
            String signStr = StrUtils.createRetStr(map);
            logger.info("[DQ]待签名原串:" + signStr);
            //生成签名串
            String sign = SignUtils.md5(signStr);
            logger.info("[DQ]生成MD5签名串:"+sign);
            map.put("sign", sign);
            map.remove("key");
            //发起请求
            String reqStr = StrUtils.createReqparam(map);
            logger.info("[DQ]大强请求地址:" + payUrl);
            logger.info("[DQ]大强请求参数:" + reqStr);
            String response = HttpPostUtils.httpClientPost(payUrl, reqStr, "utf-8");
            logger.info("[DQ]大强响应内容:" + response);
            if(StringUtils.isNotBlank(response)){
                logger.info("[DQ]大强网银支付调用第三方接口返回结果:"+response);
                return PayUtil.returnWYPayJson("success", "form", response, entity.getPayUrl(), "");
            }
            logger.error("[DQ]大强网银支付失败,调用第三方接口返回结果:"+response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[DQ]大强网银支付异常:"+e.getMessage());
        }
        return PayUtil.returnWYPayJson("error", "", "", "", "");
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        String mobile = payEntity.getMobile();
        JSONObject r_json = null;
        String form = "";
        // pc端接口
        if (StringUtils.isBlank(mobile)) {
            // pc端快捷支付
            if ("QUICK_H5".equals(payEntity.getPayCode()) || "QUICK".equals(payEntity.getPayCode())) {
                form = showPay(payEntity);
                if (StringUtils.isBlank(form)) {
                    return PayUtil.returnPayJson("error", "1", "支付表单生成失败！联系开发找bug", payEntity.getUsername(),
                            payEntity.getAmount(), payEntity.getOrderNo(), "");
                } else {
                    return PayUtil.returnPayJson("success", "1", "支付接口请求成功!", payEntity.getUsername(),
                            payEntity.getAmount(), payEntity.getOrderNo(), form);
                }
            }

            r_json = scanPay(payEntity);
            if ("success".equals(r_json.getString("status"))) {
                return PayUtil.returnPayJson("success", "2", "支付接口请求成功!", payEntity.getUsername(),
                        payEntity.getAmount(), payEntity.getOrderNo(), r_json.getString("qrCode"));
            } else {
                return PayUtil.returnPayJson("error", "2", r_json.getString("msg"), payEntity.getUsername(),
                        payEntity.getAmount(), payEntity.getOrderNo(), "");
            }

        } else {
            // 手机端接口
            form = showPay(payEntity);
            if (StringUtils.isBlank(form)) {
                return PayUtil.returnPayJson("error", "1", "支付表单生成失败！联系开发找bug", payEntity.getUsername(),
                        payEntity.getAmount(), payEntity.getOrderNo(), "");
            } else {
                return PayUtil.returnPayJson("success", "1", "支付接口请求成功!", payEntity.getUsername(),
                        payEntity.getAmount(), payEntity.getOrderNo(), form);
            }
        }
    }

    /**
     * 大强扫码支付接口
     * 
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     */
    public JSONObject scanPay(PayEntity payEntity) {
        String memberOrderId = payEntity.getOrderNo();
        String payType = payEntity.getPayCode(); // ALI_QR 微信扫码：WECHAT_QR 支付宝H5：ALI_H5
        String createTime = StrUtils.getDateymdhms(new Date());

        DecimalFormat df = new DecimalFormat("#########.00");
        String orderAmount = df.format(payEntity.getAmount());// 分为元
        String clientIP = payEntity.getIp();// "117.10.153.53";

        String respone = "";
        try {
            Map<String, String> xyMap = getIPXY(clientIP);

            String longitude = xyMap.get("y");// Varchar(20) 必填 加上提高成功率
            String latitude = xyMap.get("x");// Varchar(20) 必填 加上提高成功率

            // 构建请求参数
            Map<String, String> reqMap = new LinkedHashMap<String, String>();
            reqMap.put("version", version);
            reqMap.put("createTime", createTime);
            reqMap.put("goodsInfo", goodsInfo);
            reqMap.put("memberOrderId", memberOrderId);
            reqMap.put("merchantNo", merchantNo);
            reqMap.put("payType", payType);
            reqMap.put("orderAmount", orderAmount);
            reqMap.put("noticeUrl", noticeUrl);
            reqMap.put("signType", signType);
            reqMap.put("key", md5Key);
            reqMap.put("longitude", longitude);
            reqMap.put("latitude", latitude);
            reqMap.put("clientIP", clientIP);
            String signStr = StrUtils.createRetStr(reqMap);
            logger.info("签名原串:" + signStr);
            // // 组装签名参数
            String sign = SignUtils.md5(signStr);
            reqMap.put("sign", sign);
            reqMap.remove("key");
            // 请求
            String reqStr = StrUtils.createReqparam(reqMap);
            logger.info("大强请求地址:" + payUrl);
            logger.info("大强请求参数:" + reqStr);
            respone = HttpPostUtils.httpClientPost(payUrl, reqStr, "utf-8");
            logger.info("大强响应内容:" + respone);

            JSONObject resposeJson = JSONObject.fromObject(respone);
            if (resposeJson.containsKey("status") && "0000".equals(resposeJson.getString("status"))) {
                String qrCodeUrl = resposeJson.getString("url");
                return getReturnJson("success", qrCodeUrl, "二维码连接获取成功！");
            }
            return getReturnJson("error", "", respone);
        } catch (Exception e) {
            e.printStackTrace();
            return getReturnJson("error", "", respone);
        }
    }

    /**
     * @Description 大强收银台模式
     * @param payEntity
     * @return
     */
    public String showPay(PayEntity payEntity) {
        String memberOrderId = payEntity.getOrderNo();
        String payType = payEntity.getPayCode();
        String createTime = StrUtils.getDateymdhms(new Date());
        DecimalFormat df = new DecimalFormat("#########.00");
        String orderAmount = df.format(payEntity.getAmount());// 分为元
        String clientIP = payEntity.getIp();// "117.10.153.53";

        String respone = "";
        try {
            Map<String, String> xyMap = getIPXY(clientIP);

            String longitude = xyMap.get("y");// Varchar(20) 必填 加上提高成功率
            String latitude = xyMap.get("x");// Varchar(20) 必填 加上提高成功率

            // 构建请求参数
            Map<String, String> reqMap = new LinkedHashMap<String, String>();
            reqMap.put("version", version);
            reqMap.put("createTime", createTime);
            reqMap.put("goodsInfo", goodsInfo);
            reqMap.put("memberOrderId", memberOrderId);
            reqMap.put("merchantNo", merchantNo);
            reqMap.put("payType", payType);
            reqMap.put("orderAmount", orderAmount);
            reqMap.put("noticeUrl", noticeUrl);
            reqMap.put("signType", signType);
            reqMap.put("key", md5Key);
            reqMap.put("longitude", longitude);
            reqMap.put("latitude", latitude);
            reqMap.put("clientIP", clientIP);
            String signStr = StrUtils.createRetStr(reqMap);
            logger.info("签名原串:" + signStr);
            // // 组装签名参数
            String sign = SignUtils.md5(signStr);
            reqMap.put("sign", sign);
            reqMap.remove("key");

            return buildForm(reqMap);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 跟进IP获取对应的经纬度（为空返回当前机器经纬度）
     *
     * @param ip
     * @return map
     */
    public static Map<String, String> getIPXY(String ip) {
        if (null == ip) {
            ip = "";
        }
        Map<String, String> xyMap = new HashMap<>();
        try {
            URL url = new URL("http://ip-api.com/json/" + ip);
            InputStream inputStream = url.openStream();
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputReader);
            StringBuffer sb = new StringBuffer();
            String str = "";
            while ((str = reader.readLine()) != null) {
                sb.append(str.trim());
            }
            reader.close();
            logger.info("获取经纬度:" + sb.toString());
            // 解析sb内容
            Map<String, Object> strMap = JSONObject.fromObject(sb.toString());
            String status = strMap.get("status").toString();
            if (strMap != null && status.equals("success")) {
                String lat = strMap.get("lat").toString();
                String lon = strMap.get("lon").toString();
                xyMap.put("x", lat);
                xyMap.put("y", lon);
            } else {
                xyMap.put("x", "0");
                xyMap.put("y", "0");
            }
            return xyMap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xyMap;
    }

    /**
     * 结果返回
     * 
     * @param status
     * @param qrCode
     * @param msg
     * @return
     */
    private JSONObject getReturnJson(String status, String qrCode, String msg) {
        JSONObject json = new JSONObject();
        json.put("status", status);
        json.put("qrCode", qrCode);
        json.put("msg", msg);
        return json;
    }

    private String buildForm(Map<String, String> params) {
        String FormString = "<body onLoad=\"document.actform.submit()\">正在处理请稍候.....................<form  id=\"actform\" name=\"actform\" method=\"post\" action=\""
                + payUrl + "\">";
        for (String key : params.keySet()) {
            if (!StringUtils.isBlank(params.get(key).toString()))
                FormString += "<input name=\"" + key + "\" type=\"hidden\" value='" + params.get(key) + "'>\r\n";
        }
        FormString += "</form></body>";
        logger.info("大强支付表单:" + FormString);
        return FormString;
    }

    /**
     * @Description 回调验签
     * @param map
     * @return
     */
    @Override
    public String callback(Map<String, String> map) {
        try {
            String sourceSign = map.get("sign").toLowerCase();
            logger.info("[DQ]大强支付回调原签名串:"+sourceSign);
            map.put("key", md5Key);
            String sign = generatorSign(map);
            logger.info("[DQ]大强支付回调:本地签名:" + sign + "      服务器签名:" + sourceSign);
            if(sign.equals(sourceSign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[DQ]大强支付回调验签异常:"+e.getMessage());
        }
        return "";
    }
    
    
    private String generatorSign(Map<String,String> data){
        try {
            Map<String,String> treemap = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    // TODO Auto-generated method stub
                    return o1.compareTo(o2);
                }
            });
            treemap.putAll(data);
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = treemap.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = treemap.get(key);
                if(StringUtils.isBlank(val) || key.equals("sign")) continue;
                sb.append("&").append(key).append("=").append(val);
            }
            String signStr = sb.toString().replaceFirst("&", "");
            logger.info("[DQ]大强支付生成待签名串:"+signStr);
            String sign = SignUtils.md5(signStr);
            logger.info("[DQ]大强支付生成签名加密串:"+sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[DQ]大强支付生成签名异常:"+e.getMessage());
            return "";
        }
    }
}
