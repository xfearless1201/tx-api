package com.cn.tianxia.api.domain.txdata.v2;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.CagentYsepayEntity;

public interface CagentYsepayDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CagentYsepayEntity record);

    int insertSelective(CagentYsepayEntity record);

    CagentYsepayEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CagentYsepayEntity record);

    int updateByPrimaryKey(CagentYsepayEntity record);

    List<CagentYsepayEntity> selectPaymentListById(@Param("uid") String userId, @Param("payId") String payId);

    CagentYsepayEntity selectPaymentConfigByUidAndPayId(@Param("uid") String userId,@Param("pid") String payId);
    
    List<CagentYsepayEntity> findAllByIds(@Param("ids") List<Integer> ids);
}