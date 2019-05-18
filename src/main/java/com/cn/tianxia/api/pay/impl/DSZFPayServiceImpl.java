package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
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
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;

/**
 * DS 鼎盛支付
 * @author TX
 */
public class DSZFPayServiceImpl extends PayAbstractBaseService implements PayService{
	private final static Logger logger = LoggerFactory.getLogger(DSZFPayServiceImpl.class);
	/**回调失败响应信息*/
    private static final String ret__failed = "fail";
    /**回调成功响应信息*/
    private static final String ret__success = "OK";
	/**商户号*/
	private String merchId;
	/**秘钥*/
	private String secret;
	/**回调地址*/
	private String notifyUrl;
	/**支付地址*/
	private String payUrl;
	/**订单查询地址*/
	private String queryOrderUrl;
	
	public DSZFPayServiceImpl() {}

    public DSZFPayServiceImpl(Map<String,String> map){
		if(map.containsKey("merchId")){
			this.merchId = map.get("merchId");
		}
		if(map.containsKey("secret")){
			this.secret = map.get("secret");
		}
		if(map.containsKey("notifyUrl")){
			this.notifyUrl = map.get("notifyUrl");
		}
		if(map.containsKey("payUrl")){
			this.payUrl = map.get("payUrl");
		}
		if(map.containsKey("queryOrderUrl")){
		    this.queryOrderUrl = map.get("queryOrderUrl");
		}
	}
	
	/**
	 * 银联
	 */
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		return null;
	}

	/**
	 * 扫码
	 */
	@Override
	public JSONObject smPay(PayEntity payEntity) {
		try{
		    Map<String,String> data = sealRequest(payEntity);
			logger.info("[DSZF]鼎盛扫码支付请求参数：{}", JSONObject.fromObject(data));
			String resStr = HttpUtils.generatorForm(data, payUrl);
			logger.info("[DSZF]鼎盛扫码支付响应信息：{}", resStr);
			return PayResponse.sm_form(payEntity, resStr, "下单成功");
		}catch(Exception e){
			e.printStackTrace();
			logger.info("[DSZF]鼎盛扫码支付异常:{}",e.getMessage());
			return PayResponse.error("[DSZF]鼎盛扫码支付异常");
		}
	}

	/**
	 * 回调
	 */
	@Override
	public String callback(Map<String, String> data) {
		return null;
	}
	
	/**
	 * 参数组装
	 * @param payEntity
	 * type = 1 银联支付
	 * @throws Exception 
	 */
	private Map<String,String> sealRequest(PayEntity payEntity) throws Exception{
		Map<String,String> map = new HashMap<>();
		long time = System.currentTimeMillis()/1000;
		String amount = new DecimalFormat("0.00").format(payEntity.getAmount());
		map.put("merchant", merchId);//商户号
		map.put("qrtype", payEntity.getPayCode());//支付类型
		map.put("customno", payEntity.getOrderNo());//订单号
		map.put("money", amount);//金额
		map.put("sendtime", String.valueOf(time));//发送时间
		map.put("notifyurl", notifyUrl);//回调地址
		map.put("backurl", payEntity.getRefererUrl());
		map.put("risklevel", "");//风险级别
		map.put("sign", generatorSign(map, "0"));//风险级别
		return map;
	}
	
	/**
	 * 
	 * @param map
	 * @throws Exception 
	 */
	private String generatorSign(Map<String,String> data, String type) throws Exception{
		StringBuilder sb = new StringBuilder();
		if("0".equals(type)) {
		    sb.append("merchant=").append(data.get("merchant")).append("&");
	        sb.append("qrtype=").append(data.get("qrtype")).append("&");
	        sb.append("customno=").append(data.get("customno")).append("&");
	        sb.append("money=").append(data.get("money")).append("&");
	        sb.append("sendtime=").append(data.get("sendtime")).append("&");
	        sb.append("notifyurl=").append(data.get("notifyurl")).append("&");
	        sb.append("backurl=").append(data.get("backurl")).append("&");
	        sb.append("risklevel=").append(data.get("risklevel"));
		}
		if("1".equals(type)) {
            sb.append("merchant=").append(data.get("merchant")).append("&");
            sb.append("customno=").append(data.get("customno")).append("&");
            sb.append("sendtime=").append(data.get("sendtime"));
        }
		if("2".equals(type)) {
            sb.append("merchant=").append(data.get("merchant")).append("&");
            sb.append("qrtype=").append(data.get("qrtype")).append("&");
            sb.append("customno=").append(data.get("customno")).append("&");
            sb.append("sendtime=").append(data.get("sendtime")).append("&");
            sb.append("orderno=").append(data.get("orderno")).append("&");
            sb.append("money=").append(data.get("money")).append("&");
            sb.append("paytime=").append(data.get("paytime")).append("&");
            sb.append("state=").append(data.get("state"));
        }
		sb.append(secret);
		String signStr = sb.toString();
		logger.info("[DSZF]鼎盛扫码支付生成待签名串：{}",signStr);
		String sign = MD5Utils.md5(signStr.getBytes());
		logger.info("[DSZF]鼎盛扫码支付生成签名串：{}",sign);
		return sign;
	}
	
	/**
     * 订单查询接口
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param orderNo
     * @return
     */
    public boolean serchOrder(String orderNo) {
        try {
            long time = System.currentTimeMillis()/1000;
            Map<String, String> param = new HashMap<>();
            param.put("merchant", merchId);//商户号
            param.put("customno", orderNo);//商户订单号
            param.put("sendtime", String.valueOf(time));//发送时间 10位Unix时间戳
            param.put("sign", generatorSign(param, "1"));
            logger.info("[DSZF]鼎盛扫码支付回调查询订单{}请求参数：{}", orderNo, JSONObject.fromObject(param));
            String resStr = HttpUtils.toPostForm(param, queryOrderUrl);
            logger.info("[DSZF]鼎盛扫码支付回调查询订单{}响应信息：{}", orderNo, resStr);
            if(StringUtils.isBlank(resStr)) {
                logger.info("[DSZF]鼎盛扫码支付回调查询订单发起HTTP请求无响应,订单号{}",orderNo);
                return false;
            }
            JSONObject resJson = JSONObject.fromObject(resStr);
            if(!"0".equals(resJson.getString("errCode"))) {
                return false;
            }
            resJson = resJson.getJSONObject("data");
            if(!"1".equals(resJson.getString("state"))) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[DSZF]鼎盛扫码支付回调查询订单{}异常{}", orderNo, e.getMessage());
            return false;
        }
        
    }
	 /**
     * 回调验签
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param data
     * @return
     */
    private boolean verifyCallback(Map<String, String> data) {
        try {
            String sourceSign = data.get("sign");
            String sign = generatorSign(data, "2");
            logger.info("[DSZF]鼎盛扫码支付生成签名串：{}--源签名串：{}", sign, sourceSign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[DSZF]鼎盛扫码支付回调生成签名串异常{}",e.getMessage());
            return false;
        }
    }
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        Map<String,String> infoMap = ParamsUtils.getNotifyParams(request);  //获取回调请求参数
        logger.info("[DSZF]鼎盛扫码支付回调请求参数："+JSONObject.fromObject(infoMap));
        if (MapUtils.isEmpty(infoMap)) {
            logger.error("DSZFNotify获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.secret = config.getString("secret");//从配置中获取
        this.merchId = config.getString("merchId");//从配置中获取
        this.queryOrderUrl = config.getString("queryOrderUrl");//从配置中获取
        
        String order_amount = infoMap.get("money");//单位：元
        if(StringUtils.isBlank(order_amount)){
            logger.info("DSZFNotify获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount);
        String order_no = infoMap.get("customno");// 平台订单号
        String trade_no = infoMap.get("orderno");// 第三方订单号
        String trade_status = infoMap.get("state");//订单状态:00为成功
        String t_trade_status = "1";// 表示成功状态
        
        /**订单查询*/
        if(!serchOrder(order_no)) {
            logger.info("[DSZF]鼎盛扫码支付回调查询订单{}失败", order_no);
            return ret__failed;
        }
        /**回调验签*/
        boolean verifyRequest = verifyCallback(infoMap);
        
        String ip = StringUtils.isBlank(IPTools.getIp(request))?"127.0.0.1":IPTools.getIp(request);
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setRet__success(ret__success);    //成功返回
        processNotifyVO.setRet__failed(ret__failed);      //失败返回
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);    //支付状态
        processNotifyVO.setT_trade_status(t_trade_status);     //第三方成功状态
        processNotifyVO.setRealAmount(realAmount);
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());    //回调参数
        processNotifyVO.setPayment("DSZF");
        processNotifyVO.setConfig(config);
        return super.processSuccessNotify(processNotifyVO,verifyRequest);
    }
}
