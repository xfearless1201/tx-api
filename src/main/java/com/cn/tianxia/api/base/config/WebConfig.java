package com.cn.tianxia.api.base.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cn.tianxia.api.domain.txdata.v2.RefererUrlDao;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.service.v2.TokenService;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.v2.MD5Utils;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private RefererUrlDao refererUrlDao;
    
    @Value("${excludePaths}")
    private String excludePaths;

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        HandlerInterceptor handlerInterceptor = new HandlerInterceptor() {

            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {
                response.setContentType("application/json;charset=UTF-8");

                String url = request.getRequestURI();
                //过滤掉的路径
                List<String> urls = Arrays.asList(excludePaths.split(","));
                for(String u : urls){
                    if(url.contains(u)) return true;
                }

                // 验证白名单
                boolean result = verifyUrl(request);
                if (!result) {
                    printMessage(response, BaseResponse.faild("faild", "非法域名，无权访问").toString());
                    return result;
                }

                Map<String,String> user = tokenService.getUserInfo(request);
                if(CollectionUtils.isEmpty(user)){
                    //未登录,无权访问
                    request.getSession().invalidate();
                    printMessage(response, BaseResponse.faild("faild", "未登录,无权访问").toString());
                    return false;
                }
                
                //判断用户IP地址
                String ip = MD5Utils.md5toUpCase_16Bit(IPTools.getIp(request));
                if(!ip.equalsIgnoreCase(user.get("ip"))){
                    //未登录,无权访问
                    printMessage(response, BaseResponse.faild("faild", "非法请求,无权访问").toString());
                    return false;
                }
                return true;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                    ModelAndView modelAndView) throws Exception {
                // TODO Auto-generated method stub
                HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                    Exception ex) throws Exception {
                // TODO Auto-generated method stub
                HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
            }
        };

        // 过滤路径
        registry.addInterceptor(handlerInterceptor).addPathPatterns("/**");
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        converters.add(converter);
    }

    public void printMessage(HttpServletResponse response, String message) throws IOException {
        PrintWriter pw = response.getWriter();
        pw.print(message);
        pw.flush();
        pw.close();
    }
   
    private boolean verifyUrl(HttpServletRequest request){
        //验证域名白名单
          String refurl = request.getHeader("referer");
          if (StringUtils.isBlank(refurl)) {
              return false;
          }
          String[] urls = refurl.split("/");
          String cagent = request.getParameter("cagent");
          String domainConfig = ObjectUtils.allNotNull(request.getSession().getAttribute("refurls"))
                  ? request.getSession().getAttribute("refurls").toString() : null;
          if (StringUtils.isBlank(domainConfig) || "[]".equals(domainConfig)) {
              List<String> domains = refererUrlDao.findAllByCagent(cagent);
              request.getSession().setAttribute("refurls", domains.toString());
              domainConfig = domains.toString();
          }
          if (domainConfig.indexOf(urls[2]) < 0) {
              logger.info("来源被拦截..地址:" + refurl + "---" + domainConfig);
              return false;
          }
          return true;
      }
}
