package com.cn.tianxia.api.service.v2.impl;

import com.cn.tianxia.api.domain.txdata.v2.ApiLogDao;
import com.cn.tianxia.api.project.v2.ApiLogEntity;
import com.cn.tianxia.api.service.v2.ApiLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: zed
 * @Date: 2019/5/11 19:01
 * @Description: Api log implement
 */
@Service
public class ApiLogServiceImpl implements ApiLogService {

    @Autowired
    private ApiLogDao apiLogDao;

    @Override
    public int insertApiLog(ApiLogEntity apiLogEntity) {
        return apiLogDao.insertSelective(apiLogEntity);
    }
}
