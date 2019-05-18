package com.cn.tianxia.api.game.proxy;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.IGGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:28
 * @Description: IG游戏接口代理类
 */
@Service("IG")
public class IGGameServiceProxy implements GameInterfaceService {

    private static final Logger logger = LoggerFactory.getLogger(IGGameServiceProxy.class);

    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        Map<String,String> data = gameTransferVO.getConfig();
        String username = gameTransferVO.getUsername();
        String ag_username = gameTransferVO.getAg_username();
        String billno = gameTransferVO.getBillno();
        String credit = gameTransferVO.getMoney();
        String ag_password = gameTransferVO.getPassword();
        String type = gameTransferVO.getType();
        IGGameServiceImpl c = new IGGameServiceImpl(data,gameTransferVO.getAg_username().substring(0,3));
        String msg = c.DEPOSIT(ag_username, ag_password, billno, credit + "");
        if ("success".equalsIgnoreCase(msg)) {
            return TransferResponse.transferSuccess();
        }

        if(msg.equals("faild")) return TransferResponse.transferFaild();

        int polls = 0;
        for (;;){
            Thread.sleep(3000);
            logger.info("用户:【"+username+"】,订单号:【"+billno+"】,操作天下平台向游戏平台:【"+type+"】转入金额业务(游戏上分)业务,第{}次轮询转账订单次数", polls);
            polls++;
            String ckeckMsg = c.CHECK_REF(billno);
            if (ckeckMsg.equals("success"))  return TransferResponse.transferSuccess();
            if (ckeckMsg.equals("faild"))    return TransferResponse.transferFaild();
            if (ckeckMsg.equals("process") && polls>2)   return TransferResponse.transferProcess();
        }
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        Map<String, String> data = gameTransferVO.getConfig();
        String username = gameTransferVO.getUsername();
        String ag_username = gameTransferVO.getAg_username();
        String billno = gameTransferVO.getBillno();
        String credit = gameTransferVO.getMoney();
        String ag_password = gameTransferVO.getPassword();
        String type = gameTransferVO.getType();
        IGGameServiceImpl c = new IGGameServiceImpl(data,gameTransferVO.getAg_username().substring(0,3));
        String msg = c.WITHDRAW(ag_username, ag_password, billno, credit + "");
        if ("success".equals(msg)) return TransferResponse.transferSuccess();
        if(msg.equals("faild"))    return TransferResponse.transferFaild();

        int polls = 0;
        for (;;){
            Thread.sleep(2500);
            logger.info("用户:【"+username+"】,订单号:【"+billno+"】,操作天下平台向游戏平台:【"+type+"】转入金额业务(游戏上分)业务,第{}次轮询转账订单次数", polls);
            polls++;
            String ckeckMsg = c.CHECK_REF(billno);
            if (ckeckMsg.equals("success"))  return TransferResponse.transferSuccess();
            if (ckeckMsg.equals("faild"))    return TransferResponse.transferFaild();
            if (polls>2)   return TransferResponse.transferProcess();
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
