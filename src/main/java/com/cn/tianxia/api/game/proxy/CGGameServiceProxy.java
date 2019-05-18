package com.cn.tianxia.api.game.proxy;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.CGGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:26
 * @Description: CG游戏接口代理类
 */
@Service("CG")
public class CGGameServiceProxy implements GameInterfaceService {

    private static final Logger logger = LoggerFactory.getLogger(CGGameServiceProxy.class);

    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        Map<String,String> data = gameTransferVO.getConfig();
        String ag_username = gameTransferVO.getAg_username();
        String billno = gameTransferVO.getBillno();
        String credit = gameTransferVO.getMoney();
        String username = gameTransferVO.getUsername();
        String ag_password = gameTransferVO.getPassword();
        String type = gameTransferVO.getType();
        CGGameServiceImpl c = new CGGameServiceImpl(data);
        String msg = c.DEPOSIT(ag_username, ag_password, billno, credit + "");
        if(msg.equals("error"))  return  TransferResponse.transferFaild();
        if ("success".equalsIgnoreCase(msg)) {
            // 转账成功
            return TransferResponse.transferSuccess();
        }

        // 轮询订单
        int polls = 0;
        for (;;){
            // 休眠2秒
            Thread.sleep(3000);
            logger.info("用户:【"+username+"】,订单号:【"+billno+"】,操作天下平台向游戏平台:【"+type+"】转入金额业务(游戏上分)业务,第{}次轮询转账订单次数", polls);
            polls++;
            // 查询订单
            msg = c.CHECK_REF(billno);
            // 6601为该单据已成功,6617为处理中,2秒后再次查询该订单状态
            if ("6601".endsWith(msg)) {
                return TransferResponse.transferSuccess();
            } else{
                if (polls > 2) {
                    // 异常订单,需要人工审核
                    return TransferResponse.transferProcess();
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
        CGGameServiceImpl c = new CGGameServiceImpl(data);
        String msg = c.WITHDRAW(ag_username, ag_password, billno, credit + "");
        if(msg.equals("error"))  return  TransferResponse.transferFaild();
        if ("success".equals(msg)) {
            return TransferResponse.transferSuccess();
        } else {
            //轮询
            int polls = 0;
            for (;;){
                Thread.sleep(2000);
                logger.info("用户【"+username+"】,订单号:【"+billno+"】,游戏平台【" + type + "】向天下平台转入金额业务(游戏下分)业务,第{}次轮询转账订单次数", polls);
                polls++;
                msg = c.CHECK_REF(billno);
                //6601为该单据已成功,6607为处理中,2秒后再次查询该订单状态
                if ("6601".endsWith(msg)) {
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
