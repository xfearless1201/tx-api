package com.cn.tianxia.api.game.proxy;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.PTGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:20
 * @Description: PT游戏接口代理类
 */
@Service("PT")
public class PTGameServiceProxy implements GameInterfaceService {
    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        PTGameServiceImpl p = new PTGameServiceImpl(gameTransferVO.getConfig());
        String msg = p.Deposit(gameTransferVO.getAg_username(), gameTransferVO.getMoney(), gameTransferVO.getBillno());
        if (StringUtils.isBlank(msg)) {
            // 异常订单
            return TransferResponse.transferProcess();
        }
        JSONObject json = JSONObject.fromObject(msg);
        if (json.containsKey("result") && json.getString("result").indexOf("errorcode") < 0) {
            // 成功
            return TransferResponse.transferSuccess();
        }
        return TransferResponse.transferFaild();
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        String msg = "";
        PTGameServiceImpl p = new PTGameServiceImpl(gameTransferVO.getConfig());
        msg = p.Withdraw(gameTransferVO.getAg_username(), gameTransferVO.getMoney(), gameTransferVO.getBillno());
        JSONObject jsonObject = JSONObject.fromObject(msg);
        msg = jsonObject.getJSONObject("result").getString("result");
        if (msg.indexOf("errorcode") < 0) {
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
