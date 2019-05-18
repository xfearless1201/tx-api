package com.cn.tianxia.api.service.v2;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName ContactUsService
 * @Description 联系我们接口
 * @author Hardy
 * @Date 2019年2月4日 下午6:28:03
 * @version 1.0.0
 */
public interface ContactUsService {

    /**
     * 
     * @Description 通过平台编码获取联系我们信息
     * @param cagent
     * @return
     */
    public JSONObject getContackUsInfo(String cagent);
}
