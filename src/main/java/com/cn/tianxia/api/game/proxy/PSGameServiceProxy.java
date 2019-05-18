package com.cn.tianxia.api.game.proxy;

import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.PSGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:44
 * @Description: PS游戏接口代理类
 */
@Service("PS")
public class PSGameServiceProxy implements GameInterfaceService {
    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        PSGameServiceImpl ps = new PSGameServiceImpl(gameTransferVO.getConfig());
        String msg = ps.transIn(gameTransferVO.getAg_username(), gameTransferVO.getBillno(), gameTransferVO.getMoney());
        if ("success".equalsIgnoreCase(msg)) {
            return TransferResponse.transferSuccess();
        } else {
            return TransferResponse.transferFaild();
        }
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        String msg = "";
        PSGameServiceImpl ps = new PSGameServiceImpl(gameTransferVO.getConfig());
        msg = ps.transOut(gameTransferVO.getAg_username(), gameTransferVO.getBillno(), gameTransferVO.getMoney());
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
