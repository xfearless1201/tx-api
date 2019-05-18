package com.cn.tianxia.api.web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import com.cn.tianxia.api.game.impl.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.common.v2.CacheKeyConstants;
import com.cn.tianxia.api.common.v2.GameTypeUtils;
import com.cn.tianxia.api.common.v2.KeyConstant;
import com.cn.tianxia.api.game.GameInterfaceService;
import com.cn.tianxia.api.game.GameProxyFactory;
import com.cn.tianxia.api.game.OBService;
import com.cn.tianxia.api.game.SBService;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.po.v2.ResponseCode;
import com.cn.tianxia.api.po.v2.ResultResponse;
import com.cn.tianxia.api.project.Transfer;
import com.cn.tianxia.api.service.UserService;
import com.cn.tianxia.api.service.v2.UserGameTransferService;
import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.PlatFromConfig;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.utils.UserTypeHandicapUtil;
import com.cn.tianxia.api.vo.EswLoginVo;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.GameTransferVO;
import com.cn.tianxia.api.vo.v2.TransferVO;
import com.cn.tianxia.api.ws.LoginUserResponse;
import com.cn.tianxia.api.ws.QueryPlayerResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName TransferController
 * @Description 转账接口
 * @author Hardy
 * @Date 2019年1月27日 下午10:57:20
 * @version 1.0.0
 */
@RequestMapping("User")
@Controller
public class TransferController extends BaseController {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

    @Autowired
    private UserGameTransferService userGameTransferService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtils redisUtils;

    static SimpleDateFormat  simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");


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
    @RequestMapping("/TransferTo")
    @ResponseBody
    public JSONObject transferIn(HttpSession session, HttpServletRequest request, HttpServletResponse response,
                                 int credit, String type, String uuid, String imgcode) {
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
        try {
            //从缓存中获取用户信息
            String ag_password = usermap.get("ag_password");
            String ag_username = usermap.get("ag_username");
            String hg_username = usermap.get("hg_username");
            String username = usermap.get("userName");//用户名称
            String cid = usermap.get("cid");//平台号
            String cagent = usermap.get("cagent");//平台编码
//            String suuid = formatObjectParams(session.getAttribute("uuid"));
            String simgcode = formatObjectParams(session.getAttribute("imgcode"));
            String ip = IPTools.getIp(request);
            //查询游戏配置信息
            Map<String,String> pmap = formatPlatformConfigByCagent();
            if(CollectionUtils.isEmpty(pmap)){
                logger.info("查询平台配置信息为空");
                return BaseResponse.error("0", "查询平台配置信息为空");
            }
            //根据平台筛选
            logger.info("查询平台配置信息结果:{}",JSONObject.fromObject(pmap).toString());
            // 校验请求参数
            JSONObject verifyData = verifyRequestParams(ag_password, ag_username, simgcode, type,
                    credit, uuid, imgcode);
            type = type.trim().toUpperCase();//格式化游戏编码
            if (!"success".equalsIgnoreCase(verifyData.getString("msg"))) {
                logger.info("用户【"+username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,校验请求参数结果:{}",verifyData.toString());
                return verifyData;
            }
            // 加密密码
            DESEncrypt d = new DESEncrypt(KeyConstant.DESKEY);
            String password = d.decrypt(ag_password);
            // 检查维护状态
            PlatFromConfig pf = new PlatFromConfig();
            pf.InitData(pmap, type);
            logger.info("用户【"+username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,查询平台【"+cagent+"】游戏维护状态:{}",pf.toString());
            if ("0".equals(pf.getPlatform_status())) {
                return BaseResponse.error("0", "process");
            }
            //查询平台游戏开关
            Map<String,String> cagentGameStatus = userGameTransferService.getPlatformStatusByCid(cid);
            if(CollectionUtils.isEmpty(cagentGameStatus)){
                logger.info("查询平台游戏开关状态为空");
                return BaseResponse.error("0", "process");
            }
            logger.info("查询平台游戏开关状态结果:{}",JSONObject.fromObject(cagentGameStatus).toString());
            if(cagentGameStatus.containsKey(type)){
                String cagentStatus = String.valueOf(cagentGameStatus.get(type));
                logger.info("用户【"+username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,查询平台【"+cagent+"】游戏开关状态:{}",cagentGameStatus);
                if(!"1".equals(cagentStatus)){
                    return BaseResponse.error("0", "process");
                }
            }else{
                logger.info("用户【"+username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,查询平台【"+cagent+"】游戏开关状态时游戏类型编码【"+type+"】,不存在");
                return BaseResponse.error("0","用户【"+username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,查询平台【"+cagent+"】游戏开关状态时游戏类型编码【"+type+"】,不存在");
            }

            //检查用户游戏状态
            JSONObject checkGameAccount = checkGameReg(uid, password, ag_username, hg_username, type, ip, pmap);
            if(!"success".equalsIgnoreCase(checkGameAccount.getString("status"))){
                //检查游戏游戏状态失败
                return checkGameAccount;
            }
            String userLock = uid.intern();
            synchronized (userLock) {
                // 用户钱包余额
                double balance = userGameTransferService.getUserBalance(Integer.valueOf(uid));
                logger.info("用户【"+ag_username+"】 转账查询用户余额结果:{}",balance);
                if (balance < credit) {
                    return transferResponse("06", "转账失败,用户余额不足");
                }
                // 生成订单号
                String billno = generatorOrderNo(type, ag_username, pmap);
                if (StringUtils.isBlank(billno)) {
                    return transferResponse("error", "创建订单号为空");
                }

                //扣除用户金额
                int i  = userGameTransferService.deductUserMoney(uid,Double.valueOf(credit));

                if(i > 0 ){

                    logger.info("用户【"+username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,生成订单号:{}",billno);
                    JSONObject transferResult = transferInProcess(type, ag_username, hg_username, username,billno, credit, password, ip, pmap);
                    logger.info("用户【"+username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,发起第三方请求结果:{}",transferResult.toString());
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
                    transfer.setIp(ip);
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
                logger.info("用户【"+username+"】 调用转账接口失败！扣除钱包金额异常！");
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

            //写入日志
            FileLog f = new FileLog();
            Map<String, String> param = new HashMap<>();
            param.put("loginname", uid);
            param.put("suuid", uuid);
            param.put("Function", "TransferTo");
            f.setLog("zhuanzhang---" + type, param);
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
    @RequestMapping("/TransferFrom")
    @ResponseBody
    public JSONObject transferOut(HttpSession session, HttpServletRequest request, HttpServletResponse response,
                                  int credit, String type, String uuid, String imgcode) {
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
        // 缓存KEY
        String key = CacheKeyConstants.GAME_TRANSFER_KEY_UID + uid;
        if(redisUtils.hasKey(key)){
            jo.put("msg", "05");
            jo.put("errmsg", type + "平台转账处理中,请稍后再试");
            return jo;
        }
        redisUtils.set(key, uid,5);
        try {
            StringBuffer url = request.getRequestURL();
            String refurl = url.delete(url.length() - request.getRequestURI().length(), url.length())
                    .append("/").toString();
            //从缓存中获取用户信息
            String ag_password = usermap.get("ag_password");
            String ag_username = usermap.get("ag_username");
            String hg_username = usermap.get("hg_username");
            String username = usermap.get("userName");//用户名称
            String cid = usermap.get("cid");//平台号
            String cagent = usermap.get("cagent");//平台编码
//            String suuid = formatObjectParams(session.getAttribute("uuid"));
            String simgcode = formatObjectParams(session.getAttribute("imgcode"));
            String ip = IPTools.getIp(request);
            //查询游戏配置信息
            Map<String,String> pmap = formatPlatformConfigByCagent();
            if(CollectionUtils.isEmpty(pmap)){
                logger.info("查询平台配置信息为空");
                return BaseResponse.error("0", "查询平台配置信息为空");
            }
            logger.info("查询平台配置信息结果:{}",JSONObject.fromObject(pmap).toString());
            // 校验请求参数
            JSONObject verifyData = verifyRequestParams(ag_password, ag_username, simgcode, type,
                    credit, uuid, imgcode);
            type = type.trim().toUpperCase();//格式化游戏编码
            if (!"success".equalsIgnoreCase(verifyData.getString("msg"))) {
                logger.info("用户【"+username+"】,调用游戏平台向天下平台转入金额(游戏下分)接口,校验请求参数结果:{}",verifyData.toString());
                return verifyData;
            }
            //解密
            DESEncrypt d = new DESEncrypt(KeyConstant.DESKEY);
            String password = d.decrypt(ag_password);
            // 检查维护状态
            PlatFromConfig pf = new PlatFromConfig();
            pf.InitData(pmap, type);
            logger.info("用户【"+username+"】,调用游戏平台向天下平台转入金额(游戏下分)接口,查询平台【"+cagent+"】游戏维护状态:{}",pf.toString());
            if ("0".equals(pf.getPlatform_status())) {
                return BaseResponse.error("0", "process");
            }
            //查询平台游戏开关
            Map<String,String> cagentGameStatus = userGameTransferService.getPlatformStatusByCid(cid);
            if(CollectionUtils.isEmpty(cagentGameStatus)){
                logger.info("查询平台游戏开关状态为空");
                return BaseResponse.error("0", "process");
            }
            logger.info("查询平台游戏开关状态结果:{}",JSONObject.fromObject(cagentGameStatus).toString());
            if(cagentGameStatus.containsKey(type)){
                String cagentStatus = String.valueOf(cagentGameStatus.get(type));
                logger.info("用户【"+username+"】,调用游戏平台向天下平台转入金额(游戏下分)接口,查询平台【"+cagent+"】游戏开关状态:{}",cagentGameStatus);
                if(!"1".equals(cagentStatus)){
                    return BaseResponse.error("0", "process");
                }
            }else{
                logger.info("用户【"+username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,查询平台【"+cagent+"】游戏开关状态时游戏类型编码【"+type+"】,不存在");
                return BaseResponse.error("0","用户【"+username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,查询平台【"+cagent+"】游戏开关状态时游戏类型编码【"+type+"】,不存在");
            }
            //检查用户游戏状态
            JSONObject checkGameAccount = checkGameReg(uid, password, ag_username, hg_username, type, ip, pmap);
            if(!"success".equalsIgnoreCase(checkGameAccount.getString("status"))){
                //检查游戏游戏状态失败
                return checkGameAccount;
            }
            String userLock = uid.intern();
            synchronized (userLock) {
                //查询用户游戏余额
                double gameBalance = getBalance(uid, password, ag_username, hg_username, type, ip, refurl, pmap).getDouble("balance");
                //判断游戏余额是否足够
                if (gameBalance < credit) {
                    return transferResponse("06", "转账失败,用户游戏余额不足");
                }
                // 用户钱包余额
                double balance = userGameTransferService.getUserBalance(Integer.valueOf(uid));

                // 生成订单号
                String billno = generatorOrderNo(type, ag_username, pmap);
                if (StringUtils.isBlank(billno)) {
                    return transferResponse("error", "创建订单号为空");
                }
                logger.info("用户【"+username+"】,调用游戏平台向天下平台转入金额(游戏下分)接口,生成订单号:{}",billno);
                JSONObject transferResult = transferOutProcess(type, ag_username, hg_username, username,billno, credit, password, ip, pmap);
                logger.info("用户【"+username+"】,调用游戏平台向天下平台转入金额(游戏下分)接口,发起第三方请求响应结果:{}",billno);
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
                transfer.setIp(ip);
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
            //写入日志
            FileLog f = new FileLog();
            Map<String, String> param = new HashMap<>();
            param.put("loginname", uid);
            param.put("suuid", uuid);
            param.put("Function", "TransferFrom");
            f.setLog("zhuanzhang---" + type, param);
            //清除缓存
            session.setAttribute("imgcode", "");
            session.removeAttribute("uuid");
        }
        return transferResponse("error", "调用从游戏平台转向天下平台转出金额(游戏下分)失败");
    }


    /**
     *
     * @Description 获取用户游戏余额
     * @param request
     * @param response
     * @param BType
     * @return
     */
    private JSONObject getBalance(String uid,String ag_password,String ag_username,String hg_username,
                                  String BType,String ip,String refurl,Map<String,String> pmap) throws Exception {
        JSONObject jo = new JSONObject();
        try {
            BType = GameTypeUtils.formartGameType(BType);

            if ("JDB".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_jdb");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                // 获取JDB余额
                JDBGameServiceImpl jdb = new JDBGameServiceImpl(pmap);
                String balance = jdb.getBalance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "0.00");
                    return jo;
                }
                jo.put("balance", balance);
                return jo;
            } else if ("AG".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ag");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                // 获取AG余额
                AGGameServiceImpl agService = new AGGameServiceImpl(pmap);
                String msg = agService.CheckOrCreateGameAccout(ag_username, ag_password, "A", "CNY");
                if ("0".equals(msg)) {
                    String balance = agService.GetBalance(ag_username, ag_password, "CNY");
                    if (balance == null || balance == "") {
                        jo.put("balance", "0.00");
                        return jo;
                    } else {
                        jo.put("balance", balance);
                        return jo;
                    }
                } else {
                    jo.put("balance", "0.00");
                    return jo;
                }
            } else if ("AGIN".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_agin");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                // 获取AG余额
                AGINGameServiceImpl agService = new AGINGameServiceImpl(pmap);
                String msg = agService.CheckOrCreateGameAccout(ag_username, ag_password, "A", "CNY");
                if ("0".equals(msg)) {
                    String balance = agService.GetBalance(ag_username, ag_password, "CNY");
                    if (balance == null || balance == "") {
                        jo.put("balance", "0.00");
                        return jo;
                    } else {
                        jo.put("balance", balance);
                        return jo;
                    }
                } else {
                    jo.put("balance", "0.00");
                    return jo;
                }
            } else if ("BBIN".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_bbin");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                // 获取BBIN余额
                BBINGameServiceImpl bbinService = new BBINGameServiceImpl(pmap);
                String msg = bbinService.CheckUsrBalance(ag_username, ag_password);
                JSONObject json;
                json = JSONObject.fromObject(msg);
                if ("true".equals(json.get("result").toString())) {
                    JSONArray jsonArray = JSONArray.fromObject(json.getString("data"));
                    json = jsonArray.getJSONObject(0);
                    jo.put("balance", json.get("Balance").toString());
                } else {
                    jo.put("balance", "0.00");
                    return jo;
                }
            } else if ("DS".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ds");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                DSGameServiceImpl ds = new DSGameServiceImpl(pmap);
                String msg = ds.getBalance(ag_username, ag_password);
                JSONObject json;
                json = JSONObject.fromObject(msg);
                if ("0".equals(json.get("errorCode").toString())) {
                    json = json.getJSONObject("params");
                    jo.put("balance", json.get("balance").toString());
                } else {
                    jo.put("balance", "0.00");
                    return jo;
                }
            } else if ("OB".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ob");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                OBService ob = new OBGameServiceImpl(pmap);
                String msg = ob.get_balance(ag_username, ag_password);
                JSONObject json;
                json = JSONObject.fromObject(msg);
                if ("OK".equals(json.get("error_code").toString())) {
                    jo.put("balance", json.get("balance").toString());
                } else {
                    jo.put("balance", "0.00");
                    return jo;
                }
            } else if ("OG".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_og");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                OGGameServiceImpl og = new OGGameServiceImpl(pmap);
                String a = og.getBalance(ag_username, ag_password);
                try {
                    String str = a.substring(a.indexOf("<result>") + 8, a.indexOf("</result>"));
                    jo.put("balance", str);
                } catch (Exception e) {
                    jo.put("balance", "0.00");
                    return jo;
                }
            } else if ("SB".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_shenbo");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                SBService s = new SBGameServiceImpl(pmap);
                String atoken = s.getAccToken();
                JSONObject json = new JSONObject();
                json = JSONObject.fromObject(atoken);
                try {
                    atoken = json.get("access_token").toString();
                    String j = s.getBalance(ag_username, atoken);
                    json = JSONObject.fromObject(j);
                    if (json.get("bal").toString() == null || json.get("bal").toString() == "") {
                        jo.put("balance", "0.00");
                        return jo;
                    }
                    jo.put("balance", json.get("bal").toString());
                } catch (Exception e) {
                    jo.put("balance", "0.00");
                    return jo;
                }
            } else if ("MG".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_mg");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                MGGameServiceImpl m = new MGGameServiceImpl(pmap);
                Map<String, String> gmap = new HashMap<String, String>();
                gmap.put("ClientIP", ip);
                JSONObject json = m.queryBalance(ag_username, ag_password, gmap);
                try {
                    if ("success".equals(json.get("Code").toString())) {
                        jo.put("balance", json.get("Balance").toString());
                    } else {
                        jo.put("balance", "0.00");
                    }
                } catch (Exception e) {
                    jo.put("balance", "0.00");
                }

            } else if ("HABA".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_haba");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                HABAGameServiceImpl h = new HABAGameServiceImpl(pmap);
                QueryPlayerResponse qp = h.queryPlayer(ag_username, ag_password, null);
                if (qp.isFound() == true) {
                    jo.put("balance", qp.getRealBalance());
                } else {
                    jo.put("balance", "0.00");
                }
            } else if ("PT".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_pt");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                PTGameServiceImpl p = new PTGameServiceImpl(pmap);
                try {
                    JSONObject json = JSONObject.fromObject(p.GetPlayerInfo(ag_username));
                    json = json.getJSONObject("result");
                    if (json == null || "".equals(json.toString())) {
                        jo.put("balance", "0.00");
                    } else {
                        String balance = json.getString("BALANCE").toString();
                        jo.put("balance", balance);
                    }
                } catch (Exception e) {
                    jo.put("balance", "0.00");
                    return jo;
                }

            } else if ("GGBY".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ggby");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                GGBYGameServiceImpl gg = new GGBYGameServiceImpl(pmap);
                try {
                    String msg = gg.GetBalance(ag_username, ag_password);
                    jo.put("balance", msg);
                } catch (Exception e) {
                    jo.put("balance", "0.00");
                    return jo;
                }

            } else if ("CG".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_cg");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                CGGameServiceImpl c = new CGGameServiceImpl(pmap);
                String msg = c.getBalance(ag_username, ag_password);
                JSONObject json;
                json = JSONObject.fromObject(msg);
                if ("0".equals(json.get("errorCode").toString())) {
                    json = json.getJSONObject("params");
                    jo.put("balance", json.get("balance").toString());
                } else {
                    jo.put("balance", "0.00");
                    return jo;
                }
            } else if ("IG".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ig");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                IGGameServiceImpl c = new IGGameServiceImpl(pmap,ag_username.substring(0,3));
                String msg = c.getBalance(ag_username, ag_password);
                JSONObject json;
                json = JSONObject.fromObject(msg);
                if ("0".equals(json.get("errorCode").toString())) {
                    json = json.getJSONObject("params");
                    jo.put("balance", json.get("balance").toString());
                } else {
                    jo.put("balance", "0.00");
                    return jo;
                }
            } else if ("IGPJ".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_igpj");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                IGPJGameServiceImpl c = new IGPJGameServiceImpl(pmap,ag_username.substring(0,3));
                String msg = c.getBalance(ag_username, ag_password);
                JSONObject json;
                json = JSONObject.fromObject(msg);
                if ("0".equals(json.get("errorCode").toString())) {
                    json = json.getJSONObject("params");
                    jo.put("balance", json.get("balance").toString());
                } else {
                    jo.put("balance", "0.00");
                    return jo;
                }
            } else if ("HG".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_hg");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                XHGServiceImpl c = new XHGServiceImpl(pmap);
                String msg = c.getBalance(hg_username);
                if (!"error".equals(msg)) {
                    jo.put("balance", msg);
                } else {
                    jo.put("balance", "0.00");
                    return jo;
                }
            } else if ("BG".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_bg");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                BGGameServiceImpl c = new BGGameServiceImpl(pmap);
                JSONObject jsonObjec = c.openUserCommonAPI(ag_username, "open.balance.get", "", "", refurl);
                if ("success".equals(jsonObjec.get("code"))) {
                    jo.put("balance", JSONObject.fromObject(jsonObjec.get("params")).get("result"));
                } else {
                    jo.put("balance", "0.00");
                    return jo;
                }
            } else if ("VR".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_vr");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                VRGameServiceImpl v = new VRGameServiceImpl(pmap);
                String balance = v.getBalance(ag_username);
                try {
                    BigDecimal big = new BigDecimal(balance);
                    if (big.compareTo(BigDecimal.ZERO) < 0) {
                        balance = "0.0";
                    }
                } catch (Exception e) {
                    balance = "0.0";
                }
                jo.put("balance", balance);
                return jo;
            } else if ("JF".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_jf");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                JFGameServiceImpl jf = new JFGameServiceImpl(pmap);
                String balance = jf.GetBalance(ag_username, ag_password);
                jo.put("balance", balance);
                return jo;
            } else if ("KYQP".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_kyqp");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                KYQPGameServiceImpl k = new KYQPGameServiceImpl(pmap);
                String balance = k.queryUnderTheBalance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "0.00");
                } else {
                    balance = JSONObject.fromObject(balance).getJSONObject("d").getString("money");
                    jo.put("balance", balance);
                }
                return jo;
            } /*
             * else if ("ESW".equals(BType)) { Map<String, Object> param = new HashMap<>(); param.put("uid", uid);
             * param.put("gametype", "is_esw"); Map<String, String> user =
             * userService.selectUserGameStatus(param).get(0); Object o = user.get("cnt"); if
             * ("0".equals(o.toString())) { jo.put("balance", "0"); return jo; } ESWServiceImpl eswService = new
             * ESWServiceImpl(pmap); String data = eswService.queryUserInfo(ag_username); JSONObject jsonObject =
             * JSONObject.fromObject(data); if (jsonObject.getInt("code") == 0) { jo.put("balance",
             * jsonObject.getString("money")); } else { if(jsonObject.getInt("code") == 1012){ jo.put("balance",
             * "userCode有误，在平台服务器找不到对应的用户"); }else { jo.put("balance", "维护中"); } } return jo; }
             */ else if ("LYQP".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_lyqp");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                LYQPGameServiceImpl lyqp = new LYQPGameServiceImpl(pmap);
                String balance = lyqp.queryUnderTheBalance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "0.00");
                } else {
                    balance = JSONObject.fromObject(balance).getJSONObject("d").getString("money");
                    jo.put("balance", balance);
                }
                return jo;
            } else if ("VG".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_vg");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                VGGameServiceImpl vg = new VGGameServiceImpl(pmap);
                String balance = vg.balance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "0.00");
                } else {
                    jo.put("balance", balance);
                }
                return jo;

            } else if ("GY".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_gy");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                GYGameServiceImpl gy = new GYGameServiceImpl(pmap);
                String balance = gy.balance(ag_username, ag_password);
                if ("error".equals(balance)) {
                    jo.put("balance", "0.00");
                } else {
                    jo.put("balance", balance);
                }
                return jo;
            } else if ("PS".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ps");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                PSGameServiceImpl ps = new PSGameServiceImpl(pmap);
                String balance = ps.balance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "0.00");
                } else {
                    jo.put("balance", balance);
                }
                return jo;
            } else if ("NB".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_nb");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                NBGameServiceImpl nb = new NBGameServiceImpl(pmap);
                String balance = nb.balance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "0.00");
                } else {
                    jo.put("balance", balance);
                }
                return jo;
            } else if ("SW".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_sw");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                SWGameServiceImpl sw = new SWGameServiceImpl(pmap);
                String balance = sw.getBalance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "0.00");
                } else {
                    jo.put("balance", balance);
                }
                return jo;
            } else if ("CQJ".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_cqj");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                CQJServiceimpl cqj = new CQJServiceimpl(pmap);
                String str = cqj.findAccount(ag_username);
                if ("error".equals(str)) {
                    jo.put("balance", "维护中");
                } else {
                    jo.put("balance", str);
                }
            } else if ("ESW".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_esw");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                ESWServiceImpl eswService = new ESWServiceImpl(pmap);
                String data = eswService.queryUserInfo(ag_username);
                JSONObject jsonObject = JSONObject.fromObject(data);
                if (jsonObject.getInt("code") == 0) {
                    jo.put("balance", jsonObject.getString("money"));
                } else {
                    jo.put("balance", "0");
                }
                return jo;
            } else if ("IBC".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ibc");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                IBCGameServiceImpl service = new IBCGameServiceImpl(pmap);
                ResultResponse data = service.getBalance(ag_username);
                if (data.getStatus() == ResponseCode.SUCCESS_STATUS) {
                    jo.put("balance", data.getBalance());
                } else {
                    jo.put("balance", "0");
                }
                return jo;
            }else if("IM".equals(BType)){
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_im");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                IMONEGameServiceImpl imoneGameService = new IMONEGameServiceImpl(pmap);
                String balance = imoneGameService.getBalance(ag_username);
                if(balance != "error"){
                    jo.put("balance", balance);
                }else{
                    jo.put("balance", "0");
                }
            }else if("NWG".equals(BType)) {
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_nwg");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    jo.put("balance", "0");
                    return jo;
                }
                NWGGameServiceImpl nwgGameService = new NWGGameServiceImpl(pmap);
                String balance = nwgGameService.getBalance(ag_username);
                jo.put("balance", balance.equals("error")?"0":balance);
                return jo;
            }else {
                jo.put("balance", "0.00");
                return jo;
            }
        } catch (Exception e) {
            jo.put("balance", "0.00");
            return jo;
        }
        return jo;
    }


    /**
     *
     * @Description 检查用户游戏状态
     * @param uid 用户ID
     * @param ag_password 游戏登录密码(密码为解密后的)
     * @param ag_username
     * @param hg_username
     * @param type
     * @param ip
     * @param pmap
     * @throws Exception
     */
    private JSONObject checkGameReg(String uid,String ag_password,String ag_username,String hg_username,
                                    String type,String ip,Map<String,String> pmap) throws Exception {
        logger.info("用户【"+ag_username+"】调用检查或创建游戏账号接口开始======================START=======================");
        try {
            type = GameTypeUtils.formartGameType(type);
            if ("AGIN".equals(type)){
                String msg = "";
                // 查询用户是否在平台注册
                AGINGameServiceImpl agService = new AGINGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_agin");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    // 如果未注册AG平台,注册AG用户,并记录
                    msg = agService.CheckOrCreateGameAccout(ag_username, ag_password, "A", "CNY");
                    if ("0".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("AG".equals(type)) {
                String msg = "";
                // 查询用户是否在平台注册
                AGGameServiceImpl agService = new AGGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ag");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    // 如果未注册AG平台,注册AG用户,并记录
                    msg = agService.CheckOrCreateGameAccout(ag_username, ag_password, "A", "CNY");
                    if ("0".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("BBIN".equals(type)) {
                String msg = "";
                JSONObject json = new JSONObject();
                BBINGameServiceImpl b = new BBINGameServiceImpl(pmap);

                // 查询用户是否在平台注册,如果未注册则注册用户,并记录
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_bbin");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = b.CreateMember(ag_username, ag_password);
                    json = JSONObject.fromObject(msg);
                    if ("true".equals(json.getString("result")) || msg.indexOf("The account is repeated") > -1) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("DS".equals(type)) {
                String msg = "";
                JSONObject json = new JSONObject();
                DSGameServiceImpl ds = new DSGameServiceImpl(pmap);

                // 查询用户是否在平台注册,如果未注册则注册用户,并记录
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ds");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = ds.LoginGame(ag_username, ag_password);
                    json = JSONObject.fromObject(msg);
                    if ("0".equals(json.getString("errorCode"))) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }

            } else if ("OB".equals(type)) {
                String msg = "";
                OBService ob = new OBGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ob");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = ob.check_or_create(ag_username, ag_password);
                    if ("success".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("OG".equals(type)) {
                String msg = "";
                OGGameServiceImpl og = new OGGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_og");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = og.CreateMem(ag_username, ag_password);
                    msg = msg.substring(msg.indexOf("<result>") + 8, msg.indexOf("</result>"));
                    if ("1".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("SB".equals(type)) {
                SBService s = new SBGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_shenbo");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    // 设置盘口 默认设置申博默认盘口1-5
                    UserTypeHandicapUtil uth = new UserTypeHandicapUtil();
                    String handicap = uth.getHandicap("SB", uid, userService);
                    handicap = handicap.isEmpty() ? "4" : handicap;

                    String atoken = s.getAccToken();
                    JSONObject json = new JSONObject();
                    json = JSONObject.fromObject(atoken);
                    atoken = s.getUserToken(ip, ag_username, ag_username,
                            json.get("access_token").toString(), handicap, "");
                    json = JSONObject.fromObject(atoken);
                    if (!"".equals(json.getString("authtoken"))) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("MG".equals(type)) {
                JSONObject json = new JSONObject();
                MGGameServiceImpl m = new MGGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_mg");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    json = m.createAccount(ag_username, ag_password, null);
                    if ("success".equals(json.getString("Code"))) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }

            } else if ("PT".equals(type)) {
                String msg = "";
                PTGameServiceImpl p = new PTGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_pt");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = p.CreatePlayer(ag_username, ag_password);
                    if ("success".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("HABA".equals(type)) {
                HABAGameServiceImpl h = new HABAGameServiceImpl(pmap);
                Map<String, Object> params = new HashMap<String, Object>();
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_haba");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    LoginUserResponse lu = h.loginOrCreatePlayer(ag_username, ag_password, params);
                    if (lu.isPlayerCreated() || lu.getMessage() == null || "null".equals(lu.getMessage())) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("JDB".equals(type)) {
                JDBGameServiceImpl jdb = new JDBGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_jdb");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    String msg = jdb.createUser(ag_username);
                    if ("success".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("GGBY".equals(type)) {
                String msg = "";
                GGBYGameServiceImpl gg = new GGBYGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ggby");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = gg.CheckOrCreateGameAccout(ag_username, ag_password);
                    if ("0".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }

            } else if ("CG".equals(type)) {
                String msg = "";
                JSONObject json = new JSONObject();
                CGGameServiceImpl c = new CGGameServiceImpl(pmap);
                // 查询用户是否在平台注册,如果未注册则注册用户,并记录
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_cg");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = c.LoginGame(ag_username, ag_password);
                    json = JSONObject.fromObject(msg);
                    if ("0".equals(json.getString("errorCode"))) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("IG".equals(type)) {
                String msg = "";
                JSONObject json = new JSONObject();
                IGGameServiceImpl c = new IGGameServiceImpl(pmap,ag_username.substring(0,3));
                // 查询用户是否在平台注册,如果未注册则注册用户,并记录
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ig");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    // 首次登录默认设置A盘口
                    msg = c.LoginGame(ag_username, ag_password, "LOTTERY", "", "PC", "A");
                    if (msg != null && !"null".equals(msg)) {
                        json = JSONObject.fromObject(msg);
                        if ("0".equals(json.getString("errorCode"))) {
                            userService.insertUserGameStatus(param);
                        }else{
                            logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                            return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        }
                    }
                }
            } else if ("IGPJ".equals(type)) {
                String msg = "";
                JSONObject json = new JSONObject();
                IGPJGameServiceImpl c = new IGPJGameServiceImpl(pmap,ag_username.substring(0,3));
                // 查询用户是否在平台注册,如果未注册则注册用户,并记录
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_igpj");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    // 首次登录游戏默认设置 A盘口
                    msg = c.LoginGame(ag_username, ag_password, "LOTTERY", "", "PC", "A");
                    if (msg != null && !"null".equals(msg)) {
                        json = JSONObject.fromObject(msg);
                        if ("0".equals(json.getString("errorCode"))) {
                            userService.insertUserGameStatus(param);
                        }else{
                            logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                            return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        }
                    }
                }
            } else if ("HG".equals(type)) {
                String msg = "";
                XHGServiceImpl xhgService = new XHGServiceImpl(pmap);
                // 查询用户是否在平台注册,如果未注册则注册用户,并记录
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_hg");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = xhgService.getLogin(hg_username, "");
                    if (!"error".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("BG".equals(type)) {
                JSONObject json = new JSONObject();
                BGGameServiceImpl m = new BGGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_bg");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    json = m.openUserCreate(ag_username, ag_password, "open.user.create");
                    if ("success".equals(json.getString("code")) || json.toString().indexOf("登录名(loginId)已存在") > 0) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }

            } else if ("VR".equals(type)) {
                String msg = "";
                VRGameServiceImpl v = new VRGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_vr");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = v.CreateUser(ag_username);
                    if ("success".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("JF".equals(type)) {
                String msg = "";
                JSONObject json = new JSONObject();
                JFGameServiceImpl jf = new JFGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_jf");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = jf.CreateUser(ag_username, ag_password, ag_username);
                    json = JSONObject.fromObject(msg);
                    if (json.getString("Success").equals("true") && json.getString("Code").equals("1")) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("KYQP".equals(type)) {
                String msg = "";
                JSONObject json = new JSONObject();
                KYQPGameServiceImpl k = new KYQPGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_kyqp");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = k.checkOrCreateGameAccout(ag_username,ip, "0");
                    json = JSONObject.fromObject(msg);
                    if ("0".equals(json.getJSONObject("d").get("code") + "")) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("LYQP".equals(type)) {
                String msg = "";
                JSONObject json = new JSONObject();
                LYQPGameServiceImpl lyqp = new LYQPGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_lyqp");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = lyqp.checkOrCreateGameAccout(ag_username,ip, "0");
                    json = JSONObject.fromObject(msg);
                    if ("0".equals(json.getJSONObject("d").get("code") + "")) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("VG".equals(type)) {
                String msg = "";
                VGGameServiceImpl vg = new VGGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_vg");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = vg.createUser(ag_username);
                    if ("success".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("GY".equals(type)) {
                String msg = "";
                GYGameServiceImpl c = new GYGameServiceImpl(pmap);
                // 查询用户是否在平台注册,如果未注册则注册用户,并记录
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_gy");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = c.createUser(ag_username, ag_password);
                    if (!"error".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("PS".equals(type)) {
                String msg = "";
                PSGameServiceImpl ps = new PSGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ps");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = ps.createUser(ag_username);
                    if ("success".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("NB".equals(type)) {
                String msg = "";
                NBGameServiceImpl nb = new NBGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_nb");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = nb.createUser(ag_username);
                    if ("success".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("SW".equals(type)) {
                String msg = "";
                SWGameServiceImpl sw = new SWGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_sw");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    msg = sw.createUser(ag_username);
                    if ("success".equals(msg)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            }else if ("IBC".equals(type)) {
                IBCGameServiceImpl ibcService = new IBCGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_ibc");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                TransferVO transferVO = new TransferVO();
                transferVO.setAccount(ag_username);
                if ("0".equals(o.toString())) {
                    ResultResponse response = ibcService.CheckOrCreateGameAccout(transferVO);
                    if (response.getStatus() == 1) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("CQJ".equals(type)) {
                CQJServiceimpl cqjServiceimpl = new CQJServiceimpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_cqj");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                TransferVO transferVO = new TransferVO();
                transferVO.setAccount(ag_username);
                transferVO.setPassword(ag_password);
                if ("0".equals(o.toString())) {
                    boolean msgs = cqjServiceimpl.checkOrCreateGameAccout(transferVO);
                    if (msgs) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }

            }else if ("ESW".equals(type)) {
                ESWServiceImpl eswServiceimpl = new ESWServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_esw");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                TransferVO transferVO = new TransferVO();
                transferVO.setAccount(ag_username);
                if ("0".equals(o.toString())) {
                    EswLoginVo vo = new EswLoginVo();
                    vo.setUserCode(ag_username);
                    String data = eswServiceimpl.checkOrCreateGameAccout(vo);
                    JSONObject jsonObject = JSONObject.fromObject(data);
                    if (jsonObject.getInt("code") == 0) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            }else if("IM".equals(type)){
                IMONEGameServiceImpl imoneGameService = new IMONEGameServiceImpl(pmap);
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_im");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if ("0".equals(o.toString())) {
                    GameCheckOrCreateVO gameCheckOrCreateVO = new GameCheckOrCreateVO();
                    gameCheckOrCreateVO.setGamename(ag_username);
                    gameCheckOrCreateVO.setPassword(ag_password);
                    if (imoneGameService.checkOrCreateAccount(gameCheckOrCreateVO)) {
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            }else if("NWG".equals(type)){
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                param.put("gametype", "is_nwg");
                Map<String, String> user = userService.selectUserGameStatus(param).get(0);
                Object o = user.get("cnt");
                if("0".equals(o.toString())){
                    NWGGameServiceImpl nwgGameService = new NWGGameServiceImpl(pmap);
                    GameForwardVO gameForwardVO = new GameForwardVO();
                    gameForwardVO.setAg_username(ag_username);
                    gameForwardVO.setPassword(ag_password);
                    gameForwardVO.setGameId("620");
                    gameForwardVO.setIp(ip);
                    String params = nwgGameService.checkOrCreateAccount(gameForwardVO);
                    if(params != "error"){
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }

                }

            }
            logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号成功");
            return BaseResponse.success("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("用户【"+ag_username+"】调用检查或创建游戏账号接口异常:{}",e.getMessage());
            return transferResponse("error","调用检查或创建游戏账号接口异常");
        }
    }



    /**
     * @Description 保存转账信息
     * @param data
     * @param type
     *            1 转出(游戏上分) 2 转入(游戏下分)
     */
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

    /**
     * @Description 格式化obj参数
     * @param obj
     * @return
     */
    private String formatObjectParams(Object obj) {
        if (ObjectUtils.anyNotNull(obj)) {
            return String.valueOf(obj);
        }
        return null;
    }

    private JSONObject verifyRequestParams(String ag_password, String ag_username, String simgcode,
                                           String type, Integer credit, String uuid, String imgcode) throws Exception {
        try {
            if (StringUtils.isBlank(ag_username)) {
                return transferResponse("error", "用户游戏登录账号不能为空");
            }

            if (StringUtils.isBlank(ag_password)) {
                return transferResponse("error", "用户游戏登录密码不能为空");
            }

            if (StringUtils.isBlank(type)) {
                return transferResponse("01", "请求参数平台编码不能为空");
            }

            if (credit == null || credit < 1 || credit > 100000000) {
                return transferResponse("02", "请输入合法金额,大于【1】且小于【100000000】之间的金额");
            }

//            if (StringUtils.isBlank(suuid)) {
//                return transferResponse("error", "用户UUID不能为空");
//            }
//
//            if (StringUtils.isBlank(uuid)) {
//                return transferResponse("error", "请求参数uuid不能为空");
//            }
//
//            if (!suuid.equalsIgnoreCase(uuid)) {
//                return transferResponse("error", "非法用户,请重新登录");
//            }

            if (StringUtils.isNotBlank(simgcode)) {
                if (StringUtils.isBlank(imgcode)) {
                    return transferResponse("04", "验证不能空,请输入验证码");
                }

                if (!simgcode.equalsIgnoreCase(imgcode)) { // 忽略验证码大小写
                    return transferResponse("04", "验证码不正确,请输入正确的验证码");
                }
            }
            return transferResponse("success", "校验转账请求参数成功");
        } catch (Exception e) {
            logger.info("校验转账参数异常:{}", e.getMessage());
            return transferResponse("error", "校验转账请求参数异常");
        }
    }

    /**
     * @Description 生成订单号
     * @param type
     * @return
     * @throws Exception
     */
    private synchronized String generatorOrderNo(String type, String ag_username, Map<String, String> pmap) throws Exception {
        try {
            //同步方法随机休眠5毫秒，以生成不同的订单号
            Random interval = new Random();
            Thread.sleep(interval.nextInt(9));
            int randInt = (int)((Math.random()*9+1)*1000); // 4位随机数
            String billno = type + System.currentTimeMillis() + randInt;
            switch (type) {
                case "BBIN":
                case "BG":
                    //转帐序号(唯一值)，可用贵公司转帐纪录的流水号，以避免重覆转帐< 请用int(19)( 1~9223372036854775806)来做设定 >
                    billno = String.valueOf(System.currentTimeMillis()) + randInt;//生成一个18位字符串
                    break;
                case "OB":
                    billno = String.valueOf(System.currentTimeMillis());
                    break;
                case "GGBY":{
                    PlatFromConfig pf = new PlatFromConfig();
                    pf.InitData(pmap, "GGBY");
                    JSONObject json = JSONObject.fromObject(pf.getPlatform_config());
                    String cagent = json.getString("cagent");
                    billno = cagent + System.currentTimeMillis() + (int)((Math.random()*9+1)*100);
                    break;
                }
                case "KYQP": {
                    PlatFromConfig pf = new PlatFromConfig();
                    pf.InitData(pmap, "KYQP");
                    JSONObject json = JSONObject.fromObject(pf.getPlatform_config());
                    String api_cagent = json.getString("api_cagent");
                    billno = api_cagent + simpleDateFormat.format(new Date()) + ag_username;
                    break;
                }
                case "LYQP": {
                    PlatFromConfig pf = new PlatFromConfig();
                    pf.InitData(pmap, "LYQP");
                    JSONObject json = JSONObject.fromObject(pf.getPlatform_config());
                    String api_cagent = json.getString("api_cagent");
                    billno = api_cagent + simpleDateFormat.format(new Date()) + ag_username;
                    break;
                }
                case "IM": {
                    billno = "IM"+ UUID.randomUUID();
                    break;
                }
                case "NWG":{
                    PlatFromConfig pf = new PlatFromConfig();
                    pf.InitData(pmap, type);
                    JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());
                    billno = jo.getString("agent") + simpleDateFormat.format(new Date()) + ag_username;
                    break;
                }
            }
            return billno;
        } catch (Exception e) {
            logger.info("生成转账订单号异常:{}", e.getMessage());
            throw new Exception("生成转账订单号异常");
        }
    }

    /**
     * @Description 封装返回结果
     * @param code
     * @param message
     * @return
     */
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

    /**
     * @Description 操作天下平台向游戏平台转入金额业务(游戏上分)
     * @param type
     *            平台编码
     * @throws Exception
     */
    private JSONObject transferInProcess(String type, String ag_username, String hg_username, String username,String billno, int credit,
                                         String ag_password, String ip, Map<String, String> data) throws Exception {
        logger.info("用户:【"+username+"】,订单号:【"+billno+"】,操作天下平台向游戏平台:【"+type+"】转入金额业务(游戏上分)业务开始=============START===============");
        try {
            type = GameTypeUtils.formartGameType(type);

            GameTransferVO gameTransferVO = new GameTransferVO();
            //gameTransferVO.setUid(uid);
            gameTransferVO.setAg_username(ag_username);
            gameTransferVO.setBillno(billno);
            gameTransferVO.setHg_username(hg_username);
            gameTransferVO.setIp(ip);
            gameTransferVO.setMoney(String.valueOf(credit));
            gameTransferVO.setPassword(ag_password);
            gameTransferVO.setType("IM");
            gameTransferVO.setUsername(username);
            gameTransferVO.setConfig(JSONObject.fromObject(data));
            GameInterfaceService service = GameProxyFactory.productGameService(type);
            return service.transferIn(gameTransferVO);

        } catch (Exception e) {
            logger.info("用户:【"+username+"】,订单号:【"+billno+"】,操作天下平台向游戏平台:【"+type+"】转入金额业务(游戏上分)业务异常:{}", e.getMessage());
            logger.info("用户：【"+username+"】,订单号：【"+billno+"】，转账异常！",e);
            return transferResponse("process", "用户:【"+username+"】,订单号:【"+billno+"】,操作天下平台向游戏平台:【"+type+"】转入金额业务(游戏上分)业务失败");
        }
    }



    /**
     *
     * @Description 操作游戏平台向天下平台转入金额业务(游戏下分)
     * @param type
     * @param ag_username
     * @param hg_username
     * @param billno
     * @return
     */
    private JSONObject transferOutProcess(String type, String ag_username, String hg_username, String username, String billno, int credit,
                                          String ag_password, String ip, Map<String, String> data)throws Exception{

        logger.info("用户【"+username+"】,订单号:【"+billno+"】,游戏平台【" + type + "】向天下平台转入金额业务(游戏下分)业务开始=============START===============");
        try {
            type = GameTypeUtils.formartGameType(type);
            GameTransferVO gameTransferVO = new GameTransferVO();
            //gameTransferVO.setUid(uid);
            gameTransferVO.setAg_username(ag_username);
            gameTransferVO.setBillno(billno);
            gameTransferVO.setHg_username(hg_username);
            gameTransferVO.setIp(ip);
            gameTransferVO.setMoney(String.valueOf(credit));
            gameTransferVO.setPassword(ag_password);
            gameTransferVO.setType(type);
            gameTransferVO.setUsername(username);
            gameTransferVO.setConfig(JSONObject.fromObject(data));
            GameInterfaceService service = GameProxyFactory.productGameService(type);
            return service.transferOut(gameTransferVO);
        } catch (Exception e) {
            logger.info("用户【"+username+"】,订单号:【"+billno+"】,游戏平台【" + type + "】向天下平台转入金额业务(游戏下分)业务异常:{}",e.getMessage());
            return transferResponse("faild", "用户【"+username+"】游戏平台【" + type + "】向天下平台转入金额业务(游戏下分)业务失败");
        }
    }


    /**
     *
     * @Description 根据平台选择不同的配置信息
     * @param cagent
     * @return
     */
    private Map<String,String> formatPlatformConfigByCagent(){
        Map<String,String> data = new HashMap<>();
        try {
            String jsonConfig = redisUtils.get(KeyConstant.GAME_CONFIG);
            if(jsonConfig !=null) return JSON.parseObject(jsonConfig, Map.class);
            Map<String,String> pmap = userGameTransferService.getPlatformConfig();
            if(!CollectionUtils.isEmpty(pmap)){
                //通过平台编码获取配置信息
                Iterator<String> iterator = pmap.keySet().iterator();
                while(iterator.hasNext()){
                    String key = iterator.next();
                    String val = pmap.get(key);
                    if(StringUtils.isNotBlank(val)){
                        data.put(key,val);
                    }
                }
                redisUtils.set(KeyConstant.GAME_CONFIG, JSONObject.fromObject(data).toString(),7200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("获取平台游戏配置信息异常:{}",e.getMessage());
        }
        return data;
    }
}
