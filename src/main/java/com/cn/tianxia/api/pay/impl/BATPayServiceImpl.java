package com.cn.tianxia.api.pay.impl;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

/**
 * @ClassName: BATPayServiceImpl
 * @Description: 蝙蝠侠支付
 * @Author: Zed
 * @Date: 2019-01-09 09:49
 * @Version:1.0.0
 **/

public class BATPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(BATPayServiceImpl.class);

    private String memberid;
    private String key;
    private String payUrl;
    private String notifyUrl;
    private String zftd;
    
    public BATPayServiceImpl(Map<String,String> map) {
        if(MapUtils.isNotEmpty(map)){
            if(map.containsKey("memberid")){
                this.memberid = map.get("memberid");
            }
            if(map.containsKey("notifyUrl")){
                this.notifyUrl = map.get("notifyUrl");
            }
            if(map.containsKey("key")){
                this.key = map.get("key");
            }
            if(map.containsKey("payUrl")){
                this.payUrl = map.get("payUrl");
            }
            if(map.containsKey("zftd")){
                this.zftd = map.get("zftd");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        try {
            Map<String,String> param = sealRequest(payEntity);

            String sign = generatorSign(param);

            param.put("sign",sign);
            logger.info("[BAT]蝙蝠侠支付网银请求参数:{}",JSONObject.fromObject(param).toString());
            String formStr = HttpUtils.generatorForm(param,payUrl);

            if (StringUtils.isBlank(formStr)) {
                logger.error("[BAT]蝙蝠侠支付下单失败：生成表单结果为空");
                return PayResponse.error("[BAT]蝙蝠侠支付下单失败：生成表单结果为空");
            }

            return PayResponse.wy_form(payEntity.getPayUrl(),formStr);

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[BAT]蝙蝠侠支付网银支付下单失败"+e.getMessage());
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            Map<String,String> param = sealRequest(payEntity);

            String sign = generatorSign(param);

            param.put("sign",sign);
            logger.info("[BAT]蝙蝠侠支付扫码请求参数:{}",JSONObject.fromObject(param).toString());
            String formStr = HttpUtils.generatorForm(param,payUrl);

            if (StringUtils.isBlank(formStr)) {
                logger.error("[BAT]蝙蝠侠支付下单失败：生成表单结果为空");
                return PayResponse.error("[BAT]蝙蝠侠支付下单失败：生成表单结果为空");
            }

            return PayResponse.sm_form(payEntity,formStr,"下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[BAT]蝙蝠侠支付扫码支付下单失败"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[BAT]蝙蝠侠支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data))
            return "success";
        return "fail";
    }

    private boolean verifyCallback(String sign,Map<String,String> data) {

//        $sign=md5(status=(支付状态)&shid=(商户ID)&bb=(接口版本号)&zftd=(支付通道)&ddh=(商户订单号)&je=(订单金额)&ddmc=(订单名称)&ddbz=(订单备注)&ybtz=(异步通知地址)&tbtz=(同步跳转地址)&(商户KEY密匙));

        StringBuffer sb = new StringBuffer();
        sb.append("status=").append(data.get("status"));
        sb.append("&shid=").append(data.get("shid"));
        sb.append("&bb=").append(data.get("bb"));
        sb.append("&zftd=").append(data.get("zftd"));
        sb.append("&ddh=").append(data.get("ddh"));
        sb.append("&je=").append(data.get("je"));
        sb.append("&ddmc=").append(data.get("ddmc"));
        sb.append("&ddbz=").append(data.get("ddbz"));
        sb.append("&ybtz=").append(data.get("ybtz"));
        sb.append("&tbtz=").append(data.get("tbtz"));
        sb.append("&").append(key);
        String localSign;
        try {
            localSign = MD5Utils.md5toUpCase_32Bit(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("[BAT]蝙蝠侠支付生成支付签名串异常:"+ e.getMessage());
            return false;
        }
        return sign.equalsIgnoreCase(localSign);
    }

    /**
     *
     * @Description 封装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[BAT]蝙蝠侠支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());

            data.put("bb","1.0");// 版本号
            data.put("shid",memberid);//商户编号
            data.put("ddh",entity.getOrderNo());//商户订单号
            data.put("je",amount);//订单金额
            //zftd 支付宝和支付宝H5支付通道为LPAY（小写），微信，QQ，HPAY（小写）,快捷支付和网银支付通道为PPAY（小写)
            String[] zftdArry = zftd.split(",");
            int payType = Integer.parseInt(entity.getPayType());
            data.put("zftd", zftdArry[payType-2]);
            data.put("zfbm", entity.getPayCode());
            data.put("ybtz",notifyUrl);//异步通知URL
            data.put("tbtz",notifyUrl);//同步跳转URL
            data.put("ddmc","top_up");//订单名称
            data.put("ddbz","epay");//订单备注
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[BAT]蝙蝠侠支付封装请求参数异常:"+e.getMessage());
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
        logger.info("[BAT]蝙蝠侠支付生成支付签名串开始==================START========================");
        try {
            //签名规则:
//            $sign=md5(shid=(商户ID)&bb=(版本号)&zftd=(支付通道) &zfmb=(支付编码)&ddh=(订单号)&je=(支付金额)&ddmc=(订单名称)&ddbz=(订单备注)&ybtz=(异步通知地址)&tbtz=(同步跳转地址)&(商户KEY密匙));
//            签名采用 32 位小写 MD5 签名值，GB2312 编码
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("shid=").append(data.get("shid"));
            strBuilder.append("&bb=").append(data.get("bb"));
            strBuilder.append("&zftd="+data.get("zftd"));
            strBuilder.append("&zfbm=").append(data.get("zfbm"));
            strBuilder.append("&ddh="+data.get("ddh"));
            strBuilder.append("&je="+data.get("je"));
            strBuilder.append("&ddmc="+data.get("ddmc"));
            strBuilder.append("&ddbz="+data.get("ddbz"));
            strBuilder.append("&ybtz="+data.get("ybtz"));
            strBuilder.append("&tbtz="+data.get("tbtz"));
            strBuilder.append("&").append(key);

            logger.info("[BAT]蝙蝠侠支付生成待签名串:"+strBuilder.toString());
            String md5Value = MD5Utils.md5toUpCase_32Bit(strBuilder.toString());
            if (StringUtils.isBlank(md5Value)) {
                logger.error("[BAT]蝙蝠侠支付生成签名异常：生成签名为空");
                throw new Exception("生成支付签名串异常!");
            }
            logger.info("[BAT]蝙蝠侠支付生成加密签名串:"+md5Value.toLowerCase());
            return md5Value.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[BAT]蝙蝠侠支付生成支付签名串异常:"+e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }
}
