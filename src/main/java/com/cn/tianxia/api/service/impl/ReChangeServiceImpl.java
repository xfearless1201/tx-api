package com.cn.tianxia.api.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cn.tianxia.api.utils.MD5Encoder; 

public class ReChangeServiceImpl {
	private final String input_charset="UTF-8";
	private final String inform_url="110.164.197.122:8480/XPJ";
	private final String return_url="110.164.197.122"; 
	private final String req_referer="110.164.197.122"; 
	
	public String ReChange_Pay(String pay_type,String bank_code,String order_no,String order_amount,String customer_ip,String return_params){ 
		String params="";		
		String merchant_code=""; 
		String key="";
		String url="http://pay.9stpay.com/gstpay/gateway/pay.html?";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date(); 
		String order_time=sdf.format(now);
		if("1".equals(pay_type)){
			merchant_code="1326";
			key="dc127891953a13be6c1c1aa9d7b0d895";
			params="bank_code="+bank_code+"&";	
		}else if("2".equals(pay_type)||"2".equals(pay_type)){
			merchant_code="1333";
			key="20dfee2dcf1a16f825bef3b1281c45e7";
		}else if("3".equals(pay_type)||"3".equals(pay_type)){
			merchant_code="1335";
			key="4700859b79f93de7deffc3e5a67ffccc";
		}else{
			return "error";
		}
		params+="customer_ip="+customer_ip+"&inform_url="+inform_url+"&input_charset="+input_charset+"&merchant_code="+merchant_code+"&order_amount="+order_amount;
		params+="&order_no="+order_no+"&order_time="+order_time+"&pay_type="+pay_type+"&req_referer="+req_referer+"&return_params="+return_params+"&return_url="+return_url+"&key="+key; 
		String sign=MD5Encoder.encode(params, input_charset);
		params+="&sign="+sign; 
		//System.out.println(url+params);
		return url+params; 
	}
	
	public static void main(String[] args) {
		ReChangeServiceImpl r=new ReChangeServiceImpl();
		r.ReChange_Pay("3", "", "tx0123456789008", "0.01", "110.164.197.122","");
	}
}
