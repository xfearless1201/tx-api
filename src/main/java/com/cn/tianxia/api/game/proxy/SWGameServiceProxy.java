package com.cn.tianxia.api.game.proxy;

import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.SWGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:46
 * @Description: SW游戏接口代理类
 */
@Service("SW")
public class SWGameServiceProxy implements GameInterfaceService {
    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        SWGameServiceImpl sw = new SWGameServiceImpl(gameTransferVO.getConfig());
        String msg = sw.transferTo(gameTransferVO.getAg_username(), Double.valueOf(gameTransferVO.getMoney()), gameTransferVO.getBillno());
        if ("success".equalsIgnoreCase(msg)) {
            // 转账订单提交成功
            return TransferResponse.transferSuccess();
        } else {
            int count = 0;
            while(true){
                count++;
                String querOrder = sw.queryOrderStatus(gameTransferVO.getBillno());//查询订单信息
                
                if("success".equalsIgnoreCase(querOrder)){
                    //订单处理成功
                    return TransferResponse.transferSuccess();
                }else if("faild".equalsIgnoreCase(querOrder)){
                    return TransferResponse.transferFaild();
                }else {
                    if(count > 2){
                        return TransferResponse.transferProcess();
                    }
                }
            }
        }
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        SWGameServiceImpl sw = new SWGameServiceImpl(gameTransferVO.getConfig());
        String msg = sw.transferFrom(gameTransferVO.getAg_username(), Double.valueOf(gameTransferVO.getMoney()), gameTransferVO.getBillno());
        if ("success".equals(msg)) {
            return TransferResponse.transferSuccess();
        } else {
            int count = 0;
            while(true){
                count ++;
                String querOrder = sw.queryOrderStatus(gameTransferVO.getBillno());//查询订单信息
                
                if("success".equalsIgnoreCase(querOrder)){
                    //订单处理成功
                    return TransferResponse.transferSuccess();
                }else {
                    if(count > 2){
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
