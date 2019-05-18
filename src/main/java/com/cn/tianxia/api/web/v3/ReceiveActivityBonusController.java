package com.cn.tianxia.api.web.v3;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cn.tianxia.api.common.v2.CacheKeyConstants;
import com.cn.tianxia.api.common.v2.ResultResponse;
import com.cn.tianxia.api.service.v2.ActivityService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.web.BaseController;

/**
 * 
 * @ClassName ReceiveActivityBonusController
 * @Description 领取活动奖金接口
 * @author Hardy
 * @Date 2019年3月14日 下午12:17:26
 * @version 1.0.0
 */
@RestController
@RequestMapping("/V2/receive/bonus")
public class ReceiveActivityBonusController extends BaseController{

    //日志
    private static final Logger logger = LoggerFactory.getLogger(ReceiveActivityBonusController.class);
    
    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private RedisUtils redisUtils;
    
    
    /**
     * 
     * @Description 领取刮刮乐奖金
     * @param request
     * @param response
     * @param activityAmount 活动金额
     * @param activityId 活动ID
     * @param code 手机验证码
     * @param code 手机号码
     * @return
     */
    @PostMapping("/ggl")
    public ResultResponse receiveGGLBonus(HttpServletRequest request,HttpServletResponse response,
                                            String activityAmount,String activityId,String code,String phoneNo){
        logger.info("调用领取刮刮乐活动奖金接口开始====================START=======================");
        try {
            //从缓存中获取用户ID
            Map<String,String> data = getUserInfoMap(redisUtils,request);
            if(CollectionUtils.isEmpty(data)){
                logger.info("用户未登录,请重新登录");
                return ResultResponse.faild("用户未登录,请重新登录");
            }
            //从缓存中获取用户的刮奖金额
            Double gglAmount = Double.valueOf(redisUtils.get(CacheKeyConstants.CAGENT_GGL_UID + data.get("uid")));
            if(ObjectUtils.allNotNull(gglAmount)){
                if(StringUtils.isNotBlank(activityId)){
                    //判断缓存中的金额与传递过来的金额是否一致
                    if(StringUtils.isNotBlank(activityAmount)){
                        Double receiveAmount = Double.parseDouble(activityAmount);
                        if(Double.compare(receiveAmount, gglAmount) != 0){
                            return ResultResponse.faild("请求参数【活动金额】与刮奖金额不符合");
                        }
                    }
                }
            }
            ResultResponse result = activityService.receiveGGLBonus(data, activityAmount, activityId, code, phoneNo);
            redisUtils.delete(CacheKeyConstants.CAGENT_GGL_UID + data.get("uid"));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用领取刮刮乐活动奖金接口异常:{}",e.getMessage());
            return ResultResponse.faild("领取失败");
        }
    }
    
}
