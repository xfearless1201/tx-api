package com.cn.tianxia.api.web.v2;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.base.annotation.LogApi;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.service.v2.PlatPaymentService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName PayConfigController
 * @Description 支付配置接口
 * @author Hardy
 * @Date 2019年2月11日 下午12:08:56
 * @version 1.0.0
 */
@RequestMapping("PlatformPay")
@Controller
public class PayConfigController extends BaseController{
    
    @Autowired
    private PlatPaymentService platPaymentService;
    
    @Autowired
    private RedisUtils redisUtils;
    
    /**
     * 
     * @Description 获取支付渠道
     * @param request
     * @param session
     * @param response
     * @return
     */
    @LogApi("获取可用支付渠道")
    @RequestMapping("paymentChannel")
    @ResponseBody
    public JSONObject getPaymentChannel(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        logger.info("调用获取支付渠道接口开始=================START=================");
        try {
            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                logger.info("用户未登录,请重新登录");
                return BaseResponse.error("1001","用户未登陆！");
            }
            String uid = map.get("uid");
            //用户分层ID
            Integer typeId = Integer.parseInt(map.get("typeid").toString());
            //用户平台ID
            Integer cid = Integer.parseInt(map.get("cid").toString());
            return platPaymentService.getPaymentChannel(Integer.parseInt(uid.toString()), cid, typeId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取支付渠道接口异常:{}",e.getMessage());
            return BaseResponse.error("0", "调用获取支付渠道接口异常");
        }
    }
    
    /**
     * 
     * @Description 获取可用支付列表
     * @param request
     * @param session
     * @param response
     * @param type
     * @return
     */
    @LogApi("获取可用支付列表")
    @RequestMapping("getPaymentList")
    @ResponseBody
    public JSONObject getPaymentList(HttpServletRequest request, HttpSession session, HttpServletResponse response,
            String type) {
        logger.info("调用获取可用支付列表接口开始==================START========================");
        try {
            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                logger.info("用户未登录,请重新登录");
                return BaseResponse.error("1001","用户未登陆！");
            }
            String uid = map.get("uid");
            //用户分层ID
            Integer typeId = Integer.parseInt(map.get("typeid").toString());
            //用户平台ID
            Integer cid = Integer.parseInt(map.get("cid").toString());
            return platPaymentService.getPaymentList(Integer.parseInt(uid.toString()), cid, typeId, type);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取可用支付列表接口异常:{}",e.getMessage());
            return BaseResponse.error("0", "调用获取可用支付列表接口异常");
        }
    }
    
}
