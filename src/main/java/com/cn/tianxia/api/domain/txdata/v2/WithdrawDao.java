package com.cn.tianxia.api.domain.txdata.v2;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.WithdrawEntity;

/**
 * 
 * @ClassName WithdrawDao
 * @Description 提款表dao
 * @author Hardy
 * @Date 2019年1月29日 下午3:28:05
 * @version 1.0.0
 */
public interface WithdrawDao {
    int deleteByPrimaryKey(Integer id);

    int insert(WithdrawEntity record);

    int insertSelective(WithdrawEntity record);

    WithdrawEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WithdrawEntity record);

    int updateByPrimaryKey(WithdrawEntity record);
    
    /**
     * 
     * @Description 查询会员待提现的记录条数
     * @param uid
     * @return
     */
    int getUserUnwithDrawCounts(@Param("uid") Integer uid);
    
    /**
     * 
     * @Description 查询用户打码量
     * @param uid
     * @return
     */
    Map<String,String> selectUserQuantityByid(@Param("uid") Integer uid);
    
    /**
     * 
     * @Description 查询用户提现总次数
     * @param uid
     * @return
     */
    Map<String,String> selectWithDrawTotaltimes(@Param("uid") Integer uid);
    
    /**
     * 
     * @Description 分页查询用户提现订单记录
     * @param uid
     * @param status
     * @param bdate
     * @param edate
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<WithdrawEntity> findAllByPage(@Param("uid") String uid,@Param("status") String status,
                                        @Param("bdate") Date bdate,@Param("edate") Date edate,
                                        @Param("pageNo") Integer pageNo,@Param("pageSize") Integer pageSize);
    
    /**
     * 
     * @Description 查询总页数和总金额
     * @param uid
     * @param status
     * @param bdate
     * @param edate
     * @return
     */
    Map<String,String> selectWithDrawCount(@Param("uid") String uid,@Param("status") String status,
                                            @Param("bdate") Date bdate,@Param("edate") Date edate);

    /**
     *
     * @Description 查询会员打码量总游戏金额强制提款手续费
     * @param uid
     *
      */

    Map<String, Object> selectWithdrawConfig(String uid);
}