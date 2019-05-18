package com.cn.tianxia.api.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class Encryption {
	public static void main(String args[]) throws Exception {
		String key = "txw8888888!@#$%^"; 
		String time = String.valueOf(System.currentTimeMillis());
		String content = MD5Encoder.encode("123456");
		System.out.println("key:" + content + "   content :" + content);
		System.out.println("encrypted: " + encrypt(content, key, key));
		System.out.println("decrypted: " + desEncrypt(content, key, key).trim());
	}

	public static String encrypt(String data, String key, String iv) throws Exception {
		try {
			// String data = "123456";
			// String key = "1234567812345678";
			// String iv = "1234567812345678";
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			int blockSize = cipher.getBlockSize();
			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}
			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);
			return new sun.misc.BASE64Encoder().encode(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String desEncrypt(String data, String key, String iv) throws Exception {
		String encrypted = encrypt(data, key, iv);
		try {
			data = encrypted;
			// String key = "1234567812345678";
			// String iv = "1234567812345678";
			byte[] encrypted1 = new BASE64Decoder().decodeBuffer(data);
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}