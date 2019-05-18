package com.cn.tianxia.api.web.v2;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName TokenController
 * @Description 获取token接口
 * @author Hardy
 * @Date 2019年2月7日 下午12:28:02
 * @version 1.0.0
 */
@Controller
@RequestMapping("User")
public class TokenController extends BaseController{
    
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 
     * @Description 获取token
     * @param request
     * @param response
     * @param userKey
     * @return
     */
    @RequestMapping("/getToken")
    @ResponseBody
    public JSONObject getToken(HttpServletRequest request, HttpServletResponse response, String userKey) {
        logger.info("调用获取用户token接口开始================START===================");
        try {
            //判断请求参数
            if(StringUtils.isBlank(userKey)){
                UUID uuid = UUID.randomUUID();
                put(uuid.toString());
                return BaseResponse.success(uuid.toString());
            }
            //从缓存中获取用户token
            Map<String,String> usermap = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(usermap)){
                return BaseResponse.error("0", "error");
            }
            
            String userkey = usermap.get("userkey");
            if(userkey.equalsIgnoreCase(userKey)){
                UUID uuid = UUID.randomUUID();
                usermap.put("userkey", uuid.toString());
                return BaseResponse.success(uuid.toString());
            }
            return BaseResponse.error("0", "error");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取用户token接口异常:{}",e.getMessage());
            return BaseResponse.error("0", "error");
        }
    }
    
    private void put(String key) {
        Set<String> keys = regist.keySet();
        long time = new Date().getTime();
        if (!CollectionUtils.isEmpty(keys)) {
            for (String k : keys) {
                Long value = regist.get(k);
                if (time - value.intValue() >= 30000) {
                    regist.remove(k);
                }
            }
        }
        if (regist.containsKey(key)) {
            return;
        }
        regist.put(key, time);
    }
}
