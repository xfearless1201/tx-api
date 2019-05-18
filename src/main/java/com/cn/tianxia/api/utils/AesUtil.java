package com.cn.tianxia.api.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AesUtil {
	 static{
			try {
				Security.addProvider(new BouncyCastleProvider());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	 
		public static void main(String[] args) {
			String key="07129c5323b07a5f";
			String data="zxczczczxczxc";
	    	String s = encrypt(key, "加密内容");
	    	String s2 = decrypt(key, s);
	    	System.out.println(s);
	    	System.out.println(s2);
		}
		
		public static String encrypt(String key, String str) {
			String encryptValue = "";
			byte[] keyByte = key.getBytes(); 
	    	byte[] plaintext = null;
			try {
				plaintext = str.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

	    	try {
	    		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
	    		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");

				cipher.init(Cipher.ENCRYPT_MODE, keySpec);
				
				byte[] ciphertext = cipher.doFinal(plaintext);
				//encryptValue = Base64.getEncoder().encodeToString(ciphertext);
				encryptValue =Base64.encodeBase64String(ciphertext);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return encryptValue;
		}
		
		public static String decrypt(String key, String str) {
			String encryptValue = "";
			byte[] keyByte = key.getBytes();
	    	
	    	byte[] plaintext = null;
			try {
				plaintext = str.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

	    	try {
	    		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
	    		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");

		        cipher.init(Cipher.DECRYPT_MODE, keySpec);  
		        //byte[] bys = cipher.doFinal(Base64.getDecoder().decode(plaintext));   
		        byte[] bys = cipher.doFinal(Base64.decodeBase64(plaintext));  
				try {
					encryptValue = new String(bys, "UTF-8");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return encryptValue;
		}
}
