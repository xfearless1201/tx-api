package com.cn.tianxia.api.domain.txdata.v2;

import com.cn.tianxia.api.project.v2.PluInventoryLogEntity;

public interface PluInventoryLogDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PluInventoryLogEntity record);

    int insertSelective(PluInventoryLogEntity record);

    PluInventoryLogEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PluInventoryLogEntity record);

    int updateByPrimaryKey(PluInventoryLogEntity record);
}