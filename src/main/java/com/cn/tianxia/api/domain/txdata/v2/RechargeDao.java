package com.cn.tianxia.api.domain.txdata.v2;


import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.RechargeEntity;
import com.cn.tianxia.api.vo.RechargeOrderVO;

public interface RechargeDao {
    int deleteByPrimaryKey(Integer rId);

    int insert(RechargeEntity record);

    int insertSelective(RechargeEntity record);

    RechargeEntity selectByPrimaryKey(Integer rId);

    int updateByPrimaryKeySelective(RechargeEntity record);

    int updateByPrimaryKey(RechargeEntity record);

    RechargeEntity selectByOrderNo(String orderNo);
    
    RechargeOrderVO findOneByOrderNo(@Param("orderNo") String orderNo);
}