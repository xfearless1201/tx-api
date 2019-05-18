/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下网络 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    XYFPayServiceImpl.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下网络科技 
 *
 *    @author:     Tammy 
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2018年08月31日 14:57 
 *
 *    Revision: 
 *
 *    2018/8/31 14:57 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *  * @ClassName XYFPayServiceImpl
 *  * @Description txy新易付支付对接
 *  * @Author Tammy
 *  * @Date 2018年08月31日 14:57
 *  * @Version 1.0.0
 *  
 **/
public class XYFPayServiceImpl implements PayService {

    //日志
    private final static Logger logger = LoggerFactory.getLogger(XYFPayServiceImpl.class);

    private String merchantId;

    private String payUrl;

    private String sercet;//签名key

    //初始话配置文件
    public XYFPayServiceImpl(Map<String,String> pmap) {

        if(pmap != null && !pmap.isEmpty()){
            if(pmap.containsKey("merchantId")){
                this.merchantId = pmap.get("merchantId");
            }
            if(pmap.containsKey("payUrl")){
                this.payUrl = pmap.get("payUrl");
            }
            if(pmap.containsKey("sercet")){
                this.sercet = pmap.get("sercet");
            }
        }
    }

    /**
     * 网银支付
     * @param payEntity
     * @return
     */
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
        logger.info("扫码支付开始==========================START===================================");

        //发起第三方支付
        String username = payEntity.getUsername();
        Double paymoney = payEntity.getAmount();
        String UserNumber = payEntity.getOrderNo();
        String pay_url = payEntity.getPayUrl();
        try {
            //封装支付参数
            Map<String,String> map = sealRequest(payEntity);
            return sealResponse(map, username, paymoney, UserNumber,pay_url);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("扫码支付异常:"+e.getMessage());
            return PayUtil.returnPayJson("error","2",e.getMessage(),username, paymoney, UserNumber, pay_url);
        }
    }


    /**
     * 封装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity)throws Exception{

        logger.info("组装支付参数开始==============================START============================");
        try {
            //格式化订单金额
            Map<String,String> map = new HashMap<>();
            map.put("id",merchantId);
            map.put("banktype",entity.getPayCode());
            map.put("usernumber",entity.getOrderNo());
            map.put("paymoney",String.valueOf(entity.getAmount()));
            map.put("sign", sercet);
            logger.info("支付请求参数结果:"+map.toString());
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("封装支付请求参数异常:"+e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * 组装支付返回参数
     * @param data
     * @return
     * @throws Exception
     */
    private JSONObject sealResponse(Map<String,String> data,String username,double amount,String orderNo,String pay_url)throws Exception{
        logger.info("支付开始============================START=========================================");
        try {
          //对组装的map进行排序
            Map<String,String> treemap = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    // TODO Auto-generated method stub
                    return o1.compareTo(o2);
                }
            });
            treemap.putAll(data);
            //生成代签名串
            String signStr = generatorSignStr(treemap);
            logger.info("[XYF]新易付生成待签名串:"+signStr);
            //进行base64加密处理

//            String sign = new Base64().encodeToString(signStr.getBytes("UTF-8"));

            String sign = Base64.getEncoder().encodeToString(signStr.getBytes("UTF-8"));
            logger.info("[XYF]新易付生成签名串:"+sign);
            logger.info("[XYF]新易付拼装请求参数:"+treemap);
            Map<String,String> reqParams = new HashMap<>();
            reqParams.put("linktext", sign);
            String response = generatorForm(reqParams, payUrl);
            logger.info("支付结果返回:"+response);
            return PayUtil.returnPayJson("success","1","下单成功",username,amount,orderNo,response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用第三方支付异常:"+e.getMessage());
            throw new Exception("调用第三方支付异常!");
        }
    }

    /**
     * 新易付没有验签
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String,String> data) {
        return "success";
    }
    
    /**
     * 
     * @Description 组装字符串拼接
     * @param
     * @param parameters
     * @return
     */
    private String generatorSignStr(Map<String, String> parameters) {
        try {
            StringBuffer str = new StringBuffer();
            Set<String> es = parameters.keySet();
            Iterator<String> it = es.iterator();
            while(it.hasNext()) {
                String key = it.next();
                String val = parameters.get(key);
                str.append(key + "=" + val + "&");
            }
            String  result = str.toString().substring(0, str.length()-1);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XYF]新易付生成待签名串异常:"+e.getMessage());
        }
        return null;
    }

    public static String generatorForm(Map<String, String> params,String payUrl) {
        String FormString = "<body onLoad=\"document.actform.submit()\">正在处理请稍候.....................<form  id=\"actform\" name=\"actform\" method=\"post\" action=\""
                + payUrl + "\">";
        for (String key : params.keySet()) {
            if (StringUtils.isNotBlank(params.get(key)))
                FormString += "<input name=\"" + key + "\" type=\"hidden\" value='" + params.get(key) + "'>\r\n";
        }
        FormString += "</form></body>";
        return FormString;
    }
    
    public static void main(String[] args) {
        JSONObject data = new JSONObject();
        data.put("ID","100047");
        data.put("sercet","A3A204D54CA09E5A5DAF5CC24EA828E178D7A091739C14714B761244F62E4D6F9C4B6B57DC426DAE716BB81513433C77");
        data.put("payUrl","http://aukao.cn/pay.aspx");
        data.put("callbackurl","http://www.baidu.com");
        System.out.printf(data.toString());
    }
}
