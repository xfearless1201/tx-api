package com.cn.tianxia.api.domain.txdata.v2;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.AmountRecordEntity;
import com.cn.tianxia.api.project.v2.DepositRecordEntity;

/**
 * 
 * @ClassName AmountRecordMapper
 * @Description 平台存款记录Dao
 * @author Hardy
 * @Date 2019年1月5日 上午10:58:02
 * @version 1.0.0
 */
public interface AmountRecordDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AmountRecordEntity record);

    int insertSelective(AmountRecordEntity record);

    AmountRecordEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AmountRecordEntity record);

    int updateByPrimaryKey(AmountRecordEntity record);
    
    int sumUnauditRemittance(@Param("uid")String uid);
    
    int sumUserRemittanceTimes(@Param("uid")String uid);

    List<DepositRecordEntity> selectDepositRecordLimit(Map param);
}