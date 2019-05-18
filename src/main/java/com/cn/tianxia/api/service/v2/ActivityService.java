package com.cn.tianxia.api.service.v2;

import java.util.Map;

import com.cn.tianxia.api.common.v2.ResultResponse;

/**
 * 
 * @ClassName ActivityService
 * @Description 活动接口
 * @author Hardy
 * @Date 2019年3月13日 上午11:52:41
 * @version 1.0.0
 */
public interface ActivityService {

    /**
     * 
     * @Description 刮刮乐活动
     * @param agentCode  平台编码
     * @return
     */
    public ResultResponse guagualeAcitivity(Map<String,String> userMap, String agentCode, String type)throws Exception;
    
    /**
     * 
     * @Description 领取刮刮乐奖金
     * @param uid
     * @param activityAmount
     * @param activityId
     * @return
     * @throws Exception
     */
    public ResultResponse receiveGGLBonus(Map<String,String> userMap, String activityAmount, String activityId,String code,String phoneNo) throws Exception;
}
