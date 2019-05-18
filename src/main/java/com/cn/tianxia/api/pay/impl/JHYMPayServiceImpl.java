package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
 * @Auther: zed
 * @Date: 2019/1/28 09:53
 * @Description: 聚合银码支付
 */
public class JHYMPayServiceImpl implements PayService {
    private final static Logger logger = LoggerFactory.getLogger(JHYMPayServiceImpl.class);

    private String pid;
    private String key;
    private String payUrl;
    private String notifyUrl;

    public JHYMPayServiceImpl(Map<String,String> map) {
        if(map != null && !map.isEmpty()){
            if(map.containsKey("pid")){
                this.pid = map.get("pid");
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
        }

    }


    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        try {
            Map<String,String> param = sealRequest(payEntity);

            logger.info("[JHYM]聚合银码支付扫码请求参数:{}",JSONObject.fromObject(param).toString());
            String formStr = HttpUtils.generatorForm(param,payUrl);

            if (StringUtils.isBlank(formStr)) {
                logger.error("[JHYM]聚合银码支付下单失败：生成表单结果为空");
                PayResponse.error("[JHYM]聚合银码支付下单失败：生成表单结果为空");
            }

            return PayResponse.sm_form(payEntity,formStr,"下单成功");

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[JHYM]聚合银码支付扫码支付下单失败"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[JHYM]聚合银码支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data))
            return "success";
        return "fail";
    }

    private boolean verifyCallback(String sign,Map<String,String> data) {

        String localSign;
        try {
            localSign = generatorSign(data);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JHYM]聚合银码支付生成支付签名串异常:"+ e.getMessage());
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
        logger.info("[JHYM]聚合银码支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("0.00").format(entity.getAmount());

            data.put("pid",pid);//商户id 	是	Int	1001
            data.put("type",entity.getPayCode());//	是	String	alipay	alipay:支付宝,wxpay:微信支付
            data.put("out_trade_no",entity.getOrderNo());//	是	String	20160806151343349
            data.put("uid",entity.getuId());//	是	Int	31334	充值用户的ID
            data.put("notify_url",notifyUrl);//	是	String	http://rr335566.xyz/notify_url.php	服务器异步通知地址
            data.put("return_url",entity.getRefererUrl());//	是	String	http://rr335566.xyz/return_url.php	页面跳转通知地址
            data.put("name","top_Up");//商品名称	是	String	VIP会员
            data.put("money",amount);//	是	String	1.00
            //data.put("sitename","");// 网站名称	否	String	聚合支付任务
            data.put("sign",generatorSign(data));//	是	String	202cb962ac59075b964b07152d234b70	签名算法与支付宝签名算法相同
            data.put("sign_type","MD5");//	是	String	MD5

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JHYM]聚合银码支付封装请求参数异常:"+e.getMessage());
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
        logger.info("[JHYM]聚合银码支付生成支付签名串开始==================START========================");
        try {
            //签名规则:
            TreeMap<String,String> sortMap = new TreeMap<>(data);
            StringBuilder strBuilder = new StringBuilder();
            for (Map.Entry<String,String> entry:sortMap.entrySet()) {
                if ("sign".equalsIgnoreCase(entry.getKey()) || "sign_type".equalsIgnoreCase(entry.getKey()))
                    continue;
                strBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");

            }
            strBuilder.deleteCharAt(strBuilder.length() - 1);  //去掉最后一个&
            strBuilder.append(key);
            logger.info("[JHYM]聚合银码支付生成待签名串:"+strBuilder.toString());
//            String signString = URLEncoder.encode(strBuilder.toString(),"UTF-8");
//            logger.info("[JHYM]聚合银码支付URLEncoder后生成待签名串:"+strBuilder.toString());
            String md5Value = MD5Utils.md5toUpCase_32Bit(strBuilder.toString());
            if (StringUtils.isBlank(md5Value)) {
                logger.error("[JHYM]聚合银码支付生成签名异常：生成签名为空");
                throw new Exception("生成支付签名串异常!");
            }
            logger.info("[JHYM]聚合银码支付生成加密签名串:"+md5Value.toLowerCase());
            return md5Value.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JHYM]聚合银码支付生成支付签名串异常:"+e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }
}
