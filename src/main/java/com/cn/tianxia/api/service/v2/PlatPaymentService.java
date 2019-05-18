package com.cn.tianxia.api.service.v2;


import com.cn.tianxia.api.common.v2.ResultResponse;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName PlatPaymentService
 * @Description 平台支付商接口
 * @author Hardy
 * @Date 2018年12月31日 下午3:46:04
 * @version 1.0.0
 */
public interface PlatPaymentService {

    /**
     * 获取可用支付渠道
     * @param userId 用户id
     */
    JSONObject getPaymentChannel(Integer uid,Integer cid,Integer typeId) throws Exception;

    /**
     * 获取支付渠道列表
     * @param userId 用户id
     * @param type 支付类型
     */
    JSONObject getPaymentList(Integer uid,Integer cid,Integer typeId,String type)throws Exception;
    
   
    /**
     * 
     * @Description 获取用户具备的支付渠道
     * @param uid
     * @param cid
     * @param typeId
     * @return
     * @throws Exception
     */
    ResultResponse queryPaymentChannel(Integer uid,Integer cid,Integer typeId)throws Exception;
    
    /**
     * 
     * @Description 根据支付渠道获取用户具备的支付商信息
     * @param uid
     * @param cid
     * @param typeId
     * @param type
     * @return
     * @throws Exception
     */
    ResultResponse queryPaymentList(Integer uid,Integer cid,Integer typeId,String type)throws Exception;

}
