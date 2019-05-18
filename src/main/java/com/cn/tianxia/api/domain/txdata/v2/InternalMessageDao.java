package com.cn.tianxia.api.domain.txdata.v2;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.InternalMessageEntity;

public interface InternalMessageDao {
    int deleteByPrimaryKey(Integer id);

    int insert(InternalMessageEntity record);

    int insertSelective(InternalMessageEntity record);

    InternalMessageEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InternalMessageEntity record);

    int updateByPrimaryKey(InternalMessageEntity record);
    
    /**
     * 
     * @Description 统计用户站内信未读和已读总条数
     * @param uid
     * @return
     */
    Map<String,String> sumInternalMessagesByUid(@Param("uid") String uid,@Param("bdate") String bdate,
                                                                            @Param("edate") String edate);
    
    /**
     * 
     * @Description 查询用户站内信列表
     * @param uid
     * @param status
     * @param bdate
     * @param edate
     * @return
     */
    List<InternalMessageEntity> findAllByUid(@Param("uid") String uid,@Param("status") String status,
                                                @Param("bdate") String bdate,@Param("edate") String edate);
    
    /**
     * 
     * @Description 批量删除站内信
     * @param ids
     * @return
     */
    int batchDelInternalMessage(@Param("ids") List<String> ids);
}