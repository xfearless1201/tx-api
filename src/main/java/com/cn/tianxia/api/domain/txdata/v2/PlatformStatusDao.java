package com.cn.tianxia.api.domain.txdata.v2;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.PlatformStatusEntity;

/**
 * 
 * @ClassName PlatformStatusDao
 * @Description 平台游戏开关配置表dao
 * @author Hardy
 * @Date 2019年2月7日 下午3:10:44
 * @version 1.0.0
 */
public interface PlatformStatusDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PlatformStatusEntity record);

    int insertSelective(PlatformStatusEntity record);

    PlatformStatusEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PlatformStatusEntity record);

    int updateByPrimaryKey(PlatformStatusEntity record);
    
    Map<String,String> selectByCid(@Param("cid") String cid);

    Short selectGameStatusByCidAndType(@Param("cid") String cid, @Param("type") String type);
}