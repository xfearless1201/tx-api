package com.cn.tianxia.api.domain.txdata.v2;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.UserTreasureEntity;

public interface UserTreasureDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UserTreasureEntity record);

    int insertSelective(UserTreasureEntity record);

    UserTreasureEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserTreasureEntity record);

    int updateByPrimaryKey(UserTreasureEntity record);
    
    /**
     * 
     * @Description 分页查询用户资金流水
     * @param uid
     * @param type
     * @param bdate
     * @param edate
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<UserTreasureEntity> findAllByPage(@Param("uid") String uid,@Param("type") String type,
                                            @Param("bdate") Date bdate,@Param("edate") Date edate,
                                            @Param("pageNo") Integer pageNo,@Param("pageSize") Integer pageSize);
    /**
     * 查询开始和结束时间的充值和加款的所有资金流水
     *
     */
    Double findAllAmountByTime(@Param("uid") Integer uid,@Param("bdate") String bdate,@Param("edate") String edate);
    /**
     * 
     * @Description 统计通页数
     * @param uid
     * @param type
     * @param bdate
     * @param edate
     * @return
     */
    Map<String,String> countTotalPages(@Param("uid") String uid,@Param("type") String type,
                                                @Param("bdate") Date bdate,@Param("edate") Date edate);
    
    int countUserTreasureByOrderNo(@Param("uid") Integer uid,@Param("orderNo") String orderNo,@Param("cagent") String cagent);
}