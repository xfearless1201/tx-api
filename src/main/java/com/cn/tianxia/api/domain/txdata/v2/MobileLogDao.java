package com.cn.tianxia.api.domain.txdata.v2;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.MobileLogEntity;

public interface MobileLogDao {
    int deleteByPrimaryKey(Integer id);

    int insert(MobileLogEntity record);

    int insertSelective(MobileLogEntity record);

    MobileLogEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MobileLogEntity record);

    int updateByPrimaryKey(MobileLogEntity record);

    List<MobileLogEntity> selectMobileLogByUid(@Param("uid") String uid);
}