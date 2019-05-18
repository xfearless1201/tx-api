package com.cn.tianxia.api.utils.dc;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 

/**
 *
 * md5签名
 *
 */
public class MD5 {
	private static final Log LOG = LogFactory.getLog(MD5.class);

	/**
	 * 获取MD5加密后的值
	 *
	 * @param str
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
/*	public static String getMD5ofStr(String str) throws NoSuchAlgorithmException {
		MessageDigest alga = MessageDigest.getInstance("MD5");
		byte[] b = str.getBytes();
		alga.update(b);
		byte[] digesta = alga.digest();
		String result = StrUtil.byte2hex(digesta);
		return result;
	}*/

    /**
     * 签名字符串
     * @param text 需要签名的字符串
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
	protected final static Logger TRACE = LoggerFactory.getLogger("TRACE");

    public static String sign(String text, String key, String input_charset) {
    	text = text + key;
		TRACE.info("签名字符串："+text);
        return DigestUtils.md5Hex(getContentBytes(text, input_charset));
    }

    /**
     * 签名字符串
     * @param text 需要签名的字符串
     * @param sign 签名结果
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static boolean verify(String text, String sign, String key, String input_charset) {
    	text = text + key;
    	String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
    	if(mysign.equals(sign)) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }

    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {

            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

	/**
	 * 默认密钥为空
	 */
	private static String DEFAULT_KEY = "你能破解么,我就不信了,哈哈,傻了吧 .就是要让你傻.";
	/**
	 * 默认字符串为utf-8
	 */
	private static String DEFAULT_CODING = "utf-8";

	/**
	 *
	 * @Title: md5
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param buffer
	 * @param key
	 * @return
	 * @author 刘哈哈
	 * @date Jan 20, 2012
	 */
	public static byte[] md5(byte[] buffer, byte[] key) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(buffer);
			return md5.digest(key);
		} catch (NoSuchAlgorithmException e) {
			LOG.error("md5异常", e);
		}
		return null;
	}

	/**
	 * 采用默认key和默认字符编码
	 *
	 * @Title: md5
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param strSrc
	 * @return
	 * @author 刘哈哈
	 * @date Jan 20, 2012
	 */
	public static String md5_bf(String strSrc) {
		return md5(strSrc, DEFAULT_KEY, DEFAULT_CODING);
	}

	/**
	 *
	 * @Title: md5
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param strSrc
	 * @param key
	 * @return
	 * @author 刘哈哈
	 * @date Jan 20, 2012
	 */
	public static String md5(String strSrc, String key) {
		return md5(strSrc, key, DEFAULT_CODING);
	}

	/**
	 * 签名
	 *
	 * @Title: md5
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param strSrc
	 * @param key
	 * @param encoding
	 * @return
	 * @author 刘哈哈
	 * @date Jan 20, 2012
	 */
	public static String md5(String strSrc, String key, String encoding) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(strSrc.getBytes(encoding));

			StringBuilder result = new StringBuilder();
			byte[] temp = md5.digest(key.getBytes(encoding));
			for (int i = 0; i < temp.length; i++) {
				result.append(Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6));
			}
			return result.toString();

		} catch (NoSuchAlgorithmException e) {
			LOG.error("md5异常", e);

		} catch (Exception e) {
			LOG.error("md5异常", e);
		}
		return null;
	}

	public static String md5(String strSrc){
		char hexDigits[] = { '0', '1', '2', '3', '4',
         '5', '6', '7', '8', '9',
         'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = strSrc.getBytes();
			//获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			//使用指定的字节更新摘要
			mdInst.update(btInput);
			//获得密文
			byte[] md = mdInst.digest();
			//把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
			    byte byte0 = md[i];
			    str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			    str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		/*
		String s="RFT_CUST_PAY_BILL1.01000000900EC201203021549http://61.134.115.206:8017/notify/receiveNotify.dohttp://61.134.115.206:8017/notify/receiveReturn.do易商通商品名称2012030206032010018220000A0B0C0D0E010202";
		// 这里的展示效果是说明采用密钥为1，对123456签名以及采用空密钥，对1234561签名的效果是一样的，都是aaa42296669b958c3cee6c0475c8093e
		System.out.println(md5(s, "0A0B0C0D0E010202"));
		System.out.println(md5("1234561", ""));

		System.out.println(md5("MERCHANTID=105150172990007&POSID=494806654&BRANCHID=150000000&AUTHID=1234567890&CURCODE=01&TXCODE=520105&UName=%C1%F5%D0%CB%BB%AA&IdType=01&IdNumber=430124198406132912&EPAYNO=6227000419910205587&OTHER1=&REMARK1=&REMARK2=&CLIENTIP=&REGINFO=&PROINFO=&PUB32=30819c300d06092a864886f70d0101", ""));
		System.out.println(md5("MERCHANTID=105150172990007&POSID=494806654&BRANCHID=150000000&AUTHID=1234567890&CURCODE=01&TXCODE=520105&UName=%C1%F5%D0%CB%BB%AA&IDTYPE=01&IdNumber=430124198406132912&EPAYNO=6227000419910205587&OTHER1=&REMARK1=&REMARK2=&CLIENTIP=&REGINFO=&PROINFO=&PUB32=30819c300d06092a864886f70d0101", ""));
		*/
		System.out.println(md5("000000"));
		System.out.println(md5_bf("000000"));


        String str = "attach=附加信息&bank_type=ICBC_FP&charset=UTF-8&coupon_fee=0&fee_type=1&mch_id=001075562100008&nonce_str=7e158509216bb7c3aa4cf72165af043a&out_trade_no=1409543900454&pay_result=0&result_code=0&sign_type=MD5&status=0&time_end=20140901115747&total_fee=1&trade_type=pay.weixin.scancode&transaction_id=001075562100008201409010000129&version=1.0";
        System.out.println(MD5.sign(str, "&key=e1cf0ddcf6b47b59c351565d8ad717af", "utf-8"));

       // System.out.println(getMD5ofStr("asdfasdfasdfasdfasdf"));

	}

}
