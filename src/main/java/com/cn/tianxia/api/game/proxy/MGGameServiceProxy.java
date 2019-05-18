package com.cn.tianxia.api.game.proxy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.MGGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:19
 * @Description: MG游戏接口代理类
 */
@Service("MG")
public class MGGameServiceProxy implements GameInterfaceService {
    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        MGGameServiceImpl m = new MGGameServiceImpl(gameTransferVO.getConfig());
        Map<String, String> mgmap = new HashMap<>();
        mgmap.put("ClientIP", gameTransferVO.getIp());
        mgmap.put("OrderId", gameTransferVO.getBillno());
        JSONObject result = m.deposit(gameTransferVO.getAg_username(), gameTransferVO.getPassword(), gameTransferVO.getMoney(), mgmap);
        if (result.containsKey("Code") && "success".equalsIgnoreCase(result.getString("Code"))) {
            // 转账处理成功
            return TransferResponse.transferSuccess();
        } else if ("error".equalsIgnoreCase(result.getString("Code"))) {
            // 异常订单
             return TransferResponse.transferProcess();
        } else {
            // 失败订单
            return TransferResponse.transferFaild();
        }
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        Map<String, String> mgmap = new HashMap<>();
        mgmap.put("ClientIP", gameTransferVO.getIp());
        mgmap.put("OrderId", gameTransferVO.getBillno());
        MGGameServiceImpl m = new MGGameServiceImpl(gameTransferVO.getConfig());
        JSONObject jsonObject = m.withdrawal(gameTransferVO.getAg_username(), gameTransferVO.getPassword(),  gameTransferVO.getMoney(), mgmap);
        String msg = jsonObject.getString("Code");
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
