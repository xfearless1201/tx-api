package com.cn.tianxia.api.utils;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
@Component
@Service
public class CORSFilter implements Filter {
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
	  HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) res; 
      try{
    	  String useragent=request.getHeader("user-agent").toString();    	 
    	  if(useragent.indexOf("Mobile")>0){
        	  response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
              response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
              response.setHeader("Access-Control-Max-Age", "1800");
              response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
              response.setHeader("Access-Control-Allow-Credentials","true"); //是否支持cookie跨域 
              response.setHeader("P3P","CP='CAO IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT'");
          } 
      }catch(Exception e){ 
      }
      
      chain.doFilter(req, res);
  }
  public void init(FilterConfig filterConfig) {}
  public void destroy() {}
}