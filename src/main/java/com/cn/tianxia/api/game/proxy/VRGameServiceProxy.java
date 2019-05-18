package com.cn.tianxia.api.game.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.VRGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.service.UserService;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:34
 * @Description: VR游戏接口代理类
 */
@Service("VR")
public class VRGameServiceProxy implements GameInterfaceService {

    @Autowired
    private UserService userService;

    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        VRGameServiceImpl v = new VRGameServiceImpl(gameTransferVO.getConfig());
        String msg = v.Deposit(gameTransferVO.getBillno(), gameTransferVO.getAg_username(), Float.valueOf(gameTransferVO.getMoney()));
        if ("success".equalsIgnoreCase(msg)) {
            // 转账订单提交成功
            return TransferResponse.transferSuccess();
        } else {
            return TransferResponse.transferFaild();
        }
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        VRGameServiceImpl v = new VRGameServiceImpl(gameTransferVO.getConfig());
        String msg = v.Withdraw(gameTransferVO.getBillno(), gameTransferVO.getAg_username(), Float.valueOf(gameTransferVO.getMoney()));
        if ("success".equals(msg)) {
            return TransferResponse.transferSuccess();
        } else {
            return TransferResponse.transferFaild();
        }
    }

    @Override
    public JSONObject forwardGame(GameForwardVO gameForwardVO) throws Exception {
        return null;
    }

    @Override
    public JSONObject getBalance(GameBalanceVO gameBalanceVO) throws Exception {
        return  null;
    }

    @Override
    public JSONObject checkOrCreateAccount(GameCheckOrCreateVO gameCheckOrCreateVO) throws Exception {
        return null;
    }

    @Override
    public JSONObject queryTransferOrder(GameQueryOrderVO gameQueryOrderVO) throws Exception {
        return null;
    }
}
