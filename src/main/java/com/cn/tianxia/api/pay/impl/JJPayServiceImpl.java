package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
 * @version 1.0.0
 * @ClassName JJPayServiceImpl
 * @Description 加加支付渠道：网银扫码
 * @Date 2019/3/17 09 34
 **/
public class JJPayServiceImpl extends PayAbstractBaseService implements PayService {
    protected static final Logger logger = LoggerFactory.getLogger(JJPayServiceImpl.class);

    private String appid;//商户号
    private String md5key;//密钥
    private String payUrl;//支付地址
    private String notifyUrl;//回调地址
    private String queryOrderUrl;//订单查询地址

    private static String ret__success = "success";  //只有返回success 才算成功 其他的都不算成功
    private static String ret__failed = "fail";   //失败返回字符串
    private boolean verifySuccess = true;//回调验签默认状态为true

    private String nonce = UUID.randomUUID().toString().substring(0, 8);//随机字符
    private String time = String.valueOf(System.currentTimeMillis());

    public JJPayServiceImpl() {
    }

    public JJPayServiceImpl(Map<String, String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("appid")){
                this.appid = data.get("appid");
            }
            if(data.containsKey("md5key")){
                this.md5key = data.get("md5key");
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
        logger.info("[JJ]加加支付  扫码支付   回调开始==================================start===============================");

        this.appid = config.getString("appid");
        this.md5key = config.getString("md5key");
        this.notifyUrl = config.getString("notifyUrl");
        this.queryOrderUrl = config.getString("queryOrderUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[JJ]加加支付 商户返回信息：" + dataMap);

        String trade_no = dataMap.get("tradeNo");//第三方订单号，流水号
        String order_no = dataMap.get("outTradeNo");//支付订单号
        String amount = dataMap.get("money");//商户订单总金额，订单总金额以元为单位，精确到小数点后两位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);  //回调ip

        String trade_status = dataMap.get("state");  //第三方支付状态，2表成功
        String t_trade_status = trade_status;

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[JJ]加加支付  获取的 流水单号为空");
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[JJ]加加支付  回调订单金额为空");
            return ret__failed;
        }

        //订单查询
        try{
            Map<String, String> queryOrderMap = new HashMap<>();

            queryOrderMap.put("tradeNo", order_no);
            queryOrderMap.put("appid", appid);//APPID
            queryOrderMap.put("nonce", nonce);//随机字符串
            queryOrderMap.put("timestamp", time);//时间
            queryOrderMap.put("sign", generatorSign(queryOrderMap));
            
            logger.info("[JJ]加加支付  回调  订单查询请求参数："+queryOrderMap);
            String query = HttpUtils.get(queryOrderMap, queryOrderUrl);
            logger.info("[JJ]加加支付  回调  订单查询结果:" + query);
            if(StringUtils.isBlank(query)){
                logger.info("[JJ]加加支付  回调 订单查询失败");
                return ret__failed;
            }

            JSONObject jb = JSONObject.fromObject(query);
            if(jb.containsKey("status") && "0".equals(jb.getString("status"))){
                logger.info("[JJ]加加支付  回调查询 到的订单信息：" + jb.get("data"));
                JSONObject jsonObject = jb.getJSONObject("data");
                if("1".equals(jsonObject.getString("state"))){
                    return ret__failed;
                }
                logger.info("[JJ]加加支付  回调查询 到的订单支付状态：" + jsonObject.getString("state")  + "\n回调通知状态：" + jsonObject.get("notifyStatus") );
            }else {
                return ret__failed;
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.info("[JJ]加加支付  回调查询订单信息异常");
            return ret__failed;
        }

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
        processNotifyVO.setPayment("JJ");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[JJ]加加支付  回调验签失败");
            return ret__failed;
        }
        return processSuccessNotify(processNotifyVO, verifySuccess);

    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[JJ]加加支付  网银支付开始==================================start===============================");
        return null;
    }

    /**
     * 网银扫码支付
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[JJ]加加支付 扫码支付  开始==================================start===============================");
        try {
            Map<String,String> dataMap = sealRequest(payEntity);
            dataMap.put("sign",generatorSign(dataMap));

            String response = HttpUtils.toPostForm(dataMap,payUrl);// HTTP 请求
            if (StringUtils.isBlank(response)){
                return PayResponse.error("[JJ]加加支付  扫码支付  HTTP请求响应为空");
            }
            logger.info("[JJ]加加支付  扫码支付  HTTP请求响应："+response);
            //HTTP 请求返回参数解析
            JSONObject jb = JSONObject.fromObject(response);
            if(jb.containsKey("status") && "0".equals(jb.getString("status"))){
                //再次 data解析 取扫码链接
                JSONObject url = JSONObject.fromObject(jb.get("data"));
                logger.info("[JJ]加加支付   扫码支付  HTTP请求响应data链接："+jb.getString("data"));
                String qrcode = url.getString("qrcode");
                logger.info("[JJ]加加支付   扫码支付 是否为手机端：" + payEntity.getMobile());
                if(StringUtils.isNotBlank(payEntity.getMobile())){//手机端
                    return  PayResponse.sm_link(payEntity,qrcode,"下单成功");
                }else {//PC端
                    return PayResponse.sm_qrcode(payEntity,qrcode,"下单成功");
                }
            }
            return PayResponse.error("[JJ]加加支付  扫码支付  下单失败");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[JJ]加加支付  扫码支付  下单异常" + e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[JJ]加加支付  扫码支付   回调验签==================================start===============================");
        String sourceSign = data.remove("sign");
        String sign = generatorSign(data);
        logger.info("生成的签名：" + sign + "，服务器签名：" + sourceSign);
        if(sign.equalsIgnoreCase(sourceSign)){
            return "success";
        }
        return "fail";
    }

    public String generatorSign(Map<String, String> data){
        logger.info("[JJ]加加支付  扫码支付   生成签名==================================start===============================");
        try {
            Map<String, String> sortMap = MapUtils.sortByKeys(data);
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = sortMap.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String value =  sortMap.get(key);
                if("sign".equals(key) || StringUtils.isBlank(value)){
                    continue;
                }
                sb.append(key).append("=").append(value).append("&");
            }
            sb.append("key=").append(md5key);
            logger.info("[JJ]加加支付  生成签名前的参数：" + sb.toString());
            return MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[JJ]加加支付  签名生成异常" + e.getMessage());
            return ret__failed;
        }
    }

    public Map<String, String> sealRequest(PayEntity entity){
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(entity.getAmount());
        dataMap.put("appid", appid);//
        dataMap.put("amount", amount);//
        dataMap.put("outTradeNo", entity.getOrderNo());//594838696828583商户订单号
        dataMap.put("callbackUrl", notifyUrl);//http://127.0.0.0回调地址
        dataMap.put("customParam","TOP-UP");//附言
        dataMap.put("chanel", entity.getPayCode());//BANK_TO_BANK通道(BANK_TO_BANK卡转卡ALIPAY_TO_BANK支付宝转卡)
        dataMap.put("nonce", nonce);//mQr0uggm2GrRpg5bQgSAkstio随机字符串
        dataMap.put("timestamp", time);//1552666789563 13位时间戳
        logger.info("[JJ]加加支付  组装参数：" + dataMap);
        return dataMap;
    }

}
