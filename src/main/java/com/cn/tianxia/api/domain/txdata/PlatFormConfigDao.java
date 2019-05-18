package com.cn.tianxia.api.domain.txdata;


import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.PlatFormConfig;

public interface PlatFormConfigDao {
    /**
     * 根据key获取配置
     * @param key
     * @return
     */
    PlatFormConfig getConfigByKey(@Param("key") String key);
}
