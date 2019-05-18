package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.vo.v2.ChangeMobileVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/7 19:59
 * @Description: 手机短信服务接口
 */
public interface ShortMessageService {
    /**
     * 发送注册成功短信
     */
    JSONObject sendRegisterSuccess(String cagent,String mobileNo,String passWord,String url);

    /**
     * 发送注册验证码
     * @param cagent
     * @param mobileNo
     * @param refererUrl
     * @return
     */
    JSONObject sendRegisterCode(String cagent, String mobileNo, String refererUrl);

    /**
     * 发送手机登录验证码
     * @param cagent
     * @param mobileNo
     * @param refererUrl
     * @return
     */
    JSONObject sendLoginCode(String cagent, String mobileNo, String refererUrl);

    /**
     * 发送绑定手机验证码
     * @param cagent
     * @param mobileNo
     * @param refererUrl
     * @return
     */
    JSONObject sendChangeCode(String cagent, String mobileNo, String refererUrl);

    /**
     * 用户修改绑定手机
     * @return
     */
    JSONObject changeMobile(ChangeMobileVO changeMobileVO);

    /**
     * 验证短信验证码
     */
    JSONObject verifyMsgCode(String cagent, String mobileNo, String msgCode, String type, int minutes);
}
