package com.cn.tianxia.api.utils.pay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * 
 * @ClassName RsaUtil
 * @Description RSA加密
 * @author Hardy
 * @Date 2018年10月4日 下午8:37:04
 * @version 1.0.0
 */
public class RsaUtils {

    private static final String ALG_RSA = "RSA";
    private static final String ALG_DSA = "DSA";

    public static String sign(String data, String privateKey) {
        String sign = null;
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(hexString2ByteArr(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(keySpec);
            Signature si = Signature.getInstance("SHA1WithRSA");
            si.initSign(priKey);
            si.update(data.getBytes("UTF-8"));
            byte[] dataSign = si.sign();
            sign = byteArr2HexString(dataSign);
        } catch (Exception e) {
            e.printStackTrace();
            sign = null;
        }
        return sign;
    }

    public static boolean verify(String data, String sign, String publicKey) {
        boolean succ = false;
        try {
            Signature verf = Signature.getInstance("SHA1WithRSA");
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            PublicKey puk = keyFac.generatePublic(new X509EncodedKeySpec(hexString2ByteArr(publicKey)));
            verf.initVerify(puk);
            verf.update(data.getBytes("UTF-8"));
            succ = verf.verify(hexString2ByteArr(sign));
        } catch (Exception e) {
            e.printStackTrace();
            succ = false;
        }
        return succ;
    }

    public static String encrypt(String data, String publicKey) {
        String encryptData = null;
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFac.generatePublic(new X509EncodedKeySpec(hexString2ByteArr(publicKey)));

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] result = cipher(data.getBytes("UTF-8"), cipher, getBlockSize(pubKey) - 11);
            encryptData = byteArr2HexString(result);
        } catch (Exception e) {
            e.printStackTrace();
            encryptData = null;
        }
        return encryptData;
    }

    public static String decrypt(String encryptedData, String privateKey) {
        String decryptData = null;
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(hexString2ByteArr(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] result = cipher(hexString2ByteArr(encryptedData), cipher, getBlockSize(priKey));
            decryptData = new String(result, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            decryptData = null;
        }
        return decryptData;
    }

    public static String byteArr2HexString(byte[] bytearr) {
        if (bytearr == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();

        for (int k = 0; k < bytearr.length; k++) {
            if ((bytearr[k] & 0xFF) < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(bytearr[k] & 0xFF, 16));
        }
        return sb.toString();
    }

    public static byte[] hexString2ByteArr(String hexString) {
        if ((hexString == null) || (hexString.length() % 2 != 0)) {
            return new byte[0];
        }

        byte[] dest = new byte[hexString.length() / 2];

        for (int i = 0; i < dest.length; i++) {
            String val = hexString.substring(2 * i, 2 * i + 2);
            dest[i] = ((byte) Integer.parseInt(val, 16));
        }
        return dest;
    }

    private static byte[] cipher(byte[] data, Cipher cipher, int blockSize) throws Exception {
        final ByteArrayInputStream in = new ByteArrayInputStream(data);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] cache = new byte[blockSize];
        while (true) {
            final int r = in.read(cache);
            if (r < 0) {
                break;
            }
            final byte[] temp = cipher.doFinal(cache, 0, r);
            out.write(temp, 0, temp.length);
        }
        return out.toByteArray();
    }

    private static int getBlockSize(final Key key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final String alg = key.getAlgorithm();
        final KeyFactory keyFactory = KeyFactory.getInstance(alg);
        if (key instanceof PublicKey) {
            final BigInteger prime;
            if (ALG_RSA.equals(alg)) {
                prime = keyFactory.getKeySpec(key, RSAPublicKeySpec.class).getModulus();
            } else if (ALG_DSA.equals(alg)) {
                prime = keyFactory.getKeySpec(key, DSAPublicKeySpec.class).getP();
            } else {
                throw new NoSuchAlgorithmException("不支持的解密算法：" + alg);
            }
            return prime.toString(2).length() / 8;
        } else if (key instanceof PrivateKey) {
            final BigInteger prime;
            if (ALG_RSA.equals(alg)) {
                prime = keyFactory.getKeySpec(key, RSAPrivateKeySpec.class).getModulus();
            } else if (ALG_DSA.equals(alg)) {
                prime = keyFactory.getKeySpec(key, DSAPrivateKeySpec.class).getP();
            } else {
                throw new NoSuchAlgorithmException("不支持的解密算法：" + alg);
            }
            return prime.toString(2).length() / 8;
        } else {
            throw new RuntimeException("不支持的密钥类型：" + key.getClass());
        }
    }
    
    public static void main(String[] args) {
        String pubKeyOne = "30820122300d06092a864886f70d01010105000382010f003082010a0282010100c5c74f574c8c23a46aca8278bf0ce05369fbec7de376304c4fdc59cd2a658433c79192de40e52c392ff084d7cffe27730fdfc9ebd4e829c160a1c953555b04998fa685e9c553d6fb19645d993fe469cb1b7bb2b6bd7b052ca10447eb33801af4c3195e2afaf3e4fdfc87ec0bc4206b7b28c148e783a9192f73114bbb88d998e32f00e6350c966451c7cbbfb51923da9bf2ba8d3114a1329c9cf440b381fa6048e8caf40172eec7ae1887313497d9d4b867ac251d08027977ba0eb5987ce5c0107490cfe032798581a2ee1a24b1e6f9068080bd76b7023c14a10dd6b79d504c5377c066a14f766ceeda8b4244ec250dbfee0f062019651800af804e3a5672ac650203010001";
        
        String pubKeyTwo = "30820122300d06092a864886f70d01010105000382010f003082010a0282010100d676f80a8c94a48950ce318a41d7fb86cbc9199f095a8863c790bcfcf4f08890e3d66d306a8f442b299692dd244a6c9cbc510f75f94b4686067f2ca867539a43fa0247930c84cf770d1e6308d59a97745eae0cdb768a066dda187be225499e1314ea60236b3f24f9e2faa6dd581c56a0b590fcc9b55475a365f945b7bbd5da8a66e3dc408ad9bbc371b1e75a928ee23fe8e841e49ba1fd7b2305f5b63144a656d166c9eefe4b3dd0f478a1098ffc06907cab137a7c8a93908d09190f8fbfe43857a02f0d07ad04a458304f4a3b6fed3a43b81697214c33a0c33188af206daefa0c961d4fd914e257541498e9f1f4594b7d46f1289a41f281bdc14f08bbab30390203010001";
    
        String priKey="308204bd020100300d06092a864886f70d0101010500048204a7308204a30201000282010100c5c74f574c8c23a46aca8278bf0ce05369fbec7de376304c4fdc59cd2a658433c79192de40e52c392ff084d7cffe27730fdfc9ebd4e829c160a1c953555b04998fa685e9c553d6fb19645d993fe469cb1b7bb2b6bd7b052ca10447eb33801af4c3195e2afaf3e4fdfc87ec0bc4206b7b28c148e783a9192f73114bbb88d998e32f00e6350c966451c7cbbfb51923da9bf2ba8d3114a1329c9cf440b381fa6048e8caf40172eec7ae1887313497d9d4b867ac251d08027977ba0eb5987ce5c0107490cfe032798581a2ee1a24b1e6f9068080bd76b7023c14a10dd6b79d504c5377c066a14f766ceeda8b4244ec250dbfee0f062019651800af804e3a5672ac650203010001028201001c7c334a30853290f8ac881419f821cb01c5bdeef7b6cb0802ac8ae688c7ac3dc4ed8b88a5321ca773e7214aeff2fbfd8f78bcdd5181ca99c67026cdf0ca362463d329105d5250f1435bf83b1d3fa9990e3147b1182342c085c45713620c7e5568501c06f6b3eff440cdd6ca6f09f850f9b2b4ec5b431c947fef2ac82cd2a2393d1fd3831c71506c3ac140abef6f5681ba90a73ab076b7f8633b9213ee83acc342d9556dd1d62099ad44a0fcfdf2c51b5d05951a5f5c7206ec6209e171d92cea3f94bc705b7a781347dbde0a0a56eb91d59684b7304570f288fe01e77efdea8983515e02aa1296cea993f7b576b2f6d352b38275d8e9847cff9027b923975a1502818100f968c3bdc11358b36ae5ddb9440f572573090f9af70c314cb426f408247fd494e725015346f029e47f847514bf9c6b50afa9e06d45c8076352983b365df9652bb1703fda38084a77a866f6325f95025e4ee68f26cf69af2fed7393893d5d20390a3c26be3de4bb227e00fdf70c8d5260c7f443e2880720ffa6d7e89e3f4dbb5702818100cb01447a635715c640fa13a35bebc06041d88d1731f763e55edcf0afcc0963218f8f1aa73fa8bcdfc271ae99ddb992926e82d227532bae556e30a20c885adac2935ddb4f6602cdcce02969458c170edb269bc5d915edf33936bf5e127144f4d8562b10aae5d0b3791685bad04339bddbebb07713bc81eac90db9b15334d43ca302818000ae63927a1b866b7f083fb1c8d8cde1214e4e079b532233566ea0ce00e3847e1f9f7f406dfa2bf31ed20acbd2b627a8e1c1f205fa61d07b59f7667a0089b4d808b508c79ccd90fe80ac3f51e5e6ef49b8a7728ea59461511d31ea9e3e57a9c5de1c49f2cd1279dc5f41bb6fd84dee82ab2924a252c2ca394888606f4636fd23028181009c57e08e457da736658a739f6746bc8525910ab06779f90488b7ade22648c080f3582a15ceb03cae33c96bc7f27c5e06ca7794345718980e116c011197de29069b8a384200f57ddc2073a430bcc0715f946f79c343e15b659fe56824d9f95a7c56789a4093f7d49118286c04c3117048344f5695eba33ed812eb054768b35ff302818062794e51803cf0a4bf91fb6c5f4306d8a34c451946024065082fa5f8bc16508c770dde6c1009972dcddbd18f14b66c3f0da8b19834a626c318d443afc16fe06059f06f980ee2a521e634bd52258202b296d2cfa3733fa5b2dbd95a91f59417ad94def379092ef6dfeb68fbc23aafb1055eea8067c48e7bd133f94a12432f27b3";
        
        String pubKey = "30820122300d06092a864886f70d01010105000382010f003082010a0282010100c5c74f574c8c23a46aca8278bf0ce05369fbec7de376304c4fdc59cd2a658433c79192de40e52c392ff084d7cffe27730fdfc9ebd4e829c160a1c953555b04998fa685e9c553d6fb19645d993fe469cb1b7bb2b6bd7b052ca10447eb33801af4c3195e2afaf3e4fdfc87ec0bc4206b7b28c148e783a9192f73114bbb88d998e32f00e6350c966451c7cbbfb51923da9bf2ba8d3114a1329c9cf440b381fa6048e8caf40172eec7ae1887313497d9d4b867ac251d08027977ba0eb5987ce5c0107490cfe032798581a2ee1a24b1e6f9068080bd76b7023c14a10dd6b79d504c5377c066a14f766ceeda8b4244ec250dbfee0f062019651800af804e3a5672ac650203010001";
        
        String src = "我是中国人";
        
        String sign = sign(src, priKey);
        
        boolean verify = verify(src, sign, pubKey);
        
        System.err.println(verify);
    
    }
}
