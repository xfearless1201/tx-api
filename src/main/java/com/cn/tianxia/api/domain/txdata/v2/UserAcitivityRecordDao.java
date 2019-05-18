package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.UserAcitivityRecordEntity;

public interface UserAcitivityRecordDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserAcitivityRecordEntity record);

    int insertSelective(UserAcitivityRecordEntity record);

    UserAcitivityRecordEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserAcitivityRecordEntity record);

    int updateByPrimaryKey(UserAcitivityRecordEntity record);

    UserAcitivityRecordEntity selectRecordByAidAndUid(@Param("aid") Long aid, @Param("uid") Integer uid);
}