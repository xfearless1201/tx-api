package com.cn.tianxia.api.web;

import com.cn.tianxia.api.base.annotation.LogApi;
import com.cn.tianxia.api.common.PayConstant;
import com.cn.tianxia.api.pay.impl.*;
import com.cn.tianxia.api.po.ResultResponse;
import com.cn.tianxia.api.service.NotifyService;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.JSONUtils;
import com.cn.tianxia.api.utils.bg.HashUtil;
import com.cn.tianxia.api.utils.pay.NotifyUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.vo.CagentYespayVO;
import com.cn.tianxia.api.vo.RechargeOrderVO;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: NewNotifyController
 * @Description: 新支付回调controller(新增支付回调都写在这里)
 * @Author: Zed
 * @Date: 2019-01-11 16:08
 * @Version:1.0.0
 **/

@RequestMapping("Notify")
@Controller
@Scope("prototype")
public class NewNotifyController extends BaseController {

    @Autowired
    private NotifyService notifyService;
    private String ret_str_failed = "fail";

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description D15支付回调通知
     */
    @LogApi("D15支付回调")
    @RequestMapping("/DFIFNotify.do")
    @ResponseBody
    public String DFIFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";//收到通知后请回复  success
        String clazz_name = "DFIFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        String order_no = request.getParameter("orderId"); // 平台订单号
        if (StringUtils.isBlank(order_no)) {
            logger.info(clazz_name + "支付回调获取请求参数orderId为空!");
            return ret_str_failed;
        }
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }

            JSONObject pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            String payConfigType = getPayConfigType(String.valueOf(rechargeOrderVO.getPayType()));
            String privatekey;
            if ("wx".equals(payConfigType)) {
                privatekey = pmapsconfig.getJSONObject("wx").getString("PAY_PRIVATE_KEY");
            } else {
                privatekey = pmapsconfig.getJSONObject("ali").getString("PAY_PRIVATE_KEY");
            }
            Map<String, String> infoMap = ParamsUtils.getDFIFNotifyParams(request, privatekey);
            if (infoMap == null || infoMap.isEmpty()) {
                logger.info(clazz_name + "支付回调获取请求参数为空!");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
            //=================================获取回调基本参数结果--START===========================//
            String trade_no = "DFIF" + System.currentTimeMillis();//流水号
            String trade_status = infoMap.get("result");//0:成功，其他失败
            String t_trade_status = "00";// 00:成功
            String order_amount = infoMap.get("amount");
            if (StringUtils.isBlank(order_amount)) {
                logger.info(clazz_name + "获取实际支付金额为空!");
                return ret_str_failed;
            }
            String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.getString("notifyIp"))) {
                String notifyIp = pmapsconfig.getString("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //更新支付订单信息
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount) / 100);
            logger.info(clazz_name + "支付回调验签开始=======================START====================");

            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            String type = getPayConfigType(String.valueOf(rechargeOrderVO.getPayType()));//获取支付类型
            if (paymentName.equals(PayConstant.CONSTANT_DFIF)) {
                DFIFPayServiceImpl qft = new DFIFPayServiceImpl(pmapsconfig, type);
                String rmsg = qft.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 爱付支付回调通知
     */
    @LogApi("爱付支付回调")
    @RequestMapping("/LOVENotify.do")
    @ResponseBody
    public String LOVENotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";// 成功返回SUCCESS
        String clazz_name = "LOVENotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("merchant_billno");// 平台订单号
        String trade_no = infoMap.get("billno");// 平台订单号
        String trade_status = infoMap.get("status");//订单状态 100待支付 200已完成 300已取消
        String t_trade_status = "200";// 表示成功状态
        String order_amount = infoMap.get("paid_amount");
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));//实际支付金额
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_LOVE)) {
                LOVEPayServiceImpl xxb = new LOVEPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 爽快支付回调通知
     */
    @LogApi("爽快支付回调")
    @RequestMapping("/SKPNotify.do")
    @ResponseBody
    public String SKPNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回SUCCESS
        String clazz_name = "SKPNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = infoMap.get("trade_no");// 平台订单号
        String trade_status = infoMap.get("code");//success表示业务成功
        String t_trade_status = "success";// 表示成功状态
        String order_amount = infoMap.get("total_amount");
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));//实际支付金额
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            //获取支付类型
            String type = getPayConfigType(String.valueOf(rechargeOrderVO.getPayType()));
            if (StringUtils.isBlank(type)) {
                logger.info(clazz_name + "回调验签获取支付配置文件类型为空");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_SKP)) {
                SKPPayServiceImpl xxb = new SKPPayServiceImpl(pmapsconfig, type);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }


    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description HUIP汇付支付回调通知
     */
    @LogApi("汇付支付回调")
    @RequestMapping("/HUIPNotify.do")
    @ResponseBody
    public String HUIPNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";//收到通知后请回复  OK
        String clazz_name = "HUIPNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("amount");//实际支付金额
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");//交易流水号
        String trade_status = infoMap.get("returncode");//00代表支付成功
        String t_trade_status = "00";//1:成功
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_HUIP)) {
                HUIPPayServiceImpl xxb = new HUIPPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 众惠支付回调通知
     */
    @LogApi("众惠支付回调")
    @RequestMapping("/ZHUINotify.do")
    @ResponseBody
    public String ZHUINotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";// 成功返回SUCCESS
        String clazz_name = "ZHUINotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = clazz_name + infoMap.get("cas_time_stamp");// 第三方流水号
        String trade_status = infoMap.get("status");//PAID表示业务成功
        String t_trade_status = "00";// 00表示支付成功，非00表示失败
        String order_amount = infoMap.get("total_fee"); // 分为单位
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount) / 100);//实际支付金额，分为单位
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_ZHUI)) {
                ZHUIPayServiceImpl zhui = new ZHUIPayServiceImpl(pmapsconfig);
                String rmsg = zhui.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 新币宝支付回调通知
     */
    @LogApi("新币宝支付回调")
    @RequestMapping("/XBBZFNotify.do")
    @ResponseBody
    public String XBBZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "{\"Success\":true,\"Code\":1,\"Message\":\"SUCCESS\"}";// 成功返回
        String clazz_name = "XBBZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("OrderNum");// 平台订单号
        String trade_no = infoMap.get("OrderId");// 第三方流水号
        String State1 = infoMap.get("State1"); //订单状态
        String State2 = infoMap.get("State2"); //支付状态
        String trade_status = State1.equals("2") && State2.equals("2") ? "success" : "fail";
        String t_trade_status = "success";// 两个状态同时为2时，才给会员上分
        String order_amount = infoMap.get("LegalAmount"); // 实际充值金额，单位元
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XBBZF)) {
                XBBZFPayServiceImpl xbbzfPayService = new XBBZFPayServiceImpl(pmapsconfig);
                String rmsg = xbbzfPayService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 九久支付回调通知
     */
    @LogApi("九久支付回调")
    @RequestMapping("/JIUNotify.do")
    @ResponseBody
    public String JIUNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回SUCCESS
        String clazz_name = "JIUNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("mchno");// 平台订单号
        String trade_no = infoMap.get("transactionid");// 第三方流水号
        String trade_status = infoMap.get("resultcode");//PAID表示业务成功
        String t_trade_status = "1";// 1 成功 0 失败
        String order_amount = infoMap.get("totalfee"); // 分为单位
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，分为单位
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_JIU)) {
                JIUPayServiceImpl jiu = new JIUPayServiceImpl(pmapsconfig);
                String rmsg = jiu.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 万达支付回调通知
     */
    @LogApi("万达支付回调")
    @RequestMapping("/WDZFNotify.do")
    @ResponseBody
    public String WDZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回SUCCESS
        String clazz_name = "WDZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("customerId");// 平台订单号
        String trade_no = infoMap.get("orderId");// 第三方流水号
        String trade_status = infoMap.get("status");//PAID表示业务成功
        String t_trade_status = "1";// 1 成功 0 失败
        String order_amount = infoMap.get("money"); // 分为单位
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_WDZF)) {
                WDZFPayServiceImpl jiu = new WDZFPayServiceImpl(pmapsconfig);
                String rmsg = jiu.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 恒付支付回调通知
     */
    @LogApi("恒付支付回调")
    @RequestMapping("/HPZFNotify.do")
    @ResponseBody
    public String HPZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "{\"message\":\"success\",\n" + "\"code\":200}";// 成功返回SUCCESS
        String clazz_name = "HPZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("userReamrk");// 平台订单号
        String trade_no = infoMap.get("depositNumber");// 第三方流水号
        String trade_status = "0000";//订单状态，01：未支付 02：已支付
        String t_trade_status = "0000";// 订单状态，01：未支付 02：已支付
        String order_amount = infoMap.get("amount"); //单位为元，小数两位
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            String notifyIp = pmapsconfig.get("notifyIp");
            if (!isContainIp(notifyIp, ip)) {
                logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                rechargeOrderVO.setDescription("notify ip is not match");
                notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                return ret_str_failed;
            }

            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_HPZF)) {
                HPZFPayServiceImpl jiu = new HPZFPayServiceImpl(pmapsconfig);
                String rmsg = jiu.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }


    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 易通宝支付回调通知
     */
    @LogApi("易通宝支付回调")
    @RequestMapping("/YTBPNotify.do")
    @ResponseBody
    public String YTBPNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";// 成功返回SUCCESS
        String clazz_name = "YTBPNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");// 第三方流水号
        String trade_status = infoMap.get("returncode");//订单状态，“00” 为成功
        String t_trade_status = "00";// 订单状态，“00” 为成功
        String order_amount = infoMap.get("amount"); //单位为元，小数两位
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            String notifyIp = pmapsconfig.get("notifyIp");
            if (!isContainIp(notifyIp, ip)) {
                logger.error("YTBPNotify 回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                rechargeOrderVO.setDescription("notify ip is not match");
                notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YTBP)) {
                YTBPPayServiceImpl jiu = new YTBPPayServiceImpl(pmapsconfig);
                String rmsg = jiu.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 通支付2回调通知
     */
    @LogApi("通支付2回调")
    @RequestMapping("/EASYNotify.do")
    @ResponseBody
    public String EASYNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回SUCCESS
        String clazz_name = "EASYNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("fxddh");// 平台订单号
        String trade_no = infoMap.get("fxorder");// 第三方流水号
        String trade_status = infoMap.get("fxstatus");//【1代表支付成功
        String t_trade_status = "1";// 订单状态，“1” 为成功
        String order_amount = infoMap.get("fxfee"); //单位为元，小数两位
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_EASY)) {
                EASYPayServiceImpl jiu = new EASYPayServiceImpl(pmapsconfig);
                String rmsg = jiu.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }


    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 联盛支付回调通知
     */
    @LogApi("联盛支付回调")
    @RequestMapping("/LSZFNotify.do")
    @ResponseBody
    public String LSZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回SUCCESS
        Map<String, String> map = new HashMap<>();
        String clazz_name = "LSZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            map.put("SUCCESS", "false");
            map.put("code", "400");
            map.put("message", "支付回调获取请求参数为空");
            map.put("sn", "");
            return JSONUtils.toJSONString(map);
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderId");// 平台订单号
        String trade_no = infoMap.get("sn");// 第三方流水号
//        String trade_status = infoMap.get("fxstatus");//【1代表支付成功
        String trade_status = "1";
        String t_trade_status = "1";// 订单状态，“1” 为成功
        String order_amount = infoMap.get("amount"); //单位为元，小数两位
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            map.put("SUCCESS", "false");
            map.put("code", "400");
            map.put("message", "获取实际支付金额为空");
            map.put("sn", trade_no);
            return JSONUtils.toJSONString(map);
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            map.put("SUCCESS", "false");
            map.put("code", "400");
            map.put("message", "支付回调订单重复调用");
            map.put("sn", trade_no);
            return JSONUtils.toJSONString(map);
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                map.put("SUCCESS", "false");
                map.put("code", "400");
                map.put("message", "支付回调通知订单号为非法订单号");
                map.put("sn", trade_no);
                return JSONUtils.toJSONString(map);
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                map.put("SUCCESS", "false");
                map.put("code", "400");
                map.put("message", "非法支付商");
                map.put("sn", trade_no);
                return JSONUtils.toJSONString(map);
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_LSZF)) {
                LSZFPayServiceImpl jiu = new LSZFPayServiceImpl(pmapsconfig);
                String rmsg = jiu.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    map.put("SUCCESS", "false");
                    map.put("code", "400");
                    map.put("message", "支付回调验签失败!");
                    map.put("sn", trade_no);
                    return JSONUtils.toJSONString(map);
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                map.put("SUCCESS", "false");
                map.put("code", "400");
                map.put("message", "支付回调异常请求!");
                map.put("sn", trade_no);
                return JSONUtils.toJSONString(map);
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                map.put("SUCCESS", "true");
                map.put("code", "3000");
                map.put("message", "支付回调成功！");
                map.put("sn", trade_no);
                return JSONUtils.toJSONString(map);
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            map.put("SUCCESS", "false");
            map.put("code", "400");
            map.put("message", "支付回调失败！");
            map.put("sn", trade_no);
            return JSONUtils.toJSONString(map);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }


    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 顺隆支付回调通知
     */
    @LogApi("顺隆支付回调")
    @RequestMapping("/SLONNotify.do")
    @ResponseBody
    public String SLONNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";// 成功返回SUCCESS
        String clazz_name = "SLONNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");// 第三方流水号
        String trade_status = infoMap.get("returncode");//订单状态，“00” 为成功
        String t_trade_status = "00";// 订单状态，“00” 为成功
        String order_amount = infoMap.get("amount"); //单位为元，小数两位
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_SLON)) {
                SLONPayServiceImpl jiu = new SLONPayServiceImpl(pmapsconfig);
                String rmsg = jiu.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 大宝天下支付回调通知
     */
    @LogApi("大宝天下支付回调")
    @RequestMapping("/DBTXNotify.do")
    @ResponseBody
    public String DBTXNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";// 成功返回SUCCESS
        String clazz_name = "SLONNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");// 第三方流水号
        String trade_status = infoMap.get("returncode");//订单状态，“00” 为成功
        String t_trade_status = "00";// 订单状态，“00” 为成功
        String order_amount = infoMap.get("amount"); //单位为元，小数两位
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals("DBTX")) {
                DBTXPayServiceImpl dbtx = new DBTXPayServiceImpl(pmapsconfig);
                String rmsg = dbtx.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 聚合银码支付回调通知
     */
    @LogApi("聚合银码支付回调")
    @RequestMapping("/JHYMNotify.do")
    @ResponseBody
    public String JHYMNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";// 成功返回SUCCESS
        String clazz_name = "JHYMNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = infoMap.get("trade_no");// 第三方流水号
        String trade_status = infoMap.get("trade_status");//订单状态，“00” 为成功
        String t_trade_status = "TRADE_SUCCESS";// TRADE_SUCCESS
        String order_amount = infoMap.get("money"); //单位为元，小数两位
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals("JHYM")) {
                JHYMPayServiceImpl jhymPayService = new JHYMPayServiceImpl(pmapsconfig);
                String rmsg = jhymPayService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 城市互联支付回调通知
     */
    @LogApi("城市互联支付回调")
    @RequestMapping("/CSHLNotify.do")
    @ResponseBody
    public String CSHLNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// success
        String clazz_name = "CSHLNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        byte[] decodeBase64 = HashUtil.decodeBase64(request.getParameter("message"));
        String message = new String(decodeBase64, StandardCharsets.UTF_8);
        JSONObject reqJsonObj = JSONObject.fromObject(message);
        Map<String, String> infoMap = ParamsUtils.getCSHLNotifyParams(request);
        infoMap.remove("amount");
        infoMap.put("amount", String.valueOf(reqJsonObj.getInt("amount")));
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderNo");// 平台订单号
        String trade_no = infoMap.get("trxorderNo");// 第三方流水号
        String trade_status = infoMap.get("status");//0=失败；1=成功
        String t_trade_status = "1"; //成功状态
        String order_amount = infoMap.get("amount"); //单位 分
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount) / 100;
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位 分
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals("CSHL")) {
                CSHLPayServiceImpl cshlPayService = new CSHLPayServiceImpl(pmapsconfig);
                String rmsg = cshlPayService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 资海支付回调通知
     */
    @LogApi("资海支付回调")
    @RequestMapping("/ZIHAINotify.do")
    @ResponseBody
    public String ZIHAINotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";// 成功返回SUCCESS
        String clazz_name = "ZIHAINotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");// 第三方流水号
        String trade_status = infoMap.get("returncode");//订单状态，“00” 为成功
        String t_trade_status = "00";// 订单状态，“00” 为成功
        String order_amount = infoMap.get("amount"); //单位为元，小数两位
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals("ZIHAI")) {
                ZIHAIPayServiceImpl zihaiPayService = new ZIHAIPayServiceImpl(pmapsconfig);
                String rmsg = zihaiPayService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }


    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description XHZF新汇付1代回调通知
     */
    @LogApi("新汇付1代回调")
    @RequestMapping("/XHZFNotify.do")
    @ResponseBody
    public String XHZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        JSONObject successJson = new JSONObject();
        successJson.put("status", true);
        successJson.put("msg", "支付成功");
        JSONObject failJson = new JSONObject();
        failJson.put("status", false);
        failJson.put("msg", "错误描述！");
        String ret__success = successJson.toString();//收到通知后请回复  OK
        ret_str_failed = failJson.toString();
        String clazz_name = "XHZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("total_amount");//实际支付金额 单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("order_id");// 平台订单号
        String trade_no = infoMap.get("order_no");//交易流水号
        String trade_status = "00";//00代表支付成功
        String t_trade_status = "00";//00:成功
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XHZF)) {
                XHZFPayServiceImpl xxb = new XHZFPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description XHEI回调通知
     */
    @LogApi("XHEI回调")
    @RequestMapping("/XHEINotify.do")
    @ResponseBody
    public String XHEINotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";//收到通知后请回复  SUCCESS
        String clazz_name = "XHEINotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("amount");//实际支付金额
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("outOrderNo");// 平台订单号
        String trade_no = infoMap.get("orderNo");//交易流水号
        String trade_status = "success";//success代表支付成功
        String t_trade_status = "success";//success:成功
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount) / 100);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XHEI)) {
                XHEIPayServiceImpl xxb = new XHEIPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }


    /**
     * AMH嫌来赚支付
     *
     * @param request
     * @param response
     * @param session
     * @return
     * @Description XLZ回调通知
     */
    @LogApi("XLZ回调")
    @RequestMapping("/XLZNotify.do")
    @ResponseBody
    public String XLZNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "200";//收到通知后请回复  SUCCESS,对接方在支付成功后只收“200”
        String clazz_name = "XLZNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("userPayAmount");//实际支付金额
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("transactionId");// 平台订单号
        String trade_no = infoMap.get("id");//交易流水号
        String trade_status = "200";//success代表支付成功
        String t_trade_status = "200";//success:成功
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount) / 100);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XLZ)) {
                XLZPayServiceImpl xlz = new XLZPayServiceImpl(pmapsconfig);
                String rmsg = xlz.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");

            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }


    /**
     * 冠宝支付回调
     *
     * @param request
     * @return
     */
    @LogApi("冠宝支付回调")
    @RequestMapping("/GBNotify.do")
    @ResponseBody
    public String GBNotify(HttpServletRequest request) {
        logger.info("GBNotify {} GB冠宝支付回调请求串 GBNotify(HttpServletRequest request = {} -start", request);
        logger.info("冠宝支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info("GBNotify {} 冠宝支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info("GBNotify {}冠宝支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //支付成功返回0
        String orderId = infoMap.get("orderId");// 平台订单号
        String orderNo = infoMap.get("orderIdCp");//商户订单号
        String trade_status = infoMap.get("status").equals("0") ? "success" : "fail"; //订单状态 0为支付成功,其他都是失败！
        String t_trade_status = "success";// 订单状态，“1” 为成功
        String order_amount = infoMap.get("money"); //单位：分
        if (StringUtils.isBlank(order_amount)) {
            logger.info("GBNotify {}获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount) / 100;
        logger.info("GBNotify {} 冠宝支付实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (payMap.containsKey(orderNo)) {
            logger.info("GBNotify {}支付回调订单号:{}重复调用", orderNo);
            return ret_str_failed;
        }
        payMap.put(orderNo, "1");
        try {
            logger.info("GBNotify {} 执行回调业务开始=========================START===========================");
            //保存文件记录
            NotifyUtils.savePayFile("GBNotify {}", infoMap, IPTools.getIp(request));
            logger.info("保存文件记录成功！");
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(orderNo);
            if (rechargeOrderVO == null) {
                logger.info(" GBNotify {}支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", orderNo);
                return ret_str_failed;
            }
            rechargeOrderVO.setFinishTime(new Date());
            rechargeOrderVO.setTradeNo(orderId);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(" GBNotify {} 非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error("GBNotify回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(" GBNotify {} 支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_GB.toString())) {
                GBPayServiceImpl gb = new GBPayServiceImpl(pmapsconfig);
                String rmsg = gb.callback(infoMap);
                logger.info("冠宝支付 验签结果 ============  {} end" + rmsg);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(" GBNotify {} 支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(" GBNotify {} 支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(" GBNotify {} 支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(" GBNotify {} 支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info("GBNotify {} 支付回调业务处理成功=======================SUCCESS====================");
                return "success";
            }
            logger.info(" GBNotify {} 支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            logger.error("冠宝支付回调失败，系统业务异常，异常订单号:{}" + orderNo);
            e.printStackTrace();
            logger.info(" GBNotify {} 支付回调业务处理异常:{}" + e.getMessage(), e);
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(orderNo)) {
                logger.info(" GBNotify {} 支付回调业务处理成功,删除缓存中的订单KEY:{}", orderNo);
                payMap.remove(orderNo);
            }
        }


    }


    /**
     * 聚宝支付回调
     *
     * @param request
     * @return
     */
    @LogApi("聚宝支付回调")
    @RequestMapping("/JBZFNotify.do")
    @ResponseBody
    public String JBZFNotify(HttpServletRequest request) {
        logger.info("聚宝支付回调方法  JBZFNotify(HttpServletRequest request = {} -start" + request);
        logger.info("聚宝支付回调方法开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        String msg = "OK";//回调成功则返回ok
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info("JBNotify {} 聚宝支付回调方法获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info("JBNotify {}聚宝支付回调方法请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //支付成功返回
        String orderId = infoMap.get("orderno");// 第三方平台订单号
        String orderNo = infoMap.get("customerbillno");//商户订单号
        String trade_status = infoMap.get("paystatus").equals("SUCCESS") ? "success" : "pending"; //订单状态 0为支付成功,其他都是失败！
        String t_trade_status = "success";// 订单状态，“1” 为成功
        String order_amount = infoMap.get("orderamount"); // TODO 单位：元
        if (!trade_status.equals("success")) {
            logger.info("聚宝支付回调，订单尚未处理完成，订单正在处理中  订单编号:" + orderNo);
            return ret_str_failed; //第三方将一直回调返回成功为止！
        }
        if (StringUtils.isBlank(order_amount)) {
            logger.info("JBZFNotify {}获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info("JBZFNotify {} 聚宝支付实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (payMap.containsKey(orderNo)) {
            logger.info("JBZFNotify {}支付回调订单号:{}重复调用", orderNo);
            return ret_str_failed;
        }
        payMap.put(orderNo, "1");
        try {
            logger.info("JBZFNotify {} 执行回调业务开始=========================START===========================");
            //保存文件记录
            NotifyUtils.savePayFile("JBZFNotify {}", infoMap, IPTools.getIp(request));
            logger.info("保存文件记录成功！");
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(orderNo);
            if (rechargeOrderVO == null) {
                logger.info(" JBZFNotify {}支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", orderNo);
                return ret_str_failed;
            }
            rechargeOrderVO.setFinishTime(new Date());
            rechargeOrderVO.setTradeNo(orderId);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(" GBNotify {} 非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error("JBZFNotify回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(" JBZFNotify {} 支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_JBZF)) {
                JBZFPayServiceImpl gb = new JBZFPayServiceImpl(pmapsconfig);
                String rmsg = gb.callback(infoMap);
                logger.info("聚宝支付 验签结果 ============  {} end" + rmsg);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(" JBZFNotify {} 支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(" JBZFNotify {} 支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(" JBZFNotify {} 支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(" JBZFNotify {} 支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info("JBZFNotify {} 支付回调业务处理成功=======================SUCCESS====================");
                return msg;
            }
            logger.info(" JBZFNotify {} 支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            logger.error("聚宝支付回调失败，系统业务异常，异常订单号:{}" + orderNo);
            e.printStackTrace();
            logger.info(" JBZFNotify {} 支付回调业务处理异常:{}" + e.getMessage(), e);
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(orderNo)) {
                logger.info(" JBZFNotify {} 支付回调业务处理成功,删除缓存中的订单KEY:{}", orderNo);
                payMap.remove(orderNo);
            }
        }
    }


    /**
     * 快付支付回调
     *
     * @param request
     * @return
     */
    @LogApi("快付支付回调")
    @RequestMapping("/KFZFNotify.do")
    @ResponseBody
    public String KFZFNotify(HttpServletRequest request) {
        logger.info("KFZFNotify {} 快付支付回调请求串 GBNotify(HttpServletRequest request = {} -start" + request);
        logger.info("KFZFNotify {} 快付支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info("XJZFNotify {} 快付支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info("KFZFNotify() 快付支付回调请求参数:{}" + JSONObject.fromObject(infoMap).toString());

        String orderNo = infoMap.get("merchant_order_no");//商户订单号
        String trade_status = infoMap.get("status").equals("2") ? "success" : "fail"; //订单状态: 1待支付，2成功，3冻结，-1失败,
        String t_trade_status = "success";// 订单状态，“1” 为成功
        String order_amount = infoMap.get("trade_amount"); //单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info("KFZFNotify {}获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info("KFZFNotify () 快付支付支付实际充值金额为：{}" + amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (payMap.containsKey(orderNo)) {
            logger.info("KFZFNotify {}支付回调订单号:{}重复调用", orderNo);
            return ret_str_failed;
        }
        payMap.put(orderNo, "1");
        try {
            logger.info("KFZFNotify {} 执行回调业务开始=========================START===========================");
            //保存文件记录
            NotifyUtils.savePayFile("XJZFNotify {}", infoMap, IPTools.getIp(request));
            logger.info("保存文件记录成功！");
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(orderNo);
            if (rechargeOrderVO == null) {
                logger.info(" KFZFNotify {}支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", orderNo);
                return ret_str_failed;
            }
            rechargeOrderVO.setFinishTime(new Date());
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(" KFZFNotify {} 非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error("KFZFNotify回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(" KFZFNotify {} 支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_KFZF.toString())) {
                KFZFPayServiceImpl gb = new KFZFPayServiceImpl(pmapsconfig);
                String rmsg = gb.callback(infoMap);
                logger.info("快付支付 验签结果 ============  {} end" + rmsg);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(" XJZFNotify {} 支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(" KFZFNotify {} 支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(" KFZFNotify {} 支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(" KFZFNotify {} 支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info("KFZFNotify {} 支付回调业务处理成功=======================SUCCESS====================");
                return "success";
            }
            logger.info(" KFZFNotify {} 支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            logger.error("快付支付支付回调失败，系统业务异常，异常订单号:{}" + orderNo);
            e.printStackTrace();
            logger.info(" KFZFNotify {} 支付回调业务处理异常:{}" + e.getMessage(), e);
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(orderNo)) {
                logger.info(" KFZFNotify {} 支付回调业务处理成功,删除缓存中的订单KEY:{}", orderNo);
                payMap.remove(orderNo);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 豪富支付回调通知
     */
    @LogApi("豪富支付回调")
    @RequestMapping("/HAOFUNotify.do")
    @ResponseBody
    public String HAOFUNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "HAOFUNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("amount_str");//实际支付金额
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = infoMap.get("trade_id");//交易流水号
        String trade_status = infoMap.get("status");//状态:0处理中,1完成,2失败
        String t_trade_status = "1";//1:成功
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_HAOFU)) {
                HAOFUPayServiceImpl xxb = new HAOFUPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }


    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 银商付支付
     */
    @LogApi("银商付支付回调")
    @RequestMapping("/YSPNotify.do")
    @ResponseBody
    public String YSPNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";// 成功返回SUCCESS
        String clazz_name = "YSPNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("order_id");// 平台订单号
        String trade_no = infoMap.get("paysapi_id");// 第三方流水号
        String trade_status = infoMap.get("code");//订单状态:0 未处理 1 交易成功 2 支付失败 3 关闭交易 4 支付超时
        String t_trade_status = "1";//订单状态:0 未处理 1 交易成功 2 支付失败 3 关闭交易 4 支付超时
        String order_amount = infoMap.get("real_price"); //单位元
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YSP)) {
                YSPPayServiceImpl jiu = new YSPPayServiceImpl(pmapsconfig);
                String rmsg = jiu.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }


    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 付拉拉支付回调通知
     */
    @LogApi("付拉拉支付回调")
    @RequestMapping("/FLLNotify.do")
    @ResponseBody
    public String FLLNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回SUCCESS
        String clazz_name = "FLLNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = infoMap.get("trade_no");// 第三方流水号
        String trade_status = infoMap.get("status");//支付状态：'1'为支付成功，'error:错误信息'为未支付成功。
        String t_trade_status = "1";//订单状态:0 未处理 1 交易成功 2 支付失败 3 关闭交易 4 支付超时
        String order_amount = infoMap.get("money"); //单位元
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_FLL)) {
                FLLPayServiceImpl jiu = new FLLPayServiceImpl(pmapsconfig);
                String rmsg = jiu.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 聚北支付回调通知
     */
    @LogApi("聚北支付回调")
    @RequestMapping("/JUBEINotify.do")
    @ResponseBody
    public String JUBEINotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";//收到通知后请回复  SUCCESS
        String clazz_name = "JUBEINotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("real_amount");//实际支付金额  单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("mch_order_no");// 平台订单号
        String trade_no = infoMap.get("order_no");//交易流水号
        String trade_status = infoMap.get("result_code");//SUCCESS
        String t_trade_status = "SUCCESS";//SUCCESS:成功
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_JUBEI)) {
                JUBEIPayServiceImpl xxb = new JUBEIPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description EAZY支付回调通知
     */
    @LogApi("EAZY支付回调")
    @RequestMapping("/EAZYNotify.do")
    @ResponseBody
    public String EAZYNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "EAZYNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("amount");//实际支付金额  单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = infoMap.get("trade_no");//交易流水号
        String trade_status = infoMap.get("status");//success
        String t_trade_status = "success";//success:成功
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_EAZY)) {
                EAZYPayServiceImpl xxb = new EAZYPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 速龙支付回调通知
     */
    @LogApi("速龙支付回调")
    @RequestMapping("/SLZFNotify.do")
    @ResponseBody
    public String SLZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "SLZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, Object> dataMap = ParamsUtils.getSLZFNotifyParams(request);
        Map<String, String> infoMap = (Map<String, String>) dataMap.get("data");
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("total_fee");//实际支付金额  单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = "SLZF" + System.currentTimeMillis();//交易流水号
        String trade_status = infoMap.get("pay_status");//1为支付成功
        String t_trade_status = "1";//success:成功
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_SLZF)) {
                SLZFPayServiceImpl xxb = new SLZFPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param payType
     * @return
     * @Description 获取支付类型
     */
    private String getPayConfigType(String payType) {
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

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 安心付支付回调通知
     */
    @LogApi("安心付支付回调")
    @RequestMapping("/AXPNotify.do")
    @ResponseBody
    public String AXPNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";// 成功返回SUCCESS
        String clazz_name = "AXPNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getAXPNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("order_id");// 平台订单号
        String trade_no = infoMap.get("orderNo");// 第三方流水号
        String trade_status = infoMap.get("status");//支付状态：'1'为支付成功，'error:错误信息'为未支付成功。
        String t_trade_status = "1";//订单状态:1：成功；0：失败
        String order_amount = infoMap.get("money"); //单位分
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount) / 100;
        logger.info(clazz_name + "实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位分
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_AXP)) {
                AXPPayServiceImpl axpPayService = new AXPPayServiceImpl(pmapsconfig);
                String rmsg = axpPayService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }


    /**
     * 快乐付支付回调
     *
     * @param request
     * @return
     */
    @LogApi("快乐付支付回调")
    @RequestMapping("/KLFNotify.do")
    @ResponseBody
    public String KLFNotify(HttpServletRequest request) {
        logger.info("快乐付支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info("KLFNotify {} 快乐付支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info("KLFNotify {}快乐支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //支付成功返回0
        String order_no = infoMap.get("shop_no");// 平台订单号
        String trade_no = infoMap.get("order_no");//商户订单号
        String trade_status = infoMap.get("status"); //订单状态 0为支付成功,其他都是失败！
        String t_trade_status = "0";// 订单状态，0 为成功
        String order_amount = infoMap.get("money"); //单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info("KLFNotify {}获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info("KLFNotify {} 快乐付支付实际充值金额为：{}", amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (payMap.containsKey(order_no)) {
            logger.info("KLFNotify {}支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info("KLFNotify {} 执行回调业务开始=========================START===========================");
            //保存文件记录
            NotifyUtils.savePayFile("KLFNotify {}", infoMap, IPTools.getIp(request));
            logger.info("保存文件记录成功！");
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(" KLFNotify {}支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret_str_failed;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(" KLFNotify {} 非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error("KLFNotify回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(" KLFNotify {} 支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_KLF)) {
                KLFPayServiceImpl klfPayService = new KLFPayServiceImpl(pmapsconfig);
                String rmsg = klfPayService.callback(infoMap);
                logger.info("快乐付支付 验签结果 ============  {} end" + rmsg);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(" KLFNotify {} 支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(" KLFNotify {} 支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(" KLFNotify {} 支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(" KLFNotify {} 支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info("KLFNotify {} 支付回调业务处理成功=======================SUCCESS====================");
                return "success";
            }
            logger.info(" KLFNotify {} 支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            logger.error("快乐付支付回调失败，系统业务异常，异常订单号:{}" + order_no);
            e.printStackTrace();
            logger.info(" KLFNotify {} 支付回调业务处理异常:{}" + e.getMessage(), e);
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(" KLFNotify {} 支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }


    }

    /**
     * 全聚付支付回调
     *
     * @param request
     * @return
     */
    @LogApi("全聚付支付回调")
    @RequestMapping("/QJFNotify.do")
    @ResponseBody
    public String QJFNotify(HttpServletRequest request) {
        logger.info("[QJFNotify]全聚付回调请求串  QJFNotify(HttpServletRequest request = {}  -start" + request);
        String msg = "OK";
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info("[QJF]全聚付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info("[QJFNotify]全聚付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //支付成功返回0
        String orderId = infoMap.get("transaction_id");// 平台订单号
        String orderNo = infoMap.get("orderid");//商户订单号
        String trade_status = infoMap.get("returncode").equals("00") ? "success" : "fail"; //订单状态 00为支付成功,其他都是失败！
        String t_trade_status = "success";// 订单状态，“1” 为成功
        String order_amount = infoMap.get("amount"); //单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info("[QJFNotify]全聚付回调获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info("[QJFNotify]全聚付回调实际充值金额为：{}" + amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (payMap.containsKey(orderNo)) {
            logger.info("[QJFNotify]全聚付回调订单号:{}重复调用", orderNo);
            return ret_str_failed;
        }
        payMap.put(orderNo, "1");
        try {
            logger.info("[QJFNotify]全聚付回调 执行回调业务开始=========================START===========================");
            //保存文件记录
            NotifyUtils.savePayFile("KLFNotify {}", infoMap, IPTools.getIp(request));
            logger.info("保存文件记录成功！");
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(orderNo);
            if (rechargeOrderVO == null) {
                logger.info("[QJFNotify]全聚付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", orderNo);
                return ret_str_failed;
            }
            rechargeOrderVO.setFinishTime(new Date());
            rechargeOrderVO.setTradeNo(orderId);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(" [QJFNotify]全聚付回调  非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error("QJFNotify回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info("[QJFNotify]全聚付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_QJF)) {
                QJFPayServiceImpl qjfPayService = new QJFPayServiceImpl(pmapsconfig);
                String rmsg = qjfPayService.callback(infoMap);
                logger.info("[QJFNotify]全聚付回调 验签结果 ============  {} end" + rmsg);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info("[QJFNotify]全聚付回调 支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info("[QJFNotify]全聚付回调 支付回调验签成功!");
            } else {
                // 异常请求
                logger.error("[QJFNotify]全聚付回调 支付回调异常请求");
                return ret_str_failed;
            }
            logger.info("[QJFNotify]全聚付回调 支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info("[QJFNotify]全聚付回调 支付回调业务处理成功=======================SUCCESS====================");
                return "OK";
            }
            logger.info(" QJFNotify {} 支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            logger.error("[QJFNotify]全聚付回调失败，系统业务异常，异常订单号:{}" + orderNo);
            e.printStackTrace();
            logger.info(" QJFNotify {} 支付回调业务处理异常:{}" + e.getMessage(), e);
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(orderNo)) {
                logger.info(" QJFNotify {} 支付回调业务处理成功,删除缓存中的订单KEY:{}", orderNo);
                payMap.remove(orderNo);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description YUH 宇恒支付 回调通知
     */
    @LogApi("宇恒支付回调")
    @RequestMapping("/YUHNotify.do")
    @ResponseBody
    public String YUHNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "YUHNotify";
        logger.info(clazz_name + "[YUHNotify]宇恒支付回调 开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "[YUHNotify]宇恒支付回调 获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "[YUHNotify]宇恒支付回调 请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("r6_Order");// 平台订单号
        String trade_no = infoMap.get("r2_TrxId");//流水号
        String trade_status = infoMap.get("r1_Code");//1:成功，其他失败
        String t_trade_status = "1";//1:成功，其他失败
        String order_amount = infoMap.get("r3_Amt");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "[YUHNotify]宇恒支付回调 订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "[YUHNotify]宇恒支付回调 通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "[YUHNotify]宇恒支付回调 验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YUH)) {
                YUHPayServiceImpl yuh = new YUHPayServiceImpl(pmapsconfig);
                String rmsg = yuh.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "[YUHNotify]宇恒支付回调 验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "[YUHNotify]宇恒支付回调 验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "[YUHNotify]宇恒支付回调 异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "[YUHNotify]宇恒支付回调 验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "[YUHNotify]宇恒支付回调 业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "[YUHNotify]宇恒支付回调 业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "[YUHNotify]宇恒支付回调 业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "[YUHNotify]宇恒支付回调 业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * 银河支付1 回调通知
     *
     * @param request
     * @param response
     * @param session
     * @return
     */

    @LogApi("银河支付1回调")
    @RequestMapping("/YH1ZFNotify.do")
    @ResponseBody
    public String YHZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回success
        String clazz_name = "YH1ZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("total_fee");//实际支付金额
        if (org.apache.commons.lang3.StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = request.getParameter("order_id");// 平台订单号
        String trade_no = request.getParameter("out_transaction_id");// 平台订单号
        String trade_status = request.getParameter("pay_result");// 处理结果
        String t_trade_status = "0";// 表示成功状态
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount) / 100);
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YH1ZF)) {
                YH1ZFPayServiceImpl payService = new YH1ZFPayServiceImpl(pmapsconfig);
                String rmsg = payService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }


    /**
     * 易盛通支付
     *
     * @param request
     * @param response
     * @param session
     * @return
     * @Description EST回调通知
     */
    @LogApi("易盛通支付回调")
    @RequestMapping("/ESTNotify.do")
    @ResponseBody
    public String ESTNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Map<String, String> map = new HashMap<>();

//        String ret__success = "200";//收到通知后请回复  SUCCESS,对接方在支付成功后只收“200”
        String clazz_name = "ESTNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            map.put("result_code", "FAIL");
            map.put("result_msg", "支付回调获取请求参数为空!");
            return JSONUtils.toJSONString(map);
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("total_amount");//实际支付金额
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            map.put("result_code", "FAIL");
            map.put("result_msg", "获取实际支付金额为空!");
            return JSONUtils.toJSONString(map);
        }
        String order_no = infoMap.get("tradeno");// 平台订单号
        String trade_no = infoMap.get("serialid");//交易流水号
        String trade_status = infoMap.get("trxstatus");//success代表支付成功
        String t_trade_status = "0000";//0000:成功
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                map.put("result_code", "FAIL");
                map.put("result_msg", "获取实际支付金额为空!");
                return JSONUtils.toJSONString(map);
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                map.put("result_code", "FAIL");
                map.put("result_msg", "查询支付商信息失败!");
                return JSONUtils.toJSONString(map);
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_EST)) {
                ESTPayServiceImpl est = new ESTPayServiceImpl(pmapsconfig);
                String rmsg = est.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    map.put("result_code", "FAIL");
                    map.put("result_msg", "支付回调验签失败!");
                    return JSONUtils.toJSONString(map);
                }

                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                map.put("result_code", "FAIL");
                map.put("result_msg", "支付回调异常请求!");
                return JSONUtils.toJSONString(map);
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                map.put("result_code", "SUCCESS");
                map.put("result_msg", "支付回调业务处理成功!");
                return JSONUtils.toJSONString(map);
            }
            logger.info(clazz_name + "支付回调业务处理失败=======================FAILD====================");
            map.put("result_code", "FAIL");
            map.put("result_msg", "支付回调业务处理失败!");
            return JSONUtils.toJSONString(map);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description JQ 聚前支付 回调通知
     */
    @LogApi("聚前支付回调")
    @RequestMapping("/JQNotify.do")
    @ResponseBody
    public String JQNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";//收到通知后请回复  success
        String clazz_name = "JQNotify";
        logger.info(clazz_name + "[JQNotify]聚前支付回调 开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "[JQNotify]聚前支付回调 获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "[JQNotify]聚前支付回调 请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("mch_order_no");// 商户订单号
        String trade_no = infoMap.get("order_no");//流水号
        String trade_status = infoMap.get("result_code");//1:成功，其他失败
        String t_trade_status = "SUCCESS";//1:成功，其他失败
        String order_amount = infoMap.get("real_amount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "[JQNotify]聚前支付回调 订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "[JQNotify]聚前支付回调 通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error("JQNotify回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "[JQNotify]聚前支付回调 验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_JQ)) {
                JQPayServiceImpl jqPayService = new JQPayServiceImpl(pmapsconfig);
                String rmsg = jqPayService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "[JQNotify]聚前支付回调 验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "[JQNotify]聚前支付回调 验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "[JQNotify]聚前支付回调 异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "[JQNotify]聚前支付回调 验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "[JQNotify]聚前支付回调 业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "[JQNotify]聚前支付回调 业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "[JQNotify]聚前支付回调 业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "[JQNotify]聚前支付回调 业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 聚汇民付支付回调通知
     */
    @LogApi("聚汇民付支付回调")
    @RequestMapping("/JHNFNotify.do")
    @ResponseBody
    public String JHNFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复
        String clazz_name = "JHNFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = request.getParameter("out_trade_no");// 平台订单号
        String trade_no = request.getParameter("trade_no");// 平台订单号
        String trade_status = request.getParameter("status");// 处理结果,支付状态，支付状态只有成功一个状态（success）
        String amount = request.getParameter("amount");
        String t_trade_status = "success";// 表示成功状态
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(amount));
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_JHNF)) {
                JHNFPayServiceImpl payService = new JHNFPayServiceImpl(pmapsconfig);
                String rmsg = payService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description FNT 592支付 回调通知
     */
    @LogApi("592支付回调")
    @RequestMapping("/FNTNotify.do")
    @ResponseBody
    public String FNTNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";//收到通知后请回复  OK
        String clazz_name = "JQNotify";
        logger.info(clazz_name + "[FNTNotify]592支付回调 开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "[FNTNotify]592支付回调 获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "[FNTNotify]592支付回调 请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 商户订单号
        String trade_no = infoMap.get("transaction_id");//流水号
        String trade_status = infoMap.get("returncode");//1:成功，其他失败
        String t_trade_status = "00";//1:成功，其他失败
        String order_amount = infoMap.get("amount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "[FNTNotify]592支付回调 订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "[FNTNotify]592支付回调 通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setFinishTime(new Date());
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "[FNTNotify]592支付回调 验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_FNT)) {
                FNTPayServiceImpl fntPayService = new FNTPayServiceImpl(pmapsconfig);
                String rmsg = fntPayService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "[FNTNotify]592支付回调 验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "[FNTNotify]592支付回调 验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "[FNTNotify]592支付回调 异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "[FNTNotify]592支付回调 验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "[FNTNotify]592支付回调 业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "[FNTNotify]592支付回调 业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "[FNTNotify]592支付回调 业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "[FNTNotify]592支付回调 业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 追光支付回调通知
     */
    @LogApi("追光支付回调")
    @RequestMapping("/ZGZFNotify.do")
    @ResponseBody
    public String ZGZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";//收到通知后请回复
        String clazz_name = "ZGZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");// 平台订单号
        String trade_status = infoMap.get("returncode");//“00” 为成功
        String t_trade_status = "00";//“00” 为成功
        String order_amount = infoMap.get("amount");
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "支付回调获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_ZGZF)) {
                ZGZFPayServiceImpl payService = new ZGZFPayServiceImpl(pmapsconfig);
                String rmsg = payService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }


    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 云捷支付回调通知
     */
    @LogApi("云捷支付回调")
    @RequestMapping("/YJZFNotify.do")
    @ResponseBody
    public String YJZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复
        String clazz_name = "YJZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("sdorderno");// 平台订单号
        String trade_no = infoMap.get("sdpayno");// 平台订单号
        String trade_status = infoMap.get("status");//1:成功，其他失败
        String t_trade_status = "1";//“00” 为成功
        String order_amount = infoMap.get("total_fee");
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "支付回调获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YJZF)) {
                YJZFPayServiceImpl payService = new YJZFPayServiceImpl(pmapsconfig);
                String rmsg = payService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 贝富支付回调通知
     */
    @LogApi("贝富支付回调")
    @RequestMapping("/BEIFUNotify.do")
    @ResponseBody
    public String BEIFUNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复
        String clazz_name = "BEIFUNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("p_oid");// 商户订单号
        String trade_no = infoMap.get("p_sid");//流水号
        String trade_status = infoMap.get("p_code");//1:成功，其他失败
        String t_trade_status = "1";//1:成功，其他失败
        String order_amount = infoMap.get("p_money");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_BFP)) {
                BFPPayServiceImpl payService = new BFPPayServiceImpl(pmapsconfig);
                String rmsg = payService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description DYZF 店员支付 回调通知
     */
    @LogApi("店员支付回调")
    @RequestMapping("/DYZFNotify.do")
    @ResponseBody
    public String DYZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  OK
        String clazz_name = "DYZFNotify";
        logger.info(clazz_name + "[DYZFNotify]店员支付回调 开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "[DYZFNotify]店员支付回调 获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "[DYZFNotify]店员支付回调 请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("shop_no");// 商户订单号
        String trade_no = infoMap.get("trade_no");//流水号
        String trade_status = infoMap.get("status");//1:成功，其他失败
        String t_trade_status = "0";//1:成功，其他失败
        String order_amount = infoMap.get("money");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "[DYZFNotify]店员支付回调 订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "[DYZFNotify]店员支付回调 通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setFinishTime(new Date());
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "[DYZFNotify]店员支付回调 验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_DYZF)) {
                DYZFPayServiceImpl dyzfPayService = new DYZFPayServiceImpl(pmapsconfig);
                String rmsg = dyzfPayService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "[DYZFNotify]店员支付回调 验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "[DYZFNotify]店员支付回调 验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "[DYZFNotify]店员支付回调 异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "[DYZFNotify]店员支付回调 验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "[DYZFNotify]店员支付回调 业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "[DYZFNotify]店员支付回调 业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "[DYZFNotify]店员支付回调 业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "[DYZFNotify]店员支付回调 业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * 易达支付回调
     *
     * @param request
     * @return
     */
    @LogApi("易达支付回调")
    @RequestMapping("/YDZFNotify.do")
    @ResponseBody
    public String YDZFNotify(HttpServletRequest request) {
        logger.info("易达支付回调方法  YDZFNotify(HttpServletRequest request = {} -start" + request);
        logger.info("易达支付回调方法开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info("YDZFNotify {} 易达支付回调方法获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info("YDZFNotify {}易达支付回调方法请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //支付成功返回
        String orderId = infoMap.get("trade_no");// 第三方平台订单号
        String orderNo = infoMap.get("out_trade_no");//商户订单号
        String trade_status = infoMap.get("status").equals("1") ? "success" : "pending"; //订单状态 1为支付成功,其他都是失败！
        String t_trade_status = "success";// 订单状态，“1” 为成功
        String order_amount = infoMap.get("money"); // 单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info("YDZFNotify {}获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info("YDZFNotify {} 易达支付回调实际充值金额为：{}" + amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (payMap.containsKey(orderNo)) {
            logger.info("YDZFNotify {}支付回调订单号:{}重复调用", orderNo);
            return ret_str_failed;
        }
        payMap.put(orderNo, "1");
        try {
            logger.info("YDZFNotify {} 执行回调业务开始=========================START===========================");
            //保存文件记录
            NotifyUtils.savePayFile("YDZFNotify {}", infoMap, IPTools.getIp(request));
            logger.info("保存文件记录成功！");
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(orderNo);
            if (rechargeOrderVO == null) {
                logger.info(" YDZFNotify {}支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", orderNo);
                return ret_str_failed;
            }
            rechargeOrderVO.setFinishTime(new Date());
            rechargeOrderVO.setTradeNo(orderId);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(" GBNotify {} 非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error("YDZFNotify回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(" YDZFNotify {} 支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YDZF)) {
                YDZFPayServiceImpl gb = new YDZFPayServiceImpl(pmapsconfig);
                String rmsg = gb.callback(infoMap);
                logger.info("易达支付回调 验签结果 ============  {} end" + rmsg);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(" YDZFNotify {} 支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(" YDZFNotify {} 易达支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(" YDZFNotify {} 易达支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(" YDZFNotify {} 易达支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info("YDZFNotify {} 易达支付回调业务处理成功=======================SUCCESS====================");
                return "success";
            }
            logger.info(" YDZFNotify {} 易达支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            logger.error("易达支付回调失败，系统业务异常，异常订单号:{}" + orderNo);
            e.printStackTrace();
            logger.info(" YDZFNotify {} 易达支付回调业务处理异常:{}" + e.getMessage(), e);
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(orderNo)) {
                logger.info(" YDZFNotify {} 易达支付回调业务处理成功,删除缓存中的订单KEY:{}", orderNo);
                payMap.remove(orderNo);
            }

        }
    }

    /**
     * 捷付支付回调
     *
     * @param request
     * @return
     */
    @LogApi("捷付支付回调")
    @RequestMapping("/JEENotify.do/{cagent}")
    @ResponseBody
    public String JEENotify(HttpServletRequest request, @PathVariable("cagent") String cagent) {
        String ret__success = "{\"stauts\":1,\"error_msg\"}";//收到通知后请回复
        String clazz_name = "JEENotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        CagentYespayVO cagentYespayVO = null;
        try {//查询支付商信息
            cagentYespayVO = notifyService.getCagentYsepayByCagentAndPayment(cagent, "JEE");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(clazz_name + "通过平台编码和支付商编码获取支付商信息为空");
            return ret_str_failed;
        }
        if (cagentYespayVO == null) {
            logger.info(clazz_name + "非法支付商,查询支付商信息失败,平台编号:{},支付商名称:{}", cagent, "JEE");
            return ret_str_failed;
        }
        String paymentName = cagentYespayVO.getPaymentName();//支付商编码

        Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息

        String merchantPrivateKey;
        if (pmapsconfig.containsKey("merchantPrivateKey") && StringUtils.isNotBlank(pmapsconfig.get("merchantPrivateKey"))) {
            merchantPrivateKey = pmapsconfig.get("merchantPrivateKey");
        } else {
            logger.error(clazz_name + "获取捷付支付商户私钥异常");
            return ret_str_failed;
        }
        String paymentPublicKey;
        if (pmapsconfig.containsKey("paymentPublicKey") && StringUtils.isNotBlank(pmapsconfig.get("paymentPublicKey"))) {
            paymentPublicKey = pmapsconfig.get("paymentPublicKey");
        } else {
            logger.error(clazz_name + "获取捷付支付平台公钥异常");
            return ret_str_failed;
        }

        String data = request.getParameter("data");
        if (StringUtils.isBlank(data)) {
            logger.error(clazz_name + "回调请求参数data为空");
            return ret_str_failed;
        }
        String sign = request.getParameter("sign");
        if (StringUtils.isBlank(data)) {
            logger.error(clazz_name + "回调请求参数sign为空");
            return ret_str_failed;
        }
        //通过data参数，和商户私钥解码获取参数
        Map<String, String> infoMap = JEEPayServiceImpl.getNotifyParams(data, merchantPrivateKey);

        logger.info(clazz_name + "解码后参数：" + JSONObject.fromObject(infoMap).toString());

        if (MapUtils.isEmpty(infoMap)) {
            logger.error(clazz_name + "解码参数异常，解码后参数为空");
            return ret_str_failed;
        }

        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("merchant_order_no");// 平台订单号
        String trade_no = infoMap.get("trans_id");// 支付商订单号
        String trade_status = "1";//1:成功，其他失败
        String t_trade_status = "1";//“00” 为成功
        String order_amount = infoMap.get("amount");
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "支付回调获取实际支付金额为空!");
            return ret_str_failed;
        }

        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        //=================================获取回调基本参数结果--END===========================//
        try {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
                return ret_str_failed;
            }
            payMap.put(order_no, "1");

            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals("JEE")) {
                boolean isVaild = JEEPayServiceImpl.verifyRequest(data, sign, paymentPublicKey);
                if (!isVaild) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * 宝付惠支付回调
     *
     * @param request
     * @return
     */
    @LogApi("宝付惠支付回调")
    @RequestMapping("/BFHNotify.do")
    @ResponseBody
    public String BFHNotify(HttpServletRequest request) {
        logger.info("宝付惠支付回调方法  BFHNotify(HttpServletRequest request = {} -start" + request.toString());
        logger.info("宝付惠支付回调方法开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (StringUtils.isEmpty(infoMap.get("transaction_id")) || StringUtils.isEmpty(infoMap.get("returncode"))) {
            logger.info("BFHNotify {} 宝付惠支付回调方法获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info("BFHNotify {} 宝付惠支付回调方法请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //支付成功返回
        String orderId = infoMap.get("transaction_id");// 第三方平台订单号
        String orderNo = infoMap.get("orderid");//商户订单号
        String trade_status = infoMap.get("returncode").equals("00") ? "success" : "pending"; //订单状态 00为支付成功,其他都是失败！
        String t_trade_status = "success";// 订单状态， 为成功
        String order_amount = infoMap.get("amount"); // 单位：元
        if (StringUtils.isBlank(order_amount)) {
            logger.info("BFHNotify {}获取实际支付金额为空!");
            return ret_str_failed;
        }
        double amount = Double.parseDouble(order_amount);
        logger.info("BFHNotify {}宝付惠支付回调实际充值金额为：{}" + amount);
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (payMap.containsKey(orderNo)) {
            logger.info("BFHNotify {}支付回调订单号:{}重复调用", orderNo);
            return ret_str_failed;
        }
        payMap.put(orderNo, "1");
        try {
            logger.info("BFHNotify {} 执行回调业务开始=========================START===========================");
            //保存文件记录
            NotifyUtils.savePayFile("BFHNotify {}", infoMap, IPTools.getIp(request));
            logger.info("保存文件记录成功！");
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(orderNo);
            if (rechargeOrderVO == null) {
                logger.info("BFHNotify {}宝付惠支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", orderNo);
                return ret_str_failed;
            }
            rechargeOrderVO.setFinishTime(new Date());
            rechargeOrderVO.setTradeNo(orderId);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);//实际支付金额，单位元
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info("BFHNotify {} 非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error("BFHNotify回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info("BFHNotify {} 宝付惠支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_BFH)) {
                BFHPayServiceImpl gb = new BFHPayServiceImpl(pmapsconfig);
                String rmsg = gb.callback(infoMap);
                logger.info("宝付惠支付回调 验签结果 ============  {} end" + rmsg);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info("BFHNotify {} 宝付惠支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info("BFHNotify {} 宝付惠支付回调验签成功!");
            } else {
                // 异常请求
                logger.error("BFHNotify {}宝付惠支付回调异常请求");
                return ret_str_failed;
            }
            logger.info("BFHNotify {} 宝付惠支付回调验签结束=======================END====================");
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info("BFHNotify {} 宝付惠支付回调业务处理成功=======================SUCCESS====================");
                return "OK";
            }
            logger.info("BFHNotify {} 宝付惠支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            logger.error("宝付惠支付回调失败，系统业务异常，异常订单号:{}" + orderNo);
            e.printStackTrace();
            logger.info("BFHNotify {} 宝付惠支付回调业务处理异常:{}" + e.getMessage(), e);
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(orderNo)) {
                logger.info("BFHNotify {} 宝付惠支付回调业务处理成功,删除缓存中的订单KEY:{}", orderNo);
                payMap.remove(orderNo);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description XLZF 信连支付回调通知
     */
    @LogApi("信连支付回调")
    @RequestMapping("/XLZFNotify.do")
    @ResponseBody
    public String XLZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "XLZFNotify";
        logger.info(clazz_name + "[XLZF]信连支付回调 开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "[XLZF]信连支付回调 获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "[XLZF]信连支付回调 请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("trade_out_no");// 商户订单号
        String trade_no = infoMap.get("pay_sn");//流水号
        String trade_status = infoMap.get("error");//0:成功，其他失败
        String t_trade_status = "0";//0:成功，其他失败
        String order_amount = infoMap.get("real_amount");// 实际支付金额
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isNotBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "[XLZF]信连支付回调 订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "[XLZF]信连支付回调 通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setFinishTime(new Date());
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount) / 100);
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "[XLZF]信连支付回调 验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XLZF)) {
                XLZFPayServiceImpl xlzfPayService = new XLZFPayServiceImpl(pmapsconfig);
                String rmsg = xlzfPayService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "[XLZF]信连支付回调 验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "[XLZF]信连支付回调 验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "[XLZF]信连支付回调 异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "[XLZF]信连支付回调 验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "[XLZF]信连支付回调 业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "[XLZF]信连支付回调 业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "[XLZF]信连支付回调 业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "[XLZF]信连支付回调 业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description WBF 微宝付支付回调通知
     */
    @LogApi("微宝付支付回调")
    @RequestMapping("/WBFNotify.do")
    @ResponseBody
    public String WBFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";//收到通知后请回复  success
        String clazz_name = "WBFNotify";
        logger.info(clazz_name + "[WBF]微宝付支付回调 开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "[WBF]微宝付支付回调 获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "[WBF]微宝付支付回调 请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("outTradeNo");// 商户订单号
        String trade_no = infoMap.get("trxNo");//流水号
        String trade_status = infoMap.get("tradeStatus");//0:成功，其他失败
        String t_trade_status = "SUCCESS";//0:成功，其他失败
        String order_amount = infoMap.get("orderPrice");// 实际支付金额
        if (StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "[WBF]微宝付支付回调 订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            NotifyUtils.savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "[WBF]微宝付支付回调 通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setFinishTime(new Date());
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "[WBF]微宝付支付回调 验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_WBF)) {
                WBFPayServiceImpl wbfPayService = new WBFPayServiceImpl(pmapsconfig);
                String rmsg = wbfPayService.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "[WBF]微宝付支付回调 验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "[WBF]微宝付支付回调 验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name + "[WBF]微宝付支付回调 异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name + "[WBF]微宝付支付回调 验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "[WBF]微宝付支付回调 业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name + "[WBF]微宝付支付回调 业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "[WBF]微宝付支付回调 业务处理异常:{}", e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "[WBF]微宝付支付回调 业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }
}

