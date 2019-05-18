/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.pay.impl 
 *
 *    Filename:    BGPayServiceImpl.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:    Roman 
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年05月07日 11:56 
 *
 *    Revision: 
 *
 *    2019/5/7 11:56 
 *        - first revision 
 *
 *****************************************************************/

package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.JSONUtils;
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
 *  * @ClassName BGPayServiceImpl
 *  * @Description TODO(布谷支付)
 *  * @Author Roman
 *  * @Date 2019年05月07日 11:56
 *  * @Version 1.0.0
 *  
 **/

public class BGPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(BGPayServiceImpl.class);

    private static final String ret__failed = "Notify Is Failed";

    private static final String ret__success = "1";

    /**
     * 商户号
     */
    private String merchId;

    /**
     * 支付地址
     */
    private String payUrl;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 密钥
     */
    private String secret;

    /**
     * 订单查询地址
     */
    private String queryOrderUrl;


    /**
     * 构造器，初始化参数
     */
    public BGPayServiceImpl() {
    }

    public BGPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merchId")) {
                this.merchId = data.get("merchId");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("secret")) {
                this.secret = data.get("secret");
            }
            if (data.containsKey("queryOrderUrl")) {
                this.queryOrderUrl = data.get("queryOrderUrl");
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        return null;
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity entity) {
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(entity);
            logger.info("[BG]布谷支付扫码支付请求参数报文:{}", JSONObject.fromObject(data));

            String response = HttpUtils.generatorForm(data, payUrl);
            logger.info("[BG]布谷支付扫码支付响应参数：{}", response);
            if (StringUtils.isBlank(response)) {
                logger.info("[BG]布谷支付扫码支付发起HTTP请求无响应");
                return PayResponse.error("[BG]布谷支付扫码支付发起HTTP请求无响应");
            }
            return PayResponse.sm_form(entity, response, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[BG]布谷支付扫码支付异常" + e.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[BG]布谷支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount());
            //订单号
            String orderNo = entity.getOrderNo();
            //下单时间
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


//            goodsname	商品名称	varchar(255)	必填。商品名称
            dataMap.put("goodsname", "top_Up");

//            istype	支付类型	int(11)	必填。1=支付宝 2=微信支付 5=当面付 6=支付宝转银行卡 7=银联转账 8=支付宝红包
            dataMap.put("istype", entity.getPayCode());

//            adduid	自定义ID	int(10)	必填。商户网站的UID
            dataMap.put("adduid", entity.getPayId());

//            adduser	用户标识	String(32)	必填。商户网站的用户名
            dataMap.put("adduser", entity.getuId());

//            adddata	附加数据	varchar(255)	必填。商户网站的附加数据
            dataMap.put("adddata", "top_Up");

//            price	支付金额	decimal(10,2)	必填。支付金额
            dataMap.put("price", amount);

//            mct_id	商户号	int(10)	必填。商户号
            dataMap.put("mct_id", merchId);

//            out_trade_no	商户订单号	varchar(100)	必填。商家提交的订单号
            dataMap.put("out_trade_no", orderNo);

//            notify_url	异步通知地址	varchar(255)	必填。异步回调地址
            dataMap.put("notify_url", notifyUrl);

//            return_url	同步跳转地址	varchar(255)	必填。同步跳转地址
            dataMap.put("return_url", entity.getRefererUrl());


            //生成待签名串
            String sign = generatorSign(dataMap, 0);
//            sign	签名	varchar(32)	必填。按顺序加密然后传到服务器
            dataMap.put("sign", sign);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[BG]布谷支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param type 0:支付     1:回调     2:查询
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(Map<String, String> data, int type) throws Exception {
        try {

//            md5($out_trade_no.$price.$istype.$adduid.$adduser.$mct_id.$secret);
            StringBuffer sb = new StringBuffer();
            if (type == 0) {
                sb.append(data.get("out_trade_no"))
                        .append(data.get("price"))
                        .append(data.get("istype"))
                        .append(data.get("adduid"))
                        .append(data.get("adduser"))
                        .append(data.get("mct_id"))
                        .append(secret);
            } else if (type == 1) {
                sb.append(data.get("out_trade_no_"))
                        .append(data.get("price_"))
                        .append(data.get("istype_"))
                        .append(data.get("adduid_"))
                        .append(data.get("adduser_"))
                        .append(data.get("mct_id_"))
                        .append(secret);
            } else {
//                md5($out_trade_no.$mct_id.$secret);
                sb.append(data.get("out_trade_no"))
                        .append(data.get("mct_id"))
                        .append(secret);
            }

            //生成待签名串
            String signStr = sb.toString();
            logger.info("[BG]布谷支付生成待签名串:" + signStr);
            //生成加密串
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[BG]布谷支付生成签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[BG]布谷支付生成支付签名串异常:" + e.getMessage());
            throw new Exception("生成支付签名串异常!");
        }
    }

    /**
     * 功能描述:回调验签
     *
     * @param data
     * @return: boolean
     **/
    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[BG]布谷支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign_");
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data, 1);
            logger.info("[BG]布谷支付生成签名串：{}--源签名串：{}", sign, sourceSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BG]布谷支付生成加密串异常:{}", e.getMessage());
        }
        return sourceSign.equalsIgnoreCase(sign);
    }

    /**
     * 回调方法
     *
     * @param request  第三方请求request
     * @param response response
     * @param config   平台对应支付商配置信息
     * @return
     */
    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        //参数验签，从配置中获取
        this.secret = config.getString("secret");
        this.merchId = config.getString("merchId");
        this.queryOrderUrl = config.getString("queryOrderUrl");
        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[BG]布谷支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(infoMap);

        // 平台订单号
        String orderNo = infoMap.get("out_trade_no_");
        // 第三方订单号
        String tradeNo = infoMap.get("order_on_");
        //订单状态
        String tradeStatus = "00";
        // 表示成功状态
        String tTradeStatus = "00";
        //实际支付金额
        String orderAmount = infoMap.get("price_");
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        //查询订单信息
        /*boolean orderStatus = getOrderStatus(orderNo);
        if (!orderStatus) {
            logger.info(orderNo + "此订单尚未支付成功！");
            return ret__failed;
        }*/
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(orderNo);
        processNotifyVO.setTrade_no(tradeNo);
        processNotifyVO.setTrade_status(tradeStatus);
        processNotifyVO.setT_trade_status(tTradeStatus);
        processNotifyVO.setRealAmount(realAmount);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("BG");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }

    /**
     * 功能描述:查询订单状态
     *
     * @param orderNo 订单号
     * @return: boolean
     **/
    private boolean getOrderStatus(String orderNo) {
        try {
            //封装请求参数
            Map<String, String> map = new HashMap<>();
            map.put("mct_id", merchId);
            map.put("out_trade_no", orderNo);
            map.put("sign", generatorSign(map, 3));

            logger.info("[BG]布谷支付回调订单查询接口请求参数{}", JSONObject.fromObject(map));
            //发送请求
            String response = HttpUtils.toPostForm(map, queryOrderUrl);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            logger.info("[BG]布谷支付回调订单查询接口响应信息{}", respJson);
            if (JSONUtils.compare(respJson, "code", "1")) {
                String data = respJson.getJSONArray("data").get(0).toString();
                respJson = JSONObject.fromObject(data);
                if ("1".equals(respJson.getString("Order_str"))) {

                    logger.info("[BG]布谷支付回调订单查询成功,订单" + orderNo + "已支付。");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BG]布谷支付回调订单查询异常");
            return false;
        }
    }
}





