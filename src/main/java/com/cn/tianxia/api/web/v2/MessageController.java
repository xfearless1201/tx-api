package com.cn.tianxia.api.web.v2;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.common.v2.CacheKeyConstants;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.service.v2.ShortMessageService;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.vo.v2.ChangeMobileVO;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/8 13:53
 * @Description: 短信类接口Controller
 */
@Controller
@RequestMapping("Mobile")
public class MessageController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private ShortMessageService shortMessageService;
    
    @Autowired
    private RedisUtils redisUtils;

    /**
     *
     * @Description 发送注册验证码
     *
     * @param cagent 代理商编号
     * @param mobileNo 手机号
     *
     */
    @RequestMapping("sendRegirstCode.do")
    @ResponseBody
    public JSONObject sendRegisterCode(HttpServletRequest request, String cagent, String mobileNo) {
        logger.info("手机注册发送手机短信验证码接口--------------------------开始--------------------------");
        String key = CacheKeyConstants.SEND_PHONE_KEY_CODE+cagent+mobileNo;
        try {
            //防止重复提交
            if(redisUtils.hasKey(key)){
                return BaseResponse.error(BaseResponse.ERROR_CODE, "error");
            }
            redisUtils.set(key,mobileNo,10);
            String refererUrl = request.getHeader("referer");
            if (StringUtils.isBlank(refererUrl)) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "error");
            }
            return shortMessageService.sendRegisterCode(cagent, mobileNo, refererUrl);
        } catch (Exception e) {
            logger.error("手机注册发送手机短信验证码接口异常:{}", e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE,"error");
        } finally {
            if(redisUtils.hasKey(key)){
                redisUtils.delete(key);
            }
        }
    }

    /**
     *
     * @Description 发送注册验证码
     *
     * @param cagent 代理商编号
     * @param mobileNo 手机号
     *
     */
    @RequestMapping("sendLoginCode.do")
    @ResponseBody
    public JSONObject sendLoginCode(HttpServletRequest request, String cagent, String mobileNo) {
        logger.info("手机用户登录发送手机短信验证码接口--------------------------开始--------------------------");
        String key = CacheKeyConstants.SEND_PHONE_KEY_CODE+cagent+mobileNo;
        try {
            //防止重复提交
            if(redisUtils.hasKey(key)){
                return BaseResponse.error(BaseResponse.ERROR_CODE, "error");
            }
            redisUtils.set(key,mobileNo,10);

            String refererUrl = request.getHeader("referer");
            if (StringUtils.isBlank(refererUrl)) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "error");
            }

            return shortMessageService.sendLoginCode(cagent, mobileNo, refererUrl);
        } catch (Exception e) {
            logger.error("手机用户登录发送手机短信验证码接口异常:{}", e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE,"error");
        } finally {
            if(redisUtils.hasKey(key)){
                redisUtils.delete(key);
            }
        }
    }

    /**
     *
     * @Description 发送绑定手机验证码
     *
     * @param cagent 代理商编号
     * @param mobileNo 手机号
     *
     */
    @RequestMapping("sendChangeCode.do")
    @ResponseBody
    public JSONObject sendChangeCode(HttpServletRequest request, String cagent, String mobileNo) {
        logger.info("手机用户绑定手机发送手机短信验证码接口--------------------------开始--------------------------");
        String key = CacheKeyConstants.SEND_PHONE_KEY_CODE+cagent+mobileNo;
        try {
          //防止重复提交
            if(redisUtils.hasKey(key)){
                return BaseResponse.error(BaseResponse.ERROR_CODE, "error");
            }
            redisUtils.set(key,mobileNo,10);

            String refererUrl = request.getHeader("referer");
            if (StringUtils.isBlank(refererUrl)) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "error");
            }

            return shortMessageService.sendChangeCode(cagent, mobileNo, refererUrl);
        } catch (Exception e) {
            logger.error("手机用户绑定手机发送手机短信验证码接口异常:{}", e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE,"error");
        } finally {
            if(redisUtils.hasKey(key)){
                redisUtils.delete(key);
            }
        }
    }

    /**
     * 修改绑定手机
     *
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value = "changeMobile.do")
    @ResponseBody
    public JSONObject changeMobile(HttpServletRequest request, HttpSession session,
                                   ChangeMobileVO changeMobileVO) {
        logger.info("用户修改绑定手机接口--------------------------开始--------------------------");
        try {

            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                return BaseResponse.error( BaseResponse.ERROR_CODE , "用户未登录");
            }
            String uid = map.get("uid");
            String ip = IPTools.getIp(request);
            String userName = String.valueOf(session.getAttribute("userName"));
            String loginMobile = String.valueOf(session.getAttribute("loginmobile"));
            changeMobileVO.setUid(uid);
            changeMobileVO.setUserName(userName);
            changeMobileVO.setLoginMobile(loginMobile);
            changeMobileVO.setIp(ip);

            return shortMessageService.changeMobile(changeMobileVO);
        } catch (Exception e) {
            logger.error("用户修改绑定手机接口接口异常:{}", e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE,"error");
        }
    }

}
