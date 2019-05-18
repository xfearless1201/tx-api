package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.vo.v2.RegisterVO;

import net.sf.json.JSONObject;


/**
 * @Auther: zed
 * @Date: 2019/2/6 10:11
 * @Description: 用户注册接口
 */

public interface RegisterService {

    JSONObject verifyAccount(String cagent, String userName);

    JSONObject register(RegisterVO registerVO) throws Exception;
}
