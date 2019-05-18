package com.cn.tianxia.api.web.v2;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cn.tianxia.api.game.impl.*;
import net.sf.json.JsonConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.common.v2.KeyConstant;
import com.cn.tianxia.api.game.OBService;
import com.cn.tianxia.api.game.SBService;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.po.v2.ResponseCode;
import com.cn.tianxia.api.po.v2.ResultResponse;
import com.cn.tianxia.api.service.UserService;
import com.cn.tianxia.api.service.v2.NewUserService;
import com.cn.tianxia.api.service.v2.UserGameTransferService;
import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.PTDes;
import com.cn.tianxia.api.utils.PlatFromConfig;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.utils.UserTypeHandicapUtil;
import com.cn.tianxia.api.vo.EswLoginVo;
import com.cn.tianxia.api.vo.v2.GameCheckOrCreateVO;
import com.cn.tianxia.api.vo.v2.GameForwardVO;
import com.cn.tianxia.api.vo.v2.TransferVO;
import com.cn.tianxia.api.web.BaseController;
import com.cn.tianxia.api.ws.LoginUserResponse;
import com.cn.tianxia.api.ws.QueryPlayerResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static cn.hutool.http.ContentType.JSON;

/**
 * @ClassName NewUserController
 * @Description 用户接口
 * @author Hardy
 * @Date 2019年2月7日 下午3:57:48
 * @version 1.0.0
 */
@Controller
@RequestMapping("User")
public class NewUserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserGameTransferService userGameTransferService;

    @Autowired
    private NewUserService newUserService;

    @Autowired
    private RedisUtils redisUtils;

    @RequestMapping("/getBalance")
    @ResponseBody
    public JSONObject getBalance(HttpServletRequest request, HttpServletResponse response, String BType) {
        logger.info("调用查询用户钱包余额接口开始====================START=======================");
        JSONObject jo = new JSONObject();
        try {
            Map<String, String> map = getUserInfoMap(redisUtils, request);
            if (CollectionUtils.isEmpty(map)) {
                logger.info("用户ID为空,登录已过期,请重新登录");
                jo.put("balance", "用户ID为空,登录已过期,请重新登录");
                return jo;
            }
            String uid = map.get("uid");
            // 判断请求参数
            if (StringUtils.isBlank(BType)) {
                jo.put("balance", "请求参数异常:查询余额类型不能为空");
                return jo;
            }
            // 获取转账请求参数
            String ag_password = map.get("ag_password");// 游戏密码
            String ag_username = map.get("ag_username");
            String hg_username = map.get("hg_username");
            String username = map.get("userName");
            String cagent = map.get("cagent");
            // 解密密码
            DESEncrypt d = new DESEncrypt(KeyConstant.DESKEY);
            ag_password = d.decrypt(ag_password);
            // 获取平台游戏配置信息
            Map<String, String> pmap = formatPlatformConfigByCagent(cagent.toUpperCase());
            if (CollectionUtils.isEmpty(pmap)) {
                logger.info("查询平台配置信息为空");
                jo.put("balance", "维护中");
                return jo;
            }
            // 检查维护状态
            if (!"WALLET".equals(BType)) {
                PlatFromConfig pf = new PlatFromConfig();
                pf.InitData(pmap, BType);
                if ("0".equals(pf.getPlatform_status())) {
                    jo.put("balance", "维护中");
                    return jo;
                }
            }
            if ("WALLET".equals(BType)) {
                // 获取数据库用户余额信息
                Map<String, Object> param = new HashMap<>();
                param.put("uid", uid);
                Map<String, Object> balanceMap = userService.selectUserById(param);
                jo.put("balance", balanceMap.get("wallet").toString());
                // 账号异常锁定账号
                double balance = Double.parseDouble(balanceMap.get("wallet").toString());
                if (balance < 0) {
                    Map<String, Object> umap = new HashMap<>();
                    umap.put("userName", username);
                    umap.put("is_stop", "1");
                    userService.updateGame(umap);
                }
                return jo;
            } else if ("JDB".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                // 获取JDB余额
                JDBGameServiceImpl jdb = new JDBGameServiceImpl(pmap);
                String balance = jdb.getBalance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "维护中");
                    return jo;
                }
                jo.put("balance", balance);
                return jo;
            } else if ("AG".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                // 获取AG余额
                AGGameServiceImpl agService = new AGGameServiceImpl(pmap);
                String msg = agService.CheckOrCreateGameAccout(ag_username, ag_password, "A", "CNY");
                if ("0".equals(msg)) {
                    String balance = agService.GetBalance(ag_username, ag_password, "CNY");
                    if (balance == null || balance == "") {
                        jo.put("balance", "维护中");
                        return jo;
                    } else {
                        jo.put("balance", balance);
                        return jo;
                    }
                } else {
                    jo.put("balance", "维护中");
                    return jo;
                }
            } else if ("AGIN".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                // 获取AG余额
                AGINGameServiceImpl agService = new AGINGameServiceImpl(pmap);
                String msg = agService.CheckOrCreateGameAccout(ag_username, ag_password, "A", "CNY");
                if ("0".equals(msg)) {
                    String balance = agService.GetBalance(ag_username, ag_password, "CNY");
                    if (balance == null || balance == "") {
                        jo.put("balance", "维护中");
                        return jo;
                    } else {
                        jo.put("balance", balance);
                        return jo;
                    }
                } else {
                    jo.put("balance", "维护中");
                    return jo;
                }
            } else if ("BBIN".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
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
                    jo.put("balance", "维护中");
                    return jo;
                }
            } else if ("DS".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
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
                    jo.put("balance", "维护中");
                    return jo;
                }
            } else if ("OB".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
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
                    jo.put("balance", "维护中");
                    return jo;
                }
            } else if ("OG".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                OGGameServiceImpl og = new OGGameServiceImpl(pmap);
                String a = og.getBalance(ag_username, ag_password);
                try {
                    String str = a.substring(a.indexOf("<result>") + 8, a.indexOf("</result>"));
                    jo.put("balance", str);
                } catch (Exception e) {
                    jo.put("balance", "维护中");
                    return jo;
                }
            } else if ("SB".equals(BType)) {
                BType ="shenbo";
                if ("0".equals(findUserGameStatus(uid,BType))) {
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
                        jo.put("balance", "维护中");
                        return jo;
                    }
                    jo.put("balance", json.get("bal").toString());
                } catch (Exception e) {
                    jo.put("balance", "维护中");
                    return jo;
                }
            } else if ("MG".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                MGGameServiceImpl m = new MGGameServiceImpl(pmap);
                Map<String, String> gmap = new HashMap<String, String>();
                gmap.put("ClientIP", IPTools.getIp(request));
                JSONObject json = m.queryBalance(ag_username, ag_password, gmap);
                try {
                    if ("success".equals(json.get("Code").toString())) {
                        jo.put("balance", json.get("Balance").toString());
                    } else {
                        jo.put("balance", "维护中");
                    }
                } catch (Exception e) {
                    jo.put("balance", "维护中");
                }

            } else if ("HABA".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                HABAGameServiceImpl h = new HABAGameServiceImpl(pmap);
                QueryPlayerResponse qp = h.queryPlayer(ag_username, ag_password, null);
                if (qp.isFound() == true) {
                    jo.put("balance", qp.getRealBalance());
                } else {
                    jo.put("balance", "维护中");
                }
            } else if ("PT".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                PTGameServiceImpl p = new PTGameServiceImpl(pmap);
                try {
                    JSONObject json = JSONObject.fromObject(p.GetPlayerInfo(ag_username));
                    json = json.getJSONObject("result");
                    if (json == null || "".equals(json.toString())) {
                        jo.put("balance", "维护中");
                    } else {
                        String balance = json.getString("BALANCE").toString();
                        jo.put("balance", balance);
                    }
                } catch (Exception e) {
                    jo.put("balance", "维护中");
                    return jo;
                }

            } else if ("GGBY".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                GGBYGameServiceImpl gg = new GGBYGameServiceImpl(pmap);
                try {
                    String msg = gg.GetBalance(ag_username, ag_password);
                    jo.put("balance", msg);
                } catch (Exception e) {
                    jo.put("balance", "维护中");
                    return jo;
                }

            } else if ("CG".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
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
                    jo.put("balance", "维护中");
                    return jo;
                }
            } else if ("IG".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                IGGameServiceImpl c = new IGGameServiceImpl(pmap,cagent);
                String msg = c.getBalance(ag_username, ag_password);
                JSONObject json;
                json = JSONObject.fromObject(msg);
                if ("0".equals(json.get("errorCode").toString())) {
                    json = json.getJSONObject("params");
                    jo.put("balance", json.get("balance").toString());
                } else {
                    jo.put("balance", "维护中");
                    return jo;
                }
            } else if ("IGPJ".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                IGPJGameServiceImpl c = new IGPJGameServiceImpl(pmap,cagent);
                String msg = c.getBalance(ag_username, ag_password);
                JSONObject json;
                json = JSONObject.fromObject(msg);
                if ("0".equals(json.get("errorCode").toString())) {
                    json = json.getJSONObject("params");
                    jo.put("balance", json.get("balance").toString());
                } else {
                    jo.put("balance", "维护中");
                    return jo;
                }
            } else if ("HG".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                XHGServiceImpl c = new XHGServiceImpl(pmap);
                String msg = c.getBalance(hg_username);
                if (!"error".equals(msg)) {
                    jo.put("balance", msg);
                } else {
                    jo.put("balance", "维护中");
                    return jo;
                }
            } else if ("BG".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                StringBuffer url = request.getRequestURL();
                String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length())
                        .append("/").toString();
                BGGameServiceImpl c = new BGGameServiceImpl(pmap);
                JSONObject jsonObjec = c.openUserCommonAPI(ag_username, "open.balance.get", "", "", tempContextUrl);
                if ("success".equals(jsonObjec.get("code"))) {
                    jo.put("balance", JSONObject.fromObject(jsonObjec.get("params")).get("result"));
                } else {
                    jo.put("balance", "维护中");
                    return jo;
                }
            } else if ("VR".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
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
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                JFGameServiceImpl jf = new JFGameServiceImpl(pmap);
                String balance = jf.GetBalance(ag_username, ag_password);
                jo.put("balance", balance);
                return jo;
            } else if ("KYQP".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                KYQPGameServiceImpl k = new KYQPGameServiceImpl(pmap);
                String balance = k.queryUnderTheBalance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "维护中");
                } else {
                    balance = JSONObject.fromObject(balance).getJSONObject("d").getString("money");
                    jo.put("balance", balance);
                }
                return jo;
            } else if ("LYQP".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                LYQPGameServiceImpl lyqp = new LYQPGameServiceImpl(pmap);
                String balance = lyqp.queryUnderTheBalance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "维护中");
                } else {
                    balance = JSONObject.fromObject(balance).getJSONObject("d").getString("money");
                    jo.put("balance", balance);
                }
                return jo;
            } else if ("VG".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                VGGameServiceImpl vg = new VGGameServiceImpl(pmap);
                String balance = vg.balance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "维护中");
                } else {
                    jo.put("balance", balance);
                }
                return jo;

            } else if ("GY".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                GYGameServiceImpl gy = new GYGameServiceImpl(pmap);
                String balance = gy.balance(ag_username, ag_password);
                if ("error".equals(balance)) {
                    jo.put("balance", "维护中");
                } else {
                    jo.put("balance", balance);
                }
                return jo;
            } else if ("PS".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                PSGameServiceImpl ps = new PSGameServiceImpl(pmap);
                String balance = ps.balance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "维护中");
                } else {
                    jo.put("balance", balance);
                }
                return jo;
            } else if ("NB".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                NBGameServiceImpl nb = new NBGameServiceImpl(pmap);
                String balance = nb.balance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "维护中");
                } else {
                    jo.put("balance", balance);
                }
                return jo;
            } else if ("SW".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                SWGameServiceImpl sw = new SWGameServiceImpl(pmap);
                String balance = sw.getBalance(ag_username);
                if ("error".equals(balance)) {
                    jo.put("balance", "维护中");
                } else {
                    jo.put("balance", balance);
                }
                return jo;
            } else if ("CQJ".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
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
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                ESWServiceImpl eswService = new ESWServiceImpl(pmap);
                String data = eswService.queryUserInfo(ag_username);
                JSONObject jsonObject = JSONObject.fromObject(data);
                if (jsonObject.getInt("code") == 0) {
                    jo.put("balance", jsonObject.getString("money"));
                } else {
                    jo.put("balance", "维护中");
                }
                return jo;
            } else if ("IBC".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                IBCGameServiceImpl service = new IBCGameServiceImpl(pmap);
                ResultResponse data = service.getBalance(ag_username);
                if (data.getStatus() == ResponseCode.SUCCESS_STATUS) {
                    jo.put("balance", data.getBalance());
                } else {
                    jo.put("balance", "维护中");
                }
                return jo;
            }else if("IM".equals(BType)){
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                IMONEGameServiceImpl imoneGameService = new IMONEGameServiceImpl(pmap);
                jo.put("balance",imoneGameService.getBalance(ag_username) != null?imoneGameService.getBalance(ag_username):"维护中");
                return jo;
            }else if("NWG".equals(BType)) {
                if ("0".equals(findUserGameStatus(uid,BType))) {
                    jo.put("balance", "0");
                    return jo;
                }
                NWGGameServiceImpl nwgGameService = new NWGGameServiceImpl(pmap);
                String balance = nwgGameService.getBalance(ag_username);
                jo.put("balance", balance.equals("error")?"维护中":balance);
                return jo;
            }else {
                jo.put("balance", "维护中");
                return jo;
            }
            return jo;
        } catch (Exception e) {
            jo.put("balance", "0.00");
            return jo;
        }
    }

    public String findUserGameStatus(String uid,String gameType){
        logger.info("findUserGameStatus(String uid= {},String gameType = {}",uid,gameType);

        String gameStatus  = redisUtils.get(uid+gameType);
        if(gameStatus != null) return  gameStatus;
        Map<String, Object> param = new HashMap<>();
        param.put("uid", uid);
        param.put("gametype", "is_"+gameType.toLowerCase());
        Map<String, String> user = userService.selectUserGameStatus(param).get(0);
        Object status = user.get("cnt");
        if("0".equals(status.toString())){
            return  "0";
        }
        redisUtils.set(uid+gameType, status.toString(),3600);
        logger.info("findUserGameStatus() -end  status:"+ status);
        return  status.toString();
    }

    /**
     * @Description 跳转游戏
     * @param request
     * @param response
     * @param gameType
     * @param gameID
     * @param model
     * @return
     */
    @RequestMapping("/forwardGame")
    @ResponseBody
    public JSONObject forwardGame(HttpServletRequest request, HttpServletResponse response, String gameType,
                                  String gameID, String model) {
        logger.info("调用获取游戏跳转链接接口开始==================START===================");
        JSONObject jo = new JSONObject();
        try {
            Map<String,String> usermap = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(usermap)){
                logger.info("用户ID为空,登录已过期,请重新登录");
                return BaseResponse.error("0", "error");
            }
            String uid = usermap.get("uid");
            // 获取转账请求参数
            String ag_password = usermap.get("ag_password");// 游戏密码
            String ag_username = usermap.get("ag_username");
            String hg_username = usermap.get("hg_username");
            String username = usermap.get("userName");//用户名称
            String cid = usermap.get("cid");
            String cagent = usermap.get("cagent");
            String ip = IPTools.getIp(request);
            // 解密密码
            DESEncrypt d = new DESEncrypt(KeyConstant.DESKEY);
            ag_password = d.decrypt(ag_password);
            // 获取平台游戏配置信息
            Map<String,String> pmap = formatPlatformConfigByCagent(cagent.toUpperCase());
            if(CollectionUtils.isEmpty(pmap)){
                logger.info("查询平台配置信息为空");
                return BaseResponse.faild("0", "process");
            }
            // 检查维护状态
            PlatFromConfig pf = new PlatFromConfig();
            pf.InitData(pmap, gameType);
            if ("0".equals(pf.getPlatform_status())) {
                return BaseResponse.faild("0", "process");
            }

            String gameKey = gameType.trim();

            if("BBIN".equalsIgnoreCase(gameKey) || "SB".equalsIgnoreCase(gameKey)){
                if(StringUtils.isNotBlank(gameID) && ("1".equals(gameID) || "2".equals(gameID))){
                    gameKey += gameID;
                }
            }

            // ig彩票的特殊判断
            if (gameKey.indexOf("IGLOTTERY") != -1) {
                gameKey = "IGLOTTERY";
            } else if (gameKey.indexOf("IGPJ") != -1) {
                gameKey = "IGPJ";
            } else if (gameKey.indexOf("VR") != -1) {
                gameKey = "VR";
            } else if (gameKey.indexOf("KYQP") != -1) {
                gameKey = "KYQP";
            } else if (gameKey.indexOf("LYQP") != -1) {
                gameKey = "LYQP";
            }
            // 检查平台游戏开关状态
            Map<String, String> platformStatus = userGameTransferService.getPlatformStatusByCid(cid);
            if (CollectionUtils.isEmpty(platformStatus)) {
                logger.info("查询平台游戏开关状态失败");
                return BaseResponse.faild("0", "查询平台游戏开关状态失败");
            }
            //根据平台筛选
            logger.info("查询平台配置信息结果:{}",JSONObject.fromObject(platformStatus).toString());
            if(platformStatus.containsKey(gameType.toUpperCase())){
                String cagentStatus = String.valueOf(platformStatus.get(gameType.toUpperCase()));
                logger.info("用户【"+username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,查询平台【"+cagent+"】游戏开关状态:{}",cagentStatus);
                if(!"1".equals(cagentStatus)){
                    return BaseResponse.error("0", "process");
                }
            }else{
                logger.info("用户【"+username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,查询平台【"+cagent+"】游戏开关状态时游戏类型编码【"+gameType+"】,不存在");
                return BaseResponse.error("0","用户【"+username+"】,调用天下平台向游戏平台转入金额(游戏上分)接口,查询平台【"+cagent+"】游戏开关状态时游戏类型编码【"+gameType+"】,不存在");
            }

            checkGameReg(request,uid, ag_username, hg_username, ag_password, gameType, ip, pmap);
            if (gameType == "YOPLAY" || "YOPLAY".equals(gameType)) {
                String sid = "AGIN" + System.currentTimeMillis();
                AGINGameServiceImpl agService = new AGINGameServiceImpl(pmap);
                // 设置盘口 默认设置IG默认盘口A
                UserTypeHandicapUtil uth = new UserTypeHandicapUtil();
                String handicap = uth.getHandicap(gameType, uid, userService);
                handicap = handicap.isEmpty() ? "A" : handicap;

                if (StringUtils.isBlank(gameID)) {
                    gameID = "YP800";
                }
                // 游戏id前缀必须YP
                if (!"YP".equals(gameID.substring(0, 2))) {
                    jo.put("msg", "error");
                    return jo;
                }
                // 截取游戏id必须在800-850范围值之内
                int number = Integer.parseInt(gameID.substring(2, 5));
                if (!(number >= 800 && number < 850)) {
                    gameID = "YP800";
                }
                if ("mobile".equals(model)) {
                    String url = agService.forwardMobileGame(ag_username, ag_password, ip, sid, gameID, handicap);
                    jo.put("msg", url);
                    jo.put("type", "from");
                } else {
                    String url = agService.forwardGame(ag_username, ag_password, ip, sid, gameID, handicap);
                    jo.put("msg", url);
                    jo.put("type", "from");
                }
            } else if (gameType == "TASSPTA" || "TASSPTA".equals(gameType)) {
                String sid = "AGIN" + System.currentTimeMillis();
                AGINGameServiceImpl agService = new AGINGameServiceImpl(pmap);
                // 设置盘口 默认设置IG默认盘口A
                UserTypeHandicapUtil uth = new UserTypeHandicapUtil();
                String handicap = uth.getHandicap(gameType, uid, userService);
                handicap = handicap.isEmpty() ? "A" : handicap;

                if ("mobile".equals(model)) {
                    String url = agService.forwardMobileGame(ag_username, ag_password, ip, sid, gameType, handicap);
                    jo.put("msg", url);
                    jo.put("type", "from");
                } else {
                    String url = agService.forwardGame(ag_username, ag_password, ip, sid, gameType, handicap);
                    jo.put("msg", url);
                    jo.put("type", "from");
                }
            } else if (gameType == "AGIN" || "AGIN".equals(gameType)) {
                String sid = "AGIN" + System.currentTimeMillis();
                AGINGameServiceImpl agService = new AGINGameServiceImpl(pmap);
                // 设置盘口 默认设置IG默认盘口A
                UserTypeHandicapUtil uth = new UserTypeHandicapUtil();
                String handicap = uth.getHandicap(gameType, uid, userService);
                handicap = handicap.isEmpty() ? "A" : handicap;

                if ("mobile".equals(gameID)) {
                    String url = agService.forwardMobileGame(ag_username, ag_password, ip, sid, "11", handicap);
                    jo.put("msg", url);
                    jo.put("type", "from");
                } else {
                    String url = agService.forwardGame(ag_username, ag_password, ip, sid, "0", handicap);
                    jo.put("msg", url);
                    jo.put("type", "from");
                }
            } else if (gameType == "AG" || "AG".equals(gameType)) {
                String sid = "AG" + System.currentTimeMillis();
                AGGameServiceImpl agService = new AGGameServiceImpl(pmap);

                // 设置盘口 默认设置IG默认盘口A
                UserTypeHandicapUtil uth = new UserTypeHandicapUtil();
                String handicap = uth.getHandicap(gameType, uid, userService);
                handicap = handicap.isEmpty() ? "A" : handicap;

                if ("mobile".equals(gameID)) {
                    String url = agService.forwardMobileGame(ag_username, ag_password, ip, sid, "11", handicap);
                    jo.put("msg", url);
                    jo.put("type", "from");
                } else {
                    String url = agService.forwardGame(ag_username, ag_password, ip, sid, "0", handicap);
                    jo.put("msg", url);
                    jo.put("type", "from");
                }

            } else if (gameType == "BBIN" || "BBIN".equals(gameType)) {
                int gameno = 0;
                try {
                    gameno = Integer.parseInt(gameID);
                } catch (Exception e) {

                }
                // 1为真人,2为电子游艺
                if ("1".equals(gameID) || "2".equals(gameID) || "3".equals(gameID)) {
                    BBINGameServiceImpl b = new BBINGameServiceImpl(pmap);
                    if ("3".equals(gameID)) {
                        b.Logout(ag_username, ag_password);
                        String msg = b.Login(ag_username, ag_password, "Ltlottery");
                        jo.put("msg", msg);
                        jo.put("type", "link");
                    } else if ("2".equals(gameID)) {
                        b.Logout(ag_username, ag_password);
                        String msg = b.Login(ag_username, ag_password, "game");
                        // System.out.println(msg);
                        jo.put("msg", msg);
                        jo.put("type", "link");
                    } else {
                        b.Logout(ag_username, ag_password);
                        String msg = b.Login(ag_username, ag_password, "live");
                        // System.out.println(msg);
                        jo.put("msg", msg);
                        jo.put("type", "link");
                    }
                } else if (gameno > 5000 && gameno < 6000) {
                    BBINGameServiceImpl b = new BBINGameServiceImpl(pmap);
                    String link1 = b.Login2(ag_username, ag_password);
                    String link2 = b.PlayGame(ag_username, ag_password, gameID);
                    jo.put("link1", link1);
                    jo.put("link2", link2);
                    jo.put("type", "login");
                } else {
                    jo.put("msg", "error");
                }
            } else if (gameType == "DS" || "DS".equals(gameType)) {
                DSGameServiceImpl ds = new DSGameServiceImpl(pmap);
                String msg = ds.LoginGame(ag_username, ag_password);
                // System.out.println(msg);
                JSONObject json;
                json = JSONObject.fromObject(msg);
                json = json.getJSONObject("params");
                msg = json.getString("link");
                jo.put("msg", msg);
                jo.put("type", "link");
            } else if (gameType == "OB" || "OB".equals(gameType)) {
                OBService a = new OBGameServiceImpl(pmap);
                // a.check_or_create(ag_username, ag_password);
                String msg = a.forward_game(ag_username, ag_password, model);
                JSONObject json;
                json = JSONObject.fromObject(msg);
                msg = json.getString("gameLoginUrl");

                jo.put("msg", msg);
                jo.put("type", "link");
            } else if (gameType == "OG" || "OG".equals(gameType)) {
                OGGameServiceImpl o = new OGGameServiceImpl(pmap);
                o.CreateMem(ag_username, ag_password);
                String msg = o.Logingame(ag_username, ag_password, gameID);
                jo.put("msg", msg);
                jo.put("type", "link");
            } else if (gameType == "SB" || "SB".equals(gameType)) {
                // 1为真人视讯,2为电子游戏
                if ("1".equals(gameID) || "2".equals(gameID) || "3".equals(gameID) || "4".equals(gameID)) {

                    SBService s = new SBGameServiceImpl(pmap);
                    // 获取平台授权token
                    String atoken = s.getAccToken();
                    JSONObject json = new JSONObject();
                    json = JSONObject.fromObject(atoken);

                    // 设置盘口 默认设置申博默认盘口1-5
                    UserTypeHandicapUtil uth = new UserTypeHandicapUtil();
                    String handicap = uth.getHandicap("SB", uid, userService);
                    handicap = handicap.isEmpty() ? "4" : handicap;

                    // 获取用户token
                    String utoken = s.getUserToken(IPTools.getIp(request), ag_username, ag_username,
                            json.getString("access_token"), handicap, model);
                    json = JSONObject.fromObject(utoken);
                    // 获取游戏连接
                    String url = s.getGameUrl(json.getString("authtoken"), gameID);

                    jo.put("msg", url);
                    jo.put("type", "link");
                } else {
                    jo.put("msg", "error");
                }
            } else if (gameType == "MG" || "MG".equals(gameType)) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("ClientIP", IPTools.getIp(request));
                map.put("language", "zh");
                map.put("gameId", gameID);
                if ("fun".equals(model)) {
                    map.put("demoMode", "true");
                } else {
                    map.put("demoMode", "false");
                }
                MGGameServiceImpl m = new MGGameServiceImpl(pmap);
                JSONObject json = m.loginGame(ag_username, ag_password, map);
                if ("success".equals(json.get("Code").toString())) {
                    jo.put("msg", json.get("LaunchUrl").toString());
                    jo.put("type", "link");
                } else {
                    jo.put("msg", "error");
                }

            } else if (gameType == "HABA" || "HABA".equals(gameType)) {
                if (gameID == null || "".equals(gameID)) {
                    jo.put("msg", "error");
                }
                HABAGameServiceImpl h = new HABAGameServiceImpl(pmap);
                h.loginOrCreatePlayer(ag_username, ag_password, null);

                // 查询玩家信息
                QueryPlayerResponse qp = h.queryPlayer(ag_username, ag_password, null);
                if (qp.isFound() == true) {
                    if (!"fun".equals(model) && !"real".equals(model)) {
                        model = "real";
                    }
                    JSONObject joo = JSONObject.fromObject(pf.getPlatform_config());
                    String gameurl = joo.getString("gameurl").toString();
                    String refurl = request.getHeader("referer");
                    String[] urls = refurl.split("/");
                    String str = gameurl + "/play?brandid=" + qp.getBrandId() + "&keyname=" + gameID + "&token="
                            + qp.getToken() + "&mode=" + model + "&locale=zh-CN";
                    jo.put("msg", str);
                    jo.put("type", "link");
                } else {
                    jo.put("msg", "error");
                }
            } else if (gameType == "JDB" || "JDB".equals(gameType)) {
                String msg = "";
                JDBGameServiceImpl jdb = new JDBGameServiceImpl(pmap);
                if ("mobile".equals(model)) {
                    msg = jdb.login(ag_username, gameID, "MB");
                } else {
                    msg = jdb.login(ag_username, gameID, "");
                }
                jo.put("msg", msg);
            } else if (gameType == "PT" || "PT".equals(gameType)) {
                if (gameID == null || "".equals(gameID)) {
                    jo.put("msg", "error");
                }

                PTGameServiceImpl p = new PTGameServiceImpl(pmap);
                String msg = p.CreatePlayer(ag_username, ag_password);
                if (msg == null || "".equals(msg)) {
                    jo.put("msg", "error");
                } else {
                    if (model == null) {
                        model = "pc";
                    }
                    String ptsrc = "";
                    if ("mobile".equals(model)) {
                        ptsrc = "http://m.theworldpt.com/LoginM.aspx?uid=";
                    } else {
                        ptsrc = "http://pc.theworldpt.com/login.aspx?uid=";
                    }
                    String ptparam = "?use=" + ag_username + "&pwd=" + ag_password + "&code=" + gameID;
                    String key = "tX9UiO3b";

                    try {
                        ptparam = PTDes.toHexString(PTDes.encrypt(ptparam, key)).toUpperCase();
                    } catch (Exception e) {

                    }
                    jo.put("msg", ptsrc + ptparam);
                    jo.put("type", "link");
                }
            } else if (gameType == "GGBY" || "GGBY".equals(gameType)) {
                GGBYGameServiceImpl gg = new GGBYGameServiceImpl(pmap);
                String sid = "TE179" + System.currentTimeMillis();
                gg.CheckOrCreateGameAccout(ag_username, ag_password);
                String url = gg.forwardGame(ag_username, ag_password, sid, ip);
                jo.put("msg", url);
                jo.put("type", "link");

            } else if (gameType == "AGBY" || "AGBY".equals(gameType)) {
                String sid = "AGIN" + System.currentTimeMillis();
                String url = "";
                AGINGameServiceImpl agService = new AGINGameServiceImpl(pmap);
                // 默认设置a盘口 新增AGBY H5支持 2018-08-18
                if ("mobile".equals(model)) {
                    url = agService.forwardMobileGame(ag_username, ag_password, ip, sid, "6", "A");
                } else {
                    url = agService.forwardGame(ag_username, ag_password, ip, sid, "6", "A");
                }
                jo.put("msg", url);
                jo.put("type", "from");
            } else if (gameType == "CG" || "CG".equals(gameType)) {
                CGGameServiceImpl c = new CGGameServiceImpl(pmap);
                String msg = c.LoginGame(ag_username, ag_password);
                JSONObject json;
                json = JSONObject.fromObject(msg);
                json = json.getJSONObject("params");
                msg = json.getString("link");
                jo.put("msg", msg);
                jo.put("type", "link");
            } else if (gameType == "IGLOTTO" || "IGLOTTO".equals(gameType) || gameType == "IGLOTTERY"
                    || "IGLOTTERY".equals(gameType)) {
                // 设置盘口 默认设置IG默认盘口A
                UserTypeHandicapUtil uth = new UserTypeHandicapUtil();
                String handicap = uth.getHandicap(gameType, uid, userService);
                handicap = handicap.isEmpty() ? "A" : handicap;

                if (model == null || (!"PC".equals(model) && !"MB".equals(model))) {
                    model = "PC";
                }
                if (gameID == null) {
                    jo.put("msg", "error");
                    return jo;
                } else if (gameType == "IGLOTTO" || "IGLOTTO".equals(gameType)) {
                    gameType = "LOTTO";
                } else if (gameType == "IGLOTTERY" || "IGLOTTERY".equals(gameType)) {
                    gameType = "LOTTERY";
                    if (gameID == null || "".equals(gameID)) {
                        jo.put("msg", "error");
                        return jo;
                    }
                } else {
                    jo.put("msg", "error");
                    return jo;
                }
                IGGameServiceImpl c = new IGGameServiceImpl(pmap,cagent);
                String msg = c.LoginGame(ag_username, ag_password, gameType, gameID, model, handicap);
                if(StringUtils.isEmpty(msg)){
                    jo.put("msg", "error");
                }else{
                    JSONObject json;
                    json = JSONObject.fromObject(msg);
                    json = json.getJSONObject("params");
                    msg = json.getString("link");
                    jo.put("msg", msg);
                    jo.put("type", "link");
                }
            } else if (gameType == "IGPJLOTTO" || "IGPJLOTTO".equals(gameType) || gameType == "IGPJLOTTERY"
                    || "IGPJLOTTERY".equals(gameType)) {

                // 设置盘口 默认设置IG默认盘口A
                UserTypeHandicapUtil uth = new UserTypeHandicapUtil();
                String handicap = uth.getHandicap(gameType, uid, userService);
                handicap = handicap.isEmpty() ? "A" : handicap;

                if (model == null || (!"PC".equals(model) && !"MB".equals(model))) {
                    model = "PC";
                }
                if (gameID == null) {
                    jo.put("msg", "error");
                    return jo;
                } else if (gameType == "IGPJLOTTO" || "IGPJLOTTO".equals(gameType)) {
                    gameType = "LOTTO";
                } else if (gameType == "IGPJLOTTERY" || "IGPJLOTTERY".equals(gameType)) {
                    gameType = "LOTTERY";
                    if (gameID == null || "".equals(gameID)) {
                        jo.put("msg", "error");
                        return jo;
                    }
                } else {
                    jo.put("msg", "error");
                    return jo;
                }
                IGPJGameServiceImpl c = new IGPJGameServiceImpl(pmap,cagent);
                String msg = c.LoginGame(ag_username, ag_password, gameType, gameID, model, handicap);
                if(StringUtils.isEmpty(msg)){
                    jo.put("msg", "error");
                }else{
                    JSONObject  json = JSONObject.fromObject(msg);
                    json = json.getJSONObject("params");
                    msg = json.getString("link");
                    jo.put("msg", msg);
                    jo.put("type", "link");
                }
            } else if (gameType == "HG" || "HG".equals(gameType)) {
                XHGServiceImpl c = new XHGServiceImpl(pmap);
                if (model == null || (!"MB".equals(model) && !"PC".equals(model))) {
                    model = "PC";
                }
                // if (hg_username.length() < 10) {
                // jo.put("msg", "error");
                // }
                String msg = c.getLogin(hg_username, model);
                jo.put("msg", msg);
                jo.put("type", "link");
            } else if (gameType == "BG" || "BG".equals(gameType)) {
                String method = "";
                if ("1".equals(gameID)) {
                    method = "open.video.game.url";// 视讯
                } else if ("2".equals(gameID)) {
                    method = "open.lottery.game.url";// 彩票
                }
                StringBuffer url1 = request.getRequestURL();
                String tempContextUrl = url1.delete(url1.length() - request.getRequestURI().length(), url1.length())
                        .append("/").toString();
                BGGameServiceImpl c = new BGGameServiceImpl(pmap);
                JSONObject json = c.openUserCommonAPI(ag_username, method, model, "", tempContextUrl);
                if ("success".equals(json.get("code"))) {
                    String url = JSONObject.fromObject(json.get("params")).get("result").toString();
                    request.getSession().setAttribute(uid + "BGToken", url);
                    jo.put("msg", url);
                    jo.put("type", "link");
                } else {
                    jo.put("msg", "error");
                }
            } else if (gameType == "VR" || "VR".equals(gameType)) {
                if (gameID == null || "".equals(gameID)) {
                    gameID = "0";// 视讯
                }

                // 默认设置vr彩票盘口为空则使用后台配置
                UserTypeHandicapUtil uth = new UserTypeHandicapUtil();
                String handicap = uth.getHandicap("VR", uid, userService);
                handicap = handicap.isEmpty() ? "" : handicap;

                VRGameServiceImpl v = new VRGameServiceImpl(pmap);
                String url = v.LoginGame(ag_username, gameID, handicap);
                jo.put("msg", url);
                jo.put("type", "link");
            } else if (gameType == "JF" || "JF".equals(gameType)) {
                // String Model="";
                // if (gameID==null||"".equals(gameID)) {
                // Model = "";// 牛牛
                // }else if(gameID=="0"){
                // Model = "";// 牛牛
                // }else{
                // Model=gameID;//彩票
                // }
                JFGameServiceImpl jf = new JFGameServiceImpl(pmap);
                // {"Success":"回传结果(true/false)" , "Code":"返回值" ,
                // "Message":"消息",Username:”进游戏时的用户名”}
                // String result= jf.CreateUser(ag_username, ag_password,
                // ag_username);
                // JSONObject json = JSONObject.fromObject(result);
                // String loginName="";
                /* 登录游戏带代理前缀的用户名 */
                // if (json.getString("Code").equals("200")) {// 用户已经存在
                // loginName = json.getString("Username");
                // }else {
                // if(pmap.containsKey("agent")){
                // loginName=pmap.get("agent").toString()+ag_username;
                // }else{
                // loginName=ag_username;
                // }
                // }
                String url = jf.loginGame(ag_username, ag_password, gameID);
                jo.put("msg", url);
                jo.put("type", "link");
            } else if (gameType == "KYQP" || "KYQP".equals(gameType)) {
                if (gameID == null || "".equals(gameID)) {
                    gameID = "0";// 大厅
                }
                KYQPGameServiceImpl k = new KYQPGameServiceImpl(pmap);
                String url = k.checkOrCreateGameAccout(ag_username, ip, gameID);
                if ("error".equals(url)) {
                    jo.put("msg", "error");
                } else {
                    jo.put("msg", JSONObject.fromObject(url).getJSONObject("d").getString("url"));
                    jo.put("type", "link");
                }
            } /*
             * else if ("ESW".equals(gameType)) { if (StringUtils.isNullOrEmpty(gameID)) { gameID = "0";// 大厅 }
             * ESWServiceImpl eswService = new ESWServiceImpl(pmap); EswLoginVo eswLoginVo = new
             * EswLoginVo(ag_username,ip,Integer.parseInt(gameID)); String data =
             * eswService.checkOrCreateGameAccout(eswLoginVo); JSONObject jsonObject = JSONObject.fromObject(data); if
             * (jsonObject.getInt("code") == 0) { jo.put("msg", jsonObject.getString("fullUrl")); jo.put("type",
             * "link"); } else { jo.put("msg", jsonObject.getInt("code")); } }
             */ else if (gameType == "LYQP" || "LYQP".equals(gameType)) {
                if (gameID == null || "".equals(gameID)) {
                    gameID = "0";// 大厅
                }
                LYQPGameServiceImpl lyqp = new LYQPGameServiceImpl(pmap);
                String url = lyqp.checkOrCreateGameAccout(ag_username, ip, gameID);
                if ("error".equals(url)) {
                    jo.put("msg", "error");
                } else {
                    jo.put("msg", JSONObject.fromObject(url).getJSONObject("d").getString("url"));
                    jo.put("type", "link");
                }
            } else if (gameType == "VG" || "VG".equals(gameType)) {
                if (gameID == null || "".equals(gameID)) {
                    gameID = "1000";// 大厅
                }
                VGGameServiceImpl vg = new VGGameServiceImpl(pmap);
                String url = vg.loginWithChannel(ag_username, gameID, model, "");
                if ("error".equals(url)) {
                    jo.put("msg", "error");
                } else {
                    jo.put("msg", url);
                    jo.put("type", "link");
                }
            } else if (gameType == "GY" || "GY".equals(gameType)) {
                GYGameServiceImpl gy = new GYGameServiceImpl(pmap);
                String msg = gy.login(ag_username, ag_password, model);
                if ("error".equals(msg)) {
                    jo.put("msg", "error");
                } else {
                    jo.put("msg", msg);
                    jo.put("type", "link");
                }
            } else if (gameType == "PS" || "PS".equals(gameType)) {

                StringBuffer url = request.getRequestURL();
                String return_url = url.delete(url.length() - request.getRequestURI().length(), url.length())
                        .append("/").toString();
                String subgame_id = "";

                UUID uuid = UUID.randomUUID();
                String access_token = uuid.toString();
                int step = 3; // 表示预生成token状态
                // 生成登录验证token
                userService.insertPSToken(access_token, step, uid);

                PSGameServiceImpl ps = new PSGameServiceImpl(pmap);
                String forwardUrl = ps.login(ag_username, subgame_id, gameID, access_token, return_url);
                if ("error".equals(forwardUrl)) {
                    jo.put("msg", "error");
                } else {
                    jo.put("msg", forwardUrl);
                    jo.put("type", "link");
                }
            } else if (gameType == "NB" || "NB".equals(gameType)) {
                NBGameServiceImpl nb = new NBGameServiceImpl(pmap);
                String forwardUrl = nb.login(ag_username);
                if ("error".equals(forwardUrl)) {
                    jo.put("msg", "error");
                } else {
                    jo.put("msg", forwardUrl);
                    jo.put("type", "link");
                }
            } else if (gameType == "SW" || "SW".equals(gameType)) {
                SWGameServiceImpl sw = new SWGameServiceImpl(pmap);
                String forwardUrl = sw.loginGame(gameID, ag_username);
                if ("error".equals(forwardUrl)) {
                    jo.put("msg", "error");
                } else {
                    jo.put("msg", forwardUrl);
                    jo.put("type", "link");
                }
            }else if (gameType.equalsIgnoreCase("IBC")) {
                IBCGameServiceImpl ibcService = new IBCGameServiceImpl(pmap);
                TransferVO transferVO = new TransferVO();
                transferVO.setGameId(gameID);
                transferVO.setAccount(ag_username);
                transferVO.setTerminal(model);
                ResultResponse resultResponse = ibcService.loginGame(transferVO);
                String forwardUrl = resultResponse.getData().toString();
                if (resultResponse.getStatus() == 0) {
                    jo.put("msg", "error");
                } else {
                    jo.put("msg", forwardUrl);
                    jo.put("type", "link");
                }
            } else if ("CQJ".equals(gameType)) {
                logger.info("ag_username,ag_password" + ag_username + ag_password);
                CQJServiceimpl cqjServiceimpl = new CQJServiceimpl(pmap);
                String cqjUrl = cqjServiceimpl.getLobbylink(ag_username, ag_password);
                if ("error".equals(cqjUrl)) {
                    jo.put("msg", "error");
                } else {
                    jo.put("msg", cqjUrl);
                    jo.put("type", "link");
                }
            } else if ("ESW".equals(gameType)) {
                ESWServiceImpl eswService = new ESWServiceImpl(pmap);
                EswLoginVo eswLoginVo = new EswLoginVo(ag_username,ip,Integer.parseInt(gameID));
                String data = eswService.checkOrCreateGameAccout(eswLoginVo);
                JSONObject jsonObject = JSONObject.fromObject(data);
                if (jsonObject.getInt("code") == 0) {
                    jo.put("msg", jsonObject.getString("fullUrl"));
                    jo.put("type", "link");
                } else {
                    jo.put("msg", jsonObject.getInt("code"));
                }
            }else if("IM".equals(gameType)){
                IMONEGameServiceImpl imoneGameService = new IMONEGameServiceImpl(pmap);
                GameForwardVO gameForwardVO = new GameForwardVO();
                gameForwardVO.setAg_username(ag_username);
                gameForwardVO.setIp(ip);
                gameForwardVO.setGameId(gameID);
                gameForwardVO.setModel(model);
                String url = imoneGameService.forwardGame(gameForwardVO);
                if(url !=null){
                    jo.put("msg",url);
                    jo.put("type", "link");
                }else{
                    jo.put("msg",url);
                }
            }else if("NWG".equals(gameType)){
                NWGGameServiceImpl nwgGameService = new NWGGameServiceImpl(pmap);
                GameForwardVO gameForwardVO = new GameForwardVO();
                gameForwardVO.setIp(ip);
                gameForwardVO.setGameId(gameID);
                gameForwardVO.setAg_username(ag_username);
                String url= nwgGameService.checkOrCreateAccount(gameForwardVO);
                if(url != "error"){
                    jo.put("msg",url);
                    jo.put("type", "link");
                }else{
                    jo.put("msg", "error");
                }
            }else {
                jo.put("msg", "error");
            }
            return jo;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取游戏跳转链接接口异常:{}", e.getMessage());
            return BaseResponse.error("0", "调用获取游戏跳转链接接口异常");
        }
    }



    /**
     *
     * @Description 获取用户详情
     * @param request
     * @return
     */
    @RequestMapping("/getUserInfo")
    @ResponseBody
    public JSONObject getUserInfo(HttpServletRequest request,HttpServletResponse response) {
        logger.info("调用获取用户详情接口开始=============START================");
        try {
            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                logger.info("用户ID为空,登录已过期,请重新登录");
                return BaseResponse.error("0", "用户ID为空,登录已过期,请重新登录");
            }
            String uid = map.get("uid");
            return newUserService.getUserInfo(uid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取用户详情接口异常:{}",e.getMessage());
            return BaseResponse.error("0", "调用获取用户详情接口异常");
        }
    }








    /**
     *
     * @Description 检查用户游戏开关状态
     * @param request
     * @param uid
     * @param ag_username
     * @param hg_username
     * @param ag_password
     * @param type
     * @param ip
     * @param pmap
     */
    public JSONObject checkGameReg(HttpServletRequest request,String uid, String ag_username, String hg_username, String ag_password, String type,
                                   String ip, Map<String, String> pmap) throws Exception{
        logger.info("用户【"+ag_username+"】调用检查或创建游戏账号接口开始======================START=======================");
        try {
            // 检查维护状态
            PlatFromConfig pf = new PlatFromConfig();
            pf.InitData(pmap, type);
            if ("0".equals(pf.getPlatform_status())) {
                logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败,检查游戏为维护状态");
                return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败,检查游戏为维护状态");
            }

            if ("IGLOTTO".equals(type) || "IGLOTTERY".equals(type)) {
                type = "IG";
            }
            if ("IGPJLOTTO".equals(type) || "IGLOTTERY".equals(type)) {
                type = "IGPJ";
            }
            if ("AGIN".equals(type) || "AGBY".equals(type) || type.equals("AGBY") || "YOPLAY".equals(type) || type.equals("TASSPTA")) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    AGINGameServiceImpl agService = new AGINGameServiceImpl(pmap);
                    // 如果未注册AG平台,注册AG用户,并记录
                    String  msg = agService.CheckOrCreateGameAccout(ag_username, ag_password, "A", "CNY");
                    if ("0".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_agin");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            }  else if ("AG".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    AGGameServiceImpl agService = new AGGameServiceImpl(pmap);
                    // 如果未注册AG平台,注册AG用户,并记录
                    String msg = agService.CheckOrCreateGameAccout(ag_username, ag_password, "A", "CNY");
                    if ("0".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_ag");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("BBIN".equals(type)) {
                // 查询用户是否在平台注册,如果未注册则注册用户,并记录
                if ("0".equals(findUserGameStatus(uid,type))) {
                    BBINGameServiceImpl b = new BBINGameServiceImpl(pmap);
                    String msg = b.CreateMember(ag_username, ag_password);
                    JSONObject json = JSONObject.fromObject(msg);
                    if ("true".equals(json.getString("result")) || msg.indexOf("The account is repeated") > -1) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_bbin");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("DS".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    DSGameServiceImpl ds = new DSGameServiceImpl(pmap);
                    String msg = ds.LoginGame(ag_username, ag_password);
                    JSONObject json = JSONObject.fromObject(msg);
                    if ("0".equals(json.getString("errorCode"))) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_ds");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }

            } else if ("OB".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    OBService ob = new OBGameServiceImpl(pmap);
                    String msg = ob.check_or_create(ag_username, ag_password);
                    if ("success".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_ob");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("OG".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    OGGameServiceImpl og = new OGGameServiceImpl(pmap);
                    String msg = og.CreateMem(ag_username, ag_password);
                    msg = msg.substring(msg.indexOf("<result>") + 8, msg.indexOf("</result>"));
                    if ("1".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_og");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("SB".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,"shenbo"))) {
                    SBService s = new SBGameServiceImpl(pmap);
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
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_shenbo");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("MG".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    MGGameServiceImpl m = new MGGameServiceImpl(pmap);
                    JSONObject json = m.createAccount(ag_username, ag_password, null);
                    if ("success".equals(json.getString("Code"))) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_mg");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }

            } else if ("PT".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    PTGameServiceImpl p = new PTGameServiceImpl(pmap);
                    String msg = p.CreatePlayer(ag_username, ag_password);
                    if ("success".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_pt");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("HABA".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    HABAGameServiceImpl h = new HABAGameServiceImpl(pmap);
                    LoginUserResponse lu = h.loginOrCreatePlayer(ag_username, ag_password, new HashMap<String, Object>());
                    if (lu.isPlayerCreated() || lu.getMessage() == null || "null".equals(lu.getMessage())) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_haba");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("JDB".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    JDBGameServiceImpl jdb = new JDBGameServiceImpl(pmap);
                    String msg = jdb.createUser(ag_username);
                    if ("success".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_jdb");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("GGBY".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    GGBYGameServiceImpl gg = new GGBYGameServiceImpl(pmap);
                    String msg = gg.CheckOrCreateGameAccout(ag_username, ag_password);
                    if ("0".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_ggby");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }

            } else if ("CG".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    CGGameServiceImpl c = new CGGameServiceImpl(pmap);
                    String msg = c.LoginGame(ag_username, ag_password);
                    JSONObject json = JSONObject.fromObject(msg);
                    if ("0".equals(json.getString("errorCode"))) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_cg");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("IG".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    IGGameServiceImpl c = new IGGameServiceImpl(pmap,ag_username.substring(0,3));
                    // 首次登录默认设置A盘口
                    String msg = c.LoginGame(ag_username, ag_password, "LOTTERY", "", "PC", "A");
                    if (msg != null && !"null".equals(msg)) {
                        JSONObject json = JSONObject.fromObject(msg);
                        if ("0".equals(json.getString("errorCode"))) {
                            Map<String, Object> param = new HashMap<>();
                            param.put("uid", uid);
                            param.put("gametype", "is_ig");
                            userService.insertUserGameStatus(param);
                        }else{
                            logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                            return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        }
                    }
                }
            } else if ("IGPJ".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    IGPJGameServiceImpl c = new IGPJGameServiceImpl(pmap,ag_username.substring(0,3));
                    // 首次登录游戏默认设置 A盘口
                    String msg = c.LoginGame(ag_username, ag_password, "LOTTERY", "", "PC", "A");
                    if (msg != null && !"null".equals(msg)) {
                        JSONObject json = JSONObject.fromObject(msg);
                        if ("0".equals(json.getString("errorCode"))) {
                            Map<String, Object> param = new HashMap<>();
                            param.put("uid", uid);
                            param.put("gametype", "is_igpj");
                            userService.insertUserGameStatus(param);
                        }else{
                            logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                            return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        }
                    }
                }
            } else if ("HG".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    XHGServiceImpl c = new XHGServiceImpl(pmap);
                    String msg = c.getLogin(hg_username, "");
                    if (!"error".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_hg");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("BG".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    BGGameServiceImpl m = new BGGameServiceImpl(pmap);
                    JSONObject json = m.openUserCreate(ag_username, ag_password, "open.user.create");
                    if ("success".equals(json.getString("code")) || json.toString().indexOf("登录名(loginId)已存在") > 0) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_bg");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }

            } else if ("VR".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    VRGameServiceImpl v = new VRGameServiceImpl(pmap);
                    String msg = v.CreateUser(ag_username);
                    if ("success".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_vr");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("JF".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    JFGameServiceImpl jf = new JFGameServiceImpl(pmap);
                    String msg = jf.CreateUser(ag_username, ag_password, ag_username);
                    JSONObject json = JSONObject.fromObject(msg);
                    if (json.getString("Success").equals("true") && json.getString("Code").equals("1")) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_jf");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("KYQP".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    KYQPGameServiceImpl k = new KYQPGameServiceImpl(pmap);
                    String msg = k.checkOrCreateGameAccout(ag_username,ip, "0");
                    JSONObject json = JSONObject.fromObject(msg);
                    if ("0".equals(json.getJSONObject("d").get("code") + "")) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_kyqp");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("LYQP".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    LYQPGameServiceImpl lyqp = new LYQPGameServiceImpl(pmap);
                    String msg = lyqp.checkOrCreateGameAccout(ag_username,ip, "0");
                    JSONObject json = JSONObject.fromObject(msg);
                    if ("0".equals(json.getJSONObject("d").get("code") + "")) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_lyqp");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("VG".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    VGGameServiceImpl vg = new VGGameServiceImpl(pmap);
                    String msg = vg.createUser(ag_username);
                    if ("success".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_vg");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("GY".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    GYGameServiceImpl c = new GYGameServiceImpl(pmap);
                    String msg = c.createUser(ag_username, ag_password);
                    if (!"error".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_gy");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("PS".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    PSGameServiceImpl ps = new PSGameServiceImpl(pmap);
                    String msg = ps.createUser(ag_username);
                    if ("success".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_ps");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("NB".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    NBGameServiceImpl nb = new NBGameServiceImpl(pmap);
                    String msg = nb.createUser(ag_username);
                    if ("success".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_nb");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("SW".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    SWGameServiceImpl sw = new SWGameServiceImpl(pmap);
                    String msg = sw.createUser(ag_username);
                    if ("success".equals(msg)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_sw");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            }else if ("IBC".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    IBCGameServiceImpl ibcService = new IBCGameServiceImpl(pmap);
                    TransferVO transferVO = new TransferVO();
                    transferVO.setAccount(ag_username);
                    ResultResponse response = ibcService.CheckOrCreateGameAccout(transferVO);
                    if (response.getStatus() == 1) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_ibc");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            } else if ("CQJ".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    TransferVO transferVO = new TransferVO();
                    transferVO.setAccount(ag_username);
                    transferVO.setPassword(ag_password);
                    CQJServiceimpl cqjServiceimpl = new CQJServiceimpl(pmap);
                    boolean msgs = cqjServiceimpl.checkOrCreateGameAccout(transferVO);
                    if (msgs) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_cqj");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error","用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }

            }else if ("ESW".equals(type)) {
                if ("0".equals(findUserGameStatus(uid,type))) {
                    ESWServiceImpl eswServiceimpl = new ESWServiceImpl(pmap);
                    EswLoginVo vo = new EswLoginVo();
                    vo.setUserCode(ag_username);
                    String data = eswServiceimpl.checkOrCreateGameAccout(vo);
                    JSONObject jsonObject = JSONObject.fromObject(data);
                    if (jsonObject.getInt("code") == 0) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_esw");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            }else if("IM".equals(type)){
                if ("0".equals(findUserGameStatus(uid,type))) {
                    IMONEGameServiceImpl imoneGameService = new IMONEGameServiceImpl(pmap);
                    GameCheckOrCreateVO gameCheckOrCreateVO = new GameCheckOrCreateVO();
                    gameCheckOrCreateVO.setGamename(ag_username);
                    gameCheckOrCreateVO.setPassword(ag_password);
                    if (imoneGameService.checkOrCreateAccount(gameCheckOrCreateVO)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_im");
                        userService.insertUserGameStatus(param);
                    }else{
                        logger.info("用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                        return transferResponse("error", "用户【"+ag_username+"】,检查或创建游戏平台【"+type+"】账号失败");
                    }
                }
            }else if("NWG".equals(type)){
                if("0".equals(findUserGameStatus(uid,type))){
                    NWGGameServiceImpl nwgGameService = new NWGGameServiceImpl(pmap);
                    GameForwardVO gameForwardVO = new GameForwardVO();
                    gameForwardVO.setAg_username(ag_username);
                    gameForwardVO.setPassword(ag_password);
                    gameForwardVO.setGameId("620");
                    gameForwardVO.setIp(ip);
                    String params = nwgGameService.checkOrCreateAccount(gameForwardVO);
                    if(params != "error"){
                        Map<String, Object> param = new HashMap<>();
                        param.put("uid", uid);
                        param.put("gametype", "is_nwg");
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

    private Map<String,String> formatPlatformConfigByCagent(String cagent){
        Map<String,String> data = new HashMap<>();
        try {
            String jsonConfig = redisUtils.get(KeyConstant.GAME_CONFIG);
            if(jsonConfig !=null) return com.alibaba.fastjson.JSON.parseObject(jsonConfig,Map.class);
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

}
