package com.cn.tianxia.api.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cn.tianxia.api.base.annotation.LogApi;
import com.cn.tianxia.api.common.v2.CacheKeyConstants;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.service.v2.PlatformPayService;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.utils.pay.ParamsUtils;
import com.cn.tianxia.api.utils.v2.PrintUtils;
import com.cn.tianxia.api.vo.BankPayVO;
import com.cn.tianxia.api.vo.ScanPayVO;

import net.sf.json.JSONObject;

/**
 * @Description:TODO
 * @author:zouwei
 * @time:2017年7月9日 下午3:24:04
 */
@RequestMapping("PlatformPay")
@Controller
@Scope("prototype")
public class PlatformPayController extends BaseController {

    @Autowired
    private PlatformPayService platformPayService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * // 网银支付
     * 
     * @param request
     * @param response
     * @param model
     * @return
     * @throws IOException
     * @throws ServletException
     */
    @LogApi("网银支付接口")
    @RequestMapping("/onlineBanking")
    public String onlineBanking(HttpServletRequest request, HttpSession session, HttpServletResponse response,
            Model model, RedirectAttributes attr) {
        logger.info("网银支付请求开始--------------------START---------------------------");
        // 获取来源域名
        StringBuffer url = request.getRequestURL();
        String requestUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/")
                .toString();
        //请求IP地址
        String ip = StringUtils.isBlank(IPTools.getIp(request))?"127.0.0.1":IPTools.getIp(request);
        // 从缓存中获取用户信息,并判断用户是否登录
        Map<String,String> usermap = getUserInfoMap(redisUtils, request);
        if (CollectionUtils.isEmpty(usermap)) {
            logger.info("调用网银支付接口失败,用户未登录,请重新登录");
            return PrintUtils.printMessage(response, "调用网银支付接口失败,请联系客服", requestUrl);
        }
        String uid = String.valueOf(usermap.get("uid"));
        // 防止同一户用户提交
        String key = CacheKeyConstants.ONLINE_PAY_KEY_UID + uid;
        try {
            if (redisUtils.hasKey(key)) {
                logger.info("用户【" + uid + "】,重复提交网银支付订单");
                return PrintUtils.printMessage(response, "订单已生成,正在处理中,请不要重复提交", requestUrl);
            }
            redisUtils.set(key, uid,10);

            // 获取支付请求参数
            Map<String, String> reqParams = ParamsUtils.getParameterNames(request);
            //{"acounmt":"100","scancode":"ali","payId":"1063"}
            //校验支付请求参数
            if(CollectionUtils.isEmpty(reqParams)){
                logger.info("用户【"+uid+"】,提交网银支付订单请求参数报文为空");
                return PrintUtils.printMessage(response, "调用网银支付接口失败,请联系客服",requestUrl);
            }
            logger.info("用户【" + uid + "】,提交网银支付订单请求参数报文:{}", JSONObject.fromObject(reqParams).toString());
            // 请求VO
            BankPayVO bankPayVO = new BankPayVO();
            bankPayVO.setUid(uid);
            bankPayVO.setPayId(reqParams.get("payId"));
            bankPayVO.setAmount(Double.valueOf(reqParams.get("acounmt")));
            bankPayVO.setBankcode(reqParams.get("bankcode"));
            bankPayVO.setReturn_url(requestUrl);
            bankPayVO.setIp(ip);

            JSONObject result = platformPayService.bankPay(bankPayVO);

            // 失败直接返回--》》通知调用异常处理页面
            if (result.containsKey("status") && "error".equals(result.get("status").toString())) {
                logger.info("网银表单构造异常--->>");
                // 直接返回来源地址
                return PrintUtils.printMessage(response, "网银表单构造异常", requestUrl);
            }
            // 网银支付三种跳转情况:一、from表单通过网关服务器 二、model通过jsp页面提交 三、redirect 重定向URL
            if (result.containsKey("form")) {
                String form = URLEncoder.encode(result.getString("form"), "utf-8");
                attr.addAttribute("from", form);
                return result.getString("redirect");
            } else if (result.containsKey("jsp_content")) {
                model.addAttribute("html", result.get("jsp_content").toString());
                return result.getString("jsp_name");
            } else if (result.containsKey("link")) {
                return "redirect:" + result.getString("link");
            } else if (result.containsKey("credential")) {
                model.addAttribute("JWP_ATTR", result.getString("credential"));
                String bankurl = "/page/middle.jsp";
                return "forward:" + bankurl;
            } else if (result.containsKey("file")) {
                String htmlStr = result.getString("file");
                response.setCharacterEncoding("utf-8");
                response.setContentType("text/html");
                PrintWriter printWriter = response.getWriter();
                printWriter.print(htmlStr);
                printWriter.flush();
                printWriter.close();
                return printWriter.toString();
            } else {
                logger.info("跳转类型错误！！！");
                return "redirect:" + requestUrl;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("网银支付异常:{}", e.getMessage());
            return PrintUtils.printMessage(response, "调用网银支付接口失败,请联系客服", requestUrl);
        } finally {
            if (redisUtils.hasKey(key)) {
                redisUtils.delete(key);
            }
        }
    }

    /**
     * 扫描支付 // 参数:scancode 扫描支付code amount 支付金额 topay 支付商code
     *
     * @param request
     * @param response
     * @return
     */
    @LogApi("扫码支付接口")
    @RequestMapping("/scanPay")
    @ResponseBody
    public JSONObject scanPay(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        logger.info("扫码支付请求开始=======================START=================================");
        // 获取来源域名
        StringBuffer url = request.getRequestURL();
        logger.info("URL长度："+request.getRequestURL()+" URI长度："+request.getRequestURI());
        String requestUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/")
                .toString();
        // 请求ip
        String ip = StringUtils.isBlank(IPTools.getIp(request)) ? "127.0.0.1" : IPTools.getIp(request);

        // 从缓存中获取用户信息,并判断用户是否登录
        Map<String,String> usermap = getUserInfoMap(redisUtils, request);
        if (CollectionUtils.isEmpty(usermap)) {
            logger.info("调用扫码支付接口失败,用户未登录,请重新登录");
            return BaseResponse.faild("faild", "调用扫码支付失败,用户未登录,请重新登录");
        }
        String uid = String.valueOf(usermap.get("uid"));
        String key = CacheKeyConstants.ONLINE_PAY_KEY_UID + uid;
        // 判断缓存是否存在用户请求
        if (redisUtils.hasKey(key)) {
            logger.info("支付请求正在处理,请不要重复提交....");
            return BaseResponse.error("1000", "支付请求正在处理,请不要重复提交....");
        }
        redisUtils.set(key, uid,10);
        try {
            
            //获取支付请求参数
            Map<String,String> reqParams = ParamsUtils.getParameterNames(request);
            //{"acounmt":"100","scancode":"ali","payId":"1063"}
            //校验支付请求参数
            if(CollectionUtils.isEmpty(reqParams)){
                logger.info("用户【"+uid+"】,提交扫码支付订单请求参数报文为空");
                return BaseResponse.faild("faild", "订单提交失败,请求参数不能为空");
            }
            logger.info("用户【"+uid+"】,提交扫码支付订单请求参数报文:{}",JSONObject.fromObject(reqParams).toString());
            // 请求VO
            ScanPayVO scanPayVO = new ScanPayVO();
            scanPayVO.setUid(uid);
            scanPayVO.setPayId(reqParams.get("payId"));
            scanPayVO.setAmount(Double.valueOf(reqParams.get("acounmt")));
            scanPayVO.setScancode(reqParams.get("scancode"));
            scanPayVO.setRefererUrl(requestUrl);
            scanPayVO.setIp(ip);
            scanPayVO.setMobile(reqParams.get("mobile"));
            return platformPayService.scanPay(scanPayVO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("扫码支付异常：" + e.getMessage());
            return BaseResponse.error("1000", "扫码支付异常：" + e.getMessage());
        } finally {
            if (redisUtils.hasKey(key)) {
                redisUtils.delete(key);
            }
        }
    }

}
