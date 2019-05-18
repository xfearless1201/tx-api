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
import com.cn.tianxia.api.utils.JSONUtils;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.utils.pay.XmlUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;
/**
 *
 * @author Bing
 */
public class HPAYPayServiceImpl extends PayAbstractBaseService implements PayService{
	private final Logger logger = LoggerFactory.getLogger(HPAYPayServiceImpl.class);
	 /**回调失败响应信息*/
    private static final String ret__failed = "fail";
    /**回调成功响应信息*/
    private static final String ret__success = "ok";
	/**支付地址*/
	private String payUrl;
	/**商户编号*/
	private String payMemberid;
	/**商户接收支付成功数据的地址*/
	private String payNotifyUrl;
	/**订单查询地址*/
	private String searchOrderUrl;
	/**商户密钥*/
	private String md5Key;
	public HPAYPayServiceImpl() {}
	public HPAYPayServiceImpl(Map<String,String> data) {
		if(data!=null){
			if(data.containsKey("payUrl")){
				this.payUrl = data.get("payUrl");
			}
			if(data.containsKey("payMemberid")){
				this.payMemberid = data.get("payMemberid");
			}
			if(data.containsKey("payNotifyUrl")){
				this.payNotifyUrl = data.get("payNotifyUrl");
			}
			if(data.containsKey("searchOrderUrl")){
			    this.searchOrderUrl = data.get("searchOrderUrl");
			}
			if(data.containsKey("md5Key")){
				this.md5Key = data.get("md5Key");
			}
		}
	}
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		return null;
	}

	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("[HPAY]扫码支付开始======================START==================");
		try {
			//封装请求参数
			Map<String, String> data = sealRequest(payEntity,"1");
			logger.info("[HPAY]扫码支付请求参数:"+JSONObject.fromObject(data));
			String resStr = HttpUtils.generatorForm(data, payUrl);
			logger.info("[HPAY]扫码支付响应信息:"+resStr);
			if(StringUtils.isBlank(resStr)){
				logger.info("[HPAY]扫码支付发起HTTP请求无响应结果");
				return PayResponse.error("[HPAY]扫码支付发起HTTP请求无响应结果");
			}
			return PayResponse.sm_form(payEntity, resStr, "下单成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[HPAY]扫码支付生成异常:"+e.getMessage());
			return PayResponse.error("[HPAY]扫码支付异常");
		}
	}

	@Override
	public String callback(Map<String, String> data) {
		return null;
	}
	/**
     * 
     * @Description 封装支付请求参数
     * @param entity
     * @param 
     * @return
     * @throws Exception
     */
	public Map<String, String> sealRequest(PayEntity payEntity,String type) throws Exception{
			DecimalFormat df = new DecimalFormat("0.00");
			StringBuilder body = new StringBuilder();
			body.append("<body>");
			body.append("<MerBillNo>"+payEntity.getOrderNo()+"</MerBillNo>");
			body.append("<NotifyUrl><![CDATA["+payNotifyUrl+"]]></NotifyUrl>");
			body.append("<FailUrl><![CDATA["+payEntity.getRefererUrl()+"]]></FailUrl>");
			body.append("<ReturnUrl><![CDATA["+payEntity.getRefererUrl()+"]]></ReturnUrl>");
			body.append("<PayType>"+payEntity.getPayCode()+"</PayType>");
			body.append("<Amount>"+df.format(payEntity.getAmount())+"</Amount>");
			body.append("<GoodsName>recharge</GoodsName>");
			body.append("</body>");
			
			StringBuilder head = new StringBuilder();
			head.append("<HPay><GateWayReq>");
			head.append("<head>");
			head.append("<Version>v1.0.0</Version>");
			head.append("<MerCode>"+payMemberid+"</MerCode>");
			head.append("<Signature>"+generatorSign(body.toString())+"</Signature>");
			head.append("</head>");
			head.append(body.toString());
			head.append("</GateWayReq></HPay>");
			Map<String, String> data = new HashMap<>();
			data.put("pGateWayReq", head.toString());
			return data;
	}
	/**
     * 
     * @Description 生成签名串
     * @param data
     * @return
	 * @throws Exception
     */
    public String generatorSign(String xmlStr) throws Exception{
        StringBuffer sb = new StringBuffer(xmlStr);
        sb.append(payMemberid).append(md5Key);
        String signStr = sb.toString();
    	//生成待签名串
    	logger.info("[HPAY]支付生成待签名串:{}",signStr);
    	String sign = MD5Utils.md5(signStr.getBytes());
    	logger.info("[HPAY]支付生成加密签名串:{}",sign);
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
            Map<String, String> param = new HashMap<>();
            param.put("OrderId", orderNo);
            param.put("MerCode", payMemberid);
            logger.info("[HPAY]支付回调查询订单{}请求参数:{}", orderNo, JSONObject.fromObject(param));
            String resStr = HttpUtils.get(param, searchOrderUrl);
            logger.info("[HPAY]支付回调查询订单{}响应信息:{}", orderNo, resStr);
            
            if(StringUtils.isBlank(resStr)) {
                logger.info("[HPAY]支付回调订单查询发起HTTP请求无响应,订单号{}",orderNo);
                return false;
            }
            JSONObject resJson = JSONObject.fromObject(resStr);
            if(!"Y".equals(resJson.getString("Status"))) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HPAY]支付回调订单{}查询异常{}", orderNo, e.getMessage());
            return false;
        }
        
    }
    /**
     * 回调验签
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param data
     * @return
     */
    private boolean verifyCallback(String xmlStr, String sourceSign) {
        try {
            String sign = generatorSign(xmlStr);
            logger.info("[HPAY]支付回调生成签名串"+sign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HPAY]支付回调生成签名串异常"+e.getMessage());
            return false;
        }
    }
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        String reqXml = ParamsUtils.getHPAYNotifyParams(request);
        logger.info("[HPAY]支付回调请求参数："+reqXml);
        if (StringUtils.isBlank(reqXml)) {
            logger.error("HPAYNotify获取回调请求参数为空");
            return ret__failed;
        }
        String bodyXml = XmlUtils.getXmlStr(reqXml, "<body>", "</GateWayReq>");
        String headXml = XmlUtils.getXmlStr(reqXml, "<head>", "<body>");
        JSONObject headJson = XmlUtils.xml2Json(headXml);
        JSONObject bodyJson = XmlUtils.xml2Json(bodyXml);
        if(!JSONUtils.compare(headJson, "RspCode", "0")) {
            logger.info("[HPAY]支付回调失败："+headJson.getString("RspMsg"));
            return ret__failed;
        }
        //参数验签
        this.md5Key = config.getString("md5Key");//从配置中获取
        this.payMemberid = config.getString("payMemberid");
        this.searchOrderUrl = config.getString("searchOrderUrl");
        
        String order_amount = bodyJson.getString("Amount");//单位：元
        if(StringUtils.isBlank(order_amount)){
            logger.info("HPAYNotify获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount);
        String order_no = bodyJson.getString("MerBillNo");// 平台订单号
        String trade_no = bodyJson.getString("OrderId");// 第三方订单号
        String trade_status = bodyJson.getString("Status");//订单状态:Y为成功
        String t_trade_status = "Y";// 表示成功状态
        
       /* boolean result = serchOrder(order_no);
        if(!result)  {
            logger.info("[HPAY]支付回调订单{}查询失败",order_no);
            return ret__failed;
        }*/
        
        boolean verifyRequest = verifyCallback(bodyXml, headJson.getString("Signature"));
        
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
        processNotifyVO.setInfoMap(bodyJson.toString());    //回调参数
        processNotifyVO.setPayment("HPAY");
        processNotifyVO.setConfig(config);
        return super.processSuccessNotify(processNotifyVO,verifyRequest);
    }
}
