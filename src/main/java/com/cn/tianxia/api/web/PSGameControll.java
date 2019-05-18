package com.cn.tianxia.api.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.service.UserService;

import net.sf.json.JSONObject;

/**
 * @ClassName PSGameControll
 * @Description PS回调认证接口
 * @author zw
 * @Date 2018年5月25日 下午4:51:55
 * @version 1.0.0
 */
@RequestMapping("PSGame")
@Controller
public class PSGameControll extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping("/auth.do")
    @ResponseBody
    public JSONObject PsAuth(HttpServletRequest request, HttpServletResponse response, String access_token, int step) {
        Map<String, String> infoMap = new HashMap<String, String>();
        Enumeration<String> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            infoMap.put(paraName, request.getParameter(paraName).toString());
        }
        logger.info("请求参数" + infoMap.toString());
        // status_code 1=Token无效 0=正确
        int status_code = 1;
        String member_id = "";

        JSONObject json = new JSONObject();

        json.put("status_code", status_code);
        json.put("member_id", member_id);

        if (StringUtils.isEmpty(access_token)) {
            return json;
        }

        try {
            // 查询token是否存在
            Map<String, String> authMap = userService.selectPSByauth(access_token);

            if (authMap == null || authMap.size() < 1) {
                return json;
            }

            member_id = authMap.get("ag_username").toString();

            // 检查未结束游戏
            if (step == 0) {
                userService.UpdatePSToken(access_token, step);
                status_code = 0;
            }
            // 登陆
            if (step == 1) {
                userService.UpdatePSToken(access_token, step);
                status_code = 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return json;
        }

        json.put("status_code", status_code);
        json.put("member_id", member_id);
        return json;
    }

}
