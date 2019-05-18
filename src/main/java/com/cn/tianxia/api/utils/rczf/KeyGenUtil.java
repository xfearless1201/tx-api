package com.cn.tianxia.api.utils.rczf;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
/**
 * RSA key pair generation
 * @author kcw
 */
public class KeyGenUtil {
	 /** 
     * Randomly generated key pairs
     */  
    public static void genKeyPair() {  
        // KeyPairGenerator is used to generate the public and private key pairs, generating objects based on the RSA algorithm
        KeyPairGenerator keyPairGen = null;  
        try {  
            keyPairGen = KeyPairGenerator.getInstance("RSA");  
        } catch (NoSuchAlgorithmException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        // Initializes the key pair generator with a key size of 96-1024 bits
        keyPairGen.initialize(1024,new SecureRandom());  
        // Create a key pair and save it in keyPair
        KeyPair keyPair = keyPairGen.generateKeyPair();  
        // Get the private key
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
        // Get public key  
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
        try {  
            // Get the public key string
            String publicKeyString = Base64.encode(publicKey.getEncoded());  
            // Get the private key string
            String privateKeyString = Base64.encode(privateKey.getEncoded());  
            System.out.println("publicKeyString:"+publicKeyString);
            System.out.println("privateKeyString:"+privateKeyString);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    
}
