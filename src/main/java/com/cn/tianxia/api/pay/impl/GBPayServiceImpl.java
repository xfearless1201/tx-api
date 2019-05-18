package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @ClassName GBPayServiceImpl
 * @Description 冠宝支付
 * @author Jacky
 * @Date 2019年1月30日 下午16:58:58
 * @version 1.0.0
 */
public class GBPayServiceImpl implements PayService {

    private final static Logger logger = LoggerFactory.getLogger(GBPayServiceImpl.class);

    private String API_URL;

    private String API_KEY ;

    private String MERCHANT_NO ;

    private String NOTIFY_URL ;

    public GBPayServiceImpl(Map<String,String> map) {
        if(map.containsKey("API_URL")){
            this.API_URL = map.get("API_URL");
        }
        if(map.containsKey("API_KEY")){
            this.API_KEY = map.get("API_KEY");
        }
        if(map.containsKey("MERCHANT_NO")){
            this.MERCHANT_NO = map.get("MERCHANT_NO");
        }
        if(map.containsKey("NOTIFY_URL")){
            this.NOTIFY_URL = map.get("NOTIFY_URL");
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("冠宝支付 smPay(PayEntity payEntity ={}   -start" + payEntity);
        try {
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data);
            data.put("sign", sign);
            logger.info("GB 冠宝支付扫码支付请求参数报文： {}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = HttpUtils.toPostForm(data, API_URL);

            if(StringUtils.isBlank(response)){
                logger.info("GB 冠宝支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("GB 冠宝支付扫码支付发起HTTP请求无响应结果");
            }
            logger.info("GB 冠宝支付扫码支付发起HTTP请求响应结果:{}",response);

            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.containsKey("status") && "0".equals(jsonObject.getString("status"))){
                //下单成功
                String pageUrl = jsonObject.getString("data");//冠宝支付URL
                if(StringUtils.isBlank(payEntity.getMobile())){
                    //PC端
                    return PayResponse.sm_qrcode(payEntity, pageUrl, "扫码支付下单成功");
                }
                return PayResponse.sm_link(payEntity, pageUrl, "H5支付下单成功");
            }
            logger.error("冠宝支付下单失败！  失败返回:{}",jsonObject);
            return PayResponse.error("下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(),e);
            logger.info("GB 冠宝支付支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("GB 冠宝支付支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("GB 冠宝支付回调验签方法 callback(Map<String, String> data = {}  -start ", data);
        try {
            Map<String,String> map = new HashMap<>();
            map.put("money",data.get("money"));
            map.put("orderIdCp",data.get("orderIdCp"));
            map.put("version",data.get("version"));
            map.put("sign",data.get("sign"));

            String sourceSign = data.get("sign");
            logger.info("GB 冠宝支付回调验签获取签名原串：{}",sourceSign);
            String sign = generatorSign(map);
            logger.info("GB 冠宝支付回调验签生成签名串:{}",sign);

            if(sourceSign.equalsIgnoreCase(sign)) return "success";

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("GB 冠宝支付回调验签异常:{}",e.getMessage());
            logger.error(e.getMessage(),e);
        }
        return "faild";
    }

    /**
     * 组装参数
     * @param entity
     * @return
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("GB 冠宝支付参数组装 sealRequest(PayEntity entity = {} -start "+ entity);
        try {
            Map<String,String> dataMap = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount()* 100);//订单金额,单位元,保留两位小数
            dataMap.put("cpId",MERCHANT_NO); //平台分配给商户的应用ID
            dataMap.put("channel","alipay");//支付类型  Q码:qm  微信:wechat 支付宝:alipay
            dataMap.put("money",amount); //订单金额，保留两位小数（单位为分）
            dataMap.put("subject","GBZF");
            dataMap.put("orderIdCp",entity.getOrderNo());//商户订单ID
            dataMap.put("description",entity.getUsername());//订单备注（未确保准确到账，请填写付款支付宝绑定身份的真实姓名）
            dataMap.put("timestamp",new Date().getTime()+"");//订单创建时间，当前时间戳 13位
            dataMap.put("notifyUrl",NOTIFY_URL);//异步通知地址，订单支付成功后平台通过此地址通知商户
            dataMap.put("ip",entity.getIp());//用户ip
            dataMap.put("version","1");//版本
            dataMap.put("command","applyqr");//通道方式
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("GB   冠宝支付组装参数失败!",e);
            throw new Exception("[GB] 冠宝支付组装支付请求参数异常",e);
        }
    }

    /**
     * 加密
     * @param map
     * @return
     */
    private String generatorSign(Map<String,String> map) throws Exception{
       logger.info("GB 冠宝支付加密方法 generatorSign(Map<String,String> map = {}  -start " + map);
        try {
            //加入秘钥
            StringBuffer stringBuffer = new StringBuffer();
            //排序
            Map<String,String> data = MapUtils.sortByKeys(map);
            Iterator<String> iterator = data.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = data.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                stringBuffer.append("&").append(key).append("=").append(val);
            }
            stringBuffer.append("&").append(API_KEY);

            String signStr = stringBuffer.toString().replaceFirst("&", "");
            logger.info("GB 冠宝支付生成待签名串:{}",signStr);
            //生成MD5并转换大写
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toUpperCase();
            logger.info("GB 冠宝支付生成待签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("GB 冠宝支付生成待签名串:{}",e.getMessage());
            throw new Exception("GB  冠宝支付生成签名异常",e);
        }
    }
}
