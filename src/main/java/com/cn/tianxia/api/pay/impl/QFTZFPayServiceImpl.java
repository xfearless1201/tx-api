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
 * @ClassName: QFTZFPayServiceImpl
 * @Description: 全付通支付
 * @Author: Zed
 * @Date: 2019-01-03 16:45
 * @Version:1.0.0
 **/

public class QFTZFPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(QFTZFPayServiceImpl.class);

    /** 支付地址 **/
    private String api_url = "http://api.quanfutong.top/apiv32.aspx";

    /** 商户号 **/
    private String merchantNo = "21053";

    /** md5key **/
    private String md5Key = "746e34cf8e2d41c7";

    /** notifyUrl **/
    private String notifyUrl = "http://txw.tx8899.com/TWY/Notify/QFTZFNotify.do";

    public QFTZFPayServiceImpl(Map<String, String> pmap) {
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
        try {
            Map<String, String> paramsMap = sealRequest(payEntity);
            String sign = generatorSign(paramsMap);
            paramsMap.put("hmac", sign);
            logger.info("[QFTZF]全付通支付请求参数:" + JSONObject.fromObject(paramsMap).toString());
            String form = HttpUtils.generatorForm(paramsMap,api_url);
            return PayResponse.wy_form(payEntity.getPayUrl(),form);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[QFTZF]全付通支付扫码支付下单失败:{}",e.getMessage());
            return PayResponse.error("[QFTZF]全付通支付扫码支付下单失败:"+e.getMessage());
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            Map<String, String> paramsMap = sealRequest(payEntity);
            String sign = generatorSign(paramsMap);
            paramsMap.put("hmac", sign);
            logger.info("[QFTZF]全付通支付请求参数:" + JSONObject.fromObject(paramsMap).toString());
            String form = HttpUtils.generatorForm(paramsMap,api_url);
            return PayResponse.sm_form(payEntity,form,"下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[QFTZF]全付通支付扫码支付下单失败:{}",e.getMessage());
            return PayResponse.error("[QFTZF]全付通支付扫码支付下单失败:"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("hmac");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[QFTZF]全付通支付回调验签失败：回调签名为空！");
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
        logger.info("[QFTZF]全付通支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(payEntity.getAmount());//交易金额

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
            logger.error("[QFTZF]全付通支付封装请求参数异常:",e.getMessage());
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
        logger.info("[QFTZF]全付通支付生成支付签名串开始==================START========================");
        try {
//            Hmac 签名顺序：p0_Cmd+分隔符 +p1_MerId+分隔符 +p2_Order+分隔符 +p3_Amt+分隔符 +p4_Cur+分隔符 +p5_Pid+分隔符 +p6_Pcat+分隔符 +p7_Pdesc+分隔符 +p8_Url+分隔符 +p9_SAF+分隔符 +pa_MP+分隔符 +pd_FrpId+分隔符 +pr_NeedResponse+分隔符；
//            然后把得到的字符串与密钥用HMAC算法签名，结果给hmac 参数即可。
//            无论参数值是否为空，分隔符号都不能省略，分割符号为^|^(半角英文状态下，一个^,一个|,一个^)



            StringBuilder sb = new StringBuilder();
            sb.append(data.get("p0_Cmd")).append("^|^");
            // 商户编号
            sb.append(data.get("p1_MerId")).append("^|^");
            // 商户订单号
            sb.append(data.get("p2_Order")).append("^|^");
            // 支付金额
            sb.append(data.get("p3_Amt")).append("^|^");
            // 交易币种
            sb.append(data.get("p4_Cur")).append("^|^");
            // 商品名称
            sb.append(data.get("p5_Pid")).append("^|^");
            // 商品种类
            sb.append(data.get("p6_Pcat")).append("^|^");
            // 商品描述
            sb.append(data.get("p7_Pdesc")).append("^|^");
            // 商户接收支付成功数据的地址
            sb.append(data.get("p8_Url")).append("^|^");
            // 送货地址
            sb.append(data.get("p9_SAF")).append("^|^");
            // 商户扩展信息
            sb.append(data.get("pa_MP")).append("^|^");
            // 银行编码
            sb.append(data.get("pd_FrpId")).append("^|^");
            // 应答机制
            sb.append(data.get("pr_NeedResponse")).append("^|^");
            logger.info("[QFTZF]全付通支付待签名字符:" + sb.toString());
            String sign = XTUtils.hmacSign(sb.toString(),md5Key);
            if (StringUtils.isBlank(sign)) {
                logger.error("[QFTZF]全付通支付生成签名串为空！");
                return null;
            }
            logger.info("[QFTZF]全付通支付生成加密签名串:"+ sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[QFTZF]全付通支付生成支付签名串异常:"+ e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }

    private boolean verifyCallback(String hmac,Map<String,String> data) {
        StringBuffer sValue = new StringBuffer();
        // 商户编号
        sValue.append(data.get("p1_MerId")).append("^|^");
        // 业务类型
        sValue.append(data.get("r0_Cmd")).append("^|^");
        // 支付结果
        sValue.append(data.get("r1_Code")).append("^|^");
        // 易宝支付交易流水号
        sValue.append(data.get("r2_TrxId")).append("^|^");
        // 支付金额
        sValue.append(data.get("r3_Amt")).append("^|^");
        // 交易币种
        sValue.append(data.get("r4_Cur")).append("^|^");
        // 商品名称
        sValue.append(data.get("r5_Pid")).append("^|^");
        // 商户订单号
        sValue.append(data.get("r6_Order")).append("^|^");
        // 易宝支付会员ID
        sValue.append(data.get("r7_Uid")).append("^|^");
        // 商户扩展信息
        sValue.append(data.get("r8_MP")).append("^|^");
        // 交易结果返回类型
        sValue.append(data.get("r9_BType")).append("^|^");
        String sNewString;
        sNewString = XTUtils.hmacSign(sValue.toString(),md5Key);

        if (hmac.equals(sNewString)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        PayEntity entity = new PayEntity();
        entity.setPayCode("alipay");
        entity.setAmount(100);
        entity.setOrderNo("QFTZFbl1123456");
        QFTZFPayServiceImpl service = new QFTZFPayServiceImpl(null);
        service.smPay(entity);
    }
}
