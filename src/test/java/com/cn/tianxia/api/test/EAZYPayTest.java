package com.cn.tianxia.api.test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.cn.tianxia.api.utils.pay.HttpUtils;

/**
 * @ClassName:PayTest
 * @Description: 支付测试类
 * @Author: Bing
 * @Date: 2019-05-12 18:56
 * @Version:1.0.0
 **/

public class EAZYPayTest {
	
    /*@Test
    public void testCallback() throws Exception{
        Map<String, String> data = new TreeMap<>();
        data.put("OutPaymentNo", "TXKbs2019042719443504304338867");
        data.put("PaymentFee", "1500");
        data.put("PassbackParams", "http://m.99hhvip.com/");        
        data.put("PaymentState", "S");        
        data.put("Sign", "AF32229BB27F72FC52E58F07081B5D68");
        data.put("MerchantId", "18030423114832");
        data.put("PaymentAmount", "50000");
        data.put("Code", "200");
        data.put("PaymentNo", "180000000056193428");
        String notifyUrl = "http://localhost:223/XPJ/V2Notify.do/XPJ/BS";
        String response = HttpUtils.toPostForm(data, notifyUrl);
        
        System.err.println(response);
    }*/
    @Test
    public void testNotify1() throws Exception{
        Map<String, String> data = new HashMap<>();
        data.put("userId", "781053");
        String notifyUrl = "http://localhost:223/XPJ/V2Notify.do/XPJ/TEST";
        String response = HttpUtils.toPostForm(data, notifyUrl);
        System.err.println("测试回调响应："+response);
    }
    @Test
    public void testNotify2() throws Exception{
        Map<String, String> data = new HashMap<>();
        data.put("userId", "781053");
        data.put("cid", "49");
        String notifyUrl = "http://localhost:223/XPJ/Notify/TESTNotify.do";
        String response = HttpUtils.toPostForm(data, notifyUrl);
        System.err.println("测试回调响应："+response);
    }
    @Test
    public void testPay() throws Exception{
        Map<String, String> data = new HashMap<>();
        data.put("acounmt", "100");
        data.put("userId", "781053");
        String payUrl = "http://localhost:223/XPJ/TestPay/scanPay";
        String response = HttpUtils.toPostForm(data, payUrl);
        System.err.println("测试支付响应："+response);
    }
}
