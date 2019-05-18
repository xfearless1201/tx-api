/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下网络 
 *    http://www.d-telemedia.com/
 *
 *    Package:     com.cn.tianxia.service 
 *
 *    Filename:    IGameService.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下网络科技 
 *
 *    @author: Wilson
 *
 *    @version: 1.0.0
 *
 *    Create at:   2018年10月26日 10:08 
 *
 *    Revision: 
 *
 *    2018/10/26 10:08 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName IGameService
 * @Description TODO(这里用一句话描述这个类的作用)
 * @Author Wilson
 * @Date 2018年10月26日 10:08
 * @Version 1.0.0
 **/
public interface IGameService {
    List<Map<String,String>> selectByGameType(Map<String,Object> params);
}