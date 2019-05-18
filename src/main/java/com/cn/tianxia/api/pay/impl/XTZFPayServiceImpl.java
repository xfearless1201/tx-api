package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.domain.txdata.v2.RechargeDao;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.project.v2.RechargeEntity;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.JSONUtils;
import com.cn.tianxia.api.utils.SpringContextUtils;
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
import java.util.TreeMap;

/**
 * @Auther: Bing
 * @Date: 2019/1/22 10:25
 * @Description: 芯支付
 */
public class XTZFPayServiceImpl extends PayAbstractBaseService implements PayService {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(XTZFPayServiceImpl.class);
    /**
     * 回调失败响应信息
     */
    private static final String ret__failed = "fail";
    /**
     * 回调成功响应信息
     */
    private static final String ret__success = "success";
    /**
     * 支付地址
     */
    private String payUrl;
    /**
     * 商户编号
     */
    private String payMemberid;
    /**
     * 商户密钥
     */
    private String secretKey;
    /**
     * 回调地址
     */
    private String payNotifyUrl;
    /**
     * 查询地址
     */
    private String searchOrderUrl;


    public XTZFPayServiceImpl() {
    }

    public XTZFPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("payMemberid")) {
                this.payMemberid = data.get("payMemberid");
            }
            if (data.containsKey("secretKey")) {
                this.secretKey = data.get("secretKey");
            }
            if (data.containsKey("payNotifyUrl")) {
                this.payNotifyUrl = data.get("payNotifyUrl");
            }
            if (data.containsKey("searchOrderUrl")) {
                this.searchOrderUrl = data.get("searchOrderUrl");
            }
        }
    }

    /**
     * 网银支付
     */
    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 扫码支付
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[XTZF]芯支付扫码支付开始====================START========================");
        try {
            // 获取支付请求参数
            Map<String, String> data = sealRequest(payEntity);
            logger.info("[XTZF]芯支付扫码支付请求参数:" + JSONObject.fromObject(data));
            // 发起支付请求
            String resStr = HttpUtils.toPostForm(data, payUrl);
            logger.info("[XTZF]芯支付扫码支付响应信息:" + resStr);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[XTZF]芯支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[XTZF]芯支付扫码支付发起HTTP请求无响应结果");
            }
            JSONObject resJsonObj = JSONObject.fromObject(resStr);
            if (JSONUtils.compare(resJsonObj, "result", "success")) {
                resJsonObj = resJsonObj.getJSONObject("data");
                if (StringUtils.isNotBlank(payEntity.getMobile())) {
                    return PayResponse.sm_link(payEntity, resJsonObj.getString("trade_qrcode"), "下单成功");
                }
                return PayResponse.sm_qrcode(payEntity, resJsonObj.getString("trade_qrcode"), "下单成功");
            }
            return PayResponse.error(resJsonObj.getString("msg"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XTZF]芯支付扫码支付异常:" + e.getMessage());
            return PayResponse.error("[XTZF]芯支付扫码支付异常");
        }
    }

    /**
     * @param map
     * @return
     * @Description 回调验签
     */
    @Override
    public String callback(Map<String, String> map) {
        return null;
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[XTZF]芯支付扫码支付封装支付请求参数开始===========================START=================");
        try {
            // 创建存储参数对象
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("0").format(entity.getAmount() * 100);// 订单金额 单位：分
            String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());// 订单时间
            data.put("mer_id", payMemberid);//商户号
            data.put("timestamp", orderTime);//时间戳
            data.put("terminal", entity.getPayCode());// 支付方式
            data.put("version", "01");// 版本号
            data.put("businessnumber", entity.getOrderNo());// 订单号
            data.put("amount", amount);// 订单金额 单位：分
            data.put("backurl", entity.getRefererUrl());// 服务端通知
            data.put("failUrl", entity.getRefererUrl());// 服务端通知
            data.put("ServerUrl", payNotifyUrl);// 服务端通知
            data.put("goodsName", "recharge");// 商品名称
            data.put("sign", generatorSign(data, "0"));// 签名
            data.put("sign_type", "md5");// 签名类型
            logger.info("[XTZF]芯支付封装签名参数:" + JSONObject.fromObject(data).toString());
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XTZF]芯支付封装请求参数异常:" + e.getMessage());
            throw new Exception("[XTZF]芯支付封装支付请求参数异常!");
        }
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名
     */
    private String generatorSign(Map<String, String> data, String type) {
        try {
            // 排序
            Map<String, String> treeMap = new TreeMap<>(data);
            StringBuffer sb = new StringBuffer();
            for (String key : treeMap.keySet()) {
                String val = treeMap.get(key);
                if ("2".equals(type)) {
                    if ("sign".equalsIgnoreCase(key)) {
                        continue;
                    }
                } else {
                    if (StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) {
                        continue;
                    }
                }
                sb.append(key).append("=").append(val).append("&");
            }
            // 加上签名秘钥
            sb.append(secretKey);
            String signStr = sb.toString();
            logger.info("[XTZF]芯支付生成待加密签名串：" + signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[XTZF]芯支付生成MD5加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XTZF]芯支付扫码支付生成签名异常:" + e.getMessage());
            return "";
        }
    }

    private boolean verifyCallback(Map<String, String> data) {
        try {
            String sourceSign = data.remove("sign");
            data.remove("sign_type");
            String sign = generatorSign(data, "2");
            logger.info("[XTZF]芯支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
            return sourceSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XTZF]芯支付回调生成签名串异常" + e.getMessage());
            return false;
        }
    }

    public boolean searchOrder(String orderNo, String orderTime) {
        try {
            RechargeDao rechargeDao = (RechargeDao) SpringContextUtils.getBeanByClass(RechargeDao.class);
            RechargeEntity rechargeEntity = rechargeDao.selectByOrderNo(orderNo);
            Map<String, String> data = new HashMap<>();
            data.put("mer_id", payMemberid);
            data.put("timestamp", orderTime);
            data.put("terminal", rechargeEntity.getBankCode());
            data.put("version", "01");
            data.put("businessnumber", orderNo);
            data.put("sign", generatorSign(data, "1"));
            data.put("sign_type", "md5");
            logger.info("[XTZF]芯支付扫码支付回调订单查询请求参数：" + JSONObject.fromObject(data));
            String resStr = HttpUtils.toPostForm(data, searchOrderUrl);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[XTZF]芯支付回调订单查询发起HTTP请求无响应结果");
                return false;
            }
            JSONObject resJsonObj = JSONObject.fromObject(resStr);
            logger.info("[XTZF]芯支付扫码支付回调订单查询响应信息：" + resJsonObj);

            if (!JSONUtils.compare(resJsonObj, "result", "success")) {
                return false;
            }
            resJsonObj = resJsonObj.getJSONObject("data");
            if (!JSONUtils.compare(resJsonObj, "status", "成功")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[XTZF]芯支付回调订单查询异常：" + e.getMessage());
            return false;
        }
    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);  //获取回调请求参数
        logger.info("[XTZF]芯支付回调请求参数：" + JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("XTZFNotify获取回调请求参数为空");
            return ret__failed;
        }
        //参数验签
        this.payMemberid = config.getString("payMemberid");//从配置中获取
        this.secretKey = config.getString("secretKey");//从配置中获取
        this.searchOrderUrl = config.getString("searchOrderUrl");//从配置中获取

        String order_no = infoMap.get("businessnumber");// 平台订单号
        String orderTime = infoMap.get("transactiondate");// 平台订单号
        //调用订单查询接口
        boolean result = searchOrder(order_no, orderTime);
        if (!result) {
            return ret__failed;
        }

        boolean verifyRequest = verifyCallback(infoMap);
        String order_amount = infoMap.get("amount");//单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info("XTZFNotify获取实际支付金额为空!");
            return ret__failed;
        }

        double realAmount = Double.parseDouble(order_amount) / 100;

        String trade_no = "XTZF" + System.currentTimeMillis();// 第三方订单号
        String trade_status = infoMap.get("result");//订单状态:成功
        String t_trade_status = "success";// 表示成功状态

        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
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
        processNotifyVO.setPayment("XTZF");
        processNotifyVO.setConfig(config);
        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}
