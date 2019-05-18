package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.vo.v2.PasswordVO;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName PasswordService
 * @Description 密码接口
 * @author Hardy
 * @Date 2019年2月1日 下午6:21:29
 * @version 1.0.0
 */
public interface PasswordService {

    /**
     * 
     * @Description 修改用户登录密码
     * @param passwordVO
     * @return
     */
    public JSONObject updateLoginPassword(PasswordVO passwordVO);
    
    /**
     * 
     * @Description 修改用户取款密码
     * @param passwordVO
     * @return
     */
    public JSONObject updateQkPassword(PasswordVO passwordVO);
    
    
    /**
     * 
     * @Description 检查用户取款密码
     * @return
     */
    public JSONObject checkQkpwd(String uid);
}
