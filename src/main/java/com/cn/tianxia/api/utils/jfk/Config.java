package com.cn.tianxia.api.utils.jfk;

public class Config {
//	//服务端公钥
	public static final String serverPublicKey= "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCabJDz/66tGW6J0SBHI3zTqz+vB7lkBwEcSnnaNJ6mAZ64Garc4Ax9lcFV9aUI3/v/w7LRnhPRnMCHc9HeBFS66jPixlvk3cB/TYsVoxuQInTE/VmQDv+9cRlKYpemULGr6VoeOzAoEHz68g/YUZCjFBxbhTyOKutBoCorsAmQeQIDAQAB";  //服务端公钥
	
	//商户公钥                               
	public static final String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCA+THoVQhxHGGjkhBKPC7CrBxP7gG5o40A5O/RoaS8kukq4imlx4JAZvvHgx4mbCbVI8/QdeunI2pqOlOh3+lSJMvga5oczf6SIYC7IvAB57Nyj26CukD7RVlau0UkIDT1UAJ+F6FF5+dwbdpfUe6vUPhwChJnOkhAjmAcq5s55QIDAQAB"; //自己的公钥
		
	//商户私钥
	public static final String privateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAID5MehVCHEcYaOSEEo8LsKsHE/uAbmjjQDk79GhpLyS6SriKaXHgkBm+8eDHiZsJtUjz9B166cjamo6U6Hf6VIky+BrmhzN/pIhgLsi8AHns3KPboK6QPtFWVq7RSQgNPVQAn4XoUXn53Bt2l9R7q9Q+HAKEmc6SECOYByrmznlAgMBAAECgYBtU8/sg/hN+aMCxxQr+Wwh9UdisfygYnVqZqar6vv8JgSlJ2Xqc36EHUgczZbHNzKLxnmo2ezSl4DjX2H5fBcJJPjTK4PruhDL9JqZ8613OuEIV+YpQyXkqw/f7u9rpby1/4LCV09AS5rgLu0EFeATwA2W+daTHWTDfyBEWk/oWQJBANRrs0ecblB2DXi3PpuQPvBQZgftRTsxT0nFG9wVNU9qWDEcG/qMVRlbrDKtBwU1Na3pgnUFv3mDlvHpB7LitgcCQQCbbttv2E3P11z6FMMkeiJj0KJC9hHCWALVFIy8e1Ztu8SAMUZ26h+tQF6KNdG4RK3iINI/2z4WuDdsGW2/xbWzAkBQD8vBmN8nKmeHcSGCxopCTzs1j6NBXSNyPX1zpPaz2PQmhl6xP0UvypKLgxWbS+PQiatm6eSyKvuwb9E8BiDvAkEAiOo4z6eouzlJBF0/nCUdn/EKzZM2B4xBSnMkmW7HrjelGwrMuDj5mEJIRNStnPSaesNQkMFOSvMQlt87Zp8AswJASJmtjBNAqtXg9XCkQAgb0KqmxTZvUO8A2avtQekZbnlo8iK/QAVCbR/eV/wpLWVQD31INK3YAJwzKJmUwekScA=="; 
										   
	//版本号
	public static final String version="1.0.9";
	
	//商户号
	public static final String merId="201609212111571";
	
	//终端号
	public static final String terId="201609211200003";
	
	
	//订单查询
	public static final String queryUrl="http://www.goldenpay88.com/gateway/queryPaymentRecord";
	//订单支付
	public static final String payUrl="http://www.goldenpay88.com/gateway/orderPay";
	//获取微信或支付二维码地址
	public static final String orderPaySweepCodeUrl="http://www.goldenpay88.comm/gateway/orderPaySweepCode";

	

	/********************测试的*************************/
	
	
    ////////服务端公钥
	public static final String serverPublicKey_test= "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC2u3gv3yywyuaahp1x1zzk30s9vvztX74N/koWZlVt2kqiEk9g7XygnDTaUkRopGWvhyemflyPplUWaVQSjFuaofynYC+mgokNppv12zykYDPCwEuiyBIEkp8e4iAe4x4X7jr2RXB6QrhVDSuZPgzEsP9Q3ZRp2/oErXTpFYdAqwIDAQAB";  //服务端公钥

	//自己的公钥                               
	public static final String publicKey_test="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCA+THoVQhxHGGjkhBKPC7CrBxP7gG5o40A5O/RoaS8kukq4imlx4JAZvvHgx4mbCbVI8/QdeunI2pqOlOh3+lSJMvga5oczf6SIYC7IvAB57Nyj26CukD7RVlau0UkIDT1UAJ+F6FF5+dwbdpfUe6vUPhwChJnOkhAjmAcq5s55QIDAQAB"; //自己的公钥
	
	//自己的私钥
	public static final String privateKey_test="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAID5MehVCHEcYaOSEEo8LsKsHE/uAbmjjQDk79GhpLyS6SriKaXHgkBm+8eDHiZsJtUjz9B166cjamo6U6Hf6VIky+BrmhzN/pIhgLsi8AHns3KPboK6QPtFWVq7RSQgNPVQAn4XoUXn53Bt2l9R7q9Q+HAKEmc6SECOYByrmznlAgMBAAECgYBtU8/sg/hN+aMCxxQr+Wwh9UdisfygYnVqZqar6vv8JgSlJ2Xqc36EHUgczZbHNzKLxnmo2ezSl4DjX2H5fBcJJPjTK4PruhDL9JqZ8613OuEIV+YpQyXkqw/f7u9rpby1/4LCV09AS5rgLu0EFeATwA2W+daTHWTDfyBEWk/oWQJBANRrs0ecblB2DXi3PpuQPvBQZgftRTsxT0nFG9wVNU9qWDEcG/qMVRlbrDKtBwU1Na3pgnUFv3mDlvHpB7LitgcCQQCbbttv2E3P11z6FMMkeiJj0KJC9hHCWALVFIy8e1Ztu8SAMUZ26h+tQF6KNdG4RK3iINI/2z4WuDdsGW2/xbWzAkBQD8vBmN8nKmeHcSGCxopCTzs1j6NBXSNyPX1zpPaz2PQmhl6xP0UvypKLgxWbS+PQiatm6eSyKvuwb9E8BiDvAkEAiOo4z6eouzlJBF0/nCUdn/EKzZM2B4xBSnMkmW7HrjelGwrMuDj5mEJIRNStnPSaesNQkMFOSvMQlt87Zp8AswJASJmtjBNAqtXg9XCkQAgb0KqmxTZvUO8A2avtQekZbnlo8iK/QAVCbR/eV/wpLWVQD31INK3YAJwzKJmUwekScA=="; 
	
	
	
	//版本号
	public static final String version_test="1.0.9";
	
	public static final String payUrl_test="http://192.168.1.191:8388/gateway/orderPay";
	
	public static final String queryUrl_test="http://192.168.1.191:8388/gateway/queryPaymentRecord";
	//
	////商户号
	public static final String merId_test="201610181821442";
		
	//终端号
	public static final String terId_test="201610191904192";
	
	
	


}
