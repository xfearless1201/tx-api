package com.cn.tianxia.api.utils;



import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class Encrypt {
	
	/**
	 * MD5 32位加密
	 * @param sourceStr
	 * @return
	 */
	public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
            
        } catch (NoSuchAlgorithmException e) {
           	e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
         return result;
    }
	
	
	/**
	 * AES加密
	 * @param sSrc
	 * @return
	 * @throws Exception
	 */
	public static String AESEncrypt(String value,String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		byte[] raw = key.getBytes("UTF-8");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
		String base64 = new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码
		return URLEncoder.encode(base64, "UTF-8");//URL加密
	}
	
	/**
	 * AES加密 不进行URLEncoder
	 * @param sSrc
	 * @return
	 * @throws Exception
	 */
	public static String AESUNURLEncrypt(String value,String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		byte[] raw = key.getBytes("UTF-8");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
		return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码
	}

	/**
	 * AES 解密
	 * @param sSrc
	 * @return
	 * @throws Exception
	 */
	public static String AESDecrypt(String value,String key,boolean isDecodeURL) throws Exception {
		try {
			byte[] raw = key.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			if(isDecodeURL)	value = URLDecoder.decode(value, "UTF-8");
			byte[] encrypted1 = new BASE64Decoder().decodeBuffer(value);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original, "UTF-8");
			return originalString;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}


	/**
	 * 功能描述: DES加密
	 *
	 * @Author: Horus
	 * @Date: 2019/1/6 9:51
	 * @param value
 	 * @param key
	 * @return: java.lang.String
	 **/
	public static String DESEncrypt(String value, String key) throws Exception {
		return URLEncoder.encode(DESUNURLEncrypt(value,key), "UTF-8");//URL加密
	}

	/**
	 * 功能描述: DES加密 不进行URLEncoder
	 *
	 * @Author: Horus
	 * @Date: 2019/1/6 9:51
	 * @param value
	 	 * @param key
	 * @return: java.lang.String
	 **/
	public static String DESUNURLEncrypt(String value, String key) throws Exception {
		SecretKeySpec localSecretKeySpec = new SecretKeySpec(key.getBytes(), "DES");
		Cipher localCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		localCipher.init(Cipher.ENCRYPT_MODE, localSecretKeySpec);

		byte[] result = localCipher.doFinal(value.getBytes());
		return new BASE64Encoder().encode(result);// 此处使用BASE64做转码
	}

	/**
	 * 功能描述: DES解密
	 *
	 * @Author: Horus
	 * @Date: 2019/1/6 9:50
	 * @param value
	 	 * @param key
	 * @return: java.lang.String
	 **/
	public static String DESDecrypt(String value, String key) throws Exception {
		//解密
		SecretKeySpec localSecretKeySpec = new SecretKeySpec(key.getBytes(), "DES");
		Cipher cipher=Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, localSecretKeySpec);

		byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
		return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码
	}
}
