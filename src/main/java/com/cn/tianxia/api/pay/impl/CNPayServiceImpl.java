package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

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
 * @version 1.2.0
 * @ClassName CNPayServiceImpl
 * @Description 菜鸟支付渠道: 支付宝
 * @Date 2019/4/5 13 42
 **/
public class CNPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(CNPayServiceImpl.class);

    private String memberid;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String queryOrderUrl;//订单查询地址
    private String md5key;//密钥

    private static String ret__success = "SUCCESS";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private boolean verifySuccess = true;//回调验签默认状态为true

    private  String nonce = UUID.randomUUID().toString().substring(0, 9);//随机字符

    public CNPayServiceImpl() {
    }

    public CNPayServiceImpl(Map<String, String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("memberid")){
                this.memberid = data.get("memberid");
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
        }
    }

    /**
     * 回调
     * @param request
     * @param response
     * @param config
     * @return
     */
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        this.memberid = config.getString("memberid");
        this.notifyUrl = config.getString("notifyUrl");
        this.md5key = config.getString("md5key");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[CN]菜鸟支付    商户返回信息：" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("trade_no");//第三方订单号，流水号
        String order_no = dataMap.get("order_no");//支付订单号
        String amount = dataMap.get("pay_amount");//实际支付金额,以分为单位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[CN]菜鸟支付       获取的{} 流水单号为空", trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[CN]菜鸟支付     回调订单金额为空");
            return ret__failed;
        }


        //订单查询
        try{
            Map<String,String> queryMap = new HashMap<>();
            queryMap.put("merchant_code",  memberid);//商户号
            queryMap.put("order_no",  order_no);//商户订单号
            queryMap.put("customer_ip",  ip);//请求IP
            queryMap.put("random_char",  nonce);//随机串
            queryMap.put("sign",  generatorSign(queryMap, 1));

            String queryData = HttpUtils.toPostJsonStr(JSONObject.fromObject(queryMap), queryOrderUrl);

            logger.info("[CN] 菜鸟支付  订单查询数据：" + JSONObject.fromObject(queryData));
            if(StringUtils.isBlank(queryData)){
                return ret__failed;
            }

            JSONObject jb = JSONObject.fromObject(queryData);
            if(!"00".equals(jb.getString("flag"))){
                return ret__failed;
            }

            logger.info("[CN]菜鸟支付 订单支付状态为：" + jb.getString("trade_status") + "，交易成功");
            if(!"1".equals(jb.getString("trade_status"))){//1的意思是 已经付款，但是有可能回调失败了或者正在回调中
                if(!"3".equals(jb.getString("trade_status"))){
                    return ret__failed;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            return ret__failed;
        }

        String trade_status = dataMap.get("trade_status");  //第三方支付状态，1 支付成功
        String t_trade_status = trade_status;//第三方成功状态

        //写入数据库
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setRealAmount(Double.parseDouble(amount)/100);//以分为单位
        processNotifyVO.setIp(ip);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setInfoMap(JSONObject.fromObject(dataMap).toString());
        processNotifyVO.setT_trade_status(t_trade_status);
        processNotifyVO.setConfig(config);
        processNotifyVO.setPayment("CN");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[CN]菜鸟支付   回调验签失败");
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
        logger.info("[CN]菜鸟支付    扫码支付开始=================start===========================");
        try{
            Map<String, String> dataMap = sealRequest(payEntity);
            dataMap.put("user_mark", payEntity.getuId());//会员唯一标识符
            String responseData = HttpUtils.toPostJsonStr(JSONObject.fromObject(dataMap), payUrl);

            logger.info("[CN]菜鸟支付 扫码支付响应信息：" + JSONObject.fromObject(responseData));
            if(StringUtils.isBlank(responseData)){
                return  PayResponse.error("扫码支付响应信息为空");
            }
            //解析
            JSONObject jb = JSONObject.fromObject(responseData);
            if(jb.containsKey("flag") && "00".equals(jb.getString("flag"))){
                return PayResponse.sm_link(payEntity, jb.getString("qrCodeUrl"),"下单成功");
            }

            return  PayResponse.error("扫码支付失败");
        }catch (Exception e){
            e.printStackTrace();
            return PayResponse.error("扫码支付异常:  " + e.getMessage());
        }
    }

    /**
     * 回调验签
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {
        try{
            String sign = generatorSign(data, 2);
            String sourceSign = data.remove("sign");
            if(sign.equalsIgnoreCase(sourceSign)){
                return ret__success;
            }
            return ret__failed;
        }catch (Exception e){
            e.getMessage();
            return ret__failed;
        }
    }

    private Map<String, String> sealRequest(PayEntity payEntity) throws Exception{
        Map<String, String> dataMap = new HashMap<>();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String amount = new DecimalFormat("##").format(payEntity.getAmount()*100);//以分为单位

        dataMap.put("notify_url", notifyUrl);//服务器异步通知地址
        dataMap.put("pay_type", payEntity.getPayCode());//支付方式
        dataMap.put("merchant_code", memberid);//商户号
        dataMap.put("order_no", payEntity.getOrderNo());//商户订单号
        dataMap.put("order_amount", amount);//订单金额
        dataMap.put("order_time", time);//下单时间
        //  dataMap.put("req_referer", payEntity.getRefererUrl());//来路域名
        dataMap.put("customer_ip", payEntity.getIp());//用户IP
        dataMap.put("return_params", "TOP-UP");//回传参数
        dataMap.put("random_char", nonce);//随机串
        dataMap.put("sign", generatorSign(dataMap, 1));//签名

        logger.info("[CN]菜鸟支付  扫码支付请求参数：" + JSONObject.fromObject(dataMap));

        return dataMap;
    }

    /**
     * 生成签名
     * @param data
     * @return
     */
    private String generatorSign(Map<String, String> data, int type ) throws Exception{
        Map<String, String> dataMap = new TreeMap<>(data);

        StringBuffer sb = new StringBuffer();
        if (1 == type) {
            Iterator<String> iterator = dataMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = dataMap.get(key);
                sb.append(key).append("=").append(val).append("&");
            }
        }else if(2 == type){
            //回调
            String amount =  data.get("pay_amount").replace(".00","");
            sb.append("merchant_code=").append(data.get("merchant_code")).append("&");
            sb.append("notify_type=").append(data.get("notify_type")).append("&");
            sb.append("order_amount=").append(data.get("order_amount")).append("&");
            sb.append("order_no=").append(data.get("order_no")).append("&");
            sb.append("pay_amount=").append(amount).append("&");
            sb.append("return_params=").append(data.get("return_params")).append("&");
            sb.append("trade_no=").append(data.get("trade_no")).append("&");
            sb.append("trade_status=").append(data.get("trade_status")).append("&");
            sb.append("trade_time=").append(data.get("trade_time")).append("&");
        }
        sb.append("key=").append(md5key);
        logger.info("[CN]菜鸟支付 生成待签名串：" + sb.toString());

        return MD5Utils.md5toUpCase_32Bit(sb.toString());
    }
}
