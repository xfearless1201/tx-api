package com.cn.tianxia.api.utils.rczf;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * verify signature
 * @author kcw
 */

public class SignUtils {
    /** 
     * signature algorithm
     */  
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";  
  
    /** 
    * RSA Signature
    * @param content: Signature data to be signed 
    * @param privateKey: Merchant private key 
    * @param encode: Character set coding
    * @return Signature value
    */  
    public static String Signaturer(String content, String privateKey)  
    {  
        try   
        {  
            PKCS8EncodedKeySpec priPKCS8    = new PKCS8EncodedKeySpec( Base64Utils.decode(privateKey) );   
            KeyFactory keyf                 = KeyFactory.getInstance("RSA");  
            PrivateKey priKey               = keyf.generatePrivate(priPKCS8);  
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);  
            signature.initSign(priKey);  
            signature.update( content.getBytes());  
  
            byte[] signed = signature.sign();  
            return Base64Utils.encode(signed);  
        }  
        catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
          
        return null;  
    }  
    
    /** 
     * RSA validate signature
     * @param content: Signature data to be signed 
     * @param sign: Signature value
     * @param publicKey: merchant's public key
     * @param encode: Character set coding
     * @return boolean
     */  
     public static boolean validataSign(String content, String sign, String publicKey)  
     {  
         try   
         {  
             KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
             byte[] encodedKey = Base64Utils.decode(publicKey);  
             PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));  
             java.security.Signature signature = java.security.Signature  
             .getInstance(SIGN_ALGORITHMS);  
             signature.initVerify(pubKey);  
             signature.update( content.getBytes() );  
             boolean bverify = signature.verify( Base64Utils.decode(sign) );  
             return bverify;  
         }   
         catch (Exception e)   
         {  
             e.printStackTrace();  
         }  
           
         return false;  
     }  
}
