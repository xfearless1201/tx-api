package com.cn.tianxia.api.game.proxy;

import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.NBGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:45
 * @Description: NB游戏接口代理类
 */
@Service("NB")
public class NBGameServiceProxy implements GameInterfaceService {
    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        NBGameServiceImpl nb = new NBGameServiceImpl(gameTransferVO.getConfig());
        String msg = nb.transfer(gameTransferVO.getAg_username(), 1, 1, gameTransferVO.getMoney());
        if ("success".equalsIgnoreCase(msg)) {
            // 转账订单提交成功
            return TransferResponse.transferSuccess();
        } else {
            return TransferResponse.transferProcess();
        }
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        NBGameServiceImpl nb = new NBGameServiceImpl(gameTransferVO.getConfig());
        String msg = nb.transfer(gameTransferVO.getAg_username(), 0, 1, gameTransferVO.getMoney());
        if ("success".equals(msg)) {
            return TransferResponse.transferSuccess();
        } else {
            return TransferResponse.transferProcess();
        }
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
