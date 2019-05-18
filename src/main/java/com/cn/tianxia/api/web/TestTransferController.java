package com.cn.tianxia.api.web;

import com.cn.tianxia.api.common.v2.CacheKeyConstants;
import com.cn.tianxia.api.common.v2.GameTypeUtils;
import com.cn.tianxia.api.common.v2.KeyConstant;
import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.GameProxyFactory;
import com.cn.tianxia.api.game.impl.AGGameServiceImpl;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.project.Transfer;
import com.cn.tianxia.api.service.v2.NewUserService;
import com.cn.tianxia.api.service.v2.UserGameTransferService;
import com.cn.tianxia.api.utils.*;
import com.cn.tianxia.api.vo.v2.GameTransferVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @Author: zed
 * @Date: 2019/5/12 21:04
 * @Description: 转账压力测试
 */
@RequestMapping("User")
@Controller
public class TestTransferController extends BaseController {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private NewUserService newUserService;
    @Autowired
    private UserGameTransferService userGameTransferService;

    /**
     * @Description 天下平台转向游戏平台(转出,游戏上分)
     * @param session
     * @param request
     * @param response
     * @param credit
     * @param type
     * @param uuid
     * @param imgcode
     * @return
     */
    @RequestMapping("/TransferToTest")
    @ResponseBody
    public JSONObject transferIn(HttpSession session, HttpServletRequest request, HttpServletResponse response,
                                 int credit, String type) {
        logger.info("调用天下平台向游戏平台转入金额(游戏上分)接口开始================START=====================");
        // 创建返回结果对象
        JSONObject jo = new JSONObject();

        Map<String,String> usermap = getUserInfoMap(redisUtils, request);
        if(CollectionUtils.isEmpty(usermap)){
            // 用户ID为空,证明用户未登录
            jo.put("msg", "03");
            jo.put("errmsg", "用户ID为空,登录已过期,请重新登录");
            return jo;
        }
        String uid = usermap.get("uid");
        // 缓存KEY
        String key = CacheKeyConstants.GAME_TRANSFER_KEY_UID+uid;
        if(redisUtils.hasKey(key)){
            jo.put("msg", "05");
            jo.put("errmsg", type + "平台转账处理中,请稍后再试");
            return jo;
        }
        redisUtils.set(key, uid,5);

        String ag_username = usermap.get("userName");
        try {

            String userLock = uid.intern();
            synchronized (userLock) {
                // 用户钱包余额
                double balance = newUserService.queryUserBalance(Integer.valueOf(uid));
                logger.info("用户【"+uid+"】 转账查询用户余额结果:{}",balance);
                if (balance < credit) {
                    return transferResponse("06", "转账失败,用户余额不足");
                }
                // 生成订单号
                String billno = generatorOrderNo(type);
                if (StringUtils.isBlank(billno)) {
                    return transferResponse("error", "创建订单号为空");
                }

                //扣除用户金额
                int i  = userGameTransferService.deductUserMoney(uid,Double.valueOf(credit));

                if(i > 0 ){

                    logger.info("用户【"+uid+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,生成订单号:{}",billno);
                    JSONObject transferResult = transferInProcess(type, ag_username, billno, credit);
                    logger.info("用户【"+ag_username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,发起第三方请求结果:{}",transferResult.toString());
                    // 构建写入流水对象
                    Transfer transfer = new Transfer();
                    transfer.setUid(Integer.parseInt(uid));
                    transfer.setBillno(billno);
                    transfer.setUsername(ag_username.toLowerCase());
                    transfer.settType("OUT");
                    transfer.settMoney(Float.valueOf(credit));
                    transfer.setOldMoney(Float.valueOf(balance+""));
                    transfer.setNewMoney(Float.valueOf((balance - credit)+""));
                    transfer.setType(type);
                    transfer.setIp("127.0.0.1");
                    transfer.settTime(new Date());
                    transfer.setStatus(1);

                    if ("success".equalsIgnoreCase(transferResult.getString("msg"))) {
                        // 转账成功,扣钱,写流水
                        return saveUserTransferSuccess(transfer, 1);
                    } else if ("faild".equalsIgnoreCase(transferResult.getString("msg"))) {
                        //补钱
                        if(userGameTransferService.addUserMoney(String.valueOf(uid),Double.valueOf(credit)) > 0){
                            // 转账失败,写流水
                            return saveUserTransferFaild(transfer);
                        }
                        return saveUserTransferOutFaild(transfer);
                    } else {
                        // 扣钱,写流水
                        return saveUserTransferOutFaild(transfer);
                    }
                }
                logger.info("用户【"+ag_username+"】 调用转账接口失败！扣除钱包金额异常！");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("msg", "error");
                jsonObject.put("errmsg", "转账失败!");
                return jsonObject;

            }
        } catch (Exception e) {
            logger.info("调用天下平台向游戏平台转入金额(游戏上分)接口异常:{}", e);
        } finally {
            if(redisUtils.hasKey(key)){
                logger.info("删除缓存key:{}", key);
                redisUtils.delete(key);
            }
            //清除缓存
            session.setAttribute("imgcode", "");
            session.removeAttribute("uuid");
        }
        return transferResponse("error", "调用天下平台向游戏平台转入金额(游戏上分)失败");
    }

    /**
     * @Description 游戏平台转向天下平台(转入,游戏下分)
     * @param session
     * @param request
     * @param response
     * @param credit
     * @param type
     * @param uuid
     * @param imgcode
     * @return
     */
    @RequestMapping("/TransferFromTest")
    @ResponseBody
    public JSONObject transferOut(HttpSession session, HttpServletRequest request, HttpServletResponse response,
                                  int credit, String type ) {
        logger.info("调用从游戏平台转向天下平台转出金额(游戏下分)开始===================start====================");
        // 创建返回结果对象
        JSONObject jo = new JSONObject();
        //从缓存中获取用户信息
        Map<String,String> usermap = getUserInfoMap(redisUtils, request);
        if(CollectionUtils.isEmpty(usermap)){
            jo.put("msg", "03");
            jo.put("errmsg", "用户ID为空,登录已过期,请重新登录");
            return jo;
        }
        String uid = usermap.get("uid");
        String ag_username = usermap.get("userName");
        // 缓存KEY
        String key = CacheKeyConstants.GAME_TRANSFER_KEY_UID + uid;
        if(redisUtils.hasKey(key)){
            jo.put("msg", "05");
            jo.put("errmsg", type + "平台转账处理中,请稍后再试");
            return jo;
        }
        redisUtils.set(key, uid,5);
        try {

            String userLock = uid.intern();
            synchronized (userLock) {
                //查询用户游戏余额
//                double gameBalance = Double.parseDouble(redisUtils.get("USER:GAME:WALLET:" + uid));
                double gameBalance = (double) AGGameServiceImpl.balance;
                //判断游戏余额是否足够
                if (gameBalance < credit) {
                    return transferResponse("06", "转账失败,用户游戏余额不足");
                }
                // 用户钱包余额
                double balance = newUserService.queryUserBalance(Integer.valueOf(uid));

                // 生成订单号
                String billno = generatorOrderNo(type);
                if (StringUtils.isBlank(billno)) {
                    return transferResponse("error", "创建订单号为空");
                }
                logger.info("用户【"+ag_username+"】,调用游戏平台向天下平台转入金额(游戏下分)接口,生成订单号:{}",billno);
                JSONObject transferResult = transferOutProcess(type, ag_username,billno, credit);
                logger.info("用户【"+ag_username+"】,调用游戏平台向天下平台转入金额(游戏下分)接口,发起第三方请求响应结果:{}",billno);
                // 构建写入流水对象
                Transfer transfer = new Transfer();
                transfer.setUid(Integer.valueOf(uid));
                transfer.setBillno(billno);
                transfer.setUsername(ag_username.toLowerCase());
                transfer.settType("IN");
                transfer.settMoney(Float.valueOf(credit));
                transfer.setOldMoney(Float.valueOf(balance+""));
                transfer.setNewMoney(Float.valueOf((balance+ credit)+ ""));
                transfer.setType(type);
                transfer.setIp("127.0.0.1");
                transfer.settTime(new Date());
                transfer.setStatus(1);
                if ("success".equalsIgnoreCase(transferResult.getString("msg"))) {
                    // 转账成功,加钱,写流水
                    return saveUserTransferSuccess(transfer, 2);
                }else {
                    //写流水
                    return saveUserTransferFaild(transfer);
                }
            }
        } catch (Exception e) {
            logger.info("调用从游戏平台转向天下平台转出金额(游戏下分)接口异常:{}", e.getMessage());
        } finally {
            if(redisUtils.hasKey(key)){
                redisUtils.delete(key);
            }
            //清除缓存
            session.setAttribute("imgcode", "");
            session.removeAttribute("uuid");
        }
        return transferResponse("error", "调用从游戏平台转向天下平台转出金额(游戏下分)失败");
    }

    private JSONObject transferResponse(String code, String message) {
        JSONObject data = new JSONObject();
        if (StringUtils.isBlank(code) || StringUtils.isBlank(message)) {
            data.put("msg", "error");
            data.put("errmsg", "转账异常");
            data.put("status", "error");
        } else {
            data.put("msg", code);
            data.put("errmsg", message);
            data.put("status", "error");
        }
        return data;
    }

    private synchronized String generatorOrderNo(String type) throws Exception {
        try {
            //同步方法随机休眠5毫秒，以生成不同的订单号
            Random interval = new Random();
            Thread.sleep(interval.nextInt(9));
            int randInt = (int)((Math.random()*9+1)*1000); // 4位随机数
            String billno = type + System.currentTimeMillis() + randInt;
            return billno;
        } catch (Exception e) {
            logger.info("生成转账订单号异常:{}", e.getMessage());
            throw new Exception("生成转账订单号异常");
        }
    }

    private JSONObject transferInProcess(String type, String ag_username,String billno, int credit) throws Exception {
        logger.info("用户:【"+ag_username+"】,订单号:【"+billno+"】,操作天下平台向游戏平台:【"+type+"】转入金额业务(游戏上分)业务开始=============START===============");
        try {
            type = GameTypeUtils.formartGameType(type);

            GameTransferVO gameTransferVO = new GameTransferVO();
            //gameTransferVO.setUid(uid);
            gameTransferVO.setAg_username(ag_username);
            gameTransferVO.setBillno(billno);
            gameTransferVO.setMoney(String.valueOf(credit));
            GameInterfaceService service = GameProxyFactory.productGameService(type);
            return service.transferIn(gameTransferVO);

        } catch (Exception e) {
            logger.info("用户:【"+ag_username+"】,订单号:【"+billno+"】,操作天下平台向游戏平台:【"+type+"】转入金额业务(游戏上分)业务异常:{}", e.getMessage());
            logger.info("用户：【"+ag_username+"】,订单号：【"+billno+"】，转账异常！",e);
            return transferResponse("process", "用户:【"+ag_username+"】,订单号:【"+billno+"】,操作天下平台向游戏平台:【"+type+"】转入金额业务(游戏上分)业务失败");
        }
    }



    /**
     *
     * @Description 操作游戏平台向天下平台转入金额业务(游戏下分)
     * @param type
     * @param ag_username
     * @param billno
     * @return
     */
    private JSONObject transferOutProcess(String type, String ag_username,String billno, int credit) throws Exception{

        logger.info("用户【"+ag_username+"】,订单号:【"+billno+"】,游戏平台【" + type + "】向天下平台转入金额业务(游戏下分)业务开始=============START===============");
        try {
            type = GameTypeUtils.formartGameType(type);
            GameTransferVO gameTransferVO = new GameTransferVO();
            //gameTransferVO.setUid(uid);
            gameTransferVO.setAg_username(ag_username);
            gameTransferVO.setBillno(billno);
            gameTransferVO.setMoney(String.valueOf(credit));
            GameInterfaceService service = GameProxyFactory.productGameService(type);
            return service.transferOut(gameTransferVO);
        } catch (Exception e) {
            logger.info("用户【"+ag_username+"】,订单号:【"+billno+"】,游戏平台【" + type + "】向天下平台转入金额业务(游戏下分)业务异常:{}",e.getMessage());
            return transferResponse("faild", "用户【"+ag_username+"】游戏平台【" + type + "】向天下平台转入金额业务(游戏下分)业务失败");
        }
    }

    private synchronized JSONObject saveUserTransferSuccess(Transfer transfer, int type) {
        JSONObject jsonObject = new JSONObject();
        int result = 0;
        if (type == 1) {
            transfer.setResult("天下平台向游戏平台转出金额(游戏上分)成功!");
            jsonObject.put("errmsg", "天下平台向游戏平台转出金额(游戏上分)成功");
            result = userGameTransferService.insertUserTransferOut(transfer);
        } else {
            transfer.setResult("游戏平台向天下平台转入金额(游戏下分)成功!");
            jsonObject.put("errmsg", "游戏平台向天下平台转入金额(游戏下分)成功");
            result = userGameTransferService.insertUserTransferIn(transfer);
        }

        if (result > 0) {
            jsonObject.put("msg", "success");
        } else {
            jsonObject.put("msg", "error");
        }
        return jsonObject;
    }

    /**
     * @Description 转账失败
     * @param data
     * @return
     */
    private synchronized JSONObject saveUserTransferFaild(Transfer transfer) {
        JSONObject jsonObject = new JSONObject();
        transfer.setResult("转账失败,需人工审核!");
        userGameTransferService.insertUserTransferFaild(transfer);
        jsonObject.put("msg", "error");
        jsonObject.put("errmsg", "转账失败,需人工审核");
        return jsonObject;
    }

    /**
     *
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param data
     * @return
     */
    private synchronized JSONObject saveUserTransferOutFaild(Transfer transfer) {
        JSONObject jsonObject = new JSONObject();
        transfer.setResult("天下平台向游戏平台转入金额(游戏上分)失败,需人工审核,需人工审核");
        userGameTransferService.insertUserTransferOutFaild(transfer);
        jsonObject.put("msg", "error");
        jsonObject.put("errmsg", "天下平台向游戏平台转入金额(游戏上分)失败,需人工审核,需人工审核");
        return jsonObject;
    }

}
