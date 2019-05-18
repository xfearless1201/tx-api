package com.cn.tianxia.api.web.v2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.service.v2.ContactUsService;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName ContactUsController
 * @Description 联系我们接口
 * @author Hardy
 * @Date 2019年2月4日 下午6:26:04
 * @version 1.0.0
 */
@Controller
@RequestMapping("/User")
public class ContactUsController extends BaseController{
    
    @Autowired
    private ContactUsService contactUsService;

    /**
     * 
     * @Description 获取联系我们的信息
     * @param request
     * @param response
     * @param cagent
     * @return
     */
    @RequestMapping("/getContactInfo")
    @ResponseBody
    public JSONObject getContactInfo(HttpServletRequest request, HttpServletResponse response, String cagent) {
        logger.info("调用获取平台联系信息接口开始===============START==============");
        try {
            if(StringUtils.isBlank(cagent)){
                logger.info("请求参数异常,平台编码不能为空");
                return BaseResponse.error("0", "请求参数异常,平台编码不能为空");
            }
            return contactUsService.getContackUsInfo(cagent);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取平台联系信息接口异常:{}",e.getMessage());
            return BaseResponse.error("0", "查询平台联系信息异常");
        }
    }
}
