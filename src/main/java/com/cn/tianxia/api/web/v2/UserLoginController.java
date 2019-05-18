package com.cn.tianxia.api.web.v2;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.base.annotation.LogApi;
import com.cn.tianxia.api.common.v2.KeyConstant;
import com.cn.tianxia.api.common.v2.ResultResponse;
import com.cn.tianxia.api.common.v2.SystemConfigLoader;
import com.cn.tianxia.api.common.v2.TokenUtils;
import com.cn.tianxia.api.po.v2.LoginResponse;
import com.cn.tianxia.api.project.v2.OnlineUserEntity;
import com.cn.tianxia.api.service.v2.OnlineUserService;
import com.cn.tianxia.api.service.v2.TokenService;
import com.cn.tianxia.api.service.v2.UserLoginService;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.pay.RandomUtils;
import com.cn.tianxia.api.utils.v2.MD5Utils;
import com.cn.tianxia.api.vo.v2.MobileLoginVO;
import com.cn.tianxia.api.vo.v2.UserLoginVO;
import com.cn.tianxia.api.web.BaseController;

import cn.hutool.core.lang.UUID;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName UserLoginController
 * @Description 用户登录接口
 * @author Hardy
 * @Date 2019年2月6日 下午2:52:56
 * @version 1.0.0
 */
@Controller
public class UserLoginController extends BaseController{
    
    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private SystemConfigLoader systemConfigLoader;
    
    @Autowired
    private TokenService tokenService;

    @Autowired
    private OnlineUserService onlineUserService;
    
    /**
     * 
     * @Description 用户登录接口
     * @param request
     * @param response
     * @param tname
     * @param tpwd
     * @param savelogin
     * @param imgcode
     * @param isMobile
     * @param isImgCode
     * @return
     */
    @LogApi("用户登录")
    @RequestMapping("login.do")
    @ResponseBody
    public JSONObject pcLogin(HttpServletRequest request, HttpServletResponse response,String tname,String tpwd,
                                                    String savelogin,String imgcode,String isMobile,String isImgCode){
        logger.info("调用用户登录接口开始=================START==================");
        try {
            logger.info("登录请求参数,用户名:{},来源域名:{}",tname,request.getHeader("referer"));
            Map<String,String> usermap = tokenService.getUserInfo(request);
            if(!CollectionUtils.isEmpty(usermap)){
                logger.info("用户已登录,请不要重复登录");
                JSONObject data = new JSONObject();
                data.put("status", "ok");
                data.put("userKey", usermap.get("userkey"));
                data.put("userName",usermap.get("userName"));
                data.put("balance", usermap.get("balance"));
                data.put("integral",usermap.get("integral"));
                return data;
            }
            //获取当前sessionID
            String sessionId = request.getSession().getId();
            //判断请求参数
            if(StringUtils.isBlank(tname)){
                logger.info("请求数据异常:用户登录账号不能为空--->>>tname:{}",tname);
                return LoginResponse.faild("0", "请求数据异常:登录用户名不能为空");
            }
            
            if(StringUtils.isBlank(tpwd)){
                logger.info("请求数据异常:用户登录密码不能为空--->>>tpwd:{}",tpwd);
                return LoginResponse.faild("0", "请求数据异常:用户登录密码不能为空");
            }
            
            if(StringUtils.isBlank(isMobile)){
                isMobile = "0";//PC
            }
            
            //判断是否需要验证验证码,从配置文件中读取权限
            boolean isValidCode = true;//默认为真
            String controlUser = systemConfigLoader.getProperty(KeyConstant.CONTROL_USER_KEY);
            if(controlUser.equals(tname)){
                isValidCode = false;
            }
            logger.info("登录账号【"+tname+"】是否需要验证码,isimgCode: {}",imgcode);
            if(isValidCode && !"0".equals(isImgCode)){
                //需要验证验证码
                Object simgcodeObj = request.getSession().getAttribute("imgcode");//从缓存中获取验证码
                if(ObjectUtils.allNotNull(simgcodeObj)){
                    String simgcode = String.valueOf(simgcodeObj);
                    if(StringUtils.isBlank(imgcode)){
                        logger.info("请求参数异常:验证码不能为空--->>>imgcode:{}",imgcode);
                        return LoginResponse.faild("0", "请求参数异常:验证码不能为空");
                    }
                    
                    if(!simgcode.equalsIgnoreCase(imgcode)){
                        logger.info("请求参数异常:验证码输入不正确--->>>imgcode:{}",imgcode);
                        return LoginResponse.faild("0", "请求参数异常:验证码输入不正确");
                    }
                }else{
                    logger.info("从缓存中获取验证码失败");
                    return LoginResponse.faild("0", "获取验证码失败,请重新刷新图片验证码");
                }
            }
            
            //获取请求IP 和 请求域名
            String refurl = request.getHeader("referer");
            if(StringUtils.isBlank(refurl)){
                logger.info("获取请求域名失败");
                return LoginResponse.faild("0", "域名不匹配,获取请求域名失败");
            }
            String ip = IPTools.getIp(request);
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
            String address = IPTools.getIpCnAddress(request);
            UserLoginVO userLoginVO = new UserLoginVO();
            userLoginVO.setUsername(tname);
            userLoginVO.setPassword(tpwd);
            userLoginVO.setIsMobile(isMobile);
            userLoginVO.setAddress(address);
            userLoginVO.setRefurl(refurl);
            userLoginVO.setIp(ip);
            JSONObject result = userLoginService.login(userLoginVO);
            if(result.containsKey("status") && "ok".equalsIgnoreCase(result.getString("status"))){
                refurl = refurl.split("/")[2];
                Map<String,String> redisMap = (Map<String, String>) result.remove("cacheJson");
//                logger.info("存入redis缓存数据报文:{}",JSONObject.fromObject(redisMap).toString());
                redisMap.put("sessionid",sessionId);
                redisMap.put("ip",MD5Utils.md5toUpCase_16Bit(ip));
                redisMap.put("refurl",refurl);
                redisMap.put("address",address);
                redisMap.put("isMobile",isMobile);
                logger.info("登录接口返回用户缓存ID:{}",sessionId);
                String token = TokenUtils.generatorToken(sessionId);
                redisMap.put("token",token);
                //返回token令牌
                OnlineUserEntity onlineUserEntity = onlineUserService.getByUid(redisMap.get("uid"));
                if(onlineUserEntity == null){
                    onlineUserEntity = new OnlineUserEntity();
                }
                onlineUserEntity.setAddress(address);
                onlineUserEntity.setIp(ip);
                onlineUserEntity.setRefurl(refurl);
                onlineUserEntity.setCagent(redisMap.get("cagent"));
                onlineUserEntity.setCid(Integer.parseInt(redisMap.get("cid")));
                onlineUserEntity.setIsMobile(Byte.parseByte(isMobile));
                onlineUserEntity.setLoginTime(System.currentTimeMillis());
                onlineUserEntity.setUid(Long.parseLong(redisMap.get("uid")));
                onlineUserEntity.setOffStatus((byte)0);
                onlineUserEntity.setIsOff((byte)0);
                onlineUserEntity.setToken(token);
                onlineUserService.insertOrUpdateOnlineUser(onlineUserEntity);
                kickUserOut(redisMap);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用用户登录接口异常:{}",e.getMessage());
            return LoginResponse.faild("0", "调用用户登录接口异常");
        }finally {
            request.getSession().setAttribute("isreg", "");
            request.getSession().setAttribute("imgcode", "");
        }
    }

    @RequestMapping("Mobile/login.do")
    @ResponseBody
    public JSONObject mobileLogin(HttpServletRequest request, HttpServletResponse response,String cagent,String mobileNo,String msgCode,String isMobile) {
        logger.info("调用手机号登录接口======================START============================");
        try {
            logger.info("手机号登录请求参数,手机号:" + mobileNo + ",来源域名:{},验证码:{}",request.getHeader("referer"),msgCode);
            Map<String,String> usermap = tokenService.getUserInfo(request);
            if(!CollectionUtils.isEmpty(usermap)){
                logger.info("用户已登录,请不要重复登录");
                JSONObject data = new JSONObject();
                data.put("status", "ok");
                data.put("userKey", usermap.get("userkey"));
                data.put("userName",usermap.get("userName"));
                data.put("balance", usermap.get("balance"));
                data.put("integral",usermap.get("integral"));
                return data;
            }
            //获取当前sessionID
            String sessionId = request.getSession().getId();
            //判断请求参数
            if(StringUtils.isBlank(mobileNo)){
                logger.info("请求数据异常:用户登录手机号不能为空--->>>mobileNo:{}",mobileNo);
                return LoginResponse.faild("0", "请求数据异常:登录手机号不能为空");
            }

            if(StringUtils.isBlank(msgCode)){
                logger.info("请求数据异常:用户登录短信验证码不能为空--->>>msgCode:{}",msgCode);
                return LoginResponse.faild("0", "请求数据异常:用户登录短信验证码不能为空");
            }

            if(StringUtils.isBlank(isMobile)){
                isMobile = "0";//PC
            }

            //获取请求IP 和 请求域名
            String refurl = request.getHeader("referer");
            if(StringUtils.isBlank(refurl)){
                logger.info("获取请求域名失败");
                return LoginResponse.faild("0", "域名不匹配,获取请求域名失败");
            }
            String ip = IPTools.getIp(request);
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
            String address = IPTools.getIpCnAddress(request);

            MobileLoginVO mobileLoginVO = new MobileLoginVO();
            mobileLoginVO.setMobileNo(mobileNo);
            mobileLoginVO.setMsgCode(msgCode);
            mobileLoginVO.setCagent(cagent);
            mobileLoginVO.setIsMobile(isMobile);
            mobileLoginVO.setIp(ip);
            mobileLoginVO.setAddress(address);
            mobileLoginVO.setRefurl(refurl);
            JSONObject result = userLoginService.mobileLogin(mobileLoginVO);
            if(result.containsKey("status") && "ok".equalsIgnoreCase(result.getString("status"))){
                //登录成功,防止重复登录
                refurl = refurl.split("/")[2];
                //存入用户信息到缓存
                Map<String,String> redisMap =  (Map<String, String>) result.remove("cacheJson");
                redisMap.put("sessionid",sessionId);
                redisMap.put("ip",MD5Utils.md5toUpCase_16Bit(ip));
                redisMap.put("refurl",refurl);
                redisMap.put("address",address);
                redisMap.put("isMobile",isMobile);
                //返回token令牌
                String token = TokenUtils.generatorToken(sessionId);
                redisMap.put("token",token);
                OnlineUserEntity onlineUserEntity = onlineUserService.getByUid(redisMap.get("uid"));
                if(onlineUserEntity == null){
                    onlineUserEntity = new OnlineUserEntity();
                }
                onlineUserEntity.setAddress(address);
                onlineUserEntity.setIp(MD5Utils.md5toUpCase_16Bit(ip));
                onlineUserEntity.setRefurl(refurl);
                onlineUserEntity.setCagent(redisMap.get("cagent"));
                onlineUserEntity.setCid(Integer.parseInt(redisMap.get("cid")));
                onlineUserEntity.setIsMobile(Byte.parseByte(isMobile));
                onlineUserEntity.setLoginTime(System.currentTimeMillis());
                onlineUserEntity.setUid(Long.parseLong(redisMap.get("uid")));
                onlineUserEntity.setToken(token);
                onlineUserService.insertOrUpdateOnlineUser(onlineUserEntity);
                kickUserOut(redisMap);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用用户登录接口异常:{}",e.getMessage());
            return LoginResponse.faild("0", "调用用户登录接口异常");
        }
    }
    
    @RequestMapping("checklogin.do")
    @ResponseBody
    public JSONObject verifyIsLogin(HttpServletRequest request, HttpServletResponse response){
        logger.info("调用检查是否登录接口开始=================START======================");
        JSONObject data = new JSONObject();
        try {
            Map<String,String> user = tokenService.getUserInfo(request);
            if(CollectionUtils.isEmpty(user)){
                data.put("msg", "faild");
                return data;
            }else{
                //获取用户信息
                data.put("userkey", user.get("userkey"));
                data.put("userName",user.get("userName"));
                //查询用户当前钱包余额
                String uid = user.get("uid");
                Map<String,Object> balanceMap = userLoginService.getUserInfo(uid);
                data.put("balance", balanceMap.get("balance"));
                data.put("integral",balanceMap.get("integral"));
                data.put("msg", "success");
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("校验用户是否登录异常:{}",e.getMessage());
            data.put("msg", "faild");
            return data;
        }
    }
    
    
    

    /**
     * 退出系统
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping("logout.do")
    @ResponseBody
    public JSONObject logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception{
        try {
            //销毁缓存信息
            //获取用户sessionId
            String sessionId = request.getSession().getId();
            //校验签名
            String token = TokenUtils.generatorToken(sessionId);
            tokenService.destroyToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("退出登录异常:{}",e.getMessage());
        }finally {
            request.getSession().invalidate();
        }
        return new JSONObject();
    }
    
    @RequestMapping("pc/login.do")
    @ResponseBody
    public ResultResponse pcLogin(HttpServletRequest request, HttpServletResponse response,String tname,String tpwd) {
        logger.info("调用手机号登录接口======================START============================");
        try {
            //返回token令牌
            Map<String,String> data = new HashMap<>();
            String sessionId = UUID.randomUUID().toString();
            String ip = IPTools.getIp(request);
            String refurl = request.getHeader("referer").split("/")[2];
            String address = IPTools.getIpCnAddress(request);
            data.put("sessionid",sessionId);
            data.put("ip",MD5Utils.md5toUpCase_16Bit("127.0.0.1"));
            data.put("refurl",refurl);
            data.put("address",address);
            data.put("isMobile","1");
            data.put("username", tname);
            String num = RandomUtils.generateNumberStr(5);
            data.put("uid", num);
            data.put("cid", num);
            data.put("cagent", "XPJ");
            String token = TokenUtils.generatorToken(sessionId);
            data.put("token", token);
            OnlineUserEntity onlineUserEntity = onlineUserService.getByUid(num);
            if(onlineUserEntity == null){
                onlineUserEntity = new OnlineUserEntity();
            }
            onlineUserEntity.setAddress(address);
            onlineUserEntity.setIp(ip);
            onlineUserEntity.setRefurl(refurl);
            onlineUserEntity.setCagent("XPJ");
            onlineUserEntity.setCid(Integer.parseInt(num));
            onlineUserEntity.setIsMobile((byte)1);
            onlineUserEntity.setLoginTime(System.currentTimeMillis());
            onlineUserEntity.setUid(Long.parseLong(num));
            onlineUserEntity.setToken(token);
            onlineUserService.insertOrUpdateOnlineUser(onlineUserEntity);
            kickUserOut(data);
            return ResultResponse.success("登录成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultResponse.error("登录异常");
        }
        
    }
}
