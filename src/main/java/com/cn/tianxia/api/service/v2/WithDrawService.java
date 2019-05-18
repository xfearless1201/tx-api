package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.vo.CreateWithDrawOrderVO;
import com.cn.tianxia.api.vo.v2.WithdrawRecordVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName WithDrawService
 * @Description 提现接口
 * @author Hardy
 * @Date 2019年1月29日 下午2:57:15
 * @version 1.0.0
 */
public interface WithDrawService {
    
    /**
     * 
     * @Description 创建提现订单
     * @param createWithDrawOrderVO
     * @return
     */
    public JSONObject createWithDrawOrder(CreateWithDrawOrderVO createWithDrawOrderVO);
    
    /**
     * 
     * @Description 查询用户提现记录
     * @param withdrawRecordVO
     * @return
     */
    public JSONArray findAllByPage(WithdrawRecordVO withdrawRecordVO);

    /**
     *
     * @Description 查询用户打码量游戏流水强制提款手续费
     * @param uid
     *
     */
    JSONObject selectWithdrawConfig(String uid);



}
