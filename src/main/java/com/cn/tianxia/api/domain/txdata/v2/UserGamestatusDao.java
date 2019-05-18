package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.UserGamestatusEntity;
/**
 * 
 * @ClassName UserGamestatusDao
 * @Description 会员游戏状态表dao
 * @author Hardy
 * @Date 2019年2月7日 下午2:42:18
 * @version 1.0.0
 */
public interface UserGamestatusDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UserGamestatusEntity record);

    int insertSelective(UserGamestatusEntity record);

    UserGamestatusEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserGamestatusEntity record);

    int updateByPrimaryKey(UserGamestatusEntity record);
    
    UserGamestatusEntity selectByGameType(@Param("uid") String uid,@Param("gametype") String gametype);
}