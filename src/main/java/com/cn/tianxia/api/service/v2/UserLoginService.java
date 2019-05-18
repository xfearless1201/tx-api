package com.cn.tianxia.api.service.v2;

import java.util.Map;

import com.cn.tianxia.api.po.v2.ResultResponse;
import com.cn.tianxia.api.vo.v2.MobileLoginVO;
import com.cn.tianxia.api.vo.v2.UserLoginVO;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName UserLoginService
 * @Description 用户登录接口
 * @author Hardy
 * @Date 2019年2月6日 下午2:59:04
 * @version 1.0.0
 */
public interface UserLoginService {

    /**
     * 
     * @Description 登录接口
     * @param userLoginVO
     * @return
     */
    public JSONObject login(UserLoginVO userLoginVO);

    /**
     * 手机号登录接口
     * @param mobileLoginVO
     * @return
     */
    JSONObject mobileLogin(MobileLoginVO mobileLoginVO);
    
    public Map<String,Object> getUserInfo(String uid);
    
    public ResultResponse accountLogin(String username,String password,String isMobile,String refurl,String ip,String address,String sessionId);

}
