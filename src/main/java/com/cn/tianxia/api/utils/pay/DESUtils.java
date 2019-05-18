package com.cn.tianxia.api.utils.pay;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @ClassName DESUtils
 * @Description DES加密工具类
 * @author Hardy
 * @Date 2018年10月14日 下午2:23:20
 * @version 1.0.0
 */
public class DESUtils {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(DESUtils.class);
    // 向量
    private final static String iv = "87654321";
    // 加解密统一使用的编码方式
    private final static String encoding = "UTF-8";
    
    private static final String DES_KEY = "PR3tmPJqcH5RE0iGsW4qiN5VYtjX7sVU";
    
    /**
     * 3DES加密
     * @param plainText 普通文本
     * @param secretKey
     * @return
     * @throws Exception
     */
    public static String encrypt(String plainText,String secretKey){
        logger.info("DES加密开始=======================START===================");
        try{
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);

            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
            byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
            Base64 base = new Base64();
            return base.encodeToString(encryptData);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("DES加密异常:"+e.getMessage());
        }
        return plainText;
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decryp(String encryptText,String secretKey){
        logger.info("DES解密开始=======================START===================");
        try{
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

            Base64 base = new Base64();
            byte[] decryptData = cipher.doFinal(base.decode(encryptText));
            return new String(decryptData, encoding);
        }catch(Exception e){
            e.printStackTrace();
            logger.info("DES解密异常:"+e.getMessage());
        }
        return encryptText;
    }
    
    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decryp(String encryptText){
        logger.info("DES解密开始=======================START===================");
        try{
            return decryp(encryptText,DES_KEY);
        }catch(Exception e){
            e.printStackTrace();
            logger.info("DES解密异常:"+e.getMessage());
        }
        return encryptText;
    }
    
    public static void main(String[] args) {
        String str = "我是中国人!";
        Base64 base = new Base64();
        //加密
        String encode = base.encodeAsString(str.getBytes());
        System.err.println(encode);
        String decode = new String(base.decode(encode));
        System.err.println(decode);
    }
}
