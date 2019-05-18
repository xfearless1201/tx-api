package com.cn.tianxia.api.web.v2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.cn.tianxia.api.common.v2.SystemConfigLoader;
import com.cn.tianxia.api.project.v2.OnlineUserEntity;
import com.cn.tianxia.api.service.v2.OnlineUserService;
import com.cn.tianxia.api.service.v2.TokenService;
import com.cn.tianxia.api.web.BaseController;

/**
 * 
 * @ClassName OnlineMenberController
 * @Description 在线会员接口
 * @author Hardy
 * @Date 2019年3月18日 下午1:44:02
 * @version 1.0.0
 */
@Controller
@RequestMapping("LoginMap") 
public class OnlineMenberController extends BaseController{
    
    @Autowired
    private SystemConfigLoader systemConfigLoader;
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private OnlineUserService onlineUserService;
    
    /**
     * 
     * @Description 获取用户在线人数
     * @param key
     * @param cagent
     * @return
     */
    @RequestMapping("/getUserList.do")  
    @ResponseBody
    public JSONObject getUserList(String key, String cagent) {
        logger.info("代理后台调用接口获取在线用户信息列表开始===============START==========================");
        //从配置文件中获取key
        String acckey = systemConfigLoader.getProperty("key"); 
        if(StringUtils.isBlank(key) || StringUtils.isBlank(acckey) || !key.equalsIgnoreCase(acckey)) return null;
        return onlineUserService.findAllByCagent(cagent);
    }
    
    
    /**
     * 
     * @Description 踢人下线
     * @param uid
     * @param key
     * @return
     */
    @RequestMapping("/shotOff.do")  
    @ResponseBody
    public String shotOff(String uid) {
        logger.info("后台调用接口踢人下线接口开始================START================");
        logger.info("shotOff(String uid {},String key ={} -start",uid);
        try {
            if(StringUtils.isBlank(uid)) return "faild";
            //解析用户ID
//            List<String> list = Arrays.asList(uids.split(","));
            OnlineUserEntity onlineUserEntity = onlineUserService.getByUid(uid);
            if(onlineUserEntity == null) return "faild";
            //获取token
            String token = onlineUserEntity.getToken();
            //修改用户在线信息
            onlineUserEntity.setLogoutTime(System.currentTimeMillis());
            onlineUserEntity.setIsOff((byte)1);
            onlineUserEntity.setOffStatus((byte)2);//离线类型 0 令牌超时 1 主动退出 2 后台踢人
            onlineUserService.insertOrUpdateOnlineUser(onlineUserEntity);
            //从缓存中踢掉用户信息
            tokenService.destroyToken(token);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("后台调用接口踢人下线接口异常:{}",e.getMessage());
            return "faild";
        }
    } 
    
}
