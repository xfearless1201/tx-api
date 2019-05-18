package com.cn.tianxia.api.service.v2;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @ClassName LotteryDrawService
 * @Description 彩票开奖接口
 * @author Hardy
 * @Date 2019年3月8日 上午11:26:59
 * @version 1.0.0
 */
public interface LotteryDrawService {

    /**
     * 
     * @Description BG彩票开间查询
     * @param token
     * @param lotteryId
     * @param method
     * @param isMobile
     * @param pageSize
     * @param startTime
     * @param endTime
     * @return
     */
    public JSONObject lotteryDrawBGGame(String token, String lotteryId,String method, String isMobile, String pageSize, String startTime, String endTime);

    public net.sf.json.JSONObject lotteryDrawIGGame(String lotteryId,String dateTime);
    
    
}
