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
 * @version 1.2.0
 * @ClassName DCZFPayServiceImpl
 * @Description 东川支付  渠道：微信扫码、微信H5、支付宝扫码、支付宝H5
 * @Date 2019/4/8 10 32
 **/
public class DCZFPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(DCZFPayServiceImpl.class);

    private String memberid;//商户id
    private String payUrl;//支付地址
    private String notifyUrl;//回调通知地址
    private String queryOrderUrl;//订单查询地址
    private String md5key;//密钥

    private static String ret__success = "success";  //成功返回字符串
    private static String ret__failed = "fail";   //失败返回字符串
    private boolean verifySuccess = true;//回调验签默认状态为true

    public DCZFPayServiceImpl() {
    }

    public DCZFPayServiceImpl(Map<String, String> data) {
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
        logger.info("[DCZF]东川支付    商户返回信息：" + JSONObject.fromObject(dataMap));

        String trade_no = dataMap.get("fxorder");//第三方订单号，流水号
        String order_no = dataMap.get("fxddh");//支付订单号
        String amount = dataMap.get("fxfee");//实际支付金额,以分为单位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[DCZF]东川支付       获取的{} 流水单号为空",trade_no);
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[DCZF]东川支付      回调订单金额为空");
            return ret__failed;
        }

        String trade_status = dataMap.get("fxstatus");  //第三方支付状态，1 支付成功
        String t_trade_status = "1";//第三方成功状态

        //订单查询
        try{
            Map<String,String> queryMap = new HashMap<>();
            queryMap.put("fxid",  memberid);//商户号
            queryMap.put("fxddh",  order_no);//商户订单号
            queryMap.put("fxaction",  "orderquery");//请求IP
            queryMap.put("fxsign",  generatorSign(queryMap, 3));

            String queryData = HttpUtils.toPostForm(JSONObject.fromObject(queryMap), queryOrderUrl);

            logger.info("[DCZF]东川支付   订单查询数据：" + JSONObject.fromObject(queryData));
            if(StringUtils.isBlank(queryData)){
                return ret__failed;
            }

            JSONObject jb = JSONObject.fromObject(queryData);
            if(!"1".equals(jb.getString("fxstatus"))){
                logger.info("[DCZF]东川支付   订单查询数据： 支付异常" + jb.get("fxstatus"));
                return ret__failed;
            }

        }catch (Exception e){
            e.printStackTrace();
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
        processNotifyVO.setPayment("DCZF");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[DCZF]东川支付    回调验签失败");
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
        logger.info("[DCZF]东川支付   扫码支付开始=================start===========================");
        try{
            Map<String, String> dataMap = sealRequest(payEntity);
            dataMap.put("fxsmstyle","1");//扫码模式,用于扫码模式（sm），仅带sm接口可用，默认0返回扫码图片，为1则返回扫码跳转地址。
            String responseData = HttpUtils.toPostForm(dataMap, payUrl);

           logger.info("[DCZF]东川支付 HTTP 请求返回参数：" + JSONObject.fromObject(responseData));
            if(StringUtils.isBlank(responseData)){
                return  PayResponse.error("http请求返回为空");
            }
            JSONObject jb = JSONObject.fromObject(responseData);
            if(jb.containsKey("status") && "1".equals(jb.getString("status"))){
                logger.info("=============" + jb.getString("payurl"));
                return  PayResponse.sm_link(payEntity, jb.getString("payurl"),"下单成功");
            }
            return  PayResponse.error("下单失败");
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
            String sourceSign = data.remove("fxsign");
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
        String amount = new DecimalFormat("0.00").format(payEntity.getAmount());

        dataMap.put("fxid", memberid);//商务号,唯一号，由东川支付提供
        dataMap.put("fxddh", payEntity.getOrderNo());//商户订单号,仅允许字母或数字类型,不超过22个字符，不要有中文
        dataMap.put("fxdesc", "TOP-UP");//商品名称,utf-8编码
        dataMap.put("fxfee", amount);//支付金额,请求的价格(单位：元) 可以0.01元
        dataMap.put("fxnotifyurl", notifyUrl);//异步通知地址,异步接收支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        dataMap.put("fxbackurl", payEntity.getRefererUrl());//同步通知地址,支付成功后跳转到的地址，不参与签名。
        dataMap.put("fxpay", payEntity.getPayCode());//请求类型 【支付宝wap：zfbwap】【支付宝扫码：zfbsm】【网银：bank】【支付宝跳转扫码：zfbewm】【微信跳转扫码：wxewm】【支付宝H5：zfbh5】【云闪付扫码：ysfsm】【支付宝红包	：zfbhb】,请求支付的接口类型。
        dataMap.put("fxattch", "recharge");//附加信息,原样返回，utf-8编码
       // dataMap.put("fxnotifystyle", "1");//异步数据类型,异步返回数据的类型，默认1 返回数据为表单数据（Content-Type: multipart/form-data），2 返回post json数据。
       // dataMap.put("fxsmstyle","0");//扫码模式,用于扫码模式（sm），仅带sm接口可用，默认0返回扫码图片，为1则返回扫码跳转地址。
       // dataMap.put("fxbankcode","");//银行类型,用于网银直连模式，请求的银行编号，参考银行附录,仅网银接口可用。
       // dataMap.put("fxfs","");//反扫付款码数字,用于用户被扫，用户的付款码数字,仅反扫接口可用。
        //dataMap.put("fxuserid","");//快捷模式绑定商户id,用于识别用户绑卡信息，仅快捷接口可用。
        dataMap.put("fxsign", generatorSign(dataMap, 1));//签名【md5(商务号+商户订单号+支付金额+异步通知地址+商户秘钥)】,通过签名算法计算得出的签名值。
        dataMap.put("fxip", payEntity.getIp());//支付用户IP地址,用户支付时设备的IP地址

        logger.info("[DCZF]东川支付  HTTP请求参数：" + JSONObject.fromObject(dataMap));

        return dataMap;
    }

    /**
     * 生成签名
     * @param data
     * @return
     */
    private String generatorSign(Map<String, String> data, int type ) throws Exception{
        StringBuffer sb = new StringBuffer();
        if(1 == type){//支付
            //签名【md5(商务号+商户订单号+支付金额+异步通知地址+商户秘钥)】
            sb.append(data.get("fxid")).append(data.get("fxddh")).append(data.get("fxfee"))
                    .append(data.get("fxnotifyurl")).append(md5key);
        }else if(2 == type){//回调
            //签名【md5(订单状态+商务号+商户订单号+支付金额+商户秘钥)】
            sb.append(data.get("fxstatus")).append(data.get("fxid")).append(data.get("fxddh"))
                    .append(data.get("fxfee")).append(md5key);
        }else {//查询
            //签名【md5(商务号+商户订单号+商户查询动作+商户秘钥)】
            sb.append(data.get("fxid")).append(data.get("fxddh"))
                    .append(data.get("fxaction")).append(md5key);
        }

        logger.info("[DCZF]东川支付  签名前的参数格式：" + sb.toString());

        return MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
    }
}
