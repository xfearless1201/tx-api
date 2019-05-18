package com.cn.tianxia.api.web.v2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.common.v2.CacheKeyConstants;
import com.cn.tianxia.api.common.v2.DatePatternConstant;
import com.cn.tianxia.api.common.v2.DatePatternUtils;
import com.cn.tianxia.api.common.v2.TokenUtils;
import com.cn.tianxia.api.po.v2.RegisterResponse;
import com.cn.tianxia.api.project.v2.OnlineUserEntity;
import com.cn.tianxia.api.service.v2.OnlineUserService;
import com.cn.tianxia.api.service.v2.RegisterService;
import com.cn.tianxia.api.service.v2.ShortMessageService;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.utils.v2.MD5Utils;
import com.cn.tianxia.api.vo.v2.RegisterVO;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/6 10:09
 * @Description: 用户注册controller
 */
@Controller
public class RegisterController extends BaseController {
    
    private final static Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private RegisterService registerService;
    
    @Autowired
    private ShortMessageService shortMessageService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private OnlineUserService onlineUserService;

    /**
     * 账号异步验证
     *
     * @param request
     * @param userName
     * @param cagent
     * @param async
     * @return
     */
    @RequestMapping("User/asyncVerify")
    @ResponseBody
    public JSONObject asyncVerify(HttpServletRequest request, String userName, String cagent, String async) {
        if (StringUtils.isBlank(async) && !"1".equals(async)) {
            return RegisterResponse.error(RegisterResponse.ERROR_CODE, "error", "异步验证账号标志不为1");
        }
        return registerService.verifyAccount(cagent, userName);
    }

    /**
     * 用户注册
     */
    @RequestMapping("User/register")
    @ResponseBody
    public JSONObject register(HttpSession session, HttpServletRequest request, HttpServletResponse response, RegisterVO registerVO) throws Exception{
            String address = IPTools.getIpCnAddress(request);
            String loginIp = IPTools.getIp(request);
            String isImgCode = registerVO.getIsImgCode();
            String imgcode = registerVO.getImgcode();
            String simgCode = "";//缓存验证码
            logger.info("调用注册接口请求参数报文:{}",registerVO.toString());
            if (!"0".equals(isImgCode)) {
                if (StringUtils.isBlank(imgcode)) {
                    return RegisterResponse.error("1","011","注册验证码不能为空");
                }
                // 验证码
                if (!redisUtils.hasKey(CacheKeyConstants.VALIDATE_CODE_SESSION + session.getId())) {
                    return RegisterResponse.error("1", "011", "请重新获取注册验证码");
                }
                simgCode = redisUtils.get(CacheKeyConstants.VALIDATE_CODE_SESSION + session.getId());
                redisUtils.delete(CacheKeyConstants.VALIDATE_CODE_SESSION + session.getId());
                //对比验证码
                if(!imgcode.equalsIgnoreCase(simgCode)){
                    return RegisterResponse.error("1", "012", "注册验证码错误");
                }
            }

            // 获取验证来源域名是否属于该代理平台
            String refererUrl = request.getHeader("referer").toLowerCase();
            if (StringUtils.isBlank(refererUrl)) {
                return RegisterResponse.error("1", "error", "来源域名不能为空");
            }
            //String sessionId = request.getSession().getId();
//            String key = CacheKeyConstants.LOGIN_USER_KEY_UID+sessionId;
//            if(jedisCluster.exists(key)){
//                logger.info("用户已登录,请不要重复注册");
//                Map<String,String> usermap = jedisCluster.hgetAll(key);
//                JSONObject result = new JSONObject();
//                result.put("userKey",usermap.get("userkey"));
//                result.put("userName",usermap.get("userName"));
//                result.put("msg","success");
//                return result;
//            }
            
            if(redisUtils.hasKey(CacheKeyConstants.REGIST_USER_KEY_UID+registerVO.getUserName())){
                logger.info("注册正在处理中,请不要重复提交...");
                return RegisterResponse.error("1", "error", "注册正在处理中,请不要重复提交");
            }
            redisUtils.set(CacheKeyConstants.REGIST_USER_KEY_UID+registerVO.getUserName(),registerVO.getUserName(),10);
            registerVO.setRefererUrl(refererUrl);
            registerVO.setSimgcode(simgCode);
            registerVO.setLoginIp(loginIp);// 登录IP
            registerVO.setAddress(address);// 登录域名地址

        try{
            JSONObject result = registerService.register(registerVO);

            return registerSuccess(result,request,response,registerVO);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("用户注册异常：" + e.getMessage());
            throw e;
        } finally {
            if(redisUtils.hasKey(CacheKeyConstants.REGIST_USER_KEY_UID+registerVO.getUserName())){
                redisUtils.delete(CacheKeyConstants.REGIST_USER_KEY_UID+registerVO.getUserName());
            }
        }
    }

    /**
     * 用户手机注册
     */
    @RequestMapping("Mobile/register.do")
    @ResponseBody
    public JSONObject mobileRegister(HttpServletRequest request, HttpSession session, HttpServletResponse response,
                                     RegisterVO registerVO) throws Exception{
        logger.info("用户手机注册Controller---------------------开始--------------------------");

        String address = IPTools.getIpCnAddress(request);
        String loginIp = IPTools.getIp(request);
        
        String sessionId = request.getSession().getId();
        
        if(redisUtils.hasKey(CacheKeyConstants.REGIST_USER_KEY_UID+sessionId)){
            logger.info("注册正在处理中,请不要重复提交...");
            return RegisterResponse.error("1", "error", "注册正在处理中,请不要重复提交");
        }
        redisUtils.set(CacheKeyConstants.REGIST_USER_KEY_UID+sessionId,sessionId,10);
            // 获取来源域名
        String refererUrl = request.getHeader("referer").toLowerCase();
        if (StringUtils.isBlank(refererUrl)) {
            return RegisterResponse.error("1", "error", "来源域名不能为空");
        }


        if (StringUtils.isBlank(registerVO.getMsgCode())) {
            registerVO.setMsgCode("1");
        }

        registerVO.setAddress(address);// 登录域名地址
        registerVO.setLoginIp(loginIp);// 登录IP
        registerVO.setRefererUrl(refererUrl);
        registerVO.setFrom("mobile");  //来源手机短信注册

        logger.info("调用手机号注册接口请求参数报文:{}",registerVO.toString());
        try {
            JSONObject result = registerService.register(registerVO);

            JSONObject returnToFront = registerSuccess(result,request,response,registerVO);

            if (result.containsKey("status") && result.getString("status").equals("error")) {
                return result;
            }
            //发送注册短信

            JSONObject sendResult = shortMessageService.sendRegisterSuccess(registerVO.getCagent(),registerVO.getMobileNo(),registerVO.getPassWord(),refererUrl);

            if (sendResult.containsKey("status") && "success".equals(sendResult.getString("status"))) {
                return returnToFront;
            } else {
                return RegisterResponse.error(RegisterResponse.ERROR_CODE,sendResult.getString("msg"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("用户手机注册异常：{}",e.getMessage());
            throw e;
        } finally {
            if(redisUtils.hasKey(CacheKeyConstants.REGIST_USER_KEY_UID+sessionId)){
                redisUtils.delete(CacheKeyConstants.REGIST_USER_KEY_UID+sessionId);
            }
        }
    }

    private JSONObject registerSuccess(JSONObject result,HttpServletRequest request,HttpServletResponse response,RegisterVO registerVO) throws Exception {

        if (result.containsKey("status") && result.getString("status").equals("error")) {
            return result;
        }
        
        JSONObject dataJson = result.getJSONObject("msg");
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        String sessionId = request.getSession().getId();//缓存ID
        //存入用户信息到缓存
        Map<String,String> redisMap = new HashMap<>();
        redisMap.put("uid",dataJson.getString("uid"));
        redisMap.put("userkey", token);
        redisMap.put("userName",dataJson.getString("username"));
        redisMap.put("ag_username", dataJson.getString("ag_username"));
        redisMap.put("hg_username", dataJson.getString("hg_username"));
        redisMap.put("ag_password", dataJson.getString("ag_password"));
        redisMap.put("loginmobile", dataJson.getString("loginmobile"));
        redisMap.put("cagent",dataJson.getString("cagent"));
        redisMap.put("cid",dataJson.getString("cid"));
        redisMap.put("typeid",dataJson.getString("typeid"));
        redisMap.put("login_time",dataJson.getString("login_time"));
        long times = DatePatternUtils.strToDate(dataJson.getString("login_time"), DatePatternConstant.NORM_DATETIME_PATTERN).getTime();
        redisMap.put("sessionid",sessionId);
        redisMap.put("ip",MD5Utils.md5toUpCase_16Bit(registerVO.getLoginIp()));
        redisMap.put("times",times+"");
        redisMap.put("Transfer","0");
        redisMap.put("WithDraw","0");
        redisMap.put("refurl",registerVO.getRefererUrl());
        redisMap.put("address",registerVO.getAddress());
        redisMap.put("isMobile",dataJson.getString("isMobile"));
        String tokenKey = TokenUtils.generatorToken(sessionId);
        redisMap.put("token", tokenKey);
        OnlineUserEntity onlineUserEntity = onlineUserService.getByUid(redisMap.get("uid"));
        if(onlineUserEntity == null){
            onlineUserEntity = new OnlineUserEntity();
        }
        onlineUserEntity.setAddress(registerVO.getAddress());
        onlineUserEntity.setIp(registerVO.getLoginIp());
        onlineUserEntity.setRefurl(registerVO.getRefererUrl());
        onlineUserEntity.setCagent(redisMap.get("cagent"));
        onlineUserEntity.setCid(Integer.parseInt(redisMap.get("cid")));
        onlineUserEntity.setIsMobile(Byte.parseByte(dataJson.getString("isMobile")));
        onlineUserEntity.setLoginTime(System.currentTimeMillis());
        onlineUserEntity.setUid(Long.parseLong(redisMap.get("uid")));
        onlineUserEntity.setToken(tokenKey);
        onlineUserService.insertOrUpdateOnlineUser(onlineUserEntity);
        kickUserOut(redisMap);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userKey",token);
        jsonObject.put("userName",dataJson.getString("username"));
        jsonObject.put("balance",dataJson.getString("balance"));
        jsonObject.put("msg","success");
        jsonObject.put("uid",dataJson.getString("uid"));
        return jsonObject;
    }

}
