package com.cn.tianxia.api.game.proxy;

import java.util.Map;

import com.cn.tianxia.api.game.impl.XHGServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.HGGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:31
 * @Description: HG游戏接口代理类
 */
@Service("HG")
public class HGGameServiceProxy implements GameInterfaceService {
    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        Map<String,String> data = gameTransferVO.getConfig();
        String hg_username = gameTransferVO.getHg_username();
        String billno = gameTransferVO.getBillno();
        String credit = gameTransferVO.getMoney();
        XHGServiceImpl c = new XHGServiceImpl(data);
        if (StringUtils.isBlank(hg_username)) {
            return TransferResponse.transferFaild();
        }
        String msg = c.DEPOSIT(hg_username, billno, credit + "");
        if ("success".equalsIgnoreCase(msg)) {
            // 转账订单提交成功
            return TransferResponse.transferSuccess();
        } else {
            return TransferResponse.transferProcess();
        }
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        Map<String,String> data = gameTransferVO.getConfig();
        String hg_username = gameTransferVO.getHg_username();
        String billno = gameTransferVO.getBillno();
        String credit = gameTransferVO.getMoney();
        String msg = "";
        XHGServiceImpl c = new XHGServiceImpl(data);
        msg = c.WITHDRAW(hg_username, billno, credit + "");
        if ("success".equals(msg) || msg == "success") {
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
