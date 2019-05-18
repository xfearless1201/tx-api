package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.project.OrderQuery;
import com.cn.tianxia.api.vo.v2.BetInfoVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName GameBetService
 * @Description 游戏注单接口
 * @author Hardy
 * @Date 2019年1月30日 下午8:55:39
 * @version 1.0.0
 */
public interface GameBetService {

    public JSONArray getGameBetInfo(BetInfoVO betInfoVO);
    
    /**
     * 
     * @Description 获取BG游戏注单订单
     * @param orderQuery
     * @return
     */
    public JSONObject getBGBetOrder(OrderQuery orderQuery);
}
