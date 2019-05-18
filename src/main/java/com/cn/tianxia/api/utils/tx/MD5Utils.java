package com.cn.tianxia.api.utils.tx;

import java.security.MessageDigest;


public class MD5Utils {

    /**
     * MD5签名
     * 
     * @param paramSrc
     *            the source to be signed
     * @return
     * @throws Exception
     */
    public static String sign(String paramSrc) {
        String sign = md5(paramSrc + "&key=12345");
        System.out.println("MD5签名结果：" + sign);
        return sign;
    }

    /**
     * MD5验签
     * 
     * @param source
     *            签名内容
     * @param sign
     *            签名值
     * @return
     */
    public static boolean verify(String source, String tfbSign) {
        String sign = md5(source + "&key=12345");
        System.out.println("自签结果：" + sign);
        return tfbSign.equals(sign);
    }

    public final static String md5(String paramSrc) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput = paramSrc.getBytes("UTF-8");
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void main(String[] args) {
		//String str = "merId=100000010000013&merOrderNo=15139222661872&notifyUrl=http://join.huohuotuan.cn/index.php/home/Pay/lelhui&orderAmt=1&orderDesc=描述&orderTitle=支付&payPlat=wxpay&version=1.0.0&key=3542a11cfb312de472c70d98e34c4358";
		String  str =    "merId=100000010000013&merOrderNo=15139232137453&notifyUrl=http://join.huohuotuan.cn/index.php/home/Pay/lelhui&orderAmt=1&orderDesc=描述&orderTitle=支付&payPlat=wxpay&version=1.0.0&key=3542a11cfb312de472c70d98e34c4358";
		System.out.println("签名原串:"+str);
		System.out.println("签名值:"+md5(str));
	}
}
