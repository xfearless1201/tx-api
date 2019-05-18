package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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
 * @ClassName HFTPayServiceImpl
 * @Description 汇付2渠道 支付宝PC H5
 * @Date 2019/4/18 18 24
 **/
public class HFTPayServiceImpl extends PayAbstractBaseService implements PayService {

    private static final Logger logger = LoggerFactory.getLogger(HFTPayServiceImpl.class);

    private String merchId;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String secret;//密钥

    private static String ret__success = "{\"code\":1,\"msg\":\"success\"}";  //成功返回字符串
    private static String ret__failed = "{\"code\":0,\"msg\":\"fail\"}";   //失败返回字符串
    private boolean verifySuccess = true;//回调验签默认状态为true

    public HFTPayServiceImpl() {
    }

    public HFTPayServiceImpl(Map<String, String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(MapUtils.isNotEmpty(data)){
                if(data.containsKey("merchId")){
                    this.merchId = data.get("merchId");
                }
                if(data.containsKey("payUrl")){
                    this.payUrl = data.get("payUrl");
                }
                if(data.containsKey("notifyUrl")){
                    this.notifyUrl = data.get("notifyUrl");
                }
                if(data.containsKey("secret")){
                    this.secret = data.get("secret");
                }
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
        this.merchId = config.getString("merchId");
        this.notifyUrl = config.getString("notifyUrl");
        this.secret = config.getString("secret");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[HFT]汇付2     商户返回信息：" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("tradeCode");//第三方订单号，流水号
        String order_no = dataMap.get("orderCode");//支付订单号
        String amount = dataMap.get("price");//实际支付金额,以分为单位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[HFT]汇付2      获取的{} 流水单号为空",trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[HFT]汇付2     回调订单金额为空");
            return ret__failed;
        }

        String trade_status = dataMap.get("status");  //第三方支付状态，1 支付成功
        String t_trade_status = "PAID";//第三方成功状态

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
        processNotifyVO.setPayment("HFT");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[HFT]汇付2      回调验签失败");
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
        try{
            Map<String, String> dataMap = sealRequest(payEntity);
            String responseData = HttpUtils.toPostForm(dataMap, payUrl);

            if(StringUtils.isBlank(responseData)){
                return PayResponse.error("扫码支付响应为 空");
            }

            logger.info("[HFT]汇付2  扫码支付响应信息："+ responseData);

            JSONObject jb = JSONObject.fromObject(responseData);
            if(jb.containsKey("code") && "1".equals(jb.getString("code"))){

                String content= jb.getString("content");

                JSONObject url = JSONObject.fromObject(content);
                String qrcode = url.getString("qrcode");

                logger.info("[HFT]汇付2  支付页面地址："+ url.getString("qrcode"));

                return PayResponse.sm_link(payEntity, qrcode, "下单成功");
            }
            return PayResponse.error("扫码支付失败");
        }catch (Exception e){
            e.getStackTrace();
            return PayResponse.error("扫码支付异常" + e.getMessage());
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

        if(sign.equals(sourceSign)){
            return "success";
        }
        return "fail";
    }

    /**
     * 组装参数
     * @param payEntity
     * @return
     */
    private Map<String, String> sealRequest(PayEntity payEntity){
        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());

        dataMap.put("appKey", merchId);//商户标识
        dataMap.put("orderCode", payEntity.getOrderNo());//订单号
        dataMap.put("productName",  "TOP-UP");//商品名称
        dataMap.put("price", amount);//价格，单位元(数值型保留两位小数、1.11)
        dataMap.put("callback", notifyUrl);//回调地址()
        dataMap.put("payType", payEntity.getPayCode());//支付类型
        dataMap.put("sign", generatorSign(dataMap));//签名，规则

        logger.info("[HFT]汇付2  扫码支付请求参数：" + JSONObject.fromObject(dataMap));
        return dataMap;
    }

    /**
     * 生成签名
     * @param data
     * @return
     */
    private String generatorSign(Map<String, String> data){
        //1. 将所有请求的参数，按参数名字母进行正序排序，然后参数与值按照(key=value)方式进行拼接(参数sign除外、value为空除外)，不同参数之间以&进行拼接，
        //2. 由1中获取得到的串，再开头加上服务端下发的密钥(secret)，
        // 3. 由2中得到的串，利用MD5签名函数对字符串（UTF-8）进行签名运算，得到32位签名，然后转为大写，

        try{
            Map<String, String> treeMap = new TreeMap<>(data);
            StringBuffer sb = new StringBuffer();
            sb.append(secret);
            Iterator<String> iterator = treeMap.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String val = treeMap.get(key);
                if(StringUtils.isBlank(val) || "null".equalsIgnoreCase(val) || "sign".equalsIgnoreCase(key)){
                    continue;
                }
                sb.append(key).append("=").append(val).append("&");
            }
            sb.replace(sb.length()-1,sb.length(),"");

            logger.info("[HFT]汇付2 支付生成待签名串：" + sb.toString());
            return MD5Utils.md5toUpCase_32Bit(sb.toString());
        }catch (Exception e){
            e.getStackTrace();
            return "签名生成异常"+ e.getMessage();
        }
    }
}
