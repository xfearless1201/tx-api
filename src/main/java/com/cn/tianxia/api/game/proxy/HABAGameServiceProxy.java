package com.cn.tianxia.api.game.proxy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.HABAGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;
import com.cn.tianxia.api.ws.MoneyResponse;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:22
 * @Description: HABA游戏接口代理类
 */
@Service("HABA")
public class HABAGameServiceProxy implements GameInterfaceService {
    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        Map<String,String> data = gameTransferVO.getConfig();
        String ag_username = gameTransferVO.getAg_username();
        String billno = gameTransferVO.getBillno();
        String credit = gameTransferVO.getMoney();
        String ag_password = gameTransferVO.getPassword();
        HABAGameServiceImpl h = new HABAGameServiceImpl(data);
        Map<String, Object> params = new HashMap<>();
        params.put("amount", credit);
        params.put("requestId", billno);
        MoneyResponse mr = h.depositPlayerMoney(ag_username, ag_password, params);
        if (mr == null) {
            // 异常订单
            return TransferResponse.transferProcess();
        }
        if (mr.isSuccess()) {
            // 转账成功
            return TransferResponse.transferSuccess();
        }
        return TransferResponse.transferFaild();
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        Map<String,String> data = gameTransferVO.getConfig();
        String ag_username = gameTransferVO.getAg_username();
        String billno = gameTransferVO.getBillno();
        int credit = Integer.parseInt(gameTransferVO.getMoney());
        String ag_password = gameTransferVO.getPassword();
        HABAGameServiceImpl h = new HABAGameServiceImpl(data);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", -credit);
        params.put("requestId", billno);
        MoneyResponse mr = h.withdrawPlayerMoney(ag_username, ag_password, params);
        if (mr.isSuccess()) {
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
