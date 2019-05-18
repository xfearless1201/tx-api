package com.cn.tianxia.api.utils.pay;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName MD5Utils
 * @Description MD5工具类
 * @author Hardy
 * @Date 2018年9月12日 上午11:53:04
 * @version 1.0.0
 */
public class MD5Utils {

    /**
     * @Description 32位MD5加密---大写
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String md5toUpCase_32Bit(String str) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            int i;
            // 字符数组转换成字符串
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < byteDigest.length; offset++) {
                i = byteDigest[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            // 32位加密
            return buf.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Description 16位MD5加密---大写
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String md5toUpCase_16Bit(String str) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            int i;
            // 字符数组转换成字符串
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < byteDigest.length; offset++) {
                i = byteDigest[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            // 16位的加密
            return buf.toString().substring(8, 24).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用md5的算法进行加密
     */
    public static String md5(byte[] plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(plainText);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * @Description sha256加密
     * @param str
     * @return
     */
    public static String sha256ToUpCase(String str) {
        // 字符数组转换成字符串
        StringBuffer buf = new StringBuffer("");
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            byte[] byteDigest = messageDigest.digest();
            for (int i = 0; i < byteDigest.length; i++) {
                String temp = Integer.toHexString(byteDigest[i] & 0xFF);
                if (temp.length() == 1) {
                    // 1得到一位的进行补0操作
                    buf.append("0");
                }
                buf.append(temp);
            }
            return buf.toString().toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
