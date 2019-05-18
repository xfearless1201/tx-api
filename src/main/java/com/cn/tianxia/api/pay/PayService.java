package com.cn.tianxia.api.pay;

import java.util.Map;

import com.cn.tianxia.api.common.PayEntity;

import net.sf.json.JSONObject;

/**
 * <p>
 * Title: PayService
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:Copyright (c) 2017
 * </p>
 * 
 * @author zouwei
 * @date 2017年12月6日
 */
public interface PayService {

	/**
	 * 网银支付
	 * 
	 * @param payEntity
	 * @return
	 */
	JSONObject wyPay(PayEntity payEntity);

	/**
	 * 扫码支付
	 * 
	 * @param payEntity
	 * @return
	 */
	JSONObject smPay(PayEntity payEntity);
	
	/**
	 * 
	 * @Description 验签
	 * @param data
	 * @return
	 */
	String callback(Map<String,String> data);
}
