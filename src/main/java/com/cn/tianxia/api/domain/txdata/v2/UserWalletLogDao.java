package com.cn.tianxia.api.domain.txdata.v2;

import com.cn.tianxia.api.project.v2.UserWalletLogEntity;

public interface UserWalletLogDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UserWalletLogEntity record);

    int insertSelective(UserWalletLogEntity record);

    UserWalletLogEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserWalletLogEntity record);

    int updateByPrimaryKey(UserWalletLogEntity record);
}