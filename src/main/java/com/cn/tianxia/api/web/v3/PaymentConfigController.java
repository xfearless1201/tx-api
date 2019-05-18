package com.cn.tianxia.api.web.v3;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.base.annotation.LogApi;
import com.cn.tianxia.api.common.v2.ResultResponse;
import com.cn.tianxia.api.service.v2.PlatPaymentService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.web.BaseController;

/**
 * 
 * @ClassName PayConfigController
 * @Description 支付配置接口
 * @author Hardy
 * @Date 2019年2月11日 下午12:08:56
 * @version 1.0.0
 */
@RequestMapping("V2/PlatformPay")
@Controller
public class PaymentConfigController extends BaseController{
    
    @Autowired
    private PlatPaymentService platPaymentService;
    
    @Autowired
    private RedisUtils redisUtils;
    
    /**
     * 
     * @Description 获取会员具备的支付渠道
     * @param request
     * @param response
     * @return
     */
    @LogApi("获取会员具备的支付渠道")
    @RequestMapping("paymentChannel")
    @ResponseBody
    public ResultResponse queryPaymentChannel(HttpServletRequest request,HttpServletResponse response){
        logger.info("调用查询会员具备的支付渠道接口开始===================START===================");
        try {
            //从缓存中获取用户ID
            Map<String,String> data = getUserInfoMap(redisUtils,request);
            if(CollectionUtils.isEmpty(data)){
                logger.info("从缓存中获取用户的信息失败");
                return ResultResponse.faild("从缓存中获取用户的信息失败,请从新登录");
            }
            String uid = data.get("uid");
            //用户分层ID
            Integer typeId = Integer.parseInt(data.get("typeid").toString());
            //用户平台ID
            Integer cid = Integer.parseInt(data.get("cid").toString());
            return platPaymentService.queryPaymentChannel(Integer.parseInt(uid.toString()), cid, typeId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询会员具备的支付渠道异常:{}",e.getMessage());
            return ResultResponse.error("调用查询会员具备的支付渠道异常");
        }
    }
    
    /**
     * 
     * @Description 获取支付商列表
     * @param request
     * @param response
     * @param type
     * @return
     */
    @LogApi("获取支付商列表")
    @RequestMapping("getPaymentList")
    @ResponseBody
    public ResultResponse queryPaymentList(HttpServletRequest request,HttpServletResponse response,String type){
        logger.info("调用查询用户具备的支付商列表接口开始=================START==================");
        try {
            if(StringUtils.isBlank(type)){
                logger.info("请求参数错误,支付类型不能为空");
                return ResultResponse.faild("请求参数错误,支付类型不能为空");
            }
            //从缓存中获取用户ID
            Map<String,String> data = getUserInfoMap(redisUtils,request);
            if(CollectionUtils.isEmpty(data)){
                logger.info("从缓存中获取用户的信息失败");
                return ResultResponse.faild("从缓存中获取用户的信息失败,请从新登录");
            }
            String uid = data.get("uid");
            //用户分层ID
            Integer typeId = Integer.parseInt(data.get("typeid").toString());
            //用户平台ID
            Integer cid = Integer.parseInt(data.get("cid").toString());
            return platPaymentService.queryPaymentList(Integer.parseInt(uid.toString()), cid, typeId, type);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询用户具备的支付商列表异常:{}",e.getMessage());
            return ResultResponse.error("调用查询用户具备的支付商列表异常");
        }
    }
}
