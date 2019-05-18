package com.cn.tianxia.api.utils.jczf;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Created on 2018/1/26.
 */
public class RSA {

    private static final String CHAR_ENCODING = "UTF-8";

    public static void checkSign(Map<String, String> resMap, String publicKey)
            throws Exception {
        Map<String, String> map = getTreeMap(resMap);
        String sign = map.remove("sign");
        checkSign(JSON.toJSONString(map), sign, publicKey);
    }

    private static void checkSign(String content, String sign, String publicKey)
            throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory
                .generatePublic(new X509EncodedKeySpec(base64DecoderToBytes(publicKey)));
        java.security.Signature signature = java.security.Signature
                .getInstance("SHA1WithRSA");
        signature.initVerify(pubKey);
        signature.update(content.getBytes("utf-8"));
        /** 5. result为true时表明验签通过 */
        if (!signature.verify(base64DecoderToBytes(sign))) {
            throw new Exception("验签失败");
        }
    }

    public static String sign(Map<String, String> paramMap, String privateKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        Map<String, String> map = getTreeMap(paramMap);
        return sign(JSON.toJSONString(map), privateKey);
    }

    private static Map<String, String> getTreeMap(Map<String, String> resMap) {
        Map<String, String> map = new TreeMap();
        for (Entry<String, String> entry : resMap.entrySet()) {
            if (entry.getValue() != null) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    private static String sign(String content, String privateKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                base64DecoderToBytes(privateKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Signature signature = Signature.getInstance("SHA1WithRSA");
        signature.initSign(priKey);
        signature.update(content.getBytes(CHAR_ENCODING));
        return base64BytesEncoder(signature.sign());
    }

    public static byte[] base64DecoderToBytes(String value) {
        return Base64.getDecoder().decode(value.getBytes());
    }

    public static String base64BytesEncoder(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

}
