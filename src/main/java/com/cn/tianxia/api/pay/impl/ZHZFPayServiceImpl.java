package com.cn.tianxia.api.pay.impl;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 众惠支付2
 * @ClassName ZHZFPayServiceImpl
 * @Description TODO(这里用一句话描述这个类的作用)
 * @author bing
 * @Date 2019年2月25日 上午10:43:55
 * @version 1.0.0
 */
public class ZHZFPayServiceImpl extends PayAbstractBaseService implements PayService {
	private final static Logger logger = LoggerFactory.getLogger(ZHZFPayServiceImpl.class);
	/**回调失败响应信息*/
	private static final String ret__failed = "fail";
    /**回调成功响应信息*/
    private static final String ret__success = "success";
	/**支付地址*/
	private String payUrl;
	/**查询地址*/
	private String queryUrl;
	/**商户编号*/
	private String payMemberid;
	/**商户密钥*/
	private String secretKey;
	/**回调地址*/
    private String payNotifyUrl;
    /**收款类型*/
    private String payCategory;
	/**无参构造方法*/
	public ZHZFPayServiceImpl() {}
	/**构造方法*/
    public ZHZFPayServiceImpl(Map<String,String> data) {
		if(MapUtils.isNotEmpty(data)){
			if(data.containsKey("payUrl")){
				this.payUrl = data.get("payUrl");
			}
			if(data.containsKey("queryUrl")){
				this.queryUrl = data.get("queryUrl");
			}
			if(data.containsKey("payMemberid")){
				this.payMemberid = data.get("payMemberid");
			}
			if(data.containsKey("secretKey")){
				this.secretKey = data.get("secretKey");
			}
			if(data.containsKey("payNotifyUrl")){
			    this.payNotifyUrl = data.get("payNotifyUrl");
			}
			if(data.containsKey("payCategory")){
			    this.payCategory = data.get("payCategory");
			}
		}
	}
	@Override
	public JSONObject wyPay(PayEntity payEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject smPay(PayEntity payEntity) {
		logger.info("[ZHZF]众惠2扫码支付开始======================START==================");
		try {
			//封装请求参数
			Map<String, String> data = sealRequest(payEntity);
			logger.info("[ZHZF]众惠2扫码支付请求参数:"+JSONObject.fromObject(data));
			//生成请求表单
			String resStr = HttpUtils.toPostForm(data, payUrl);
			logger.info("[ZHZF]众惠2扫码支付响应信息:"+resStr);
			if(StringUtils.isBlank(resStr)){
				logger.info("[ZHZF]众惠2扫码支付发起HTTP请求无响应结果");
				return PayResponse.error("[ZHZF]众惠2扫码支付发起HTTP请求无响应结果");
			}
			JSONObject resJsonObj = JSONObject.fromObject(resStr);
			if(resJsonObj.containsKey("code")&&"0".equals(resJsonObj.getString("code"))){
			    return PayResponse.sm_link(payEntity, resJsonObj.getString("data"), "下单成功");
			}
			return PayResponse.error("[ZHZF]众惠2扫码支付失败");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[ZHZF]众惠2扫码支付生成异常:"+e.getMessage());
			return PayResponse.error("[ZHZF]众惠2扫码支付下单异常");
		}
	}

	@Override
	public String callback(Map<String, String> data) {
		return null;
	}
	/**
     * 
     * @Description 封装支付请求参数
     * @param
     * @param 
     * @return
     * @throws Exception
     */
	public Map<String, String> sealRequest(PayEntity payEntity){
	    try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DecimalFormat df = new DecimalFormat("0");
            Map<String, String> data = new HashMap<>();
            data.put("channelId", payMemberid);//商户号
            data.put("goodsName", "recharge");//商品名称
            data.put("totalFee", df.format(payEntity.getAmount()*100));//金额 单位：分
            data.put("orderNo", payEntity.getOrderNo());//订单号
            data.put("notifyUrl", payNotifyUrl);//回调地址
            data.put("orderTime", sdf.format(new Date()));//下单时间
            data.put("timeStamp", String.valueOf(System.currentTimeMillis()));//时间戳
            data.put("payType", payEntity.getPayCode());//支付类型  支付宝：1；微信:2; QQ钱包:3
            data.put("payCategory", payCategory);//收款类型
            data.put("secretKey", secretKey);//安全密钥
            data.put("sign", generatorSign(data, "0"));
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZHZF]众惠2扫码支付拼接参数异常");
            return null;
        }
	}
	/**
     * 
     * @Description 生成签名串
     * @param data
     * @return
	 * @throws Exception
     */
    public String generatorSign(Map<String, String> data, String type) throws Exception{
        StringBuffer sb = new StringBuffer();
        if("0".equals(type)){
            sb.append(data.get("orderNo"));
            sb.append(data.get("totalFee"));
            sb.append(data.get("notifyUrl"));
            sb.append(data.get("orderTime"));
            sb.append(data.get("timeStamp"));
        }else{
            sb.append(data.get("out_trade_no"));
            sb.append(data.get("total_fee"));
            sb.append(payNotifyUrl);
            sb.append(data.get("orderTime"));
            sb.append(data.get("cas_time_stamp"));
        }
        sb.append(secretKey);
    	//生成待签名串
    	String signStr = sb.toString();
    	logger.info("[ZHZF]众惠2扫码支付生成待签名串:{}",signStr);
    	String sign = MD5Utils.md5(signStr.getBytes());
    	logger.info("[ZHZF]众惠2扫码支付生成加密签名串:{}",sign);
    	return sign;
    }
    
    private boolean verifyCallback(Map<String,String> data) {
        try {
            String sourceSign = data.get("sign");
            String sign = generatorSign(data, "1");
            logger.info("[ZHZF]众惠2扫码支付回调生成签名串"+sign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[ZHZF]众惠2扫码支付回调生成签名串异常"+e.getMessage());
            return false;
        }
  }
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        Map<String,String> infoMap = ParamsUtils.getNotifyParams(request);  //获取回调请求参数
        logger.info("[ZHZF]众惠2扫码支付回调请求参数："+JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("ZHZFNotify获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.secretKey = config.getString("secretKey");//从配置中获取
        this.payNotifyUrl=config.getString("payNotifyUrl");//从配置中获取
        this.payMemberid=config.getString("payMemberid");//从配置中获取
        this.queryUrl = config.getString("queryUrl");
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        //调用查询接口查询订单信息
        boolean orderStatus = getOrderStatus(order_no);
		if(!orderStatus){
			return ret__failed;
		}
        boolean verifyRequest = verifyCallback(infoMap);
        String order_amount = infoMap.get("total_fee");//单位：分
        if(StringUtils.isBlank(order_amount)){
            logger.info("ZHZFNotify获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(order_amount)/100;
        String trade_no = "ZHZF"+System.currentTimeMillis();// 第三方订单号
        String trade_status = infoMap.get("status");//订单状态
        String t_trade_status = "200";// 表示成功状态
        
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
        processNotifyVO.setPayment("ZHZF");

        return super.processSuccessNotify(processNotifyVO,verifyRequest);
    }
    public boolean getOrderStatus(String orderNo){
    	try {
			String resStr = HttpUtils.get(queryUrl+"/"+payMemberid+"/"+orderNo);
			JSONObject resJson = JSONObject.fromObject(resStr);
            logger.info("[ZHZF]众惠2扫码支付查询接口响应信息{}",resJson);
			if(resJson.containsKey("status")&&!"success".equals(resJson.getString("status"))){
				logger.info("[ZHZF]众惠2扫码支付查询支付商订单信息{}",resJson.getString("message"));
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[ZHZF]众惠2扫码支付查询异常");
			return false;
		}
    }
}
