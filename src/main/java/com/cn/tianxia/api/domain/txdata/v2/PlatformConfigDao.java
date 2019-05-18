package com.cn.tianxia.api.domain.txdata.v2;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.PlatformConfigEntity;

/**
 * 
 * @ClassName PlatformConfigDao
 * @Description 游戏平台配置表dao
 * @author Hardy
 * @Date 2019年2月6日 上午11:45:28
 * @version 1.0.0
 */
public interface PlatformConfigDao{

    int deleteByPrimaryKey(Integer id);

    int insert(PlatformConfigEntity record);

    int insertSelective(PlatformConfigEntity record);

    PlatformConfigEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PlatformConfigEntity record);

    int updateByPrimaryKey(PlatformConfigEntity record);
    
    List<PlatformConfigEntity> findAll();

    PlatformConfigEntity selectByPlatformKey(@Param("platformKey") String platformKey);
}