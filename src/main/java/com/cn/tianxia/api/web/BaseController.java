package com.cn.tianxia.api.web;
 
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.cn.tianxia.api.common.v2.CacheKeyConstants;
import com.cn.tianxia.api.common.v2.ParamValidate;
import com.cn.tianxia.api.error.BusinessErrorEnum;
import com.cn.tianxia.api.error.BusinessException;
import com.cn.tianxia.api.error.CommonError;
import com.cn.tianxia.api.error.OriginalError;
import com.cn.tianxia.api.po.CommonReturnObject;
import com.cn.tianxia.api.service.v2.TokenService;
import com.cn.tianxia.api.utils.RedisUtils;

import redis.clients.jedis.JedisCluster;

/**
 * Controller基类
 */
public class BaseController {
    
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    
    protected final static SimpleDateFormat SDF =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    //在线用户
    public static ConcurrentHashMap<String,Map<String, String>> loginmaps=new ConcurrentHashMap<String,Map<String, String>>();
    // 注册验证码
    public static final ConcurrentHashMap<String, Long> regist = new ConcurrentHashMap<String, Long>();
    //支付回调
    public static ConcurrentHashMap<String,String> payMap=new ConcurrentHashMap<String,String>();
    //短信发送
    public static ConcurrentHashMap<String,String> msgMap=new ConcurrentHashMap<String,String>();
    //短信登录,累计密码错误次数记录
    public static ConcurrentHashMap<String,Integer> errorMap=new ConcurrentHashMap<String,Integer>();
    //现在人数缓存
    public static ConcurrentHashMap<String,Map<String, String>> onlineMap=new ConcurrentHashMap<String,Map<String, String>>();
    
    @Autowired
    private RedisUtils redisUtils;
    
    @Autowired
    private TokenService tokenService;
    
    
	/**
	 * 处理controller抛出的异常
	 * @param request
	 * @param ex 抛出的异常
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Object handleException(HttpServletRequest request, Exception ex) {

		//new 一个通用的返回类型
		CommonReturnObject commonReturnObject;
		ex.printStackTrace();   //打印异常方法栈
		if (ex instanceof BusinessException) {
			//如果抛出的是业务异常，返回业务异常的code 和 message
            BusinessException businessException = (BusinessException) ex;
            logger.error("业务异常===={}", businessException.getErrMsg());
            commonReturnObject = CommonReturnObject.error(businessException.getErrCode(),businessException.getErrMsg());
            CommonError error = businessException.getCommonError();
            if (error instanceof OriginalError) {
                commonReturnObject = CommonReturnObject.error((OriginalError) error);
            }
		} else {
			//如果抛出的是系统异常，返回未知错误
			commonReturnObject = CommonReturnObject.error(BusinessErrorEnum.UNKNOW_ERROR.getErrCode(),BusinessErrorEnum.UNKNOW_ERROR.getErrMsg());
		}

		return commonReturnObject;
	}

    /**
     * 从session中获取用户id
     * @param request
     * @return
     * @throws BusinessException
     */
	protected String getUidFromSession(HttpServletRequest request) throws BusinessException {
        Object uidObj = request.getSession().getAttribute("uid");
        ParamValidate.notNull(uidObj,"登录已过期,请重新登录");
        // 转换用户ID
        return Optional.of(uidObj).map(Object::toString).orElseThrow(()-> new BusinessException(OriginalError.getError(BusinessErrorEnum.PARAMMETER_VALIDATION_ERROR, "登录已过期，请重新登录")));
    }

    protected Object getLoggedInUserInfo(JedisCluster jedisCluster, HttpServletRequest request) throws BusinessException {
        String uid = getUidFromSession(request);
	    Object userInfoObj = jedisCluster.hgetAll(uid);
	    ParamValidate.notNull(userInfoObj, "登录已过期,请重新登录");
	    return userInfoObj;
    }
    
    
    protected Map<String,String> getUserInfoMap(RedisUtils redisUtils, HttpServletRequest request){
        return tokenService.getUserInfo(request);
    }
    
    /**
     * 
     * @Description 踢掉之前的用户下线
     * @param jedisCluster
     * @param request
     * @param data
     * @throws Exception 
     */
    protected String kickUserOut(Map<String,String> data) throws Exception {
        String token = data.get("token");
        //获取用户ID
        String uid = data.get("uid");
        //生成一个MD5加密的token
        String key = CacheKeyConstants.KICK_USER_KEY_UID + uid;
        if(redisUtils.hasKey(key)){
            //用户已存在
            String oldTokenKey = redisUtils.get(key);
            if(!oldTokenKey.equalsIgnoreCase(token)){
                //不是同一用户,踢掉之前的用户,并情况之前的所有缓存
                String oldKey = CacheKeyConstants.LOGIN_USER_KEY_TOKEN + oldTokenKey;
                redisUtils.delete(oldKey);
            }
        }
        //写入新的key
        String newKey = CacheKeyConstants.LOGIN_USER_KEY_TOKEN + token;
        //存储踢人的信息
        redisUtils.set(key,token,CacheKeyConstants.EXPIRE_TIME);
        //存入用户信息
        redisUtils.set(newKey,data,CacheKeyConstants.EXPIRE_TIME);//用户信息保存6个小时
        //在线用户信息
        String onlineKey = CacheKeyConstants.ONLINE_USER_KEY_UID + uid;
        redisUtils.set(onlineKey, uid,CacheKeyConstants.EXPIRE_TIME);
        return token;
    }
   
}
