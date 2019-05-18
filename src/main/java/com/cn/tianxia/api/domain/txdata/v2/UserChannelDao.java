package com.cn.tianxia.api.domain.txdata.v2;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.UserChannelEntity;

public interface UserChannelDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UserChannelEntity record);

    int insertSelective(UserChannelEntity record);

    UserChannelEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserChannelEntity record);

    int updateByPrimaryKey(UserChannelEntity record);
    
    /**
     * 
     * @Description 查询平台分层支付渠道列表
     * @param cid
     * @param typeId
     * @return
     */
    List<UserChannelEntity> findAllByType(@Param("payIds") List<String> payIds,@Param("cid") Integer cid,@Param("typeId") Integer typeId,@Param("type") String type);
    
}