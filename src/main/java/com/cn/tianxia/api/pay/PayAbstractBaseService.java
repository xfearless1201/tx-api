package com.cn.tianxia.api.pay;

import com.cn.tianxia.api.base.annotation.LogApi;
import com.cn.tianxia.api.domain.txdata.v2.RechargeDao;
import com.cn.tianxia.api.po.ResultResponse;
import com.cn.tianxia.api.project.v2.RechargeEntity;
import com.cn.tianxia.api.service.NotifyService;
import com.cn.tianxia.api.utils.SpringContextUtils;
import com.cn.tianxia.api.utils.pay.NotifyUtils;
import com.cn.tianxia.api.vo.ProcessNotifyVO;
import com.cn.tianxia.api.vo.RechargeOrderVO;
import com.cn.tianxia.api.web.BaseController;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: zed
 * @Date: 2019/2/19 10:25
 * @Description: 支付实现类抽象基类
 */
public abstract class PayAbstractBaseService {

    protected static final String ret__success = "SUCCESS";
    protected static final String ret__failed = "FAILED";
    private static final Logger logger = LoggerFactory.getLogger(PayAbstractBaseService.class);
    private static final NotifyService notifyService;
    private static final RechargeDao rechargeDao;

    static {
        notifyService = (NotifyService) SpringContextUtils.getBeanByClass(NotifyService.class);
        rechargeDao = (RechargeDao) SpringContextUtils.getBeanByClass(RechargeDao.class);
    }
    @LogApi("回调验证")
    public abstract String notify(HttpServletRequest request, HttpServletResponse response, JSONObject config);
    
    protected String processSuccessNotify(ProcessNotifyVO processNotifyVO, boolean verifySuccess) {
        String order_no = processNotifyVO.getOrder_no();  //支付订单号
        String ip = processNotifyVO.getIp();  //回调ip
        String ret__success = processNotifyVO.getRet__success();  //成功返回字符串
        String ret__failed = processNotifyVO.getRet__failed();   //失败返回字符串
        String trade_no = processNotifyVO.getTrade_no();   //第三方订单号，流水号
        String trade_status = processNotifyVO.getTrade_status();  //第三方支付状态
        String t_trade_status = processNotifyVO.getT_trade_status();   //第三方成功状态
        JSONObject infoMap = JSONObject.fromObject(processNotifyVO.getInfoMap());  //回调请求参数
        double realAmount = processNotifyVO.getRealAmount();  //实际支付金额
        String payment = processNotifyVO.getPayment();   //支付商名称
        JSONObject config = processNotifyVO.getConfig();//支付配置

        if (BaseController.payMap.containsKey(order_no)) {
            RechargeEntity rechargeEntity = rechargeDao.selectByOrderNo(order_no);
            String tradeStatus = rechargeEntity.getTradeStatus();
            if ("paying".equals(tradeStatus)) {
                BaseController.payMap.remove(order_no);
            } else {
                logger.info("支付回调订单号:{}重复调用", order_no);
                return ret__failed;
            }
        }
        BaseController.payMap.put(order_no, "1");

        try {
            logger.info(payment + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(payment, infoMap, ip);
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(payment + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(infoMap.toString());
            rechargeOrderVO.setOrderAmount(realAmount);//实际支付金额

            //验证回调IP
            if (null != config && config.containsKey("notifyIp") && StringUtils.isNotBlank(config.getString("notifyIp"))) {
                String notifyIp = config.getString("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(payment + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret__failed;
                }
            }
            //验签失败
            if (!verifySuccess) {
                logger.info(payment + "支付回调验签失败!");
                rechargeOrderVO.setDescription("The top-up order is notify faild,because notify verify sign is faild,Please contact customer service.");
                notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                return ret__failed;
            }

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(payment + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(payment + "支付回调业务处理成功=======================FAILD====================");
            return ret__failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(payment + "支付回调业务处理异常:{}", e.getMessage());
            return ret__failed;
        } finally {
            if (BaseController.payMap.containsKey(order_no)) {
                logger.info("回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                BaseController.payMap.remove(order_no);
            }
        }
    }

    /**
     * @param payType
     * @return
     * @Description 获取支付类型
     */

    public String getPayConfigType(String payType) {
        if ("1".equals(payType) || "21".equals(payType)) {
            return "bank";
        } else if ("3".equals(payType) || "23".equals(payType)) {
            return "wx";
        } else if ("4".equals(payType) || "24".equals(payType)) {
            return "ali";
        } else if ("5".equals(payType) || "25".equals(payType)) {
            return "cft";
        } else if ("6".equals(payType) || "26".equals(payType)) {
            return "jd";
        } else if ("7".equals(payType) || "27".equals(payType)) {
            return "yl";
        } else if ("9".equals(payType) || "29".equals(payType)) {
            return "kj";
        } else if ("10".equals(payType) || "30".equals(payType)) {
            return "wxtm";
        }
        return null;
    }

    /**
     * 回调IP验证
     *
     * @param notifyIps
     * @param ip        回调IP
     * @return
     */
    private boolean isContainIp(String notifyIps, String ip) {
        boolean isContainsIp = false;
        if (notifyIps.contains(",")) {
            String[] ipArr = notifyIps.split(",");
            for (String notifyIp : ipArr) {
                if (StringUtils.isNotBlank(notifyIp) && notifyIp.contains(ip)) {
                    isContainsIp = true;
                    break;
                }
            }
        } else {
            if (StringUtils.isNotBlank(notifyIps) && notifyIps.contains(ip)) {
                isContainsIp = true;
            }
        }
        return isContainsIp;
    }

}
