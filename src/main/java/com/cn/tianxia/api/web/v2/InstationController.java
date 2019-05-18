package com.cn.tianxia.api.web.v2;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.po.v2.JSONArrayResponse;
import com.cn.tianxia.api.service.v2.InstationService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName InstationController
 * @Description 站内信接口
 * @author Hardy
 * @Date 2019年2月1日 下午8:37:35
 * @version 1.0.0
 */
@Controller
@RequestMapping("/User")
public class InstationController extends BaseController{
    
    @Autowired
    private InstationService instationService;
    
    @Autowired
    private RedisUtils redisUtils;
    

    /**
     * 
     * @Description 获取站内信数量
     * @param request
     * @param response
     * @param bdate
     * @param edate
     * @return
     */
    @RequestMapping("/getMessageNum")
    @ResponseBody
    public JSONObject getMessageNum(HttpServletRequest request, HttpServletResponse response, String bdate,
            String edate) {
        logger.info("调用查询用户站内信条数接口开始===================START=================");
        try {
            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                logger.info("获取用户ID失败,用户登录超时");
                return BaseResponse.error("0", "获取用户ID失败,用户登录超时");
            }
            String uid = map.get("uid");
            return instationService.getMessageNum(uid, bdate, edate);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询用户站内信条数接口异常:{}",e.getMessage());
            return BaseResponse.error("0", "调用查询用户站内信条数接口异常");
        }
    }
    
    /**
     * 
     * @Description 获取站内信列表
     * @param request
     * @param response
     * @param status
     * @param bdate
     * @param edate
     * @return
     */
    @RequestMapping("/getMessageList")
    @ResponseBody
    public JSONArray getMessageList(HttpServletRequest request, HttpServletResponse response, String status,
            String bdate, String edate) {
        logger.info("调用查询用户站内信列表接口开始===================START=================");
        try {
            // 从缓存中获取用户ID
            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                logger.info("获取用户ID失败,用户登录超时");
                return JSONArrayResponse.faild("获取用户ID失败,用户登录超时");
            }
            String uid = map.get("uid");
            return instationService.getMessageList(uid,status, bdate, edate);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询用户站内信列表接口异常:{}",e.getMessage());
            return JSONArrayResponse.faild("调用查询用户站内信列表接口异常");
        }
    }
    
    /**
     * 
     * @Description 获取站内信详情
     * @param request
     * @param response
     * @param id
     * @return
     */
    @RequestMapping("/getMessageInfo")
    @ResponseBody
    public JSONObject getMessageInfo(HttpServletRequest request, HttpServletResponse response, String id) {
        logger.info("调用查看用户站内信详情接口开始===================START=================");
        try {
            
            if(StringUtils.isBlank(id)){
                return BaseResponse.error("0", "请求参数异常,站内信ID不能为空");
            }
            
            return instationService.getMessageInfo(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询用户站内信详情接口异常:{}",e.getMessage());
            return BaseResponse.error("0","调用查看用户站内信详情接口异常");
        }
    }
    
    /**
     * 
     * @Description 删除站内信详情
     * @param request
     * @param response
     * @param id
     * @return
     */
    @RequestMapping("/deleteMessage")
    @ResponseBody
    public JSONObject deleteMessage(HttpServletRequest request, HttpServletResponse response, String id) {
        logger.info("调用删除用户站内信接口开始===================START=================");
        try {
            
            if(StringUtils.isBlank(id)){
                return BaseResponse.faild("0", "请求参数异常,站内信ID不能为空");
            }
            return instationService.deleteMessage(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用删除用户站内信接口异常:{}",e.getMessage());
            return BaseResponse.faild("0","调用删除用户站内信接口异常");
        }
    }
}
