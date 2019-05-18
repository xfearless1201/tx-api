/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下网络 
 *    http://www.d-telemedia.com/
 *
 *    Package:     com.cn.tianxia.controller 
 *
 *    Filename:    GameControl.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下网络科技 
 *
 *    @author: Wilson
 *
 *    @version: 1.0.0
 *
 *    Create at:   2018年10月25日 21:24 
 *
 *    Revision: 
 *
 *    2018/10/25 21:24 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.service.IGameService;

import net.sf.json.JSONObject;

/**
 * @ClassName GameControl
 * @Description 游戏相关接口
 * @Author Wilson
 * @Date 2018年10月25日 21:24
 * @Version 1.0.0
 **/
@Controller
@RequestMapping("game")
@Scope("prototype")
public class GameControl {

    @Autowired
    private IGameService gameService;

    @RequestMapping("/getGameList")
    @ResponseBody
    public Object getGameList(HttpServletRequest request,String gameType,String pageSize,String pageNo,String gameName){
        Map<String,Object> params=new HashMap<>();
        JSONObject rtnMsg=new JSONObject();
        if(StringUtils.isBlank(gameType)){
            rtnMsg.put("mssage","游戏类型不能为空");
            return rtnMsg;
        }
        if(StringUtils.isBlank(pageSize)){
            rtnMsg.put("mssage","每页记录数不能为空");
            return rtnMsg;
        }
        if(StringUtils.isBlank(pageNo)){
            rtnMsg.put("mssage","页码不能为空");
            return rtnMsg;
        }

        if (StringUtils.isNotBlank(gameName)){
            params.put("gameName", gameName);
        }

        params.put("gameType",gameType);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);

        List<Map<String, String>> gameLists = gameService.selectByGameType(params);
        return gameLists;
    }
}