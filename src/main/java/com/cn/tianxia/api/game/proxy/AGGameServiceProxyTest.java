package com.cn.tianxia.api.game.proxy;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.AGGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.vo.v2.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: zed
 * @Date: 2019/5/12 17:51
 * @Description: 压力测试转账
 */
@Service("AG")
public class AGGameServiceProxyTest implements GameInterfaceService {

    @Autowired
    RedisUtils redisUtils;

    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        Thread.sleep(1500);
        long randmon = System.currentTimeMillis() % 10;
        if (randmon == 0||randmon == 1 || randmon ==2 || randmon == 3 || randmon == 4 || randmon == 5 || randmon == 6) {
//            String gameBalace = redisUtils.get("USER:GAME:WALLET:" + gameTransferVO.getUid());
//            redisUtils.set("USER:GAME:WALLET:" + gameTransferVO.getUid(), Double.valueOf(gameTransferVO.getMoney()) + Double.valueOf(gameBalace));
            AGGameServiceImpl.balance = AGGameServiceImpl.balance + Integer.valueOf(gameTransferVO.getMoney());
            return TransferResponse.transferSuccess();
        } else if(randmon == 7) {
            return TransferResponse.transferFaild();
        } else {
            return TransferResponse.transferProcess();
        }
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        Thread.sleep(1500);
        long randmon = System.currentTimeMillis() % 10;
        if (randmon == 0||randmon == 1 || randmon ==2 || randmon == 3 || randmon == 4 || randmon == 5 || randmon == 6) {
//            String gameBalace = redisUtils.get("USER:GAME:WALLET:" + gameTransferVO.getUid());
//            redisUtils.set("USER:GAME:WALLET:" + gameTransferVO.getUid(), Double.valueOf(gameTransferVO.getMoney()) - Double.valueOf(gameBalace));
            AGGameServiceImpl.balance = AGGameServiceImpl.balance - Integer.valueOf(gameTransferVO.getMoney());
            return TransferResponse.transferSuccess();
        } else if(randmon == 7) {
            return TransferResponse.transferFaild();
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
