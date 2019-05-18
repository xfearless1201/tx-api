package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.domain.txdata.v2.RechargeDao;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.project.v2.RechargeEntity;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.SpringContextUtils;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import com.google.gson.Gson;

import net.sf.json.JSONObject;

/**
 *  * @ClassName HHZFPayServiceImpl
 *  * @Description TODO(这里用一句话描述这个类的作用)
 *  * @Author Roman
 *  * @Date 2019年02月28日 14:35
 *  * @Version 1.0.0
 *  
 **/

public class HHZFPayServiceImpl extends PayAbstractBaseService implements PayService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(HHZFPayServiceImpl.class);

    private static final String ret__failed = "fail";

    private static final String ret__success = "0000";

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 支付请求地址
     */

    private String payUrl;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 密钥
     */
    private String key;


    /**
     * 构造器，初始化参数
     */
    public HHZFPayServiceImpl() {
    }

    public HHZFPayServiceImpl(Map<String, String> data,String type) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey(type)){
                JSONObject jsonObject = JSONObject.fromObject(data.get(type));
                if (jsonObject.containsKey("mch_id")) {
                    this.mchId = jsonObject.getString("mch_id");
                }
                if (jsonObject.containsKey("payUrl")) {
                    this.payUrl = jsonObject.getString("payUrl");
                }
                if (jsonObject.containsKey("notifyUrl")) {
                    this.notifyUrl = jsonObject.getString("notifyUrl");
                }
                if (jsonObject.containsKey("key")) {
                    this.key = jsonObject.getString("key");
                }
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
    public JSONObject smPay(PayEntity payEntity) {
        try {
            //获取支付请求参数
            JSONObject jsonsStr = sealRequest(payEntity);

            //生成Base64加密串 reqStr
            Map<String, String> map = new HashMap<>();
            byte[] bytes = jsonsStr.toString().getBytes();
            String reqStr = Base64.getEncoder().encodeToString(bytes);

            String sign = generatorSign(reqStr, key);

            map.put("req", reqStr);
            map.put("sign", sign);
            logger.info("[HHZF]互汇支付扫码支付请求参数报文:{}", jsonsStr.toString());
            String response = HttpUtils.toPostForm(map, payUrl);
            logger.info("[HHZF]互汇支付扫码支付发起HTTP请求响应结果:{}", response);
            if (StringUtils.isBlank(response)) {
                logger.error("[HHZF]互汇支付下单失败：生成请求form为空");
                PayResponse.error("[HHZF]互汇支付下单失败：生成请求form为空");
            }

            JSONObject jsonObject = JSONObject.fromObject(response);
            String resp = jsonObject.getString("resp");
            String respStr = new String(Base64.getDecoder().decode(resp));

            logger.info("[HHZF]互汇支付解密串===================={}",respStr);
            JSONObject respJson = JSONObject.fromObject(respStr);
            if (respJson.containsKey("respcode") && "00".equals(respJson.getString("respcode"))) {
                //下单成功

                String payurl = respJson.getString("formaction");

                //微信返回生成的二维码
                if ("2".equals(payEntity.getPayType())){
                    return PayResponse.sm_qrcode(payEntity, payurl, "扫码支付下单成功");
                }
                return PayResponse.sm_link(payEntity, payurl, "扫码支付下单成功");

            }
            return PayResponse.error("下单失败:" + response);

        } catch (Exception e) {
            e.printStackTrace();
            return PayResponse.error("[HHZF]互汇支付扫码支付下单失败" + e.getMessage());
        }
    }

    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[HHZF]互汇支付回调验签开始==============START===========");
        //获取回调通知原签名串
        String respSign = data.get("sign");
        //获取回调通知Base64加密信息
        String resp = data.get("resp");
        logger.info("[HHZF]互汇支付回调验签获取原签名串:{}", respSign);
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(resp, key);
            logger.info("[HHZF]互汇支付回调验签生成加密串:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HHZF]互汇支付生成加密串异常:{}", e.getMessage());
        }
        return respSign.equalsIgnoreCase(sign);
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private JSONObject sealRequest(PayEntity entity) throws Exception {
        logger.info("[HHZF]互汇支付封装请求参数开始=====================START==================");
        try {
            //创建支付请求参数存储对象
            Map<String, String> dataMap = new HashMap<>();
            JSONObject jsonObject = new JSONObject();
            //订单金额
            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);
            String orderNo = entity.getOrderNo();

//            请求方式 action string 微信：WxCode/WxSao/WxJsApi/WxH5  支付宝：AliCode/AliSao/AliWap   QQ支付：QQCode/QQSao 银联：USao
            jsonObject.put("action", entity.getPayCode());

//           交易金额 txnamt int 订单金额，单位为分 N
            jsonObject.put("txnamt", amount);

//            商户号 merid string 商户号，接入手机支付平台时分配 N
            jsonObject.put("merid", mchId);

//           商户订单号 orderid string由商户生成，必需唯一，长度 8-32 位，由字母和数字组成 N
            jsonObject.put("orderid", orderNo);

//           交易二维码 code string 反扫时的码，App 扫描微信/支付宝生成的码 Y
            jsonObject.put("code", "");

//           openid openid string 微信公众号支付时的 openid Y
            jsonObject.put("openid", "");

//            用户 IP ip string WxH5 时需上送，用户真实 IP，错误将无法交易 Y
            jsonObject.put("ip", entity.getIp());

//           通知 URL backurl string商户系统的地址，支付结束后，通过该 url 通知商户交易结果，POST 返回参数参考 3.4 N
            jsonObject.put("backurl", notifyUrl);

//           前台 URL fronturl string仅 WxJsApi 支持，丌填时使用默讣页面，GET 返回 参数参考 3.4
            jsonObject.put("fronturl", entity.getRefererUrl());

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[HHZF]互汇支付封装请求参数异常:" + e.getMessage());
            throw new Exception("封装支付请求参数异常!");
        }
    }

    /**
     * @param data Base64加密后的数据
     * @param key  密钥
     * @return
     * @throws Exception
     * @Description 生成支付签名串
     */
    public String generatorSign(String data, String key) throws Exception {
        logger.info("[HHZF]互汇支付生成支付签名串开始==================START========================");
        //生成待加密串
        String signStr = data + key;
        logger.info("[HHZF]互汇支付生成待签名串:" + signStr);
        //生成MD5加密串
        String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
        return sign;
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

        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        String resp = infoMap.get("resp");

        String respStr = new String(Base64.getDecoder().decode(resp));
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();
        map = gson.fromJson(respStr, map.getClass());

        logger.info("[HHZF]互汇支付回调请求参数:{}", map);
        if (org.apache.commons.collections.MapUtils.isEmpty(map)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        // 平台订单号
        String orderNo = map.get("orderid");

        RechargeDao RechargeDao = (RechargeDao) SpringContextUtils.getBeanByClass(RechargeDao.class);
        RechargeEntity rechargeEntity = RechargeDao.selectByOrderNo(orderNo);
        String type =getPayConfigType(String.valueOf(rechargeEntity.getPayType()));//获取支付类型
        config = config.getJSONObject(type);
        //参数验签，从配置中获取
        this.key = config.getString("key");
        boolean verifyRequest = verifyCallback(infoMap);


        // 第三方订单号
        String tradeNo = map.get("queryid");
        //订单状态
        String tradeStatus = map.get("resultcode");
        // 表示成功状态
        String tTradeStatus = "0000";
        //实际支付金额
        String orderAmount = String.valueOf(map.get("txnamt"));
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        //成功返回
        processNotifyVO.setRet__success(ret__success);
        //失败返回
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(orderNo);
        processNotifyVO.setTrade_no(tradeNo);
        processNotifyVO.setTrade_status(tradeStatus);
        processNotifyVO.setT_trade_status(tTradeStatus);
        processNotifyVO.setRealAmount(realAmount/100);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(map).toString());
        processNotifyVO.setPayment("HHZF");

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}

