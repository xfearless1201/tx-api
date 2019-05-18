package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.RSAUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;

import net.sf.json.JSONObject;

/**
 * @ClassName: JRZFPayServiceImpl
 * @Description: 金睿支付
 * @Author: Zed
 * @Date: 2018-12-14 14:25
 * @Version:1.0.0
 **/

public class JRZFPayServiceImpl implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(JRZFPayServiceImpl.class);
    private String partnerId = "1813053202124555";//商户号
    private String notifyUrl = "http://txw.tx8899.com/AMH/Notify/JRZFNotify.do";
    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJnElFoherENgsteVh6cz9xuYaWrSJh5AUQBPS3Zh58OU65+vWXzgsZ+KNtYdx01Sl5LCJWKdW5bmkP0tQADJ5ty+5gghE8U+xb83o2WCYHrTJyq+YFIsvJkFcYmqbeQGHGY16z2z3/D5WR8dOI3n5MhNuH4aZmC+EqEawthh0X9AgMBAAEC\n" +
            "gYB9ijMciuzilIdOhkyFXb2O2Ee8vGTepxxTazeJsWm6gDXt5ue2zuLcL7AntMg6/oEjDtuJS6uo" +
            "V/1Qsf78bZbtWup+vfQUkmiJdx34rnjS6wgzGHGmysSak5a2pUiGwH2fIz4Xo6dejP6ApjdaDvHw" +
            "VjKYvcuwJnMcQVc+fqtzAQJBAN9YC24iifMKPg+JTZJVW4eW3FiqOmjmFRnTeVjRC9FiJQuJg3qP" +
            "YD/MCYSc7oPYc8HXHqubYCu7ng/Krid8j90CQQCwQD5y4zUqEbU+8LgJXIKdzFLvWlUtireHoOXy" +
            "WyxKUoPbVbUxu1BOhF4ONNPa6kXjIB1maQNBibrHOoWZTTyhAkBJ0LOWZtvo842nXN3Ca2ug2H9i" +
            "3oAHg2Od8YK9k/mv7hwIQB6wNwq8ixbqOWOrN7Kqcgq09NlIGu4WkgoVCtoRAkAttTt8x/etpVH/" +
            "tKXFp7wn+Ck58Y9NdEAMaGm55HMkAyo2449OCmnVMvbBcGnQyBi/wkBdorY5dOJ/cGndS+6BAkBu" +
            "IOQzpr6tLAtWNI7vg1sgW4hFnD4KYIofya3C9OwPAOgBB3MT4yZWEdhoGAu8jUzNiVXfJsNb+10M" +
            "jIpdco6P";
    private String publiceKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCZxJRaIXqxDYLLXlYenM/cbmGlq0iYeQFEAT0t2YefDlOufr1l84LGfijbWHcdNUpeSwiVinVuW5pD9LUAAyebcvuYIIRPFPsW/N6NlgmB60ycqvmBSLLyZBXGJqm3kBhxmNes9s9/w+VkfHTiN5+TITbh+GmZgvhKhGsLYYdF/QIDAQAB";
    private String payUrl = "http://www.jruipay.com/Service/pay/unify";    //支付地址

    public JRZFPayServiceImpl(Map<String,String> map){
        if(map != null && !map.isEmpty()){
            if(map.containsKey("partnerId")){
                this.partnerId = map.get("partnerId");
            }
            if(map.containsKey("notifyUrl")){
                this.notifyUrl = map.get("notifyUrl");
            }
            if(map.containsKey("privateKey")){
                this.privateKey = map.get("privateKey");
            }
            if(map.containsKey("publiceKey")){
                this.publiceKey = map.get("publiceKey");
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
            logger.info("[JRZF]金睿支付扫码支付==================START====================");
            Map<String, String> params = sealRequest(payEntity);
            String signData = generatorSign(params);
            params.put("signMsg",signData);
            logger.info("[JRZF]金睿支付扫码支付请求参数;{}",params.toString());

            String response = HttpUtils.post(params,payUrl);

            if (StringUtils.isBlank(response)) {
                logger.error("[JRZF]金睿支付扫码支付请求异常,请求结果为空!");
                return PayResponse.error("[JRZF]金睿支付扫码支付请求异常,请求结果为空!");
            }
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.containsKey("errCode") && "0000".equals(jsonObject.getString("errCode"))){
                logger.info("[JRZF]金睿支付扫码支付成功状态值:{}",jsonObject.getString("errCode"));
                //支付充值成功
                String qrCode = jsonObject.getString("qrCode");
                if(org.apache.commons.lang.StringUtils.isBlank(payEntity.getMobile())){
                    //PC端
                    return PayResponse.sm_qrcode(payEntity, qrCode, "扫码下单成功");
                }
                String retHtml = jsonObject.getString("retHtml");
                return PayResponse.sm_form(payEntity, retHtml, "H5下单成功");
            }

            return PayResponse.error("下单失败:"+response);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JRZF]金睿支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[JRZF]金睿支付扫码支付异常:"+e.getMessage());
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        try {
            String sourceSign = data.remove("signMsg");
            logger.info("[JRZF]金睿支付回调验签原签名串;",data.toString());
            logger.info("[JRZF]金睿支付回调验签原签名字段：{}",sourceSign);
            TreeMap<String,String> treeMap = new TreeMap<>();
            treeMap.putAll(data);
            boolean validSign = RSAUtil.doCheck(treeMap,sourceSign,publiceKey);
            if (validSign) {
                logger.info("[JRZF]金睿支付回调验签成功");
                return "success";
            }
            return "fail";
        } catch (Exception e) {
            logger.error("[JFZF]发家支付回调验签异常:" + e.getMessage());
            return "fail";
        }
    }

    /**
     *
     * @Description 封装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[JRZF]金睿支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String,String> data = new HashMap<>();
            //订单时间
            Date orderTime = new Date();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);//交易金额 分为单位
            data.put("inputCharset","1");//字符集    固定填1；1代表UTF-8
            data.put("partnerId",partnerId);//商户号
            data.put("signType","1");   // 1 代表RSA
            data.put("notifyUrl",notifyUrl);//后台回调通知地址
            data.put("returnUrl",notifyUrl);//页面通知地址
            data.put("orderNo",entity.getOrderNo());//订单号
            data.put("orderAmount",amount);// 交易金额
            data.put("orderCurrency","156");// 固定填156;人民币
            data.put("orderDatetime",new SimpleDateFormat("yyyyMMddHHmmss").format(orderTime));
            data.put("payMode",entity.getPayCode());
            //data.put("ip",entity.getIp());  //微信h5必传，商户获取客户的ip，然后提交给平台，非商户的服务器ip

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JRZF]金睿支付封装请求参数异常:"+e.getMessage());
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
        logger.info("[JY]九域支付生成支付签名串开始==================START========================");
        try {
            //签名规则:
//            1. 筛选
//            获取所有请求参数，不包括字节类型参数，如文件、字节流，剔除 sign 与 sign_type 参数。
//            2. 排序
//            将筛选的参数按照第一个字符的键值 ASCII 码递增排序（字母升序排序），如果遇到相同字符则按照
//            第二个字符的键值 ASCII 码递增排序，以此类推。
//            3. 拼接
//            将排序后的参数与其对应值，组合成“参数=参数值”的格式，并且把这些参数用&字符连接起来，此时生成的字符串为待签名字符串。将待签名字符串和商户私钥带入 SHA1 算法中得出 sign。

            TreeMap<String,String> treemap = new TreeMap<>();
            treemap.putAll(data);

            logger.info("[JRZF]金睿支付生成待签名串:"+treemap.toString());
            String sign = RSAUtil.sign(treemap,privateKey);
            logger.info("[JRZF]金睿支付生成加密签名串:"+sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[JRZF]金睿支付生成支付签名串异常:"+e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }
}
