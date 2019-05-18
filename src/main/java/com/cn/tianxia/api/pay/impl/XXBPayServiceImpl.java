package com.cn.tianxia.api.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.utils.qyf.ToolKit;

import net.sf.json.JSONObject;

/**
 * @ClassName XXBPayServiceImpl
 * @Description XXBPayServiceImpl
 * @author zw
 * @Date 2018年9月1日 下午8:33:02
 * @version 1.0.0
 */
public class XXBPayServiceImpl implements PayService {

    private String payUrl;

    private String merchantId;

    private String md5Key;

    private String notifyURL;

    private String goodsName;

    private String type;

    private final static Logger logger = LoggerFactory.getLogger(XXBPayServiceImpl.class);

    public XXBPayServiceImpl(Map<String, String> pmap) {
        if (pmap != null) {
            if (pmap.containsKey("payUrl")) {
                payUrl = pmap.get("payUrl");
            }
            if (pmap.containsKey("merchantId")) {
                merchantId = pmap.get("merchantId");
            }
            if (pmap.containsKey("md5Key")) {
                md5Key = pmap.get("md5Key");
            }
            if (pmap.containsKey("notifyURL")) {
                notifyURL = pmap.get("notifyURL");
            }
            if (pmap.containsKey("goodsName")) {
                goodsName = pmap.get("goodsName");
            }
            if (pmap.containsKey("type")) {
                type = pmap.get("type");
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
        String form = scanPay(payEntity);

        return PayUtil.returnPayJson("success", "1", "支付接口请求成功!", payEntity.getUsername(), payEntity.getAmount(),
                payEntity.getOrderNo(), form);
    }

    /**
     * @Description 扫码接口
     * @param payEntity
     * @return
     */
    public String scanPay(PayEntity payEntity) {

        Double amount = payEntity.getAmount();
        String paytype = payEntity.getPayCode();
        String userName = payEntity.getUsername();
        String returnURL = payEntity.getRefererUrl();

        DecimalFormat df = new DecimalFormat("############");
        String money = df.format(amount);

        String timestamp = System.currentTimeMillis() + "";

        String merchantOrderId = payEntity.getOrderNo();

        String tempStr = money + "&" + merchantId + "&" + notifyURL + "&" + returnURL + "&" + merchantOrderId + "&"
                + timestamp + "&" + md5Key;
        String sign = ToolKit.MD5(tempStr, "UTF-8");
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("type", type);
        paramsMap.put("merchantId", merchantId);
        paramsMap.put("money", money);
        paramsMap.put("timestamp", timestamp);
        paramsMap.put("returnURL", returnURL);
        paramsMap.put("merchantOrderId", merchantOrderId);
        paramsMap.put("merchantUid", userName);
        paramsMap.put("sign", sign);
        paramsMap.put("paytype", paytype);
        paramsMap.put("goodsName", goodsName);
        paramsMap.put("notifyURL", notifyURL);

        String form = buildForm(paramsMap, payUrl);

        return form;
    }

    /**
     * @Description 回调验签
     * @param map
     * @return
     */
    @Override
    public String callback(Map<String, String> map) {
        try {
            String orderNo = map.get("orderNo");
            String merchantOrderNo = map.get("merchantOrderNo");
            String money = map.get("money");
            String payAmount = map.get("payAmount");
            String serverSign = map.get("sign");
            String tempStr = orderNo + "&" + merchantOrderNo + "&" + money + "&" + payAmount + "&" + md5Key;

            String localSign = ToolKit.MD5(tempStr, "UTF-8");
            logger.info("本地签名:" + localSign + "      服务器签名:" + serverSign);
            if (serverSign.equalsIgnoreCase(localSign)) {
                return "success";
            }
        } catch (Exception e) {
            logger.info("小熊宝回调验签失败！");
            e.printStackTrace();
            return "";
        }
        return "";
    }

    /**
     * @Description 生成表单
     * @param paramMap
     * @param payUrl
     * @return
     */
    public static String buildForm(Map<String, String> paramMap, String payUrl) {
        // 待请求参数数组
        String FormString = "<body onLoad=\"document.actform.submit()\">正在处理请稍候.....................<form  id=\"actform\" name=\"actform\" method=\"post\" action=\""
                + payUrl + "\">";
        for (String key : paramMap.keySet()) {
            FormString += "<input name=\"" + key + "\" type=\"hidden\" value='" + paramMap.get(key) + "'>\r\n";
        }
        FormString += "</form></body>";
        logger.info("小熊宝支付表单:" + FormString);
        return FormString;
    }

}
