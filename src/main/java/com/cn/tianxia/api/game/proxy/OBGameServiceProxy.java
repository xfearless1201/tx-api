package com.cn.tianxia.api.game.proxy;

import java.security.SecureRandom;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.OBService;
import com.cn.tianxia.api.game.impl.OBGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:16
 * @Description: OB游戏接口代理类
 */
@Service("OB")
public class OBGameServiceProxy implements GameInterfaceService {
    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        OBService ob = new OBGameServiceImpl(gameTransferVO.getConfig());
        String msg = ob.agent_client_transfer(gameTransferVO.getAg_username(), gameTransferVO.getBillno(), "1", gameTransferVO.getMoney());
        if (StringUtils.isBlank(msg)) {
            // 查询订单
            msg = ob.queryOrder(new SecureRandom().nextLong(), gameTransferVO.getBillno());
            if (StringUtils.isBlank(msg)) {
                // 查询无响应结果
                return TransferResponse.transferProcess();
            }

            if ("1".equals(JSONObject.fromObject(msg).getString("transferState"))) {
                // 转账订单处理成功
                return TransferResponse.transferSuccess();
            }

        } else if ("Ok".equalsIgnoreCase(JSONObject.fromObject(msg).getString("error_code"))) {
            // 转账订单处理成功
            return TransferResponse.transferSuccess();
        }

        // 转账订单处理失败
        return TransferResponse.transferFaild();
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        OBService ob = new OBGameServiceImpl(gameTransferVO.getConfig());
        String msg = ob.agent_client_transfer(gameTransferVO.getAg_username(), gameTransferVO.getBillno(), "0", gameTransferVO.getMoney());
        JSONObject json = JSONObject.fromObject(msg);
        if ("OK".equals(json.getString("error_code"))) {
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
