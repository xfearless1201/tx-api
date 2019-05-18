package com.cn.tianxia.api.web;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.base.annotation.LogApi;
import com.cn.tianxia.api.common.PayConstant;
import com.cn.tianxia.api.pay.impl.ABHPayServiceImpl;
import com.cn.tianxia.api.pay.impl.APAYPayServiceImpl;
import com.cn.tianxia.api.pay.impl.ASPayServiceImpl;
import com.cn.tianxia.api.pay.impl.BATPayServiceImpl;
import com.cn.tianxia.api.pay.impl.BCZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.BFZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.BLPayServiceImpl;
import com.cn.tianxia.api.pay.impl.BPZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.CFZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.CORALPayServiceImpl;
import com.cn.tianxia.api.pay.impl.CZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.DQPayServiceImpl;
import com.cn.tianxia.api.pay.impl.DSZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.FHZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.FIREPayServiceImpl;
import com.cn.tianxia.api.pay.impl.FXPayServiceImpl;
import com.cn.tianxia.api.pay.impl.GBZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.GPAYPayServiceImpl;
import com.cn.tianxia.api.pay.impl.HANYPayServiceImpl;
import com.cn.tianxia.api.pay.impl.HDZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.HLSYPayServiceImpl;
import com.cn.tianxia.api.pay.impl.HONGYPayServiceImpl;
import com.cn.tianxia.api.pay.impl.HUAXPayServiceImpl;
import com.cn.tianxia.api.pay.impl.HXPayServiceImpl;
import com.cn.tianxia.api.pay.impl.HYPayServiceImpl;
import com.cn.tianxia.api.pay.impl.HYZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.IIZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.JCZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.JFTPPayServiceImpl;
import com.cn.tianxia.api.pay.impl.JIANPayServiceImpl;
import com.cn.tianxia.api.pay.impl.JIDAPayServiceImpl;
import com.cn.tianxia.api.pay.impl.JRZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.KJFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.KLTPayServiceImpl;
import com.cn.tianxia.api.pay.impl.LBAOPayServiceImpl;
import com.cn.tianxia.api.pay.impl.LBZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.LMZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.MJFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.PAYSPayServiceImpl;
import com.cn.tianxia.api.pay.impl.QFTZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.QGZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.RCZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.RUYIPayServiceImpl;
import com.cn.tianxia.api.pay.impl.RXPayServiceImpl;
import com.cn.tianxia.api.pay.impl.SHANPayServiceImpl;
import com.cn.tianxia.api.pay.impl.SHUNPayServiceImpl;
import com.cn.tianxia.api.pay.impl.SMZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.SRBPayServiceImpl;
import com.cn.tianxia.api.pay.impl.SYOUPayServiceImpl;
import com.cn.tianxia.api.pay.impl.TEJFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.TESTPayServiceImpl;
import com.cn.tianxia.api.pay.impl.TEYEPayServiceImpl;
import com.cn.tianxia.api.pay.impl.TONGPayServiceImpl;
import com.cn.tianxia.api.pay.impl.TXZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.WANFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.WMPPayServiceImpl;
import com.cn.tianxia.api.pay.impl.WOWPayServiceImpl;
import com.cn.tianxia.api.pay.impl.WTPayServiceImpl;
import com.cn.tianxia.api.pay.impl.WTXXPayServiceImpl;
import com.cn.tianxia.api.pay.impl.WZZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.XBFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.XCFPPayServiceImpl;
import com.cn.tianxia.api.pay.impl.XFTPayServiceImpl;
import com.cn.tianxia.api.pay.impl.XHFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.XINFAPayServiceImpl;
import com.cn.tianxia.api.pay.impl.XPAYPayServiceImpl;
import com.cn.tianxia.api.pay.impl.XUNCPayServiceImpl;
import com.cn.tianxia.api.pay.impl.XWTPayServiceImpl;
import com.cn.tianxia.api.pay.impl.XXBPayServiceImpl;
import com.cn.tianxia.api.pay.impl.XYFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.XYZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.XYZPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YDPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YFFSPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YFZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YHBPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YHZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YICZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YIFAPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YIFBPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YINFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YISZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YIZHIPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YMZFPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YUNSUPayServiceImpl;
import com.cn.tianxia.api.pay.impl.YZFZFPayServiceImpl;
import com.cn.tianxia.api.po.ResultResponse;
import com.cn.tianxia.api.service.NotifyService;
import com.cn.tianxia.api.service.UserService;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.JDBCTools;
import com.cn.tianxia.api.utils.JSONUtils;
import com.cn.tianxia.api.utils.bg.HashUtil;
import com.cn.tianxia.api.utils.mjf.MJFToolKit;
import com.cn.tianxia.api.utils.pay.DESUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.utils.pay.TestParamsUtils;
import com.cn.tianxia.api.utils.xinfa.ToolKit;
import com.cn.tianxia.api.utils.xyz.HttpUtil;
import com.cn.tianxia.api.utils.xyz.XMLUtils;
import com.cn.tianxia.api.vo.CagentYespayVO;
import com.cn.tianxia.api.vo.RechargeOrderVO;

import net.sf.json.JSONObject;

/**
 * @author Hardy
 * @version 1.1.0
 * @ClassName NotifyController
 * @Description 支付回调接口
 * @Date 2018年9月30日 上午11:03:56
 */
@RequestMapping("Notify")
@Controller
@Scope("prototype")
public class NotifyController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotifyService notifyService;

    private String ret_str_success = "success";
    private String ret_str_failed = "fail";
    private String t_trade_status; // 商户交易状态

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 小熊宝回调通知
     */
    @LogApi("小熊宝回调")
    @RequestMapping("/XXBNotify.do")
    @ResponseBody
    public String XXBNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "200";// 成功返回success
        String clazz_name = "XXBNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = request.getParameter("merchantOrderNo");// 平台订单号
        String trade_no = request.getParameter("orderNo");// 平台订单号
        String trade_status = "SUCCESS";
        String t_trade_status = "SUCCESS";// 表示成功状态
        String order_amount = infoMap.get("payAmount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isEmpty(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XXB)) {
                XXBPayServiceImpl xxb = new XXBPayServiceImpl(pmapsconfig);
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
     * @Description 珊瑚支付回调通知
     */
    @LogApi("珊瑚支付回调")
    @RequestMapping("/CORALNotify.do")
    @ResponseBody
    public String CORALNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";// 成功返回success
        String clazz_name = "CORALNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");// 支付商订单号
        String trade_status = infoMap.get("returncode");// 处理结果
        String t_trade_status = "00";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_CORAL)) {
                CORALPayServiceImpl xxb = new CORALPayServiceImpl(pmapsconfig);
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

    @LogApi("大强支付回调")
    @RequestMapping("/DQNotify.do")
    @ResponseBody
    public String DQNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回success
        String clazz_name = "DQNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("memberOrderId");// 平台订单号
        String trade_no = infoMap.get("orderId");// 支付商订单号
        String trade_status = infoMap.get("stateCode");// 处理结果
        String t_trade_status = "SUCCESS";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_DQ)) {
                DQPayServiceImpl xxb = new DQPayServiceImpl(pmapsconfig);
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
     * 功能描述: 鑫发支付异步回调
     *
     * @param request * @param response  * @param session
     * @Author: Elephone
     * @Date: 2018年08月18日 16:04:24
     * @return: java.lang.String
     **/
    @LogApi("鑫发支付回调")
    @RequestMapping("/XINFAPAYNotify.do")
    @ResponseBody
    public String XINFAPAYNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";// 成功返回success
        logger.info("[XINFAPAYNotify]鑫发支付异步回调开始----------------------------- XINFAPAYNotify.do start ------------------------------");
        String data = request.getParameter("data");
        logger.info("[XINFAPAYNotify]鑫发支付异步回调请求参数:{}", data);
        String order_no = request.getParameter("orderNo");
        logger.info("[XINFAPAYNotify]鑫发支付异步回调请求参数订单号:{}", order_no);
        String clazz_name = "XINFAPAYNotify";
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
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            /*byte[] bytes = ToolKit.decryptByPrivateKey(new BASE64Decoder().decodeBuffer(data),Base64.getDecoder().decode(data)
                    pmapsconfig.get("MECHA_PRIVATE_KEY"));*/
            byte[] bytes = ToolKit.decryptByPrivateKey(Base64.getDecoder().decode(data),
                    pmapsconfig.get("MECHA_PRIVATE_KEY"));
            String resultData = new String(bytes, ToolKit.CHARSET);// 解密数据
            JSONObject jsonObj = JSONObject.fromObject(resultData);
            Map<String, String> infoMap = new TreeMap<String, String>();
            infoMap.put("merchNo", jsonObj.getString("merchNo"));
            infoMap.put("payType", jsonObj.getString("payType"));
            infoMap.put("orderNo", jsonObj.getString("orderNo"));
            infoMap.put("amount", jsonObj.getString("amount"));
            infoMap.put("goodsName", jsonObj.getString("goodsName"));
            infoMap.put("payStateCode", jsonObj.getString("payStateCode"));// 支付状态
            infoMap.put("payDate", jsonObj.getString("payDate"));// yyyyMMddHHmmss
            infoMap.put("sign", jsonObj.getString("sign"));//
            //=================================获取回调基本参数结果--START===========================//
            String trade_no = System.currentTimeMillis() + "";
            String trade_status = jsonObj.getString("payStateCode");// 处理结果
            String t_trade_status = "00";// 表示成功状态
            String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
            //=================================获取回调基本参数结果--END===========================//
            // 保存文件记录
            savePayFile("XINFAPAYNotify", infoMap, IPTools.getIp(request));
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());

            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XINFA)) {
                XINFAPayServiceImpl payService = new XINFAPayServiceImpl(pmapsconfig);
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
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    @LogApi("XPAY支付回调")
    @RequestMapping("/XPAYNotify.do")
    @ResponseBody
    public String XPAYNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";// 成功返回success
        String clazz_name = "XPAYNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = request.getParameter("orderid");// 平台订单号
        String trade_no = request.getParameter("orderid");// 支付商订单号
        String trade_status = request.getParameter("returncode");// 处理结果
        String t_trade_status = "2";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XPAY)) {
                XPAYPayServiceImpl payService = new XPAYPayServiceImpl(pmapsconfig);
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

    @LogApi("金彩支付回调")
    @RequestMapping("/JCZFNotify.do")
    @ResponseBody
    public Object JCZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";
        String clazz_name = "JCZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("amount");//实际支付金额
        if (org.apache.commons.lang3.StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = request.getParameter("order_id");// 平台订单号
        String trade_no = request.getParameter("order_abc");// 平台订单号
        String trade_status = request.getParameter("status");// 处理结果
        String t_trade_status = "1";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_JCZF)) {
                JCZFPayServiceImpl payService = new JCZFPayServiceImpl(pmapsconfig);
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
     * 天下支付回调
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("天下支付回调")
    @RequestMapping("/TXZFNotify.do")
    @ResponseBody
    public String TXZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回success
        String clazz_name = "TXZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("outTradeNo");// 商户订单号
        String trade_no = infoMap.get("trxNo");// 平台订单号
        String trade_status = infoMap.get("tradeStatus");// 处理结果
        String t_trade_status = "SUCCESS";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_TXZF)) {
                TXZFPayServiceImpl payService = new TXZFPayServiceImpl(pmapsconfig);
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
     * 华菱盛业支付回调
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("华菱盛业支付回调")
    @RequestMapping("/HLSYNotify.do")
    @ResponseBody
    public String HLSYNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回success
        String clazz_name = "HLSYNotify";
        logger.info(" ----------------------------- HLSYNotify.do start ------------------------------");
        byte[] decodeBase64 = HashUtil.decodeBase64(request.getParameter("message"));
        String message = new String(decodeBase64);
        String signature = request.getParameter("signature");
        logger.info("decodeBase64参数===>" + message);
        JSONObject reqJsonObj = JSONObject.fromObject(message);
        HashMap<String, String> map = JSONUtils.toHashMap(message);
        Map<String, String> infoMap = new TreeMap<String, String>();
        infoMap.putAll(map);
        infoMap.put("sign", signature);
        logger.info("请求参数===>" + infoMap.toString());
        Integer order_amount = reqJsonObj.getInt("amount");
        if (order_amount == 0) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("orderNo");// 商户订单号
        String trade_no = infoMap.get("trxorderNo");// 平台订单号
        String trade_status = infoMap.get("status");// 处理结果
        String t_trade_status = "1";// 表示成功状态
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount.toString()) / 100);
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_HLSY)) {
                HLSYPayServiceImpl payService = new HLSYPayServiceImpl(pmapsconfig);
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
     * 快捷付回调
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("快捷支付回调")
    @RequestMapping("/KjfNotify.do")
    @ResponseBody
    public String KjfNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "ok";// 成功返回success
        String clazz_name = "KjfNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = new TreeMap<String, String>();
        Enumeration enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            infoMap.put(paraName, request.getParameter(paraName).toString());
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("PayMoney");//实际支付金额
        if (org.apache.commons.lang3.StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }

        String order_no = request.getParameter("OrderId");// 商户订单号
        String trade_no = request.getParameter("TransactionId");// 平台订单号
        String trade_status = request.getParameter("ErrCode");// 处理结果
        String t_trade_status = "0000";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_KFJ)) {
                KJFPayServiceImpl payService = new KJFPayServiceImpl(pmapsconfig);
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

    @LogApi("银河支付回调")
    @RequestMapping("/YHZFNotify.do")
    @ResponseBody
    public String YHZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回success
        String clazz_name = "YHZFNotify";
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YHZF)) {
                YHZFPayServiceImpl payService = new YHZFPayServiceImpl(pmapsconfig);
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
     * 云付支付回调
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("云付支付回调")
    @RequestMapping("/YFZFNotify.do")
    @ResponseBody
    public String YFZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回success
        String clazz_name = "YFZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = request.getParameter("orderNo");// 平台订单号
        String trade_no = request.getParameter("trxNo");// 交易平台编号
        String trade_status = request.getParameter("status");// 交易结果
        String t_trade_status = "SUCCESS";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YFZF)) {
                YFZFPayServiceImpl payService = new YFZFPayServiceImpl(pmapsconfig);
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
     * 融灿支付回调
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("融灿支付回调")
    @RequestMapping("/RczfNotify.do")
    @ResponseBody
    public String RczfNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回success
        String clazz_name = "RczfNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("successAmt");//实际支付金额
        if (org.apache.commons.lang3.StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = request.getParameter("orderNo");// 商户订单号
        String trade_no = request.getParameter("payOrderNo");// 平台订单号
        String trade_status = request.getParameter("orderStatus");// 处理结果
        String t_trade_status = "Success";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_RCZF)) {
                RCZFPayServiceImpl payService = new RCZFPayServiceImpl(pmapsconfig);
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
     * 明捷付
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("明捷支付回调")
    @RequestMapping("/MJFNotify.do")
    @ResponseBody
    public String MJFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";// 成功返回success
        String clazz_name = "MJFNotify";
        String data = request.getParameter("data");
        String order_no = request.getParameter("orderNum");
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START==========================={}", order_no);
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
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            /*byte[] bytes = MJFToolKit.decryptByPrivateKey(new BASE64Decoder().decodeBuffer(data),
                    pmapsconfig.get("PRIVATE_KEY"));// 获取用户密钥*/
            byte[] bytes = MJFToolKit.decryptByPrivateKey(Base64.getDecoder().decode(data),
                    pmapsconfig.get("PRIVATE_KEY"));// 获取用户密钥
            String resultData = new String(bytes, MJFToolKit.CHARSET);// 解密数据
            JSONObject jsonObj = JSONObject.fromObject(resultData);
            Map<String, String> infoMap = MJFToolKit.json2Map(jsonObj);
            //=================================获取回调基本参数结果--START===========================//
            String trade_no = order_no;
            String trade_status = infoMap.get("payStateCode");
            String t_trade_status = "00";// 表示成功状态
            String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
            //=================================获取回调基本参数结果--END===========================//
            // 保存文件记录
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_MJF)) {
                MJFPayServiceImpl payService = new MJFPayServiceImpl(pmapsconfig);
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
     * 金睿支付回调
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("金睿支付回调")
    @RequestMapping("/JRZFNotify.do")
    @ResponseBody
    public String JRZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回j
        String clazz_name = "JRZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        //获取订单号
        String order_no = infoMap.get("orderNo");//订单号
        //流水号，第三方支付订单号
        String trade_no = infoMap.get("tradeSeq");
        String trade_status = infoMap.get("payResult");
        String t_trade_status = "1";// 1 支付成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_JRZF)) {
                JRZFPayServiceImpl jrzf = new JRZFPayServiceImpl(pmapsconfig);
                String rmsg = jrzf.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    rechargeOrderVO.setDescription("支付回调验签失败");
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
     * 虎云支付回调
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("虎云支付回调")
    @RequestMapping("/HYZFNotify.do")
    @ResponseBody
    public String HYZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回
        String clazz_name = "HYZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        //获取订单号
        String order_no = infoMap.get("sdorderno");//订单号
        //流水号，第三方支付订单号
        String trade_no = infoMap.get("sdpayno");
        String trade_status = infoMap.get("status");
        String t_trade_status = "1";// 1:成功，其他失败
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_HYZF)) {
                HYZFPayServiceImpl hyzf = new HYZFPayServiceImpl(pmapsconfig);
                String rmsg = hyzf.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");
                    rechargeOrderVO.setDescription("支付回调验签失败");
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
     * 畅支付回调
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("畅支付回调")
    @RequestMapping("/CZFNotify.do")
    @ResponseBody
    public String CZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回success
        String clazz_name = "CZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        String order_amount = infoMap.get("mch_amt");//实际支付金额
        if (org.apache.commons.lang3.StringUtils.isBlank(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = request.getParameter("mch_order");
        String trade_no = request.getParameter("mch_order");
        String trade_status = request.getParameter("status");
        String t_trade_status = "2";// 表示成功状态
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount) / 1000);
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_CZF)) {
                CZFPayServiceImpl xxb = new CZFPayServiceImpl(pmapsconfig);
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
     * 佰富回调
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("佰富支付回调")
    @RequestMapping("/BFNotify.do")
    @ResponseBody
    public String BFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "000000";
        String clazz_name = "BFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        String data = request.getParameter("paramData");
        JSONObject jsonObj = JSONObject.fromObject(data);
        String order_no = jsonObj.getString("orderNum");// 平台订单号
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            Map<String, String> metaSignMaP = new TreeMap<String, String>();
            metaSignMaP.put("merchantNo", jsonObj.getString("merchantNo"));
            metaSignMaP.put("netwayCode", jsonObj.getString("netwayCode"));
            metaSignMaP.put("orderNum", jsonObj.getString("orderNum"));
            metaSignMaP.put("payAmount", jsonObj.getString("payAmount"));
            metaSignMaP.put("goodsName", jsonObj.getString("goodsName"));
            metaSignMaP.put("resultCode", jsonObj.getString("resultCode"));// 支付状态
            metaSignMaP.put("payDate", jsonObj.getString("payDate"));

            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            savePayFile(clazz_name, metaSignMaP, IPTools.getIp(request));
            String trade_no = metaSignMaP.get("orderNum");// 平台订单号
            String trade_status = metaSignMaP.get("resultCode");// 处理结果
            String t_trade_status = "00";// 表示成功状态
            String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
            //=================================获取回调基本参数结果--END===========================//
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
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(metaSignMaP).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");

            //验证白名单
            String notifyIp = null;
            if (pmapsconfig.containsKey("notifyIp")) {
                notifyIp = pmapsconfig.get("notifyIp");
            }
            //验证白名单
            if (!ip.equals(notifyIp)) {
                logger.info(clazz_name + "支付回调失败,回调订单号:【" + order_no + "】发起回调请求IP地址为非法IP:【" + ip + "】");
                return ret__success;
            }

            if (paymentName.equals(PayConstant.CONSTANT_BF)) {
                String jsonStr = JSONObject.fromObject(metaSignMaP).toString();
                JSONObject merchant = getPublicKey(jsonObj.getString("merchantNo"));
                logger.info("加密前字符串:" + jsonStr + merchant.getString("key"));
                String sign = ToolKit.MD5(jsonStr.toString() + merchant.getString("key"), "UTF-8");
                logger.info("本地sign:" + sign + "     服务器sign:" + jsonObj.getString("sign"));
                if (!sign.equals(jsonObj.getString("sign"))) {
                    logger.info(clazz_name + "支付回调验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "签名校验成功");
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
     * 仁信支付回调
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("仁信支付回调")
    @RequestMapping("/RXNotify.do")
    @ResponseBody
    public String RXNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "ok";// 成功返回success
        String clazz_name = "RXNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = request.getParameter("ordernumber");
        String trade_no = request.getParameter("sysnumber");
        String trade_status = request.getParameter("orderstatus");
        String t_trade_status = "1";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_RX)) {
                RXPayServiceImpl xxb = new RXPayServiceImpl(pmapsconfig);
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
     * @Title: WTNotify @Description: 万通支付回调 @param: @param request @param: @param response @param: @return @return:
     * String @throws
     */
    @LogApi("万通支付回调")
    @RequestMapping("/WTNotify.do")
    @ResponseBody
    public String WTNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";// 成功返回success
        String clazz_name = "WTNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid") + "";// 商户订单号
        /** 升级之前流水号为【out_order_id】 */
        // String trade_no = jsonObject.getString("out_order_id");// 平台订单号
        /** 升级之后流水号字段改为【transid】 */
        String trade_no = infoMap.get("transid") + "";// 平台订单号
        String trade_status = infoMap.get("status") + "";// 订单支付状态 1 未支付 2 已支付
        String t_trade_status = "2";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_WT)) {
                WTPayServiceImpl xxb = new WTPayServiceImpl(pmapsconfig);
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
     * 新易付支付
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("新易付支付回调")
    @RequestMapping("/XYFNotify.do")
    @ResponseBody
    public String XYFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK"; //成功返回success
        String clazz_name = "XYFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = new HashMap<String, String>();
        //解析异步回调参数
        try {
            String reqParams = request.getParameter("linktext");
            if (StringUtils.isEmpty(reqParams)) {
                logger.error(clazz_name + "支付回调参数不能为空!");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调请求参数:" + reqParams);
            //对新易付传过来的参数进行解密
            String params = new String(Base64.getDecoder().decode(reqParams), "UTF-8");
            logger.info(clazz_name + "支付回调解密之后参数:" + params);
            //分割参数字符创
            String[] paramArr = params.split("&");
            if (paramArr.length > 0) {
                for (int i = 0; i < paramArr.length; i++) {
                    String key = paramArr[i].split("=")[0].toString();
                    String val = paramArr[i].split("=")[1].toString();
                    logger.info("key=" + key + ": val=" + val);
                    infoMap.put(key, val);
                }
            }
            if (infoMap.isEmpty()) {
                logger.info(clazz_name + "支付回调参数失败!");
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调解析之后的请求参数:" + JSONObject.fromObject(infoMap).toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name + "支付回调解析请求参数异常:" + e.getMessage());
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("paymoney");//实际支付金额  单位：元
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("usernumber");//订单号
        //流水号，第三方支付订单号
        String trade_no = "XYF" + System.currentTimeMillis();
        String trade_status = infoMap.get("orderstatus");
        String t_trade_status = "1";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount));
            rechargeOrderVO.setNotifyIp(ip);
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
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && org.apache.commons.lang3.StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }

            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XYF)) {
                XYFPayServiceImpl xxb = new XYFPayServiceImpl(pmapsconfig);
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
     * @Description 商入宝支付回调
     */
    @LogApi("商入宝支付回调")
    @RequestMapping("/SRBNotify.do")
    @ResponseBody
    public String SRBNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "200";// 成功返回200
        String clazz_name = "SRBNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("paysapi_id");// 平台订单号
        String trade_status = "success";// 回调没有支付状态，能回调就是支付过的
        String t_trade_status = "success";// 表示成功状态
        String order_amount = infoMap.get("realprice");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_SRB)) {
                SRBPayServiceImpl xxb = new SRBPayServiceImpl(pmapsconfig);
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
     * @Description 宝来支付回调
     */
    @LogApi("宝来支付回调")
    @RequestMapping("/BLNotify.do")
    @ResponseBody
    public String BLNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回success
        String clazz_name = "BLNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("out_trade_no");// 商户订单号
        String trade_no = infoMap.get("plat_trade_no");// 宝来订单号
        String trade_status = "SUCCESS";
        String t_trade_status = "SUCCESS";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_BL)) {
                BLPayServiceImpl xxb = new BLPayServiceImpl(pmapsconfig);
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
     * @Description 阿里宝盒支付回调通知
     */
    @LogApi("阿里宝盒支付回调")
    @RequestMapping("/ABHNotify.do")
    @ResponseBody
    public String ABHNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "FS";// 成功返回success
        String clazz_name = "ABHNotify";
        logger.info(clazz_name + "支付回调开始----------------------------START------------------------------");
        //获取回调请求参数
        String reqData = request.getParameter("data");
        if (StringUtils.isEmpty(reqData)) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调获取请求参数密文:" + reqData);
        //解密回调请求参数
        try {
            reqData = DESUtils.decryp(URLDecoder.decode(reqData, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(clazz_name + "支付回调解密回调请求参数异常:" + e.getMessage());
            return ret_str_failed;
        }
        if (StringUtils.isEmpty(reqData)) {
            logger.error(clazz_name + "支付回到解密异常!");
            return ret_str_failed;
        }
        Map<String, String> infoMap = new HashMap<>();
        //解析回调参数
        JSONObject jsonData = JSONObject.fromObject(reqData);
        //重组回调请求参数
        if (jsonData != null && !jsonData.isEmpty()) {
            Iterator<String> iterator = jsonData.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = jsonData.getString(key);
                infoMap.put(key, val);
            }
        }
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderId");// 平台订单号
        String trade_no = infoMap.get("flowId");// 平台订单号
        String trade_status = infoMap.get("transCode");//00表示成功FF失败其他处理中
        String t_trade_status = "00";// 表示成功状态
        String order_amount = infoMap.get("transAmt");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double money = Double.valueOf(order_amount) / 100;//回调，以 分为单位,需要除以 100
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            rechargeOrderVO.setOrderAmount(money);
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_ABH)) {
                ABHPayServiceImpl xxb = new ABHPayServiceImpl(pmapsconfig);
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
     * @Description 易发支付回调通知
     */
    @LogApi("易发支付回调")
    @RequestMapping("/YIFANotify.do")
    @ResponseBody
    public String YIFANotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回success
        String clazz_name = "YIFANotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("shop_no");// 平台订单号
        String trade_no = infoMap.get("trade_no");// 平台订单号
        String trade_status = infoMap.get("status");//0:支付成功，其他支付失败
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YIFA)) {
                YIFAPayServiceImpl xxb = new YIFAPayServiceImpl(pmapsconfig);
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
     * @Description 财富支付回调通知
     */
    @LogApi("财富支付回调")
    @RequestMapping("/CFZFNotify.do")
    @ResponseBody
    public String CFZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回success
        String clazz_name = "CFZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("trade_out_no");// 平台订单号
        String trade_no = infoMap.get("pay_sn");// 平台订单号
        String trade_status = infoMap.get("error");//0 为成功 -1为付款失败
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_CFZF)) {
                CFZFPayServiceImpl xxb = new CFZFPayServiceImpl(pmapsconfig);
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
     * @Description 易智支付回调通知
     */
    @LogApi("易智支付回调")
    @RequestMapping("/YIZHINotify.do")
    @ResponseBody
    public String YIZHINotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "ok";// 成功返回ok
        String clazz_name = "YIZHINotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("ordernumber");// 平台订单号
        String trade_no = infoMap.get("sysnumber");//流水号
        String trade_status = infoMap.get("orderstatus");//订单状态，1:支付成功，非1为支付失败
        String t_trade_status = "1";//1:支付成功，非1为支付失败
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YIZHI)) {
                YIZHIPayServiceImpl xxb = new YIZHIPayServiceImpl(pmapsconfig);
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
     * @Description 信誉支付回调通知
     */
    @LogApi("信誉支付回调")
    @RequestMapping(value = "/XYZNotify.do", produces = {"application/xml;charset=UTF-8"})
    @ResponseBody
    public String XYZNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";// 成功返回success
        String clazz_name = "XYZNotify信誉支付回调";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = new HashMap<String, String>();
        try {
            String str = HttpUtil.getRequestBody(request);

            logger.info("回调函数获取的参数: " + str);
            infoMap = XMLUtils.formatXMlToMap(str);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[XYZNotify]信誉支付回调获取请求参数异常:{}", e.getMessage());
        }

        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "ss");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("out_trade_no");//商户订单号
        String trade_no = infoMap.get("transaction_id");//平台订单号
        String trade_status = infoMap.get("pay_result");// 0 表示成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================XYZNotifty START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XYZ)) {
                XYZPayServiceImpl xxb = new XYZPayServiceImpl(pmapsconfig);
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
     * 风携支付 FX
     */
    @LogApi("风携支付回调")
    @RequestMapping("/FXNotify.do")
    @ResponseBody
    public String FXNotify(HttpServletRequest request, HttpServletResponse response) {
        String ret_success = "success";//需要小写
        logger.info("[风携支付] 回调开始 ============================START========================");
        String clazz_name = "[FXNotify]风携支付回调";
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        logger.info("[风携支付] 回调返回参数:{}", infoMap);
        if (infoMap == null || infoMap.isEmpty()) {
            return ret_str_failed;
        }
        String order_no = infoMap.get("fx_order_id");//商户订单号
        String trade_no = System.currentTimeMillis() + "";//流水号
        String orderAmount = infoMap.get("fx_order_amount");//需要修改的订单金额
        logger.info("原金额金额为:{}, 修改金额为:{}", infoMap.get("fx_original_amount"), infoMap.get("fx_order_amount"));
        String trade_status = infoMap.get("fx_status_code");// 200 表示成功
        String t_trade_status = "200";//0000 成功 false 失败
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret_success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(Double.valueOf(orderAmount));//修改后的金额
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_FX)) {//风携支付
                FXPayServiceImpl xxb = new FXPayServiceImpl(pmapsconfig);
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
                return ret_success;
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
     * 汇鑫支付 回调函数
     *
     * @param request
     * @param response
     */
    @LogApi("汇鑫支付回调")
    @RequestMapping("HXNotify")
    @ResponseBody
    public String HXNotify(HttpServletRequest request, HttpServletResponse response) {
        String ret_success = "ok";//成功返回 小写 ok
        logger.info("汇鑫支付回调支付开始回调函数................................");
        String clazz_name = "汇鑫支付回调";
        Map<String, String> infoMap = new HashMap<String, String>();
        try {
            infoMap = ParamsUtils.getNotifyParams(request);//解析参数
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HXNotity]汇鑫支付接受参数出现错误:{}", e.getMessage());
        }

        if (null == infoMap || infoMap.isEmpty()) {
            logger.info("[HXNotity]汇鑫支付 无接收参数");
            return ret_str_failed;
        }

        logger.info("[HXNotity]汇鑫支付回调请求参数:{}", infoMap);

        String order_no = infoMap.get("out_trade_no");//订单号
        String trade_no = infoMap.get("order_id");
        String trade_status = infoMap.get("status");//状态
        String t_trade_status = "1";//1:支付成功，非1为支付失败
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        try {
            savePayFile("HXNotity", infoMap, IPTools.getIp(request));//写日志
            //查询订单号
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret_success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_HX)) {//汇鑫支付
                HXPayServiceImpl xxb = new HXPayServiceImpl(pmapsconfig);
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
                return ret_success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[HXNotity]汇鑫支付回调 出现错误,错误信息:{}", e.getMessage());
            return ret_str_failed;
        }
    }

    /**
     * 安盛支付
     */
    @LogApi("安盛支付回调")
    @RequestMapping("/ASNotify.do")
    @ResponseBody
    public String ASNotify(HttpServletRequest request, HttpServletResponse response) {
        logger.info("[ASNotify]安盛支付 回调函数 开始 ---------------------------------");
        String clazz_name = "安盛支付回调";
        String ret_success = "success";
        Map<String, String> infoMap = new HashMap<>();
        try {
            infoMap = ParamsUtils.getNotifyParams(request);//获取参数
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("安盛支付出现问题:{}", e.getMessage());
            return ret_str_failed;
        }

        if (null == infoMap || infoMap.isEmpty()) {
            logger.info("[ASNotify]安盛支付 无接收参数");
            return ret_str_failed;
        }

        savePayFile("HXNotity", infoMap, IPTools.getIp(request));//写日志
        if (!infoMap.get("status").equals("1")) {
            logger.info("[ASNotify]安盛支付回调单据异常获取支付商信息失败！");
            return ret_str_failed;
        }

        logger.info("[ASNotify]安盛支付回调请求参数:{}", infoMap);
        String order_no = infoMap.get("order_id");//平台订单号
        String trade_no = StringUtils.isEmpty(infoMap.get("paysapi_id")) ? System.currentTimeMillis() + "" : infoMap.get("paysapi_id");// 平台订单号
        String trade_status = infoMap.get("code");//订单状态判断标准：0 未处理 1 交易成功 2 支付失败 3 关闭交易 4 支付超时
        String t_trade_status = "1";
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        logger.info("[ASNotify]安盛支付回调开始调用------------");

        try {
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret_success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_AS)) {//安盛支付
                ASPayServiceImpl xxb = new ASPayServiceImpl(pmapsconfig);
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
                return ret_success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(clazz_name + "出现错误!");
            return ret_str_failed;
        }
    }

    /**
     * 鼎盛支付
     *
     * @param request
     * @param response
     * @return
     */
    @LogApi("鼎盛支付回调")
    @RequestMapping("/DSZFNotify.do")
    @ResponseBody
    public String DSZFNotiy(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        logger.info("DSZFNotify鼎盛支付回调开始.........");
        String ret_success = "success";
        String clazz_name = "DSNotify鼎盛支付";

        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        logger.info("DSZFNotify鼎盛支付回调参数:{}", infoMap);

        if (infoMap.isEmpty()) {
            logger.info("{}DSZFNotify 鼎盛支付 回调参数为空!", clazz_name);
            return ret_str_failed;
        }

        String order_no = infoMap.get("fxddh");//订单号
        String trade_no = infoMap.get("fxorder");//平台订单号
        String trade_status = infoMap.get("fxstatus");//状态
        String t_trade_status = "1";//1:支付成功
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");

        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret_success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_DSZF)) {//鼎盛支付
                DSZFPayServiceImpl xxb = new DSZFPayServiceImpl(pmapsconfig);
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
                return ret_success;
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
     * 新2万通支付
     */
    @LogApi("新万通2支付回调")
    @RequestMapping("/XWTNotify.do")
    @ResponseBody
    public String XWTNotify(HttpServletRequest request, HttpServletResponse response) {
        String ret_success = "OK";/// 成功标识
        String clazz_name = "XWTNotify";
        logger.info("{}新2万通支付回调函数开始回调了...............................", clazz_name);
        TreeMap<String, String> mapInfo = new TreeMap<String, String>();
        mapInfo.putAll(ParamsUtils.getNotifyParams(request));
        logger.info("新2万通支付回调参数:{}", mapInfo);

        if (mapInfo.isEmpty()) {
            return ret_str_failed;
        }

        String order_no = mapInfo.get("orderid");// 订单金额
        String trade_no = mapInfo.get("transaction_id");// 平台订单号
        String trade_status = mapInfo.get("returncode");// 状态
        String t_trade_status = "00";// 1:支付成功
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");

        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            savePayFile(clazz_name, mapInfo, IPTools.getIp(request));
            // 通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret_success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(mapInfo).toString());
            Integer payId = rechargeOrderVO.getPayId();// 支付商ID
            // 查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();// 支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());// 支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XWT)) {// 新2万通
                XWTPayServiceImpl xxb = new XWTPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(mapInfo);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!订单号:{}", order_no);

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!订单号:{}", order_no);
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求,订单号:{}", order_no);
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================:{}", order_no);
                return ret_success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================:{}", order_no);
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
     * 保诚支付回调方法
     *
     * @param request
     * @param response
     * @return
     */
    @LogApi("保诚支付回调")
    @RequestMapping("/BCZFNotify.do")
    @ResponseBody
    public String BCZFNotify(HttpServletRequest request, HttpServletResponse response) {
        String clazz_name = "BCZFNotify";
        logger.info("{}保诚支付回调函数开始................", clazz_name);
        String ret_success = "OK";
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap.isEmpty()) {
            logger.info("{}保诚支付回调函数参数为空!", clazz_name);
            return ret_str_failed;
        }

        logger.info("{}保诚支付回调函数回调参数值:{}", clazz_name, infoMap);
        /*
         * 订单编号
         */
        String order_no = infoMap.get("orderid");
        /*
         * 系统编号
         */
        String trade_no = infoMap.get("transaction_id");
        //成功状态
        String trade_status = infoMap.get("returncode");
        String t_trade_status = "00";
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (payMap.containsKey(order_no)) {
            logger.info("{}保诚支付回调订单号:{}重复调用", clazz_name, order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");

        try {
            logger.info("{}执行回调业务开始=========================START==========================={}", clazz_name, order_no);
            savePayFile(clazz_name, infoMap, ip);
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info("{}支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", clazz_name, order_no);
                return ret_success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();// 支付商ID
            // 查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();// 支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());// 支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_BCZF)) {//保诚支付
                BCZFPayServiceImpl xxb = new BCZFPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!订单号:{}", order_no);

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name + "支付回调验签成功!订单号:{}", order_no);
            } else {
                // 异常请求
                logger.error(clazz_name + "支付回调异常请求,订单号:{}", order_no);
                return ret_str_failed;
            }
            logger.info(clazz_name + "支付回调验签结束=======================END====================");

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info(clazz_name + "支付回调业务处理成功=======================SUCCESS====================:{}", order_no);
                return ret_success;
            }
            logger.info(clazz_name + "支付回调业务处理成功=======================FAILD====================:{}", order_no);
            return ret_str_failed;

        } catch (Exception e) {
            logger.info("{}保诚支付出现错误!!错误信息:{}", clazz_name, e.getMessage());
            e.printStackTrace();
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name + "支付回调业务处理成功,删除缓存中的订单KEY:{}", order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * 富豪支付回调函数
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @LogApi("富豪支付回调")
    @RequestMapping("/FHZFNotify.do")
    @ResponseBody
    public String FHZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret_success = "success";
        String clazz_name = "FHZFNotify富豪支付";
        logger.info("{}回调函数 开始回调...............................", clazz_name);
        Map<String, String> infoMap = new TreeMap<String, String>();
        infoMap.putAll(ParamsUtils.getNotifyParams(request));

        logger.info("{}回调函数的参数值:{}", clazz_name, infoMap);
        if (infoMap.isEmpty()) {
            logger.info("{}回调函数参数值为空", infoMap);
            return ret_str_failed;
        }
        /**
         * 订单号
         */
        String order_no = infoMap.get("out_trade_no");
        String trade_no = infoMap.get("trade_id");
        String trade_status = infoMap.get("status");
        String t_trade_status = "1";//1:支付成功，非1为支付失败
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        logger.info("获取基本参数结束...................");
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");

        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret_success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_FHZF)) {//富豪支付
                FHZFPayServiceImpl xxb = new FHZFPayServiceImpl(pmapsconfig);
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
                return ret_success;
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
     * iipays 支付
     *
     * @param request
     * @param response
     * @return
     */
    @LogApi("iipays支付回调")
    @RequestMapping("/IIZFNotify.do")
    @ResponseBody
    public String IIZFNotify(HttpServletRequest request, HttpServletResponse response) {
        String ret_success = "success";
        String clazz_name = "IIZF iipays支付回调开始";
        logger.info("{}............开始回调..............", clazz_name);
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        logger.info("{} 获取的参数:{}", clazz_name, infoMap);
        if (infoMap.isEmpty()) {
            return ret_str_failed;
        }

        String order_no = infoMap.get("sdorderno");
        String trade_no = infoMap.get("sdpayno");
        String trade_status = infoMap.get("status");
        String t_trade_status = "1";//1:支付成功，非1为支付失败
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret_success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_IIZF)) {//iipays 支付
                IIZFPayServiceImpl xxb = new IIZFPayServiceImpl(pmapsconfig);
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
                return ret_success;
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
     * @Description 乐美支付
     */
    @LogApi("乐美支付回调")
    @RequestMapping("/LMZFNotify.do")
    @ResponseBody
    public String LMZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";//返回字符串OK，就表示回调已收到。
        String clazz_name = "LMZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("OrderID");// 平台订单号
        String trade_no = infoMap.get("OrderIDP");//流水号
        String trade_status = infoMap.get("PayState");//1为充值成功 0为失败
        String t_trade_status = "1";//1为充值成功 0为失败
        String order_amount = infoMap.get("FaceValue");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_LMZF)) {
                LMZFPayServiceImpl xxb = new LMZFPayServiceImpl(pmapsconfig);
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
     * @Description 万福支付回调通知
     */
    @LogApi("万福支付回调")
    @RequestMapping("/WANFNotify.do")
    @ResponseBody
    public String WANFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "WANFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("order_no");// 平台订单号
        String trade_no = infoMap.get("order_id");//流水号
        String trade_status = "1";//infoMap.get("status");//1:成功，其他失败
        String t_trade_status = "1";//1:成功，其他失败
        String order_amount = infoMap.get("realprice");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_WANF)) {
                WANFPayServiceImpl xxb = new WANFPayServiceImpl(pmapsconfig);
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
     * @Description 顺优支付回调通知
     */
    @LogApi("顺优支付回调")
    @RequestMapping("/SYOUNotify.do")
    @ResponseBody
    public String SYOUNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "SYOUNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("merOdNo");// 平台订单号
        String trade_no = infoMap.get("orderNo");//流水号
        String trade_status = infoMap.get("tradeResult");//1:成功，其他失败
        String t_trade_status = "1";//1:成功，其他失败
        String order_amount = infoMap.get("amount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_SYOU)) {
                SYOUPayServiceImpl xxb = new SYOUPayServiceImpl(pmapsconfig);
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
     * @Description 火火支付回调通知
     */
    @LogApi("火火支付回调")
    @RequestMapping("/FIRENotify.do")
    @ResponseBody
    public String FIRENotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";//收到通知后请回复  OK
        String clazz_name = "FIRENotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("platform_trade_no");//流水号
        String trade_status = infoMap.get("result_code");//SUCCESS或FAIL，注意大写
        String t_trade_status = "SUCCESS";//1:成功，其他失败
        String order_amount = infoMap.get("realprice");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_FIRE)) {
                FIREPayServiceImpl xxb = new FIREPayServiceImpl(pmapsconfig);
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
     * @Description 溢发支付2回调通知
     */
    @LogApi("溢发支付回调")
    @RequestMapping("/YFFSNotify.do")
    @ResponseBody
    public String YFFSNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";// 成功返回success
        String clazz_name = "YFFSNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = new HashMap<String, String>();
        try {
            for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                if (entry.getValue() != null && entry.getValue().length > 0) {
                    infoMap = JSONUtils.toHashMap(entry.getKey());
                }
            }
            logger.info(clazz_name + "支付回调请求参数:{}" + infoMap.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("解析json参数异常");
            return ret__success;
        }

        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderNo");// 平台订单号
        String trade_no = infoMap.get("orderNo");// 支付商订单号
        String trade_status = infoMap.get("resultStatus");// 处理结果
        String t_trade_status = "SUCCESS";// 表示成功状态
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YFFS)) {
                YFFSPayServiceImpl xxb = new YFFSPayServiceImpl(pmapsconfig);
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
     * @Description BP支付回调通知
     */
    @LogApi("BP支付回调")
    @RequestMapping("/BPZFNotify.do")
    @ResponseBody
    public String BPZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "BPZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = StringUtils.isEmpty(infoMap.get("out_transaction_id")) ? String.valueOf(System.currentTimeMillis()) : infoMap.get("out_transaction_id");//流水号
        String trade_status = infoMap.get("result");//0 成功
        String t_trade_status = "0";//1:成功，其他失败
        String order_amount = infoMap.get("real_fee");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount) / 100);//实际支付金额
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_BPZF)) {
                BPZFPayServiceImpl xxb = new BPZFPayServiceImpl(pmapsconfig);
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
     * @Description 闪付
     */
    @LogApi("闪付支付回调")
    @RequestMapping("/SHANNotify.do")
    @ResponseBody
    public String SHANNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "SHANNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = StringUtils.isEmpty(infoMap.get("trade_no")) ? String.valueOf(System.currentTimeMillis()) : infoMap.get("trade_no");//流水号
        String trade_status = "0000";//0 成功
        String t_trade_status = "0000";//1:成功，其他失败
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            String type = getPayConfigType(String.valueOf(rechargeOrderVO.getPayType()));//获取支付类型
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            //验证回调IP
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && org.apache.commons.lang.StringUtils.isNotBlank(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_SHAN)) {
                SHANPayServiceImpl xxb = new SHANPayServiceImpl(pmapsconfig, type);
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
     * @Description 通支付回调通知
     */
    @LogApi("通支付回调")
    @RequestMapping("/TONGNotify.do")
    @ResponseBody
    public String TONGNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  SUCCESS
        String clazz_name = "TONGNotify";
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");

        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = infoMap.get("trade_no");//流水号
        String trade_status = infoMap.get("status");//支付状态：'1'为支付成功，'error:错误信息'为未支付成功
        String t_trade_status = "1";//1:成功，其他失败
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_TONG)) {
                TONGPayServiceImpl xxb = new TONGPayServiceImpl(pmapsconfig);
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
     * @Description 信付通回调通知
     */
    @LogApi("信付通支付回调")
    @RequestMapping("/XFTNotify.do")
    @ResponseBody
    public String XFTNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  SUCCESS
        String clazz_name = "XFTNotify";
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");

        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("order_no");// 平台订单号
        String trade_no = infoMap.get("trade_no");//流水号
        String trade_status = infoMap.get("trade_status");//支付状态：'1'为支付成功，'error:错误信息'为未支付成功
        String t_trade_status = "TRADE_FINISHED";//成功状态：TRADE_FINISHED
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XFT)) {
                XFTPayServiceImpl xft = new XFTPayServiceImpl(pmapsconfig);
                String rmsg = xft.callback(infoMap);
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
     * @Description 易付宝支付回调通知
     */
    @LogApi("易付宝支付回调")
    @RequestMapping("/YIFBNotify.do")
    @ResponseBody
    public String YIFBNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";//收到通知后请回复  OK
        String clazz_name = "YIFBNotify";
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");

        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");//流水号
        String trade_status = infoMap.get("returncode");//支付状态：'1'为支付成功，'error:错误信息'为未支付成功
        String t_trade_status = "00";//“00” 为成功
        String order_amount = infoMap.get("amount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && StringUtils.isEmpty(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }

            logger.info(clazz_name + "支付回调验签开始=======================START====================");

            if (paymentName.equals(PayConstant.CONSTANT_YIFB)) {
                YIFBPayServiceImpl yifb = new YIFBPayServiceImpl(pmapsconfig);
                String rmsg = yifb.callback(infoMap);
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
     * @Description 特捷付支付
     */
    @LogApi("特捷支付回调")
    @RequestMapping("/TEJFNotify.do")
    @ResponseBody
    public String TEJFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "TEJFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("merOdNo");// 平台订单号
        String trade_no = infoMap.get("orderNo");//流水号
        String trade_status = infoMap.get("tradeResult");//1:成功，其他失败
        String t_trade_status = "1";//1:成功，其他失败
        String order_amount = infoMap.get("amount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_TEJF)) {
                TEJFPayServiceImpl xxb = new TEJFPayServiceImpl(pmapsconfig);
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

    @LogApi("天眼支付回调")
    @RequestMapping("/TEYENotify.do")
    @ResponseBody
    public String TEYENotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "TEYENotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("ordernumber");// 平台订单号
        String trade_no = StringUtils.isEmpty(infoMap.get("sysnumber")) ? "TEYE" + System.currentTimeMillis() : infoMap.get("sysnumber");//流水号
        String trade_status = infoMap.get("orderstatus");//1:成功，其他失败
        String t_trade_status = "1";//1:成功，其他失败
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_TEYE)) {
                TEYEPayServiceImpl xxb = new TEYEPayServiceImpl(pmapsconfig);
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
     * @Description 顺付支付回调通知
     */
    @LogApi("顺付支付回调")
    @RequestMapping("/SHUNNotify.do")
    @ResponseBody
    public String SHUNNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "SHUNNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("userRemark");// 平台订单号
        String trade_no = StringUtils.isEmpty(infoMap.get("depositNumber")) ? "shunf" + System.currentTimeMillis() : infoMap.get("depositNumber");//流水号
        String trade_status = "0000";//0000:成功，其他失败
        String t_trade_status = "0000";//1:成功，其他失败
        String order_amount = infoMap.get("amount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_SHUN)) {
                SHUNPayServiceImpl xxb = new SHUNPayServiceImpl(pmapsconfig);
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
     * @Description 华信支付回调通知
     */
    @LogApi("华信支付回调")
    @RequestMapping("/HUAXNotify.do")
    @ResponseBody
    public String HUAXNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";//收到通知后请回复  success
        String clazz_name = "HUAXNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("outOrderNo");// 平台订单号
        String trade_no = infoMap.get("orderNo");//流水号
        String trade_status = "0000";//0000:成功，其他失败
        String t_trade_status = "0000";//1:成功，其他失败
        String order_amount = infoMap.get("amount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && !StringUtils.isEmpty(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_HUAX)) {
                HUAXPayServiceImpl xxb = new HUAXPayServiceImpl(pmapsconfig);
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
     * @Description 盈付支付回调通知
     */
    @LogApi("盈付支付回调")
    @RequestMapping("/YINFNotify.do")
    @ResponseBody
    public String YINFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";//收到通知后请回复  success
        String clazz_name = "YINFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getYINFNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = infoMap.get("transaction_id");//流水号
        String trade_status = infoMap.get("result_code");//0 表示成功非 0 表示失败
        String t_trade_status = "0";//0 表示成功非 0 表示失败
        String order_amount = infoMap.get("total_fee");//
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YINF)) {
                YINFPayServiceImpl xxb = new YINFPayServiceImpl(pmapsconfig);
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
     * @Description 龙宝支付回调通知
     */
    @LogApi("龙宝支付回调")
    @RequestMapping("/LBAONotify.do")
    @ResponseBody
    public String LBAONotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "LBAONotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
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
            logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_LBAO)) {
                LBAOPayServiceImpl xxb = new LBAOPayServiceImpl(pmapsconfig);
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
     * @Description 如意支付回调通知
     */
    @LogApi("如意支付回调")
    @RequestMapping("/RUYINotify.do")
    @ResponseBody
    public String RUYINotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "stopnotify";//收到通知后请回复  success
        String clazz_name = "RUYINotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("merOrdId");// 平台订单号
        String trade_no = infoMap.get("sysOrdId");//流水号
        String trade_status = infoMap.get("tradeStatus");//交易状态，success002 表示成功
        String t_trade_status = "success002";//交易状态，success002 表示成功
        String order_amount = infoMap.get("merOrdAmt");//单位为分
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_RUYI)) {
                RUYIPayServiceImpl xxb = new RUYIPayServiceImpl(pmapsconfig);
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
     * @Description YunSu支付回调通知
     */
    @LogApi("YunSu支付回调")
    @RequestMapping("/YUNSUNotify.do")
    @ResponseBody
    public String YUNSUNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "YUNSUNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderId");// 平台订单号
        String trade_no = infoMap.get("tradeId");//流水号
        String trade_status = infoMap.get("code");//状态：0 成功.
        String t_trade_status = "0";//交易状态，0 表示成功
        String order_amount = infoMap.get("money");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount) / 100);//单位为分
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YUNSU)) {
                YUNSUPayServiceImpl xxb = new YUNSUPayServiceImpl(pmapsconfig);
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
     * @Description 鑫财富支付回调通知
     */
    @LogApi("鑫财富支付回调")
    @RequestMapping("/XCFPNotify.do")
    @ResponseBody
    public String XCFPNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";//收到通知后请回复  success
        String clazz_name = "XCFPNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");//流水号
        String trade_status = infoMap.get("returncode");//“00” 为成功
        String t_trade_status = "00";//“00” 为成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XCFP)) {
                XCFPPayServiceImpl xxb = new XCFPPayServiceImpl(pmapsconfig);
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
     * @Description 竣付通支付回调通知
     */
    @LogApi("竣付通支付回调")
    @RequestMapping("/JFTPNotify.do")
    @ResponseBody
    public String JFTPNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "JFTPNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getJFTPNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("p2_ordernumber");// 平台订单号
        String trade_no = infoMap.get("p5_orderid");//流水号
        String trade_status = infoMap.get("p4_zfstate");//支付返回结果 1 代表成功，其他为失败
        String t_trade_status = "1";//支付返回结果 1 代表成功，其他为失败
        String order_amount = infoMap.get("p13_zfmoney");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_JFTP)) {
                JFTPPayServiceImpl xxb = new JFTPPayServiceImpl(pmapsconfig);
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
     * @Description GPAY支付回调通知
     */
    @LogApi("GPAY支付回调")
    @RequestMapping("/GPAYNotify.do")
    @ResponseBody
    public String GPAYNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "GPAYNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getGPAYNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("bizCode");// 平台订单号
        String trade_no = infoMap.get("orderId");//流水号
        String trade_status = infoMap.get("status");//支付返回结果 END
        String t_trade_status = "END";//支付返回结果 END 代表成功，其他为失败
        String order_amount = infoMap.get("actualMoney");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_GPAY)) {
                GPAYPayServiceImpl xxb = new GPAYPayServiceImpl(pmapsconfig);
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
     * @Description 即达支付回调通知
     */
    @LogApi("即达支付回调")
    @RequestMapping("/JIDANotify.do")
    @ResponseBody
    public String JIDANotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  OK
        String clazz_name = "JIDANotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("sdorderno");// 平台订单号
        String trade_no = infoMap.get("sdpayno");//流水号
        String trade_status = infoMap.get("status");//状态：0|1 支付失败、2支付成功。未支付不会对异步通知做任何推送
        String t_trade_status = "1";//状态：0|1 支付失败、2支付成功。未支付不会对异步通知做任何推送
        String order_amount = infoMap.get("total_fee");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_JIDA)) {
                JIDAPayServiceImpl xxb = new JIDAPayServiceImpl(pmapsconfig);
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
            logger.info(clazz_name + "支付回调业务处理失败=======================FAILD====================");
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
     * @Description 云端支付回调通知
     */
    @LogApi("云端支付回调")
    @RequestMapping("/YDNotify.do")
    @ResponseBody
    public String YDNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  SUCCESS
        String clazz_name = "YDNotify";
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");

        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("amount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");//流水号
        String trade_status = infoMap.get("returncode");//支付状态：'1'为支付成功，'error:错误信息'为未支付成功
        String t_trade_status = "00";//成功状态：00
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YD)) {
                YDPayServiceImpl yd = new YDPayServiceImpl(pmapsconfig);
                String rmsg = yd.callback(infoMap);
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
     * @Description 新汇支付回调通知
     */
    @LogApi("新汇支付回调")
    @RequestMapping("/XHFNotify.do")
    @ResponseBody
    public String XHFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  SUCCESS
        String clazz_name = "XHFNotify";
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");

        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("amount");//
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");//流水号
        String trade_status = infoMap.get("returncode");//支付状态：'1'为支付成功，'error:错误信息'为未支付成功
        String t_trade_status = "00";//成功状态：00
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XHF)) {
                XHFPayServiceImpl xhf = new XHFPayServiceImpl(pmapsconfig);
                String rmsg = xhf.callback(infoMap);
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
     * @Description WOW支付回调通知
     */
    @LogApi("WOW支付回调")
    @RequestMapping("/WOWNotify.do")
    @ResponseBody
    public String WOWNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";//收到通知后请回复  success
        String clazz_name = "WOWNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("amount");//
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("merchant_order_id");// 平台订单号
        String trade_no = "WOW" + System.currentTimeMillis();//流水号
        String trade_status = infoMap.get("success");//success:成功，其他失败
        String t_trade_status = "true";//true:成功，false失败
        if (!trade_status.equals(t_trade_status)) {
            return ret__success;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_WOW)) {
                WOWPayServiceImpl xxb = new WOWPayServiceImpl(pmapsconfig);
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
     * @Description 完美支付回调通知
     */
    @LogApi("完美支付回调")
    @RequestMapping("/WMPNotify.do")
    @ResponseBody
    public String WMPNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "WMPNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderNum");// 平台订单号
        String trade_no = "WMP" + System.currentTimeMillis();//流水号
        String trade_status = infoMap.get("state");//状态：success:成功，其他失败
        String t_trade_status = "success";//状态：success:成功，其他失败
        String order_amount = infoMap.get("amount");//单位为分
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_WMP)) {
                WMPPayServiceImpl xxb = new WMPPayServiceImpl(pmapsconfig);
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
     * 乐百支付回调通知
     */
    @LogApi("乐百支付回调")
    @RequestMapping("/LBZFNotify.do")
    @ResponseBody
    public String LBZFNotify(HttpServletRequest request) {
        logger.info("乐百支付LBZFNotify .........................");
        String ret_success = "ok";//返回回调成功
        String clazz_name = "乐百支付LBZFNotify";
        Map<String, String> infoMap = new HashMap<String, String>();
        try {
            infoMap = ParamsUtils.getNotifyParams(request);
            logger.info("{}获取参数完成", clazz_name);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{}出现错误...", clazz_name, e.getMessage());
        }
        logger.info("{} 参数:{}", clazz_name, infoMap);

        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info("{} 支付回调请求参数:{}", clazz_name, infoMap);

        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");
        String trade_status = infoMap.get("returncode");//00:成功，其他失败
        String t_trade_status = "00";//00:成功
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        if (payMap.containsKey(order_no)) {
            logger.info("{}支付回调订单号:{}重复调用", clazz_name, order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");

        try {
            logger.info("{}执行回调业务开始=========================START===========================", clazz_name);
            // 保存文件记录
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info("{}支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", clazz_name, order_no);
                return ret_success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info("{} 非法支付商ID,查询支付商信息失败,支付商ID:{}", clazz_name, payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info("支付回调验签开始=======================START====================", clazz_name);
            if (paymentName.equals(PayConstant.CONSTANT_LBZF)) {
                LBZFPayServiceImpl xxb = new LBZFPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name + "支付回调验签失败!");

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info("{}支付回调验签成功!", clazz_name);
            } else {
                // 异常请求
                logger.error("{}支付回调异常请求", clazz_name);
                return ret_str_failed;
            }
            logger.info("{}支付回调验签结束=======================END====================", clazz_name);

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info("{}支付回调业务处理成功=======================SUCCESS====================", clazz_name);
                return ret_success;
            }
            logger.info("{} 支付回调业务处理成功=======================FAILD====================", clazz_name);
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("{}支付回调业务处理异常:{}", clazz_name, e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info("支付回调业务处理成功,删除缓存中的订单KEY:{}", clazz_name, order_no);
                payMap.remove(order_no);
            }
        }

    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description APAY支付回调通知
     */
    @LogApi("APAY支付回调")
    @RequestMapping("/APAYNotify.do")
    @ResponseBody
    public String APAYNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";//收到通知后请回复  SUCCESS
        String clazz_name = "APAYNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("totalAmount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("merchantTradeNo");// 平台订单号
        String trade_no = infoMap.get("SystemTradeNo");//流水号
        String trade_status = infoMap.get("orderStatus");//0交易成功，1交易失败，2未交易
        String t_trade_status = "0";//0:成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_APAY)) {
                APAYPayServiceImpl xxb = new APAYPayServiceImpl(pmapsconfig);
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
     * @Description HY黄岩支付回调通知
     */
    @LogApi("黄岩支付回调")
    @RequestMapping("/HYNotify.do")
    @ResponseBody
    public String HYNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "HYNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("total_fee");//分为单位
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("order_id");// 平台订单号
        String trade_no = infoMap.get("out_transaction_id");//流水号
        String trade_status = infoMap.get("pay_result");//0:成功，其他失败
        String t_trade_status = "0";//0:成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            if (null != pmapsconfig && pmapsconfig.containsKey("notifyIp") && !StringUtils.isEmpty(pmapsconfig.get("notifyIp"))) {
                String notifyIp = pmapsconfig.get("notifyIp");
                if (!isContainIp(notifyIp, ip)) {
                    logger.error(clazz_name + "回调来源ip与支付商回调ip不匹配,来源ip:{}", ip);
                    rechargeOrderVO.setDescription("notify ip is not match");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
            }
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_HY)) {
                HYPayServiceImpl xxb = new HYPayServiceImpl(pmapsconfig);
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
     * @Description XYZF新艺支付回调通知
     */
    @LogApi("新艺支付回调")
    @RequestMapping("/XYZFNotify.do")
    @ResponseBody
    public String XYZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "XYZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("je");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("ddh");// 平台订单号
        String trade_no = "XYZF" + System.currentTimeMillis();//流水号
        String trade_status = infoMap.get("status");//success:成功，其他失败
        String t_trade_status = "success";//0:成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XYZF)) {
                XYZFPayServiceImpl xxb = new XYZFPayServiceImpl(pmapsconfig);
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
     * @Description HANY瀚银支付回调通知
     */
    @LogApi("瀚银支付回调")
    @RequestMapping("/HANYNotify.do")
    @ResponseBody
    public String HANYNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "HANYNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("orderAmount");//订单金额 单位：分
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("orderNo");// 平台订单号
        String trade_no = infoMap.get("transSeq");//流水号
        String trade_status = infoMap.get("statusCode");//00:成功，其他失败
        String t_trade_status = "00";//00:成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_HANY)) {
                //订单支付渠道
                String payType = String.valueOf(rechargeOrderVO.getPayType());
                if (PayConstant.CHANEL_ALI.equals(payType)) {
                    payType = PayConstant.CONSTANT_ALI;//支付宝
                } else if (PayConstant.CHANEL_WX.equals(payType)) {
                    payType = PayConstant.CONSTANT_WX;//微信
                } else if (PayConstant.CHANEL_YL.equals(payType)) {
                    payType = PayConstant.CONSTANT_YL;//银联支付
                } else if (PayConstant.CHANEL_CFT.equals(payType)) {
                    payType = PayConstant.CONSTANT_CFT;//财付通
                } else if (PayConstant.CHANEL_JD.equals(payType)) {
                    payType = PayConstant.CONSTANT_JD;//京东
                } else if (PayConstant.CHANEL_KJ.equals(payType)) {
                    payType = PayConstant.CONSTANT_KJ;//微信条码
                } else if (PayConstant.CHANEL_ALITM.equals(payType)) {
                    payType = PayConstant.CONSTANT_ALITM;//支付宝条码
                } else if ("1".equals(payType)) {
                    payType = "bank";
                } else {
                    logger.error(clazz_name + "支付回调验签类型匹配异常");
                    return ret_str_success;
                }
                HANYPayServiceImpl xxb = new HANYPayServiceImpl(pmapsconfig, payType);
                infoMap.put("payType", payType);
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
     * @Description QGZF钱柜支付回调通知
     */
    @LogApi("钱柜支付回调")
    @RequestMapping("/QGZFNotify.do")
    @ResponseBody
    public String QGZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";//收到通知后请回复  success
        String clazz_name = "QGZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("pay_money");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = infoMap.get("system_order_sn");//支付商订单号
        String trade_status = infoMap.get("status");//0 是接收订单  1 付款中 2 已支付 3 支付失败 4 已退款
        String t_trade_status = "2";//2:成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_QGZF)) {
                QGZFPayServiceImpl xxb = new QGZFPayServiceImpl(pmapsconfig);
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
     * @Description 宜橙支付回调通知
     */
    @LogApi("宜橙支付回调")
    @RequestMapping("/YICZFNotify.do")
    @ResponseBody
    public String YICZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "SUCCESS";//收到通知后请回复  success
        String clazz_name = "YICZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("sysorderid");//流水号
        String trade_status = infoMap.get("opstate");//0:成功，其他失败
        String t_trade_status = "0";//0:成功
        String order_amount = infoMap.get("ovalue");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YICZF)) {
                YICZFPayServiceImpl yic = new YICZFPayServiceImpl(pmapsconfig);
                String rmsg = yic.callback(infoMap);
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
     * @Description 易收支付回调通知
     */
    @LogApi("易收支付回调")
    @RequestMapping("/YISZFNotify.do")
    @ResponseBody
    public String YISZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "YISZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("sdorderno");// 平台订单号
        String trade_no = infoMap.get("sdpayno");//流水号
        String trade_status = infoMap.get("status");//0:成功，其他失败
        String t_trade_status = "1";//0:成功
        String order_amount = infoMap.get("total_fee");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YISZF)) {
                YISZFPayServiceImpl yis = new YISZFPayServiceImpl(pmapsconfig);
                String rmsg = yis.callback(infoMap);
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
     * @Description 宝富支付回调通知
     */
    @LogApi("宝富支付回调")
    @RequestMapping("/BFZFNotify.do")
    @ResponseBody
    public String BFZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";//收到通知后请回复  success
        String clazz_name = "BFZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");//流水号
        String trade_status = infoMap.get("returncode");//0:成功，其他失败
        String t_trade_status = "00";//00:成功
        String order_amount = infoMap.get("amount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
                return ret__success;
            }
            //金额验证
            Double orderAmount = rechargeOrderVO.getOrderAmount();
            double amount = Double.parseDouble(order_amount);
            if (Math.abs(orderAmount - amount) > 1) {
                logger.info(clazz_name + "支付金额{}与回调金额{}相差大于1", orderAmount, amount);
                return ret_str_failed;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(amount);
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_BFZF)) {
                BFZFPayServiceImpl bf = new BFZFPayServiceImpl(pmapsconfig);
                String rmsg = bf.callback(infoMap);
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
     * 新YZF 支付
     */
    @LogApi("新YZF支付回调")
    @RequestMapping("/YZFZFNotify.do")
    @ResponseBody
    public String YZFZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        logger.info("YZFZFNotify 新YZF支付开始回调调用------------");
        String clazz_name = "新YZF支付回调";
        String ret_success = "success";
        Map<String, String> infoMap = new TreeMap<String, String>();
        try {
            String str = HttpUtil.getRequestBody(request);
            logger.info("回调函数获取的参数: {}", str);
            infoMap.putAll(XMLUtils.formatXMlToMap(str));
            logger.info("{}获取参数完成,参数值:{}", clazz_name, infoMap);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{}出现错误...", clazz_name, e.getMessage());
        }
        logger.info("{} 参数:{}", clazz_name, infoMap);

        if (infoMap == null || infoMap.isEmpty()) {
            logger.info("{}支付回调获取请求参数为空!", clazz_name);
            return ret_str_failed;
        }
        logger.info("{} 支付回调请求参数:{}", clazz_name, infoMap);

        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = System.currentTimeMillis() + "";
        String trade_status = infoMap.get("status");
        String t_trade_status = "0";//订单状态：0支付成功
        String price = infoMap.get("total_fee");
        if (StringUtils.isEmpty(price)) {
            logger.info("{}实际支付金额为null", price);
            return ret_str_failed;
        }
        double money = Double.valueOf(price) / 100;//回调，以 分为单位,需要除以 100
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        logger.info("{} 会员实际支付金额:{}", clazz_name, money);

        if (payMap.containsKey(order_no)) {
            logger.info("{}支付回调订单号:{}重复调用", clazz_name, order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");

        try {
            logger.info("{}执行回调业务开始=========================START===========================", clazz_name);
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info("{}支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", clazz_name, order_no);
                return ret_success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(money);
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info("{} 非法支付商ID,查询支付商信息失败,支付商ID:{}", clazz_name, payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info("支付回调验签开始=======================START====================", clazz_name);
            if (paymentName.equals(PayConstant.CONSTANT_YZFZF)) {
                YZFZFPayServiceImpl xxb = new YZFZFPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info("{}支付回调验签失败!", clazz_name);

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info("{}支付回调验签成功!", clazz_name);
            } else {
                // 异常请求
                logger.error("{}支付回调异常请求", clazz_name);
                return ret_str_failed;
            }
            logger.info("{}支付回调验签结束=======================END====================", clazz_name);

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info("{}支付回调业务处理成功=======================SUCCESS====================", clazz_name);
                return ret_success;
            }
            logger.info("{} 支付回调业务处理成功=======================FAILD====================", clazz_name);
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("{}支付回调业务处理异常:{}", clazz_name, e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info("支付回调业务处理成功,删除缓存中的订单KEY:{}", clazz_name, order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param session
     * @return
     * @Description 简付支付回调通知
     */
    @LogApi("简付支付回调")
    @RequestMapping("/JIANNotify.do")
    @ResponseBody
    public String JIANNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "JIANNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        logger.info("请求参数结果:{}", infoMap.toString());
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("fxddh");// 平台订单号
        String trade_no = infoMap.get("fxorder");//流水号
        String trade_status = infoMap.get("fxstatus");//【1代表支付成功】
        String t_trade_status = "1";//1支付成功     0支付失败
        String order_amount = infoMap.get("fxfee");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_JIAN)) {
                JIANPayServiceImpl jian = new JIANPayServiceImpl(pmapsconfig);
                String rmsg = jian.callback(infoMap);
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
     * @Description 蝙蝠侠支付回调通知
     */
    @LogApi("蝙蝠侠支付回调")
    @RequestMapping("/BATNotify.do")
    @ResponseBody
    public String BATNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "BATNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("ddh");// 平台订单号
        String trade_no = "bat" + System.currentTimeMillis();//流水号
        String trade_status = infoMap.get("status");//0:成功，其他失败
        String t_trade_status = "success";//success:成功，fail失败
        String order_amount = infoMap.get("je");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_BAT)) {
                BATPayServiceImpl bat = new BATPayServiceImpl(pmapsconfig);
                String rmsg = bat.callback(infoMap);
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
     * @Description 全付通支付回调通知
     */
    @LogApi("全付通支付回调")
    @RequestMapping("/QFTZFNotify.do")
    @ResponseBody
    public String QFTZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "QFTZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("r6_Order");// 平台订单号
        String trade_no = infoMap.get("r2_TrxId");//流水号
        String trade_status = infoMap.get("r1_Code");//0:成功，其他失败
        String t_trade_status = "1";//success:成功，fail失败
        String order_amount = infoMap.get("r3_Amt");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        try {
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (payMap.containsKey(order_no)) {
                String tradeStatus = rechargeOrderVO.getTradeStatus();
                if ("paying".equals(tradeStatus)) {
                    BaseController.payMap.remove(order_no);
                } else {
                    logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
                    return ret_str_failed;
                }
            }
            payMap.put(order_no, "1");

            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息

            if (rechargeOrderVO == null) {
                logger.info(clazz_name + "支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", order_no);
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_QFTZF)) {
                QFTZFPayServiceImpl qft = new QFTZFPayServiceImpl(pmapsconfig);
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
     * @Description PAYS支付回调通知
     */
    @LogApi("PAYS支付回调")
    @RequestMapping("/PAYSNotify.do")
    @ResponseBody
    public JSONObject PAYSNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        JSONObject ret__success = new JSONObject();
        ret__success.put("ret_code", "0000");
        ret__success.put("ret_msg", "ok");
        JSONObject ret_str_failed = new JSONObject();
        ret_str_failed.put("ret_code", "0001");
        ret_str_failed.put("ret_msg", "fail");
        String clazz_name = "PAYSNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("confirm_money");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("no_order");// 平台订单号
        String trade_no = "PAYS" + System.currentTimeMillis();//支付商订单号
        String trade_status = infoMap.get("pay_state");//0 支付中 1支付成功 3支付超时 4支付失败
        String t_trade_status = "1";//1:成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_PAYS)) {
                PAYSPayServiceImpl xxb = new PAYSPayServiceImpl(pmapsconfig);
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
     * @Description GBZF国宝支付回调通知
     */
    @LogApi("国宝支付回调")
    @RequestMapping("/GBZFNotify.do")
    @ResponseBody
    public String GBZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "GBZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("tradeAmount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("merchOrderNo");// 平台订单号
        String trade_no = infoMap.get("platformOrderNo");//支付商订单号
        String trade_status = infoMap.get("status");//1代表支付成功
        String t_trade_status = "1";//1:成功
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        try {
            logger.info(clazz_name + "执行回调业务开始=========================START===========================");
            // 保存文件记录
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (payMap.containsKey(order_no)) {
                String tradeStatus = rechargeOrderVO.getTradeStatus();
                if ("paying".equals(tradeStatus)) {
                    BaseController.payMap.remove(order_no);
                } else {
                    logger.info(clazz_name + "支付回调订单号:{}重复调用", order_no);
                    return ret_str_failed;
                }
            }
            payMap.put(order_no, "1");
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_GBZF)) {
                GBZFPayServiceImpl xxb = new GBZFPayServiceImpl(pmapsconfig);
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
     * @Description 迅驰支付回调通知
     */
    @LogApi("迅驰支付回调")
    @RequestMapping("/XUNCNotify.do")
    @ResponseBody
    public String XUNCNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "XUNCNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        String returnData = JSONObject.fromObject(infoMap).getString("return_type");
        infoMap = JSONObject.fromObject(returnData);
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("price");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("order_id");// 平台订单号
        String trade_no = "XUNC" + System.currentTimeMillis();//支付商订单号
        String trade_status = "000";//0 支付中 1支付成功 3支付超时 4支付失败
        String t_trade_status = "000";//1:成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XUNC)) {
                XUNCPayServiceImpl xxb = new XUNCPayServiceImpl(pmapsconfig);
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
     * @Description 宏达支付回调通知
     */
    @LogApi("宏达支付回调")
    @RequestMapping("/HDZFNotify.do")
    @ResponseBody
    public String HDZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "HDZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getHDZFNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("out_order_id");// 平台订单号
        String trade_no = "HD" + System.currentTimeMillis();//流水号
        String trade_status = "1";//0:成功，其他失败
        String t_trade_status = "1";//success:成功，fail失败
        String order_amount = infoMap.get("money");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_HDZF)) {
                HDZFPayServiceImpl qft = new HDZFPayServiceImpl(pmapsconfig);
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
     * @Description HONGY鸿运支付回调通知
     */
    @LogApi("鸿运支付回调")
    @RequestMapping("/HONGYNotify.do")
    @ResponseBody
    public String HONGYNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";//收到通知后请回复  OK
        String clazz_name = "HONGYNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("amount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");//交易流水号
        String trade_status = infoMap.get("returncode");//00代表支付成功
        String t_trade_status = "00";//00:成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_HONGY)) {
                HONGYPayServiceImpl xxb = new HONGYPayServiceImpl(pmapsconfig);
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
     * @Description WZZF五洲支付回调通知
     */
    @LogApi("五洲支付回调")
    @RequestMapping("/WZZFNotify.do")
    @ResponseBody
    public String WZZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";//收到通知后请回复  OK
        String clazz_name = "WZZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("amount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");//交易流水号
        String trade_status = infoMap.get("returncode");//00代表支付成功
        String t_trade_status = "00";//00:成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_WZZF)) {
                WZZFPayServiceImpl xxb = new WZZFPayServiceImpl(pmapsconfig);
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
     * @Description YHB亿汇宝支付回调通知
     */
    @LogApi("亿汇宝支付回调")
    @RequestMapping("/YHBNotify.do")
    @ResponseBody
    public String YHBNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "OK";//收到通知后请回复  OK
        String clazz_name = "YHBNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("amount");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transaction_id");//交易流水号
        String trade_status = infoMap.get("returncode");//00代表支付成功
        String t_trade_status = "00";//00:成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YHB)) {
                //订单支付渠道
                String payType = String.valueOf(rechargeOrderVO.getPayType());
                if (PayConstant.CHANEL_ALI.equals(payType)) {
                    payType = PayConstant.CONSTANT_ALI;//支付宝
                } else if (PayConstant.CHANEL_WX.equals(payType)) {
                    payType = PayConstant.CONSTANT_WX;//微信
                } else if (PayConstant.CHANEL_YL.equals(payType)) {
                    payType = PayConstant.CONSTANT_YL;//银联支付
                } else if (PayConstant.CHANEL_CFT.equals(payType)) {
                    payType = PayConstant.CONSTANT_CFT;//财付通
                } else if (PayConstant.CHANEL_JD.equals(payType)) {
                    payType = PayConstant.CONSTANT_JD;//京东
                } else if (PayConstant.CHANEL_KJ.equals(payType)) {
                    payType = PayConstant.CONSTANT_KJ;//微信条码
                } else if (PayConstant.CHANEL_ALITM.equals(payType)) {
                    payType = PayConstant.CONSTANT_ALITM;//支付宝条码
                } else if ("1".equals(payType)) {
                    payType = "bank";
                } else {
                    logger.error(clazz_name + "支付回调验签类型匹配异常");
                    return ret_str_success;
                }
                YHBPayServiceImpl xxb = new YHBPayServiceImpl(pmapsconfig, payType);
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
     * @Description XBF新宝付支付回调通知
     */
    @LogApi("新宝付支付回调")
    @RequestMapping("/XBFNotify.do")
    @ResponseBody
    public String XBFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "XBFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("Moneys");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("LinkID");// 平台订单号
        String trade_no = "XBF" + System.currentTimeMillis();//交易流水号
        String trade_status = infoMap.get("sErrorCode");//1代表支付成功
        String t_trade_status = "1";//1:成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_XBF)) {
                XBFPayServiceImpl xxb = new XBFPayServiceImpl(pmapsconfig);
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
     * @Description YMZF易码支付回调通知
     */
    @LogApi("易码支付回调")
    @RequestMapping("/YMZFNotify.do")
    @ResponseBody
    public String YMZFNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "YMZFNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_amount = infoMap.get("needAmount");//实际支付金额
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        String order_no = infoMap.get("out_trade_no");// 平台订单号
        String trade_no = infoMap.get("ordersn");//交易流水号
        String trade_status = "1";//1代表支付成功
        String t_trade_status = "1";//1:成功
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_YMZF)) {
                YMZFPayServiceImpl xxb = new YMZFPayServiceImpl(pmapsconfig);
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
     * @Description 开联通支付回调通知
     */
    @LogApi("开联通支付回调")
    @RequestMapping("/KLTNotify.do")
    @ResponseBody
    public String KLTNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String ret__success = "success";//收到通知后请回复  success
        String clazz_name = "KLTNotify";
        logger.info(clazz_name + "支付回调开始-----------------------------START------------------------------");
        Map<String, String> infoMap = ParamsUtils.getNotifyParams(request);
        if (infoMap == null || infoMap.isEmpty()) {
            logger.info(clazz_name + "支付回调获取请求参数为空!");
            return ret_str_failed;
        }
        logger.info(clazz_name + "支付回调请求参数:{}", JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderNo");// 平台订单号
        String trade_no = infoMap.get("mchtOrderId");//流水号
        String trade_status = infoMap.get("payResult");//状态：0：处理中 1：支付成功 2：失败
        String t_trade_status = "1";//状态：0：处理中 1：支付成功 2：失败
        String order_amount = infoMap.get("orderAmount");//单位：分
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
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
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
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
            rechargeOrderVO.setOrderAmount(Double.parseDouble(order_amount) / 100);//单位：分
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info(clazz_name + "非法支付商ID,查询支付商信息失败,支付商ID:{}", payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name + "支付回调验签开始=======================START====================");
            if (paymentName.equals(PayConstant.CONSTANT_KLT)) {
                KLTPayServiceImpl xxb = new KLTPayServiceImpl(pmapsconfig);
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
     * 万通XX 支付回调函数
     *
     * @param request
     * @return
     */
    @LogApi("万通XX支付回调")
    @RequestMapping("/WTXXNotify.do")
    @ResponseBody
    public String WTXXNotify(HttpServletRequest request) {
        logger.info("WTXXNotify 万通XX支付开始回调调用------------");
        String clazz_name = "万通XX支付回调";
        String ret_success = "SUCCESS";
        Map<String, String> infoMap = new HashMap<String, String>();
        try {
            infoMap = ParamsUtils.getNotifyParams(request);
            logger.info("{}获取参数完成,参数值:{}", clazz_name, infoMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{}出现错误...", clazz_name, e.getMessage());
        }
        logger.info("{} 参数:{}", clazz_name, infoMap);

        if (infoMap == null || infoMap.isEmpty()) {
            logger.info("{}支付回调获取请求参数为空!", clazz_name);
            return ret_str_failed;
        }
        logger.info("{} 支付回调请求参数:{}", clazz_name, infoMap);

        String order_no = infoMap.get("orderid");// 平台订单号
        String trade_no = infoMap.get("transid");
        String trade_status = infoMap.get("status");
        String t_trade_status = "1";//交易结果, 1:已支付
        String order_amount = infoMap.get("price");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info(clazz_name + "获取实际支付金额为空!");
            return ret_str_failed;
        }
        double money = Double.valueOf(order_amount) / 100;//回调，以 分为单位,需要除以 100
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        logger.info("{} 会员实际支付金额:{}", clazz_name, money);

        if (payMap.containsKey(order_no)) {
            logger.info("{}支付回调订单号:{}重复调用", clazz_name, order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");

        try {
            logger.info("{}执行回调业务开始=========================START===========================", clazz_name);
            // 保存文件记录
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info("{}支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", clazz_name, order_no);
                return ret_success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(money);
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info("{} 非法支付商ID,查询支付商信息失败,支付商ID:{}", clazz_name, payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info("支付回调验签开始=======================START====================", clazz_name);
            if (paymentName.equals(PayConstant.CONSTANT_WTXX)) {
                WTXXPayServiceImpl xxb = new WTXXPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info("{}支付回调验签失败!", clazz_name);

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info("{}支付回调验签成功!", clazz_name);
            } else {
                // 异常请求
                logger.error("{}支付回调异常请求", clazz_name);
                return ret_str_failed;
            }
            logger.info("{}支付回调验签结束=======================END====================", clazz_name);

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info("{}支付回调业务处理成功=======================SUCCESS====================", clazz_name);
                return ret_success;
            }
            logger.info("{} 支付回调业务处理成功=======================FAILD====================", clazz_name);
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("{}支付回调业务处理异常:{}", clazz_name, e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info("支付回调业务处理成功,删除缓存中的订单KEY:{}", clazz_name, order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * 商盟支付
     *
     * @param request
     * @return
     */
    @LogApi("商盟支付回调")
    @RequestMapping("/SMZFNotify.do")
    @ResponseBody
    public String SMZFNotify(HttpServletRequest request) {
        logger.info("SMZFNotify 商盟支付开始回调调用------------");
        String clazz_name = "商盟支付回调";
        String ret_success = "success";
        Map<String, String> infoMap = new HashMap<String, String>();
        try {
            infoMap = ParamsUtils.getNotifyParams(request);
            logger.info("{}获取参数完成,参数值:{}", clazz_name, infoMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{}出现错误...", clazz_name, e.getMessage());
        }
        logger.info("{} 参数:{}", clazz_name, infoMap);

        if (infoMap == null || infoMap.isEmpty()) {
            logger.info("{}支付回调获取请求参数为空!", clazz_name);
            return ret_str_failed;
        }
        logger.info("{} 支付回调请求参数:{}", clazz_name, infoMap);

        String order_no = infoMap.get("order_id");// 平台订单号
        String trade_no = infoMap.get("platform_order_id");
        String trade_status = infoMap.get("status");
        String t_trade_status = "2";//订单状态：1- 待支付；2- 支付成功；3- 支付失败；
        String order_amount = infoMap.get("money");
        if (StringUtils.isEmpty(order_amount)) {
            logger.info("{} {}实际支付金额为空", clazz_name, order_no);
            return ret_str_failed;
        }
        double money = Double.valueOf(order_amount) / 100;//回调，以 分为单位,需要除以 100
        logger.info("{}实际支付金额为 {}", order_no, money);
        String ip = StringUtils.isEmpty(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        logger.info("{} 会员实际支付金额:{}", clazz_name, money);

        if (payMap.containsKey(order_no)) {
            logger.info("{}支付回调订单号:{}重复调用", clazz_name, order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");

        try {
            logger.info("{}执行回调业务开始=========================START===========================", clazz_name);
            // 保存文件记录
            //savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if (rechargeOrderVO == null) {
                logger.info("{}支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}", clazz_name, order_no);
                return ret_success;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            rechargeOrderVO.setOrderAmount(money);
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if (cagentYespayVO == null) {
                logger.info("{} 非法支付商ID,查询支付商信息失败,支付商ID:{}", clazz_name, payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String, String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info("支付回调验签开始=======================START====================", clazz_name);
            if (paymentName.equals(PayConstant.CONSTANT_SMZF)) {
                SMZFPayServiceImpl xxb = new SMZFPayServiceImpl(pmapsconfig);
                String rmsg = xxb.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info("{}支付回调验签失败!", clazz_name);

                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info("{}支付回调验签成功!", clazz_name);
            } else {
                // 异常请求
                logger.error("{}支付回调异常请求", clazz_name);
                return ret_str_failed;
            }
            logger.info("{}支付回调验签结束=======================END====================", clazz_name);

            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if (ResultResponse.SUCCESS_CODE.equals(result)) {
                logger.info("{}支付回调业务处理成功=======================SUCCESS====================", clazz_name);
                return ret_success;
            }
            logger.info("{} 支付回调业务处理成功=======================FAILD====================", clazz_name);
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("{}支付回调业务处理异常:{}", clazz_name, e.getMessage());
            return ret_str_failed;
        } finally {
            if (payMap.containsKey(order_no)) {
                logger.info("支付回调业务处理成功,删除缓存中的订单KEY:{}", clazz_name, order_no);
                payMap.remove(order_no);
            }
        }
    }
    /**
     * 
     * @Description 测试回调通知
     * @param request
     * @param response
     * @param session
     * @return
     */
    @RequestMapping("/TESTNotify.do")
    @ResponseBody
    public String TESTNotify(HttpServletRequest request, HttpServletResponse response, HttpSession session,String orderNo) {
        String ret__success = "success";//返回字符串OK，就表示回调已收到。
        String clazz_name = "TESTNoify";
        logger.info(clazz_name+"支付回调开始-----------------------------START------------------------------");
        Map<String,String> infoMap = TestParamsUtils.getNotifyParams(request);
        if(infoMap == null || infoMap.isEmpty()){
          logger.info(clazz_name+"支付回调获取请求参数为空!");
          return ret_str_failed;
        }
        logger.info(clazz_name+"支付回调请求参数",JSONObject.fromObject(infoMap).toString());
        //=================================获取回调基本参数结果--START===========================//
        String order_no = infoMap.get("orderno");// 平台订单号
        String trade_no = infoMap.get("porderno");// 第三方订单号
        String trade_status = infoMap.get("status");//订单状态:00为成功
        String t_trade_status = "success";// 表示成功状态
        String ip = StringUtils.isBlank(IPTools.getIp(request))?"127.0.0.1":IPTools.getIp(request);
        //=================================获取回调基本参数结果--END===========================//
        if (payMap.containsKey(order_no)) {
            logger.info(clazz_name+"支付回调订单号:{}重复调用",order_no);
            return ret_str_failed;
        }
        payMap.put(order_no, "1");
        try {
            logger.info(clazz_name+"执行回调业务开始=========================START===========================");
            // 保存文件记录
            savePayFile(clazz_name, infoMap, IPTools.getIp(request));
            //通过订单号查询订单信息
            RechargeOrderVO rechargeOrderVO = notifyService.findNotifyOrderByOrderNo(order_no);
            if(rechargeOrderVO == null){
                logger.info(clazz_name+"支付回调通知订单号为非法订单号,查询订单信息失败,订单号:{}",order_no);
                return ret_str_failed;
            }
            rechargeOrderVO.setTradeNo(trade_no);
            rechargeOrderVO.setTradeStatus(trade_status);
            rechargeOrderVO.setSuccessStatus(t_trade_status);
            rechargeOrderVO.setNotifyIp(ip);
            rechargeOrderVO.setNotifyParams(JSONObject.fromObject(infoMap).toString());
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            //查询支付商信息
            CagentYespayVO cagentYespayVO = notifyService.getCagentYespayByPayId(payId);
            if(cagentYespayVO == null){
                logger.info(clazz_name+"非法支付商ID,查询支付商信息失败,支付商ID:{}",payId);
                return ret_str_failed;
            }
            String paymentName = cagentYespayVO.getPaymentName();//支付商编码
            Map<String,String> pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息
            logger.info(clazz_name+"支付回调验签开始=======================START====================");
            if (paymentName.equals("TEST")) {
                TESTPayServiceImpl test = new TESTPayServiceImpl(pmapsconfig);
                String rmsg = test.callback(infoMap);
                if (!"success".equalsIgnoreCase(rmsg)) {
                    logger.info(clazz_name+"支付回调验签失败!");
                    notifyService.updateNotifyOrderDescription(rechargeOrderVO);
                    return ret_str_failed;
                }
                logger.info(clazz_name+"支付回调验签成功!");
            } else {
                // 异常请求
                logger.error(clazz_name+"支付回调异常请求");
                return ret_str_failed;
            }
            logger.info(clazz_name+"支付回调验签结束=======================END====================");
            
            logger.info("==========================处理订单回调业务并修改订单状态==========================");
            String result = notifyService.processNotifyOrder(rechargeOrderVO);
            if(ResultResponse.SUCCESS_CODE.equals(result)){
                logger.info(clazz_name+"支付回调业务处理成功=======================SUCCESS====================");
                return ret__success;
            }
            logger.info(clazz_name+"支付回调业务处理成功=======================FAILD====================");
            return ret_str_failed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(clazz_name+"支付回调业务处理异常:{}",e.getMessage());
            return ret_str_failed;
        }finally {
            if (payMap.containsKey(order_no)) {
                logger.info(clazz_name+"支付回调业务处理成功,删除缓存中的订单KEY:{}",order_no);
                payMap.remove(order_no);
            }
        }
    }

    /**
     * 佰富支付获取md5_key
     *
     * @param merId
     * @return
     */
    public JSONObject getPublicKey(String merId) {
        // 201709181816002
        List<Map<String, String>> ysepay = userService.selectTcagentYsepay("BF");
        String paymentConfig = "";
        JSONObject jsStr = new JSONObject();
        for (int i = 0; i < ysepay.size(); i++) {
            paymentConfig = ysepay.get(i).get("payment_config");
            jsStr = JSONObject.fromObject(paymentConfig);
            if (merId.equals(jsStr.get("merNo"))) {
                return jsStr;
            }
        }
        return jsStr;
    }

    /**
     * 验证支付订单
     *
     * @param order_no
     * @return
     */
    private Map<String, Object> check(String order_no) {
        String ret__error = "error";
        String ret__success = "success";
        Map<String, Object> returnMap = new HashMap<>();

        Map<String, Object> map = new HashMap<>();
        map.put("orderno", order_no);
        List<Map<String, String>> lm = userService.selectChickReCharge(map);
        // 单据不存在,直接返回

        if (lm.size() <= 0) {
            logger.info("单据不存在,直接返回");
            returnMap.put("status", ret__error);
            return returnMap;
        }
        Map<String, String> order = lm.get(0);
        // 获取用户信息
        Object ouid = order.get("uid");
        String uid = ouid.toString();
        Map<String, Object> param = new HashMap<>();
        param.put("uid", uid);
        map = userService.selectUserById(param);
        JSONObject jmap = JSONObject.fromObject(map);

        String payId = String.valueOf(order.get("pay_id"));
        if (StringUtils.isEmpty(payId)) {
            logger.info("支付商id不存在！");
            returnMap.put("status", ret__error);
            return returnMap;
        }
        List<Map<String, String>> plist = userService.selectYsepaybyId(payId, uid);
        if (plist.size() > 0) {
        } else {
            logger.info("单据不存在,直接返回");
            returnMap.put("status", ret__error);
            return returnMap;
        }

        // // 获取配置信息
        Map<String, String> hsConfigMap = plist.get(0);
        // // 支付商
        String paymentName = hsConfigMap.get("payment_name").toString();
        logger.info("用户支付商为:" + paymentName);

        if (null == paymentName || "".equals(paymentName)) {
            logger.info("用户支付商不存在:" + paymentName);
            returnMap.put("status", ret__error);
            return returnMap;
        }
        String PaymentConfig = hsConfigMap.get("payment_config").toString();
        // // 配置信息
        Map<String, String> pmapsconfig = JSONUtils.toHashMap(PaymentConfig);

        returnMap.put("order", order);
        returnMap.put("jmap", jmap);
        returnMap.put("plist", plist);
        returnMap.put("paymentName", paymentName);
        returnMap.put("status", ret__success);
        returnMap.put("pmapsconfig", pmapsconfig);
        return returnMap;

    }

    /**
     * 保存文件
     *
     * @param fileName
     * @param request1
     * @param ip
     */
    private void savePayFile(String fileName, Map<String, String> request1, String ip) {
        // 文件记录
        FileLog f = new FileLog();
        Map<String, String> fileMap = new HashMap<String, String>();
        fileMap.put("requestIp", ip);
        fileMap.put("requestParams", JSONUtils.toJSONString(request1));
        f.setLog(fileName, fileMap);
    }

    /**
     * 支付存储过程
     *
     * @param ip
     * @param requestMap
     * @param order_no
     * @param trade_no
     * @param trade_status
     * @param order
     * @param uid
     * @param jmap
     * @param plist
     * @return
     * @throws Exception
     * @throws SQLException
     */
    private String SaveProcess(String ip, Map<String, String> requestMap, String order_no, String trade_no,
                               String trade_status, Map<String, String> order, String uid, JSONObject jmap,
                               List<Map<String, String>> plist, String t_trade_status) throws Exception, SQLException {
        String ret__success = "error";
        // 回调日志
        Map<String, Object> cmap = new HashMap<>();
        Map<String, String[]> errorMap = new HashMap<String, String[]>();
        cmap.put("params", JSONObject.fromObject(requestMap).toString());
        cmap.put("ip", ip);
        cmap.put("status", ret_str_success);
        userService.InsertCallbacklog(cmap);
        logger.info("回调记录保存成功!");

        JSONObject jo = JSONObject.fromObject(order);
        String oamount = jo.getString("order_amount");

        String status = order.get("trade_status");
        // 如果单据状态不是交易中则跳过后续步骤
        if (!"处理中".equals(status)) {
            logger.info("订单状态异常" + status);
            return "success";
        }
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        Map<String, Object> user = userService.selectUserById(params);

        double wallet = Double.parseDouble(jmap.getString("wallet"));
        double amt = Double.parseDouble(oamount);

        String sql2 = "update t_recharge set trade_status = ?,trade_no=? where order_no=?";
        List<String> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Map<String, Object> paramsMap = new HashMap<String, Object>();

        if (t_trade_status.equals(trade_status)) {
            JSONObject json = JSONObject.fromObject(plist.get(0));
            Double cj = amt * json.getDouble("dividend_rate");
            Double dml = (amt + cj) * json.getInt("coding_rate");
            // logger.info("单据成功");
            DecimalFormat df = new DecimalFormat("######0.00");
            cj = Double.parseDouble(df.format(cj));

            try {
                paramsMap.put("amt", amt);
                paramsMap.put("uid", uid);
                paramsMap.put("wallet", wallet);
                paramsMap.put("cj", cj);
                paramsMap.put("tradeNo", trade_no);
                paramsMap.put("outTradeNo", order_no);
                paramsMap.put("cagent", user.get("cagent"));
                paramsMap.put("dml", dml);
                paramsMap.put("number", "CJ" + System.currentTimeMillis());
                paramsMap.put("number2", "CK" + System.currentTimeMillis());
                paramsMap.put("_err", "0");
                /** 进入存储过程前做一次验证 **/
                /*
                 * int i = userService.selectOrderNoStatus(paramsMap); if(i >= 1) { return ret__success; }
                 */
                // userService.ysePayCallBack(paramsMap);

                int res = notifyService.saveProcess(paramsMap);

                if (res == -1) {
                    logger.info("执行回调失败!");
                    return ret__success;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ret__success;
            }
            logger.info("UID : " + uid);
            logger.info("Username : " + jmap.getString("username"));
            logger.info("order : " + jo);
            logger.info("amt : " + amt);
            logger.info("dml : " + dml);
            logger.info("cj : " + cj);
            logger.info("------------------订单完成----------------------");
        } else {
            logger.info("回调成功，修改记录");
            // 更新转账信息
            connection = JDBCTools.getConnection();
            connection.setAutoCommit(false);

            list.clear();
            list.add("faild");
            list.add(trade_no);
            list.add(order_no);

            preparedStatement = connection.prepareStatement(sql2);

            for (int i = 0; i < list.size(); i++) {
                preparedStatement.setObject(i + 1, list.get(i));
            }
            preparedStatement.executeUpdate();
            connection.commit();
        }
        return "success";
    }


    private String SaveRechageProcess(String ip, Map<String, String> requestMap, String order_no, String trade_no,
                                      String trade_status, Map<String, String> order, String uid, JSONObject jmap,
                                      List<Map<String, String>> plist, String t_trade_status) throws Exception {
        logger.info("回调验签成功后,修改用户订单、打码量、余额等开始==================START==========================");
        try {
            //队列
            ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
            String params = JSONObject.fromObject(requestMap).toString();
            //入队列
            queue.offer(notifyService.saveRecharge(order_no, trade_status, trade_no, t_trade_status, ip, params));
            //出队列
            String response = queue.poll();
            if (response.equalsIgnoreCase("success")) {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("回调验签成功后,修改用户订单、打码量、余额等异常:" + e.getMessage());
        }
        return "error";
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
                if (org.apache.commons.lang.StringUtils.isNotBlank(notifyIp) && notifyIp.contains(ip)) {
                    isContainsIp = true;
                    break;
                }
            }
        } else {
            if (org.apache.commons.lang.StringUtils.isNotBlank(notifyIps) && notifyIps.contains(ip)) {
                isContainsIp = true;
            }
        }
        return isContainsIp;
    }

}
