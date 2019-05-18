package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 * @author Vicky
 * @version 1.2.0
 * @ClassName ASPayServiceImpl
 * @Description 安盛支付  渠道：微信扫码、微信H5、支付宝扫码、支付宝H5, 无订单查询接口，直接支付商后台查询
 * @Date 2019/4/26 15 27
 **/
public class ASPayServiceImpl extends PayAbstractBaseService implements PayService{

	private static final Logger logger = LoggerFactory.getLogger(ASPayServiceImpl.class);

	private String merchId;//商户id
	private String alipayUrl;//支付宝支付地址
	private String wxpayUrl;//微信支付地址
	private String notifyUrl;//回调通知地址
	private String secret;//密钥

	private static String ret__success = "SUCCESS";  //成功返回字符串
	private static String ret__failed = "fail";   //失败返回字符串
	private boolean verifySuccess = true;//回调验签默认状态为true

	public ASPayServiceImpl() {
	}

	public ASPayServiceImpl(Map<String, String> data) {
		if(MapUtils.isNotEmpty(data)){
			if(MapUtils.isNotEmpty(data)){
				if(data.containsKey("merchId")){
					this.merchId = data.get("merchId");
				}
				if(data.containsKey("alipayUrl")){
					this.alipayUrl = data.get("alipayUrl");
				}
				if(data.containsKey("wxpayUrl")){
					this.wxpayUrl = data.get("wxpayUrl");
				}
				if(data.containsKey("notifyUrl")){
					this.notifyUrl = data.get("notifyUrl");
				}
				if(data.containsKey("secret")){
					this.secret = data.get("secret");
				}

			}
		}
	}

	/**
	 * 回调  无订单查询接口，直接支付商后台查询
	 * @param request
	 * @param response
	 * @param config
	 * @return
	 */
	@Override
	public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
		this.merchId = config.getString("merchId");
		this.notifyUrl = config.getString("notifyUrl");
		this.secret = config.getString("secret");

		//商户返回信息
		Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
		logger.info("[AS]安盛支付扫码支付回调请求参数：" + JSONObject.fromObject(dataMap));

		if (!MapUtils.isNotEmpty(dataMap)) {
			logger.error("ASNotify获取回调请求参数为空");
			return ret__failed;
		}

		String trade_no = dataMap.get("orderid");//第三方订单号，流水号,支付商不传回
		String order_no = dataMap.get("orderid");//支付订单号
		String amount = dataMap.get("amount");//实际支付金额,以分为单位
		String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

		if (StringUtils.isBlank(trade_no)) {
			logger.info("[AS]安盛支付扫码支付回调请求参数,获取的{} 流水单号为空",trade_no);
			return ret__failed;
		}
		if (StringUtils.isBlank(amount)) {
			logger.info("[AS]安盛支付扫码支付回调请求参数,订单金额为空");
			return ret__failed;
		}

		String trade_status = dataMap.get("opstate");  //第三方支付状态，1 支付成功
		String t_trade_status = "0";//第三方成功状态

		//写入数据库
		ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
		processNotifyVO.setOrder_no(order_no);
		processNotifyVO.setRealAmount(Double.parseDouble(amount));
		processNotifyVO.setIp(ip);
		processNotifyVO.setTrade_no(trade_no);
		processNotifyVO.setTrade_status(trade_status);
		processNotifyVO.setRet__failed(ret__failed);
		processNotifyVO.setRet__success(ret__success);
		processNotifyVO.setInfoMap(JSONObject.fromObject(dataMap).toString());
		processNotifyVO.setT_trade_status(t_trade_status);
		processNotifyVO.setConfig(config);
		processNotifyVO.setPayment("AS");

		//回调验签
		if ("fail".equals(callback(dataMap))) {
			verifySuccess = false;
			logger.info("[AS]安盛支付回调验签失败");
			return ret__failed;
		}
		return processSuccessNotify(processNotifyVO, verifySuccess);
	}

	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		return null;
	}

	/**
	 * 扫码支付
	 * @param payEntity
	 * @return
	 */
	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("[AS]安盛支付扫码支付开始===============START========================");
		try{
			Map<String, String> dataMap = sealRequest(payEntity);
			String responseData;

			if("1".equals(payEntity.getPayCode())){
				responseData = HttpUtils.generatorFormGet(dataMap, wxpayUrl);
			}else {
				responseData = HttpUtils.generatorFormGet(dataMap, alipayUrl);
			}

			logger.info("[AS]安盛支付扫码支付响应信息：{}", responseData);
			if(StringUtils.isBlank(responseData)){
				logger.info("[AS]安盛支付发起HTTP请求无响应");
				return PayResponse.error("[AS]安盛支付扫码支付发起HTTP请求无响应");
			}
			return PayResponse.sm_form(payEntity, responseData, "下单成功");

		}catch (Exception e){
			e.getStackTrace();
			logger.info("[AS]安盛支付扫码支付异常:{}", e.getMessage());
			return PayResponse.error("[AS]安盛支付扫码支付异常");
		}
	}

	/**
	 * 回调验签
	 * @param data
	 * @return
	 */
	@Override
	public String callback(Map<String, String> data) {

		String sign = generatorSign(data,2);
		String sourceSign = data.remove("sign");
		logger.info("[AS]安盛支付扫码支付生成签名串：{}--源签名串：{}", sign, sourceSign);

		if(sign.equalsIgnoreCase(sourceSign)){
			return "success";
		}
		return "fail";
	}

	/**
	 * 参数组装
	 * @param payEntity
	 * @return
	 */
	private Map<String, String> sealRequest(PayEntity payEntity){
		Map<String, String> dataMap = new HashMap<>();
		String amount = new DecimalFormat("0.00").format(payEntity.getAmount());

		dataMap.put("machid", merchId);//商户编号
		dataMap.put("type", payEntity.getPayCode());//类型
		dataMap.put("amount", amount);//金额
		dataMap.put("orderid", payEntity.getOrderNo());//订单号码
		dataMap.put("callbackurl", notifyUrl);//异步通知地址
		//dataMap.put("hrefbackurl","");//同步通知地址
		dataMap.put("ip", payEntity.getIp());//请求 IP
		dataMap.put("text","TOP-UP");//描述
		dataMap.put("sign", generatorSign(dataMap, 1));//MD5 签名

		logger.info("[AS]安盛支付扫码支付请求参数：{}", JSONObject.fromObject(dataMap));
		return dataMap;
	}

	/**
	 *	生成签名
	 * @param data
	 * @param type
	 * @return
	 */
	private String generatorSign(Map<String, String> data, int type){
		StringBuffer sb = new StringBuffer();
		try{
			if(1 == type){//充值
				//machid={}&type={}&amount={}&orderid={}&callbackurl={}key
				//key 为商户密钥，直接连接在尾部。
				sb.append("machid=").append(merchId).append("&");
				sb.append("type=").append(data.get("type")).append("&");
				sb.append("amount=").append(data.get("amount")).append("&");
				sb.append("orderid=").append(data.get("orderid")).append("&");
				sb.append("callbackurl=").append(data.get("callbackurl"));
			}else {//回调
				//参与签名顺序：orderid={}&opstate={}&amount={}key
				sb.append("orderid=").append(data.get("orderid")).append("&");
				sb.append("opstate=").append(data.get("opstate")).append("&");
				sb.append("amount=").append(data.get("amount"));
			}
			sb.append(secret);

			String str = sb.toString();
			logger.info("[AS]安盛支付扫码支付生成待签名串：{}",str);

			String sign = MD5Utils.md5toUpCase_32Bit(str).toLowerCase();
			logger.info("[AS]安盛支付扫码支付生成签名串：{}",sign);

			return sign;
		}catch (Exception e){
			e.getStackTrace();
			logger.info("[AS]安盛支付扫码支付生成签名串：{}",e.getMessage());
			return "[AS]安盛支付扫码支付签名异常";
		}
	}
}
