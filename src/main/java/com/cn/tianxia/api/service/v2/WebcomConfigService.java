package com.cn.tianxia.api.service.v2;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface WebcomConfigService {

    /**
     * 
     * @Description 获取blaner图
     * @param cagent
     * @return
     */
    public JSONArray getBanner(String cagent);
    
    /**
     * 
     * @Description 获取网站公告
     * @param cagent
     * @return
     */
    public JSONArray getNoticeInfo(String cagent);
    
    /**
     * 
     * @Description 获取网站设置
     * @param cagent
     * @param type
     * @return
     */
    public JSONObject getWebcomConfig(String cagent,Integer type);
    
    /**
     * 
     * @Description 获取手机端网站设置
     * @param cagent
     * @param type
     * @return
     */
    public JSONArray getMobileWebcomConfig(String cagent,String type);
}
