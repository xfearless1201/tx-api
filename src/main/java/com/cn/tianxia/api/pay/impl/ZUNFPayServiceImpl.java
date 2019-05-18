package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName ZUNFPayServiceImpl
 * @Description 尊付支付对接支付宝
 * @Date 2019/3/28 20 09
 **/
public class ZUNFPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(ZUNFPayServiceImpl.class);

    private String merId;//商户号
    private String payUrl;//支付地址
    private String notifyUrl;//回调地址
    private String md5key;//密钥
    private String queryOrderUrl;//订单查询地址

    private static String ret__success = "success";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private boolean verifySuccess = true;//回调验签默认状态为true

    public ZUNFPayServiceImpl() {
    }

    public ZUNFPayServiceImpl(Map<String, String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("merId")){
                this.merId = data.get("merId");
            }
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            if(data.containsKey("md5key")){
                this.md5key = data.get("md5key");
            }
            if(data.containsKey("queryOrderUrl")){
                this.queryOrderUrl = data.get("queryOrderUrl");
            }
        }
    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        this.merId = config.getString("merId");
        this.notifyUrl = config.getString("notifyUrl");
        this.md5key = config.getString("md5key");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[ZUNF]尊付支付   商户返回信息：" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("trxNo");//第三方订单号，流水号
        String order_no = dataMap.get("orderNo");//支付订单号
        String amount = dataMap.get("amount");//实际支付金额，精确到小数点后两位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[ZUNF]尊付支付     获取的{} 流水单号为空",trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[ZUNF]尊付支付   回调订单金额为空");
            return ret__failed;
        }

        String trade_status = dataMap.get("status");  //第三方支付状态，1 支付成功
        String t_trade_status = "1";//第三方成功状态

        //订单查询
        try{
            Map<String,String> queryMap = new HashMap<>();
            queryMap.put("merId", merId);
            queryMap.put("orderNo", order_no);
            queryMap.put("sign", generatorSign(queryMap));

            String queryData = HttpUtils.toPostForm(queryMap, queryOrderUrl);
            logger.info("[ZUNF]尊付支付  订单查询 HTTP请求响应信息：" + JSONObject.fromObject(queryData));

            if(StringUtils.isBlank(queryData)){
                return ret__failed;
            }
            JSONObject jb = JSONObject.fromObject(queryData);
            if(!"1".equals(jb.getString("code"))){
                return ret__failed;
            }
            JSONObject dataJb = jb.getJSONObject("data");
            if(!"1".equals(dataJb.getString("status"))){
                return ret__failed;
            }
        }catch (Exception e){
            e.printStackTrace();
            return ret__failed;
        }

        //写入数据库
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setRealAmount(Double.parseDouble(amount));//以分为单位
        processNotifyVO.setIp(ip);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setInfoMap(JSONObject.fromObject(dataMap).toString());
        processNotifyVO.setT_trade_status(t_trade_status);
        processNotifyVO.setConfig(config);
        processNotifyVO.setPayment("ZUNF");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[ZUNF]尊付支付    回调验签失败");
            return ret__failed;
        }
        return processSuccessNotify(processNotifyVO, verifySuccess);
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    /**
     * 扫码支付
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[ZUNF]尊付支付  扫码支付开始=====================Start=========================");
        try {
            Map<String, String> data = sealRequest(payEntity);
            String responseData = HttpUtils.toPostForm(data, payUrl);
            logger.info("[ZUNF]尊付支付  扫码支付  HTTP请求返回信息：" + JSONObject.fromObject(responseData));

            if(StringUtils.isBlank(responseData)){
                return PayResponse.error("[ZUNF]尊付支付  扫码支付  HTTP请求返回为空");
            }
            //请求信息解析
            JSONObject json = JSONObject.fromObject(responseData);
            if(json.containsKey("code") && "1".equals(json.getString("code"))){
                JSONObject jsonData = json.getJSONObject("data");
                String url = jsonData.getString("url");
                return PayResponse.sm_link(payEntity, url, "下单成功");
            }
            return PayResponse.error("[ZUNF]尊付支付  扫码支付 下单失败");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[ZUNF]尊付支付  扫码支付异常" + e.getMessage());
        }
    }

    /**
     * 回调验签
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {
        String sign = generatorSign(data);
        String sourceSign = data.remove("sign");
        if(sign.equalsIgnoreCase(sourceSign)){
            return ret__success;
        }
        return ret__failed;
    }

    /**
     * 参数组装
     * @param payEntity
     * @return
     */
    public Map<String, String> sealRequest(PayEntity payEntity){
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());

        dataMap.put("merId", merId);//商户编号
        dataMap.put("orderNo", payEntity.getOrderNo());//商户订单号
        dataMap.put("amount", amount);//订单金额
        dataMap.put("payType", payEntity.getPayCode());//支付类型
        dataMap.put("goodsName", "TOP-UP");//商品名称
        //dataMap.put("mp", "recharge");//扩展信息
        dataMap.put("notifyUrl", notifyUrl);//通知url
        dataMap.put("sign", generatorSign(dataMap));//签名数据

        logger.info("[ZUNF]尊付支付 HTTP请求参数：" + JSONObject.fromObject(dataMap));
        return dataMap;
    }

    /**
     * 生成签名
     * @param data
     * @return
     */
    public String generatorSign(Map<String, String> data){
        try {
            StringBuffer sb = new StringBuffer();
            //参数排序
            Map<String, String> dataMap = MapUtils.sortByKeys(data);

            Iterator<String> iterator = dataMap.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String value = dataMap.get(key);
               if(StringUtils.isBlank(value) || "sign".equalsIgnoreCase(key)){
                    continue;
               }
                sb.append(value);
            }
            sb.append(md5key);
            logger.info("[ZUNF]尊付支付  生成签名前参数：" + sb.toString());

            return MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "[ZUNF]尊付支付  生成签名异常";
        }
    }
}
