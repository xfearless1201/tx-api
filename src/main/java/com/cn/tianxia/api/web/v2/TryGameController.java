package com.cn.tianxia.api.web.v2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.service.v2.TryGameService;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName TryGameController
 * @Description 试玩游戏接口
 * @author Hardy
 * @Date 2019年2月6日 上午10:21:19
 * @version 1.0.0
 */
@Controller
@RequestMapping("DemoPlay")
public class TryGameController extends BaseController{
    
    @Autowired
    private TryGameService tryGameService;
    
    

    /**
     * 
     * @Description 获取试玩游戏链接
     * @param request
     * @param response
     * @param cagent
     * @param gameType
     * @param gameID
     * @param accountCode
     * @param model
     * @return
     */
    @RequestMapping("/IG")
    @ResponseBody
    public JSONObject forwardGame(HttpServletRequest request, HttpServletResponse response, String cagent,
            String gameType, String gameID, String accountCode, String model) {
        logger.info("调用获取试玩游戏链接接口开始=================START==================");
        try {
            
            String ip = IPTools.getIp(request);
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
            
            //判断请求参数
            if(StringUtils.isBlank(cagent) || cagent.length() > 3){
                logger.info("请求参数平台编码异常-->>cagent:{}",cagent);
                return BaseResponse.error("0", "error");
            }
            
            if(StringUtils.isBlank(gameType) || "IGLOTTO".equals(gameType)){
                gameType = "LOTTO";
            }else if("IGLOTTERY".equals(gameType)){
                gameType = "LOTTERY";
                //判断游戏ID
                if(StringUtils.isBlank(gameID)){
                    logger.info("请求参数游戏ID异常-->>gameID:{}",gameID);
                }
            }else{
                logger.info("请求参数游戏类型异常-->>gameType:{}",gameType);
                return BaseResponse.error("0", "error");
            }
            
            if(StringUtils.isBlank(accountCode)){
                accountCode = "0";
            }
            
            if(StringUtils.isBlank(model)){
                model = "PC";
            }
            return tryGameService.forwardIGGame(cagent, gameType, gameID, accountCode, model,ip);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取试玩游戏链接接口异常:{}",e.getMessage());
            return BaseResponse.error("0", "调用获取试玩游戏链接接口异常");
        }
    }
    
    /**
     * 
     * @Description 获取AG游戏试玩链接
     * @param request
     * @param response
     * @param cagent
     * @param gameType
     * @param gameID
     * @param accountCode
     * @param model
     * @return
     */
    @RequestMapping("/AGIN")
    @ResponseBody
    public JSONObject forwardAGGame(HttpServletRequest request, HttpServletResponse response, String cagent,
            String gameType, String gameID, String accountCode, String model) {
        logger.info("调用获取AG游戏试玩链接接口开始==============START=================");
        try {
            //获取用户请求IP
            String ip = IPTools.getIp(request);
            if(StringUtils.isBlank(ip)){
                ip = "1270.0.1";
            }
            
            if(StringUtils.isBlank(gameType)){
                logger.info("请求参数异常--->>>gameType:{}",gameType);
                return BaseResponse.error("0", "error");
            }
            
            if(StringUtils.isBlank(cagent) || cagent.length() > 3){
                logger.info("请求参数异常--->>>cagent：{}",cagent);
                return BaseResponse.error("0", "error");
            }
            
            if(StringUtils.isBlank(accountCode)){
                logger.info("请求参数异常--->>>accountCode：{}",accountCode);
                return BaseResponse.error("0", "error");
            }
            
            return tryGameService.forwardAGGame(cagent, gameType, gameID, accountCode, model,ip);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取AG游戏试玩链接接口异常:{}",e.getMessage());
            return BaseResponse.error("0", "调用获取试玩AG游戏链接接口异常");
        }
    }
}
