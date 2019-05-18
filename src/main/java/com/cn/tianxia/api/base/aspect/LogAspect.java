/******************************************************************
 *
 *    Powered By tianxia-online.
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下网络
 *    http://www.d-telemedia.com/
 *
 *    Package:     com.tianxia.business.api.service.aop
 *
 *    Filename:    GetpidAspect.java
 *
 *    Description: 通过切面获取平台ID
 *
 *    Copyright:   Copyright (c) 2018-2020
 *
 *    Company:     天下网络科技
 *
 *    @author: HH
 *
 *    @version: 1.0.0
 *
 *    Create at:   2018年7月6日 下午4:52:29
 *
 *    Revision:
 *
 *    2018年7月6日 下午4:52:29
 *        - first revision
 *
 *****************************************************************/
package com.cn.tianxia.api.base.aspect;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @ClassName LogAspect
 * @Description 日志系统
 * @author Hardy
 * @Date 2019年5月11日 下午2:29:57
 * @version 1.0.0
 */
@Aspect
@Component
public class LogAspect {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("@annotation(com.cn.tianxia.api.base.annotation.LogApi)")
    public void aspect() {
    }

    @Before("aspect()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        String CLASS_METHOD=joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        Map<String, String> maps = new HashMap<String, String>();
        maps.put("URL",request.getRequestURL().toString());
        maps.put("HTTP_METHOD",request.getMethod());
        maps.put("IP",request.getRemoteAddr());

        Enumeration enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            maps.put(paraName, request.getParameter(paraName));
        }
        logger.info("\n 执行方法：[{}] \n 请求参数：{}",CLASS_METHOD,JSONObject.toJSONString(maps).toString());
    }

    @AfterReturning(returning = "ret", pointcut = "aspect()")
    public void doAfterReturning(JoinPoint joinPoint,Object ret) throws Throwable {
        // 处理完请求，返回内容
        System.out.println("方法的返回值 : " + ret);
        logger.info("\n 执行方法：[{}] \n 返回结果：{}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),
                JSONObject.toJSONString(ret).toString());
    }

}
