package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import net.sf.json.JSONObject;

public class HANYPayServiceImpl extends PayAbstractBaseService implements PayService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(HANYPayServiceImpl.class);

    private static final String ret__failed = "fail";

    private static final String ret__success = "success";

    /**
     * 支付地址
     */
    private String payUrl;

    /**
     * 机构商户号
     */
    private String orgMemberid;

    /**
     * 瀚银商户号
     */
    private String hyMemberid;

    /**
     * 异步回调地址
     */
    private String notifyUrl;

    /**
     * 机构号
     */
    private String orgNo;

    /**
     * 商户密钥
     */
    private String md5Key;


    /**
     * 订单查询地址
     */

    private String queryUrl;


    /**
     * 构造器，初始化参数
     */
    public HANYPayServiceImpl() {
    }

    public HANYPayServiceImpl(Map<String, String> data, String type) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey(type)) {
                JSONObject jsonObject = JSONObject.fromObject(data.get(type));
                if (jsonObject.containsKey("payUrl")) {
                    this.payUrl = jsonObject.getString("payUrl");
                }
                if (jsonObject.containsKey("orgMemberid")) {
                    this.orgMemberid = jsonObject.getString("orgMemberid");
                }
                if (jsonObject.containsKey("hyMemberid")) {
                    this.hyMemberid = jsonObject.getString("hyMemberid");
                }
                if (jsonObject.containsKey("notifyUrl")) {
                    this.notifyUrl = jsonObject.getString("notifyUrl");
                }
                if (jsonObject.containsKey("orgNo")) {
                    this.orgNo = jsonObject.getString("orgNo");
                }
                if (jsonObject.containsKey("md5Key")) {
                    this.md5Key = jsonObject.getString("md5Key");
                }
            }
        }
    }


    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        logger.info("[HANY]瀚银网银支付开始------------------------------------");
        try {
            Map<String, String> data = sealRequest(payEntity, 1);

            String sign = generatorSign(data, 1);
            data.put("signature", sign);//签名
            logger.info("[HANY]瀚银网银支付请求参数：" + JSONObject.fromObject(data));
            String resStr = HttpUtils.generatorForm(data, payUrl);
            logger.info("[HANY]瀚银网银支付响应信息：" + resStr);
            if (StringUtils.isBlank(resStr)) {
                logger.info("[HANY]瀚银网银支付发起HTTP请求无响应结果");
                return PayResponse.error("[HANY]瀚银网银支付发起HTTP请求无响应结果");
            }
            return PayResponse.wy_write(resStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String callback(Map<String, String> data) {
        return null;
    }

    @Override
    public JSONObject smPay(PayEntity entity) {
        return null;
    }

    /**
     * @param
     * @param type 1 网银支付  0 扫码支付
     * @return
     * @throws Exception
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    private Map<String, String> sealRequest(PayEntity payEntity, Integer type) throws Exception {
        try {
            DecimalFormat df = new DecimalFormat("0");
            String uid = UUID.randomUUID().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String toDate = simpleDateFormat.format(new Date());
            Map<String, String> data = new HashMap<>();
            if (type == 1) {
                data.put("signType", "MD5");//签名方法
                data.put("encoding", "UTF-8");//编码方式
                data.put("version", "2.0.0");//版本号
                data.put("insCode", orgNo);//机构号
                data.put("insMerchantCode", orgMemberid);//机构商户号
                data.put("hpMerCode", hyMemberid);//瀚银商户号
                data.put("nonceStr", uid.substring(0, 8));//随机参数
                data.put("orderNo", payEntity.getOrderNo());//商户订单号
                data.put("orderDate", toDate);//商户订单日期YYYYMMDD
                data.put("orderTime", sdf.format(new Date()));//商户订单发送时间YYYYMMDDhhmmss
                data.put("orderAmount", df.format(payEntity.getAmount() * 100));//订单金额
                data.put("currencyCode", "156");//币种
//				data.put("paymentChannel", payEntity.getPayCode());
                data.put("paymentChannel", "");//银行代码
                data.put("frontUrl", notifyUrl);//前台通知地址
                data.put("backUrl", notifyUrl);//后台异步通知地址
                data.put("merReserve", "");//商户自定义域
                data.put("ledger", "");//分账域
                data.put("riskArea", "");//风控域
                data.put("gatewayProductType", "B2C");//网关支付产品类型
                data.put("accNoType", "DEBIT");//卡类型 CREDIT:贷记卡 DEBIT:借记卡
                data.put("clientType", "");//客户端类型01:PC浏览器  02:手机浏览器  03:手机APP 99:其他
            } else {
                data.put("insCode", orgNo);//机构号
                data.put("insMerchantCode", orgMemberid);//机构商户号
                data.put("hpMerCode", hyMemberid);//瀚银商户号
                data.put("orderNo", payEntity.getOrderNo());//商户订单号
                data.put("orderTime", toDate);//商户订单发送时间YYYYMMDDhhmmss
                data.put("orderAmount", df.format(payEntity.getAmount() * 100));//订单金额 单位：分
                data.put("currencyCode", "156");//币种
                data.put("name", "");//姓名
                data.put("idNumber", "");//身份证号
                data.put("accNo", "");//银行卡号
                data.put("telNo", "");//手机号
                data.put("productType", "100000");//产品类型
                data.put("paymentType", payEntity.getPayCode());//支付类型
                data.put("merGroup", "");//商户类型
                data.put("nonceStr", uid.replace("-", ""));//随机参数
                data.put("frontUrl", notifyUrl);//前台通知地址
                data.put("backUrl", notifyUrl);//后台异步通知地址
            }

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HANY]瀚银支付组装请求参数异常：" + e.getMessage());
            return null;
        }
    }

    /**
     * @param type 1： 网银   2：查询
     * @return
     * @throws Exception
     * @Description 生成签名串
     */
    private String generatorSign(Map<String, String> data, int type) throws Exception {
        try {
            StringBuilder sb = new StringBuilder();
            if (type == 1) {
                List<String> commonAttr = commonAttr("1");
                for (String attr : commonAttr) {
                    sb.append(attr + "=").append(data.get(attr)).append("&");
                }
                sb.append("signKey=").append(md5Key);//密钥
            } else if (type == 2) {
                List<String> commonAttr = commonAttr("5");
                for (String attr : commonAttr) {
                    sb.append(data.get(attr)).append("|");
                }
                sb.append(md5Key);//密钥
            } else {
                List<String> commonAttr = commonAttr("3");
                for (String attr : commonAttr) {
                    sb.append(data.get(attr)).append("|");
                }
                sb.append(md5Key);//密钥
            }
            //生成待签名串
            String signStr = sb.toString();
            logger.info("[HANY]瀚银支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[HANY]瀚银支付生成加密签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HANY]瀚银支付生成签名异常：" + e.getMessage());
            return null;
        }
    }

    private List<String> commonAttr(String type) {
        //网银请求参数
        String[] wyReqArray = {"signType", "encoding", "version", "insCode", "insMerchantCode", "hpMerCode", "nonceStr", "orderNo", "orderDate", "orderTime", "orderAmount", "currencyCode", "paymentChannel",
                "frontUrl", "backUrl", "merReserve", "ledger", "riskArea", "gatewayProductType", "accNoType", "clientType"};
        //网银回调参数
        String[] wyCallbackArray = {"signType", "encoding", "version", "insCode", "insMerchantCode", "hpMerCode", "nonceStr", "orderNo", "orderDate", "orderTime", "orderAmount", "currencyCode", "transSeq",
                "transCharge", "settleDate", "merReserved", "statusCode", "statusMsg"};
        //扫码请求参数
        String[] smReqArray = {"insCode", "insMerchantCode", "hpMerCode", "orderNo", "orderTime", "currencyCode", "orderAmount", "name", "idNumber", "accNo", "telNo", "productType", "paymentType", "merGroup",
                "nonceStr", "frontUrl", "backUrl"};
        //扫码回调参数
        String[] smCallbackArray = {"hpMerCode", "orderNo", "transDate", "transStatus", "transAmount", "actualAmount", "transSeq", "statusCode", "statusMsg"};
        //订单查询参数
        String[] queryArray = {"insCode", "insMerchantCode", "hpMerCode", "orderNo", "transDate", "transSeq", "productType", "paymentType", "nonceStr"};

        String[] attrArray = null;
        if (type.equals("1")) {
            attrArray = wyReqArray;
        }
        if (type.equals("2")) {
            attrArray = wyCallbackArray;
        }
        if (type.equals("3")) {
            attrArray = smReqArray;
        }
        if (type.equals("4")) {
            attrArray = smCallbackArray;
        }
        if (type.equals("5")) {
            attrArray = queryArray;
        }
        List<String> list = new ArrayList<>();
        for (String attrStr : attrArray) {
            list.add(attrStr);
        }
        return list;
    }

    /**
     * 功能描述:回调验签
     *
     * @param data
     * @return: boolean
     **/
    private boolean verifyCallback(Map<String, String> data) {

        String payType = data.remove("payType");
        String sourceSign = data.get("signature");
        StringBuilder sb = new StringBuilder();
        if (payType.equals("bank")) {
            List<String> commonAttr = commonAttr("2");
            for (String attr : commonAttr) {
                sb.append(data.get(attr)).append("|");
            }
        } else {
            List<String> commonAttr = commonAttr("4");
            for (String attr : commonAttr) {
                sb.append(data.get(attr)).append("|");
            }
        }
        sb.append(md5Key);//密钥
        //生成待签名串
        String sign = null;
        try {
            String signStr = sb.toString();
            logger.info("[HANY]瀚银回调验签生成待签名串:{}", signStr);
            sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[HANY]瀚银回调验签生成加密签名串:{}", sign);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HANY]瀚银支付回调验签失败：" + e.getMessage());
        }
        return sign.equalsIgnoreCase(sourceSign);
    }

    /**
     * 功能描述:查询订单状态
     *
     * @param orderNo 订单号
     * @return: boolean
     **/
    private boolean getOrderStatus(String orderNo, String orderTime, String tradeNo) {
        try {
            //封装请求参数
            Map<String, String> map = new HashMap<>();
//            机构号
            map.put("insCode", orgNo);
//            机构商户编号
            map.put("insMerchantCode", orgMemberid);
//            瀚银商户号
            map.put("hpMerCode", hyMemberid);
//            商户订单号
            map.put("orderNo", orderNo);
//            商户订单发送时间
            map.put("transDate", orderTime);

            map.put("transSeq", tradeNo);
//            产品类型	productType	N1..30 	M	默认值：100000
            map.put("productType", "100000");
//            支付类型	paymentType	N1..30	M	默认值：网银B2C：2000
            map.put("paymentType", "2000");
            map.put("nonceStr", UUID.randomUUID().toString().substring(0, 8));
            map.put("signature", generatorSign(map, 2));

            logger.info("[HANY]瀚银支付订单查询接口订单{}请求参数{}", orderNo, JSONObject.fromObject(map));
            //发送请求
            String response = HttpUtils.toPostForm(map, queryUrl);
            logger.info("[HANY]瀚银支付订单查询接口响应信息{}", response);

            //解析响应参数
            JSONObject respJson = JSONObject.fromObject(response);
            if (respJson.containsKey("statusCode") && "00".equals(respJson.getString("statusCode"))) {
                if ("00".equals(respJson.getString("transStatus"))) {

                    logger.info("[HANY]瀚银支付订单查询成功,订单" + orderNo + "已支付。");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HANY]瀚银支付订单查询异常");
            return false;
        }
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

        logger.info("[HANY]瀚银支付回调请求参数:{}", JSONObject.fromObject(infoMap));
        if (!MapUtils.isNotEmpty(infoMap)) {
            logger.error("获取回调请求参数为空");
            return ret__failed;
        }


        // 平台商订单号
        String orderNo = infoMap.get("orderNo");

        RechargeDao RechargeDao = (RechargeDao) SpringContextUtils.getBeanByClass(RechargeDao.class);
        RechargeEntity rechargeEntity = RechargeDao.selectByOrderNo(orderNo);
        String type = getPayConfigType(String.valueOf(rechargeEntity.getPayType()));//获取支付类型
        config = config.getJSONObject(type);
        infoMap.put("payType", type);
        //参数验签，从配置中获取
        this.md5Key = config.getString("md5Key");
        this.hyMemberid = config.getString("hyMemberid");
        this.queryUrl = config.getString("queryUrl");
        this.orgNo = config.getString("orgNo");
        this.orgMemberid = config.getString("orgMemberid");
        boolean verifyRequest = verifyCallback(infoMap);

        // 支付商订单号
        String tradeNo = infoMap.get("transSeq");
        //订单状态
        String tradeStatus = infoMap.get("statusCode");
        // 表示成功状态
        String tTradeStatus = "00";
        //实际支付金额
        String orderAmount = infoMap.get("orderAmount");

        //订单发送时间
        String orderTime = infoMap.get("orderTime");
        if (StringUtils.isBlank(orderAmount)) {
            logger.info("获取实际支付金额为空!");
            return ret__failed;
        }
        double realAmount = Double.parseDouble(orderAmount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        //调用查询接口查询订单信息

        boolean orderStatus = getOrderStatus(orderNo, orderTime, tradeNo);
        if (!orderStatus) {
            logger.info(orderNo + "此订单尚未支付成功！");
            return ret__failed;
        }
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
        processNotifyVO.setPayment("HANY");
        processNotifyVO.setConfig(config);

        return super.processSuccessNotify(processNotifyVO, verifyRequest);
    }
}


