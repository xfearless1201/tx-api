package com.cn.tianxia.api.game.proxy;

import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.NWGGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * NWG 新世界棋牌
 * jacky
 *
// */
@Service("NWG")
public class NWGGameServiceProxy  implements GameInterfaceService {

    private static final String  SUCCESS = "success";
    private static final String  FAILD = "faild";


    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {

        NWGGameServiceImpl nwgGameService = new NWGGameServiceImpl(gameTransferVO.getConfig());
        String status = nwgGameService.transferIn(gameTransferVO);
        if(status.equals(SUCCESS))  return TransferResponse.transferSuccess();
        if(status.equals(FAILD))    return TransferResponse.transferFaild();

        return TransferResponse.transferProcess();
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {

        NWGGameServiceImpl nwgGameService = new NWGGameServiceImpl(gameTransferVO.getConfig());
        String status = nwgGameService.transferOut(gameTransferVO);
        if(status.equals(SUCCESS) ) {
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
