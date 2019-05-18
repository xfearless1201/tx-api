package com.cn.tianxia.api.service.v2;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName TryGameService
 * @Description 试玩游戏接口
 * @author Hardy
 * @Date 2019年2月6日 上午11:16:43
 * @version 1.0.0
 */
public interface TryGameService {

    
    /**
     * 
     * @Description 获取IG游戏试玩链接
     * @param cagent
     * @param gameType
     * @param gameID
     * @param accountCode
     * @param model
     * @return
     */
    public JSONObject forwardIGGame(String cagent,String gameType, String gameID, String accountCode, String model,String ip);
    
    /**
     * 
     * @Description 获取AG游戏试玩链接
     * @param cagent
     * @param gameType
     * @param gameID
     * @param accountCode
     * @param model
     * @return
     */
    public JSONObject forwardAGGame(String cagent,String gameType, String gameID, String accountCode, String model,String ip);

    
    /**
     * 
     * @Description 获取BG游戏试玩链接
     * @param gameID
     * @param model
     * @param agent
     * @param refurl
     * @return
     */
    public JSONObject forwardBGGame(String gameID,String model,String agent,String refurl);
}
