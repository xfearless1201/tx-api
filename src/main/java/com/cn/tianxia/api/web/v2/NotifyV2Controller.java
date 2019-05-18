package com.cn.tianxia.api.web.v2;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.base.annotation.LogApi;
import com.cn.tianxia.api.service.NotifyService;
import com.cn.tianxia.api.vo.CagentYespayVO;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/19 09:37
 * @Description: v2版本回调controller
 */
@Controller
@Scope("prototype")
public class NotifyV2Controller extends BaseController {

    @Autowired
    private NotifyService notifyService;

    public String ret_str_failed = "fail";

    @LogApi("通用回调接口")
    @RequestMapping("/V2Notify.do/{cagent}/{payment}")
    @ResponseBody
    public String V2Notify(HttpServletRequest request, HttpServletResponse response, @PathVariable String cagent, @PathVariable String payment) {
        logger.info("支付回调开始-----------------------------START------------------------------");
        CagentYespayVO cagentYespayVO = notifyService.getCagentYsepayByCagentAndPayment(cagent, payment);
        if (cagentYespayVO == null) {
            logger.info("非法支付商,查询支付商信息失败,平台编号:{},支付商名称:{}", cagent, payment);
            return ret_str_failed;
        }

        JSONObject pmapsconfig = JSONObject.fromObject(cagentYespayVO.getPaymentConfig());//支付商配置信息

        try {
            logger.info("通过反射获取支付实现类回调方法开始=================start=====================");
            StringBuilder sb = new StringBuilder();
            sb.append("com.cn.tianxia.api.pay.impl").append(".");// 包名
            sb.append(payment).append("PayServiceImpl");
            logger.info("反射接口包名:{}", sb.toString());

            Class payService = Class.forName(sb.toString());

            Method notifyMethod = payService.getDeclaredMethod("notify", HttpServletRequest.class, HttpServletResponse.class,JSONObject.class);

            return (String) notifyMethod.invoke(payService.newInstance(),request,response,pmapsconfig);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("通过反射调用支付实现类方法异常:{}",e.getMessage());
            return ret_str_failed;
        }
    }
}
