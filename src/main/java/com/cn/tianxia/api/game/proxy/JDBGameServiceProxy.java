package com.cn.tianxia.api.game.proxy;

import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.JDBGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:23
 * @Description: JDB游戏接口代理类
 */
@Service("JDB")
public class JDBGameServiceProxy implements GameInterfaceService {
    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        JDBGameServiceImpl jdb = new JDBGameServiceImpl(gameTransferVO.getConfig());
        String msg = jdb.transIn(gameTransferVO.getAg_username(), gameTransferVO.getBillno(), Integer.valueOf(gameTransferVO.getMoney()));
        if ("success".equalsIgnoreCase(msg)) {
            // 成功
            return TransferResponse.transferSuccess();
        }

        if ("error".equalsIgnoreCase(msg)) {
            // 异常订单
            return TransferResponse.transferProcess();
        }
        return TransferResponse.transferFaild();
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        JDBGameServiceImpl jdb = new JDBGameServiceImpl(gameTransferVO.getConfig());
        String msg = jdb.transOut(gameTransferVO.getAg_username(), gameTransferVO.getBillno(), Integer.valueOf(gameTransferVO.getMoney()));
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
