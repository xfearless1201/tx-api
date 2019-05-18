package com.cn.tianxia.api.common;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.cn.tianxia.api.web.BaseController; 

public class CustomHandlerExceptionResolver extends BaseController implements HandlerExceptionResolver{
    
    static String regEx = "[\u4e00-\u9fa5]";
    static Pattern pat = Pattern.compile(regEx);

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("e", e);
        //记录错误日志
        logger.info(e.getMessage()); 
        e.printStackTrace();
        HandlerMethod handlerMethod = (HandlerMethod) o; 
        if (WebUtils.isAjax(handlerMethod)) {
            Result result = new Result(); 
            result.setMsg("系统错误"); 
            MappingJackson2JsonView view = new MappingJackson2JsonView(); 
            view.setContentType("text/html;charset=UTF-8");
            return new ModelAndView(view, BeanUtils.toMap(result));
        }
        
        //这里可根据不同异常引起类做不同处理方式，本例做不同返回页面。
        String viewName = ClassUtils.getShortName(e.getClass());
        return new ModelAndView(viewName, model);
    } 
    
    public static boolean isContainsChinese(String str) {
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find()) {
            flg = true;
        }
        return flg;
    }
}
