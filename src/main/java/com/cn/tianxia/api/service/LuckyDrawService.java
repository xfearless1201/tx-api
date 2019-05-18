package com.cn.tianxia.api.service;

import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 功能概要：UserService接口类
 */
public interface LuckyDrawService {

    //根据来源域名查询平台活动
    List<Map<String, Object>> selectLuckyDrawStatus(String domain);

    JSONObject luckyDraw(String userName,String refurl) throws Exception;

}
