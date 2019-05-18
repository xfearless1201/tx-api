package com.cn.tianxia.api.game.proxy;

import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.OGGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:17
 * @Description: OG游戏接口代理类
 */
@Service("OG")
public class OGGameServiceProxy implements GameInterfaceService {
    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        OGGameServiceImpl o = new OGGameServiceImpl(gameTransferVO.getConfig());
        String msg = o.DEPOSIT(gameTransferVO.getAg_username(), gameTransferVO.getPassword(), gameTransferVO.getBillno(), gameTransferVO.getMoney());
        if ("success".equalsIgnoreCase(msg)) {
            // 转账订单处理成功
            return TransferResponse.transferSuccess();
        }
        return TransferResponse.transferFaild();
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        OGGameServiceImpl o = new OGGameServiceImpl(gameTransferVO.getConfig());
        String msg = o.WITHDRAW(gameTransferVO.getAg_username(), gameTransferVO.getPassword(), gameTransferVO.getBillno(), gameTransferVO.getMoney());
        if ("success".equals(msg)) {
            // 转账订单处理成功
            return TransferResponse.transferSuccess();
        }
        return TransferResponse.transferFaild();
    }

    @Override
    public JSONObject forwardGame(GameForwardVO gameForwardVO) throws Exception {
        return null;
    }

    @Override
    public JSONObject getBalance(GameBalanceVO gameBalanceVO) throws Exception {
        return null;
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
