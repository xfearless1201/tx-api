package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.domain.txdata.v2.RechargeDao;
import com.cn.tianxia.api.pay.PayAbstractBaseService;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.project.v2.RechargeEntity;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.SpringContextUtils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.utils.pay.SHANUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName SHANPayServiceImpl
 * @Description 闪付
 * @Date 2018年12月22日 上午10:16:37
 */
public class SHANPayServiceImpl extends PayAbstractBaseService implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(SHANPayServiceImpl.class);

    private String merno;//商户号

    private String payUrl;//支付地址

    private String notifyUrl;//回调地址

    private String reqSecret;//请求秘钥

    private String resSecret;//响应秘钥

    private String form;

    private String mchId; //手机端商户号

    private String mreqSecret; //手机端请求密钥

    private String mrepsSecret; //手机端响应密钥

    public SHANPayServiceImpl() {
    }

    public SHANPayServiceImpl(Map<String, String> data, String type) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey(type)) {
                JSONObject jsonObject = JSONObject.fromObject(data.get(type));
                if (jsonObject.containsKey("merno")) {
                    this.merno = jsonObject.getString("merno");
                }
                if (jsonObject.containsKey("payUrl")) {
                    this.payUrl = jsonObject.getString("payUrl");
                }
                if (jsonObject.containsKey("notifyUrl")) {
                    this.notifyUrl = jsonObject.getString("notifyUrl");
                }
                if (jsonObject.containsKey("reqSecret")) {
                    this.reqSecret = jsonObject.getString("reqSecret");
                }
                if (jsonObject.containsKey("resSecret")) {
                    this.resSecret = jsonObject.getString("resSecret");
                }
                if (jsonObject.containsKey("form")) {
                    this.form = jsonObject.getString("form");
                }
                if (jsonObject.containsKey("mchId")) {
                    this.mchId = jsonObject.getString("mchId");
                }
                if (jsonObject.containsKey("mreqSecret")) {
                    this.mreqSecret = jsonObject.getString("mreqSecret");
                }
                if (jsonObject.containsKey("mrespSecret")) {
                    this.mrepsSecret = jsonObject.getString("mrespSecret");
                }
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[SHAN]闪付网银支付开始==================START==================");
        try {

            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity, 1);
            //生成签名串
            String sign = generatorSign(data, 1, reqSecret);
            data.put("sign", sign);
            logger.info("[SHAN]闪付网银支付请求参数报文:{}", JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String response = SHANUtils.sendHttpReq(payUrl, JSONObject.fromObject(data).toString(), "UTF-8");
            if (StringUtils.isBlank(response)) {
                logger.info("[SHAN]闪付网银支付失败,发起HTTP请求无响应结果");
                return PayResponse.error("[SHAN]闪付网银支付失败,发起HTTP请求无响应结果");
            }
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[SHAN]闪付网银支付失败,发起HTTP请求响应结果:{}", jsonObject);

            if (jsonObject.containsKey("code") && "20000".equals(jsonObject.getString("code"))) {
                //下单成功
                String htmlStr = jsonObject.getJSONObject("data").getString("pay_html");
                return PayResponse.wy_write(htmlStr);
            }
            return PayResponse.wy_write("下单失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SHAN]闪付网银支付异常:{}", e.getMessage());
            return PayResponse.error("[SHAN]闪付网银支付异常");
        }
    }

    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[SHAN]闪付扫码支付开始==================START==================");

        if (!StringUtils.isBlank(payEntity.getMobile())) {
            this.reqSecret = mreqSecret;
        }
        try {
            //获取支付请求参数
            Map<String, String> data = sealRequest(payEntity, 0);
            //生成签名串
            String sign = generatorSign(data, 1, reqSecret);
            data.put("sign", sign);
            logger.info("[SHAN]闪付扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());
            String response = SHANUtils.sendHttpReq(payUrl, JSONObject.fromObject(data).toString(), "UTF-8");
            if (StringUtils.isBlank(response)) {
                logger.info("[SHAN]闪付扫码支付失败,发起HTTP请求无响应结果");
                return PayResponse.error("[SHAN]闪付扫码支付失败,发起HTTP请求无响应结果");
            }
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[SHAN]闪付扫码支付HTTP请求响应结果:{}", jsonObject);

            if (jsonObject.containsKey("code") && "20000".equals(jsonObject.getString("code"))) {
                //下单成功
                String htmlStr;
                //支付宝返回pay_url
                if ("7".equals(payEntity.getPayType())) {
                    htmlStr = jsonObject.getJSONObject("data").getString("pay_html");
                    htmlStr = getResStr(htmlStr);
                    logger.info("响应信息：" + htmlStr);
                    return PayResponse.sm_form(payEntity, htmlStr, "下单成功");
                } else {

                    htmlStr = jsonObject.getJSONObject("data").getString("pay_url");
                    if ("".equals(payEntity.getMobile())) {
                        return PayResponse.sm_qrcode(payEntity, htmlStr, "下单成功");
                    }
                    return PayResponse.sm_link(payEntity, htmlStr, "下单成功");
                }
            }
            return PayResponse.error("下单失败:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SHAN]闪付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[SHAN]闪付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    private String getResStr(String formStr) {
        int endIndex = formStr.indexOf("<script>");
        formStr = formStr.substring(0, endIndex);
        formStr = formStr.replace(form, "actform");
        StringBuffer sb = new StringBuffer();
        sb.append("<body onLoad=\"document.actform.submit()\">正在处理请稍候.....................");
        sb.append(formStr);
        sb.append("</body>");
        return sb.toString();
    }

    /**
     * @param entity
     * @param type   1 网银   其他 扫码
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity, int type) throws Exception {
        logger.info("[SHAN]闪付支付组装支付请求参数开始================START=================");

        if (!StringUtils.isBlank(entity.getMobile())) {
            this.merno = mchId;
        }
        try {
            Map<String, String> data = new HashMap<>();

            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);

            data.put("out_trade_no", entity.getOrderNo());//商户订单号 最大长度30  Y   字符串，只允许使用字母、数字、- 、_,并以字母或数字开头，每商户提交的订单号，必须在自身账户交易中唯一
            data.put("amount", amount);//商户订单金额  数字  Y   单位（分）
            data.put("subject", "TOP-UP");//订单标题   最大长度50  Y
            data.put("merchant_id", merno);//商户id       Y   商户在平台的唯一标识
            if (type == 1) {
                data.put("biz_code", "3001");//业务代码      Y   3001：网关支付
                data.put("bank_code", entity.getPayCode());//银行代码     Y   支持部分银行（该字段不参与签名）
            } else {
                data.put("biz_code", entity.getPayCode());
            }
            data.put("notify_url", notifyUrl);//异步回调地址  长度最大256 N   接收异步通知地址，合法URL
            data.put("sign", "");//签名        Y   以上必填字段参与签名
            data.put("version", "2");
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SHAN]闪付支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[SHAN]闪付支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @param type 1 支付 其他 回调
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    private String generatorSign(Map<String, String> data, int type, String secret) throws Exception {
        logger.info("[SHAN]闪付生成签名串开始=====================START=================");
        try {
            SortedMap<String, Object> orderedMap = new TreeMap<String, Object>();
            Set<String> pKeys = data.keySet();
            if (type == 1) {
                for (String key : pKeys) {
                    if (data.get(key) != null && !"sign".equalsIgnoreCase(key)
                            && !"notify_url".equalsIgnoreCase(key) && !"bank_code".equalsIgnoreCase(key) &&
                            !"version".equalsIgnoreCase(key)) {
                        orderedMap.put(key, data.get(key));
                    }
                }
            } else {
                for (String key : pKeys) {
                    if (data.get(key) != null && !"sign".equalsIgnoreCase(key)) {
                        orderedMap.put(key, data.get(key));
                    }
                }
            }
            pKeys = orderedMap.keySet();
            List<String> temp = new ArrayList<String>();
            for (String key : pKeys) {
                temp.add(key + "=" + orderedMap.get(key));
            }
            return SHANUtils.parse(StringUtils.join(temp.toArray(), "&") + secret, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SHAN]闪付生成签名串异常:{}", e.getMessage());
            throw new Exception("[SHAN]闪付生成签名串异常");
        }
    }

    /**
     * 功能描述:回调验签
     *
     * @param data
     * @return: boolean
     **/
    private boolean verifyCallback(Map<String, String> data) {
        logger.info("[SHAN]闪付支付回调验签开始==============START===========");

        //获取回调通知原签名串
        String sourceSign = data.remove("sign");
        //生成验签签名串
        String sign = null;
        try {
            sign = generatorSign(data, 0, resSecret);
            logger.info("[SHAN]闪付支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SHAN]闪付支付生成加密串异常:{}", e.getMessage());
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

        //获取回调请求参数
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);

        logger.info("[SHAN]闪付支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }

        // 平台订单号
        String orderNo = infoMap.get("out_trade_no");

        RechargeDao rechargeDao = (RechargeDao) SpringContextUtils.getBeanByClass(RechargeDao.class);
        RechargeEntity rechargeEntity = rechargeDao.selectByOrderNo(orderNo);
        String type = getPayConfigType(String.valueOf(rechargeEntity.getPayType()));//获取支付类型
        config = config.getJSONObject(type);
        //参数验签，从配置中获取
        if ("24".equals(String.valueOf(rechargeEntity.getPayType()))) {
            this.resSecret = config.getString("mrespSecret");
        } else {
            this.resSecret = config.getString("resSecret");
        }

        // 第三方订单号
        String tradeNo = infoMap.get("trade_no");
        //订单状态
        String tradeStatus = "0000";
        // 表示成功状态
        String tTradeStatus = "0000";
        //实际支付金额
        String orderAmount = infoMap.get("amount");
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        boolean verifyRequest = verifyCallback(infoMap);

        ProcessNotifyVO processNotifyVO = new ProcessNotifyVO();
        processNotifyVO.setRet__success(ret__success);
        processNotifyVO.setRet__failed(ret__failed);
        processNotifyVO.setIp(ip);
        processNotifyVO.setOrder_no(orderNo);
        processNotifyVO.setTrade_no(tradeNo);
        processNotifyVO.setTrade_status(tradeStatus);
        processNotifyVO.setT_trade_status(tTradeStatus);
        processNotifyVO.setRealAmount(realAmount / 100);
        //回调参数
        processNotifyVO.setInfoMap(JSONObject.fromObject(infoMap).toString());
        processNotifyVO.setPayment("SHAN");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}
