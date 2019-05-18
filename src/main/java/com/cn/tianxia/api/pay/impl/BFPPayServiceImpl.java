package com.cn.tianxia.api.pay.impl;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.pay.PayResponse;
import com.cn.tianxia.api.utils.MD5Encoder;
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

/**
 *
 * @ClassName SKPPayServiceImpl
 * @Description 贝富支付
 * @author  Vicky
 * @Date 2019年02月15日 下午10:12:42
 * @version 1.0.0
 */
public class BFPPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(BFPPayServiceImpl.class);

    private String sid;//商户号

    private String payUrl;//支付请求地址

    private String notifyUrl;//回调地址

    private String md5key;//签名key

    private String syspwd;//系统密码验证，加密时用的关键参数

    private String deskey;//加密时用的关键参数

    //构造器,初始化参数
    public BFPPayServiceImpl(Map<String,String> data) {
        if(MapUtils.isNotEmpty(data)){
            if(data.containsKey("sid")){
                this.sid = data.get("sid");
            }
            if(data.containsKey("payUrl")){
                this.payUrl = data.get("payUrl");
            }
            if(data.containsKey("notifyUrl")){
                this.notifyUrl = data.get("notifyUrl");
            }
            if(data.containsKey("md5key")){
                this.md5key = data.get("md5key");
            }
            if(data.containsKey("syspwd")){
                this.syspwd = data.get("syspwd");
            }
            if(data.containsKey("deskey")){
                this.deskey = data.get("deskey");
            }
        }
    }

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
        logger.info("[BFP]贝富支付扫码支付开始============START===========");
        try {
            Map<String,String> map = sealRequest(payEntity,2);
            logger.info("[BFP]贝富支付扫码支付生成请求参数报文:{}",JSONObject.fromObject(map).toString());
            String params = ganeratorSign(map,1);
            logger.info("[BFP]贝富支付响应信息:"+params);

            //组装地址
            StringBuffer reqUrl = new StringBuffer();
            reqUrl.append(payUrl).append("?").append("params=");
            reqUrl.append(params).append("&").append("uname=").append(sid);
            String reqUrlStr = reqUrl.toString();
            logger.info("请求地址:{}",reqUrlStr);
            return PayResponse.sm_link(payEntity,reqUrlStr,"下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BFP]贝富支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[BFP]贝富支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[BFP]贝富支付回调验签开始==============START===========");
        try {
            String p_md = data.get("p_md5");
            String p_syspwd = data.get("p_syspwd");
            String pSyspwdStr = MD5Encoder.encode(syspwd+md5key);
            if(p_syspwd.equalsIgnoreCase(pSyspwdStr)){
                String sign = ganeratorSign(data,2);
                if(sign.equalsIgnoreCase(p_md)){
                    return "success";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BFP]贝富支付回调验签异常:"+e.getMessage());
        }
        return "faild";
    }

    /**
     *
     * @Description 组装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity,int type) throws Exception{
        logger.info("[BFP]贝富支付组装支付请求参数开始==============START==================");
        try {
            //创建参数存储对象
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());
            data.put("p_name",sid);//商户号，分配的登录账户
            data.put("p_oid",entity.getOrderNo());//订单号，不可大于 32 位的字母和数字
            data.put("p_money",amount);//支付金额，保留 2 位小数
            if(type == 1){
                data.put("p_bank",entity.getPayCode());//银行卡类型，网银支付必填
                data.put("p_type","WAY_TYPE_BANK");//通道类型
            }else{
                data.put("p_type",entity.getPayCode());//通道类型
            }
            data.put("p_url",notifyUrl);//回调地址，支付成功后的通知地址，最多三次，收到回复验证成功，返回“success”即可停止通知请求。
            data.put("p_surl",entity.getRefererUrl());//成功地址，支付成功后立即通知地址(部分通道源可用)
            data.put("p_remarks","TOP-UP");//备注，通知时原样返回
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[BFP]贝富支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[BFP]贝富支付组装支付请求参数异常");
        }
    }


    /**
     *
     * @Description 加密得到支付跳转页面
     * @param data
     * @return
     * @throws Exception
     */
    private String ganeratorSign(Map<String,String> data,int type) throws Exception{
        logger.info("[BFP]贝富支付加密开始=================START==================");
        try{
            StringBuffer sb = new StringBuffer();
            if(type == 1){
                //支付
                sb.append("p_name=").append(data.get("p_name")).append("!");
                sb.append("p_type=").append(data.get("p_type")).append("!");
                sb.append("p_oid=").append(data.get("p_oid")).append("!");
                sb.append("p_money=").append(data.get("p_money")).append("!");
                sb.append("p_bank=").append(data.get("p_bank")).append("!");
                sb.append("p_url=").append(data.get("p_url")).append("!");
                sb.append("p_surl=").append(data.get("p_surl")).append("!");
                sb.append("p_remarks=").append(data.get("p_remarks")).append("!");
                //加密
                String p_syspwd=MD5Encoder.encode(syspwd+md5key);
                sb.append("p_syspwd=").append(p_syspwd);
                String signStr = sb.toString();
                logger.info("生产待加密字符串:{}",signStr);
                //进行转移
                String sgin = URLEncoder.encode(signStr, "UTF-8");
                //进行加密
                String desStr = encrypt(sgin, deskey);
                return desStr;
            }else{
                //回调
                sb.append(data.get("p_name")).append(data.get("p_oid"));
                sb.append(data.get("p_money")).append(syspwd).append(md5key);
                String callbackSignStr = sb.toString();
                logger.info("回调待签名字符串:{}",callbackSignStr);
                String callbackSign = MD5Encoder.encode(callbackSignStr);
                return callbackSign;
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.info("[BFP]贝富支付生产加密异常:{}",e.getMessage());
            throw  new Exception("[BFP]贝富支付生产加密异常");
        }
    }

    public static String encrypt(String message, String key) {
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            String str = Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));
            return str.replaceAll("\r|\n", "");
        } catch (Exception e) {
            return null;
        }
    }
}
