package com.cn.tianxia.api.game.proxy;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.impl.IBCGameServiceImpl;
import com.cn.tianxia.api.po.v2.TransferResponse;
import com.cn.tianxia.api.vo.v2.GameBalanceVO;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameQueryOrderVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;
import com.cn.tianxia.api.vo.v2.TransferVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/3/8 14:49
 * @Description: IBC游戏接口代理类
 */
@Service("IBC")
public class IBCGameServiceProxy implements GameInterfaceService {

    private static final Logger logger = LoggerFactory.getLogger(IBCGameServiceProxy.class);

    @Override
    public JSONObject transferIn(GameTransferVO gameTransferVO) throws Exception {
        Map<String,String> data = gameTransferVO.getConfig();
        String username = gameTransferVO.getUsername();
        String ag_username = gameTransferVO.getAg_username();
        String billno = gameTransferVO.getBillno();
        String credit = gameTransferVO.getMoney();
        String type = gameTransferVO.getType();
        IBCGameServiceImpl service = new IBCGameServiceImpl(data);
        TransferVO vo = new TransferVO();
        vo.setAccount(ag_username);
        vo.setMoney(Double.parseDouble(String.valueOf(credit)));
        vo.setOrderNo(billno);
        if(vo.getMoney()>1000.0){
            //用户转入金额大于1000就修改用户转账金额限制为最大值（处理老用户的转账金额限制为1000）
            service.updateMember(vo);
        }
        String resultResponse = service.transferIn(vo);
        if (resultResponse.equals("success")) {
            return TransferResponse.transferSuccess();
        } else if (resultResponse.equals("faild")) {
            return TransferResponse.transferFaild();
        } else {
            //轮训查询订单状态,判断转账状态
            int polls = 0;
            for(;;) {
                Thread.sleep(1500);
                logger.info("用户【" + username + "】操作天下平台向游戏平台【" + type + "】转入金额业务(游戏上分)业务,第{}次轮询转账订单次数", polls);
                polls++;
                String returnStr = service.checkFundTransfer(vo);
                logger.info("IBC 查询订单结果返回：" + returnStr + "订单编号："+ vo.getOrderNo());
                if(returnStr.equals("success")){
                    return TransferResponse.transferSuccess();
                }
                if(returnStr.equals("faild")){
                    return TransferResponse.transferFaild();
                }
                if(returnStr.equals("process" )&& polls > 2){
                    return TransferResponse.transferProcess();
                }
            }
        }
    }

    @Override
    public JSONObject transferOut(GameTransferVO gameTransferVO) throws Exception {
        Map<String,String> data = gameTransferVO.getConfig();
        String username = gameTransferVO.getUsername();
        String ag_username = gameTransferVO.getAg_username();
        String billno = gameTransferVO.getBillno();
        String credit = gameTransferVO.getMoney();
        String type = gameTransferVO.getType();
        IBCGameServiceImpl service = new IBCGameServiceImpl(data);
        TransferVO vo = new TransferVO();
        vo.setAccount(ag_username);
        vo.setMoney(Double.parseDouble(String.valueOf(credit)));
        vo.setOrderNo(billno);
        if(vo.getMoney()>1000.0){
            //用户转出金额大于1000，就发起修改用户信息请求，设置用户转出金额最大值（以前创建的用户转出金额最大是1000）
            String result = service.updateMember(vo);
            if(result.equals("error")){
                return TransferResponse.transferFaild();
            }
        }
        String resultResponse = service.transferOut(vo);
        if(resultResponse.equals("success")){
            return TransferResponse.transferSuccess();
        }else {
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
