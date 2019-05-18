package com.cn.tianxia.api.game.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.IGPJGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:30
 * @Description: IGPJ游戏接口代理类
 */
@Service("IGPJ")
public class IGPJGameServiceProxy implements GameInterfaceService {
    private static Logger logger = LoggerFactory.getLogger(IGPJGameServiceProxy.class);
    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        IGPJGameServiceImpl c = new IGPJGameServiceImpl(gameTransferVO.getConfig(),gameTransferVO.getAg_username().substring(0,3));
        String msg = c.DEPOSIT(gameTransferVO.getAg_username(), gameTransferVO.getPassword(), gameTransferVO.getBillno(), gameTransferVO.getMoney());
        if ("success".equalsIgnoreCase(msg)) {
            return TransferResponse.transferSuccess();
        }
        if(msg.equals("error"))   return TransferResponse.transferFaild();
        // 轮询
        boolean isPoll = true;
        int polls = 0;
        do {
            Thread.sleep(3000);
            logger.info("用户:【"+gameTransferVO.getAg_username()+"】,订单号:【"+gameTransferVO.getBillno()+"】,操作天下平台向游戏平台:【"+gameTransferVO.getType()+"】转入金额业务(游戏上分)业务,第{}次轮询转账订单次数", polls);
            polls++;
            String ckeckMsg = c.CHECK_REF(gameTransferVO.getBillno());
            if ("6601".endsWith(ckeckMsg)) {
                // 转账成功
                return TransferResponse.transferSuccess();
            } else if ("6617".endsWith(ckeckMsg)) {
                if (polls > 2) {
                    return TransferResponse.transferProcess();
                }
            } else if ("0".equals(ckeckMsg)){
                return TransferResponse.transferFaild();
            }
        } while (isPoll);

        return TransferResponse.transferProcess();
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        String msg = "";
        IGPJGameServiceImpl c = new IGPJGameServiceImpl(gameTransferVO.getConfig(),gameTransferVO.getAg_username().substring(0,3));
        msg = c.WITHDRAW(gameTransferVO.getAg_username(), gameTransferVO.getPassword(), gameTransferVO.getBillno(), gameTransferVO.getMoney());
        if(msg.equals("error")) return  TransferResponse.transferFaild();
        if ("success".equals(msg)) {
            return TransferResponse.transferSuccess();
        } else {
            //轮询
            boolean isPoll = true;
            int polls = 0;
            do {
                Thread.sleep(2000);
                logger.info("用户:【"+gameTransferVO.getAg_username()+"】,订单号:【"+gameTransferVO.getBillno()+"】,操作天下平台向游戏平台:【"+gameTransferVO.getType()+"】转入金额业务(游戏上分)业务,第{}次轮询转账订单次数", polls);
                polls++;
                msg = c.CHECK_REF(gameTransferVO.getBillno());
                //6601为该单据已成功,6607为处理中,2秒后再次查询该订单状态
                if ("6601".endsWith(msg)) {
                    return TransferResponse.transferSuccess();
                } else {
                    if(polls > 2){
                        return TransferResponse.transferFaild();
                    }
                }
            } while (isPoll);
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
