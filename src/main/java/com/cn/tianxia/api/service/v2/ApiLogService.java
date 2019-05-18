package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.project.v2.ApiLogEntity;

/**
 * @Author: zed
 * @Date: 2019/5/11 19:00
 * @Description: api log service
 */
public interface ApiLogService {
    int insertApiLog(ApiLogEntity apiLogEntity);
}
