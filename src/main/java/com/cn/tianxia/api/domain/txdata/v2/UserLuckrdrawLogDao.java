package com.cn.tianxia.api.domain.txdata.v2;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.UserLuckrdrawLogEntity;

public interface UserLuckrdrawLogDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UserLuckrdrawLogEntity record);

    int insertSelective(UserLuckrdrawLogEntity record);

    UserLuckrdrawLogEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserLuckrdrawLogEntity record);

    int updateByPrimaryKey(UserLuckrdrawLogEntity record);

    List<UserLuckrdrawLogEntity> selectByUidAndLid(@Param("uid") Integer uid, @Param("lid") Integer lid, @Param("bdate") Date bdate, @Param("edate") Date edate,@Param("typesOf") String typesOf);

    UserLuckrdrawLogEntity selectRecentRecord(@Param("uid") Integer uid);
}