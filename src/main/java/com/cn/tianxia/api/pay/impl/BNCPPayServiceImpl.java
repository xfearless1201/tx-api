package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName BNCPPayServiceImpl
 * @Description BNCP支付渠道网银，支付宝
 * @Date 2019/3/27 16 44
 **/
public class BNCPPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(BNCPPayServiceImpl.class);

    public String customerid;//商户号
    public String payUrl;//支付地址
    public String notifyUrl;//回调地址
    public String queryOrderUrl;//查询地址
    public String md5key;//密钥
    public String version;//版本号

    public BNCPPayServiceImpl() {
    }

    public BNCPPayServiceImpl(Map<String, String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("customerid")){
                this.customerid = data.get("customerid");
            }
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            if(data.containsKey("queryOrderUrl")){
                this.queryOrderUrl = data.get("queryOrderUrl");
            }
            if(data.containsKey("md5key")){
                this.md5key = data.get("md5key");
            }
            if(data.containsKey("version")){
                this.version = data.get("version");
            }
        }
    }

    private static String ret__success = "success";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private boolean verifySuccess = true;//回调验签默认状态为true

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        this.md5key = config.getString("md5key");
        this.customerid = config.getString("customerid");
        this.notifyUrl = config.getString("notifyUrl");
        this.queryOrderUrl = config.getString("queryOrderUrl");
        this.version = config.getString("version");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[BNCP]BNCP支付   商户返回信息：" + dataMap);

        String trade_no = dataMap.get("sdpayno");//第三方订单号，流水号
        String order_no = dataMap.get("sdorderno");//支付订单号
        String amount = dataMap.get("total_fee");//商户订单总金额，订单总金额以元为单位，精确到小数点后两位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);  //回调ip

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[BNCP]BNCP支付    获取的 流水单号为空");
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[BNCP]BNCP支付   回调订单金额为空");
            return ret__failed;
        }

        //订单查询
        try{
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("customerid", customerid);
            queryMap.put("sdorderno", order_no);
            String time = new SimpleDateFormat("yyyymmddhhmmss").format(new Date());
            queryMap.put("reqtime", time);
            queryMap.put("sign", generatorSign(queryMap, 3));

            String queryData = HttpUtils.toPostForm(queryMap, queryOrderUrl);
            logger.info("[BNCP]BNCP支付  订单查询结果：" + queryData);
            if(StringUtils.isBlank(queryData)){
                return ret__failed;
            }
            JSONObject jb = JSONObject.fromObject(queryData);
            if(jb.containsKey("status") && "0".equals(jb.getString("status"))){
                return ret__failed;
            }
        }catch (Exception e){
            e.printStackTrace();
            return ret__failed;
        }

        String trade_status = dataMap.get("status");  //第三方支付状态，success = 支付成功
        String t_trade_status = "1";//第三方成功状态

        //写入数据库
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setRealAmount(Double.parseDouble(amount));
        processNotifyVO.setIp(ip);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setInfoMap(JSONObject.fromObject(dataMap).toString());
        processNotifyVO.setT_trade_status(t_trade_status);
        processNotifyVO.setConfig(config);
        processNotifyVO.setPayment("BNCP");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[BNCP]BNCP支付    回调验签失败");
            return ret__failed;
        }
        return processSuccessNotify(processNotifyVO, verifySuccess);
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[BNCP]BNCP支付 网银支付  开始===========================start==================================");
        try{
            Map<String, String> dataMap = sealRequst(payEntity, 1);
            logger.info("[BNCP]BNCP支付 网银支付  发起HTTP请求 参数：" +JSONObject.fromObject(dataMap));
            String responseData = HttpUtils.generatorForm(dataMap, payUrl);

            return PayResponse.wy_write(responseData);
        }catch (Exception e){
            e.printStackTrace();
            return PayResponse.error("[BNCP]BNCP支付 网银支付 异常");
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[BNCP]BNCP支付 扫码支付  开始===========================start==================================");
        try{
            Map<String, String> dataMap = sealRequst(payEntity, 2);

            String responseData = HttpUtils.generatorForm(dataMap, payUrl);

            //logger.info("[BNCP]BNCP支付 扫码支付  HTTP请求返回信息：" + JSONObject.fromObject(responseData));

            return PayResponse.sm_form(payEntity,responseData,"下单成功");
        }catch (Exception e){
            e.printStackTrace();
            return PayResponse.error("[BNCP]BNCP支付 扫码支付 异常");
        }
    }

    /**
     * 回调验签
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {
        String sign = generatorSign(data, 2);
        String sourceSign = data.remove("sign");
        if(sign.equals(sourceSign)){
            return ret__success;
        }
        return ret__failed;
    }

    /**
     * 组装参数
     * @param payEntity
     * @param type
     * @return
     */
    public Map<String, String> sealRequst(PayEntity payEntity, int type){
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());
        dataMap.put("version", version);//版本号
        dataMap.put("customerid", customerid);//商户编号
        dataMap.put("sdorderno", payEntity.getOrderNo());//商户订单号
        dataMap.put("number", amount);//订单金额
        dataMap.put("currency", "CNY");//币种
        if(1 == type){
            dataMap.put("paytype", "bank");//支付编号
        }else {
            dataMap.put("paytype", payEntity.getPayCode());//支付编号
        }
        dataMap.put("bankcode","ICBC");//银行编号
        dataMap.put("notifyurl", notifyUrl);//异步通知
        dataMap.put("returnurl", payEntity.getRefererUrl());//同步跳转
        dataMap.put("remark", "TOP-UP");//订单备注说明
        dataMap.put("sign", generatorSign(dataMap, 1));//签名串

        logger.info("[BNCP]BNCP支付 扫码支付  HTTP请求参数：" +JSONObject.fromObject(dataMap));
        return dataMap;
    }

    /**
     * 生成签名
     * @param data
     * @param type
     * @return
     */
    public String generatorSign(Map<String, String> data, int type){
        logger.info("[BNCP]BNCP支付 生成签名  开始===========================start==================================");
        try {
            StringBuffer sb = new StringBuffer();
            if(1 == type){
                //$signstr='version='.$version.'&currency='.$currency.'&customerid='.$customerid.'&sdorderno='.$sdorderno.'&notifyurl='.$notifyurl.'&returnurl='.$returnurl.'&'.$userkey;
                //$sign=md5($signstr);
                sb.append("version=").append(data.get("version")).append("&");
                sb.append("currency=").append(data.get("currency")).append("&");
                sb.append("customerid=").append(data.get("customerid")).append("&");
                sb.append("sdorderno=").append(data.get("sdorderno")).append("&");
                sb.append("notifyurl=").append(data.get("notifyurl")).append("&");
                sb.append("returnurl=").append(data.get("returnurl")).append("&");
            }else if(2 == type) {//回调验签
                sb.append("customerid=").append(data.get("customerid")).append("&");
                sb.append("status=").append(data.get("status")).append("&");
                sb.append("sdpayno=").append(data.get("sdpayno")).append("&");
                sb.append("sdorderno=").append(data.get("sdorderno")).append("&");
                sb.append("total_fee=").append(data.get("total_fee")).append("&");
                sb.append("paytype=").append(data.get("paytype")).append("&");
            }else {//订单查询验签
                sb.append("customerid=").append(data.get("customerid")).append("&");
                sb.append("sdorderno=").append(data.get("sdorderno")).append("&");
                sb.append("reqtime=").append(data.get("reqtime")).append("&");
            }
            sb.append(md5key);
            logger.info("[BNCP]BNCP支付 加密前参数：" + sb.toString());
            return MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "[BNCP]BNCP支付 加密异常";
        }
    }
}
