package com.cn.tianxia.api.service;

import com.cn.tianxia.api.vo.EswLoginVo;
import com.cn.tianxia.api.vo.EswTransferVo;

/**
 * 第三方游戏service
 */
public interface ESWService {
    /**
     * 功能描述:验证游戏账号，如果账号不存在则创建游戏账号并为账号上分
     *
     * @Author: Horus
     * @Date: 2019/1/7 9:03
     * @param eswLoginVo
     **/
    String checkOrCreateGameAccout(EswLoginVo eswLoginVo) throws Exception;

    /**
     * 功能描述:
     *
     * @Author: Horus
     * @Date: 2019/1/14 14:51
     * @param userCode
     * @return: money/error
     **/
    String getBalance(String userCode) throws Exception;

    /**
     * 功能描述: 查询用户的甲方平台游戏内总分、用户可下分余额、用户在线状态
     *
     * @Author: Horus
     * @Date: 2019/1/7 13:46
     * @param userCode
     * @return: {"code":0,"money":4006.0048,"freeMoney":4006.0048,"status":0}/{"code":1012}
     **/
    String queryUserInfo(String userCode) throws Exception;

    /**
     * 功能描述: 上分
     *
     * @Author: Horus
     * @Date: 2019/1/14 14:40
     * @param userCode
     * @param money
     * @return: success/error
     **/
    public String transferIn(String userCode,String money,String orderId) throws Exception;

    /**
     * 功能描述: 下分
     *
     * @Author: Horus
     * @Date: 2019/1/14 14:43
     * @param userCode
     * @param money
     * @return: success/error
     **/
    String transferOut(String userCode,String money,String orderId) throws Exception;

    /**
     * 功能描述: 查询订单状态
     *
     * @Author: Horus
     * @Date: 2019/1/7 19:40
     * @param eswTransferVo
     * @return: java.lang.String
     **/
    String queryOrderStatus(EswTransferVo eswTransferVo) throws Exception;

    /**
     * 功能描述: 查询所有游戏状态
     *
     * @Author: Horus
     * @Date: 2019/1/7 19:41
     * @param
     * @return: java.lang.String
     **/
    String queryAllGameStatus()throws Exception;



}
