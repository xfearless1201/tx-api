package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.domain.txdata.v2.XbbzfPaymentDao;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.project.v2.XbbzfPaymentEntity;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.JSONUtils;
import com.cn.tianxia.api.utils.SpringContextUtils;
import com.cn.tianxia.api.utils.jf.DESUtil;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.utils.pay.RandomUtils;
import com.cn.tianxia.api.utils.tx.MD5Utils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;

import net.sf.json.JSONObject;


/**
 * 
 * @ClassName XBBZFPayServiceImpl
 * @Description 新币宝虚拟币 支付
 * @author Hardy
 * @Date 2019年1月15日 下午12:21:32
 * @version 1.0.0
 */
public class XBBZFPayServiceImpl extends PayAbstractBaseService implements PayService{
	private final static Logger logger = LoggerFactory.getLogger(XBBZFPayServiceImpl.class);
	/**回调失败响应信息*/
    private static final String ret__failed = "fail";
    /**回调成功响应信息*/
    private static final String ret__success = "{\"Success\":true,\"Code\":1,\"Message\":\"SUCCESS\"}";// 成功返回
	/**商家编号**/
	private String MerCode;
	/**回调地址**/
	private String notify_url;
	/**获取地址**/
	private String getAddress;
	/**创建会员**/
	private String addUserUrl;
	/**login**/
	private String login;
	/**订单查询**/
	private String orderDetail;
	/**DES key**/
	private String DESKey;
	/**keyA**/
	private String keyA;
    /**keyB**/
	private String keyB;
	/**keyC**/
	private String keyC;
	/****/
	private String CoinCode;

	public XBBZFPayServiceImpl() {}
	public XBBZFPayServiceImpl(Map<String,String> map){
		if(map.containsKey("MerCode")){
			this.MerCode = map.get("MerCode");
		}
		if(map.containsKey("notify_url")){
			this.notify_url = map.get("notify_url");
		}
		if(map.containsKey("addUserUrl")){
			this.addUserUrl = map.get("addUserUrl");
		}
		if(map.containsKey("getAddress")){
			this.getAddress = map.get("getAddress");
		}
		if(map.containsKey("login")){
			this.login = map.get("login");
		}
		if(map.containsKey("orderDetail")){
		    this.orderDetail = map.get("orderDetail");
		}
		if(map.containsKey("DESKey")){
			this.DESKey = map.get("DESKey");
		}
		if(map.containsKey("keyA")){
			this.keyA = map.get("keyA");
		}
		if(map.containsKey("keyB")){
			this.keyB = map.get("keyB");
		}
		if(map.containsKey("keyC")){
			this.keyC = map.get("keyC");
		}
		if(map.containsKey("CoinCode")){
			this.CoinCode = map.get("CoinCode");
		}
	}
	
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		return null;
	}

	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("新币宝支付开始扫描支付...............");
		
		String uid = payEntity.getuId();
		
		//查询会员是否存在
		if(checkUser(uid)){
			//创建会员
			JSONObject regist = registUser(payEntity);
			if (null != regist) {
				return regist;
			}
		}

		//开始支付
		Map<String,String> payMap = sealRequest(payEntity, 2);
		String payResponse;
		try {
			payResponse = HttpUtils.get(payMap,login);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求新币宝登录支付接口失败:{}",e.getMessage());
			return PayResponse.error("请求新币宝登录支付接口失败:"+e.getMessage());
		}
		logger.info("新币宝支付返回结果:{}", payResponse);
		if(StringUtils.isEmpty(payResponse)){
			return PayResponse.error("新币宝支付 请求登录支付无返回值");
		}
		
		JSONObject pay = JSONObject.fromObject(payResponse);
		if(pay.containsKey("Code") && "1".equals(pay.getString("Code"))){
			JSONObject url = pay.getJSONObject("Data");
			logger.info("新币宝支付 支付地址url= {}",url.getString("Url")+"/"+url.getString("Token"));
			return PayResponse.sm_link(payEntity, url.getString("Url")+"/"+url.getString("Token"), "新币宝支付成功");
		}
		
		return PayResponse.error("新币宝支付下单失败：" + pay.getString("Message"));
	}

	@Override
	public String callback(Map<String, String> data) {
		return null;
	}

	private boolean verifyCallback(Map<String, String> data) {
	    String sourceSign = data.remove("Sign");
        if (StringUtils.isBlank(sourceSign)) {
            logger.info("[XBBZF]新币宝支付回调验签失败：回调签名为空！");
            return false;
        }
		try {
		    StringBuffer sb = new StringBuffer();
	        TreeMap<String,String> sortMap = new TreeMap<>(data);
	        for (String key : sortMap.keySet()) {
	            String val = sortMap.get(key);
	            if("FinishTime".equals(key) || "Sign".equalsIgnoreCase(key)) continue;
	            sb.append(key).append("=").append(val).append("&");
	        }
	        sb.deleteCharAt(sb.length() - 1);
	        sb.append(keyB);
	        logger.info("[XBBZF]新币宝支付回调待签名串"+sb.toString());
	        String localSign = MD5Utils.md5(sb.toString());
	        logger.info("[XBBZF]新币宝支付回调签名串"+localSign);
			return sourceSign.equalsIgnoreCase(localSign);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[XBBZF]新币宝支付生成支付签名串异常:"+ e.getMessage());
			return false;
		}
	}

	private JSONObject registUser(PayEntity entity) {
		Map<String,String> registUserParam = sealRequest(entity, 0);
		logger.info("注册用户请求参数："+registUserParam);
		String response;
		try {
			response = HttpUtils.toPostForm(registUserParam,addUserUrl);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求新币宝注册用户接口失败:{}",e.getMessage());
			return PayResponse.error("请求新币宝注册用户接口失败:"+e.getMessage());
		}
		logger.info("新币宝支付新增会员 请求返回值:"+response);
		if(StringUtils.isEmpty(response)) {
			return PayResponse.error("新币宝支付新增会员 请求无返回值,请联系第三方...");
		}

		JSONObject jsonObject = JSONObject.fromObject(response);
		if(jsonObject.containsKey("Code") && "1".equals(jsonObject.getString("Code"))){
			//获取会员支付地址
			Map<String,String> paramMap = sealRequest(entity, 1);
			String addressResponse;
			try {
				addressResponse = HttpUtils.get(paramMap, getAddress);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("请求新币宝获取用户钱包地址接口失败:{}",e.getMessage());
				return PayResponse.error("请求新币宝获取用户钱包地址接口失败:"+e.getMessage());
			}
			if(StringUtils.isEmpty(addressResponse)){
				return PayResponse.error("新币宝支付获取会员支付地址请求返回为空,请联系第三方");
			}

			JSONObject json = JSONObject.fromObject(addressResponse);

			if(json.containsKey("Code") && "1".equals(json.getString("Code"))){
				//插入到数据库
				JSONObject address = json.getJSONObject("Data");
				int i = insertXbbzfPaymentUser(entity.getuId(),address.getString("Address"));
				if(i > 0){
					logger.info("新币宝支付获取会员支付地址插入数据库成功");
				}else{
					return PayResponse.error("新币宝支付获取会员支付地址 插入数据库出现错误");
				}
			}
		} else {
			logger.error("新币宝支付新增会员失败:{}", jsonObject.getString("Message"));
			return PayResponse.error("新币宝支付新增会员失败:"+ jsonObject.getString("Message"));
		}
		return null;
	}

	/**
	 * 验证码(需全小写)，組成方式如下:Key=A+B+C(验证码組合方式)
	 * @param payEntity
	 * @return
	 */
	private Map<String,String> sealRequest(PayEntity payEntity,int type){
		String amount = new DecimalFormat("0.00").format(payEntity.getAmount());
		Map<String,String> map = new LinkedHashMap<>();
		map.put("MerCode", MerCode);
		map.put("Timestamp", System.currentTimeMillis()+"");
		map.put("UserName", payEntity.getUsername());
		//获取请求地址
		if(type == 1){
			map.put("UserType", "1");
			//币种 DC(钻石币)
			map.put("CoinCode", CoinCode);
		}else if(type == 2){
			map.put("Type", "1");
			map.put("Coin", "DC");
			map.put("Amount", amount);
			map.put("OrderNum", payEntity.getOrderNo());
			map.put("PayMethods", payEntity.getPayCode());
		}else if(type == 3){
            map.put("OrderNum", payEntity.getOrderNo());
        }
        logger.info("请求参数："+map);
		//将请求参数用des加密放在map中
		StringBuilder desSourceStr = new StringBuilder();
		for (Map.Entry entry:map.entrySet()) {
			desSourceStr.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		Map<String,String> param = new HashMap<>();
		DESUtil desUtil = new DESUtil(DESKey);
		String desStr = null;
		try {
			desStr = desUtil.encrypt(desSourceStr.deleteCharAt(desSourceStr.length()-1).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		param.put("param",desStr);

		//key字段签名方法
		StringBuilder key = new StringBuilder(RandomUtils.generateLowerString(Integer.valueOf(keyA)));
		//创建会员
		if(type == 0){
			key.append(generatorSign(map,0));
		}else if(type == 1){
			key.append(generatorSign(map,1));
		}else if(type == 2){
			key.append(generatorSign(map,2));
		}else if(type == 3){
            key.append(generatorSign(map,3));
        }
		key.append(RandomUtils.generateLowerString(Integer.valueOf(keyC)));
		param.put("key", key.toString());

		logger.info("新币宝支付 组装的参数:{}",map);

		return param;
	}
	
	/**
	 * MD5(MerCode + UserName + KeyB + YYYYMMDD)
	 * @param map
	 * @return
	 */
	private String generatorSign(Map<String,String> map,int type){
		logger.info("新币宝支付 开始生成 MD5值");
		StringBuilder sb = new StringBuilder();
		sb.append(map.get("MerCode"));
		//新增会员
		if(type == 0){
			sb.append(map.get("UserName"));
		}else if(type == 1){//获取请求地址
			sb.append(map.get("UserType")).append(map.get("CoinCode"));
		}else if(type == 2){//支付
			sb.append(map.get("UserName")).append(map.get("Type")).append(map.get("OrderNum"));
		}else if(type == 3){//支付
            sb.append(map.get("UserName")).append(map.get("OrderNum"));
        }

		sb.append(keyB).append(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		logger.info("新币宝支付 加密前参数:{}", sb);
		String md5Value = MD5Utils.md5(sb.toString());
		logger.info("新币宝支付加密后值:{}",md5Value);
		
		return md5Value;
	}
	/**
	 * 查询会员是否存在
	 * @param payEntity
	 * @return
	 */
	private boolean checkUser(String uid){
		logger.info("查询新币宝会员是否存在");
		XbbzfPaymentDao xbbzfPaymentDao = (XbbzfPaymentDao) SpringContextUtils.getBeanByClass(XbbzfPaymentDao.class);
		XbbzfPaymentEntity entity = xbbzfPaymentDao.selectUserName(Integer.valueOf(uid));
		//查询没有，新增会员
		if(entity == null){
			logger.info("新币宝 会员uid = {} 暂时没有创建会员，需要创建会员，返回 true",uid);
			return true;
		}else{
			logger.info("新币宝 会员uid = {} 暂时已经创建会员，需要创建会员，返回 false",uid);
			return false;
		}
	}
	
	private int insertXbbzfPaymentUser(String uid,String address){
		XbbzfPaymentEntity entity = new XbbzfPaymentEntity();
		entity.setUid(uid);
		entity.setUsername(uid);
		entity.setAddress(address);
		XbbzfPaymentDao xbbzfPaymentDao = (XbbzfPaymentDao) SpringContextUtils.getBeanByClass(XbbzfPaymentDao.class);
		int i = xbbzfPaymentDao.insertXbbzfPaymentEntity(entity);
		if(i > 0){
			logger.info("新币宝 支付插入会员成功.....");
		}
		return i;
	}
    public boolean searchOrder(Map<String, String> data){
        try {
            PayEntity payEntity = new PayEntity();
            payEntity.setUsername(data.get("UserName"));
            payEntity.setOrderNo(data.get("OrderNum"));
            Map<String, String> param = sealRequest(payEntity, 3);
            logger.info("[XBBZF]新币宝支付查询订单请求参数："+param);
            String resStr = HttpUtils.toPostForm(param, orderDetail);
            logger.info("[XBBZF]新币宝支付查询订单响应信息："+resStr);
            if(StringUtils.isBlank(resStr)){
                logger.info("[XBBZF]新币宝支付查询订单发起HTTP请求无响应结果");
                return false;
            }   
            JSONObject resJsonObj = JSONObject.fromObject(resStr);
            if(JSONUtils.compare(resJsonObj, "Success", "true")) {
                resJsonObj = resJsonObj.getJSONObject("Data");
                String State1 = resJsonObj.getString("State1"); //订单状态
                String State2 = resJsonObj.getString("State2"); //支付状态
                String result =  State1.equals("2") && State2.equals("2")?"success":"fail";
                if("success".equals(result)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XBBZF]新币宝支付回调查询订单异常");
            return false;
        } 
    }
    @SuppressWarnings("unchecked")
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        Map<String,String> infoMap = ParamsUtils.getNotifyParams(request);  //获取回调请求参数
        logger.info("[XBBZF]新币宝支付回调请求参数："+JSONObject.fromObject(infoMap));
        if (MapUtils.isEmpty(infoMap)) {
            logger.error("[XBBZF]新币宝支付获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.keyA = config.getString("keyA");//从配置中获取
        this.keyB = config.getString("keyB");//从配置中获取
        this.keyC = config.getString("keyC");//从配置中获取
        this.DESKey = config.getString("DESKey");
        this.MerCode = config.getString("MerCode");
        this.orderDetail = config.getString("orderDetail");
        
        String order_amount = infoMap.get("LegalAmount"); // 实际充值金额，单位元
        if(StringUtils.isBlank(order_amount)){
            logger.info("[XBBZF]新币宝支付获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount);
        String order_no = infoMap.get("OrderNum");// 平台订单号
        String trade_no = infoMap.get("OrderId");// 第三方流水号
        String State1 = infoMap.get("State1"); //订单状态
        String State2 = infoMap.get("State2"); //支付状态
        String trade_status =  State1.equals("2") && State2.equals("2")?"success":"fail";
        String t_trade_status = "success";// 两个状态同时为2时，才给会员上分
        
        boolean result = searchOrder(infoMap);
        if(!result) {
            return ret__failed;  
        }
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
        processNotifyVO.setPayment("XBBZF");
        processNotifyVO.setConfig(config);
        return super.processSuccessNotify(processNotifyVO,verifyRequest);
    }
}
