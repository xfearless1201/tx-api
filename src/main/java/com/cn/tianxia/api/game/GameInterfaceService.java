package com.cn.tianxia.api.game;

import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 11:07
 * @Description: 游戏抽象接口
 */
public interface GameInterfaceService {
    /**
     *
     * @Description 上分接口
     * @param gameTransferVO
     * @return
     */
    JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception;


    /**
     *
     * @Description 下分接口
     * @param gameTransferVO
     * @return
     */
    JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception;

    /**
     *
     * @Description 游戏跳转
     * @return
     */
    JSONObject forwardGame(GameForwardVO gameForwardVO) throws Exception;

    /**
     * 获取游戏余额
     * @Description
     * @return
     */
    JSONObject getBalance(GameBalanceVO gameBalanceVO) throws Exception;

    /**
     *
     * @Description 检查或创建用户信息
     * @return
     */
    JSONObject checkOrCreateAccount(GameCheckOrCreateVO gameCheckOrCreateVO) throws Exception;

    /**
     *
     * @Description 查询订单
     * @return
     */
    JSONObject queryTransferOrder(GameQueryOrderVO gameQueryOrderVO) throws Exception;
}
