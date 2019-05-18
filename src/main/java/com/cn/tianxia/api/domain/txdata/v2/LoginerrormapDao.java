package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.LoginerrormapEntity;

/**
 * 
 * @ClassName LoginerrormapDao
 * @Description 用户登录失败次数dao
 * @author Hardy
 * @Date 2019年2月6日 下午5:01:07
 * @version 1.0.0
 */
public interface LoginerrormapDao {
    int deleteByPrimaryKey(Integer id);

    int insert(LoginerrormapEntity record);

    int insertSelective(LoginerrormapEntity record);

    LoginerrormapEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LoginerrormapEntity record);

    int updateByPrimaryKey(LoginerrormapEntity record);
    
    LoginerrormapEntity findAllByUsername(@Param("username") String username);
}