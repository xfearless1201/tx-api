package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.XTUtils;

import net.sf.json.JSONObject;

/**
 * @ClassName: LBAOPayServiceImpl
 * @Description: 龙宝支付
 * @Author: Zed
 * @Date: 2018-12-28 14:36
 * @Version:1.0.0
 **/

public class LBAOPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(LBAOPayServiceImpl.class);

    /** 支付地址 **/
    private String api_url = "http://www.lbopay.com/GateWay/ReceiveBank.aspx";

    /** 商户号 **/
    private String merchantNo = "1693";

    /** md5key **/
    private String md5Key = "gprxfpnBCsQFSjcnASXmfu3ulu2pRaCa";

    /** notifyUrl **/
    private String notifyUrl = "http://txw8899.com/TXY/Notify/LBAONotify.do";

    public LBAOPayServiceImpl(Map<String, String> pmap) {
        if (pmap != null) {
            if (pmap.containsKey("api_url")) {
                this.api_url = pmap.get("api_url");
            }
            if (pmap.containsKey("merchantNo")) {
                this.merchantNo = pmap.get("merchantNo");
            }
            if (pmap.containsKey("md5Key")) {
                this.md5Key = pmap.get("md5Key");
            }
            if (pmap.containsKey("notifyUrl")) {
                this.notifyUrl = pmap.get("notifyUrl");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            Map<String, String> paramsMap = sealRequest(payEntity);
            String sign = generatorSign(paramsMap);
            paramsMap.put("hmac", sign);
            logger.info("[LBAO]龙宝支付请求参数:" + JSONObject.fromObject(paramsMap).toString());
            String form = HttpUtils.generatorForm(paramsMap,api_url);
            return PayResponse.sm_form(payEntity,form,"下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[LBAO]龙宝支付扫码支付下单失败:{}",e.getMessage());
            return PayResponse.error("[LBAO]龙宝支付扫码支付下单失败:"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("hmac");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[LBAO]龙宝支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data))
            return "success";
        return "fail";
    }

    /**
     *
     * @param payEntity
     * @return
     */
    private Map<String,String> sealRequest(PayEntity payEntity) throws Exception {
        logger.info("[LBAO]龙宝支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(payEntity.getAmount());//交易金额 分为单位

            data.put("p0_Cmd","Buy");//	业务类型	是	Max(20)	固定值 “Buy”
            data.put("p1_MerId",merchantNo);//	商户编号
            data.put("p2_Order",payEntity.getOrderNo());//	商户订单号	是	Max(50)	若不为“”，提交的订单号必须在自身账户交易中唯一；
            data.put("p3_Amt",amount);//	支付金额	是	Max(20)	单位：元，精确到分，保留小数点后两位
            data.put("p4_Cur","CNY");//	交易币种	是	Max(10)	固定值 “CNY”
            data.put("p5_Pid","top_up");//	商品名称	否	Max(20)	用于支付时显示在[API支付平台]网关左侧的订单产品信息；此参数如用到中文，请注意转码.
            data.put("p6_Pcat","");//	商品种类	否	Max(20)	商品种类； 此参数如用到中文，请注意转码
            data.put("p7_Pdesc","");//	商品描述	否	Max(20)	商品描述； 此参数如用到中文，请注意转码
            data.put("p8_Url",notifyUrl);//	商户接收支付成功数据的地址	是	Max(200)	支付成功后[API支付平台]会向该地址发送两次成功通知，该地址不可以带参数
            data.put("p9_SAF","0");//	送货地址	是	Max(1)	为“1”：需要用户将送货地址留在[API支付平台]系统；
            data.put("pa_MP","");//	商户扩展信息	否	Max(200)	返回时原样返回；
            data.put("pd_FrpId",payEntity.getPayCode());//支付通道编码
            data.put("pr_NeedResponse","1");//应答机制 是	Max(1)	固定值为“1”：需要应答机制；

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[LBAO]龙宝支付封装请求参数异常:",e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     *
     * @Description 生成支付签名串
     * @param data
     * @return
     * @throws Exception
     */
    public String generatorSign(Map<String,String> data) throws Exception{
        logger.info("[LBAO]龙宝支付生成支付签名串开始==================START========================");
        try {

            StringBuilder sb = new StringBuilder();
            sb.append(data.get("p0_Cmd"));
            // 商户编号
            sb.append(data.get("p1_MerId"));
            // 商户订单号
            sb.append(data.get("p2_Order"));
            // 支付金额
            sb.append(data.get("p3_Amt"));
            // 交易币种
            sb.append(data.get("p4_Cur"));
            // 商品名称
            sb.append(data.get("p5_Pid"));
            // 商品种类
            sb.append(data.get("p6_Pcat"));
            // 商品描述
            sb.append(data.get("p7_Pdesc"));
            // 商户接收支付成功数据的地址
            sb.append(data.get("p8_Url"));
            // 送货地址
            sb.append(data.get("p9_SAF"));
            // 商户扩展信息
            sb.append(data.get("pa_MP"));
            // 银行编码
            sb.append(data.get("pd_FrpId"));
            // 应答机制
            sb.append(data.get("pr_NeedResponse"));
            logger.info("[LBAO]龙宝支付待签名字符:" + sb.toString());
            String sign = XTUtils.hmacSign(sb.toString(),md5Key);
            if (StringUtils.isBlank(sign)) {
                logger.error("[LBAO]龙宝支付生成签名串为空！");
                return null;
            }
            logger.info("[LBAO]龙宝支付生成加密签名串:"+ sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[LBAO]龙宝支付生成支付签名串异常:"+ e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }

    private boolean verifyCallback(String hmac,Map<String,String> data) {
        StringBuffer sValue = new StringBuffer();
        // 商户编号
        sValue.append(data.get("p1_MerId")==null?"":data.get("p1_MerId"));
        // 业务类型
        sValue.append(data.get("r0_Cmd")==null?"":data.get("r0_Cmd"));
        // 支付结果
        sValue.append(data.get("r1_Code")==null?"":data.get("r1_Code"));
        // 易宝支付交易流水号
        sValue.append(data.get("r2_TrxId")==null?"":data.get("r2_TrxId"));
        // 支付金额
        sValue.append(data.get("r3_Amt")==null?"":data.get("r3_Amt"));
        // 交易币种
        sValue.append(data.get("r4_Cur")==null?"":data.get("r4_Cur"));
        // 商品名称
        sValue.append(data.get("r5_Pid")==null?"":data.get("r5_Pid"));
        // 商户订单号
        sValue.append(data.get("r6_Order")==null?"":data.get("r6_Order"));
        // 易宝支付会员ID
        sValue.append(data.get("r7_Uid")==null?"":data.get("r7_Uid"));
        // 商户扩展信息
        sValue.append(data.get("r8_MP")==null?"":data.get("r8_MP"));
        // 交易结果返回类型
        sValue.append(data.get("r9_BType")==null?"":data.get("r9_BType"));
        String sNewString;
        sNewString = XTUtils.hmacSign(sValue.toString(),md5Key);

        if (hmac.equals(sNewString)) {
            return true;
        }
        return false;
    }
}
