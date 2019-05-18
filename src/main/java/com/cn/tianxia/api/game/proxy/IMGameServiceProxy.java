package com.cn.tianxia.api.game.proxy;

import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.IMONEGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * IM 游戏入口
 * jacky
 */
@Service("IM")
public class IMGameServiceProxy implements GameInterfaceService {
    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        IMONEGameServiceImpl imoneGameService = new IMONEGameServiceImpl(gameTransferVO.getConfig());
        String status = imoneGameService.transferIn(gameTransferVO);
        if(status == "success")  return TransferResponse.transferSuccess();
        if(status == "faild")    return TransferResponse.transferFaild();
        return  TransferResponse.transferProcess();
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        IMONEGameServiceImpl imoneGameService = new IMONEGameServiceImpl(gameTransferVO.getConfig());
        String status = imoneGameService.transferOut(gameTransferVO);
        if(status == "success")  return TransferResponse.transferSuccess();
        if(status == "faild")    return TransferResponse.transferFaild();
        return  TransferResponse.transferProcess();
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
