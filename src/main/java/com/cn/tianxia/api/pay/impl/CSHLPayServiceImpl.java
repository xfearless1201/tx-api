package com.cn.tianxia.api.pay.impl;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;

import net.sf.json.JSONObject;

/**
 * 城市互联支付
 * 
 * @author hb
 * @date 2018-06-11
 */
public class CSHLPayServiceImpl implements PayService {
	private final static Logger logger = LoggerFactory.getLogger(CSHLPayServiceImpl.class);

	private String payUrl;//支付地址
	private String md5Key ;//= "003f040d3512aa0c88a94e580218e19c";// 密钥
	private String merchantNo ;//= "PC00000022X";// 商户号
	private String notifyUrl ;//= "http://www.baidu.com";// 异步通知地址

	public CSHLPayServiceImpl() {
	}

	public CSHLPayServiceImpl(Map<String, String> map) {
		if(map!= null && !map.isEmpty()){
			if(map.containsKey("merchantNo")){
				this.merchantNo = map.get("merchantNo");
			}
			if(map.containsKey("payUrl")){
				this.payUrl = map.get("payUrl");
			}
			if(map.containsKey("notifyUrl")){
				this.notifyUrl = map.get("notifyUrl");
			}
			if(map.containsKey("md5Key")){
				this.md5Key = map.get("md5Key");
			}
		}
	}

	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	// {"retCode":"00000","retMsg":"OK","merchantNo":"PC00000022X","orderNo":"20180611162638370","amount":"12300",
	// "qrCode":"http://pay.01.luckypaying.com/t_gobank.do?h5Param=MjAxODA2MTEwMDYwMTMwMDAwMzI5NTc1Mw==","sign":"8fd8a9df6ebf52710b844cb6915b8de2"}
	@Override
	public JSONObject smPay(PayEntity payEntity) {

		String p2_OrderNo = payEntity.getOrderNo();// 订单号
		double amount = payEntity.getAmount();// 金额
		String p3_Amount = new DecimalFormat("0").format(amount * 100);
		String p7_ReturnUrl = payEntity.getRefererUrl();//页面回显地址


		Map<String, String> params = new TreeMap<>();
		params.put("p1_MerchantNo", this.merchantNo);
		params.put("p2_OrderNo", p2_OrderNo);
		params.put("p3_Amount", p3_Amount);
		params.put("p4_Cur", "1");//币种
		params.put("p5_ProductName", "top_up");
		params.put("p6_NotifyUrl", this.notifyUrl);
		params.put("p7_ReturnUrl", p7_ReturnUrl);

		StringBuilder sb = new StringBuilder();
		for (String key : params.keySet()) {
			String value = params.get(key);
			if (StringUtils.isBlank(value)) {
				continue;
			}
			sb.append(value);
		}
		sb.append(this.md5Key);

		String sign = null;
		try {
			sign = MD5Utils.md5toUpCase_32Bit(sb.toString());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		params.put("sign", sign.toLowerCase());
		logger.info("支付报文:" + params);

		payUrl = payUrl + payEntity.getPayCode() + ".do";

		String responseStr = null;
		try {
			responseStr = HttpUtils.toPostForm(params,payUrl);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[CSHL]城市互联支付请求第三方服务器异常:{}",e.getMessage());
			return PayResponse.error("[CSHL]城市互联支付请求第三方服务器异常:"+ e.getMessage());
		}
		JSONObject responseJson = JSONObject.fromObject(responseStr);
		logger.info("响应:" + responseJson);
		
		if("00000".equals(responseJson.getString("retCode"))) {//支付成功
			return PayResponse.sm_link(payEntity,responseJson.getString("qrCode"),"下单成功");
		}
		
		return PayResponse.error(responseJson.getString("retMsg"));
	}

	/**
	 * 回调验签
	 * @param infoMap
	 * @return
	 */
    @Override
    public String callback(Map<String, String> data) {
        String sourceSign = data.remove("sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[CSHL]城市互联支付回调验签失败：回调签名为空！");
            return "fail";
        }
        if(verifyCallback(sourceSign,data))
            return "success";
        return "fail";
    }

    private boolean verifyCallback(String sign,Map<String,String> data) {

        String localSign;
        try {

            JSONObject json = new JSONObject();
            json.put("merchantNo",data.get("merchantNo"));
            json.put("orderNo",data.get("orderNo"));
            json.put("officialOrderNo",data.get("officialOrderNo"));
            json.put("trxorderNo",data.get("trxorderNo"));
            json.put("amount",Integer.parseInt(data.get("amount")));
            json.put("status",data.get("status"));
            json.put("finalTime",data.get("finalTime"));
            StringBuffer sb = new StringBuffer();
            sb.append(json.toString()).append(md5Key);
            String signStr = sb.toString();
			logger.info("[CSHL]城市互联支付回调生成待签名串:{}",signStr);
			localSign = MD5Utils.md5(signStr.getBytes());
			logger.info("[CSHL]城市互联支付回调生成加密签名串:{}",localSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[CSHL]城市互联支付生成支付签名串异常:"+ e.getMessage());
            return false;
        }
        return sign.equalsIgnoreCase(localSign);
    }

}
