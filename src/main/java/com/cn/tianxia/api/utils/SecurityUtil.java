package com.cn.tianxia.api.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.codec.binary.StringUtils;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV; 
import org.apache.commons.codec.binary.Base64;
public class SecurityUtil extends Properties {

	
	
	public static String defaultIV;
	
	
	public static void main(String[] args) {
		String originalString = "(ACTIVE_KEY|MjSBcsS6kz|x1d|x1d0123456789|RMB|22222)+ SCKEYhjshgsdgy37GHhsd567+x1d0123456789"; // String to be encrypted.
		String keyString = "SCKEYhjshgsdgy37GHhsd567"; // A secret key that is used during
										// encryption and decryption
		try {
			String encryptedString = encrypt(originalString, keyString);
			////System.out.println("Original String: " + originalString);
			////System.out.println("Encrypted String: " + encryptedString);
			String decryptedString = decrypt(encryptedString, keyString);
			////System.out.println("Decrypted String: " + decryptedString);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String decrypt(String name, String keyString) throws Exception {
		BlowfishEngine engine = new BlowfishEngine();
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(engine);
		StringBuffer result = new StringBuffer();
		KeyParameter key = new KeyParameter(keyString.getBytes());
		cipher.init(false, key);
		byte out[] = Base64.decodeBase64(name);
		byte out2[] = new byte[cipher.getOutputSize(out.length)];
		int len2 = cipher.processBytes(out, 0, out.length, out2, 0);
		cipher.doFinal(out2, len2);
		String s2 = new String(out2);
		for (int i = 0; i < s2.length(); i++) {
			char c = s2.charAt(i);
			if (c != 0) {
				result.append(c);
			}
		}

		return result.toString();
	}

	public static String encrypt(String value, String keyString) throws Exception {
		BlowfishEngine engine = new BlowfishEngine();
		
		
//		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(engine);
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CFBBlockCipher(new BlowfishEngine(), 8));
		
//		KeyParameter key = new KeyParameter(keyString.getBytes());
//		cipher.init(true, key);
		
		
		//1111111
		StringBuilder str=new StringBuilder();
		Random randoms=new Random(); 
		//随机生成数字，并添加到字符串 
		 int max=99999999; 
	     int min=10000000; 
		 int ss = randoms.nextInt(max)%(max-min+1) + min; 
		 str.append(ss); 
		
		////System.out.println(str.toString());
		
		int random =Integer.parseInt(str.toString());
		
		//defaultIV = Base64.toBase64String(StringUtils.getBytesUsAscii(String.valueOf(random))).toString();
		defaultIV=Base64.encodeBase64String(StringUtils.getBytesUsAscii(String.valueOf(random)));
		////System.out.println(defaultIV.toString());
		
		//defaultIV = Convert.ToBase64String(StringUtils.getBytesUsAscii(String.valueOf(random)));
		//defaultIV = "MTIzNDU2Nzg=";
//		byte[] bb = Base64.decode(defaultIV);
//		
//		////System.out.println(Base64.decode(defaultIV).toString());
		
		cipher.init(true, new ParametersWithIV(new KeyParameter(StringUtils.getBytesUsAscii(setKey(keyString))), Base64.decodeBase64(defaultIV))); 
		
		
		//1111111
//		  byte[] temp1 = cipher.doFinal(StringUtils.getBytesUsAscii(value));
//
//
//        byte[] temp2 = Base64.toBase64String(defaultIV);
//
//        byte[] temp3 = new byte[temp1.Length + temp2.Length];
//
//        temp2.CopyTo(temp3, 0);
//        temp1.CopyTo(temp3, temp2.Length);
//
//
//
//        string IV_encryptedStr = Convert.ToBase64String(temp3);
//        return IV_encryptedStr;
		
		
		
		
		byte in[] = value.getBytes();
		byte temp2[] = Base64.decodeBase64(defaultIV);
		
		
		byte temp3[] = new byte[in.length+temp2.length];
		//temp2.CopyTo(temp3, 0); 
//      temp1.CopyTo(temp3, temp2.Length);
		
		////System.out.println("temp2 length"+temp2.length);
	    System.arraycopy(temp2, 0, temp3, 0, temp2.length);   
	    
	    
	    
		////System.out.println("length"+in.length);
		byte out[] = new byte[cipher.getOutputSize(in.length)];
		int len1 = cipher.processBytes(in, 0, in.length, out, 0);
		try {
			cipher.doFinal(out, len1);
			////System.out.println("out length"+out.length);
			System.arraycopy(out, 0, temp3, temp2.length, out.length-1);  
		} catch (CryptoException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		////System.out.println(len1);
		String s = new String(Base64.encodeBase64(temp3));
		return s;
	}
	
	
	public final static String setKey(String key)
	{
//            
//                         // get the size of the encryption key
//			$keysize = mcrypt_get_key_size ($this->cipher, $this->mode);
//			echo "Keysize = $keysize, Key = $encryptkey, Key Length = ".strlen($encryptkey)."<br>\n";
//
//            // if the encryption key is less than 32 characters long and the expected keysize is at least 32 md5 the key
//			if ((strlen($encryptkey) < 32) && ($keysize >= 32)) {
//				$encryptkey = md5($encryptkey);
//            // if encryption key is longer than $keysize and the keysize is 32 then md5 the encryption key
//			} elseif ((strlen($encryptkey) > $keysize) && ($keysize == 32)) {
//				$encryptkey = md5($encryptkey);
//			} else {
//				if ($keysize > strlen($encryptkey)) {
//                    // if encryption key is shorter than the keysize, strpad it with space
//					$encryptkey = str_pad($encryptkey, $keysize);
//				} else {
//                    // if encryption key is longer than the keysize substr it to the correct keysize length
//					$encryptkey = substr($encryptkey, 0, $keysize);
//				}
//			}
//            //echo "Keysize = $keysize, Key = $encryptkey, Key Length = ".strlen($encryptkey)."<br>\n";
//			$this->key = $encryptkey;
//		} else {
//			return 0;
//		}
//             
//             
		String rekey;
		int keysize = 56;
		if (key.length() < 32 && keysize >= 32)
		{
			rekey = getMd5(key);
		}
		else if (key.length() > keysize && keysize == 32)
		{
			rekey = getMd5(key);
		}
		else
		{
			if (keysize > key.length())
			{
				rekey = key;
				for (int i = key.length(); i < keysize; i++)
				{
					rekey += " ";
				}

			}
			else
			{
				rekey = key.substring(0, keysize);
			}
		}

		return rekey;

	}
	
	
	//静态方法，便于作为工具类   
	   public static String getMd5(String plainText) {   
	       try {   
	           MessageDigest md = MessageDigest.getInstance("MD5");   
	           md.update(plainText.getBytes());   
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
	           //32位加密   
	           return buf.toString();   
	           // 16位的加密   
	           //return buf.toString().substring(8, 24);   
	       } catch (NoSuchAlgorithmException e) {   
	           e.printStackTrace();   
	           return null;   
	       }   
	  
	   }
}