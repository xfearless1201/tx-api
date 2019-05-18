package com.cn.tianxia.api.web.v2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cn.tianxia.api.common.v2.CacheKeyConstants;
import com.cn.tianxia.api.common.v2.SystemConfigLoader;
import com.cn.tianxia.api.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.po.v2.JSONArrayResponse;
import com.cn.tianxia.api.service.v2.WebcomConfigService;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName WebcomConfigController
 * @Description 网站配置接口
 * @author Hardy
 * @Date 2019年2月5日 下午6:54:32
 * @version 1.0.0
 */
@Controller
public class WebcomConfigController extends BaseController{
    
    @Autowired
    private WebcomConfigService webcomConfigService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private SystemConfigLoader systemConfigLoader;
    
    /**
     * 
     * @Description 获取网站公告图接口
     * @param request
     * @param response
     * @param cagent
     * @return
     */
    @RequestMapping(value="webcom.do")
    @ResponseBody
    public  JSONArray webcom(HttpServletRequest request, HttpServletResponse response,String cagent){
        logger.info("调用查询网站广告图接口开始=================start=============");
        try {
            if(StringUtils.isBlank(cagent)){
                logger.info("请求参数,平台编码不能为空");
                return JSONArrayResponse.faild("请求参数错误:平台编码不能为空");
            }
            return webcomConfigService.getBanner(cagent);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询网站广告图接口异常:{}",e.getMessage());
            return JSONArrayResponse.faild("调用查询网站广告图接口异常");
        }
    }
    

    /**
     * 
     * @Description 获取网站公告
     * @param request
     * @param response
     * @param cagent
     * @return
     */
    @RequestMapping(value="gonggao.do")
    @ResponseBody
    public  JSONArray gonggao(HttpServletRequest request, HttpServletResponse response,String cagent){
        logger.info("调用获取网站公告接口开始=============START===============");
        try {
            if(StringUtils.isBlank(cagent)){
                return JSONArrayResponse.faild("请求参数错误:平台编码不能为空");
            }
            String key = CacheKeyConstants.WEB_GONGGAO_CAGENT + cagent;
            if (!redisUtils.hasKey(key)) {
                JSONArray config = webcomConfigService.getNoticeInfo(cagent);
                if (config != null && config.size() >= 1 && !config.getJSONObject(0).containsValue("faild")) {
                    redisUtils.set(key, config, 2 * 60 * 60);
                }
                return config;
            } else {
                return redisUtils.get(key, JSONArray.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取网站公告接口异常:{}",e.getMessage());
            return JSONArrayResponse.faild("调用获取网站公告接口异常");
        }
    }
    
    
    /**
     * 
     * @Description 获取网站设置
     * @param request
     * @param response
     * @param cagent
     * @param type
     * @return
     */
    @RequestMapping(value="webcomconfig.do")
    @ResponseBody
    public  JSONObject selectWebcomConfig(HttpServletRequest request, HttpServletResponse response,String cagent,Integer type){
        logger.info("调用查询网站设置接口开始===============START====================");
        try {
            if(StringUtils.isBlank(cagent)){
                return BaseResponse.faild("0", "请求参数异常:平台编码不能为空");
            }
            if(type == null){
                return BaseResponse.faild("0", "请求参数异常:类型不能为空");
            }
            String key = CacheKeyConstants.WEB_CONFIG_CAGENT + cagent + type;
            if (!redisUtils.hasKey(key)) {
                JSONObject config = webcomConfigService.getWebcomConfig(cagent, type);
                if (config != null && !"faild".equals(config.getString("status"))) {
                    redisUtils.set(key, config,  2 * 60 * 60);
                }
                return config;
            } else {
                return redisUtils.get(key, JSONObject.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询网站设置接口异常:{}",e.getMessage());
            return BaseResponse.error("0", "查询网站设置异常");
        }
    }
    
    /**
     * 
     * @Description 查询手机网站设置信息
     * @param request
     * @param response
     * @param cagent
     * @param type
     * @return
     */
    @RequestMapping(value="mobleWebcomConfig.do")
    @ResponseBody
    public  JSONArray selectMobleWebcomConfig(HttpServletRequest request, HttpServletResponse response,String cagent,Integer type){
        logger.info("调用查询平台手机网站设置接口开始================START====================");
        try {
            if(StringUtils.isBlank(cagent)){
                return JSONArrayResponse.faild("请求参数异常:平台编码不能为空");
            }
            String bannerType = type==0?"10":type==1?"11":"12";
            //查询手机端网站设置
            String key = CacheKeyConstants.MOBILE_WEB_CONFIG_CAGENT + cagent + bannerType;
            if (!redisUtils.hasKey(key)) {
                JSONArray array = webcomConfigService.getMobileWebcomConfig(cagent, bannerType);
                if (array != null && array.size() >= 1 && !array.getJSONObject(0).containsValue("faild")) {
                    redisUtils.set(key, array,  2*60*60);
                }
                return array;
            } else {
                return redisUtils.get(key, JSONArray.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询平台手机网站设置接口异常:{}",e.getMessage());
            return JSONArrayResponse.faild("调用查询平台手机网站设置接口异常");
        }
    }
    
    /**
     * 获取网站图文信息
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param request
     * @param response
     * @param cagent
     * @return
     */
    @RequestMapping(value="newGonggao.do")
    @ResponseBody
    public JSONArray newGonggao(HttpServletRequest request,HttpServletResponse response,String cagent){
        logger.info("调用获取网站图文信息接口开始====================START====================");
        try {
            if(StringUtils.isBlank(cagent) || cagent.length() > 3){
                return JSONArrayResponse.faild("请求参数异常:平台编码不能为空");
            }
            return webcomConfigService.getMobileWebcomConfig(cagent, "10");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取网站图文信息接口异常:{}",e.getMessage());
            return JSONArrayResponse.faild("调用查询平台图文信息异常");
        }
    }

    /**
     *
     * @Description 清除网站配置缓存
     * @param key
     * @return
     */
    @RequestMapping("/clearWebConfig.do")
    @ResponseBody
    public JSONObject clearWebConfig(String cagent,String key, String config, String type) {
        logger.info("清除网站配置缓存开始===============START==========================");
        logger.info("清除网站配置缓存，请求参数：cagent:" + cagent + ",config:" + config + ",type:" + type);
        try {
            //从配置文件中获取key
            String acckey = systemConfigLoader.getProperty("key");
            if(StringUtils.isBlank(key) || StringUtils.isBlank(acckey) || !key.equalsIgnoreCase(acckey))
                return BaseResponse.error("0", "key不匹配");
            if (StringUtils.isBlank(cagent))
                return BaseResponse.error("0", "cagent不能为空");
            if (StringUtils.isBlank(config))
                return BaseResponse.error("0", "清理类型不能为空");
            if (("webcomconfig".equals(config) || "mobleWebcomConfig".equals(config)) && StringUtils.isBlank(type))
                return BaseResponse.error("0", "类型不能为空");
            switch (config) {
                case "gonggao":
                    String gonggaoKey = CacheKeyConstants.WEB_GONGGAO_CAGENT + cagent;
                    redisUtils.delete(gonggaoKey);
                    break;
                case "webcomconfig":
                    String webcomkey = CacheKeyConstants.WEB_CONFIG_CAGENT + cagent + type;
                    redisUtils.delete(webcomkey);
                    break;
                case "mobleWebcomConfig":
                    int i = Integer.valueOf(type);
                    String bannerType = i==0?"10":i==1?"11":"12";
                    String mobleWebkey = CacheKeyConstants.MOBILE_WEB_CONFIG_CAGENT + cagent + bannerType;
                    redisUtils.delete(mobleWebkey);
                default:
                    return BaseResponse.error("0", "config 清理类型不存在");
            }
            return BaseResponse.success("清理成功！");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("清除网站配置缓存异常:{}",e.getMessage());
            return BaseResponse.error("0", "清除网站缓存配置异常:" + e.getMessage() );
        }
    }
}
