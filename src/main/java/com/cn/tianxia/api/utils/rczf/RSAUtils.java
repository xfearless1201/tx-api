package com.cn.tianxia.api.utils.rczf;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
/**
 * RSA Util
 * @author kcw
 */
public class RSAUtils {
	 /** 
     * Dedicated set of bytes to string data
     */  
    private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  
    
    /** 
     * Load a public key from a string
     *  
     * @param publicKeyStr 
     *            Public key data string
     * @throws Exception 
     *             The exception that occurs when the public key is loaded
     */  
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr)  
            throws Exception {  
        try {  
            byte[] buffer = Base64Utils.decode(publicKeyStr);  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);  
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("No such algorithm");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("Public key illegal");  
        } catch (NullPointerException e) {  
            throw new Exception("The public key data is empty");  
        }  
    }  
    
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)  
            throws Exception {  
        try {  
            byte[] buffer = Base64Utils.decode(privateKeyStr);  
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("No such algorithm");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("Private key illegal");  
        } catch (NullPointerException e) {  
            throw new Exception("Private key data is empty");  
        }  
    }  
    
	 /** 
     * Public key encryption process
     *  
     * @param publicKey 
     *            Public key 
     * @param plainTextData 
     *            plainText data 
     * @return 
     * @throws Exception 
     *             The exception information in the encryption process
     */  
    public static byte[] publicKeyEncrypt(RSAPublicKey publicKey, byte[] plainTextData)  
            throws Exception {  
        if (publicKey == null) {  
            throw new Exception("The encrypted public key is empty. Please set");  
        }  
        Cipher cipher = null;  
        try {  
            // Use default RSA 
            cipher = Cipher.getInstance("RSA");  
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
            byte[] output = cipher.doFinal(plainTextData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("No such encryption algorithm");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("Encrypting public key is illegal, please check");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("Plaintext length illegal");  
        } catch (BadPaddingException e) {  
            throw new Exception("The plaintext data is corrupted");  
        }  
    }  
    /** 
     * Public key decryption process
     *  
     * @param publicKey 
     *            public key 
     * @param cipherData 
     *            Ciphertext data
     * @return plaintex data
     * @throws Exception 
     *             The exception information in the decryption process
     */  
    public static String publicKeyDecrypt(String publicKey, String cipherData)  throws Exception {
    	RSAPublicKey tmpPublicKey = loadPublicKeyByStr(publicKey);
        if (publicKey == null) {  
            throw new Exception("The decryption public key is empty. Please set");  
        }  
        Cipher cipher = null;  
        try {  
            // Use default RSA 
            cipher = Cipher.getInstance("RSA");  
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
            cipher.init(Cipher.DECRYPT_MODE, tmpPublicKey);  
            byte[] output = cipher.doFinal(Base64Utils.decode(cipherData));  
            return new String(output);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("No such decryption algorithm");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("Decrypting public key is illegal. Please check");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("Ciphertext length illegal");  
        } catch (BadPaddingException e) {  
            throw new Exception("The ciphertext data is corrupted");  
        }  
    }  
    
    /** 
     * Private key encryption process
     *  
     * @param privateKey 
     *            Private key
     * @param plainTextData 
     *            plainText Data
     * @return 
     * @throws Exception 
     *             The exception information in the encryption process
     */  
    public static String privateKeyEncrypt(String  privateKey, String plainTextData) throws Exception { 
    	RSAPrivateKey tmpPrivateKey = loadPrivateKeyByStr(privateKey);
            
        if (privateKey == null) {  
            throw new Exception("The encryption private key is empty. Please set");  
        }  
        Cipher cipher = null;  
        try {  
            // Use default RSA
            cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.ENCRYPT_MODE, tmpPrivateKey);  
            byte[] output = cipher.doFinal(plainTextData.getBytes());  
            return Base64Utils.encode(output);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("No such encryption algorithm");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("Encrypting private key is illegal. Please check");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("Plaintext length illegal");  
        } catch (BadPaddingException e) {  
            throw new Exception("The plaintext data is corrupted");  
        }  
    }  
    
    /** 
     * Decryption process of private key
     *  
     * @param privateKey 
     *            private key
     * @param cipherData 
     *            Ciphertext data
     * @return plainText Data
     * @throws Exception 
     *             The exception information in the decryption process
     */  
    public static String privateKeyDecrypt(RSAPrivateKey privateKey, byte[] cipherData)  
            throws Exception {  
        if (privateKey == null) {  
            throw new Exception("The decryption private key is empty. Please set");  
        }  
        Cipher cipher = null;  
        try {  
            // Use default RSA  
            cipher = Cipher.getInstance("RSA");  
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
            cipher.init(Cipher.DECRYPT_MODE, privateKey);  
            byte[] output = cipher.doFinal(cipherData);  
            return new String(output);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("No such decryption algorithm");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("Decrypting private key is illegal. Please check");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("Ciphertext length illegal");  
        } catch (BadPaddingException e) {  
            throw new Exception("The ciphertext data is corrupted");  
        }  
    }  
    
    /** 
     * The byte data is converted to a sixteen - ary string
     *  
     * @param data 
     *            Input data 
     * @return Sixteen hexadecimal content
     */  
    public static String byteArrayToString(byte[] data) {  
        StringBuilder stringBuilder = new StringBuilder();  
        for (int i = 0; i < data.length; i++) {  
            // Take the four bit of the byte as an index to get the corresponding sixteen - digit identifier, and pay attention to unsigned right shift
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);  
            // The lower four bits of the byte are taken as indexes to obtain the corresponding sixteen - digit identifier
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);  
            if (i < data.length - 1) {  
                stringBuilder.append(' ');  
            }  
        }  
        return stringBuilder.toString();  
    }  
}
