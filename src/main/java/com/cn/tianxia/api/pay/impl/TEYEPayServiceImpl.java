package com.cn.tianxia.api.pay.impl;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName TEYEPayServiceImpl
 * @Description 天眼支付
 * @Date 2018年12月27日 下午8:13:17
 */
public class TEYEPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(TEYEPayServiceImpl.class);

    private String partner;//商户号

    private String payUrl;//支付请求地址

    private String notifyUrl;//回调通知地址

    private String md5Key;//签名秘钥

    //构造器,初始化参数
    public TEYEPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("partner")) {
                this.partner = data.get("partner");
            }
            if (data.containsKey("payUrl")) {
                this.payUrl = data.get("payUrl");
            }
            if (data.containsKey("notifyUrl")) {
                this.notifyUrl = data.get("notifyUrl");
            }
            if (data.containsKey("md5Key")) {
                this.md5Key = data.get("md5Key");
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
        logger.info("[TEYE]天眼支付扫码支付开始=================START===================");
        try {

            Map<String, String> data = sealRequest(payEntity);
            String sign = generatorSign(data, 1);
            data.put("sign", sign);
            logger.info("[TEYE]天眼支付扫码支付请求参数报文:{}", JSONObject.fromObject(data).toString());
            String formStr = HttpUtils.generatorForm(data, payUrl);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TEYE]天眼支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[TEYE]天眼支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[TEYE]天眼支付回调验签开始===============START====================");
        try {
            //获取原签名串
            String sourceSign = data.get("sign");
            String sign = generatorSign(data, 0);
            logger.info("[TEYE]天眼支付回调生成签名串：{}--源签名串：{}", sign, sourceSign);
            if (sourceSign.equalsIgnoreCase(sign)) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TEYE]天眼支付回调验签异常:{}", e.getMessage());
        }
        return "faild";
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 封装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[TEYE]天眼支付组装支付请求参数开始===================START===============");
        try {
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("##").format(entity.getAmount());//订单金额
            data.put("partner", partner);//商户ID   Y   商户id,由天眼支付API分配
            data.put("banktype", entity.getPayCode());//接入类型  Y   接入类型，具体参考附录1,default为跳转到天眼支付API接口进行选择支付
            data.put("paymoney", amount);//金额    Y   单位元（人民币）
            data.put("ordernumber", entity.getOrderNo());//商户订单号  Y   商户系统订单号，该订单号将作为天眼支付API接口的返回数据。该值需在商户系统内唯一，天眼支付API系统暂时不检查该值是否唯一
            data.put("callbackurl", notifyUrl);//下行异步通知地址   Y   下行异步通知的地址，需要以http://开头且没有任何参数
            data.put("hrefbackurl", entity.getRefererUrl());//下行同步通知地址   N   下行同步通知过程的返回地址(在支付完成后天眼支付API接口将会跳转到的商户系统连接地址)。
            data.put("attach", "TOP-UP");//备注信息    N   备注信息，下行中会原样返回。若该值包含中文，请注意编码
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TEYE]天眼支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[TEYE]天眼支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @param type 1 支付签名 2 回调签名
     * @return
     * @throws Exception
     * @Description 签名
     */
    private String generatorSign(Map<String, String> data, int type) throws Exception {
        logger.info("[TEYE]天眼支付生成签名开始==================START=========================");
        try {

            StringBuffer sb = new StringBuffer();
            if (type == 1) {
                sb.append("partner=").append(partner).append("&");
                sb.append("banktype=").append(data.get("banktype")).append("&");
                sb.append("paymoney=").append(data.get("paymoney")).append("&");
                sb.append("ordernumber=").append(data.get("ordernumber")).append("&");
                sb.append("callbackurl=").append(data.get("callbackurl"));
            } else {
                sb.append("partner=").append(partner).append("&");
                sb.append("ordernumber=").append(data.get("ordernumber")).append("&");
                sb.append("orderstatus=").append(data.get("orderstatus")).append("&");
                sb.append("paymoney=").append(data.get("paymoney"));
            }
            sb.append(md5Key);

            String signStr = sb.toString();
            logger.info("[TEYE]天眼支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[TEYE]天眼支付生成加密签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[TEYE]天眼支付生成签名异常:{}", e.getMessage());
            throw new Exception("[TEYE]天眼支付生成签名异常");
        }
    }
}
