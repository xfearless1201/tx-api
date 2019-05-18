package com.cn.tianxia.api.service.v2.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cn.tianxia.api.common.v2.KeyConstant;
import com.cn.tianxia.api.domain.txdata.v2.DemoAccountDao;
import com.cn.tianxia.api.domain.txdata.v2.PlatformConfigDao;
import com.cn.tianxia.api.game.impl.AGINFreeServiceImpl;
import com.cn.tianxia.api.game.impl.BGGameServiceImpl;
import com.cn.tianxia.api.game.impl.IGFunServiceImpl;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.project.v2.DemoAccountEntity;
import com.cn.tianxia.api.project.v2.PlatformConfigEntity;
import com.cn.tianxia.api.service.v2.TryGameService;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName TryGameServiceImpl
 * @Description 试玩游戏接口实现类
 * @author Hardy
 * @Date 2019年2月6日 上午11:16:24
 * @version 1.0.0
 */
@Service
public class TryGameServiceImpl implements TryGameService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(TryGameServiceImpl.class);
    
    @Autowired
    private DemoAccountDao demoAccountDao;
    
    @Autowired
    private PlatformConfigDao platformConfigDao;

    @Override
    public JSONObject forwardIGGame(String cagent, String gameType, String gameID, String accountCode, String model,String ip) {
        logger.info("调用获取IG游戏试玩链接业务开始==================START===================");
        try {
            boolean isnew = true;//是否为新玩家,默认为true;
            String platformConfigKey = "IGFUN";//获取游戏配置信息key
            //初次试玩默认账号和密码
            String username = cagent + System.currentTimeMillis();
            String password = KeyConstant.TRY_GAME_DESKEY;//试玩密码
            DemoAccountEntity demoAccount = new DemoAccountEntity(); 
            if(!"0".equals(accountCode)){
                //非初次试玩,获取用户账号
                demoAccount = demoAccountDao.selectByAccountcode(accountCode);
                if(demoAccount != null){
                    logger.info("IG试玩帐号已经存在-------username:" + username + "password:" + password);
                    username = demoAccount.getUsername();//试玩登录账号
                    password = demoAccount.getPassword();//试玩登录密码
                    isnew = false;
                }
            }
            
            //获取游戏配置信息
            PlatformConfigEntity platformConfig = platformConfigDao.selectByPlatformKey(platformConfigKey);
            if(platformConfig == null){
                logger.info("IG游戏正在维护中.....");
                return BaseResponse.error("0", "process");
            }
            
            Map<String, String> pmap = new HashMap<>();
            pmap.put(platformConfig.getPlatformKey(), platformConfig.getPlatformConfig());
            
            //登录IG游戏
            IGFunServiceImpl ig = new IGFunServiceImpl(pmap);

            String msg;
            //如果是新玩家就创建账号
            if(isnew){
                if(demoAccount == null){
                    demoAccount = new DemoAccountEntity();
                }
                demoAccount.setUsername(username);
                demoAccount.setPassword(password);
                demoAccount.setCagent(cagent);
                demoAccount.setAccountcode(accountCode);
                demoAccount.setAddtime(new Date());
                demoAccount.setIp(ip);
                demoAccountDao.insertSelective(demoAccount);
                String billno = cagent + System.currentTimeMillis();
                logger.info("IG试玩创建新帐号-------username:" + username + "password:" + password + "billno:" + billno);
                msg = ig.LoginGame(username, password, gameType, gameID, model);
                String res = ig.DEPOSIT(username, password, billno, "2000");
                if ("success".equals(res)) {
                    logger.info("IG试玩创建成功-------");
                } else {
                    logger.info("IG试玩创建失败-------");
                }
            } else {
                msg = ig.LoginGame(username, password, gameType, gameID, model);
            }

            if(StringUtils.isBlank(msg)){
                logger.info("获取IG游戏试玩链接失败:{}",msg);
                return BaseResponse.error("0", "error");
            }
            
            //解析响应结果
            JSONObject jsonObject = JSONObject.fromObject(msg);
            if ("0".equals(jsonObject.getString("errorCode"))) {
                jsonObject = jsonObject.getJSONObject("params");
                msg = jsonObject.getString("link");
                jsonObject.put("msg", msg);
                jsonObject.put("type", "link");
                return jsonObject;
            }
            logger.info("获取IG游戏试玩链接失败:{}",msg);
            return BaseResponse.error("0", "process");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取IG游戏试玩链接业务异常:{}",e.getMessage());
            return BaseResponse.error("0", "调用获取IG游戏试玩链接业务异常");
        }
    }

    @Override
    public JSONObject forwardAGGame(String cagent, String gameType, String gameID, String accountCode, String model,String ip) {
        logger.info("调用获取AG游戏试玩链接业务开始==================START======================");
        try {
            boolean isnew = true;//是否为新玩家,默认为true;
            String platformConfigKey = "AGINFUN";//获取游戏配置信息key
            //获取游戏配置信息
            PlatformConfigEntity platformConfig = platformConfigDao.selectByPlatformKey(platformConfigKey);
            if(platformConfig == null){
                logger.info("IG游戏正在维护中.....");
                return BaseResponse.error("0", "process");
            }
            //初次试玩默认账号和密码
            String username = cagent + System.currentTimeMillis();
            String password = KeyConstant.TRY_GAME_DESKEY;//试玩密码
            DemoAccountEntity demoAccount = new DemoAccountEntity(); 
            if(!"0".equals(accountCode)){
                //非初次试玩,获取用户账号
                demoAccount = demoAccountDao.selectByAccountcode(accountCode);
                if(demoAccount != null){
                    logger.info("IG试玩帐号已经存在-------username:" + username + "password:" + password);
                    username = demoAccount.getUsername();//试玩登录账号
                    password = demoAccount.getPassword();//试玩登录密码
                    isnew = false;
                }
            }
            Map<String, String> pmap = new HashMap<>();
            pmap.put(platformConfig.getPlatformKey(), platformConfig.getPlatformConfig());
            AGINFreeServiceImpl agService = new AGINFreeServiceImpl(pmap,platformConfigKey);
            //创建用户
            if(isnew){
                if(demoAccount == null){
                    demoAccount = new DemoAccountEntity();
                }
                demoAccount.setUsername(username);
                demoAccount.setPassword(password);
                demoAccount.setCagent(cagent);
                demoAccount.setAccountcode(accountCode);
                demoAccount.setAddtime(new Date());
                demoAccount.setIp(ip);
                demoAccountDao.insertSelective(demoAccount);
                String billno = cagent + System.currentTimeMillis();
                logger.info("AG试玩创建新帐号-------username:" + username + "password:" + password + "billno:" + billno);
                // 检查并创建帐号  oddtype 表示下注金额 50-5000
                String msg = agService.CheckOrCreateGameAccout(username, password, "A", "CNY");
                if ("0".equals(msg)) {
                    logger.info("AGIN试玩创建新帐号-------username:" + username + "password:" + password );
                } else {
                    return BaseResponse.error("0", "error");
                }
            }
            
            // 获取游戏连接
            JSONObject data = new JSONObject();
            String sid = "AGIN" + System.currentTimeMillis();
            if ("mobile".equals(gameID)) {
                String url = agService.forwardMobileGame(username, password, ip, sid, "11");
                data.put("msg", url);
                data.put("type", "from");
            } else {
                String url = agService.forwardGame(username, password, ip, sid, "2");
                data.put("msg", url);
                data.put("type", "from");
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取AG游戏试玩链接业务异常:{}",e.getMessage());
            return BaseResponse.error("0", "调用获取AG游戏试玩链接业务异常");
        }
    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONObject forwardBGGame(String gameID, String model, String cagent,String refurl) {
        logger.info("调用BG游戏试玩跳转链接业务开始=====================START==================");
        try {
            //获取所有游戏配置信息
            List<PlatformConfigEntity> platformConfigs = platformConfigDao.findAll();
            if(CollectionUtils.isEmpty(platformConfigs)){
                logger.info("BG游戏维护中,获取BG游戏配置信息为空");
                return BaseResponse.error("process", "查询游戏配置信息失败");
            }
            
            //转成map
            Map<String,String> data = platformConfigs.stream().collect(Collectors.toMap(PlatformConfigEntity::getPlatformKey, PlatformConfigEntity::getPlatformConfig));
            if(CollectionUtils.isEmpty(data)){
                logger.info("BG游戏维护中,获取BG游戏配置信息为空");
                return BaseResponse.error("process", "查询游戏配置信息失败");
            }
            //格式化游戏配置信息
            formatPlatformConfig(data, cagent);
            
            String method = "";
            //获取试玩游戏链接
            if("1".equals(gameID)){
                method = "open.video.game.url";// 视讯
            }else{
                method = "open.lottery.game.url";// 彩票
            }
            PlatFromConfig pf = new PlatFromConfig();
            pf.InitData(data,"BG");
            if ("0".equals(pf.getPlatform_status())) {
                return BaseResponse.error("0", "process");
            }
            //调用第三方游戏接口
            BGGameServiceImpl gameService = new BGGameServiceImpl(JSONObject.fromObject(pf.getPlatform_config()));
            JSONObject response = gameService.openUserCommonAPI("", method, model,"", refurl);
            if(response.containsKey("code") && "success".equalsIgnoreCase(response.getString("code"))){
                //获取跳转地址成功
                JSONObject result = new JSONObject();
                String link = net.sf.json.JSONObject.fromObject(response.get("params")).get("result").toString();
                result.put("msg", link);
                result.put("type", "link");
                return result;
            }
            return BaseResponse.error("process", "调用第三方游戏试玩接口失败");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用BG游戏试玩跳转链接业务异常:{}",e.getMessage());
            return BaseResponse.error("0", "调用BG游戏试玩跳转链接业务异常");
        }
    }

    
    /**
     * 
     * @Description 格式化平台游戏配置信息
     * @param data
     * @param cagent
     */
    private void formatPlatformConfig(Map<String,String> data,String cagent){
        Iterator<String> iterator = data.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            String val = data.get(key);
            if(StringUtils.isNotBlank(val)){
                JSONObject jsonObject = JSONObject.fromObject(val);
                if(jsonObject.containsKey(cagent)){
                    val = jsonObject.getString(cagent);
                }else if(jsonObject.containsKey("ALL")){
                    val = jsonObject.getString("ALL");
                }
            }
            data.put(key, val);
        }
    }
}
