package com.cn.tianxia.api.utils.pay;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.domain.txdata.v2.RechargeDao;
import com.cn.tianxia.api.project.v2.RechargeEntity;
import com.cn.tianxia.api.utils.SpringContextUtils;

/**
 * 
 * @ClassName ParamsUtils
 * @Description 参数工具类
 * @author Hardy
 * @Date 2018年9月29日 下午5:58:14
 * @version 1.0.0
 */
public class TestParamsUtils {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(TestParamsUtils.class);

    /**
     * 
     * @Description 获取流参数
     * @param request
     * @return
     */
    public static Map<String,String> getNotifyParams(HttpServletRequest request){
        try {
            String uid = request.getParameter("userId");
            String cid = request.getParameter("cid");
            System.out.println("用户Id:"+uid+"平台Id:"+cid);
            String orderNo = "xpjtest"+System.currentTimeMillis();
            RechargeDao rechargeDao = (RechargeDao) SpringContextUtils.getBeanByClass(RechargeDao.class);
            RechargeEntity entity = new RechargeEntity();
            entity.setUid(Integer.valueOf(uid));
            entity.setBankCode("alipay");
            entity.setCid(Integer.valueOf(cid));
            entity.setCagent("xpj");
            entity.setOrderNo(orderNo);
            entity.setPayAmount(100.00d);
            entity.setOrderAmount(100.00d);
            entity.setOrderTime(new Date());
            entity.setMerchant("TEST");
            entity.setTradeStatus("paying");
            entity.setTradeNo("");
            entity.setIp("127.0.0.1"); //ip
            entity.setIntegral(0.00);
            entity.setPayId(1207);
            entity.setPayType((byte) 24);
            rechargeDao.insertSelective(entity);
            Map<String, String> data = new HashMap<>();
            data.put("orderno", orderNo);
            data.put("porderno", "TEST"+System.currentTimeMillis());
            data.put("merchId", "aa123456");
            data.put("status", "success");
            data.put("amount", "100.00");
            data.put("sign", generatorSign(data));
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("解析流参数异常:" + e.getMessage());
            return null;
        }
    }
    
    /**
     * @param data
     * @return
     * @throws Exception
     * @Description 生成签名
     */
    private static String generatorSign(Map<String, String> data) throws Exception {
        try {
            // 排序
            Map<String, String> treeMap = new TreeMap<>(data);
            StringBuffer sb = new StringBuffer();
            for (String key : treeMap.keySet()) {
                String val = treeMap.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                sb.append(key).append("=").append(val).append("&");
            }
            // 加上签名秘钥
            sb.append("key=").append("af32229bb27f72fc52e58f07081b5d68");
            String signStr = sb.toString();
            logger.info("[TEST]测试扫码支付生成待加密签名串：" + signStr);
            String sign = MD5Utils.md5(signStr.getBytes());
            logger.info("[TEST]测试扫码支付生成MD5加密签名串:" + sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[TEST]测试扫码支付生成签名异常:" + e.getMessage());
            throw new Exception("[TEST]测试扫码支付生成签名串异常!");
        }
    }
}
