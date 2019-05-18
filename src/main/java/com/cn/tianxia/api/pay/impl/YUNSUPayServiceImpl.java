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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName YUNSUPayServiceImpl
 * @Description YunSu支付
 * @Date 2018年12月30日 上午11:15:20
 */
public class YUNSUPayServiceImpl implements PayService {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(YUNSUPayServiceImpl.class);

    private String merId;// 商户号

    private String payUrl;// 支付地址

    private String notifyUrl;// 回调地址

    private String md5Key;// 秘钥

    // 构造器,初始化参数
    public YUNSUPayServiceImpl(Map<String, String> data) {
        if (MapUtils.isNotEmpty(data)) {
            if (data.containsKey("merId")) {
                this.merId = data.get("merId");
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
        logger.info("[YUNSU]云速支付扫码支付开始 ==================START=================");
        try {
            //获取支付请求参数
            String type = payEntity.getPayCode();
            Map<String, String> data = sealRequest(payEntity);
            //生成签名串
            String sign = generatorSign(data, 1);
            data.put("sign", sign);
            JSONObject jsonData = JSONObject.fromObject(data);
            logger.info("[YUNSU]云速支付扫码支付请求参数报文:{}", jsonData.toString());
            //发起支付请求
            String response = HttpUtils.toPostJsonStr(jsonData, payUrl);
            if (StringUtils.isBlank(response)) {
                logger.info("[YUNSU]云速支付扫码支付发起HTTP请求无响应结果");
                return PayResponse.error("[YUNSU]云速支付扫码支付发起HTTP请求无响应结果");
            }
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(response);
            logger.info("[YUNSU]云速支付扫码支付请求响应报文:{}", jsonObject);
            if (jsonObject.containsKey("code") && "0".equals(jsonObject.getString("code"))) {
                //成功
                String object = jsonObject.getJSONObject("object").getString("data");
                return PayResponse.sm_link(payEntity, object, "下单成功");
            }

            return PayResponse.error("下单失败:" + response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YUNSU]云速支付扫码支付异常:{}", e.getMessage());
            return PayResponse.error("[YUNSU]云速支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[YUNSU]云速支付回调验签开始===================START=================");
        try {
            //获取回调验签原签名串
            String sourceSign = data.get("sign");
            logger.info("[YUNSU]云速支付回调验签获取原签名串:{}", sourceSign);
            //生成签名串
            String sign = generatorSign(data, 0);
            logger.info("[YUNSU]云速支付回调验签生成签名串:{}", sign);
            if (sourceSign.equalsIgnoreCase(sign)) {
                return "success";
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YUNSU]云速支付回调验签异常:{}", e.getMessage());
        }
        return "faild";
    }

    /**
     * @param entity
     * @return
     * @throws Exception
     * @Description 组装支付请求参数
     */
    private Map<String, String> sealRequest(PayEntity entity) throws Exception {
        logger.info("[YUNSU]云速支付组装支付请求参数开始===================START==============");
        try {
            Map<String, String> data = new HashMap<>();
            String amount = new DecimalFormat("##").format(entity.getAmount() * 100);
            data.put("version", "1.0");// 版本号:默认 1.0
            data.put("merId", merId);//商户编号. 商户后台获取
            data.put("orderId", entity.getOrderNo());//商户订单号.商户唯一订单号
            data.put("totalMoney", amount);//订单金额.单位:分，大于1块
            data.put("ip", entity.getIp());//用户真实ip,一定真实用户ip,否者微信拉取会出问题。（该字段是必填字段针对微信H5外部拉取）
            data.put("tradeType", entity.getPayCode());//支付类型:支付宝：alipay,微信： wechat,银联: unionpay,QQ 钱包: qqpay
            data.put("describe", "TOP-UP");//商品描述:商品描述
            data.put("notify", notifyUrl);//异步通知 URL:数据异步通知(可在后台配置，后台配置的通知地址优先)
            data.put("redirectUrl", entity.getRefererUrl());//同步跳转 URL:不能带有任何参数(某些通道无效)
//            data.put("remark","");//订单备注说明:可为空，如果传递必须为字符串或者数据组合
            data.put("fromtype", "wap");//支付来源:wap : 普通 wap , weixinwap : 微信内 wap， weixinouterwap : 微信外wap（ip字段必传，并且是真实的用户ip，否者无效）
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YUNSU]云速支付组装支付请求参数异常:{}", e.getMessage());
            throw new Exception("[YUNSU]云速支付组装支付请求参数异常");
        }
    }

    /**
     * @param data
     * @param type 1 支付 2 回调
     * @return
     * @throws Exception
     * @Description 签名
     */
    private String generatorSign(Map<String, String> data, int type) throws Exception {
        logger.info("[YUNSU]云速支付生成签名开始===================START==================");
        try {
            StringBuffer sb = new StringBuffer();
            if (type == 1) {
                //md5 签名串:参照签名校验规则------- 签名校验规则如下:{value}要替换成接收到的值，{key}要替换成平台分配的接入密钥，
                //可在商户后台获取.merId={value}&orderId={value}&totalMoney={value}&tradeType={value}&{key}使用 md5 
                //签名上面拼接的字符串即可生成 32 位密文再转换成大写.
                sb.append("merId=").append(merId).append("&");
                sb.append("orderId=").append(data.get("orderId")).append("&");
                sb.append("totalMoney=").append(data.get("totalMoney")).append("&");
                sb.append("tradeType=").append(data.get("tradeType")).append("&");
            } else {
                //回调签名规则:签名串：参照签名说明----------使用接收到的值替换每个参数的{value},{key}使用商户 key： 
                //code{value}merId{value}money{value}orderId{value}payWay{value}remark{value}time{value}tradeId{ value}{key}然后 md5 
                //签名上面拼接的字符串即可生成 32 位密文并转为大写---注意:{} 需要去掉.
                sb.append("code").append(data.get("code"));
                sb.append("merId").append(data.get("merId"));
                sb.append("money").append(data.get("money"));
                sb.append("orderId").append(data.get("orderId"));
                sb.append("payWay").append(data.get("payWay"));
                sb.append("remark").append(data.get("remark"));
                sb.append("time").append(data.get("time"));
                sb.append("tradeId").append(data.get("tradeId"));
            }
            String signStr = sb.append(md5Key).toString();
            logger.info("[YUNSU]云速支付生成待签名串:{}", signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr);
            logger.info("[YUNSU]云速支付生成加密签名串:{}", sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[YUNSU]云速支付生成签名异常:{}", e.getMessage());
            throw new Exception("[YUNSU]云速支付生成签名异常");
        }
    }
}
