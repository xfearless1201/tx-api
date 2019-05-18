/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    YUHPayServiceImpl.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:     Administrator 
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年02月08日 15:55 
 *
 *    Revision: 
 *
 *    2019/2/8 15:55 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.XTUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 *  * @ClassName YUHPayServiceImpl
 *  * @Description TODO(宇恒支付)
 *  * @Author Roman
 *  * @Date 2019年02月08日 15:55
 *  * @Version 1.0.0
 *  
 **/
public class YUHPayServiceImpl implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(YUHPayServiceImpl.class);

    /**
     * 商户号
     */
    private String p1_MerId;

    /**
     * 支付请求地址
     */

    private String payUrl;

    /**
     * 回调地址
     */
    private String notifyUrl;


    /**
     * 秘钥
     */
    private String secret;


    /**
     * 构造器，初始化参数
     */
    public YUHPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("p1_MerId")) {
                this.p1_MerId = data.get("p1_MerId");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("secret")) {
                this.secret = data.get("secret");
            }
        }
    }


    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[YUH]宇恒支付网银渠道  wyPay(PayEntity payEntity = {}",payEntity);
        try {
            Map<String,String> data = sealRequest(payEntity);
            String sign = generatorSign(data);
            data.put("hmac", sign);
            logger.info("[YUH]宇恒支付网银渠道请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = HttpUtils.generatorForm(data, payUrl);
            logger.info("[YUH]宇恒支付网银渠道支付生成form表单请求结果:{}",response);
            return PayResponse.wy_form(payEntity.getPayUrl(), response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YUH]宇恒支付网银渠道异常:{}",e.getMessage());
            return PayResponse.error("[YUH]宇恒支付网银渠道支付异常");
        }
    }


    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[YUH]宇恒支付扫码支付开始================START============");
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);

            //生成签名串
            String sign = generatorSign(data);
            data.put("hmac", sign);

            logger.info("[YUH]宇恒支付扫码支付请求参数报文:{}",data);
            logger.info("请求地址：" + payUrl);
            //发起HTTP请求
            String response = HttpUtils.generatorForm(data, payUrl);

            if (StringUtils.isBlank(response) || response.contains("FAIL")) {
                logger.info("[YUH]宇恒支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[YUH]宇恒支付扫码支付发起HTTP请求无响应结果");
            }
            logger.info("[YUH]宇恒支付扫码支付发起HTTP请求响应结果:{}", response);

            return PayResponse.sm_form(payEntity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YUH]宇恒支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[YUH]宇恒支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[YUH]宇恒支付回调验签开始==============START===========");
        String sourceSign = data.remove("hmac");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[YUH]宇恒支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data)) {
            return "success";
        }
        return "fail";
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[YUH]宇恒支付组装支付请求参数开始==============START==================");
        try {
            //创建参数存储对象
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            String orderNo = entity.getOrderNo();


            data.put("p0_Cmd","Buy");//	业务类型	是	Max(20)	固定值 “Buy”
            data.put("p1_MerId",p1_MerId);//	商户编号
            data.put("p2_Order",orderNo);//	商户订单号	是	Max(50)	若不为“”，提交的订单号必须在自身账户交易中唯一；
            data.put("p3_Amt",amount);//	支付金额	是	Max(20)	单位：元，精确到分，保留小数点后两位
            data.put("p4_Cur","CNY");//	交易币种	是	Max(10)	固定值 “CNY”
            data.put("p5_Pid","top_up");//	商品名称	否	Max(20)	用于支付时显示在[API支付平台]网关左侧的订单产品信息；此参数如用到中文，请注意转码.
            data.put("p6_Pcat","");//	商品种类	否	Max(20)	商品种类； 此参数如用到中文，请注意转码
            data.put("p7_Pdesc","");//	商品描述	否	Max(20)	商品描述； 此参数如用到中文，请注意转码
            data.put("p8_Url",notifyUrl);//	商户接收支付成功数据的地址	是	Max(200)	支付成功后[API支付平台]会向该地址发送两次成功通知，该地址不可以带参数
            data.put("p9_SAF","0");//	送货地址	是	Max(1)	为“1”：需要用户将送货地址留在[API支付平台]系统；
            data.put("pa_MP","");//	商户扩展信息	否	Max(200)	返回时原样返回；
            data.put("pd_FrpId",entity.getPayCode());//支付通道编码
            data.put("pr_NeedResponse","1");//应答机制 是	Max(1)	固定值为“1”：需要应答机制；

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YUH]宇恒支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[YUH]宇恒支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    public String generatorSign(Map<String, String> data) throws Exception {
        logger.info("[YUH]宇恒支付生成支付签名串开始==================START========================");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(data.get("p0_Cmd"));
            sb.append(data.get("p1_MerId"));
            sb.append(data.get("p2_Order"));
            sb.append(data.get("p3_Amt"));
            sb.append(data.get("p4_Cur"));
            sb.append(data.get("p5_Pid"));
            sb.append(data.get("p6_Pcat"));
            sb.append(data.get("p7_Pdesc"));
            sb.append(data.get("p8_Url"));
            sb.append(data.get("p9_SAF"));
            sb.append(data.get("pa_MP"));
            sb.append(data.get("pd_FrpId"));
            sb.append(data.get("pr_NeedResponse"));
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[YUH]宇恒支付生成待签名串:{}", signStr);
            String sign = XTUtils.hmacSign(sb.toString(), secret);
            if (StringUtils.isBlank(sign)) {
                logger.error("[YUH]宇恒支付生成签名串为空！");
                return null;
            }
            logger.info("[YUH]宇恒支付生成加密签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[YUH]宇恒支付生成支付签名串异常:" + e.getMessage());
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
        sNewString = XTUtils.hmacSign(sValue.toString(),secret);

        if (hmac.equals(sNewString)) {
            return true;
        }
        return false;
    }


}

