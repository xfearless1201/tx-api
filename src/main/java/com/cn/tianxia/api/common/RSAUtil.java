package com.cn.tianxia.api.common;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.cn.tianxia.api.utils.rczf.Base64Utils;

/**
 * @ClassName: RSAUtil
 * @Description: RSA加密工具类
 * @Author: Zed
 * @Date: 2018-12-14 14:49
 * @Version:1.0.0
 **/

public class RSAUtil {

    public static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * 签名算法
     */
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * RSA签名
     * @param content 待签名数据
     * @param privateKey 商户私钥
     * @return 签名值
     */
    public static String sign(String content, String privateKey)
    {
        try
        {
            System.out.println("待签名字符串：" + content);
            PKCS8EncodedKeySpec priPKCS8    = new PKCS8EncodedKeySpec( Base64.decodeBase64(privateKey) );

            KeyFactory keyf                 = KeyFactory.getInstance("RSA");
            PrivateKey priKey               = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update( content.getBytes(DEFAULT_CHARSET));

            byte[] signed = signature.sign();

            return Base64.encodeBase64String(signed);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 签名
     * @param params
     * @return
     * @throws Exception
     */
    public static String sign(TreeMap<String,String> params, String privateKey) throws Exception{
        String content = getSignContent(params);
        String sign = sign(content,privateKey);
        return sign;
    }

    public static String getSignContent(TreeMap<String,String> params){
        if(params.containsKey("signMsg"))//签名明文组装不包含sign字段和signType
            params.remove("signMsg");
        if(params.containsKey("signType"))//签名明文组装不包含sign字段和signType
            params.remove("signType");

        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry:params.entrySet()){
            if(entry.getValue()!=null&&entry.getValue().length()>0){
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        if(sb.length()>0){
            sb.deleteCharAt(sb.length()-1);
        }
        //String sign = md5(sb.toString().getBytes("UTF-8"));//记得是md5编码的加签
        return sb.toString();
    }

    public static boolean doCheck(TreeMap<String,String> params, String sign, String publicKey)  {
        String content = getSignContent(params);
        return doCheck(content, sign, publicKey);
    }

    /**
     * RSA验签名检查
     * @param content 待签名数据
     * @param sign 签名值
     * @param publicKey 分配给开发商公钥
     * @return 布尔值
     */
    public static boolean doCheck(String content, String sign, String publicKey)
    {
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));


            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update( content.getBytes(DEFAULT_CHARSET) );

            boolean bverify = signature.verify( Base64.decodeBase64(sign) );
            return bverify;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    /**
     *
     * 校验数字签名
     *
     *
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     *
     * @return
     * @throws Exception
     *
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64Utils.decode(sign));
    }

    public static Boolean  verifyMap(Map map,String sign,String publicKey){
        Boolean success = false;
        try {
            if(!map.isEmpty()){
                Set<String> keySet = map.keySet();
                String[] keyArray = keySet.toArray(new String[keySet.size()]);
                Arrays.sort(keyArray);
                StringBuffer buffer   =  new StringBuffer();
                for(String key : keyArray){
                    /**
                     * 如果为空就不加入签名
                     */
                    String value = map.get(key)==null||"null".equals(map.get(key).toString())?"":map.get(key).toString();
                    if(StringUtils.isNotBlank(value)){
                        buffer.append(key+"="+value).append("&");
                    }
                }
                /**
                 * 重新排序后的字符串
                 */
                String str = buffer.toString().toUpperCase();
                success = verify(str.getBytes(),publicKey,sign);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return success;
    }


    public static void main(String[] args) {
        String inputCharset = "1";//1-utf-8

        String cus_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwqqrew6Gj3+dG1/ducnevZdUlJYsWK0NG7xcA/HMB5KnT1xgdGlydaDMM7R38l8qj6wlzQrUycQX1uDsS35o3WXs8Fn54Dqj3B434fedNNuCYhcuuXYattE2eyq+IhlE45Xm9CRQP2am0K5xa9DtFNgJl4Ti8ZWLbfAEtkReBvwIDAQAB";
        String cus_private_key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALCqqt7DoaPf50bX925yd69l1SUlixYrQ0bvFwD8cwHkqdPXGB0aXJ1oMwztHfyXyqPrCXNCtTJxBfW4OxLfmjdZezwWfngOqPcHjfh950024JiFy65dhq20TZ7Kr4iGUTjleb0JFA/ZqbQrnFr0O0U2AmXhOLxlYtt8AS2RF4G/AgMBAAECgYB9I4cBYCWhBLq4DcZwb5ijn0hJlbxdqatwFW9//VIhpy08pwGnq8KqpOJ0Z4l0ILkjPxtkj332CuUj1qnRhp2fJfMCv7WNV5+FAlXxmTj0BjNCDbWhbBgUq2yc+6/REe8Gk7e0j1hKMtd40Wj0X2h1RNunQ7tfZNazNaK7dD4KcQJBANZGXDjPtK/AlxUkES87hwhmWk88Ay+FpIpL0xB8BEbJiulY+Sv5q5yyRD3D2/zDFlMrtnMoac1k7xfrIC/NklUCQQDTEYhsC+64Yh+MRDpcuB66pCccz0SkarbBmNbVB++TQKX+32pM5BMzMkqLzABCt6zBdYF/Gbad6gEaH9Fz5d/DAkAx99h7uxwkvCG/YVjMjfIwaEX5IkKP63dydLo0pucMZuWJAyGgOqnlccDuMmVzrT7giFoUwsDhj/8dscwguay9AkEAwgClkhpNq7CVKsozkrGxnP/w62wfvbhQjxW6sUiADqsnaGNWQ6KHe1FIb8JsouZMpe0pv/eUgQVrSeXhEVRDHQJAeBXXn9Nqe75h+XTBfcb8vSNXiS+QWkw8ji1elV1xgWJ6WVjo+YsSnBYssdY5jXR0EMZabYXgYwtR+0KbAz81mw==";
        String partnerId = "1808052411081102";

        String signType = "1";
        String notifyUrl = "http://localhost:8080/notify";
        String returnUrl = "http://localhost:8080/returns";
        String orderNo = String.valueOf(System.currentTimeMillis());
        String orderAmount = "1";//1分
        String orderCurrency = "156";//人民币
        String orderDatetime = "2018-03-22 08:08:00";
        String signMsg = "";//签名信息
        String payMode = "1";//1：微信，2：支付宝，5：QQ
        String isPhone = "0";
        String subject = "测试订单名称"+orderNo;//
        String body = "测试订单内容";


        TreeMap<String,String> params = new TreeMap<String,String>();
        params.put("inputCharset", inputCharset);
        params.put("partnerId", partnerId);
        params.put("signType", signType);
        params.put("notifyUrl", notifyUrl);
        params.put("returnUrl", returnUrl);
        params.put("orderNo", orderNo);
        params.put("orderAmount", orderAmount);
        params.put("orderCurrency", orderCurrency);
        params.put("orderDatetime", orderDatetime);
        params.put("signMsg", signMsg);
        params.put("payMode", payMode);
        params.put("isPhone", isPhone);
        params.put("subject", subject);
        params.put("body", body);

        try {
            signMsg = sign(params,cus_private_key);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("签名：signMsg="+signMsg);

        boolean check = doCheck(params, signMsg, cus_public_key);
        System.out.println("验证签名：结果="+check);
    }

}
