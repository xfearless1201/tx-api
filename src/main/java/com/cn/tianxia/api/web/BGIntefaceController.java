package com.cn.tianxia.api.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.project.OrderQuery;
import com.cn.tianxia.api.service.v2.GameBetService;
import com.cn.tianxia.api.service.v2.LotteryDrawService;
import com.cn.tianxia.api.service.v2.TryGameService;

import net.sf.json.JSONObject;

/**
 * 
 * @author zw
 *
 */

@Controller
@RequestMapping("/bg")
public class BGIntefaceController extends BaseController {
	@Autowired
	private TryGameService tryGameService;
	
	@Autowired
	private GameBetService gameBetService;
	
	@Autowired
	private LotteryDrawService lotteryDrawService;


	/***
	 * 彩票开奖结果走势图查询
	 * 
	 * @param request
	 * @param response
	 * @param uid
	 * @param lotteryId
	 * @param method
	 * @param isMobile
	 * @param pageSize
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping("/lotteryCheck")
	@ResponseBody
	public net.sf.json.JSONObject lotteryCheck(HttpServletRequest request, HttpServletResponse response,String token, String lotteryId,
			String method, String isMobile, String pageSize, String startTime, String endTime) {
	    logger.info("调用获取BG游戏彩票开奖信息接口开始=================START=================");
		net.sf.json.JSONObject data = new net.sf.json.JSONObject();
	    try {
	        data = lotteryDrawService.lotteryDrawIGGame(lotteryId,null);
//	        logger.info("调用获取BG游戏彩票开奖信息响应结果:{}",data.toString());
	        return data;
	    } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取BG游戏彩票查询接口异常:{}",e.getMessage());
            data.put("msg", "error");
            return data;
        }
	}

	/**
	 * 限制只查代理的用户注单(open.order.agent.query)
	 * 
	 * @param request
	 * @param response
	 * @param orderQuery
	 * @return
	 */
	@RequestMapping("/orderAgentQuery")
	@ResponseBody
	public JSONObject orderAgentQuery(HttpServletRequest request, HttpServletResponse response, OrderQuery orderQuery) {
		logger.info("调用获取BG游戏注单接口开始====================START=========================");
	    try {
	        return gameBetService.getBGBetOrder(orderQuery);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取BG游戏注单接口异常:{}",e.getMessage());
            return BaseResponse.error("error", "调用获取BG游戏注单接口异常");
        }
	}

	/**
	 * bg 试玩连接
	 * 
	 * @param request
	 * @param response
	 * @param gameID
	 * @param model
	 * @return
	 */
	@RequestMapping("/bgTrialGame")
	@ResponseBody
	public JSONObject bgTrialGame(HttpServletRequest request, HttpServletResponse response, String gameID,
			String model,String agent) {
	    logger.info("调用BG游戏试玩接口开始=================START==================");
	    try {
	        //代理号不能为空
	        if(StringUtils.isBlank(agent)){
	            logger.info("请求参数异常:平台编码不能为空");
	            return BaseResponse.error("0", "error");
	        }
	        
	        if(StringUtils.isBlank(model)){
	            logger.info("请求参数异常:游戏类型不能为空");
	            return BaseResponse.error("0", "error");
	        }
	        
	        if(StringUtils.isBlank(gameID)){
	            logger.info("请求参数异常:游戏类型ID不能为空");
	            return BaseResponse.error("0", "error");
	        }
	        
	        StringBuffer url = request.getRequestURL();  
	        String refurl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
	        return tryGameService.forwardBGGame(gameID, model, agent, refurl);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用BG游戏试玩接口异常:{}",e.getMessage());
            return BaseResponse.error("error", "调用BG游戏试玩接口异常");
        }
	}

}
