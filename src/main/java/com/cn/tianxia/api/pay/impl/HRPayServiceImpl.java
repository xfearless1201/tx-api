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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vicky
 * @version 1.0.0
 * @ClassName HRPayServiceImpl
 * @Description 汇融支付渠道：支付宝扫码、支付宝H5
 * @Date 2019/3/22 10 18
 **/
public class HRPayServiceImpl extends PayAbstractBaseService implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(HRPayServiceImpl.class);
    private static String ret__success = "success";  //只有返回success 才算成功 其他的都不算成功
    private static String ret__failed = "fail";   //失败返回字符串
    private String mch_id;//商户Id
    private String notifyUrl;//回调地址
    private String payUrl;//支付地址
    private String md5key;//密钥
    private boolean verifySuccess = true;//回调验签默认状态为true

    public HRPayServiceImpl() {
    }

    public HRPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("mch_id")) {
                this.mch_id = data.get("mch_id");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("md5key")) {
                this.md5key = data.get("md5key");
            }
        }
    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config) {
        logger.info("[HR]汇融支付扫码支付回调开始==================================start===============================");

        this.mch_id = config.getString("mch_id");
        this.md5key = config.getString("md5key");
        this.notifyUrl = config.getString("notifyUrl");

        //商户返回信息
        Map<String, String> dataMap = ParamsUtils.getNotifyParams(request);
        logger.info("[HR]汇融支付商户返回信息：" + dataMap);

        String trade_no = dataMap.get("order_id");//第三方订单号，流水号
        String order_no = dataMap.get("mch_order_id");//支付订单号
        String amount = dataMap.get("amount");//商户订单总金额，订单总金额以元为单位，精确到小数点后两位
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);  //回调ip

        String trade_status = "00";  //第三方支付状态，支付商的文档中没有写明此状态,此处为自定义的值
        String t_trade_status = trade_status;

        if (StringUtils.isBlank(trade_no)) {
            logger.info("[HR]汇融支付获取的流水单号为空");
            return ret__failed;
        }
        if (StringUtils.isBlank(amount)) {
            logger.info("[HR]汇融支付回调订单金额为空");
            return ret__failed;
        }

        //支付商没有订单查询接口(只有订单支付成功了才会有支付通知，其他情况都不会有通知)

        //写入数据库
        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setOrder_no(order_no);
        processNotifyVO.setRealAmount(Double.parseDouble(amount) / 100);//以分为单位
        processNotifyVO.setIp(ip);
        processNotifyVO.setTrade_no(trade_no);
        processNotifyVO.setTrade_status(trade_status);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setInfoMap(JSONObject.fromObject(dataMap).toString());
        processNotifyVO.setT_trade_status(t_trade_status);
        processNotifyVO.setConfig(config);
        processNotifyVO.setPayment("HR");

        //回调验签
        if ("fail".equals(callback(dataMap))) {
            verifySuccess = false;
            logger.info("[HR]汇融支付回调验签失败");
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
     *
     * @param payEntity
     * @return
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[HR]汇融支付扫码支付开始==================================start===============================");
        try {
            Map<String, String> dataMap = sealRequest(payEntity);
            String sign = generatorSign(dataMap);
            dataMap.put("sign", sign);
            logger.info("[HR]汇融支付扫码支付请求参数：" + dataMap);

            String response = HttpUtils.toPostJsonStr(JSONObject.fromObject(dataMap), payUrl);
            if (StringUtils.isEmpty(response)) {
                return PayResponse.error("[HR]汇融支付扫码支付响应信息为空");
            }
            JSONObject jb = JSONObject.fromObject(response);
            logger.info("[HR]汇融支付扫码支付扫码支付响应信息：" + jb);
            if (jb.containsKey("result_code") && "0".equals(jb.get("result_code")) && "success".equals(jb.get("result_msg"))) {
                String url = String.valueOf(jb.get("qrcode_url"));
                return PayResponse.sm_link(payEntity, url, "下单成功");
            }
            return PayResponse.error("[HR]汇融支付下单失败：" + jb);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HR]汇融支付扫码支付异常:" + e.getMessage());
            return PayResponse.error("[HR]汇融支付扫码支付异常:" + e.getMessage());
        }
    }

    /**
     * 回调验签
     *
     * @param data
     * @return
     */
    @Override
    public String callback(Map<String, String> data) {
        logger.info("[HR]汇融支付扫码支付回调验签开始==================================start===============================");

        String sign = generatorSign(JSONObject.fromObject(data));
        String sourceSign = data.remove("sign");
        logger.info("[HR]汇融支付扫码支付生成签名串：{}--源签名串：{}", sign, sourceSign);
        if (sign.equals(sourceSign)) {
            return ret__success;
        }
        return ret__failed;
    }

    /**
     * 生成签名
     *
     * @param data
     * @return
     */
    public String generatorSign(Map<String, String> data) {
        logger.info("[HR]汇融支付扫码支付签名开始==================================start===============================");
        //示例：String sign = MD5(mch_id+amount+mch_order_id+key, "utf-8"); 32位小写
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(data.get("mch_id")).append(data.get("amount")).append(data.get("mch_order_id")).append(md5key);
            logger.info("[HR]汇融支付扫码支付生成待签名串：" + sb.toString());
            String sign = MD5Utils.md5toUpCase_32Bit(sb.toString()).toLowerCase();
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HR]汇融支付扫码支付生成签名串异常：" + e.getMessage());
            return "[HR]汇融支付扫码支付生成签名串异常";
        }
    }

    /**
     * 参数组装
     *
     * @param payEntity
     * @return
     */
    public Map<String, String> sealRequest(PayEntity payEntity) {

        Map<String, String> dataMap = new HashMap<>();
        String amount = new DecimalFormat("##").format(payEntity.getAmount() * 100);

        dataMap.put("mch_id", mch_id);//1234567商户id
        dataMap.put("amount", amount);//10000订单金额单位分。不能小于100
        dataMap.put("mch_order_id", payEntity.getOrderNo());//98776475商户订单号,不可传入重复
        dataMap.put("out_channer", payEntity.getPayCode());//zfbhb支付通道固定值zfbhb
        dataMap.put("notify_url", notifyUrl);//http://www.xx.com异步通知订单支付成功
        dataMap.put("ip", payEntity.getIp());//付款用户真实IP
        dataMap.put("attach", "TOP-UP");//透传原样返回字段

        return dataMap;
    }
}
