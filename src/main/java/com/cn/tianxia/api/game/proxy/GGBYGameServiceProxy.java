package com.cn.tianxia.api.game.proxy;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.GGBYGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:24
 * @Description: GGBYGameServiceProxy
 */
@Service("GGBY")
public class GGBYGameServiceProxy implements GameInterfaceService {

    private static final Logger logger = LoggerFactory.getLogger(GGBYGameServiceProxy.class);

    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        Map<String,String> data = gameTransferVO.getConfig();
        String ag_username = gameTransferVO.getAg_username();
        String billno = gameTransferVO.getBillno();
        String credit = gameTransferVO.getMoney();
        String username = gameTransferVO.getUsername();
        String ag_password = gameTransferVO.getPassword();
        String type = gameTransferVO.getType();
        String ip = gameTransferVO.getIp();
        GGBYGameServiceImpl gg = new GGBYGameServiceImpl(data);
        String msg = gg.TransferCredit(ag_username, billno, credit, "IN", ag_password, ip);
        if ("success".equalsIgnoreCase(msg)) {
            // 成功
            return TransferResponse.transferSuccess();
        }

        // 轮询订单
        int polls = 0;
        for (;;){
            Thread.sleep(2000);
            logger.info("用户:【"+username+"】,订单号:【"+billno+"】,操作天下平台向游戏平台:【"+type+"】转入金额业务(游戏上分)业务,第{}次轮询转账订单次数", polls);
            polls++;
            msg = gg.QueryOrderStatus(billno);
            if ("0".equals(msg)) {
                // 0为该单据已成功
                return TransferResponse.transferSuccess();
            } else if ("-1".equals(msg)) {
                // 异常订单
                if (polls > 2) {
                    return TransferResponse.transferProcess();
                }
            } else {
                if (polls > 2) {
                    return TransferResponse.transferFaild();
                }
            }
        }
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        Map<String,String> data = gameTransferVO.getConfig();
        String ag_username = gameTransferVO.getAg_username();
        String billno = gameTransferVO.getBillno();
        String credit = gameTransferVO.getMoney();
        String username = gameTransferVO.getUsername();
        String ag_password = gameTransferVO.getPassword();
        String type = gameTransferVO.getType();
        String ip = gameTransferVO.getIp();
        String msg = "";
        GGBYGameServiceImpl gg = new GGBYGameServiceImpl(data);
        msg = gg.TransferCredit(ag_username, billno, credit + "", "OUT", ag_password, ip);
        if ("success".equals(msg)) {
            return TransferResponse.transferSuccess();
        } else {
            //轮询
            int polls = 0;
            for (;;){
                Thread.sleep(2000);
                logger.info("用户【"+username+"】,订单号:【"+billno+"】,游戏平台【" + type + "】向天下平台转入金额业务(游戏下分)业务,第{}次轮询转账订单次数", polls);
                polls++;
                msg = gg.QueryOrderStatus(billno);
                if ("0".endsWith(msg)) {
                    return TransferResponse.transferSuccess();
                } else {
                    if(polls > 2){
                        return TransferResponse.transferFaild();
                    }
                }
            }
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
