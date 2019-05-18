package com.cn.tianxia.api.utils.v2;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @ClassName PrintUtils
 * @Description 输出工具类
 * @author Hardy
 * @Date 2019年3月18日 上午11:46:03
 * @version 1.0.0
 */
public class PrintUtils {
    
    
    /**
     * 
     * @Description 流输出
     * @param response
     * @param message
     * @return
     */
    public static String printMessage(HttpServletResponse response,String message,String redirectUrl){
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.print(message);
            pw.flush();
            pw.close();
            return pw.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:" + redirectUrl;
        }
    }
}
