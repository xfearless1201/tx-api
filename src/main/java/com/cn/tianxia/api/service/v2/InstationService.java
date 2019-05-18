package com.cn.tianxia.api.service.v2;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName InstationService
 * @Description  站内信接口
 * @author Hardy
 * @Date 2019年2月1日 下午8:42:24
 * @version 1.0.0
 */
public interface InstationService {

    
    /**
     * 
     * @Description 获取站内信数量
     * @param uid
     * @param bdate
     * @param edate
     * @return
     */
    public JSONObject getMessageNum(String uid,String bdate,String edate);
    
    /**
     * 
     * @Description 获取站内信列表
     * @param status
     * @param bdate
     * @param edate
     * @return
     */
    public JSONArray getMessageList(String uid,String status,String bdate, String edate);
    
    /**
     * 
     * @Description 获取站内信详情
     * @param id
     * @return
     */
    public JSONObject getMessageInfo(String id);
    
    /**
     * 
     * @Description 删除站内信详情
     * @param id
     * @return
     */
    public JSONObject deleteMessage(String id);
    
    
}
